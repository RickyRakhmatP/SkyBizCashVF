package skybiz.com.posoffline.m_NewSummary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.CalculateLineAmt;
import skybiz.com.posoffline.ui_CreditNote.CreditNote;
import skybiz.com.posoffline.ui_SalesOrder.SalesOrder;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class AddItem_BySearch extends AsyncTask<Void, Void, String> {
    Context c;
    String ItemCode,Description,vQty,
            vUnitPrice,vHCDiscount,vDisRate1,
            isItem,DocType;

    String IPAddress, UserName, Password,
            DBName,Port,DBStatus,
            ItemConn,URL,z,EncodeType,
            BranchCode,LocationCode;

    String deviceId,RunNo;
    Double Qty,UnitPrice,HCTax, HCDiscount, HCLineAmt,dQty,DisRate1;
    TelephonyManager telephonyManager;

    public AddItem_BySearch(Context c,String DocType, String itemCode, String vQty) {
        this.c = c;
        this.DocType = DocType;
        ItemCode = itemCode;
        this.vQty = vQty;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.fnadd();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result==null){
            Toast.makeText(c,"Add Item Failure", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Add Item Successfull", Toast.LENGTH_SHORT).show();
            refreshData();
        }
    }
    private String fnadd(){
        try{
            JSONObject jsonReq,jsonRes;
            telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = telephonyManager.getDeviceId();
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName, UserName, Password, " +
                    "DBName, Port, DBStatus, " +
                    "ItemConn, EncodeType, BranchCode," +
                    "LocationCode from tb_setting ";
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
                BranchCode=curSet.getString(8);
                LocationCode=curSet.getString(9);
            }
            dQty=0.00;

            String vCheck="select RunNo, Qty, HCUnitCost, HCDiscount, DisRate1 from cloud_cus_inv_dt where" +
                    " ItemCode='" + ItemCode + "' and ComputerName='" + deviceId + "' and DocType='"+DocType+"' ";
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vCheck);
                jsonReq.put("action", "select");
                String response1 = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response1);
                String res = jsonRes.getString("hasil");
                if(!res.equals("0")) {
                    JSONArray jaCheck = new JSONArray(res);
                    JSONObject joCheck = null;
                    for (int i = 0; i < jaCheck.length(); i++) {
                        joCheck = jaCheck.getJSONObject(i);
                        RunNo = joCheck.getString("RunNo");
                        dQty = joCheck.getDouble("QTY");
                        UnitPrice = joCheck.getDouble("HCUnitCost");
                        HCDiscount = joCheck.getDouble("HCDiscount");
                        DisRate1 = joCheck.getDouble("DisRate1");
                    }
                }
            }else {
                Cursor curCheck = db.getQuery(vCheck);
                while (curCheck.moveToNext()) {
                    RunNo = curCheck.getString(0);
                    dQty = curCheck.getDouble(1);
                    vUnitPrice = curCheck.getString(2);
                    vHCDiscount = curCheck.getString(3);
                    vDisRate1 = curCheck.getString(4);
                }
            }
            String vData="";
            if(dQty==0.00){
                Qty=1.00;
                vData = addItem(ItemCode, Qty);
            }else{
                Qty=dQty+1.00;
                UnitPrice  = Double.parseDouble(vUnitPrice);
                HCDiscount = Double.parseDouble(vHCDiscount);
                DisRate1   = Double.parseDouble(vDisRate1);
                vData = updateItem(RunNo, ItemCode, Qty ,UnitPrice ,HCDiscount, DisRate1);
            }
            db.closeDB();
            return vData;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    private String addItem(String ItemCode, Double Qty){
        String vInsert = "";
        String vUnitPrice="";
        JSONObject jsonReq,jsonRes;
        try {
            Double HCTax, HCDiscount, HCLineAmt, DisRate1, TaxRate1,
                    UnitPrice, UOMPrice1, UOMPrice2, UOMPrice3, UOMPrice4,
                    UOMFactor1, UOMFactor2, UOMFactor3, UOMFactor4, FactorQty;
            String DetailTaxCode, Description, DefaultUOM, UOM, UOM1,
                    UOM2, UOM3, UOM4, RetailTaxCode, PurchaseTaxCode,
                    SalesTaxCode;
            HCTax         =0.00;
            HCLineAmt     =0.00;
            UOMPrice1     =0.00;
            UOMPrice2     =0.00;
            UOMPrice3     =0.00;
            UOMPrice4     =0.00;
            UOMFactor1    =0.00;
            UOMFactor2    =0.00;
            UOMFactor3    =0.00;
            UOMFactor4    =0.00;
            FactorQty     =0.00;
            DefaultUOM    ="";
            UOM           ="";
            UOM1          ="";
            UOM2          ="";
            UOM3          ="";
            UOM4          ="";
            RetailTaxCode ="";
            Description   ="";
            DisRate1 = 0.00;
            HCDiscount = 0.00;
            String CurCode="";
            String DUD6="";
            String sql = "Select '' as RunNo,DefaultUOM,Description,UOM,UOM1," +
                    "UOM2,UOM3,UOM4,IFNULL(UOMFactor1,0) as UOMFactor1,IFNULL(UOMFactor2,0) as UOMFactor2," +
                    " IFNULL(UOMFactor3,0) as UOMFactor3, IFNULL(UOMFactor4,0) as UOMFactor4," +
                    " IFNULL(UOMPrice1,0) as UOMPrice1, IFNULL(UOMPrice2,0) as UOMPrice2, " +
                    " IFNULL(UOMPrice3,0) as UOMPrice3, IFNULL(UOMPrice4,0) UOMPrice4," +
                    " RetailTaxCode,PurchaseTaxCode, SalesTaxCode," +
                    " UnitPrice, IFNULL(HCDiscount,0)as HCDiscount, IFNULL(DisRate1)as DisRate1 " +
                    " from stk_master where ItemCode='" + ItemCode + "' ";
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            if (DBStatus.equals("0")) {
                Cursor rsData = db.getQuery(sql);
                while (rsData.moveToNext()) {
                    vUnitPrice = rsData.getString(19);
                    vUnitPrice = vUnitPrice.replaceAll(",", "");
                    DefaultUOM = rsData.getString(1);
                    Description = rsData.getString(2);
                    Description = Description.replace("'", "''");
                    UOM = rsData.getString(3);
                    UOM1 = rsData.getString(4);
                    UOM2 = rsData.getString(5);
                    UOM3 = rsData.getString(6);
                    UOM4 = rsData.getString(7);
                    UOMFactor1 = rsData.getDouble(8);
                    UOMFactor2 = rsData.getDouble(9);
                    UOMFactor3 = rsData.getDouble(10);
                    UOMFactor4 = rsData.getDouble(11);
                    UOMPrice1 = rsData.getDouble(12);
                    UOMPrice2 = rsData.getDouble(13);
                    UOMPrice3 = rsData.getDouble(14);
                    UOMPrice4 = rsData.getDouble(15);
                    RetailTaxCode = rsData.getString(16);
                    HCDiscount = rsData.getDouble(21);
                    DisRate1 = rsData.getDouble(22);
                }
            } else if (DBStatus.equals("1")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    Statement stmtItem = conn.createStatement();
                    stmtItem.executeQuery("SET NAMES 'LATIN1'");
                    stmtItem.executeQuery("SET CHARACTER SET 'LATIN1'");
                    stmtItem.execute(sql);
                    ResultSet rsItem = stmtItem.getResultSet();
                    while (rsItem.next()) {
                        vUnitPrice = rsItem.getString(20);
                        vUnitPrice = vUnitPrice.replaceAll(",", "");
                        DefaultUOM = rsItem.getString(2);
                        Description = encodeChar(rsItem.getString(3));
                        Description = Description.replace("'", "''");
                        UOM = rsItem.getString(4);
                        UOM1 = rsItem.getString(5);
                        UOM2 = rsItem.getString(6);
                        UOM3 = rsItem.getString(7);
                        UOM4 = rsItem.getString(8);
                        UOMFactor1 = rsItem.getDouble(9);
                        UOMFactor2 = rsItem.getDouble(10);
                        UOMFactor3 = rsItem.getDouble(11);
                        UOMFactor4 = rsItem.getDouble(12);
                        UOMPrice1 = rsItem.getDouble(13);
                        UOMPrice2 = rsItem.getDouble(14);
                        UOMPrice3 = rsItem.getDouble(15);
                        UOMPrice4 = rsItem.getDouble(16);
                        RetailTaxCode = rsItem.getString(17);
                        HCDiscount = rsItem.getDouble(21);
                        DisRate1 = rsItem.getDouble(22);
                    }
                }
            } else if (DBStatus.equals("2")) {
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", sql);
                jsonReq.put("action", "select");
                String response1 = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response1);
                String response = jsonRes.getString("hasil");
                JSONArray rsData = new JSONArray(response);
                JSONObject vData = null;
                for (int i = 0; i < rsData.length(); i++) {
                    vData       = rsData.getJSONObject(i);
                    vUnitPrice  = vData.getString("UnitPrice");
                    vUnitPrice  = vUnitPrice.replaceAll(",", "");
                    DefaultUOM  = vData.getString("DefaultUOM");
                    Description = vData.getString("Description");
                    Description = Description.replace("'", "''");
                    UOM         = vData.getString("UOM");
                    UOM1        = vData.getString("UOM1");
                    UOM2        = vData.getString("UOM2");
                    UOM3        = vData.getString("UOM3");
                    UOM4        = vData.getString("UOM4");
                    UOMFactor1  = vData.getDouble("UOMFactor1");
                    UOMFactor2  = vData.getDouble("UOMFactor2");
                    UOMFactor3  = vData.getDouble("UOMFactor3");
                    UOMFactor4  = vData.getDouble("UOMFactor4");
                    UOMPrice1   = vData.getDouble("UOMPrice1");
                    UOMPrice2   = vData.getDouble("UOMPrice2");
                    UOMPrice3   = vData.getDouble("UOMPrice3");
                    UOMPrice4   = vData.getDouble("UOMPrice4");
                    RetailTaxCode = vData.getString("RetailTaxCode");
                    HCDiscount = vData.getDouble("HCDiscount");
                    DisRate1 = vData.getDouble("DisRate1");
                }
            }
            UnitPrice=Double.parseDouble(vUnitPrice);
            if (DefaultUOM.equals("0")) {
                FactorQty = 1.00;
            } else if (DefaultUOM.equals("1")) {
                UnitPrice = UOMPrice1;
                FactorQty = UOMFactor1;
                UOM = UOM1;
            }else if (DefaultUOM.equals("2")) {
                UnitPrice = UOMPrice2;
                FactorQty = UOMFactor2;
                UOM = UOM2;
            }else if (DefaultUOM.equals("3")) {
                UnitPrice = UOMPrice3;
                FactorQty = UOMFactor3;
                UOM = UOM3;
            }else if (DefaultUOM.equals("4")) {
                UnitPrice = UOMPrice4;
                FactorQty = UOMFactor4;
                UOM = UOM4;
            }
            String checkGST="select GSTNo,CurCode from companysetup";
            String GSTYN="";
            Cursor rsGST=db.getQuery(checkGST);
            while(rsGST.moveToNext()){
                GSTYN=rsGST.getString(0);
                CurCode=rsGST.getString(1);
            }
            if(GSTYN.equals("NO")){
                DetailTaxCode="";
            }else{
                DetailTaxCode = RetailTaxCode;
            }
            if(HCDiscount>0 && DisRate1>0){
                DisRate1=0.00;
                DUD6="Discount by amount "+CurCode+HCDiscount;
            }else if(HCDiscount>0 && DisRate1==0){
                DisRate1=0.00;
                DUD6="Discount by percentage "+DisRate1+"%";
            }else if(HCDiscount==0 && DisRate1>0){
                HCDiscount=0.00;
                DUD6="Discount by amount "+CurCode+HCDiscount;
            }
            CalculateLineAmt vDataAmt = fncalculate(c, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
            TaxRate1 = vDataAmt.getvR_ate();
            HCDiscount = vDataAmt.getvHCDiscount();
            DisRate1 = vDataAmt.getvDisRate1();
            HCTax = vDataAmt.getvHCTax();
            HCLineAmt = vDataAmt.getvHCLineAmt();
            SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String datedNow = DateCurr.format(date);
            String vSQLInsert = "INSERT INTO cloud_cus_inv_dt (Doc1No, N_o, ItemCode, Description, " +
                    "Qty, FactorQty, UOM, UOMSingular, " +
                    "HCUnitCost, DisRate1, HCDiscount, TaxRate1, " +
                    "HCTax, DetailTaxCode, HCLineAmt, BranchCode, " +
                    "DepartmentCode, ProjectCode, SalesPersonCode, LocationCode, " +
                    "WarrantyDate, BlankLine, DocType, AnalysisCode2," +
                    "ComputerName, SORunNo, DUD6)" +
                    "VALUES(" +
                    " '" + datedNow + "','a','" + ItemCode + "','" + Description + "','" + Qty + "','" + FactorQty + "','" + UOM + "','" + UOM + "'," +
                    " '" + UnitPrice + "','" + DisRate1 + "','" + HCDiscount + "','" + TaxRate1 + "','" + HCTax + "','" + DetailTaxCode + "','" + HCLineAmt + "'," +
                    " '"+BranchCode+"','android','android','android','"+LocationCode+"'," +
                    " '" + datedNow + "', '0', '"+DocType+"', '0', " +
                    " '"+deviceId+"', '0', '"+DUD6+"')";
            Log.d("CLOUD DT",vSQLInsert);
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal conn1 = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vSQLInsert);
                jsonReq.put("action", "insert");
                String response = conn1.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response);
                String hasil = jsonRes.getString("success");
                Log.d("RES", hasil);
            }else {
                db.addQuery(vSQLInsert);
            }
            db.closeDB();
            return vInsert;
        }catch (SQLException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return vInsert;
    }




    //update item new
    private String updateItem(String RunNo ,String ItemCode, Double Qty, Double UnitPrice ,Double HCDiscount,Double DisRate1){
        Double HCTax, HCLineAmt,TaxRate1,UOMPrice1,UOMPrice2,UOMPrice3,UOMPrice4,UOMFactor1,UOMFactor2,UOMFactor3,UOMFactor4,FactorQty;
        String DetailTaxCode,Description,DefaultUOM,UOM,UOM1,UOM2,UOM3,UOM4,RetailTaxCode,PurchaseTaxCode,SalesTaxCode;
        HCTax         =0.00;
        HCLineAmt     =0.00;
        UOMPrice1     =0.00;
        UOMPrice2     =0.00;
        UOMPrice3     =0.00;
        UOMPrice4     =0.00;
        UOMFactor1    =0.00;
        UOMFactor2    =0.00;
        UOMFactor3    =0.00;
        UOMFactor4    =0.00;
        DefaultUOM    ="";
        UOM           ="";
        UOM1          ="";
        UOM2          ="";
        UOM3          ="";
        UOM4          ="";
        RetailTaxCode ="";
        Description   ="";
        String vUpdate="";
        JSONObject jsonReq,jsonRes;
        try{
            String sql="Select IFNULL(UnitPrice,0) as UnitPrice,DefaultUOM,Description,UOM,UOM1,UOM2,UOM3,UOM4,IFNULL(UOMFactor1,0) as UOMFactor1,IFNULL(UOMFactor2,0) as UOMFactor2, IFNULL(UOMFactor3,0) as UOMFactor3, IFNULL(UOMFactor4,0) as UOMFactor4," +
                    " IFNULL(UOMPrice1,0) as UOMPrice1, IFNULL(UOMPrice2,0) as UOMPrice2, IFNULL(UOMPrice3,0) as UOMPrice3, IFNULL(UOMPrice4,0) UOMPrice4,RetailTaxCode,PurchaseTaxCode, SalesTaxCode from stk_master where ItemCode='"+ItemCode+"' ";
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            if(DBStatus.equals("0")){
                Cursor rsData=db.getQuery(sql);
                while(rsData.moveToNext()){
                    DefaultUOM      = rsData.getString(1);
                    Description     = rsData.getString(2);
                    Description     =Description.replace("'","''");
                    UOM             = rsData.getString(3);
                    UOM1            = rsData.getString(4);
                    UOM2            = rsData.getString(5);
                    UOM3            = rsData.getString(6);
                    UOM4            = rsData.getString(7);
                    UOMFactor1      = rsData.getDouble(8);
                    UOMFactor2      = rsData.getDouble(9);
                    UOMFactor3      = rsData.getDouble(10);
                    UOMFactor4      = rsData.getDouble(11);
                    UOMPrice1       = rsData.getDouble(12);
                    UOMPrice2       = rsData.getDouble(13);
                    UOMPrice3       = rsData.getDouble(14);
                    UOMPrice4       = rsData.getDouble(15);
                    RetailTaxCode   = rsData.getString(16);
                }
            }else if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                if (conn != null) {
                    Statement stmtItem = conn.createStatement();
                    stmtItem.execute(sql);
                    ResultSet rsItem = stmtItem.getResultSet();
                    while (rsItem.next()) {
                        DefaultUOM = rsItem.getString(2);
                        Description = rsItem.getString(3);
                        Description = Description.replace("'", "''");
                        UOM = rsItem.getString(4);
                        UOM1 = rsItem.getString(5);
                        UOM2 = rsItem.getString(6);
                        UOM3 = rsItem.getString(7);
                        UOM4 = rsItem.getString(8);
                        UOMFactor1 = rsItem.getDouble(9);
                        UOMFactor2 = rsItem.getDouble(10);
                        UOMFactor3 = rsItem.getDouble(11);
                        UOMFactor4 = rsItem.getDouble(12);
                        UOMPrice1 = rsItem.getDouble(13);
                        UOMPrice2 = rsItem.getDouble(14);
                        UOMPrice3 = rsItem.getDouble(15);
                        UOMPrice4 = rsItem.getDouble(16);
                        RetailTaxCode = rsItem.getString(17);
                    }
                }
            }else if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", sql);
                jsonReq.put("action", "select");
                String response1 = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response1);
                String response = jsonRes.getString("hasil");
                JSONArray rsData = new JSONArray(response);
                JSONObject vData = null;
                for (int i = 0; i < rsData.length(); i++) {
                    vData           = rsData.getJSONObject(i);
                    DefaultUOM      = vData.getString("DefaultUOM");
                    Description     = vData.getString("Description");
                    Description     = Description.replace("'", "''");
                    UOM             = vData.getString("UOM");
                    UOM1            = vData.getString("UOM1");
                    UOM2            = vData.getString("UOM2");
                    UOM3            = vData.getString("UOM3");
                    UOM4            = vData.getString("UOM4");
                    UOMFactor1      = vData.getDouble("UOMFactor1");
                    UOMFactor2      = vData.getDouble("UOMFactor2");
                    UOMFactor3      = vData.getDouble("UOMFactor3");
                    UOMFactor4      = vData.getDouble("UOMFactor4");
                    UOMPrice1       = vData.getDouble("UOMPrice1");
                    UOMPrice2       = vData.getDouble("UOMPrice2");
                    UOMPrice3       = vData.getDouble("UOMPrice3");
                    UOMPrice4       = vData.getDouble("UOMPrice4");
                    RetailTaxCode   = vData.getString("RetailTaxCode");
                }
            }
           /* if (DefaultUOM.equals("0")) {
                //UnitPrice   =UnitPrice;
                FactorQty = 1.00;
                //UOM         =UOM;
            } else if (DefaultUOM.equals("1")) {
                UnitPrice = UOMPrice1;
                FactorQty = UOMFactor1;
                UOM = UOM1;
            } else if (DefaultUOM.equals("2")) {
                UnitPrice = UOMPrice2;
                FactorQty = UOMFactor2;
                UOM = UOM2;
            } else if (DefaultUOM.equals("3")) {
                UnitPrice = UOMPrice3;
                FactorQty = UOMFactor3;
                UOM = UOM3;
            } else if (DefaultUOM.equals("4")) {
                UnitPrice = UOMPrice4;
                FactorQty = UOMFactor4;
                UOM = UOM4;
            }*/

            String checkGST="select GSTNo from companysetup";
            String GSTYN="";
            Cursor rsGST=db.getQuery(checkGST);
            while(rsGST.moveToNext()){
                GSTYN=rsGST.getString(0);
            }
            if(GSTYN.equals("NO")){
                DetailTaxCode="";
            }else{
                DetailTaxCode = RetailTaxCode;
            }
            CalculateLineAmt vDataAmt = fncalculate(c, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
            TaxRate1 = vDataAmt.getvR_ate();
            DisRate1 = vDataAmt.getvDisRate1();
            HCDiscount = vDataAmt.getvHCDiscount();
            HCTax = vDataAmt.getvHCTax();
            HCLineAmt = vDataAmt.getvHCLineAmt();
            String vSQLUpdate = "UPDATE cloud_cus_inv_dt SET Qty='" + Qty + "'," +
                    " HCUnitCost='" + UnitPrice + "', DisRate1='" + DisRate1 + "'," +
                    " HCDiscount='" + HCDiscount + "', HCTax='" + HCTax + "', " +
                    " HCLineAmt='" + HCLineAmt + "' " +
                    " where RunNo ='" + RunNo + "' ";
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vSQLUpdate);
                jsonReq.put("action", "update");
                String response = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response);
                String hasil = jsonRes.getString("success");
                Log.d("RES", hasil);
            }else {
                db.updateQuery(vSQLUpdate);
            }
            db.closeDB();
            return vUpdate;
        }catch (SQLException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return vUpdate;
    }


    public static CalculateLineAmt fncalculate(Context context, Double Qty, Double HCUnitCost, Double DisRate1, Double HCDiscount, String DetailTaxCode) {
        Double vR_ate,vHCTax,vAmountB4Tax,vTempAmt,vHCLineAmt,vDisRate1,vHCDiscount;
        String vTaxType;
        vR_ate      =0.00;
        vHCTax      =0.00;
        vHCLineAmt  =0.00;
        vHCDiscount =HCDiscount;
        vDisRate1   =0.00;
        if(DisRate1>0){
            vDisRate1		=DisRate1/100;
            vHCDiscount	    =Qty*HCUnitCost*vDisRate1;
            vAmountB4Tax	=(Qty*HCUnitCost)-vHCDiscount;
        }else{
            if(HCDiscount>0){
                vDisRate1	    =(HCDiscount*100)/(Qty*HCUnitCost);
                vHCDiscount	    = HCDiscount;
                vAmountB4Tax	 =(Qty*HCUnitCost)-vHCDiscount;
                DisRate1        =vDisRate1;
            }else{
                vHCDiscount	    =0.00;
                DisRate1	    =0.00;
                vAmountB4Tax	 =(Qty*HCUnitCost)-vHCDiscount;
            }
        }
        //URL = "jdbc:mysql://" + IPAddress + "/" + DBName;

        if(DetailTaxCode.isEmpty()) {
            vHCLineAmt   = vAmountB4Tax;
            vR_ate       = 0.00;
            vHCTax       = 0.00;
        }else{
            String strSQL = "select R_ate,TaxType from stk_tax where TaxCode='" + DetailTaxCode + "' ";
            DBAdapter db = new DBAdapter(context);
            db.openDB();
            Cursor resultSet2 = db.getQuery(strSQL);
            while (resultSet2.moveToNext()) {
                //vR_ate      = Double.parseDouble(resultSet2.getString(0));
                vR_ate      =0.00;
                vTaxType    = resultSet2.getString(1);

                //calculate tax
                if (vTaxType.equals("0") || vTaxType.equals("2")) {
                    vHCTax      =vAmountB4Tax * (vR_ate / (vR_ate + 100));
                    vTempAmt    =vAmountB4Tax - vHCTax;
                    vHCLineAmt  =vTempAmt + vHCTax;
                } else if (vTaxType.equals("1") || vTaxType.equals("3")) {
                    vHCTax      = vAmountB4Tax * (vR_ate / 100);
                    vHCLineAmt  = vAmountB4Tax + vHCTax;
                }
            }
            db.closeDB();
        }
        Log.d("RESULT", "TAX: " + vHCTax.toString());
        return new CalculateLineAmt(DisRate1, vHCDiscount, vR_ate, vHCTax, vHCLineAmt);
    }
    private void refreshData(){
        if(DocType.equals("CusCN")){
            ((CreditNote) c).refreshOrder();
        }else if(DocType.equals("SO")){
            ((SalesOrder) c).refreshOrder();
        }
    }
    private String encodeChar(String txt){
        String newText="";
        try{
            byte[] b=txt.getBytes("ISO-8859-1");
            newText=new String(b,"utf-8");
            return newText;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return newText;
    }
}
