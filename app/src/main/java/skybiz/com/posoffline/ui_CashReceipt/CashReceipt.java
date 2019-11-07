package skybiz.com.posoffline.ui_CashReceipt;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import skybiz.com.posoffline.DialogForceSync;
import skybiz.com.posoffline.GlobalApplication;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_Connection.ConnectivityReceiver;
import skybiz.com.posoffline.m_Ipay88.DialogPrint;
import skybiz.com.posoffline.m_NFC.DialogNFC;
import skybiz.com.posoffline.m_NewItemList.DialogItem;
//import skybiz.com.posoffline.m_NewReprint.DialogReprint;
import skybiz.com.posoffline.m_NewSalesPerson.DialogSalesPerson;
import skybiz.com.posoffline.m_PaymentNote.DialogPaymentNote;
import skybiz.com.posoffline.ui_CashReceipt.m_Customer.DialogCustomer;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Order;
import skybiz.com.posoffline.ui_CashReceipt.m_GuestCheck.DialogGuest;
import skybiz.com.posoffline.ui_CashReceipt.m_Item.DownloaderItem;
import skybiz.com.posoffline.ui_CashReceipt.m_Item.DownloaderItemLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Item.ItemAdapter;
import skybiz.com.posoffline.ui_CashReceipt.m_ListSO.AddDish;
import skybiz.com.posoffline.ui_CashReceipt.m_Misc.DownloaderMisc;
import skybiz.com.posoffline.ui_CashReceipt.m_Modifier.DialogModifier;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItem_BySearch;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddMisc;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.Dialog_Qty;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.DownloaderOrder;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.OrderAdapter;
import skybiz.com.posoffline.ui_CashReceipt.m_OrderSlip.Dialog_OrderSlip;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.FnAddOnOrder;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.FnSaveOrder;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.SaveCS;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_IposAIDL.Utils.IposAidlUtil;
//import skybiz.com.posoffline.ui_CashReceipt.m_Reprint.DialogReprint;
import skybiz.com.posoffline.ui_CashReceipt.m_Reprint.DialogReprint;
import skybiz.com.posoffline.ui_CashReceipt.m_Reprint.ReprintLast;
import skybiz.com.posoffline.ui_CashReceipt.m_VoidBill.DialogVoid;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;
import skybiz.com.posoffline.ui_TopUpNFC.TopUpNFC;

public class CashReceipt extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener{

    private GridLayoutManager lLayout;
    ItemAdapter adapter;
    OrderAdapter orderAdapter;
    ArrayList<Spacecraft> items=new ArrayList<>();
    ArrayList<Spacecraft_Order> orders=new ArrayList<>();
    SearchView sv;
    RecyclerView rv,rvOrder;
    Toolbar toolbar;
    Double dServiceCharges=0.00;


    private static final String TAG = CashReceipt.class.getSimpleName();
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mTechList;
    private boolean mDebug = true;
    private boolean mShowDataAsHexString;
    private boolean mReadAll=false,mWriteAll;
    private String mCharset = "ISO-8859-1";
    String mReadBlockIndex="9",
            mWriteBlockContent,
            mWriteBlockIndex,
            BalanceAmount="0.00",
            vDateTime,HidePaymentYN="0";

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
    private ViewPager mViewPager2;
    String GSTYN="";


