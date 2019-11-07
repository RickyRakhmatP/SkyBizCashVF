package skybiz.com.posoffline.m_PriceMatrix;

import android.content.Context;
import android.database.Cursor;
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
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.Dialog_Qty;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;


/**
 * Created by 7 on 14/11/2017.
 */

public class DownloadPriceMatrix extends AsyncTask<Void, Void, String> {
    Context c;
    String IPAddress,DBName,UserName,
            Password,URL,Port,ItemConn
            ,DBStatus;
    RecyclerView rv;
    Dialog_Qty dialog_qty;


    public DownloadPriceMatrix(Context c, RecyclerView rv, Dialog_Qty dialog_qty) {
        this.c = c;
        this.rv = rv;
        this.dialog_qty=dialog_qty;
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
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);
        if(jsonData==null){
            Toast.makeText(c,"Unsuccessfull, No data retrieve",Toast.LENGTH_SHORT).show();
        }else{
            //parse
            PriceMatrixParser p=new PriceMatrixParser(c,jsonData,rv,dialog_qty);
            p.execute();
        }
    }

    private String downloadData(){
        try {
            DBAdapter db=new DBAdapter(c);
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
                ItemConn=cur1.getString(9);
            }
            String sql="select ItemCode, ItemGroup, CategoryCode, " +
                    " Description, Pct, Criteria," +
                    " PeriodYN, B_ase, TimeStart, TimeEnd," +
                    " Status, ServiceChargeYN, IFNULL(Memo,'')as Memo, " +
                    " Qty " +
                    " From stk_pricematrix " +
                    " where Description<>'' " +
                    " Group By Description " +
                    " Order By B_ase Desc";
            JSONArray results = new JSONArray();
            if(DBStatus.equals("1")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    Statement stmt = conn.createStatement();
                    stmt.executeQuery("SET NAMES 'LATIN1'");
                    stmt.executeQuery("SET CHARACTER SET 'LATIN1' ");
                    stmt.execute(sql);
                    ResultSet rsData=stmt.getResultSet();
                    while (rsData.next()) {
                       JSONObject row=new JSONObject();
                       row.put("ItemCode",rsData.getString(1));
                       row.put("ItemGroup",rsData.getString(2) );
                       row.put("CategoryCode",rsData.getString(3));
                       row.put("Description",rsData.getString(4));
                       row.put("Pct",rsData.getString(5));
                       row.put("Criteria",rsData.getString(6));
                       row.put("PeriodYN",rsData.getString(7));
                       row.put("B_ase",rsData.getString(8));
                       row.put("TimeStart",rsData.getString(9));
                       row.put("TimeEnd",rsData.getString(10));
                       row.put("Status",rsData.getString(11));
                       row.put("ServiceChargeYN",rsData.getString(12));
                       row.put("Memo",rsData.getString(13));
                        row.put("Qty",rsData.getString(14));
                       results.put(row);

                    }
                    stmt.close();
                    Log.d("RESULT", results.toString());
                    return results.toString();
                }
            }else {
                Cursor rsData=db.getQuery(sql);
                while (rsData.moveToNext()) {
                    JSONObject row=new JSONObject();
                    row.put("ItemCode",rsData.getString(0));
                    row.put("ItemGroup",rsData.getString(1) );
                    row.put("CategoryCode",rsData.getString(2));
                    row.put("Description",rsData.getString(3));
                    row.put("Pct",rsData.getString(4));
                    row.put("Criteria",rsData.getString(5));
                    row.put("PeriodYN",rsData.getString(6));
                    row.put("B_ase",rsData.getString(7));
                    row.put("TimeStart",rsData.getString(8));
                    row.put("TimeEnd",rsData.getString(9));
                    row.put("Status",rsData.getString(10));
                    row.put("ServiceChargeYN",rsData.getString(11));
                    row.put("Memo",rsData.getString(12));
                    row.put("Qty",rsData.getString(13));
                    results.put(row);
                }
                Log.d("RESULT", results.toString());
                return results.toString();
            }
            db.closeDB();
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
