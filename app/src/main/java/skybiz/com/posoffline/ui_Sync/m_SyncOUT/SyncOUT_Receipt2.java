package skybiz.com.posoffline.ui_Sync.m_SyncOUT;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;
import skybiz.com.posoffline.ui_Sync.Sync;

/**
 * Created by 7 on 29/12/2017.
 */

public class SyncOUT_Receipt2 extends AsyncTask<Void, Integer, String> {
    Context c;
    ProgressBar pgbar;
    String IPAddress,UserName,Password,URL,DBName,z,Port;
    int totalrows=0;
    String T_ypeSync="";

    public SyncOUT_Receipt2(Context c, ProgressBar pgbar) {
        this.c      = c;
        this.pgbar  = pgbar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
       // pgbar.setMax(100);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (this.pgbar != null) {
            pgbar.setMax(totalrows);
            pgbar.setProgress(values[0]);
           // ((Sync)c).SetProgressReceipt(T_ypeSync+" "+values[0] +" of "+ totalrows);
        }
        //pgbar.setProgress(progress[0]);
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.syncout();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("error")){
            Toast.makeText(c,"Failure, Sync Receipt2", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Success, Sync Receipt2", Toast.LENGTH_SHORT).show();
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
            URL="jdbc:mysql://"+IPAddress+":"+Port+"/"+DBName;
            Connection conn= Connector.connect(URL, UserName, Password);
            //Connection conn= Connect_db.getConnection();
            if (conn == null) {
                z="error";
            }else{
                T_ypeSync = "Sync Out Receipt : ";
                String qTotal = "select count(*)as totalrows from stk_receipt2 where SynYN='0'";
                Cursor rsTotal = db.getQuery(qTotal);
                while (rsTotal.moveToNext()) {
                    totalrows = rsTotal.getInt(0);
                }
                String sqlDetail="select * from stk_receipt2 where SynYN='0' " ;
                Cursor rsR=db.getQuery(sqlDetail);
                int i=1;
                float vcounter=0;
                int numrows = rsR.getCount();
                while(rsR.moveToNext()){
                    int numrow=0;
                    String qCheck="select Count(*) as numrows from stk_receipt2 where Doc1No='"+rsR.getString(4)+"' ";
                    Statement stmtCheck=conn.createStatement();
                    stmtCheck.execute(qCheck);
                    ResultSet rsCheck=stmtCheck.getResultSet();
                    while(rsCheck.next()){
                        numrow=rsCheck.getInt(1);
                    }
                    if(numrow==0) {
                        String vDetail = "INSERT INTO stk_receipt2 (D_ate, T_ime, D_ateTime, Doc1No," +
                                " CashAmt, CC1Code, CC1Amt, CC1No, " +
                                " CC1Expiry, CC1ChargesAmt, CC1ChargesRate, CC2Code, " +
                                " CC2Amt, CC2No, CC2Expiry, CC2ChargesAmt," +
                                " CC2ChargesRate, Cheque1Code, Cheque1Amt, Cheque1No," +
                                " Cheque2Code, Cheque2Amt, Cheque2No, PointAmt," +
                                " VoucherAmt, CurCode, CurRate, FCAmt," +
                                " CusCode, BalanceAmount, ChangeAmt, CounterCode," +
                                " UserCode, DocType, VoidYN, CurRate1) values(" +
                                " '" + rsR.getString(1) + "', '" + rsR.getString(2) + "', '" + rsR.getString(3) + "', '" + rsR.getString(4) + "'," +
                                " '" + rsR.getString(5) + "', '" + rsR.getString(6) + "', '" + rsR.getString(7) + "', '" + rsR.getString(8) + "'," +
                                " '" + rsR.getString(9) + "', '" + rsR.getString(10) + "', '" + rsR.getString(11) + "', '" + rsR.getString(12) + "'," +
                                " '" + rsR.getString(13) + "', '" + rsR.getString(14) + "', '" + rsR.getString(15) + "', '" + rsR.getString(16) + "'," +
                                " '" + rsR.getString(17) + "', '" + rsR.getString(18) + "', '" + rsR.getString(19) + "', '" + rsR.getString(20) + "'," +
                                " '" + rsR.getString(21) + "', '" + rsR.getString(22) + "', '" + rsR.getString(23) + "', '" + rsR.getString(24) + "', " +
                                " '" + rsR.getString(25) + "', '" + rsR.getString(26) + "', '" + rsR.getString(27) + "', '" + rsR.getString(28) + "', " +
                                " '" + rsR.getString(29) + "', '" + rsR.getString(30) + "', '" + rsR.getString(31) + "', '" + rsR.getString(32) + "', " +
                                " '" + rsR.getString(33) + "', '" + rsR.getString(34) + "', '" + rsR.getString(36) + "', '"+rsR.getString(27) +"' )";
                        Statement statement = conn.createStatement();
                        statement.execute(vDetail);
                        String sqlUpdate = "Update stk_receipt2 set SynYN='1' where RunNo='" + rsR.getString(0) + "' ";
                        long add = db.updateQuery(sqlUpdate);
                        if (add > 0) {
                            //vcounter += 100 / numrows;
                            //publishProgress(Math.round(vcounter));

                        }

                    }else{
                        String sqlUpdate = "Update stk_receipt2 set SynYN='1' where RunNo='" + rsR.getString(0) + "' ";
                        db.updateQuery(sqlUpdate);
                    }
                    publishProgress(i);
                    i++;
                }
               // statement.close();
                db.closeDB();
                z="success";
            }
            return z;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }
    private String checkDate(String vText){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String datedShort = sdf.format(date);
        if(vText.equals("null") || vText.isEmpty()){
            vText=datedShort;
        }
        return vText;
    }

}
