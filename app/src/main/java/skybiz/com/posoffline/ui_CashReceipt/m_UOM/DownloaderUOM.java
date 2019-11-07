package skybiz.com.posoffline.ui_CashReceipt.m_UOM;

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
import skybiz.com.posoffline.ui_CashReceipt.m_Order.Dialog_Qty;
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
    Connection conn=null;
    DBAdapter db=null;

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
        return this.fnretuom();
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

    private String fnretuom(){
        try{
            db=new DBAdapter(c);
            db.openDB();
            String querySet="select * from tb_setting";
            Cursor cur1=db.getQuery(querySet);
            while (cur1.moveToNext()) {
                IPAddress = cur1.getString(1);
                UserName = cur1.getString(2);
                Password = cur1.getString(3);
                DBName = cur1.getString(4);
                Port= cur1.getString(5);
                DBStatus= cur1.getString(8);
            }
            if(DBStatus.equals("1")) {
                 URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                 conn = Connector.connect(URL, UserName, Password);
                //conn= Connect_db.getConnection();
            }
            String qOther="select NewUOMYN from tb_othersetting ";
            Cursor rsOther=db.getQuery(qOther);
            String NewUOMYN="";
            while (rsOther.moveToNext()) {
                NewUOMYN=rsOther.getString(0);
            }
            if(NewUOMYN.equals("1")){
                z=fngetnewuom();
            }else{
                z=fngetuom();
            }
            db.closeDB();
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return null;
    }

    private String fngetnewuom(){
        try {
            JSONArray results = new JSONArray();
            String sql="select UOM, FactorQty, UnitPrice From stk_master_uom where ItemCode='"+ItemCode+"' ";
            if (conn != null) {
                Statement stmt = conn.createStatement();
                stmt.executeQuery("SET NAMES 'LATIN1'");
                stmt.executeQuery("SET CHARACTER SET 'LATIN1' ");
                stmt.execute(sql);
                ResultSet rsData=stmt.getResultSet();
                while (rsData.next()) {
                    JSONObject row=new JSONObject();
                    row.put("UOMType","UOM");
                    row.put("UOM", EncodeChar.setChar(c,rsData.getString(1)));
                    row.put("UOMFactor",rsData.getString(2));
                    row.put("vPrice",rsData.getString(3));
                    results.put(row);
                }
                return results.toString();
            }else{
                Cursor rsData=db.getQuery(sql);
                while(rsData.moveToNext()){
                    JSONObject row=new JSONObject();
                    row.put("UOMType","UOM");
                    row.put("UOM", EncodeChar.setChar(c,rsData.getString(0)));
                    row.put("UOMFactor",rsData.getString(1));
                    row.put("vPrice",rsData.getString(2));
                    results.put(row);
                }
                return results.toString();
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
    private String fngetuom(){
        try {
            JSONArray results = new JSONArray();
            if (conn != null) {
                String sql="select UOM, UOM1, UOM2, UOM3, UOM4, " +
                        " UnitPrice, UOMPrice1, UOMPrice2, UOMPrice3, UOMPrice4, " +
                        " UOMFactor1, UOMFactor2, UOMFactor3, UOMFactor4 " +
                        " From stk_master where ItemCode='"+ItemCode+"' ";
                Statement stmt = conn.createStatement();
                stmt.executeQuery("SET NAMES 'LATIN1'");
                stmt.executeQuery("SET CHARACTER SET 'LATIN1' ");
                stmt.execute(sql);
                ResultSet rsData=stmt.getResultSet();
                while (rsData.next()) {
                    JSONObject row=new JSONObject();
                    row.put("UOMType","UOM");
                    row.put("UOM", EncodeChar.setChar(c,rsData.getString(1)));
                    row.put("vPrice",rsData.getString(6));
                    row.put("UOMFactor","1");
                    results.put(row);
                    if(!rsData.getString(2).isEmpty()){
                        JSONObject row1=new JSONObject();
                        row1.put("UOMType","UOM1");
                        row1.put("UOM", EncodeChar.setChar(c,rsData.getString(2)));
                        row1.put("vPrice",rsData.getString(7));
                        row1.put("UOMFactor",rsData.getString(11));
                        results.put(row1);
                    }
                    if(!rsData.getString(3).isEmpty()){
                        JSONObject row2=new JSONObject();
                        row2.put("UOMType","UOM2");
                        row2.put("UOM", EncodeChar.setChar(c,rsData.getString(3)));
                        row2.put("vPrice",rsData.getString(8));
                        row2.put("UOMFactor",rsData.getString(12));
                        results.put(row2);
                    }
                    if(!rsData.getString(4).isEmpty()){
                        JSONObject row3=new JSONObject();
                        row3.put("UOMType","UOM3");
                        row3.put("UOM", EncodeChar.setChar(c,rsData.getString(4)));
                        row3.put("vPrice",rsData.getString(9));
                        row3.put("UOMFactor",rsData.getString(13));
                        results.put(row3);
                    }
                    if(!rsData.getString(5).isEmpty()){
                        JSONObject row4=new JSONObject();
                        row4.put("UOMType","UOM4");
                        row4.put("UOM", EncodeChar.setChar(c,rsData.getString(5)));
                        row4.put("vPrice",rsData.getString(10));
                        row4.put("UOMFactor",rsData.getString(14));
                        results.put(row4);
                    }
                }
                stmt.close();
                Log.d("RESULT", results.toString());
                return results.toString();
            }else {
                String sql="select UOM, UOM1, UOM2, UOM3, UOM4, " +
                        " round(UnitPrice,2) as UnitPrice, round(UOMPrice1,2) as UOMPrice1, " +
                        " round(UOMPrice2,2) as UOMPrice2, round(UOMPrice3,2) as UOMPrice3, " +
                        " round(UOMPrice4,2) as UOMPrice4, " +
                        " UOMFactor1, UOMFactor2, UOMFactor3, UOMFactor4 " +
                        " From stk_master where ItemCode='"+ItemCode+"' ";
                Cursor rsData=db.getQuery(sql);
                while (rsData.moveToNext()) {
                    JSONObject row=new JSONObject();
                    row.put("UOMType","UOM");
                    row.put("UOM",rsData.getString(0));
                    row.put("vPrice",rsData.getString(5));
                    row.put("UOMFactor","1");
                    results.put(row);

                    if(!rsData.getString(1).isEmpty()){
                        JSONObject row1=new JSONObject();
                        row1.put("UOMType","UOM1");
                        row1.put("UOM",rsData.getString(1));
                        row1.put("vPrice",rsData.getString(6));
                        row1.put("UOMFactor",rsData.getString(10));
                        results.put(row1);
                    }
                    if(!rsData.getString(2).isEmpty()){
                        JSONObject row2=new JSONObject();
                        row2.put("UOMType","UOM2");
                        row2.put("UOM",rsData.getString(2));
                        row2.put("vPrice",rsData.getString(7));
                        row2.put("UOMFactor",rsData.getString(11));
                        results.put(row2);
                    }
                    if(!rsData.getString(3).isEmpty()){
                        JSONObject row3=new JSONObject();
                        row3.put("UOMType","UOM3");
                        row3.put("UOM",rsData.getString(3));
                        row3.put("vPrice",rsData.getString(8));
                        row3.put("UOMFactor",rsData.getString(12));
                        results.put(row3);
                    }
                    if(!rsData.getString(4).isEmpty()){
                        JSONObject row4=new JSONObject();
                        row4.put("UOMType","UOM4");
                        row4.put("UOM",rsData.getString(4));
                        row4.put("vPrice",rsData.getString(9));
                        row4.put("UOMFactor",rsData.getString(13));
                        results.put(row4);
                    }

                }
                Log.d("RESULT", results.toString());
                return results.toString();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    private String encodeChar(String txt){
        String newText="";
        try{
            byte[] b=txt.getBytes("ISO-8859-1");
            newText=new String(b,"utf-8");
            //byte[] b=txt.getBytes("ISO-8859-1");
            //newText=new String(b);
            return newText;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return newText;
    }
}
