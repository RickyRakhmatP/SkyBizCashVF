package skybiz.com.posoffline.ui_Member.m_PointLedger;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
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
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloadHistory extends AsyncTask<Void,Void,String> {
    Context c;
    String CusCode;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
    String CurCode;
    String TotalPoint;
    public DownloadHistory(Context c, String CusCode, RecyclerView rv) {
        this.c = c;
        this.CusCode = CusCode;
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
            ((HistoryPoint)c).setHeader(CusCode,CurCode,TotalPoint);
            HistoryParser p=new HistoryParser(c,result,rv);
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
            Double dTotalPoint=0.00;
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    String sql ="Select DATE_FORMAT(P.D_ateTime,'%Y-%m-%d %H:%i:%s') as D_ateTime,IFNULL(P.Point,0) as Point,P.DocType," +
                            " P.Remark,C.CusCode,C.CusName, '"+CurCode+"' as CurCode "+
                            " from ret_pointadjustment P inner join customer C " +
                            " on P.cuscode=C.CusCode where C.CusCode='"+CusCode+"' order by P.RunNo desc ";
                    JSONArray results = new JSONArray();
                    Statement statement = conn.createStatement();
                    if (statement.execute(sql)) {
                        ResultSet rsData = statement.getResultSet();
                       // ResultSetMetaData columns = resultSet.getMetaData();
                        while (rsData.next()) {
                            JSONObject row = new JSONObject();
                            /*for (int i = 1; i <= columns.getColumnCount(); i++) {
                                row.put(columns.getColumnName(i), resultSet.getObject(i));
                            }*/
                            row.put("D_ate",rsData.getString(1));
                            row.put("Point",rsData.getString(2));
                            row.put("DocType",rsData.getString(3));
                            row.put("Remark",rsData.getString(4));
                            row.put("CusCode",rsData.getString(5));
                            row.put("CusName",rsData.getString(6));
                            row.put("CurCode",rsData.getString(7));
                            String DocType=rsData.getString(3);
                            if(DocType.equals("Increase")) {
                                dTotalPoint += rsData.getDouble(2);
                            }else{
                                dTotalPoint -= rsData.getDouble(2);
                            }
                            results.put(row);
                        }
                        TotalPoint=zeroDecimal(dTotalPoint);
                        rsData.close();
                    }
                    statement.close();
                    return results.toString();
                }
            }else if(DBStatus.equals("0")){
                String sql ="Select P.D_ateTime,IFNULL(P.Point,0) as Point,P.DocType," +
                        " P.Remark, C.CusCode, C.CusName, '"+CurCode+"' as CurCode "+
                        " from ret_pointadjustment P inner join customer C " +
                        " on P.cuscode=C.CusCode where C.CusCode='"+CusCode+"' order by P.RunNo desc ";
                Cursor rsData=db.getQuery(sql);
                JSONArray results2=new JSONArray();
                while(rsData.moveToNext()){
                    JSONObject row2=new JSONObject();
                    row2.put("D_ate",rsData.getString(0));
                    row2.put("Point",rsData.getString(1));
                    row2.put("DocType",rsData.getString(2));
                    row2.put("Remark",rsData.getString(3));
                    row2.put("CusCode",rsData.getString(4));
                    row2.put("CusName",rsData.getString(5));
                    row2.put("CurCode",rsData.getString(6));
                    String DocType=rsData.getString(2);
                    if(DocType.equals("Increase")) {
                        dTotalPoint += rsData.getDouble(1);
                    }else{
                        dTotalPoint -= rsData.getDouble(1);
                    }
                    //dTotalPoint+=rsData.getDouble(1);
                    results2.put(row2);
                }
                TotalPoint=zeroDecimal(dTotalPoint);
                db.closeDB();
                //Log.d("RESULT JSON",results2.toString());
                return results2.toString();
            }/*else if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                String vQuery="Select CusCode,CusName,TermCode," +
                        " D_ay, SalesPersonCode,MembershipClass" +
                        " from customer where CusCode <>'' "+vClause+" ";
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vQuery);
                jsonReq.put("action", "select");
                ConnectorLocal connectorLocal=new ConnectorLocal();
                String response=connectorLocal.ConnectSocket(IPAddress,8080,jsonReq.toString());
                jsonRes = new JSONObject(response);
                String result=jsonRes.getString("hasil");
                return result;
            }*/
        }catch (SQLException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    private String twoDecimal(Double values){
        String textDecimal="";
        textDecimal=String.format(Locale.US, "%,.2f", values);
        return textDecimal;
    }
    private String zeroDecimal(Double values){
        String textDecimal="";
        textDecimal=String.format(Locale.US, "%,.0f", values);
        return textDecimal;
    }
}
