package skybiz.com.posoffline.m_ServiceSync.m_SyncIN;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 22/12/2017.
 */

public class Sync_PaymentType extends AsyncTask<Void,Void,String> {
    Context c;
    String IPAddress,UserName,Password,DBName,Port,z,URL,DBStatus,ItemConn,
            PaymentCode,PaymentType,PaidByCompanyYN,Charges1,MerchantCode,MerchantKey;

    public Sync_PaymentType(Context c) {
        this.c = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            JSONObject jsonReq,jsonRes;
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
            db.DelAllPayType();

            if(DBStatus.equals("2")){
                String vQuery = "SELECT PaymentCode,PaymentType, PaidByCompanyYN," +
                        "Charges1, MerchantCode, MerchantKey " +
                        "FROM ret_paymenttype  Order By PaymentType, PaymentCode";
                jsonReq=new JSONObject();
                ConnectorLocal conn = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vQuery);
                jsonReq.put("action", "select");
                String response = conn.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response);
                String rsGroup = jsonRes.getString("hasil");
                JSONArray jaPay = new JSONArray(rsGroup);
                JSONObject joPay = null;
                for (int i = 0; i < jaPay.length(); i++) {
                    joPay = jaPay.getJSONObject(i);
                    PaymentCode     = joPay.getString("PaymentCode");
                    PaymentType     = joPay.getString("PaymentType");
                    PaidByCompanyYN = joPay.getString("PaidByCompanyYN");
                    Charges1        = joPay.getString("Charges1");
                    MerchantCode    = joPay.getString("MerchantCode");
                    MerchantKey     = joPay.getString("MerchantKey");
                    long result = db.addPayType(PaymentCode, PaymentType, Charges1,
                            PaidByCompanyYN,MerchantCode,MerchantKey);
                    if (result > 0) {

                    }
                }
                z = "success";
            }else {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName;
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn == null) {
                    z = "error";
                } else {
                    String vQuery = "SELECT PaymentCode,PaymentType,CC_PaidByCompanyYN," +
                            "Charges1, MerchantCode, MerchantKey " +
                            "FROM ret_paymenttype WHERE PaymentCode <> '' AND Status = 'Active' " +
                            "Order By PaymentType, PaymentCode";
                    Statement stmtPay = conn.createStatement();
                    if (stmtPay.execute(vQuery)) {
                        ResultSet rsPay = stmtPay.getResultSet();
                        int i = 1;
                        while (rsPay.next()) {
                            PaymentCode     = rsPay.getString("PaymentCode");
                            PaymentType     = rsPay.getString("PaymentType");
                            PaidByCompanyYN = rsPay.getString("CC_PaidByCompanyYN");
                            Charges1        = rsPay.getString("Charges1");
                            MerchantCode    = rsPay.getString("MerchantCode");
                            MerchantKey     = rsPay.getString("MerchantKey");
                            long result = db.addPayType(PaymentCode, PaymentType, Charges1,
                                    PaidByCompanyYN,MerchantCode,MerchantKey);
                            if (result > 0) {

                                //
                            }
                            i++;
                        }
                        z = "success";
                    } else {
                        z = "error";
                    }
                    stmtPay.close();
                }
                db.closeDB();
            }
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
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("error")){
            Toast.makeText(c,"Failure sync in data payment type", Toast.LENGTH_SHORT).show();
        }else if(result.equals("success")){
            Toast.makeText(c,"Successfull sync in data payment type", Toast.LENGTH_SHORT).show();
        }else if(result.equals("zero")){
            //data to sync in not found
        }
    }
}
