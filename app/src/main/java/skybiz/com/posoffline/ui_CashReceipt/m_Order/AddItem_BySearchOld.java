package skybiz.com.posoffline.ui_CashReceipt.m_Order;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 22/12/2017.
 */

public class AddItem_BySearchOld {
    Context context;
    String ItemCode,deviceId,isItem,Description,RunNo,vQty;
    Double Qty,UnitPrice,HCTax, HCDiscount, HCLineAmt,dQty;
    TelephonyManager telephonyManager;

    public AddItem_BySearchOld(Context context, String ItemCode, String vQty) {
        this.context = context;
        this.ItemCode = ItemCode;
        this.vQty = vQty;
    }
    public void additem(){
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();
        DBAdapter db = new DBAdapter(context);
        dQty=0.00;
        db.openDB();
        //ItemCode=ItemCode.toLowerCase();
        String vCheckItem="select Count(*) as jumlah from stk_master where ItemCode='"+ItemCode+"' ";
        Cursor rsItem=db.getQuery(vCheckItem);
        while(rsItem.moveToNext()){
            isItem=rsItem.getString(0);
        }
        if(isItem.equals("0")){
            //return null;
        }else {
            String vCheck = "select  RunNo, Qty, HCUnitCost from cloud_cus_inv_dt where ItemCode='" + ItemCode + "' and ComputerName='" + deviceId + "' ";
            Cursor curCheck = db.getQuery(vCheck);
            while (curCheck.moveToNext()) {
                RunNo       = curCheck.getString(0);
                dQty        = curCheck.getDouble(1);
                UnitPrice   =curCheck.getDouble(2);
            }
            if (dQty == 0.00 ) {
                Qty = 1.00;
                FnAddItem vData = fnadditem(context, ItemCode, Qty, deviceId);
                HCLineAmt = vData.getHCLineAmt();
                HCTax = vData.getHCTax();
                HCDiscount = vData.getHCDiscount();
                Description = vData.getDescription();
            } else {
                Qty=dQty+1.00;
                FnUpdateItem vDataUp = fnupdateitem(context, RunNo, ItemCode, Qty,UnitPrice, deviceId);
                HCLineAmt = vDataUp.getHCLineAmt();
                HCTax = vDataUp.getHCTax();
                HCDiscount = vDataUp.getHCDiscount();
                Description = vDataUp.getDescription();
            }
            db.closeDB();
        }
    }

