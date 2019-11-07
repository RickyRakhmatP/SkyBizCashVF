package skybiz.com.posoffline.ui_Member.m_MemberList;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Order;
import skybiz.com.posoffline.ui_CashReceipt.m_ListSO.Fragment_ListSO;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItem_ByDesc;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItem_BySearch;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.DownloaderOrder;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.OrderAdapter;
import skybiz.com.posoffline.ui_Member.m_MemberList.m_Customer.DownloaderCustomer;

/**
 * Created by 7 on 11/12/2017.
 */

public class Fragment_MemberList extends Fragment {

    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svCustomer;
    Spinner spSearchBy;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_customer, container, false);
        rv=(RecyclerView) view.findViewById(R.id.rvCustomer);
        svCustomer=(SearchView)view.findViewById(R.id.svCustomer);
        spSearchBy = (Spinner) view.findViewById(R.id.spSearchBy);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.searchby2_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSearchBy.setAdapter(adapter);
        spSearchBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String Item=adapterView.getItemAtPosition(i).toString();
                if(Item.equals("Show All")){
                    refresh("");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        svCustomer.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String Keyword) {
                callSearch(Keyword);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
            public void callSearch(String Keyword) {
                refresh(Keyword);
            }
        });
        svCustomer.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                refresh("");
                return false;
            }
        });
        return view;
    }
    public void refresh(String Keyword) {
        String SearchBy = spSearchBy.getSelectedItem().toString();
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloaderCustomer dCustomer=new DownloaderCustomer(getActivity(),SearchBy,Keyword,rv);
        dCustomer.execute();
    }

}
