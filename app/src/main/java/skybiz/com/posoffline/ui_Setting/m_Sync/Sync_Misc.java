package skybiz.com.posoffline.ui_Setting.m_Sync;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class Sync_Misc extends AsyncTask<Void,Integer,String> {
    Context c;
    ProgressBar pb;
    String IPAddress,UserName,Password,URL,DBName,z,Port,DBStatus,ItemConn;

    public Sync_Misc(Context c, ProgressBar pb) {
        this.c = c;
        this.pb = pb;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pb.setMax(100);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        pb.setProgress(progress[0]);
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.downloadItem();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //pd.dismiss();
        if(result.equals("error")){
            Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Successfull, All Syncrhonize", Toast.LENGTH_SHORT).show();
        }
    }

    private String downloadItem() {
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String querySet = "select * from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(1);
                UserName = curSet.getString(2);
                Password = curSet.getString(3);
                DBName = curSet.getString(4);
                Port = curSet.getString(5);
                DBStatus = curSet.getString(7);
                ItemConn = curSet.getString(8);
            }
            if (DBStatus.equals("2")) {
                z = "success";
            } else {
                // URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=UTF-8";
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn == null) {
                    z = "error";
                } else {
                    String vDel="delete from stk_othercharges";
                    db.addQuery(vDel);
                    String sql = "select OCCode, Description, FORMAT(R_ate,2) as R_ate, T_ype, " +
                            " GLCode, L_ink, FORMAT(UnitCost,2) as UnitCost, UOM, " +
                            " PurchaseTaxCode, SalesTaxCode, RetailTaxCode, OCGroup," +
                            " OCAna1, OCAna2, OCAna3, SuspendedYN, " +
                            " MinimumCharge, PurchaseAccount, BasedGrossAmountYN, ImportServiceYN " +
                            " from stk_othercharges where SuspendedYN='0' ";
                    Statement statement = conn.createStatement();
                    statement.executeQuery("SET NAMES 'LATIN1'");
                    statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                    statement.execute(sql);
                    ResultSet rsData = statement.getResultSet();
                    int i = 1;
                    while(rsData.next()){
                        String qInsert="Insert into stk_othercharges(OCCode, Description, R_ate," +
                                " T_ype, GLCode, L_ink," +
                                " UnitCost, UOM, PurchaseTaxCode," +
                                " SalesTaxCode, RetailTaxCode, OCGroup, " +
                                " OCAna1, OCAna2, OCAna3, " +
                                " SuspendedYN, MinimumCharge, PurchaseAccount," +
                                " BasedGrossAmountYN, ImportServiceYN)values(" +
                                " '"+rsData.getString("OCCode")+"', '"+rsData.getString("Description")+"', '"+rsData.getString("R_ate")+"', " +
                                " '"+rsData.getString("T_ype")+"', '"+rsData.getString("GLCode")+"', '"+rsData.getString("L_ink")+"', " +
                                " '"+rsData.getString("UnitCost")+"', '"+rsData.getString("UOM")+"', '"+rsData.getString("PurchaseTaxCode")+"', " +
                                " '"+rsData.getString("SalesTaxCode")+"', '"+rsData.getString("RetailTaxCode")+"', '"+rsData.getString("OCGroup")+"', " +
                                " '"+rsData.getString("OCAna1")+"', '"+rsData.getString("OCAna2")+"', '"+rsData.getString("OCAna3")+"', " +
                                " '"+rsData.getString("SuspendedYN")+"', '"+rsData.getString("MinimumCharge")+"', '"+rsData.getString("PurchaseAccount")+"', " +
                                " '"+rsData.getString("BasedGrossAmountYN")+"', '"+rsData.getString("ImportServiceYN")+"' )";
                        db.addQuery(qInsert);
                        Log.d("INSERT MISC",qInsert);
                        publishProgress(i);
                        i++;
                    }
                }
                z="success";
            }
            db.closeDB();
            return z;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }
}
