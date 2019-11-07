package skybiz.com.posoffline.ui_Setting.m_Sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 22/12/2017.
 */

public class Sync_SysGeneralSetup extends AsyncTask<Void, Integer, String> {
    Context c;
    ProgressBar barGeneral;
    String IPAddress,DBName,UserName,Password,URL,DBStatus,ItemConn,
            z,sql,vDateFormat, NewDoc,Port;

    public Sync_SysGeneralSetup(Context c, ProgressBar barGeneral) {
        this.c = c;
        this.barGeneral = barGeneral;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        barGeneral.setMax(100);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        barGeneral.setProgress(progress[0]);
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.downloadData();
    }

    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);
        if(jsonData==null){
            Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Successfull retrieve Setting", Toast.LENGTH_SHORT).show();
            barGeneral.setProgress(100);
        }
    }

    private String downloadData(){
        String CurCode          ="";
        String GSTNO            ="";
        String CompanyName      ="";
        String vPostGlobalTaxYN ="";
        String RoundingCS       ="";
        String LayawayAsSalesYN ="";
        try {
            JSONObject jsonReq,jsonRes;
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
                db.DelCompany();
                db.DeleteGeneralSetup();
                String vCompSetup = "SELECT CompanyCode, CompanyName, Address, ComTown, ComState, " +
                        " ComCountry, Tel1, Fax1, '' as CompanyEmail, GSTNo, CurCode FROM  companysetup";
                jsonReq=new JSONObject();
                ConnectorLocal conn = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vCompSetup);
                jsonReq.put("action", "select");
                String response = conn.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response);
                String reqCom = jsonRes.getString("hasil");

                JSONArray jaCom = new JSONArray(reqCom);
                JSONObject rsCom = null;
                ContentValues cvCom = new ContentValues();
                for (int i = 0; i < jaCom.length(); i++) {
                    rsCom = jaCom.getJSONObject(i);
                    cvCom.put("CompanyCode", rsCom.getString("CompanyCode"));
                    cvCom.put("CompanyName", rsCom.getString("CompanyName"));
                    cvCom.put("Address", rsCom.getString("Address"));
                    cvCom.put("ComTown", rsCom.getString("ComTown"));
                    cvCom.put("ComState", rsCom.getString("ComState"));
                    cvCom.put("ComCountry", rsCom.getString("ComCountry"));
                    cvCom.put("Tel1", rsCom.getString("Tel1"));
                    cvCom.put("Fax1", rsCom.getString("Fax1"));
                    cvCom.put("CompanyEmail", rsCom.getString("CompanyEmail"));
                    cvCom.put("GSTNo", rsCom.getString("GSTNo"));
                    cvCom.put("CurCode", rsCom.getString("CurCode"));
                    db.addComSetup(cvCom);
                    db.addGeneral(rsCom.getString("CurCode"), rsCom.getString("GSTNo"), rsCom.getString("CompanyName"), "", "", "0", "");
                    publishProgress(i);
                }
                z = "success";
            }else{
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName;
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn == null) {
                    z = "error";
                    //((MainActivity)c).showSetting();
                    //Toast.makeText(c,"Please check your connection",Toast.LENGTH_SHORT).show();
                } else {
                    String sql = "select CurCode,GSTNo,CompanyName from companysetup ";
                    // JSONArray results = new JSONArray();
                    Statement statement = conn.createStatement();
                    statement.execute(sql);
                    ResultSet resultSet = statement.getResultSet();
                    while (resultSet.next()) {
                        CurCode = resultSet.getString("CurCode");
                        GSTNO = resultSet.getString("GSTNo");
                        CompanyName = resultSet.getString("CompanyName");
                    }
                    resultSet.close();
                    String sql2 = "SELECT PostGlobalTaxYN FROM sys_general_setup2";
                    Statement statement2 = conn.createStatement();
                    statement2.execute(sql2);
                    ResultSet rs2 = statement2.getResultSet();
                    while (rs2.next()) {
                        vPostGlobalTaxYN = rs2.getString("PostGlobalTaxYN");
                    }

                    String sql3 = "SELECT T_ext,C_ode FROM sys_general_setup4 where C_ode IN ('RoundingCS','LayawayAsSalesYN') and ProgramName='Dis' order by C_ode Desc";
                    Statement statement3 = conn.createStatement();
                    statement3.execute(sql3);
                    ResultSet rs3 = statement3.getResultSet();
                    while (rs3.next()) {
                        String C_ode = rs3.getString("C_ode");
                        String T_ext = rs3.getString("T_ext");
                        if (C_ode.equals("RoundingCS")) {
                            RoundingCS = rs3.getString("T_ext");
                        }
                        if (C_ode.equals("LayawayAsSalesYN")) {
                            LayawayAsSalesYN = rs3.getString("T_ext");
                        }
                    }
                    db.DelGen3();
                    String vGeneral3 = "Select * from  sys_general_setup3";
                    Statement stmtGen3 = conn.createStatement();
                    stmtGen3.execute(vGeneral3);
                    ResultSet rsGen3 = stmtGen3.getResultSet();
                    ContentValues cv = new ContentValues();
                    while (rsGen3.next()) {
                        cv.put("SalesTaxCode", rsGen3.getString("SalesTaxCode"));
                        cv.put("SalesTaxRate", rsGen3.getString("SalesTaxRate"));
                        cv.put("PurchaseTaxCode", rsGen3.getString("PurchaseTaxCode"));
                        cv.put("PurchaseTaxRate", rsGen3.getString("PurchaseTaxRate"));
                        cv.put("RetailTaxCode", rsGen3.getString("RetailTaxCode"));
                        cv.put("RetailTaxRate", rsGen3.getString("RetailTaxRate"));
                        db.addGen3(cv);
                    }

                    db.DelCompany();
                    String vCompSetup = "Select * from  companysetup";
                    Statement stmtCom = conn.createStatement();
                    stmtCom.execute(vCompSetup);
                    ResultSet rsCom = stmtCom.getResultSet();
                    ContentValues cvCom = new ContentValues();
                    int i = 1;
                    while (rsCom.next()) {
                        cvCom.put("CompanyCode", rsCom.getString("CompanyCode"));
                        cvCom.put("CompanyName", rsCom.getString("CompanyName"));
                        cvCom.put("Address", rsCom.getString("Address"));
                        cvCom.put("ComTown", rsCom.getString("ComTown"));
                        cvCom.put("ComState", rsCom.getString("ComState"));
                        cvCom.put("ComCountry", rsCom.getString("ComCountry"));
                        cvCom.put("Tel1", rsCom.getString("Tel1"));
                        cvCom.put("Fax1", rsCom.getString("Fax1"));
                        cvCom.put("CompanyEmail", rsCom.getString("CompanyEmail"));
                        cvCom.put("GSTNo", rsCom.getString("GSTNo"));
                        cvCom.put("CurCode", rsCom.getString("CurCode"));
                        db.addComSetup(cvCom);
                        publishProgress(i);
                        i++;
                    }

                    //fnret lastno
                    SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date();
                    String datedShort = DateCurr.format(date);
                    String queryLastNo = "SELECT OrderType,Prefix,LastNo,Suffix,DateFormatType FROM sys_runno_dt WHERE '" + datedShort + "' >= DateFrom AND '" + datedShort + "' <= DateTo AND RunnoCode='CS' ORDER BY RunNo ASC LIMIT 1 ";
                    Statement statement4 = conn.createStatement();
                    statement4.execute(queryLastNo);
                    ResultSet rs4 = statement4.getResultSet();
                    while (rs4.next()) {
                        String OrderType = rs4.getString("OrderType");
                        String Prefix = rs4.getString("Prefix");
                        String LastNo = rs4.getString("LastNo");
                        String Suffix = rs4.getString("Suffix");
                        String DateFormat1 = rs4.getString("DateFormatType");
                        if ((!DateFormat1.equals("None")) && (!DateFormat1.equals(""))) {
                            vDateFormat = DateFormat1.replace("MM", datedShort.substring(5, 2));
                            vDateFormat = DateFormat1.replace("YYYY", datedShort.substring(1, 4));
                            vDateFormat = DateFormat1.replace("YY", datedShort.substring(3, 2));
                        } else {
                            vDateFormat = "";
                        }

                        if (OrderType.equals("Prefix, New No, Date Format, Suffix")) {
                            NewDoc = Prefix + LastNo + vDateFormat + Suffix;
                        } else if (OrderType.equals("Prefix, Date Format, New No, Suffix")) {
                            NewDoc = Prefix + vDateFormat + LastNo + Suffix;
                        } else if (OrderType.equals("Prefix, Suffix, Date Format, New No")) {
                            NewDoc = Prefix + Suffix + vDateFormat + LastNo;
                        } else if (OrderType.equals("Date Format, New No, Prefix, Suffix")) {
                            NewDoc = vDateFormat + LastNo + Prefix + Suffix;
                        } else if (OrderType.equals("New No, Date Format, Prefix, Suffix")) {
                            NewDoc = LastNo + vDateFormat + Prefix + Suffix;
                        } else {
                            NewDoc = Prefix + LastNo + Suffix + vDateFormat;
                        }
                    }
                    db.DeleteGeneralSetup();
                    long result = db.addGeneral(CurCode, GSTNO, CompanyName, RoundingCS, LayawayAsSalesYN, vPostGlobalTaxYN, NewDoc);
                    if (result > 0) {
                        Log.d("LOG", "Success Insert SysGeneralSetup to local db ");
                    } else {
                        Log.d("LOG", "Error Insert SysGeneralSetup to local db ");
                    }
                    db.closeDB();
                    statement2.close();
                    statement3.close();
                    statement4.close();
                    statement.close();
                    z = "success";
                }
            }
            return z;
        }catch (SQLException e ){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return z;
    }
}

