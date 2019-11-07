package skybiz.com.posoffline.ui_SalesOrder.m_Customer;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_SalesOrder.SalesOrder;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogCustomer extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svCustomer;
    Spinner spSearchBy;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_customer, container, false);
        rv=(RecyclerView) view.findViewById(R.id.rvCustomer);
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
        svCustomer=(SearchView)view.findViewById(R.id.svCustomer);
        svCustomer.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String Keyword) {
                callSearch(Keyword);
               // svCustomer.setQuery("", true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(),"CLICK, Search View", Toast.LENGTH_SHORT).show();
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
        getDialog().setTitle("List of Customer");
        refresh("");
        return view;
    }

    public void refresh(String Keyword) {
        String SearchBy = spSearchBy.getSelectedItem().toString();
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloaderCustomer dCustomer=new DownloaderCustomer(getActivity(),SearchBy,Keyword,rv,DialogCustomer.this);
        dCustomer.execute();
    }
    public void setCustomer(String CusCode,String CusName, String TermCode, String D_ay){
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String vDel="delete from tb_member";
        db.addQuery(vDel);
        String vInsert="insert into tb_member(CusCode, CusName, TermCode, D_ay, SalesPersonCode)" +
                "values('"+CusCode+"', '"+CusName+"', '"+TermCode+"', '"+D_ay+"', '')";
        db.addQuery(vInsert);
        db.closeDB();
        ((SalesOrder)getActivity()).setCustomerBar(CusName);
        dismiss();
    }

}
