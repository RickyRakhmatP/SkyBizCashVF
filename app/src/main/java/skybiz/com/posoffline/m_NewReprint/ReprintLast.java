package skybiz.com.posoffline.m_NewReprint;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
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

import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothPrinter;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothZebra;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_USB.PrintingUSB;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI.PrintingWifi;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class ReprintLast extends AsyncTask<Void,Void,String> {
    Context c;
    String z,IPAddress,UserName,
            Password,Port,URL,
            DBName,EncodeType,Doc1No,
            DocType,DBStatus;
    Boolean isBT;

    public ReprintLast(Context c, String DocType) {
        this.c = c;
        this.DocType = DocType;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.printlast();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("error")){
            Toast.makeText(c,"Last Reprint Failure", Toast.LENGTH_SHORT).show();
        }else{
            RePrint rePrint=new RePrint(c,DocType,Doc1No);
            rePrint.execute();
        }
    }

    private String printlast() {
        try {
            z="error";
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName, UserName, Password," +
                    "DBName, Port, DBStatus," +
                    "EncodeType "+
                    "from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(0);
                UserName = curSet.getString(1);
                Password = curSet.getString(2);
                DBName = curSet.getString(3);
                Port = curSet.getString(4);
                DBStatus=curSet.getString(5);
                EncodeType=curSet.getString(6);
            }
            String tb_header="";
            if(DocType.equals("SO")){
                tb_header="stk_sales_order_hd";
            }else if(DocType.equals("CusCN")){
                tb_header="stk_cus_inv_hd";
            }
            String qCheck="select Doc1No from "+tb_header+"  ORDER BY D_ate DESC, Doc1No DESC LIMIT 1 ";
            if(DBStatus.equals("1")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                if (conn != null) {
                    Statement stmt = conn.createStatement();
                    stmt.execute(qCheck);
                    ResultSet rsData = stmt.getResultSet();
                    while (rsData.next()) {
                        Doc1No=rsData.getString(1);
                    }
                    z=Doc1No;
                }
            }else{
                Cursor rsData=db.getQuery(qCheck);
                while(rsData.moveToNext()){
                    Doc1No=rsData.getString(0);
                }
                z=Doc1No;
            }
            db.closeDB();
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }

}
