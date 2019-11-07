package skybiz.com.posoffline.ui_Sync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewObject.Decode;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class Sync extends AppCompatActivity {

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
    TextView txtSyncItem,txtSyncGroup,
            txtSyncCustomer,txtSyncDetail,
            txtSyncDetailOut,txtSyncReceipt,
            txtSyncHeader,txtSyncCS,txtSyncSO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        getMenuInflater().inflate(R.menu.menu_sync, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
            switch (position) {
                case 0:
                    Fragment_SyncIN tab1 = new Fragment_SyncIN();
                    return tab1;
                case 1:
                    Fragment_SyncOUT tab2 = new Fragment_SyncOUT();
                    return tab2;
                case 2:
                    Fragment_Resync tab3 = new Fragment_Resync();
                    return tab3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SYNC IN";
                case 1:
                    return "SYNC OUT";
                case 2:
                    return "Re-Sync";
            }
            return null;
        }
    }
    public void changeStatus(String Doc1No,String newStatus){
        try {
            DBAdapter db = new DBAdapter(this);
            db.openDB();
            String vUpdateHd = "update stk_cus_inv_hd set SynYN='" + newStatus + "' where Doc1No='" + Doc1No + "' ";
            db.addQuery(vUpdateHd);

            String vUpdateR = "update stk_receipt2 set SynYN='" + newStatus + "' where Doc1No='" + Doc1No + "' ";
            db.addQuery(vUpdateR);

            String vUpdateDt = "update stk_cus_inv_dt set SynYN='" + newStatus + "' where Doc1No='" + Doc1No + "' ";
            db.addQuery(vUpdateDt);

            String vUpdateDt2 = "Update stk_detail_trn_out set SynYN='" + newStatus + "' where Doc3No='" + Doc1No + "' ";
            db.addQuery(vUpdateDt2);
            db.closeDB();
            if(newStatus.equals("0")){
                ReSyncCS reSyncCS=new ReSyncCS(this,Doc1No);
                reSyncCS.execute();
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    public void SetProgressItem(String vValue){
        txtSyncItem=(TextView)findViewById(R.id.txtSyncItem);
        txtSyncItem.setText(vValue);
    }
    public void SetProgressGroup(String vValue){
        txtSyncGroup=(TextView)findViewById(R.id.txtSyncGroup);
        txtSyncGroup.setText(vValue);
    }
    public void SetProgressCustomer(String vValue){
        txtSyncCustomer=(TextView)findViewById(R.id.txtSyncCustomer);
        txtSyncCustomer.setText(vValue);
    }
    public void SetProgressCS(String vValue){
        txtSyncCS=(TextView)findViewById(R.id.txtSyncCS);
        txtSyncCS.setText(vValue);
    }
    public void SetProgressSO(String vValue){
        txtSyncSO=(TextView)findViewById(R.id.txtSyncSO);
        txtSyncSO.setText(vValue);
    }

    private class ReSyncCS extends AsyncTask<Void,Void,String>{
        Context c;
        String Doc1No;
        String IPAddress,UserName,Password,
                URL,DBName,z,
                Port,DBStatus,ItemConn,
                EncodeType,D_ate;
        DBAdapter db=null;
        Connection conn=null;

        public ReSyncCS(Context c, String doc1No) {
            this.c = c;
            Doc1No = doc1No;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("error")){
                Toast.makeText(c,"Failure, ReSync CS", Toast.LENGTH_SHORT).show();
            }else if(result.equals("success")){
                Toast.makeText(c,"Success, ReSync CS ["+Doc1No+"]", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fnresync();
        }

        private String fnresync(){
            try{
                z   = "error";
                db  = new DBAdapter(c);
                db.openDB();
                String querySet = "select ServerName, UserName, Password," +
                        " DBName, Port, DBStatus," +
                        " ItemConn, EncodeType" +
                        " from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(0);
                    UserName = curSet.getString(1);
                    Password = curSet.getString(2);
                    DBName = curSet.getString(3);
                    Port = curSet.getString(4);
                    DBStatus = curSet.getString(5);
                    ItemConn = curSet.getString(6);
                    EncodeType = curSet.getString(7);
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                D_ate = sdf.format(date);
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                conn = Connector.connect(URL, UserName, Password);
                if (conn == null) {
                    z = "error";
                } else {
                    syncDt();
                    synDt2();
                    syncHd();
                    syncReceipt2();
                    z = "success";
                }
                db.closeDB();
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }
            return z;
        }
        private void syncReceipt2(){
            try{
                String sqlDetail="select * from stk_receipt2 where Doc1No='"+Doc1No+"' " ;
                Cursor rsR=db.getQuery(sqlDetail);
                int i=1;
                while(rsR.moveToNext()){
                    int numrow=0;
                    String qCheck="select Count(*) as numrows from stk_receipt2 where Doc1No='"+rsR.getString(4)+"' ";
                    Statement stmtCheck=conn.createStatement();
                    stmtCheck.execute(qCheck);
                    ResultSet rsCheck=stmtCheck.getResultSet();
                    while(rsCheck.next()){
                        numrow=rsCheck.getInt(1);
                    }
                    if(numrow==0) {
                        String vDetail = "INSERT INTO stk_receipt2 (D_ate, T_ime, D_ateTime, Doc1No," +
                                " CashAmt, CC1Code, CC1Amt, CC1No, " +
                                " CC1Expiry, CC1ChargesAmt, CC1ChargesRate, CC2Code, " +
                                " CC2Amt, CC2No, CC2Expiry, CC2ChargesAmt," +
                                " CC2ChargesRate, Cheque1Code, Cheque1Amt, Cheque1No," +
                                " Cheque2Code, Cheque2Amt, Cheque2No, PointAmt," +
                                " VoucherAmt, CurCode, CurRate, FCAmt," +
                                " CusCode, BalanceAmount, ChangeAmt, CounterCode," +
                                " UserCode, DocType, VoidYN, CurRate1) values(" +
                                " '" + rsR.getString(1) + "', '" + rsR.getString(2) + "', '" + rsR.getString(3) + "', '" + rsR.getString(4) + "'," +
                                " '" + rsR.getString(5) + "', '" + rsR.getString(6) + "', '" + rsR.getString(7) + "', '" + rsR.getString(8) + "'," +
                                " '" + rsR.getString(9) + "', '" + rsR.getString(10) + "', '" + rsR.getString(11) + "', '" + rsR.getString(12) + "'," +
                                " '" + rsR.getString(13) + "', '" + rsR.getString(14) + "', '" + rsR.getString(15) + "', '" + rsR.getString(16) + "'," +
                                " '" + rsR.getString(17) + "', '" + rsR.getString(18) + "', '" + rsR.getString(19) + "', '" + rsR.getString(20) + "'," +
                                " '" + rsR.getString(21) + "', '" + rsR.getString(22) + "', '" + rsR.getString(23) + "', '" + rsR.getString(24) + "', " +
                                " '" + rsR.getString(25) + "', '" + rsR.getString(26) + "', '" + rsR.getString(27) + "', '" + rsR.getString(28) + "', " +
                                " '" + rsR.getString(29) + "', '" + rsR.getString(30) + "', '" + rsR.getString(31) + "', '" + rsR.getString(32) + "', " +
                                " '" + rsR.getString(33) + "', '" + rsR.getString(34) + "', '" + rsR.getString(36) + "', '"+rsR.getString(27) +"' )";
                        Statement statement = conn.createStatement();
                        statement.execute(vDetail);
                        String sqlUpdate = "Update stk_receipt2 set SynYN='1' where RunNo='" + rsR.getString(0) + "' ";
                        db.updateQuery(sqlUpdate);
                    }else{
                        String sqlUpdate = "Update stk_receipt2 set SynYN='1' where RunNo='" + rsR.getString(0) + "' ";
                        db.updateQuery(sqlUpdate);
                    }
                    //publishProgress(i);
                    i++;
                }
                rsR.close();
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        private void syncHd(){
            try{
                String sqlDetail="select * from stk_cus_inv_hd where Doc1No='"+Doc1No+"' " ;
                Cursor rsDt=db.getQuery(sqlDetail);
                int i=1;
                while(rsDt.moveToNext()){
                    int numrow=0;
                    String qCheck="select count(*) as numrows " +
                            "from stk_cus_inv_hd where Doc1No='"+rsDt.getString(1)+"' ";
                    Statement stmtCheck=conn.createStatement();
                    stmtCheck.execute(qCheck);
                    ResultSet rsCheck=stmtCheck.getResultSet();
                    while(rsCheck.next()){
                        numrow=rsCheck.getInt(1);
                    }
                    if(numrow==0) {
                        String vDetail = "INSERT INTO stk_cus_inv_hd (Doc1No, Doc2No, Doc3No, D_ate," +
                                " D_ateTime, CusCode, MemberNo, DueDate, " +
                                " TaxDate, CurCode, CurRate1,CurRate2, " +
                                " CurRate3, TermCode, D_ay, Attention," +
                                " AddCode, BatchCode, GbDisRate1, GbDisRate2," +
                                " GbDisRate3, HCGbDiscount, GbTaxRate1, GbTaxRate2," +
                                " GbTaxRate3, HCGbTax, GlobalTaxCode, HCDtTax, " +
                                " HCNetAmt, AdjAmt, GbOCCode, GbOCRate," +
                                " GbOCAmt, DocType, ApprovedYN, RetailYN," +
                                " UDRunNo, L_ink, Status, Status2) values(" +
                                " '" + rsDt.getString(1) + "', '" + rsDt.getString(2) + "', '" + rsDt.getString(3) + "', '" + rsDt.getString(4) + "'," +
                                " '" + rsDt.getString(5) + "', '" + rsDt.getString(6) + "', '" + rsDt.getString(7) + "', '" + rsDt.getString(8) + "'," +
                                " '" + rsDt.getString(9) + "', '" + rsDt.getString(10) + "', '" + rsDt.getString(11) + "', '" + rsDt.getString(12) + "'," +
                                " '" + rsDt.getString(13) + "', '" + rsDt.getString(14) + "', '" + rsDt.getString(15) + "', '" + rsDt.getString(16) + "'," +
                                " '" + rsDt.getString(17) + "', '" + rsDt.getString(18) + "', '" + rsDt.getString(19) + "', '" + rsDt.getString(20) + "'," +
                                " '" + rsDt.getString(21) + "', '" + rsDt.getString(22) + "', '" + rsDt.getString(23) + "', '" + rsDt.getString(24) + "', " +
                                " '" + rsDt.getString(25) + "', '" + rsDt.getString(26) + "', '" + rsDt.getString(27) + "', '" + rsDt.getString(28) + "', " +
                                " '" + rsDt.getString(29) + "', '" + rsDt.getString(30) + "', '" + rsDt.getString(31) + "', '" + rsDt.getString(32) + "', " +
                                " '" + rsDt.getString(33) + "', '" + rsDt.getString(34) + "', '" + rsDt.getString(35) + "', '" + rsDt.getString(36) + "' ," +
                                " '" + rsDt.getString(37) + "', '" + rsDt.getString(38) + "', '" + rsDt.getString(39) + "', '" + rsDt.getString(40) + "' )";
                        Log.d("HEADER", vDetail);
                        Statement statement = conn.createStatement();
                        statement.execute(vDetail);
                        String sqlUpdate = "Update stk_cus_inv_hd set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                        db.updateQuery(sqlUpdate);
                    }else{
                        String sqlUpdate = "Update stk_cus_inv_hd set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                        db.updateQuery(sqlUpdate);
                    }
                    //publishProgress(i);
                    i++;
                }
                rsDt.close();
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        private void syncDt(){
            try{
                Statement stmtInDt = conn.createStatement();
                stmtInDt.executeQuery("SET NAMES 'LATIN1'");
                stmtInDt.executeQuery("SET CHARACTER SET 'LATIN1'");
                String sqlDetail = "select '' as RunNo, Doc1No, N_o, ItemCode, Description," +
                        "  Qty, FactorQty, UOM, UOMSingular, " +
                        "  HCUnitCost, DisRate1, HCDiscount, TaxRate1, " +
                        "  HCTax, DetailTaxCode, HCLineAmt, BranchCode, " +
                        "  DepartmentCode, ProjectCode, SalesPersonCode, LocationCode, " +
                        "  IFNULL(WarrantyDate, '" + D_ate + "') as WDate, IFNULL(LineNo,'')as LineNo, BlankLine, DocType, " +
                        "  AnalysisCode2, DUD6 " +
                        "  from stk_cus_inv_dt where Doc1No='"+Doc1No+"' ";
                Cursor rsDt = db.getQuery(sqlDetail);
                int i = 1;
                while (rsDt.moveToNext()) {
                    String Doc1No = rsDt.getString(1);
                    String ItemCode = rsDt.getString(3);
                    String LineNo = rsDt.getString(22);
                    if (LineNo.isEmpty()) {
                        LineNo = "" + i;
                    }
                    int numrow = 0;
                    String qCheck = "select count(*) as numrows " +
                            " from stk_cus_inv_dt where Doc1No='" + Doc1No + "' " +
                            " and ItemCode='" + ItemCode + "' and LineNo='" + LineNo + "'   ";
                    Log.d("qCheck", qCheck);
                    Statement stmtCheck = conn.createStatement();
                    stmtCheck.execute(qCheck);
                    ResultSet rsCheck = stmtCheck.getResultSet();
                    while (rsCheck.next()) {
                        numrow = rsCheck.getInt(1);
                    }

                    if (numrow == 0) {
                        String vDetail = "INSERT INTO stk_cus_inv_dt (Doc1No, N_o, ItemCode, Description," +
                                " Qty, FactorQty, UOM, UOMSingular, " +
                                " HCUnitCost, DisRate1, HCDiscount, TaxRate1, " +
                                " HCTax, DetailTaxCode, HCLineAmt, BranchCode," +
                                " DepartmentCode, ProjectCode, SalesPersonCode, LocationCode," +
                                " WarrantyDate, LineNo, BlankLine, DocType," +
                                " AnalysisCode2, DUD6) values(" +
                                " '" + rsDt.getString(1) + "', '" + rsDt.getString(2) + "', '" + rsDt.getString(3) + "', '" + Decode.setChar(EncodeType, rsDt.getString(4)) + "'," +
                                " '" + rsDt.getString(5) + "', '" + rsDt.getString(6) + "', '" + Decode.setChar(EncodeType, rsDt.getString(7)) + "', '" + Decode.setChar(EncodeType, rsDt.getString(8)) + "'," +
                                " '" + rsDt.getString(9) + "', '" + rsDt.getString(10) + "', '" + rsDt.getString(11) + "', '" + rsDt.getString(12) + "'," +
                                " '" + rsDt.getString(13) + "', '" + rsDt.getString(14) + "', '" + rsDt.getString(15) + "', '" + rsDt.getString(16) + "'," +
                                " '" + rsDt.getString(17) + "', '" + rsDt.getString(18) + "', '" + rsDt.getString(19) + "', '" + rsDt.getString(20) + "'," +
                                " '" + rsDt.getString(21) + "', '" + rsDt.getString(22) + "', '" + rsDt.getString(23) + "', '" + rsDt.getString(24) + "', " +
                                " '" + rsDt.getString(25) + "', '" + rsDt.getString(26) + "' )";

                        stmtInDt.addBatch(vDetail);
                        String sqlUpdate = "Update stk_cus_inv_dt set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                        db.updateQuery(sqlUpdate);
                    } else {
                        String sqlUpdate = "Update stk_cus_inv_dt set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                        db.updateQuery(sqlUpdate);
                    }
                    i++;
                }
                stmtInDt.executeBatch();
                stmtInDt.close();
                rsDt.close();
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        private void synDt2() {
            try {
                Statement stmtDt2 = conn.createStatement();
                String sqlDetail = "select * from stk_detail_trn_out where Doc3No='"+Doc1No+"'  ";
                Cursor rsDt = db.getQuery(sqlDetail);
                int i = 1;
                while (rsDt.moveToNext()) {
                    int numrow = 0;
                    String ItemCode = rsDt.getString(1);
                    String Doc1No = rsDt.getString(2);
                    String Doc3NoRunNo = rsDt.getString(10);
                    String qCheck = "select count(*) as numrows " +
                            " from stk_detail_trn_out where Doc3No='" + Doc1No + "' " +
                            " and ItemCode='" + ItemCode + "' and Doc3NoRunNo='" + Doc3NoRunNo + "'   ";
                    Log.d("qCheck", qCheck);
                    Statement stmtCheckDt2 = conn.createStatement();
                    stmtCheckDt2.execute(qCheck);
                    ResultSet rsCheckDt2 = stmtCheckDt2.getResultSet();
                    while (rsCheckDt2.next()) {
                        numrow = rsCheckDt2.getInt(1);
                    }
                    if (numrow == 0) {
                        String vdetailOut = " insert into stk_detail_trn_out(ItemCode,Doc3No,D_ate,QtyOUT," +
                                " FactorQty,UOM,UnitPrice,CusCode," +
                                " DocType3,Doc3NoRunNo,LocationCode,L_ink," +
                                " HCTax,BookDate) values(" +
                                " '" + rsDt.getString(1) + "', '" + rsDt.getString(2) + "', '" + rsDt.getString(3) + "', '" + rsDt.getString(4) + "'," +
                                " '" + rsDt.getString(5) + "', '" + rsDt.getString(6) + "', '" + rsDt.getString(7) + "', '" + rsDt.getString(8) + "'," +
                                " '" + rsDt.getString(9) + "', '" + rsDt.getString(10) + "', '" + rsDt.getString(11) + "', '" + rsDt.getString(12) + "'," +
                                " '" + rsDt.getString(13) + "', '" + rsDt.getString(14) + "' )";
                        stmtDt2.addBatch(vdetailOut);
                        String sqlUpdate = "Update stk_detail_trn_out set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                        db.updateQuery(sqlUpdate);
                        //publishProgress(i);
                    } else {
                        String sqlUpdate = "Update stk_detail_trn_out set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                        db.updateQuery(sqlUpdate);
                    }
                    i++;
                }
                stmtDt2.executeBatch();
                stmtDt2.close();
                rsDt.close();
            } catch (SQLiteException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

