package skybiz.com.posoffline.ui_Sync.m_SyncOUT;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_NewObject.Decode;
import skybiz.com.posoffline.m_NewObject.DecodeChar;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;
import skybiz.com.posoffline.ui_Sync.Sync;

/**
 * Created by 7 on 29/12/2017.
 */

public class SyncOUT_Detail extends AsyncTask<Void, Integer, String> {
    Context c;
    ProgressBar pgbar;
    String IPAddress,UserName,Password,
            URL,DBName,z,
            Port,DBStatus,ItemConn,EncodeType;
    int totalrows=0;
    String T_ypeSync="";

    public SyncOUT_Detail(Context c, ProgressBar pgbar) {
        this.c = c;
        this.pgbar = pgbar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //pgbar.setMax(100);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        //pgbar.setProgress(progress[0]);
        if (this.pgbar != null) {
            pgbar.setMax(totalrows);
            pgbar.setProgress(values[0]);
           // ((Sync)c).SetProgressDt(T_ypeSync+" "+values[0] +" of "+ totalrows);
        }
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
        }else if(result.equals("success")){
            Toast.makeText(c,"Success, Sync Detail", Toast.LENGTH_SHORT).show();
        }
    }

    private String syncout(){
        try {
            z = "error";
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String querySet = "select ServerName, UserName, Password," +
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
                DBStatus = curSet.getString(5);
                ItemConn = curSet.getString(6);
                EncodeType = curSet.getString(7);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String datedShort = sdf.format(date);
            URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
            Connection conn = Connector.connect(URL, UserName, Password);
            //Connection conn= Connect_db.getConnection();
            if (conn == null) {
                z = "error";
            } else {
                T_ypeSync = "Sync Out Detail : ";
                String qTotal = "select count(*)as totalrows from stk_cus_inv_dt where SynYN='0'";
                Cursor rsTotal = db.getQuery(qTotal);
                while (rsTotal.moveToNext()) {
                    totalrows = rsTotal.getInt(0);
                }
                Statement stmtInDt = conn.createStatement();
                stmtInDt.executeQuery("SET NAMES 'LATIN1'");
                stmtInDt.executeQuery("SET CHARACTER SET 'LATIN1'");
                String sqlDetail = "select '' as RunNo, Doc1No, N_o, ItemCode, Description," +
                        "  Qty, FactorQty, UOM, UOMSingular, " +
                        "  HCUnitCost, DisRate1, HCDiscount, TaxRate1, " +
                        "  HCTax, DetailTaxCode, HCLineAmt, BranchCode, " +
                        "  DepartmentCode, ProjectCode, SalesPersonCode, LocationCode, " +
                        "  IFNULL(WarrantyDate, '" + datedShort + "') as WDate, IFNULL(LineNo,'')as LineNo, BlankLine, DocType, " +
                        "  AnalysisCode2, DUD6 " +
                        "  from stk_cus_inv_dt where SynYN='0' ";
                Cursor rsDt = db.getQuery(sqlDetail);
                int i = 1;
                float vcounter = 0;
                int numrows = rsDt.getCount();
                while (rsDt.moveToNext()) {
                    String Doc1No = rsDt.getString(1);
                    String ItemCode = rsDt.getString(3);
                    String LineNo = rsDt.getString(22);
                    if (LineNo.isEmpty()) {
                        LineNo = "" + i;
                    }
                    int numrow = 0;
                    String qCheck = "select count(*) as numrows " +
                            " from stk_cus_inv_dt where Doc1No='" + Doc1No + "' " +
                            " and ItemCode='" + ItemCode + "' and LineNo='" + LineNo + "'   ";
                    Log.d("qCheck", qCheck);
                    Statement stmtCheck = conn.createStatement();
                    stmtCheck.execute(qCheck);
                    ResultSet rsCheck = stmtCheck.getResultSet();
                    while (rsCheck.next()) {
                        numrow = rsCheck.getInt(1);
                    }
                    /*int numrow2=0;
                    String qCheck2="select Count(*) as numrows from stk_cus_inv_dt where Doc1No='"+rsDt.getString(1)+"' ";
                    Cursor rsCheck2=db.getQuery(qCheck2);
                    while(rsCheck2.moveToNext()){
                        numrow2=rsCheck2.getInt(0);
                    }*/
                    // Log.d("DETAIL", String.valueOf(numrow)+"/"+String.valueOf(numrow2));
                    if (numrow == 0) {
                        String vDetail = "INSERT INTO stk_cus_inv_dt (Doc1No, N_o, ItemCode, Description," +
                                " Qty, FactorQty, UOM, UOMSingular, " +
                                " HCUnitCost, DisRate1, HCDiscount, TaxRate1, " +
                                " HCTax, DetailTaxCode, HCLineAmt, BranchCode," +
                                " DepartmentCode, ProjectCode, SalesPersonCode, LocationCode," +
                                " WarrantyDate, LineNo, BlankLine, DocType," +
                                " AnalysisCode2, DUD6) values(" +
                                " '" + rsDt.getString(1) + "', '" + rsDt.getString(2) + "', '" + rsDt.getString(3) + "', '" + Decode.setChar(EncodeType, rsDt.getString(4)) + "'," +
                                " '" + rsDt.getString(5) + "', '" + rsDt.getString(6) + "', '" + Decode.setChar(EncodeType, rsDt.getString(7)) + "', '" + Decode.setChar(EncodeType, rsDt.getString(8)) + "'," +
                                " '" + rsDt.getString(9) + "', '" + rsDt.getString(10) + "', '" + rsDt.getString(11) + "', '" + rsDt.getString(12) + "'," +
                                " '" + rsDt.getString(13) + "', '" + rsDt.getString(14) + "', '" + rsDt.getString(15) + "', '" + rsDt.getString(16) + "'," +
                                " '" + rsDt.getString(17) + "', '" + rsDt.getString(18) + "', '" + rsDt.getString(19) + "', '" + rsDt.getString(20) + "'," +
                                " '" + rsDt.getString(21) + "', '" + rsDt.getString(22) + "', '" + rsDt.getString(23) + "', '" + rsDt.getString(24) + "', " +
                                " '" + rsDt.getString(25) + "', '" + rsDt.getString(26) + "' )";

                        /*statement.executeQuery("SET NAMES 'LATIN1'");
                        statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                        statement.execute(vDetail);*/
                        stmtInDt.addBatch(vDetail);
                        String sqlUpdate = "Update stk_cus_inv_dt set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                        long add = db.updateQuery(sqlUpdate);
                        if (add > 0) {
                            // vcounter += 100 / numrows;
                            // publishProgress(Math.round(vcounter));
                        }
                    } else {
                        String sqlUpdate = "Update stk_cus_inv_dt set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                        db.updateQuery(sqlUpdate);
                    }
                    publishProgress(i);
                    i++;
                }
                stmtInDt.executeBatch();
                stmtInDt.close();
                //conn.close();
                //statement.close();
                db.closeDB();
                z = "success";
            }
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }
    private String checkDate(String vText){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String datedShort = sdf.format(date);
        if(vText.equals("")){
            vText=datedShort;
        }else{
            vText=datedShort;
        }
        return vText;
    }


}
