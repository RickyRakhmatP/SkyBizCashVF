package skybiz.com.posoffline.ui_SalesOrder.m_UOM;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
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
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_NewObject.EncodeChar;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_SalesOrder.m_Order.Dialog_Qty;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderUOM extends AsyncTask<Void,Void,String> {
    Context c;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn,ItemCode;
    String CurCode;
    Dialog_Qty dialogQty;

    public DownloaderUOM(Context c, String ItemCode, RecyclerView rv, Dialog_Qty dialogQty) {
        this.c = c;
        this.ItemCode = ItemCode;
        this.rv = rv;
        this.dialogQty=dialogQty;
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
            UOMParser p=new UOMParser(c,result,rv,dialogQty);
            p.execute();
        }
    }

    private String downloadData(){
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
                ItemConn=curSet.getString(8);
            }
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    String sql ="select  UOM, UOM1, UOM2, UOM3, UOM4, " +
                            " UnitPrice, UOMPrice1, UOMPrice2, UOMPrice3, UOMPrice4, " +
                            " UOMFactor1, UOMFactor2, UOMFactor3, UOMFactor4 " +
                            " from stk_master where ItemCode='"+ItemCode+"' ";
                    Log.d("QUERY",sql);
                    JSONArray results = new JSONArray();
                    Statement statement = conn.createStatement();
                    statement.executeQuery("SET NAMES 'LATIN1'");
                    statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                    if (statement.execute(sql)) {
                        ResultSet resultSet = statement.getResultSet();
                        while (resultSet.next()) {
                            JSONObject row = new JSONObject();
                            row.put("UOMType","UOM");
                            row.put("UOM", EncodeChar.setChar(c,resultSet.getString(1)));
                            row.put("vPrice",resultSet.getString(6));
                            row.put("UOMFactor","1");
                            results.put(row);
                            if(!resultSet.getString(2).isEmpty()) {
                                JSONObject row1 = new JSONObject();
                                row1.put("UOMType", "UOM1");
                                row1.put("UOM",  EncodeChar.setChar(c,resultSet.getString(2)));
                                row1.put("vPrice", resultSet.getString(7));
                                row1.put("UOMFactor", resultSet.getString(11));
                                results.put(row1);
                            }
                            if(!resultSet.getString(3).isEmpty()) {
                                JSONObject row2 = new JSONObject();
                                row2.put("UOMType", "UOM2");
                                row2.put("UOM",  EncodeChar.setChar(c,resultSet.getString(3)));
                                row2.put("vPrice", resultSet.getString(8));
                                row2.put("UOMFactor", resultSet.getString(12));
                                results.put(row2);
                            }
                            if(!resultSet.getString(4).isEmpty()) {
                                JSONObject row3 = new JSONObject();
                                row3.put("UOMType", "UOM3");
                                row3.put("UOM",  EncodeChar.setChar(c,resultSet.getString(4)));
                                row3.put("vPrice", resultSet.getString(9));
                                row3.put("UOMFactor", resultSet.getString(13));
                                results.put(row3);
                            }
                            if(!resultSet.getString(5).isEmpty()) {
                                JSONObject row4 = new JSONObject();
                                row4.put("UOMType", "UOM4");
                                row4.put("UOM",  EncodeChar.setChar(c,resultSet.getString(5)));
                                row4.put("vPrice", resultSet.getString(10));
                                row4.put("UOMFactor", resultSet.getString(14));
                                results.put(row4);
                            }
                        }
                        resultSet.close();
                    }
                    statement.close();
                    return results.toString();
                }
            }else if(DBStatus.equals("0")){
                String vQuery ="select  UOM, UOM1, UOM2, UOM3, UOM4, " +
                        " round(UnitPrice,2)as UnitPrice, round(UOMPrice1,2)as UOMPrice1," +
                        " round(UOMPrice2,2)as UOMPrice2, round(UOMPrice3,2)as UOMPrice3," +
                        " round(UOMPrice4,2)as UOMPrice4, " +
                        " UOMFactor1, UOMFactor2, UOMFactor3, UOMFactor4 " +
                        " from stk_master where ItemCode='"+ItemCode+"' ";
                Cursor rsData=db.getQuery(vQuery);
                JSONArray results2=new JSONArray();
                while(rsData.moveToNext()){
                        JSONObject row=new JSONObject();
                        row.put("UOMType","UOM");
                        row.put("UOM",rsData.getString(0));
                        row.put("vPrice",rsData.getDouble(5));
                        row.put("UOMFactor","1");
                        results2.put(row);
                        if(!rsData.getString(1).isEmpty()) {
                            JSONObject row1 = new JSONObject();
                            row1.put("UOMType", "UOM1");
                            row1.put("UOM", rsData.getString(1));
                            row1.put("vPrice", rsData.getDouble(6));
                            row1.put("UOMFactor", rsData.getString(10));
                            results2.put(row1);
                        }
                        if(!rsData.getString(2).isEmpty()) {
                            JSONObject row2 = new JSONObject();
                            row2.put("UOMType", "UOM2");
                            row2.put("UOM", rsData.getString(2));
                            row2.put("vPrice", rsData.getDouble(7));
                            row2.put("UOMFactor", rsData.getString(11));
                            results2.put(row2);
                        }
                        if(!rsData.getString(3).isEmpty()) {
                             JSONObject row3 = new JSONObject();
                             row3.put("UOMType", "UOM3");
                             row3.put("UOM", rsData.getString(3));
                             row3.put("vPrice", rsData.getDouble(8));
                             row3.put("UOMFactor", rsData.getString(12));
                             results2.put(row3);
                        }
                        if(!rsData.getString(4).isEmpty()) {
                            JSONObject row4 = new JSONObject();
                            row4.put("UOMType", "UOM4");
                            row4.put("UOM", rsData.getString(4));
                            row4.put("vPrice", rsData.getDouble(9));
                            row4.put("UOMFactor", rsData.getString(13));
                            results2.put(row4);
                        }
                }
                Log.d("JSON",results2.toString());
                db.closeDB();
                return results2.toString();
            }else if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                String vQuery ="select  UOM, UOM1, UOM2, UOM3, UOM4, " +
                        " UnitPrice, UOMPrice1, UOMPrice2, UOMPrice3, UOMPrice4, " +
                        " UOMFactor1, UOMFactor2, UOMFactor3, UOMFactor4  " +
                        " from stk_master where ItemCode='"+ItemCode+"' ";
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vQuery);
                jsonReq.put("action", "select");
                ConnectorLocal conn=new ConnectorLocal();
                String response=conn.ConnectSocket(IPAddress,8080,jsonReq.toString());
                jsonRes = new JSONObject(response);
                String result=jsonRes.getString("hasil");
                JSONArray rsData = new JSONArray(result);
                JSONArray rs=new JSONArray();
                JSONObject vData = null;
                for (int i = 0; i < rsData.length(); i++) {
                    vData = rsData.getJSONObject(i);
                    JSONObject row=new JSONObject();
                    row.put("UOMType","UOM");
                    row.put("UOM",vData.getString("UOM"));
                    row.put("vPrice",vData.getString("UnitPrice"));
                    row.put("UOMFactor","1");
                    rs.put(row);
                    if(!vData.getString("UOM1").isEmpty()) {
                        JSONObject row1 = new JSONObject();
                        row1.put("UOMType", "UOM1");
                        row1.put("UOM", vData.getString("UOM1"));
                        row1.put("vPrice", vData.getString("UOMPrice1"));
                        row1.put("UOMFactor", vData.getString("UOMFactor1"));
                        rs.put(row1);
                    }
                    if(!vData.getString("UOM2").isEmpty()) {
                        JSONObject row2 = new JSONObject();
                        row2.put("UOMType", "UOM2");
                        row2.put("UOM", vData.getString("UOM2"));
                        row2.put("vPrice", vData.getString("UOMPrice2"));
                        row2.put("UOMFactor", vData.getString("UOMFactor2"));
                        rs.put(row2);
                    }
                    if(!vData.getString("UOM3").isEmpty()) {
                        JSONObject row3 = new JSONObject();
                        row3.put("UOMType", "UOM3");
                        row3.put("UOM", vData.getString("UOM3"));
                        row3.put("vPrice", vData.getString("UOMPrice3"));
                        row3.put("UOMFactor", vData.getString("UOMFactor3"));
                        rs.put(row3);
                    }
                    if(!vData.getString("UOM4").isEmpty()) {
                        JSONObject row4 = new JSONObject();
                        row4.put("UOMType", "UOM4");
                        row4.put("UOM", vData.getString("UOM4"));
                        row4.put("vPrice", vData.getString("UOMPrice4"));
                        row4.put("UOMFactor", vData.getString("UOMFactor4"));
                        rs.put(row4);
                    }
                }
                return rs.toString();
            }
        }catch (SQLiteException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }catch (SQLException e) {
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
