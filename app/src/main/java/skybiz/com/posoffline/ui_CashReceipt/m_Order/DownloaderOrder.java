package skybiz.com.posoffline.ui_CashReceipt.m_Order;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
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

import skybiz.com.posoffline.ui_CashReceipt.m_ListSO.Fragment_ListSO;
import skybiz.com.posoffline.ui_CashReceipt.m_ListSO.ListSOParser;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloaderOrder extends AsyncTask<Void,Void,String> {
    Context c;
    RecyclerView rv;
    String IPAddress, UserName, Password,
            DBName,Port,DBStatus,
            ItemConn,URL,z,
            EncodeType,BranchCode,LocationCode,
            DepartmentCode,vCategoryCode="",UserCode,
            CounterCode;
    String CurCode,deviceId;
    FragmentManager fm;
    TelephonyManager telephonyManager;
    public DownloaderOrder(Context c, RecyclerView rv, FragmentManager fm) {
        this.c = c;
        this.rv = rv;
        this.fm=fm;
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
            OrderParser p=new OrderParser(c,result,rv,fm);
            p.execute();
        }
    }

    private String downloadData(){
        JSONObject jsonReq,jsonRes;
       // telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        //deviceId = telephonyManager.getDeviceId();
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            Cursor cur = db.getGeneralSetup();
            while (cur.moveToNext()) {
                CurCode = cur.getString(1);
            }
            String querySet="select ServerName, UserName, Password, " +
                    "DBName, Port, DBStatus, " +
                    "ItemConn, EncodeType, BranchCode," +
                    "LocationCode, DepartmentCode, UserCode," +
                    "CounterCode from tb_setting ";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress       = curSet.getString(0);
                UserName        = curSet.getString(1);
                Password        = curSet.getString(2);
                DBName          = curSet.getString(3);
                Port            = curSet.getString(4);
                DBStatus        =curSet.getString(5);
                ItemConn        =curSet.getString(6);
                EncodeType      =curSet.getString(7);
                BranchCode      =curSet.getString(8);
                LocationCode    =curSet.getString(9);
                DepartmentCode  =curSet.getString(10);
                UserCode        =curSet.getString(11);
                CounterCode     =curSet.getString(12);
            }

            Log.d("ItemConn",ItemConn);
            if(DBStatus.equals("2")){
                /*String vQuery="select c.Qty,c.Doc1No,m.ItemCode,m.ItemGroup,m.Description, " +
                        "ROUND(c.HCUnitCost,2) as HCUnitCost,ROUND(c.HCDiscount,2)as HCDiscount,ROUND(c.DisRate1,2)as DisRate1, c.RunNo," +
                        " c.Description2, c.UOM, c.FactorQty" +
                        " from cloud_cus_inv_dt c inner join stk_master m on c.ItemCode=m.ItemCode  Order By c.RunNo desc";*/
                String vQuery = "select Qty, Doc1No, ItemCode, " +
                        " ItemGroup, Description, IFNULL(HCUnitCost,0) as HCUnitCost, " +
                        " IFNULL(HCDiscount,0)as HCDiscount, IFNULL(DisRate1,0)as DisRate1, " +
                        " RunNo, Description2, UOM, " +
                        " FactorQty, BlankLine, DetailTaxCode, " +
                        " AnalysisCode2, UnitCost, ServiceChargeYN " +
                        " from cloud_cus_inv_dt where ComputerName='"+UserCode+"' " +
                        " Order By RunNo desc";
                Log.d("Query", vQuery);
                jsonReq=new JSONObject();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vQuery);
                jsonReq.put("action", "select");
                ConnectorLocal connectorLocal=new ConnectorLocal();
                String response=connectorLocal.ConnectSocket(IPAddress,8080,jsonReq.toString());
                jsonRes = new JSONObject(response);
                String result=jsonRes.getString("hasil");
                Log.d("JSON AND SERVER",result);
                //db.closeDB();
                if(!result.equals("0")) {
                    return result;
                }else{
                    return null;
                }
                //return result;
            }else {
                /*String vQuery = "select c.Qty,c.Doc1No,m.ItemCode,m.ItemGroup,m.Description, " +
                        "ROUND(c.HCUnitCost,2) as HCUnitCost,ROUND(c.HCDiscount,2)as HCDiscount, " +
                        "ROUND(c.DisRate1,2)as DisRate1, c.RunNo, c.Description2, c.UOM, c.FactorQty " +
                        "from cloud_cus_inv_dt c inner join stk_master m on c.ItemCode=m.ItemCode  Order By c.RunNo desc";*/
                String vQuery = "select Qty, Doc1No, ItemCode, " +
                        " ItemGroup, Description, IFNULL(HCUnitCost,0) as HCUnitCost," +
                        " IFNULL(HCDiscount,0)as HCDiscount, IFNULL(DisRate1,0)as DisRate1, " +
                        " RunNo, Description2, UOM, " +
                        " FactorQty, BlankLine, DetailTaxCode," +
                        " AnalysisCode2, UnitCost, ServiceChargeYN " +
                        " from cloud_cus_inv_dt where ComputerName='"+UserCode+"'" +
                       // " and BlankLine='0' " +
                        " Order By RunNo desc";

                Cursor rsData=db.getQuery(vQuery);
                JSONArray results = new JSONArray();
                while(rsData.moveToNext()) {
                    JSONObject row=new JSONObject();
                    row.put("QTY",rsData.getString(0));
                    row.put("Doc1No",rsData.getString(1));
                    row.put("ItemCode",rsData.getString(2));
                    row.put("ItemGroup",rsData.getString(3));
                    row.put("Description",rsData.getString(4));
                    row.put("HCUnitCost",rsData.getString(5));
                    row.put("HCDiscount",rsData.getString(6));
                    row.put("DisRate1",rsData.getString(7));
                    row.put("RunNo",rsData.getString(8));
                    row.put("Description2",rsData.getString(9));
                    row.put("UOM",rsData.getString(10));
                    row.put("FactorQty",rsData.getString(11));
                    row.put("BlankLine",rsData.getString(12));
                    row.put("DetailTaxCode",rsData.getString(13));
                    row.put("AnalysisCode2",rsData.getString(14));
                    row.put("UnitCost",rsData.getString(15));
                    row.put("ServiceChargeYN",rsData.getString(16));
                    results.put(row);
                }
                return results.toString();
            }
        }catch (SQLiteException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
