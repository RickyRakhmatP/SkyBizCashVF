package skybiz.com.posoffline.ui_Dashboard.m_CashSales;

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
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_NewSummary.AddItem_BySearch;
import skybiz.com.posoffline.m_NewSummary.DownloaderOrder;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Dashboard.m_CashSales.byBranch.SalesD2Download;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class Fragment_ByDate extends Fragment {

    View view;
    TextView txtDateFrom,txtDateTo;
    Spinner spBranch,spDate;
    DatePickerDialog datePickerDialog;
    RecyclerView rvBranch;
    private GridLayoutManager lLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bydate, container, false);
        txtDateFrom=(TextView)view.findViewById(R.id.txtDateFrom);
        txtDateTo=(TextView)view.findViewById(R.id.txtDateTo);
        spBranch=(Spinner)view.findViewById(R.id.spBranch);
        spDate=(Spinner)view.findViewById(R.id.spDate);
        rvBranch=(RecyclerView)view.findViewById(R.id.rvBranch);
        txtDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDate(txtDateFrom);
            }
        });
        txtDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDate(txtDateTo);
            }
        });
        loadBranch();
        loadDate();
        spDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String Dated=adapterView.getItemAtPosition(i).toString();
                genDate(Dated);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }
    private void loadBranch(){
        RetBranch retBranch=new RetBranch(getActivity());
        retBranch.execute();
    }
    private void loadDate(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.datebydate_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDate.setAdapter(adapter);
    }
    private void openDate(final TextView txtView){
        final Calendar c=Calendar.getInstance();
        final Calendar cale=Calendar.getInstance();
        int mYear=c.get(Calendar.YEAR);
        final int mMonth=c.get(Calendar.MONTH);
        int mDay=c.get(Calendar.DAY_OF_MONTH);
        final DecimalFormat mFormat= new DecimalFormat("00");
        datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                final Double dMonth=(monthOfYear+1)*1.00;
                final Double dDay=dayOfMonth*1.00;
                txtView.setText(year+"-"+mFormat.format(dMonth)+"-"+mFormat.format(dDay));
                getTotal();
            }
        },mYear,mMonth,mDay);
       /* String TypeDate=spDate.getSelectedItem().toString();
        cale.add(Calendar.DAY_OF_MONTH, -30);
        if(TypeDate.equals("Choose Date")){
            datePickerDialog.getDatePicker().setMinDate(cale.getTimeInMillis());
        }*/
        datePickerDialog.show();
    }
    private void openDate2(){
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
                getTotal();
            }
        },mYear,mMonth,mDay);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }
    private void genDate(String TypeDate){
        Log.d("TYPE DATE", TypeDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -7);
        String sevenday = sdf.format(calendar.getTime());
        String datenow = sdf.format(date);
        if(TypeDate.equals("Today")){
            txtDateFrom.setEnabled(false);
            txtDateTo.setVisibility(View.GONE);
            txtDateTo.setText("");
            txtDateFrom.setText(datenow);
            getTotal();
        }else if(TypeDate.equals("7 Days")){
            txtDateFrom.setText(sevenday);
            txtDateTo.setText(datenow);
            txtDateFrom.setEnabled(true);
            txtDateTo.setVisibility(View.VISIBLE);
            getTotal();
        }else if(TypeDate.equals("Choose Date")){
            txtDateTo.setVisibility(View.VISIBLE);
            txtDateFrom.setEnabled(true);
            txtDateTo.setText(datenow);
            openDate(txtDateFrom);
        }else{
            txtDateTo.setVisibility(View.GONE);
            txtDateFrom.setText(datenow);
        }
    }
    private void getTotal(){
        String BranchCode   =spBranch.getSelectedItem().toString();
        String vBranchCode="";
        if(!BranchCode.isEmpty()) {
            String[] separated = BranchCode.split("-");
            vBranchCode = separated[0].trim();
        }
        String DateFrom     =txtDateFrom.getText().toString();
        String DateTo       =txtDateTo.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date datefrom = sdf.parse(DateFrom);
            Date datefto= sdf.parse(DateTo);
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(datefrom);
            cal2.setTime(datefto);
            long millis1 = cal1.getTimeInMillis();
            long millis2 = cal2.getTimeInMillis();

            // Calculate difference in milliseconds
            long diff = millis2 - millis1;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            Log.d("diffDays","days"+diffDays);
            rvBranch.setHasFixedSize(true);
            lLayout = new GridLayoutManager(getActivity(), 1);
            rvBranch.setLayoutManager(lLayout);
            rvBranch.setItemAnimator(new DefaultItemAnimator());

            if(diffDays>30) {
                Toast.makeText(getActivity(),"Different days cannot more than 30", Toast.LENGTH_SHORT).show();
            }else{
                SalesD2Download d2Download = new SalesD2Download(getActivity(), vBranchCode, DateFrom, DateTo, rvBranch);
                d2Download.execute();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public class RetBranch extends AsyncTask<Void,Void,String> {
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
        ArrayList lsBranch=new ArrayList<String>();

        public RetBranch(Context c) {
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
                spBranch.setAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,lsBranch));
            }else{
                Toast.makeText(c,"branch is empty", Toast.LENGTH_SHORT).show();
            }
        }

        private String downloadData() {
            try {
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
                String sql ="select IFNULL(BranchCode,'')as BranchCode, IFNULL(Description,'')as Description " +
                        "from stk_branch Order By Description ";
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement stmt = conn.createStatement();
                        stmt.execute(sql);
                        ResultSet resultSet = stmt.getResultSet();
                        int i=0;
                        lsBranch.add("All Branches");
                        while (resultSet.next()) {
                            lsBranch.add(resultSet.getString(1)+" - "+resultSet.getString(2));
                            i++;
                        }
                        if(i>0){
                            z="success";
                        }else{
                            z="error";
                        }
                    }
                }else{
                    Cursor rsBranch=db.getQuery(sql);
                    int i=0;
                    lsBranch.add("All Branches");
                    while(rsBranch.moveToNext()){
                        lsBranch.add(rsBranch.getString(0)+" - "+rsBranch.getString(1));
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
