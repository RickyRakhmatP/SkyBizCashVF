package skybiz.com.posoffline.ui_Listing;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.DialogAdminPass;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Listing.m_Listing.DownloaderList;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class Listing extends AppCompatActivity {

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
    String vDocType;
    private GridLayoutManager lLayout;
    RecyclerView rvCashReceipt;
    Button btnPrint,btnRefresh;
    EditText txtDateFrom,txtDateTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        /*mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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
        getMenuInflater().inflate(R.menu.menu_listing, menu);
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
                    Fragment_ListCS tab0 = new Fragment_ListCS();
                    return tab0;
                case 1:
                    Fragment_ListSO tab1 = new Fragment_ListSO();
                    return tab1;
                case 2:
                    Fragment_ListCusCN tab2 = new Fragment_ListCusCN();
                    return tab2;
                case 3:
                    Fragment_ListPO tab3 = new Fragment_ListPO();
                    return tab3;
                case 4:
                    Fragment_DailyCheckout tab4 = new Fragment_DailyCheckout();
                    return tab4;
                case 5:
                    Fragment_ItemSales tab5 = new Fragment_ItemSales();
                    return tab5;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Cash Receipt";
                case 1:
                    return "Sales Order";
                case 2:
                    return "Credit Note";
                case 3:
                    return "Purchase Order";
                case 4:
                    return "Daily Checkout";
                case 5:
                    return "Product Sales";
            }
            return null;
        }
    }
    public void refresh(String DocType) {
        rvCashReceipt=(RecyclerView)findViewById(R.id.rec_list_cs);
        btnPrint=(Button)findViewById(R.id.btnPrint);
        btnRefresh=(Button)findViewById(R.id.btnRefresh);
        txtDateFrom=(EditText)findViewById(R.id.txtDateFrom);
        txtDateTo=(EditText)findViewById(R.id.txtDateTo);
        String DateFrom=txtDateFrom.getText().toString();
        String DateTo=txtDateTo.getText().toString();
        Log.d("DATEFROM", DateFrom+DateTo);
        rvCashReceipt.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 1);
        rvCashReceipt.setLayoutManager(lLayout);
        rvCashReceipt.setItemAnimator(new DefaultItemAnimator());
        DownloaderList downloaderList=new DownloaderList(this,DocType,DateFrom,DateTo,rvCashReceipt);
        downloaderList.execute();
    }
    public void delItem(String DocType,String Doc1No){
        vDocType=DocType;
        Bundle b=new Bundle();
        b.putString("ITEMCODE_KEY",Doc1No);
        b.putString("TYPE_KEY","delete");
        b.putString("UFROM_KEY","listing");
        DialogAdminPass dialogAdminPass = new DialogAdminPass();
        dialogAdminPass.setArguments(b);
        dialogAdminPass.show(getSupportFragmentManager(), "mTag");
    }
    public void setDeleteList(String Doc1No){
        DeleteTrn deleteItem=new DeleteTrn(this,vDocType,Doc1No);
        deleteItem.execute();
    }

    public class DeleteTrn extends AsyncTask<Void,Void,String> {
        Context c;
        String Doc1No,DocType;
        String IPAddress,DBName,Password,Port,z,URL,UserName,MacAddress,DBStatus,EncodeType;

        public DeleteTrn(Context c,String DocType, String Doc1No) {
            this.c = c;
            this.DocType = DocType;
            this.Doc1No = Doc1No;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.fndelete();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("error")){
                Toast.makeText(c,"Failure Delete Trn ", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(c,"Successfull Delete Trn ", Toast.LENGTH_SHORT).show();
                refresh(DocType);
            }
        }
        private String fndelete() {
            try {
                DBAdapter db=new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName,UserName,Password," +
                        "DBName,Port,DBStatus," +
                        "ItemConn,EncodeType" +
                        " from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(0);
                    UserName = curSet.getString(1);
                    Password = curSet.getString(2);
                    DBName = curSet.getString(3);
                    Port = curSet.getString(4);
                    DBStatus=curSet.getString(5);
                    EncodeType=curSet.getString(7);
                }
                String TableHd="",TableDt="",TableRec2="";
                if(DocType.equals("SO")){
                    TableHd="stk_sales_order_hd";
                    TableDt="stk_sales_order_dt";
                }else if(DocType.equals("CS")){
                    TableHd="stk_cus_inv_hd";
                    TableDt="stk_cus_inv_dt";
                    TableRec2="stk_receipt2";
                }else if(DocType.equals("CusCN")){
                    TableHd="stk_cus_inv_hd";
                    TableDt="stk_cus_inv_dt";
                }
                String qDeleteHd="Delete from "+TableHd+" where Doc1No='"+Doc1No+"' ";
                String qDeleteDt="Delete from "+TableDt+" where Doc1No='"+Doc1No+"' ";
                String qDeleteR="Delete from "+TableRec2+" where Doc1No='"+Doc1No+"' ";
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement stmtHd = conn.createStatement();
                        stmtHd.execute(qDeleteHd);
                        Statement stmtDt = conn.createStatement();
                        stmtDt.execute(qDeleteDt);
                        if(DocType.equals("CS")){
                            Statement stmtR = conn.createStatement();
                            stmtR.execute(qDeleteR);
                        }
                        z="success";
                    }
                }else{
                   /* URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    if (conn != null) {
                        Statement stmtHd = conn.createStatement();
                        stmtHd.execute(qDeleteHd);
                        Statement stmtDt = conn.createStatement();
                        stmtDt.execute(qDeleteDt);
                        if (DocType.equals("CS")) {
                            Statement stmtR = conn.createStatement();
                            stmtR.execute(qDeleteR);
                        }
                    }*/
                    long del=db.addQuery(qDeleteHd);
                    db.addQuery(qDeleteDt);
                    if (DocType.equals("CS")) {
                         db.addQuery(qDeleteR);
                    }
                    if (del > 0) {
                        z = "success";
                    } else {
                        z = "error";
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
}
