package skybiz.com.posoffline.ui_CashReceipt.m_Pay;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;


/**
 * Created by 7 on 05/12/2017.
 */

public class FnCheckBalance extends AsyncTask<Void,Void, String> {
    Context c;
    String CC1No,IPAddress,DBName,UserName,Password,URL;
    TelephonyManager telephonyManager;
    Double vTotalTopUp,vTotalUsed,vBalance;
    String tBalance;
    public FnCheckBalance(Context c, String CC1No) {
        this.c = c;
        this.CC1No = CC1No;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(Void... params)
    {
        return this.fncheckbalance();
    }

    @Override
    protected void onPostExecute(String vData) {
        super.onPostExecute(vData);
        if(vData==null){
            Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Your balance is :"+vData+" Card No "+CC1No, Toast.LENGTH_SHORT).show();
        }
    }
    private String fncheckbalance() {
        telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        DBAdapter db = new DBAdapter(c);
        db.openDB();
        //get setting
        Cursor cur1=db.getAllSeting();
        while (cur1.moveToNext()) {
            IPAddress = cur1.getString(1);
            UserName = cur1.getString(2);
            Password = cur1.getString(3);
            DBName = cur1.getString(4);
        }
        db.closeDB();
        try {
            URL = "jdbc:mysql://" + IPAddress + "/" + DBName;
            Connection conn = Connector.connect(URL, UserName, Password);
            //Connection conn= Connect_db.getConnection();
            if (conn == null) {

            }
            String queryTotal="select IFNULL(ROUND(sum(Value),2), 0) as iValue from stk_voucher where T_ype='mnuTopUp' And PaymentCode='DC' And CardNo = '"+CC1No+"' ";
            Statement stmtTotal = conn.createStatement();
            stmtTotal.execute(queryTotal);
            ResultSet rsTotal = stmtTotal.getResultSet();
            while (rsTotal.next()) {
                vTotalTopUp = Double.parseDouble(rsTotal.getString("iValue"));
            }
            String queryUsed="Select IFNULL(ROUND(sum(CC1Amt),2),0) as iTotalUsed1, IFNULL(ROUND(sum(CC2Amt),2),0) as iTotalUsed2 From stk_receipt2 Where CC1Code='DC' And CC1No = '"+CC1No+"' ";
            Statement stmtUsed = conn.createStatement();
            stmtUsed.execute(queryUsed);
            ResultSet rsUsed = stmtUsed.getResultSet();
            while (rsUsed.next()) {
                Double iTotalUsed1=Double.parseDouble(rsUsed.getString("iTotalUsed1"));
                Double iTotalUsed2=Double.parseDouble(rsUsed.getString("iTotalUsed2"));
                vTotalUsed = iTotalUsed1+iTotalUsed2;
            }
            vBalance=vTotalTopUp - vTotalUsed;
            tBalance=vBalance.toString();
            return tBalance;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
