package skybiz.com.posoffline.ui_CashReceipt.m_Pay;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import skybiz.com.posoffline.MyKeyboard_Order;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddMisc;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 23/02/2018.
 */

public class  DialogTable extends DialogFragment {
    View view;
    Button btnOK,btnCancel,btnOK2,btnCancel2;
    EditText edTableNo,txtCardNo,txtNote1;
    LinearLayout lnTable,lnAddOn,lnAppointment,lnKeyboard,lnScan;
    TextView txtTimeApp,txtDateApp;
    DatePicker dpDateApp;
    TimePicker tpTimeApp;
    CheckBox chkApp;
    Boolean appointyn=false;
    MyKeyboard_Order keyboard;
    String T_ype="";
    Double dServiceCharges=0.00;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_table, container, false);
        btnCancel=(Button)view.findViewById(R.id.btnCancel);
        btnOK=(Button)view.findViewById(R.id.btnOK);
        btnCancel2=(Button)view.findViewById(R.id.btnCancel2);
        btnOK2=(Button)view.findViewById(R.id.btnOK2);
        lnTable=(LinearLayout)view.findViewById(R.id.lnTable);
        lnAddOn=(LinearLayout)view.findViewById(R.id.lnAddOn);
        lnKeyboard=(LinearLayout)view.findViewById(R.id.lnKeyboard);
        lnAppointment=(LinearLayout)view.findViewById(R.id.lnAppointment);
        txtTimeApp=(TextView)view.findViewById(R.id.txtTimeApp);
        txtDateApp=(TextView)view.findViewById(R.id.txtDateApp);
        dpDateApp=(DatePicker)view.findViewById(R.id.dpDateApp);
        tpTimeApp=(TimePicker)view.findViewById(R.id.tpTimeApp);
        chkApp=(CheckBox)view.findViewById(R.id.chkApp);
        edTableNo=(EditText)view.findViewById(R.id.edTableNo);
        txtCardNo=(EditText)view.findViewById(R.id.txtCardNo);
        txtNote1=(EditText)view.findViewById(R.id.txtNote1);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnCancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAddOn();
            }
        });
        lnScan=(LinearLayout)view.findViewById(R.id.lnScan);
        lnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScanner();
            }
        });

        btnOK2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder2();
                /*btnOK2.setEnabled(false);
                String TableNo=edTableNo.getText().toString();
                String CardNo=txtCardNo.getText().toString();
                String AnalysisCode2=TableNo;
                if(!CardNo.isEmpty()){
                    AnalysisCode2=TableNo+";"+CardNo;
                }
                FnAddOnOrder fnaddon=new FnAddOnOrder(getActivity(),AnalysisCode2);
                fnaddon.execute();
                dismiss();*/
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnconfirm();
                //confirmOrder();
                //checkduplicate();
            }
        });
        keyboard=(MyKeyboard_Order)view.findViewById(R.id.keyboard);
        edTableNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                edTableNo.setRawInputType(InputType.TYPE_CLASS_TEXT);
                edTableNo.setTextIsSelectable(true);
                InputConnection ic=edTableNo.onCreateInputConnection(new EditorInfo());
                keyboard.setInputConnection(ic);
                edTableNo.setInputType(0);
               // hideKeyboard(edTableNo);
                return false;
            }
        });

        txtCardNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtCardNo.setRawInputType(InputType.TYPE_CLASS_TEXT);
                InputConnection ic2=txtCardNo.onCreateInputConnection(new EditorInfo());
                keyboard.setInputConnection(ic2);
                txtCardNo.setInputType(0);
                //hideKeyboard(txtCardNo);
                return false;
            }
        });
        chkApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lnKeyboard.setVisibility(View.GONE);
                    lnAppointment.setVisibility(View.VISIBLE);
                    appointyn=true;
                }else{
                    lnKeyboard.setVisibility(View.VISIBLE);
                    lnAppointment.setVisibility(View.GONE);
                    appointyn=false;
                }
            }
        });
        txtTimeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tpTimeApp.setVisibility(View.VISIBLE);
                dpDateApp.setVisibility(View.GONE);
            }
        });
        txtDateApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tpTimeApp.setVisibility(View.GONE);
                dpDateApp.setVisibility(View.VISIBLE);
            }
        });

        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        dpDateApp.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                txtDateApp.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
            }
        });
        tpTimeApp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                txtTimeApp.setText(hourOfDay+":"+minute);
            }
        });
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdf2=new SimpleDateFormat("yyy-MM-dd");
        Date date=new Date();
        final String timed=sdf.format(date);
        final String dated=sdf2.format(date);
        txtTimeApp.setText(timed);
        txtDateApp.setText(dated);
        InputConnection ic=edTableNo.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        edTableNo.setTextIsSelectable(true);
        edTableNo.setInputType(0);
        txtCardNo.setInputType(0);
        initHold();
        //hideKeyboard(edTableNo);
        return view;
    }
    /*private void callBackFg(){
        Fragment_Payment frag = (Fragment_Payment)getTargetFragment();
        if(frag != null){
            frag.refreshPayment();
        }
    }*/
    private void openScanner(){
        Intent i=new Intent(getActivity(), ScanTable.class);
        i.putExtra("DOCTYPE_KEY", T_ype);
        startActivity(i);
        dismiss();
    }
    private void initHold(){
        try{
            DBAdapter db=new DBAdapter(getActivity());
            db.openDB();
            String qCheck="select Doc1No,Doc2No,Attention from dum_stk_sales_order_hd";
            Cursor rsCheck=db.getQuery(qCheck);
            int i=0;
            String Doc2No="";
            while(rsCheck.moveToNext()){
                Doc2No=rsCheck.getString(1);
                if(Doc2No.contains(";")){
                    final String[] separated = Doc2No.split(";");
                    String vTableNo = separated[0];
                    String vCardNo = separated[1];
                    edTableNo.setText(vTableNo);
                    txtCardNo.setText(vCardNo);
                }else{
                    edTableNo.setText(Doc2No);
                }
                txtNote1.setText(rsCheck.getString(2));
                txtNote1.setEnabled(false);
                i++;
            }
            if(i>0){
                getDialog().setTitle("Add On");
                T_ype="Add On";
                edTableNo.setEnabled(false);
                txtCardNo.setEnabled(false);
            }else {
                getDialog().setTitle("New Order");
                T_ype="New Order";
                edTableNo.setEnabled(true);
                txtCardNo.setEnabled(true);
            }
            String qOtherSet="select ServiceCharges from tb_othersetting";
            Cursor rsOther=db.getQuery(qOtherSet);
            while(rsOther.moveToNext()){
                dServiceCharges=rsOther.getDouble(0);
                if(dServiceCharges>0 && !T_ype.equals("Add On")){
                    AddMisc addMisc=new AddMisc(getActivity(),"CS", "0");
                    addMisc.execute();
                }
            }
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    private void fnconfirm(){
        String TableNo  =edTableNo.getText().toString();
        String CardNo   =txtCardNo.getText().toString();
        if(T_ype.equals("Add On")) {
            confirmOrder2();
        }else{
            CheckDuplicate check = new CheckDuplicate(getActivity(), TableNo, CardNo);
            check.execute();
        }
    }
    private void showAddOn(){
        lnAddOn.setVisibility(View.VISIBLE);
        lnTable.setVisibility(View.GONE);
    }
    private void hideAddOn(){
        lnAddOn.setVisibility(View.GONE);
        lnTable.setVisibility(View.VISIBLE);
    }
    private void checkduplicate(){
        String TableNo=edTableNo.getText().toString();
        String CardNo=txtCardNo.getText().toString();
        CheckDuplicate check=new CheckDuplicate(getActivity(),TableNo,CardNo);
        check.execute();
    }
    private void confirmOrder2(){
        try {
            btnOK2.setEnabled(false);
            String TableNo = edTableNo.getText().toString();
            String CardNo = txtCardNo.getText().toString();
            String AnalysisCode2 = TableNo;
            if (!CardNo.isEmpty()) {
                AnalysisCode2 = TableNo + ";" + CardNo;
            }
            if (dServiceCharges > 0) {
                AddMisc addMisc = new AddMisc(getActivity(), "SO", AnalysisCode2);
                addMisc.execute();
            }
            Thread.sleep(2000);
            FnAddOnOrder fnaddon = new FnAddOnOrder(getActivity(), AnalysisCode2);
            fnaddon.execute();
            dismiss();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void confirmOrder(){
        try {
            btnOK.setEnabled(false);
            String TableNo = edTableNo.getText().toString();
            String CardNo = txtCardNo.getText().toString();
            String Attention = txtNote1.getText().toString();
            String Doc3No = txtTimeApp.getText().toString() + "\r\n" + txtDateApp.getText().toString();
            String AnalysisCode2 = TableNo;
            if (!CardNo.isEmpty()) {
                AnalysisCode2 = TableNo + ";" + CardNo;
            }
            if (TableNo.isEmpty() && CardNo.isEmpty()) {
                Toast.makeText(getContext(), "Card Number Cannot Empty", Toast.LENGTH_SHORT).show();
            } else {
                Thread.sleep(2000);
                FnSaveOrder fnsave = new FnSaveOrder(getActivity(), AnalysisCode2, Doc3No,
                        Attention);
                String NewDoc = fnsave.execute().get();
                if (NewDoc.equals("error")) {

                } else if (NewDoc.equals("success")) {
                        ((CashReceipt) getActivity()).refreshNext();
                        dismiss();
                } else if (NewDoc.equals("duplicate")) {
                    btnOK.setEnabled(true);
                        //showAddOn();
                }
            }
        }  catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    public class CheckDuplicate extends AsyncTask<Void,Void,String>{
        Context c;
        String TableNo,CardNo;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus;

        public CheckDuplicate(Context c, String tableNo, String cardNo) {
            this.c = c;
            TableNo = tableNo;
            CardNo = cardNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.fncheck();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("cannot")){
                Toast.makeText(c,"Cannot Proceed", Toast.LENGTH_SHORT).show();
            }else if(result.equals("duplicate")){
                txtCardNo.setText(CardNo);
                edTableNo.setText(TableNo);
                Toast.makeText(c,"Table Occupied !",Toast.LENGTH_SHORT).show();
                edTableNo.requestFocus();
                //showAddOn();
            }else if(result.equals("addnew")){
                txtCardNo.setText(CardNo);
                edTableNo.setText(TableNo);
                confirmOrder();
            }else if(result.equals("compulsory")){
                Toast.makeText(c,"Cannot Proceed, compulsary card number", Toast.LENGTH_SHORT).show();
                txtCardNo.requestFocus();
            }
        }

        private String fncheck() {
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
                }
                String vDuplicate="";
                if(DBStatus.equals("1")){
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        if(TableNo.isEmpty()){
                            vDuplicate="select count(*) as numrows  from stk_sales_order_hd where " +
                                    "Status='Waiting'  and CONCAT(';',Doc2No) like '%;"+CardNo+"' ";
                            Statement stmtDup = conn.createStatement();
                            stmtDup.execute(vDuplicate);
                            ResultSet rsDup = stmtDup.getResultSet();
                            int numRows=0;
                            while (rsDup.next()) {
                                numRows = rsDup.getInt(1);
                            }
                            if(numRows>0){
                                String check="select Doc2No from stk_sales_order_hd where " +
                                        "Status='Waiting'  and CONCAT(';',Doc2No) like '%;"+CardNo+"'";
                                Statement stmtCheck = conn.createStatement();
                                stmtCheck.execute(check);
                                ResultSet rsCheck = stmtCheck.getResultSet();
                                String vTableNo="";
                                String vCardNo="";
                                while (rsCheck.next()) {
                                    String Doc2No=rsCheck.getString(1);
                                    if(Doc2No.contains(";")) {
                                        final String[] separated = Doc2No.split(";");
                                        vTableNo = separated[0];
                                        vCardNo = separated[1];
                                    }
                                    if(vCardNo.equals(CardNo)){
                                        TableNo=vTableNo;
                                        z="duplicate";
                                    }else{
                                        z="cannot";
                                    }

                                }
                            }else{
                                z="addnew";
                            }
                        }else if(CardNo.isEmpty()){
                            vDuplicate="select count(*) as numrows  from stk_sales_order_hd where " +
                                    "Status='Waiting'  and Doc2No='"+TableNo+"' ";
                            Statement stmtDup = conn.createStatement();
                            stmtDup.execute(vDuplicate);
                            ResultSet rsDup = stmtDup.getResultSet();
                            int numRows=0;
                            while (rsDup.next()) {
                                numRows = rsDup.getInt(1);
                            }
                            if(numRows>0){
                                z="duplicate";
                            }else{
                                String check="select Doc2No from stk_sales_order_hd where " +
                                        "Status='Waiting' and CONCAT(';',Doc2No) like '%"+TableNo+";%' ";
                                Log.d("Query",check);
                                Statement stmtCheck = conn.createStatement();
                                stmtCheck.execute(check);
                                ResultSet rsCheck = stmtCheck.getResultSet();
                                String vTableNo="";
                                String vCardNo="";
                                while (rsCheck.next()) {
                                    String vDoc2No=rsCheck.getString(1);
                                    if(vDoc2No.contains(";")) {
                                        final String[] separated = vDoc2No.split(";");
                                        vTableNo = separated[0];
                                        vCardNo = separated[1];
                                    }
                                }
                                if(!vCardNo.equals("")){
                                    CardNo=vCardNo;
                                    z="compulsory";
                                }else{
                                    z="addnew";
                                }
                            }
                        }else{
                            String Doc2No=TableNo+";"+CardNo;
                            vDuplicate="select count(*) as numrows  from stk_sales_order_hd where " +
                                    "Status='Waiting' and Doc2No='"+Doc2No+"' ";
                            Statement stmtDup = conn.createStatement();
                            stmtDup.execute(vDuplicate);
                            ResultSet rsDup = stmtDup.getResultSet();
                            int numRows=0;
                            while (rsDup.next()) {
                                numRows = rsDup.getInt(1);
                            }
                            if(numRows>0){
                                z="duplicate";
                            }else{
                                String check="select Doc2No from stk_sales_order_hd where " +
                                        "Status='Waiting' and CONCAT(';',Doc2No) like '%;"+CardNo+"' ";
                                Log.d("Query",check);
                                Statement stmtCheck = conn.createStatement();
                                stmtCheck.execute(check);
                                ResultSet rsCheck = stmtCheck.getResultSet();
                                String vTableNo="";
                                String vCardNo="";
                                while (rsCheck.next()) {
                                    String vDoc2No=rsCheck.getString(1);
                                    if(vDoc2No.contains(";")) {
                                        final String[] separated = vDoc2No.split(";");
                                        vTableNo = separated[0];
                                        vCardNo = separated[1];
                                    }
                                }
                                if(vTableNo.equals(TableNo) && !vCardNo.equals(CardNo)){
                                    z="addnew";
                                }else if(!vTableNo.equals(TableNo) && !vCardNo.equals(CardNo)){
                                    z="addnew";
                                }else{
                                    z="cannot";
                                }
                            }
                        }
                    }
                }else{
                    if(TableNo.isEmpty()){
                        vDuplicate="select count(*) as numrows  from stk_sales_order_hd where " +
                                "Status='Waiting'  and Doc2No || ';' like '%;"+CardNo+"' ";
                        Cursor rsDup = db.getQuery(vDuplicate);
                        int numRows=0;
                        while (rsDup.moveToNext()) {
                            numRows = rsDup.getInt(0);
                        }
                        if(numRows>0){
                            String check="select Doc2No from stk_sales_order_hd where " +
                                    "Status='Waiting'  and Doc2No || ';' like '%;"+CardNo+"'";
                            Cursor rsCheck = db.getQuery(check);
                            String vTableNo="";
                            String vCardNo="";
                            while (rsCheck.moveToNext()) {
                                String Doc2No=rsCheck.getString(0);
                                if(Doc2No.contains(";")) {
                                    final String[] separated = Doc2No.split(";");
                                    vTableNo = separated[0];
                                    vCardNo = separated[1];
                                }
                                if(vCardNo.equals(CardNo)){
                                    TableNo=vTableNo;
                                    z="duplicate";
                                }else{
                                    z="cannot";
                                }

                            }
                            //z="duplicate";
                        }else{
                            z="addnew";
                        }
                    }else if(CardNo.isEmpty()){
                        vDuplicate="select count(*) as numrows  from stk_sales_order_hd where " +
                                "Status='Waiting'  and Doc2No='"+TableNo+"' ";
                        Cursor rsDup = db.getQuery(vDuplicate);
                        int numRows=0;
                        while (rsDup.moveToNext()) {
                            numRows = rsDup.getInt(0);
                        }
                        if(numRows>0){
                            z="duplicate";
                        }else{
                            String check="select Doc2No from stk_sales_order_hd where " +
                                    "Status='Waiting' and Doc2No || ';' like '%"+TableNo+";%' ";
                            Log.d("Query",check);
                            Cursor rsCheck = db.getQuery(check);
                            String vTableNo="";
                            String vCardNo="";
                            while (rsCheck.moveToNext()) {
                                String vDoc2No=rsCheck.getString(0);
                                if(vDoc2No.contains(";")) {
                                    final String[] separated = vDoc2No.split(";");
                                    vTableNo = separated[0];
                                    vCardNo = separated[1];
                                }

                            }
                            if(!vCardNo.equals("")){
                                CardNo=vCardNo;
                                z="compulsory";
                            }else{
                                z="addnew";
                            }
                        }
                    }else{
                        String Doc2No=TableNo+";"+CardNo;
                        vDuplicate="select count(*) as numrows  from stk_sales_order_hd where " +
                                "Status='Waiting' and Doc2No='"+Doc2No+"' ";
                        Cursor rsDup = db.getQuery(vDuplicate);
                        int numRows=0;
                        while (rsDup.moveToNext()) {
                            numRows = rsDup.getInt(0);
                        }
                        if(numRows>0){
                            z="duplicate";
                        }else{
                            String check="select Doc2No from stk_sales_order_hd where " +
                                    "Status='Waiting' and Doc2No || ';' like '%;"+CardNo+"' ";
                            Log.d("Query",check);
                            Cursor rsCheck = db.getQuery(check);
                            String vTableNo="";
                            String vCardNo="";
                            while (rsCheck.moveToNext()) {
                                String vDoc2No=rsCheck.getString(0);
                                if(vDoc2No.contains(";")) {
                                    final String[] separated = vDoc2No.split(";");
                                    vTableNo = separated[0];
                                    vCardNo = separated[1];
                                }
                            }
                            if(vTableNo.equals(TableNo) && !vCardNo.equals(CardNo)){
                                z="addnew";
                            }else if(!vTableNo.equals(TableNo) && !vCardNo.equals(CardNo)){
                                z="addnew";
                            }else{
                                z="cannot";
                            }
                        }
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

    private void hideKeyboard(View v){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
