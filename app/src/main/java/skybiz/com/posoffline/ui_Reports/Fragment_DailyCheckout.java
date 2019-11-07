package skybiz.com.posoffline.ui_Reports;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.BytesUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothPrinter;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_IposAIDL.Utils.IposAidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_USB.PrintingUSB;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI.PrintingWifi;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 13/12/2017.
 */

public class Fragment_DailyCheckout extends Fragment {
    String IPAddress,UserName,Password,DBName,URL,z,isNum,strPrint,TypePrinter,IPPrinter,NamePrinter,NewDoc,datedPrint,vPort;
    View view;
    Button btnPrint;
    Boolean isBT;
    int i;
    //str to report
    String tCompanyName,tCashSalesAmt,tTotalCashSalesAmt,
            tCashReceiptAmt,tCreditCardAmt,tTotalCashSalesDt,
            tCashCashier,tTotalCashCashier,tNoOfCashReceipt,
            tGSTAmt,tTotalGSTAmt,tNoOfReceipt,
            tCreditNote;
    Double dCashSalesAmt,dTotalCashSalesAmt,dCashReceiptAmt,
            dCreditCardAmt,dTotalCashSalesDt,dCashCashier,
            dTotalCashCashier,dNoOfCashReceipt,dGSTAmt,
            dTotalGSTAmt,dNoOfReceipt,dCreditNote;
    TextView vCompanyName,vDateTimePrint,vCashSalesAmt,
            vTotalCashSalesAmt,vCashReceiptAmt,vCreditCardAmt,
            vTotalCashDt,vCashCashier,vTotalCashCashier,
            vNoOfCashReceipt,vGSTAmt,vTotalGSTAmt,
            vNoOfReceipt,vCreditNote;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dailycheckout, container, false);

        btnPrint=(Button)view.findViewById(R.id.btnPrint);

        vCompanyName=(TextView)view.findViewById(R.id.txtCompanyName);
        vCashSalesAmt=(TextView)view.findViewById(R.id.vCashSalesAmt);
        vDateTimePrint=(TextView)view.findViewById(R.id.vDateTimePrint);
        vTotalCashSalesAmt=(TextView)view.findViewById(R.id.vTotalCashSalesAmt);
        vCashReceiptAmt=(TextView)view.findViewById(R.id.vCashReceipt);
        //vCreditCardAmt=(TextView)view.findViewById(R.id.vCC1);
        vTotalCashDt=(TextView)view.findViewById(R.id.vTotalCashDt);
        vCashCashier=(TextView)view.findViewById(R.id.vCashCashier);
        vTotalCashCashier=(TextView)view.findViewById(R.id.vTotalCashCashier);
        vNoOfReceipt=(TextView)view.findViewById(R.id.vNoOfReceipt);
        vGSTAmt=(TextView)view.findViewById(R.id.vGSTAmt);
        vCreditNote=(TextView)view.findViewById(R.id.vCreditNote);

        SimpleDateFormat DateCurr1 = new SimpleDateFormat("yyyy-MM-dd");
        Date dates = new Date();
        datedPrint = DateCurr1.format(dates);
        DBAdapter db = new DBAdapter(this.getContext());
        db.openDB();
        Cursor cur = db.getGeneralSetup();
        //get generalsetup
        while (cur.moveToNext()) {
            tCompanyName = cur.getString(3);
        }

        Cursor cPrint=db.getSettingPrint();
        while (cPrint.moveToNext()) {
            TypePrinter = cPrint.getString(1);
            NamePrinter = cPrint.getString(2);
            IPPrinter = cPrint.getString(3);
            vPort = cPrint.getString(5);
        }

        db.closeDB();

        vCompanyName.setText(tCompanyName);
        vDateTimePrint.setText(datedPrint);
        FnGenerateDaily(getActivity());
       // FnGenerateDaily fndaily=new FnGenerateDaily(getActivity());
        //fndaily.execute();
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewDoc="success print";
                if(TypePrinter.equals("AIDL")){
                    FnGenerate58();
                    AidlUtil.getInstance().connectPrinterService(getActivity());
                    AidlUtil.getInstance().initPrinter();
                }else if(TypePrinter.equals("Ipos AIDL")){
                    FnGenerate58();
                    IposAidlUtil.getInstance().connectPrinterService(getActivity());
                    IposAidlUtil.getInstance().initPrinter();
                }else{
                    FnGenerate78();
                    Log.d("LAYOUT",strPrint);
                }
                if(TypePrinter.equals("AIDL")) {
                    byte[] bytes = strPrint.getBytes();
                    String content2 = BytesUtil.getHexStringFromBytes(bytes);
                    AidlUtil.getInstance().sendRawData(BytesUtil.getBytesFromHexString(content2));
                }else if(TypePrinter.equals("Ipos AIDL")){
                    //byte[] bytes = strPrint.getBytes();
                    //String content2 = BytesUtil.getHexStringFromBytes(bytes);
                    IposAidlUtil.getInstance().setPrint(strPrint);
                }else if(TypePrinter.equals("Bluetooth")){
                    //Log.d("STR PRINT"+NamePrinter,stringPrint);
                    BluetoothPrinter fncheck=new BluetoothPrinter();
                    isBT= fncheck.fnBluetooth(getActivity(),NamePrinter,strPrint);
                    if(isBT==false){
                        NewDoc="error print";
                    }
                }else if(TypePrinter.equals("Wifi")){
                    PrintingWifi fnprintw=new PrintingWifi();
                    int Port=Integer.parseInt(vPort);
                    isBT=fnprintw.fnprintwifi(getActivity(),IPPrinter, Port, strPrint);
                    if(isBT==false){
                        NewDoc="error print";
                    }
                }else if(TypePrinter.equals("USB")){
                    PrintingUSB fnprintu=new PrintingUSB();
                    isBT=fnprintu.fnprintusb(getActivity(),strPrint);
                    if(isBT==false){
                        NewDoc="error print";
                    }
                }else{
                    //No Printer
                }
                Toast.makeText(getActivity(), "Process "+NewDoc, Toast.LENGTH_LONG).show();
            }
        });


        return view;
    }

    public void  FnGenerate78(){
        tCompanyName=vCompanyName.getText().toString();
        tCashSalesAmt=vCashSalesAmt.getText().toString();
        tCashReceiptAmt=vCashReceiptAmt.getText().toString();
        tCreditCardAmt=vCreditCardAmt.getText().toString();
        tTotalCashSalesDt=vTotalCashDt.getText().toString();
        tCashCashier=vCashCashier.getText().toString();
        tNoOfCashReceipt=vNoOfReceipt.getText().toString();
        tGSTAmt=vGSTAmt.getText().toString();
        tCreditNote=vCreditNote.getText().toString();
        tTotalCashSalesAmt=vTotalCashSalesAmt.getText().toString();
        strPrint="";
        //Toast.makeText(getActivity(), "Cash Sales Amt "+tCashSalesAmt, Toast.LENGTH_LONG).show();
        String vLine1="";
        String vLine2="";
        String vLine3="";
        String vLine4="";
        String vLine5="";
        String vLine6="";
        String vLine7="";
        String vLine8="";
        String vLine9="";
        vLine1          = str_pad(tCompanyName, 48, " ", "STR_PAD_BOTH");
        vLine2          = str_pad("SALES DETAILS", 48, " ", "STR_PAD_RIGHT");
        vLine3          = str_pad(tCashSalesAmt, 18, " ", "STR_PAD_LEFT");
        String vLine3_1 = str_pad(tCreditNote, 18, " ", "STR_PAD_LEFT");
        String vLine3_2 = str_pad(tTotalCashSalesAmt, 18, " ", "STR_PAD_LEFT");
        vLine4          = str_pad(tCashReceiptAmt, 18, " ", "STR_PAD_LEFT");
        vLine5          = str_pad(tCreditCardAmt, 18, " ", "STR_PAD_LEFT");
        vLine6          = str_pad(tTotalCashSalesDt, 18, " ", "STR_PAD_LEFT");
        vLine7          = str_pad(tCashCashier, 18, " ", "STR_PAD_LEFT");
        vLine8          = str_pad(tGSTAmt, 18, " ", "STR_PAD_LEFT");
        vLine9          = str_pad(tNoOfCashReceipt, 18, " ", "STR_PAD_LEFT");
        strPrint        += vLine1+"\n";
        strPrint        += "DAILY CHECKOUT AS AT    : "+datedPrint+"\n\n";
        strPrint        += vLine2+"\n";
        strPrint        += "CASH SALES              : "+  vLine3 +"\n";
        strPrint        += "-CREDIT NOTE            : "+  vLine3_1 +"\n";
        strPrint        += "TOTAL SALES             : "+  vLine3_2 +"\n\n";
        strPrint        += "CASH DETAILS (SYSTEM CALCULATED)\n";
        strPrint        += "CASH RECEIPT            : "+vLine4+"\n";
        strPrint        += "CREDIT CARD             : "+vLine5+"\n";
        strPrint        += "TOTAL                   : "+vLine6+"\n\n";
        strPrint        += "CASH DETAILS (CASHIER CALCULATED)\n";
        strPrint        += "CASH                    : "+vLine7+"\n\n";
        strPrint        += "BILLS ANALYSIS\n";
        strPrint        += "NO OF RECEIPT           : "+vLine9+"\n\n";
        strPrint        += "GST SUMMARY\n";
        strPrint        += "GST AMOUNT              : "+vLine8+"\n\n";
        strPrint        += "________________________________________________\n\n";

    }


    public void  FnGenerate58(){
        strPrint="";
        tCompanyName=vCompanyName.getText().toString();
        tCashSalesAmt=vCashSalesAmt.getText().toString();
        tCashReceiptAmt=vCashReceiptAmt.getText().toString();
        tCreditCardAmt=vCreditCardAmt.getText().toString();
        tTotalCashSalesDt=vTotalCashDt.getText().toString();
        tCashCashier=vCashCashier.getText().toString();
        tNoOfCashReceipt=vNoOfReceipt.getText().toString();
        tGSTAmt=vGSTAmt.getText().toString();
        //Toast.makeText(getActivity(), "Cash Sales Amt "+tCashSalesAmt, Toast.LENGTH_LONG).show();
        String vLine1="";
        String vLine2="";
        String vLine3="";
        String vLine4="";
        String vLine5="";
        String vLine6="";
        String vLine7="";
        String vLine8="";
        String vLine9="";
        vLine1          = str_pad(tCompanyName, 32, " ", "STR_PAD_BOTH");
        vLine2          = str_pad("SALES DETAILS", 32, " ", "STR_PAD_RIGHT");
        vLine3          = str_pad(tCashSalesAmt, 11, " ", "STR_PAD_LEFT");
        vLine4          = str_pad(tCashReceiptAmt, 11, " ", "STR_PAD_LEFT");
        vLine5          = str_pad(tCreditCardAmt, 11, " ", "STR_PAD_LEFT");
        vLine6          = str_pad(tTotalCashSalesDt, 11, " ", "STR_PAD_LEFT");
        vLine7          = str_pad(tCashCashier, 11, " ", "STR_PAD_LEFT");
        vLine8          = str_pad(tGSTAmt, 11, " ", "STR_PAD_LEFT");
        vLine9          = str_pad(tNoOfCashReceipt, 11, " ", "STR_PAD_LEFT");
        strPrint        += vLine1+"\n";
        strPrint        += "DAILY CHECKOUT AS AT:"+datedPrint+"\n\n";
        strPrint        += vLine2+"\n";
        strPrint        += "CASH SALES        : "+  vLine3 +"\n";
        strPrint        += "TOTAL SALES       : "+  vLine3 +"\n\n";
        strPrint        += "CASH DETAILS (SYSTEM)\n";
        strPrint        += "CASH RECEIPT      : "+vLine4+"\n";
        strPrint        += "CREDIT CARD       : "+vLine5+"\n";
        strPrint        += "TOTAL             : "+vLine6+"\n\n";
        strPrint        += "CASH DETAILS (CASHIER)\n";
        strPrint        += "CASH              : "+vLine7+"\n\n";
        strPrint        += "BILLS ANALYSIS\n";
        strPrint        += "NO OF RECEIPT     : "+vLine9+"\n\n";
        strPrint        += "GST SUMMARY\n";
        strPrint        += "GST AMOUNT        : "+vLine8+"\n\n";
        strPrint 		+= "________________________________\n\n";

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


    protected String _fill_string(String pad, int resto )
    {
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


    public void FnGenerateDaily(Context c){
        tGSTAmt             ="0.00";
        tNoOfReceipt        ="0.00";
        tCashSalesAmt       ="0.00";
        tCashReceiptAmt     ="0.00";
        tCreditCardAmt      ="0.00";
        tTotalCashSalesDt   ="0.00";
        tCashCashier        ="0.00";
        tCreditNote         ="0.00";

        DBAdapter db = new DBAdapter(c);
        db.openDB();
        SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat DateCurr1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String datedFrom = DateCurr.format(date)+" 00:00:00";
        String dated = DateCurr.format(date);
        String datedTo = DateCurr1.format(date);
        String qCheck = "select Count(*) as jumlah from stk_cus_inv_hd where D_ate='"+dated+"' ";
        Cursor rs1= db.getQuery(qCheck);
        while (rs1.moveToNext()) {
            isNum = rs1.getString(0);
        }
        if (!isNum.equals("0")){
            String qCashSales = "Select sum(HCNetAmt)as CashSales from stk_cus_inv_hd where DocType='CS' and D_ate='"+dated+"' ";
            Cursor rsCS = db.getQuery(qCashSales);
            while (rsCS.moveToNext()) {
                dCashSalesAmt = rsCS.getDouble(0);
            }

            String qCreditNote = "Select sum(HCNetAmt)as CreditNote from stk_cus_inv_hd where DocType='CusCN' and D_ate='"+dated+"' ";
            Cursor rsCusCN = db.getQuery(qCashSales);
            while (rsCS.moveToNext()) {
                dCreditNote = rsCS.getDouble(0);
            }
            dTotalCashSalesAmt=dCashSalesAmt-dCashSalesAmt;
            tTotalCashSalesAmt =String.format(Locale.US, "%,.2f", dTotalCashSalesAmt);
            tCashSalesAmt =String.format(Locale.US, "%,.2f", dCashSalesAmt);
            String qCashReceipt = "SELECT IFNULL(SUM(rh.CashAmount),0) AS CashAmt  FROM" +
                    " (SELECT SUM(R.CashAmt) AS CashAmount FROM stk_receipt2 R " +
                    " INNER JOIN stk_cus_inv_hd H ON R.Doc1No = H.Doc1No  " +
                    " WHERE H.D_ate='" + dated + "' AND R.CashAmt !=0 AND R.DocType = 'CS' GROUP BY R.Doc1No) rh";
            Log.d("QUERY", qCashReceipt);
            Cursor rsCR = db.getQuery(qCashReceipt);
            while (rsCR.moveToNext()) {
                dCashReceiptAmt = rsCR.getDouble(0);
            }
            tCashReceiptAmt = String.format(Locale.US, "%,.2f", dCashReceiptAmt);
            String qCC1 = "SELECT IFNULL(SUM(R.CC1Amt),0) AS CC1Amt " +
                    " FROM stk_receipt2 R " +
                    " WHERE R.D_ate='" + dated + "' " +
                    " AND R.Cheque1Amt > 0";

            Cursor rsCC1 = db.getQuery(qCC1);
            while (rsCC1.moveToNext()) {
                dCreditCardAmt = rsCC1.getDouble(0);
            }
            tCreditCardAmt = String.format(Locale.US, "%,.2f", dCreditCardAmt);
            dTotalCashSalesDt = (Double.parseDouble(tCashReceiptAmt.replaceAll(",",""))) + (Double.parseDouble(tCreditCardAmt.replaceAll(",","")));
            tTotalCashSalesDt = String.format(Locale.US, "%,.2f", dTotalCashSalesDt);
            //tTotalCashSalesDt=dTotalCashSalesDt.toString();

            String qCashColl = "SELECT Value_100,Value_50,Value_20,Value_10,Value_5,Value_2,Value_1,Value_050,Value_020,Value_010,Value_005,Value_001 ,Value_100000,Value_50000,Value_20000,Value_10000,Value_5000,Value_1000,Value_500,Value_200 from stk_counter_trn  ORDER BY RunNo DESC LIMIT 1";
            Cursor rsColl = db.getQuery(qCashColl);
            while (rsColl.moveToNext()) {
                    String Value_100 = rsColl.getString(0);
                    String Value_50 = rsColl.getString(1);
                    String Value_20 = rsColl.getString(2);
                    String Value_10 = rsColl.getString(3);
                    String Value_5 = rsColl.getString(4);
                    String Value_2 = rsColl.getString(5);
                    String Value_1 = rsColl.getString(6);
                    String Value_050 = rsColl.getString(7);
                    String Value_020 = rsColl.getString(8);
                    String Value_010 = rsColl.getString(9);
                    String Value_005 = rsColl.getString(10);
                    String Value_001 = rsColl.getString(11);
                    Log.d("VALUE", Value_100);
                    dCashCashier = (Double.parseDouble(Value_100)) + (Double.parseDouble(Value_50)) + (Double.parseDouble(Value_20)) + (Double.parseDouble(Value_10)) + (Double.parseDouble(Value_5)) + (Double.parseDouble(Value_2)) + (Double.parseDouble(Value_1)) + (Double.parseDouble(Value_050)) + (Double.parseDouble(Value_020)) + (Double.parseDouble(Value_010)) + (Double.parseDouble(Value_005)) + (Double.parseDouble(Value_001));
                    dCashCashier = Math.round(dCashCashier * 100.00) / 100.00;
                    tCashCashier = String.format(Locale.US, "%,.2f", dCashCashier);
                    //DecimalFormat df    = new DecimalFormat("#.##");
                    // tCashCashier        = df.format(dCashCashier);
            }

            String qNoReceipt = "SELECT IFNULL(COUNT(NOReceipt.NoOfReceipt),0) AS vNoOfReceipt " +
                    " FROM (SELECT COUNT(H.Doc1No) AS NoOfReceipt " +
                    " FROM stk_cus_inv_hd H Inner Join stk_receipt2 R On H.Doc1No = R.Doc1No " +
                    " WHERE H.D_ate='"+dated+"' " +
                    "  GROUP BY H.Doc1No) NOReceipt";
            Cursor rsNoR = db.getQuery(qNoReceipt);
            while (rsNoR.moveToNext()) {
                tNoOfCashReceipt = rsNoR.getString(0);
                dNoOfReceipt = Math.round((Double.parseDouble(tNoOfCashReceipt)) * 100.0) / 100.0;
                tNoOfReceipt = String.format(Locale.US, "%,.2f", dNoOfReceipt);
                //tNoOfReceipt=dNoOfReceipt.toString();
            }

            String qTax = " SELECT IFNULL(SUM(TGst.HCTax),0) AS vHCTax " +
                    " FROM(SELECT SUM(D.HCTax) AS HCTax " +
                    " FROM stk_cus_inv_hd H INNER JOIN stk_cus_inv_dt D ON H.Doc1No = D.Doc1No Inner Join stk_tax T ON D.DetailTaxCode = T.TaxCode " +
                    " INNER JOIN stk_receipt2 R On H.Doc1No = R.Doc1No " +
                    " WHERE H.D_ate='" + dated + "' " +
                    " AND T.GSTTaxCode <> '' Group By H.Doc1No) TGst";

            Cursor rsTax = db.getQuery(qTax);
            while (rsTax.moveToNext()) {
                    tGSTAmt = rsTax.getString(0);
                    dGSTAmt = Math.round((Double.parseDouble(tGSTAmt)) * 100.00) / 100.00;
                    tGSTAmt = String.format(Locale.US, "%,.2f", dGSTAmt);
            }

        }else{

        }
        vCashSalesAmt.setText(tCashSalesAmt);
        vTotalCashSalesAmt.setText(tTotalCashSalesAmt);
        vCashReceiptAmt.setText(tCashReceiptAmt);
        vCreditCardAmt.setText(tCreditCardAmt);
        vTotalCashDt.setText(tTotalCashSalesDt);
        vCashCashier.setText(tCashCashier);
        vTotalCashCashier.setText(tCashCashier);
        vNoOfReceipt.setText(tNoOfReceipt);
        vGSTAmt.setText(tGSTAmt);


    }
    /*public class FnGenerateDaily extends AsyncTask<Void,Void,String>{

        Context c;
        Boolean isSuccess;
        public FnGenerateDaily(Context c) {
            this.c  = c;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            //return this.downloadData();
           // z="error";
            tCashCashier = "0.00";
            try{
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                Cursor curSet = db.getAllSeting();
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(1);
                    UserName = curSet.getString(2);
                    Password = curSet.getString(3);
                    DBName = curSet.getString(4);
                }
                db.closeDB();
                URL="jdbc:mysql://"+IPAddress+"/"+DBName;
                Connection conn= Connector.connect(URL, UserName, Password);
                SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat DateCurr1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String datedFrom = DateCurr.format(date)+" 00:00:00";
                String dated = DateCurr.format(date);
                String datedTo = DateCurr1.format(date);
                JSONArray results = new JSONArray();
                JSONObject row = new JSONObject();
                if(conn==null){
                    z="error";
                }else {

                    String qCheck = "select Count(*) as jumlah from stk_cus_inv_hd where D_ate='"+dated+"' ";
                    Statement stmt1 = conn.createStatement();
                    stmt1.execute(qCheck);
                    ResultSet rs1= stmt1.getResultSet();
                    while (rs1.next()) {
                        isNum = rs1.getString("jumlah");
                    }
                    if (!isNum.equals("0")){
                        String qCashSales = " SELECT FORMAT(SUM(TCashSales.HCNetAmt),2) AS CashSales  FROM (SELECT SUM(H.HCNetAmt) AS HCNetAmt FROM stk_cus_inv_hd H INNER JOIN stk_receipt2 R ON H.Doc1No = R.Doc1No WHERE H.D_ate='" + dated + "' AND H.Status2 <> 'Void' GROUP BY H.Doc1No) TCashSales";
                        Statement stmtCS = conn.createStatement();
                        stmtCS.execute(qCashSales);
                        ResultSet rsCS = stmtCS.getResultSet();
                        while (rsCS.next()) {
                            tCashSalesAmt = rsCS.getString("CashSales");
                        }
                        String qCashReceipt = "SELECT IFNULL(FORMAT(SUM(rh.CashAmount),2),0) AS CashAmt  FROM (SELECT SUM(R.CashAmt) AS CashAmount FROM stk_receipt2 R INNER JOIN stk_cus_inv_hd H ON R.Doc1No = H.Doc1No  WHERE H.D_ate='" + dated + "' AND R.CashAmt !=0 AND R.DocType = 'CS' GROUP BY R.Doc1No) rh";
                        Log.d("QUERY", qCashReceipt);
                        Statement stmtCR = conn.createStatement();
                        stmtCR.execute(qCashReceipt);
                        ResultSet rsCR = stmtCR.getResultSet();
                        while (rsCR.next()) {
                            tCashReceiptAmt = rsCR.getString("CashAmt");
                        }

                        String qCC1 = "SELECT FORMAT(IFNULL(SUM(R.CC1Amt),0),2) AS CC1Amt " +
                                " FROM stk_receipt2 R " +
                                " WHERE R.D_ate='" + dated + "' " +
                                " AND R.Cheque1Amt > 0";
                        Statement stmtCC1 = conn.createStatement();
                        stmtCC1.execute(qCC1);
                        ResultSet rsCC1 = stmtCC1.getResultSet();
                        while (rsCC1.next()) {
                            tCreditCardAmt = rsCC1.getString("CC1Amt");
                        }
                        dTotalCashSalesDt = (Double.parseDouble(tCashReceiptAmt.replaceAll(",",""))) + (Double.parseDouble(tCreditCardAmt.replaceAll(",","")));
                        tTotalCashSalesDt = String.format(Locale.US, "%,.2f", dTotalCashSalesDt);
                        //tTotalCashSalesDt=dTotalCashSalesDt.toString();

                        String qCashColl = "SELECT Value_100,Value_50,Value_20,Value_10,Value_5,Value_2,Value_1,Value_050,Value_020,Value_010,Value_005,Value_001 ,Value_100000,Value_50000,Value_20000,Value_10000,Value_5000,Value_1000,Value_500,Value_200 from stk_counter_trn  ORDER BY RunNo DESC LIMIT 1";
                        Statement stmtColl = conn.createStatement();
                        if (stmtColl.execute(qCashColl)) {
                            ResultSet rsColl = stmtColl.getResultSet();
                            while (rsColl.next()) {
                                String Value_100 = rsColl.getString("Value_100");
                                String Value_50 = rsColl.getString("Value_50");
                                String Value_20 = rsColl.getString("Value_20");
                                String Value_10 = rsColl.getString("Value_10");
                                String Value_5 = rsColl.getString("Value_5");
                                String Value_2 = rsColl.getString("Value_2");
                                String Value_1 = rsColl.getString("Value_1");
                                String Value_050 = rsColl.getString("Value_050");
                                String Value_020 = rsColl.getString("Value_020");
                                String Value_010 = rsColl.getString("Value_010");
                                String Value_005 = rsColl.getString("Value_005");
                                String Value_001 = rsColl.getString("Value_001");
                                Log.d("VALUE", Value_100);
                                dCashCashier = (Double.parseDouble(Value_100)) + (Double.parseDouble(Value_50)) + (Double.parseDouble(Value_20)) + (Double.parseDouble(Value_10)) + (Double.parseDouble(Value_5)) + (Double.parseDouble(Value_2)) + (Double.parseDouble(Value_1)) + (Double.parseDouble(Value_050)) + (Double.parseDouble(Value_020)) + (Double.parseDouble(Value_010)) + (Double.parseDouble(Value_005)) + (Double.parseDouble(Value_001));
                                dCashCashier = Math.round(dCashCashier * 100.00) / 100.00;
                                tCashCashier = String.format(Locale.US, "%,.2f", dCashCashier);
                                //DecimalFormat df    = new DecimalFormat("#.##");
                                // tCashCashier        = df.format(dCashCashier);
                            }
                        }


                        String qNoReceipt = "SELECT FORMAT(IFNULL(COUNT(NOReceipt.NoOfReceipt),0),2) AS vNoOfReceipt " +
                                " FROM (SELECT COUNT(H.Doc1No) AS NoOfReceipt " +
                                " FROM stk_cus_inv_hd H Inner Join stk_receipt2 R On H.Doc1No = R.Doc1No " +
                                " WHERE H.D_ate='"+dated+"' " +
                                " AND H.Status2 <> 'Void' GROUP BY H.Doc1No) NOReceipt";
                        Statement stmtNoR = conn.createStatement();
                        stmtNoR.execute(qNoReceipt);
                        ResultSet rsNoR = stmtNoR.getResultSet();
                        while (rsNoR.next()) {
                            tNoOfCashReceipt = rsNoR.getString("vNoOfReceipt");
                            dNoOfReceipt = Math.round((Double.parseDouble(tNoOfCashReceipt)) * 100.0) / 100.0;
                            tNoOfReceipt = String.format(Locale.US, "%,.2f", dNoOfReceipt);
                            //tNoOfReceipt=dNoOfReceipt.toString();
                        }

                        String qTax = " SELECT IFNULL(SUM(TGst.HCTax),0) AS vHCTax " +
                                " FROM(SELECT SUM(D.HCTax) AS HCTax " +
                                " FROM stk_cus_inv_hd H INNER JOIN stk_cus_inv_dt D ON H.Doc1No = D.Doc1No Inner Join stk_tax T ON D.DetailTaxCode = T.TaxCode " +
                                " INNER JOIN stk_receipt2 R On H.Doc1No = R.Doc1No " +
                                " WHERE H.D_ate='" + dated + "' " +
                                " AND T.GSTTaxCode <> ''  AND H.Status2 <> 'Void' Group By H.Doc1No) TGst";
                        Statement stmtTax = conn.createStatement();
                       if(stmtTax.execute(qTax)) {
                           ResultSet rsTax = stmtTax.getResultSet();
                           while (rsTax.next()) {
                               tGSTAmt = rsTax.getString("vHCTax");
                               dGSTAmt = Math.round((Double.parseDouble(tGSTAmt)) * 100.00) / 100.00;
                               tGSTAmt = String.format(Locale.US, "%,.2f", dGSTAmt);
                           }
                       }else{
                           tGSTAmt="0.00";
                       }
                        row.put("CSAmt", tCashSalesAmt);
                        row.put("CRAmt", tCashReceiptAmt);
                        row.put("CC1Amt", tCreditCardAmt);
                        row.put("TotalCashDt", tTotalCashSalesDt);
                        row.put("CashCashier", tCashCashier);
                        row.put("NoOfReceipt", tNoOfCashReceipt);
                        row.put("GSTAmt", tGSTAmt);

                        results.put(row);
                        Log.d("JSON", results.toString());
                        return results.toString();
                    }else{
                        return null;
                    }
                }
                //return z;
            }catch (SQLException e){
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result==null){
                Toast.makeText(c,"Generate Failed", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(c,"Generate Successful", Toast.LENGTH_SHORT).show();

                try {
                    JSONArray ja=new JSONArray(result);
                    JSONObject jo=null;
                    for (int i=0;i<ja.length();i++) {
                        jo = ja.getJSONObject(i);
                        String CashSalesAmt = jo.getString("CSAmt");
                        String CashReceiptAmt = jo.getString("CRAmt");
                        String CC1Amt = jo.getString("CC1Amt");
                        String TotalCashDt = jo.getString("TotalCashDt");
                        String CashCashier = jo.getString("CashCashier");
                        String NoOfReceipt = jo.getString("NoOfReceipt");
                        String GSTAmt = jo.getString("GSTAmt");

                        vCashSalesAmt.setText(CashSalesAmt);
                        vTotalCashSalesAmt.setText(CashSalesAmt);
                        vCashReceiptAmt.setText(CashReceiptAmt);
                        vCreditCardAmt.setText(CC1Amt);
                        vTotalCashDt.setText(TotalCashDt);
                        vCashCashier.setText(CashCashier);
                        vTotalCashCashier.setText(CashCashier);
                        vNoOfReceipt.setText(NoOfReceipt);
                        vGSTAmt.setText(GSTAmt);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }*/

}
