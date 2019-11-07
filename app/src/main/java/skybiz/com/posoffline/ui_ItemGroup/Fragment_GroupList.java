package skybiz.com.posoffline.ui_ItemGroup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import skybiz.com.posoffline.ui_Item.m_ItemListing.DownloaderItem;
import skybiz.com.posoffline.ui_ItemGroup.m_GroupListing.DownloaderGroup;

/**
 * Created by 7 on 11/12/2017.
 */

public class Fragment_GroupList extends Fragment {

    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svItem;
    String DocType;
    Spinner spSearchBy;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_item, container, false);
        rv=(RecyclerView) view.findViewById(R.id.rvItem);
        svItem=(SearchView)view.findViewById(R.id.svItem);
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
        svItem.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String keyword) {
                callSearch(keyword);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(),"CLICK, Search View", Toast.LENGTH_SHORT).show();
                return true;
            }
            public void callSearch(String keyword) {
                refresh(keyword);
            }
        });
        svItem.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                refresh("");
                return false;
            }
        });
        refresh("");
        return view;
    }
    public void refresh(String Keyword) {
        String SearchBy = spSearchBy.getSelectedItem().toString();
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloaderGroup downloaderItem=new DownloaderGroup(getActivity(),SearchBy,
                Keyword,rv);
        downloaderItem.execute();
    }

}
