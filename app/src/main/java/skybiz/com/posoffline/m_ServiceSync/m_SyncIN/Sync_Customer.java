package skybiz.com.posoffline.m_ServiceSync.m_SyncIN;

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
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 21/12/2017.
 */

public class Sync_Customer extends AsyncTask<Void,Void,String> {

    Context c;
    String IPAddress,UserName,Password,
            URL,DBName,z,
            Port,DBStatus,ItemConn,
            EncodeType,DateTimeSync;

    public Sync_Customer(Context c) {
        this.c = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.retCustomer();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("error")){
            Toast.makeText(c,"Failure, sync in data customer", Toast.LENGTH_SHORT).show();
        }else if(result.equals("success")){
            Toast.makeText(c,"Successfull, sync in data customer", Toast.LENGTH_SHORT).show();
        }else if(result.equals("zero")){
            //data to sync in not found
        }
    }
    private String retCustomer(){
        try{
            z="zero";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String DateTimeLast = sdf.format(date);
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName,UserName,Password," +
                    " DBName,Port,DBStatus," +
                    " ItemConn,EncodeType, IFNULL(D_ateTimeSync,'') as D_ateTimeSync " +
                    " from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress   = curSet.getString(0);
                UserName    = curSet.getString(1);
                Password    = curSet.getString(2);
                DBName      = curSet.getString(3);
                Port        = curSet.getString(4);
                DBStatus    = curSet.getString(5);
                ItemConn    = curSet.getString(6);
                EncodeType  = curSet.getString(7);
                DateTimeSync= curSet.getString(8);
            }
            String D_ate="";
            String T_ime="";
            if(!DateTimeSync.isEmpty()) {
                String[] split = DateTimeSync.split(" ");
                D_ate = split[0];
                T_ime = split[1];
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
                    String qAuditLog="select Remark from sys_audit_log where " +
                            "D_ate>='"+D_ate+"' and T_ime>='"+T_ime+"' " +
                            "and EventType='Delete' and Sources='Customer' ";
                    Statement stmtCA = conn.createStatement();
                    stmtCA.execute(qAuditLog);
                    ResultSet rsA=stmtCA.getResultSet();
                    while(rsA.next()){
                        String checkDel="select count(*)as numrows from customer where CusCode='"+rsA.getString(1)+"' ";
                        Cursor rsDel=db.getQuery(checkDel);
                        while(rsDel.moveToNext()){
                            int numrows=rsDel.getInt(0);
                            if(numrows>0){
                                String delCus="delete from customer where CusCode='"+rsA.getString(1)+"' ";
                                db.addQuery(delCus);
                            }
                        }
                    }
                    //String del = "delete from customer";
                    //db.addQuery(del);
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
                            " from customer where StatusBadYN='0' and DateTimeModified>='"+DateTimeSync+"' ";
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
                                " '" + rsCus.getString("Fax2") + "', '" + rsCus.getString("Contact") + "', '" + rsCus.getString("ContactTel") + "', " +
                                " '" + rsCus.getString("Email") + "', '" + rsCus.getString("StatusBadYN") + "', '" + rsCus.getString("Town") + "',  " +
                                " '" + rsCus.getString("State") + "', '" + rsCus.getString("Country") + "', '" + rsCus.getString("PostCode") + "',  " +
                                " '" + rsCus.getString("L_ink") + "', '" + rsCus.getString("NRICNo") + "', '" + rsCus.getString("DOB") + "',  " +
                                " '" + rsCus.getString("Sex") + "', '" + rsCus.getString("MemberType") + "', '" + rsCus.getString("CardNo") + "',  " +
                                " '" + rsCus.getString("PaymentCode") + "', '" + rsCus.getString("DateTimeModified") + "', '"+rsCus.getString("MembershipClass")+"')";
                        long addItem = db.addQuery(QueryIn);
                        if (addItem > 0) {

                        } else {
                           // Log.d("ERROR", rsCus.getString("ItemCode"));
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
                    //db.closeDB();
                    //
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

                    String vUpdate="update tb_setting set D_ateTimeSync='"+DateTimeLast+"' ";
                    db.addQuery(vUpdate);

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
        String newText=text.replaceAll("[\\.$|,|;|']","");
        return newText;
    }

}
