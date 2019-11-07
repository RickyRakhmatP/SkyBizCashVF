package skybiz.com.posoffline.ui_CashReceipt.m_Item;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderItemLocal extends AsyncTask<Void,Void,String> {
    Context c;
    String ItemGroup;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
    String CurCode;
    String respnose;

    public DownloaderItemLocal(Context c, String itemGroup, RecyclerView rv) {
        this.c = c;
        this.ItemGroup = itemGroup;
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
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result==null){
            Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
        }else{
            ItemParser p=new ItemParser(c,result,rv);
            p.execute();
        }
    }

    private String downloadData(){
        JSONObject jsonReq,jsonRes;

        try {
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
            if(ItemConn.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                if (conn != null) {
                    if (ItemGroup.equals("")) {
                        String sqlGroup = "select ItemGroup from stk_master order by ItemGroup limit 1";
                        Statement stmtGroup = conn.createStatement();
                        stmtGroup.execute(sqlGroup);
                        ResultSet rsGroup = stmtGroup.getResultSet();
                        while (rsGroup.next()) {
                            ItemGroup = rsGroup.getString("ItemGroup");
                        }
                    }
                    String sql = "select '" + CurCode + "' as CurCode, '0' as Qty," +
                            " PhotoFile, FORMAT(UnitPrice,2) as UnitPrice, ItemCode," +
                            " Description, ItemGroup from stk_master where SuspendedYN='0' and ItemGroup='" + ItemGroup + "' ";
                    JSONArray results = new JSONArray();
                    Statement statement = conn.createStatement();
                    if (statement.execute(sql)) {
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
            }else{
                if (ItemGroup.equals("")) {
                    String vQueryG = "select ItemGroup from stk_master order by ItemGroup limit 1 ";
                    Cursor cGroup = db.getQuery(vQueryG);
                    while (cGroup.moveToNext()) {
                        ItemGroup = cGroup.getString(0);
                    }
                }
                String sql="select '"+CurCode+"' as CurCode, '0' as Qty, ItemCode, ItemGroup, Description," +
                        "UnitPrice, '' as PhotoFile from stk_master where ItemGroup='"+ItemGroup+"' ";
                jsonReq=new JSONObject();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", sql);
                jsonReq.put("action", "select");
                ConnectorLocal connectorLocal=new ConnectorLocal();
                String response=connectorLocal.ConnectSocket("192.168.1.5",8080,jsonReq.toString());
                jsonRes = new JSONObject(response);
                String result=jsonRes.getString("hasil");
               /* Cursor rsData=db.getQuery(vQuery);
                Log.d("TOTAL ROWS",vQuery+String.valueOf(rsData.getCount()));
                JSONArray results2=new JSONArray();
                while(rsData.moveToNext()){
                    JSONObject row2=new JSONObject();
                    row2.put("CurCode",rsData.getString(0));
                    row2.put("Qty",rsData.getString(1));
                    row2.put("ItemCode",rsData.getString(2));
                    row2.put("ItemGroup",rsData.getString(3));
                    row2.put("Description",rsData.getString(4));
                    row2.put("UnitPrice",rsData.getString(5));
                    row2.put("PhotoFile",rsData.getString(6));
                    results2.put(row2);
                }*/
                db.closeDB();
                return result;
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String downloadData2(){
        JSONObject jsonData;
        Boolean success;
        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;
        jsonData=new JSONObject();
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            Cursor cur = db.getGeneralSetup();
            while (cur.moveToNext()) {
                CurCode = cur.getString(1);
            }
            String sql="select '"+CurCode+"' as CurCode, '0' as Qty, ItemCode, ItemGroup, Description," +
                    "UnitPrice from stk_master where ItemGroup='"+ItemGroup+"' limit 100";
            jsonData.put("request", "request-connect-client");
            jsonData.put("ipAddress", "");
            jsonData.put("query", sql);
            jsonData.put("action", "select");
            // Create a new Socket instance and connect to host
            socket = new Socket("192.168.1.5", 8080);

            dataOutputStream = new DataOutputStream(
                    socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            // transfer JSONObject as String to the server
            dataOutputStream.writeUTF(jsonData.toString());
            Log.i("SOCKET", "waiting for response from host");

            // Thread will wait till server replies
            String response = dataInputStream.readUTF();

            final JSONObject jsonhasil;
            jsonhasil = new JSONObject(response);
            String result = jsonhasil.getString("hasil");

          /*  if (respnose != null && response.equals("Connection Accepted")) {
                success = true;
            } else if (respnose != null && response.equals("Connection Accepted")) {
                success = true;
            } else {
                success = false;
            }*/
          return result;
        }catch (JSONException e){
            e.printStackTrace();
        }catch (UnknownHostException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } finally {

            // close socket
            if (socket != null) {
                try {
                    Log.i("SOCKET", "closing the socket");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // close input stream
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // close output stream
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
