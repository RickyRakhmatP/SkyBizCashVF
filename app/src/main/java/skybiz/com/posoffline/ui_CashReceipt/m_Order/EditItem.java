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

import java.io.UnsupportedEncodingException;
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
 * Created by 7 on 16/04/2018.
 */

public class EditItem extends AsyncTask<Void, Void, String> {
    Context c;
    String ItemCode,Description,vQty,vUnitPrice,vHCDiscount,vDisRate1;
    String IPAddress, UserName, Password,
            DBName,Port,DBStatus,
            ItemConn,URL,z,EncodeType,DepartmentCode,BranchCode,LocationCode,
            UserCode,CounterCode;
    String deviceId,RunNo,UOM,FactorQty;
    Double Qty,UnitPrice,HCTax, HCDiscount, HCLineAmt,dQty,DisRate1;
    TelephonyManager telephonyManager;
    public EditItem(Context c, String RunNo, String itemCode, String vQty, String vUnitPrice, String vHCDiscount, String vDisRate1, String UOM, String FactorQty) {
        this.c = c;
        this.RunNo = RunNo;
        ItemCode = itemCode;
        this.vQty = vQty;
        this.vUnitPrice = vUnitPrice;
        this.vHCDiscount = vHCDiscount;
        this.vDisRate1 = vDisRate1;
        this.UOM = UOM;
        this.FactorQty=FactorQty;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.fnedit();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result==null){
            Toast.makeText(c,"Edit Item Failure", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Edit Item Successfull", Toast.LENGTH_SHORT).show();
        }
    }
    private String fnedit(){
        try{
            //telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
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
            Connection conn=null;
            String vData="";
            Qty         = Double.parseDouble(vQty);
            UnitPrice  = Double.parseDouble(vUnitPrice);
            HCDiscount = Double.parseDouble(vHCDiscount);
            DisRate1   = Double.parseDouble(vDisRate1);
            Double dFactorQty=Double.parseDouble(FactorQty);
            vData = updateItem(RunNo, ItemCode, Qty ,UnitPrice ,HCDiscount, DisRate1,UOM, dFactorQty);
            db.closeDB();
            return vData;
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return null;
    }


    //update item new

    private String updateItem(String RunNo ,String ItemCode, Double Qty, Double UnitPrice ,Double HCDiscount,Double DisRate1, String UOM, Double FactorQty){
        Double HCTax, HCLineAmt,TaxRate1,UOMPrice1,UOMPrice2,UOMPrice3,UOMPrice4,UOMFactor1,UOMFactor2,UOMFactor3,UOMFactor4;
        String DetailTaxCode,Description,DefaultUOM,UOM1,UOM2,UOM3,UOM4,RetailTaxCode,PurchaseTaxCode,SalesTaxCode;
        RetailTaxCode ="";
        Description   ="";
        String vUpdate="";
        JSONObject jsonReq,jsonRes;
        try{
            DBAdapter db=new DBAdapter(c);
            db.openDB();
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
                    " HCLineAmt='" + HCLineAmt + "', UOM='"+UOM+"', FactorQty='"+FactorQty+"' " +
                    " where RunNo ='" + RunNo + "' ";
            Log.d("UPDATE",vSQLUpdate);
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
                Log.d("RES", vSQLUpdate);
                db.updateQuery(vSQLUpdate);
            }
            db.closeDB();
            return vUpdate;
        }catch (JSONException e){
            e.printStackTrace();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return vUpdate;
    }

    //add new item
    private String fnadditem(String ItemCode, Double Qty, String deviceId){
        Double HCTax, HCDiscount, HCLineAmt, DisRate1,TaxRate1,UnitPrice,UOMPrice1,UOMPrice2,UOMPrice3,UOMPrice4,UOMFactor1,UOMFactor2,UOMFactor3,UOMFactor4,FactorQty;
        String DetailTaxCode,Description,DefaultUOM,UOM,UOM1,UOM2,UOM3,UOM4,RetailTaxCode,PurchaseTaxCode,SalesTaxCode;
        HCTax         =0.00;
        HCDiscount    =0.00;
        HCLineAmt     =0.00;
        UnitPrice     =0.00;
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
        String vUnitPrice="";
        String sql="Select RunNo,DefaultUOM,Description,UOM,UOM1," +
                "UOM2,UOM3,UOM4,IFNULL(UOMFactor1,0) as UOMFactor1,IFNULL(UOMFactor2,0) as UOMFactor2," +
                " IFNULL(UOMFactor3,0) as UOMFactor3, IFNULL(UOMFactor4,0) as UOMFactor4," +
                " IFNULL(UOMPrice1,0) as UOMPrice1, IFNULL(UOMPrice2,0) as UOMPrice2, " +
                " IFNULL(UOMPrice3,0) as UOMPrice3, IFNULL(UOMPrice4,0) UOMPrice4," +
                " RetailTaxCode,PurchaseTaxCode, SalesTaxCode," +
                " UnitPrice from stk_master where ItemCode='"+ItemCode+"' ";
        DBAdapter db = new DBAdapter(c);
        db.openDB();
        Cursor resultSet = db.getQuery(sql);
        while (resultSet.moveToNext()) {
            vUnitPrice       = resultSet.getString(19);
            vUnitPrice         =vUnitPrice.replaceAll(",","");
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

        DisRate1 = 0.00;
        HCDiscount = 0.00;
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
        SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String datedNow = DateCurr.format(date);
        String vSQLInsert = "INSERT INTO cloud_cus_inv_dt (Doc1No, N_o, ItemCode, Description, Qty, FactorQty, UOM, UOMSingular," +
                "HCUnitCost, DisRate1, HCDiscount, TaxRate1, HCTax, DetailTaxCode, HCLineAmt," +
                "BranchCode, DepartmentCode, ProjectCode, SalesPersonCode, LocationCode, WarrantyDate, BlankLine, DocType, AnalysisCode2, ComputerName, SORunNo)" +
                "VALUES(" +
                " '" + datedNow + "','a','" + ItemCode + "','" + Description + "','" + Qty + "','" + FactorQty + "','" + UOM + "','" + UOM + "'," +
                " '" + UnitPrice + "','" + DisRate1 + "','" + HCDiscount + "','" + TaxRate1 + "','" + HCTax + "','" + DetailTaxCode + "','" + HCLineAmt + "'," +
                " 'android','android','android','android','android'," +
                " '" + datedNow + "', '0', 'CS', '0', '"+deviceId+"', '0')";
        Log.d("CLOUD DT",vSQLInsert);
        db.addQuery(vSQLInsert);
        db.closeDB();
        return vSQLInsert;
        //return new FnAddItem(HCLineAmt, HCTax, HCDiscount, Qty, ItemCode, Description);
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
        Log.d("RESULT", "HCLineAmt: " + vHCLineAmt.toString());
        return new CalculateLineAmt(DisRate1, vHCDiscount, vR_ate, vHCTax, vHCLineAmt);
    }

    //update item
    public String fnupdateitem(String RunNo ,String ItemCode, Double Qty, Double UnitPrice ,Double HCDiscount,Double DisRate1,String deviceId){
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
        String sql="Select IFNULL(UnitPrice,0) as UnitPrice,DefaultUOM,Description,UOM,UOM1,UOM2,UOM3,UOM4," +
                " IFNULL(UOMFactor1,0) as UOMFactor1,IFNULL(UOMFactor2,0) as UOMFactor2, IFNULL(UOMFactor3,0) as UOMFactor3, IFNULL(UOMFactor4,0) as UOMFactor4," +
                " IFNULL(UOMPrice1,0) as UOMPrice1, IFNULL(UOMPrice2,0) as UOMPrice2, IFNULL(UOMPrice3,0) as UOMPrice3, IFNULL(UOMPrice4,0) UOMPrice4," +
                " RetailTaxCode,PurchaseTaxCode, SalesTaxCode from stk_master where ItemCode='"+ItemCode+"' ";
        DBAdapter db = new DBAdapter(c);
        db.openDB();
        Cursor resultSet = db.getQuery(sql);
        while (resultSet.moveToNext()) {
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
       // DetailTaxCode = RetailTaxCode;
        CalculateLineAmt vDataAmt = fncalculate(c, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
        TaxRate1 = vDataAmt.getvR_ate();
        DisRate1 = vDataAmt.getvDisRate1();
        HCDiscount=vDataAmt.getvHCDiscount();
        HCTax = vDataAmt.getvHCTax();
        HCLineAmt = vDataAmt.getvHCLineAmt();
        SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String datedNow = DateCurr.format(date);
        //String vSQLUpdate ="UPDATE cloud_cus_inv_dt SET Qty='"+Qty+"', HCUnitCost='"+UnitPrice+"', DisRate1='"+DisRate1+"', HCDiscount='"+HCDiscount+"', HCTax='"+HCTax+"', HCLineAmt='"+HCLineAmt+"' where ItemCode='"+ItemCode+"' and ComputerName='"+deviceId+"'";
        ContentValues cv=new ContentValues();
        cv.put("Qty", Qty);
        cv.put("HCUnitCost", UnitPrice);
        cv.put("DisRate1", DisRate1);
        cv.put("HCDiscount", HCDiscount);
        cv.put("HCTax", HCTax);
        cv.put("HCLineAmt", HCLineAmt);
        db.UpdateCloud(RunNo,cv);
       return RunNo;
    }


    //add new item
    public String fnadditem1(String ItemCode, Double Qty, String deviceId){
        Double HCTax, HCDiscount, HCLineAmt, DisRate1,TaxRate1,UnitPrice,UOMPrice1,UOMPrice2,UOMPrice3,UOMPrice4,UOMFactor1,UOMFactor2,UOMFactor3,UOMFactor4,FactorQty;
        String DetailTaxCode,Description,DefaultUOM,UOM,UOM1,UOM2,UOM3,UOM4,RetailTaxCode,PurchaseTaxCode,SalesTaxCode;
        HCTax         =0.00;
        HCDiscount    =0.00;
        HCLineAmt     =0.00;
        UnitPrice     =0.00;
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
        String vUnitPrice="0";
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
            Connection conn= Connector.connect(URL, UserName, Password);
            //Connection conn= Connect_db.getConnection();
            if (conn != null) {
                String sql = "Select DefaultUOM,Description,UOM,UOM1,UOM2,UOM3,UOM4," +
                        " IFNULL(UOMFactor1,0) as UOMFactor1,IFNULL(UOMFactor2,0) as UOMFactor2, IFNULL(UOMFactor3,0) as UOMFactor3, IFNULL(UOMFactor4,0) as UOMFactor4," +
                        " IFNULL(UOMPrice1,0) as UOMPrice1, IFNULL(UOMPrice2,0) as UOMPrice2, IFNULL(UOMPrice3,0) as UOMPrice3, IFNULL(UOMPrice4,0) UOMPrice4," +
                        " RetailTaxCode,PurchaseTaxCode, SalesTaxCode,UnitPrice from stk_master where ItemCode='" + ItemCode + "' ";
                Statement stmtItem = conn.createStatement();
                stmtItem.execute(sql);
                ResultSet rsItem = stmtItem.getResultSet();
                while (rsItem.next()) {
                    vUnitPrice = rsItem.getString(19);
                    vUnitPrice = vUnitPrice.replaceAll(",", "");
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
                UnitPrice = Double.parseDouble(vUnitPrice);
                if (DefaultUOM.equals("0")) {
                    FactorQty = 1.00;
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

                DisRate1 = 0.00;
                HCDiscount = 0.00;
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
                //DetailTaxCode = RetailTaxCode;
                CalculateLineAmt vDataAmt = fncalculate1(URL,UserName,Password, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
                TaxRate1 = vDataAmt.getvR_ate();
                DisRate1 = vDataAmt.getvDisRate1();
                HCTax = vDataAmt.getvHCTax();
                HCLineAmt = vDataAmt.getvHCLineAmt();
                SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String datedNow = DateCurr.format(date);
                String vSQLInsert = "INSERT INTO cloud_cus_inv_dt (Doc1No, N_o, ItemCode, Description, Qty, FactorQty, UOM, UOMSingular," +
                        "HCUnitCost, DisRate1, HCDiscount, TaxRate1, HCTax, DetailTaxCode, HCLineAmt," +
                        "BranchCode, DepartmentCode, ProjectCode, SalesPersonCode, LocationCode, WarrantyDate, BlankLine, DocType, AnalysisCode2, ComputerName, SORunNo)" +
                        "VALUES(" +
                        " '" + datedNow + "','a','" + ItemCode + "','" + Description + "','" + Qty + "','" + FactorQty + "','" + UOM + "','" + UOM + "'," +
                        " '" + UnitPrice + "','" + DisRate1 + "','" + HCDiscount + "','" + TaxRate1 + "','" + HCTax + "','" + DetailTaxCode + "','" + HCLineAmt + "'," +
                        " 'android','android','android','android','android'," +
                        " '" + datedNow + "', '0', 'CS', '0', '" + deviceId + "', '0')";
                Log.d("INSERT",vSQLInsert);
                db.addQuery(vSQLInsert);
                db.closeDB();
                return vSQLInsert;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
        //return new FnAddItem(HCLineAmt, HCTax, HCDiscount, Qty, ItemCode, Description);
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
                    String strSQL = "select IFNULL(R_ate,0) as R_ate,TaxType from stk_tax where TaxCode='" + DetailTaxCode + "' ";
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
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return new CalculateLineAmt(DisRate1, vHCDiscount, vR_ate, vHCTax, vHCLineAmt);
    }


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
            //Connection conn= Connect_db.getConnection();
            if (conn != null) {
                String sql = "Select DefaultUOM,Description,UOM,UOM1," +
                        " UOM2,UOM3,UOM4,IFNULL(UOMFactor1,0) as UOMFactor1,IFNULL(UOMFactor2,0) as UOMFactor2, " +
                        " IFNULL(UOMFactor3,0) as UOMFactor3, IFNULL(UOMFactor4,0) as UOMFactor4," +
                        " IFNULL(UOMPrice1,0) as UOMPrice1, IFNULL(UOMPrice2,0) as UOMPrice2, " +
                        " IFNULL(UOMPrice3,0) as UOMPrice3, IFNULL(UOMPrice4,0) UOMPrice4," +
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
                   //DetailTaxCode = RetailTaxCode;
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
