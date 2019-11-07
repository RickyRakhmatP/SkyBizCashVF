package skybiz.com.posoffline.m_NewTerm;

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
import skybiz.com.posoffline.ui_Service.MService;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogTerm extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svTerm;
    String DocType;
    Spinner spSearchBy;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_term, container, false);
        rv=(RecyclerView) view.findViewById(R.id.rvTerm);
        svTerm=(SearchView)view.findViewById(R.id.svTerm);
        DocType=this.getArguments().getString("DOCTYPE_KEY");
        spSearchBy = (Spinner) view.findViewById(R.id.spSearchBy);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.searchby_array, android.R.layout.simple_spinner_item);
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

        svTerm.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        svTerm.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                refresh("");
                return false;
            }
        });
        getDialog().setTitle("List of Term");
        refresh("");
        return view;
    }


    public void refresh(final String Keyword) {
        String SearchBy = spSearchBy.getSelectedItem().toString();
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloadTerm dCustomer=new DownloadTerm(getActivity(),SearchBy,Keyword,rv, DialogTerm.this);
        dCustomer.execute();
    }
    public void setTerm(String TermCode,String TermDesc, String D_ay){
        ((MService)getActivity()).setD_ay(TermCode,TermDesc,D_ay);
        dismiss();
    }

}
