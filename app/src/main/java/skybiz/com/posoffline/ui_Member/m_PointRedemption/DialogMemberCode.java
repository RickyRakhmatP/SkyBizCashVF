package skybiz.com.posoffline.ui_Member.m_PointRedemption;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.MyKeyboard;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Item.ItemListing;
import skybiz.com.posoffline.ui_ItemGroup.ItemGroupList;
import skybiz.com.posoffline.ui_Listing.Listing;
import skybiz.com.posoffline.ui_Member.m_MemberList.MemberList;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 23/02/2018.
 */

public class DialogMemberCode extends DialogFragment {
    View view;
    Button btnOK;
    EditText txtPassword;
    MyKeyboard keyboard;
    String ItemCode,T_ype,UFrom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_password, container, false);
        btnOK=(Button)view.findViewById(R.id.btnOK);
        txtPassword=(EditText)view.findViewById(R.id.txtPassword);
        txtPassword.setHint("Key In Your Member Code");
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCode();
            }
        });
        keyboard=(MyKeyboard)view.findViewById(R.id.keyboard);

        txtPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtPassword.setInputType(0);
                txtPassword.setRawInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                InputConnection ic2=txtPassword.onCreateInputConnection(new EditorInfo());
                keyboard.setInputConnection(ic2);
                return false;
            }
        });

        initView();

        return view;
    }
    private  void initView(){
        InputConnection ic=txtPassword.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        txtPassword.setTextIsSelectable(true);
        txtPassword.setInputType(0);
        txtPassword.setRawInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        txtPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());

    }

    private void checkCode(){
        String Code = txtPassword.getText().toString();
        if (Code.isEmpty()) {
            Toast.makeText(getActivity(),"Member Code Cannot Empty", Toast.LENGTH_SHORT).show();
        } else {
            int d=Integer.parseInt(d_ay());
            int m=Integer.parseInt(m_onth());
            int y=Integer.parseInt(y_ear());
            int C_ode=Integer.parseInt(Code);
            int SecretCode=C_ode/d/m/y;
            CheckMember checkMember=new CheckMember(getActivity(),String.valueOf(SecretCode));
            checkMember.execute();
        }
    }

    public class CheckMember extends AsyncTask<Void,Void,String>{
        Context c;
        String RunNoCus;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
        String CusCode,CusName;

        public CheckMember(Context c, String RunNoCus) {
            this.c = c;
            this.RunNoCus = RunNoCus;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.checkmember();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("error")){
                Toast.makeText(c,"Invalid Member Code", Toast.LENGTH_SHORT).show();
            }else if(result.equals("success")){
                ((PointRedeem)c).retBF(CusCode,CusName);
                dismiss();
            }
        }
        private String checkmember(){
            try{
                z="error";
                DBAdapter db=new DBAdapter(c);
                db.openDB();
                Cursor cur = db.getGeneralSetup();
                while (cur.moveToNext()) {
                   String CurCode = cur.getString(1);
                }
                String querySet="select * from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(1);
                    UserName = curSet.getString(2);
                    Password = curSet.getString(3);
                    DBName = curSet.getString(4);
                    Port = curSet.getString(5);
                    DBStatus=curSet.getString(7);
                    ItemConn=curSet.getString(8);
                }

                String sql ="select CusCode,CusName from customer where RunNo='"+RunNoCus+"' ";
                Log.d("QUERY",sql);
                if(DBStatus.equals("1")){
                    URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn= Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement statement = conn.createStatement();
                        statement.execute(sql);
                        ResultSet rsCus = statement.getResultSet();
                        while (rsCus.next()) {
                            CusCode=rsCus.getString(1);
                            CusName=rsCus.getString(2);
                        }
                        statement.close();
                        z="success";
                    }
                }else if(DBStatus.equals("0")){
                    Cursor rsCus=db.getQuery(sql);
                    while(rsCus.moveToNext()){
                        CusCode=rsCus.getString(0);
                        CusName=rsCus.getString(1);
                    }
                    z="success";
                }
                db.closeDB();
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return z;
        }

    }


    public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;

            public PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }

            public char charAt(int index) {
                return '*'; // This is the important part
            }

            public int length() {
                return mSource.length(); // Return default
            }

            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
    }

    private String d_ay(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date date = new Date();
        String D_ay = sdf.format(date);
        return D_ay;
    }
    private String m_onth(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        Date date = new Date();
        String M_onth = sdf.format(date);
        return M_onth;
    }
    private String y_ear(){
        SimpleDateFormat sdf = new SimpleDateFormat("yy");
        Date date = new Date();
        String Y_ear = sdf.format(date);
        return Y_ear;
    }

}
