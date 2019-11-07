package skybiz.com.posoffline.ui_CashReceipt.m_Reprint;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_NewObject.Encode;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.BytesUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothPrinter;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothZebra;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_IposAIDL.Utils.IposAidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_USB.PrintingUSB;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI.PrintingWifi;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogReprint extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svReprint;
    String CurCode,vPostGlobalTaxYN;
    Boolean isBT;
    String TypePrinter,NamePrinter,IPPrinter,
            Port, NewDoc, PaperSize;
    String IPAddress,DBStatus;
    EditText txtDateFrom,txtDateTo;
    Button btnRefresh;
    DatePickerDialog datePickerDialog;
    Bitmap bmpHeader;
    String imgHeader="";


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_reprint, container, false);
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
        /*svReprint=(SearchView)view.findViewById(R.id.svReprint);
        svReprint.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String Doc1No) {
                callSearch(Doc1No);
               // svCustomer.setQuery("", true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(),"CLICK, Search View", Toast.LENGTH_SHORT).show();
                return true;
            }
            public void callSearch(String Doc1No) {
                refresh(Doc1No);
            }
        });*/
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dated = sdf.format(date);
        txtDateFrom.setText(dated);
        txtDateTo.setText(dated);
        getDialog().setTitle("List of Reprint");
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
        DownloaderReprint downloaderReprint=new DownloaderReprint(getActivity(),DateFrom,DateTo,rv,DialogReprint.this);
        downloaderReprint.execute();
    }
    private void readyPrinter(){
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String query="select TypePrinter, NamePrinter, IPPrinter," +
                "Port, PaperSize from tb_settingprinter";
        Cursor rsPrint=db.getQuery(query);
        while(rsPrint.moveToNext()){
            TypePrinter = rsPrint.getString(0);
            NamePrinter = rsPrint.getString(1);
            IPPrinter   = rsPrint.getString(2);
            Port        = rsPrint.getString(3);
            PaperSize    = rsPrint.getString(4);
        }
        if(TypePrinter.equals("AIDL")){
            AidlUtil.getInstance().connectPrinterService(getActivity());
            AidlUtil.getInstance().initPrinter();
        }else if(TypePrinter.equals("Ipos AIDL")){
            //IposAidlUtil.getInstance().connectPrinterService(getActivity());
            //IposAidlUtil.getInstance().initPrinter();
        }else{

        }
        db.closeDB();
    }
    public class rePrint extends AsyncTask<Void,Void,String>{
        Context c;
        String Doc1No,z,Doc2No,Doc3No,D_ateTime;
        String IPAddress,DBStatus,ReceiptType,
                ReceiptHeader,UserName,Password,
                DBName,Port,URL,
                ItemConn,EncodeType;
        Double dServiceCharges=0.00;
        Connection conn=null;
        public rePrint(Context c, String doc1No) {
            this.c = c;
            Doc1No = doc1No;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.fnprint();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("error")){
                Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(c,"Reprint successful ", Toast.LENGTH_SHORT).show();
                //dismiss();
            }
        }


        private String fnprint(){
            try{
                JSONObject jsonReq,jsonRes;
                z="success";
                DBAdapter db=new DBAdapter(c);
                db.openDB();
                /*String querySet="select ServerName, DBStatus,ReceiptType from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress=curSet.getString(0);
                    DBStatus=curSet.getString(1);
                    ReceiptType=curSet.getString(2);
                }*/
                String querySet="select ServerName, UserName, Password," +
                        "DBName, Port, DBStatus," +
                        "ItemConn, EncodeType, ReceiptType" +
                        " from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress   = curSet.getString(0);
                    UserName    = curSet.getString(1);
                    Password    = curSet.getString(2);
                    DBName      = curSet.getString(3);
                    Port        = curSet.getString(4);
                    DBStatus    = curSet.getString(5);
                    ItemConn    = curSet.getString(6);
                    EncodeType    = curSet.getString(7);
                    ReceiptType = curSet.getString(8);
                }
                if(DBStatus.equals("1")){
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    conn = Connector.connect(URL, UserName, Password);
                   // conn= Connect_db.getConnection();
                }

                Cursor cur = db.getGeneralSetup();
                while (cur.moveToNext()) {
                    CurCode = cur.getString(1);
                    vPostGlobalTaxYN = cur.getString(6);
                }

                String qOtherSet="select ServiceCharges, ReceiptHeader from tb_othersetting";
                Cursor rsOther=db.getQuery(qOtherSet);
                while(rsOther.moveToNext()){
                    dServiceCharges =rsOther.getDouble(0);
                    ReceiptHeader   =rsOther.getString(1);
                }

                String last = "select Doc1No,Doc2No,Doc3No,D_ateTime from stk_cus_inv_hd where Doc1No='"+Doc1No+"' ";
                if(DBStatus.equals("2")) {
                    jsonReq = new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", last);
                    jsonReq.put("action", "select");
                    String response1 = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(response1);
                    String response = jsonRes.getString("hasil");
                    JSONArray rsData = new JSONArray(response);
                    JSONObject vData = null;
                    for (int i = 0; i < rsData.length(); i++) {
                        vData       = rsData.getJSONObject(i);
                        Doc2No      = vData.getString("Doc2No");
                        Doc3No      = vData.getString("Doc3No");
                        D_ateTime   = vData.getString("D_ateTime");
                    }
                }else if(DBStatus.equals("1")){
                    Statement stmtLast = conn.createStatement();
                    stmtLast.execute(last);
                    ResultSet rsLast = stmtLast.getResultSet();
                    while (rsLast.next()) {
                        Doc2No      = rsLast.getString(2);
                        Doc3No      = rsLast.getString(3);
                        D_ateTime   = rsLast.getString(4);
                    }
                }else if(DBStatus.equals("0")) {
                    Cursor rsLast = db.getQuery(last);
                    while (rsLast.moveToNext()) {
                        Doc2No      = rsLast.getString(1);
                        Doc3No      = rsLast.getString(2);
                        D_ateTime   = rsLast.getString(3);
                    }
                }

                String stringPrint="";
                if(PaperSize.equals("78mm") && !ReceiptType.equals("Format01")){
                    stringPrint=fngenerate78();
                }else if(PaperSize.equals("58mm") && !ReceiptType.equals("Format01")){
                    stringPrint=fngenerate58();
                }else if(PaperSize.equals("78mm") && ReceiptType.equals("Format01")){
                    stringPrint=receipt78();
                }else if(PaperSize.equals("58mm") && ReceiptType.equals("Format01")){
                    stringPrint= receipt58();
                }

                if (TypePrinter.equals("AIDL")) {
                    if(!imgHeader.isEmpty()){AidlUtil.getInstance().printBitmap(bmpHeader);}
                    AidlUtil.getInstance().printText(stringPrint);
                }else if(TypePrinter.equals("Ipos AIDL")){
                    IposAidlUtil.getInstance().setPrint(stringPrint);
                } else if (TypePrinter.equals("Bluetooth")) {
                    BluetoothPrinter fncheck = new BluetoothPrinter();
                    isBT = fncheck.fnBluetooth(getActivity(), NamePrinter, stringPrint);
                    if (isBT == false) {
                        NewDoc = "error print";
                        z="error";
                    }
                } else if (TypePrinter.equals("Bluetooth Zebra")) {
                    BluetoothZebra fncheck = new BluetoothZebra();
                    isBT = fncheck.fnBluetooth(getActivity(), NamePrinter, stringPrint);
                    if (isBT == false) {
                        NewDoc = "error print";
                        z="error";
                    }
                } else if (TypePrinter.equals("Wifi")) {
                    int iPort =Integer.parseInt(Port);
                    PrintingWifi fnprintw = new PrintingWifi();
                    isBT = fnprintw.fnprintwifi(getActivity(), IPPrinter, iPort, stringPrint);
                    if (isBT == false) {
                        NewDoc = "error print";
                        z="error";
                    }
                } else if (TypePrinter.equals("USB")) {
                    PrintingUSB fnprintu = new PrintingUSB();
                    isBT = fnprintu.fnprintusb(getActivity(), stringPrint);
                    if (isBT == false) {
                        NewDoc = "error print";
                        z="error";
                    }
                } else {
                    //No Printer
                }
                db.closeDB();
                return z;
            }catch (SQLiteException e) {
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return z;
        }

        private String receipt58(){
            String strPrint="";
            try{
                String vLine1="";
                String vLine2="";
                String vLine3="";
                String vLine4="";
                String vLine5="";
                String vLine5_1="";
                String vLine6="";
                String vLine7="";
                String vLine7_1 ="";
                String vLine7_2 ="";
                String vLine8   ="";
                String vLine8_1 ="";
                String vLine8_2 ="";
                String vLine9   ="";
                String vLine9_1 ="";
                String vLine9_2 ="";
                String vLine10   ="";
                String vLine10_1 ="";
                String vLine11 ="";
                String vLine12 ="";

                DBAdapter db=new DBAdapter(c);
                db.openDB();
                vLine1 = str_pad("RESIT RESMI", 32, " ", "STR_PAD_BOTH");
                vLine2 = str_pad("No: "+Doc1No, 32, " ", "STR_PAD_BOTH");

                String qCom="select CurCode,CompanyName,CompanyCode," +
                        "GSTNo,Address,Tel1," +
                        "Fax1,CompanyEmail,ComTown," +
                        "ComState,ComCountry, IFNULL(PhotoFile,'')as PhotoFile FROM companysetup ";
                Cursor rsCom=db.getQuery(qCom);
                String vAddress = "";
                while(rsCom.moveToNext()){
                    vLine3 = str_pad(rsCom.getString(1), 32, " ", "STR_PAD_BOTH");
                    vLine4 = str_pad(rsCom.getString(5), 32, " ", "STR_PAD_BOTH");
                    String Address=rsCom.getString(4);
                    String AddressAll = Address.replaceAll("(.{32})", "$1\n");
                    String[] address = AddressAll.split("\n");
                    for (String add : address) {
                        vAddress += str_pad(add, 32, " ", "STR_PAD_BOTH");
                    }

                }
                vLine5 = str_pad("SELAMAT DATANG", 32, " ", "STR_PAD_BOTH");
                vLine5_1 = str_pad("Welcome", 32, " ", "STR_PAD_BOTH");
                vLine6 = str_pad("(Taxi Svcs. Surcharge)", 32, " ", "STR_PAD_BOTH");
                vLine7 = str_pad("DATE", 13, " ", "STR_PAD_RIGHT");
                vLine7_1 = str_pad(":", 2, " ", "STR_PAD_RIGHT");
                vLine7_2 = str_pad(D_ateTime.substring(0,10), 17, " ", "STR_PAD_RIGHT");
                vLine8 = str_pad("TAXI NO", 13, " ", "STR_PAD_RIGHT");
                vLine8_1 = str_pad(":", 2, " ", "STR_PAD_RIGHT");
                vLine8_2 = str_pad(Doc2No, 17, " ", "STR_PAD_RIGHT");
                vLine9 = str_pad("DESTINATION", 13, " ", "STR_PAD_RIGHT");
                vLine9_1 = str_pad(":", 2, " ", "STR_PAD_RIGHT");
                vLine9_2 = str_pad(Doc3No, 17, " ", "STR_PAD_RIGHT");

                strPrint +=vLine1+"\n";
                strPrint +=vLine2+"\n";
                strPrint +=vLine3+"\n";
                strPrint +=vLine4+"\n";
                strPrint +=vAddress+"\n";
                strPrint += "________________________________\n";
                strPrint +=vLine5+"\n";
                strPrint +=vLine5_1+"\n";
                strPrint +=vLine6+"\n";
                strPrint += "________________________________\n";
                strPrint +=vLine7+vLine7_1+vLine7_2+"\n";
                strPrint +=vLine8+vLine8_1+vLine8_2+"\n";
                strPrint +=vLine9+vLine9_1+vLine9_2+"\n\n";
                String qDt="select SUBSTR(Description,1,20)as Description,HCUnitCost from stk_cus_inv_dt where Doc1No='"+Doc1No+"'";

                if(DBStatus.equals("0")) {
                    Cursor rsDt = db.getQuery(qDt);
                    while (rsDt.moveToNext()) {
                        Double dHCUnitCost  = rsDt.getDouble(1);
                        String HCUnitCost   = String.format(Locale.US, "%,.2f", dHCUnitCost);
                        String Description  = rsDt.getString(0);
                        vLine10             = str_pad(Description, 20, " ", "STR_PAD_RIGHT");
                        vLine10_1           = str_pad(HCUnitCost, 12, " ", "STR_PAD_LEFT");
                        strPrint            += vLine10 + vLine10_1 + "\n";
                    }
                }else if(DBStatus.equals("1")){
                    Statement stmtDt=conn.createStatement();
                    stmtDt.execute(qDt);
                    ResultSet rsDt=stmtDt.getResultSet();
                    while(rsDt.next()){
                        Double dHCUnitCost  = rsDt.getDouble(2);
                        String HCUnitCost   = String.format(Locale.US, "%,.2f", dHCUnitCost);
                        String Description  = rsDt.getString(1);
                        vLine10             = str_pad(Description, 20, " ", "STR_PAD_RIGHT");
                        vLine10_1           = str_pad(HCUnitCost, 12, " ", "STR_PAD_LEFT");
                        strPrint            += vLine10 + vLine10_1 + "\n";
                    }
                }
                vLine11 = str_pad("Disahkan Oleh", 20, " ", "STR_PAD_BOTH");
                vLine12 = str_pad("...............", 20, " ", "STR_PAD_BOTH");
                strPrint += "________________________________\n\n";
                strPrint +=vLine11+"\n\n\n\n";
                strPrint +=vLine12+"\n\n\n\n";
                db.closeDB();
                return strPrint;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return strPrint;
        }

        private String receipt78(){
            String strPrint="";
            try{
                String vLine1="";
                String vLine2="";
                String vLine3="";
                String vLine4="";
                String vLine5="";
                String vLine5_1="";
                String vLine6="";
                String vLine7="";
                String vLine7_1 ="";
                String vLine7_2 ="";
                String vLine8   ="";
                String vLine8_1 ="";
                String vLine8_2 ="";
                String vLine9   ="";
                String vLine9_1 ="";
                String vLine9_2 ="";
                String vLine10   ="";
                String vLine10_1 ="";
                String vLine11 ="";
                String vLine12 ="";



                DBAdapter db=new DBAdapter(c);
                db.openDB();
                vLine1 = str_pad("RESIT RESMI", 48, " ", "STR_PAD_BOTH");
                vLine2 = str_pad("No: "+Doc1No, 48, " ", "STR_PAD_BOTH");

                String qCom="select CurCode,CompanyName,CompanyCode," +
                        "GSTNo,Address,Tel1," +
                        "Fax1,CompanyEmail,ComTown," +
                        "ComState,ComCountry,IFNULL(PhotoFile,'')as PhotoFile FROM companysetup ";
                Cursor rsCom=db.getQuery(qCom);
                String vAddress = "";
                while(rsCom.moveToNext()){
                    vLine3 = str_pad(rsCom.getString(1), 48, " ", "STR_PAD_BOTH");
                    vLine4 = str_pad(rsCom.getString(5), 48, " ", "STR_PAD_BOTH");
                    String Address=rsCom.getString(4);
                    String AddressAll = Address.replaceAll("(.{48})", "$1\n");
                    String[] address = AddressAll.split("\n");
                    for (String add : address) {
                        vAddress += str_pad(add, 48, " ", "STR_PAD_BOTH");
                    }
                    imgHeader           = rsCom.getString(11);
                    byte[] decString    = Base64.decode(imgHeader, Base64.DEFAULT);
                    bmpHeader           = BitmapFactory.decodeByteArray(decString,0,decString.length);
                }
                vLine5 = str_pad("SELAMAT DATANG", 48, " ", "STR_PAD_BOTH");
                vLine5_1 = str_pad("Welcome", 48, " ", "STR_PAD_BOTH");
                vLine6 = str_pad("(Taxi Svcs. Surcharge)", 48, " ", "STR_PAD_BOTH");
                vLine7 = str_pad("DATE", 18, " ", "STR_PAD_RIGHT");
                vLine7_1 = str_pad(":", 2, " ", "STR_PAD_RIGHT");
                vLine7_2 = str_pad(D_ateTime.substring(0,10), 28, " ", "STR_PAD_RIGHT");
                vLine8 = str_pad("TAXI NO", 18, " ", "STR_PAD_RIGHT");
                vLine8_1 = str_pad(":", 2, " ", "STR_PAD_RIGHT");
                vLine8_2 = str_pad(Doc2No, 28, " ", "STR_PAD_RIGHT");
                vLine9 = str_pad("DESTINATION", 18, " ", "STR_PAD_RIGHT");
                vLine9_1 = str_pad(":", 2, " ", "STR_PAD_RIGHT");
                vLine9_2 = str_pad(Doc3No, 28, " ", "STR_PAD_RIGHT");

                strPrint +=vLine1+"\n";
                strPrint +=vLine2+"\n";
                strPrint +=vLine3+"\n";
                strPrint +=vLine4+"\n";
                strPrint +=vAddress+"\n";
                strPrint += "________________________________________________\n";
                strPrint +=vLine5+"\n";
                strPrint +=vLine5_1+"\n";
                strPrint +=vLine6+"\n";
                strPrint += "________________________________________________\n";
                strPrint +=vLine7+vLine7_1+vLine7_2+"\n";
                strPrint +=vLine8+vLine8_1+vLine8_2+"\n";
                strPrint +=vLine9+vLine9_1+vLine9_2+"\n\n";
                String qDt="select SUBSTR(Description,1,20)as Description, HCUnitCost from stk_cus_inv_dt where Doc1No='"+Doc1No+"'";
                if(DBStatus.equals("0")) {
                    Cursor rsDt = db.getQuery(qDt);
                    while (rsDt.moveToNext()) {
                        Double dHCUnitCost = rsDt.getDouble(1);
                        String HCUnitCost = String.format(Locale.US, "%,.2f", dHCUnitCost);
                        String Description = rsDt.getString(0);
                        vLine10 = str_pad(Description, 34, " ", "STR_PAD_RIGHT");
                        vLine10_1 = str_pad(HCUnitCost, 14, " ", "STR_PAD_LEFT");
                        strPrint += vLine10 + vLine10_1 + "\n";
                    }
                }else if(DBStatus.equals("1")){
                    Statement stmtDt=conn.createStatement();
                    stmtDt.execute(qDt);
                    ResultSet rsDt=stmtDt.getResultSet();
                    while(rsDt.next()){
                        Double dHCUnitCost  = rsDt.getDouble(2);
                        String HCUnitCost   = String.format(Locale.US, "%,.2f", dHCUnitCost);
                        String Description  = rsDt.getString(1);
                        vLine10             = str_pad(Description, 34, " ", "STR_PAD_RIGHT");
                        vLine10_1           = str_pad(HCUnitCost, 14, " ", "STR_PAD_LEFT");
                        strPrint            += vLine10 + vLine10_1 + "\n";
                    }
                }
                vLine11 = str_pad("Disahkan Oleh", 25, " ", "STR_PAD_BOTH");
                vLine12 = str_pad("...............", 25, " ", "STR_PAD_BOTH");
                strPrint += "________________________________________________\n\n";
                strPrint +=vLine11+"\n\n\n\n";
                strPrint +=vLine12+"\n\n\n\n";
                //strPrint;
                db.closeDB();
                return strPrint;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return strPrint;
        }

        private String fngenerate58() {
            String vLine1, vLine2, vLine3, vLine4, vLine4_1, vLine5, vLine6, vLine7, vLine8, vLine9, vLine10, vLine11, vLine12, vLine13, vLine14, vLine15, vLine16, vLine17;
            String vLine18, vLine19, vLine20, vLine21, vLine22, vLine23, vLine24, vLine25, vLine4_2, vLine4_3, vLine4_4;
            vLine1 = "";
            vLine2 = "";
            vLine3 = "";
            vLine4 = "";
            vLine4_1 = "";
            vLine4_2 = "";
            vLine4_3 = "";
            vLine4_4 = "";
            vLine5 = "";
            vLine6 = "";
            vLine7 = "";
            vLine8 = "";
            vLine9 = "";
            vLine10 = "";
            vLine11 = "";
            vLine12 = "";
            vLine13 = "";
            vLine14 = "";
            vLine15 = "";
            vLine16 = "";
            vLine17 = "";
            vLine18 = "";
            vLine19 = "";
            vLine20 = "";
            vLine21 = "";
            vLine22 = "";
            vLine23 = "";
            vLine24 = "";
            vLine25 = "";

            String strPrint = "";
            String CompanyName = "";
            String CompanyCode = "";
            String GSTNo = "";
            String Address = "";
            String ComTown = "";
            String ComState = "";
            String ComCountry = "";
            String Tel = "";
            String Fax = "";
            String CusName = "";
            String CusCode = "";
            String TelCus = "";
            String AdddresCus = "";
            String SalesPersonCode = "";
           // String D_ateTime = "";
            String Disc2 = "";
            String Minus2 = "";
            String vCC1No = "";
            String vLine11_1="";
            String datedTime="";
            String TableNo="";
            String VoidYN="";
            String vLine71="";
            String vFooter="";
            try {
                JSONObject jsonReq,jsonRes;
                DBAdapter db = new DBAdapter(c);
                db.openDB();


                String Company = "select CurCode, CompanyName, CompanyCode," +
                        "GSTNo, Address, Tel1," +
                        "Fax1, CompanyEmail, ComTown," +
                        "ComState, ComCountry,  IFNULL(Footer_CR,'')as Footer_CR," +
                        "IFNULL(PhotoFile,'')as PhotoFile " +
                        " FROM companysetup ";
                if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", Company);
                    jsonReq.put("action", "select");
                    String rsCom = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsCom);
                    String rsHeader = jsonRes.getString("hasil");
                    JSONArray rsData = new JSONArray(rsHeader);
                    JSONObject vData = null;
                    for (int i = 0; i < rsData.length(); i++) {
                        vData = rsData.getJSONObject(i);
                        CompanyName = vData.getString("CompanyName");
                        CompanyCode = vData.getString("CompanyCode");
                        GSTNo       = vData.getString("GSTNo");
                        Address     =vData.getString("Address");
                        Tel         = vData.getString("Tel1");
                        Fax         = vData.getString("Fax1");
                        vFooter     = String.valueOf(Html.fromHtml(vData.getString("Footer_CR")));

                    }
                }else {
                    Cursor rsCom = db.getQuery(Company);
                    while (rsCom.moveToNext()) {
                        CompanyName = rsCom.getString(1);
                        CompanyCode = rsCom.getString(2);
                        GSTNo = rsCom.getString(3);
                        Address =  rsCom.getString(4);
                        Tel = rsCom.getString(5);
                        Fax = rsCom.getString(6);
                        ComTown = rsCom.getString(8);
                        ComState = rsCom.getString(9);
                        ComCountry = rsCom.getString(10);
                        vFooter = String.valueOf(Html.fromHtml(rsCom.getString(11)));
                        imgHeader           = rsCom.getString(12);
                        byte[] decString    = Base64.decode(imgHeader, Base64.DEFAULT);
                        bmpHeader           = BitmapFactory.decodeByteArray(decString,0,decString.length);
                    }
                }

                vLine1 = str_pad(CompanyName, 32, " ", "STR_PAD_BOTH");
                vLine2 = str_pad("Co.Reg.No.: " + CompanyCode, 32, " ", "STR_PAD_BOTH");
                vLine3 = str_pad("GST Reg.No.: " + GSTNo, 32, " ", "STR_PAD_BOTH");
                String AddressAll = Address.replaceAll("(.{32})", "$1\n");
                String[] address = AddressAll.split("\n");
                String vAddress = "";
                for (String add : address) {
                    vAddress += str_pad(add, 32, " ", "STR_PAD_BOTH");
                }
                vLine4 = vAddress;

                String FooterAll = vFooter.replaceAll("(.{31})", "$1\n");
                String [] footer=FooterAll.split("\n");
                String Footer="";
                for (String add: footer) {
                    Footer +=str_pad(add, 31, " ", "STR_PAD_BOTH");
                }

                vLine5 = str_pad(Tel, 32, " ", "STR_PAD_BOTH");
                vLine6 = str_pad(Fax, 32, " ", "STR_PAD_BOTH");
                if (!GSTNo.equals("NO")) {
                    vLine7 = str_pad("TAX INVOICE (REPRINT)", 32, " ", "STR_PAD_BOTH");
                } else {
                    vLine71=str_pad("VOID BILL", 32, " ", "STR_PAD_BOTH");
                    vLine7 = str_pad(ReceiptHeader+" (REPRINT)", 32, " ", "STR_PAD_BOTH");
                }
                //end query header
                vLine9 = str_pad("Bill #  : " + Doc1No, 32, " ", "STR_PAD_RIGHT");


                String qCustomer="select H.D_ateTime, D.AnalysisCode2, H.CusCode, C.CusName, C.Address," +
                        " H.Status2,D.SalesPersonCode from stk_cus_inv_hd H" +
                        " inner join stk_cus_inv_dt D on H.Doc1No=D.Doc1No inner join customer C ON H.CusCode=C.CusCode " +
                        " where H.Doc1No='"+Doc1No+"' Group By H.Doc1No  ";
                /*String qCustomer = "select H.CusCode,C.CusName,C.Address " +
                        " from stk_cus_inv_hd H inner join customer C" +
                        " on H.CusCode=C.CusCode where H.Doc1No='" + Doc1No + "'";*/
                if(DBStatus.equals("2")) {
                    jsonReq = new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qCustomer);
                    jsonReq.put("action", "select");
                    String rsCus = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsCus);
                    String resCus = jsonRes.getString("hasil");
                    JSONArray jaCus = new JSONArray(resCus);
                    JSONObject vCus = null;
                    for (int i = 0; i < jaCus.length(); i++) {
                        vCus = jaCus.getJSONObject(i);
                        CusCode = vCus.getString("CusCode");
                        CusName = vCus.getString("CusName");
                        AdddresCus = vCus.getString("Address");
                        datedTime = vCus.getString("D_ateTime");
                        TableNo = vCus.getString("AnalysisCode2");
                        VoidYN = vCus.getString("Status2");
                        SalesPersonCode = vCus.getString("SalesPersonCode");
                    }
                }else if(DBStatus.equals("1")){
                    Statement stmtCus = conn.createStatement();
                    stmtCus.execute(qCustomer);
                    ResultSet rsCus = stmtCus.getResultSet();
                    while (rsCus.next()) {
                        datedTime       = rsCus.getString(1);
                        TableNo         = rsCus.getString(2);
                        CusCode         = rsCus.getString(3);
                        CusName         = rsCus.getString(4);
                        AdddresCus      = rsCus.getString(5);
                        VoidYN          = rsCus.getString(6);
                        SalesPersonCode = rsCus.getString(7);
                    }
                }else if(DBStatus.equals("0")) {
                    Cursor rsCus = db.getQuery(qCustomer);
                    while (rsCus.moveToNext()) {
                        datedTime   = rsCus.getString(0);
                        TableNo     = rsCus.getString(1);
                        CusCode     = rsCus.getString(2);
                        CusName     = rsCus.getString(3);
                        AdddresCus  = rsCus.getString(4);
                        VoidYN     = rsCus.getString(5);
                        SalesPersonCode     = rsCus.getString(6);
                    }
                }
                vLine11 = str_pad(D_ateTime, 18, " ", "STR_PAD_RIGHT");
                if (TableNo.equals("0") || TableNo.isEmpty()) {
                    vLine11_1 = str_pad("", 13, " ", "STR_PAD_RIGHT");
                } else {
                    vLine11_1 = str_pad("Table #: " + TableNo, 13, " ", "STR_PAD_RIGHT");
                }
                vLine12 = str_pad("TO: " + CusName, 32, " ", "STR_PAD_RIGHT");
                String vLine12_1 = str_pad("Doc2No: " + Doc2No, 32, " ", "STR_PAD_RIGHT");
                String vLine12_2 = str_pad("Salesman: "+SalesPersonCode, 32, " ", "STR_PAD_RIGHT");
                vLine13 = str_pad(AdddresCus, 32, " ", "STR_PAD_RIGHT");
                String vLine131 = str_pad("Qty", 7, " ", "STR_PAD_RIGHT");
                String vLine132 = str_pad("Price", 11, " ", "STR_PAD_BOTH");
                String vLine133 = str_pad("Amount (" + CurCode + ")", 14, " ", "STR_PAD_LEFT");

                //end query customer
                strPrint += vLine1 + "\n";
                if(!CompanyCode.isEmpty()) {
                    strPrint += vLine2 + "\n";
                }

                if(!GSTNo.equals("NO")) {
                    strPrint += vLine3 + "\n";
                }
                strPrint += vLine4 + "\n";
                strPrint += "________________________________\n";
                if(VoidYN.equals("Void") && GSTNo.equals("NO")){
                    strPrint += vLine71 + "\n";
                }
                strPrint += vLine7 + "\n";
                strPrint += "________________________________\n";
                //end header

                //customer
                strPrint += vLine9 + "\n";
                strPrint += vLine11 + vLine11_1 + "\n";
                strPrint += vLine12 + "\n";
                strPrint += vLine12_2;
                if(!Doc2No.isEmpty()) {
                    strPrint += vLine12_1;
                }
                if (!CusCode.equals("999999")) {
                   // strPrint += vLine13 + "\n";
                }
                strPrint += "________________________________\n";
                strPrint += vLine131 + vLine132 + vLine133 + "\n";
                strPrint += "________________________________\n";
                //end customer

                //detail
                String qDetail = "select D.ItemCode, IFNULL(D.Qty,0) as Qty, D.UOM, " +
                        " D.DetailTaxCode, SUBSTR(D.Description,1,45) as Description, IFNULL(D.HCUnitCost,0) as HCUnitCost, " +
                        " IFNULL(D.HCLineAmt,0) AS HCLineAmt, IFNULL(D.DisRate1,0) as DisRate1, IFNULL(D.HCDiscount,0) as HCDiscount," +
                        " M.AlternateItem " +
                        " from stk_cus_inv_dt D inner join stk_master M on D.ItemCode=M.ItemCode" +
                        " Where D.Doc1No='" + Doc1No + "'  ";
                if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qDetail);
                    jsonReq.put("action", "select");
                    String reqDet = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(reqDet);
                    String resDet= jsonRes.getString("hasil");
                    Log.d("JSONDET",resDet);
                    JSONArray jaDt = new JSONArray(resDet);
                    JSONObject vDet = null;
                    for (int i = 0; i < jaDt.length(); i++) {
                        vDet = jaDt.getJSONObject(i);
                        String ItemCode         = vDet.getString("ItemCode");
                        Double dQty             = vDet.getDouble("Qty");
                        String Qty              = String.format(Locale.US, "%,.2f", dQty);
                        String UOM              = vDet.getString("UOM").trim().replaceAll("\\s+", " ");
                        String DetailTaxCode    = vDet.getString("DetailTaxCode");
                        String Description      = vDet.getString("Description").trim().replaceAll("\\s+", " ");
                        Double dHCUnitCost      = vDet.getDouble("HCUnitCost");
                        String HCUnitCost       = String.format(Locale.US, "%,.2f", dHCUnitCost);
                        Double dHCLineAmt       = vDet.getDouble("HCLineAmt");
                        String HCLineAmt        = String.format(Locale.US, "%,.2f", dHCLineAmt);
                        Double dDisRate1        = vDet.getDouble("DisRate1");
                        String DisRate1         = String.format(Locale.US, "%,.2f", dDisRate1);
                        Double dHCDiscount      = vDet.getDouble("HCDiscount");
                        String HCDiscount       = String.format(Locale.US, "%,.2f", dHCDiscount);
                        vLine14                 = str_pad(Description, 26, " ", "STR_PAD_RIGHT");
                        String AlternateItem     = vDet.getString("AlternateItem").trim().replaceAll("\\s+", " ");
                        String vLine14_1        = str_pad(UOM, 6, " ", "STR_PAD_RIGHT");
                        String vLine14_2        = str_pad("", 6, " ", "STR_PAD_RIGHT");
                        String vLine14_3        = str_pad(AlternateItem, 20, " ", "STR_PAD_RIGHT");
                        String vLine15_1        = str_pad(Qty, 4, " ", "STR_PAD_LEFT");
                        String vLine15_2        = str_pad(HCUnitCost, 12, " ", "STR_PAD_LEFT");
                        String vLine15_4        = str_pad(HCLineAmt, 13, " ", "STR_PAD_LEFT");
                        strPrint                += vLine14+vLine14_1 + "\n";
                        if(AlternateItem.length()>1){
                            strPrint            += vLine14_3+vLine14_2+"\n";
                        }
                        if(HCUnitCost.length()>12 || HCLineAmt.length()>12) {
                            strPrint                += vLine15_1 + " x" + str_pad(HCUnitCost, 25, " ", "STR_PAD_LEFT") + "\n" ;
                            strPrint                += str_pad(HCLineAmt, 31, " ", "STR_PAD_LEFT") + "\n";

                        }else{
                            strPrint                += vLine15_1 + " x" + vLine15_2 + vLine15_4 + "\n";
                        }
                       // strPrint                += vLine15_1 + " x" + vLine15_2 + vLine15_4 + "\n";
                        if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                            Disc2 = "(" + HCDiscount + ")";
                            String vLine15_5 = str_pad(Disc2, 18, " ", "STR_PAD_LEFT");
                            String vLine15_6 = str_pad(" ", 13, " ", "STR_PAD_LEFT");
                            strPrint += vLine15_5 + vLine15_6 + "\n";
                        }
                    }
                }else if(DBStatus.equals("1")){
                    Statement stmtDet = conn.createStatement();
                    stmtDet.execute(qDetail);
                    ResultSet rsDt = stmtDet.getResultSet();
                    while (rsDt.next()) {
                        String ItemCode         = rsDt.getString(1);
                        Double dQty             = rsDt.getDouble(2);
                        String Qty              = String.format(Locale.US, "%,.2f", dQty);
                        String UOM              = Encode.setChar(EncodeType,rsDt.getString(3).trim().replaceAll("\\s+", " "));
                        String DetailTaxCode    = rsDt.getString(4);
                        String Description      = Encode.setChar(EncodeType,rsDt.getString(5).trim().replaceAll("\\s+", " "));
                        Double dHCUnitCost      = rsDt.getDouble(6);
                        String HCUnitCost       = String.format(Locale.US, "%,.2f", dHCUnitCost);
                        Double dHCLineAmt       = rsDt.getDouble(7);
                        String HCLineAmt        = String.format(Locale.US, "%,.2f", dHCLineAmt);
                        Double dDisRate1        = rsDt.getDouble(8);
                        String DisRate1         = String.format(Locale.US, "%,.2f", dDisRate1);
                        Double dHCDiscount      = rsDt.getDouble(9);
                        String HCDiscount       = String.format(Locale.US, "%,.2f", dHCDiscount);
                        String AlternateItem    = Encode.setChar(EncodeType,rsDt.getString(10).trim().replaceAll("\\s+", " "));
                        vLine14                 = str_pad(Description, 26, " ", "STR_PAD_RIGHT");
                        String vLine14_1        = str_pad(UOM, 6, " ", "STR_PAD_RIGHT");
                        String vLine14_2        = str_pad("", 6, " ", "STR_PAD_RIGHT");
                        String vLine14_3        = str_pad(AlternateItem, 20, " ", "STR_PAD_RIGHT");
                        String vLine15_1        = str_pad(Qty, 4, " ", "STR_PAD_LEFT");
                        String vLine15_2        = str_pad(HCUnitCost, 12, " ", "STR_PAD_LEFT");
                        String vLine15_4        = str_pad(HCLineAmt, 13, " ", "STR_PAD_LEFT");
                        strPrint += vLine14+vLine14_1 + "\n";
                        if(AlternateItem.length()>1){
                            strPrint            += vLine14_3+vLine14_2+"\n";
                        }
                        if(HCUnitCost.length()>12 || HCLineAmt.length()>12) {
                            strPrint                += vLine15_1 + " x" + str_pad(HCUnitCost, 25, " ", "STR_PAD_LEFT") + "\n" ;
                            strPrint                += str_pad(HCLineAmt, 31, " ", "STR_PAD_LEFT") + "\n";

                        }else{
                            strPrint                += vLine15_1 + " x" + vLine15_2 + vLine15_4 + "\n";
                        }
                        //strPrint += vLine15_1 + " x" + vLine15_2 + vLine15_4 + "\n";
                        if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                            Disc2 = "(" + HCDiscount + ")";
                            String vLine15_5 = str_pad(Disc2, 18, " ", "STR_PAD_LEFT");
                            String vLine15_6 = str_pad(" ", 13, " ", "STR_PAD_LEFT");
                            strPrint += vLine15_5 + vLine15_6 + "\n";
                        }
                    }
                }else if(DBStatus.equals("0")) {
                    Cursor rsDt = db.getQuery(qDetail);
                    while (rsDt.moveToNext()) {
                        String ItemCode         = rsDt.getString(0);
                        Double dQty             = rsDt.getDouble(1);
                        String Qty              = String.format(Locale.US, "%,.2f", dQty);
                        String UOM              = rsDt.getString(2).trim().replaceAll("\\s+", " ");
                        String DetailTaxCode    = rsDt.getString(3);
                        String Description      = rsDt.getString(4).trim().replaceAll("\\s+", " ");
                        Double dHCUnitCost      = rsDt.getDouble(5);
                        String HCUnitCost       = String.format(Locale.US, "%,.2f", dHCUnitCost);
                        Double dHCLineAmt       = rsDt.getDouble(6);
                        String HCLineAmt        = String.format(Locale.US, "%,.2f", dHCLineAmt);
                        Double dDisRate1        = rsDt.getDouble(7);
                        String DisRate1         = String.format(Locale.US, "%,.2f", dDisRate1);
                        Double dHCDiscount      = rsDt.getDouble(8);
                        String HCDiscount       = String.format(Locale.US, "%,.2f", dHCDiscount);
                        String AlternateItem    = rsDt.getString(9).trim().replaceAll("\\s+", " ");
                        vLine14                 = str_pad(Description, 26, " ", "STR_PAD_RIGHT");
                        String vLine14_1        = str_pad(UOM, 6, " ", "STR_PAD_RIGHT");
                        String vLine14_2        = str_pad("", 6, " ", "STR_PAD_RIGHT");
                        String vLine14_3        = str_pad(AlternateItem, 20, " ", "STR_PAD_RIGHT");
                        String vLine15_1        = str_pad(Qty, 4, " ", "STR_PAD_LEFT");
                        String vLine15_2        = str_pad(HCUnitCost, 12, " ", "STR_PAD_LEFT");
                        String vLine15_4        = str_pad(HCLineAmt, 13, " ", "STR_PAD_LEFT");
                        strPrint += vLine14+vLine14_1 + "\n";
                        if(AlternateItem.length()>1){
                            strPrint            += vLine14_3+vLine14_2+"\n";
                        }
                        if(HCUnitCost.length()>12 || HCLineAmt.length()>12) {
                            strPrint                += vLine15_1 + " x" + str_pad(HCUnitCost, 25, " ", "STR_PAD_LEFT") + "\n" ;
                            strPrint                += str_pad(HCLineAmt, 31, " ", "STR_PAD_LEFT") + "\n";

                        }else{
                            strPrint                += vLine15_1 + " x" + vLine15_2 + vLine15_4 + "\n";
                        }
                        //strPrint += vLine15_1 + " x" + vLine15_2 + vLine15_4 + "\n";
                        if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                            Disc2 = "(" + HCDiscount + ")";
                            String vLine15_5 = str_pad(Disc2, 18, " ", "STR_PAD_LEFT");
                            String vLine15_6 = str_pad(" ", 13, " ", "STR_PAD_LEFT");
                            strPrint += vLine15_5 + vLine15_6 + "\n";
                        }
                    }
                }
                //end detail

                //start Total
                String qTotal = "SELECT IFNULL(H.HCNetAmt,0)AS HCNetAmt, IFNULL(H.HCDtTax,0) AS HCDtTax, "+
                        " IFNULL(R.CashAmt + R.ChangeAmt,0) AS CashAmt,  IFNULL(R.ChangeAmt,0) AS ChangeAmt, "+
                        " IFNULL(SUM(D.HCLineAmt)-SUM(D.HCTax),0) AS AmtExTax,IFNULL(R.BalanceAmount,0) AS BalanceAmount, "+
                        " IFNULL(R.CC1Amt,0) AS CC1Amt, IFNULL(R.CC1No,'') AS CC1No,  " +
                        " IFNULL(R.CC1Code,'')AS CC1Code, IFNULL(SUM(D.Qty),0) AS ItemTender," +
                        " IFNULL(H.AdjAmt,0)as AdjAmt "+
                        " FROM stk_cus_inv_hd H LEFT JOIN stk_receipt2 R ON R.Doc1No=H.Doc1No INNER JOIN stk_cus_inv_dt D ON D.Doc1No=H.Doc1No "+
                        " where H.Doc1No='" + Doc1No + "' and D.ItemCode<>'M999999SC' Group By H.Doc1No ";
                Double dPayAmt=0.00;
                Double dCC1Amt=0.00;
                Double dBalAmt=0.00;
                String tCC1Code="";
                String vLine22_2="";
                String vLine22_3="";
                String vLine22_4="";
                String AmtExtTax="",TaxAmt="",AdjAmt="",TotAmt="",PayAmt="",ChAmt="",BalAmt="",ItemTender="",CC1Amt="",CC1Code="";
                if(DBStatus.equals("2")) {
                    jsonReq = new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qTotal);
                    jsonReq.put("action", "select");
                    String rsTot = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsTot);
                    String resTot = jsonRes.getString("hasil");
                    JSONArray jaTot = new JSONArray(resTot);
                    JSONObject vTot = null;
                    for (int i = 0; i < jaTot.length(); i++) {
                        vTot                = jaTot.getJSONObject(i);
                        Double dTotAmt      = vTot.getDouble("HCNetAmt");
                        TotAmt              = String.format(Locale.US, "%,.2f", dTotAmt);
                        Double dTaxAmt      = vTot.getDouble("HCDtTax");
                        TaxAmt              = String.format(Locale.US, "%,.2f", dTaxAmt);
                        dPayAmt             = vTot.getDouble("CashAmt");
                        PayAmt              = String.format(Locale.US, "%,.2f", dPayAmt);
                        Double dChAmt       = vTot.getDouble("ChangeAmt");
                        ChAmt               = String.format(Locale.US, "%,.2f", dChAmt);
                        Double dAmtExtTax   = vTot.getDouble("AmtExTax");
                        AmtExtTax           = String.format(Locale.US, "%,.2f", dAmtExtTax);
                        dBalAmt             = vTot.getDouble("BalanceAmount");
                        BalAmt              = String.format(Locale.US, "%,.2f", dBalAmt);
                        dCC1Amt             = vTot.getDouble("CC1Amt");
                        CC1Amt              = String.format(Locale.US, "%,.2f", dCC1Amt);
                        if (dCC1Amt > 0) {
                            String tCC1No = vTot.getString("CC1No");
                            vCC1No = tCC1No.substring(tCC1No.length() - 4);
                        }
                        CC1Code             = vTot.getString("CC1Code");
                        Double dItemTender  = vTot.getDouble("ItemTender");
                        ItemTender          = String.format(Locale.US, "%,.2f", dItemTender);
                        AdjAmt              = String.format(Locale.US, "%,.2f", vTot.getDouble("AdjAmt"));
                        /*vLine16 = str_pad(AmtExtTax, 11, " ", "STR_PAD_LEFT");
                        vLine17 = str_pad(TaxAmt, 11, " ", "STR_PAD_LEFT");
                        vLine18 = str_pad(AdjAmt, 11, " ", "STR_PAD_LEFT");
                        vLine19 = str_pad(TotAmt, 11, " ", "STR_PAD_LEFT");
                        vLine20 = str_pad(PayAmt, 11, " ", "STR_PAD_LEFT");
                        vLine21 = str_pad(ChAmt, 11, " ", "STR_PAD_LEFT");
                        vLine22 = str_pad(BalAmt, 11, " ", "STR_PAD_LEFT");
                        vLine22_2 = str_pad(ItemTender, 11, " ", "STR_PAD_LEFT");
                        vLine22_3 = str_pad(CC1Amt, 11, " ", "STR_PAD_LEFT");
                        vLine22_4 = str_pad("xxxxx" + vCC1No, 11, " ", "STR_PAD_LEFT");
                        tCC1Code = str_pad(CC1Code, 19, " ", "STR_PAD_RIGHT");*/
                    }
                }else if(DBStatus.equals("1")){
                    Statement stmtTot = conn.createStatement();
                    stmtTot.execute(qTotal);
                    ResultSet rsTot = stmtTot.getResultSet();
                    while (rsTot.next()) {
                        Double dTotAmt      = rsTot.getDouble(1);
                        TotAmt              = String.format(Locale.US, "%,.2f", dTotAmt);
                        Double dTaxAmt      = rsTot.getDouble(2);
                        TaxAmt              = String.format(Locale.US, "%,.2f", dTaxAmt);
                        dPayAmt             = rsTot.getDouble(3);
                        PayAmt              = String.format(Locale.US, "%,.2f", dPayAmt);
                        Double dChAmt       = rsTot.getDouble(4);
                        ChAmt               = String.format(Locale.US, "%,.2f", dChAmt);
                        Double dAmtExtTax   = rsTot.getDouble(5);
                        AmtExtTax           = String.format(Locale.US, "%,.2f", dAmtExtTax);
                        dBalAmt             = rsTot.getDouble(6);
                        BalAmt              = String.format(Locale.US, "%,.2f", dBalAmt);
                        dCC1Amt             = rsTot.getDouble(7);
                        CC1Amt              = String.format(Locale.US, "%,.2f", dCC1Amt);
                        if (dCC1Amt > 0) {
                            String tCC1No   = rsTot.getString(8);
                            if (tCC1No.length() > 4) {
                                vCC1No = tCC1No.substring(tCC1No.length() - 4);
                            }
                        }
                        CC1Code             = rsTot.getString(9);
                        Double dItemTender  = rsTot.getDouble(10);
                        ItemTender          = String.format(Locale.US, "%,.2f", dItemTender);
                        AdjAmt              = String.format(Locale.US, "%,.2f", rsTot.getDouble(11));
                    }
                }else if(DBStatus.equals("0")){
                    Cursor rsTot = db.getQuery(qTotal);
                    while (rsTot.moveToNext()) {
                        Double dTotAmt = rsTot.getDouble(0);
                        TotAmt = String.format(Locale.US, "%,.2f", dTotAmt);
                        Double dTaxAmt = rsTot.getDouble(1);
                        TaxAmt = String.format(Locale.US, "%,.2f", dTaxAmt);
                        dPayAmt = rsTot.getDouble(2);
                        PayAmt = String.format(Locale.US, "%,.2f", dPayAmt);
                        Double dChAmt = rsTot.getDouble(3);
                        ChAmt = String.format(Locale.US, "%,.2f", dChAmt);
                        Double dAmtExtTax = rsTot.getDouble(4);
                        AmtExtTax = String.format(Locale.US, "%,.2f", dAmtExtTax);
                        dBalAmt = rsTot.getDouble(5);
                        BalAmt = String.format(Locale.US, "%,.2f", dBalAmt);
                        dCC1Amt = rsTot.getDouble(6);
                        CC1Amt = String.format(Locale.US, "%,.2f", dCC1Amt);
                        if (dCC1Amt > 0) {
                            String tCC1No = rsTot.getString(7);
                            if (tCC1No.length() > 4) {
                                vCC1No = tCC1No.substring(tCC1No.length() - 4);
                            }
                        }
                        CC1Code      = rsTot.getString(8);
                        Double dItemTender  = rsTot.getDouble(9);
                        ItemTender   = String.format(Locale.US, "%,.2f", dItemTender);
                        AdjAmt      = String.format(Locale.US, "%,.2f", rsTot.getDouble(10));
                        //
                    }
                }
                vLine16 = str_pad(replaceDec(AmtExtTax), 11, " ", "STR_PAD_LEFT");
                vLine17 = str_pad(replaceDec(TaxAmt), 11, " ", "STR_PAD_LEFT");
                vLine18 = str_pad(replaceDec(AdjAmt), 11, " ", "STR_PAD_LEFT");
                vLine19 = str_pad(replaceDec(TotAmt), 11, " ", "STR_PAD_LEFT");
                vLine20 = str_pad(replaceDec(PayAmt), 11, " ", "STR_PAD_LEFT");
                vLine21 = str_pad(replaceDec(ChAmt), 11, " ", "STR_PAD_LEFT");
                vLine22 = str_pad(replaceDec(BalAmt), 11, " ", "STR_PAD_LEFT");
                vLine22_2 = str_pad(ItemTender, 11, " ", "STR_PAD_LEFT");
                vLine22_3 = str_pad(replaceDec(CC1Amt), 11, " ", "STR_PAD_LEFT");
                vLine22_4 = str_pad("xxxxx" + vCC1No, 11, " ", "STR_PAD_LEFT");
                tCC1Code = str_pad(CC1Code, 18, " ", "STR_PAD_RIGHT");

                strPrint += "________________________________\n\n";
                if (!GSTNo.equals("NO")) {
                    strPrint += "Amount Exc Tax    : " + vLine16 + "\n";
                    strPrint += "Add Total GST Amt : " + vLine17 + "\n";
                    strPrint += "Rounding          : " + vLine18 + "\n";
                    strPrint += "Total Amount Due  : " + vLine19 + "\n";
                    strPrint += "Paid Amount       : " + vLine20 + "\n";
                    if (dPayAmt > 0) {
                        strPrint += "Cash              : " + vLine20 + "\n";
                        strPrint += "Change            : " + vLine21 + "\n";
                    } else {
                        strPrint += tCC1Code + ": " + vLine22_3 + "\n";
                        strPrint += "Verification Code : " + vLine22_4 + "\n";
                    }
                } else {
                    strPrint += "Gross Amount      : " + vLine19 + "\n";
                    if (dPayAmt > 0) {
                        strPrint += "Cash              : " + vLine20 + "\n";
                        strPrint += "Change            : " + vLine21 + "\n";
                    }
                    if (dCC1Amt > 0) {
                        strPrint += tCC1Code + ": " + vLine22_3 + "\n";
                        strPrint += "Verification Code : " + vLine22_4 + "\n";
                    }
                    strPrint += "Total Qty Tender  : " + vLine22_2 + "\n";

                }

                if (dBalAmt > 0) {
                    strPrint += "Balance Amount    : " + vLine22 + "\n";
                }
                if (!GSTNo.equals("NO")) {
                    strPrint += "________________________________\n";
                    strPrint += "GST Summary \n";
                    strPrint += "Code  Rate   Goods Amt  GST Amt\n";
                }
                //end total

                //start GST
                String TaxCode="",TaxRate="",GoodAmt="",GSTAmt="";
                String qGST = "select IFNULL(TaxRate1,0)as TaxRate1, DetailTaxCode, IFNULL(sum(HCLineAmt)-sum(HCTax),0) as GoodAmt, " +
                        "IFNULL(sum(HCTax),0) as GSTAmt from stk_cus_inv_dt  where Doc1No='" + Doc1No + "' Group By DetailTaxCode  ";
                if(DBStatus.equals("2")) {
                    jsonReq = new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qGST);
                    jsonReq.put("action", "select");
                    String rsGST = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsGST);
                    String resGST = jsonRes.getString("hasil");
                    JSONArray jaGST = new JSONArray(resGST);
                    JSONObject vTax = null;
                    for (int i = 0; i < jaGST.length(); i++) {
                        vTax = jaGST.getJSONObject(i);
                        Double dTaxRate     = vTax.getDouble("TaxRate1");
                        TaxRate             = String.format(Locale.US, "%,.2f", dTaxRate);
                        TaxCode             = vTax.getString("DetailTaxCode");
                        Double dGoodAmt     = vTax.getDouble("GoodAmt");
                        GoodAmt             = String.format(Locale.US, "%,.2f", dGoodAmt);
                        Double dGSTAmt      = vTax.getDouble("GSTAmt");
                        GSTAmt              = String.format(Locale.US, "%,.2f", dGSTAmt);
                        String vLine23_1    = str_pad(TaxCode, 5, " ", "STR_PAD_LEFT");
                        String vLine23_2    = str_pad(TaxRate, 5, " ", "STR_PAD_LEFT");
                        String vLine23_3    = str_pad(GoodAmt, 11, " ", "STR_PAD_LEFT");
                        String vLine23_4    = str_pad(GSTAmt, 11, " ", "STR_PAD_LEFT");
                        vLine23             = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                        if (!GSTNo.equals("NO")) {
                            strPrint        += vLine23 + "\n";
                        }
                    }
                }else if(DBStatus.equals("1")){
                    Statement stmtGST = conn.createStatement();
                    stmtGST.execute(qGST);
                    ResultSet rsGST = stmtGST.getResultSet();
                    while (rsGST.next()) {
                        Double dTaxRate     = rsGST.getDouble(1);
                        TaxRate             = String.format(Locale.US, "%,.2f", dTaxRate);
                        TaxCode             = rsGST.getString(2);
                        if(TaxCode.length()>4){
                            TaxCode=TaxCode.substring(0, 4);
                        }
                        Double dGoodAmt     = rsGST.getDouble(3);
                        GoodAmt             = String.format(Locale.US, "%,.2f", dGoodAmt);
                        Double dGSTAmt      = rsGST.getDouble(4);
                        GSTAmt              = String.format(Locale.US, "%,.2f", dGSTAmt);
                        String vLine23_1    = str_pad(TaxCode, 5, " ", "STR_PAD_LEFT");
                        String vLine23_2    = str_pad(TaxRate, 5, " ", "STR_PAD_LEFT");
                        String vLine23_3    = str_pad(GoodAmt, 11, " ", "STR_PAD_LEFT");
                        String vLine23_4    = str_pad(GSTAmt, 11, " ", "STR_PAD_LEFT");
                        vLine23             = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                        if (!GSTNo.equals("NO")) {
                            strPrint        += vLine23 + "\n";
                        }
                    }
                }else if(DBStatus.equals("0")){
                    Cursor rsGST = db.getQuery(qGST);
                    while (rsGST.moveToNext()) {
                        Double dTaxRate     = rsGST.getDouble(0);
                        TaxRate             = String.format(Locale.US, "%,.2f", dTaxRate);
                        TaxCode             = rsGST.getString(1);
                        if(TaxCode.length()>4){
                            TaxCode=TaxCode.substring(0, 4);
                        }
                        Double dGoodAmt     = rsGST.getDouble(2);
                        GoodAmt             = String.format(Locale.US, "%,.2f", dGoodAmt);
                        Double dGSTAmt      = rsGST.getDouble(3);
                        GSTAmt              = String.format(Locale.US, "%,.2f", dGSTAmt);
                        String vLine23_1    = str_pad(TaxCode, 5, " ", "STR_PAD_LEFT");
                        String vLine23_2    = str_pad(TaxRate, 5, " ", "STR_PAD_LEFT");
                        String vLine23_3    = str_pad(GoodAmt, 11, " ", "STR_PAD_LEFT");
                        String vLine23_4    = str_pad(GSTAmt, 11, " ", "STR_PAD_LEFT");
                        vLine23             = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                        if (!GSTNo.equals("NO")) {
                            strPrint        += vLine23 + "\n";
                        }
                    }
                }
                //end GST

                if(!vFooter.isEmpty()){
                    strPrint += Footer+"\n";
                }else {
                    strPrint += "*Goods sold non-returnable & non-refundable\n";
                    strPrint += "Thank you, please come again! \n";
                }

                if(ReceiptType.equals("Format02")){
                    strPrint += "________________________________\n";
                    strPrint += vLine9 + "\n";
                    strPrint += vLine11 + vLine11_1 + "\n";
                    strPrint += vLine12 + "\n";
                    strPrint += vLine12_2;
                    if(!Doc2No.isEmpty()) {
                        strPrint += vLine12_1;
                    }
                }
                strPrint += "________________________________\n \n\n";
                //Log.d("RESULT", strPrint);
                return strPrint;
            }catch (SQLiteException e) {
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return strPrint;
        }

        private String fngenerate78(){
            String vLine1,vLine2,vLine3,vLine4,vLine4_1,vLine5,vLine6,vLine7,vLine8,vLine9,vLine10,vLine11,vLine12,vLine13,vLine14,vLine15,vLine16,vLine17;
            String vLine18,vLine19,vLine20,vLine21,vLine22,vLine23,vLine24,vLine25;
            vLine1="";
            vLine2="";
            vLine3="";
            vLine4="";
            vLine4_1="";
            vLine5="";
            vLine6="";
            vLine7="";
            vLine8="";
            vLine9="";
            vLine10="";
            vLine11="";
            vLine12="";
            vLine13="";
            vLine14="";
            vLine15="";
            vLine16="";
            vLine17="";
            vLine18="";
            vLine19="";
            vLine20="";
            vLine21="";
            vLine22="";
            vLine23="";
            vLine24="";
            vLine25="";

            String strPrint="";
            String CompanyName="";
            String CompanyCode="";
            String GSTNo="";
            String Address="";
            String ComTown="";
            String ComState="";
            String ComCountry="";
            String Tel="";
            String Fax="";
            String CusCode="";
            String CusName="";
            String TelCus="";
            String AdddresCus="";
            String SalesPersonCode="";
            //String D_ateTime="";
            String Disc2="";
            String Minus2="";
            String vCC1No="";
            String vLine11_1="";
            String datedTime="";
            String TableNo="";
            String vLine71="";
            String VoidYN="";
            String vFooter="";
            try {
                JSONObject jsonReq,jsonRes;
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String Company = "select CurCode, CompanyName, CompanyCode," +
                        "GSTNo, Address, Tel1, " +
                        "Fax1, CompanyEmail, ComTown," +
                        "ComState,ComCountry, IFNULL(Footer_CR,'')as Footer_CR," +
                        "IFNULL(PhotoFile,'')as PhotoFile " +
                        " FROM companysetup ";
                if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", Company);
                    jsonReq.put("action", "select");
                    String rsCom = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsCom);
                    String rsHeader = jsonRes.getString("hasil");
                    JSONArray rsData = new JSONArray(rsHeader);
                    JSONObject vData = null;
                    for (int i = 0; i < rsData.length(); i++) {
                        vData = rsData.getJSONObject(i);
                        CompanyName = vData.getString("CompanyName");
                        CompanyCode = vData.getString("CompanyCode");
                        GSTNo       = vData.getString("GSTNo");
                        Address     = vData.getString("Address");
                        Tel         = vData.getString("Tel1");
                        Fax         = vData.getString("Fax1");
                        vFooter         = String.valueOf(Html.fromHtml(vData.getString("Footer_CR")));
                    }
                }else {
                    Cursor rsCom = db.getQuery(Company);
                    while (rsCom.moveToNext()) {
                        CompanyName = rsCom.getString(1);
                        CompanyCode = rsCom.getString(2);
                        GSTNo = rsCom.getString(3);
                        Address =  rsCom.getString(4);
                        Tel = rsCom.getString(5);
                        Fax = rsCom.getString(6);
                        ComTown = rsCom.getString(8);
                        ComState = rsCom.getString(9);
                        ComCountry = rsCom.getString(10);
                        vFooter = String.valueOf(Html.fromHtml(rsCom.getString(11)));
                        imgHeader           = rsCom.getString(12);
                        byte[] decString    = Base64.decode(imgHeader, Base64.DEFAULT);
                        bmpHeader           = BitmapFactory.decodeByteArray(decString,0,decString.length);
                    }
                }
                vLine1 = str_pad(CompanyName, 48, " ", "STR_PAD_BOTH");
                vLine2 = str_pad("Co.Reg.No.: " + CompanyCode, 48, " ", "STR_PAD_BOTH");
                vLine3 = str_pad("GST Reg.No.: " + GSTNo, 48, " ", "STR_PAD_BOTH");
                String AddressAll = Address.replaceAll("(.{48})", "$1\n");
                String[] address = AddressAll.split("\n");
                String vAddress = "";
                for (String add : address) {
                    vAddress += str_pad(add, 48, " ", "STR_PAD_BOTH");
                }
                vLine4 = vAddress;

                String FooterAll = vFooter.replaceAll("(.{47})", "$1\n");
                String [] footer=FooterAll.split("\n");
                String Footer="";
                for (String add: footer) {
                    Footer +=str_pad(add, 47, " ", "STR_PAD_BOTH");
                }

                //vLine4   =str_pad(AddressAll, 48, " ", "STR_PAD_BOTH");
                vLine5 = str_pad("Tel.:" + Tel, 48, " ", "STR_PAD_BOTH");
                vLine6 = str_pad(Fax, 48, " ", "STR_PAD_BOTH");
                if (!GSTNo.equals("NO")) {
                    vLine7 = str_pad("TAX INVOICE (REPRINT)", 48, " ", "STR_PAD_BOTH");
                } else {
                    vLine71 = str_pad("VOID BILL", 48, " ", "STR_PAD_BOTH");
                    vLine7 = str_pad(ReceiptHeader+" (REPRINT)", 48, " ", "STR_PAD_BOTH");
                }
                //end query header

                //start customer

                String qCustomer="select H.D_ateTime, D.AnalysisCode2, H.CusCode," +
                        " C.CusName,C.Address, H.Status2, " +
                        " D.SalesPersonCode from stk_cus_inv_hd H" +
                        " inner join stk_cus_inv_dt D on H.Doc1No=D.Doc1No inner join customer C ON H.CusCode=C.CusCode " +
                        " where H.Doc1No='"+Doc1No+"' Group By H.Doc1No  ";
                if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qCustomer);
                    jsonReq.put("action", "select");
                    String rsCus = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsCus);
                    String resCus= jsonRes.getString("hasil");
                    Log.d("JSON H",resCus);
                    JSONArray jaCus = new JSONArray(resCus);
                    JSONObject vCus = null;
                    for (int i = 0; i < jaCus.length(); i++) {
                        vCus        = jaCus.getJSONObject(i);
                        CusCode     = vCus.getString("CusCode");
                        CusName     = vCus.getString("CusName");
                        AdddresCus  = vCus.getString("Address");
                        datedTime   = vCus.getString("D_ateTime");
                        TableNo     = vCus.getString("AnalysisCode2");
                        VoidYN      =vCus.getString("Status2");
                        SalesPersonCode  =vCus.getString("SalesPersonCode");
                    }
                }else if(DBStatus.equals("1")){
                    Statement stmtCus = conn.createStatement();
                    stmtCus.execute(qCustomer);
                    ResultSet rsCus = stmtCus.getResultSet();
                    while (rsCus.next()) {
                        datedTime       = rsCus.getString(1);
                        TableNo         = rsCus.getString(2);
                        CusCode         = rsCus.getString(3);
                        CusName         = rsCus.getString(4);
                        AdddresCus      = rsCus.getString(5);
                        VoidYN          = rsCus.getString(6);
                        SalesPersonCode = rsCus.getString(7);
                    }
                }else if(DBStatus.equals("0")){
                    Cursor rsCus = db.getQuery(qCustomer);
                    while (rsCus.moveToNext()) {
                        datedTime           = rsCus.getString(0);
                        TableNo             = rsCus.getString(1);
                        CusCode             = rsCus.getString(2);
                        CusName             = rsCus.getString(3);
                        AdddresCus          = rsCus.getString(4);
                        VoidYN              = rsCus.getString(5);
                        SalesPersonCode     = rsCus.getString(6);
                    }
                }
                vLine8 = str_pad("Bill #: " + Doc1No, 48, " ", "STR_PAD_RIGHT");
                vLine11 = str_pad(D_ateTime, 24, " ", "STR_PAD_RIGHT");
                if (TableNo.equals("0")  ||  TableNo.isEmpty()) {
                    vLine11_1 = str_pad("", 24, " ", "STR_PAD_RIGHT");
                } else {
                    vLine11_1 = str_pad("Table No #: " + TableNo, 24, " ", "STR_PAD_RIGHT");
                }
                vLine12 = str_pad("TO: " + CusName, 48, " ", "STR_PAD_RIGHT");
                String vLine12_1 = str_pad("Doc2No: " + Doc2No, 48, " ", "STR_PAD_RIGHT");
                String vLine12_2 = str_pad("Salesman: "+SalesPersonCode, 48, " ", "STR_PAD_RIGHT");
                vLine13 = str_pad(AdddresCus, 48, " ", "STR_PAD_RIGHT");
                String vLine131 = str_pad("Quantity", 9, " ", "STR_PAD_RIGHT");
                String vLine132 = str_pad("Unit Price", 19, " ", "STR_PAD_BOTH");
                String vLine133 = str_pad("Amount (" + CurCode + ")", 20, " ", "STR_PAD_LEFT");
                //end query customer

                strPrint += vLine1 + "\n";
                if(!CompanyCode.isEmpty()) {
                    strPrint += vLine2 + "\n";
                }

                if(!GSTNo.equals("NO")) {
                    strPrint += vLine3 + "\n";
                }
                strPrint += vLine4 + "\n";
                if (!Tel.equals("")) {
                    strPrint += vLine5 + "\n";
                }
                if (!Fax.equals("")) {
                    // strPrint += vLine6+"\n";
                }
                strPrint += "________________________________________________\n";
                if(VoidYN.equals("Void") && GSTNo.equals("NO")){
                    strPrint += vLine71 + "\n";
                }
                strPrint += vLine7 + "\n";
                strPrint += "________________________________________________\n";
                //end header


                strPrint += vLine8 + "\n";
                strPrint += vLine11 + vLine11_1 + "\n";
                strPrint += vLine12 +"\n";
                strPrint += vLine12_2;
                if(!Doc2No.isEmpty()){
                    strPrint += vLine12_1;
                }
                if (!CusCode.equals("999999")) {
                    //strPrint += vLine13 + "\n";
                }
                strPrint += "________________________________________________\n";
                strPrint += vLine131 + vLine132 + vLine133 + "\n";
                strPrint += "________________________________________________\n";
                //end customer

                //detail
                String qDetail = "select D.ItemCode,IFNULL(D.Qty,0) as Qty, D.UOM," +
                        " D.DetailTaxCode, substr(D.Description,1,42) as Description, IFNULL(D.HCUnitCost,0) as HCUnitCost," +
                        " IFNULL(D.HCLineAmt,0) AS HCLineAmt,IFNULL(D.DisRate1,0) as DisRate1," +
                        " IFNULL(D.HCDiscount,0) as HCDiscount, M.AlternateItem " +
                        " from stk_cus_inv_dt D inner join stk_master M on D.ItemCode=M.ItemCode " +
                        " Where D.Doc1No='" + Doc1No + "'  ";
                if(DBStatus.equals("2")) {
                    jsonReq = new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qDetail);
                    jsonReq.put("action", "select");
                    String reqDet = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(reqDet);
                    String resDet = jsonRes.getString("hasil");
                    JSONArray jaDt = new JSONArray(resDet);
                    JSONObject vDet = null;
                    for (int i = 0; i < jaDt.length(); i++) {
                        vDet = jaDt.getJSONObject(i);
                        String ItemCode = vDet.getString("ItemCode");
                        Double dQty = vDet.getDouble("Qty");
                        String Qty = String.format(Locale.US, "%,.2f", dQty);
                        String UOM = vDet.getString("UOM").trim().replaceAll("\\s+", " ");
                        String DetailTaxCode = vDet.getString("DetailTaxCode");
                        String Description = vDet.getString("Description").trim().replaceAll("\\s+", " ");
                        Double dHCUnitCost = vDet.getDouble("HCUnitCost");
                        String HCUnitCost = String.format(Locale.US, "%,.2f", dHCUnitCost);
                        Double dHCLineAmt = vDet.getDouble("HCLineAmt");
                        String HCLineAmt = String.format(Locale.US, "%,.2f", dHCLineAmt);
                        Double dDisRate1 = vDet.getDouble("DisRate1");
                        String DisRate1 = String.format(Locale.US, "%,.2f", dDisRate1);
                        Double dHCDiscount = vDet.getDouble("HCDiscount");
                        String HCDiscount = String.format(Locale.US, "%,.2f", dHCDiscount);
                        if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                            Disc2 = "(" + HCDiscount + ")";
                        } else {
                            Disc2 = " ";
                        }
                        vLine14 = str_pad(Description, 42, " ", "STR_PAD_RIGHT");
                        String AlternateItem = vDet.getString("AlternateItem").trim().replaceAll("\\s+", " ");
                        String vLine14_1 = str_pad(UOM, 6, " ", "STR_PAD_RIGHT");
                        String vLine14_2 = str_pad("", 6, " ", "STR_PAD_RIGHT");
                        String vLine14_3 = str_pad(AlternateItem, 35, " ", "STR_PAD_RIGHT");
                        String vLine15_1 = str_pad(Qty, 6, " ", "STR_PAD_LEFT");
                        String vLine15_2 = str_pad(HCUnitCost, 13, " ", "STR_PAD_LEFT");
                        String vLine15_3 = str_pad(Disc2, 12, " ", "STR_PAD_LEFT");
                        String vLine15_4 = str_pad(HCLineAmt, 15, " ", "STR_PAD_LEFT");
                        strPrint += vLine14 + vLine14_1 + "\n";
                        if (AlternateItem.length() > 1) {
                            strPrint += vLine14_3 + vLine14_2 + "\n";
                        }
                        strPrint += "" + vLine15_1 + " x" + vLine15_2 + vLine15_3 + vLine15_4 + "\n";
                    }
                }else if(DBStatus.equals("1")){
                    Statement stmtDt = conn.createStatement();
                    stmtDt.execute(qDetail);
                    ResultSet rsDt = stmtDt.getResultSet();
                    while (rsDt.next()) {
                        String ItemCode         = rsDt.getString(1);
                        Double dQty             = rsDt.getDouble(2);
                        String Qty              = String.format(Locale.US, "%,.2f", dQty);
                        String UOM              = Encode.setChar(EncodeType,rsDt.getString(3).trim().replaceAll("\\s+", " "));
                        String DetailTaxCode    = rsDt.getString(4);
                        String Description      = Encode.setChar(EncodeType,rsDt.getString(5).trim().replaceAll("\\s+", " "));
                        Double dHCUnitCost      = rsDt.getDouble(6);
                        String HCUnitCost       = String.format(Locale.US, "%,.2f", dHCUnitCost);
                        Double dHCLineAmt       = rsDt.getDouble(7);
                        String HCLineAmt        = String.format(Locale.US, "%,.2f", dHCLineAmt);
                        Double dDisRate1        = rsDt.getDouble(8);
                        String DisRate1         = String.format(Locale.US, "%,.2f", dDisRate1);
                        Double dHCDiscount      = rsDt.getDouble(9);
                        String HCDiscount       = String.format(Locale.US, "%,.2f", dHCDiscount);
                        if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                            Disc2 = "(" + HCDiscount + ")";
                        } else {
                            Disc2 = " ";
                        }
                        String AlternateItem    = Encode.setChar(EncodeType,rsDt.getString(10).trim().replaceAll("\\s+", " "));
                        vLine14                 = str_pad(Description, 42, " ", "STR_PAD_RIGHT");
                        String vLine14_1        = str_pad(UOM, 6, " ", "STR_PAD_RIGHT");
                        String vLine14_2        = str_pad("", 6, " ", "STR_PAD_RIGHT");
                        String vLine14_3        = str_pad(AlternateItem, 35, " ", "STR_PAD_RIGHT");
                        String vLine15_1        = str_pad(Qty, 6, " ", "STR_PAD_LEFT");
                        String vLine15_2        = str_pad(HCUnitCost, 13, " ", "STR_PAD_LEFT");
                        String vLine15_3        = str_pad(Disc2, 12, " ", "STR_PAD_LEFT");
                        String vLine15_4        = str_pad(HCLineAmt, 15, " ", "STR_PAD_LEFT");
                        strPrint += vLine14+vLine14_1 + "\n";
                        if(AlternateItem.length()>1){
                            strPrint            += vLine14_3+vLine14_2+"\n";
                        }
                        strPrint += "" + vLine15_1 + " x" + vLine15_2 + vLine15_3 + vLine15_4 + "\n";
                    }

                }else if(DBStatus.equals("0")) {
                    Cursor rsDt = db.getQuery(qDetail);
                    while (rsDt.moveToNext()) {
                        String ItemCode         = rsDt.getString(0);
                        Double dQty             = rsDt.getDouble(1);
                        String Qty              = String.format(Locale.US, "%,.2f", dQty);
                        String UOM              = rsDt.getString(2).trim().replaceAll("\\s+", " ");
                        String DetailTaxCode    = rsDt.getString(3);
                        String Description      = rsDt.getString(4).trim().replaceAll("\\s+", " ");
                        Double dHCUnitCost      = rsDt.getDouble(5);
                        String HCUnitCost       = String.format(Locale.US, "%,.2f", dHCUnitCost);
                        Double dHCLineAmt       = rsDt.getDouble(6);
                        String HCLineAmt        = String.format(Locale.US, "%,.2f", dHCLineAmt);
                        Double dDisRate1        = rsDt.getDouble(7);
                        String DisRate1         = String.format(Locale.US, "%,.2f", dDisRate1);
                        Double dHCDiscount      = rsDt.getDouble(8);
                        String HCDiscount       = String.format(Locale.US, "%,.2f", dHCDiscount);
                        if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                            Disc2 = "(" + HCDiscount + ")";
                        } else {
                            Disc2 = " ";
                        }
                        String AlternateItem    = rsDt.getString(9).trim().replaceAll("\\s+", " ");
                        vLine14                 = str_pad(Description, 42, " ", "STR_PAD_RIGHT");
                        String vLine14_1        = str_pad(UOM, 6, " ", "STR_PAD_RIGHT");
                        String vLine14_2        = str_pad("", 6, " ", "STR_PAD_RIGHT");
                        String vLine14_3        = str_pad(AlternateItem, 35, " ", "STR_PAD_RIGHT");
                        String vLine15_1        = str_pad(Qty, 6, " ", "STR_PAD_LEFT");
                        String vLine15_2        = str_pad(HCUnitCost, 13, " ", "STR_PAD_LEFT");
                        String vLine15_3        = str_pad(Disc2, 12, " ", "STR_PAD_LEFT");
                        String vLine15_4        = str_pad(HCLineAmt, 15, " ", "STR_PAD_LEFT");
                        strPrint += vLine14+vLine14_1 + "\n";
                        if(AlternateItem.length()>1){
                            strPrint            += vLine14_3+vLine14_2+"\n";
                        }
                        strPrint += "" + vLine15_1 + " x" + vLine15_2 + vLine15_3 + vLine15_4 + "\n";
                    }
                }
                //end detail

                //start Total

                String qTotal = "SELECT IFNULL(H.HCNetAmt,0)AS HCNetAmt,IFNULL(H.HCDtTax,0) AS HCDtTax, "+
                        " IFNULL(R.CashAmt + R.ChangeAmt,0) AS CashAmt,  IFNULL(R.ChangeAmt,0) AS ChangeAmt, "+
                        " IFNULL(SUM(D.HCLineAmt)-SUM(D.HCTax),0) AS AmtExTax,IFNULL(R.BalanceAmount,0) AS BalanceAmount, "+
                        " IFNULL(R.CC1Amt,0) AS CC1Amt, IFNULL(R.CC1No,'') AS CC1No, " +
                        " IFNULL(R.CC1Code,'')AS CC1Code, IFNULL(SUM(D.Qty),0) AS ItemTender," +
                        " IFNULL(H.AdjAmt,0)as AdjAmt "+
                        " FROM stk_cus_inv_hd H LEFT JOIN stk_receipt2 R ON R.Doc1No=H.Doc1No " +
                        " INNER JOIN stk_cus_inv_dt D ON D.Doc1No=H.Doc1No "+
                        " where H.Doc1No='" + Doc1No + "' and D.ItemCode<>'M999999SC' Group By H.Doc1No ";

                Double dPayAmt=0.00;
                Double dCC1Amt=0.00;
                Double dBalAmt=0.00;
                String tCC1Code="";
                String vLine22_2="";
                String vLine22_3="";
                String vLine22_4="";
                String TaxAmt="",TotAmt="",AmtExtTax="",AdjAmt="",BalAmt="",ChAmt="",CC1Amt="",CC1Code="",PayAmt="",ItemTender="";
                if(DBStatus.equals("2")) {
                    jsonReq = new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qTotal);
                    jsonReq.put("action", "select");
                    String rsTot = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsTot);
                    String resTot = jsonRes.getString("hasil");
                    JSONArray jaTot = new JSONArray(resTot);
                    JSONObject vTot = null;
                    for (int i = 0; i < jaTot.length(); i++) {
                        vTot                = jaTot.getJSONObject(i);
                        Double dTotAmt      = vTot.getDouble("HCNetAmt");
                        TotAmt              = String.format(Locale.US, "%,.2f", dTotAmt);
                        Double dTaxAmt      = vTot.getDouble("HCDtTax");
                        TaxAmt              = String.format(Locale.US, "%,.2f", dTaxAmt);
                        dPayAmt             = vTot.getDouble("CashAmt");
                        PayAmt              = String.format(Locale.US, "%,.2f", dPayAmt);
                        Double dChAmt       = vTot.getDouble("ChangeAmt");
                        ChAmt               = String.format(Locale.US, "%,.2f", dChAmt);
                        Double dAmtExtTax   = vTot.getDouble("AmtExTax");
                        AmtExtTax           = String.format(Locale.US, "%,.2f", dAmtExtTax);
                        dBalAmt             = vTot.getDouble("BalanceAmount");
                        BalAmt              = String.format(Locale.US, "%,.2f", dBalAmt);
                        dCC1Amt             = vTot.getDouble("CC1Amt");
                        CC1Amt              = String.format(Locale.US, "%,.2f", dCC1Amt);
                        if (dCC1Amt > 0) {
                            String tCC1No   = vTot.getString("CC1No");
                            vCC1No = tCC1No.substring(tCC1No.length() - 4);
                        }
                        CC1Code             = vTot.getString("CC1Code");
                        Double dItemTender  = vTot.getDouble("ItemTender");
                        ItemTender          = String.format(Locale.US, "%,.2f", dItemTender);
                        AdjAmt              = String.format(Locale.US, "%,.2f", vTot.getDouble("AdjAmt"));

                    }
                }else if(DBStatus.equals("1")){
                    Statement stmtTot   = conn.createStatement();
                    stmtTot.execute(qTotal);
                    ResultSet rsTot      = stmtTot.getResultSet();
                    while (rsTot.next()) {
                        Double dTotAmt      = rsTot.getDouble(1);
                        TotAmt              = String.format(Locale.US, "%,.2f", dTotAmt);
                        Double dTaxAmt      = rsTot.getDouble(2);
                        TaxAmt              = String.format(Locale.US, "%,.2f", dTaxAmt);
                        dPayAmt             = rsTot.getDouble(3);
                        PayAmt              = String.format(Locale.US, "%,.2f", dPayAmt);
                        Double dChAmt       = rsTot.getDouble(4);
                        ChAmt               = String.format(Locale.US, "%,.2f", dChAmt);
                        Double dAmtExtTax   = rsTot.getDouble(5);
                        AmtExtTax           = String.format(Locale.US, "%,.2f", dAmtExtTax);
                        dBalAmt             = rsTot.getDouble(6);
                        BalAmt              = String.format(Locale.US, "%,.2f", dBalAmt);
                        dCC1Amt             = rsTot.getDouble(7);
                        CC1Amt              = String.format(Locale.US, "%,.2f", dCC1Amt);
                        if (dCC1Amt > 0) {
                            String tCC1No = rsTot.getString(8);
                            if (tCC1No.length() > 4){
                                vCC1No = tCC1No.substring(tCC1No.length() - 4);
                            }
                        }
                        CC1Code             = rsTot.getString(9);
                        Double dItemTender  = rsTot.getDouble(10);
                        ItemTender          = String.format(Locale.US, "%,.2f", dItemTender);
                        AdjAmt             = String.format(Locale.US, "%,.2f", rsTot.getDouble(11));
                    }
                }else if(DBStatus.equals("0")){
                    Cursor rsTot = db.getQuery(qTotal);
                    while (rsTot.moveToNext()) {
                        Double dTotAmt = rsTot.getDouble(0);
                        TotAmt              = String.format(Locale.US, "%,.2f", dTotAmt);
                        Double dTaxAmt      = rsTot.getDouble(1);
                        TaxAmt              = String.format(Locale.US, "%,.2f", dTaxAmt);
                        dPayAmt             = rsTot.getDouble(2);
                        PayAmt              = String.format(Locale.US, "%,.2f", dPayAmt);
                        Double dChAmt       = rsTot.getDouble(3);
                        ChAmt               = String.format(Locale.US, "%,.2f", dChAmt);
                        Double dAmtExtTax   = rsTot.getDouble(4);
                        AmtExtTax           = String.format(Locale.US, "%,.2f", dAmtExtTax);
                        dBalAmt             = rsTot.getDouble(5);
                        BalAmt              = String.format(Locale.US, "%,.2f", dBalAmt);
                        dCC1Amt             = rsTot.getDouble(6);
                        CC1Amt              = String.format(Locale.US, "%,.2f", dCC1Amt);
                        if (dCC1Amt > 0) {
                            String tCC1No   = rsTot.getString(7);
                            if (tCC1No.length() > 4) {
                                vCC1No = tCC1No.substring(tCC1No.length() - 4);
                            }
                        }
                        CC1Code             = rsTot.getString(8);
                        Double dItemTender  = rsTot.getDouble(9);
                        ItemTender          = String.format(Locale.US, "%,.2f", dItemTender);
                        AdjAmt              = String.format(Locale.US, "%,.2f", rsTot.getDouble(10));

                    }
                }
                vLine16 = str_pad(AmtExtTax, 22, " ", "STR_PAD_LEFT");
                vLine17 = str_pad(TaxAmt, 22, " ", "STR_PAD_LEFT");
                vLine18 = str_pad(AdjAmt, 22, " ", "STR_PAD_LEFT");
                vLine19 = str_pad(TotAmt, 22, " ", "STR_PAD_LEFT");
                vLine20 = str_pad(PayAmt, 22, " ", "STR_PAD_LEFT");
                vLine21 = str_pad(ChAmt, 22, " ", "STR_PAD_LEFT");
                vLine22 = str_pad(BalAmt, 22, " ", "STR_PAD_LEFT");
                vLine22_2 = str_pad(ItemTender, 22, " ", "STR_PAD_LEFT");
                vLine22_3 = str_pad(CC1Amt, 22, " ", "STR_PAD_LEFT");
                vLine22_4 = str_pad("xxxxxxxxxxx" + vCC1No, 22, " ", "STR_PAD_LEFT");
                tCC1Code = str_pad(CC1Code, 24, " ", "STR_PAD_RIGHT");
                strPrint += "________________________________________________\n\n";
                if (!GSTNo.equals("NO")) {
                    strPrint += "Amount Exc Tax         : " + vLine16 + "\n";
                    strPrint += "Add Total Tax Amount   : " + vLine17 + "\n";
                    strPrint += "Rounding               : " + vLine18 + "\n";
                    strPrint += "Total Amount Due       : " + vLine19 + "\n";
                    if (dCC1Amt > 0) {
                        strPrint += tCC1Code + vLine22_3 + "\n";
                        strPrint += "Verification Code      : " + vLine22_4 + "\n";
                    } else {
                        strPrint += "Cash                   : " + vLine20 + "\n";
                        strPrint += "Change                 : " + vLine21 + "\n";
                    }
                } else {
                    strPrint += "Gross Amount           : " + vLine19 + "\n";
                    if (dPayAmt > 0) {
                        strPrint += "Cash                   : " + vLine20 + "\n";
                        strPrint += "Change                 : " + vLine21 + "\n";
                    }
                    if (dCC1Amt > 0) {
                        strPrint += tCC1Code + ":" + vLine22_3 + "\n";
                        strPrint += "Verification Code      : " + vLine22_4 + "\n";
                    }
                    strPrint += "Total Quantity Tender  : " + vLine22_2 + "\n";

                }
                if (dBalAmt > 0) {
                    strPrint += "Balance Amount         : " + vLine22 + "\n";
                }
                if (!GSTNo.equals("NO")) {
                    strPrint += "________________________________________________\n";
                    strPrint += "Tax Summary \n";
                    strPrint += "Tax Code      Rate      Goods Amt      Tax Amt  \n";
                }
                //end total

                //start GST
                String qGST = "select IFNULL(TaxRate1,0) as TaxRate1, DetailTaxCode, IFNULL(sum(HCLineAmt)-sum(HCTax),0) as GoodAmt," +
                        " IFNULL(sum(HCTax),0) as GSTAmt from stk_cus_inv_dt  where Doc1No='" + Doc1No + "' Group By DetailTaxCode  ";
                if(DBStatus.equals("2")) {
                    jsonReq = new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qGST);
                    jsonReq.put("action", "select");
                    String rsGST = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsGST);
                    String resGST = jsonRes.getString("hasil");
                    Log.d("JSON GST", resGST);
                    JSONArray jaGST = new JSONArray(resGST);
                    JSONObject vTax = null;
                    for (int i = 0; i < jaGST.length(); i++) {
                        vTax = jaGST.getJSONObject(i);
                        Double dTaxRate     = vTax.getDouble("TaxRate1");
                        String TaxRate      = String.format(Locale.US, "%,.2f", dTaxRate);
                        String TaxCode      = vTax.getString("DetailTaxCode");
                        Double dGoodAmt     = vTax.getDouble("GoodAmt");
                        String GoodAmt      = String.format(Locale.US, "%,.2f", dGoodAmt);
                        Double dGSTAmt      = vTax.getDouble("GSTAmt");
                        String GSTAmt       = String.format(Locale.US, "%,.2f", dGSTAmt);
                        String vLine23_1    = str_pad(TaxCode, 6, " ", "STR_PAD_LEFT");
                        String vLine23_2    = str_pad(TaxRate, 12, " ", "STR_PAD_LEFT");
                        String vLine23_3    = str_pad(GoodAmt, 15, " ", "STR_PAD_LEFT");
                        String vLine23_4    = str_pad(GSTAmt, 15, " ", "STR_PAD_LEFT");
                        if (!GSTNo.equals("NO")) {
                            vLine23         = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                            strPrint        += vLine23 + "\n";
                        }

                    }
                }else if(DBStatus.equals("1")){
                    Statement stmtGST   = conn.createStatement();
                    stmtGST.execute(qGST);
                    ResultSet rsGST      = stmtGST.getResultSet();
                    while (rsGST.next()) {
                        Double dTaxRate     = rsGST.getDouble(1);
                        String TaxRate      = String.format(Locale.US, "%,.2f", dTaxRate);
                        String TaxCode      = rsGST.getString(2);
                        Double dGoodAmt     = rsGST.getDouble(3);
                        String GoodAmt      = String.format(Locale.US, "%,.2f", dGoodAmt);
                        Double dGSTAmt      = rsGST.getDouble(4);
                        String GSTAmt       = String.format(Locale.US, "%,.2f", dGSTAmt);
                        String vLine23_1    = str_pad(TaxCode, 6, " ", "STR_PAD_LEFT");
                        String vLine23_2    = str_pad(TaxRate, 12, " ", "STR_PAD_LEFT");
                        String vLine23_3    = str_pad(GoodAmt, 15, " ", "STR_PAD_LEFT");
                        String vLine23_4    = str_pad(GSTAmt, 15, " ", "STR_PAD_LEFT");
                        if (!GSTNo.equals("NO")) {
                            vLine23         = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                            strPrint        += vLine23 + "\n";
                        }
                    }
                }else if(DBStatus.equals("0")) {
                    Cursor rsGST = db.getQuery(qGST);
                    while (rsGST.moveToNext()) {
                        Double dTaxRate     = rsGST.getDouble(0);
                        String TaxRate      = String.format(Locale.US, "%,.2f", dTaxRate);
                        String TaxCode      = rsGST.getString(1);
                        Double dGoodAmt     = rsGST.getDouble(2);
                        String GoodAmt      = String.format(Locale.US, "%,.2f", dGoodAmt);
                        Double dGSTAmt      = rsGST.getDouble(3);
                        String GSTAmt       = String.format(Locale.US, "%,.2f", dGSTAmt);
                        String vLine23_1    = str_pad(TaxCode, 6, " ", "STR_PAD_LEFT");
                        String vLine23_2    = str_pad(TaxRate, 12, " ", "STR_PAD_LEFT");
                        String vLine23_3    = str_pad(GoodAmt, 15, " ", "STR_PAD_LEFT");
                        String vLine23_4    = str_pad(GSTAmt, 15, " ", "STR_PAD_LEFT");
                        if (!GSTNo.equals("NO")) {
                            vLine23         = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                            strPrint         += vLine23 + "\n";
                        }
                    }
                }

                //end GST
                strPrint += "________________________________________________\n";
                if(!vFooter.isEmpty()){
                    strPrint += Footer+"\n";
                }else {
                    strPrint += "*Goods sold non-returnable & non-refundable\n";
                    strPrint += "Thank you, please come again! \n";
                }


                if(ReceiptType.equals("Format02")){
                    strPrint += "________________________________________________\n";
                    strPrint += vLine8 + "\n";
                    strPrint += vLine11 + vLine11_1 + "\n";
                    strPrint += vLine12 +"\n";
                    strPrint += vLine12_2;
                    if(!Doc2No.isEmpty()){
                        strPrint += vLine12_1;
                    }
                }
                strPrint += "________________________________________________\n\n\n";
                //vPostGlobalTaxYN
                //Log.d("PostGlobalTaxYN", vPostGlobalTaxYN);
               return strPrint;
            }catch (SQLiteException e) {
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return strPrint;
        }

        public String str_pad(String input, int length, String pad, String    sense)
        {
            int resto_pad = length - input.length();
            String padded = "";

            if (resto_pad <= 0){ return input; }

            if(sense.equals("STR_PAD_RIGHT"))
            {
                padded  = input;
                padded += _fill_string(pad,resto_pad);
            }
            else if(sense.equals("STR_PAD_LEFT"))
            {
                padded  = _fill_string(pad, resto_pad);
                padded += input;
            }
            else // STR_PAD_BOTH
            {
                int pad_left  = (int) Math.ceil(resto_pad/2);
                int pad_right = resto_pad - pad_left;

                padded  = _fill_string(pad, pad_left);
                padded += input;
                padded += _fill_string(pad, pad_right);
            }
            return padded;
        }


        protected String _fill_string(String pad, int resto ) {
            boolean first = true;
            String padded = "";

            if (resto >= pad.length())
            {
                for (int i = resto; i >= 0; i = i - pad.length())
                {
                    if (i  >= pad.length())
                    {
                        if (first){ padded = pad; } else { padded += pad; }
                    }
                    else
                    {
                        if (first){ padded = pad.substring(0, i); } else { padded += pad.substring(0, i); }
                    }
                    first = false;
                }
            }
            else
            {
                padded = pad.substring(0,resto);
            }
            return padded;
        }
    }

    private String replaceDec(String value){
        String newValue="";
        if(value.length()>12) {
            newValue= value.substring(0, value.length() - 3) + "-";
        }else{
            newValue=value;
        }
        return newValue;
    }
    public void setReprint(String Doc1No){
        rePrint print=new rePrint(getActivity(),Doc1No);
        print.execute();
    }


    private String replaceChar(String text){
        String newtext=text.replaceAll("[-\\[\\]^,'*:.!><~@#$%+=?|\"\\\\()]+", "");
        return newtext;
    }
}
