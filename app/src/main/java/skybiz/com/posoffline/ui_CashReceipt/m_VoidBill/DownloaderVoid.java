package skybiz.com.posoffline.ui_CashReceipt.m_VoidBill;

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

public class DownloaderVoid extends AsyncTask<Void,Void,String> {
    Context c;
    String DateFrom,DateTo;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
    String CurCode;
    DialogVoid dialogVoid;

    public DownloaderVoid(Context c, String DateFrom,String DateTo, RecyclerView rv, DialogVoid dialogVoid) {
        this.c = c;
        this.DateFrom = DateFrom;
        this.DateTo = DateTo;
        this.rv = rv;
        this.dialogVoid = dialogVoid;
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
            VoidParser p=new VoidParser(c,result,rv, dialogVoid);
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
            }
            String vQuery = "select H.Doc1No, H.D_ateTime, IFNULL(H.HCNetAmt,0) as HCNetAmt," +
                    " IFNULL(Sum(D.Qty),0) as Qty, H.Status2, IFNULL(R.CC1Code,'') as CC1Code " +
                    " from stk_cus_inv_hd H inner join stk_cus_inv_dt D on D.Doc1No=H.Doc1No " +
                    " inner join stk_receipt2 R ON H.Doc1No=R.Doc1No " +
                    " where H.D_ate>='"+DateFrom+"' and H.D_ate<='"+DateTo+"' " +
                    " Group By H.Doc1No Order By H.D_ate Desc";
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vQuery);
                jsonReq.put("action", "select");
                ConnectorLocal connectorLocal=new ConnectorLocal();
                String response=connectorLocal.ConnectSocket(IPAddress,8080,jsonReq.toString());
                jsonRes = new JSONObject(response);
                String result=jsonRes.getString("hasil");
                return result;
            }else if(DBStatus.equals("0")) {
                Cursor rsData = db.getQuery(vQuery);
                JSONArray results = new JSONArray();
                while (rsData.moveToNext()) {
                    Double dTotal = rsData.getDouble(2);
                    String Total = String.format(Locale.US, "%,.2f", dTotal);
                    JSONObject row = new JSONObject();
                    row.put("Doc1No", rsData.getString(0));
                    row.put("D_ateTime", rsData.getString(1));
                    row.put("HCNetAmt", Total);
                    row.put("Qty", rsData.getString(3));
                    row.put("Status2", rsData.getString(4));
                    row.put("PaymentCode", rsData.getString(5));
                    results.put(row);
                }
                db.closeDB();
                Log.d("HASIL", results.toString());
                return results.toString();
            }else if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    JSONArray results = new JSONArray();
                    Statement stmtVoid = conn.createStatement();
                    stmtVoid.execute(vQuery);
                    ResultSet rsVoid = stmtVoid.getResultSet();
                    while (rsVoid.next()) {
                        Double dTotal = rsVoid.getDouble(3);
                        String Total = String.format(Locale.US, "%,.2f", dTotal);
                        JSONObject row = new JSONObject();
                        row.put("Doc1No", rsVoid.getString(1));
                        row.put("D_ateTime", rsVoid.getString(2));
                        row.put("HCNetAmt", Total);
                        row.put("Qty", rsVoid.getString(4));
                        row.put("Status2", rsVoid.getString(5));
                        row.put("PaymentCode", rsVoid.getString(6));
                        results.put(row);
                    }
                    return results.toString();
                }else{
                    return "error";
                }
            }
        }catch (SQLiteException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
            /*Cursor cur = db.getGeneralSetup();
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
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                if (conn != null) {
                    String sql ="Select CusCode,CusName,TermCode,D_ay,SalesPersonCode from customer where FinCatCode='B55' and " +
                            "CusName like '%"+Query+"%' ";
                    Log.d("QUERY",sql);
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
                String vQuery="Select CusCode,CusName,TermCode,D_ay, SalesPersonCode from customer where CusName like '%"+Query+"%' ";
                Cursor rsData=db.getQuery(vQuery);
                Log.d("TOTAL ROWS",vQuery+String.valueOf(rsData.getCount()));
                JSONArray results2=new JSONArray();
                while(rsData.moveToNext()){
                    JSONObject row2=new JSONObject();
                    row2.put("CusCode",rsData.getString(0));
                    row2.put("CusName",rsData.getString(1));
                    row2.put("TermCode",rsData.getString(2));
                    row2.put("D_ay",rsData.getString(3));
                    row2.put("SalesPersonCode",rsData.getString(4));
                    results2.put(row2);
                }
                db.closeDB();
                Log.d("RESULT JSON",results2.toString());
                return results2.toString();
            }*/
