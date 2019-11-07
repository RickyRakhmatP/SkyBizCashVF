package skybiz.com.posoffline.ui_Setting.m_Sync;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
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
 * Created by 7 on 21/12/2017.
 */

public class Sync_Tax extends AsyncTask<Void,Integer,String> {
    Context c;
    String IPAddress,UserName,Password,URL,DBName,z,Port,DBStatus,ItemConn;
    ProgressBar barTax;
    //ProgressDialog pd;
    public Sync_Tax(Context c,ProgressBar barTax) {
        this.c = c;
        this.barTax=barTax;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //pd=new ProgressDialog(c);
        //pd.setTitle("Synchronize Item");
        //pd.setMessage("{Processing... Please Wait");
       // pd.show();
        barTax.setMax(100);
    }


    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        barTax.setProgress(progress[0]);
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.downloadItem();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
       // pd.dismiss();
        if(result.equals("error")){
            Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Successfull, data item retrieve", Toast.LENGTH_SHORT).show();
            barTax.setProgress(100);
        }
    }

    private String downloadItem(){
        try{
            JSONObject jsonReq,jsonRes;
            DBAdapter db = new DBAdapter(c);
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
            db.DelTax();
            String sql = "select TaxCode, Description, R_ate, TaxType, GSTTaxType, '' as GSTTaxCode from stk_tax ";
            if(DBStatus.equals("2")) {
                jsonReq=new JSONObject();
                ConnectorLocal conn = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", sql);
                jsonReq.put("action", "select");
                String response = conn.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response);
                String resTax = jsonRes.getString("hasil");
                JSONArray jaTax = new JSONArray(resTax);
                JSONObject joTax = null;
                ContentValues cv = new ContentValues();
                for (int i = 0; i < jaTax.length(); i++) {
                    joTax = jaTax.getJSONObject(i);
                    cv.put("TaxCode", joTax.getString("TaxCode"));
                    cv.put("R_ate", joTax.getString("R_ate"));
                    cv.put("TaxType", joTax.getString("TaxType"));
                    cv.put("GSTTaxType", joTax.getString("GSTTaxType"));
                    cv.put("GSTTaxCode", joTax.getString("GSTTaxCode"));
                    cv.put("Description", joTax.getString("Description"));
                    long addTax = db.add_stk_tax(cv);
                    if (addTax > 0) {
                        publishProgress(i);
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
                    Statement statement = conn.createStatement();
                    statement.execute(sql);
                    ResultSet resultSet = statement.getResultSet();
                    ContentValues cv = new ContentValues();
                    int i = 1;
                    while (resultSet.next()) {
                        cv.put("TaxCode", resultSet.getString("TaxCode"));
                        cv.put("R_ate", resultSet.getString("R_ate"));
                        cv.put("TaxType", resultSet.getString("TaxType"));
                        cv.put("GSTTaxType", resultSet.getString("GSTTaxType"));
                        cv.put("GSTTaxCode", resultSet.getString("GSTTaxCode"));
                        cv.put("Description", resultSet.getString("Description"));
                        long addTax = db.add_stk_tax(cv);
                        if (addTax > 0) {
                            publishProgress(i);
                        } else {
                            Log.d("ERROR", resultSet.getString("TaxCode"));
                        }
                        i++;
                    }
                    statement.close();
                    db.closeDB();
                    z = "success";
                }
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
}
