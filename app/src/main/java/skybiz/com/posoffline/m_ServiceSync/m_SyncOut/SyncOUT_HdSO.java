package skybiz.com.posoffline.m_ServiceSync.m_SyncOut;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 29/12/2017.
 */

public class SyncOUT_HdSO extends AsyncTask<Void, Void, String> {
    Context c;
    String IPAddress,UserName,Password,URL,DBName,z,Port;

    public SyncOUT_HdSO(Context c) {
        this.c = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(Void... params) {
        return this.syncout();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("error")){
            Toast.makeText(c,"Failure, Sync Header", Toast.LENGTH_SHORT).show();
        }else if(result.equals("success")){
            Toast.makeText(c,"Success, Sync Header", Toast.LENGTH_SHORT).show();
        }else if(result.equals("zero")){
            //data to sync out not found
        }
    }

    private String syncout(){
        try{
            z="zero";
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
            URL="jdbc:mysql://"+IPAddress+":"+Port+"/"+DBName;
            Connection conn= Connector.connect(URL, UserName, Password);
            //Connection conn= Connect_db.getConnection();
            if (conn == null) {
                z="error";
            }else{
                SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                date.setTime(date.getTime() - 30L * 24 * 60 * 60 * 1000);
                String datedShort = DateCurr.format(date);
                String vDelHd="delete from stk_sales_order_hd where D_ate='"+datedShort+"' ";
                db.addQuery(vDelHd);

                //Log.d("DELETE",vDelete);
                String sqlDetail="select * from stk_sales_order_hd where SynYN='0' " ;
                Cursor rsDt=db.getQuery(sqlDetail);
                int i=1;
                float vcounter=0;
                int numrows = rsDt.getCount();
               //  Doc1No, Doc2No, Doc3No,
                /*D_ate, CusCode, DueDate,
                        CurCode, CurRate1, CurRate2, CurRate3,
                        TermCode, D_ay, Attention,
                        GbDisRate1, GbDisRate2, GbDisRate3, HCGbDiscount,
                        GbTaxRate1, GbTaxRate2, GbTaxRate3, HCGbTax, GlobalTaxCode, HCDtTax,
                        HCNetAmt, GbOCCode, GbOCRate, GbOCAmt,
                        DepositNo, Deposit, DepositAccountCode,
                        DocType, ApprovedYN, UDRunNo, L_ink)*/
                while(rsDt.moveToNext()){
                    String vDetail = "INSERT INTO stk_sales_order_hd (Doc1No, Doc2No, Doc3No, D_ate," +
                            " CusCode, DueDate, " +
                            " CurCode, CurRate1, CurRate2, " +
                            " CurRate3, TermCode, D_ay, Attention, " +
                            " GbDisRate1, GbDisRate2, " +
                            " GbDisRate3, HCGbDiscount, GbTaxRate1, GbTaxRate2," +
                            " GbTaxRate3, HCGbTax, GlobalTaxCode, HCDtTax, " +
                            " HCNetAmt, GbOCCode, GbOCRate, GbOCAmt, " +
                            " DepositNo, Deposit, DepositAccountCode, " +
                            " DocType, ApprovedYN, UDRunNo, L_ink) values(" +
                            " '"+rsDt.getString(1)+"', '"+rsDt.getString(2)+"', '"+rsDt.getString(3)+"', '"+rsDt.getString(4)+"'," +
                            " '"+rsDt.getString(5)+"', '"+rsDt.getString(6)+"', '"+rsDt.getString(7)+"', '"+rsDt.getString(8)+"'," +
                            " '"+rsDt.getString(9)+"', '"+rsDt.getString(10)+"', '"+rsDt.getString(11)+"', '"+rsDt.getString(12)+"'," +
                            " '"+rsDt.getString(13)+"', '"+rsDt.getString(15)+"', '"+rsDt.getString(16)+"', '"+rsDt.getString(17)+"'," +
                            " '"+rsDt.getString(18)+"', '"+rsDt.getString(19)+"', '"+rsDt.getString(20)+"', '"+rsDt.getString(21)+"'," +
                            " '"+rsDt.getString(22)+"', '"+rsDt.getString(23)+"', '"+rsDt.getString(24)+"', '"+rsDt.getString(25)+"', " +
                            " '"+rsDt.getString(26)+"', '"+rsDt.getString(27)+"', '"+rsDt.getString(28)+"', '"+rsDt.getString(29)+"', " +
                            " '0', '"+rsDt.getString(31)+"', '"+rsDt.getString(32)+"', '"+rsDt.getString(33)+"', " +
                            " '"+rsDt.getString(34)+"', '"+rsDt.getString(35)+"')";
                   // Log.d("HEADER",vDetail);
                    Statement statement     = conn.createStatement();
                    statement.execute(vDetail);
                    String sqlUpdate="Update stk_sales_order_hd set SynYN='1' where RunNo='"+rsDt.getString(0)+"' ";
                    long add=db.updateQuery(sqlUpdate);
                    if(add>0) {

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
