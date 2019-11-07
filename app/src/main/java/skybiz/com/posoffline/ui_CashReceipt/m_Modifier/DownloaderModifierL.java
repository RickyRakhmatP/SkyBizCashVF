package skybiz.com.posoffline.ui_CashReceipt.m_Modifier;

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
import skybiz.com.posoffline.m_NewObject.Decode;
import skybiz.com.posoffline.m_NewObject.Encode;
import skybiz.com.posoffline.m_NewObject.EncodeChar;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.Dialog_Qty;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderModifierL extends AsyncTask<Void,Void,String> {
    Context c;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn,ItemGroup,EncodeType;
    DialogModifier dialogModifier;
    public DownloaderModifierL(Context c, String ItemGroup, RecyclerView rv, DialogModifier dialogModifier) {
        this.c = c;
        this.ItemGroup = ItemGroup;
        this.rv = rv;
        this.dialogModifier=dialogModifier;
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
            ModiferLParser p=new ModiferLParser(c,result,rv,dialogModifier);
            p.execute();
        }
    }

    private String downloadData(){
        try {
            JSONObject jsonReq,jsonRes;
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName,UserName,Password," +
                    "DBName,Port,DBStatus," +
                    "ItemConn,EncodeType" +
                    " from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(0);
                UserName = curSet.getString(1);
                Password = curSet.getString(2);
                DBName = curSet.getString(3);
                Port = curSet.getString(4);
                DBStatus=curSet.getString(5);
                ItemConn=curSet.getString(6);
                EncodeType=curSet.getString(7);
            }
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    String sql ="select Modifier1 from stk_group where ItemGroup='"+ Decode.setChar(EncodeType,ItemGroup)+"' ";
                    Log.d("QUERY",sql);
                    JSONArray results = new JSONArray();
                    Statement statement = conn.createStatement();
                    statement.executeQuery("SET NAMES 'LATIN1'");
                    statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                    if (statement.execute(sql)) {
                        ResultSet resultSet = statement.getResultSet();
                        while (resultSet.next()) {
                            String vModifier= Encode.setChar(EncodeType,resultSet.getString(1));
                            String[] Modifier = vModifier.split("\\|");
                            for (String modifier : Modifier) {
                                JSONObject row = new JSONObject();
                                row.put("Modifier",modifier);
                                results.put(row);
                            }
                        }
                        resultSet.close();
                    }
                    statement.close();
                    return results.toString();
                }
            }else if(DBStatus.equals("0")){
                String vQuery="select Modifier1 from stk_group where ItemGroup='"+ItemGroup+"' ";
                Cursor rsData=db.getQuery(vQuery);
                JSONArray results2=new JSONArray();
                while(rsData.moveToNext()){
                    String vModifier=rsData.getString(0);
                    String[] aModifier = vModifier.split("\\|");
                    //Log.d("LENGTH",String.valueOf(aModifier.length));
                    for (String imodifier : aModifier) {
                        JSONObject row2=new JSONObject();
                        row2.put("Modifier", imodifier);
                        results2.put(row2);
                    }
                }
                Log.d("RESULT JSON",results2.toString());
                db.closeDB();
                return results2.toString();
            }else if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                String vQuery="select Modifier1 from stk_group where ItemGroup='"+ItemGroup+"' ";
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
                    String vModifier = vData.getString("Modifier1");
                    String[] aModifier = vModifier.split("\\|");
                    for (String imodifier : aModifier) {
                        JSONObject row=new JSONObject();
                        row.put("Modifier", imodifier);
                        rs.put(row);
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
