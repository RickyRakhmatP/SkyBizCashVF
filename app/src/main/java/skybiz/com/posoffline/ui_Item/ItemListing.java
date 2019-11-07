package skybiz.com.posoffline.ui_Item;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import skybiz.com.posoffline.DialogAdminPass;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Item.m_ItemListing.DownloaderItem;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class ItemListing extends AppCompatActivity {

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

    Spinner spItemGroup,spDefaultUOM,spUOM,spUOM1,spUOM2,spUOM3,spUOM4;

    EditText txtItemCode,txtDescription,txtAlternateItem,txtSupplierItemCode,
            txtUOMFactor,txtUOMFactor1,txtUOMFactor2,txtUOMFactor3,txtUOMFactor4,txtUnitPrice,
            txtUOMPrice1,txtUOMPrice2,txtUOMPrice3,txtUOMPrice4,txtBaseCode,txtUOMCode1,txtUOMCode2,
            txtUOMCode3,txtUOMCode4,txtMSP,txtMSP1,txtMSP2,txtMSP3,txtMSP4,txtMAXSP,txtMAXSP1,
            txtMAXSP2,txtMAXSP3,txtMAXSP4,txtMPP,txtUnitCost,txtAnalysisCode1,txtAnalysisCode2,txtAnalysisCode3,txtAnalysisCode4;
    CheckBox chkSuspendedYN;
    Button btnSave;

    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svItem;
    Spinner spSearchBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_listing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        getMenuInflater().inflate(R.menu.menu_item_listing, menu);
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
                    Fragment_ItemList tab1 = new Fragment_ItemList();
                    return tab1;
                case 1:
                    Fragment_AddNew tab2 = new Fragment_AddNew();
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
                    return "LIST";
                case 1:
                    return "ADD NEW";
            }
            return null;
        }
    }

    public void deleteitem(String ItemCode){
        Bundle b=new Bundle();
        b.putString("ITEMCODE_KEY",ItemCode);
        b.putString("TYPE_KEY","delete");
        b.putString("UFROM_KEY","item");
        DialogAdminPass dialogAdminPass = new DialogAdminPass();
        dialogAdminPass.setArguments(b);
        dialogAdminPass.show(getSupportFragmentManager(), "mTag");
    }
    public void edititem(String ItemCode){
        Bundle b=new Bundle();
        b.putString("ITEMCODE_KEY",ItemCode);
        b.putString("TYPE_KEY","edit");
        b.putString("UFROM_KEY","item");
        DialogAdminPass dialogAdminPass = new DialogAdminPass();
        dialogAdminPass.setArguments(b);
        dialogAdminPass.show(getSupportFragmentManager(), "mTag");
    }
    public void setEdit(String ItemCode){
        SetEditItem setEditItem=new SetEditItem(this,ItemCode);
        setEditItem.execute();
    }
    public void setDeleteItem(String ItemCode){
        DeleteItem deleteItem=new DeleteItem(this,ItemCode);
        deleteItem.execute();
    }

    public class DeleteItem extends AsyncTask<Void,Void,String>{
        Context c;
        String ItemCode;
        String IPAddress,DBName,Password,Port,z,URL,UserName,MacAddress,DBStatus,EncodeType;

        public DeleteItem(Context c, String itemCode) {
            this.c = c;
            ItemCode = itemCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.fnsetitem();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("error")){
                Toast.makeText(c,"Failure Delete Item ", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(c,"Successfull Delete Item ", Toast.LENGTH_SHORT).show();
                refresh("");
            }
        }
        private String fnsetitem() {
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
                String qDelete="Delete from stk_master where ItemCode='"+ItemCode+"' ";
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement statement = conn.createStatement();
                        statement.execute(qDelete);
                        z="success";
                    }
                }else{
                   /* URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    if (conn != null) {
                        Statement statement = conn.createStatement();
                        statement.execute(qDelete);
                    }*/
                    long del=db.addQuery(qDelete);
                    if(del>0){
                        z="success";
                    }else {
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
    public class SetEditItem extends AsyncTask<Void,Void,String>{
        Context c;
        String ItemCode;
        String IPAddress,DBName,Password,Port,z,URL,UserName,MacAddress,DBStatus,EncodeType;

        public SetEditItem(Context c, String itemCode) {
            this.c = c;
            ItemCode = itemCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.fnsetitem();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result==null){
                Toast.makeText(c,"Failure Retreive Item ", Toast.LENGTH_SHORT).show();
            }else{
                EditParser(result);
                //Toast.makeText(c,"Invalid Current Password", Toast.LENGTH_SHORT).show();
            }
        }
        private String fnsetitem() {
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
                String qItem="select ItemCode,Description,ItemGroup, " +
                        " AlternateItem,SupplierItemCode,SuspendedYN, " +
                        " UOM,UOM1,UOM2," +
                        " UOM3,UOM4,UOMFactor1, " +
                        " UOMFactor2,UOMFactor3,UOMFactor4," +
                        " UnitPrice,UOMPrice1, UOMPrice2," +
                        " UOMPrice3,UOMPrice4,BaseCode," +
                        " UOMCode1,UOMCode2,UOMCode3," +
                        " UOMCode4,MSP," +
                        " MSP1,MSP2,MSP3," +
                        " MSP4,MAXSP,MAXSP1," +
                        " MAXSP2,MAXSP3,MAXSP4," +
                        " MPP,UnitCost,DefaultUOM," +
                        " AnalysisCode1,AnalysisCode2,AnalysisCode3," +
                        " AnalysisCode4 from stk_master where ItemCode='"+ItemCode+"' ";
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement statement = conn.createStatement();
                        statement.executeQuery("SET NAMES 'LATIN1'");
                        statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                        statement.execute(qItem);
                        JSONArray results = new JSONArray();
                        if (statement.execute(qItem)) {
                            ResultSet resultSet = statement.getResultSet();
                            ResultSetMetaData columns = resultSet.getMetaData();
                            while (resultSet.next()) {
                                JSONObject row = new JSONObject();
                                for (int i = 1; i <= columns.getColumnCount(); i++) {
                                    row.put(columns.getColumnName(i), resultSet.getObject(i));
                                }
                                results.put(row);
                            }
                            resultSet.close();
                            return results.toString();
                        }
                    }
                }else{
                    Cursor rsItem=db.getQuery(qItem);
                    JSONArray results=new JSONArray();
                    while(rsItem.moveToNext()){
                        JSONObject row=new JSONObject();
                        row.put("ItemCode", ItemCode);
                        row.put("Description", rsItem.getString(1));
                        row.put("ItemGroup", rsItem.getString(2));
                        row.put("AlternateItem", rsItem.getString(3));
                        row.put("SupplierItemCode",rsItem.getString(4));
                        row.put("SuspendedYN", rsItem.getString(5));
                        row.put("UOM", rsItem.getString(6));
                        row.put("UOM1", rsItem.getString(7));
                        row.put("UOM2", rsItem.getString(8));
                        row.put("UOM3", rsItem.getString(9));
                        row.put("UOM4", rsItem.getString(10));
                        row.put("UOMFactor1", rsItem.getString(11));
                        row.put("UOMFactor2", rsItem.getString(12));
                        row.put("UOMFactor3", rsItem.getString(13));
                        row.put("UOMFactor4", rsItem.getString(14));
                        row.put("UnitPrice", rsItem.getString(15));
                        row.put("UOMPrice1", rsItem.getString(16));
                        row.put("UOMPrice2", rsItem.getString(17));
                        row.put("UOMPrice3", rsItem.getString(18));
                        row.put("UOMPrice4", rsItem.getString(19));
                        row.put("BaseCode", rsItem.getString(20));
                        row.put("UOMCode1", rsItem.getString(21));
                        row.put("UOMCode2", rsItem.getString(22));
                        row.put("UOMCode3", rsItem.getString(23));
                        row.put("UOMCode4",rsItem.getString(24));
                        row.put("MSP", rsItem.getString(25));
                        row.put("MSP1",rsItem.getString(26));
                        row.put("MSP2",rsItem.getString(27));
                        row.put("MSP3", rsItem.getString(28));
                        row.put("MSP4", rsItem.getString(29));
                        row.put("MAXSP", rsItem.getString(30));
                        row.put("MAXSP1",rsItem.getString(31));
                        row.put("MAXSP2", rsItem.getString(32));
                        row.put("MAXSP3", rsItem.getString(33));
                        row.put("MAXSP4", rsItem.getString(34));
                        row.put("MPP", rsItem.getString(35));
                        row.put("UnitCost", rsItem.getString(36));
                        row.put("DefaultUOM",  rsItem.getString(37));
                        row.put("AnalysisCode1",  rsItem.getString(38));
                        row.put("AnalysisCode2",  rsItem.getString(39));
                        row.put("AnalysisCode3",  rsItem.getString(40));
                        row.put("AnalysisCode4",  rsItem.getString(41));
                        results.put(row);
                    }
                    return results.toString();
                }
                db.closeDB();
                //return z;
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

    private void EditParser(String result){
        try {
            btnSave=(Button)findViewById(R.id.btnSave);
            spItemGroup=(Spinner)findViewById(R.id.spItemGroup);
            spDefaultUOM=(Spinner)findViewById(R.id.spCategoryCode);
            spUOM=(Spinner)findViewById(R.id.spUOM);
            spUOM1=(Spinner)findViewById(R.id.spUOM1);
            spUOM2=(Spinner)findViewById(R.id.spUOM2);
            spUOM3=(Spinner)findViewById(R.id.spUOM3);
            spUOM4=(Spinner)findViewById(R.id.spUOM4);
            spDefaultUOM=(Spinner)findViewById(R.id.spDefaultUOM);
            txtItemCode=(EditText)findViewById(R.id.txtItemCode);
            txtDescription=(EditText)findViewById(R.id.txtDescription);
            txtAlternateItem=(EditText)findViewById(R.id.txtAlternateItem);
            txtSupplierItemCode=(EditText)findViewById(R.id.txtSupplierItemCode);
            txtUOMFactor=(EditText)findViewById(R.id.txtUOMFactor);
            txtUOMFactor1=(EditText)findViewById(R.id.txtUOMFactor1);
            txtUOMFactor2=(EditText)findViewById(R.id.txtUOMFactor2);
            txtUOMFactor3=(EditText)findViewById(R.id.txtUOMFactor3);
            txtUOMFactor4=(EditText)findViewById(R.id.txtUOMFactor4);
            txtUOMPrice1=(EditText)findViewById(R.id.txtUOMPrice1);
            txtUOMPrice2=(EditText)findViewById(R.id.txtUOMPrice2);
            txtUOMPrice3=(EditText)findViewById(R.id.txtUOMPrice3);
            txtUOMPrice4=(EditText)findViewById(R.id.txtUOMPrice4);
            txtUOMCode1=(EditText)findViewById(R.id.txtUOMCode1);
            txtUOMCode2=(EditText)findViewById(R.id.txtUOMCode2);
            txtUOMCode3=(EditText)findViewById(R.id.txtUOMCode3);
            txtUOMCode4=(EditText)findViewById(R.id.txtUOMCode4);
            txtBaseCode=(EditText)findViewById(R.id.txtBaseCode);
            txtMSP=(EditText)findViewById(R.id.txtMSP);
            txtMSP1=(EditText)findViewById(R.id.txtMSP1);
            txtMSP2=(EditText)findViewById(R.id.txtMSP2);
            txtMSP3=(EditText)findViewById(R.id.txtMSP3);
            txtMSP4=(EditText)findViewById(R.id.txtMSP4);
            txtMAXSP=(EditText)findViewById(R.id.txtMAXSP);
            txtMAXSP1=(EditText)findViewById(R.id.txtMAXSP1);
            txtMAXSP2=(EditText)findViewById(R.id.txtMAXSP2);
            txtMAXSP3=(EditText)findViewById(R.id.txtMAXSP3);
            txtMAXSP4=(EditText)findViewById(R.id.txtMAXSP4);
            txtMPP=(EditText)findViewById(R.id.txtMPP);
            txtUnitPrice=(EditText)findViewById(R.id.txtUnitPrice);
            txtUnitCost=(EditText)findViewById(R.id.txtUnitCost);
            txtAnalysisCode1=(EditText)findViewById(R.id.txtAnalysisCode1);
            txtAnalysisCode2=(EditText)findViewById(R.id.txtAnalysisCode2);
            txtAnalysisCode3=(EditText)findViewById(R.id.txtAnalysisCode3);
            txtAnalysisCode4=(EditText)findViewById(R.id.txtAnalysisCode4);
            chkSuspendedYN=(CheckBox) findViewById(R.id.chkSuspendedYN);
            JSONArray ja = new JSONArray(result);
            JSONObject jo = null;
            for (int i=0;i<ja.length();i++) {
                jo = ja.getJSONObject(i);
                txtItemCode.setText(jo.getString("ItemCode"));
                txtDescription.setText(jo.getString("Description"));
                fnloadgroup(jo.getString("ItemGroup"));
                txtAlternateItem.setText(jo.getString("AlternateItem"));
                txtSupplierItemCode.setText(jo.getString("SupplierItemCode"));

                String SuspendedYN = jo.getString("SuspendedYN");
                if(SuspendedYN.equals("1")){
                    chkSuspendedYN.setChecked(true);
                }else{
                    chkSuspendedYN.setChecked(false);
                }
                fnloaduom(jo.getString("UOM"),spUOM);
                fnloaduom(jo.getString("UOM1"),spUOM1);
                fnloaduom(jo.getString("UOM2"),spUOM2);
                fnloaduom(jo.getString("UOM3"),spUOM3);
                fnloaduom(jo.getString("UOM4"),spUOM4);
                txtUOMFactor1.setText(jo.getString("UOMFactor1"));
                txtUOMFactor2.setText(jo.getString("UOMFactor2"));
                txtUOMFactor3.setText(jo.getString("UOMFactor3"));
                txtUOMFactor4.setText(jo.getString("UOMFactor4"));
                txtUnitPrice.setText(jo.getString("UnitPrice"));
                txtUOMPrice1.setText(jo.getString("UOMPrice1"));
                txtUOMPrice2.setText(jo.getString("UOMPrice2"));
                txtUOMPrice3.setText(jo.getString("UOMPrice3"));
                txtUOMPrice4.setText(jo.getString("UOMPrice4"));
                txtMSP.setText(jo.getString("MSP"));
                txtMSP1.setText(jo.getString("MSP1"));
                txtMSP2.setText(jo.getString("MSP2"));
                txtMSP3.setText(jo.getString("MSP3"));
                txtMSP4.setText(jo.getString("MSP4"));
                txtMAXSP.setText(jo.getString("MAXSP"));
                txtMAXSP1.setText(jo.getString("MAXSP1"));
                txtMAXSP2.setText( jo.getString("MAXSP2"));
                txtMAXSP3.setText(jo.getString("MAXSP3"));
                txtMAXSP4.setText(jo.getString("MAXSP4"));
                txtMPP.setText(jo.getString("MPP"));
                txtUnitCost.setText(jo.getString("UnitCost"));
                txtAnalysisCode1.setText(jo.getString("AnalysisCode1"));
                txtAnalysisCode2.setText(jo.getString("AnalysisCode2"));
                txtAnalysisCode3.setText(jo.getString("AnalysisCode3"));
                txtAnalysisCode4.setText(jo.getString("AnalysisCode4"));
                setDefUOM(jo.getString("DefaultUOM"));
                btnSave.setText("UPDATE");
                txtItemCode.setEnabled(false);
                mViewPager.setCurrentItem(1);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    private void setDefUOM(String DefUOM){
        ArrayList<String> list=new ArrayList( Arrays.asList(getResources().getStringArray(R.array.defuom_array)) );
        int pos= list.indexOf(DefUOM);
        spDefaultUOM.setSelection(pos);
    }
    private void fnloadgroup(String ItemGroup){
        RetGroup retGroup=new RetGroup(this);
        retGroup.execute();
        /*int pos= lsGroup.indexOf(ItemGroup);
        spItemGroup.setSelection(pos);*/
        final String vItemGroup=ItemGroup;
        final Spinner vspUOM=spUOM;
        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int pos= lsGroup.indexOf(vItemGroup);
                spItemGroup.setSelection(pos);
            }
        },1000);
    }
    private void fnloaduom(String UOM, Spinner spUOM){
        RetUOM retUOM=new RetUOM(this);
        retUOM.execute();
        final String vUOM=UOM;
        final Spinner vspUOM=spUOM;
        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int pos= lsUOM.indexOf(vUOM);
                vspUOM.setSelection(pos);
            }
        },1000);
    }
    ArrayList lsGroup=new ArrayList<String>();
    ArrayList lsUOM=new ArrayList<String>();

    public class RetUOM extends AsyncTask<Void,Void,String>{
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
        ArrayList vlsUOM=new ArrayList<String>();

        public RetUOM(Context c) {
            this.c = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.downloadData();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                lsUOM=vlsUOM;
            }else{
                Toast.makeText(c,"UOM is Empty", Toast.LENGTH_SHORT).show();
            }
        }

        private String downloadData() {
            try {
                DBAdapter db=new DBAdapter(c);
                db.openDB();
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
                String sql ="select Distinct UOM  from stk_master Order By UOM ";
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement stmt = conn.createStatement();
                        stmt.execute(sql);
                        ResultSet resultSet = stmt.getResultSet();
                        int i=0;
                        vlsUOM.add("");
                        while (resultSet.next()) {
                            vlsUOM.add(resultSet.getString(1));
                            i++;
                        }
                        if(i>0){
                            z="success";
                        }else{
                            z="error";
                        }
                    }
                }else{
                    Cursor rsGroup=db.getQuery(sql);
                    int i=0;
                    vlsUOM.add("");
                    while(rsGroup.moveToNext()){
                        vlsUOM.add(rsGroup.getString(0));
                        i++;
                    }
                    if(i>0){
                        z="success";
                    }else{
                        lsUOM.add("Bag");
                        lsUOM.add("Box");
                        lsUOM.add("Piece");
                        lsUOM.add("Unit");
                        lsUOM.add("Bottle");
                        z="success";
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
    public class RetGroup extends AsyncTask<Void,Void,String>{
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
        ArrayList vlsGroup=new ArrayList<String>();

        public RetGroup(Context c) {
            this.c = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.downloadData();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                lsGroup=vlsGroup;
            }else{
                Toast.makeText(c,"ret membership class empty", Toast.LENGTH_SHORT).show();
            }
        }

        private String downloadData() {
            try {
                DBAdapter db=new DBAdapter(c);
                db.openDB();
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
                String sql ="select ItemGroup, Description from stk_group Order By ItemGroup ";
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {

                        Statement stmt = conn.createStatement();
                        stmt.execute(sql);
                        ResultSet resultSet = stmt.getResultSet();
                        int i=0;
                        while (resultSet.next()) {
                            vlsGroup.add(resultSet.getString(1));
                            i++;
                        }
                        if(i>0){
                            z="success";
                        }else{
                            z="error";
                        }
                    }
                }else{
                    Cursor rsGroup=db.getQuery(sql);
                    int i=0;
                    while(rsGroup.moveToNext()){
                        vlsGroup.add(rsGroup.getString(0));
                        i++;
                    }
                    if(i>0){
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

    public void refresh(String Keyword) {
        rv=(RecyclerView) findViewById(R.id.rvItem);
        svItem=(SearchView)findViewById(R.id.svItem);
        spSearchBy = (Spinner) findViewById(R.id.spSearchBy);
        String SearchBy = spSearchBy.getSelectedItem().toString();
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloaderItem downloaderItem=new DownloaderItem(this,SearchBy,
                Keyword,rv);
        downloaderItem.execute();
    }
}
