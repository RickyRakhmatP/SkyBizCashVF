package skybiz.com.posoffline.ui_QuickCash.m_ItemQuick;

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
import java.util.Locale;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_NewObject.Encode;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderItem extends AsyncTask<Void,Void,String> {
    Context c;
    String Keyword,SearchBy;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn,EncodeType;
    String CurCode,ItemGroup="";
    JSONObject jsonReq,jsonRes;

    public DownloaderItem(Context c, RecyclerView rv) {
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
        try {
            CurCode="RM";
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            Cursor cur = db.getGeneralSetup();
            while (cur.moveToNext()) {
                CurCode = cur.getString(1);
            }
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
            Log.d("ItemConn",ItemConn);
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
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
                        String sql = "select ItemCode, Description, UnitCost from stk_master where ItemGroup='"+ItemGroup+"'  ";
                        Log.d("QUERY",sql);
                        JSONArray results = new JSONArray();
                        Statement statement = conn.createStatement();
                        statement.executeQuery("SET NAMES 'LATIN1'");
                        statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                        if (statement.execute(sql)) {
                            ResultSet rsData = statement.getResultSet();
                            while (rsData.next()) {
                                JSONObject row = new JSONObject();
                                row.put("ItemCode",rsData.getString(1));
                                row.put("Description",charReplace(Encode.setChar(EncodeType,rsData.getString(2))));
                                row.put("UnitCost",rsData.getDouble(3));
                                results.put(row);
                            }
                            rsData.close();
                        }
                        statement.close();
                        Log.d("JSON",results.toString());
                        return results.toString();
                    }
            }else if(DBStatus.equals("0")) {
                if (ItemGroup.equals("")) {
                    String vQueryG = "select ItemGroup from stk_master order by ItemGroup limit 1 ";
                    Cursor cGroup = db.getQuery(vQueryG);
                    while (cGroup.moveToNext()) {
                        ItemGroup = cGroup.getString(0);
                    }
                }
                String sql = "select ItemCode, Description, UnitCost from stk_master where ItemGroup='"+ItemGroup+"'  ";
                Log.d("QUERY", sql);
                Cursor rsData = db.getQuery(sql);
                JSONArray results2 = new JSONArray();
                while (rsData.moveToNext()) {
                    JSONObject row = new JSONObject();
                    row.put("ItemCode",rsData.getString(0));
                    row.put("Description",rsData.getString(1));
                    row.put("UnitCost",rsData.getDouble(2));
                    results2.put(row);
                }
                db.closeDB();
                Log.d("RESULT JSON", results2.toString());
                return results2.toString();
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String charReplace(String text){
        String newText=text.replaceAll("[\\.$|,|;|']","");
        return newText;
    }
}
