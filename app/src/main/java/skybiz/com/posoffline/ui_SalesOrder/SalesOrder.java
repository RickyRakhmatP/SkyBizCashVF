package skybiz.com.posoffline.ui_SalesOrder;

import android.content.Intent;
import android.database.Cursor;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewItemList.DialogItem;
import skybiz.com.posoffline.m_NewReprint.DialogReprint;
import skybiz.com.posoffline.m_NewReprint.ReprintLast;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Order;
import skybiz.com.posoffline.ui_SalesOrder.m_Customer.DialogCustomer;
import skybiz.com.posoffline.ui_SalesOrder.m_Item.DownloaderItem;
import skybiz.com.posoffline.ui_SalesOrder.m_Item.ItemAdapter;
import skybiz.com.posoffline.ui_SalesOrder.m_Misc.DownloaderMisc;
import skybiz.com.posoffline.ui_SalesOrder.m_Order.OrderAdapter;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class SalesOrder extends AppCompatActivity {

    private GridLayoutManager lLayout;
    OrderAdapter orderAdapter;
    ItemAdapter itemAdapter;
    ArrayList<Spacecraft_Order> orders=new ArrayList<>();
    ArrayList<Spacecraft> items=new ArrayList<>();
    RecyclerView rvOrder,rv;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order);

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
        getMenuInflater().inflate(R.menu.menu_sales_order, menu);
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
         }else if (id == R.id.mnItem) {
            Bundle b = new Bundle();
            b.putString("DOCTYPE_KEY", "SO");
            DialogItem dialogItem = new DialogItem();
            dialogItem.setArguments(b);
            dialogItem.show(getSupportFragmentManager(), "mListItem");
            return true;
        }else if(id==R.id.mnReprint){
            Bundle b = new Bundle();
            b.putString("DOCTYPE_KEY", "SO");
            DialogReprint dialogReprint = new DialogReprint();
            dialogReprint.setArguments(b);
            dialogReprint.show(getSupportFragmentManager(), "mListReprint");
            return true;
        }else if(id==R.id.mnReprint2){
            ReprintLast reprintLast=new ReprintLast(this,"SO");
            reprintLast.execute();
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
                    Fragment_Summary tab1 = new Fragment_Summary();
                    return tab1;
                case 1:
                    Fragment_Save tab2 = new Fragment_Save();
                    return tab2;
                case 2:
                    Fragment_Item tab3 = new Fragment_Item();
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
                    return "Details";
                case 1:
                    return "Summary";
                case 2:
                    return "Items";
            }
            return null;
        }
    }

    public void refreshNext(){
        finish();
        Intent mainIntent = new Intent(SalesOrder.this, SalesOrder.class);
        startActivity(mainIntent);
    }
    public void setCustomerBar(String CusName){
        getSupportActionBar().setTitle(CusName);
    }

    public void refreshContent(String ItemGroup){
        rv=(RecyclerView)findViewById(R.id.rec_list);
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 2);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        if(ItemGroup.equals("Miscellaneous")) {
            DownloaderMisc dMisc = new DownloaderMisc(SalesOrder.this, ItemGroup, rv);
            dMisc.execute();
        }else{
            DownloaderItem dItem = new DownloaderItem(SalesOrder.this, ItemGroup, rv);
            dItem.execute();
        }
        /*itemAdapter=new VoidAdapter(this,items);
        retItem(ItemGroup);*/
    }

    public void refreshOrder(){
        rvOrder=(RecyclerView)findViewById(R.id.rec_order);
        rvOrder.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 1);
        rvOrder.setLayoutManager(lLayout);
        rvOrder.setItemAnimator(new DefaultItemAnimator());
        orderAdapter=new OrderAdapter(this,orders,this.getSupportFragmentManager());
        retOrder();
    }
    public void newRefresh(){
        finish();
        Intent mainIntent = new Intent(SalesOrder.this, SalesOrder.class);
        startActivity(mainIntent);
    }

    public void retOrder(){
        orders.clear();
        DBAdapter db=new DBAdapter(this);
        db.openDB();
        String vQuery="select c.Qty,c.Doc1No,m.ItemCode,m.ItemGroup,m.Description, IFNULL(c.HCUnitCost,0) as HCUnitCost," +
                "IFNULL(c.HCDiscount,0)as HCDiscount,IFNULL(c.DisRate1,0)as DisRate1  from cloud_sales_order_dt c inner join stk_master m on c.ItemCode=m.ItemCode  Order By c.RunNo desc";
        Cursor c=db.getQuery(vQuery);
        while (c.moveToNext()) {
            String Qty=c.getString(0);
            String ItemCode=c.getString(2);
            String ItemDesc=c.getString(4);
            Double dUnitPrice=c.getDouble(5);
            Double dHCDiscount=c.getDouble(6);
            Double dDisRate1=c.getDouble(7);
            String UnitPrice=dUnitPrice.toString();
            String HCDiscount=dHCDiscount.toString();
            String DisRate1=dDisRate1.toString();
            Spacecraft_Order s=new Spacecraft_Order();
            s.setItemCode(ItemCode);
            s.setDescription(ItemDesc);
            s.setHCUnitCost(UnitPrice);
            s.setHCDiscount(HCDiscount);
            s.setDisRate1(DisRate1);
            s.setBtnQty(Qty);
            orders.add(s);
        }
        //CHECK IF ARRAYLIST ISNT EMPTY
        if(!(orders.size()<1)) {
            rvOrder.setAdapter(orderAdapter);
        }
        db.closeDB();;
    }

}
