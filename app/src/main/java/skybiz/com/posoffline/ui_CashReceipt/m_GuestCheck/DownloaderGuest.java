package skybiz.com.posoffline.ui_CashReceipt.m_GuestCheck;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_ListSO.ListSOParser;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.OrderParser;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class DownloaderGuest extends AsyncTask<Void,Void,String> {
    Context c;
    RecyclerView rv;
    DialogGuest dialogGuest;
    String IPAddress,UserName,Password,DBName,Port,z,DBStatus,URL;


    public DownloaderGuest(Context c, RecyclerView rv, DialogGuest dialogGuest) {
        this.c = c;
        this.rv = rv;
        this.dialogGuest = dialogGuest;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.downloadData();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result==null){
            Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
        }else{
            GuestParser p=new GuestParser(c,result,rv,dialogGuest);
            p.execute();
        }
    }

    private String downloadData() {
        try {
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
            }
            String vQuery="select Doc1No,Doc2No from stk_sales_order_hd where " +
                    "Status='Waiting' and Doc2No NOT IN('9999','0','') ";

            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    Statement statement = conn.createStatement();
                    JSONArray results = new JSONArray();
                    if (statement.execute(vQuery)) {
                        ResultSet rsSO = statement.getResultSet();
                        while (rsSO.next()) {
                            JSONObject row=new JSONObject();
                            row.put("Doc1No",rsSO.getString(1));
                            row.put("Doc2No",rsSO.getString(2));
                            results.put(row);
                        }
                    }
                    statement.close();
                    return results.toString();
                }
            }else if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vQuery);
                jsonReq.put("action", "select");
                ConnectorLocal connectorLocal=new ConnectorLocal();
                String response=connectorLocal.ConnectSocket(IPAddress,8080,jsonReq.toString());
                jsonRes = new JSONObject(response);
                String result=jsonRes.getString("hasil");
                return result;
            }else {
                JSONArray results = new JSONArray();
                Cursor rsSO=db.getQuery(vQuery);
                while (rsSO.moveToNext()) {
                    JSONObject row=new JSONObject();
                    row.put("Doc1No",rsSO.getString(0));
                    row.put("Doc2No",rsSO.getString(1));
                    results.put(row);
                }
                db.closeDB();
                return results.toString();
            }
            //return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }
}
