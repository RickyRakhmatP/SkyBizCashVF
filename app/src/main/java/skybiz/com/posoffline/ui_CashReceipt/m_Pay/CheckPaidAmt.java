package skybiz.com.posoffline.ui_CashReceipt.m_Pay;

import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Locale;

import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 22/12/2017.
 */

public class CheckPaidAmt {
    Context c;
    String CurCode,vPostGlobalTaxYN;
    String GlobalTaxCode,TaxType,z;
    Double R_ate,HCGbTax,HCDtTax,HCGbDiscount,TotalAmt,GbTaxRate1;
    TelephonyManager telephonyManager;
    public CheckPaidAmt(Context c) {
        this.c = c;
    }

    public String fncheckpaid(){
        GlobalTaxCode = "";
        telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        DBAdapter db = new DBAdapter(c);
        db.openDB();
        Cursor cur = db.getGeneralSetup();
        while (cur.moveToNext()) {
            CurCode = cur.getString(1);
            vPostGlobalTaxYN = cur.getString(6);
        }

        if (vPostGlobalTaxYN.equals("1")) {
            String vDefault = "SELECT a.RetailTaxCode,b.R_ate,b.TaxType,b.TaxCode FROM sys_general_setup3 a INNER JOIN stk_tax b ON a.RetailTaxCode=b.TaxCode";
            Cursor cDef=db.getQuery(vDefault);
            while (cDef.moveToNext()) {
                GlobalTaxCode   = cDef.getString(0);
                R_ate           = Double.parseDouble(cDef.getString(1));
                TaxType         = cDef.getString(2);
            }
        } else {
            TaxType = "0";
            R_ate = 6.00;
        }
        FnCalculateHCNetAmt vNetAmt = fncalculatehcnetamt(c, deviceId, GlobalTaxCode, TaxType, R_ate);
        HCGbTax     = vNetAmt.getHCGbTax();
        TotalAmt    = vNetAmt.getHCNetAmt();
        GbTaxRate1  = vNetAmt.getGbTaxRate1();
        HCDtTax     = vNetAmt.getHCDtTax();
        HCGbDiscount = vNetAmt.getHCGbDiscount();
        if(TotalAmt>0){
            z=String.format(Locale.US, "%,.2f",TotalAmt );
            Log.d("TOTAL",z);
        }else{
            z="error";
        }
        return z;
    }

    public static FnCalculateHCNetAmt fncalculatehcnetamt(Context c, String UUID,String vGlobalTaxCode, String TaxType, Double R_ate){
        Double vHCTax,HCGbDiscount,vHCLineAmt,HCDtTax,HCGbTax,HCNetAmt,GbTaxRate1,AmountB4Tax;
        vHCLineAmt=0.00;
        vHCTax=0.00;
        HCGbDiscount=0.00;
        HCDtTax=0.00;
        HCGbTax=0.00;
        HCNetAmt=0.00;
        GbTaxRate1=0.00;
        Double TotalPoint=0.00;
        DBAdapter db = new DBAdapter(c);
        db.openDB();
        String strSQL="SELECT  IFNULL(sum(HCTax),'0') as vHCTax, IFNULL(sum(HCLineAmt),'0') as vHCLineAmt, " +
                "IFNULL(sum(HCDiscount),'0') as HCGbDiscount FROM cloud_cus_inv_dt WHERE ComputerName = '"+UUID+"' GROUP BY '' ";
        Cursor rsNetAmt=db.getQuery(strSQL);
        while (rsNetAmt.moveToNext()) {
                vHCTax          = rsNetAmt.getDouble(0);
                vHCLineAmt      = rsNetAmt.getDouble(1);
                HCGbDiscount    = rsNetAmt.getDouble(2);
        }

        if(vGlobalTaxCode.equals("")){
            HCDtTax 		= vHCTax;
            HCGbTax 		= 0.00;
            HCNetAmt 		= vHCLineAmt;
            GbTaxRate1		= 0.00;
        }else{
            AmountB4Tax 	= vHCLineAmt;
            HCDtTax			= 0.00;
            GbTaxRate1		= R_ate;
                //0,2 is inclusive
            if(TaxType.equals("0") || TaxType.equals("2")){
                HCGbTax	= AmountB4Tax *(GbTaxRate1/(GbTaxRate1 + 100));
                HCNetAmt	= AmountB4Tax;
            }else if(TaxType.equals("1") || TaxType.equals("3")){
                HCGbTax	= AmountB4Tax *(GbTaxRate1/100);
                HCNetAmt	= AmountB4Tax + HCGbTax;
            }
        }
        return new FnCalculateHCNetAmt(HCNetAmt, HCDtTax, HCGbTax, HCGbDiscount, GbTaxRate1, vGlobalTaxCode, TotalPoint);
    }
}
