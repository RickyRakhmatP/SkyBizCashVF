package skybiz.com.posoffline.ui_Listing.m_ItemSales;

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
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class ItemSalesDownload extends AsyncTask<Void,Void,String> {
    Context c;
    String DateFrom,DateTo,ItemGroup;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,Mgt01YN;
    String CurCode,DocType;

    public ItemSalesDownload(Context c, String DateFrom, String DateTo,
                             String ItemGroup, RecyclerView rv) {
        this.c = c;
        this.DateFrom   = DateFrom;
        this.DateTo     = DateTo;
        this.ItemGroup  = ItemGroup;
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
        }else if(result.equals("error")) {
            Toast.makeText(c,"check permission to retrieve data", Toast.LENGTH_SHORT).show();
        }else{
            ItemSalesParser p=new ItemSalesParser(c,DocType,result,rv);
            p.execute();
        }
    }

    private String downloadData(){
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName,UserName,Password," +
                    "DBName,Port,DBStatus," +
                    "Mgt01YN" +
                    " from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(0);
                UserName = curSet.getString(1);
                Password = curSet.getString(2);
                DBName = curSet.getString(3);
                Port = curSet.getString(4);
                DBStatus=curSet.getString(5);
                Mgt01YN=curSet.getString(6);
            }
            //String vGroupBy="D.ItemCode ";
            String vClause="";
            if(!ItemGroup.isEmpty()){
               // vGroupBy="M.ItemGroup ";
                vClause =" and M.ItemGroup='"+ItemGroup+"' ";
            }
            String sql = "select D.ItemCode, SUM(D.Qty)as Qty, SUM(D.HCLineAmt) as Amount," +
                    " D.Description " +
                    " from stk_cus_inv_hd H inner join stk_cus_inv_dt D ON H.Doc1No=D.Doc1No " +
                    " inner join stk_master M on D.ItemCode=M.ItemCode " +
                    " where H.DocType='CS' and H.Status2<>'Void' " +
                    " and H.D_ate>='" + DateFrom + "' and H.D_ate<='" + DateTo + "'" +
                    " "+vClause+" " +
                    " Group By D.ItemCode Order By D.ItemCode ";

            if(DBStatus.equals("1")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    Log.d("QUERY",sql);
                    Statement statement = conn.createStatement();
                    statement.execute(sql);
                    ResultSet rsData = statement.getResultSet();
                    JSONArray results2 = new JSONArray();
                    while (rsData.next()) {
                        JSONObject row2 = new JSONObject();
                        Double dAmount = rsData.getDouble(3);
                        String Amount = String.format(Locale.US, "%,.2f", dAmount);
                        Double dQty = rsData.getDouble(2);
                        String Qty = String.format(Locale.US, "%,.0f", dQty);
                        row2.put("ItemCode", rsData.getString(1)+"\n"+ rsData.getString(4));
                        row2.put("Qty", Qty);
                        row2.put("Amount", Amount);
                        results2.put(row2);
                    }
                    db.closeDB();
                    Log.d("RESULT JSON", results2.toString());
                    return results2.toString();
                }
            }else{
                Cursor rsData=db.getQuery(sql);
                JSONArray results2 = new JSONArray();
                while (rsData.moveToNext()) {
                    JSONObject row2 = new JSONObject();
                    Double dAmount = rsData.getDouble(2);
                    String Amount = String.format(Locale.US, "%,.2f", dAmount);
                    Double dQty = rsData.getDouble(1);
                    String Qty = String.format(Locale.US, "%,.0f", dQty);
                    row2.put("ItemCode", rsData.getString(0)+"\n"+ rsData.getString(3));
                    row2.put("Qty", Qty);
                    row2.put("Amount", Amount);
                    results2.put(row2);
                }
                db.closeDB();
                Log.d("RESULT JSON", results2.toString());
                return results2.toString();
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return null;
    }
}
