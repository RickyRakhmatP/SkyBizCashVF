package skybiz.com.posoffline.ui_CashReceipt.m_Order;

import android.content.ContentValues;
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 22/12/2017.
 */

public class  MinusItem extends AsyncTask<Void,Void,String>{
    Context c;
    String IPAddress, UserName, Password,
            DBName,Port,DBStatus,
            ItemConn,URL,z,
            EncodeType,BranchCode,LocationCode,
            DepartmentCode,vCategoryCode="",UserCode,
            CounterCode,PriceMatrixDesc="",ServiceChargeYN="1";
    String ItemCode,Description,vQty,vUnitPrice, isItem,RunNo,vHCDiscount,vDisRate1;
    Double Qty,UnitPrice,HCTax, HCDiscount, HCLineAmt,dQty,DisRate1;
    TelephonyManager telephonyManager;
    public MinusItem(Context c, String itemCode) {
        this.c = c;
        ItemCode = itemCode;
    }

   /* public void minusitem(){
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();
        DBAdapter db = new DBAdapter(context);
        dQty=0.00;
        db.openDB();
        String vCheck="select RunNo,Qty,HCUnitCost,HCDiscount,DisRate1 from cloud_cus_inv_dt where ItemCode='" + ItemCode + "' and ComputerName='" + deviceId + "' ";
        Cursor curCheck = db.getQuery(vCheck);
        while (curCheck.moveToNext()) {
            RunNo      = curCheck.getString(0);
            dQty       =curCheck.getDouble(1);
            UnitPrice  =curCheck.getDouble(2);
            HCDiscount  =curCheck.getDouble(3);
            DisRate1  =curCheck.getDouble(4);
        }
        Qty         = dQty-1.00;
        if(Qty==0.00){
            DelItem fndel=new DelItem(context,RunNo);
            fndel.delitem();
        }else{
            FnUpdateItem vDataUp = fnupdateitem(context, RunNo, ItemCode, Qty,UnitPrice,HCDiscount,DisRate1, deviceId);
            HCLineAmt = vDataUp.getHCLineAmt();
            HCTax = vDataUp.getHCTax();
            HCDiscount = vDataUp.getHCDiscount();
            Description = vDataUp.getDescription();
        }
        db.closeDB();
    }*/
   @Override
   protected void onPreExecute() {
       super.onPreExecute();
   }

