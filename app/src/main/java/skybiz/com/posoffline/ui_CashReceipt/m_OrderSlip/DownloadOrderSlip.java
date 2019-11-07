package skybiz.com.posoffline.ui_CashReceipt.m_OrderSlip;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloadOrderSlip extends AsyncTask<Void,Void,String> {
    Context c;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
    String CurCode;
    Dialog_OrderSlip dialogOrderSlip;
    public DownloadOrderSlip(Context c, RecyclerView rv, Dialog_OrderSlip dialogOrderSlip) {
        this.c = c;
        this.rv = rv;
        this.dialogOrderSlip=dialogOrderSlip;
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
            OrderSlipParser p=new OrderSlipParser(c,result,rv,dialogOrderSlip);
            p.execute();
        }
    }

    private String downloadData(){
        try {
            JSONObject jsonReq,jsonRes;
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            Cursor cur = db.getGeneralSetup();
            while (cur.moveToNext()) {
                CurCode = cur.getString(1);
            }
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
            Log.d("ItemConn",ItemConn);
            if(DBStatus.equals("1")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    String query = "select Doc1No,Doc2No from stk_sales_order_hd " +
                            "where Status='Waiting'  and Doc2No NOT IN('9999','0','') ";
                    JSONArray results = new JSONArray();
                    Statement statement = conn.createStatement();
                    if (statement.execute(query)) {
                        ResultSet resultSet = statement.getResultSet();
                        ResultSetMetaData columns = resultSet.getMetaData();
                        while (resultSet.next()) {
                            JSONObject row = new JSONObject();
                            for (int i = 1; i <= columns.getColumnCount(); i++) {
                                row.put(columns.getColumnName(i), resultSet.getObject(i));
                            }
                            results.put(row);
                        }
                        resultSet.close();
                    }
                    statement.close();
                    return results.toString();
                }
                // z="success";
            }else if(DBStatus.equals("0")){
                String query = "select Doc1No,Doc2No from stk_sales_order_hd where Status='Waiting' and Doc2No!='' ";
                Cursor rsData=db.getQuery(query);
                JSONArray results = new JSONArray();
                while(rsData.moveToNext()){
                    JSONObject row=new JSONObject();
                    row.put("Doc1No",rsData.getString(0));
                    row.put("Doc2No",rsData.getString(1));
                    results.put(row);
                }
                db.closeDB();
                return results.toString();
            }else if(DBStatus.equals("2")){
                String sql="select Doc1No,Doc2No from stk_sales_order_hd where Status='Waiting' and Doc2No!='' ";
                Log.d("query",sql);
                jsonReq=new JSONObject();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", sql);
                jsonReq.put("action", "select");
                ConnectorLocal connectorLocal=new ConnectorLocal();
                String response=connectorLocal.ConnectSocket(IPAddress,8080,jsonReq.toString());
                jsonRes = new JSONObject(response);
                String result=jsonRes.getString("hasil");
                return result;
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
