package skybiz.com.posoffline.ui_Sync.m_SyncOUT;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.m_NewObject.Decode;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;
import skybiz.com.posoffline.ui_Sync.Sync;

/**
 * Created by 7 on 29/12/2017.
 */

public class SyncOut extends AsyncTask<Void, Integer, String> {
    Context c;
    ProgressBar pgbar;
    String IPAddress,UserName,Password,
            URL,DBName,z,
            Port,DBStatus,ItemConn,EncodeType;
    int totalrows=0;
    String T_ypeSync="",D_ate;
    DBAdapter db=null;
    Connection conn=null;

    public SyncOut(Context c, ProgressBar pgbar) {
        this.c = c;
        this.pgbar = pgbar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (this.pgbar != null) {
            pgbar.setMax(totalrows);
            pgbar.setProgress(values[0]);
            ((Sync)c).SetProgressCS(T_ypeSync+" "+values[0] +" of "+ totalrows);
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
            Toast.makeText(c,"Failure, Sync CS", Toast.LENGTH_SHORT).show();
        }else if(result.equals("success")){
            Toast.makeText(c,"Success, Sync CS", Toast.LENGTH_SHORT).show();
        }
    }

    private String syncout(){
        try {
            z   = "error";
            db  = new DBAdapter(c);
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
            D_ate = sdf.format(date);
            URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
            conn = Connector.connect(URL, UserName, Password);
            if (conn == null) {
                z = "error";
            } else {
                syncDt();
                synDt2();
                syncHd();
                syncReceipt2();
                z = "success";
            }
            db.closeDB();
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return z;
    }
    private void syncReceipt2(){
        try{
            T_ypeSync = "Sync Out Receipt : ";
            String qTotal = "select count(*)as totalrows from stk_receipt2 where SynYN='0'";
            Cursor rsTotal = db.getQuery(qTotal);
            while (rsTotal.moveToNext()) {
                totalrows = rsTotal.getInt(0);
            }
            rsTotal.close();
            Statement stmtRec= conn.createStatement();
            String sqlDetail="select * from stk_receipt2 where SynYN='0' " ;
            Cursor rsR=db.getQuery(sqlDetail);
            int i=1;
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
                    stmtRec.addBatch(vDetail);
                   // Statement statement = conn.createStatement();
                   // statement.execute(vDetail);
                    String sqlUpdate = "Update stk_receipt2 set SynYN='1' where RunNo='" + rsR.getString(0) + "' ";
                    db.updateQuery(sqlUpdate);
                }else{
                    String sqlUpdate = "Update stk_receipt2 set SynYN='1' where RunNo='" + rsR.getString(0) + "' ";
                    db.updateQuery(sqlUpdate);
                }
                publishProgress(i);
                i++;
            }
            stmtRec.executeBatch();
            stmtRec.close();
            rsR.close();
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    private void syncHd(){
        try{
            String sqlDetail="select * from stk_cus_inv_hd where SynYN='0' " ;
            Cursor rsDt=db.getQuery(sqlDetail);
            T_ypeSync = "Sync Out Header : ";
            String qTotal = "select count(*)as totalrows from stk_cus_inv_hd where SynYN='0' ";
            Cursor rsTotal = db.getQuery(qTotal);
            while (rsTotal.moveToNext()) {
                totalrows = rsTotal.getInt(0);
            }
            rsTotal.close();
            Statement stmtHd = conn.createStatement();
            int i=1;
            while(rsDt.moveToNext()){
                int numrow=0;
                String qCheck="select count(*) as numrows " +
                        "from stk_cus_inv_hd where Doc1No='"+rsDt.getString(1)+"' ";
                Statement stmtCheck=conn.createStatement();
                stmtCheck.execute(qCheck);
                ResultSet rsCheck=stmtCheck.getResultSet();
                while(rsCheck.next()){
                    numrow=rsCheck.getInt(1);
                }
                if(numrow==0) {
                    String vDetail = "INSERT INTO stk_cus_inv_hd (Doc1No, Doc2No, Doc3No, D_ate," +
                            " D_ateTime, CusCode, MemberNo, DueDate, " +
                            " TaxDate, CurCode, CurRate1,CurRate2, " +
                            " CurRate3, TermCode, D_ay, Attention," +
                            " AddCode, BatchCode, GbDisRate1, GbDisRate2," +
                            " GbDisRate3, HCGbDiscount, GbTaxRate1, GbTaxRate2," +
                            " GbTaxRate3, HCGbTax, GlobalTaxCode, HCDtTax, " +
                            " HCNetAmt, AdjAmt, GbOCCode, GbOCRate," +
                            " GbOCAmt, DocType, ApprovedYN, RetailYN," +
                            " UDRunNo, L_ink, Status, Status2) values(" +
                            " '" + rsDt.getString(1) + "', '" + rsDt.getString(2) + "', '" + rsDt.getString(3) + "', '" + rsDt.getString(4) + "'," +
                            " '" + rsDt.getString(5) + "', '" + rsDt.getString(6) + "', '" + rsDt.getString(7) + "', '" + rsDt.getString(8) + "'," +
                            " '" + rsDt.getString(9) + "', '" + rsDt.getString(10) + "', '" + rsDt.getString(11) + "', '" + rsDt.getString(12) + "'," +
                            " '" + rsDt.getString(13) + "', '" + rsDt.getString(14) + "', '" + rsDt.getString(15) + "', '" + rsDt.getString(16) + "'," +
                            " '" + rsDt.getString(17) + "', '" + rsDt.getString(18) + "', '" + rsDt.getString(19) + "', '" + rsDt.getString(20) + "'," +
                            " '" + rsDt.getString(21) + "', '" + rsDt.getString(22) + "', '" + rsDt.getString(23) + "', '" + rsDt.getString(24) + "', " +
                            " '" + rsDt.getString(25) + "', '" + rsDt.getString(26) + "', '" + rsDt.getString(27) + "', '" + rsDt.getString(28) + "', " +
                            " '" + rsDt.getString(29) + "', '" + rsDt.getString(30) + "', '" + rsDt.getString(31) + "', '" + rsDt.getString(32) + "', " +
                            " '" + rsDt.getString(33) + "', '" + rsDt.getString(34) + "', '" + rsDt.getString(35) + "', '" + rsDt.getString(36) + "' ," +
                            " '" + rsDt.getString(37) + "', '" + rsDt.getString(38) + "', '" + rsDt.getString(39) + "', '" + rsDt.getString(40) + "' )";
                    Log.d("HEADER", vDetail);
                    stmtHd.addBatch(vDetail);
                    //Statement statement = conn.createStatement();
                    //statement.execute(vDetail);
                    String sqlUpdate = "Update stk_cus_inv_hd set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                     db.updateQuery(sqlUpdate);
                }else{
                    String sqlUpdate = "Update stk_cus_inv_hd set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                    db.updateQuery(sqlUpdate);
                }
                publishProgress(i);
                i++;
            }
            stmtHd.executeBatch();
            stmtHd.close();
            rsDt.close();
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void syncDt(){
        try{
            T_ypeSync = "Sync Out Detail : ";
            String qTotal = "select count(*)as totalrows from stk_cus_inv_dt where SynYN='0'";
            Cursor rsTotal = db.getQuery(qTotal);
            while (rsTotal.moveToNext()) {
                totalrows = rsTotal.getInt(0);
            }
            rsTotal.close();
            Statement stmtInDt = conn.createStatement();
            stmtInDt.executeQuery("SET NAMES 'LATIN1'");
            stmtInDt.executeQuery("SET CHARACTER SET 'LATIN1'");
            String sqlDetail = "select '' as RunNo, Doc1No, N_o, ItemCode, Description," +
                    "  Qty, FactorQty, UOM, UOMSingular, " +
                    "  HCUnitCost, DisRate1, HCDiscount, TaxRate1, " +
                    "  HCTax, DetailTaxCode, HCLineAmt, BranchCode, " +
                    "  DepartmentCode, ProjectCode, SalesPersonCode, LocationCode, " +
                    "  IFNULL(WarrantyDate, '" + D_ate + "') as WDate, IFNULL(LineNo,'')as LineNo, BlankLine, DocType, " +
                    "  AnalysisCode2, DUD6 " +
                    "  from stk_cus_inv_dt where SynYN='0' ";
            Cursor rsDt = db.getQuery(sqlDetail);
            int i = 1;
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

                    stmtInDt.addBatch(vDetail);
                    String sqlUpdate = "Update stk_cus_inv_dt set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                    db.updateQuery(sqlUpdate);
                } else {
                    String sqlUpdate = "Update stk_cus_inv_dt set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                    db.updateQuery(sqlUpdate);
                }
                publishProgress(i);
                i++;
            }
            rsDt.close();
            stmtInDt.executeBatch();
            stmtInDt.close();
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void synDt2(){
        try{
            T_ypeSync       = "Sync Out Detail Out : ";
            String qTotal   = "select count(*)as totalrows from stk_detail_trn_out where SynYN='0'";
            Cursor rsTotal = db.getQuery(qTotal);
            while (rsTotal.moveToNext()) {
                totalrows = rsTotal.getInt(0);
            }
            rsTotal.close();
            Statement stmtDt2    = conn.createStatement();
            String sqlDetail="select * from stk_detail_trn_out where SynYN='0' " ;
            Cursor rsDt=db.getQuery(sqlDetail);
            int i=1;
            while(rsDt.moveToNext()){
                int numrow=0;
                String ItemCode     =rsDt.getString(1);
                String Doc1No       =rsDt.getString(2);
                String Doc3NoRunNo  =rsDt.getString(10);
                String qCheck = "select count(*) as numrows " +
                        " from stk_detail_trn_out where Doc3No='" + Doc1No + "' " +
                        " and ItemCode='" + ItemCode + "' and Doc3NoRunNo='" + Doc3NoRunNo + "'   ";
                Log.d("qCheck", qCheck);
                Statement stmtCheckDt2 = conn.createStatement();
                stmtCheckDt2.execute(qCheck);
                ResultSet rsCheckDt2 = stmtCheckDt2.getResultSet();
                while (rsCheckDt2.next()) {
                    numrow = rsCheckDt2.getInt(1);
                }
                if(numrow==0) {
                    String vdetailOut = " insert into stk_detail_trn_out(ItemCode,Doc3No,D_ate,QtyOUT," +
                            " FactorQty,UOM,UnitPrice,CusCode," +
                            " DocType3,Doc3NoRunNo,LocationCode,L_ink," +
                            " HCTax,BookDate) values(" +
                            " '" + rsDt.getString(1) + "', '" + rsDt.getString(2) + "', '" + rsDt.getString(3) + "', '" + rsDt.getString(4) + "'," +
                            " '" + rsDt.getString(5) + "', '" + rsDt.getString(6) + "', '" + rsDt.getString(7) + "', '" + rsDt.getString(8) + "'," +
                            " '" + rsDt.getString(9) + "', '" + rsDt.getString(10) + "', '" + rsDt.getString(11) + "', '" + rsDt.getString(12) + "'," +
                            " '" + rsDt.getString(13) + "', '" + rsDt.getString(14) + "' )";
                    stmtDt2.addBatch(vdetailOut);
                    String sqlUpdate = "Update stk_detail_trn_out set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                    db.updateQuery(sqlUpdate);
                    publishProgress(i);
                }else{
                    String sqlUpdate = "Update stk_detail_trn_out set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                    db.updateQuery(sqlUpdate);
                }
                i++;
            }
            stmtDt2.executeBatch();
            stmtDt2.close();
            rsDt.close();

            Statement stmtPointHd = conn.createStatement();
            String qPointHd="select Doc1No,D_ate,T_ime, " +
                    " Remark,ClientCode,L_ink," +
                    " RunNo from ret_pointredeem_hd where SynYN='0' ";
            Cursor rsPointHd=db.getQuery(qPointHd);
            while(rsPointHd.moveToNext()){
                String inPointHd="Insert into ret_pointredeem_hd (Doc1No,D_ate,T_ime, " +
                        " Remark,ClientCode,L_ink)values('"+rsPointHd.getString(0)+"', '"+rsPointHd.getString(1)+"', '"+rsPointHd.getString(2)+"'," +
                        "'"+rsPointHd.getString(3)+"', '"+rsPointHd.getString(4)+"', '"+rsPointHd.getString(5)+"')";
                stmtPointHd.addBatch(inPointHd);
                //Statement stmtPointHd = conn.createStatement();
                // stmtPointHd.execute(inPointHd);
                String update="update ret_pointredeem_hd set SynYN='1' where RunNo='"+rsPointHd.getString(6)+"' ";
                db.addQuery(update);

            }
            stmtPointHd.executeBatch();
            stmtPointHd.close();

            Statement stmtPointDt = conn.createStatement();
            String qPointDt="select Doc1No,ItemCode,Point, " +
                    " UnitCost,Qty,FactorQty, " +
                    " UOM,Description,RunNo from ret_pointredeem_dt where SynYN='0' ";
            Cursor rsPointDt=db.getQuery(qPointDt);
            while(rsPointDt.moveToNext()){
                String inPointDt="Insert into ret_pointredeem_dt (Doc1No,ItemCode,Point," +
                        " UnitCost,Qty,FactorQty, " +
                        " UOM,Description)values('"+rsPointDt.getString(0)+"', '"+rsPointDt.getString(1)+"', '"+rsPointDt.getString(2)+"'," +
                        "'"+rsPointDt.getString(3)+"', '"+rsPointDt.getString(4)+"', '"+rsPointDt.getString(5)+"'," +
                        "'"+rsPointDt.getString(6)+"', '"+rsPointDt.getString(7)+"')";
                stmtPointDt.addBatch(inPointDt);
                //Statement stmtPointDt = conn.createStatement();
                //stmtPointDt.execute(inPointDt);
                String update="update ret_pointredeem_dt set SynYN='1' where RunNo='"+rsPointDt.getString(8)+"' ";
                db.addQuery(update);
            }
            stmtPointDt.executeBatch();
            stmtPointDt.close();

            Statement stmtPointAdj = conn.createStatement();
            String qPointAdj="select cuscode,D_ate,Point," +
                    " DocType,Remark,D_ateTime," +
                    " RunNo from ret_pointadjustment where SynYN='0' ";
            Cursor rsPointAdj=db.getQuery(qPointAdj);
            while(rsPointAdj.moveToNext()){
                String inPointAdj="Insert into ret_pointadjustment (cuscode,D_ate,Point," +
                        "DocType,Remark,D_ateTime)values('"+rsPointAdj.getString(0)+"', '"+rsPointAdj.getString(1)+"', '"+rsPointAdj.getString(2)+"'," +
                        "'"+rsPointAdj.getString(3)+"', '"+rsPointAdj.getString(4)+"', '"+rsPointAdj.getString(5)+"')";
                //Statement stmtPointAdj = conn.createStatement();
                //stmtPointAdj.execute(inPointAdj);
                stmtPointAdj.addBatch(inPointAdj);
                String update="update ret_pointadjustment set SynYN='1' where RunNo='"+rsPointAdj.getString(6)+"' ";
                db.addQuery(update);

            }
            stmtPointAdj.executeBatch();
            stmtPointAdj.close();
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
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