    @Override
    protected String doInBackground(Void... params) {
        return this.fnminus();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result==null){
            Toast.makeText(c,"Minus Item Failure", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Minus Item Successfull", Toast.LENGTH_SHORT).show();
        }
    }
    private String fnminus() {
        try {
            JSONObject jsonReq,jsonRes;
           // telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
           // deviceId = telephonyManager.getDeviceId();
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName, UserName, Password, " +
                    "DBName, Port, DBStatus, " +
                    "ItemConn, EncodeType, BranchCode," +
                    "LocationCode, DepartmentCode, UserCode," +
                    "CounterCode from tb_setting ";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress       = curSet.getString(0);
                UserName        = curSet.getString(1);
                Password        = curSet.getString(2);
                DBName          = curSet.getString(3);
                Port            = curSet.getString(4);
                DBStatus        =curSet.getString(5);
                ItemConn        =curSet.getString(6);
                EncodeType      =curSet.getString(7);
                BranchCode      =curSet.getString(8);
                LocationCode    =curSet.getString(9);
                DepartmentCode  =curSet.getString(10);
                UserCode        =curSet.getString(11);
                CounterCode     =curSet.getString(12);
            }
            String vCheck="select RunNo,Qty,HCUnitCost,HCDiscount,DisRate1 " +
                    "from cloud_cus_inv_dt where ItemCode='" + ItemCode + "' and ComputerName='" + UserCode + "' ";

            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vCheck);
                jsonReq.put("action", "select");
                String response = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response);
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
                    UnitPrice = curCheck.getDouble(2);
                    HCDiscount = curCheck.getDouble(3);
                    DisRate1 = curCheck.getDouble(4);
                }
            }
            String vDataUp="";
            Qty         = dQty-1.00;
            if(Qty==0.00){
                DelOrder fndel=new DelOrder(c,RunNo);
                fndel.execute();
            }else{
                vDataUp = updateItem(RunNo, ItemCode, Qty, UnitPrice, HCDiscount, DisRate1);
            }
            return vDataUp;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return z;
    }


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
                //Connection conn= Connect_db.getConnection();
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
            vHCDiscount	    =Qty*HCUnitCost*DisRate1;
            vAmountB4Tax	=(Qty*HCUnitCost)-vHCDiscount;
        }else{
            if(HCDiscount>0){
                vDisRate1	    =(HCDiscount*100)/(Qty*HCUnitCost);
                vHCDiscount	    = HCDiscount;
                vAmountB4Tax	=(Qty*HCUnitCost)-vHCDiscount;
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
                vR_ate      = Double.parseDouble(resultSet2.getString(0));
                vTaxType    = resultSet2.getString(1);
                Log.d("RESULT", "taxtype: " + vTaxType);
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

    //update item
    /*public String fnupdateitem(String RunNo ,String ItemCode, Double Qty, Double UnitPrice ,Double HCDiscount,Double DisRate1,String deviceId){
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
        String sql="Select ROUND(UnitPrice,2) as UnitPrice,DefaultUOM,Description,UOM,UOM1,UOM2,UOM3,UOM4,ROUND(UOMFactor1,2) as UOMFactor1,ROUND(UOMFactor2,2) as UOMFactor2, ROUND(UOMFactor3,2) as UOMFactor3, ROUND(UOMFactor4,2) as UOMFactor4," +
                " ROUND(UOMPrice1,2) as UOMPrice1, ROUND(UOMPrice2,2) as UOMPrice2, ROUND(UOMPrice3,2) as UOMPrice3, ROUND(UOMPrice4,2) UOMPrice4,RetailTaxCode,PurchaseTaxCode, SalesTaxCode from stk_master where ItemCode='"+ItemCode+"' ";
        DBAdapter db = new DBAdapter(c);
        db.openDB();
        Cursor resultSet = db.getQuery(sql);
        while (resultSet.moveToNext()) {
            //UnitPrice       = resultSet.getDouble(0);
            DefaultUOM      = resultSet.getString(1);
            Description     = resultSet.getString(2);
            Description     =Description.replace("'","''");
            UOM             = resultSet.getString(3);
            UOM1            = resultSet.getString(4);
            UOM2            = resultSet.getString(5);
            UOM3            = resultSet.getString(6);
            UOM4            = resultSet.getString(7);
            UOMFactor1      = resultSet.getDouble(8);
            UOMFactor2      = resultSet.getDouble(9);
            UOMFactor3      = resultSet.getDouble(10);
            UOMFactor4      = resultSet.getDouble(11);
            UOMPrice1       = resultSet.getDouble(12);
            UOMPrice2       = resultSet.getDouble(13);
            UOMPrice3       = resultSet.getDouble(14);
            UOMPrice4       = resultSet.getDouble(15);
            RetailTaxCode   = resultSet.getString(16);
        }
        if (DefaultUOM.equals("0")) {
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
        }

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
        HCDiscount=vDataAmt.getvHCDiscount();
        HCTax = vDataAmt.getvHCTax();
        HCLineAmt = vDataAmt.getvHCLineAmt();
        String vSQLUpdate ="UPDATE cloud_cus_inv_dt SET Qty='"+Qty+"'," +
                " HCUnitCost='"+UnitPrice+"'," +
                " DisRate1='"+DisRate1+"'," +
                " HCDiscount='"+HCDiscount+"'," +
                " HCTax='"+HCTax+"'," +
                " HCLineAmt='"+HCLineAmt+"' " +
                " where RunNo='"+RunNo+"'";
        db.addQuery(vSQLUpdate);
        return RunNo;
    }


    public static CalculateLineAmt fncalculate1(String URL, String UserName, String Password,
                                                Double Qty, Double HCUnitCost, Double DisRate1, Double HCDiscount, String DetailTaxCode) {
        Double vR_ate,vHCTax,vAmountB4Tax,vTempAmt,vHCLineAmt,vDisRate1,vHCDiscount;
        String vTaxType;
        vR_ate      =0.00;
        vHCTax      =0.00;
        vHCLineAmt  =0.00;
        vHCDiscount =HCDiscount;
        vDisRate1   =DisRate1;
        try {
            if(DisRate1>0){
                vDisRate1		=DisRate1/100;
                vHCDiscount	    =Qty*HCUnitCost*vDisRate1;
                vAmountB4Tax	=(Qty*HCUnitCost)-vHCDiscount;
            }else{
                if(HCDiscount>0){
                    vDisRate1	    =(HCDiscount*100)/(Qty*HCUnitCost);
                    vHCDiscount	    = HCDiscount;
                    vAmountB4Tax	 =(Qty*HCUnitCost)-vHCDiscount;
                }else{
                    vHCDiscount	    =0.00;
                    vDisRate1	    =0.00;
                    vAmountB4Tax	 =(Qty*HCUnitCost)-vHCDiscount;
                }
            }
            Log.d("RESULT", "DISC: " + vHCDiscount.toString());
            Connection conn = Connector.connect(URL, UserName, Password);
            if (conn == null) {

            }else {
                if (DetailTaxCode.isEmpty()) {
                    vHCLineAmt = vAmountB4Tax;
                    vR_ate = 0.00;
                    vHCTax = 0.00;
                } else {
                    String strSQL = "select ROUND(R_ate,2) as R_ate,TaxType from stk_tax where TaxCode='" + DetailTaxCode + "' ";
                    Statement statement2 = conn.createStatement();
                    if (statement2.execute(strSQL)) {
                        ResultSet resultSet2 = statement2.getResultSet();
                        while (resultSet2.next()) {
                            vR_ate = Double.parseDouble(resultSet2.getString("R_ate"));
                            vTaxType = resultSet2.getString("TaxType");
                            Log.d("RESULT", "taxtype: " + vTaxType);
                            //calculate tax
                            if (vTaxType.equals("0") || vTaxType.equals("2")) {
                                vHCTax = vAmountB4Tax * (vR_ate / (vR_ate + 100));
                                vTempAmt = vAmountB4Tax - vHCTax;
                                vHCLineAmt = vTempAmt + vHCTax;
                            } else if (vTaxType.equals("1") || vTaxType.equals("3")) {
                                vHCTax = vAmountB4Tax * (vR_ate / 100);
                                vHCLineAmt = vAmountB4Tax + vHCTax;
                            }
                        }
                    }
                    statement2.close();
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return new CalculateLineAmt(DisRate1, vHCDiscount, vR_ate, vHCTax, vHCLineAmt);
    }
    //update item
    public String fnupdateitem1(String RunNo , String ItemCode, Double Qty, Double UnitPrice,
                                Double HCDiscount,Double DisRate1,String deviceId){
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
        try{
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
            Connection conn= Connector.connect(URL, UserName, Password);
            if (conn != null) {
                String sql = "Select DefaultUOM,Description,UOM,UOM1," +
                        " UOM2,UOM3,UOM4,ROUND(UOMFactor1,2) as UOMFactor1,ROUND(UOMFactor2,2) as UOMFactor2, " +
                        " ROUND(UOMFactor3,2) as UOMFactor3, ROUND(UOMFactor4,2) as UOMFactor4," +
                        " ROUND(UOMPrice1,2) as UOMPrice1, ROUND(UOMPrice2,2) as UOMPrice2, " +
                        " ROUND(UOMPrice3,2) as UOMPrice3, ROUND(UOMPrice4,2) UOMPrice4," +
                        " RetailTaxCode,PurchaseTaxCode, SalesTaxCode,UnitPrice" +
                        " from stk_master where ItemCode='" + ItemCode + "' ";
                Statement stmtItem = conn.createStatement();
                stmtItem.execute(sql);
                ResultSet rsItem = stmtItem.getResultSet();
                while (rsItem.next()) {
                    DefaultUOM = rsItem.getString(1);
                    Description = rsItem.getString(2);
                    Description = Description.replace("'", "''");
                    UOM = rsItem.getString(3);
                    UOM1 = rsItem.getString(4);
                    UOM2 = rsItem.getString(5);
                    UOM3 = rsItem.getString(6);
                    UOM4 = rsItem.getString(7);
                    UOMFactor1 = rsItem.getDouble(8);
                    UOMFactor2 = rsItem.getDouble(9);
                    UOMFactor3 = rsItem.getDouble(10);
                    UOMFactor4 = rsItem.getDouble(11);
                    UOMPrice1 = rsItem.getDouble(12);
                    UOMPrice2 = rsItem.getDouble(13);
                    UOMPrice3 = rsItem.getDouble(14);
                    UOMPrice4 = rsItem.getDouble(15);
                    RetailTaxCode = rsItem.getString(16);
                }
                if (DefaultUOM.equals("0")) {
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
                }

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
                //CalculateLineAmt vDataAmt = fncalculate1(c, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
                CalculateLineAmt vDataAmt = fncalculate1(URL, UserName, Password, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
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
                db.updateQuery(vSQLUpdate);
                db.closeDB();
                stmtItem.close();
                Log.d("UPDATE",vSQLUpdate);
                return vSQLUpdate;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }*/

}