    String TransId,Amount,PayType,MerchantCode,
            D_ateTime,ResultPrint,RefNo,Doc2No,MerchantKey,PaymentCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_receipt);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cash Receipt");
        checkGSTYN();
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                TransId= null;
            } else {
                TransId= extras.getString("TRANSID_KEY");
                Amount= extras.getString("AMOUNT_KEY");
                PayType= extras.getString("PAYTYPE_KEY");
                PaymentCode= extras.getString("PAYMENTCODE_KEY");
                MerchantCode= extras.getString("MERCHANTCODE_KEY");
                MerchantKey= extras.getString("MERCHANTKEY_KEY");
                RefNo= extras.getString("REFNO_KEY");
                Doc2No= extras.getString("DOC2NO_KEY");
                //openPrint(TransId, Amount, PayType, MerchantCode, RefNo);
                if(PayType.equals("Gift Card")) {
                    openDialogNFC();
                }else if(PayType.equals("New Order")){
                    CreateOrder(RefNo);
                } else if(PayType.equals("Add On")){
                    CreateAddOn(RefNo);
                } else if(PayType.equals("Add Dish")){
                    CreateAddDish(RefNo);
                } else if(PayType.equals("Sales Person")){
                    CreateSalesPerson(RefNo);
                } else if(PayType.equals("Search Item")){
                    SearchItem(RefNo);
                } else if(PayType.equals("Search Customer")){
                    SetCustomer(RefNo);
                }else{
                    openPrint(TransId, Amount, PayType, MerchantCode, RefNo);
                }
            }
        } else {
            TransId= (String) savedInstanceState.getSerializable("TRANSID_KEY");
            Amount= (String) savedInstanceState.getSerializable("AMOUNT_KEY");
            PayType= (String) savedInstanceState.getSerializable("PAYTYPE_KEY");
            PaymentCode=(String) savedInstanceState.getSerializable("PAYMENTCODE_KEY");
            MerchantCode= (String) savedInstanceState.getSerializable("MERCHANTCODE_KEY");
            MerchantKey= (String) savedInstanceState.getSerializable("MERCHANTKEY_KEY");
            RefNo= (String) savedInstanceState.getSerializable("REFNO_KEY");
            Doc2No= (String) savedInstanceState.getSerializable("DOC2NO_KEY");
            if(PayType.equals("Gift Card")) {
                openDialogNFC();
            }else if(PayType.equals("New Order")){
                CreateOrder(RefNo);
            } else if(PayType.equals("Add On")){
                CreateAddOn(RefNo);
            } else if(PayType.equals("Add Dish")){
                CreateAddDish(RefNo);
            } else if(PayType.equals("Sales Person")){
                CreateSalesPerson(RefNo);
            } else if(PayType.equals("Search Item")){
                SearchItem(RefNo);
            } else if(PayType.equals("Search Customer")){
                SetCustomer(RefNo);
            }else {
                openPrint(TransId, Amount, PayType, MerchantCode, RefNo);
            }
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        sv =(SearchView)findViewById(R.id.search);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                if(position==1){
                    fncheckmodules();
                }
            }
            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               /* Toast.makeText(CashReceipt.this,
                        "Scroll page position: " + position, Toast.LENGTH_SHORT).show();*/
                // Code goes here
                if(position==1){
                    fncheckmodules();
                }
            }
            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });
        checkConnection();
        initNFC();
    }
    private void initNFC(){
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            //this.finish();
            Toast.makeText(CashReceipt.this,
                     "Not Support NFC", Toast.LENGTH_SHORT).show();
            return;
        }else {
            mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
            try {
                filter.addDataType("*/*");
            } catch (IntentFilter.MalformedMimeTypeException e) {
                e.printStackTrace();
            }
            mIntentFilters = new IntentFilter[]{filter};
            mTechList = new String[][]{new String[]{MifareClassic.class.getName()}};
        }
    }

    public void fncheckmodules(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String qModule="select Modules,CompCustomerYN from tb_setting";
            Cursor rsModule=db.getQuery(qModule);
            String lisModule="";
            String CompCustomerYN="0";
            while(rsModule.moveToNext()){
                lisModule=rsModule.getString(0);
                CompCustomerYN=rsModule.getString(1);
            }
            Log.d("CompCustomerYN",CompCustomerYN);
            if(CompCustomerYN.equals("1")){
                checkMember();
            }

            String qOtherSet="select ServiceCharges from tb_othersetting";
            Cursor rsOther=db.getQuery(qOtherSet);
            while(rsOther.moveToNext()){
                dServiceCharges=rsOther.getDouble(0);
            }
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    public void setPayment(){
        mViewPager.setCurrentItem(2);
    }
    private void checkMember(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String countM="select count(*)as numrows from tb_member ";
            Cursor rsCount=db.getQuery(countM);
            int numMember=0;
            while(rsCount.moveToNext()){
                numMember=rsCount.getInt(0);
            }
            if(numMember==0){
                Toast.makeText(CashReceipt.this,
                        "Compulsory Customer !", Toast.LENGTH_SHORT).show();
               mViewPager.setCurrentItem(0);
            }
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    private void checkGSTYN(){
        try {
            DBAdapter db = new DBAdapter(this);
            db.openDB();
            String checkGST = "select GSTNo from companysetup";
            Cursor rsGST = db.getQuery(checkGST);
            while (rsGST.moveToNext()) {
                GSTYN = rsGST.getString(0);
            }
            String qMember = "select CusCode,CusName from tb_member";
            Cursor rsMember = db.getQuery(qMember);
            String CusCode = "";
            String CusName = "";
            while (rsMember.moveToNext()) {
                setCustomerBar();
            }
            String TypePrinter = "";
            Cursor cPrinter = db.getSettingPrint();
            while (cPrinter.moveToNext()) {
                TypePrinter = cPrinter.getString(1);
            }
            if (TypePrinter.equals("AIDL")) {
                AidlUtil.getInstance().connectPrinterService(this);
                AidlUtil.getInstance().initPrinter();
            } else if (TypePrinter.equals("Ipos AIDL")) {
                IposAidlUtil.getInstance().connectPrinterService(this);
                // IposAidlUtil.getInstance().initPrinter();
                //
            } else {

            }

            String chkPayment="select HidePaymentYN from tb_othersetting";
            Cursor rsPay=db.getQuery(chkPayment);
            while(rsPay.moveToNext()){
                HidePaymentYN=rsPay.getString(0);
            }
            Log.d("Hide Payment",HidePaymentYN);
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    public void setCustomerBar(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String qMember="select CusCode,CusName from tb_member";
            Cursor rsMember=db.getQuery(qMember);
            String CusCode="";
            String CusName="";
            String SalesPersonCode="";
            while(rsMember.moveToNext()){
                CusCode=rsMember.getString(0);
                CusName=rsMember.getString(1);
            }
            if(!CusCode.isEmpty()){
                getSupportActionBar().setTitle(CusCode+"|"+CusName);
            }
            String qSales="select SalesPersonCode from tb_salesperson";
            Cursor rsSales=db.getQuery(qSales);
            while(rsSales.moveToNext()){
                SalesPersonCode=rsSales.getString(0);
            }
            if(!SalesPersonCode.isEmpty()){
                getSupportActionBar().setTitle(SalesPersonCode+" |"+CusCode+"|"+CusName);
            }
            db.closeDB();
            //getSupportActionBar().setTitle(CusCode+"|"+CusName);
        }catch (SQLiteException e){
            e.printStackTrace();
        }

    }
    public void setSubTitle(String Doc2No){
        getSupportActionBar().setSubtitle("Table : "+Doc2No);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cash_receipt, menu);
        if(GSTYN.equals("NO")){
            menu.findItem(R.id.mnVoidBill).setVisible(true);
        }else{
            menu.findItem(R.id.mnVoidBill).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.mnCustomer) {
            DialogCustomer dialogCustomer = new DialogCustomer();
            dialogCustomer.show(getSupportFragmentManager(), "mListCustomer");
            return true;
        }else if (id == R.id.mnSalesPerson) {
            Bundle b=new Bundle();
            b.putString("DOCTYPE_KEY","CS");
            DialogSalesPerson dialogSalesPerson = new DialogSalesPerson();
            dialogSalesPerson.setArguments(b);
            dialogSalesPerson.show(getSupportFragmentManager(), "mListSalesPerson");
            return true;
        }else if (id == R.id.mnItem) {
            Bundle b=new Bundle();
            b.putString("DOCTYPE_KEY","CS");
            DialogItem dialogItem = new DialogItem();
            dialogItem.setArguments(b);
            dialogItem.show(getSupportFragmentManager(), "mListItem");
            return true;
        }else if(id==R.id.mnGuestCheck){
            DialogGuest dialogGuest = new DialogGuest();
            dialogGuest.show(getSupportFragmentManager(), "mGuest");
            return true;
        }else if(id==R.id.mnOrderSlip){
            Dialog_OrderSlip orderSlip = new Dialog_OrderSlip();
            orderSlip.show(getSupportFragmentManager(), "mOrderSlip");
            return true;
        }else if(id==R.id.mnReprint){
           /* Bundle b = new Bundle();
            b.putString("DOCTYPE_KEY", "CS");
            DialogReprint dialogReprint = new DialogReprint();
            dialogReprint.setArguments(b);
            dialogReprint.show(getSupportFragmentManager(), "mListReprint");*/
            DialogReprint dialogReprint = new DialogReprint();
            dialogReprint.show(getSupportFragmentManager(), "mListReprint");
            return true;
        }else if(id==R.id.mnReprint2){
            ReprintLast reprintLast=new ReprintLast(CashReceipt.this);
            reprintLast.execute();
            return true;
        }else if(id==R.id.mnReset){
            deleteOrder();
            return true;
        }else if(id==R.id.mnVoidBill) {
            DialogVoid dialogVoid = new DialogVoid();
            dialogVoid.show(getSupportFragmentManager(), "mListVoid");
            return true;
        }else if(id==R.id.mnPaymentNote) {
            DialogPaymentNote dialogPaymentNote = new DialogPaymentNote();
            dialogPaymentNote.show(getSupportFragmentManager(), "mPaymentNote");
            return true;
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
            if(HidePaymentYN.equals("1")) {
                switch (position) {
                    case 0:
                        Fragment_Summary tab0 = new Fragment_Summary();
                        return tab0;
                    case 1:
                        Fragment_Main tab1 = new Fragment_Main();
                        return tab1;
                    default:
                        return null;
                }
            }else{
                switch (position) {
                    case 0:
                        Fragment_Summary tab0 = new Fragment_Summary();
                        return tab0;
                    case 1:
                        Fragment_Main tab1 = new Fragment_Main();
                        return tab1;
                    case 2:
                        Fragment_Payment tab2 = new Fragment_Payment();
                        return tab2;
                    default:
                        return null;
                }
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            if(HidePaymentYN.equals("1")){
                return 2;
            }else {
                return 3;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(HidePaymentYN.equals("1")) {
                switch (position) {
                    case 0:
                        return "ORDER SUMMARY";
                    case 1:
                        return "ITEMS";
                }
            }else{
                switch (position) {
                    case 0:
                        return "ORDER SUMMARY";
                    case 1:
                        return "ITEMS";
                    case 2:
                        return "PAYMENT";
                }
            }
            return null;
        }
    }

    public void deleteOrder(){
        resetOrder resetOrder=new resetOrder(this);
        resetOrder.execute();
    }
    public void refreshNext(){
        finish();
        Intent mainIntent = new Intent(CashReceipt.this, CashReceipt.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }
    public void setActiveTab(Integer noTab){
        TabHost host = (TabHost)findViewById(android.R.id.tabhost);
        host.setCurrentTab(noTab);
    }
    public void refreshContent(String ItemGroup){
    }

    public void showEdit(String jsonData){
        Bundle b=new Bundle();
        b.putString("JSONDATA_KEY",jsonData);
        Dialog_Qty dialogQty=new Dialog_Qty();
        dialogQty.setArguments(b);
        dialogQty.show(getSupportFragmentManager(),"mTag");
    }
    public void showModifier(String ItemCode,String ItemGroup, String Printer,
                             String HCDiscount, String DisRate1, String Point){
        Bundle b=new Bundle();
        DialogModifier dialogModifier=new DialogModifier();
        b.putString("ITEMCODE_KEY",ItemCode);
        b.putString("ITEMGROUP_KEY",ItemGroup);
        b.putString("PRINTER_KEY",Printer);
        b.putString("HCDISCOUNT_KEY",HCDiscount);
        b.putString("DISRATE1_KEY",DisRate1);
        b.putString("POINT_KEY",Point);
        dialogModifier.setArguments(b);
        dialogModifier.show(getSupportFragmentManager(),"mModifier");
    }
    public void retItem(String ItemGroup){
        Log.d("ITEMGROUP",ItemGroup);
        rv=(RecyclerView)findViewById(R.id.rec_list);
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 2);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        if(ItemGroup.equals("Miscellaneous")){
            DownloaderMisc dMisc = new DownloaderMisc(CashReceipt.this, ItemGroup, rv);
            dMisc.execute();
        }else {
            DownloaderItem dItem = new DownloaderItem(CashReceipt.this, ItemGroup, rv);
            dItem.execute();
        }
    }

    public void refreshOrder(){
        rvOrder=(RecyclerView)findViewById(R.id.rec_order);
        rvOrder.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 1);
        rvOrder.setLayoutManager(lLayout);
        rvOrder.setItemAnimator(new DefaultItemAnimator());
        DownloaderOrder dOrder=new DownloaderOrder(this,rvOrder,this.getSupportFragmentManager());
        dOrder.execute();
    }

    public class resetOrder extends AsyncTask<Void,Void,String>{
        Context c;
        String z,IPAddress,DBStatus;
        TelephonyManager telephonyManager;

        public resetOrder(Context c) {
            this.c = c;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.delOrder();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("error")){
                Toast.makeText(c,"error, delete summary", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(c,"success, delete summary", Toast.LENGTH_SHORT).show();
                refreshNext();
            }
        }

        private String delOrder() {
            try {
                JSONObject jsonReq,jsonRes;
                //telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
               // String deviceId = telephonyManager.getDeviceId();
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName,DBStatus,UserCode from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                String UserCode="";
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(0);
                    DBStatus=curSet.getString(1);
                    UserCode=curSet.getString(2);
                }
                if(DBStatus.equals("2")){
                    String qDel="delete from dum_stk_sales_order_hd";
                    db.addQuery(qDel);
                    String qDelM="delete from tb_member";
                    db.addQuery(qDelM);
                    String qDelSP="delete from tb_salesperson";
                    db.addQuery(qDelSP);
                    String vDel="delete from cloud_cus_inv_dt where ComputerName='"+UserCode+"' ";
                    jsonReq=new JSONObject();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", vDel);
                    jsonReq.put("action", "delete");
                    ConnectorLocal connectorLocal=new ConnectorLocal();
                    String response=connectorLocal.ConnectSocket(IPAddress,8080,jsonReq.toString());
                    jsonRes = new JSONObject(response);
                    String result=jsonRes.getString("hasil");
                    z=result;
                }else{
                    String vDel="delete from cloud_cus_inv_dt";
                    db.addQuery(vDel);

                    String qDel="delete from dum_stk_sales_order_hd";
                    db.addQuery(qDel);

                    String qDelM="delete from tb_member";
                    db.addQuery(qDelM);

                    String qDelSP="delete from tb_salesperson";
                    db.addQuery(qDelSP);
                    z="success";
                }
                db.closeDB();
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return z;
        }
    }

    private void openDialogNFC(){
        SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date               = new Date();
        vDateTime               = sdf.format(date);
        Bundle b                = new Bundle();
        b.putString("AMOUNT_KEY", Amount);
        b.putString("DOC1NO_KEY", RefNo);
        b.putString("DOC2NO_KEY", Doc2No);
        b.putString("PAYTYPE_KEY", PayType);
        b.putString("MERCHANTKEY_KEY", MerchantKey);
        DialogNFC dialogNFC      = new DialogNFC();
        dialogNFC.setArguments(b);
        dialogNFC.show(getSupportFragmentManager(), "mNFC");
    }

    public void setPayment(String RefId,String AmountDue, String PayType){
        String Doc1No=RefId;
        String Doc2No="";
        String Doc3No="";
        Double ChangeAmt=0.00;
        Double CC1Amt=0.00;
        Double BalanceAmt=0.00;
        String CC1No="";
        Double CashAmt=Double.parseDouble(AmountDue);
        if(PayType.equals("NFC01")){
             CC1Amt=CashAmt;
            CashAmt=0.00;
        }
        SaveCS fnsave = new SaveCS(this, "CR",Doc1No, Doc2No, Doc3No,
                    PayType, CC1No, CashAmt,
                    ChangeAmt, CC1Amt, BalanceAmt,
                    "0.00", "1");
        fnsave.execute();

    }

    public void openPrint(String TransId, String Amount, String PayType,String MerchantCode,String Doc1No){
        Bundle b=new Bundle();
        b.putString("TRANSID_KEY",TransId);
        b.putString("AMOUNT_KEY",Amount);
        b.putString("PAYTYPE_KEY",PayType);
        b.putString("MERCHANTCODE_KEY",MerchantCode);
        b.putString("REFNO_KEY",Doc1No);
        DialogPrint dialogPrint = new DialogPrint();
        dialogPrint.setArguments(b);
        dialogPrint.show(getSupportFragmentManager(), "mListItem");
    }

    @Override
    protected void onResume() {
        super.onResume();
        GlobalApplication.getInstance().setConnectivityListener(this);
        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mTechList);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (!NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            return;
        }
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        debug("resolveIntent...", false);
        if (mReadBlockIndex == null && mWriteBlockContent == null && !mReadAll) {
            debug(getString(R.string.toast_need_data), true);
            return;
        }
        MifareClassic mfc = MifareClassic.get((Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
        if (mfc == null) {
            debug(getString(R.string.toast_not_mifare_classic), true);
            return;
        }
        try {
            String result1 = performRead(mfc);
            if (result1 != null) {
                if (mReadAll) {
                    fncalcbalance(result1);
                   // debug(result1,true);
                } else {
                    fncalcbalance(result1);
                   // debug(result1, true);
                   // if(mReadBlockIndex.equals("9")){
                     //   initNFC();
                  //  }
                }
            }

            List<Integer> result2 = performWrite(mfc);
            if (result2 != null) {
                if (result2.size() == 0) {
                    debug(getString(R.string.toast_write_success), true);
                    setPayment(RefNo,Amount,PaymentCode);
                } else if (result2.size() == 1 && !mReadAll) {
                    debug(getString(R.string.toast_write_fail), true);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(getString(R.string.result_failed_the_following));
                    sb.append(LINE_SEPARATOR);
                    for (int i = 0; i < result2.size(); i++) {
                        sb.append(result2.get(i));
                        if (i != 0 && i % 5 == 0) {
                            sb.append(LINE_SEPARATOR);
                        } else {
                            sb.append(", ");
                        }
                    }
                    sb.delete(sb.length() - 2, sb.length());
                    alert(sb.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //enableAllOptions();
            try {
                mfc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void debug(String info, boolean toast) {
        if (mDebug) {
            Log.d(TAG, info);
            if (toast) {
                showToast(info);
            }
        }
    }

    private void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    private String performRead(MifareClassic mfc) throws IOException {
        if (mReadBlockIndex == null && !mReadAll) {
            return null;
        }
        if (!mfc.isConnected()) {
            mfc.connect();
        }
        String ret = null;

        if(mReadAll) {
            int blockCount = mfc.getBlockCount();
            boolean auth;
            for (int i = 1; i < blockCount; i++) {
                if ((i + 1) % 4 == 0) {
                    continue;
                }
                mReadBlockIndex = String.valueOf(i);
                auth = mfc.authenticateSectorWithKeyA(mfc.blockToSector(i), MifareClassic.KEY_DEFAULT);
                if (auth) {
                    byte[] data = readBlock(mfc, i);
                    ret = convertBytes2String(data);
                }

               /* int blockIndex = i;
                mReadBlockIndex = String.valueOf(blockIndex);
                if(i==2) {
                    int sectorIndex = mfc.blockToSector(blockIndex);
                    boolean auth = mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_DEFAULT);
                    if (auth) {
                        byte[] data = readBlock(mfc, blockIndex);
                        ret = convertBytes2String(data);
                    } else {
                        debug(getString(R.string.auth_failed), true);
                    }
                }
                if(i==9) {
                    int sectorIndex = mfc.blockToSector(blockIndex);
                    boolean auth = mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_DEFAULT);
                    if (auth) {
                        byte[] data = readBlock(mfc, blockIndex);
                        ret = convertBytes2String(data);
                    } else {
                        debug(getString(R.string.auth_failed), true);
                    }
                }*/

                /*if(mReadBlockIndex.equals("2")) {
                    int sectorIndex = mfc.blockToSector(blockIndex);
                    boolean auth = mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_DEFAULT);
                    if (auth) {
                        byte[] data = readBlock(mfc, blockIndex);
                        ret = convertBytes2String(data);
                    } else {
                        debug(getString(R.string.auth_failed), true);
                    }
                }

                if(mReadBlockIndex.equals("9")) {
                    int sectorIndex = mfc.blockToSector(blockIndex);
                    boolean auth = mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_DEFAULT);
                    if (auth) {
                        byte[] data = readBlock(mfc, blockIndex);
                        ret = convertBytes2String(data);
                    } else {
                        debug(getString(R.string.auth_failed), true);
                    }
                }*/

            }
           /* int blockIndex = Integer.parseInt("2");
            if (!validateBlockIndex(mfc, blockIndex)) {
                debug(getString(R.string.err_block_index_out_of_bound), true);
                return ret;
            }
            int sectorIndex = mfc.blockToSector(blockIndex);
            boolean auth = mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_DEFAULT);
            if (auth) {
                byte[] data = readBlock(mfc, blockIndex);
                ret = convertBytes2String(data);
            } else {
                debug(getString(R.string.auth_failed), true);
            }*/
        }else if(mReadBlockIndex.equals("2")){
            int blockIndex = Integer.parseInt("2");
            if (!validateBlockIndex(mfc, blockIndex)) {
                debug(getString(R.string.err_block_index_out_of_bound), true);
                return ret;
            }
            int sectorIndex = mfc.blockToSector(blockIndex);
            boolean auth = mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_DEFAULT);
            if (auth) {
                byte[] data = readBlock(mfc, blockIndex);
                ret = convertBytes2String(data);
            } else {
                debug(getString(R.string.auth_failed), true);
            }
        }else if(mReadBlockIndex.equals("9")){
            int blockIndex = Integer.parseInt("9");
            if (!validateBlockIndex(mfc, blockIndex)) {
                debug(getString(R.string.err_block_index_out_of_bound), true);
                return ret;
            }
            int sectorIndex = mfc.blockToSector(blockIndex);
            boolean auth = mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_DEFAULT);
            if (auth) {
                byte[] data = readBlock(mfc, blockIndex);
                ret = convertBytes2String(data);
            } else {
                debug(getString(R.string.auth_failed), true);
            }
            /*int blockIndex = Integer.parseInt("1");
            if (!validateBlockIndex(mfc, blockIndex)) {
                debug(getString(R.string.err_block_index_out_of_bound), true);
                return ret;
            }
            int sectorIndex = mfc.blockToSector(blockIndex);
            boolean auth = mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_DEFAULT);
            if (auth) {
                byte[] data = readBlock(mfc, blockIndex);
                ret = convertBytes2String(data);
            } else {
                debug(getString(R.string.auth_failed), true);
            }*/
        }
        return ret;
    }

    private List<Integer> performWrite(MifareClassic mfc) throws IOException {
        if (mWriteBlockContent == null) {
            return null;
        }
        if (!mfc.isConnected()) {
            mfc.connect();
        }

        List<Integer> ret = new ArrayList<Integer>();
        String content = "";
        if (mWriteAll) {
            for (int i = 9; i < 16; i++) {
                if ((i + 1) % 4 == 0) {
                    continue;
                }
                if(i==9){
                    content=BalanceAmount;
                }
                if(i==10){
                    content=RefNo;
                }
                if((i==11)){
                    content="Sale";
                }
                if((i==14)){
                    content=vDateTime;
                }
                if((i==15)){
                    content=Amount;
                }
                boolean auth = mfc.authenticateSectorWithKeyA(mfc.blockToSector(i), MifareClassic.KEY_DEFAULT);
                if (auth) {
                    writeBlock(mfc, i, convertString2Bytes(content));
                } else {
                    ret.add(i);
                    debug(getString(R.string.auth_failed), true);
                }
            }
        }
        return ret;
    }

    private byte[] readBlock(MifareClassic mfc, int blockIndex) throws IOException {
        return mfc.readBlock(blockIndex);
    }

    private void writeBlock(MifareClassic mfc, int blockIndex, byte[] data) throws IOException {
        mfc.writeBlock(blockIndex, data);
    }

    private String convertBytes2String(byte[] data) throws UnsupportedEncodingException {
        String ret;
        if (mShowDataAsHexString) {
            StringBuilder sb = new StringBuilder();
            for (byte b : data) {
                int i = (int) b;
                sb.append(Integer.toHexString(i).toUpperCase());
            }
            ret = sb.toString();
        } else {
            int pos = data.length;
            for (int i = data.length - 1; i >= 0; i--) {
                if (data[i] != 0) {
                    break;
                }
                pos = i;
            }
            ret = new String(data, 0, pos, mCharset);
        }
        return ret;
    }

    private byte[] convertString2Bytes(String content) throws UnsupportedEncodingException {
        byte[] ret = new byte[16];
        byte[] buf = content.getBytes(mCharset);
        int retLen = ret.length;
        int bufLen = buf.length;
        boolean b = retLen > bufLen;

        for (int i = 0; i < retLen; i++) {
            if (b && i >= bufLen) {
                ret[i] = 0;
                continue;
            }
            ret[i] = buf[i];
        }
        return ret;
    }

    private String getText(EditText et) {
        if (TextUtils.isEmpty(et.getText())) {
            return null;
        }
        return et.getText().toString().trim();
    }

    private int randomBlockIndex(MifareClassic mfc) {
        int i = new Random().nextInt(mfc.getBlockCount());
        if (i == 0 || (i + 1) % 4 == 0) {
            return randomBlockIndex(mfc);
        }
        return i;
    }

    private boolean validateBlockIndex(MifareClassic mfc, int blockIndex) {
        if (blockIndex >= mfc.getBlockCount()) {
            return false;
        }
        return true;
    }
    private void alert(String msg) {
        new AlertDialog.Builder(this).setMessage(msg).show();
    }
    private void fncalcbalance(String result){
        switch (mReadBlockIndex) {
            case "2":
                String CusCode      = result;
                Log.d("BLOCK 2", CusCode);
                if(CusCode.isEmpty()){
                    Toast.makeText(this, "Invalid Gift Card!", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    if (checkCustomer(CusCode)) {
                        mWriteAll            = true;
                        mWriteBlockContent  = "write";
                    } else {
                        mWriteAll = false;
                        Toast.makeText(this, "Customer Not Found!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
            case "9":
                //Toast.makeText(this, "Insufficient Credit Available: "+result, Toast.LENGTH_SHORT).show();
                //Log.d("BLOCK 9", result);
               String oldBalance    =result;
               Double dOldBalance   =Double.parseDouble(oldBalance);
               Double dAmount        =Double.parseDouble(Amount);
               Double dBalance       =dOldBalance-dAmount;
               if (dBalance >= 0) {
                   mReadBlockIndex  = "2";
                   mWriteAll        = false;
                   BalanceAmount     =String.format(Locale.US, "%,.2f", dBalance);
                   Toast.makeText(this, "Credit Available: "+result+", Please tap again to confirm your payment!", Toast.LENGTH_SHORT).show();
               } else {
                   mWriteAll = false;
                   Toast.makeText(this, "Insufficient Credit Available! "+result, Toast.LENGTH_SHORT).show();
                   return;
               }
               break;
        }
    }

    private Boolean checkCustomer(String CusCode){
        Boolean isCustomer=false;
        String result="";
        CheckCustomer check=new CheckCustomer(this,CusCode);
        try {
            result= check.execute().get();
            if(result.equals("success")){
                isCustomer=true;
            }else{
                isCustomer=false;
            }
            return isCustomer;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return isCustomer;
    }

    private class CheckCustomer extends AsyncTask<Void,Void,String>{
        Context c;
        String CusCode,CusName,SalesPersonCode,
                MembershipClass,TermCode,D_ay,
                ContactTel,CategoryCode,RatioPoint,
                RatioAmount,Email;

        String IPAddress,Password,CurCode,
                ItemConn,URL,UserName,
                DBName,DBStatus,EncodeType,
                Port,z,UserCode,
                CounterCode,D_ateTime,Doc1No,Remark;

        public CheckCustomer(Context c, String cusCode) {
            this.c = c;
            CusCode = cusCode;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fncheck();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        private String fncheck(){
            try{

                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName,UserName,Password," +
                        "DBName,Port,DBStatus," +
                        "ItemConn,EncodeType, UserCode," +
                        "CounterCode " +
                        " from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(0);
                    UserName = curSet.getString(1);
                    Password = curSet.getString(2);
                    DBName = curSet.getString(3);
                    Port = curSet.getString(4);
                    DBStatus=curSet.getString(5);
                    ItemConn=curSet.getString(6);
                    EncodeType=curSet.getString(7);
                    UserCode=curSet.getString(8);
                    CounterCode=curSet.getString(9);
                }
                String qCheck="select C.CusCode, C.CusName, C.TermCode," +
                        " C.D_ay, C.SalesPersonCode, C.MembershipClass, " +
                        " IFNULL(M.RatioPoint,0)as RatioPoint, IFNULL(M.RatioAmount,0) as RatioAmount, "+
                        " C.ContactTel, C.Email, C.CategoryCode " +
                        " from customer C left join ret_membership_class M " +
                        " on C.MembershipClass=M.Class where C.FinCatCode='B55' and C.CusCode='"+CusCode+"' ";
                int numrows=0;
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement stmtCheck = conn.createStatement();
                        stmtCheck.execute(qCheck);
                        ResultSet rsCheck = stmtCheck.getResultSet();
                        while (rsCheck.next()) {
                            CusName=rsCheck.getString(2);
                            TermCode=rsCheck.getString(3);
                            D_ay=rsCheck.getString(4);
                            SalesPersonCode=rsCheck.getString(5);
                            MembershipClass=rsCheck.getString(6);
                            RatioPoint=rsCheck.getString(7);
                            RatioAmount=rsCheck.getString(8);
                            ContactTel=rsCheck.getString(9);
                            Email=rsCheck.getString(10);
                            CategoryCode=rsCheck.getString(11);
                            numrows++;
                        }
                        if(numrows>0){
                            String vDel="delete from tb_member";
                            db.addQuery(vDel);
                            String vInsert="insert into tb_member(CusCode, CusName, TermCode, " +
                                    "D_ay, SalesPersonCode,MembershipClass," +
                                    "RatioPoint,RatioAmount,ContactTel," +
                                    "Email, CategoryCode)" +
                                    "values('"+CusCode+"', '"+CusName+"', '"+TermCode+"'," +
                                    " '"+D_ay+"', '"+SalesPersonCode+"', '"+MembershipClass+"'," +
                                    " '"+RatioPoint+"', '"+RatioAmount+"', '"+ContactTel+"'," +
                                    "'"+Email+"', '"+CategoryCode+"')";
                            db.addQuery(vInsert);
                            z="success";
                        }else{
                            z="error";
                        }
                    }
                }else{
                    Cursor rsCheck=db.getQuery(qCheck);
                    while(rsCheck.moveToNext()){
                        CusName=rsCheck.getString(1);
                        TermCode=rsCheck.getString(2);
                        D_ay=rsCheck.getString(3);
                        SalesPersonCode=rsCheck.getString(4);
                        MembershipClass=rsCheck.getString(5);
                        RatioPoint=rsCheck.getString(6);
                        RatioAmount=rsCheck.getString(7);
                        ContactTel=rsCheck.getString(8);
                        Email=rsCheck.getString(9);
                        CategoryCode=rsCheck.getString(10);
                        numrows++;
                    }
                    if(numrows>0){
                        z="success";
                    }else{
                        z="error";
                    }
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

    private void CreateOrder(String AnalysisCode2){
        try {
            Thread.sleep(2000);
            FnSaveOrder fnsave = new FnSaveOrder(this, AnalysisCode2, "",
                        "");
            String NewDoc = fnsave.execute().get();
            if (NewDoc.equals("error")) {
            } else if (NewDoc.equals("success")) {
                refreshNext();
            } else if (NewDoc.equals("duplicate")) {
                Toast.makeText(this,"Table Occupied !",Toast.LENGTH_SHORT).show();
            }
        }  catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void CreateAddDish(String Doc2No){
        AddDish addDish=new AddDish(this,Doc2No);
        addDish.execute();
    }
    private void CreateAddOn(String AnalysisCode2) {
        try {
            if (dServiceCharges > 0) {
                AddMisc addMisc = new AddMisc(this, "SO", AnalysisCode2);
                addMisc.execute();
            }
            Thread.sleep(2000);
            FnAddOnOrder fnaddon = new FnAddOnOrder(this, AnalysisCode2);
            fnaddon.execute();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void AfterSetSO(String Doc1No,String Doc2No, String Attention){
        try {
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String qDel="delete from dum_stk_sales_order_hd";
            db.addQuery(qDel);
            String qInsert="insert into dum_stk_sales_order_hd(Doc1No,Doc2No,Attention)values(" +
                    " '"+Doc1No+"', '"+Doc2No+"', '"+Attention+"')";
            db.addQuery(qInsert);
            db.closeDB();
            refreshOrder();
            setSubTitle(Doc2No);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    public void SetCustomer(String CusCode){
        CheckCustomer checkCustomer=new CheckCustomer(this,CusCode);
        checkCustomer.execute();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setCustomerBar();
            }
        }, 1000);

       /* DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String vDel="delete from tb_member";
        db.addQuery(vDel);
        String vInsert="insert into tb_member(CusCode, CusName, TermCode, " +
                "D_ay, SalesPersonCode,MembershipClass," +
                "RatioPoint,RatioAmount,ContactTel," +
                "Email, CategoryCode)" +
                "values('"+CusCode+"', '"+CusName+"', '"+TermCode+"'," +
                " '"+D_ay+"', '"+SalesPersonCode+"', '"+MembershipClass+"'," +
                " '"+RatioPoint+"', '"+RatioAmount+"', '"+ContactTel+"'," +
                "'"+Email+"', '"+CategoryCode+"')";
        db.addQuery(vInsert);
        String qCheck="select count(*)as totals from tb_salesperson";
        Cursor rsCheck=db.getQuery(qCheck);
        int numrows=0;
        while(rsCheck.moveToNext()){
            numrows=rsCheck.getInt(0);
        }
        if(numrows==0) {
            //String vDel2="delete from tb_salesperson";
            //db.addQuery(vDel2);
            String qInsert = "insert into tb_salesperson(SalesPersonCode,SalesPersonName)values" +
                    "('" + SalesPersonCode + "', '')";
            db.addQuery(qInsert);
        }
        db.closeDB();
        ///setCustomerBar();
       */
    }
    public void CreateSalesPerson(String SalesPersonCode){
        try {
            DBAdapter db = new DBAdapter(this);
            db.openDB();
            String qDel="delete from tb_salesperson";
            db.addQuery(qDel);
            String qInsert = "insert into tb_salesperson(SalesPersonCode,SalesPersonName)values" +
                    "('"+SalesPersonCode+"', '')";
            db.addQuery(qInsert);
            db.closeDB();
            //setPayment();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    public void SearchItem(String ItemCode){
        AddItem_BySearch additem=new AddItem_BySearch(this , ItemCode,"1");
        additem.execute();
    }
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        SetStatusBar(isConnected);
    }

    // Showing the status on status bar
    private void SetStatusBar(boolean isConnected) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if(isConnected) {
                window.setStatusBarColor(getResources().getColor(R.color.colorSkyBiz));
            }else{
                window.setStatusBarColor(Color.RED);
            }
        }else{

        }
    }
    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        SetStatusBar(isConnected);
    }
/*
Block 1= MerchantKey
Block 2= CusCode
Block 5= CusName;
Block 6= ContactTel
Block 9= Balance
Block 10= LastTrnDoc1No
Block 11= LastTrnType
Block 14= LastTrnDateTime
Block 15=LastTrnAmount
 */

}
