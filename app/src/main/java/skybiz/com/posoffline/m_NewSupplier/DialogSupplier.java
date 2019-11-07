package skybiz.com.posoffline.m_NewSupplier;

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
import skybiz.com.posoffline.ui_CreditNote.CreditNote;
import skybiz.com.posoffline.ui_Service.MService;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogSupplier extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svCustomer;
    String DocType;
    Spinner spSearchBy;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_customer, container, false);
        rv=(RecyclerView) view.findViewById(R.id.rvCustomer);
        svCustomer=(SearchView)view.findViewById(R.id.svCustomer);
        DocType=this.getArguments().getString("DOCTYPE_KEY");
        spSearchBy = (Spinner) view.findViewById(R.id.spSearchBy);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.searchby4_array, android.R.layout.simple_spinner_item);
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
        DownloadSupplier dCustomer=new DownloadSupplier(getActivity(),SearchBy,Keyword,rv, DialogSupplier.this);
        dCustomer.execute();
    }
    public void setSupplier(String CusCode,String CusName, String TermCode, String D_ay, String Tel, String Fax){
        if(DocType.equals("Service")){
            ((MService)getActivity()).setVendor(CusCode,CusName,Tel);
        }else if(DocType.equals("SO")){

        }
        dismiss();
    }
    /*private void setBar(String CusName){
        if(DocType.equals("CusCN")){
            ((CreditNote)getActivity()).setCustomerBar(CusName);
        }else if(DocType.equals("SO")){
            ((CashReceipt)getActivity()).setCustomerBar(CusName);
        }else if(DocType.equals("CR")){
            ((CashReceipt)getActivity()).setCustomerBar(CusName);
        }else if(DocType.equals("Service")){
            ((MService)getActivity()).setCustomerBar(CusName);
        }
        dismiss();
    }*/

}
