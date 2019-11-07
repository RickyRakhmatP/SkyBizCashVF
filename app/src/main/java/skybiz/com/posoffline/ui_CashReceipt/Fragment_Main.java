package skybiz.com.posoffline.ui_CashReceipt;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.CompabilityUtil;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;
import skybiz.com.posoffline.ui_CashReceipt.m_Item.DownloaderItem;
import skybiz.com.posoffline.ui_CashReceipt.m_Item.DownloaderItemLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Item.ItemAdapter;
import skybiz.com.posoffline.ui_CashReceipt.m_ItemGroup.CustomAdapter;
import skybiz.com.posoffline.ui_CashReceipt.m_ItemGroup.DownloaderGroup;
import skybiz.com.posoffline.ui_CashReceipt.m_ItemGroup.DownloaderGroupL;
import skybiz.com.posoffline.ui_CashReceipt.m_ItemGroup.GroupAdapter;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 11/12/2017.
 */

public class Fragment_Main extends Fragment {
    RecyclerView rvG,rv;
    private GridLayoutManager lLayout;
    ItemAdapter adapter;
    GroupAdapter adapterG;
    ArrayList<Spacecraft> items=new ArrayList<>();
    ArrayList<Spacecraft> groups=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        rvG=(RecyclerView) view.findViewById(R.id.list_group);
        rv=(RecyclerView) view.findViewById(R.id.rec_list);
        retItemGroup();
        retItem("");
        return view;
    }
    public void retItem(String ItemGroup){
        int spanCount=2;
        if(CompabilityUtil.isTablet(getActivity())){
            spanCount=3;
        }
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), spanCount);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloaderItem dItem=new DownloaderItem(getActivity(),ItemGroup,rv);
        dItem.execute();
       /* String vItemGroup="";
        items.clear();
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String vQueryG="select ItemGroup from stk_master order by ItemGroup limit 1 ";
        Cursor cGroup=db.getQuery(vQueryG);
        while (cGroup.moveToNext()) {
            vItemGroup=cGroup.getString(0);
        }
        String vQuery="select * from stk_master where ItemGroup='"+vItemGroup+"' ";
        Cursor c=db.getQuery(vQuery);
       // Cursor c=db.getAllItem();
        while (c.moveToNext()) {
            int id=c.getInt(0);
            String ItemCode=c.getString(1);
            String ItemDesc=c.getString(2);
            String UnitPrice=c.getString(4);
            String Qty="0";
            String CurCode="RM";
            Spacecraft s=new Spacecraft();
            s.setItemCode(ItemCode);
            s.setDescription(ItemDesc);
            s.setUnitPrice(UnitPrice);
            s.setBtnQty(Qty);
            s.setCurCode(CurCode);
            items.add(s);
        }
        //CHECK IF ARRAYLIST ISNT EMPTY
        if(!(items.size()<1)) {
            rv.setAdapter(adapter);
        }
        db.closeDB();*/
    }
    public void retItemGroup(){
        rvG.setHasFixedSize(true);
        rvG.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvG.setItemAnimator(new DefaultItemAnimator());
        DownloaderGroup dGroup=new DownloaderGroup(getActivity(),rvG);
        dGroup.execute();
       /* groups.clear();
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();

        Cursor cItemGroup=db.getAllItemGroup();
        while (cItemGroup.moveToNext()) {
            String ItemGroup=cItemGroup.getString(0);
            Spacecraft sItemGroup=new Spacecraft();
            sItemGroup.setItemGroup(ItemGroup);
            groups.add(sItemGroup);
        }
        //CHECK IF ARRAYLIST ISNT EMPTY
        if(!(groups.size()<1)) {
            rvG.setAdapter(adapterG);
        }
        db.closeDB();*/
    }

}
