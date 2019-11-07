package skybiz.com.posoffline;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;
//import skybiz.com.cashoff.m_Tax.DownloaderTax;


/**
 * Created by 7 on 14/12/2017.
 */

public class DialogShowImage extends DialogFragment {
    View view;
    Button btnCancel;
    ImageView ImgItem;
    String ItemCode,uFrom,Desc;
    Bitmap bmpItem;
    TextView txtHeader;
    private Animator currentAnimator;
    private int shortAnimationDuration;
    ProgressBar pbImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view            = inflater.inflate(R.layout.dialog_showimage, container, false);
        btnCancel       = (Button)view.findViewById(R.id.btnCancel);
        ImgItem         = (ImageView) view.findViewById(R.id.ImgItem);
        txtHeader       = (TextView)view.findViewById(R.id.txtHeader);
        pbImage         = (ProgressBar)view.findViewById(R.id.pbImage);
        uFrom           = this.getArguments().getString("TYPE_KEY");
        ItemCode        = this.getArguments().getString("ITEMCODE_KEY");
        Desc            = this.getArguments().getString("DESC_KEY");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        initview();
        getDialog().setTitle("Preview Image");
        //Drawable img=new BitmapDrawable(Bitmap.createScaledBitmap(bmpItem,120,125,true));
        txtHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fnzoom();
            }
        });
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        return view;
    }
    private void fnzoom(){
        zoomImageFromThumb(txtHeader, bmpItem);
    }
    private void initview(){
        pbImage.setVisibility(View.VISIBLE);
        txtHeader.setText(ItemCode+" | "+Desc);
        RetImage retImage=new RetImage(getActivity(),ItemCode,pbImage);
        retImage.execute();
    }

    private class RetImage extends AsyncTask<Void,Integer,String>{
        Context c;
        String ItemCode;
        ProgressBar pbImage;
        String IPAddress,UserName,Password,
                DBName,Port,URL,z,
                DBStatus,ItemConn,EncodeType;
        JSONObject jsonReq,jsonRes;
        int LImage=1;

        public RetImage(Context c, String itemCode, ProgressBar pbImage) {
            this.c = c;
            ItemCode = itemCode;
            this.pbImage=pbImage;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fnshow();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                Drawable img=new BitmapDrawable(Bitmap.createScaledBitmap(bmpItem,120,125,true));
                ImgItem.setImageDrawable(img);
                pbImage.setVisibility(View.GONE);
            }else if(result.equals("error")){
                Toast.makeText(c,"Error, Cannot Showing Image", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (this.pbImage != null) {
                pbImage.setMax(LImage);
                pbImage.setProgress(values[0]);
            }
        }

        private String fnshow(){
            try{
                z="error";
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName, UserName, Password," +
                        " DBName, Port, DBStatus," +
                        " ItemConn, EncodeType" +
                        " from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress   = curSet.getString(0);
                    UserName    = curSet.getString(1);
                    Password    = curSet.getString(2);
                    DBName      = curSet.getString(3);
                    Port        = curSet.getString(4);
                    DBStatus    = curSet.getString(5);
                    ItemConn    = curSet.getString(6);
                    EncodeType  = curSet.getString(7);
                }
                curSet.close();
                String sql = "select IFNULL(PhotoFile,'')as PhotoFile " +
                        " from stk_master_photo where ItemCode='"+ItemCode+"'  ";
                if(DBStatus.equals("1")){
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    Statement stmt = conn.createStatement();
                    stmt.execute(sql);
                    ResultSet rsData = stmt.getResultSet();
                    int i=0;
                    while (rsData.next()) {
                        String PhotoFile = rsData.getString("PhotoFile");
                        if(!PhotoFile.isEmpty()){
                            Blob test           = rsData.getBlob(1);
                            int blobl           = (int)test.length();
                            byte[] blobasbyte   = test.getBytes(1,blobl);
                           /* int j=0;
                            LImage=blobasbyte.length;
                            while(j<LImage){
                                publishProgress(j);
                                j++;
                            }*/
                            bmpItem             = BitmapFactory.decodeByteArray(blobasbyte,0,blobasbyte.length);
                            publishProgress(i);
                            i++;
                            z="success";
                        }else{
                            z="error";
                        }
                    }
                    rsData.close();
                }else if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", sql);
                    jsonReq.put("action", "select");
                    String response1 = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(response1);
                    String rsGroup = jsonRes.getString("hasil");
                    JSONArray jData = new JSONArray(rsGroup);
                    JSONObject rsData = null;
                    for (int i = 0; i < jData.length(); i++) {
                        rsData              = jData.getJSONObject(i);
                        String PhotoFile    = rsData.getString("PhotoFile");
                        final byte[] imgStr = Base64.decode(PhotoFile,Base64.DEFAULT);
                        bmpItem             = BitmapFactory.decodeByteArray(imgStr,0,imgStr.length);
                        z="success";
                    }
                }else if(DBStatus.equals("0")){
                    int i=0;
                    Cursor rsData=db.getQuery(sql);
                    while(rsData.moveToNext()){
                        String PhotoFile    = rsData.getString(0);
                        if(!PhotoFile.isEmpty()) {
                            final byte[] imgStr = Base64.decode(PhotoFile, Base64.DEFAULT);
                            /*int j = 0;
                            LImage = imgStr.length;
                            while (j < LImage) {
                                publishProgress(j);
                                j++;
                            }*/
                            bmpItem = BitmapFactory.decodeByteArray(imgStr, 0, imgStr.length);
                            publishProgress(i);
                            i++;
                            z="success";
                        }else{
                            z="error";
                        }
                    }
                }
                db.closeDB();
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return z;
        }
    }

    private void zoomImageFromThumb(final View thumbView, Bitmap bmpImage) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }
        //Drawable img=new BitmapDrawable(Bitmap.createScaledBitmap(bmpItem,120,125,true));
        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView)view. findViewById(
                R.id.ImgItem);
        expandedImageView.setImageBitmap(bmpImage);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        view.findViewById(R.id.lnImage)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }
}
