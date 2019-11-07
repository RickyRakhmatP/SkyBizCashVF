package skybiz.com.posoffline.ui_CashReceipt.m_VoidBill;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class VoidBill extends AsyncTask<Void,Void,String> {
    Context c;
    String Doc1No;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus;
    public VoidBill(Context c, String Doc1No) {
        this.c = c;
        this.Doc1No = Doc1No;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.voidbill();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("error")){
            Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Success Void Bill ", Toast.LENGTH_SHORT).show();
        }
    }
    private String voidbill(){
        try {
            z="error";
            JSONObject jsonReq,jsonRes;
            DBAdapter db=new DBAdapter(c);
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
            }
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                if (conn != null) {
                    String vUpdateHd="update stk_cus_inv_hd set Status2='Void' where Doc1No='"+Doc1No+"' ";
                    Statement stmtHd=conn.createStatement();
                    stmtHd.execute(vUpdateHd);

                    String vUpdateR="update stk_receipt2 set VoidYN='1', CashAmt='0', CC1Amt='0', ChangeAmt='0'," +
                            "BalanceAmount='0', CC1ChargesAmt='0' where Doc1No='"+Doc1No+"' ";
                    Statement stmtR=conn.createStatement();
                    stmtR.execute(vUpdateR);


                    String vUpdateDt="update stk_detail_trn_out set QtyOUT='0' where Doc3No='"+Doc1No+"' ";
                    Statement stmtDt=conn.createStatement();
                    stmtDt.execute(vUpdateDt);
                }
            }
            String vUpdateHd = "update stk_cus_inv_hd set Status2='Void' where Doc1No='" + Doc1No + "' ";

            String vUpdateR = "update stk_receipt2 set VoidYN='1', CashAmt='0', CC1Amt='0', ChangeAmt='0'," +
                    "BalanceAmount='0', CC1ChargesAmt='0' where Doc1No='" + Doc1No + "' ";

            String vUpdateDt = "update stk_detail_trn_out set QtyOUT='0' where Doc3No='" + Doc1No + "' ";

            if(DBStatus.equals("2")) {
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vUpdateHd);
                jsonReq.put("action", "update");
                String rsHd = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rsHd);
                String resHd = jsonRes.getString("hasil");

                jsonReq=new JSONObject();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vUpdateR);
                jsonReq.put("action", "update");
                String rsRec = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rsRec);
                String resRec = jsonRes.getString("hasil");

                jsonReq=new JSONObject();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vUpdateDt);
                jsonReq.put("action", "update");
                String rsDt = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rsDt);
                String resDt = jsonRes.getString("success");
                z = resDt;
            }else {
                db.addQuery(vUpdateHd);
                db.addQuery(vUpdateR);
                db.addQuery(vUpdateDt);
                z = "success";
                db.closeDB();
            }
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return z;
    }
}
