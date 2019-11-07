package skybiz.com.posoffline.ui_CreditNote;

import android.content.Intent;
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

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewCustomer.DialogCustomer;
import skybiz.com.posoffline.m_NewItem.DownloaderItem;
import skybiz.com.posoffline.m_NewItemList.DialogItem;
import skybiz.com.posoffline.m_NewMisc.DownloaderMisc;
import skybiz.com.posoffline.m_NewReprint.DialogReprint;
import skybiz.com.posoffline.m_NewReprint.ReprintLast;
import skybiz.com.posoffline.m_NewSummary.DownloaderOrder;

public class CreditNote extends AppCompatActivity {

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
    RecyclerView rv,rvOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Credit Note");
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
        getMenuInflater().inflate(R.menu.menu_credit_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.mnCustomer) {
            Bundle b=new Bundle();
            b.putString("DOCTYPE_KEY","CusCN");
            DialogCustomer dialogCustomer = new DialogCustomer();
            dialogCustomer.setArguments(b);
            dialogCustomer.show(getSupportFragmentManager(), "List Customer");
            return true;
        }else if (id == R.id.mnItem) {
            Bundle b=new Bundle();
            b.putString("DOCTYPE_KEY","CusCN");
            DialogItem dialogItem = new DialogItem();
            dialogItem.setArguments(b);
            dialogItem.show(getSupportFragmentManager(), "mListItem");
            return true;
        }else if(id==R.id.mnReprint){
            Bundle b = new Bundle();
            b.putString("DOCTYPE_KEY", "CusCN");
            DialogReprint dialogReprint = new DialogReprint();
            dialogReprint.setArguments(b);
            dialogReprint.show(getSupportFragmentManager(), "mListReprint");
            return true;
        }else if(id==R.id.mnReprint2){
            ReprintLast reprintLast=new ReprintLast(this,"CusCN");
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
                    Fragment_Items tab3 = new Fragment_Items();
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
                    return "DETAILS";
                case 1:
                    return "SUMMARY";
                case 2:
                    return "ITEMS";
            }
            return null;
        }
    }

    public void setCustomerBar(String CusName){
        getSupportActionBar().setTitle(CusName);
    }

    public void refreshOrder(){
        rvOrder=(RecyclerView)findViewById(R.id.rec_order);
        rvOrder.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 1);
        rvOrder.setLayoutManager(lLayout);
        rvOrder.setItemAnimator(new DefaultItemAnimator());
        DownloaderOrder order=new DownloaderOrder(this,"CusCN",rvOrder,this.getSupportFragmentManager());
        order.execute();
    }
    public void retItem(String ItemGroup) {
        rv = (RecyclerView) findViewById(R.id.rec_list);
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 2);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        if (ItemGroup.equals("Miscellaneous")) {
            DownloaderMisc dMisc = new DownloaderMisc(CreditNote.this, "CusCN", rv);
            dMisc.execute();
        } else {
             DownloaderItem dItem = new DownloaderItem(CreditNote.this, "CusCN", ItemGroup, rv);
             dItem.execute();
        }
    }
    public void newRefresh(){
        finish();
        Intent mainIntent = new Intent(CreditNote.this, CreditNote.class);
        startActivity(mainIntent);
    }
}
