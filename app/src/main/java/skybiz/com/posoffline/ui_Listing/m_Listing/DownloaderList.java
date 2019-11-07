package skybiz.com.posoffline.ui_Listing.m_Listing;

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

public class DownloaderList extends AsyncTask<Void,Void,String> {
    Context c;
    String DateFrom,DateTo;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,Mgt01YN;
    String CurCode,DocType;

    public DownloaderList(Context c, String DocType,String DateFrom, String DateTo, RecyclerView rv) {
        this.c = c;
        this.DocType = DocType;
        this.DateFrom = DateFrom;
        this.DateTo = DateTo;
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
            ListParser p=new ListParser(c,DocType,result,rv);
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
            if(DBStatus.equals("1")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    String sql="";
                    if(!DocType.equals("SO")) {
                        sql = "select H.Doc1No, H.D_ateTime, H.HCNetAmt, " +
                                " sum(D.Qty) as Qty, IFNULL(C.CusName,'')as CusName, '1' as SynYN, " +
                                " IFNULL(C.CusCode,'')as CusCode, H.Status2  " +
                                " from stk_cus_inv_hd H inner join stk_cus_inv_dt D ON H.Doc1No=D.Doc1No " +
                                " left join customer C ON H.CusCode=C.CusCode where H.DocType='" + DocType + "' " +
                                " and H.D_ate>='" + DateFrom + "' and H.D_ate<='" + DateTo + "' " +
                                " Group By H.Doc1No Order By H.D_ate desc ";
                    }else{
                        sql = "select H.Doc1No, H.D_ate, H.HCNetAmt, " +
                                " sum(D.Qty) as Qty, IFNULL(C.CusName,'')as CusName, '1' as SynYN, " +
                                " IFNULL(C.CusCode,'')as CusCode, H.Status " +
                                " from stk_sales_order_hd H inner join stk_sales_order_dt D ON H.Doc1No=D.Doc1No " +
                                " left join customer C ON H.CusCode=C.CusCode where H.DocType='" + DocType + "' " +
                                " and H.D_ate>='" + DateFrom + "' and H.D_ate<='" + DateTo + "' " +
                                " Group By H.Doc1No Order By H.D_ateTime DESC, H.Doc1No DESC ";
                    }
                    Log.d("QUERY",sql);
                    Statement statement = conn.createStatement();
                    statement.execute(sql);
                    ResultSet rsData = statement.getResultSet();
                    JSONArray results2 = new JSONArray();
                    while (rsData.next()) {
                        JSONObject row2 = new JSONObject();
                        Double dHCNetAmt = rsData.getDouble(3);
                        String HCNetAmt = String.format(Locale.US, "%,.2f", dHCNetAmt);
                        Double dQty = rsData.getDouble(4);
                        String TotalQty = String.format(Locale.US, "%,.2f", dQty);
                        row2.put("Doc1No", rsData.getString(1));
                        row2.put("D_ate", rsData.getString(2));
                        row2.put("HCNetAmt", HCNetAmt);
                        row2.put("TotalQty", TotalQty);
                        row2.put("CusName", rsData.getString(5));
                        row2.put("SyncYN", rsData.getString(6));
                        row2.put("CusCode", rsData.getString(7));
                        row2.put("Status", rsData.getString(8));
                        results2.put(row2);
                    }
                    db.closeDB();
                    Log.d("RESULT JSON", results2.toString());
                    return results2.toString();
                }
            }else{
                String sql="";
                if(!DocType.equals("SO")) {
                    sql = "select H.Doc1No, H.D_ateTime, H.HCNetAmt, " +
                            " sum(D.Qty) as Qty, IFNULL(C.CusName,'')as CusName, H.SynYN," +
                            " IFNULL(C.CusCode,'')as CusCode, H.Status2 " +
                            " from stk_cus_inv_hd H inner join stk_cus_inv_dt D ON H.Doc1No=D.Doc1No " +
                            " left join customer C ON H.CusCode=C.CusCode where H.DocType='" + DocType + "' " +
                            " and H.D_ate>='" + DateFrom + "' and H.D_ate<='" + DateTo + "' " +
                            " Group By H.Doc1No Order By H.D_ate desc ";
                }else{
                    sql = "select H.Doc1No, H.D_ate, H.HCNetAmt, " +
                            " sum(D.Qty) as Qty, IFNULL(C.CusName,'')as CusName, H.SynYN, " +
                            " IFNULL(C.CusCode,'')as CusCode, H.Status " +
                            " from stk_sales_order_hd H inner join stk_sales_order_dt D ON H.Doc1No=D.Doc1No " +
                            " left join customer C ON H.CusCode=C.CusCode where H.DocType='" + DocType + "' " +
                            " and H.D_ate>='" + DateFrom + "' and H.D_ate<='" + DateTo + "' " +
                            " Group By H.Doc1No Order By H.D_ate desc ";
                }
                Cursor rsData=db.getQuery(sql);
                JSONArray results2 = new JSONArray();
                while (rsData.moveToNext()) {
                    JSONObject row2 = new JSONObject();
                    Double dHCNetAmt = rsData.getDouble(2);
                    String HCNetAmt = String.format(Locale.US, "%,.2f", dHCNetAmt);
                    Double dQty = rsData.getDouble(3);
                    String TotalQty = String.format(Locale.US, "%,.2f", dQty);
                    row2.put("Doc1No", rsData.getString(0));
                    row2.put("D_ate", rsData.getString(1));
                    row2.put("HCNetAmt", HCNetAmt);
                    row2.put("TotalQty", TotalQty);
                    row2.put("CusName", rsData.getString(4));
                    row2.put("SyncYN", rsData.getString(5));
                    row2.put("CusCode", rsData.getString(6));
                    row2.put("Status", rsData.getString(7));
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
