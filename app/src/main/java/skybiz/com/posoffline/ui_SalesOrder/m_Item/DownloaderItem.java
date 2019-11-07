package skybiz.com.posoffline.ui_SalesOrder.m_Item;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
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
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderItem extends AsyncTask<Void,Void,String> {
    Context c;
    String ItemGroup;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
    String CurCode;

    public DownloaderItem(Context c, String itemGroup, RecyclerView rv) {
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
            if(ItemConn.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
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
                    String sql = "select '" + CurCode + "' as CurCode, '0' as Qty, PhotoFile," +
                            " INFULL(UnitPrice,0) as UnitPrice, ItemCode, Description," +
                            " ItemGroup, IFNULL(HCDiscount,0) as, IFNULL(DisRate1,0) as DisRate1 " +
                            " from stk_master where SuspendedYN='0' and ItemGroup='" + ItemGroup + "' Order By ItemCode";
                    JSONArray results = new JSONArray();
                    Statement statement = conn.createStatement();
                    statement.executeQuery("SET NAMES 'LATIN1'");
                    statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                    if (statement.execute(sql)) {
                        ResultSet resultSet = statement.getResultSet();
                        ResultSetMetaData columns = resultSet.getMetaData();
                        while (resultSet.next()) {
                            JSONObject row=new JSONObject();
                            row.put("CurCode",resultSet.getString(1));
                            row.put("Qty",resultSet.getString(2));
                            row.put("ItemCode",resultSet.getString(5));
                            row.put("ItemGroup",resultSet.getString(7));
                            row.put("Description", EncodeChar.setChar(c,resultSet.getString(6)));
                            row.put("UnitPrice",resultSet.getString(4));
                            row.put("PhotoFile",resultSet.getString(3));
                            row.put("HCDiscount",resultSet.getDouble(8));
                            row.put("DisRate1",resultSet.getDouble(9));
                            results.put(row);

                        }
                        resultSet.close();
                    }
                    statement.close();
                    return results.toString();
                }
            }else{
                if (ItemGroup.equals("")) {
                    String vQueryG = "select ItemGroup from stk_master order by ItemGroup limit 1 ";
                    Cursor cGroup = db.getQuery(vQueryG);
                    while (cGroup.moveToNext()) {
                        ItemGroup = cGroup.getString(0);
                    }
                }
                String vQuery="select '"+CurCode+"' as CurCode, '0' as Qty, ItemCode, " +
                        "ItemGroup, Description, UnitPrice, " +
                        "'' as PhotoFile, IFNULL(HCDiscount,0)as HCDiscount, IFNULL(DisRate1,0)as DisRate1" +
                        " from stk_master where ItemGroup='"+ItemGroup+"' Order By ItemCode";
                Cursor rsData=db.getQuery(vQuery);
                JSONArray results=new JSONArray();
                while(rsData.moveToNext()){
                    JSONObject row=new JSONObject();
                    row.put("CurCode",rsData.getString(0));
                    row.put("Qty",rsData.getString(1));
                    row.put("ItemCode",rsData.getString(2));
                    row.put("ItemGroup",rsData.getString(3));
                    row.put("Description",rsData.getString(4));
                    row.put("UnitPrice",rsData.getString(5));
                    row.put("PhotoFile",rsData.getString(6));
                    row.put("HCDiscount",rsData.getDouble(7));
                    row.put("DisRate1",rsData.getDouble(8));
                    results.put(row);
                }
                return results.toString();
            }
            db.closeDB();
        }catch (SQLException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
