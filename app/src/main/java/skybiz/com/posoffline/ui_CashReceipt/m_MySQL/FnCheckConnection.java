package skybiz.com.posoffline.ui_CashReceipt.m_MySQL;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 21/11/2017.
 */

public class FnCheckConnection extends AsyncTask<Void, String, String> {
    Context c;
    String IPAddress,DBName,UserName,Password,Port,DBStatus,MacAddress;
    String vDateFormat, NewDoc,PaymentCode,PaymentType,PaidByCompanyYN,Charges1;

    String URL;
    String sql;
    String z;
    Boolean isSuccess = false;
    //ProgressDialog pd;

    public FnCheckConnection(Context c, String IPAddress, String DBName,
                             String UserName, String password, String Port,
                             String DBStatus, String MacAddress) {
        this.c = c;
        this.IPAddress = IPAddress;
        this.DBName = DBName;
        this.UserName = UserName;
        Password = password;
        this.Port = Port;
        this.DBStatus=DBStatus;
        this.MacAddress=MacAddress;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

      //  pd=new ProgressDialog(c);
      //  pd.setTitle("Check Connection");
      //  pd.setMessage("Checking... Please Wait");
      //  pd.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String CurCode          ="";
            String GSTNo            ="";
            String CompanyName      ="";
            String vPostGlobalTaxYN ="";
            String RoundingCS       ="";
            String LayawayAsSalesYN ="";
            JSONObject jsonReq,jsonRes;
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            if(!DBStatus.equals("Android Server")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName;
                //Log.d("URL",URL);
                Connection conn = Connector.connect(URL, UserName, Password);
                if(conn==null) {
                    z           = "error";
                    isSuccess   = false;
                }else {
                    Log.d("ERROR", "Connection");
                    String sql = "select CurCode, IFNULL(GSTNo,'') as GSTNo ,CompanyName " +
                            "from companysetup ";
                    Statement statement = conn.createStatement();
                    statement.execute(sql);
                    ResultSet resultSet = statement.getResultSet();
                    while (resultSet.next()) {
                        CurCode = resultSet.getString("CurCode");
                        GSTNo = resultSet.getString("GSTNo");
                        if (GSTNo.equals("")) {
                            GSTNo = "NO";
                        } else if (GSTNo.isEmpty()) {
                            GSTNo = "NO";
                        }
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
                    rs3.close();
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
                    String vCompSetup = "Select CompanyCode, CompanyName, Address," +
                            " ComTown, ComState, ComCountry," +
                            "Tel1, Fax1, CompanyEmail," +
                            "IFNULL(GSTNo,'') as GSTNo, CurCode," +
                            "IFNULL(Footer_CR,'') as Footer_CR, IFNULL(PhotoFile,'')as PhotoFile from  companysetup";
                    Statement stmtCom = conn.createStatement();
                    stmtCom.execute(vCompSetup);
                    ResultSet rsCom = stmtCom.getResultSet();
                    ContentValues cvCom = new ContentValues();
                    int i = 1;
                    while (rsCom.next()) {
                        GSTNo = rsCom.getString("GSTNo");
                        if (GSTNo.equals("")) {
                            GSTNo = "NO";
                        } else if (GSTNo.isEmpty()) {
                            GSTNo = "NO";
                        }
                        String PhotoFile    =rsCom.getString("PhotoFile");
                        if(!PhotoFile.isEmpty()){
                            Blob test           =rsCom.getBlob(13);
                            int blobl           =(int)test.length();
                            byte[] blobasbyte   =test.getBytes(1,blobl);
                            Bitmap bmp          = BitmapFactory.decodeByteArray(blobasbyte,0,blobasbyte.length);
                            PhotoFile            =encodeBmp(bmp);
                        }
                        cvCom.put("CompanyCode", rsCom.getString("CompanyCode"));
                        cvCom.put("CompanyName", rsCom.getString("CompanyName"));
                        cvCom.put("Address", rsCom.getString("Address"));
                        cvCom.put("ComTown", rsCom.getString("ComTown"));
                        cvCom.put("ComState", rsCom.getString("ComState"));
                        cvCom.put("ComCountry", rsCom.getString("ComCountry"));
                        cvCom.put("Tel1", rsCom.getString("Tel1"));
                        cvCom.put("Fax1", rsCom.getString("Fax1"));
                        cvCom.put("CompanyEmail", rsCom.getString("CompanyEmail"));
                        cvCom.put("GSTNo", GSTNo);
                        cvCom.put("CurCode", rsCom.getString("CurCode"));
                        cvCom.put("Footer_CR", rsCom.getString("Footer_CR"));
                        cvCom.put("PhotoFile", PhotoFile);
                        db.addComSetup(cvCom);
                        i++;
                    }
                    rsCom.close();
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
                    rs4.close();
                    db.DeleteGeneralSetup();
                    long result = db.addGeneral(CurCode, GSTNo, CompanyName, RoundingCS, LayawayAsSalesYN, vPostGlobalTaxYN, NewDoc);
                    if (result > 0) {
                        Log.d("LOG", "Success Insert SysGeneralSetup to local db ");
                    } else {
                        Log.d("LOG", "Error Insert SysGeneralSetup to local db ");
                    }

                    /*db.DelAllPayType();
                    String vQuery = "SELECT PaymentCode,PaymentType,CC_PaidByCompanyYN," +
                            "Charges1, MerchantCode, MerchantKey " +
                            "FROM ret_paymenttype WHERE PaymentCode <> '' AND Status = 'Active' " +
                            "Order By PaymentType, PaymentCode";
                    Statement stmtPay = conn.createStatement();
                    if (stmtPay.execute(vQuery)) {
                        ResultSet rsPay = stmtPay.getResultSet();
                        while (rsPay.next()) {
                            PaymentCode = rsPay.getString("PaymentCode");
                            PaymentType = rsPay.getString("PaymentType");
                            PaidByCompanyYN = rsPay.getString("CC_PaidByCompanyYN");
                            Charges1 = rsPay.getString("Charges1");
                            String MerchantCode = rsPay.getString("MerchantCode");
                            String MerchantKey = rsPay.getString("MerchantKey");
                            db.addPayType(PaymentCode, PaymentType, Charges1, PaidByCompanyYN, MerchantCode, MerchantKey);
                            //db.addPayType(PaymentCode, PaymentType, Charges1, PaidByCompanyYN);
                        }
                        rsPay.close();
                    }*/

                    db.DelTax();
                    String sqlTax = "select TaxCode,Description,R_ate,TaxType,GSTTaxType, '' as GSTTaxCode from stk_tax ";
                    Statement stmtTax = conn.createStatement();
                    stmtTax.execute(sqlTax);
                    ResultSet rsTax = stmtTax.getResultSet();
                    ContentValues cvTax = new ContentValues();
                    while (rsTax.next()) {
                        cvTax.put("TaxCode", rsTax.getString("TaxCode"));
                        cvTax.put("R_ate", rsTax.getString("R_ate"));
                        cvTax.put("TaxType", rsTax.getString("TaxType"));
                        cvTax.put("GSTTaxType", rsTax.getString("GSTTaxType"));
                        cvTax.put("GSTTaxCode", rsTax.getString("GSTTaxCode"));
                        cvTax.put("Description", rsTax.getString("Description"));
                        db.add_stk_tax(cvTax);
                    }
                    rsTax.close();
                    String vDelG4 = "delete from sys_general_setup4";
                    db.addQuery(vDelG4);
                    String qGen4 = "select C_ode, T_ype, E_num , " +
                            " DATE_FORMAT(D_ate,'%Y-%m-%d') as D_ate, T_ext, I_nteger, " +
                            " D_ouble, DATE_FORMAT(D_ateTime,'%Y-%m-%d %H:%i:%s') as D_ateTime, DATE_FORMAT(T_ime,'%H:%i:%s') as T_ime, " +
                            " ProgramName " +
                            " from sys_general_setup4 where ProgramName='Dis' and C_ode<>'' ";
                    Statement stmtGen4 = conn.createStatement();
                    stmtGen4.execute(qGen4);
                    ResultSet rsGen4 = stmtGen4.getResultSet();
                    while (rsGen4.next()) {
                        String inGen4 = "insert into sys_general_setup4( C_ode, T_ype,E_num, " +
                                " D_ate, T_ext, I_nteger, " +
                                " D_ouble, D_ateTime, T_ime, " +
                                " ProgramName)values(" +
                                " '" + rsGen4.getString(1) + "', '" + rsGen4.getString(2) + "', '" + rsGen4.getString(3) + "'," +
                                " '" + rsGen4.getString(4) + "', '" + rsGen4.getString(5) + "', '" + rsGen4.getString(6) + "'," +
                                " '" + rsGen4.getString(7) + "', '" + rsGen4.getString(8) + "', '" + rsGen4.getString(9) + "'," +
                                " '" + rsGen4.getString(10) + "')";

                        Log.d("INSERT GEN 4", inGen4);
                        db.addQuery(inGen4);
                    }
                    rsGen4.close();

                    String vDelG2="delete from sys_general_setup2";
                    db.addQuery(vDelG2);
                    String qSysGen2="select UseSerialNoYN, ItemGradingYN, PricingMatrixYN, " +
                            " TransactionBatchYN, StockBundlingYN, ItemBatchYN, " +
                            " QuantityFormulaYN, UnitPriceFormulaYN, WarrantyDateYN, " +
                            " SalesPersonYN, BranchYN, DepartmentYN, " +
                            " ProjectYN, LocationYN, UserDefineAnalysisCodeYN, " +
                            " ResetDetailYN, CompBranchYN, CompDepartmentYN, " +
                            " CompProjectYN, CompLocationYN, PostGlobalTaxYN, " +
                            " DepositAccountCode from sys_general_setup2";
                    Statement stmtGen2 = conn.createStatement();
                    stmtGen2.execute(qSysGen2);
                    ResultSet rsGen2 = stmtGen2.getResultSet();
                    while (rsGen2.next()){
                        String inGen2="insert into sys_general_setup2(UseSerialNoYN, ItemGradingYN, PricingMatrixYN, " +
                                " TransactionBatchYN, StockBundlingYN, ItemBatchYN, " +
                                " QuantityFormulaYN, UnitPriceFormulaYN, WarrantyDateYN, " +
                                " SalesPersonYN, BranchYN, DepartmentYN, " +
                                " ProjectYN, LocationYN, UserDefineAnalysisCodeYN, " +
                                " ResetDetailYN, CompBranchYN, CompDepartmentYN, " +
                                " CompProjectYN, CompLocationYN, PostGlobalTaxYN, " +
                                " DepositAccountCode)values(" +
                                " '"+rsGen2.getString(1)+"', '"+rsGen2.getString(2)+"', '"+rsGen2.getString(3)+"'," +
                                " '"+rsGen2.getString(4)+"', '"+rsGen2.getString(5)+"', '"+rsGen2.getString(6)+"'," +
                                " '"+rsGen2.getString(7)+"', '"+rsGen2.getString(8)+"', '"+rsGen2.getString(9)+"'," +
                                " '"+rsGen2.getString(10)+"', '"+rsGen2.getString(11)+"', '"+rsGen2.getString(12)+"'," +
                                " '"+rsGen2.getString(13)+"', '"+rsGen2.getString(14)+"', '"+rsGen2.getString(15)+"'," +
                                " '"+rsGen2.getString(16)+"', '"+rsGen2.getString(17)+"', '"+rsGen2.getString(18)+"'," +
                                " '"+rsGen2.getString(19)+"', '"+rsGen2.getString(20)+"', '"+rsGen2.getString(21)+"'," +
                                " '"+rsGen2.getString(22)+"')";
                        //Log.d("INSERT GEN 2",inGen2);
                        db.addQuery(inGen2);
                    }
                    rsGen2.close();

                    z           = "success";
                    isSuccess   = true;
                }
                return z;
            }else{
                //sync general setup
                db.DelCompany();
                db.DeleteGeneralSetup();
                String vCompSetup = "SELECT CompanyCode, CompanyName, Address, ComTown, ComState, " +
                        " ComCountry, Tel1, Fax1, '' as CompanyEmail, GSTNo, CurCode FROM  companysetup";
                jsonReq=new JSONObject();
                ConnectorLocal conn = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vCompSetup);
                jsonReq.put("action", "select");
                String response1 = conn.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response1);
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
                }
                //sync payment type
                db.DelAllPayType();
                String vQuery = "SELECT PaymentCode, PaymentType, PaidByCompanyYN, " +
                        "Charges1, MerchantCode, MerchantKey " +
                        "FROM ret_paymenttype Order By PaymentType";
                jsonReq=new JSONObject();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vQuery);
                jsonReq.put("action", "select");
                String rspPay = conn.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rspPay);
                String rsGroup = jsonRes.getString("hasil");
                JSONArray jaPay = new JSONArray(rsGroup);
                JSONObject joPay = null;
                for (int i = 0; i < jaPay.length(); i++) {
                    joPay               = jaPay.getJSONObject(i);
                    PaymentCode         = joPay.getString("PaymentCode");
                    PaymentType         = joPay.getString("PaymentType");
                    PaidByCompanyYN     = joPay.getString("PaidByCompanyYN");
                    Charges1            = joPay.getString("Charges1");
                    String MerchantCode = joPay.getString("MerchantCode");
                    String MerchantKey  = joPay.getString("MerchantKey");
                    db.addPayType(PaymentCode, PaymentType, Charges1, PaidByCompanyYN,MerchantCode,MerchantKey);
                }

                //sync tax
                db.DelTax();
                String sqlTax = "select TaxCode,Description,R_ate,TaxType,GSTTaxType, '' as GSTTaxCode from stk_tax ";
                jsonReq=new JSONObject();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", sqlTax);
                jsonReq.put("action", "select");
                String rspTax = conn.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rspTax);
                String resTax = jsonRes.getString("hasil");
                JSONArray jaTax = new JSONArray(resTax);
                JSONObject joTax = null;
                ContentValues cv = new ContentValues();
                for (int i = 0; i < jaTax.length(); i++) {
                    joTax = jaTax.getJSONObject(i);
                    cv.put("TaxCode", joTax.getString("TaxCode"));
                    cv.put("R_ate", joTax.getString("R_ate"));
                    cv.put("TaxType", joTax.getString("TaxType"));
                    cv.put("GSTTaxType", joTax.getString("GSTTaxType"));
                    cv.put("GSTTaxCode", joTax.getString("GSTTaxCode"));
                    cv.put("Description", joTax.getString("Description"));
                    db.add_stk_tax(cv);

                }
                JSONObject jsonReq2,jsonRes2;
                jsonReq2 = new JSONObject();
                jsonReq2.put("request", "request-connect-client");
                jsonReq2.put("query", "");
                jsonReq2.put("action", "check");
                ConnectorLocal connectorLocal = new ConnectorLocal();
                String response = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq2.toString());
                jsonRes2 = new JSONObject(response);
                String hasil= jsonRes2.getString("success");
                if(hasil.equals("success")){
                    z = "success";
                    isSuccess = true;
                }else{
                    z = "error";
                    isSuccess = false;
                }
                return z;
            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }

    private String encodeBmp(Bitmap bmp){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,70,baos);
        byte[] b=baos.toByteArray();
        String base64= Base64.encodeToString(b,Base64.DEFAULT);
        return base64;
    }
    @Override
    protected void onPostExecute(String isConnect) {
        super.onPostExecute(isConnect);
      //  pd.dismiss();
        if(isSuccess==false){
            Toast.makeText(c,"Connection Failure", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"You have a successful connection", Toast.LENGTH_SHORT).show();

        }
    }
}
