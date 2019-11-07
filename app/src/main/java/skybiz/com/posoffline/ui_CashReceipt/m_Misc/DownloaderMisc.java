package skybiz.com.posoffline.ui_CashReceipt.m_Misc;

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
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderMisc extends AsyncTask<Void,Void,String> {
    Context c;
    String ItemGroup;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
    String CurCode;
    JSONObject jsonReq,jsonRes;

    public DownloaderMisc(Context c, String itemGroup, RecyclerView rv) {
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
            MiscParser p=new MiscParser(c,result,rv);
            p.execute();
        }
    }

    private String downloadData(){
        try {
            CurCode="RM";
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
            if(DBStatus.equals("1")){
                if(ItemConn.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        String sql = "select '" + CurCode + "' as CurCode, '0' as Qty," +
                                " '' as PhotoFile, IFNULL(R_ate,0) as UnitPrice, OCCode," +
                                " Description, '"+ItemGroup+"' as ItemGroup, UOM, RetailTaxCode from stk_othercharges where SuspendedYN='0'  ";
                        JSONArray results = new JSONArray();
                        Statement statement = conn.createStatement();
                        statement.executeQuery("SET NAMES 'LATIN1'");
                        statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                        if (statement.execute(sql)) {
                            ResultSet resultSet = statement.getResultSet();
                            ResultSetMetaData columns = resultSet.getMetaData();
                            while (resultSet.next()) {
                                JSONObject row = new JSONObject();
                                row.put("CurCode",resultSet.getString(1));
                                row.put("Qty",resultSet.getString(2));
                                row.put("ItemCode",resultSet.getString(5));
                                row.put("ItemGroup",resultSet.getString(7));
                                row.put("Description",encodeChar(resultSet.getString(6)));
                                row.put("UnitPrice",resultSet.getString(4));
                                row.put("PhotoFile",resultSet.getString(3));
                                row.put("UOM",resultSet.getString(8));
                                row.put("RetailTaxCode",resultSet.getString(9));
                               /* for (int i = 1; i <= columns.getColumnCount(); i++) {
                                    row.put(columns.getColumnName(i), resultSet.getObject(i));
                                }*/
                                results.put(row);
                            }
                            resultSet.close();
                        }
                        statement.close();
                        Log.d("JSON",results.toString());
                        return results.toString();
                    }
                }else{
                    String vQuery="select '" + CurCode + "' as CurCode, '0' as Qty," +
                            " OCCode, '"+ItemGroup+"',Description, " +
                            "  R_ate as UnitPrice, '' as PhotoFile, UOM, RetailTaxCode " +
                            "  from stk_othercharges where SuspendedYN='0'";
                    Cursor rsData=db.getQuery(vQuery);
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
                        row2.put("UOM",rsData.getString(7));
                        row2.put("RetailTaxCode",rsData.getString(8));
                        results2.put(row2);
                    }
                    db.closeDB();
                    Log.d("RESULT JSON",results2.toString());
                    return results2.toString();
                }
               // z="success";
            }else if(DBStatus.equals("0")){
                String vQuery="select '" + CurCode + "' as CurCode, '0' as Qty," +
                        " OCCode, '"+ItemGroup+"',Description, " +
                        "  R_ate as UnitPrice, '' as PhotoFile, UOM, RetailTaxCode " +
                        "  from stk_othercharges where SuspendedYN='0'";
                Cursor rsData=db.getQuery(vQuery);
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
                    row2.put("UOM",rsData.getString(7));
                    row2.put("RetailTaxCode",rsData.getString(8));
                    results2.put(row2);
                }
                db.closeDB();
                Log.d("RESULT JSON",results2.toString());
                return results2.toString();
            }else if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                String vQuery="select '" + CurCode + "' as CurCode, '0' as Qty," +
                        " '' as PhotoFile, R_ate as UnitPrice, OCCode as ItemCode," +
                        " Description, OCGroup as ItemGroup,UOM,RetailTaxCode" +
                        " from stk_othercharges where SuspendedYN='0'";
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vQuery);
                jsonReq.put("action", "select");
                //ConnectorLocal connectorLocal=new ConnectorLocal();
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

    private String encodeChar(String txt){
        String newText="";
        try{
            byte[] b=txt.getBytes("ISO-8859-1");
            newText=new String(b,"utf-8");
            return newText;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return newText;
    }
}
