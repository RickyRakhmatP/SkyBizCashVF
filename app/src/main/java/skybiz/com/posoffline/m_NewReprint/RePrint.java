package skybiz.com.posoffline.m_NewReprint;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import skybiz.com.posoffline.m_NewObject.Encode;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothPrinter;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothZebra;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_USB.PrintingUSB;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI.PrintingWifi;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class RePrint extends AsyncTask<Void,Void,String> {
    Context c;
    String DocType,Doc1No;
    String z,Doc2No,Doc3No,D_ateTime;
    String IPAddress,UserName,Password,
            DBName,Port,DBStatus,URL,
            EncodeType,CurCode,NamePrinter,
            IPPrinter,vPostGlobalTaxYN,TypePrinter,
            vPort,NewDoc;
    Boolean isBT;
    DBAdapter db;
    Connection conn;
    String tb_header,tb_detail;

    public RePrint(Context c, String docType, String doc1No) {
        this.c = c;
        DocType = docType;
        Doc1No = doc1No;
        this.Doc1No = doc1No;
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
            Toast.makeText(c,"Error, Reprint", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Success, Reprint", Toast.LENGTH_SHORT).show();
           //
        }
    }

    private String fnprint(){
        try{
            JSONObject jsonReq,jsonRes;
            z="success";
            db=new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName, UserName, Password," +
                    "DBName, Port, DBStatus," +
                    "EncodeType "+
                    "from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(0);
                UserName = curSet.getString(1);
                Password = curSet.getString(2);
                DBName = curSet.getString(3);
                Port = curSet.getString(4);
                DBStatus=curSet.getString(5);
                EncodeType=curSet.getString(6);
            }
            String query="select TypePrinter,NamePrinter,IPPrinter,Port from tb_settingprinter";
            Cursor rsPrint=db.getQuery(query);
            while(rsPrint.moveToNext()){
                TypePrinter = rsPrint.getString(0);
                NamePrinter = rsPrint.getString(1);
                IPPrinter = rsPrint.getString(2);
                vPort = rsPrint.getString(3);
            }


            Cursor cur = db.getGeneralSetup();
            while (cur.moveToNext()) {
                CurCode = cur.getString(1);
                vPostGlobalTaxYN = cur.getString(6);
            }

            if(DocType.equals("SO")){
                tb_header="stk_sales_order_hd";
                tb_detail="stk_sales_order_dt";
            }else if(DocType.equals("CusCN")){
                tb_header="stk_cus_inv_hd";
                tb_detail="stk_cus_inv_dt";
            }
            String last = "select Doc1No ,Doc2No, Doc3No, D_ate " +
                    "from "+tb_header+" where Doc1No='"+Doc1No+"' ";
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                conn = Connector.connect(URL, UserName, Password);
                if (conn != null) {
                    Statement stmt = conn.createStatement();
                    stmt.execute(last);
                    ResultSet rsData = stmt.getResultSet();
                    while (rsData.next()) {
                        Doc2No      = rsData.getString(2);
                        Doc3No      = rsData.getString(3);
                        D_ateTime   = rsData.getString(4);
                    }
                }
            }else if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
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
                    vData           = rsData.getJSONObject(i);
                    Doc2No          = vData.getString("Doc2No");
                    Doc3No          = vData.getString("Doc3No");
                    D_ateTime       = vData.getString("D_ateTime");
                }
            }else {
                Cursor rsLast = db.getQuery(last);
                while (rsLast.moveToNext()) {
                    Doc2No      = rsLast.getString(1);
                    Doc3No      = rsLast.getString(2);
                    D_ateTime   = rsLast.getString(3);
                }
            }

            String stringPrint="";
            if (TypePrinter.equals("AIDL")) {
                stringPrint=fngenerate78();
            } else if(!TypePrinter.equals("AIDL")) {
                stringPrint=fngenerate78();
            }

            if (TypePrinter.equals("AIDL")) {
                AidlUtil.getInstance().printText(stringPrint);
            } else if (TypePrinter.equals("Bluetooth")) {
                BluetoothPrinter fncheck = new BluetoothPrinter();
                isBT = fncheck.fnBluetooth(c, NamePrinter, stringPrint);
                if (isBT == false) {
                    NewDoc = "error print";
                    z="error";
                }
            } else if (TypePrinter.equals("Bluetooth Zebra")) {
                //Log.d("STR PRINT"+NamePrinter,stringPrint);
                BluetoothZebra fncheck = new BluetoothZebra();
                isBT = fncheck.fnBluetooth(c, NamePrinter, stringPrint);
                if (isBT == false) {
                    NewDoc = "error print";
                    z="error";
                }
            } else if (TypePrinter.equals("Wifi")) {
                int iPort =Integer.parseInt(vPort);
                PrintingWifi fnprintw = new PrintingWifi();
                isBT = fnprintw.fnprintwifi(c, IPPrinter, iPort, stringPrint);
                if (isBT == false) {
                    NewDoc = "error print";
                    z="error";
                }
            } else if (TypePrinter.equals("USB")) {
                PrintingUSB fnprintu = new PrintingUSB();
                isBT = fnprintu.fnprintusb(c, stringPrint);
                if (isBT == false) {
                    NewDoc = "error print";
                    z="error";
                }
            } else {
                //No Printer
            }
            z=stringPrint;
            // db.closeDB();
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }
    private String fngenerate78(){
        String strPrint="";
        try{
            int len=48;
            int len1=9;
            int len1_1=19;
            int len1_2=20;
            int len2=6;
            int len2_1=42;
            int len3=6;
            int len3_1=13;
            int len3_2=12;
            int len3_3=15;
            int len4=22;
            int len5=6;
            int len5_1=12;
            int len5_2=15;
            int len5_3=15;
            if(TypePrinter.equals("AIDL")){
                len=32;
                len1=8;
                len1_1=12;
                len1_2=12;
                len2=5;
                len2_1=27;
                len3=5;
                len3_1=8;
                len3_2=8;
                len3_3=11;
                len4=12;
                len5=5;
                len5_1=8;
                len5_2=9;
                len5_3=10;
            }

            String CompanyName="";
            String CompanyCode="";
            String GSTNo="";
            String Address="";
            String Fax="";
            String Tel="";
            String ComTown="";
            String ComState="";
            String ComCountry="";
            String CusName="";
            String CusCode="";
            String AdddresCus="";
            String Disc2="";
            String Title="";
            String Company="select CurCode,CompanyName,CompanyCode," +
                    "GSTNo,Address,Tel1," +
                    "Fax1,CompanyEmail,ComTown," +
                    "ComState,ComCountry FROM companysetup ";
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
            String vLine        =str_pad("_", len, "_", "STR_PAD_BOTH");
            String vLine1   = str_pad(CompanyName, len, " ", "STR_PAD_BOTH");
            String vLine2   = str_pad("Co.Reg.No.: "+CompanyCode, len, " ", "STR_PAD_BOTH");
            String vLine3   = str_pad("GST Reg.No.: "+GSTNo, len, " ", "STR_PAD_BOTH");
            String AddressAll = Address.replaceAll("(.{"+len+"})", "$1;");
            String [] address=AddressAll.split(";");
            String vAddress="";
            for(String add:address){
                vAddress += str_pad(add,len," ", "STR_PAD_BOTH");
            }
            String vLine4      = vAddress;
            String vLine5      = str_pad(Tel, len, " ", "STR_PAD_BOTH");
            String vLine6      = str_pad(Fax, len, " ", "STR_PAD_BOTH");
            if(DocType.equals("SO")){
                Title="SALES ORDER (REPRINT)";
            }else if(DocType.equals("CusCN")){
                Title="CREDIT NOTE (REPRINT)";
            }
            String vLine7      = str_pad(Title, len, " ", "STR_PAD_BOTH");

            String qCustomer="select H.CusCode,C.CusName,C.Address " +
                    " from "+tb_header+" H inner join customer C" +
                    " on H.CusCode=C.CusCode where H.Doc1No='"+Doc1No+"'";
            if(DBStatus.equals("1")){
                if (conn != null) {
                    Statement stmtCus   = conn.createStatement();
                    stmtCus.execute(qCustomer);
                    ResultSet rsCus     = stmtCus.getResultSet();
                    while (rsCus.next()) {
                        CusCode     =rsCus.getString(1);
                        CusName     =rsCus.getString(2);
                        AdddresCus  =rsCus.getString(3);
                    }
                }
            }else if(DBStatus.equals("0")){
                Cursor rsCus=db.getQuery(qCustomer);
                while(rsCus.moveToNext()){
                    CusCode     =rsCus.getString(0);
                    CusName     =rsCus.getString(1);
                    AdddresCus  =rsCus.getString(2);
                }
            }

            String vLine8  =str_pad("Bill #: "+Doc1No, len, " ", "STR_PAD_RIGHT");
            String vLine11 =str_pad(D_ateTime, len, " ", "STR_PAD_RIGHT");
            String vLine12 =str_pad("BILL TO : "+CusName , len, " ", "STR_PAD_RIGHT");
            String vLine13 =str_pad(AdddresCus , len, " ", "STR_PAD_RIGHT");
            String vLine131=str_pad("Quantity", len1, " ", "STR_PAD_RIGHT");
            String vLine132=str_pad("Unit Price", len1_1, " ", "STR_PAD_BOTH");
            String vLine133=str_pad("Amount ("+CurCode+")", len1_2, " ", "STR_PAD_LEFT");

            strPrint += vLine2+"\n";
            strPrint += vLine3+"\n";
            strPrint += vLine4+"\n";
            if(!Tel.equals("")) {
                // strPrint += vLine5 + "\n";
            }
            if(!Fax.equals("")){
                // strPrint += vLine6+"\n";
            }
            strPrint += vLine+"\n";
            strPrint += vLine7+"\n";
            strPrint += vLine+"\n";
            //end header

            //customer
            strPrint += vLine8+"\n";
            strPrint += vLine11+"\n";
            strPrint += vLine12+"\n";
            if(!CusCode.equals("999999")) {
                strPrint += vLine13 + "\n";
            }
            strPrint += vLine+"\n";
            strPrint += vLine131+vLine132+vLine133+"\n";
            strPrint += vLine+"\n";
            //end customer

            //detail
            String qDetail="select ItemCode, IFNULL(Qty,0) as Qty, UOM," +
                    " DetailTaxCode,substr(Description,1,45) as Description, IFNULL(HCUnitCost,0) as HCUnitCost," +
                    " IFNULL(HCLineAmt,0) AS HCLineAmt,IFNULL(DisRate1,0) as DisRate1,IFNULL(HCDiscount,0) as HCDiscount " +
                    " from "+tb_detail+" Where Doc1No='"+Doc1No+"' ";
            if(DBStatus.equals("1")) {
                if (conn != null) {
                    Statement stmtDet   = conn.createStatement();
                    stmtDet.execute(qDetail);
                    ResultSet rsDet     = stmtDet.getResultSet();
                    while (rsDet.next()) {
                        String ItemCode         = rsDet.getString(1);
                        String Qty              = twoDecimal(rsDet.getDouble(2));
                        String UOM              = Encode.setChar(EncodeType,rsDet.getString(3));
                        String DetailTaxCode    = rsDet.getString(4);
                        String Description      = Encode.setChar(EncodeType,rsDet.getString(5));
                        String HCUnitCost       = twoDecimal(rsDet.getDouble(6));
                        String HCLineAmt        = twoDecimal(rsDet.getDouble(7));
                        String DisRate1         = twoDecimal(rsDet.getDouble(8));
                        String HCDiscount       = twoDecimal(rsDet.getDouble(9));
                        if(!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")){
                            Disc2		    ="("+HCDiscount+")";
                        }else{
                            Disc2			=" ";
                        }
                        String vLine14          =str_pad(Description, len2_1, " ", "STR_PAD_RIGHT");
                        String vLine14_1        =str_pad(UOM, len2, " ", "STR_PAD_RIGHT");
                        String vLine15_1        =str_pad(Qty, len3, " ", "STR_PAD_LEFT");
                        String vLine15_2        =str_pad(HCUnitCost, len3_1, " ", "STR_PAD_LEFT");
                        String vLine15_3        =str_pad(Disc2, len3_2, " ", "STR_PAD_LEFT");
                        String vLine15_4        =str_pad(HCLineAmt, len3_3, " ", "STR_PAD_LEFT");
                        strPrint 				+= vLine14_1+vLine14+"\n";
                        strPrint 				+= ""+vLine15_1+" x"+vLine15_2+vLine15_3+vLine15_4+"\n";
                    }
                }
            }else if(DBStatus.equals("0")){
                Cursor rsDet     = db.getQuery(qDetail);
                while (rsDet.moveToNext()) {
                    String ItemCode         = rsDet.getString(0);
                    String Qty              = twoDecimal(rsDet.getDouble(1));
                    String UOM              = rsDet.getString(2);
                    String DetailTaxCode    = rsDet.getString(3);
                    String Description      = rsDet.getString(4);
                    String HCUnitCost       = twoDecimal(rsDet.getDouble(5));
                    String HCLineAmt        = twoDecimal(rsDet.getDouble(6));
                    String DisRate1         = twoDecimal(rsDet.getDouble(7));
                    String HCDiscount       = twoDecimal(rsDet.getDouble(8));
                    if(!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")){
                        Disc2		    ="("+HCDiscount+")";
                    }else{
                        Disc2			=" ";
                    }
                    String vLine14          =str_pad(Description, len2_1, " ", "STR_PAD_RIGHT");
                    String vLine14_1        =str_pad(UOM, len2, " ", "STR_PAD_RIGHT");
                    String vLine15_1        =str_pad(Qty, len3, " ", "STR_PAD_LEFT");
                    String vLine15_2        =str_pad(HCUnitCost, len3_1, " ", "STR_PAD_LEFT");
                    String vLine15_3        =str_pad(Disc2, len3_2, " ", "STR_PAD_LEFT");
                    String vLine15_4        =str_pad(HCLineAmt, len3_3, " ", "STR_PAD_LEFT");
                    strPrint 				+= vLine14_1+vLine14+"\n";
                    strPrint 				+= ""+vLine15_1+" x"+vLine15_2+vLine15_3+vLine15_4+"\n";
                }
            }
            //end detail

            //total
            String qTotal="select IFNULL(H.HCNetAmt,0)as HCNetAmt, IFNULL(H.HCDtTax,0) as HCDtTax, " +
                    "IFNULL(sum(D.HCLineAmt)-sum(D.HCTax),0) as AmtExTax, IFNULL(Sum(D.Qty),0) as ItemTender " +
                    "from "+tb_header+" H inner join "+tb_detail+" D ON D.Doc1No=H.Doc1No " +
                    "where H.Doc1No='"+Doc1No+"' Group By H.Doc1No ";
            Log.d("QUERY",qTotal);
            if(DBStatus.equals("1")) {
                if (conn != null) {
                    Statement stmtTot   = conn.createStatement();
                    stmtTot.execute(qTotal);
                    ResultSet rsTot     = stmtTot.getResultSet();
                    while (rsTot.next()) {
                        String TotAmt       =twoDecimal(rsTot.getDouble(1));
                        String TaxAmt       =twoDecimal(rsTot.getDouble(2));
                        String AmtExtTax    =twoDecimal(rsTot.getDouble(3));
                        String ItemTender   =twoDecimal(rsTot.getDouble(4));
                        String vLine16      =str_pad(AmtExtTax, len4, " ", "STR_PAD_LEFT");
                        String vLine17      =str_pad(TaxAmt, len4, " ", "STR_PAD_LEFT");
                        String vLine18      =str_pad("0.00", len4, " ", "STR_PAD_LEFT");
                        String vLine19      =str_pad(TotAmt, len4, " ", "STR_PAD_LEFT");
                        String vLine22_2    =str_pad(ItemTender, len4, " ", "STR_PAD_LEFT");
                        strPrint            += vLine+"\n\n";
                        if(!GSTNo.equals("NO")) {
                            strPrint += "Amount Exc Tax         : " + vLine16 + "\n";
                            strPrint += "Add Total GST Amount   : " + vLine17 + "\n";
                            strPrint += "Rounding               : " + vLine18 + "\n";
                            strPrint += "Total Amount Due       : " + vLine19 + "\n";
                        }else{
                            strPrint += "Total Amt Payable      : " + vLine19 + "\n";
                            strPrint += "Total Qty Tender       : " + vLine22_2 + "\n";
                        }
                        if(!GSTNo.equals("NO")) {
                            strPrint          += vLine+"\n";
                            strPrint          += "GST Summary \n";
                            strPrint          += "   Code       Rate      Goods Amt      GST Amt  \n";
                        }
                    }
                }
            }else if(DBStatus.equals("0")){
                Cursor rsTot=db.getQuery(qTotal);
                while(rsTot.moveToNext()){
                    String TotAmt       =twoDecimal(rsTot.getDouble(0));
                    String TaxAmt       =twoDecimal(rsTot.getDouble(1));
                    String AmtExtTax    =twoDecimal(rsTot.getDouble(2));
                    String ItemTender   =twoDecimal(rsTot.getDouble(3));
                    String vLine16      =str_pad(AmtExtTax, len4, " ", "STR_PAD_LEFT");
                    String vLine17      =str_pad(TaxAmt, len4, " ", "STR_PAD_LEFT");
                    String vLine18      =str_pad("0.00", len4, " ", "STR_PAD_LEFT");
                    String vLine19      =str_pad(TotAmt, len4, " ", "STR_PAD_LEFT");
                    String vLine22_2    =str_pad(ItemTender, len4, " ", "STR_PAD_LEFT");
                    strPrint            += vLine+"\n\n";
                    if(!TypePrinter.equals("AIDL")) {
                        if (!GSTNo.equals("NO")) {
                            strPrint += "Amount Exc Tax         : " + vLine16 + "\n";
                            strPrint += "Add Total GST Amount   : " + vLine17 + "\n";
                            strPrint += "Rounding               : " + vLine18 + "\n";
                            strPrint += "Total Amount Due       : " + vLine19 + "\n";
                        } else {
                            strPrint += "Total Amt Payable      : " + vLine19 + "\n";
                            strPrint += "Total Qty Tender       : " + vLine22_2 + "\n";
                        }
                        if (!GSTNo.equals("NO")) {
                            strPrint += vLine + "\n";
                            strPrint += "GST Summary \n";
                            strPrint += "   Code       Rate      Goods Amt      GST Amt  \n";
                        }
                    }else{
                        if (!GSTNo.equals("NO")) {
                            strPrint += "Amount Exc Tax    : " + vLine16 + "\n";
                            strPrint += "Add Total GST Amt : " + vLine17 + "\n";
                            strPrint += "Rounding          : " + vLine18 + "\n";
                            strPrint += "Total Amt Due     : " + vLine19 + "\n";
                        } else {
                            strPrint += "Total Amt Payable : " + vLine19 + "\n";
                            strPrint += "Total Qty Tender  : " + vLine22_2 + "\n";
                        }
                        if (!GSTNo.equals("NO")) {
                            strPrint += vLine + "\n";
                            strPrint += "GST Summary \n";
                            strPrint += "Code  Rate   Goods Amt  GST Amt\n";
                        }
                    }
                }
            }
            //end total

            //gst
            String qGST="select IFNULL(TaxRate1,0)as TaxRate1, DetailTaxCode, IFNULL(sum(HCLineAmt)-sum(HCTax),0) as GoodAmt," +
                    "IFNULL(sum(HCTax),0) as GSTAmt " +
                    "from "+tb_detail+"  Where Doc1No='"+Doc1No+"' Group By DetailTaxCode  ";
            if(DBStatus.equals("1")) {
                if (conn != null) {
                    Statement stmtGST   = conn.createStatement();
                    stmtGST.execute(qGST);
                    ResultSet rsGST     = stmtGST.getResultSet();
                    while (rsGST.next()) {
                        String TaxRate      =twoDecimal(rsGST.getDouble(1));
                        String TaxCode      =rsGST.getString(2);
                        String GoodAmt      =twoDecimal(rsGST.getDouble(3));
                        String GSTAmt       =twoDecimal(rsGST.getDouble(4));
                        String vLine23_1    =str_pad(TaxCode, len5, " ", "STR_PAD_LEFT");
                        String vLine23_2    =str_pad(TaxRate, len5_1, " ", "STR_PAD_LEFT");
                        String vLine23_3    =str_pad(GoodAmt, len5_2, " ", "STR_PAD_LEFT");
                        String vLine23_4    =str_pad(GSTAmt, len5_3, " ", "STR_PAD_LEFT");
                        if(!GSTNo.equals("NO")) {
                            String vLine23 = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                            strPrint += vLine23 + "\n";
                        }
                    }
                }
            }else if(DBStatus.equals("0")){
                Cursor rsGST=db.getQuery(qGST);
                while (rsGST.moveToNext()) {
                    String TaxRate      =twoDecimal(rsGST.getDouble(0));
                    String TaxCode      =rsGST.getString(1);
                    String GoodAmt      =twoDecimal(rsGST.getDouble(2));
                    String GSTAmt       =twoDecimal(rsGST.getDouble(3));
                    String vLine23_1    =str_pad(TaxCode, len5, " ", "STR_PAD_LEFT");
                    String vLine23_2    =str_pad(TaxRate, len5_1, " ", "STR_PAD_LEFT");
                    String vLine23_3    =str_pad(GoodAmt, len5_2, " ", "STR_PAD_LEFT");
                    String vLine23_4    =str_pad(GSTAmt, len5_3, " ", "STR_PAD_LEFT");
                    if(!GSTNo.equals("NO")) {
                        String vLine23 = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                        strPrint += vLine23 + "\n";
                    }
                }
            }
            //end gst
            strPrint                += vLine+"\n";
            strPrint 				+= "*Goods sold non-returnable & non-refundable\n";
            strPrint 				+= "Thank you, please come again! \n";
            strPrint                += vLine+"\n\n\n";
            return strPrint;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return strPrint;
    }
    private String twoDecimal(Double values){
        String textDecimal="";
        textDecimal=String.format(Locale.US, "%,.2f", values);
        return textDecimal;
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
        else {
            padded = pad.substring(0,resto);
        }
        return padded;
    }

}
