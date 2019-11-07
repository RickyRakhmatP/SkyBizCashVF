package skybiz.com.posoffline.ui_CashReceipt.m_Pay;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 20/12/2017.
 */

public class FnPaymentType extends AsyncTask<Void,Void,String> {
    Context c;
    String IPAddress,UserName,Password,DBName,Port,z,URL,PaymentCode,PaymentType,PaidByCompanyYN,Charges1;

    public FnPaymentType(Context c) {
        this.c = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            Cursor cSetting=db.getAllSeting();
            while (cSetting.moveToNext()) {
                IPAddress   = cSetting.getString(1);
                UserName    = cSetting.getString(2);
                Password    = cSetting.getString(3);
                DBName      = cSetting.getString(4);
            }
            db.DelAllPayType();
            URL = "jdbc:mysql://" + IPAddress + "/" + DBName;
            Connection conn = Connector.connect(URL, UserName, Password);
            if (conn == null) {
                z="error";
            } else {
                String vQuery="SELECT PaymentCode,PaymentType,CC_PaidByCompanyYN," +
                        "Charges1, MerchantCode,MerchantKey "+
                        "FROM ret_paymenttype " +
                        "WHERE PaymentCode <> '' AND Status = 'Active' Order By PaymentType, PaymentCode";
                 Log.d("QUERY",vQuery);
                Statement stmtPay = conn.createStatement();
                if(stmtPay.execute(vQuery)) {
                    ResultSet rsPay = stmtPay.getResultSet();
                    while (rsPay.next()) {

                        PaymentCode     =rsPay.getString("PaymentCode");
                        PaymentType     =rsPay.getString("PaymentType");
                        PaidByCompanyYN =rsPay.getString("CC_PaidByCompanyYN");
                        Charges1        =rsPay.getString("Charges1");
                        String MerchantCode=rsPay.getString("MerchantCode");
                        String MerchantKey=rsPay.getString("MerchantKey");
                        long result=db.addPayType(PaymentCode,PaymentType,Charges1,
                                PaidByCompanyYN,MerchantCode,MerchantKey);
                        if(result>0) {
                            //
                        }else {
                            //
                        }
                    }
                    z = "success";
                }else{
                    z = "error";
                }
                stmtPay.close();
            }
            return z;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("error")){
            Toast.makeText(c,"Empty Retrieve Payment Type", Toast.LENGTH_SHORT).show();
        }else{
           // Toast.makeText(c,"Succesful Retrieve GRN", Toast.LENGTH_SHORT).show();
           // ((Menu_GRN)c).fnloadgrn();
        }
    }
}
