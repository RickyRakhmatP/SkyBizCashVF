package skybiz.com.posoffline.m_NewTerm;

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

public class DownloadTerm extends AsyncTask<Void,Void,String> {
    Context c;
    String SearchBy,Keyword;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
    String CurCode;
    DialogTerm dialogTerm;
    JSONObject jsonReq,jsonRes;

    public DownloadTerm(Context c, String SearchBy , String Keyword, RecyclerView rv, DialogTerm dialogTerm) {
        this.c = c;
        this.SearchBy = SearchBy;
        this.Keyword = Keyword;
        this.rv = rv;
        this.dialogTerm=dialogTerm;
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
            TermParser p=new TermParser(c,result,rv,dialogTerm);
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
            if(SearchBy.equals("By Code")){
                vClause="where TermCode like '%"+Keyword+"%' ";
            }else if(SearchBy.equals("By Desc")){
                vClause="where TermDesc like '%"+Keyword+"%' ";
            }
            String sql ="Select TermCode,TermDesc,D_ay from stk_term  "+vClause+" ";
            Log.d("QUERY TERM",sql);
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
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
                Cursor rsData=db.getQuery(sql);
                JSONArray results2=new JSONArray();
                while(rsData.moveToNext()){
                    JSONObject row2=new JSONObject();
                    row2.put("TermCode",rsData.getString(0));
                    row2.put("TermDesc",rsData.getString(1));
                    row2.put("D_ay",rsData.getString(2));
                    results2.put(row2);
                }
                db.closeDB();
                Log.d("RESULT JSON",results2.toString());
                return results2.toString();
            }else if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", sql);
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
