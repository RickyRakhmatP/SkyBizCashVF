package skybiz.com.posoffline.ui_Listing;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_Listing.m_ItemSales.ItemSalesDownload;
import skybiz.com.posoffline.ui_Listing.m_Listing.DownloaderList;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;


/**
 * Created by 7 on 29/12/2017.
 */

public class Fragment_ItemSales extends Fragment {
    View view;
    SwipeRefreshLayout swipeRefreshLayout;
    private GridLayoutManager lLayout;
    RecyclerView rvCashReceipt;
    Button btnPrint,btnRefresh;
    EditText txtDateFrom,txtDateTo;
    DatePickerDialog datePickerDialog;
    Spinner spItemGroup;
   //z LinearLayout lnType;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_itemsales, container, false);
        rvCashReceipt=(RecyclerView)view.findViewById(R.id.rvItemSales);
        btnPrint=(Button)view.findViewById(R.id.btnPrint);
        btnRefresh=(Button)view.findViewById(R.id.btnRefresh);
        txtDateFrom=(EditText)view.findViewById(R.id.txtDateFrom);
        txtDateTo=(EditText)view.findViewById(R.id.txtDateTo);
        spItemGroup=(Spinner)view.findViewById(R.id.spItemGroup);
        //lnType=(LinearLayout)view.findViewById(R.id.lnType);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dated = sdf.format(date);
        txtDateFrom.setText(dated);
        txtDateTo.setText(dated);
        initGroup();
        readyprinter();
        //refresh();
       // lnType.setVisibility(View.VISIBLE);
        return view;
    }
    private void initGroup(){
        RetGroup retGroup=new RetGroup(getActivity());
        retGroup.execute();
    }
    private void fnPrint(){
        String DateFrom     =txtDateFrom.getText().toString();
        String DateTo       =txtDateTo.getText().toString();
        String ItemGroup    =spItemGroup.getSelectedItem().toString();
        PrintItemSales printList=new PrintItemSales(getActivity(),DateFrom,DateTo,ItemGroup);
        printList.execute();
        refresh();
    }
    private void readyprinter(){
        try {
            String TypePrinter="";
            DBAdapter db=new DBAdapter(getActivity());
            db.openDB();
            Cursor cPrinter = db.getSettingPrint();
            while (cPrinter.moveToNext()) {
                TypePrinter = cPrinter.getString(1);
            }
            db.closeDB();
            if (TypePrinter.equals("AIDL")) {
                AidlUtil.getInstance().connectPrinterService(getActivity());
                AidlUtil.getInstance().initPrinter();
            } else {
                //
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    public void refresh() {
        String DateFrom=txtDateFrom.getText().toString();
        String DateTo=txtDateTo.getText().toString();
        String ItemGroup=spItemGroup.getSelectedItem().toString();

        rvCashReceipt.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rvCashReceipt.setLayoutManager(lLayout);
        rvCashReceipt.setItemAnimator(new DefaultItemAnimator());
        ItemSalesDownload downloaderList=new ItemSalesDownload(getActivity(),DateFrom,DateTo,ItemGroup,rvCashReceipt);
        downloaderList.execute();
    }

    public class RetGroup extends AsyncTask<Void,Void,String> {
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
        ArrayList lsGroup=new ArrayList<String>();

        public RetGroup(Context c) {
            this.c = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.downloadData();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                spItemGroup.setAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,lsGroup));
            }else{
                Toast.makeText(c,"Group is empty", Toast.LENGTH_SHORT).show();
            }
        }

        private String downloadData() {
            try {
                lsGroup.clear();
                DBAdapter db=new DBAdapter(c);
                db.openDB();
                String querySet="select * from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(1);
                    UserName = curSet.getString(2);
                    Password = curSet.getString(3);
                    DBName = curSet.getString(4);
                    Port = curSet.getString(5);
                    DBStatus=curSet.getString(7);
                    ItemConn=curSet.getString(8);
                }
                String sql ="select ItemGroup, Description from stk_group Order By ItemGroup ";
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        lsGroup.add("");
                        Statement stmt = conn.createStatement();
                        stmt.execute(sql);
                        ResultSet resultSet = stmt.getResultSet();
                        int i=0;
                        while (resultSet.next()) {
                            lsGroup.add(resultSet.getString(1));
                            i++;
                        }
                        if(i>0){
                            z="success";
                        }else{
                            z="error";
                        }
                    }
                }else{
                    lsGroup.add("");
                    Cursor rsGroup=db.getQuery(sql);
                    int i=0;
                    while(rsGroup.moveToNext()){
                        lsGroup.add(rsGroup.getString(0));
                        i++;
                    }
                    if(i>0){
                        z="success";
                    }else{
                        z="error";
                    }
                }
                db.closeDB();
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return z;
        }
    }

}
