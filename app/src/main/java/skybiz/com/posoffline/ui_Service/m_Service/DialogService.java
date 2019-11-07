package skybiz.com.posoffline.ui_Service.m_Service;

import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.Arrays;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CreditNote.CreditNote;
import skybiz.com.posoffline.ui_Service.MService;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogService extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svService;
    String DocType;
    Spinner spSearchBy;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_service, container, false);
        rv=(RecyclerView) view.findViewById(R.id.rvService);
        DocType=this.getArguments().getString("DOCTYPE_KEY");
       /* svService=(SearchView)view.findViewById(R.id.svService);

        spSearchBy = (Spinner) view.findViewById(R.id.spSearchBy2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.searchby3_array, android.R.layout.simple_spinner_item);
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

        svService.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        svService.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                refresh("");
                return false;
            }
        });*/
        getDialog().setTitle("List of Service");
        refresh("");
        return view;
    }

    private void retSearchBy(){
        final ArrayList<String> list = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.searchby3_array)));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final int pos = list.indexOf(DocType);
                spSearchBy.setSelection(pos); }
            }, 100);

    }
    public void refresh(final String Keyword) {
        String SearchBy =DocType;
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloadService dCustomer=new DownloadService(getActivity(),SearchBy,Keyword,rv, DialogService.this);
        dCustomer.execute();
        /*retSearchBy();
        final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
               }
            }, 500);*/
    }
    public void setService(String Particular){
        ((MService)getActivity()).setParticular(DocType,Particular);
        dismiss();
    }

}
