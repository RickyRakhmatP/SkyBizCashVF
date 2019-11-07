package skybiz.com.posoffline.ui_SalesOrder.m_Order;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.CalculateLineAmt;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class EditItem extends AsyncTask<Void, Void, String> {
    Context c;
    String ItemCode,Description,vQty,vUnitPrice,vHCDiscount,vDisRate1,UOM,FactorQty,RunNo;
    String IPAddress, UserName, Password,DBName,Port,DBStatus,ItemConn,URL,z;
    String deviceId;
    Double Qty,UnitPrice,HCTax, HCDiscount, HCLineAmt,dQty,DisRate1;
    TelephonyManager telephonyManager;
    public EditItem(Context c, String RunNo, String itemCode, String vQty, String vUnitPrice, String vHCDiscount, String vDisRate1,String UOM, String FactorQty) {
        this.c = c;
        this.RunNo = RunNo;
        ItemCode = itemCode;
        this.vQty = vQty;
        this.vUnitPrice = vUnitPrice;
        this.vHCDiscount = vHCDiscount;
        this.vDisRate1 = vDisRate1;
        this.UOM = UOM;
        this.FactorQty = FactorQty;
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
        }
    }
    private String fnadd(){
        try{
            telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = telephonyManager.getDeviceId();
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
                ItemConn=curSet.getString(8);
            }
            dQty=0.00;
            String vCheck="select RunNo,Qty,HCUnitCost from cloud_sales_order_dt where" +
                    " ItemCode='" + ItemCode + "' and ComputerName='" + deviceId + "' ";
            Cursor curCheck = db.getQuery(vCheck);
            while (curCheck.moveToNext()) {
                RunNo      = curCheck.getString(0);
                dQty       =curCheck.getDouble(1);
                UnitPrice  =curCheck.getDouble(2);
            }
            Connection conn=null;
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                conn= Connector.connect(URL, UserName,Password);
            }
            String vData="";
            Qty         = Double.parseDouble(vQty);
            UnitPrice   = Double.parseDouble(vUnitPrice);
            HCDiscount  = Double.parseDouble(vHCDiscount);
            DisRate1    = Double.parseDouble(vDisRate1);
            Double dFactorQty=Double.parseDouble(FactorQty);
            vData = updateItem(RunNo, Qty, UnitPrice,HCDiscount,DisRate1,UOM, dFactorQty);
            db.closeDB();
            return vData;
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return null;
    }

    private String updateItem(String RunNo, Double Qty, Double UnitPrice ,Double HCDiscount,Double DisRate1, String UOM, Double FactorQty){
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
            String vSQLUpdate = "UPDATE cloud_sales_order_dt SET Qty='" + Qty + "'," +
                    " HCUnitCost='" + UnitPrice + "', DisRate1='" + DisRate1 + "'," +
                    " HCDiscount='" + HCDiscount + "', HCTax='" + HCTax + "', " +
                    " HCLineAmt='" + HCLineAmt + "', UOM='"+UOM+"', FactorQty='"+FactorQty+"' " +
                    " where RunNo ='" + RunNo + "' ";

           /* String vSQLUpdate ="UPDATE cloud_sales_order_dt SET Qty='"+Qty+"'," +
                    " HCUnitCost='"+UnitPrice+"'," +
                    " DisRate1='"+DisRate1+"', " +
                    " HCDiscount='"+HCDiscount+"', " +
                    " HCTax='"+HCTax+"', " +
                    " HCLineAmt='"+HCLineAmt+"'" +
                    " where RunNo='"+RunNo+"' ";*/
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
        String sql="Select RunNo,DefaultUOM,Description,UOM,UOM1,UOM2,UOM3,UOM4,IFNULL(UOMFactor1,0) as UOMFactor1,IFNULL(UOMFactor2,0) as UOMFactor2, IFNULL(UOMFactor3,0) as UOMFactor3, IFNULL(UOMFactor4,0) as UOMFactor4," +
                " IFNULL(UOMPrice1,0) as UOMPrice1, IFNULL(UOMPrice2,0) as UOMPrice2, IFNULL(UOMPrice3,0) as UOMPrice3, IFNULL(UOMPrice4,0) UOMPrice4," +
                " RetailTaxCode,PurchaseTaxCode, SalesTaxCode,UnitPrice from stk_master where ItemCode='"+ItemCode+"' ";
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
        HCTax = vDataAmt.getvHCTax();
        HCLineAmt = vDataAmt.getvHCLineAmt();
        SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String datedNow = DateCurr.format(date);
        String vSQLInsert = "INSERT INTO cloud_sales_order_dt (Doc1No, N_o, ItemCode," +
                " Description, Qty, FactorQty," +
                " UOM, UOMSingular, HCUnitCost, " +
                " DisRate1, HCDiscount, TaxRate1, " +
                " HCTax, DetailTaxCode, HCLineAmt," +
                " BranchCode, DepartmentCode, ProjectCode," +
                " SalesPersonCode, LocationCode," +
                " BlankLine, DocType," +
                " ComputerName )" +
                "VALUES(" +
                " '" + datedNow + "', 'a', '" + ItemCode + "'," +
                " '" + Description + "', '" + Qty + "','" + FactorQty + "'," +
                " '" + UOM + "','" + UOM + "', '" + UnitPrice + "'," +
                " '" + DisRate1 + "','" + HCDiscount + "','" + TaxRate1 + "'," +
                " '" + HCTax + "','" + DetailTaxCode + "','" + HCLineAmt + "'," +
                " 'android', 'android', 'android'," +
                " 'android', 'android'," +
                " '0', 'CS', " +
                " '" + deviceId + "')";
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
        Log.d("RESULT", "TAX: " + vHCTax.toString());
        return new CalculateLineAmt(DisRate1, vHCDiscount, vR_ate, vHCTax, vHCLineAmt);
    }

    //update item
    public String fnupdateitem(String RunNo ,String ItemCode, Double Qty, Double UnitPrice ,Double HCDiscount,Double DisRate1,String deviceId, String UOM, Double FactorQty){
        Double HCTax, HCLineAmt,TaxRate1,UOMPrice1,UOMPrice2,UOMPrice3,UOMPrice4,UOMFactor1,UOMFactor2,UOMFactor3,UOMFactor4;
        String DetailTaxCode,Description,DefaultUOM,UOM1,UOM2,UOM3,UOM4,RetailTaxCode,PurchaseTaxCode,SalesTaxCode;
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
        SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String datedNow = DateCurr.format(date);
        String vSQLUpdate ="UPDATE cloud_sales_order_dt SET Qty='"+Qty+"'," +
                " HCUnitCost='"+UnitPrice+"'," +
                " DisRate1='"+DisRate1+"', " +
                " HCDiscount='"+HCDiscount+"', " +
                " HCTax='"+HCTax+"', " +
                " HCLineAmt='"+HCLineAmt+"'" +
                " where RunNo='"+RunNo+"' ";
        db.addQuery(vSQLUpdate);
        db.closeDB();
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
            if (conn != null) {
                String sql = "Select DefaultUOM,Description,UOM,UOM1,UOM2,UOM3,UOM4,ROUND(UOMFactor1,2) as UOMFactor1,ROUND(UOMFactor2,2) as UOMFactor2, ROUND(UOMFactor3,2) as UOMFactor3, ROUND(UOMFactor4,2) as UOMFactor4," +
                        " ROUND(UOMPrice1,2) as UOMPrice1, ROUND(UOMPrice2,2) as UOMPrice2, ROUND(UOMPrice3,2) as UOMPrice3, ROUND(UOMPrice4,2) UOMPrice4,RetailTaxCode,PurchaseTaxCode, SalesTaxCode,UnitPrice from stk_master where ItemCode='" + ItemCode + "' ";
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
                CalculateLineAmt vDataAmt = fncalculate1(URL,UserName,Password, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
                TaxRate1 = vDataAmt.getvR_ate();
                DisRate1 = vDataAmt.getvDisRate1();
                HCTax = vDataAmt.getvHCTax();
                HCLineAmt = vDataAmt.getvHCLineAmt();
                SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String datedNow = DateCurr.format(date);
                String vSQLInsert = "INSERT INTO cloud_sales_order_dt (Doc1No, N_o, ItemCode," +
                        " Description, Qty, FactorQty," +
                        " UOM, UOMSingular, HCUnitCost, " +
                        " DisRate1, HCDiscount, TaxRate1, " +
                        " HCTax, DetailTaxCode, HCLineAmt," +
                        " BranchCode, DepartmentCode, ProjectCode," +
                        " SalesPersonCode, LocationCode," +
                        " BlankLine, DocType," +
                        " ComputerName )" +
                        "VALUES(" +
                        " '" + datedNow + "', 'a', '" + ItemCode + "'," +
                        " '" + Description + "', '" + Qty + "','" + FactorQty + "'," +
                        " '" + UOM + "','" + UOM + "', '" + UnitPrice + "'," +
                        " '" + DisRate1 + "','" + HCDiscount + "','" + TaxRate1 + "'," +
                        " '" + HCTax + "','" + DetailTaxCode + "','" + HCLineAmt + "'," +
                        " 'android', 'android', 'android'," +
                        " 'android', 'android'," +
                        " '0', 'CS', " +
                        " '" + deviceId + "')";
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
                    String vSQLUpdate = "UPDATE cloud_sales_order_dt SET Qty='" + Qty + "'," +
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

}
