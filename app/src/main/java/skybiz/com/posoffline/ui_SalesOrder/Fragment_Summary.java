package skybiz.com.posoffline.ui_SalesOrder;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Order;
import skybiz.com.posoffline.ui_SalesOrder.m_Order.AddItem_BySearch;
import skybiz.com.posoffline.ui_SalesOrder.m_Order.OrderAdapter;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 15/01/2018.
 */

public class Fragment_Summary extends Fragment {
    View view;
    private GridLayoutManager lLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchView search;
    RecyclerView rvOrder;
    OrderAdapter adapter;
    ArrayList<Spacecraft_Order> orders=new ArrayList<>();
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentso_summary, container, false);

        search=(SearchView)view.findViewById(R.id.search);
        search.setIconified(false);
        search.requestFocusFromTouch();
        //fnactivsearch();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String ItemCode) {
                callSearch(ItemCode);
                search.setQuery("", true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(),"CLICK, Search View", Toast.LENGTH_SHORT).show();
                return true;
            }
            public void callSearch(String ItemCode) {
                fnAddSearch(getActivity(),ItemCode);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        refresh();
        return view;
    }

    public void refresh() {
        rvOrder=(RecyclerView)view.findViewById(R.id.rec_order);
        rvOrder.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rvOrder.setLayoutManager(lLayout);
        rvOrder.setItemAnimator(new DefaultItemAnimator());
        adapter=new OrderAdapter(getActivity(),orders,getActivity().getSupportFragmentManager());
        retOrder();
    }

    public void retOrder(){
        orders.clear();
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String vQuery="select Qty, Doc1No, ItemCode, ItemGroup," +
                " Description, IFNULL(HCUnitCost,0) as HCUnitCost, IFNULL(HCDiscount,0)as HCDiscount," +
                " IFNULL(DisRate1,0)as DisRate1, UOM, RunNo  " +
                "from cloud_sales_order_dt Order By RunNo desc";
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
            String UOM=c.getString(8);
            int RunNo=c.getInt(9);
            Spacecraft_Order s=new Spacecraft_Order();
            s.setItemCode(ItemCode);
            s.setDescription(ItemDesc);
            s.setHCUnitCost(UnitPrice);
            s.setHCDiscount(HCDiscount);
            s.setDisRate1(DisRate1);
            s.setBtnQty(Qty);
            s.setUOM(UOM);
            s.setRunNo(RunNo);
            orders.add(s);
        }
        //CHECK IF ARRAYLIST ISNT EMPTY
        if(!(orders.size()<1)) {
            rvOrder.setAdapter(adapter);
        }
        db.closeDB();;
    }

    public void fnAddSearch(Context c, String ItemCode){
        String vQty="1";
        AddItem_BySearch fnaddsearch=new AddItem_BySearch(c , ItemCode,vQty);
        fnaddsearch.execute();
        refresh();
    }
}
