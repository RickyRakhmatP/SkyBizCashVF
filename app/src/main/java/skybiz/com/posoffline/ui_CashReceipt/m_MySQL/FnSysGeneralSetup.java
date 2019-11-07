package skybiz.com.posoffline.ui_CashReceipt.m_MySQL;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/11/2017.
 */

public class FnSysGeneralSetup extends AsyncTask<Void,Void,String> {
    Context c;
    String IPAddress;
    String DBName;
    String UserName;
    String Password;
    String URL,z;
    String sql;
    String vDateFormat, NewDoc;

    ProgressDialog pd;

    public FnSysGeneralSetup(Context c) {
        this.c = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //pd=new ProgressDialog(c);
        //pd.setTitle("Retrieve");
       // pd.setMessage("Retrieving... Please Wait");
       // pd.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.downloadData();
    }

    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);
        //pd.dismiss();
        if(jsonData==null){
            Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Successfull retrieve Setting", Toast.LENGTH_SHORT).show();
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
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            Cursor curSet = db.getAllSeting();
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(1);
                UserName = curSet.getString(2);
                Password = curSet.getString(3);
                DBName = curSet.getString(4);
            }
            URL="jdbc:mysql://"+IPAddress+"/"+DBName;
            Connection conn= Connector.connect(URL, UserName, Password);
            if (conn == null) {
                z="error";
                //((MainActivity)c).showSetting();
                //Toast.makeText(c,"Please check your connection",Toast.LENGTH_SHORT).show();
            }else {
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
                //ResultSet resultSet = statement.getResultSet();
                //ResultSetMetaData columns = resultSet.getMetaData();
                   /* while (resultSet.next()) {
                        CurCode = resultSet.getString("CurCode");
                        GSTNO = resultSet.getString("GSTNo");
                        CompanyName = resultSet.getString("CompanyName");
                        JSONObject row = new JSONObject();
                        for (int i = 1; i <= columns.getColumnCount(); i++) {
                            row.put(columns.getColumnName(i), resultSet.getObject(i));
                        }
                        results.put(row);
                    }*/
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
            return z;
        }catch (SQLException e ){
            e.printStackTrace();
        }
        return z;
    }
}
