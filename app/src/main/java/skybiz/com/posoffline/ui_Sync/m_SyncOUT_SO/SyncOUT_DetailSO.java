package skybiz.com.posoffline.ui_Sync.m_SyncOUT_SO;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_NewObject.DecodeChar;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 29/12/2017.
 */

public class SyncOUT_DetailSO extends AsyncTask<Void, Integer, String> {
    Context c;
    ProgressBar pgbar;
    String IPAddress,UserName,Password,URL,DBName,z,Port;

    public SyncOUT_DetailSO(Context c, ProgressBar pgbar) {
        this.c = c;
        this.pgbar = pgbar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pgbar.setMax(100);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        pgbar.setProgress(progress[0]);
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.syncout();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("error")){
            Toast.makeText(c,"Failure, Sync Detail", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Success, Sync Detail", Toast.LENGTH_SHORT).show();
        }
    }

    private String syncout(){
        try{
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            Cursor curSet = db.getAllSeting();
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(1);
                UserName = curSet.getString(2);
                Password = curSet.getString(3);
                DBName = curSet.getString(4);
                Port = curSet.getString(5);
            }
            //URL="jdbc:mysql://"+IPAddress+":"+Port+"/"+DBName;
            URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
            Connection conn= Connector.connect(URL, UserName, Password);
            //Connection conn= Connect_db.getConnection();
            if (conn == null) {
                z="error";
            }else{
                String sqlDetail="select RunNo, Doc1No, N_o, ItemCode, Description, " +
                        " Qty, FactorQty, UOM, UOMSingular, " +
                        " HCUnitCost, DisRate1, HCDiscount, TaxRate1, " +
                        " HCTax, DetailTaxCode, HCLineAmt, BranchCode," +
                        " DepartmentCode, ProjectCode, SalesPersonCode, LocationCode," +
                        " LineNo, BlankLine, DUD6 from stk_sales_order_dt where SynYN='0' " ;
                Cursor rsDt=db.getQuery(sqlDetail);
                int i=1;
                float vcounter=0;
                int numrows = rsDt.getCount();
                while(rsDt.moveToNext()){
                    String vDetail = "INSERT INTO stk_sales_order_dt (Doc1No, N_o, ItemCode, Description," +
                            " Qty, FactorQty, UOM, UOMSingular, " +
                            " HCUnitCost, DisRate1, HCDiscount, TaxRate1, " +
                            " HCTax, DetailTaxCode, HCLineAmt, BranchCode," +
                            " DepartmentCode, ProjectCode, SalesPersonCode, LocationCode," +
                            " LineNo, BlankLine, DUD6 ) values(" +
                            " '"+rsDt.getString(1)+"', '"+rsDt.getString(2)+"', '"+rsDt.getString(3)+"', '"+ DecodeChar.setChar(c,rsDt.getString(4))+"'," +
                            " '"+rsDt.getString(5)+"', '"+rsDt.getString(6)+"', '"+DecodeChar.setChar(c,rsDt.getString(7))+"', '"+DecodeChar.setChar(c,rsDt.getString(8))+"'," +
                            " '"+rsDt.getString(9)+"', '"+rsDt.getString(10)+"', '"+rsDt.getString(11)+"', '"+rsDt.getString(12)+"'," +
                            " '"+rsDt.getString(13)+"', '"+rsDt.getString(14)+"', '"+rsDt.getString(15)+"', '"+rsDt.getString(16)+"'," +
                            " '"+rsDt.getString(17)+"', '"+rsDt.getString(18)+"', '"+rsDt.getString(19)+"', '"+rsDt.getString(20)+"'," +
                            "  '"+i+"', '"+rsDt.getString(22)+"', '"+rsDt.getString(23)+"')";
                    Statement statement     = conn.createStatement();
                    statement.executeQuery("SET NAMES 'LATIN1'");
                    statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                    statement.execute(vDetail);
                    String sqlUpdate="Update stk_sales_order_dt set SynYN='1' where RunNo='"+rsDt.getString(0)+"' ";
                    long add=db.updateQuery(sqlUpdate);
                    if(add>0) {
                        vcounter +=100/numrows;
                        publishProgress(Math.round(vcounter));
                        //publishProgress(i);
                    }
                    i++;
                }
                //statement.close();
                db.closeDB();
                z="success";
            }
            return z;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }
}
