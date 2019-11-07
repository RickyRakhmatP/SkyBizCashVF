package skybiz.com.posoffline.m_NewItemList;

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
import skybiz.com.posoffline.m_NewObject.Encode;
import skybiz.com.posoffline.m_NewObject.EncodeChar;
import skybiz.com.posoffline.m_NewObject.GetPoint;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderItem extends AsyncTask<Void,Void,String> {
    Context c;
    String Keyword,DocType,SearchBy;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn,EncodeType;
    String CurCode;
    DialogItem dialogItem;
    JSONObject jsonReq,jsonRes;

    public DownloaderItem(Context c, String DocType, String SearchBy, String Keyword, RecyclerView rv, DialogItem dialogItem) {
        this.c = c;
        this.DocType = DocType;
        this.SearchBy = SearchBy;
        this.Keyword = Keyword;
        this.rv = rv;
        this.dialogItem=dialogItem;
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
            ItemParser p=new ItemParser(c,result,DocType,rv,dialogItem);
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
            String querySet="select ServerName, UserName, Password," +
                    " DBName, Port, DBStatus," +
                    " ItemConn, EncodeType" +
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
                EncodeType=curSet.getString(7);
            }
            String vClause="";
            if(SearchBy.equals("By Code")){
                vClause="and M.ItemCode like '%"+Keyword+"%'";
            }else if(SearchBy.equals("By Desc")){
                vClause="and M.Description like '%"+Keyword+"%'";
            }
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    String sql = "select '" + CurCode + "' as CurCode, '0' as Qty, IFNULL(M.UnitPrice,0) as UnitPrice," +
                            " M.ItemCode, M.Description, M.ItemGroup, " +
                            " IFNULL(G.Printer,'')as Printer, IFNULL(G.Modifier1,'')as Modifier, M.DefaultUOM, " +
                            " M.UOM, M.UOM1, M.UOM2, " +
                            " M.UOM3, M.UOM4, M.UOMFactor1," +
                            " M.UOMFactor2, M.UOMFactor3, M.UOMFactor4," +
                            " M.UOMPrice1, M.UOMPrice2, M.UOMPrice3, " +
                            " M.UOMPrice4, M.RetailTaxCode, M.AlternateItem, " +
                            " IFNULL(M.HCDiscount,0) as HCDiscount, IFNULL(M.DisRate1,'')as DisRate1," +
                            " IF(P.DocType = 'Collect', IFNULL(P.Point,0),0) AS POINT " +
                            " from stk_master M left join stk_group G ON M.ItemGroup=G.ItemGroup " +
                            " left join ret_point P ON M.ItemCode=P.Item " +
                            " where M.SuspendedYN='0' "+vClause+" " +
                            " Group By M.ItemCode Order By M.ItemCode ";
                    Log.d("QUERY",sql);
                    JSONArray results = new JSONArray();
                    Statement statement = conn.createStatement();
                    statement.executeQuery("SET NAMES 'LATIN1'");
                    statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                    if (statement.execute(sql)) {
                        ResultSet resultSet = statement.getResultSet();
                        ResultSetMetaData columns = resultSet.getMetaData();
                        while (resultSet.next()) {
                            Double dUnitPrice=resultSet.getDouble(3);
                            String UnitPrice     =  String.format(Locale.US, "%,.2f", dUnitPrice);
                            JSONObject row = new JSONObject();
                            row.put("CurCode",resultSet.getString(1));
                            row.put("Qty",resultSet.getString(2));
                            row.put("UnitPrice",UnitPrice);
                            row.put("ItemCode",resultSet.getString(4));
                            row.put("Description", charReplace(Encode.setChar(EncodeType,resultSet.getString(5))));
                            row.put("ItemGroup",charReplace(Encode.setChar(EncodeType,resultSet.getString(6))));
                            row.put("Printer",resultSet.getString(7));
                            row.put("Modifier1",resultSet.getString(8));
                            row.put("DefaultUOM",resultSet.getString(9));
                            row.put("UOM",charReplace(Encode.setChar(EncodeType,resultSet.getString(10))));
                            row.put("UOM1",Encode.setChar(EncodeType,resultSet.getString(11)));
                            row.put("UOM2",Encode.setChar(EncodeType,resultSet.getString(12)));
                            row.put("UOM3",Encode.setChar(EncodeType,resultSet.getString(13)));
                            row.put("UOM4",Encode.setChar(EncodeType,resultSet.getString(14)));
                            row.put("UOMFactor1",resultSet.getString(15));
                            row.put("UOMFactor2",resultSet.getString(16));
                            row.put("UOMFactor3",resultSet.getString(17));
                            row.put("UOMFactor4",resultSet.getString(18));
                            row.put("UOMPrice1",resultSet.getString(19));
                            row.put("UOMPrice2",resultSet.getString(20));
                            row.put("UOMPrice3",resultSet.getString(21));
                            row.put("UOMPrice4",resultSet.getString(22));
                            row.put("RetailTaxCode",resultSet.getString(23));
                            row.put("AlternateItem",charReplace(Encode.setChar(EncodeType,resultSet.getString(24))));
                            row.put("HCDiscount",resultSet.getString(25));
                            row.put("DisRate1",resultSet.getString(26));
                            row.put("Point",resultSet.getString(27));
                            results.put(row);
                        }
                        resultSet.close();
                    }
                    statement.close();
                    return results.toString();
                }
               // z="success";
            }else if(DBStatus.equals("0")){
                String sql = "select '" + CurCode + "' as CurCode, '0' as Qty, IFNULL(M.UnitPrice,0) as UnitPrice," +
                        " M.ItemCode, M.Description, M.ItemGroup, " +
                        " IFNULL(G.Printer,'')as Printer, IFNULL(G.Modifier1,'')as Modifier1, M.DefaultUOM, " +
                        " M.UOM, M.UOM1, M.UOM2, " +
                        " M.UOM3, M.UOM4, M.UOMFactor1," +
                        " M.UOMFactor2, M.UOMFactor3, M.UOMFactor4," +
                        " M.UOMPrice1, M.UOMPrice2, M.UOMPrice3, " +
                        " M.UOMPrice4, M.RetailTaxCode, M.AlternateItem, " +
                        " IFNULL(M.HCDiscount,0) as HCDiscount, IFNULL(M.DisRate1,'')as DisRate1," +
                        " (CASE P.DocType WHEN  'Collect' THEN P.Point ELSE '0' END) as POINT "+
                        " from stk_master M left join stk_group G ON M.ItemGroup=G.ItemGroup " +
                        " left join ret_point P ON M.ItemCode=P.Item " +
                        " where M.ItemCode<>'' "+vClause+"  Group By M.ItemCode Order By M.ItemCode ";
                Cursor rsData=db.getQuery(sql);
                JSONArray results2=new JSONArray();
                while(rsData.moveToNext()){
                    JSONObject row=new JSONObject();
                    row.put("CurCode",rsData.getString(0));
                    row.put("Qty",rsData.getString(1));
                    row.put("UnitPrice",rsData.getString(2));
                    row.put("ItemCode",rsData.getString(3));
                    row.put("Description",rsData.getString(4));
                    row.put("ItemGroup",rsData.getString(5));
                    row.put("Printer",rsData.getString(6));
                    row.put("Modifier1",rsData.getString(7));
                    row.put("DefaultUOM",rsData.getString(8));
                    row.put("UOM",rsData.getString(9));
                    row.put("UOM1",rsData.getString(10));
                    row.put("UOM2",rsData.getString(11));
                    row.put("UOM3",rsData.getString(12));
                    row.put("UOM4",rsData.getString(13));
                    row.put("UOMFactor1",rsData.getString(14));
                    row.put("UOMFactor2",rsData.getString(15));
                    row.put("UOMFactor3",rsData.getString(16));
                    row.put("UOMFactor4",rsData.getString(17));
                    row.put("UOMPrice1",rsData.getString(18));
                    row.put("UOMPrice2",rsData.getString(19));
                    row.put("UOMPrice3",rsData.getString(20));
                    row.put("UOMPrice4",rsData.getString(21));
                    row.put("RetailTaxCode",rsData.getString(22));
                    row.put("AlternateItem",rsData.getString(23));
                    row.put("HCDiscount",rsData.getString(24));
                    row.put("DisRate1",rsData.getString(25));
                    row.put("Point",GetPoint.Value(c,rsData.getString(3),"Collect"));
                    results2.put(row);
                }
                db.closeDB();
                Log.d("RESULT JSON",results2.toString());
                return results2.toString();
            }else if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                String sql = "select '" + CurCode + "' as CurCode, '0' as Qty, IFNULL(M.UnitPrice,0) as UnitPrice, " +
                        " M.ItemCode, M.Description, M.ItemGroup, " +
                        " IFNULL(G.Printer,'')as Printer, IFNULL(G.Modifier1,'') as Modifier1, M.DefaultUOM, " +
                        " M.UOM, M.UOM1, M.UOM2, " +
                        " M.UOM3, M.UOM4, M.UOMFactor1," +
                        " M.UOMFactor2, M.UOMFactor3, M.UOMFactor4, " +
                        " M.UOMPrice1, M.UOMPrice2, M.UOMPrice3, " +
                        " M.UOMPrice4, M.RetailTaxCode, M.AlternateItem, " +
                        " IFNULL(M.HCDiscount,0) as HCDiscount, IFNULL(M.DisRate1,0)as DisRate1," +
                        " '0' as Point "+
                        " from stk_master M left join stk_group G ON M.ItemGroup=G.ItemGroup " +
                        " left join ret_point P ON M.ItemCode=P.Item " +
                        " where M.ItemCode<>'' "+vClause+" " +
                        " Group By M.ItemCode "+
                        " Order By M.ItemCode ";
                Log.d("QUERY",sql);
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", sql);
                jsonReq.put("action", "select");
                String response=connectorLocal.ConnectSocket(IPAddress,8080,jsonReq.toString());
                jsonRes = new JSONObject(response);
                String result=jsonRes.getString("hasil");
                Log.d("JSON",result);
                return result;
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String charReplace(String text){
        String newText=text.replaceAll("[\\.$|,|;|']","");
        return newText;
    }
}
