package skybiz.com.posoffline.ui_CashReceipt.m_GuestCheck;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_NewObject.Encode;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_SO;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.BytesUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothPrinter;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothZebra;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_USB.PrintingUSB;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI.PrintingWifi;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogGuest extends DialogFragment {
    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    TelephonyManager telephonyManager;
    String CurCode,vPostGlobalTaxYN;
    Boolean isBT;
    String TypePrinter,NamePrinter,IPPrinter,Port,PaperSize,NewDoc;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_listso, container, false);
        rv=(RecyclerView) view.findViewById(R.id.list_so);
        getDialog().setTitle("List of Guest Check");
        refresh();
        readyPrinter();
        return view;
    }

    public void refresh() {
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 3);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloaderGuest downloaderGuest=new DownloaderGuest(getActivity(),rv,DialogGuest.this);
        downloaderGuest.execute();
        //adapter=new GuestAdapter(getActivity(),listso,DialogGuest.this);
       // retlistso();
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

    public class  PrintGuest extends AsyncTask<Void,Void,String>{
        Context c;
        String Doc1No,Doc2No,datedTime;
        String IPAddress,Password,DBName,UserName,URL,Port,z,vPort,DBStatus,ItemConn,EncodeType;
        Connection conn=null;

        public PrintGuest(Context c, String doc1No, String doc2No) {
            this.c = c;
            Doc1No = doc1No;
            Doc2No = doc2No;
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
            if(result.equals("error")){
                Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
            }else{
                dismiss();
            }
        }

        private String downloadData(){
            try{
                z="success";
                DBAdapter db=new DBAdapter(getActivity());
                db.openDB();
                Cursor cur = db.getGeneralSetup();
                while (cur.moveToNext()) {
                    CurCode = cur.getString(1);
                    vPostGlobalTaxYN = cur.getString(6);
                }

                String querySet="select ServerName,UserName,Password," +
                        "DBName,Port,DBStatus," +
                        "ItemConn,EncodeType" +
                        " from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(0);
                    UserName = curSet.getString(1);
                    Password = curSet.getString(2);
                    DBName = curSet.getString(3);
                    Port = curSet.getString(4);
                    DBStatus=curSet.getString(5);
                    ItemConn=curSet.getString(6);
                    EncodeType=curSet.getString(7);
                }
                if(DBStatus.equals("1")) {
                    //URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                   // conn = Connector.connect(URL, UserName, Password);
                    conn= Connect_db.getConnection();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                datedTime = sdf.format(date);
                String stringPrint=genPrint();
                if (TypePrinter.equals("AIDL")) {
                   /// stringPrint=fngenerate58(Doc1No,Doc2No,datedTime);
                } else {
                   // stringPrint= fngenerate78(Doc1No,Doc2No,datedTime);
                }

                if (TypePrinter.equals("AIDL")) {
                    AidlUtil.getInstance().printText(stringPrint);
                } else if (TypePrinter.equals("Bluetooth")) {
                    BluetoothPrinter fncheck = new BluetoothPrinter();
                    isBT = fncheck.fnBluetooth(getActivity(), NamePrinter, stringPrint);
                    if (isBT == false) {
                        NewDoc = "error print";
                        z="error";
                    }
                } else if (TypePrinter.equals("Bluetooth Zebra")) {
                    //Log.d("STR PRINT"+NamePrinter,stringPrint);
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
            }catch (SQLiteException e){
                e.printStackTrace();
            }
          return z;
        }

        private String genPrint(){
            String strPrint="";
            JSONObject jsonReq,jsonRes;
            try{

                int len=48;
                int len1=48;
                int len3=24;
                int len3_1=24;
                int len5=9;
                int len5_1=19;
                int len5_2=20;
                int len6=40;
                int len6_1=8;
                int len6_2=35;
                int len7=6;
                int len7_1=13;
                int len7_2=12;
                int len7_3=15;
                int len8=24;
                int len8_1=2;
                int len8_2=22;
                int len14=6;
                int len14_1=12;
                int len14_2=15;
                int len14_3=15;
                if(TypePrinter.equals("AIDL")){
                     len=32;
                     len3=18;
                     len3_1=13;
                     len5=7;
                     len5_1=11;
                     len5_2=14;
                     len6=26;
                     len6_1=6;
                     len6_2=20;
                     len7=4;
                     len7_1=9;
                     len7_2=6;
                     len7_3=10;
                     len8=19;
                     len8_1=2;
                     len8_2=11;
                     len14=4;
                     len14_1=6;
                     len14_2=11;
                     len14_3=11;
                }

                DBAdapter db=new DBAdapter(c);
                db.openDB();

                String GSTNo="";
                String CurCode="";
                String Company = "select CurCode,CompanyName,CompanyCode,GSTNo,Address," +
                        "Tel1,Fax1,CompanyEmail,ComTown,ComState,ComCountry FROM companysetup ";
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
                        GSTNo       = vData.getString("GSTNo");
                        CurCode       = vData.getString("CurCode");
                    }
                }else {
                    Cursor rsCom = db.getQuery(Company);
                    while (rsCom.moveToNext()) {
                        GSTNo = rsCom.getString(3);
                        CurCode = rsCom.getString(0);
                    }
                }

                String CusName="";
                String qCustomer = "select C.CusName, C.CusCode, C.Address " +
                        " from stk_sales_order_hd H inner join customer C ON H.CusCode=C.CusCode " +
                        " where H.Doc1No='" + Doc1No + "'  ";
                if(DBStatus.equals("0")){
                    Cursor rsCus = db.getQuery(qCustomer);
                    while (rsCus.moveToNext()) {
                        CusName = rsCus.getString(0);
                    }
                }else if(DBStatus.equals("1")){
                    if (conn != null) {
                        Statement stmtCus = conn.createStatement();
                        if (stmtCus.execute(qCustomer)) {
                            ResultSet rsCus = stmtCus.getResultSet();
                            while (rsCus.next()) {
                                CusName = rsCus.getString(1);
                            }
                        }
                    }
                }else if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qCustomer);
                    jsonReq.put("action", "select");
                    String rsCus = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsCus);
                    String resCus= jsonRes.getString("hasil");
                    JSONArray jaCus = new JSONArray(resCus);
                    JSONObject vCus = null;
                    for (int i = 0; i < jaCus.length(); i++) {
                        vCus = jaCus.getJSONObject(i);
                        CusName = vCus.getString("CusName");
                    }
                }
                //header
                String vLine = str_pad("_", len, "_", "STR_PAD_BOTH");
                String vLine1 = str_pad("GUEST CHECK", len, " ", "STR_PAD_BOTH");
                String vLine2 = str_pad("Bill #  : " + Doc1No, len, " ", "STR_PAD_RIGHT");
                String vLine3 = str_pad(datedTime, len3, " ", "STR_PAD_RIGHT");
                String vLine3_1="";
                if (Doc2No.equals("0")) {
                    vLine3_1 = str_pad("", len3_1, " ", "STR_PAD_RIGHT");
                } else {
                    vLine3_1 = str_pad("Table #: " + Doc2No, len3_1, " ", "STR_PAD_RIGHT");
                }
                String vLine4   = str_pad("BILL TO : " + CusName, len, " ", "STR_PAD_RIGHT");
                String vLine5   = str_pad("Qty", len5, " ", "STR_PAD_RIGHT");
                String vLine5_1 = str_pad("Price", len5_1, " ", "STR_PAD_BOTH");
                String vLine5_2 = str_pad("Amount (" + CurCode + ")", len5_2, " ", "STR_PAD_LEFT");

                strPrint += vLine+"\n";
                strPrint += vLine1 + "\n";
                strPrint += vLine+"\n";
                strPrint += vLine2 + "\n";
                strPrint += vLine3 + vLine3_1 + "\n";
                strPrint += vLine4 + "\n";
                strPrint += vLine+"\n";
                strPrint += vLine5 + vLine5_1 + vLine5_2 + "\n";
                strPrint += vLine+"\n";
                //detail
                String qDetail = "SELECT ItemCode,IFNULL(Qty,0) as Qty, UOM, DetailTaxCode, SUBSTR(Description,1,"+len6+") as Description, " +
                        "IFNULL(HCUnitCost,0) as HCUnitCost, IFNULL(HCLineAmt,0) as HCLineAmt,IFNULL(DisRate1,0) as DisRate1," +
                        "IFNULL(HCDiscount,0) as HCDiscount, AlternateItem " +
                        "FROM stk_sales_order_dt WHERE Doc1No='" + Doc1No + "' ";
                if(DBStatus.equals("0")){
                    Cursor rsDt=db.getQuery(qDetail);
                    while(rsDt.moveToNext()) {
                        String Disc2            ="";
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
                        String HCDiscount = String.format(Locale.US, "%,.2f", dHCDiscount);
                        if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                            Disc2 = "(" + HCDiscount + ")";
                        } else {
                            Disc2 = " ";
                        }
                        String AlternateItem    = rsDt.getString(9).trim().replaceAll("\\s+", " ");
                        String vLine6           = str_pad(Description, len6, " ", "STR_PAD_RIGHT");
                        String vLine6_1         = str_pad(UOM, len6_1, " ", "STR_PAD_RIGHT");
                        String vLine6_2         = str_pad(AlternateItem, len6_2, " ", "STR_PAD_RIGHT");
                        String vLine6_3         = str_pad("", len6_1, " ", "STR_PAD_RIGHT");
                        String vLine7           = str_pad(Qty, len7, " ", "STR_PAD_LEFT");
                        String vLine7_1         = str_pad(HCUnitCost, len7_1, " ", "STR_PAD_LEFT");
                        String vLine7_2         = str_pad(Disc2, len7_2, " ", "STR_PAD_LEFT");
                        String vLine7_3         = str_pad(HCLineAmt, len7_3, " ", "STR_PAD_LEFT");
                        strPrint += vLine6+vLine6_1 + "\n";
                        if(AlternateItem.length()>1){
                            strPrint            += vLine6_2+vLine6_3+"\n";
                        }

                        if(HCUnitCost.length()>12 || HCLineAmt.length()>12) {
                            strPrint    += "" + vLine7 + " x" + vLine7_1 + vLine7_2  + "\n";
                            strPrint    += str_pad(HCLineAmt, 31, " ", "STR_PAD_LEFT") + "\n";

                        }else{
                            strPrint += "" + vLine7 + " x" + vLine7_1 + vLine7_2 + vLine7_3 + "\n";
                        }
                       // strPrint += "" + vLine7 + " x" + vLine7_1 + vLine7_2 + vLine7_3 + "\n";
                    }
                }else if(DBStatus.equals("1")){
                    if (conn != null) {
                        String qDetail2 = "SELECT D.ItemCode,IFNULL(D.Qty,0) as Qty, D.UOM, D.DetailTaxCode, SUBSTR(D.Description,1,"+len6+") as Description, " +
                                "IFNULL(D.HCUnitCost,0) as HCUnitCost, IFNULL(D.HCLineAmt,0) as HCLineAmt,IFNULL(D.DisRate1,0) as DisRate1," +
                                "IFNULL(D.HCDiscount,0) as HCDiscount, SUBSTR(M.AlternateItem,1,"+len6_2+") as AlternateItem " +
                                "FROM stk_sales_order_dt D inner join stk_master M on D.ItemCode=M.ItemCode " +
                                "WHERE D.Doc1No='" + Doc1No + "' ";
                        Statement stmtDt = conn.createStatement();
                        stmtDt.executeQuery("SET NAMES 'LATIN1'");
                        stmtDt.executeQuery("SET CHARACTER SET 'LATIN1'");
                        if (stmtDt.execute(qDetail2)) {
                            ResultSet rsDt = stmtDt.getResultSet();
                            while (rsDt.next()) {
                                String Disc2            ="";
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
                                String HCDiscount = String.format(Locale.US, "%,.2f", dHCDiscount);
                                if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                                    Disc2 = "(" + HCDiscount + ")";
                                } else {
                                    Disc2 = " ";
                                }
                                String AlternateItem    = Encode.setChar(EncodeType,rsDt.getString(10).trim().replaceAll("\\s+", " "));
                                String vLine6           = str_pad(Description, len6, " ", "STR_PAD_RIGHT");
                                String vLine6_1         = str_pad(UOM, len6_1, " ", "STR_PAD_RIGHT");
                                String vLine6_2         = str_pad(AlternateItem, len6_2, " ", "STR_PAD_RIGHT");
                                String vLine6_3         = str_pad("", len6_1, " ", "STR_PAD_RIGHT");
                                String vLine7           = str_pad(Qty, len7, " ", "STR_PAD_LEFT");
                                String vLine7_1         = str_pad(HCUnitCost, len7_1, " ", "STR_PAD_LEFT");
                                String vLine7_2         = str_pad(Disc2, len7_2, " ", "STR_PAD_LEFT");
                                String vLine7_3         = str_pad(HCLineAmt, len7_3, " ", "STR_PAD_LEFT");
                                strPrint += vLine6+vLine6_1 + "\n";
                                if(AlternateItem.length()>1){
                                    strPrint            += vLine6_2+vLine6_3+"\n";
                                }
                                if(HCUnitCost.length()>12 || HCLineAmt.length()>12) {
                                    strPrint    += "" + vLine7 + " x" + vLine7_1 + vLine7_2  + "\n";
                                    strPrint    += str_pad(HCLineAmt, 31, " ", "STR_PAD_LEFT") + "\n";

                                }else{
                                    strPrint += "" + vLine7 + " x" + vLine7_1 + vLine7_2 + vLine7_3 + "\n";
                                }
                                //strPrint += "" + vLine7 + " x" + vLine7_1 + vLine7_2 + vLine7_3 + "\n";
                            }
                        }
                    }

                }else if(DBStatus.equals("2")){
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
                        String Disc2            ="";
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
                        if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                            Disc2 = "(" + HCDiscount + ")";
                        } else {
                            Disc2 = " ";
                        }
                        String AlternateItem    = vDet.getString("AlternateItem").trim().replaceAll("\\s+", " ");
                        String vLine6           = str_pad(Description, len6, " ", "STR_PAD_RIGHT");
                        String vLine6_1         = str_pad(UOM, len6_1, " ", "STR_PAD_RIGHT");
                        String vLine6_2         = str_pad(AlternateItem, len6_2, " ", "STR_PAD_RIGHT");
                        String vLine6_3         = str_pad("", len6_1, " ", "STR_PAD_RIGHT");
                        String vLine7           = str_pad(Qty, len7, " ", "STR_PAD_LEFT");
                        String vLine7_1         = str_pad(HCUnitCost, len7_1, " ", "STR_PAD_LEFT");
                        String vLine7_2         = str_pad(Disc2, len7_2, " ", "STR_PAD_LEFT");
                        String vLine7_3         = str_pad(HCLineAmt, len7_3, " ", "STR_PAD_LEFT");
                        strPrint += vLine6+vLine6_1 + "\n";
                        if(AlternateItem.length()>1){
                            strPrint            += vLine6_2+vLine6_3+"\n";
                        }
                        strPrint += "" + vLine7 + " x" + vLine7_1 + vLine7_2 + vLine7_3 + "\n";
                    }
                }
                //end detail

                //total
                String vLine8="";
                String vLine8_1="";
                String vLine8_2="";
                String vLine9="";
                String vLine9_1="";
                String vLine9_2="";
                String vLine10="";
                String vLine10_1="";
                String vLine10_2="";
                String vLine11="";
                String vLine11_1="";
                String vLine11_2="";
                String vLine12="";
                String vLine12_1="";
                String vLine12_2="";
                String vLine13="";
                String qTotal = "select IFNULL(H.HCNetAmt,0)as HCNetAmt, IFNULL(sum(D.HCTax),0) as HCDtTax," +
                        " IFNULL(sum(D.HCLineAmt)-sum(D.HCTax),0) as AmtExTax, " +
                        " IFNULL(Sum(D.Qty),0) as ItemTender " +
                        " from stk_sales_order_hd H inner join" +
                        " stk_sales_order_dt D ON D.Doc1No=H.Doc1No where H.Doc1No='" + Doc1No + "' Group By H.Doc1No ";
                if(DBStatus.equals("0")){
                    Cursor rsTot=db.getQuery(qTotal);
                    while(rsTot.moveToNext()){
                        Double dTotAmt = rsTot.getDouble(0);
                        String TotAmt = String.format(Locale.US, "%,.2f", dTotAmt);
                        Double dTaxAmt = rsTot.getDouble(1);
                        String TaxAmt = String.format(Locale.US, "%,.2f", dTaxAmt);
                        Double dAmtExtTax = rsTot.getDouble(2);
                        String AmtExtTax = String.format(Locale.US, "%,.2f", dAmtExtTax);
                        Double dItemTender = rsTot.getDouble(3);
                        String ItemTender = String.format(Locale.US, "%,.2f", dItemTender);
                        vLine8_2 = str_pad(AmtExtTax, len8_2, " ", "STR_PAD_LEFT");
                        vLine9_2 = str_pad(TaxAmt, len8_2, " ", "STR_PAD_LEFT");
                        vLine10_2 = str_pad("0.00", len8_2, " ", "STR_PAD_LEFT");
                        vLine11_2 = str_pad(TotAmt, len8_2, " ", "STR_PAD_LEFT");
                        vLine12_2 = str_pad(ItemTender, len8_2, " ", "STR_PAD_LEFT");
                    }
                }else if(DBStatus.equals("1")){
                    if (conn != null) {
                        Statement statement = conn.createStatement();
                        if (statement.execute(qTotal)) {
                            ResultSet rsTot = statement.getResultSet();
                            while (rsTot.next()) {
                                Double dTotAmt = rsTot.getDouble(1);
                                String TotAmt = String.format(Locale.US, "%,.2f", dTotAmt);
                                Double dTaxAmt = rsTot.getDouble(2);
                                String TaxAmt = String.format(Locale.US, "%,.2f", dTaxAmt);
                                Double dAmtExtTax = rsTot.getDouble(3);
                                String AmtExtTax = String.format(Locale.US, "%,.2f", dAmtExtTax);
                                Double dItemTender = rsTot.getDouble(4);
                                String ItemTender = String.format(Locale.US, "%,.2f", dItemTender);
                                vLine8_2 = str_pad(AmtExtTax, len8_2, " ", "STR_PAD_LEFT");
                                vLine9_2 = str_pad(TaxAmt, len8_2, " ", "STR_PAD_LEFT");
                                vLine10_2 = str_pad("0.00", len8_2, " ", "STR_PAD_LEFT");
                                vLine11_2 = str_pad(TotAmt, len8_2, " ", "STR_PAD_LEFT");
                                vLine12_2 = str_pad(ItemTender, len8_2, " ", "STR_PAD_LEFT");
                            }
                        }
                    }
                }else if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qTotal);
                    jsonReq.put("action", "select");
                    String rsTot = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsTot);
                    String resTot= jsonRes.getString("hasil");
                    JSONArray jaTot = new JSONArray(resTot);
                    JSONObject vTot = null;
                    for (int i = 0; i < jaTot.length(); i++) {
                        vTot            = jaTot.getJSONObject(i);
                        Double dTotAmt  = vTot.getDouble("HCNetAmt");
                        String TotAmt   = String.format(Locale.US, "%,.2f", dTotAmt);
                        Double dTaxAmt  = vTot.getDouble("HCDtTax");
                        String TaxAmt   = String.format(Locale.US, "%,.2f", dTaxAmt);
                        Double dAmtExTax   = vTot.getDouble("AmtExTax");
                        String AmtExTax    = String.format(Locale.US, "%,.2f", dAmtExTax);
                        Double dItemTender  = vTot.getDouble("ItemTender");
                        String ItemTender   = String.format(Locale.US, "%,.2f", dItemTender);
                        vLine8_2 = str_pad(AmtExTax, len8_2, " ", "STR_PAD_LEFT");
                        vLine9_2 = str_pad(TaxAmt, len8_2, " ", "STR_PAD_LEFT");
                        vLine10_2 = str_pad("0.00", len8_2, " ", "STR_PAD_LEFT");
                        vLine11_2 = str_pad(TotAmt, len8_2, " ", "STR_PAD_LEFT");
                        vLine12_2 = str_pad(ItemTender, len8_2, " ", "STR_PAD_LEFT");
                    }
                }

                strPrint += vLine+"\n\n";
                vLine8=str_pad("Amount Exc Tax", len8, " ", "STR_PAD_RIGHT");
                vLine8_1=str_pad(":", len8_1, " ", "STR_PAD_RIGHT");
                vLine9=str_pad("Add Total Tax Amt", len8, " ", "STR_PAD_RIGHT");
                vLine9_1=str_pad(":", len8_1, " ", "STR_PAD_RIGHT");
                vLine10=str_pad("Rounding", len8, " ", "STR_PAD_RIGHT");
                vLine10_1=str_pad(":", len8_1, " ", "STR_PAD_RIGHT");
                vLine11=str_pad("Total Amount Due", len8, " ", "STR_PAD_RIGHT");
                vLine11_1=str_pad(":", len8_1, " ", "STR_PAD_RIGHT");
                vLine12=str_pad("Total Qty Tender", len8, " ", "STR_PAD_RIGHT");
                vLine12_1=str_pad(":", len8_1, " ", "STR_PAD_RIGHT");
                vLine13=str_pad("Gross Amount", len8, " ", "STR_PAD_RIGHT");

                if (!GSTNo.equals("NO")) {
                    strPrint += vLine8+vLine8_1+vLine8_2+ "\n";
                    strPrint += vLine9 +vLine9_1+vLine9_2+"\n";
                    strPrint += vLine10+vLine10_1+vLine10_2+"\n";
                    strPrint += vLine11+ vLine11_1 + vLine11_2+"\n";
                } else {
                    strPrint += vLine13+vLine11_1+ vLine11_2+ "\n";
                    strPrint += vLine12+vLine12_1+vLine12_2+ "\n";
                }

                if (!GSTNo.equals("NO")) {
                    strPrint += vLine+"\n";
                    strPrint += "Tax Summary \n";
                    String vLine15=str_pad("Code", len14, " ", "STR_PAD_LEFT");
                    String vLine15_1=str_pad("Rate", len14_1, " ", "STR_PAD_LEFT");
                    String vLine15_2=str_pad("Goods Amt", len14_2, " ", "STR_PAD_LEFT");
                    String vLine15_3=str_pad("Tax Amt", len14_3, " ", "STR_PAD_LEFT");
                    strPrint += vLine15+vLine15_1+vLine15_2+vLine15_3+"\n";
                }
                //end total
                String qGST = "SELECT IFNULL(TaxRate1,0)as TaxRate1, DetailTaxCode, " +
                        "IFNULL(sum(HCLineAmt)-sum(HCTax),0) as GoodAmt, " +
                        "IFNULL(sum(HCTax),0) as GSTAmt " +
                        "FROM stk_sales_order_dt " +
                        "WHERE Doc1No='" + Doc1No + "' Group By DetailTaxCode  ";

                if(DBStatus.equals("0")){
                    Cursor rsGST=db.getQuery(qGST);
                    while(rsGST.moveToNext()){
                        Double dTaxRate = rsGST.getDouble(0);
                        String TaxRate = String.format(Locale.US, "%,.2f", dTaxRate);
                        String TaxCode = rsGST.getString(1);
                        Double dGoodAmt = rsGST.getDouble(2);
                        String GoodAmt = String.format(Locale.US, "%,.2f", dGoodAmt);
                        Double dGSTAmt = rsGST.getDouble(3);
                        String GSTAmt = String.format(Locale.US, "%,.2f", dGSTAmt);
                        String vLine14 = str_pad(TaxCode, len14, " ", "STR_PAD_LEFT");
                        String vLine14_1 = str_pad(TaxRate, len14_1, " ", "STR_PAD_LEFT");
                        String vLine14_2 = str_pad(GoodAmt, len14_2, " ", "STR_PAD_LEFT");
                        String vLine14_3 = str_pad(GSTAmt, len14_3, " ", "STR_PAD_LEFT");
                        if (!GSTNo.equals("NO")) {
                            strPrint += vLine14 +vLine14_1+vLine14_2+vLine14_3+"\n";
                        }
                    }
                }else if(DBStatus.equals("1")){
                    if (conn != null) {
                        Statement stmtGST = conn.createStatement();
                        if (stmtGST.execute(qGST)) {
                            ResultSet rsGST = stmtGST.getResultSet();
                            while (rsGST.next()) {
                                Double dTaxRate = rsGST.getDouble(1);
                                String TaxRate = String.format(Locale.US, "%,.2f", dTaxRate);
                                String TaxCode = rsGST.getString(2);
                                Double dGoodAmt = rsGST.getDouble(3);
                                String GoodAmt = String.format(Locale.US, "%,.2f", dGoodAmt);
                                Double dGSTAmt = rsGST.getDouble(4);
                                String GSTAmt = String.format(Locale.US, "%,.2f", dGSTAmt);
                                String vLine14 = str_pad(TaxCode, len14, " ", "STR_PAD_LEFT");
                                String vLine14_1 = str_pad(TaxRate, len14_1, " ", "STR_PAD_LEFT");
                                String vLine14_2 = str_pad(GoodAmt, len14_2, " ", "STR_PAD_LEFT");
                                String vLine14_3 = str_pad(GSTAmt, len14_3, " ", "STR_PAD_LEFT");
                                if (!GSTNo.equals("NO")) {
                                    strPrint += vLine14 +vLine14_1+vLine14_2+vLine14_3+"\n";
                                }
                            }
                        }
                    }
                }else if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qGST);
                    jsonReq.put("action", "select");
                    String rsGST = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsGST);
                    String resGST= jsonRes.getString("hasil");
                    JSONArray jaGST = new JSONArray(resGST);
                    JSONObject vTax = null;
                    for (int i = 0; i < jaGST.length(); i++) {
                        vTax = jaGST.getJSONObject(i);
                        Double dTaxRate = vTax.getDouble("TaxRate1");
                        String TaxRate = String.format(Locale.US, "%,.2f", dTaxRate);
                        String TaxCode = vTax.getString("DetailTaxCode");
                        Double dGoodAmt = vTax.getDouble("GoodAmt");
                        String GoodAmt = String.format(Locale.US, "%,.2f", dGoodAmt);
                        Double dGSTAmt = vTax.getDouble("GSTAmt");
                        String GSTAmt = String.format(Locale.US, "%,.2f", dGSTAmt);
                        String vLine14 = str_pad(TaxCode, len14, " ", "STR_PAD_LEFT");
                        String vLine14_1 = str_pad(TaxRate, len14_1, " ", "STR_PAD_LEFT");
                        String vLine14_2 = str_pad(GoodAmt, len14_2, " ", "STR_PAD_LEFT");
                        String vLine14_3 = str_pad(GSTAmt, len14_3, " ", "STR_PAD_LEFT");
                        if (!GSTNo.equals("NO")) {
                            strPrint += vLine14 +vLine14_1+vLine14_2+vLine14_3+"\n";
                        }
                    }
                }
                //gst
                strPrint += vLine+"\n\n\n";

                db.closeDB();
                return strPrint;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return strPrint;
        }

        private String fngenerate58(String Doc1No,String TableNo, String datedTime) {
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
            try {
                JSONObject jsonReq,jsonRes;
                DBAdapter db = new DBAdapter(getActivity());
                db.openDB();
                //header
                vLine7 = str_pad("GUEST CHECK", 32, " ", "STR_PAD_BOTH");
                //end query header

                //start query customer
                String qCustomer = "select C.CusName, C.CusCode, C.Address " +
                        " from stk_sales_order_hd H inner join customer C ON H.CusCode=C.CusCode " +
                        " where H.Doc1No='" + Doc1No + "'  ";
                if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qCustomer);
                    jsonReq.put("action", "select");
                    String rsCus = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsCus);
                    String resCus= jsonRes.getString("hasil");
                    JSONArray jaCus = new JSONArray(resCus);
                    JSONObject vCus = null;
                    for (int i = 0; i < jaCus.length(); i++) {
                        vCus = jaCus.getJSONObject(i);
                        CusCode = vCus.getString("CusCode");
                        CusName = vCus.getString("CusName");
                        AdddresCus = vCus.getString("Address");
                    }
                }else {
                    Cursor rsCus = db.getQuery(qCustomer);
                    while (rsCus.moveToNext()) {
                        CusName = rsCus.getString(0);
                    }
                }

                vLine9 = str_pad("Bill #  : " + Doc1No, 32, " ", "STR_PAD_RIGHT");
                vLine11 = str_pad(datedTime, 18, " ", "STR_PAD_RIGHT");
                if (TableNo.equals("0")) {
                    vLine11_1 = str_pad("", 13, " ", "STR_PAD_RIGHT");
                } else {
                    vLine11_1 = str_pad("Table No #: " + TableNo, 13, " ", "STR_PAD_RIGHT");
                }
                vLine12 = str_pad("BILL TO : " + CusName, 32, " ", "STR_PAD_RIGHT");
                //vLine13 = str_pad(CurCode+" ", 32, " ", "STR_PAD_LEFT");
                String vLine131 = str_pad("Qty", 7, " ", "STR_PAD_RIGHT");
                String vLine132 = str_pad("Price", 11, " ", "STR_PAD_BOTH");
                String vLine133 = str_pad("Amount (" + CurCode + ")", 14, " ", "STR_PAD_LEFT");
                //end query customer
                strPrint += "________________________________\n";
                strPrint += vLine7 + "\n";
                strPrint += "________________________________\n";
                //end header
                strPrint += vLine9 + "\n";
                strPrint += vLine11 + vLine11_1 + "\n";
                strPrint += vLine12 + "\n";
                strPrint += "________________________________\n";
                strPrint += vLine131 + vLine132 + vLine133 + "\n";
                strPrint += "________________________________\n";
                //end customer
                //detail
                String qDetail = "SELECT ItemCode,ROUND(Qty,2) as Qty, UOM, DetailTaxCode, SUBSTR(Description,1,45) as Description, " +
                        "ROUND(HCUnitCost,2) as HCUnitCost, ROUND(HCLineAmt,2) as HCLineAmt,ROUND(DisRate1,2) as DisRate1," +
                        "ROUND(HCDiscount,2) as HCDiscount FROM stk_sales_order_dt WHERE Doc1No='" + Doc1No + "' ";

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
                        Double dQty             = vDet.getDouble("Qty");
                        String Qty              = String.format(Locale.US, "%,.2f", dQty);
                        String UOM              = vDet.getString("UOM");
                        String DetailTaxCode    = vDet.getString("DetailTaxCode");
                        String Description      = vDet.getString("Description");
                        Double dHCUnitCost      = vDet.getDouble("HCUnitCost");
                        String HCUnitCost       = String.format(Locale.US, "%,.2f", dHCUnitCost);
                        Double dHCLineAmt       = vDet.getDouble("HCLineAmt");
                        String HCLineAmt        = String.format(Locale.US, "%,.2f", dHCLineAmt);
                        Double dDisRate1        = vDet.getDouble("DisRate1");
                        String DisRate1         = String.format(Locale.US, "%,.2f", dDisRate1);
                        Double dHCDiscount      = vDet.getDouble("HCDiscount");
                        String HCDiscount       = String.format(Locale.US, "%,.2f", dHCDiscount);
                        if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                            Disc2 = "(" + HCDiscount + ")";
                        } else {
                            Disc2 = " ";
                        }
                        vLine14             = str_pad(Description, 26, " ", "STR_PAD_RIGHT");
                        String vLine14_1    = str_pad(UOM, 6, " ", "STR_PAD_RIGHT");
                        String vLine15_1    = str_pad(Qty, 4, " ", "STR_PAD_LEFT");
                        String vLine15_2    = str_pad(HCUnitCost, 9, " ", "STR_PAD_LEFT");
                        String vLine15_3    = str_pad(Disc2, 6, " ", "STR_PAD_LEFT");
                        String vLine15_4    = str_pad(HCLineAmt, 10, " ", "STR_PAD_LEFT");
                        strPrint            += vLine14_1+vLine14 + "\n";
                        strPrint            += "" + vLine15_1 + " x" + vLine15_2 + vLine15_3 + vLine15_4 + "\n";
                    }
                }else {
                    Cursor rsDt = db.getQuery(qDetail);
                    while (rsDt.moveToNext()) {
                        String ItemCode = rsDt.getString(0);
                        Double dQty = rsDt.getDouble(1);
                        String Qty = String.format(Locale.US, "%,.2f", dQty);
                        String UOM = rsDt.getString(2);
                        String DetailTaxCode = rsDt.getString(3);
                        String Description = rsDt.getString(4);
                        Double dHCUnitCost = rsDt.getDouble(5);
                        String HCUnitCost = String.format(Locale.US, "%,.2f", dHCUnitCost);
                        Double dHCLineAmt = rsDt.getDouble(6);
                        String HCLineAmt = String.format(Locale.US, "%,.2f", dHCLineAmt);
                        Double dDisRate1 = rsDt.getDouble(7);
                        String DisRate1 = String.format(Locale.US, "%,.2f", dDisRate1);
                        Double dHCDiscount = rsDt.getDouble(8);
                        String HCDiscount = String.format(Locale.US, "%,.2f", dHCDiscount);
                        if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                            Disc2 = "(" + HCDiscount + ")";
                        } else {
                            Disc2 = " ";
                        }
                        vLine14 = str_pad(Description, 26, " ", "STR_PAD_RIGHT");
                        String vLine14_1 = str_pad(UOM, 6, " ", "STR_PAD_RIGHT");
                        String vLine15_1 = str_pad(Qty, 4, " ", "STR_PAD_LEFT");
                        String vLine15_2 = str_pad(HCUnitCost, 9, " ", "STR_PAD_LEFT");
                        String vLine15_3 = str_pad(Disc2, 6, " ", "STR_PAD_LEFT");
                        String vLine15_4 = str_pad(HCLineAmt, 10, " ", "STR_PAD_LEFT");
                        strPrint += vLine14_1+vLine14 + "\n";
                        strPrint += "" + vLine15_1 + " x" + vLine15_2 + vLine15_3 + vLine15_4 + "\n";
                    }
                }
                //end detail

                //start Total
                String qTotal = "select ROUND(H.HCNetAmt,2)as HCNetAmt, ROUND(sum(D.HCTax),2) as HCDtTax," +
                        " ROUND(sum(D.HCLineAmt)-sum(D.HCTax),2) as AmtExTax, " +
                        " ROUND(Sum(D.Qty),2) as ItemTender " +
                        " from stk_sales_order_hd H inner join" +
                        " stk_sales_order_dt D ON D.Doc1No=H.Doc1No where H.Doc1No='" + Doc1No + "' Group By H.Doc1No ";
                String vLine22_2="";
                if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qTotal);
                    jsonReq.put("action", "select");
                    String rsTot = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsTot);
                    String resTot= jsonRes.getString("hasil");
                    JSONArray jaTot = new JSONArray(resTot);
                    JSONObject vTot = null;
                    for (int i = 0; i < jaTot.length(); i++) {
                        vTot            = jaTot.getJSONObject(i);
                        Double dTotAmt  = vTot.getDouble("HCNetAmt");
                        String TotAmt   = String.format(Locale.US, "%,.2f", dTotAmt);
                        Double dTaxAmt  = vTot.getDouble("HCDtTax");
                        String TaxAmt   = String.format(Locale.US, "%,.2f", dTaxAmt);
                        Double dAmtExTax   = vTot.getDouble("AmtExTax");
                        String AmtExTax    = String.format(Locale.US, "%,.2f", dAmtExTax);
                        Double dItemTender  = vTot.getDouble("ItemTender");
                        String ItemTender   = String.format(Locale.US, "%,.2f", dItemTender);
                        vLine16 = str_pad(AmtExTax, 11, " ", "STR_PAD_LEFT");
                        vLine17 = str_pad(TaxAmt, 11, " ", "STR_PAD_LEFT");
                        vLine18 = str_pad("0.00", 11, " ", "STR_PAD_LEFT");
                        vLine19 = str_pad(TotAmt, 11, " ", "STR_PAD_LEFT");
                        vLine22_2 = str_pad(ItemTender, 11, " ", "STR_PAD_LEFT");
                    }
                }else {
                    Cursor rsTot = db.getQuery(qTotal);
                    while (rsTot.moveToNext()) {
                        Double dTotAmt = rsTot.getDouble(0);
                        String TotAmt = String.format(Locale.US, "%,.2f", dTotAmt);
                        Double dTaxAmt = rsTot.getDouble(1);
                        String TaxAmt = String.format(Locale.US, "%,.2f", dTaxAmt);
                        Double dAmtExtTax = rsTot.getDouble(2);
                        String AmtExtTax = String.format(Locale.US, "%,.2f", dAmtExtTax);
                        Double dItemTender = rsTot.getDouble(3);
                        String ItemTender = String.format(Locale.US, "%,.2f", dItemTender);
                        vLine16 = str_pad(AmtExtTax, 11, " ", "STR_PAD_LEFT");
                        vLine17 = str_pad(TaxAmt, 11, " ", "STR_PAD_LEFT");
                        vLine18 = str_pad("0.00", 11, " ", "STR_PAD_LEFT");
                        vLine19 = str_pad(TotAmt, 11, " ", "STR_PAD_LEFT");
                        vLine22_2 = str_pad(ItemTender, 11, " ", "STR_PAD_LEFT");
                    }
                }
                strPrint += "________________________________\n\n";
                if (!GSTNo.equals("NO")) {
                    strPrint += "Amount Exc Tax    : " + vLine16 + "\n";
                    strPrint += "Add Total GST Amt : " + vLine17 + "\n";
                    strPrint += "Rounding          : " + vLine18 + "\n";
                    strPrint += "Total Amount Due  : " + vLine19 + "\n";
                } else {
                    strPrint += "Total Amt Payable : " + vLine19 + "\n";
                    strPrint += "Total Qty Tender  : " + vLine22_2 + "\n";
                }

                if (!GSTNo.equals("NO")) {
                    strPrint += "________________________________\n";
                    strPrint += "GST Summary \n";
                    strPrint += "Code  Rate   Goods Amt  GST Amt\n";
                }
                //end total

                //start GST
                String qGST = "select round(TaxRate1,2)as TaxRate1,DetailTaxCode,round(sum(HCLineAmt)-sum(HCTax),2) as GoodAmt," +
                        "round(sum(HCTax),2) as GSTAmt from stk_sales_order_dt  Where Doc1No='" + Doc1No + "' Group By DetailTaxCode  ";

                if(DBStatus.equals("2")) {
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qGST);
                    jsonReq.put("action", "select");
                    String rsGST = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsGST);
                    String resGST= jsonRes.getString("hasil");
                    JSONArray jaGST = new JSONArray(resGST);
                    JSONObject vTax = null;
                    for (int i = 0; i < jaGST.length(); i++) {
                        vTax = jaGST.getJSONObject(i);
                        Double dTaxRate = vTax.getDouble("TaxRate1");
                        String TaxRate = String.format(Locale.US, "%,.2f", dTaxRate);
                        String TaxCode = vTax.getString("DetailTaxCode");
                        Double dGoodAmt = vTax.getDouble("GoodAmt");
                        String GoodAmt = String.format(Locale.US, "%,.2f", dGoodAmt);
                        Double dGSTAmt = vTax.getDouble("GSTAmt");
                        String GSTAmt = String.format(Locale.US, "%,.2f", dGSTAmt);
                        String vLine23_1 = str_pad(TaxCode, 4, " ", "STR_PAD_LEFT");
                        String vLine23_2 = str_pad(TaxRate, 6, " ", "STR_PAD_LEFT");
                        String vLine23_3 = str_pad(GoodAmt, 11, " ", "STR_PAD_LEFT");
                        String vLine23_4 = str_pad(GSTAmt, 11, " ", "STR_PAD_LEFT");
                        vLine23 = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                        if (!GSTNo.equals("NO")) {
                            strPrint += vLine23 + "\n";
                        }
                    }
                }else{
                    Cursor rsGST = db.getQuery(qGST);
                    while (rsGST.moveToNext()) {
                        Double dTaxRate = rsGST.getDouble(0);
                        String TaxRate = String.format(Locale.US, "%,.2f", dTaxRate);
                        String TaxCode = rsGST.getString(1);
                        Double dGoodAmt = rsGST.getDouble(2);
                        String GoodAmt = String.format(Locale.US, "%,.2f", dGoodAmt);
                        Double dGSTAmt = rsGST.getDouble(3);
                        String GSTAmt = String.format(Locale.US, "%,.2f", dGSTAmt);
                        String vLine23_1 = str_pad(TaxCode, 4, " ", "STR_PAD_LEFT");
                        String vLine23_2 = str_pad(TaxRate, 6, " ", "STR_PAD_LEFT");
                        String vLine23_3 = str_pad(GoodAmt, 11, " ", "STR_PAD_LEFT");
                        String vLine23_4 = str_pad(GSTAmt, 11, " ", "STR_PAD_LEFT");
                        vLine23 = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                        if (!GSTNo.equals("NO")) {
                            strPrint += vLine23 + "\n";
                        }
                    }
                }

                strPrint += "________________________________\n \n\n";
                Log.d("RESULT", strPrint);
                return strPrint;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch(JSONException e){
                e.printStackTrace();
            }
            return strPrint;
        }

        private String fngenerate78(String Doc1No,String TableNo,String datedTime){
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
            try {
                JSONObject jsonReq,jsonRes;
                DBAdapter db = new DBAdapter(getActivity());
                db.openDB();
                vLine7 = str_pad("GUEST CHECK", 48, " ", "STR_PAD_BOTH");
                //start query customer
                String qCustomer = "select C.CusName, C.CusCode, C.Address " +
                        " from stk_sales_order_hd H inner join customer C ON H.CusCode=C.CusCode " +
                        " where H.Doc1No='" + Doc1No + "' ";
                if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qCustomer);
                    jsonReq.put("action", "select");
                    String rsCus = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsCus);
                    String resCus= jsonRes.getString("hasil");
                    JSONArray jaCus = new JSONArray(resCus);
                    JSONObject vCus = null;
                    for (int i = 0; i < jaCus.length(); i++) {
                        vCus = jaCus.getJSONObject(i);
                        CusCode = vCus.getString("CusCode");
                        CusName = vCus.getString("CusName");
                        AdddresCus = vCus.getString("Address");
                    }
                }else {
                    Cursor rsCus = db.getQuery(qCustomer);
                    while (rsCus.moveToNext()) {
                        CusName = rsCus.getString(0);
                    }
                }

                vLine8 = str_pad("Bill #: " + Doc1No, 48, " ", "STR_PAD_RIGHT");
                vLine11 = str_pad(datedTime, 24, " ", "STR_PAD_RIGHT");
                if (TableNo.equals("0")) {
                    vLine11_1 = str_pad("", 24, " ", "STR_PAD_RIGHT");
                } else {
                    vLine11_1 = str_pad("Table No #: " + TableNo, 24, " ", "STR_PAD_RIGHT");
                }
                vLine12 = str_pad("BILL TO : " + CusName, 48, " ", "STR_PAD_RIGHT");

                String vLine131 = str_pad("Quantity", 9, " ", "STR_PAD_RIGHT");
                String vLine132 = str_pad("Unit Price", 19, " ", "STR_PAD_BOTH");
                String vLine133 = str_pad("Amount (" + CurCode + ")", 20, " ", "STR_PAD_LEFT");
                //end query customer
                strPrint += "________________________________________________\n";
                strPrint += vLine7 + "\n";
                strPrint += "________________________________________________\n";
                //end header
                //customer
                strPrint += vLine8 + "\n";
                //strPrint += vLine9+"\n";
                //strPrint += vLine10+"\n";
                strPrint += vLine11 + vLine11_1 + "\n";
                strPrint += vLine12 + "\n";
                strPrint += "________________________________________________\n";
                strPrint += vLine131 + vLine132 + vLine133 + "\n";
                strPrint += "________________________________________________\n";
                //end customer

                //detail
                String qDetail = "SELECT ItemCode, ROUND(Qty,2) as Qty, UOM, DetailTaxCode, SUBSTR(Description,1,45) as Description, " +
                        "ROUND(HCUnitCost,2) as HCUnitCost, ROUND(HCLineAmt,2) AS HCLineAmt, ROUND(DisRate1,2) as DisRate1, " +
                        "ROUND(HCDiscount,2) as HCDiscount FROM stk_sales_order_dt WHERE Doc1No='" + Doc1No + "' ";
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
                        vDet                = jaDt.getJSONObject(i);
                        String ItemCode     = vDet.getString("ItemCode");
                        Double dQty         = vDet.getDouble("Qty");
                        String Qty          = String.format(Locale.US, "%,.2f", dQty);
                        String UOM          = vDet.getString("UOM");
                        String DetailTaxCode = vDet.getString("DetailTaxCode");
                        String Description  = vDet.getString("Description");
                        Double dHCUnitCost  = vDet.getDouble("HCUnitCost");
                        String HCUnitCost   = String.format(Locale.US, "%,.2f", dHCUnitCost);
                        Double dHCLineAmt   = vDet.getDouble("HCLineAmt");
                        String HCLineAmt    = String.format(Locale.US, "%,.2f", dHCLineAmt);
                        Double dDisRate1    = vDet.getDouble("DisRate1");
                        String DisRate1     = String.format(Locale.US, "%,.2f", dDisRate1);
                        Double dHCDiscount  = vDet.getDouble("HCDiscount");
                        String HCDiscount = String.format(Locale.US, "%,.2f", dHCDiscount);
                        if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                            Disc2 = "(" + HCDiscount + ")";
                        } else {
                            Disc2 = " ";
                        }
                        vLine14 = str_pad(Description, 40, " ", "STR_PAD_RIGHT");
                        String vLine14_1 = str_pad(UOM, 8, " ", "STR_PAD_RIGHT");
                        String vLine15_1 = str_pad(Qty, 6, " ", "STR_PAD_LEFT");
                        String vLine15_2 = str_pad(HCUnitCost, 13, " ", "STR_PAD_LEFT");
                        String vLine15_3 = str_pad(Disc2, 12, " ", "STR_PAD_LEFT");
                        String vLine15_4 = str_pad(HCLineAmt, 15, " ", "STR_PAD_LEFT");
                        strPrint += vLine14_1+vLine14 + "\n";
                        strPrint += "" + vLine15_1 + " x" + vLine15_2 + vLine15_3 + vLine15_4 + "\n";
                    }
                }else {
                    Cursor rsDt = db.getQuery(qDetail);
                    while (rsDt.moveToNext()) {
                        String ItemCode = rsDt.getString(0);
                        Double dQty = rsDt.getDouble(1);
                        String Qty = String.format(Locale.US, "%,.2f", dQty);
                        String UOM = rsDt.getString(2);
                        String DetailTaxCode = rsDt.getString(3);
                        String Description = rsDt.getString(4);
                        Double dHCUnitCost = rsDt.getDouble(5);
                        String HCUnitCost = String.format(Locale.US, "%,.2f", dHCUnitCost);
                        Double dHCLineAmt = rsDt.getDouble(6);
                        String HCLineAmt = String.format(Locale.US, "%,.2f", dHCLineAmt);
                        Double dDisRate1 = rsDt.getDouble(7);
                        String DisRate1 = String.format(Locale.US, "%,.2f", dDisRate1);
                        Double dHCDiscount = rsDt.getDouble(8);
                        String HCDiscount = String.format(Locale.US, "%,.2f", dHCDiscount);
                        if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                            Disc2 = "(" + HCDiscount + ")";
                        } else {
                            Disc2 = " ";
                        }
                        vLine14 = str_pad(Description, 40, " ", "STR_PAD_RIGHT");
                        String vLine14_1 = str_pad(UOM, 8, " ", "STR_PAD_RIGHT");
                        String vLine15_1 = str_pad(Qty, 6, " ", "STR_PAD_LEFT");
                        String vLine15_2 = str_pad(HCUnitCost, 13, " ", "STR_PAD_LEFT");
                        String vLine15_3 = str_pad(Disc2, 12, " ", "STR_PAD_LEFT");
                        String vLine15_4 = str_pad(HCLineAmt, 15, " ", "STR_PAD_LEFT");
                        strPrint += vLine14_1+vLine14 + "\n";
                        strPrint += "" + vLine15_1 + " x" + vLine15_2 + vLine15_3 + vLine15_4 + "\n";
                    }
                }
                //end detail
                //start Total
                String vLine22_2 ="";
                String qTotal = "SELECT ROUND(H.HCNetAmt,2)as HCNetAmt," +
                        " ROUND(sum(D.HCTax),2) as HCDtTax," +
                        " ROUND(sum(D.HCLineAmt)-sum(D.HCTax),2) as AmtExTax, " +
                        " ROUND(Sum(D.Qty),2) as ItemTender " +
                        " FROM stk_sales_order_hd H INNER JOIN" +
                        " stk_sales_order_dt D ON D.Doc1No=H.Doc1No " +
                        " WHERE H.Doc1No='" + Doc1No + "' Group By H.Doc1No ";
                if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qTotal);
                    jsonReq.put("action", "select");
                    String rsTot = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsTot);
                    String resTot= jsonRes.getString("hasil");
                    JSONArray jaTot = new JSONArray(resTot);
                    JSONObject vTot = null;
                    for (int i = 0; i < jaTot.length(); i++) {
                        vTot = jaTot.getJSONObject(i);
                        Double dTotAmt = vTot.getDouble("HCNetAmt");
                        String TotAmt = String.format(Locale.US, "%,.2f", dTotAmt);
                        Double dTaxAmt = vTot.getDouble("HCDtTax");
                        String TaxAmt = String.format(Locale.US, "%,.2f", dTaxAmt);
                        Double dAmtExTax = vTot.getDouble("AmtExTax");
                        String AmtExTax = String.format(Locale.US, "%,.2f", dAmtExTax);
                        Double dItemTender = vTot.getDouble("ItemTender");
                        String ItemTender = String.format(Locale.US, "%,.2f", dItemTender);
                        vLine16 = str_pad(AmtExTax, 22, " ", "STR_PAD_LEFT");
                        vLine17 = str_pad(TaxAmt, 22, " ", "STR_PAD_LEFT");
                        vLine18 = str_pad("0.00", 22, " ", "STR_PAD_LEFT");
                        vLine19 = str_pad(TotAmt, 22, " ", "STR_PAD_LEFT");
                        vLine22_2 = str_pad(ItemTender, 22, " ", "STR_PAD_LEFT");
                    }
                }else {
                    Cursor rsTot = db.getQuery(qTotal);
                    while (rsTot.moveToNext()) {
                        Double dTotAmt = rsTot.getDouble(0);
                        String TotAmt = String.format(Locale.US, "%,.2f", dTotAmt);
                        Double dTaxAmt = rsTot.getDouble(1);
                        String TaxAmt = String.format(Locale.US, "%,.2f", dTaxAmt);
                        Double dAmtExtTax = rsTot.getDouble(2);
                        String AmtExtTax = String.format(Locale.US, "%,.2f", dAmtExtTax);
                        Double dItemTender = rsTot.getDouble(3);
                        String ItemTender = String.format(Locale.US, "%,.2f", dItemTender);
                        vLine16 = str_pad(AmtExtTax, 22, " ", "STR_PAD_LEFT");
                        vLine17 = str_pad(TaxAmt, 22, " ", "STR_PAD_LEFT");
                        vLine18 = str_pad("0.00", 22, " ", "STR_PAD_LEFT");
                        vLine19 = str_pad(TotAmt, 22, " ", "STR_PAD_LEFT");
                        vLine22_2 = str_pad(ItemTender, 22, " ", "STR_PAD_LEFT");
                    }
                }
                strPrint += "________________________________________________\n\n";
                if (!GSTNo.equals("NO")) {
                    strPrint += "Amount Exc Tax         : " + replaceDec(vLine16) + "\n";
                    strPrint += "Add Total GST Amount   : " + replaceDec(vLine17) + "\n";
                    strPrint += "Rounding               : " + vLine18 + "\n";
                    strPrint += "Total Amount Due       : " + replaceDec(vLine19) + "\n";
                } else {
                    strPrint += "Total Quantity Tender  : " + vLine22_2 + "\n";

                }
                if (!GSTNo.equals("NO")) {
                    strPrint += "________________________________________________\n";
                    strPrint += "GST Summary \n";
                    strPrint += "Tax Code      Rate      Goods Amt      GST Amt  \n";
                }
                //end total

                //start GST
                String qGST = "SELECT ROUND(TaxRate1,2)as TaxRate1, DetailTaxCode, " +
                        "ROUND(sum(HCLineAmt)-sum(HCTax),2) as GoodAmt, " +
                        "ROUND(sum(HCTax),2) as GSTAmt " +
                        "FROM stk_sales_order_dt " +
                        "WHERE Doc1No='" + Doc1No + "' Group By DetailTaxCode  ";
                if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", qGST);
                    jsonReq.put("action", "select");
                    String rsGST = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsGST);
                    String resGST= jsonRes.getString("hasil");
                    Log.d("JSON GST",resGST);
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
                            vLine23 = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                            strPrint += vLine23 + "\n";
                        }
                    }
                }else {
                    Cursor rsGST = db.getQuery(qGST);
                    while (rsGST.moveToNext()) {
                        Double dTaxRate = rsGST.getDouble(0);
                        String TaxRate = String.format(Locale.US, "%,.2f", dTaxRate);
                        String TaxCode = rsGST.getString(1);
                        Double dGoodAmt = rsGST.getDouble(2);
                        String GoodAmt = String.format(Locale.US, "%,.2f", dGoodAmt);
                        Double dGSTAmt = rsGST.getDouble(3);
                        String GSTAmt = String.format(Locale.US, "%,.2f", dGSTAmt);
                        String vLine23_1 = str_pad(TaxCode, 6, " ", "STR_PAD_LEFT");
                        String vLine23_2 = str_pad(TaxRate, 12, " ", "STR_PAD_LEFT");
                        String vLine23_3 = str_pad(GoodAmt, 15, " ", "STR_PAD_LEFT");
                        String vLine23_4 = str_pad(GSTAmt, 15, " ", "STR_PAD_LEFT");
                        if (!GSTNo.equals("NO")) {
                            vLine23 = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                            strPrint += vLine23 + "\n";
                        }
                    }
                }

                strPrint += "________________________________________________\n\n\n";
                return strPrint;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
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

    private String replaceDec(String value){
        String newValue="";
        if(value.length()>12) {
            newValue= value.substring(0, value.length() - 3) + "-";
        }else{
            newValue=value;
        }
        return newValue;
    }
    public void setPrint(String Doc1No,String Doc2No){
        PrintGuest print=new PrintGuest(getActivity(),Doc1No,Doc2No);
        print.execute();
    }

}
