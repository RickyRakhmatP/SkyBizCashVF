package skybiz.com.posoffline.ui_CashReceipt.m_OrderSlip;

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
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_NewObject.Encode;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_ListSO.DownloaderListSO;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothPrinter;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothZebra;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_USB.PrintingUSB;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI.PrintingWifi;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 17/01/2018.
 */

public class Dialog_OrderSlip extends DialogFragment {

    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    String TypePrinter,NamePrinter,IPPrinter,PrinterPort,NewDoc;
    String IPAddress,Password,DBName,UserName,URL,Port,z,vPort,DBStatus,ItemConn,EncodeType;
    Boolean isBT;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_listso, container, false);
        getDialog().setTitle("List Of Order Slip");
        refresh();
        readyPrinter();
        return view;
    }

    public void refresh() {
        rv=(RecyclerView)view.findViewById(R.id.list_so);
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 3);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloadOrderSlip dlistSO=new DownloadOrderSlip(getActivity(),rv,Dialog_OrderSlip.this);
        dlistSO.execute();
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
            PrinterPort = rsPrint.getString(3);
        }
        if(TypePrinter.equals("AIDL")){
            AidlUtil.getInstance().connectPrinterService(getActivity());
            AidlUtil.getInstance().initPrinter();
        }else{

        }
        db.closeDB();
    }

    public class printSlip extends AsyncTask<Void,Void,String>{
        Context c;
        String Doc1No;

        public printSlip(Context c, String doc1No) {
            this.c = c;
            Doc1No = doc1No;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.genPrint();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result==null){
                Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
            }else{
                fnPrint(result);
                getDialog().dismiss();
               //OrderSlipParser p=new OrderSlipParser(c,result,rv,dialogOrderSlip);
               // p.execute();
            }
        }
        private String genPrint(){
            String vStr="";
            try{
                Connection conn=null;
                JSONObject jsonReq,jsonRes;
                String vLine0="";
                String vLine0_1="";
                String vLine1="";
                String vLine2="";
                String vLine3="";
                String vLine4="";
                String vLine5="";
                String vLine51="";
                String vLine52="";
                String vLine53="";
                String vLine61="";
                String vLine62="";
                String vLine63="";
                int lenH=32;
                int lenM=8;
                int len =48;
                int len1=18;
                int len1_1=30;
                int len2=6;
                int len3=41;
                if(TypePrinter.equals("AIDL")){
                    lenM=4;
                    lenH=24;
                    len =32;
                    len1=18;
                    len1_1=14;
                    len3=25;
                }
                DBAdapter db=new DBAdapter(getActivity());
                db.openDB();
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
                String strSQL = "SELECT H.Doc1No,D.AnalysisCode2, IFNULL(SUM(D.Qty),'0')as Pax," +
                        "H.Doc2No " +
                        "FROM stk_sales_order_hd H inner join stk_sales_order_dt D ON D.Doc1No=H.Doc1No" +
                        " where H.Doc1No='" + Doc1No + "' Group By H.Doc1No ";
                String Pax="";
                String TableNo="";
                if(DBStatus.equals("1")) {
                    //URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    //conn = Connector.connect(URL, UserName, Password);
                    conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement statement = conn.createStatement();
                        if (statement.execute(strSQL)) {
                            ResultSet rsHeader = statement.getResultSet();
                            while (rsHeader.next()) {
                                Double dPax= rsHeader.getDouble(3);
                                Pax     =  String.format(Locale.US, "%,.2f", dPax);
                                TableNo = rsHeader.getString(4);
                            }
                        }
                    }
                }else if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", strSQL);
                    jsonReq.put("action", "select");
                    String rs = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rs);
                    String rsHeader = jsonRes.getString("hasil");
                    JSONArray rsData = new JSONArray(rsHeader);
                    JSONObject vData = null;
                    for (int i = 0; i < rsData.length(); i++) {
                        vData = rsData.getJSONObject(i);
                        Pax = vData.getString("Pax");
                        TableNo = vData.getString("AnalysisCode2");
                    }
                }else{
                    Cursor rsHeader = db.getQuery(strSQL);
                    while (rsHeader.moveToNext()) {
                        Pax = rsHeader.getString(2);
                        TableNo = rsHeader.getString(1);
                    }
                }
                String vLine =str_pad("_", len, "_", "STR_PAD_BOTH");
                String vTine =str_pad("-", len, "-", "STR_PAD_BOTH");
                vLine0 = str_pad("* ORDER SLIP *", lenH, " ", "STR_PAD_BOTH");
                vLine0_1 = str_pad(" ", lenM, " ", "STR_PAD_BOTH");
                vLine1 = str_pad("===== NEW =====", lenH, " ", "STR_PAD_BOTH");
                vLine2 = str_pad("TABLE#:" + TableNo + "  Pax:" + Pax, lenH, " ", "STR_PAD_BOTH");
                vLine3 = str_pad("Check   : " + Doc1No, len, " ", "STR_PAD_RIGHT");
                vLine4 = str_pad("Printer : " + TypePrinter, len, " ", "STR_PAD_RIGHT");
                vStr += vLine0_1+vLine0+vLine0_1+ "\n";
                vStr += vLine0_1+vLine1+vLine0_1+ "\n";
                vStr += vLine0_1+vLine2 +vLine0_1+ "\n";
                vStr += vLine + "\n";
                vStr += vLine3 + "\n";
                vStr += vLine4 + "\n";
                vStr += vLine + "\n";

                String strSQLD = "select IFNULL(Qty, '0') as vQty, SUBSTR(Description,1,"+len1_1+") as Description, " +
                        "Description2, UOM, SUBSTR(AlternateItem,1,"+len1_1+") as AlternateItem " +
                        "from stk_sales_order_dt Where Doc1No ='"+Doc1No+"' ";
                if(DBStatus.equals("1")) {
                    if (conn != null) {
                        String strSQLD2 = "select IFNULL(D.Qty, '0') as vQty, SUBSTR(D.Description,1,"+len1_1+") as Description, " +
                                "D.Description2, D.UOM, SUBSTR(M.AlternateItem,1,"+len1_1+") AlternateItem " +
                                "from stk_sales_order_dt D inner join stk_master M on D.ItemCode=M.ItemCode " +
                                "Where D.Doc1No ='"+Doc1No+"' ";
                        Statement stmtD = conn.createStatement();
                        stmtD.executeQuery("SET NAMES 'LATIN1'");
                        stmtD.executeQuery("SET CHARACTER SET 'LATIN1'");
                        if (stmtD.execute(strSQLD2)) {
                            ResultSet rsDet = stmtD.getResultSet();
                            while (rsDet.next()) {
                                String Modifier         = Encode.setChar(EncodeType,rsDet.getString(3));
                                String AlternateItem    = Encode.setChar(EncodeType,rsDet.getString(5));
                                Double dQty             = rsDet.getDouble(1);
                                String Qty              = String.format(Locale.US, "%,.0f", dQty);
                                vLine5      = str_pad("D"+" [  ] "+Qty+" "+ Encode.setChar(EncodeType,rsDet.getString(4)), len1, " ", "STR_PAD_LEFT");
                                vLine51     = str_pad(Encode.setChar(EncodeType,rsDet.getString(2)), len1_1, " ", "STR_PAD_RIGHT");
                                vLine52     = str_pad(AlternateItem, len3, " ", "STR_PAD_RIGHT");
                                vLine62     = str_pad("(" + Modifier + ")", len3, " ", "STR_PAD_LEFT");
                                vLine61     = str_pad("", len2, " ", "STR_PAD_LEFT");
                                vStr += vLine51 + vLine5;
                                if(AlternateItem.length()>1){
                                    vStr        += vLine52+"\n";
                                }
                                if (Modifier.length() > 1) {
                                    vStr        +=vLine62 +"\n";
                                }
                                vStr += vTine+"\n";
                            }
                        }
                    }

                }else if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", strSQLD);
                    jsonReq.put("action", "select");
                    String rs = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rs);
                    String rsDetail = jsonRes.getString("hasil");
                    JSONArray rsData = new JSONArray(rsDetail);
                    JSONObject vData = null;
                    for (int i = 0; i < rsData.length(); i++) {
                        vData           = rsData.getJSONObject(i);
                        String Modifier = vData.getString("Description2");
                        String AlternateItem = vData.getString("AlternateItem");
                        vLine5          = str_pad("D"+" [  ] "+vData.getString("vQty")+" "+vData.getString("UOM"), len1, " ", "STR_PAD_LEFT");
                        vLine51         = str_pad(vData.getString("Description"), len1_1, " ", "STR_PAD_RIGHT");
                        vLine52         = str_pad(AlternateItem, len3, " ", "STR_PAD_RIGHT");
                        vLine62         = str_pad("(" + Modifier + ")", len3, " ", "STR_PAD_LEFT");
                        vLine61         = str_pad("", len2, " ", "STR_PAD_LEFT");
                        vStr            += vLine51 + vLine5;
                        if(AlternateItem.length()>1){
                            vStr        += vLine52+"\n";
                        }
                        if (Modifier.length() > 1) {
                            vStr        += vLine62 +"\n";
                        }
                        vStr += "--------------------------------\n";
                    }
                }else{
                    Cursor rsDet = db.getQuery(strSQLD);
                    while (rsDet.moveToNext()) {
                        String Modifier         = rsDet.getString(2);
                        String AlternateItem    = rsDet.getString(4);
                        vLine5                  = str_pad("D"+" [  ] "+rsDet.getString(0)+""+rsDet.getString(3), len1, " ", "STR_PAD_LEFT");
                        vLine51                 = str_pad(rsDet.getString(1), len1_1, " ", "STR_PAD_RIGHT");
                        vLine52                 = str_pad(AlternateItem, len1_1, " ", "STR_PAD_RIGHT");
                        vLine62                 = str_pad("(" + Modifier + ")", len3, " ", "STR_PAD_LEFT");
                        vLine61                 = str_pad("", len2, " ", "STR_PAD_LEFT");
                        vStr += vLine51 + vLine5;
                        if(AlternateItem.length()>1){
                            vStr        += vLine52+"\n";
                        }
                        if (Modifier.length() > 1) {
                            vStr        += vLine61 + vLine62 +"\n";
                        }
                        vStr += vTine+"\n";

                    }
                }
               // vStr += str_pad(Doc1No, len, " ", "STR_PAD_BOTH")+"\n";
                vStr += "\n\n\n";
                Log.d("PRINT",vStr);
                db.closeDB();
                return vStr;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return vStr;
        }

    }


    public void print(String Doc1No){
        //String strPrint=genPrint(Doc1No);
        printSlip print=new printSlip(getActivity(),Doc1No);
        print.execute();
    }
    private void fnPrint(String strPrint){
        if (TypePrinter.equals("AIDL")) {
            AidlUtil.getInstance().printText(strPrint);
        } else if (TypePrinter.equals("Bluetooth")) {
            BluetoothPrinter fncheck = new BluetoothPrinter();
            isBT = fncheck.fnBluetooth(getActivity(), NamePrinter, strPrint);
            if (isBT == false) {
                NewDoc = "error print";
            }
        } else if (TypePrinter.equals("Bluetooth Zebra")) {
            BluetoothZebra fncheck = new BluetoothZebra();
            isBT = fncheck.fnBluetooth(getActivity(), NamePrinter, strPrint);
            if (isBT == false) {
                NewDoc = "error print";
            }
        } else if (TypePrinter.equals("Wifi")) {
            PrintingWifi fnprintw = new PrintingWifi();
            int Port = Integer.parseInt(vPort);
            isBT = fnprintw.fnprintwifi(getActivity(), IPPrinter, Port, strPrint);
            if (isBT == false) {
                NewDoc = "error print";
            }
        } else if (TypePrinter.equals("USB")) {
            PrintingUSB fnprintu = new PrintingUSB();
            isBT = fnprintu.fnprintusb(getActivity(), strPrint);
            if (isBT == false) {
                NewDoc = "error print";
            }
        } else {
            //No Printer
        }
    }
    private String genPrint(String Doc1No){
        String vStr="";
        try{
            Connection conn=null;
            JSONObject jsonReq,jsonRes;
            String vLine0="";
            String vLine0_1="";
            String vLine1="";
            String vLine2="";
            String vLine3="";
            String vLine4="";
            String vLine5="";
            String vLine51="";
            String vLine52="";
            String vLine53="";
            String vLine61="";
            String vLine62="";
            String vLine63="";
            int lenH=32;
            int lenM=8;
            int len =48;
            int len1=18;
            int len1_1=48;
            int len2=6;
            int len3=42;
            if(TypePrinter.equals("AIDL")){
                lenM=4;
                lenH=24;
                len =32;
                len1=18;
                len1_1=32;
                len3=26;
            }
            DBAdapter db=new DBAdapter(getActivity());
            db.openDB();
            String strSQL = "SELECT H.Doc1No,D.AnalysisCode2, IFNULL(SUM(D.Qty),'0')as Pax " +
                    "FROM stk_sales_order_hd H inner join stk_sales_order_dt D ON D.Doc1No=H.Doc1No" +
                    " where H.Doc1No='" + Doc1No + "' Group By H.Doc1No ";
            String Pax="";
            String TableNo="";
            if(DBStatus.equals("1")) {
                //URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                //conn = Connector.connect(URL, UserName, Password);
                conn= Connect_db.getConnection();
                if (conn != null) {
                    Statement statement = conn.createStatement();
                    if (statement.execute(strSQL)) {
                        ResultSet rsHeader = statement.getResultSet();
                        while (rsHeader.next()) {
                            Pax     = rsHeader.getString(3);
                            TableNo = rsHeader.getString(2);
                        }
                    }
                }
            }else if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", strSQL);
                jsonReq.put("action", "select");
                String rs = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rs);
                String rsHeader = jsonRes.getString("hasil");
                JSONArray rsData = new JSONArray(rsHeader);
                JSONObject vData = null;
                for (int i = 0; i < rsData.length(); i++) {
                    vData = rsData.getJSONObject(i);
                    Pax = vData.getString("Pax");
                    TableNo = vData.getString("AnalysisCode2");
                }
            }else{
                Cursor rsHeader = db.getQuery(strSQL);
                while (rsHeader.moveToNext()) {
                    Pax = rsHeader.getString(2);
                    TableNo = rsHeader.getString(1);
                }
            }
            String vLine =str_pad("_", len, "_", "STR_PAD_BOTH");
            String vTine =str_pad("-", len, "-", "STR_PAD_BOTH");
            vLine0 = str_pad("* ORDER SLIP *", lenH, " ", "STR_PAD_BOTH");
            vLine0_1 = str_pad(" ", lenM, " ", "STR_PAD_BOTH");
            vLine1 = str_pad("===== NEW =====", lenH, " ", "STR_PAD_BOTH");
            vLine2 = str_pad("TABLE#:" + TableNo + "  Pax:" + Pax, lenH, " ", "STR_PAD_BOTH");
            vLine3 = str_pad("Check   : " + Doc1No, len, " ", "STR_PAD_RIGHT");
            vLine4 = str_pad("Printer : " + TypePrinter, len, " ", "STR_PAD_RIGHT");
            vStr += vLine0_1+vLine0+vLine0_1+ "\n";
            vStr += vLine0_1+vLine1+vLine0_1+ "\n";
            vStr += vLine0_1+vLine2 +vLine0_1+ "\n";
            vStr += vLine + "\n";
            vStr += vLine3 + "\n";
            vStr += vLine4 + "\n";
            vStr += vLine + "\n";

            String strSQLD = "select IFNULL(Qty, '0') as vQty, SUBSTR(Description,1,"+len1_1+") as Description, " +
                    "Description2, UOM " +
                    "from stk_sales_order_dt Where Doc1No ='"+Doc1No+"' ";

            if(DBStatus.equals("1")) {
                if (conn != null) {
                    Statement stmtD = conn.createStatement();
                    stmtD.executeQuery("SET NAMES 'LATIN1'");
                    stmtD.executeQuery("SET CHARACTER SET 'LATIN1'");
                    if (stmtD.execute(strSQLD)) {
                        ResultSet rsDet = stmtD.getResultSet();
                        while (rsDet.next()) {
                            String Modifier = rsDet.getString(3);
                            vLine5 = str_pad("D"+" [  ] "+rsDet.getString(1)+""+rsDet.getString(4), len1, " ", "STR_PAD_RIGHT");
                            vLine51 = str_pad(rsDet.getString(2), len1_1, " ", "STR_PAD_RIGHT");
                            vLine62 = str_pad("(" + Modifier + ")", len3, " ", "STR_PAD_LEFT");
                            vLine61  = str_pad("", len2, " ", "STR_PAD_LEFT");
                            vStr += vLine5 + vLine51  + "\n";
                            if (Modifier.length() > 1) {
                                vStr        += vLine61 + vLine62 +"\n";
                            }
                            vStr += vTine+"\n";
                        }
                    }
                }

            }else if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", strSQLD);
                jsonReq.put("action", "select");
                String rs = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rs);
                String rsDetail = jsonRes.getString("hasil");
                JSONArray rsData = new JSONArray(rsDetail);
                JSONObject vData = null;
                for (int i = 0; i < rsData.length(); i++) {
                    vData           = rsData.getJSONObject(i);
                    String Modifier = vData.getString("Description2");
                    vLine5          = str_pad("D"+" [  ] "+vData.getString("vQty")+" "+vData.getString("UOM"), len1, " ", "STR_PAD_RIGHT");
                    vLine51         = str_pad(vData.getString("Description"), len1_1, " ", "STR_PAD_RIGHT");
                    vLine62       = str_pad("(" + Modifier + ")", len3, " ", "STR_PAD_LEFT");
                    vLine61         = str_pad("", len2, " ", "STR_PAD_LEFT");
                    vStr            += vLine5 + vLine51  + "\n";
                    if (Modifier.length() > 1) {
                        vStr        += vLine61 + vLine62 +"\n";
                    }
                    vStr += "--------------------------------\n";
                }
            }else{
                Cursor rsDet = db.getQuery(strSQLD);
                while (rsDet.moveToNext()) {
                    String Modifier = rsDet.getString(2);
                    vLine5 = str_pad("D"+" [  ] "+rsDet.getString(0)+""+rsDet.getString(3), len1, " ", "STR_PAD_RIGHT");
                    vLine51 = str_pad(rsDet.getString(1), len1_1, " ", "STR_PAD_RIGHT");
                    vLine62 = str_pad("(" + Modifier + ")", len3, " ", "STR_PAD_LEFT");
                    vLine61  = str_pad("", len2, " ", "STR_PAD_LEFT");
                    vStr += vLine5 + vLine51  + "\n";
                    if (Modifier.length() > 1) {
                        vStr        += vLine61 + vLine62 +"\n";
                    }
                    vStr += vTine+"\n";

                }
            }
            vStr += str_pad(Doc1No, len, " ", "STR_PAD_BOTH")+"\n";
            vStr += "\n\n\n";
            db.closeDB();
            return vStr;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return vStr;
    }

    public String str_pad(String input, int length, String pad, String    sense) {
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
