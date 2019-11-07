package skybiz.com.posoffline.ui_Service;

import android.content.Context;
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

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewSummary.AddItem_BySearch;
import skybiz.com.posoffline.m_NewSummary.DownloaderOrder;

public class Repair_Parts extends Fragment {
    View view;
    RecyclerView rvOrder;
    private GridLayoutManager lLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchView search;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentcn_summary, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retOrder();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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
        retOrder();
        return view;
    }
    private void retOrder(){
        rvOrder=(RecyclerView)view.findViewById(R.id.rec_order);
        rvOrder.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rvOrder.setLayoutManager(lLayout);
        rvOrder.setItemAnimator(new DefaultItemAnimator());
        DownloaderOrder dOrder=new DownloaderOrder(getActivity(),"Service", rvOrder,getActivity().getSupportFragmentManager());
        dOrder.execute();
    }
    public void fnAddSearch(Context c, String ItemCode){
        String vQty="1";
        AddItem_BySearch fnaddsearch=new AddItem_BySearch(c, "Service", ItemCode,vQty);
        fnaddsearch.execute();
        //refresh();
    }
}
