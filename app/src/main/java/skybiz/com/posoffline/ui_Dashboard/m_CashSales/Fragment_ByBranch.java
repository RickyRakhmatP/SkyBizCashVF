package skybiz.com.posoffline.ui_Dashboard.m_CashSales;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ipay.Ipay;
import com.ipay.IpayPayment;
import com.ipay.IpayResultDelegate;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CreditNote.m_Save.CheckPaidAmt;
import skybiz.com.posoffline.ui_Dashboard.m_CashSales.byBranch.SalesDDownload;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class Fragment_ByBranch extends Fragment {

    View view;
    RecyclerView rvBranch;
    TextView txtDate;
    Spinner spDate;
    DatePickerDialog datePickerDialog;
    private GridLayoutManager lLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bybranch, container, false);

        rvBranch=(RecyclerView)view.findViewById(R.id.rvBranch);
        txtDate=(TextView)view.findViewById(R.id.txtDate);
        spDate=(Spinner)view.findViewById(R.id.spDate);
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
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDate();
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dated = sdf.format(date);
        txtDate.setText(dated);
        retList();
        return view;
    }
    private void retList(){
        String D_ate=txtDate.getText().toString();
        rvBranch.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rvBranch.setLayoutManager(lLayout);
        rvBranch.setItemAnimator(new DefaultItemAnimator());
        SalesDDownload dDownload=new SalesDDownload(getActivity(),D_ate,rvBranch);
        dDownload.execute();
    }
    private void openDate(){
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
                txtDate.setText(year+"-"+mFormat.format(dMonth)+"-"+mFormat.format(dDay));
                retList();
            }
        },mYear,mMonth,mDay);
        datePickerDialog.show();
    }
    private void genDate(String TypeDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        String yesterday = sdf.format(calendar.getTime());
        String datenow = sdf.format(date);
        if(TypeDate.equals("Today")){
            txtDate.setEnabled(false);
            txtDate.setText(datenow);
            retList();
        }else if(TypeDate.equals("Yesterday")){
            txtDate.setEnabled(false);
            txtDate.setText(yesterday);
            retList();
        }else if(TypeDate.equals("Choose Date")){
            txtDate.setEnabled(true);
            txtDate.setText(datenow);
            openDate();
        }
    }
    private void loadDate(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.datebranch_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDate.setAdapter(adapter);
    }
}
