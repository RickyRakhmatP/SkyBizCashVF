package skybiz.com.posoffline.ui_CashReceipt.m_Customer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_SO;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.ScanTable;
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
    LinearLayout lnScan;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view        = inflater.inflate(R.layout.dialog_customer, container, false);
        rv          = (RecyclerView) view.findViewById(R.id.rvCustomer);
        spSearchBy  = (Spinner) view.findViewById(R.id.spSearchBy);
        lnScan      = (LinearLayout) view.findViewById(R.id.lnScan);

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
        lnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScanner();
            }
        });
        getDialog().setTitle("List of Customer");
        refresh("");
        return view;
    }

    private void openScanner(){
        Intent i=new Intent(getActivity(), ScanTable.class);
        i.putExtra("DOCTYPE_KEY", "Search Customer");
        startActivity(i);
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
    public void setCustomer(String CusCode,String CusName, String TermCode,
                            String D_ay, String SalesPersonCode, String MembershipClass,
                            String RatioPoint, String RatioAmount, String ContactTel,
                            String Email, String CategoryCode){
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String vDel="delete from tb_member";
        db.addQuery(vDel);
        String vInsert="insert into tb_member(CusCode, CusName, TermCode, " +
                "D_ay, SalesPersonCode,MembershipClass," +
                "RatioPoint,RatioAmount,ContactTel," +
                "Email, CategoryCode)" +
                "values('"+CusCode+"', '"+CusName+"', '"+TermCode+"'," +
                " '"+D_ay+"', '"+SalesPersonCode+"', '"+MembershipClass+"'," +
                " '"+RatioPoint+"', '"+RatioAmount+"', '"+ContactTel+"'," +
                "'"+Email+"', '"+CategoryCode+"')";
        db.addQuery(vInsert);
        String qCheck="select count(*)as totals from tb_salesperson";
        Cursor rsCheck=db.getQuery(qCheck);
        int numrows=0;
        while(rsCheck.moveToNext()){
            numrows=rsCheck.getInt(0);
        }
        if(numrows==0) {
            //String vDel2="delete from tb_salesperson";
            //db.addQuery(vDel2);
            String qInsert = "insert into tb_salesperson(SalesPersonCode,SalesPersonName)values" +
                    "('" + SalesPersonCode + "', '')";
            db.addQuery(qInsert);
        }
        db.closeDB();
        ((CashReceipt)getActivity()).setCustomerBar();
        dismiss();
    }

}
