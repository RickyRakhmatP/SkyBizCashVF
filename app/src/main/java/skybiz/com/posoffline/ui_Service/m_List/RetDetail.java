package skybiz.com.posoffline.ui_Service.m_List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Service.MService;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class RetDetail extends AsyncTask<Void,Void,String> {
    Context c;
    String Doc1No;
    String IPAddress,UserName,Password,
            Port,DBName,URL,
            z,DBStatus,EncodeType,
            BranchCode,LocationCode,deviceId;
    TelephonyManager telephonyManager;

    public RetDetail(Context c, String doc1No) {
        this.c = c;
        Doc1No = doc1No;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.fnretimage();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("success")){
            ((MService)c).refreshOrder();
           // Toast.makeText(c,"failure downloading image", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"failure retreive data detail", Toast.LENGTH_SHORT).show();
        }
    }
    private String fnretimage() {
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = telephonyManager.getDeviceId();
            String querySet = "select ServerName, UserName, Password, " +
                    "DBName, Port, DBStatus, " +
                    "EncodeType, BranchCode, LocationCode " +
                    "from tb_setting ";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(0);
                UserName = curSet.getString(1);
                Password = curSet.getString(2);
                DBName = curSet.getString(3);
                Port = curSet.getString(4);
                DBStatus = curSet.getString(5);
                EncodeType = curSet.getString(6);
                BranchCode = curSet.getString(7);
                LocationCode = curSet.getString(8);
            }
            String del="delete from cloud_cus_inv_dt where DocType='Service' ";
            db.addQuery(del);
            if (DBStatus.equals("1")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    String check="select count(*)as numrows from stk_service_dt where Doc1No='"+Doc1No+"' ";
                    Statement stmtCheck = conn.createStatement();
                    stmtCheck.execute(check);
                    ResultSet rsCheck = stmtCheck.getResultSet();
                    int numrows=0;
                    while (rsCheck.next()) {
                        numrows=rsCheck.getInt(1);
                    }
                    if(numrows>0) {
                        String sql = "Select  Doc1No, repairpartscode, repairpartsdesc, " +
                                " repairpartsserialno, repairpartsqty, repairunitcost, " +
                                " repairlineamount, L_ink, '', " +
                                " BlankLine, N_o, ItemCode, " +
                                " Description, Qty, FactorQty, " +
                                " UOM, UOMSingular, HCUnitCost, " +
                                " DisRate1, DisRate2, DisRate3, " +
                                " HCDiscount, TaxRate1, TaxRate2, " +
                                " TaxRate3, DetailTaxCode, DetailTaxType, " +
                                " HCTax, HCLineAmt, DocType, " +
                                " DefectCode " +
                                " from stk_service_dt where Doc1No='" + Doc1No + "' Order by repairpartscode ";
                        Statement statement = conn.createStatement();
                        statement.execute(sql);
                        ResultSet rsData = statement.getResultSet();
                        while (rsData.next()) {
                            String vSQLInsert = "INSERT INTO cloud_cus_inv_dt (Doc1No, N_o, ItemCode, Description, " +
                                    "Qty, FactorQty, UOM, UOMSingular, " +
                                    "HCUnitCost, DisRate1, HCDiscount, TaxRate1, " +
                                    "HCTax, DetailTaxCode, HCLineAmt, BranchCode, " +
                                    "DepartmentCode, ProjectCode, SalesPersonCode, LocationCode, " +
                                    "WarrantyDate, BlankLine, DocType, AnalysisCode2," +
                                    "ComputerName, SORunNo)" +
                                    "VALUES(" +
                                    " '" + rsData.getString(1) + "', 'a' ,'" + rsData.getString(2) + "','" + rsData.getString(3) + "'," +
                                    " '" + rsData.getString(5) + "','" + rsData.getString(15) + "','" + rsData.getString(16) + "','" + rsData.getString(17) + "'," +
                                    " '" + rsData.getString(6) + "','" + rsData.getString(19) + "','" + rsData.getString(22) + "','" + rsData.getString(23) + "'," +
                                    " '" + rsData.getString(28) + "','" + rsData.getString(26) + "','" + rsData.getString(7) + "'," +
                                    " '" + BranchCode + "','android','android','android','" + LocationCode + "'," +
                                    " '', '0', 'Service', '0', '" + deviceId + "', '0')";
                            db.addQuery(vSQLInsert);
                        }
                        z="success";
                    }else{
                        z="error";
                    }
                }

            }else{
                z="success";
            }
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }
}
