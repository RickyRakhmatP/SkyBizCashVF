package skybiz.com.posoffline.ui_Listing;

import android.app.DatePickerDialog;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Trn;
import skybiz.com.posoffline.ui_Listing.m_ListSO.TrnAdapter;
import skybiz.com.posoffline.ui_Listing.m_Listing.DownloaderList;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;


/**
 * Created by 7 on 29/12/2017.
 */

public class Fragment_ListSO extends Fragment {
    View view;
    SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    /*TrnAdapter adapter;
    ArrayList<Spacecraft_Trn> lsso=new ArrayList<>();
    SearchView sv;*/
    Button btnPrint,btnRefresh;
    EditText txtDateFrom,txtDateTo;
    DatePickerDialog datePickerDialog;
    Switch swType;
    LinearLayout lnType;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_cs, container, false);
        rv=(RecyclerView)view.findViewById(R.id.rec_list_cs);
        btnPrint=(Button)view.findViewById(R.id.btnPrint);
        btnRefresh=(Button)view.findViewById(R.id.btnRefresh);
        txtDateFrom=(EditText)view.findViewById(R.id.txtDateFrom);
        txtDateTo=(EditText)view.findViewById(R.id.txtDateTo);
        swType=(Switch)view.findViewById(R.id.swType);
        lnType=(LinearLayout)view.findViewById(R.id.lnType);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        /*sv= (SearchView)view.findViewById(R.id.searchtrn);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                //FILTER AS YOU TYPE
                adapter.getFilter().filter(query);
                return false;
            }
        });*/
        txtDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c=Calendar.getInstance();
                int mYear=c.get(Calendar.YEAR);
                final int mMonth=c.get(Calendar.MONTH);
                int mDay=c.get(Calendar.DAY_OF_MONTH);
                final DecimalFormat mFormat= new DecimalFormat("00");
                datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        final Double dMonth=(monthOfYear+1)*1.00;
                        final Double dDay=dayOfMonth*1.00;
                        txtDateFrom.setText(year+"-"+mFormat.format(dMonth)+"-"+mFormat.format(dDay));
                    }
                },mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });
        txtDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c=Calendar.getInstance();
                int mYear=c.get(Calendar.YEAR);
                int mMonth=c.get(Calendar.MONTH);
                int mDay=c.get(Calendar.DAY_OF_MONTH);
                final DecimalFormat mFormat= new DecimalFormat("00");
                datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        final Double dMonth=(monthOfYear+1)*1.00;
                        final Double dDay=dayOfMonth*1.00;
                        txtDateTo.setText(year+"-"+mFormat.format(dMonth)+"-"+mFormat.format(dDay));
                    }
                },mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnPrint();
            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        swType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    swType.setText("Detail");
                }else{
                    swType.setText("Summary");
                }
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dated = sdf.format(date);
        txtDateFrom.setText(dated);
        txtDateTo.setText(dated);
        refresh();
        lnType.setVisibility(View.VISIBLE);
        return view;
    }

    private void fnPrint() {
        String DateFrom = txtDateFrom.getText().toString();
        String DateTo = txtDateTo.getText().toString();
        String T_ype = "Summary";
        if (swType.isChecked()) {
            T_ype = "Detail";
        }
        PrintList printList=new PrintList(getActivity(),"SO",T_ype,DateFrom,DateTo);
        printList.execute();
        refresh();
    }
    public void refresh() {
        String DateFrom=txtDateFrom.getText().toString();
        String DateTo=txtDateTo.getText().toString();
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloaderList downloaderList=new DownloaderList(getActivity(),"SO",DateFrom,DateTo,rv);
        downloaderList.execute();
        //adapter=new TrnAdapter(getActivity(),lsso,getActivity().getSupportFragmentManager());
        //retSO("","");
    }

    public void retSO(String DateFrom,String DateTo){
        /*String vClause="";
        if(!DateFrom.isEmpty() && !DateTo.isEmpty()){
            vClause="AND H.D_ate>='"+DateFrom+"' AND H.D_ate<='"+DateTo+"' ";
        }
        lsso.clear();
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String vQuery="select H.Doc1No, H.D_ate, H.HCNetAmt, H.SynYN ,Sum(D.Qty) as Qty " +
                "from stk_sales_order_hd H inner join stk_sales_order_dt D on D.Doc1No=H.Doc1No " +
                "Where H.DocType='SO' "+vClause+" Group By H.Doc1No Order By H.RunNo Desc";
        Cursor c=db.getQuery(vQuery);
        while (c.moveToNext()) {
            String Doc1No=c.getString(0);
            String Dated=c.getString(1);
            String HCNetAmt=c.getString(2);
            String SynYN=c.getString(3);
            String Qty=c.getString(4);
            Spacecraft_Trn s=new Spacecraft_Trn();
            s.setDoc1No(Doc1No);
            s.setD_ateTime(Dated);
            s.setHCNetAmt(HCNetAmt);
            s.setQty(Qty);
            s.setSynYN(SynYN);
            lsso.add(s);
        }
        //CHECK IF ARRAYLIST ISNT EMPTY
        if(!(lsso.size()<1)) {
            rvCashReceipt.setAdapter(adapter);
        }
        db.closeDB();*/
    }
}
