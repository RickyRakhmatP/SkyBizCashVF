package skybiz.com.posoffline.m_ServiceSync.m_SyncOut;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 29/12/2017.
 */

public class SyncOUT_Detail2 extends AsyncTask<Void, Void, String> {
    Context c;
    ProgressBar pgbar;
    String IPAddress,UserName,Password,URL,DBName,z,Port;

    public SyncOUT_Detail2(Context c) {
        this.c = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.syncdetail2();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("error")){
            Toast.makeText(c,"Failure, Sync Detail Trn Out", Toast.LENGTH_SHORT).show();
        }else if(result.equals("success")){
            Toast.makeText(c,"Success, Sync Detail Trn Out", Toast.LENGTH_SHORT).show();
        }else if(result.equals("zero")){
            //data to sync out not found
            //Toast.makeText(c,"data Trn Out", Toast.LENGTH_SHORT).show();
        }
    }

    private String syncdetail2(){
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
                Statement stmtDt2=conn.createStatement();
                String sqlDetail="select * from stk_detail_trn_out where SynYN='0' " ;
                Cursor rsDt=db.getQuery(sqlDetail);
                int i=1;
                float vcounter=0;
                int numrows = rsDt.getCount();
                while(rsDt.moveToNext()){
                   /* int numrow=0;
                    String qCheck="select Count(*) as numrows from stk_detail_trn_out where Doc3No='"+rsDt.getString(2)+"' ";
                    Statement stmtCheck=conn.createStatement();
                    stmtCheck.execute(qCheck);
                    ResultSet rsCheck=stmtCheck.getResultSet();
                    while(rsCheck.next()){
                        numrow=rsCheck.getInt(1);
                    }
                    int numrow2=0;
                    String qCheck2="select Count(*) as numrows from stk_detail_trn_out where Doc3No='"+rsDt.getString(2)+"' ";
                    Cursor rsCheck2=db.getQuery(qCheck2);
                    while(rsCheck2.moveToNext()){
                        numrow2=rsCheck2.getInt(0);
                    }
                    if(numrow!=numrow2) {*/
                        String vdetailOut = " insert into stk_detail_trn_out(ItemCode,Doc3No,D_ate,QtyOUT," +
                                " FactorQty,UOM,UnitPrice,CusCode," +
                                " DocType3,Doc3NoRunNo,LocationCode,L_ink," +
                                " HCTax,BookDate) values(" +
                                " '" + rsDt.getString(1) + "', '" + rsDt.getString(2) + "', '" + rsDt.getString(3) + "', '" + rsDt.getString(4) + "'," +
                                " '" + rsDt.getString(5) + "', '" + rsDt.getString(6) + "', '" + rsDt.getString(7) + "', '" + rsDt.getString(8) + "'," +
                                " '" + rsDt.getString(9) + "', '" + rsDt.getString(10) + "', '" + rsDt.getString(11) + "', '" + rsDt.getString(12) + "'," +
                                " '" + rsDt.getString(13) + "', '" + rsDt.getString(14) + "' )";
                        //Statement statement = conn.createStatement();
                        //statement.execute(vdetailOut);
                        stmtDt2.addBatch(vdetailOut);
                        String sqlUpdate = "Update stk_detail_trn_out set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                        long add = db.updateQuery(sqlUpdate);
                        if (add > 0) {

                        }
                   /* }else{
                        String sqlUpdate = "Update stk_detail_trn_out set SynYN='1' where RunNo='" + rsDt.getString(0) + "' ";
                        db.updateQuery(sqlUpdate);
                    }*/
                    i++;
                }
                stmtDt2.executeBatch();
                stmtDt2.close();

                Statement stmtPointHd=conn.createStatement();
                String qPointHd="select Doc1No,D_ate,T_ime, " +
                        " Remark,ClientCode,L_ink," +
                        " RunNo from ret_pointredeem_hd where SynYN='0' ";
                Cursor rsPointHd=db.getQuery(qPointHd);
                while(rsPointHd.moveToNext()){
                    String inPointHd="Insert into ret_pointredeem_hd (Doc1No,D_ate,T_ime, " +
                            " Remark,ClientCode,L_ink)values('"+rsPointHd.getString(0)+"', '"+rsPointHd.getString(1)+"', '"+rsPointHd.getString(2)+"'," +
                            "'"+rsPointHd.getString(3)+"', '"+rsPointHd.getString(4)+"', '"+rsPointHd.getString(5)+"')";
                    //Statement stmtPointHd = conn.createStatement();
                    //stmtPointHd.execute(inPointHd);
                    stmtPointHd.addBatch(inPointHd);
                    String update="update ret_pointredeem_hd set SynYN='1' where RunNo='"+rsPointHd.getString(6)+"' ";
                    db.addQuery(update);
                }
                stmtPointHd.executeBatch();
                stmtPointHd.close();

                Statement stmtPointDt=conn.createStatement();
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
                    stmtPointHd.addBatch(inPointDt);
                    //Statement stmtPointDt = conn.createStatement();
                    //stmtPointDt.execute(inPointDt);
                    String update="update ret_pointredeem_dt set SynYN='1' where RunNo='"+rsPointDt.getString(8)+"' ";
                    db.addQuery(update);
                }
                stmtPointDt.executeBatch();
                stmtPointDt.close();

                Statement stmtPointAdj=conn.createStatement();
                String qPointAdj="select cuscode,D_ate,Point," +
                        " DocType,Remark,D_ateTime," +
                        " RunNo from ret_pointadjustment where SynYN='0' ";
                Cursor rsPointAdj=db.getQuery(qPointAdj);
                while(rsPointAdj.moveToNext()){
                    String inPointAdj="Insert into ret_pointadjustment (cuscode,D_ate,Point," +
                            "DocType,Remark,D_ateTime)values('"+rsPointAdj.getString(0)+"', '"+rsPointAdj.getString(1)+"', '"+rsPointAdj.getString(2)+"'," +
                            "'"+rsPointAdj.getString(3)+"', '"+rsPointAdj.getString(4)+"', '"+rsPointAdj.getString(5)+"')";
                    stmtPointHd.addBatch(inPointAdj);
                    //Statement stmtPointAdj = conn.createStatement();
                    //stmtPointAdj.execute(inPointAdj);
                    String update="update ret_pointadjustment set SynYN='1' where RunNo='"+rsPointAdj.getString(6)+"' ";
                    db.addQuery(update);
                }
                stmtPointAdj.executeBatch();
                stmtPointAdj.close();


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
