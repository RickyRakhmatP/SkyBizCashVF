package skybiz.com.posoffline.ui_Member.m_MemberList;

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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.DialogAdminPass;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Member.m_MemberList.m_Customer.DownloaderCustomer;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class MemberList extends AppCompatActivity {

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
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svCustomer;
    Spinner spSearchBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

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

       /* mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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
        getMenuInflater().inflate(R.menu.menu_member_list, menu);
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
                    Fragment_MemberList tab1 = new Fragment_MemberList();
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

    public void refresh(String Keyword) {
        rv=(RecyclerView) findViewById(R.id.rvCustomer);
        svCustomer=(SearchView)findViewById(R.id.svCustomer);
        spSearchBy = (Spinner)findViewById(R.id.spSearchBy);
        String SearchBy = spSearchBy.getSelectedItem().toString();
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloaderCustomer dCustomer=new DownloaderCustomer(this,SearchBy,Keyword,rv);
        dCustomer.execute();
    }
    public void setDelete(String CusCode){
        Bundle b=new Bundle();
        b.putString("ITEMCODE_KEY",CusCode);
        b.putString("TYPE_KEY","delete");
        b.putString("UFROM_KEY","member");
        DialogAdminPass dialogAdminPass = new DialogAdminPass();
        dialogAdminPass.setArguments(b);
        dialogAdminPass.show(getSupportFragmentManager(), "mTag");
    }
    public void setDeleteMember(String CusCode){
       DeleteMember deleteMember=new DeleteMember(this,CusCode);
       deleteMember.execute();
    }
    public class DeleteMember extends AsyncTask<Void,Void,String> {
        Context c;
        String CusCode;
        String IPAddress,DBName,Password,Port,z,URL,UserName,MacAddress,DBStatus,EncodeType;

        public DeleteMember(Context c, String CusCode) {
            this.c = c;
            this.CusCode = CusCode;
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
                Toast.makeText(c,"Failure Delete Member ", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(c,"Successfull Delete Member ", Toast.LENGTH_SHORT).show();
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
                String qDelete="Delete from customer where CusCode='"+CusCode+"' ";
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
                        z="success";
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
}
