package skybiz.com.posoffline.ui_CashReceipt.m_ItemGroup;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_NewObject.EncodeChar;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;


/**
 * Created by 7 on 14/11/2017.
 */

public class DownloaderGroup extends AsyncTask<Void, Void, String> {
    Context c;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
    JSONObject jsonReq,jsonRes;
    public DownloaderGroup(Context c, RecyclerView rv) {
        this.c = c;
        this.rv = rv;
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
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);
        if(jsonData==null){
            Toast.makeText(c,"Unsuccessfull, No data retrieve",Toast.LENGTH_SHORT).show();
        }else{
            GroupParser p=new GroupParser(c,jsonData,rv);
            p.execute();
        }
    }

    private String downloadData(){
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String querySet="select * from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(1);
                UserName = curSet.getString(2);
                Password = curSet.getString(3);
                DBName = curSet.getString(4);
                Port=curSet.getString(5);
                DBStatus=curSet.getString(7);
                ItemConn=curSet.getString(8);
            }

            if(DBStatus.equals("1")) {
                if(ItemConn.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    db.closeDB();
                    if (conn != null) {
                        String sql = "select ItemGroup,Description from stk_group Order By Description";
                        JSONArray results = new JSONArray();
                        Statement statement = conn.createStatement();
                        statement.executeQuery("SET NAMES 'LATIN1'");
                        statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                        if (statement.execute(sql)) {
                            ResultSet resultSet = statement.getResultSet();
                            ResultSetMetaData columns = resultSet.getMetaData();
                            while (resultSet.next()) {
                                JSONObject row = new JSONObject();
                                row.put("ItemGroup",EncodeChar.setChar(c,resultSet.getString(1)));
                                row.put("Description", EncodeChar.setChar(c,resultSet.getString(2)));
                                /*for (int i = 1; i <= columns.getColumnCount(); i++) {
                                    row.put(columns.getColumnName(i), resultSet.getObject(i));
                                }*/
                                results.put(row);
                            }
                            resultSet.close();
                        }
                        Log.d("JSON",results.toString());
                        statement.close();
                        return results.toString();
                    }
                }else{
                    JSONArray results=new JSONArray();
                    String sql = "select ItemGroup, Description  from stk_group  Order By Description";
                    Cursor rsData=db.getQuery(sql);
                    while (rsData.moveToNext()) {
                        JSONObject row=new JSONObject();
                        row.put("ItemGroup",rsData.getString(0));
                        row.put("Description",rsData.getString(1));
                        results.put(row);
                    }
                    db.closeDB();
                    return results.toString();
                }
            }else if(DBStatus.equals("0")){
                JSONArray results=new JSONArray();
                String sql = "select ItemGroup, Description  from stk_group  Order By Description";
                Cursor rsData=db.getQuery(sql);
                while (rsData.moveToNext()) {
                    JSONObject row=new JSONObject();
                    row.put("ItemGroup",rsData.getString(0));
                    row.put("Description",rsData.getString(1));
                    results.put(row);
                }
                db.closeDB();
                return results.toString();
            }else if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                String sql = "select ItemGroup, Description  from stk_group  Order By Description";
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

    /*private String encodeChar(String txt){
        String newText="";
        try{
            byte[] b=txt.getBytes("ISO-8859-1");
            newText=new String(b,"utf-8");
            return newText;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return newText;
    }*/
}
