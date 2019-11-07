package skybiz.com.posoffline.ui_CashReceipt.m_Reprint;

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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderReprint extends AsyncTask<Void,Void,String> {
    Context c;
    String DateFrom,DateTo;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus;
    String CurCode;
    DialogReprint dialogReprint;
    public DownloaderReprint(Context c, String DateFrom, String DateTo, RecyclerView rv, DialogReprint dialogReprint) {
        this.c = c;
        this.DateFrom = DateFrom;
        this.DateTo = DateTo;
        this.rv = rv;
        this.dialogReprint=dialogReprint;
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
        }else if((result.equals("error"))) {
            Toast.makeText(c,"Error Connection", Toast.LENGTH_SHORT).show();
        }else{
            ReprintParser p=new ReprintParser(c,result,rv,dialogReprint);
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
                IPAddress   = curSet.getString(0);
                UserName    = curSet.getString(1);
                Password    = curSet.getString(2);
                DBName      = curSet.getString(3);
                Port        = curSet.getString(4);
                DBStatus    =curSet.getString(5);
            }
            String vQuery="SELECT H.Doc1No, H.D_ateTime, H.HCNetAmt, IFNULL(Sum(D.Qty),0) as Qty" +
                    " FROM stk_cus_inv_hd H INNER JOIN stk_cus_inv_dt D on D.Doc1No=H.Doc1No " +
                    " WHERE H.D_ate>='"+DateFrom+"' and H.D_ate<='"+DateTo+"'  " +
                    " GROUP BY H.Doc1No ORDER BY H.D_ateTime Desc";
            if(DBStatus.equals("2")) {
                jsonReq = new JSONObject();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vQuery);
                jsonReq.put("action", "select");
                ConnectorLocal connectorLocal = new ConnectorLocal();
                String response = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response);
                String result = jsonRes.getString("hasil");
                return result;
            }else if(DBStatus.equals("1")){
                URL     = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                String vQuery2="SELECT H.Doc1No, DATE_FORMAT(H.D_ateTime,'%Y-%m-%d %H:%i:%s') as D_ateTime, H.HCNetAmt, " +
                        " IFNULL(Sum(D.Qty),0) as Qty" +
                        " FROM stk_cus_inv_hd H INNER JOIN stk_cus_inv_dt D on D.Doc1No=H.Doc1No " +
                        " WHERE H.D_ate>='"+DateFrom+"' and H.D_ate<='"+DateTo+"'  " +
                        " GROUP BY H.Doc1No ORDER BY H.D_ateTime Desc";
                JSONArray results = new JSONArray();
                if(conn!=null) {
                    Statement stmtData = conn.createStatement();
                    stmtData.execute(vQuery2);
                    ResultSet rsData = stmtData.getResultSet();
                    while (rsData.next()) {
                        JSONObject row = new JSONObject();
                        String Total = String.format(Locale.US, "%,.2f", rsData.getDouble(3));
                        String TotalQty = String.format(Locale.US, "%,.2f", rsData.getDouble(4));
                        row.put("Doc1No", rsData.getString(1));
                        row.put("D_ateTime", rsData.getString(2));
                        row.put("HCNetAmt", Total);
                        row.put("Qty", TotalQty);
                        results.put(row);
                    }
                    return results.toString();
                }else{
                    return "error";
                }
            }else if(DBStatus.equals("0")) {
                Cursor rsData = db.getQuery(vQuery);
                Log.d("TOTAL ROWS", String.valueOf(rsData.getCount()));
                JSONArray results = new JSONArray();
                while (rsData.moveToNext()) {
                    JSONObject row = new JSONObject();
                    row.put("Doc1No", rsData.getString(0));
                    row.put("D_ateTime", rsData.getString(1));
                    row.put("HCNetAmt", rsData.getString(2));
                    row.put("Qty", rsData.getString(3));
                    results.put(row);
                }
                db.closeDB();
                return results.toString();
            }
        }catch (SQLiteException e ) {
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
