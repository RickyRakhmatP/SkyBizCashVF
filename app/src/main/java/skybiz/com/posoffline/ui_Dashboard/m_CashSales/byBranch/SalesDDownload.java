package skybiz.com.posoffline.ui_Dashboard.m_CashSales.byBranch;

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
import skybiz.com.posoffline.ui_CashReceipt.m_Customer.DialogCustomer;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Dashboard.Dashboard;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class SalesDDownload extends AsyncTask<Void,Void,String> {
    Context c;
    String D_ate,CurCode;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
    JSONObject jsonReq,jsonRes;
    String uFrom="By Branch";
    String TotalSales;

    public SalesDDownload(Context c,String D_ate,  RecyclerView rv) {
        this.c = c;
        this.D_ate = D_ate;
        this.rv = rv;;
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
           // ((Dashboard)c).setTotalSales(TotalSales);
            SalesDParser p=new SalesDParser(c,result,rv,uFrom);
            p.execute();
        }
    }

    private String downloadData(){
        try {
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
            Cursor cur = db.getGeneralSetup();
            while (cur.moveToNext()) {
                CurCode = cur.getString(1);
            }
            String sql ="Select  D.BranchCode, IFNULL(SUM(H.HCNetAmt),0)as Total, '"+CurCode+"' as CurCode, " +
                    " '' as D_ate from stk_cus_inv_hd H inner join stk_cus_inv_dt D ON H.Doc1No=D.Doc1No " +
                    "where H.D_ate='"+D_ate+"' and H.DocType='CS' Group By D.BranchCode Order By D.BranchCode  ";
            Log.d("QUERY",sql);
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    JSONArray results = new JSONArray();
                    Statement statement = conn.createStatement();
                    if (statement.execute(sql)) {
                        ResultSet rsData = statement.getResultSet();
                        Double dTotalSales=0.00;
                        while (rsData.next()) {
                            JSONObject row2=new JSONObject();
                            String Total=String.format(Locale.US, "%,.2f", rsData.getDouble(2));
                            row2.put("BranchCode",rsData.getString(1));
                            row2.put("Total",Total);
                            row2.put("CurCode",rsData.getString(3));
                            row2.put("D_ate",rsData.getString(4));
                            dTotalSales+=rsData.getDouble(2);
                            results.put(row2);
                        }
                        rsData.close();
                        TotalSales=String.format(Locale.US, "%,.2f", dTotalSales);
                    }
                    statement.close();
                    return results.toString();
                }
            }else if(DBStatus.equals("0")){
                Cursor rsData=db.getQuery(sql);
                JSONArray results2=new JSONArray();
                Double dTotalSales=0.00;
                while(rsData.moveToNext()){
                    JSONObject row2=new JSONObject();
                    String Total=String.format(Locale.US, "%,.2f", rsData.getDouble(1));
                    row2.put("BranchCode",rsData.getString(0));
                    row2.put("Total",Total);
                    row2.put("CurCode",rsData.getString(2));
                    row2.put("D_ate",rsData.getString(3));
                    dTotalSales+=rsData.getDouble(1);
                    results2.put(row2);
                }
                db.closeDB();
                TotalSales=String.format(Locale.US, "%,.2f", dTotalSales);
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