    //add new item
    public static FnAddItem fnadditem(Context context, String ItemCode, Double Qty, String deviceId){
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
        String sql="Select ROUND(UnitPrice,2) as UnitPrice,DefaultUOM,Description,UOM,UOM1,UOM2,UOM3,UOM4,ROUND(UOMFactor1,2) as UOMFactor1,ROUND(UOMFactor2,2) as UOMFactor2, ROUND(UOMFactor3,2) as UOMFactor3, ROUND(UOMFactor4,2) as UOMFactor4," +
                " ROUND(UOMPrice1,2) as UOMPrice1, ROUND(UOMPrice2,2) as UOMPrice2, ROUND(UOMPrice3,2) as UOMPrice3, ROUND(UOMPrice4,2) UOMPrice4,RetailTaxCode,PurchaseTaxCode, SalesTaxCode from stk_master where ItemCode='"+ItemCode+"' ";
        DBAdapter db = new DBAdapter(context);
        db.openDB();
        Cursor resultSet = db.getQuery(sql);
        while (resultSet.moveToNext()) {
            UnitPrice       = resultSet.getDouble(0);
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
            //UOMPrice4       = Double.parseDouble(resultSet.getString(15));
            RetailTaxCode   = resultSet.getString(16);
        }
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
        DetailTaxCode = RetailTaxCode;
        CalculateLineAmt vDataAmt = fncalculate(context, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
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
                " '" + datedNow + "', '0', 'CS', '0', '"+deviceId+"', '0')";
        db.addQuery(vSQLInsert);
        db.closeDB();
        /*ContentValues cv=new ContentValues();
        cv.put("Doc1No", datedNow);
        cv.put("N_o", "a");
        cv.put("ItemCode", ItemCode);
        cv.put("Description", Description);
        cv.put("Qty", Qty);
        cv.put("FactorQty", FactorQty);
        cv.put("UOM", UOM);
        cv.put("UOMSingular", UOM);
        cv.put("HCUnitCost", UnitPrice);
        cv.put("DisRate1", DisRate1);
        cv.put("HCDiscount", HCDiscount);
        cv.put("TaxRate1", TaxRate1);
        cv.put("HCTax", HCTax);
        cv.put("DetailTaxCode", DetailTaxCode);
        cv.put("HCLineAmt", HCLineAmt);
        cv.put("BranchCode", "android");
        cv.put("DepartmentCode","android");
        cv.put("ProjectCode", "android");
        cv.put("SalesPersonCode", "android");
        cv.put("LocationCode", "android");
        cv.put("WarrantyDate",datedNow);
        cv.put("BlankLine","0");
        cv.put("DocType", "CS");
        cv.put("AnalysisCode2", "0");
        cv.put("ComputerName", deviceId);
        db.addCloud(cv);
        db.closeDB();*/
        return new FnAddItem(HCLineAmt, HCTax, HCDiscount, Qty, ItemCode, Description);
    }

    public static CalculateLineAmt fncalculate(Context context, Double Qty, Double HCUnitCost, Double DisRate1, Double HCDiscount, String DetailTaxCode) {
        Double vR_ate,vHCTax,vAmountB4Tax,vTempAmt,vHCLineAmt,vDisRate1,vHCDiscount;
        String vTaxType;
        vR_ate      =0.00;
        vHCTax      =0.00;
        vHCLineAmt  =0.00;
        vHCDiscount =HCDiscount;
        vDisRate1   =DisRate1;
        if(DisRate1>0){
            vDisRate1		=DisRate1/100;
            vHCDiscount	    =Qty*HCUnitCost*DisRate1;
            vAmountB4Tax	=(Qty*HCUnitCost)-HCDiscount;
        }else{
            if(HCDiscount>0){
                vDisRate1	    =(HCDiscount*100)/(Qty*HCUnitCost);
                vHCDiscount	    = HCDiscount;
                vAmountB4Tax	 =(Qty*HCUnitCost)-HCDiscount;
            }else{
                vHCDiscount	    =0.00;
                vDisRate1	    =0.00;
                vAmountB4Tax	 =(Qty*HCUnitCost)-HCDiscount;
            }
        }
        //URL = "jdbc:mysql://" + IPAddress + "/" + DBName;

        if(DetailTaxCode.isEmpty()) {
            vHCLineAmt   = vAmountB4Tax;
            vR_ate       = 0.00;
            vHCTax       = 0.00;
        }else{
            String strSQL = "select R_ate,TaxType from stk_tax where TaxCode='" + DetailTaxCode + "' ";
            Log.d("RESULT", "query: " + strSQL);
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
        return new CalculateLineAmt(vDisRate1, vHCDiscount, vR_ate, vHCTax, vHCLineAmt);
    }

    //update item
    public static FnUpdateItem fnupdateitem(Context context, String RunNo ,String ItemCode, Double Qty, Double UnitPrice ,String deviceId){
        Double HCTax, HCDiscount, HCLineAmt, DisRate1,TaxRate1,UOMPrice1,UOMPrice2,UOMPrice3,UOMPrice4,UOMFactor1,UOMFactor2,UOMFactor3,UOMFactor4,FactorQty;
        String DetailTaxCode,Description,DefaultUOM,UOM,UOM1,UOM2,UOM3,UOM4,RetailTaxCode,PurchaseTaxCode,SalesTaxCode;
        HCTax         =0.00;
        HCDiscount    =0.00;
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
        DBAdapter db = new DBAdapter(context);
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

        DisRate1 = 0.00;
        HCDiscount = 0.00;
        DetailTaxCode = RetailTaxCode;
        CalculateLineAmt vDataAmt = fncalculate(context, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
        TaxRate1 = vDataAmt.getvR_ate();
        DisRate1 = vDataAmt.getvDisRate1();
        HCTax = vDataAmt.getvHCTax();
        HCLineAmt = vDataAmt.getvHCLineAmt();
        SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String datedNow = DateCurr.format(date);
        //String vSQLUpdate ="UPDATE cloud_cus_inv_dt SET Qty='"+Qty+"', HCUnitCost='"+UnitPrice+"', DisRate1='"+DisRate1+"', HCDiscount='"+HCDiscount+"', HCTax='"+HCTax+"', HCLineAmt='"+HCLineAmt+"' where ItemCode='"+ItemCode+"' and ComputerName='"+deviceId+"'";

        //HCDiscount='"+HCDiscount+"', HCTax='"+HCTax+"', HCLineAmt='"+HCLineAmt+"'
        ContentValues cv=new ContentValues();
        cv.put("Qty", Qty);
        cv.put("HCUnitCost", UnitPrice);
        cv.put("DisRate1", DisRate1);
        cv.put("HCDiscount", HCDiscount);
        cv.put("HCTax", HCTax);
        cv.put("HCLineAmt", HCLineAmt);
        db.UpdateCloud(RunNo,cv);
        return new FnUpdateItem(HCLineAmt, HCTax, HCDiscount, Qty, ItemCode, Description);
    }

}
