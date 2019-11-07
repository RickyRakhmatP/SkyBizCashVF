package skybiz.com.posoffline.ui_Service.m_List;

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
import skybiz.com.posoffline.ui_Service.MService;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogListJS extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svCustomer;
    String Status="";
    Spinner spSearchBy;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_service, container, false);
        rv = (RecyclerView) view.findViewById(R.id.rvService);
        Status = this.getArguments().getString("STATUS_KEY");
       /* svCustomer = (SearchView) view.findViewById(R.id.svCustomer);
        spSearchBy = (Spinner) view.findViewById(R.id.spSearchBy);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.searchby2_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSearchBy.setAdapter(adapter);
        spSearchBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String Item = adapterView.getItemAtPosition(i).toString();
                if (Item.equals("Show All")) {
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
        });*/
        getDialog().setTitle("");
        if(Status.equals("Open")) {
            getDialog().setTitle("List of Outstanding JS");
        }else if(Status.equals("Closed")){
            getDialog().setTitle("List of Closed JS");
        }
        refresh("");
        return view;
    }

    public void refresh(String Keyword) {
        //String SearchBy = spSearchBy.getSelectedItem().toString();
        String SearchBy="All";
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloadListJS downloadListJS = new DownloadListJS(getActivity(),Status, SearchBy, Keyword, rv, DialogListJS.this);
        downloadListJS.execute();
    }

}
