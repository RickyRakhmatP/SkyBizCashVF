package skybiz.com.posoffline.ui_Member;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import  skybiz.com.posoffline.R;

public class HistoryPointOld extends AppCompatActivity {


    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svCustomer;
    Spinner spSearchBy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_point);
        rv=(RecyclerView)findViewById(R.id.rvHistoryPoint);
        spSearchBy = (Spinner)findViewById(R.id.spSearchBy);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.searchby2_array, android.R.layout.simple_spinner_item);
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

        svCustomer=(SearchView)findViewById(R.id.svCustomer);
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
    }

    private void refresh(String keyword){
        String SearchBy = spSearchBy.getSelectedItem().toString();
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        //DownloadHistory dCustomer=new DownloadHistory(getActivity(),SearchBy,Keyword,rv,DialogCustomer.this);
       // dCustomer.execute();
    }
}
