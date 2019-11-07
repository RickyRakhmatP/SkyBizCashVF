package skybiz.com.posoffline.ui_Sync;

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
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Trn;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;
import skybiz.com.posoffline.ui_Sync.m_Resync.ResyncAdapter;

/**
 * Created by 7 on 29/12/2017.
 */

public class Fragment_Resync extends Fragment {
    View view;
    SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager lLayout;
    RecyclerView rvCashReceipt;
    ResyncAdapter adapter;
    ArrayList<Spacecraft_Trn> lscr=new ArrayList<>();
    SearchView sv;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_resync, container, false);
        refresh();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

       /* sv= (SearchView)view.findViewById(R.id.searchtrn);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                //FILTER AS YOU TYPE
                adapter.getFilter().filter(query);
                return false;
            }
        });*/


        return view;
    }

    public void refresh() {
        rvCashReceipt=(RecyclerView)view.findViewById(R.id.rec_list_cs);
        rvCashReceipt.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rvCashReceipt.setLayoutManager(lLayout);
        rvCashReceipt.setItemAnimator(new DefaultItemAnimator());
        adapter=new ResyncAdapter(getActivity(),lscr,getActivity().getSupportFragmentManager());
        retCS();
    }

    public void retCS(){
        lscr.clear();
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String vQuery="select H.Doc1No,H.D_ateTime,H.HCNetAmt,H.SynYN,Sum(D.Qty) as Qty " +
                "from stk_cus_inv_hd H inner join stk_cus_inv_dt D on D.Doc1No=H.Doc1No Group By H.Doc1No Order By H.RunNo Desc";
        Cursor c=db.getQuery(vQuery);
        while (c.moveToNext()) {
            String Doc1No=c.getString(0);
            String Dated=c.getString(1);
            String HCNetAmt=c.getString(2);
            String SynYN=c.getString(3);
            String Qty=c.getString(4);
            Spacecraft_Trn s=new Spacecraft_Trn();
            s.setDoc1No(Doc1No);
            s.setD_ateTime(Dated);
            s.setHCNetAmt(HCNetAmt);
            s.setQty(Qty);
            s.setSynYN(SynYN);
            lscr.add(s);
        }
        //CHECK IF ARRAYLIST ISNT EMPTY
        if(!(lscr.size()<1)) {
            rvCashReceipt.setAdapter(adapter);
        }
        db.closeDB();;
    }
}
