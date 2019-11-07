package skybiz.com.posoffline.m_NewReprint;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogReprint extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    EditText txtDateFrom,txtDateTo;
    Button btnRefresh;
    String TypePrinter,NamePrinter,IPPrinter,Port;
    String DocType;
    DatePickerDialog datePickerDialog;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_reprint, container, false);

        DocType=this.getArguments().getString("DOCTYPE_KEY");
        txtDateFrom=(EditText)view.findViewById(R.id.txtDateFrom);
        txtDateTo=(EditText)view.findViewById(R.id.txtDateTo);
        btnRefresh=(Button)view.findViewById(R.id.btnRefresh);

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
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        getDialog().setTitle("List of Reprint");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dated = sdf.format(date);
        txtDateFrom.setText(dated);
        txtDateTo.setText(dated);
        refresh();
        readyPrinter();
        return view;
    }
    public void refresh() {
        String DateFrom=txtDateFrom.getText().toString();
        String DateTo=txtDateTo.getText().toString();
        rv=(RecyclerView) view.findViewById(R.id.rvReprint);
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloaderReprint dCustomer=new DownloaderReprint(getActivity(),DocType,DateFrom,DateTo,rv, DialogReprint.this);
        dCustomer.execute();
    }
    private void readyPrinter(){
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String query="select TypePrinter,NamePrinter,IPPrinter,Port from tb_settingprinter";
        Cursor rsPrint=db.getQuery(query);
        while(rsPrint.moveToNext()){
            TypePrinter = rsPrint.getString(0);
            NamePrinter = rsPrint.getString(1);
            IPPrinter = rsPrint.getString(2);
            Port = rsPrint.getString(3);
        }
        if(TypePrinter.equals("AIDL")){
            AidlUtil.getInstance().connectPrinterService(getActivity());
            AidlUtil.getInstance().initPrinter();
        }else{

        }
        db.closeDB();
    }

    public void setReprint(String Doc1No){
        RePrint print=new RePrint(getActivity(),DocType,Doc1No);
        try {
            String results= print.execute().get();
            if(results.equals("error")){

            }else{
               dismiss();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //print.execute();
        //dismiss();
    }

}
