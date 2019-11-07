package skybiz.com.posoffline.ui_CashReceipt.m_Pay;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TabHost;
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
import java.util.Date;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_NewObject.Decode;
import skybiz.com.posoffline.m_NewObject.DecodeChar;
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
 * Created by 7 on 23/02/2018.
 */

public class FnAddOnOrder extends AsyncTask<Void,Void,String> {
    Context c;
    String TableNo,Attention;
    String IPAddress,Password,DBName,
            UserName,URL,Port,
            z,vPort,DBStatus,
            ItemConn,UserCode;
    String CurCode,vPostGlobalTaxYN,Doc1No;
    TelephonyManager telephonyManager;
    String GlobalTaxCode, TaxType, CusCode,
            EncodeType, CC1No, CC2Code,
            CC2No, LastNo, NewDoc,
            RunNo, stringPrint;
    String TypePrinter, NamePrinter, IPPrinter,
            isDuplicate, EachSlip, PaperSize;
    Double R_ate,HCGbTax,HCDtTax,HCGbDiscount,TotalAmt,GbTaxRate1;
    Boolean isBT;
    String deviceId,datedTime;

    public FnAddOnOrder(Context c, String tableNo) {
        this.c = c;
        TableNo = tableNo;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.fnsaveorder();
    }

    @Override
    protected void onPostExecute(String vData) {
        super.onPostExecute(vData);
        if(vData.equals("error")){
            Toast.makeText(c,"Failed, cannot add on ", Toast.LENGTH_SHORT).show();
        }else{
            ((CashReceipt)c).refreshNext();
        }
    }
    private String fnsaveorder(){
        try{
            JSONObject jsonReq,jsonRes;
            String CusCode="999999";
            GlobalTaxCode = "";
            vPostGlobalTaxYN="0";
            //telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
           // deviceId = telephonyManager.getDeviceId();
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            Cursor cur = db.getGeneralSetup();
            while (cur.moveToNext()) {
                int RunNo = cur.getInt(0);
                CurCode = cur.getString(1);
               // vPostGlobalTaxYN = cur.getString(6);
            }

            String QueryP="select * from tb_kitchenprinter";
            Cursor cPrint=db.getQuery(QueryP);
            while (cPrint.moveToNext()) {
                TypePrinter     = cPrint.getString(1);
                NamePrinter     = cPrint.getString(2);
                IPPrinter       = cPrint.getString(3);
                vPort           = cPrint.getString(5);
                EachSlip        = cPrint.getString(6);
                PaperSize       = cPrint.getString(7);
            }

            String querySet="select ServerName,UserName,Password," +
                    "DBName,Port,DBStatus," +
                    "ItemConn,EncodeType, UserCode" +
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
                UserCode=curSet.getString(8);
            }
            String vMember="select CusCode,CusName,TermCode,D_ay,SalesPersonCode from tb_member ";
            Cursor rsMember=db.getQuery(vMember);
            String vClause="";
            while(rsMember.moveToNext()){
                CusCode=rsMember.getString(0);
                vClause=", CusCode='"+CusCode+"' ";
            }

            String SalesPersonCode="";
            String qSales="select SalesPersonCode from tb_salesperson";
            Cursor rsSales=db.getQuery(qSales);
            while(rsSales.moveToNext()){
                SalesPersonCode=rsSales.getString(0);
            }
            if (vPostGlobalTaxYN.equals("1")) {
                String vDefault = "SELECT a.RetailTaxCode,b.R_ate,b.TaxType,b.TaxCode " +
                        "FROM sys_general_setup3 a INNER JOIN stk_tax b ON a.RetailTaxCode=b.TaxCode";
                Cursor rsDef= db.getQuery(vDefault);
                while (rsDef.moveToNext()) {
                    GlobalTaxCode   = rsDef.getString(0);
                    R_ate           = Double.parseDouble(rsDef.getString(1));
                    TaxType         = rsDef.getString(2);
                }
            } else {
                TaxType = "0";
                R_ate   = 0.00;
            }


            SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat DateCurr1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat DateCurr2 = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            String datedShort = DateCurr.format(date);
            datedTime = DateCurr1.format(date);

            Log.d("DBStatus", DBStatus);
            if(DBStatus.equals("0")){
                String vCheck1="select Doc1No,Attention from " +
                        "stk_sales_order_hd where Status='Waiting' and Doc2No='"+TableNo+"' ";
                Cursor rsCheck1=db.getQuery(vCheck1);
                while (rsCheck1.moveToNext()) {
                    Doc1No      =rsCheck1.getString(0);
                    Attention   =rsCheck1.getString(1);
                }
                //String qDelDt="delete from stk_sales_order_dt where AnalysisCode2='"+TableNo+"' and Doc1No='"+Doc1No+"' ";
                //db.addQuery(qDelDt);

                String vDetail1 = "INSERT INTO stk_sales_order_dt (Doc1No, N_o, ItemCode, Description," +
                        "Qty, FactorQty, UOM, UOMSingular, " +
                        "HCUnitCost, DisRate1, HCDiscount, TaxRate1," +
                        "HCTax, DetailTaxCode, HCLineAmt, BranchCode, " +
                        "DepartmentCode, ProjectCode, SalesPersonCode, LocationCode," +
                        "LineNo, BlankLine, AnalysisCode2, SynYN," +
                        "Description2,Printer,AlternateItem,DUD6," +
                        "ServiceChargeYN, VoidQty)" +
                        "SELECT '" + Doc1No + "', N_o, ItemCode, Description, " +
                        "Qty, FactorQty, UOM, UOMSingular," +
                        "HCUnitCost, DisRate1, HCDiscount, TaxRate1," +
                        "HCTax, DetailTaxCode, HCLineAmt, BranchCode, " +
                        "DepartmentCode, ProjectCode, '"+SalesPersonCode+"' , LocationCode," +
                        "LineNo, BlankLine, '" + TableNo + "', '"+DBStatus+"'," +
                        "Description2,Printer,AlternateItem,DUD6," +
                        "ServiceChargeYN, '0' " +
                        "FROM cloud_cus_inv_dt" +
                        " WHERE ComputerName='" + UserCode + "'" +
                        " and AnalysisCode2='0' ";
                Log.d("QUERYD", vDetail1);
                db.addQuery(vDetail1);

                FnCalculateHCNetAmt vNetAmt = fncalculatehcnetamt(c, Doc1No, GlobalTaxCode, TaxType, R_ate);
                HCGbTax = vNetAmt.getHCGbTax();
                TotalAmt = vNetAmt.getHCNetAmt();
                GbTaxRate1 = vNetAmt.getGbTaxRate1();
                HCDtTax = vNetAmt.getHCDtTax();
                HCGbDiscount = vNetAmt.getHCGbDiscount();
                //update stk_cus_inv_hd
                String vHeader = "UPDATE  stk_sales_order_hd SET  HCGbDiscount='"+HCGbDiscount+"', "+
                        " GbTaxRate1='"+GbTaxRate1+"', " +
                        " HCDtTax='"+HCDtTax+"'," +
                        " HCNetAmt='"+TotalAmt+"' "+vClause+" " +
                        " where Doc1No='"+Doc1No+"' and Doc2No='"+TableNo+"'  ";
                Log.d("QUERY", vHeader);
                db.addQuery(vHeader);
                String vDel = "delete from cloud_cus_inv_dt where ComputerName='"+UserCode+"' ";
                Log.d("DEL CLOUD", vDel);
                db.updateQuery(vDel);

            }else if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    String vCheck1="select Doc1No,Attention from stk_sales_order_hd " +
                            "where Status='Waiting' and Doc2No='"+TableNo+"' ";
                    Statement stmtCheck = conn.createStatement();
                    stmtCheck.execute(vCheck1);
                    ResultSet rsCheck1 = stmtCheck.getResultSet();
                    while (rsCheck1.next()) {
                        Doc1No=rsCheck1.getString(1);
                        Attention=rsCheck1.getString(2);
                    }
                   // String qDelDt="delete from stk_sales_order_dt where AnalysisCode2='"+TableNo+"' and Doc1No='"+Doc1No+"' ";
                   // db.addQuery(qDelDt);
                    String vDetail1 = "INSERT INTO stk_sales_order_dt (Doc1No, N_o, ItemCode, Description," +
                            "Qty, FactorQty, UOM, UOMSingular, " +
                            "HCUnitCost, DisRate1, HCDiscount, TaxRate1," +
                            "HCTax, DetailTaxCode, HCLineAmt, BranchCode, " +
                            "DepartmentCode, ProjectCode, SalesPersonCode, LocationCode," +
                            "LineNo, BlankLine, AnalysisCode2, SynYN," +
                            "Description2, Printer, AlternateItem, DUD6," +
                            "ServiceChargeYN)" +
                            "SELECT '" + Doc1No + "', N_o, ItemCode, Description, " +
                            "Qty, FactorQty, UOM, UOMSingular," +
                            "HCUnitCost, DisRate1, HCDiscount, TaxRate1," +
                            "HCTax, DetailTaxCode, HCLineAmt, BranchCode, " +
                            "DepartmentCode, ProjectCode, '"+SalesPersonCode+"', LocationCode," +
                            "LineNo, BlankLine, '" + TableNo + "', '"+DBStatus+"'," +
                            "Description2,Printer, AlternateItem, DUD6," +
                            "ServiceChargeYN " +
                            "FROM cloud_cus_inv_dt " +
                            "WHERE ComputerName='" + UserCode + "' " +
                            "and N_o='a'  " +
                            "and AnalysisCode2='0' ";
                    Log.d("QUERYD LOCAL", vDetail1);
                    db.addQuery(vDetail1);

                    FnCalculateHCNetAmt vNetAmt = fncalculatehcnetamt(c, Doc1No, GlobalTaxCode, TaxType, R_ate);
                    HCGbTax = vNetAmt.getHCGbTax();
                    TotalAmt = vNetAmt.getHCNetAmt();
                    GbTaxRate1 = vNetAmt.getGbTaxRate1();
                    HCDtTax = vNetAmt.getHCDtTax();
                    HCGbDiscount = vNetAmt.getHCGbDiscount();
                    //update stk_cus_inv_hd
                    String vHeader1 = "UPDATE  stk_sales_order_hd SET  HCGbDiscount='"+HCGbDiscount+"', "+
                            " GbTaxRate1='"+GbTaxRate1+"'," +
                            " HCDtTax='"+HCDtTax+"'," +
                            " HCNetAmt='"+TotalAmt+"'  "+vClause+" " +
                            " where Doc1No='"+Doc1No+"' and Doc2No='"+TableNo+"'  ";
                    Log.d("QUERYUP LOCAL", vHeader1);
                    db.addQuery(vHeader1);
                    //end local

                   // Statement stmDelDt = conn.createStatement();
                    //stmDelDt.execute(qDelDt);
                    String QueryD="select '"+Doc1No+"', N_o, ItemCode," +
                            " Description, Qty, FactorQty, " +
                            " UOM, UOMSingular, HCUnitCost, " +
                            " DisRate1, HCDiscount, TaxRate1," +
                            " HCTax, DetailTaxCode, HCLineAmt," +
                            " BranchCode, DepartmentCode, ProjectCode," +
                            " '"+SalesPersonCode+"', LocationCode, BlankLine, " +
                            " '"+TableNo+"', Description2, DUD6," +
                            " ServiceChargeYN, Printer " +
                            " from cloud_cus_inv_dt" +
                            " where ComputerName= '"+UserCode+"' " +
                            " and N_o='a' " +
                            " and AnalysisCode2='0' ";
                    Cursor rsDt=db.getQuery(QueryD);
                    int i=1;
                    while(rsDt.moveToNext()) {
                        String vDetail = "INSERT INTO stk_sales_order_dt (" +
                                "Doc1No, N_o, ItemCode, " +
                                "Description, Qty, FactorQty, " +
                                "UOM, UOMSingular, HCUnitCost, " +
                                "DisRate1, HCDiscount, TaxRate1," +
                                "HCTax, DetailTaxCode, HCLineAmt, " +
                                "BranchCode, DepartmentCode, ProjectCode," +
                                "SalesPersonCode, LocationCode, BlankLine," +
                                "AnalysisCode2, LineNo, Description2, " +
                                "DUD6, ServiceChargeYN, Printer)" +
                                "Values(" +
                                "'"+rsDt.getString(0)+"', '"+rsDt.getString(1)+"', '"+rsDt.getString(2)+"'," +
                                "'"+Decode.setChar(EncodeType,rsDt.getString(3))+"', '"+rsDt.getString(4)+"', '"+rsDt.getString(5)+"'," +
                                "'"+Decode.setChar(EncodeType,rsDt.getString(6))+"', '"+Decode.setChar(EncodeType,rsDt.getString(7))+"', '"+rsDt.getString(8)+"'," +
                                "'"+rsDt.getString(9)+"', '"+rsDt.getString(10)+"', '"+rsDt.getString(11)+"'," +
                                "'"+rsDt.getString(12)+"', '"+rsDt.getString(13)+"', '"+rsDt.getString(14)+"'," +
                                "'"+rsDt.getString(15)+"', '"+rsDt.getString(16)+"', '"+rsDt.getString(17)+"'," +
                                "'"+rsDt.getString(18)+"', '"+rsDt.getString(19)+"', '"+rsDt.getString(20)+"'," +
                                "'"+rsDt.getString(21)+"', '"+i+"', '"+ Decode.setChar(EncodeType,rsDt.getString(22))+"'," +
                                " '"+rsDt.getString(23)+"', '"+rsDt.getString(24)+"', '"+rsDt.getString(25)+"')";
                        Log.d("QUERYD", vDetail);
                        Statement stmtDetail = conn.createStatement();
                        stmtDetail.execute(vDetail);
                        stmtDetail.close();
                        i++;
                    }

                    FnCalculateHCNetAmt vNetAmt1 = fncalculatehcnetamt1(Doc1No, GlobalTaxCode, TaxType, R_ate, URL, UserName, Password);
                    HCGbTax = vNetAmt1.getHCGbTax();
                    TotalAmt = vNetAmt1.getHCNetAmt();
                    GbTaxRate1 = vNetAmt1.getGbTaxRate1();
                    HCDtTax = vNetAmt1.getHCDtTax();
                    HCGbDiscount = vNetAmt1.getHCGbDiscount();

                    //insert stk_cus_inv_hd
                    String vHeader = "UPDATE  stk_sales_order_hd SET  HCGbDiscount='"+HCGbDiscount+"', "+
                            " GbTaxRate1='"+GbTaxRate1+"', " +
                            " HCDtTax='"+HCDtTax+"', " +
                            " HCNetAmt='"+TotalAmt+"' "+vClause+" " +
                            " where  Doc1No='"+Doc1No+"' " +
                            " and Doc2No='"+TableNo+"'  ";
                    Log.d("QUERY", vHeader);
                    Statement stmtHeader = conn.createStatement();
                    stmtHeader.execute(vHeader);
                }

