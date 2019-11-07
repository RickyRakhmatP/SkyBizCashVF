package skybiz.com.posoffline.ui_CashReceipt.m_VoidBill;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_Ipay88.DialogIpay88;
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

public class DialogVoid extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    SearchView svReprint;
    String CurCode,vPostGlobalTaxYN;
    Boolean isBT;
    String TypePrinter,NamePrinter,IPPrinter,Port,PaperSize,NewDoc;
    EditText txtDateFrom,txtDateTo;
    Button btnRefresh;
    DatePickerDialog datePickerDialog;

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
        getDialog().setTitle("List of Void Bill");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dated = sdf.format(date);
        txtDateFrom.setText(dated);
        txtDateTo.setText(dated);
        refresh();
        readyPrinter();
        return view;
    }
    public void showDialogYN(final String Doc1No, final String PaymentCode, final String Amount){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Confirmation");
        alertDialog.setMessage("Are you sure want to void this bill ?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        fnvoid(Doc1No,PaymentCode,Amount);
                    }
                });
        alertDialog.show();
    }
    private void fnvoid(String Doc1No,String PaymentCode,String Amount){
        switch (PaymentCode) {
            case "Alipay":
                openVoidIpay(Doc1No,PaymentCode,Amount);
                break;
            case "Boost":
                openVoidIpay(Doc1No,PaymentCode,Amount);
                break;
            case "Touch N Go":
                openVoidIpay(Doc1No,PaymentCode,Amount);
                break;
            case "Mcash":
                openVoidIpay(Doc1No,PaymentCode,Amount);
                break;
            case "UnionPay QR":
                openVoidIpay(Doc1No,PaymentCode,Amount);
                break;
            case "Nets QR":
                openVoidIpay(Doc1No,PaymentCode,Amount);
                break;
            case "CIMB Pay":
                openVoidIpay(Doc1No,PaymentCode,Amount);
                break;
            case "MBB QR":
                openVoidIpay(Doc1No,PaymentCode,Amount);
                break;
            case "Grabpay":
                openVoidIpay(Doc1No,PaymentCode,Amount);
                break;
            default:
                VoidBill voidBill = new VoidBill(getActivity(), Doc1No);
                voidBill.execute();
        }
       /* if(PaymentCode.isEmpty()) {
            VoidBill voidBill = new VoidBill(getActivity(), Doc1No);
            voidBill.execute();
        }else if(){

        }*/
    }
    private void openVoidIpay(String Doc1No,String PayType, String Amount){
        Bundle b=new Bundle();
        b.putString("PAYTYPE_KEY",PayType);
        b.putString("REFNO_KEY",Doc1No);
        b.putString("AMOUNT_KEY",Amount);
        DialogVoidIpay dialogVoidIpay = new DialogVoidIpay();
        dialogVoidIpay.setArguments(b);
        dialogVoidIpay.show(getActivity().getSupportFragmentManager(), "mListItem");
    }
    public class VoidBill extends AsyncTask<Void,Void,String>{
        Context c;
        String Doc1No;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus;
        public VoidBill(Context c, String Doc1No) {
            this.c = c;
            this.Doc1No = Doc1No;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.voidbill();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("error")){
                Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(c,"Success Void Bill ", Toast.LENGTH_SHORT).show();
                refresh();
               // setReprint(Doc1No);
            }
        }
        private String voidbill(){
            try {
                z="error";
                JSONObject jsonReq,jsonRes;
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
                if(DBStatus.equals("1")){
                    URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn= Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        String vUpdateHd="update stk_cus_inv_hd set Status2='Void' where Doc1No='"+Doc1No+"' ";
                        Statement stmtHd=conn.createStatement();
                        stmtHd.execute(vUpdateHd);

                        String vUpdateR="update stk_receipt2 set VoidYN='1', CashAmt='0', CC1Amt='0', ChangeAmt='0'," +
                                "BalanceAmount='0', CC1ChargesAmt='0' where Doc1No='"+Doc1No+"' ";
                        Statement stmtR=conn.createStatement();
                        stmtR.execute(vUpdateR);


                        String vUpdateDt="update stk_detail_trn_out set QtyOUT='0' where Doc3No='"+Doc1No+"' ";
                        Statement stmtDt=conn.createStatement();
                        stmtDt.execute(vUpdateDt);
                    }
                }
                String vUpdateHd = "update stk_cus_inv_hd set Status2='Void' where Doc1No='" + Doc1No + "' ";

                String vUpdateR = "update stk_receipt2 set VoidYN='1', CashAmt='0', CC1Amt='0', ChangeAmt='0'," +
                        "BalanceAmount='0', CC1ChargesAmt='0' where Doc1No='" + Doc1No + "' ";

                String vUpdateDt = "update stk_detail_trn_out set QtyOUT='0' where Doc3No='" + Doc1No + "' ";

                if(DBStatus.equals("2")) {
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", vUpdateHd);
                    jsonReq.put("action", "update");
                    String rsHd = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsHd);
                    String resHd = jsonRes.getString("hasil");

                    jsonReq=new JSONObject();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", vUpdateR);
                    jsonReq.put("action", "update");
                    String rsRec = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsRec);
                    String resRec = jsonRes.getString("hasil");

                    jsonReq=new JSONObject();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", vUpdateDt);
                    jsonReq.put("action", "update");
                    String rsDt = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsDt);
                    String resDt = jsonRes.getString("success");
                    z = resDt;
                }else {
                    db.addQuery(vUpdateHd);
                    db.addQuery(vUpdateR);
                    db.addQuery(vUpdateDt);
                    z = "success";
                    db.closeDB();
                }
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return z;
        }
    }

    public void refresh() {
        String DateFrom=txtDateFrom.getText().toString();
        String DateTo=txtDateTo.getText().toString();
        rv=(RecyclerView) view.findViewById(R.id.rvReprint);
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloaderVoid dCustomer=new DownloaderVoid(getActivity(),DateFrom,DateTo,rv,DialogVoid.this);
        dCustomer.execute();
    }
    private void readyPrinter(){
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String query="select TypePrinter, NamePrinter, IPPrinter, " +
                "Port, PaperSize from tb_settingprinter";
        Cursor rsPrint=db.getQuery(query);
        while(rsPrint.moveToNext()){
            TypePrinter = rsPrint.getString(0);
            NamePrinter = rsPrint.getString(1);
            IPPrinter   = rsPrint.getString(2);
            Port        = rsPrint.getString(3);
            PaperSize   = rsPrint.getString(4);
        }
        if(TypePrinter.equals("AIDL")){
            AidlUtil.getInstance().connectPrinterService(getActivity());
            AidlUtil.getInstance().initPrinter();
        }else{

        }
        db.closeDB();


    }
    public void setReprint(String Doc1No){
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        Cursor cur = db.getGeneralSetup();
        while (cur.moveToNext()) {
            CurCode = cur.getString(1);
            vPostGlobalTaxYN = cur.getString(6);
        }
        String stringPrint="";
       /* if (TypePrinter.equals("AIDL")) {
            stringPrint = fngenerate58(Doc1No);
        }else if (TypePrinter.equals("Ipos AIDL")) {
            IposAidlUtil.getInstance().connectPrinterService(getActivity());
            stringPrint=fngenerate58(Doc1No);
        } else {
            stringPrint= fngenerate78(Doc1No);
        }*/
        if(PaperSize.equals("78mm")){
            stringPrint = fngenerate78(Doc1No);
        }else if(PaperSize.equals("58mm")){
            stringPrint = fngenerate58(Doc1No);
        }

        if (TypePrinter.equals("AIDL")) {
            byte[] bytes = stringPrint.getBytes();
            String content2 = BytesUtil.getHexStringFromBytes(bytes);
            AidlUtil.getInstance().sendRawData(BytesUtil.getBytesFromHexString(content2));
        }else if (TypePrinter.equals("Ipos AIDL")) {
            IposAidlUtil.getInstance().setPrint(stringPrint);
        } else if (TypePrinter.equals("Bluetooth")) {
            BluetoothPrinter fncheck = new BluetoothPrinter();
            isBT = fncheck.fnBluetooth(getActivity(), NamePrinter, stringPrint);
            if (isBT == false) {
                NewDoc = "error print";
            }
        } else if (TypePrinter.equals("Bluetooth Zebra")) {
            //Log.d("STR PRINT"+NamePrinter,stringPrint);
            BluetoothZebra fncheck = new BluetoothZebra();
            isBT = fncheck.fnBluetooth(getActivity(), NamePrinter, stringPrint);
            if (isBT == false) {
                NewDoc = "error print";
            }
        } else if (TypePrinter.equals("Wifi")) {
            int iPort =Integer.parseInt(Port);
            PrintingWifi fnprintw = new PrintingWifi();
            isBT = fnprintw.fnprintwifi(getActivity(), IPPrinter, iPort, stringPrint);
            if (isBT == false) {
                NewDoc = "error print";
            }
        } else if (TypePrinter.equals("USB")) {
            PrintingUSB fnprintu = new PrintingUSB();
            isBT = fnprintu.fnprintusb(getActivity(), stringPrint);
            if (isBT == false) {
                NewDoc = "error print";
            }
        } else {
            //No Printer
        }
        db.closeDB();
        dismiss();
    }

    private String fngenerate58(String Doc1No) {
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
        String CusCode = "";
        String TelCus = "";
        String AdddresCus = "";
        String SalesPersonCode = "";
        String D_ateTime = "";
        String Disc2 = "";
        String Minus2 = "";
        String vCC1No = "";
        String vLine11_1="";
        String CusName="";
        String datedTime="";
        String TableNo="";
        DBAdapter db = new DBAdapter(getActivity());
        db.openDB();

        String Company = "select CurCode, CompanyName, CompanyCode, GSTNo, Address," +
                " Tel1, Fax1, CompanyEmail, ComTown, ComState, ComCountry FROM companysetup ";
        Cursor rsCom = db.getQuery(Company);
        while (rsCom.moveToNext()) {
            CompanyName = rsCom.getString(1);
            CompanyCode = rsCom.getString(2);
            GSTNo = rsCom.getString(3);
            Address = rsCom.getString(4);
            Tel = rsCom.getString(5);
            Fax = rsCom.getString(6);
            ComTown = rsCom.getString(8);
            ComState = rsCom.getString(9);
            ComCountry = rsCom.getString(10);

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
        vLine5 = str_pad(Tel, 32, " ", "STR_PAD_BOTH");
        vLine6 = str_pad(Fax, 32, " ", "STR_PAD_BOTH");

        if(!GSTNo.equals("NO")) {
            vLine7 = str_pad("TAX INVOICE (VOID)", 32, " ", "STR_PAD_BOTH");
        } else {
            vLine7 = str_pad("INVOICE (VOID)", 32, " ", "STR_PAD_BOTH");
        }
        //end query header

        //start query customer
        String qCustomer="select H.D_ateTime,D.AnalysisCode2, C.CusName from stk_cus_inv_hd H" +
                " inner join stk_cus_inv_dt D on H.Doc1No=D.Doc1No inner join customer C ON H.CusCode=C.CusCode " +
                " where H.Doc1No='"+Doc1No+"' Group By H.Doc1No  ";
        Cursor rsCus=db.getQuery(qCustomer);
        while(rsCus.moveToNext()){
            datedTime=rsCus.getString(0);
            TableNo=rsCus.getString(1);
            CusName=rsCus.getString(2);
        }
       //
        vLine9 = str_pad("Bill #  : " + Doc1No, 32, " ", "STR_PAD_RIGHT");
        vLine11 = str_pad(datedTime, 18, " ", "STR_PAD_RIGHT");
        if (TableNo.equals("0")) {
            vLine11_1 = str_pad("", 13, " ", "STR_PAD_RIGHT");
        }else {
            vLine11_1 = str_pad("Table No #: "+TableNo, 13, " ", "STR_PAD_RIGHT");
        }

        // String qCustomer="select C.CusCode,CusName, f";
        vLine12 = str_pad("BILL TO : "+ CusName, 32, " ", "STR_PAD_RIGHT");
        //vLine13 = str_pad(CurCode+" ", 32, " ", "STR_PAD_LEFT");
        String vLine131=str_pad("Qty", 7, " ", "STR_PAD_RIGHT");
        String vLine132=str_pad("Price", 11, " ", "STR_PAD_BOTH");
        String vLine133=str_pad("Amount ("+CurCode+")", 14, " ", "STR_PAD_LEFT");

        //end query customer
        strPrint += vLine1+"\n";
        strPrint += vLine2+"\n";
        strPrint += vLine3+"\n";
        strPrint += vLine4+"\n";
        strPrint += "________________________________\n";
        strPrint += vLine7+"\n";
        strPrint += "________________________________\n";
        //end header

        //customer
        strPrint += vLine9+"\n";
        strPrint += vLine11+vLine11_1+"\n";
        strPrint += vLine12+"\n";
        strPrint += "________________________________\n";
        strPrint += vLine131+vLine132+vLine133+"\n";
        strPrint += "________________________________\n";
        //end customer

        //detail
        String qDetail="select ItemCode,IFNULL(Qty,0) as Qty,UOM,DetailTaxCode," +
                "substr(Description,1,45) as Description,IFNULL(HCUnitCost,0) as HCUnitCost," +
                "IFNULL(HCLineAmt,0) AS HCLineAmt,IFNULL(DisRate1,0) as DisRate1,IFNULL(HCDiscount,0) as HCDiscount " +
                "from stk_cus_inv_dt Where Doc1No='"+Doc1No+"' ";
        Cursor rsDt=db.getQuery(qDetail);
        while (rsDt.moveToNext()) {
            String ItemCode         = rsDt.getString(0);
            Double dQty             = rsDt.getDouble(1);
            String Qty              = String.format(Locale.US, "%,.2f",dQty );
            String UOM              = rsDt.getString(2);
            String DetailTaxCode    = rsDt.getString(3);
            String Description      = rsDt.getString(4);
            Double dHCUnitCost      =rsDt.getDouble(5);
            String HCUnitCost       = String.format(Locale.US, "%,.2f",dHCUnitCost);
            Double dHCLineAmt       = rsDt.getDouble(6);
            String HCLineAmt        = String.format(Locale.US, "%,.2f",dHCLineAmt);
            Double dDisRate1        = rsDt.getDouble(7);
            String DisRate1         = String.format(Locale.US, "%,.2f",dDisRate1);
            Double dHCDiscount      = rsDt.getDouble(8);
            String HCDiscount       = String.format(Locale.US, "%,.2f",dHCDiscount);
            if(!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")){
                Disc2		    ="("+HCDiscount+")";
            }else{
                Disc2			=" ";
            }

            vLine14                 =str_pad(Description, 32, " ", "STR_PAD_RIGHT");
            String vLine15_1        =str_pad(Qty, 4, " ", "STR_PAD_LEFT");
            String vLine15_2        =str_pad(HCUnitCost, 9, " ", "STR_PAD_LEFT");
            String vLine15_3        =str_pad(Disc2, 6, " ", "STR_PAD_LEFT");
            String vLine15_4        =str_pad(HCLineAmt, 10, " ", "STR_PAD_LEFT");
            strPrint 				+= vLine14+"\n";
            strPrint 				+= ""+vLine15_1+" x"+vLine15_2+vLine15_3+vLine15_4+"\n";

        }
        //end detail

        //start Total
        // String qTotal="select round(H.HCNetAmt,2)as HCNetAmt,round(H.HCDtTax,2) as HCDtTax,round(R.CashAmt,2) as CashAmt, round(R.ChangeAmt,2) as ChangeAmt,round(sum(D.HCLineAmt)-sum(D.HCTax),2) as AmtExTax,round(R.BalanceAmount,2) as BalanceAmount from stk_cus_inv_hd H inner join stk_receipt2 R on R.Doc1No=H.Doc1No inner join stk_cus_inv_dt D ON D.Doc1No=H.Doc1No where H.Doc1No='"+Doc1No+"' Group By H.Doc1No ";
        String qTotal="select IFNULL(H.HCNetAmt,0)as HCNetAmt,IFNULL(H.HCDtTax,0) as HCDtTax," +
                "IFNULL(R.CashAmt + R.ChangeAmt, 0) as CashAmt, IFNULL(R.ChangeAmt,0) as ChangeAmt," +
                "IFNULL(sum(D.HCLineAmt)-sum(D.HCTax),0) as AmtExTax,IFNULL(R.BalanceAmount,0) as BalanceAmount," +
                " IFNULL(R.CC1Amt,0) as CC1Amt, R.CC1No, R.CC1Code, IFNULL(Sum(D.Qty),0) as ItemTender " +
                "from stk_cus_inv_hd H inner join stk_receipt2 R on R.Doc1No=H.Doc1No inner join stk_cus_inv_dt D ON D.Doc1No=H.Doc1No where H.Doc1No='"+Doc1No+"' Group By H.Doc1No ";
        Cursor rsTot=db.getQuery(qTotal);
        while (rsTot.moveToNext()) {
            Double dTotAmt      =rsTot.getDouble(0);
            String TotAmt       = String.format(Locale.US, "%,.2f",dTotAmt);
            Double dTaxAmt      =rsTot.getDouble(1);
            String TaxAmt       =String.format(Locale.US, "%,.2f",dTaxAmt);
            Double dPayAmt      =rsTot.getDouble(2);
            String PayAmt       =String.format(Locale.US, "%,.2f",dPayAmt);
            Double dChAmt       =rsTot.getDouble(3);
            String ChAmt        =String.format(Locale.US, "%,.2f",dChAmt);
            Double dAmtExtTax   =rsTot.getDouble(4);
            String AmtExtTax    =String.format(Locale.US, "%,.2f",dAmtExtTax);
            Double dBalAmt      =rsTot.getDouble(5);
            String BalAmt       =String.format(Locale.US, "%,.2f",dBalAmt);
            Double dCC1Amt       =rsTot.getDouble(6);
            String CC1Amt       =String.format(Locale.US, "%,.2f",dCC1Amt);
            if(dCC1Amt>0) {
                String tCC1No   =rsTot.getString(7);
                vCC1No          = tCC1No.substring(tCC1No.length() -4);
            }
            String CC1Code      =rsTot.getString(8);
            Double dItemTender  =rsTot.getDouble(9);
            String ItemTender   =String.format(Locale.US, "%,.2f",dItemTender);
            vLine16             =str_pad(AmtExtTax, 11, " ", "STR_PAD_LEFT");
            vLine17             =str_pad(TaxAmt, 11, " ", "STR_PAD_LEFT");
            vLine18             =str_pad("0.00", 11, " ", "STR_PAD_LEFT");
            vLine19             =str_pad(TotAmt, 11, " ", "STR_PAD_LEFT");
            vLine20             =str_pad(PayAmt, 11, " ", "STR_PAD_LEFT");
            vLine21             =str_pad(ChAmt, 11, " ", "STR_PAD_LEFT");
            vLine22             =str_pad(BalAmt, 11, " ", "STR_PAD_LEFT");
            String vLine22_2    =str_pad(ItemTender, 11, " ", "STR_PAD_LEFT");
            String vLine22_3    =str_pad(CC1Amt, 11, " ", "STR_PAD_LEFT");
            String vLine22_4    =str_pad("xxxxx"+vCC1No, 11, " ", "STR_PAD_LEFT");
            String tCC1Code     =str_pad(CC1Code, 19, " ", "STR_PAD_RIGHT");
            strPrint 			+= "________________________________\n\n";
            if(!GSTNo.equals("NO")) {
                strPrint += "Amount Exc Tax    : " + vLine16 + "\n";
                strPrint += "Add Total GST Amt : " + vLine17 + "\n";
                strPrint += "Rounding          : " + vLine18 + "\n";
                strPrint += "Total Amount Due  : " + vLine19 + "\n";
                strPrint += "Paid Amount       : " + vLine20 + "\n";
                if(dPayAmt>0) {
                    strPrint += "Paid Amount       : " + vLine20 + "\n";
                    strPrint += "Changed Amount    : " + vLine21 + "\n";
                }else{
                    strPrint += tCC1Code+":"+vLine22_3+"\n";
                    strPrint += "Credit Card No    : " + vLine22_4 + "\n";
                }
            }else{
                strPrint += "Total Amt Payable : " + vLine19 + "\n";
                if(dPayAmt>0) {
                    strPrint += "Paid Cash Amt     : " + vLine20 + "\n";
                    strPrint += "Changed Amount    : " + vLine21 + "\n";
                }
                if(dCC1Amt>0){
                    strPrint += tCC1Code+":"+vLine22_3+"\n";
                    strPrint += "Credit Card No    : " + vLine22_4 + "\n";
                }
                strPrint += "Total Qty Tender  : " + vLine22_2 + "\n";

            }

            if(dBalAmt>0){
                strPrint += "Balance Amount    : "+vLine22+"\n";
            }
            if(!GSTNo.equals("NO")) {
                strPrint += "________________________________\n";
                strPrint += "GST Summary \n";
                strPrint += "Code  Rate   Goods Amt  GST Amt\n";
            }
        }
        //end total
        //start GST
        String qGST="select IFNULL(TaxRate1,0)as TaxRate1,DetailTaxCode,IFNULL(sum(HCLineAmt)-sum(HCTax),0) as GoodAmt," +
                "IFNULL(sum(HCTax),0) as GSTAmt from stk_cus_inv_dt  Where Doc1No='"+Doc1No+"' Group By DetailTaxCode  ";
        Cursor rsGST=db.getQuery(qGST);
        while (rsGST.moveToNext()) {
            Double dTaxRate      =rsGST.getDouble(0);
            String TaxRate       =String.format(Locale.US, "%,.2f",dTaxRate);
            String TaxCode       =rsGST.getString(1);
            Double dGoodAmt      =rsGST.getDouble(2);
            String GoodAmt       =String.format(Locale.US, "%,.2f",dGoodAmt);
            Double dGSTAmt       =rsGST.getDouble(3);
            String GSTAmt        =String.format(Locale.US, "%,.2f",dGSTAmt);
            String vLine23_1     =str_pad(TaxCode, 4, " ", "STR_PAD_LEFT");
            String vLine23_2     =str_pad(TaxRate, 6, " ", "STR_PAD_LEFT");
            String vLine23_3     =str_pad(GoodAmt, 11, " ", "STR_PAD_LEFT");
            String vLine23_4     =str_pad(GSTAmt, 11, " ", "STR_PAD_LEFT");
            vLine23              =vLine23_1+vLine23_2+vLine23_3+vLine23_4;
            if(!GSTNo.equals("NO")) {
                strPrint += vLine23 + "\n";
            }
        }
        //end GST
        strPrint 				+= "________________________________\n";
        strPrint 				+= "*Goods sold non-returnable & non-refundable\n";
        strPrint 				+= "Thank you, please come again! \n";
        strPrint 				+= "________________________________\n \n\n";

        Log.d("RESULT",strPrint);
       // stringPrint=strPrint;
        return strPrint;
    }

    private String fngenerate78(String Doc1No){
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
        String TelCus="";
        String AdddresCus="";
        String SalesPersonCode="";
        String D_ateTime="";
        String Disc2="";
        String Minus2="";
        String vCC1No="";
        String vLine11_1="";
        String CusName="";
        String datedTime="";
        String TableNo="";
        DBAdapter db = new DBAdapter(getActivity());
        db.openDB();

        String Company="select CurCode,CompanyName,CompanyCode,GSTNo,Address,Tel1,Fax1,CompanyEmail,ComTown,ComState,ComCountry FROM companysetup ";
        Cursor rsCom=db.getQuery(Company);
        while (rsCom.moveToNext()) {
            CompanyName = rsCom.getString(1);
            CompanyCode = rsCom.getString(2);
            GSTNo       = rsCom.getString(3);
            Address     = rsCom.getString(4);
            Tel         = rsCom.getString(5);
            Fax         = rsCom.getString(6);
            ComTown     = rsCom.getString(8);
            ComState    = rsCom.getString(9);
            ComCountry  = rsCom.getString(10);

        }
        vLine1   =str_pad(CompanyName, 48, " ", "STR_PAD_BOTH");
        vLine2   =str_pad("Co.Reg.No.: "+CompanyCode, 48, " ", "STR_PAD_BOTH");
        vLine3   =str_pad("GST Reg.No.: "+GSTNo, 48, " ", "STR_PAD_BOTH");
        String AddressAll = Address.replaceAll("(.{48})", "$1\n");
        String [] address=AddressAll.split("\n");
        String vAddress="";
        for(String add:address){
            vAddress += str_pad(add,48," ", "STR_PAD_BOTH");
        }
        vLine4=vAddress;
        //vLine4   =str_pad(AddressAll, 48, " ", "STR_PAD_BOTH");
        vLine5   =str_pad("Tel.:"+Tel, 48, " ", "STR_PAD_BOTH");
        vLine6   =str_pad(Fax, 48, " ", "STR_PAD_BOTH");
        if(!GSTNo.equals("NO")) {
            vLine7 = str_pad("TAX INVOICE (VOID)", 48, " ", "STR_PAD_BOTH");
        }else{
            vLine7 = str_pad("INVOICE (VOID)", 48, " ", "STR_PAD_BOTH");
        }

        //start query customer
        String qCustomer="select H.D_ateTime,D.AnalysisCode2, C.CusName from stk_cus_inv_hd H" +
                " inner join stk_cus_inv_dt D on H.Doc1No=D.Doc1No inner join customer C ON H.CusCode=C.CusCode " +
                " where H.Doc1No='"+Doc1No+"' Group By H.Doc1No  ";
        Cursor rsCus=db.getQuery(qCustomer);
        while(rsCus.moveToNext()){
            datedTime=rsCus.getString(0);
            TableNo=rsCus.getString(1);
            CusName=rsCus.getString(2);
        }
        //

        vLine8  =str_pad("Bill #: "+Doc1No, 48, " ", "STR_PAD_RIGHT");
        vLine11 = str_pad(datedTime, 24, " ", "STR_PAD_RIGHT");
        if (TableNo.equals("0")) {
            vLine11_1 = str_pad("", 24, " ", "STR_PAD_RIGHT");
        }else {
            vLine11_1 = str_pad("Table No #: "+TableNo, 24, " ", "STR_PAD_RIGHT");
        }
        vLine12 =str_pad("BILL TO : "+CusName , 48, " ", "STR_PAD_RIGHT");
        //vLine13=str_pad(CurCode+" ", 48, " ", "STR_PAD_LEFT");
        String vLine131=str_pad("Quantity", 9, " ", "STR_PAD_RIGHT");
        String vLine132=str_pad("Unit Price", 19, " ", "STR_PAD_BOTH");
        String vLine133=str_pad("Amount ("+CurCode+")", 20, " ", "STR_PAD_LEFT");
        //end query customer

        strPrint += vLine1+"\n";
        strPrint += vLine2+"\n";
        strPrint += vLine3+"\n";
        strPrint += vLine4+"\n";
        if(!Tel.equals("")) {
            strPrint += vLine5 + "\n";
        }
        if(!Fax.equals("")){
            // strPrint += vLine6+"\n";
        }
        strPrint += "________________________________________________\n";
        strPrint += vLine7+"\n";
        strPrint += "________________________________________________\n";
        //end header

        //customer
        strPrint += vLine8+"\n";
        //strPrint += vLine9+"\n";
        //strPrint += vLine10+"\n";
        strPrint += vLine11+vLine11_1+"\n";
        strPrint += vLine12+"\n";
        strPrint += "________________________________________________\n";
        strPrint += vLine131+vLine132+vLine133+"\n";
        strPrint += "________________________________________________\n";
        //end customer

        //detail
        String qDetail="select ItemCode,IFNULL(Qty,0) as Qty,UOM,DetailTaxCode,substr(Description,1,45) as Description," +
                "IFNULL(HCUnitCost,0) as HCUnitCost,IFNULL(HCLineAmt,0) AS HCLineAmt,IFNULL(DisRate1,0) as DisRate1," +
                "IFNULL(HCDiscount,0) as HCDiscount from stk_cus_inv_dt Where Doc1No='"+Doc1No+"' ";
        // Log.d("QUERY",qDetail);
        Cursor rsDt=db.getQuery(qDetail);
        // int numrows=rsDt.getCount();
        //Log.d("NUMROWS",String.valueOf(numrows));
        while (rsDt.moveToNext()) {
            String ItemCode         = rsDt.getString(0);
            Double dQty             = rsDt.getDouble(1);
            String Qty              = String.format(Locale.US, "%,.2f",dQty );
            String UOM              = rsDt.getString(2);
            String DetailTaxCode    = rsDt.getString(3);
            String Description      = rsDt.getString(4);
            Double dHCUnitCost      =rsDt.getDouble(5);
            String HCUnitCost       = String.format(Locale.US, "%,.2f",dHCUnitCost);
            Double dHCLineAmt       = rsDt.getDouble(6);
            String HCLineAmt        = String.format(Locale.US, "%,.2f",dHCLineAmt);
            Double dDisRate1        = rsDt.getDouble(7);
            String DisRate1         = String.format(Locale.US, "%,.2f",dDisRate1);
            Double dHCDiscount      = rsDt.getDouble(8);
            String HCDiscount       = String.format(Locale.US, "%,.2f",dHCDiscount);
            if(!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")){
                Disc2		    ="("+HCDiscount+")";
            }else{
                Disc2			=" ";
            }

            vLine14                 =str_pad(Description, 48, " ", "STR_PAD_RIGHT");
            String vLine15_1        =str_pad(Qty, 6, " ", "STR_PAD_LEFT");
            String vLine15_2        =str_pad(HCUnitCost, 13, " ", "STR_PAD_LEFT");
            String vLine15_3        =str_pad(Disc2, 12, " ", "STR_PAD_LEFT");
            String vLine15_4        =str_pad(HCLineAmt, 15, " ", "STR_PAD_LEFT");
            strPrint 				+= vLine14+"\n";
            strPrint 				+= ""+vLine15_1+" x"+vLine15_2+vLine15_3+vLine15_4+"\n";

        }
        //end detail

        //start Total
        String qTotal="select IFNULL(H.HCNetAmt,0)as HCNetAmt,IFNULL(H.HCDtTax,0) as HCDtTax," +
                "IFNULL(R.CashAmt + R.ChangeAmt, 0) as CashAmt, IFNULL(R.ChangeAmt,0) as ChangeAmt," +
                "IFNULL(sum(D.HCLineAmt)-sum(D.HCTax),0) as AmtExTax,IFNULL(R.BalanceAmount,0) as BalanceAmount," +
                " IFNULL(R.CC1Amt,0) as CC1Amt, R.CC1No, R.CC1Code, IFNULL(Sum(D.Qty),0) as ItemTender " +
                "from stk_cus_inv_hd H inner join stk_receipt2 R on R.Doc1No=H.Doc1No inner join stk_cus_inv_dt D ON D.Doc1No=H.Doc1No where H.Doc1No='"+Doc1No+"' Group By H.Doc1No ";
        Log.d("QUERY",qTotal);
        Cursor rsTot=db.getQuery(qTotal);
        while (rsTot.moveToNext()) {
            Double dTotAmt      =rsTot.getDouble(0);
            String TotAmt       = String.format(Locale.US, "%,.2f",dTotAmt);
            Double dTaxAmt      =rsTot.getDouble(1);
            String TaxAmt       =String.format(Locale.US, "%,.2f",dTaxAmt);
            Double dPayAmt      =rsTot.getDouble(2);
            String PayAmt       =String.format(Locale.US, "%,.2f",dPayAmt);
            Double dChAmt       =rsTot.getDouble(3);
            String ChAmt        =String.format(Locale.US, "%,.2f",dChAmt);
            Double dAmtExtTax   =rsTot.getDouble(4);
            String AmtExtTax    =String.format(Locale.US, "%,.2f",dAmtExtTax);
            Double dBalAmt      =rsTot.getDouble(5);
            String BalAmt       =String.format(Locale.US, "%,.2f",dBalAmt);
            Double dCC1Amt       =rsTot.getDouble(6);
            String CC1Amt       =String.format(Locale.US, "%,.2f",dCC1Amt);
            if(dCC1Amt>0) {
                String tCC1No   =rsTot.getString(7);
                vCC1No          = tCC1No.substring(tCC1No.length() -4);
            }
            String CC1Code       =rsTot.getString(8);
            Double dItemTender  =rsTot.getDouble(9);
            String ItemTender   =String.format(Locale.US, "%,.2f",dItemTender);
            vLine16             =str_pad(AmtExtTax, 22, " ", "STR_PAD_LEFT");
            vLine17             =str_pad(TaxAmt, 22, " ", "STR_PAD_LEFT");
            vLine18             =str_pad("0.00", 22, " ", "STR_PAD_LEFT");
            vLine19             =str_pad(TotAmt, 22, " ", "STR_PAD_LEFT");
            vLine20             =str_pad(PayAmt, 22, " ", "STR_PAD_LEFT");
            vLine21             =str_pad(ChAmt, 22, " ", "STR_PAD_LEFT");
            vLine22             =str_pad(BalAmt, 22, " ", "STR_PAD_LEFT");
            String vLine22_2    =str_pad(ItemTender, 22, " ", "STR_PAD_LEFT");
            String vLine22_3    =str_pad(CC1Amt, 22, " ", "STR_PAD_LEFT");
            String vLine22_4    =str_pad("xxxxxxxxxxx"+vCC1No, 22, " ", "STR_PAD_LEFT");
            String tCC1Code     =str_pad(CC1Code, 24, " ", "STR_PAD_RIGHT");
            strPrint            += "________________________________________________\n\n";
            if(!GSTNo.equals("NO")) {
                strPrint += "Amount Exc Tax         : " + vLine16 + "\n";
                strPrint += "Add Total GST Amount   : " + vLine17 + "\n";
                strPrint += "Rounding               : " + vLine18 + "\n";
                strPrint += "Total Amount Due       : " + vLine19 + "\n";
                if(dCC1Amt>0){
                    strPrint += tCC1Code+vLine22_3+"\n";
                    strPrint += "Credit Card No         : " + vLine22_4 + "\n";
                }else {
                    strPrint += "Paid Amount            : " + vLine20 + "\n";
                    strPrint += "Changed Amount         : " + vLine21 + "\n";
                }
            }else{
                strPrint += "Total Amount Payable   : " + vLine19 + "\n";
                if(dPayAmt>0) {
                    strPrint += "Paid Cash Amount       : " + vLine20 + "\n";
                    strPrint += "Changed Amount         : " + vLine21 + "\n";
                }
                if(dCC1Amt>0){
                    strPrint += tCC1Code+":"+vLine22_3+"\n";
                    strPrint += "Credit Card No         : " + vLine22_4 + "\n";
                }
                strPrint += "Total Quantity Tender  : " + vLine22_2 + "\n";

            }
            if(dBalAmt>0){
                strPrint 		+= "Balance Amount         : "+vLine22+"\n";
            }
            if(!GSTNo.equals("NO")) {
                strPrint          += "________________________________________________\n";
                strPrint          += "GST Summary \n";
                strPrint          += "Tax Code      Rate      Goods Amt      GST Amt  \n";
            }
        }
        //end total
        //start GST
        String qGST="select IFNULL(TaxRate1,0)as TaxRate1,DetailTaxCode,IFNULL(sum(HCLineAmt)-sum(HCTax),0) as GoodAmt," +
                "IFNULL(sum(HCTax),0) as GSTAmt from stk_cus_inv_dt  Where Doc1No='"+Doc1No+"' Group By DetailTaxCode  ";
        Cursor rsGST=db.getQuery(qGST);
        while (rsGST.moveToNext()) {
            Double dTaxRate      =rsGST.getDouble(0);
            String TaxRate       =String.format(Locale.US, "%,.2f",dTaxRate);
            String TaxCode       =rsGST.getString(1);
            Double dGoodAmt       =rsGST.getDouble(2);
            String GoodAmt       =String.format(Locale.US, "%,.2f",dGoodAmt);
            Double dGSTAmt        =rsGST.getDouble(3);
            String GSTAmt       =String.format(Locale.US, "%,.2f",dGSTAmt);
            String vLine23_1     =str_pad(TaxCode, 6, " ", "STR_PAD_LEFT");
            String vLine23_2     =str_pad(TaxRate, 12, " ", "STR_PAD_LEFT");
            String vLine23_3     =str_pad(GoodAmt, 15, " ", "STR_PAD_LEFT");
            String vLine23_4     =str_pad(GSTAmt, 15, " ", "STR_PAD_LEFT");
            if(!GSTNo.equals("NO")) {
                vLine23 = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                strPrint += vLine23 + "\n";
            }
        }
        //end GST
        strPrint                += "________________________________________________\n";
        strPrint 				+= "*Goods sold non-returnable & non-refundable\n";
        strPrint 				+= "Thank you, please come again! \n";
        strPrint                += "________________________________________________\n\n\n";
        return strPrint;
    }

    public String str_pad(String input, int length, String pad, String    sense) {
        int resto_pad = length - input.length();
        String padded = "";

        if (resto_pad <= 0){ return input; }

        if(sense.equals("STR_PAD_RIGHT")) {
            padded  = input;
            padded += _fill_string(pad,resto_pad);
        } else if(sense.equals("STR_PAD_LEFT")) {
            padded  = _fill_string(pad, resto_pad);
            padded += input;
        } else {
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
        if (resto >= pad.length()) {
            for (int i = resto; i >= 0; i = i - pad.length()) {
                if (i  >= pad.length()) {
                    if (first){ padded = pad; } else { padded += pad; }
                } else {
                    if (first){ padded = pad.substring(0, i); } else { padded += pad.substring(0, i); }
                }
                first = false;
            }
        } else {
            padded = pad.substring(0,resto);
        }
        return padded;
    }
}
