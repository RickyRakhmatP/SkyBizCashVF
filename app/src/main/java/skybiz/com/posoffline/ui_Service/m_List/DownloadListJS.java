package skybiz.com.posoffline.ui_Service.m_List;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Service.m_Save.SaveService;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class DownloadListJS extends AsyncTask<Void,Void,String> {
    Context c;
    String SearchBy,Keyword,StatusService;
    RecyclerView rv;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
    String CurCode;
    DialogListJS dialogListJS;
    JSONObject jsonReq,jsonRes;

    public DownloadListJS(Context c, String StatusService, String SearchBy , String Keyword, RecyclerView rv, DialogListJS dialogListJS) {
        this.c = c;
        this.StatusService=StatusService;
        this.SearchBy = SearchBy;
        this.Keyword = Keyword;
        this.rv = rv;
        this.dialogListJS=dialogListJS;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.downloadData();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result==null){
            Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
        }else{
            ListJSParser p=new ListJSParser(c,result,rv,dialogListJS);
            p.execute();
        }
    }

    private String downloadData(){
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            Cursor cur = db.getGeneralSetup();
            while (cur.moveToNext()) {
                CurCode = cur.getString(1);
            }
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
            String vClause="";
            if(SearchBy.equals("By CusCode")){
                vClause="  and CusCode like '%"+Keyword+"%' ";
            }else if(SearchBy.equals("By CusName")){
                vClause="  and CusName like '%"+Keyword+"%' ";
            }else if(SearchBy.equals("Show All")){
                //vClause="";
            }
            String vClause1="";
            if(StatusService.equals("Closed")){
                vClause1="where servicestatus='Closed' ";
            }else{
                vClause1="where servicestatus<>'Closed' ";
            }
            String sql ="Select Doc1No, cuscode, cusname, " +
                    " mobileno, receiptno, DATE_FORMAT(receiptdate,'%Y-%m-%d') as receiptdate, " +
                    " repairtype, casetype, entryid, " +
                    " d_ate, outputid, DATE_FORMAT(outputdate,'%Y-%m-%d') as outputdate, " +
                    " receivemode, termcode, productmodel, " +
                    " partno, serialno, supplierserialno," +
                    " warrantystatus, warrantydesc, DATE_FORMAT(warrantyexpirydate,'%Y-%m-%d') as warrantyexpirydate, " +
                    " accessories, problemdesc, collectedby, " +
                    " DATE_FORMAT(collecteddate,'%Y-%m-%d') as collecteddate, sendtovendorYN, DATE_FORMAT(sendtovendordate,'%Y-%m-%d') as sendtovendordate, " +
                    " vendorwarrantystatus, vendorcode, vendorname, " +
                    " vendortelno, backfromvendorYN, DATE_FORMAT(backfromvendordate,'%Y-%m-%d') as backfromvendordate, " +
                    " returnbackenduserYN, DATE_FORMAT(returnbackenduserdate,'%Y-%m-%d') as returnbackenduserdate, returnbackenduserby," +
                    " servicenoteremark, L_ink, Address, " +
                    " IFNULL(ContactTel,'') as ContactTel, IFNULL(Email,'') as Email, servicestatus, " +
                    " IFNULL(Technician,'') as Technician, IFNULL(Contact,'')as Contact, IFNULL(Priority,'') as Priority," +
                    " DATE_FORMAT(T_ime,'%H:%i') as T_ime, DATE_FORMAT(InstallationDate,'%Y-%m-%d %H:%i') as InstallationDate, TechnicalReport, " +
                    " DATE_FORMAT(DateTimeAttended,'%Y-%m-%d %H:%i:%s') as DateTimeAttended, IFNULL(PhotoFile,'') as PhotoFile," +
                    " IFNULL(PhotoFile2,'') as PhotoFile2, IFNULL(PhotoFileName,'') as PhotoFileName, DATE_FORMAT(ActionTimeStart,'%H:%i') as ActionTimeStart," +
                    " DATE_FORMAT(ActionTimeEnd,'%H:%i') as ActionTimeEnd " +
                    " from stk_service_hd "+vClause1+"  "+vClause+" ";
            Log.d("QUERY", sql);
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    JSONArray results = new JSONArray();
                    Statement statement = conn.createStatement();
                    if (statement.execute(sql)) {
                        ResultSet rsData = statement.getResultSet();
                        //ResultSetMetaData columns = resultSet.getMetaData();
                        while (rsData.next()) {
                            /*JSONObject row = new JSONObject();
                            for (int i = 1; i <= columns.getColumnCount(); i++) {
                                row.put(columns.getColumnName(i), resultSet.getObject(i));
                            }*/
                            JSONObject row2=new JSONObject();
                            row2.put("Doc1No",rsData.getString(1));
                            row2.put("cuscode",rsData.getString(2));
                            row2.put("cusname",rsData.getString(3));
                            row2.put("mobileno",rsData.getString(4));
                            row2.put("receiptno",rsData.getString(5));
                            row2.put("receiptdate",rsData.getString(6));
                            row2.put("repairtype",rsData.getString(7));
                            row2.put("casetype",rsData.getString(8));
                            row2.put("entryid",rsData.getString(9));
                            row2.put("d_ate",rsData.getString(10));
                            row2.put("outputid",rsData.getString(11));
                            row2.put("outputdate",rsData.getString(12));
                            row2.put("receivemode",rsData.getString(13));
                            row2.put("termcode",rsData.getString(14));
                            row2.put("productmodel",rsData.getString(15));
                            row2.put("partno",rsData.getString(16));
                            row2.put("serialno",rsData.getString(17));
                            row2.put("supplierserialno",rsData.getString(18));
                            row2.put("warrantystatus",rsData.getString(19));
                            row2.put("warrantydesc",rsData.getString(20));
                            row2.put("warrantyexpirydate",rsData.getString(21));
                            row2.put("accessories",rsData.getString(22));
                            row2.put("problemdesc",rsData.getString(23));
                            row2.put("collectedby",rsData.getString(24));
                            row2.put("collecteddate",rsData.getString(25));
                            row2.put("sendtovendorYN",rsData.getString(26));
                            row2.put("sendtovendordate",rsData.getString(27));
                            row2.put("vendorwarrantystatus",rsData.getString(28));
                            row2.put("vendorcode",rsData.getString(29));
                            row2.put("vendorname",rsData.getString(30));
                            row2.put("vendortelno",rsData.getString(31));
                            row2.put("backfromvendorYN",rsData.getString(32));
                            row2.put("backfromvendordate",rsData.getString(33));
                            row2.put("returnbackenduserYN",rsData.getString(34));
                            row2.put("returnbackenduserdate",rsData.getString(35));
                            row2.put("returnbackenduserby",rsData.getString(36));
                            row2.put("servicenoteremark",rsData.getString(37));
                            row2.put("L_ink",rsData.getString(38));
                            row2.put("Address",rsData.getString(39));
                            row2.put("ContactTel",rsData.getString(40));
                            row2.put("Email",rsData.getString(41));
                            row2.put("servicestatus",rsData.getString(42));
                            row2.put("Technician",rsData.getString(43));
                            row2.put("Contact",rsData.getString(44));
                            row2.put("Priority",rsData.getString(45));
                            row2.put("T_ime",rsData.getString(46));
                            row2.put("InstallationDate",rsData.getString(47));
                            row2.put("TechnicalReport",rsData.getString(48));
                            row2.put("DateTimeAttended",rsData.getString(49));
                            row2.put("PhotoFile",rsData.getString(50));
                            row2.put("PhotoFile2","");
                            row2.put("PhotoFileName",rsData.getString(52));
                            row2.put("ActionTimeStart",rsData.getString(53));
                            row2.put("ActionTimeEnd",rsData.getString(54));
                            results.put(row2);
                        }
                        rsData.close();
                    }
                    statement.close();
                    //Log.d("RESULT",results.toString());
                    return results.toString();
                }
            }else if(DBStatus.equals("0")){
                Cursor rsData=db.getQuery(sql);
                JSONArray results2=new JSONArray();
                while(rsData.moveToNext()){
                    JSONObject row2=new JSONObject();
                    row2.put("Doc1No",rsData.getString(0));
                    row2.put("cuscode",rsData.getString(1));
                    row2.put("cusname",rsData.getString(2));
                    row2.put("mobileno",rsData.getString(3));
                    row2.put("receiptno",rsData.getString(4));
                    row2.put("receiptdate",rsData.getString(5));
                    row2.put("repairtype",rsData.getString(6));
                    row2.put("casetype",rsData.getString(7));
                    row2.put("entryid",rsData.getString(8));
                    row2.put("d_ate",rsData.getString(9));
                    row2.put("outputid",rsData.getString(10));
                    row2.put("outputdate",rsData.getString(11));
                    row2.put("receivemode",rsData.getString(12));
                    row2.put("termcode",rsData.getString(13));
                    row2.put("productmodel",rsData.getString(14));
                    row2.put("partno",rsData.getString(15));
                    row2.put("serialno",rsData.getString(16));
                    row2.put("supplierserialno",rsData.getString(17));
                    row2.put("warrantystatus",rsData.getString(18));
                    row2.put("warrantydesc",rsData.getString(19));
                    row2.put("warrantyexpirydate",rsData.getString(20));
                    row2.put("accessories",rsData.getString(21));
                    row2.put("problemdesc",rsData.getString(22));
                    row2.put("collectedby",rsData.getString(23));
                    row2.put("collecteddate",rsData.getString(24));
                    row2.put("sendtovendorYN",rsData.getString(25));
                    row2.put("sendtovendordate",rsData.getString(26));
                    row2.put("vendorwarrantystatus",rsData.getString(27));
                    row2.put("vendorcode",rsData.getString(28));
                    row2.put("vendorname",rsData.getString(29));
                    row2.put("vendortelno",rsData.getString(30));
                    row2.put("backfromvendorYN",rsData.getString(31));
                    row2.put("backfromvendordate",rsData.getString(32));
                    row2.put("returnbackenduserYN",rsData.getString(33));
                    row2.put("returnbackenduserdate",rsData.getString(34));
                    row2.put("returnbackenduserby",rsData.getString(35));
                    row2.put("servicenoteremark",rsData.getString(36));
                    row2.put("L_ink",rsData.getString(37));
                    row2.put("Address",rsData.getString(38));
                    row2.put("ContactTel",rsData.getString(39));
                    row2.put("Email",rsData.getString(40));
                    row2.put("servicestatus",rsData.getString(41));
                    row2.put("Technician",rsData.getString(42));
                    row2.put("Contact",rsData.getString(43));
                    row2.put("Priority",rsData.getString(44));
                    row2.put("T_ime",rsData.getString(45));
                    row2.put("InstallationDate",rsData.getString(46));
                    row2.put("TechnicalReport",rsData.getString(47));
                    row2.put("DateTimeAttended",rsData.getString(48));
                    row2.put("PhotoFile",rsData.getString(49));
                    row2.put("PhotoFile2","");
                    row2.put("PhotoFileName",rsData.getString(51));
                    row2.put("ActionTimeStart",rsData.getString(52));
                    row2.put("ActionTimeEnd",rsData.getString(53));
                    results2.put(row2);
                }
                db.closeDB();
                Log.d("RESULT JSON",results2.toString());
                return results2.toString();
            }
        }catch (SQLException e ){
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
