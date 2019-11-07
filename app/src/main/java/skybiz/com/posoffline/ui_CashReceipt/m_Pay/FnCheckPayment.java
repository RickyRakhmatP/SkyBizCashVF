package skybiz.com.posoffline.ui_CashReceipt.m_Pay;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;

import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Payment;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;


/**
 * Created by 7 on 29/11/2017.
 */

public class FnCheckPayment extends AsyncTask<Void,String,String>{
    Context c;
    String IPAddress,Password,DBName,UserName,URL,CurCode,vPostGlobalTaxYN;
    String GlobalTaxCode,TaxType,z;
    Double R_ate,HCGbTax,HCDtTax,HCGbDiscount,TotalAmt,GbTaxRate1;
    ProgressDialog pd;
    TelephonyManager telephonyManager;
    Boolean isSuccess = false;
    ArrayList<Spacecraft_Payment> spacecrafts=new ArrayList<>();
    public FnCheckPayment(Context c) {
        this.c = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
      //  pd=new ProgressDialog(c);
       // pd.setTitle("Retrieve All Item");
      //  pd.setMessage("Retrieving... Please Wait");
       // pd.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        try {
            GlobalTaxCode = "";
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            Cursor cur = db.getGeneralSetup();
            while (cur.moveToNext()) {
                CurCode = cur.getString(1);
                vPostGlobalTaxYN = cur.getString(6);
            }
            Cursor curSet = db.getAllSeting();
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(1);
                UserName = curSet.getString(2);
                Password = curSet.getString(3);
                DBName = curSet.getString(4);
            }
            db.closeDB();
            URL = "jdbc:mysql://" + IPAddress + "/" + DBName;
            Connection conn = Connector.connect(URL, UserName, Password);
            if (conn == null) {

            }


            if (vPostGlobalTaxYN.equals("1")) {
                String vDefault = "SELECT a.RetailTaxCode,b.R_ate,b.TaxType,b.TaxCode FROM sys_general_setup3 a INNER JOIN stk_tax b ON a.RetailTaxCode=b.TaxCode";
                Statement stmtDef = conn.createStatement();
                stmtDef.execute(vDefault);
                ResultSet rsDef = stmtDef.getResultSet();
                while (rsDef.next()) {
                    GlobalTaxCode = rsDef.getString("RetailTaxCode");
                    TaxType = rsDef.getString("TaxType");
                    R_ate = Double.parseDouble(rsDef.getString("TaxType"));
                }
                stmtDef.close();
            } else {
                //GlobalTaxCode = "";
                TaxType = "0";
                R_ate = 6.00;
            }
            FnCalculateHCNetAmt vNetAmt = fncalculatehcnetamt(deviceId, GlobalTaxCode, TaxType, R_ate, URL, UserName, Password);
            HCGbTax     = vNetAmt.getHCGbTax();
            TotalAmt    = vNetAmt.getHCNetAmt();
            GbTaxRate1  = vNetAmt.getGbTaxRate1();
            HCDtTax     = vNetAmt.getHCDtTax();
            HCGbDiscount = vNetAmt.getHCGbDiscount();

            if(TotalAmt>0){
                //z=TotalAmt.toString();
                //DecimalFormat df = new DecimalFormat("##.##");
               // TotalAmt=df.format(TotalAmt);
               // z=TotalAmt.toString();
                z=String.format(Locale.US, "%,.2f",TotalAmt );
                Log.d("TOTAL",z);
                isSuccess=true;
            }else{
                z="error";
                isSuccess=false;
            }
            return z;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
        //return null;
        //return this.fnshowpayment();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
       // pd.dismiss();
        if(isSuccess==false){
            Toast.makeText(c,"Empty, calculate total amount due", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Success, calculate total amount due ", Toast.LENGTH_SHORT).show();
            //fnpayment(result);
        }
    }


    public static FnCalculateHCNetAmt fncalculatehcnetamt(String UUID,String vGlobalTaxCode, String TaxType, Double R_ate, String URL, String UserName, String Password){
        Double vHCTax,HCGbDiscount,vHCLineAmt,HCDtTax,HCGbTax,HCNetAmt,GbTaxRate1,AmountB4Tax;
        vHCLineAmt=0.00;
        vHCTax=0.00;
        HCGbDiscount=0.00;
        HCDtTax=0.00;
        HCGbTax=0.00;
        HCNetAmt=0.00;
        GbTaxRate1=0.00;
        Double TotalPoint=0.00;
        try {
            //Log.d("RESULT", "URL: " + URL + ", UserName: " + UserName +", Password: "+Password);
            Connection conn = Connector.connect(URL, UserName, Password);
            if (conn == null) {

            }
            String strSQL="SELECT  ROUND(sum(HCTax),2) as vHCTax, ROUND(sum(HCLineAmt),2) as vHCLineAmt, ROUND(sum(HCDiscount),2) as HCGbDiscount FROM cloud_cus_inv_dt WHERE ComputerName = '"+UUID+"' GROUP BY '' ";
            Statement stmtSum = conn.createStatement();
            stmtSum.execute(strSQL);
            ResultSet rsNetAmt= stmtSum.getResultSet();
            while (rsNetAmt.next()) {
                vHCTax= Double.parseDouble(rsNetAmt.getString("vHCTax"));
                vHCLineAmt=Double.parseDouble(rsNetAmt.getString("vHCLineAmt"));
                Log.d("FN TOTAL ",vHCLineAmt.toString());
                HCGbDiscount=Double.parseDouble(rsNetAmt.getString("HCGbDiscount"));
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

        }catch(SQLException e){
            e.printStackTrace();
        }
        return new FnCalculateHCNetAmt(HCNetAmt, HCDtTax, HCGbTax, HCGbDiscount, GbTaxRate1, vGlobalTaxCode,TotalPoint);
    }
}
