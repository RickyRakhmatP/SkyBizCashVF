package skybiz.com.posoffline.ui_Setting.m_Sync;

import android.content.ContentValues;
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
import skybiz.com.posoffline.ui_Sync.Sync;

/**
 * Created by 7 on 21/12/2017.
 */

public class Sync_Customer extends AsyncTask<Void,Integer,String> {
    Context c;
    ProgressBar bar;
    String IPAddress,UserName,Password,URL,DBName,z,Port,DBStatus,ItemConn;
    int totalrows=0;
    String T_ypeSync="";


    public Sync_Customer(Context c, ProgressBar bar) {
        this.c = c;
        this.bar = bar;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //bar.setMax(100);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (this.bar != null) {
            bar.setMax(totalrows);
            bar.setProgress(values[0]);
            ((Sync)c).SetProgressCustomer(T_ypeSync+" "+values[0] +" of "+ totalrows);
        }
        //bar.setProgress(progress[0]);
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.retCustomer();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("error")){
            Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Successfull, data customer retrieve", Toast.LENGTH_SHORT).show();
        }
    }
    private String retCustomer(){
        try{
            DBAdapter db = new DBAdapter(c);
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
                ItemConn=curSet.getString(8);
            }
            if(DBStatus.equals("2")) {
                z = "success";
            }else {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=UTF-8";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn == null) {
                    z = "error";
                } else {

                    T_ypeSync = "Sync In Customer : ";
                    String qTotal = "select count(*)as totalrows from customer  where StatusBadYN='0' ";
                    Statement stmtTotal = conn.createStatement();
                    stmtTotal.execute(qTotal);
                    ResultSet rsTotal = stmtTotal.getResultSet();
                    while (rsTotal.next()) {
                        totalrows = rsTotal.getInt(1);
                    }
                    String del = "delete from customer";
                    db.addQuery(del);
                    String sql = "select CusCode,CusName,FinCatCode," +
                            " AccountCode,Address,CurCode, " +
                            " TermCode,D_ay,SalesPersonCode," +
                            " Tel,Tel2,Fax," +
                            " Fax2,Contact,ContactTel," +
                            " Email,StatusBadYN, Town," +
                            " State,Country,PostCode," +
                            " L_ink,NRICNo,DATE_FORMAT(DOB,'%Y-%m-%d') as DOB, " +
                            " Sex, MemberType, CardNo," +
                            " PaymentCode, DATE_FORMAT(DateTimeModified,'%Y-%m-%d %H:%i:%s') as DateTimeModified, MembershipClass  " +
                            " from customer where StatusBadYN='0' ";
                    Statement statement = conn.createStatement();
                    statement.execute(sql);
                    ResultSet rsCus = statement.getResultSet();
                    int i = 1;
                    float vcounter = 0;
                    while (rsCus.next()) {
                        String QueryIn = "Insert into customer(CusCode, CusName, FinCatCode, " +
                                " AccountCode, Address,CurCode, " +
                                " TermCode, D_ay, SalesPersonCode, " +
                                " Tel, Tel2, Fax, " +
                                " Fax2, Contact, ContactTel, " +
                                " Email, StatusBadYN, Town," +
                                " State, Country, PostCode," +
                                " L_ink, NRICNo, DOB, " +
                                " Sex, MemberType, CardNo," +
                                " PaymentCode, DateTimeModified, MembershipClass)values(" +
                                " '" + rsCus.getString("CusCode") + "', '" + charReplace(rsCus.getString("CusName")) + "', '" + rsCus.getString("FinCatCode") + "', " +
                                " '" + rsCus.getString("AccountCode") + "', '" + charReplace(rsCus.getString("Address")) + "', '" + rsCus.getString("CurCode") + "', " +
                                " '" + rsCus.getString("TermCode") + "', '" + rsCus.getString("D_ay") + "', '" + rsCus.getString("SalesPersonCode") + "'," +
                                " '" + rsCus.getString("Tel") + "', '" + rsCus.getString("Tel2") + "',  '" + rsCus.getString("Fax") + "', " +
                                " '" + rsCus.getString("Fax2") + "', '" + charReplace(rsCus.getString("Contact")) + "', '" + rsCus.getString("ContactTel") + "', " +
                                " '" + rsCus.getString("Email") + "', '" + rsCus.getString("StatusBadYN") + "', '" + rsCus.getString("Town") + "',  " +
                                " '" + rsCus.getString("State") + "', '" + rsCus.getString("Country") + "', '" + rsCus.getString("PostCode") + "',  " +
                                " '" + rsCus.getString("L_ink") + "', '" + rsCus.getString("NRICNo") + "', '" + rsCus.getString("DOB") + "',  " +
                                " '" + rsCus.getString("Sex") + "', '" + rsCus.getString("MemberType") + "', '" + rsCus.getString("CardNo") + "',  " +
                                " '" + rsCus.getString("PaymentCode") + "', '" + rsCus.getString("DateTimeModified") + "', '"+rsCus.getString("MembershipClass")+"')";
                        long addItem = db.addQuery(QueryIn);
                        if (addItem > 0) {
                            publishProgress(i);
                        } else {
                            Log.d("ERROR", rsCus.getString("ItemCode"));
                        }
                        i++;
                    }
                    statement.close();
                    String vDelMember="delete from ret_membership_class";
                    db.addQuery(vDelMember);
                    String qMembership="select Class, " +
                            " Description, CollectionMethod, DateFrom, " +
                            " DateTo, RatioPoint, RatioAmount from ret_membership_class";
                    Statement stmtM = conn.createStatement();
                    stmtM.execute(qMembership);
                    ResultSet rsMember = stmtM.getResultSet();
                    while(rsMember.next()){
                        String insertMember="insert into ret_membership_class(Class, " +
                                " Description, CollectionMethod, DateFrom, " +
                                " DateTo, RatioPoint, RatioAmount)values('"+rsMember.getString(1)+"'," +
                                " '"+rsMember.getString(2)+"', '"+rsMember.getString(3)+"', '"+rsMember.getString(4)+"'," +
                                " '"+rsMember.getString(5)+"', '"+rsMember.getString(6)+"', '"+rsMember.getString(7)+"')";
                        db.addQuery(insertMember);
                    }

                    String vDelCateg="delete from stk_category";
                    db.addQuery(vDelCateg);
                    String qCateg="select CategoryCode, Description, AccountCode, " +
                            "L_ink, CategoryFor, SMobileYN from stk_category";
                    Statement stmtCat = conn.createStatement();
                    stmtCat.execute(qCateg);
                    ResultSet rsCat = stmtCat.getResultSet();
                    while(rsCat.next()){
                        String insertCat="insert into stk_category(CategoryCode, Description, AccountCode," +
                                " L_ink, CategoryFor, SMobileYN)values(" +
                                " '"+rsCat.getString(1)+"', '"+rsCat.getString(2)+"', '"+rsCat.getString(3)+"'," +
                                " '"+rsCat.getString(4)+"', '"+rsCat.getString(5)+"', '"+rsCat.getString(6)+"')";
                        db.addQuery(insertCat);
                    }
                   // db.closeDB();


                    String vDelSales="delete from stk_salesman";
                    db.addQuery(vDelSales);
                    String qSales="select SalesPersonCode, N_ame, Address, " +
                            "AreaCode, Tel, Fax, " +
                            "ParentCode, DetailYN, Status, " +
                            "R_atio, BranchCode from stk_salesman";
                    Statement stmtSales = conn.createStatement();
                    stmtSales.execute(qSales);
                    ResultSet rsSales = stmtSales.getResultSet();
                    while(rsSales.next()){
                        String insertSales="insert into stk_salesman(SalesPersonCode, N_ame, Address," +
                                " AreaCode, Tel, Fax," +
                                " ParentCode, DetailYN, Status," +
                                " R_atio, BranchCode)values(" +
                                " '"+rsSales.getString(1)+"', '"+rsSales.getString(2)+"', '"+rsSales.getString(3)+"'," +
                                " '"+rsSales.getString(4)+"', '"+rsSales.getString(5)+"', '"+rsSales.getString(6)+"'," +
                                " '"+rsSales.getString(7)+"', '"+rsSales.getString(8)+"', '"+rsSales.getString(9)+"'," +
                                " '"+rsSales.getString(10)+"', '"+rsSales.getString(11)+"' )";
                        db.addQuery(insertSales);
                    }
                    db.closeDB();
                    z = "success";
                }
            }
            return z;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }
    private String charReplace(String text){
        String newText=text.replaceAll("[\\$|,|;|']","");
        return newText;
    }

}
