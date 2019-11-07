package skybiz.com.posoffline.m_NewCustomer;

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
import skybiz.com.posoffline.ui_Member.m_PointLedger.HistoryPoint;
import skybiz.com.posoffline.ui_Member.m_PointRedemption.PointRedeem;
import skybiz.com.posoffline.ui_SalesOrder.SalesOrder;
import skybiz.com.posoffline.ui_Service.MService;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogCustomer extends DialogFragment {
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
        DownloaderCustomer dCustomer=new DownloaderCustomer(getActivity(),SearchBy,Keyword,rv, DialogCustomer.this);
        dCustomer.execute();
    }
    public void setCustomer(String CusCode,String CusName, String TermCode,
                            String D_ay, String Address, String TermDesc,
                            String SalesPersonCode){
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String vDel="delete from tb_member";
        db.addQuery(vDel);
        String vInsert="insert into tb_member(CusCode, CusName, TermCode, D_ay, SalesPersonCode)" +
                "values('"+CusCode+"', '"+CusName+"', '"+TermCode+"', '"+D_ay+"', '"+SalesPersonCode+"')";
        db.addQuery(vInsert);
        db.closeDB();
        setBar(CusCode,CusName,Address,TermCode,TermDesc);
    }
    private void setBar(String CusCode,String CusName,String Address, String TermCode, String TermDesc){
        if(DocType.equals("CusCN")){
            ((CreditNote)getActivity()).setCustomerBar(CusName);
        }else if(DocType.equals("SO")){
            ((SalesOrder)getActivity()).setCustomerBar(CusName);
        }else if(DocType.equals("CR")){
            ((CashReceipt)getActivity()).setCustomerBar();
        }else if(DocType.equals("Service")){
            ((MService)getActivity()).setCustomer(CusCode,CusName,Address,TermCode,TermDesc);
        }else if(DocType.equals("HistoryPoint")){
            ((HistoryPoint)getActivity()).retHistory(CusCode,CusName);
        }else if(DocType.equals("PR")){
            ((PointRedeem)getActivity()).retBF(CusCode,CusName);
        }
        dismiss();
    }

}
