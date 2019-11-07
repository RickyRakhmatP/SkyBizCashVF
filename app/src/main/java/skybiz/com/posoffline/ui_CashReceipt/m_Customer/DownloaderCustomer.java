package skybiz.com.posoffline.ui_CashReceipt.m_Customer;

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

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderCustomer extends AsyncTask<Void,Void,String> {
    Context c;
    String Keyword,SearchBy;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
    String CurCode;
    DialogCustomer dialogCustomer;
    JSONObject jsonReq,jsonRes;

    public DownloaderCustomer(Context c, String SearchBy, String Keyword, RecyclerView rv, DialogCustomer dialogCustomer) {
        this.c = c;
        this.SearchBy = SearchBy;
        this.Keyword = Keyword;
        this.rv = rv;
        this.dialogCustomer=dialogCustomer;
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
            CustomerParser p=new CustomerParser(c,result,rv,dialogCustomer);
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
            String vClause="";
            if(SearchBy.equals("By CusCode")){
                vClause=" and C.CusCode like '%"+Keyword+"%' ";
            }else if(SearchBy.equals("By CusName")){
                vClause=" and C.CusName like '%"+Keyword+"%' ";
            }
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    String sql ="Select C.CusCode, C.CusName, C.TermCode," +
                            " C.D_ay, C.SalesPersonCode, C.MembershipClass," +
                            " IFNULL(M.RatioPoint,0)as RatioPoint, IFNULL(M.RatioAmount,0) as RatioAmount," +
                            " C.ContactTel, C.Email, C.CategoryCode " +
                            " from customer C left join ret_membership_class M " +
                            " on C.MembershipClass=M.Class where C.FinCatCode='B55' "+vClause+" ";
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
            }else if(DBStatus.equals("0")){
                String vQuery="Select C.CusCode, C.CusName, C.TermCode," +
                        " C.D_ay, C.SalesPersonCode, C.MembershipClass," +
                        " IFNULL(M.RatioPoint,0)as RatioPoint, IFNULL(M.RatioAmount,0) as RatioAmount, " +
                        " C.ContactTel, C.Email, C.CategoryCode " +
                        " from customer C left join ret_membership_class M on C.MembershipClass=M.Class " +
                        " where C.CusCode <>'' "+vClause+" ";
                Cursor rsData=db.getQuery(vQuery);
               // Log.d("TOTAL ROWS",vQuery+String.valueOf(rsData.getCount()));
                JSONArray results2=new JSONArray();
                while(rsData.moveToNext()){
                    JSONObject row2=new JSONObject();
                    row2.put("CusCode",rsData.getString(0));
                    row2.put("CusName",rsData.getString(1));
                    row2.put("TermCode",rsData.getString(2));
                    row2.put("D_ay",rsData.getString(3));
                    row2.put("SalesPersonCode",rsData.getString(4));
                    row2.put("MembershipClass",rsData.getString(5));
                    row2.put("RatioPoint",rsData.getString(6));
                    row2.put("RatioAmount",rsData.getString(7));
                    row2.put("ContactTel",rsData.getString(8));
                    row2.put("Email",rsData.getString(9));
                    row2.put("CategoryCode",rsData.getString(10));
                    results2.put(row2);
                }
                db.closeDB();
                Log.d("RESULT JSON",results2.toString());
                return results2.toString();
            }else if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                String vQuery="Select C.CusCode, C.CusName, C.TermCode," +
                        " C.D_ay, C.SalesPersonCode, C.MembershipClass," +
                        " IFNULL(M.RatioPoint,0)as RatioPoint, IFNULL(M.RatioAmount,0) as RatioAmount, " +
                        " C.ContactTel, C.Email, C.CategoryCode " +
                        " from customer C left join ret_membership_class M on C.MembershipClass=M.Class " +
                        " where C.CusCode <>'' "+vClause+" ";
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vQuery);
                jsonReq.put("action", "select");
                ConnectorLocal connectorLocal=new ConnectorLocal();
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
}