                String vDel = "delete from cloud_cus_inv_dt where ComputerName='"+UserCode+"' ";
                Log.d("DEL CLOUD", vDel);
                db.updateQuery(vDel);
            }else if(DBStatus.equals("2")){
                //check doc1no
                String vCheck1="select Doc1No from stk_sales_order_hd where Status='Waiting' and Doc2No='"+TableNo+"' ";
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vCheck1);
                jsonReq.put("action", "select");
                String resCheck = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(resCheck);
                String rsCheck = jsonRes.getString("hasil");
                String DupTable = "";
                if(!rsCheck.equals("0")) {
                    JSONArray rsData = new JSONArray(rsCheck);
                    JSONObject vData = null;
                    for (int i = 0; i < rsData.length(); i++) {
                        vData = rsData.getJSONObject(i);
                        Doc1No=vData.getString("Doc1No");
                    }
                }
                //insert dt
                String vDetail1 = "INSERT INTO stk_sales_order_dt (Doc1No, N_o, ItemCode, Description," +
                        "Qty, FactorQty, UOM, UOMSingular, " +
                        "HCUnitCost, DisRate1, HCDiscount, TaxRate1," +
                        "HCTax, DetailTaxCode, HCLineAmt, BranchCode, " +
                        "DepartmentCode, ProjectCode, SalesPersonCode, LocationCode," +
                        "LineNo, BlankLine, AnalysisCode2, SynYN," +
                        "Description2,Printer, AlternateItem, DUD6," +
                        " VoidQty, ServiceChargeYN)" +
                        "SELECT '" + Doc1No + "', N_o, ItemCode, Description, " +
                        "Qty, FactorQty, UOM, UOMSingular," +
                        "HCUnitCost, DisRate1, HCDiscount, TaxRate1," +
                        "HCTax, DetailTaxCode, HCLineAmt, BranchCode, " +
                        "DepartmentCode, ProjectCode, SalesPersonCode, LocationCode," +
                        "LineNo, BlankLine, '" + TableNo + "', '"+DBStatus+"'," +
                        "Description2,Printer, AlternateItem, DUD6," +
                        " '0', '1' " +
                        "FROM cloud_cus_inv_dt WHERE ComputerName='" + UserCode + "' and N_o='a' ";
                Log.d("QUERYD", vDetail1);
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vDetail1);
                jsonReq.put("action", "insert");
                String resIn = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(resIn);
                String rsInDt = jsonRes.getString("hasil");

                FnCalculateHCNetAmt vNetAmt = fncalculatehcnetamt(c, Doc1No, GlobalTaxCode, TaxType, R_ate);
                HCGbTax = vNetAmt.getHCGbTax();
                TotalAmt = vNetAmt.getHCNetAmt();
                GbTaxRate1 = vNetAmt.getGbTaxRate1();
                HCDtTax = vNetAmt.getHCDtTax();
                HCGbDiscount = vNetAmt.getHCGbDiscount();

                //update stk_cus_inv_hd
                String vHeader = "UPDATE  stk_sales_order_hd SET  HCGbDiscount='"+HCGbDiscount+"', "+
                        " GbTaxRate1='"+GbTaxRate1+"', HCDtTax='"+HCDtTax+"', HCNetAmt='"+TotalAmt+"'" +
                        " where Doc1No='"+Doc1No+"' and Doc2No='"+TableNo+"'  ";
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vHeader);
                jsonReq.put("action", "insert");
                String resInH = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(resInH);
                String rsInH = jsonRes.getString("hasil");
                Log.d("QUERY", vHeader+rsInH);

                //del cloud
                String vDel = "delete from cloud_cus_inv_dt where ComputerName='"+UserCode+"' ";
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vDel);
                jsonReq.put("action", "insert");
                String resDel = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(resDel);
                String rsDel = jsonRes.getString("hasil");
                Log.d("DEL CLOUD", vDel+rsDel);
            }

            String qDel="delete from dum_stk_sales_order_hd";
            db.addQuery(qDel);
            db.closeDB();
            getPrinter();
           /* if (TypePrinter.equals("AIDL")) {
                stringPrint = fngenerate58(Doc1No, datedShort, URL, UserName, Password);
            } else {
                stringPrint = fngenerate78(Doc1No, datedShort, URL, UserName, Password);
            }

            if (TypePrinter.equals("AIDL")) {
                AidlUtil.getInstance().printText(stringPrint);
            } else if (TypePrinter.equals("Bluetooth")) {
                BluetoothPrinter fncheck = new BluetoothPrinter();
                isBT = fncheck.fnBluetooth(c, NamePrinter, stringPrint);
                if (isBT == false) {
                    NewDoc = "error print";
                }
            }else if (TypePrinter.equals("Bluetooth Zebra")) {
                BluetoothZebra fncheck = new BluetoothZebra();
                isBT = fncheck.fnBluetooth(c, NamePrinter, stringPrint);
                if (isBT == false) {
                    NewDoc = "error print";
                }
            } else if (TypePrinter.equals("Wifi")) {
                PrintingWifi fnprintw = new PrintingWifi();
                int Port=Integer.parseInt(vPort);
                isBT = fnprintw.fnprintwifi(c, IPPrinter, Port, stringPrint);
                if (isBT == false) {
                    NewDoc = "error print";
                }
            } else if (TypePrinter.equals("USB")) {
                PrintingUSB fnprintu = new PrintingUSB();
                isBT = fnprintu.fnprintusb(c, stringPrint);
                if (isBT == false) {
                    NewDoc = "error print";
                }
            } else {
                //No Printer
            }*/
            z = "success";
            return z;
        }catch (SQLException e){
            e.printStackTrace();
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return z;
    }
    private void getPrinter(){
        try{
            JSONObject jsonReq,jsonRes;
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String sql="select GROUP_CONCAT(RunNo,',')as RunNo, IFNULL(Printer,'')as Printer " +
                    "from stk_sales_order_dt where Doc1No='"+Doc1No+"' and N_o='a'  and BlankLine='0' Group By Printer ";
            if(DBStatus.equals("2")) {
                jsonReq = new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", sql);
                jsonReq.put("action", "select");
                String resCheck = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(resCheck);
                String rsCheck = jsonRes.getString("hasil");
                if (!rsCheck.equals("0")) {
                    JSONArray rsData = new JSONArray(rsCheck);
                    JSONObject vData = null;
                    for (int i = 0; i < rsData.length(); i++) {
                        vData = rsData.getJSONObject(i);
                        String RunNo = vData.getString("RunNo");
                        String Printer = vData.getString("Printer");
                        if (Printer.equals("")) {
                            String strPrint = genPrint(RunNo, "AIDL",TypePrinter);
                            fnPrinting(strPrint);
                        } else {
                            String strPrint = genPrint(RunNo, Printer,"Wifi");
                            fnPrinting2(strPrint, Printer);
                        }
                    }
                }
            }else {
                Cursor rsData = db.getQuery(sql);
                while (rsData.moveToNext()) {
                    String RunNo = rsData.getString(0);
                    String Printer = rsData.getString(1);
                    if (Printer.equals("")) {
                        String strPrint = genPrint(RunNo, "AIDL",TypePrinter);
                        fnPrinting(strPrint);
                    } else {
                        String strPrint = genPrint(RunNo, Printer,"Wifi");
                        fnPrinting2(strPrint, Printer);
                    }
                }
            }
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    private void fnPrinting(String strPrint){
        try {
            Log.d("PRINT", strPrint);
            if (TypePrinter.equals("AIDL")) {
                AidlUtil.getInstance().printText(strPrint);
            }else if(TypePrinter.equals("Ipos AIDL")){

                IposAidlUtil.getInstance().setPrint(strPrint);
            } else if (TypePrinter.equals("Bluetooth")) {
                BluetoothPrinter fncheck = new BluetoothPrinter();
                isBT = fncheck.fnBluetooth(c, NamePrinter, strPrint);
                if (isBT == false) {
                    NewDoc = "error print";
                }
            } else if (TypePrinter.equals("Bluetooth Zebra")) {
                BluetoothZebra fncheck = new BluetoothZebra();
                isBT = fncheck.fnBluetooth(c, NamePrinter, strPrint);
                if (isBT == false) {
                    NewDoc = "error print";
                }
            } else if (TypePrinter.equals("Wifi")) {
                int Port = Integer.parseInt(vPort);
                String[] StrPrint = strPrint.split("cutpaper");
                for (String add : StrPrint) {
                    int count = 1;
                    while (count < 2) {
                        PrintingWifi fnprintw = new PrintingWifi();
                        isBT = fnprintw.fnprintwifi(c, IPPrinter, Port, add);
                        if (isBT == false) {
                            NewDoc = "error print";
                        }
                        ++count;
                        Thread.sleep(1500);
                    }
                }
                /*PrintingWifi fnprintw = new PrintingWifi();
                int Port = Integer.parseInt(vPort);
                isBT = fnprintw.fnprintwifi(c, IPPrinter, Port, strPrint);
                if (isBT == false) {
                    NewDoc = "error print";
                }*/
            } else if (TypePrinter.equals("USB")) {
                PrintingUSB fnprintu = new PrintingUSB();
                isBT = fnprintu.fnprintusb(c, strPrint);
                if (isBT == false) {
                    NewDoc = "error print";
                }
            } else {
                //No Printer
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void fnPrinting2(String strPrint,String Printer){
        try {
            int Port = Integer.parseInt("9100");
            String[] StrPrint = strPrint.split("cutpaper");
            for (String add : StrPrint) {
                int count = 1;
                while (count < 2) {
                    PrintingWifi fnprintw = new PrintingWifi();
                    isBT = fnprintw.fnprintwifi(c, Printer, Port, add);
                    if (isBT == false) {
                        NewDoc = "error print";
                    }
                    ++count;
                    Thread.sleep(1500);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private String genPrint(String RunNo,String Printer, String PrinterType2){
        String vStr="";
        try{
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
            String vLine5_2="";
            String vLine53="";
            String vLine61="";
            String vLine62="";
            String vLine63="";
            int lenH=32;
            int lenM=8;
            int len =48;
            int len1=16;
            int len1_1=26;
            int len1_2=6;
            int len2=6;
            int len3=41;
            if(PrinterType2.equals("AIDL") || PrinterType2.equals("Ipos AIDL")) {
                lenM = 4;
                lenH = 24;
                len = 32;
                len1 = 14;
                len1_2 = 6;
                len1_1 = 12;
                len3 = 25;
            }else if(PrinterType2.equals("Wifi")){
                 lenH=30;
                 lenM=6;
                 len =42;
                 len1=12;
                 len1_1=24;
                 len1_2=6;
                 len2=6;
                 len3=35;
            }
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String strSQL = "SELECT H.Doc1No,D.AnalysisCode2, IFNULL(SUM(D.Qty),'0')as Pax " +
                    "FROM stk_sales_order_hd H inner join stk_sales_order_dt D ON D.Doc1No=H.Doc1No" +
                    " where H.Doc1No='" + Doc1No + "' and D.N_o='a' Group By H.Doc1No ";
            String Pax="";
            if(DBStatus.equals("2")){
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
                }
            }else{
                Cursor rsHeader = db.getQuery(strSQL);
                while (rsHeader.moveToNext()) {
                    Pax = rsHeader.getString(2);
                }
            }
            String vLine =str_pad("_", len, "_", "STR_PAD_BOTH");
            String vTine =str_pad("-", len, "-", "STR_PAD_BOTH");
            vLine0 = str_pad("* ORDER SLIP *", lenH, " ", "STR_PAD_BOTH");
            vLine0_1 = str_pad(" ", lenM, " ", "STR_PAD_BOTH");
            vLine1 = str_pad("==== ADD ON ====", lenH, " ", "STR_PAD_BOTH");
            vLine2 = str_pad("TABLE#:" + TableNo + "  Pax:" + Pax, lenH, " ", "STR_PAD_BOTH");
            vLine3 = str_pad(Attention, len, " ", "STR_PAD_BOTH");
            vLine4 = str_pad("Check  : " + datedTime, len, " ", "STR_PAD_RIGHT");
            vLine5 = str_pad("Printer: " + Printer, len, " ", "STR_PAD_RIGHT");
            vStr += vLine0_1+vLine0+vLine0_1+ "\n";
            vStr += vLine0_1+vLine1+vLine0_1+ "\n";
            vStr += vLine0_1+vLine2 +vLine0_1+ "\n";
            if(!Attention.isEmpty()) {
                vStr += vLine3 + "\n";
            }
            vStr += vLine + "\n";
            vStr += vLine4 + "\n";
            vStr += vLine5 + "\n";
            vStr += vLine + "\n";

            String strSQLD = "select IFNULL(Qty, '0') as vQty, SUBSTR(Description,1,"+len1_1+") as Description, " +
                    "Description2, UOM, AlternateItem " +
                    "from stk_sales_order_dt Where RunNo IN("+RunNo+") ";
            String strUp = "update stk_sales_order_dt set N_o='0' where RunNo IN( "+ RunNo + ") and BlankLine='0'  ";
            if(DBStatus.equals("2")){
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
                    vLine5          = str_pad(TableNo+" [  ] "+vData.getString("vQty")+" "+vData.getString("UOM"), len1, " ", "STR_PAD_LEFT");
                    vLine51         = str_pad(vData.getString("Description"), len1_1, " ", "STR_PAD_RIGHT");
                    vLine52         = str_pad(AlternateItem, len3, " ", "STR_PAD_RIGHT");
                    vLine62         = str_pad("(" + Modifier + ")", len3, " ", "STR_PAD_RIGHT");
                    vLine61         = str_pad("", len2, " ", "STR_PAD_LEFT");
                    vStr            += vLine51 + vLine5+"\n";
                    if(AlternateItem.length()>1){
                        vStr        += vLine52+"\n";
                    }
                    if (Modifier.length() > 1) {
                        vStr        +=  vLine62 +"\n";
                    }
                    vStr += vTine+"\n";
                    if(PrinterType2.equals("Wifi") && EachSlip.equals("Each Item One Slip")){
                        vStr += "\n cutpaper";
                    }else{
                        vStr += "";
                    }
                }
                jsonReq=new JSONObject();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", strUp);
                jsonReq.put("action", "update");
                String rsUp = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rsUp);
                String resultUp = jsonRes.getString("hasil");
            }else{
                Cursor rsDet = db.getQuery(strSQLD);
                while (rsDet.moveToNext()) {
                    String Modifier = rsDet.getString(2);
                    String AlternateItem = rsDet.getString(4);
                    vLine5 = str_pad("x"+rsDet.getString(0)+" "+rsDet.getString(3), len1, " ", "STR_PAD_RIGHT");
                    vLine51 = str_pad(rsDet.getString(1), len1_1, " ", "STR_PAD_RIGHT");
                    if(EachSlip.equals("Each Item One Slip")) {
                        vLine5_2 = str_pad(TableNo, len1_2, " ", "STR_PAD_RIGHT");
                    }else{
                        vLine5_2 = str_pad("", len1_2, " ", "STR_PAD_RIGHT");
                    }
                    vLine52 = str_pad(AlternateItem, len3, " ", "STR_PAD_RIGHT");
                    vLine62 = str_pad("(" + Modifier + ")", len3, " ", "STR_PAD_RIGHT");
                    vLine61 = str_pad("", len2, " ", "STR_PAD_LEFT");
                    vStr += vLine5+vLine51 +vLine5_2+"\n";
                    if (AlternateItem.length() > 1) {
                        vStr        += vLine52+"\n";
                    }
                    if (Modifier.length() > 1) {
                        vStr        += vLine62 +"\n";
                    }
                    vStr += vTine+"\n";
                    if(PrinterType2.equals("Wifi") && EachSlip.equals("Each Item One Slip")){
                        vStr += "\n cutpaper";
                    }else{
                        vStr += "";
                    }
                }
                db.addQuery(strUp);
            }
          //  vStr += vTine+"\n";
            //vStr += str_pad(Doc1No, len, " ", "STR_PAD_BOTH")+"\n";
            if(PrinterType2.equals("Wifi") && EachSlip.equals("Each Group One Slip")){
                vStr += "\n cutpaper";
            }
            if(PrinterType2.equals("AIDL") || PrinterType2.equals("Ipos AIDL")) {
                vStr += "\n\n\n\n";
            }
            db.closeDB();
            return vStr;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return vStr;
    }
    /*private String fnsaveorder(){
        String CusCode="999999";
        GlobalTaxCode = "";
        telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();
        DBAdapter db = new DBAdapter(c);
        db.openDB();
        Cursor cur = db.getGeneralSetup();
        while (cur.moveToNext()) {
            int RunNo = cur.getInt(0);
            CurCode = cur.getString(1);
            vPostGlobalTaxYN = cur.getString(6);
        }
        String QueryP="select * from tb_kitchenprinter";
        Cursor cPrint=db.getQuery(QueryP);
        while (cPrint.moveToNext()) {
            TypePrinter = cPrint.getString(1);
            NamePrinter = cPrint.getString(2);
            IPPrinter   = cPrint.getString(3);
            vPort       = cPrint.getString(5);
        }

        String vCheck="select Doc1No from stk_sales_order_hd where Status='Waiting' and Doc2No='"+TableNo+"' ";
        Cursor rsCheck=db.getQuery(vCheck);
        while (rsCheck.moveToNext()) {
            Doc1No=rsCheck.getString(0);
        }
                    //insert stk_cus_inv detail
        String vDetail = "INSERT INTO stk_sales_order_dt (Doc1No, N_o, ItemCode, Description, Qty, FactorQty, UOM, UOMSingular, " +
                            "HCUnitCost, DisRate1, HCDiscount, TaxRate1, HCTax, DetailTaxCode, HCLineAmt, " +
                            "BranchCode, DepartmentCode, ProjectCode, SalesPersonCode, LocationCode,LineNo, BlankLine, AnalysisCode2, SynYN)" +
                            "SELECT '" + Doc1No + "', N_o, ItemCode, Description, Qty, FactorQty, UOM, UOMSingular, " +
                            "HCUnitCost, DisRate1, HCDiscount, TaxRate1, HCTax, DetailTaxCode, HCLineAmt, " +
                            "BranchCode, DepartmentCode, ProjectCode, SalesPersonCode, LocationCode,LineNo, BlankLine, '" + TableNo + "', '0' " +
                            "FROM cloud_cus_inv_dt WHERE ComputerName='" + deviceId + "' ";

        Log.d("QUERYD", vDetail);
        db.addQuery(vDetail);
        String vDelete = "DELETE FROM cloud_cus_inv_dt where ComputerName='" + deviceId + "' ";
        Log.d("DEL", vDelete);
        db.addQuery(vDelete);

        if (vPostGlobalTaxYN.equals("1")) {
            String vDefault = "SELECT a.RetailTaxCode,b.R_ate,b.TaxType,b.TaxCode FROM sys_general_setup3 a INNER JOIN stk_tax b ON a.RetailTaxCode=b.TaxCode";
            Cursor rsDef= db.getQuery(vDefault);
            while (rsDef.moveToNext()) {
                GlobalTaxCode   = rsDef.getString(0);
                R_ate           = Double.parseDouble(rsDef.getString(1));
                TaxType         = rsDef.getString(2);
            }
        } else {
            TaxType = "0";
            R_ate = 6.00;
        }

        FnCalculateHCNetAmt vNetAmt = fncalculatehcnetamt(c, deviceId, GlobalTaxCode, TaxType, R_ate);
        HCGbTax = vNetAmt.getHCGbTax();
        TotalAmt = vNetAmt.getHCNetAmt();
        GbTaxRate1 = vNetAmt.getGbTaxRate1();
        HCDtTax = vNetAmt.getHCDtTax();
        HCGbDiscount = vNetAmt.getHCGbDiscount();

        SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat DateCurr1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat DateCurr2 = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String datedShort = DateCurr.format(date);
        String datedTime = DateCurr1.format(date);
        String vTime = DateCurr2.format(date);
                //insert stk_cus_inv_hd
        String vHeader = "UPDATE  stk_sales_order_hd SET  HCGbDiscount='"+HCGbDiscount+"', "+
                        " GbTaxRate1='"+GbTaxRate1+"', HCDtTax='"+HCDtTax+"', HCNetAmt='"+TotalAmt+"' where Doc1No='"+Doc1No+"' and Doc2No='"+TableNo+"'  ";
        Log.d("QUERY", vHeader);
        db.addQuery(vHeader);

        if (TypePrinter.equals("AIDL")) {
            stringPrint = fngenerate58(Doc1No, datedShort, URL, UserName, Password);
        } else {
            stringPrint = fngenerate78(Doc1No, datedShort, URL, UserName, Password);
        }

        if (TypePrinter.equals("AIDL")) {
            byte[] bytes = stringPrint.getBytes();
            String content2 = BytesUtil.getHexStringFromBytes(bytes);
            AidlUtil.getInstance().sendRawData(BytesUtil.getBytesFromHexString(content2));
        } else if (TypePrinter.equals("Bluetooth")) {
            BluetoothPrinter fncheck = new BluetoothPrinter();
            isBT = fncheck.fnBluetooth(c, NamePrinter, stringPrint);
            if (isBT == false) {
                            NewDoc = "error print";
            }
        }else if (TypePrinter.equals("Bluetooth Zebra")) {
            BluetoothZebra fncheck = new BluetoothZebra();
            isBT = fncheck.fnBluetooth(c, NamePrinter, stringPrint);
            if (isBT == false) {
                NewDoc = "error print";
            }
        } else if (TypePrinter.equals("Wifi")) {
            PrintingWifi fnprintw = new PrintingWifi();
            int Port=Integer.parseInt(vPort);
            isBT = fnprintw.fnprintwifi(c, IPPrinter, Port, stringPrint);
            if (isBT == false) {
                NewDoc = "error print";
            }
        } else if (TypePrinter.equals("USB")) {
            PrintingUSB fnprintu = new PrintingUSB();
            isBT = fnprintu.fnprintusb(c, stringPrint);
            if (isBT == false) {
                NewDoc = "error print";
            }
        } else {
            //No Printer
        }
        z = "success";
        return z;
    }*/

    public String fngenerate58(String Doc1No,String Dated,String URL, String UserName,String Password){
        String vStr="";
        String vLine0="";
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
        try {
            JSONObject jsonReq,jsonRes;
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String strSQL = "SELECT H.Doc1No, D.AnalysisCode2, IFNULL(SUM(D.Qty),'0')as Pax " +
                    "FROM stk_sales_order_hd H inner join stk_sales_order_dt D ON D.Doc1No=H.Doc1No" +
                    " where H.Doc1No='" + Doc1No + "' and D.N_o='a' Group By H.Doc1No ";
            String Pax="";
            if (DBStatus.equals("2")) {
                jsonReq = new JSONObject();
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
                }
            } else {
                Cursor rsHeader = db.getQuery(strSQL);
                while (rsHeader.moveToNext()) {
                     Pax = rsHeader.getString(2);
                }
            }
            vLine0 = str_pad("* ORDER SLIP *", 32, " ", "STR_PAD_BOTH");
            vLine1 = str_pad("===== NEW =====", 32, " ", "STR_PAD_BOTH");
            vLine2 = str_pad("TABLE#: " + TableNo + "    Pax : " + Pax, 32, " ", "STR_PAD_RIGHT");
            vLine3 = str_pad("Chk   : " + datedTime, 32, " ", "STR_PAD_RIGHT");
            vLine4 = str_pad("Print : " + deviceId, 32, " ", "STR_PAD_RIGHT");
            vStr += vLine0 + "\n";
            vStr += vLine1 + "\n\n\n";
            vStr += vLine2 + "\n";
            vStr += "________________________________\n";
            vStr += vLine3 + "\n";
            vStr += vLine4 + "\n";
            vStr += "________________________________\n";

            String strSQLD = "select IFNULL(Qty, '0') as vQty, SUBSTR(Description,1,18) as Description," +
                    " Description2, UOM " +
                    " from stk_sales_order_dt Where Doc1No='" + Doc1No + "' and N_o='a' ";
            String strUp = "update stk_sales_order_dt set N_o='0' where Doc1No='" + Doc1No + "'   ";
            if(DBStatus.equals("2")){
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
                    vLine5          = str_pad(vData.getString("vQty")+""+vData.getString("UOM"), 8, " ", "STR_PAD_RIGHT");
                    vLine51         = str_pad(vData.getString("Description"), 18, " ", "STR_PAD_RIGHT");
                    vLine52         = str_pad("D", 2, " ", "STR_PAD_LEFT");
                    vLine53         = str_pad("[  ]", 4, " ", "STR_PAD_LEFT");
                    vLine61         = str_pad("(" + Modifier + ")", 32, " ", "STR_PAD_LEFT");
                    vStr            += vLine5 + vLine51 + vLine52 + vLine53 + "\n";
                    if (Modifier.length() > 1) {
                        vStr        += vLine61 + "\n";
                    }
                    vStr += "--------------------------------\n";
                }
                jsonReq=new JSONObject();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", strUp);
                jsonReq.put("action", "update");
                String rsUp = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rsUp);
                String resultUp = jsonRes.getString("hasil");
            }else {
                Cursor rsDet = db.getQuery(strSQLD);
                while (rsDet.moveToNext()) {
                    vLine5 = str_pad(rsDet.getString(0)+" "+rsDet.getString(3), 8, " ", "STR_PAD_RIGHT");
                    vLine51 = str_pad(rsDet.getString(1), 18, " ", "STR_PAD_RIGHT");
                    vLine52 = str_pad("D", 2, " ", "STR_PAD_LEFT");
                    vLine53 = str_pad("[  ]", 4, " ", "STR_PAD_LEFT");
                    String Modifier = rsDet.getString(2);
                    vLine62 = str_pad("(" + Modifier + ")", 32, " ", "STR_PAD_RIGHT");
                    vStr += vLine5 + vLine51 + vLine52 + vLine53 + "\n";
                    if (Modifier.length() > 1) {
                        vStr += vLine62 + "\n";
                    }
                    vStr += "--------------------------------\n";
                }
                db.addQuery(strUp);
            }
            vStr += "--------------------------------\n\n\n\n\n";
            db.closeDB();
            return vStr;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return vStr;
    }

    public String fngenerate78(String Doc1No,String Dated,String URL, String UserName,String Password){
        String vStr="";
        String vLine0="";
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
        try {
            JSONObject jsonReq,jsonRes;
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String strSQL = "SELECT H.Doc1No, D.AnalysisCode2, IFNULL(SUM(D.Qty),'0')as Pax " +
                    "FROM stk_sales_order_hd H inner join stk_sales_order_dt D ON D.Doc1No=H.Doc1No" +
                    " where H.Doc1No='" + Doc1No + "' and D.N_o='a' Group By H.Doc1No ";
            String Pax="";
            if(DBStatus.equals("2")) {
                jsonReq = new JSONObject();
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
                }
            }else {
                Cursor rsHeader = db.getQuery(strSQL);
                while (rsHeader.moveToNext()) {
                     Pax = rsHeader.getString(2);
                }
            }
            vLine0 = str_pad("* ORDER SLIP *", 48, " ", "STR_PAD_BOTH");
            vLine1 = str_pad("===== NEW =====", 48, " ", "STR_PAD_BOTH");
            vLine2 = str_pad("TABLE# : " + TableNo + "    Pax : " + Pax, 48, " ", "STR_PAD_RIGHT");
            vLine3 = str_pad("Chk    : " + datedTime, 48, " ", "STR_PAD_RIGHT");
            vLine4 = str_pad("Print  : " + deviceId, 48, " ", "STR_PAD_RIGHT");
            vStr += vLine0 + "\n";
            vStr += vLine1 + "\n\n\n";
            vStr += vLine2 + "\n";
            vStr += "________________________________________________\n";
            vStr += vLine3 + "\n";
            vStr += vLine4 + "\n";
            vStr += "________________________________________________\n";

            String strSQLD = "select IFNULL(Qty, '0') as vQty, SUBSTR(Description,1,33) as Description," +
                    "Description2, UOM " +
                    "from stk_sales_order_dt Where Doc1No='" + Doc1No + "' and N_o='a' ";
            String strUp = "update stk_sales_order_dt set N_o='0' where Doc1No='" + Doc1No + "'   ";
            if(DBStatus.equals("2")){
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
                    vLine5          = str_pad(vData.getString("vQty")+" "+vData.getString("UOM"), 8, " ", "STR_PAD_RIGHT");
                    vLine51         = str_pad(vData.getString("Description"), 33, " ", "STR_PAD_RIGHT");
                    vLine52         = str_pad("D", 3, " ", "STR_PAD_LEFT");
                    vLine53         = str_pad("[  ]", 4, " ", "STR_PAD_LEFT");
                    vLine61         = str_pad("(" + Modifier + ")", 48, " ", "STR_PAD_LEFT");
                    vStr            += vLine5 + vLine51 + vLine52 + vLine53 + "\n";
                    if (Modifier.length() > 1) {
                        vStr        += vLine61 + "\n";
                    }
                    vStr += "------------------------------------------------\n";
                }
                jsonReq=new JSONObject();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", strUp);
                jsonReq.put("action", "update");
                String rsUp = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rsUp);
                String resultUp = jsonRes.getString("hasil");
            }else {
                Cursor rsDet = db.getQuery(strSQLD);
                while (rsDet.moveToNext()) {
                    vLine5 = str_pad(rsDet.getString(0)+" "+rsDet.getString(3), 8, " ", "STR_PAD_RIGHT");
                    vLine51 = str_pad(rsDet.getString(1), 33, " ", "STR_PAD_RIGHT");
                    vLine52 = str_pad("D", 3, " ", "STR_PAD_LEFT");
                    vLine53 = str_pad("[   ]", 4, " ", "STR_PAD_LEFT");
                    String Modifier = rsDet.getString(2);
                    vLine61 = str_pad("(" + Modifier + ")", 48, " ", "STR_PAD_RIGHT");
                    vStr += vLine5 + vLine51 + vLine52 + vLine53 + "\n";
                    if (Modifier.length() > 1) {
                        vStr += vLine63 + "\n";
                    }
                    vStr += "------------------------------------------------\n";
                }
                db.addQuery(strUp);
            }
            vStr += "------------------------------------------------\n\n\n\n\n";
            db.closeDB();
            return vStr;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return vStr;
    }

    public static FnCalculateHCNetAmt fncalculatehcnetamt1(String Doc1No, String GlobalTaxCode, String TaxType, Double R_ate, String URL, String UserName, String Password) {
        Double vHCTax, HCGbDiscount, vHCLineAmt, HCDtTax, HCGbTax, HCNetAmt, GbTaxRate1, AmountB4Tax;
        vHCLineAmt = 0.00;
        vHCTax = 0.00;
        HCGbDiscount = 0.00;
        HCDtTax = 0.00;
        HCGbTax = 0.00;
        HCNetAmt = 0.00;
        GbTaxRate1 = 0.00;
        try {
            Connection conn = Connector.connect(URL, UserName, Password);
            //Connection conn= Connect_db.getConnection();
            if (conn == null) {

            }
            String strSQL = "SELECT  ROUND(sum(HCTax),2) as vHCTax, ROUND(sum(HCLineAmt),2) as vHCLineAmt, " +
                    "ROUND(sum(HCDiscount),2) as HCGbDiscount FROM stk_sales_order_dt WHERE Doc1No = '" + Doc1No + "'" +
                    " GROUP BY '' ";
            Statement stmtSum = conn.createStatement();
            stmtSum.execute(strSQL);
            ResultSet rsNetAmt = stmtSum.getResultSet();
            while (rsNetAmt.next()) {
                vHCTax = Double.parseDouble(rsNetAmt.getString("vHCTax"));
                vHCLineAmt = Double.parseDouble(rsNetAmt.getString("vHCLineAmt"));
                HCGbDiscount = Double.parseDouble(rsNetAmt.getString("HCGbDiscount"));
            }

            if (GlobalTaxCode.equals("")) {
                HCDtTax = vHCTax;
                HCGbTax = 0.00;
                HCNetAmt = vHCLineAmt;
                GbTaxRate1 = 0.00;
            } else {
                AmountB4Tax = vHCLineAmt;
                HCDtTax = 0.00;
                GbTaxRate1 = R_ate;
                //0,2 is inclusive
                if (TaxType.equals("0") || TaxType.equals("2")) {
                    HCGbTax = AmountB4Tax * (GbTaxRate1 / (GbTaxRate1 + 100));
                    HCNetAmt = AmountB4Tax;
                } else if (TaxType.equals("1") || TaxType.equals("3")) {
                    HCGbTax = AmountB4Tax * (GbTaxRate1 / 100);
                    HCNetAmt = AmountB4Tax + HCGbTax;
                }
            }
            stmtSum.close();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return new FnCalculateHCNetAmt(HCNetAmt, HCDtTax, HCGbTax,
                HCGbDiscount, GbTaxRate1, GlobalTaxCode, 0.00);
    }



    public static FnCalculateHCNetAmt fncalculatehcnetamt(Context c, String Doc1No,String vGlobalTaxCode, String TaxType, Double R_ate){
        Double vHCTax,HCGbDiscount,vHCLineAmt,HCDtTax,HCGbTax,HCNetAmt,GbTaxRate1,AmountB4Tax;
        vHCLineAmt      =0.00;
        vHCTax          =0.00;
        HCGbDiscount    =0.00;
        HCDtTax         =0.00;
        HCGbTax         =0.00;
        HCNetAmt        =0.00;
        GbTaxRate1      =0.00;
        Double TotalPoint      =0.00;
        String IPAddress="";
        String DBStatus ="";
        try {
            JSONObject jsonReq,jsonRes;
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String querySet="select * from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(1);
                DBStatus=curSet.getString(7);
            }
           /* String strSQL = "SELECT  IFNULL(sum(HCTax),0) as vHCTax, IFNULL(sum(HCLineAmt),0) as vHCLineAmt," +
                    " IFNULL(sum(HCDiscount),0) as HCGbDiscount FROM stk_sales_order_dt " +
                    " WHERE Doc1No = '" + Doc1No + "' GROUP BY '' ";*/

            String strSQL = "SELECT  ROUND(sum(HCTax),2) as vHCTax, ROUND(sum(HCLineAmt),2) as vHCLineAmt, " +
                    "ROUND(sum(HCDiscount),2) as HCGbDiscount FROM stk_sales_order_dt WHERE Doc1No = '" + Doc1No + "'" +
                    " GROUP BY '' ";

            if(DBStatus.equals("2")) {
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", strSQL);
                jsonReq.put("action", "select");
                String response1 = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response1);
                String response = jsonRes.getString("hasil");
                JSONArray rsData = new JSONArray(response);
                JSONObject vData = null;
                for (int i = 0; i < rsData.length(); i++) {
                    vData           = rsData.getJSONObject(i);
                    vHCTax          = vData.getDouble("vHCTax");
                    vHCLineAmt      = vData.getDouble("vHCLineAmt");
                    HCGbDiscount    = vData.getDouble("HCGbDiscount");
                }

            }else{
                Cursor rsNetAmt = db.getQuery(strSQL);
                while (rsNetAmt.moveToNext()) {
                    vHCTax = rsNetAmt.getDouble(0);
                    vHCLineAmt = rsNetAmt.getDouble(1);
                    HCGbDiscount = rsNetAmt.getDouble(2);
                }
            }

            if (vGlobalTaxCode.equals("")) {
                HCDtTax = vHCTax;
                HCGbTax = 0.00;
                HCNetAmt = vHCLineAmt;
                GbTaxRate1 = 0.00;
            } else {
                AmountB4Tax = vHCLineAmt;
                HCDtTax     = 0.00;
                GbTaxRate1 = R_ate;
                //0,2 is inclusive
                if (TaxType.equals("0") || TaxType.equals("2")) {
                    HCGbTax = AmountB4Tax * (GbTaxRate1 / (GbTaxRate1 + 100));
                    HCNetAmt = AmountB4Tax;
                } else if (TaxType.equals("1") || TaxType.equals("3")) {
                    HCGbTax = AmountB4Tax * (GbTaxRate1 / 100);
                    HCNetAmt = AmountB4Tax + HCGbTax;
                }
            }
            return new FnCalculateHCNetAmt(HCNetAmt, HCDtTax, HCGbTax, HCGbDiscount, GbTaxRate1, vGlobalTaxCode,TotalPoint);
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return new FnCalculateHCNetAmt(HCNetAmt, HCDtTax, HCGbTax,
                HCGbDiscount, GbTaxRate1, vGlobalTaxCode,
                TotalPoint);
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
