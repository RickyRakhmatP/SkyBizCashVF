package skybiz.com.posoffline.ui_Service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewItemList.DialogItem;
import skybiz.com.posoffline.m_NewSummary.DownloaderOrder;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_Service.m_List.DialogListJS;
import skybiz.com.posoffline.ui_Service.m_List.RetDetail;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class MService extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    RecyclerView rvOrder;
    private GridLayoutManager lLayout;
    boolean doubleBackToExitPressedOnce = false;

    EditText txtCaseType,txtRepairType,txtServiceStatus,
            txtTermCode,txtTermDesc,txtVendorTel,
            txtVendorName,txtVendorCode,txtCusCode,
            txtCusName,txtDoc1No,txtAddress,
            txtContact,txtContactTel,txtReceiveMode,
            txtReceiveNo,txtReceiveDate,
            txtEntryID,txtEntryDate,txtOutputID,txtOutputDate,txtProductModel,
            txtPartNumber,txtSupplierSerialNo,txtAccessories,
            txtProblemDesc, txtWarrantyDesc,txtCollectedBy,
            txtServiceNoteRemark, txtReturnBackBy,txtSerialNo,
            txtWarrantyExpDate,txtSendToVendorYN,txtBackFromVendorYN,
            txtReturnBackEndUserYN,txtActionTimeStart,txtActionTimeEnd,
            txtT_ime;
    Button btnSave;
    CheckBox chkSendToVendorYN,chkBackFromVendorYN,chkReturnBackEndUserYN;

    Spinner spWarrantyStatus,spVenWarrantyStatus,spPriority,spServiceStatus;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    ImageView imgService,imgService2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mservice);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Service");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);
        retLastNo();
      /*  mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));*/

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mservice, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mnItem) {
            Bundle b=new Bundle();
            b.putString("DOCTYPE_KEY","Service");
            DialogItem dialogItem = new DialogItem();
            dialogItem.setArguments(b);
            dialogItem.show(getSupportFragmentManager(), "mListItem");
            return true;
        }else if(id==R.id.mnOutJS){
            Bundle b = new Bundle();
            b.putString("STATUS_KEY", "Open");
            DialogListJS dialogListJS = new DialogListJS();
            dialogListJS.setArguments(b);
            dialogListJS.show(getSupportFragmentManager(), "List Service");
        }else if(id==R.id.mnClosedJS){
            Bundle b = new Bundle();
            b.putString("STATUS_KEY", "Closed");
            DialogListJS dialogListJS = new DialogListJS();
            dialogListJS.setArguments(b);
            dialogListJS.show(getSupportFragmentManager(), "List Service");
        }

        return super.onOptionsItemSelected(item);
    }





    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            switch (position) {
                case 0:
                    Service_Information tab1 = new Service_Information();
                    return tab1;
                case 1:
                    Repair_Parts tab2 = new Repair_Parts();
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Service Information";
                case 1:
                    return "Repair Parts";
            }
            return null;
        }
    }

    public void setCustomer(String CusCode, String CusName, String Address, String TermCode,String TermDesc){
        getSupportActionBar().setTitle(CusName);
        txtCusCode=(EditText)findViewById(R.id.txtCusCode) ;
        txtCusCode.setText(CusCode);
        txtCusName=(EditText)findViewById(R.id.txtCusName) ;
        txtCusName.setText(CusName);
        txtAddress=(EditText)findViewById(R.id.txtAddress) ;
        txtAddress.setText(Address);
        txtTermCode=(EditText)findViewById(R.id.txtTermCode) ;
        txtTermCode.setText(TermCode);
        txtTermDesc=(EditText)findViewById(R.id.txtTermDesc) ;
        txtTermDesc.setText(TermDesc);
    }
    public void setParticular(String DocType, String Particular){
        if(DocType.equals("CaseType")){
            txtCaseType=(EditText)findViewById(R.id.txtCaseType) ;
            txtCaseType.setText(Particular);
        }else if(DocType.equals("RepairType")){
            txtRepairType=(EditText)findViewById(R.id.txtRepairType) ;
            txtRepairType.setText(Particular);
        }else if(DocType.equals("ServiceStatus")){
           // txtServiceStatus=(EditText)findViewById(R.id.txtServiceStatus) ;
           // txtServiceStatus.setText(Particular);
        }
    }

    public void setD_ay(String TermCode,String TermDesc, String D_ay){
        txtTermCode=(EditText)findViewById(R.id.txtTermCode) ;
        txtTermCode.setText(TermCode);
        txtTermDesc=(EditText)findViewById(R.id.txtTermDesc) ;
        txtTermDesc.setText(TermDesc);
    }

    public void setVendor(String CusCode,String CusName,String Tel){
        txtVendorName=(EditText)findViewById(R.id.txtVendorName) ;
        txtVendorName.setText(CusName);
        txtVendorTel=(EditText)findViewById(R.id.txtVendorTel) ;
        txtVendorTel.setText(Tel);
        txtVendorCode=(EditText)findViewById(R.id.txtVendorCode) ;
        txtVendorCode.setText(CusCode);
    }

    private void retLastNo(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String Doc1No="";
            String vQuery="select Prefix,LastNo from sys_runno_dt where RunNoCode='Service' ";
            Cursor cRunNo = db.getQuery(vQuery);
            while (cRunNo.moveToNext()) {
                String Prefix= cRunNo.getString(0);
                String LastNo= cRunNo.getString(1);
                Doc1No=Prefix+LastNo;

            }
            getSupportActionBar().setSubtitle("Ref # : "+Doc1No);
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public void setOnClick(String result){
        try {
            JSONArray ja = new JSONArray(result);
            JSONObject jo = null;
            for (int i = 0; i < ja.length(); i++) {
                jo=ja.getJSONObject(i);
                txtDoc1No=(EditText)findViewById(R.id.txtDoc1No);
                txtDoc1No.setText(jo.getString("Doc1No"));
                txtCusCode=(EditText)findViewById(R.id.txtCusCode);
                txtCusCode.setText(jo.getString("cuscode"));
                txtCusName=(EditText)findViewById(R.id.txtCusName);
                txtCusName.setText(jo.getString("cusname"));
                txtContact=(EditText)findViewById(R.id.txtContact);
                txtContact.setText(jo.getString("Contact"));
                txtContactTel=(EditText)findViewById(R.id.txtContactTel);
                txtContactTel.setText(jo.getString("ContactTel"));
                txtAddress=(EditText)findViewById(R.id.txtAddress);
                txtAddress.setText( jo.getString("Address"));
                txtRepairType=(EditText)findViewById(R.id.txtRepairType);
                txtRepairType.setText(jo.getString("repairtype"));
                txtReceiveMode=(EditText)findViewById(R.id.txtReceiveMode);
                txtReceiveMode.setText(jo.getString("receivemode"));
                txtReceiveNo=(EditText)findViewById(R.id.txtReceiveNo);
                txtReceiveNo.setText(jo.getString("receiptno"));
                txtReceiveDate=(EditText)findViewById(R.id.txtReceiveDate);
                txtReceiveDate.setText(jo.getString("receiptdate"));
                txtCaseType=(EditText)findViewById(R.id.txtCaseType);
                txtCaseType.setText(jo.getString("casetype"));
                String ServiceStatus=jo.getString("servicestatus");
                setServiceStatus(ServiceStatus);
               // txtServiceStatus=(EditText)findViewById(R.id.txtServiceStatus);
                btnSave=(Button) findViewById(R.id.btnSave);
                if(ServiceStatus.equals("Closed")){
                    btnSave.setEnabled(false);
                    btnSave.setVisibility(View.GONE);
                }else{
                    btnSave.setEnabled(true);
                    btnSave.setVisibility(View.VISIBLE);
                }
                setPriority(jo.getString("Priority"));
                txtEntryID=(EditText)findViewById(R.id.txtEntryID);
                txtEntryID.setText(jo.getString("entryid"));
                txtEntryDate=(EditText)findViewById(R.id.  txtEntryDate);
                txtEntryDate.setText(jo.getString("d_ate"));
                txtOutputID=(EditText)findViewById(R.id.txtOutputID);
                txtOutputID.setText(jo.getString("outputid"));
                txtOutputDate=(EditText)findViewById(R.id.txtOutputDate);
                txtOutputDate.setText(jo.getString("outputdate"));
                txtProductModel=(EditText)findViewById(R.id.txtProductModel);
                txtProductModel.setText(jo.getString("productmodel"));
                txtPartNumber=(EditText)findViewById(R.id.txtPartNumber);
                txtPartNumber.setText(jo.getString("partno"));
                txtSerialNo=(EditText)findViewById(R.id.  txtSerialNo);
                txtSerialNo.setText(jo.getString("serialno"));
                txtSupplierSerialNo=(EditText)findViewById(R.id.txtSupplierSerialNo);
                txtSupplierSerialNo.setText(jo.getString("supplierserialno"));
                txtAccessories=(EditText)findViewById(R.id.  txtAccessories);
                txtAccessories.setText(jo.getString("accessories"));
                txtProblemDesc=(EditText)findViewById(R.id.txtProblemDesc);
                txtProblemDesc.setText(jo.getString("problemdesc"));
                txtTermCode=(EditText)findViewById(R.id.txtTermCode);
                txtTermCode.setText(jo.getString("termcode"));
                txtTermDesc=(EditText)findViewById(R.id.txtTermDesc);
                txtTermDesc.setText(jo.getString("termcode"));
                setWarranty(jo.getString("warrantystatus"));
                txtWarrantyExpDate=(EditText)findViewById(R.id.txtWarrantyExpDate);
                txtWarrantyExpDate.setText(jo.getString("warrantyexpirydate"));
                txtWarrantyDesc=(EditText)findViewById(R.id.txtWarrantyDesc);
                txtWarrantyDesc.setText(jo.getString("warrantydesc"));
                txtCollectedBy=(EditText)findViewById(R.id.txtCollectedBy);
                txtCollectedBy.setText(jo.getString("collectedby"));
                txtVendorCode=(EditText)findViewById(R.id.txtVendorCode);
                txtVendorCode.setText(jo.getString("vendorcode"));
                txtVendorName=(EditText)findViewById(R.id.txtVendorName);
                txtVendorName.setText(jo.getString("vendorname"));
                txtVendorTel=(EditText)findViewById(R.id.txtVendorTel);
                txtVendorTel.setText(jo.getString("vendortelno"));
                setWarranty2(jo.getString("vendorwarrantystatus"));
                txtServiceNoteRemark=(EditText)findViewById(R.id.txtServiceNoteRemark);
                txtServiceNoteRemark.setText(jo.getString("servicenoteremark"));
                setSendVendorYN(jo.getString("sendtovendorYN"));
                txtSendToVendorYN=(EditText)findViewById(R.id.txtSendToVendorYN);
                txtSendToVendorYN.setText(jo.getString("sendtovendordate"));
                setBackVendorYN(jo.getString("backfromvendorYN"));
                txtBackFromVendorYN=(EditText)findViewById(R.id.txtBackFromVendorYN);
                txtBackFromVendorYN.setText(jo.getString("backfromvendordate"));
                setReturnBackEndUserYN(jo.getString("returnbackenduserYN"));
                txtReturnBackEndUserYN=(EditText)findViewById(R.id.txtReturnBackEndUserYN);
                txtReturnBackEndUserYN.setText(jo.getString("returnbackenduserdate"));
                txtReturnBackBy=(EditText)findViewById(R.id.txtReturnBackBy);
                txtReturnBackBy.setText(jo.getString("returnbackenduserby"));
                getSupportActionBar().setSubtitle("Ref # : "+jo.getString("Doc1No"));
                getSupportActionBar().setTitle(jo.getString("cusname"));
                String PhotoFileName=jo.getString("PhotoFileName");
                imgService=(ImageView)findViewById(R.id.imgService);
                byte[] decodedString = Base64.decode(jo.getString("PhotoFile"), Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                if (bmp != null) {
                    Drawable image = new BitmapDrawable(Bitmap.createScaledBitmap(bmp, 580, 450, true));
                    imgService.setImageDrawable(image);
                }
                if(!PhotoFileName.isEmpty()){
                    imgService2=(ImageView)findViewById(R.id.imgService2);
                    imgService2.setImageResource(R.drawable.downloading);
                }
                txtActionTimeStart=(EditText)findViewById(R.id.txtActionTimeStart);
                txtActionTimeStart.setText(jo.getString("ActionTimeStart"));
                txtActionTimeEnd=(EditText)findViewById(R.id.txtActionTimeEnd);
                txtActionTimeEnd.setText(jo.getString("ActionTimeEnd"));
                txtT_ime=(EditText)findViewById(R.id.txtT_ime);
                txtT_ime.setText(jo.getString("T_ime"));
                RetDetail retDetail=new RetDetail(MService.this,jo.getString("Doc1No"));
                retDetail.execute();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    private void setReturnBackEndUserYN(String value){
        chkReturnBackEndUserYN=(CheckBox)findViewById(R.id.chkBackFromVendorYN);
        if(value.equals("1")){
            chkReturnBackEndUserYN.setChecked(true);
        }else{
            chkReturnBackEndUserYN.setChecked(false);
        }
    }
    private void setBackVendorYN(String value){
        chkBackFromVendorYN=(CheckBox)findViewById(R.id.chkBackFromVendorYN);
        if(value.equals("1")){
            chkBackFromVendorYN.setChecked(true);
        }else{
            chkBackFromVendorYN.setChecked(false);
        }
    }
    private void setSendVendorYN(String value){
        chkSendToVendorYN=(CheckBox)findViewById(R.id.chkSendToVendorYN);
        if(value.equals("1")){
            chkSendToVendorYN.setChecked(true);
        }else{
            chkSendToVendorYN.setChecked(false);
        }
    }
    private void setPriority(String Priority){
        spPriority=(Spinner) findViewById(R.id.spPriority);
        ArrayList<String> list=new ArrayList( Arrays.asList(getResources().getStringArray(R.array.priority_array)) );
        int pos= list.indexOf(Priority);
        spPriority.setSelection(pos);
    }

    private void setServiceStatus(String ServiceStatus){
        spServiceStatus=(Spinner) findViewById(R.id.spServiceStatus);
        ArrayList<String> list=new ArrayList( Arrays.asList(getResources().getStringArray(R.array.srstatus_array)) );
        int pos= list.indexOf(ServiceStatus);
        spServiceStatus.setSelection(pos);
    }

    private void setWarranty(String warranty){
        if(warranty.equals("1")){
            warranty="IN WARRANTY";
        }else{
            warranty="NO WARRANTY";
        }
        spWarrantyStatus=(Spinner) findViewById(R.id.spWarrantyStatus);
        ArrayList<String> list=new ArrayList( Arrays.asList(getResources().getStringArray(R.array.warranty_array)) );
        int pos= list.indexOf(warranty);
        spWarrantyStatus.setSelection(pos);
    }


    private void setWarranty2(String warranty){
        if(warranty.equals("1")){
            warranty="IN WARRANTY";
        }else{
            warranty="NO WARRANTY";
        }
        spVenWarrantyStatus=(Spinner) findViewById(R.id.spWarrantyStatus);
        ArrayList<String> list=new ArrayList( Arrays.asList(getResources().getStringArray(R.array.warranty_array)) );
        int pos= list.indexOf(warranty);
        spVenWarrantyStatus.setSelection(pos);
    }

    public void refreshNext() {
        finish();
        Intent mainIntent = new Intent(MService.this, MService.class);
        startActivity(mainIntent);
    }
    public void setImgService(Bitmap bmp){
        if (bmp != null) {
            Drawable image = new BitmapDrawable(Bitmap.createScaledBitmap(bmp, 580, 450, true));
            imgService2.setImageDrawable(image);
        }

    }
    public void refreshOrder(){
        rvOrder=(RecyclerView)findViewById(R.id.rec_order);
        rvOrder.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 1);
        rvOrder.setLayoutManager(lLayout);
        rvOrder.setItemAnimator(new DefaultItemAnimator());
        DownloaderOrder order=new DownloaderOrder(this,"Service",rvOrder,this.getSupportFragmentManager());
        order.execute();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
