package skybiz.com.posoffline.ui_CashReceipt.m_Pay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
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
import java.util.Locale;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_NewObject.DecodeChar;
import skybiz.com.posoffline.m_NewObject.Rounding;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.Fragment_Payment;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothPrinter;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothZebra;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_IposAIDL.Utils.IposAidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_USB.PrintingUSB;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI.PrintingWifi;
import skybiz.com.posoffline.ui_QuickCash.QuickCash;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 18/04/2018.
 */

public class SaveCS extends AsyncTask<Void,Void,String> {
    Context c;
    Fragment_Payment fragmentPayment;
    String Doc1No,Doc2No,Doc3No,
            CC1Code,CC1No, AdjAmt,
            uFrom,PaymentType="Cash",ReceiptHeader,
            PaperSize;
    Double CashAmt,ChangeAmt,CC1Amt,
            BalanceAmount,CC2Amt,dServiceCharges;
    String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
    String CC2Code,CC2No;
    TelephonyManager telephonyManager;
    String isDuplicate,NewDoc,CurCode,vPostGlobalTaxYN,GlobalTaxCode,TaxType,LastNo,CusCode,DirectPrintYN="1";
    String TypePrinter,NamePrinter,IPPrinter,vPort,CusName,SalesPersonCode;
    Double R_ate,HCGbTax,HCDtTax,HCGbDiscount,TotalAmt,GbTaxRate1;
    Integer NewNo,Copies=1;
    Boolean isBT;
    String stringPrint,datedTime,Doc1NoSO,
            TableNo,PostAs,RetailYN,
            ReceiptType,MembershipClass,UserCode,
            CounterCode,DocType="CS",tModel="V1",PrintYN;
    Bitmap bmpHeader;
    String imgHeader="";
    public SaveCS(Context c, String uFrom, String doc1No, String Doc2No,
                  String Doc3No,String CC1Code, String CC1No,
                  Double cashAmt, Double changeAmt,
                  Double CC1Amt, Double balanceAmount, String AdjAmt,
                  String PrintYN) {
        this.c = c;
        this.uFrom = uFrom;
        Doc1No = doc1No;
        this.Doc2No=Doc2No;
        this.Doc3No=Doc3No;
        this.CC1Code = CC1Code;
        this.CC1No = CC1No;
        CashAmt = cashAmt;
        ChangeAmt = changeAmt;
        this.CC1Amt = CC1Amt;
        BalanceAmount = balanceAmount;
        this.AdjAmt = AdjAmt;
        this.PrintYN = PrintYN;
        //this.fragmentPayment=fragmentPayment;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params)
    {
        return this.fnsavecs();
    }

    @Override
    protected void onPostExecute(String vData) {
        super.onPostExecute(vData);
        if(vData.equals("error")){
            Toast.makeText(c,"Failed, data cannot save", Toast.LENGTH_SHORT).show();
        }else{
           //fragmentPayment.refreshPayment();
           final Handler handler=new Handler();
           handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (uFrom.equals("CR")) {
                        ((CashReceipt) c).refreshNext();
                    }else if(uFrom.equals("QC")){
                        ((QuickCash)c).refreshNext();
                    }
                }
            },500);
        }
    }
    private String fnsavecs() {
        try {
            String model = SystemProperties.get("ro.product.model");
            tModel=model.substring(0,2);
            Log.d("MODEL",tModel);
            JSONObject jsonReq,jsonRes;
            CusCode             = "999999";
            CusName             = "Cash Sales";
            z                   = "error";
            GlobalTaxCode       = "";
            vPostGlobalTaxYN    = "0";
            Double TotalPointI  =0.00;
            SimpleDateFormat DateCurr  = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat DateCurr1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat DateCurr2 = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            String datedShort = DateCurr.format(date);
            datedTime = DateCurr1.format(date);
            String vTime = DateCurr2.format(date);

            DBAdapter db = new DBAdapter(c);
            db.openDB();
            Cursor cur = db.getGeneralSetup();
            while (cur.moveToNext()) {
                int RunNo = cur.getInt(0);
                CurCode = cur.getString(1);
               // vPostGlobalTaxYN = cur.getString(6);
            }
           // Cursor cPrint=db.getSettingPrint();
            String query = "select * from tb_settingprinter";
            Cursor cPrint=db.getQuery(query);
            while (cPrint.moveToNext()) {
                TypePrinter     = cPrint.getString(1);
                NamePrinter     = cPrint.getString(2);
                IPPrinter       = cPrint.getString(3);
                vPort           = cPrint.getString(5);
                PaperSize       = cPrint.getString(6);
                String vCopies   = cPrint.getString(7);
                if(vCopies.length()>1) {
                    vCopies = vCopies.substring(0, 1);
                    Copies  = Integer.parseInt(vCopies);
                }
            }


            String querySet="select ServerName, UserName, Password," +
                    "DBName, Port, DBStatus, " +
                    "ItemConn,PostAs,ReceiptType," +
                    "DirectPrintYN, UserCode, CounterCode " +
                    "from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(0);
                UserName = curSet.getString(1);
                Password = curSet.getString(2);
                DBName = curSet.getString(3);
                Port = curSet.getString(4);
                DBStatus=curSet.getString(5);
                ItemConn=curSet.getString(6);
                PostAs=curSet.getString(7);
                ReceiptType=curSet.getString(8);
                DirectPrintYN=curSet.getString(9);
                UserCode=curSet.getString(10);
                CounterCode=curSet.getString(11);
            }
            if(PostAs.equals("0")){
                RetailYN="1";
                DocType="CS";
               // tbl_receipt="stk_receipt2";
            }else if(PostAs.equals("1")){
                RetailYN="0";
                DocType="CusInv";
               // tbl_receipt="stk_receipt";
            }
            String TermCode="1";
            String D_ay="0";
            //String SalesPersonCode="";
            String MembershipClass="";
            String vMember="select CusCode,CusName,TermCode,D_ay," +
                    "SalesPersonCode,MembershipClass,RatioPoint," +
                    "RatioAmount from tb_member ";
            Cursor rsMember=db.getQuery(vMember);
            Double dPercentagePoint=0.00;
            while(rsMember.moveToNext()){
                CusCode=rsMember.getString(0);
                CusName=rsMember.getString(1);
                TermCode=rsMember.getString(2);
                D_ay=rsMember.getString(3);
               // SalesPersonCode=rsMember.getString(4);
                MembershipClass=rsMember.getString(5);
                Double dRatioPoint=rsMember.getDouble(6);
                Double dRatioAmount=rsMember.getDouble(7);
                if(MembershipClass.length()>2){
                    dPercentagePoint=dRatioPoint/dRatioAmount;
                }
            }
            String qSales="select SalesPersonCode from tb_salesperson";
            Cursor rsSales=db.getQuery(qSales);
            while(rsSales.moveToNext()){
                SalesPersonCode=rsSales.getString(0);
            }
            String qOtherSet="select ServiceCharges, ReceiptHeader from tb_othersetting";
            Cursor rsOther=db.getQuery(qOtherSet);
            while(rsOther.moveToNext()){
                dServiceCharges =rsOther.getDouble(0);
                ReceiptHeader   =rsOther.getString(1);
            }

            String qCloudHd="select Doc2No, Doc3No from cloud_cus_inv_hd";
            Cursor rsCloudHd=db.getQuery(qCloudHd);
            while(rsCloudHd.moveToNext()){
                Doc2No          =rsCloudHd.getString(0);
                Doc3No          =rsCloudHd.getString(1);
            }
            if (vPostGlobalTaxYN.equals("1")) {
                String vDefault = "SELECT a.RetailTaxCode,b.R_ate,b.TaxType,b.TaxCode FROM sys_general_setup3 a INNER JOIN stk_tax b ON a.RetailTaxCode=b.TaxCode";
                Cursor rsDef = db.getQuery(vDefault);
                while (rsDef.moveToNext()) {
                    GlobalTaxCode = rsDef.getString(0);
                    R_ate = Double.parseDouble(rsDef.getString(1));
                    TaxType = rsDef.getString(2);
                }
            } else {
                TaxType = "0";
                R_ate = 0.00;
            }


            FnCalculateHCNetAmt vNetAmt = fncalculatehcnetamt(c, UserCode, GlobalTaxCode, TaxType, R_ate);
            HCGbTax = vNetAmt.getHCGbTax();
            TotalAmt = vNetAmt.getHCNetAmt();
            GbTaxRate1 = vNetAmt.getGbTaxRate1();
            HCDtTax = vNetAmt.getHCDtTax();
            HCGbDiscount = vNetAmt.getHCGbDiscount();
            TotalPointI=vNetAmt.getTotalPoint();
            CC2Amt = 0.00;
            CC2Code = "";
            CC2No = "";

            String vCC1ChargesRate = "0";
            String vCC1ChargesAmt = "0";
            if (CC1Amt > 0 || !CC1No.equals("")) {
                Cursor cPayType = db.getRowPayType(CC1Code);
                while (cPayType.moveToNext()) {
                    String Charges1         = cPayType.getString(0);
                    String PaidByCompanyYN  = cPayType.getString(1);
                    PaymentType             = cPayType.getString(2);
                    Double CC1ChargesRate   = Double.parseDouble(Charges1);
                    Double CC1ChargesAmt    = CC1Amt * CC1ChargesRate / 100;
                    if (PaidByCompanyYN.equals("0")) {
                        CC1Amt = CC1Amt + CC1ChargesAmt;
                    }
                    vCC1ChargesAmt = CC1ChargesAmt.toString();
                    vCC1ChargesRate = CC1ChargesRate.toString();
                }
            }else{
                Double RoundingAmt  = Rounding.setRound(c, TotalAmt);
                TotalAmt            = RoundingAmt;
            }
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    String vDuplicate="select count(*) as jumlah from stk_cus_inv_hd where Doc1No='"+Doc1No+"' ";
                    Statement stmtDup = conn.createStatement();
                    stmtDup.execute(vDuplicate);
                    ResultSet rsDup = stmtDup.getResultSet();
                    int vnumrows=0;
                    while(rsDup.next()) {
                        vnumrows=rsDup.getInt(1);
                    }
                    String vCheckSO = "select SORunNo,Doc1No from cloud_cus_inv_dt where ComputerName='" + UserCode + "' ";
                    Cursor rsSO = db.getQuery(vCheckSO);
                    while (rsSO.moveToNext()) {
                        String SORunNo = rsSO.getString(0);
                        Doc1NoSO = rsSO.getString(1);
                        if (!SORunNo.equals("0")) {
                            String vUpHdSO = "Update stk_sales_order_hd  set Status='Used'  where Doc1No='" + Doc1NoSO + "' ";
                            db.addQuery(vUpHdSO);
                        }
                    }
                    TableNo="0";
                    String checkTable="select Doc2No from stk_sales_order_hd where Doc1No='"+Doc1NoSO+"' ";
                    Cursor rsTb=db.getQuery(checkTable);
                    while(rsTb.moveToNext()){
                        TableNo=rsTb.getString(0);
                    }
                    if(vnumrows>0){
                        fnsavelastno(c);
                        z="error";
                    }else {
                        String vHeader = "INSERT INTO stk_cus_inv_hd(" +
                                " Doc1No, Doc2No, Doc3No," +
                                " D_ate, D_ateTime,CusCode," +
                                " DueDate, TaxDate, CurCode, " +
                                " CurRate1, CurRate2, CurRate3," +
                                " TermCode, D_ay, Attention," +
                                " GbDisRate1, GbDisRate2, GbDisRate3," +
                                " HCGbDiscount, GbTaxRate1, GbTaxRate2," +
                                " GbTaxRate3, HCGbTax, GlobalTaxCode," +
                                " HCDtTax, HCNetAmt, AdjAmt, " +
                                " GbOCCode, GbOCRate, GbOCAmt," +
                                " DocType, ApprovedYN, RetailYN," +
                                " UDRunNo, L_ink, Status," +
                                " Status2 )Values (" +
                                " '" + Doc1No + "', '"+Doc2No+"', '"+Doc3No+"', " +
                                " '" + datedShort + "', '" + datedTime + "', '" + CusCode + "', " +
                                " '" + datedShort + "', '" + datedShort + "', '"+ CurCode + "', " +
                                " '1', '1', '1', " +
                                " '"+TermCode+"', '"+D_ay+"', 'android pos', " +
                                " '0', '0', '0'," +
                                " '0', '0', '0'," +
                                " '0', '0', '" + GlobalTaxCode + "', " +
                                " '" + HCDtTax + "', '" + TotalAmt + "', '"+AdjAmt+"'," +
                                " '', '0', '0'," +
                                " '"+DocType+"', '1', '"+RetailYN+"'," +
                                " '0', '1', 'Used'," +
                                " '')";
                        Statement stmtHeader = conn.createStatement();

                        stmtHeader.execute(vHeader);

                        String vReceipt = "INSERT INTO stk_receipt2 (" +
                                " D_ate, T_ime, D_ateTime," +
                                " Doc1No, CashAmt, CC1Code, " +
                                " CC1Amt, CC1No, CC1Expiry, " +
                                " CC1ChargesAmt, CC1ChargesRate,CC2Code," +
                                " CC2Amt, CC2No, CC2Expiry," +
                                " CC2ChargesAmt, CC2ChargesRate, Cheque1Code," +
                                " Cheque1Amt, Cheque1No, Cheque2Code," +
                                " Cheque2Amt, Cheque2No, PointAmt, " +
                                " VoucherAmt, CurCode, CurRate, " +
                                " FCAmt, CusCode, BalanceAmount, " +
                                " ChangeAmt, CounterCode, UserCode, " +
                                " DocType, CurRate1 )" +
                                " VALUES (" +
                                " '" + datedShort + "', '" + vTime + "', '" + datedTime + "', " +
                                " '" + Doc1No + "', '" + CashAmt + "', '" + CC1Code + "', " +
                                " '" + CC1Amt + "', '" + CC1No + "', ''," +
                                " '"+vCC1ChargesAmt+"', '"+vCC1ChargesRate+"', '" + CC2Code + "'," +
                                " '" + CC2Amt + "', '" + CC2No + "', '', " +
                                " '0', '0', '', " +
                                " '0', '', '', " +
                                " '0', '', '0', " +
                                " '0', '" + CurCode + "', '1', " +
                                " '0', '" + CusCode + "', '" + BalanceAmount + "', " +
                                " '" + ChangeAmt + "', '"+CounterCode+"', '"+UserCode+"', " +
                                " 'CS', '1' )";
                        Statement stmtReceipt = conn.createStatement();
                        if(PostAs.equals("0")){
                            stmtReceipt.execute(vReceipt);
                        }
                        String QueryD="select RunNo, '"+Doc1No+"', N_o, " +
                                     " ItemCode, Description, Qty," +
                                     " FactorQty, UOM, UOMSingular," +
                                     " HCUnitCost, DisRate1, HCDiscount," +
                                     " TaxRate1, HCTax, DetailTaxCode," +
                                     " HCLineAmt, BranchCode, DepartmentCode," +
                                     " ProjectCode, SalesPersonCode, LocationCode," +
                                     " WarrantyDate, LineNo, BlankLine," +
                                     " '"+DocType+"', AnalysisCode2, DUD6 " +
                                     " from cloud_cus_inv_dt where ComputerName='"+UserCode+"' ";
                        Cursor rsDt=db.getQuery(QueryD);
                        int i=1;
                        while(rsDt.moveToNext()){
                            String vDetail="insert into stk_cus_inv_dt(" +
                                    "Doc1No, N_o, ItemCode," +
                                    "Description, Qty, FactorQty," +
                                    "UOM, UOMSingular, HCUnitCost," +
                                    "DisRate1, HCDiscount, TaxRate1," +
                                    "HCTax, DetailTaxCode, HCLineAmt," +
                                    "BranchCode, DepartmentCode, ProjectCode," +
                                    "SalesPersonCode, LocationCode, WarrantyDate," +
                                    "LineNo, BlankLine, DocType," +
                                    "AnalysisCode2, DUD6)values(" +
                                    "'"+rsDt.getString(1)+"' , '"+rsDt.getString(2)+"' , '"+rsDt.getString(3)+"', " +
                                    "'"+DecodeChar.setChar(c,rsDt.getString(4).replaceAll("'",""))+"' , '"+rsDt.getString(5)+"' , '"+rsDt.getString(6)+"', " +
                                    "'"+DecodeChar.setChar(c,rsDt.getString(7))+"' , '"+DecodeChar.setChar(c,rsDt.getString(8))+"' , '"+rsDt.getString(9)+"', " +
                                    "'"+rsDt.getString(10)+"' , '"+rsDt.getString(11)+"' , '"+rsDt.getString(12)+"', " +
                                    "'"+rsDt.getString(13)+"' , '"+rsDt.getString(14)+"' , '"+rsDt.getString(15)+"', " +
                                    "'"+rsDt.getString(16)+"' , '"+rsDt.getString(17)+"' , '"+rsDt.getString(18)+"', " +
                                    "'"+SalesPersonCode+"' , '"+rsDt.getString(20)+"' , '"+datedShort+"', " +
                                    "'"+i+"' , '"+rsDt.getString(23)+"' , '"+DocType+"', " +
                                    "'"+TableNo+"', '"+rsDt.getString(26)+"' )";
                            Statement stmtDt = conn.createStatement();

                            stmtDt.execute(vDetail);
                            String vDetailOut="insert into stk_detail_trn_out(" +
                                    "ItemCode, Doc3No, D_ate," +
                                    "QtyOUT, FactorQty, UOM," +
                                    "UnitPrice, CusCode, DocType3," +
                                    "Doc3NoRunNo, LocationCode, L_ink," +
                                    "HCTax, BookDate)values(" +
                                    "'"+rsDt.getString(3)+"', '"+rsDt.getString(1)+"', '"+datedShort+"'," +
                                    "'"+rsDt.getString(5)+"', '"+rsDt.getString(6)+"', '"+rsDt.getString(7)+"'," +
                                    "'"+rsDt.getString(9)+"', '"+CusCode+"', '"+DocType+"'," +
                                    "'"+rsDt.getString(0)+"', '"+rsDt.getString(20)+"', '1'," +
                                    "'"+rsDt.getString(13)+"', '"+datedShort+"')";
                            Statement stmtDt2 = conn.createStatement();
                            Log.d("DETAIL",vDetailOut);
                            stmtDt2.execute(vDetailOut);
                            i++;
                        }

                        String vCheckSO1="select SORunNo, Doc1No from cloud_cus_inv_dt where ComputerName= '"+UserCode+"' ";
                        Cursor rsSO1=db.getQuery(vCheckSO1);
                        while(rsSO1.moveToNext()){
                            String SORunNo=rsSO1.getString(0);
                            String Doc1NoSO1=rsSO1.getString(1);
                            if(!SORunNo.equals("0")){
                                String vUpdateSO="update stk_sales_order_hd set Status='Used' where Doc1No='"+Doc1NoSO1+"' ";
                                Statement stmtUp=conn.createStatement();
                                stmtUp.execute(vUpdateSO);
                            }
                        }

                        if(TotalPointI>0) {
                            String qPoint = "insert into ret_pointadjustment(cuscode," +
                                    " D_ate, Point, DocType," +
                                    " Remark, Screen, D_ateTime)values('" + CusCode + "'," +
                                    " '" + datedShort + "', '" + TotalPointI + "', 'Increase'," +
                                    " '" + Doc1No + "', '', '"+datedTime+"')";
                            Statement stmtIn = conn.createStatement();
                            stmtIn.execute(qPoint);
                        }else if(MembershipClass.length()>2){
                           /*Double dPoint = TotalAmt * dPercentagePoint;
                           String qPoint = "insert into ret_pointadjustment(cuscode," +
                                        " D_ate, Point, DocType," +
                                        " Remark, Screen, D_ateTime)values('" + CusCode + "'," +
                                        " '" + datedShort + "', '" + dPoint + "', 'Increase'," +
                                        " '" + Doc1No + "', '', '"+datedTime+"')";
                           Statement stmtIn = conn.createStatement();
                           stmtIn.execute(qPoint);*/
                        }

                        if(CC1Code.equals("NFC01")){
                            String qPointAdj = "insert into ret_pointadjustment(cuscode," +
                                    " D_ate, Point, DocType," +
                                    " Remark, Screen, D_ateTime)values('" + CusCode + "'," +
                                    " '" + datedShort + "', '-" + CC1Amt + "', 'Decrease'," +
                                    " '" + Doc1No + "', '', '"+datedTime+"')";
                            Statement stmtPointAdj = conn.createStatement();
                            stmtPointAdj.execute(qPointAdj);
                        }
                    }
                }
            }

            String vDel     = "delete from cloud_cus_inv_dt";
            String vDel4    = "delete from cloud_cus_inv_hd";
            String vDel2    = "delete from tb_member";
            String vDel3    = "delete from tb_salesperson";
            if(DBStatus.equals("2")){
                String vDuplicate = "select Doc1No from stk_cus_inv_hd where Doc1No='" + Doc1No + "' ";
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vDuplicate);
                jsonReq.put("action", "select");
                String resCheck = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(resCheck);
                String rsCheck = jsonRes.getString("hasil");
                String DupTable = "";
                TableNo = "0";
                if(!rsCheck.equals("0")) {
                    JSONArray rsData = new JSONArray(rsCheck);
                    JSONObject vData = null;
                    for (int i = 0; i < rsData.length(); i++) {
                        vData = rsData.getJSONObject(i);
                        DupTable = vData.getString("Doc1No");
                    }
                }
                if(DupTable.equals(Doc1No)){
                    fnsavelastno(c);
                    z = "error";
                }else{
                    String vHeader = "INSERT INTO stk_cus_inv_hd(" +
                            " Doc1No, Doc2No, Doc3No," +
                            " D_ate, D_ateTime,CusCode," +
                            " DueDate, TaxDate, CurCode, " +
                            " CurRate1, CurRate2, CurRate3," +
                            " TermCode, D_ay, Attention," +
                            " GbDisRate1, GbDisRate2, GbDisRate3," +
                            " HCGbDiscount, GbTaxRate1, GbTaxRate2," +
                            " GbTaxRate3, HCGbTax, GlobalTaxCode," +
                            " HCDtTax, HCNetAmt, AdjAmt, " +
                            " GbOCCode, GbOCRate, GbOCAmt, " +
                            " DocType, ApprovedYN, RetailYN, " +
                            " UDRunNo, L_ink, Status, " +
                            " Status2, SynYN)Values (" +
                            " '" + Doc1No + "', '"+Doc2No+"', '"+Doc3No+"', " +
                            " '" + datedShort + "', '" + datedTime + "', '" + CusCode + "', " +
                            " '" + datedShort + "', '" + datedShort + "', '" + CurCode + "', " +
                            " '1', '1', '1', " +
                            " '" + TermCode + "', '" + D_ay + "', 'android pos', " +
                            " '0', '0', '0'," +
                            " '0', '0', '0'," +
                            " '0', '0', '" + GlobalTaxCode + "'," +
                            " '" + HCDtTax + "', '" + TotalAmt + "', '"+AdjAmt+"'," +
                            " '', '0', '0'," +
                            " '"+DocType+"','1', '"+RetailYN+"'," +
                            " '0', '1', 'Used'," +
                            " '', '0')";

                    String vReceipt = "INSERT INTO stk_receipt2 (" +
                            " D_ate, T_ime, D_ateTime," +
                            " Doc1No, CashAmt, CC1Code, " +
                            " CC1Amt, CC1No, CC1Expiry, " +
                            " CC1ChargesAmt, CC1ChargesRate,CC2Code," +
                            " CC2Amt, CC2No, CC2Expiry," +
                            " CC2ChargesAmt, CC2ChargesRate, Cheque1Code," +
                            " Cheque1Amt, Cheque1No, Cheque2Code," +
                            " Cheque2Amt, Cheque2No, PointAmt, " +
                            " VoucherAmt, CurCode, CurRate, " +
                            " FCAmt, CusCode, BalanceAmount, " +
                            " ChangeAmt, CounterCode, UserCode, " +
                            " DocType, SynYN, VoidYN," +
                            " CurRate1)" +
                            " VALUES (" +
                            " '" + datedShort + "', '" + vTime + "', '" + datedTime + "', " +
                            " '" + Doc1No + "', '" + CashAmt + "', '" + CC1Code + "', " +
                            " '" + CC1Amt + "', '" + CC1No + "', ''," +
                            " '" + vCC1ChargesAmt + "', '" + vCC1ChargesRate + "', '" + CC2Code + "'," +
                            " '" + CC2Amt + "', '" + CC2No + "', '', " +
                            " '0', '0', '', " +
                            " '0', '', '', " +
                            " '0', '', '0', " +
                            " '0', '" + CurCode + "', '1', " +
                            " '0', '" + CusCode + "', '" + BalanceAmount + "', " +
                            " '" + ChangeAmt + "', '" + CounterCode + "', '" + UserCode + "', " +
                            " '"+DocType+"', '0', '0'," +
                            " '1' )";

                    String vdetailOut = "insert into stk_detail_trn_out(ItemCode, Doc3No, D_ate," +
                            " QtyOUT, FactorQty, UOM," +
                            " UnitPrice, CusCode, DocType3," +
                            " Doc3NoRunNo, LocationCode, L_ink," +
                            " HCTax, BookDate, SynYN)" +
                            " SELECT ItemCode, Doc1No, '" + datedShort + "'," +
                            " Qty, FactorQty, UOMSingular," +
                            " HCUnitCost, '" + CusCode + "', DocType," +
                            " RunNo, LocationCode, '1', " +
                            " HCTax,'" + datedShort + "', '0' " +
                            " from stk_cus_inv_dt where Doc1No='" + Doc1No + "'  ";
                    //insert header
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", vHeader);
                    jsonReq.put("action", "insert");
                    String rsInsertHd = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsInsertHd);
                    String rsHd = jsonRes.getString("hasil");
                    Log.d("QUERY", vHeader+rsHd);

                    //insert receipt2
                    if(PostAs.equals("0")) {
                        jsonReq.put("request", "request-connect-client");
                        jsonReq.put("query", vReceipt);
                        jsonReq.put("action", "insert");
                        String rsReceipt = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                        jsonRes = new JSONObject(rsReceipt);
                        String rsRec = jsonRes.getString("hasil");
                        Log.d("QUERY", vReceipt + rsRec);
                    }

                    String vCheckSO = "select SORunNo, Doc1No " +
                            " from cloud_cus_inv_dt where ComputerName='" + UserCode + "' ";
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", vCheckSO);
                    jsonReq.put("action", "select");
                    String rsSO = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsSO);
                    String rsHasilSO = jsonRes.getString("hasil");
                    if(!rsHasilSO.equals("0")) {
                        JSONArray jaData = new JSONArray(rsHasilSO);
                        JSONObject vData = null;
                        String SORunNo = "";
                        for (int i = 0; i < jaData.length(); i++) {
                            vData = jaData.getJSONObject(i);
                            Doc1NoSO = vData.getString("Doc1No");
                            SORunNo = vData.getString("SORunNo");
                        }
                        if (!SORunNo.equals("0")) {
                            String vUpHdSO = "Update stk_sales_order_hd  set Status='Used'  where Doc1No='" + Doc1NoSO + "' ";
                            jsonReq.put("request", "request-connect-client");
                            jsonReq.put("query", vUpHdSO);
                            jsonReq.put("action", "update");
                            String rsUp= connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                            jsonRes = new JSONObject(rsUp);
                            String rsUpdate = jsonRes.getString("hasil");
                        }

                        String checkTable = "select Doc2No from stk_sales_order_hd where Doc1No='" + Doc1NoSO + "' ";
                        jsonReq.put("request", "request-connect-client");
                        jsonReq.put("query", checkTable);
                        jsonReq.put("action", "select");
                        String resTable = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                        jsonRes = new JSONObject(resTable);
                        String rsTable = jsonRes.getString("hasil");
                        if(!rsTable.equals("0")) {
                            JSONArray jaTable = new JSONArray(rsTable);
                            JSONObject vTable = null;
                            for (int i = 0; i < jaTable.length(); i++) {
                                vTable = jaTable.getJSONObject(i);
                                TableNo = vTable.getString("Doc2No");
                            }
                        }
                    }
                    //insert stk_cus_inv detail
                    String vDetail = "INSERT INTO stk_cus_inv_dt (Doc1No, N_o, ItemCode," +
                            " Description, Qty, FactorQty," +
                            " UOM, UOMSingular, HCUnitCost," +
                            " DisRate1, HCDiscount, TaxRate1, " +
                            " HCTax, DetailTaxCode, HCLineAmt, " +
                            " BranchCode, DepartmentCode, ProjectCode, " +
                            " SalesPersonCode, LocationCode, WarrantyDate," +
                            " LineNo, BlankLine, DocType, " +
                            " AnalysisCode2, SORunNo, SynYN," +
                            " AlternateItem, DUD6)" +
                            " SELECT '" + Doc1No + "', N_o, ItemCode, " +
                            " Description, Qty, FactorQty, " +
                            " UOM, UOMSingular, HCUnitCost, " +
                            " DisRate1, HCDiscount, TaxRate1," +
                            " HCTax, DetailTaxCode, HCLineAmt, " +
                            " BranchCode, DepartmentCode, ProjectCode, " +
                            " '"+SalesPersonCode+"', LocationCode, '" + datedShort + "' ," +
                            " LineNo, BlankLine, '"+DocType+"'," +
                            " AnalysisCode2, SORunNo,  '0'," +
                            " AlternateItem, DUD6 " +
                            " FROM cloud_cus_inv_dt WHERE ComputerName='" + UserCode + "' ";

                    Log.d("DETAIL",vDetail);
                    //insert detail
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", vDetail);
                    jsonReq.put("action", "insert");
                    String rsInsertDt = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsInsertDt);
                    String rsDt = jsonRes.getString("hasil");


                    //insert detail trn out
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", vdetailOut);
                    jsonReq.put("action", "insert");
                    String rsInsertDt2 = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsInsertDt2);
                    String rsDt2 = jsonRes.getString("hasil");

                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", vDel);
                    jsonReq.put("action", "delete");
                    String rsDel = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(rsDel);
                    String resultDel = jsonRes.getString("hasil");
                    z="printing";
                }
            }else {
                String vHeader = "INSERT INTO stk_cus_inv_hd(" +
                        " Doc1No, Doc2No, Doc3No," +
                        " D_ate, D_ateTime,CusCode," +
                        " DueDate, TaxDate, CurCode, " +
                        " CurRate1, CurRate2, CurRate3," +
                        " TermCode, D_ay, Attention," +
                        " GbDisRate1, GbDisRate2, GbDisRate3," +
                        " HCGbDiscount, GbTaxRate1, GbTaxRate2," +
                        " GbTaxRate3, HCGbTax, GlobalTaxCode," +
                        " HCDtTax, HCNetAmt, AdjAmt, " +
                        " GbOCCode, GbOCRate, GbOCAmt," +
                        " DocType, ApprovedYN, RetailYN," +
                        " UDRunNo, L_ink, Status," +
                        " Status2, SynYN )Values (" +
                        " '" + Doc1No + "', '"+Doc2No+"', '"+Doc3No+"', " +
                        " '" + datedShort + "', '" + datedTime + "', '" + CusCode + "', " +
                        " '" + datedShort + "', '" + datedShort + "', '" + CurCode + "', " +
                        " '1', '1', '1', " +
                        " '" + TermCode + "', '" + D_ay + "', 'android pos', " +
                        " '0', '0', '0'," +
                        " '0', '0', '0'," +
                        " '0', '0', '" + GlobalTaxCode + "'," +
                        " '" + HCDtTax + "', '" + TotalAmt + "', '"+AdjAmt+"'," +
                        " '', '0', '0'," +
                        " '"+DocType+"','1', '"+RetailYN+"'," +
                        " '0', '1', 'Used'," +
                        " '', '" + DBStatus + "')";

                String vReceipt = "INSERT INTO stk_receipt2 (" +
                        " D_ate, T_ime, D_ateTime," +
                        " Doc1No, CashAmt, CC1Code, " +
                        " CC1Amt, CC1No, CC1Expiry, " +
                        " CC1ChargesAmt, CC1ChargesRate,CC2Code," +
                        " CC2Amt, CC2No, CC2Expiry," +
                        " CC2ChargesAmt, CC2ChargesRate, Cheque1Code," +
                        " Cheque1Amt, Cheque1No, Cheque2Code," +
                        " Cheque2Amt, Cheque2No, PointAmt, " +
                        " VoucherAmt, CurCode, CurRate, " +
                        " FCAmt, CusCode, BalanceAmount, " +
                        " ChangeAmt, CounterCode, UserCode, " +
                        " DocType, SynYN, VoidYN," +
                        " CurRate1)" +
                        " VALUES (" +
                        " '" + datedShort + "', '" + vTime + "', '" + datedTime + "', " +
                        " '" + Doc1No + "', '" + CashAmt + "', '" + CC1Code + "', " +
                        " '" + CC1Amt + "', '" + CC1No + "', ''," +
                        " '" + vCC1ChargesAmt + "', '" + vCC1ChargesRate + "', '" + CC2Code + "'," +
                        " '" + CC2Amt + "', '" + CC2No + "', '', " +
                        " '0', '0', '', " +
                        " '0', '', '', " +
                        " '0', '', '0', " +
                        " '0', '" + CurCode + "', '1', " +
                        " '0', '" + CusCode + "', '" + BalanceAmount + "', " +
                        " '" + ChangeAmt + "', '" + CounterCode + "', '" + UserCode + "', " +
                        " 'CS', '" + DBStatus + "', '0', " +
                        " '1' )";
                String vdetailOut = "insert into stk_detail_trn_out(ItemCode, Doc3No, D_ate," +
                        " QtyOUT, FactorQty, UOM, " +
                        " UnitPrice, CusCode, DocType3," +
                        " Doc3NoRunNo, LocationCode, L_ink," +
                        " HCTax, BookDate, SynYN) " +
                        " SELECT ItemCode, Doc1No,'" + datedShort + "'," +
                        " Qty, FactorQty, UOMSingular, " +
                        " HCUnitCost, '" + CusCode + "', '"+DocType+"', " +
                        " RunNo, LocationCode, '1', " +
                        " HCTax, '" + datedShort + "', '" + DBStatus + "' " +
                        " from stk_cus_inv_dt where Doc1No='" + Doc1No + "'  ";

                int vnumrows=0;
                String vDuplicate = "select count(*) as jumlah from stk_cus_inv_hd where Doc1No='" + Doc1No + "' ";
                Cursor rsDup = db.getQuery(vDuplicate);
                while (rsDup.moveToNext()) {
                    vnumrows = rsDup.getInt(0);
                }
                String vCheckSO = "select SORunNo,Doc1No from cloud_cus_inv_dt where ComputerName='" + UserCode + "' ";
                Cursor rsSO = db.getQuery(vCheckSO);
                while (rsSO.moveToNext()) {
                    String SORunNo = rsSO.getString(0);
                    Doc1NoSO = rsSO.getString(1);
                    if (!SORunNo.equals("0")) {
                        String vUpHdSO = "Update stk_sales_order_hd  set Status='Used'  where Doc1No='" + Doc1NoSO + "' ";
                        db.addQuery(vUpHdSO);
                    }
                }
                TableNo="0";
                String checkTable="select Doc2No from stk_sales_order_hd where Doc1No='"+Doc1NoSO+"' ";
                Cursor rsTb=db.getQuery(checkTable);
                while(rsTb.moveToNext()){
                    TableNo=rsTb.getString(0);
                }
                if (vnumrows>0) {
                    fnsavelastno(c);
                    z = "error";
                } else {
                    //insert hd
                    db.updateQuery(vHeader);
                    //insert stk_receipt2
                    if(PostAs.equals("0")) {
                        db.updateQuery(vReceipt);
                    }
                    //insert stk_cus_inv detail
                    String vDetail = "INSERT INTO stk_cus_inv_dt (Doc1No, N_o, ItemCode," +
                            " Description, Qty, FactorQty," +
                            " UOM, UOMSingular, HCUnitCost," +
                            " DisRate1, HCDiscount, TaxRate1, " +
                            " HCTax, DetailTaxCode, HCLineAmt, " +
                            " BranchCode, DepartmentCode, ProjectCode, " +
                            " SalesPersonCode, LocationCode, WarrantyDate," +
                            " LineNo, BlankLine, DocType, " +
                            " AnalysisCode2, SORunNo, SynYN, " +
                            " AlternateItem, DUD6 )" +
                            " SELECT '" + Doc1No + "', N_o, ItemCode, " +
                            " Description, Qty, FactorQty, " +
                            " UOM, UOMSingular, HCUnitCost, " +
                            " DisRate1, HCDiscount, TaxRate1," +
                            " HCTax, DetailTaxCode, HCLineAmt, " +
                            " BranchCode, DepartmentCode, ProjectCode, " +
                            " '"+SalesPersonCode+"', LocationCode, '" + datedShort + "' ," +
                            " LineNo, BlankLine, '"+DocType+"'," +
                            " AnalysisCode2, SORunNo,  '" + DBStatus + "', " +
                            " AlternateItem, DUD6 " +
                            " FROM cloud_cus_inv_dt WHERE ComputerName='" + UserCode + "' ";
                    //insert dt
                    long addDetail = db.addQuery(vDetail);
                    if (addDetail > 0) {
                        Log.d("SUCCESS DETAIL", vDetail);
                    } else {
                        Log.d("ERROR DETAIL", "failed");
                    }
                    if(TotalPointI>0) {
                        String qPoint="insert into ret_pointadjustment(cuscode," +
                                " D_ate, Point, DocType," +
                                " Remark, Screen, SynYN," +
                                " D_ateTime )values('"+CusCode+"'," +
                                " '"+datedShort+"', '"+TotalPointI+"', 'Increase'," +
                                " '"+Doc1No+"', '', '"+DBStatus+"'," +
                                " '"+datedTime+"')";
                        db.addQuery(qPoint);
                    }else if(MembershipClass.length()>2){
                        /*Double dPoint=TotalAmt*dPercentagePoint;
                        String qPoint="insert into ret_pointadjustment(cuscode," +
                                " D_ate, Point, DocType," +
                                " Remark, Screen, SynYN," +
                                " D_ateTime)values('"+CusCode+"'," +
                                " '"+datedShort+"', '"+dPoint+"', 'Increase'," +
                                " '"+Doc1No+"', '', '"+DBStatus+"'," +
                                " '"+datedTime+"')";
                        db.addQuery(qPoint);*/
                    }

                    if(CC1Code.equals("NFC01")){
                        String qPointDec = "insert into ret_pointadjustment(cuscode," +
                                " D_ate, Point, DocType," +
                                " Remark, Screen, SynYN)values('" + CusCode + "'," +
                                " '" + datedShort + "', '" + CC1Amt + "', 'Decrease'," +
                                " '" + Doc1No + "', '', '" + DBStatus + "')";
                        db.addQuery(qPointDec);
                    }

                    //insert stk_detail_trn_out
                    db.addQuery(vdetailOut);
                    db.addQuery(vDel);
                    db.addQuery(vDel2);
                    db.addQuery(vDel3);
                    z="printing";
                }
            }
            String qDel="delete from dum_stk_sales_order_hd";
            db.addQuery(qDel);
            db.addQuery(vDel4);
            if(z.equals("printing") && PrintYN.equals("1")) {
                fnsavelastno(c);
                Log.d("RECEIPT TYPE",ReceiptType);
                NewDoc = "xxxxxx";
                if(PaperSize.equals("78mm") && !ReceiptType.equals("Format01")){
                    fngenerate78(Doc1No);
                }else if(PaperSize.equals("58mm") && !ReceiptType.equals("Format01")){
                    fngenerate58(Doc1No);
                }else if(PaperSize.equals("78mm") && ReceiptType.equals("Format01")){
                    receipt78();
                }else if(PaperSize.equals("58mm") && ReceiptType.equals("Format01")){
                    receipt58();
                }
               /* if ((TypePrinter.equals("AIDL") || TypePrinter.equals("Ipos AIDL"))&& ReceiptType.equals("Normal")) {
                    if(tModel.equals("T1")) {
                        fngenerate78(Doc1No);
                    }else{
                        fngenerate58(Doc1No);

                    }
                } else if((!TypePrinter.equals("AIDL") || !TypePrinter.equals("Ipos AIDL")) && ReceiptType.equals("Normal")) {
                    fngenerate78(Doc1No);
                }else if((TypePrinter.equals("AIDL") || TypePrinter.equals("Ipos AIDL")) && ReceiptType.equals("Customize")) {
                    receipt58();
                }else if((!TypePrinter.equals("AIDL") || !TypePrinter.equals("Ipos AIDL")) && ReceiptType.equals("Customize")) {
                    receipt78();
                }*/
                //Log.d("STR PRINT",stringPrint);
                if (TypePrinter.equals("AIDL") && DirectPrintYN.equals("1")) {
                    int i = 0;
                    while (i < Copies) {
                        if (!imgHeader.isEmpty()) {
                            AidlUtil.getInstance().printBitmap(bmpHeader);
                        }
                        AidlUtil.getInstance().printText(stringPrint);
                        if (tModel.equals("T1")) {
                            AidlUtil.getInstance().cutPapers();
                        }
                        i++;
                    }
                }else if(TypePrinter.equals("Ipos AIDL") && DirectPrintYN.equals("1")){
                    int i = 0;
                    while (i < Copies) {
                        IposAidlUtil.getInstance().setPrint(stringPrint);
                        i++;
                    }
                } else if (TypePrinter.equals("Bluetooth")&& DirectPrintYN.equals("1")) {
                    BluetoothPrinter fncheck = new BluetoothPrinter();
                    for (int i = 0; i < Copies; i++) {
                        int count = 1;
                        while (count < 2) {
                            isBT = fncheck.fnBluetooth(c, NamePrinter, stringPrint);
                            if (isBT == false) {
                                NewDoc = "error print";
                            }
                            ++count;
                            Thread.sleep(2200);
                        }
                    }
                } else if (TypePrinter.equals("Bluetooth Zebra")&& DirectPrintYN.equals("1")) {
                    BluetoothZebra fncheck = new BluetoothZebra();
                    for (int i = 0; i < Copies; i++) {
                        int count = 1;
                        while (count < 2) {
                            isBT = fncheck.fnBluetooth(c, NamePrinter, stringPrint);
                            if (isBT == false) {
                                NewDoc = "error print";
                            }
                            ++count;
                            Thread.sleep(2200);
                        }
                    }
                } else if (TypePrinter.equals("Wifi")&& DirectPrintYN.equals("1")) {
                    int Port = Integer.parseInt(vPort);
                    PrintingWifi fnprintw = new PrintingWifi();
                    for (int i = 0; i < Copies; i++) {
                        int count = 1;
                        while (count < 2) {
                            isBT = fnprintw.fnprintwifi(c, IPPrinter, Port, stringPrint);
                            if (isBT == false) {
                                NewDoc = "error print";
                            }
                            ++count;
                            Thread.sleep(2200);
                        }
                    }
                } else if (TypePrinter.equals("USB")&& DirectPrintYN.equals("1")) {
                    PrintingUSB fnprintu = new PrintingUSB();
                    for (int i = 0; i < Copies; i++) {
                        int count = 1;
                        while (count < 2) {
                            isBT = fnprintu.fnprintusb(c, stringPrint);
                            if (isBT == false) {
                                NewDoc = "error print";
                            }
                            ++count;
                            Thread.sleep(2200);
                        }
                    }
                } else {
                    //No Printer
                }
                z="success";
            }else{
                z="success";
            }

            db.closeDB();
            return z;
        }catch (SQLException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return z;
    }

    public static FnCalculateHCNetAmt fncalculatehcnetamt(Context c, String UUID,String vGlobalTaxCode, String TaxType, Double R_ate){
        Double vHCTax,HCGbDiscount,vHCLineAmt,HCDtTax,HCGbTax,HCNetAmt,GbTaxRate1,AmountB4Tax;
        vHCLineAmt      =0.00;
        vHCTax          =0.00;
        HCGbDiscount    =0.00;
        HCDtTax         =0.00;
        HCGbTax         =0.00;
        HCNetAmt        =0.00;
        GbTaxRate1      =0.00;
        Double TotalPoint =0.00;
        String IPAddress="";
        String DBStatus ="";
        try {
            JSONObject jsonReq,jsonRes;
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String querySet="select * from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(1);
                DBStatus=curSet.getString(7);
            }
            String strSQL = "SELECT  IFNULL(sum(HCTax),0) as vHCTax, IFNULL(sum(HCLineAmt),0) as vHCLineAmt," +
                    " IFNULL(sum(HCDiscount),0) as HCGbDiscount, IFNULL(sum(Qty*Point),0)as TotalPoint" +
                    " FROM cloud_cus_inv_dt" +
                    " WHERE ComputerName = '" + UUID + "' GROUP BY '' ";

            if(DBStatus.equals("2")) {
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", strSQL);
                jsonReq.put("action", "select");
                String response1 = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response1);
                String response = jsonRes.getString("hasil");
                JSONArray rsData = new JSONArray(response);
                JSONObject vData = null;
                for (int i = 0; i < rsData.length(); i++) {
                    vData           = rsData.getJSONObject(i);
                    vHCTax          = vData.getDouble("vHCTax");
                    vHCLineAmt      = vData.getDouble("vHCLineAmt");
                    HCGbDiscount    = vData.getDouble("HCGbDiscount");
                    TotalPoint      = vData.getDouble("TotalPoint");
                }

            }else{
                Cursor rsNetAmt = db.getQuery(strSQL);
                while (rsNetAmt.moveToNext()) {
                    vHCTax = rsNetAmt.getDouble(0);
                    vHCLineAmt = rsNetAmt.getDouble(1);
                    HCGbDiscount = rsNetAmt.getDouble(2);
                    TotalPoint = rsNetAmt.getDouble(3);
                }
            }

            if (vGlobalTaxCode.equals("")) {
                HCDtTax = vHCTax;
                HCGbTax = 0.00;
                HCNetAmt = vHCLineAmt;
                GbTaxRate1 = 0.00;
            } else {
                AmountB4Tax = vHCLineAmt;
                HCDtTax = 0.00;
                GbTaxRate1 = R_ate;
                //0,2 is inclusive
                if (TaxType.equals("0") || TaxType.equals("2")) {
                    HCGbTax = AmountB4Tax * (GbTaxRate1 / (GbTaxRate1 + 100));
                    HCNetAmt = AmountB4Tax;
                } else if (TaxType.equals("1") || TaxType.equals("3")) {
                    HCGbTax = AmountB4Tax * (GbTaxRate1 / 100);
                    HCNetAmt = AmountB4Tax + HCGbTax;
                }
            }
            return new FnCalculateHCNetAmt(HCNetAmt, HCDtTax, HCGbTax, HCGbDiscount, GbTaxRate1, vGlobalTaxCode, TotalPoint);
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return new FnCalculateHCNetAmt(HCNetAmt, HCDtTax, HCGbTax, HCGbDiscount, GbTaxRate1, vGlobalTaxCode, TotalPoint);
    }
    public void fnsavelastno(Context c){
        String RunNo="";
        String queryLast="Select RunNo,LastNo from sys_runno_dt where RunNoCode='CS' ";
        DBAdapter dbAdapter  = new DBAdapter(c);
        dbAdapter.openDB();
        Cursor rsLast   =dbAdapter.getQuery(queryLast);
        while (rsLast.moveToNext()) {
            RunNo   = rsLast.getString(0);
            LastNo  = "1"+rsLast.getString(1);
            NewNo   = (Integer.parseInt(LastNo)) + 1;
        }
        NewDoc = String.valueOf(NewNo);
        String vNewDoc = NewDoc.substring(1,NewDoc.length());
        ContentValues cv=new ContentValues();
        cv.put("LastNo",vNewDoc);
        dbAdapter.UpdateSysRunNo(cv,RunNo);
    }

    private void receipt58(){
        try{
            String strPrint="";
            String vLine1="";
            String vLine2="";
            String vLine3="";
            String vLine4="";
            String vLine5="";
            String vLine5_1="";
            String vLine6="";
            String vLine7="";
            String vLine7_1 ="";
            String vLine7_2 ="";
            String vLine8   ="";
            String vLine8_1 ="";
            String vLine8_2 ="";
            String vLine9   ="";
            String vLine9_1 ="";
            String vLine9_2 ="";
            String vLine10   ="";
            String vLine10_1 ="";
            String vLine11 ="";
            String vLine12 ="";



            DBAdapter db=new DBAdapter(c);
            db.openDB();
            vLine1 = str_pad("RESIT RESMI", 32, " ", "STR_PAD_BOTH");
            vLine2 = str_pad("No: "+Doc1No, 32, " ", "STR_PAD_BOTH");

            String qCom="select CurCode,CompanyName,CompanyCode," +
                    "GSTNo,Address,Tel1," +
                    "Fax1,CompanyEmail,ComTown," +
                    "ComState,ComCountry FROM companysetup ";
            Cursor rsCom=db.getQuery(qCom);
            String vAddress = "";
            while(rsCom.moveToNext()){
                vLine3      = str_pad(rsCom.getString(1), 32, " ", "STR_PAD_BOTH");
                vLine4      = str_pad(rsCom.getString(5), 32, " ", "STR_PAD_BOTH");
                vAddress    = rsCom.getString(4).trim();
            }

            String AddressAll = vAddress.replaceAll("(.{30})", "$1\n");
            String[] address = AddressAll.split(";\n");
            for (String add : address) {
                vAddress += str_pad(add, 31, " ", "STR_PAD_BOTH");

            }


            vLine5 = str_pad("SELAMAT DATANG", 32, " ", "STR_PAD_BOTH");
            vLine5_1 = str_pad("Welcome", 32, " ", "STR_PAD_BOTH");
            vLine6 = str_pad("(Taxi Svcs. Surcharge)", 32, " ", "STR_PAD_BOTH");
            vLine7 = str_pad("DATE", 13, " ", "STR_PAD_RIGHT");
            vLine7_1 = str_pad(":", 2, " ", "STR_PAD_RIGHT");
            vLine7_2 = str_pad(datedTime.substring(0,10), 17, " ", "STR_PAD_RIGHT");
            vLine8 = str_pad("TAXI NO", 13, " ", "STR_PAD_RIGHT");
            vLine8_1 = str_pad(":", 2, " ", "STR_PAD_RIGHT");
            vLine8_2 = str_pad(Doc2No, 17, " ", "STR_PAD_RIGHT");
            vLine9 = str_pad("DESTINATION", 13, " ", "STR_PAD_RIGHT");
            vLine9_1 = str_pad(":", 2, " ", "STR_PAD_RIGHT");
            vLine9_2 = str_pad(Doc3No, 17, " ", "STR_PAD_RIGHT");

            strPrint +=vLine1+"\n";
            strPrint +=vLine2+"\n";
            strPrint +=vLine3+"\n";
            //strPrint +=vLine4+"\n";
            strPrint +=vAddress+"\n";
            strPrint += "________________________________\n";
            strPrint +=vLine5+"\n";
            strPrint +=vLine5_1+"\n";
            strPrint +=vLine6+"\n";
            strPrint += "________________________________\n";
            strPrint +=vLine7+vLine7_1+vLine7_2+"\n";
            strPrint +=vLine8+vLine8_1+vLine8_2+"\n";
            strPrint +=vLine9+vLine9_1+vLine9_2+"\n\n";
            String qDt="select SUBSTR(Description,1,20)as Description,HCUnitCost from stk_cus_inv_dt where Doc1No='"+Doc1No+"'";
            Cursor rsDt=db.getQuery(qDt);
            while(rsDt.moveToNext()){
                Double dHCUnitCost      = rsDt.getDouble(1);
                String HCUnitCost       = String.format(Locale.US, "%,.2f", dHCUnitCost);
                String Description      = rsDt.getString(0);
                vLine10                 = str_pad(Description, 20, " ", "STR_PAD_RIGHT");
                vLine10_1               = str_pad(HCUnitCost, 12, " ", "STR_PAD_LEFT");
                strPrint                +=vLine10+vLine10_1+"\n";
            }
            vLine11 = str_pad("Disahkan Oleh", 20, " ", "STR_PAD_BOTH");
            vLine12 = str_pad("...............", 20, " ", "STR_PAD_BOTH");
            strPrint += "________________________________\n\n";
            strPrint +=vLine11+"\n\n\n\n";
            strPrint +=vLine12+"\n\n\n\n";
            stringPrint=strPrint;
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private void receipt78(){
        try{
            String strPrint="";
            String vLine1="";
            String vLine2="";
            String vLine3="";
            String vLine4="";
            String vLine5="";
            String vLine5_1="";
            String vLine6="";
            String vLine7="";
            String vLine7_1 ="";
            String vLine7_2 ="";
            String vLine8   ="";
            String vLine8_1 ="";
            String vLine8_2 ="";
            String vLine9   ="";
            String vLine9_1 ="";
            String vLine9_2 ="";
            String vLine10   ="";
            String vLine10_1 ="";
            String vLine11 ="";
            String vLine12 ="";



            DBAdapter db=new DBAdapter(c);
            db.openDB();
            vLine1 = str_pad("RESIT RESMI", 48, " ", "STR_PAD_BOTH");
            vLine2 = str_pad("No: "+Doc1No, 48, " ", "STR_PAD_BOTH");

            String qCom="select CurCode,CompanyName,CompanyCode," +
                    "GSTNo,Address,Tel1," +
                    "Fax1,CompanyEmail,ComTown," +
                    "ComState,ComCountry FROM companysetup ";
            Cursor rsCom=db.getQuery(qCom);
            String vAddress = "";
            while(rsCom.moveToNext()){
                vLine3 = str_pad(rsCom.getString(1), 48, " ", "STR_PAD_BOTH");
                vLine4 = str_pad(rsCom.getString(5), 48, " ", "STR_PAD_BOTH");
                String Address=rsCom.getString(4).trim();
                String AddressAll = Address.replaceAll("(.{48})", "$1\n");
                String[] address = AddressAll.split("\n");
                for (String add : address) {
                    vAddress += str_pad(add, 48, " ", "STR_PAD_BOTH");
                }
            }
            vLine5 = str_pad("SELAMAT DATANG", 48, " ", "STR_PAD_BOTH");
            vLine5_1 = str_pad("Welcome", 48, " ", "STR_PAD_BOTH");
            vLine6 = str_pad("(Taxi Svcs. Surcharge)", 48, " ", "STR_PAD_BOTH");
            vLine7 = str_pad("DATE", 18, " ", "STR_PAD_RIGHT");
            vLine7_1 = str_pad(":", 2, " ", "STR_PAD_RIGHT");
            vLine7_2 = str_pad(datedTime.substring(0,10), 28, " ", "STR_PAD_RIGHT");
            vLine8 = str_pad("TAXI NO", 18, " ", "STR_PAD_RIGHT");
            vLine8_1 = str_pad(":", 2, " ", "STR_PAD_RIGHT");
            vLine8_2 = str_pad(Doc2No, 28, " ", "STR_PAD_RIGHT");
            vLine9 = str_pad("DESTINATION", 18, " ", "STR_PAD_RIGHT");
            vLine9_1 = str_pad(":", 2, " ", "STR_PAD_RIGHT");
            vLine9_2 = str_pad(Doc3No, 28, " ", "STR_PAD_RIGHT");

            strPrint +=vLine1+"\n";
            strPrint +=vLine2+"\n";
            strPrint +=vLine3+"\n";
            //strPrint +=vLine4+"\n";
            strPrint +=vAddress+"\n";
            strPrint += "________________________________________________\n";
            strPrint +=vLine5+"\n";
            strPrint +=vLine5_1+"\n";
            strPrint +=vLine6+"\n";
            strPrint += "________________________________________________\n";
            strPrint +=vLine7+vLine7_1+vLine7_2+"\n";
            strPrint +=vLine8+vLine8_1+vLine8_2+"\n";
            strPrint +=vLine9+vLine9_1+vLine9_2+"\n\n";
            String qDt="select SUBSTR(Description,1,20)as Description,HCUnitCost from stk_cus_inv_dt where Doc1No='"+Doc1No+"'";
            Cursor rsDt=db.getQuery(qDt);
            while(rsDt.moveToNext()){
                Double dHCUnitCost      = rsDt.getDouble(1);
                String HCUnitCost       = String.format(Locale.US, "%,.2f", dHCUnitCost);
                String Description      = rsDt.getString(0);
                vLine10                 = str_pad(Description, 34, " ", "STR_PAD_RIGHT");
                vLine10_1               = str_pad(HCUnitCost, 14, " ", "STR_PAD_LEFT");
                strPrint                +=vLine10+vLine10_1+"\n";
            }
            vLine11 = str_pad("Disahkan Oleh", 25, " ", "STR_PAD_BOTH");
            vLine12 = str_pad("...............", 25, " ", "STR_PAD_BOTH");
            strPrint += "________________________________________________\n\n";
            strPrint +=vLine11+"\n\n\n\n";
            strPrint +=vLine12+"\n\n\n\n";
            stringPrint=strPrint;
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    public void fngenerate58(String Doc1No) {
        String vLine1, vLine2, vLine3, vLine4, vLine4_1, vLine5, vLine6, vLine7, vLine8, vLine9, vLine10, vLine11, vLine12, vLine13, vLine14, vLine15, vLine16, vLine17;
        String vLine18, vLine19, vLine20, vLine21, vLine22, vLine23, vLine24, vLine25, vLine4_2, vLine4_3, vLine4_4;
        vLine1 = "";
        vLine2 = "";
        vLine3 = "";
        vLine4 = "";
        vLine4_1 = "";
        vLine4_2 = "";
        vLine4_3 = "";
        vLine4_4 = "";
        vLine5 = "";
        vLine6 = "";
        vLine7 = "";
        vLine8 = "";
        vLine9 = "";
        vLine10 = "";
        vLine11 = "";
        vLine12 = "";
        vLine13 = "";
        vLine14 = "";
        vLine15 = "";
        vLine16 = "";
        vLine17 = "";
        vLine18 = "";
        vLine19 = "";
        vLine20 = "";
        vLine21 = "";
        vLine22 = "";
        vLine23 = "";
        vLine24 = "";
        vLine25 = "";

        String strPrint = "";
        String CompanyName = "";
        String CompanyCode = "";
        String GSTNo = "";
        String Address = "";
        String ComTown = "";
        String ComState = "";
        String ComCountry = "";
        String Tel = "";
        String Fax = "";
        //String CusName = "";
       // String CusCode = "";
        String TelCus = "";
        String AdddresCus = "";
        //String SalesPersonCode = "";
        String D_ateTime = "";
        String Disc2 = "";
        String Minus2 = "";
        String vCC1No = "";
        String vLine11_1="";
        String vFooter="";

        try {
            JSONObject jsonReq,jsonRes;
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String Company = "select CurCode,CompanyName,CompanyCode," +
                    " GSTNo, Address, Tel1," +
                    " Fax1, CompanyEmail, ComTown," +
                    " ComState, ComCountry, IFNULL(Footer_CR,'')as Footer_CR, " +
                    " IFNULL(PhotoFile,'')as PhotoFile FROM companysetup ";
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", Company);
                jsonReq.put("action", "select");
                String rsCom = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rsCom);
                String rsHeader = jsonRes.getString("hasil");
                JSONArray rsData = new JSONArray(rsHeader);
                JSONObject vData = null;
                for (int i = 0; i < rsData.length(); i++) {
                    vData = rsData.getJSONObject(i);
                    CompanyName = vData.getString("CompanyName");
                    CompanyCode = vData.getString("CompanyCode");
                    GSTNo       = vData.getString("GSTNo");
                    Address     = vData.getString("Address").trim();
                    Tel         = vData.getString("Tel1");
                    Fax         = vData.getString("Fax1");
                    vFooter     = String.valueOf(Html.fromHtml(vData.getString("Footer_CR")));
                }
            }else {
                Cursor rsCom = db.getQuery(Company);
                while (rsCom.moveToNext()) {
                    CompanyName     = rsCom.getString(1);
                    CompanyCode     = rsCom.getString(2);
                    GSTNo           = rsCom.getString(3);
                    Address         = rsCom.getString(4);
                    Tel             = rsCom.getString(5);
                    Fax             = rsCom.getString(6);
                    ComTown         = rsCom.getString(8);
                    ComState        = rsCom.getString(9);
                    ComCountry      = rsCom.getString(10);
                    vFooter         = String.valueOf(Html.fromHtml(rsCom.getString(11)));
                    imgHeader       = rsCom.getString(12);
                    byte[] decString= Base64.decode(imgHeader, Base64.DEFAULT);
                    bmpHeader       = BitmapFactory.decodeByteArray(decString,0,decString.length);
                }
            }

            vLine1 = str_pad(CompanyName, 32, " ", "STR_PAD_BOTH");
            vLine2 = str_pad("Co.Reg.No.: " + CompanyCode, 32, " ", "STR_PAD_BOTH");
            vLine3 = str_pad("GST Reg.No.: " + GSTNo, 32, " ", "STR_PAD_BOTH");
            String AddressAll = Address.replaceAll("(.{32})", "$1\n");
            String[] address = AddressAll.split("\n");
            String vAddress = "";
            for (String add : address) {
                vAddress += str_pad(add,32, " ", "STR_PAD_BOTH");
                Log.d("ADDRESS",vAddress);
            }


            vLine4 = vAddress;

            String FooterAll = vFooter.replaceAll("(.{32})", "$1\n");
            String [] footer=FooterAll.split("\n");
            String Footer="";
            for (String add: footer) {
                Footer +=str_pad(add, 31, " ", "STR_PAD_BOTH");
            }


            vLine5 = str_pad(Tel, 32, " ", "STR_PAD_BOTH");
            vLine6 = str_pad(Fax, 32, " ", "STR_PAD_BOTH");
            if (!GSTNo.equals("NO")) {
                vLine7 = str_pad("TAX INVOICE", 32, " ", "STR_PAD_BOTH");
            } else {
                vLine7 = str_pad(ReceiptHeader, 32, " ", "STR_PAD_BOTH");
            }
            //end query header
            vLine9 = str_pad("Bill #  : " + Doc1No, 32, " ", "STR_PAD_RIGHT");
            vLine11 = str_pad(datedTime, 18, " ", "STR_PAD_RIGHT");
            if (TableNo.equals("0")) {
                vLine11_1 = str_pad("", 13, " ", "STR_PAD_RIGHT");
            } else {
                vLine11_1 = str_pad("Table #: " + TableNo, 13, " ", "STR_PAD_RIGHT");
            }

            //customer
            String qCustomer = "select H.CusCode, C.CusName, C.Address " +
                    " from stk_cus_inv_hd H inner join customer C " +
                    " on H.CusCode=C.CusCode where H.Doc1No='" + Doc1No + "'";
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", qCustomer);
                jsonReq.put("action", "select");
                String rsCus = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rsCus);
                String resCus= jsonRes.getString("hasil");
                JSONArray jaCus = new JSONArray(resCus);
                JSONObject vCus = null;
                for (int i = 0; i < jaCus.length(); i++) {
                    vCus        = jaCus.getJSONObject(i);
                    //CusCode   = vCus.getString("CusCode");
                   // CusName   = vCus.getString("CusName");
                    AdddresCus = vCus.getString("Address");
                }
            }else {
                Cursor rsCus = db.getQuery(qCustomer);
                while (rsCus.moveToNext()) {
                   // CusCode = rsCus.getString(0);
                   // CusName = rsCus.getString(1);
                    AdddresCus = rsCus.getString(2);
                }
            }
            vLine12 = str_pad("TO: " + CusName, 32, " ", "STR_PAD_RIGHT");
            String vLine12_1 = str_pad("Doc2No: "+Doc2No, 32, " ", "STR_PAD_RIGHT");
            String vLine12_2 = str_pad("Salesman: "+SalesPersonCode, 32, " ", "STR_PAD_RIGHT");
            vLine13 = str_pad(AdddresCus, 32, " ", "STR_PAD_RIGHT");
            String vLine131 = str_pad("Qty", 7, " ", "STR_PAD_RIGHT");
            String vLine132 = str_pad("Price", 11, " ", "STR_PAD_BOTH");
            String vLine133 = str_pad("Amount (" + CurCode + ")", 14, " ", "STR_PAD_LEFT");
            //end query customer
            strPrint += vLine1 + "\n";
            if(!CompanyCode.isEmpty()) {
                strPrint += vLine2 + "\n";
            }
            if(!GSTNo.equals("NO")) {
                strPrint += vLine3 + "\n";
            }
            strPrint += vLine4 + "\n";
            strPrint += "________________________________\n";
            strPrint += vLine7 + "\n";
            strPrint += "________________________________\n";
            //end header

            //customer
            strPrint += vLine9 + "\n";
            strPrint += vLine11 + vLine11_1 + "\n";
            strPrint += vLine12 +"\n";
            strPrint += vLine12_2+"\n";
            if(!Doc2No.isEmpty()){
                strPrint += vLine12_1;
            }
            strPrint += "________________________________\n";
            strPrint += vLine131 + vLine132 + vLine133 + "\n";
            strPrint += "________________________________\n";
            //end customer

            //detail
            String qDetail = "select ItemCode, IFNULL(Qty,0) as Qty,UOM,DetailTaxCode,SUBSTR(Description,1,26) as Description, " +
                    "IFNULL(HCUnitCost,0) as HCUnitCost, IFNULL(HCLineAmt,0) AS HCLineAmt, IFNULL(DisRate1,0) as DisRate1," +
                    "IFNULL(HCDiscount,0) as HCDiscount, AlternateItem " +
                    "from stk_cus_inv_dt Where Doc1No='" + Doc1No + "' ";
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", qDetail);
                jsonReq.put("action", "select");
                String reqDet = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(reqDet);
                String resDet= jsonRes.getString("hasil");
                Log.d("JSONDET",resDet);
                JSONArray jaDt = new JSONArray(resDet);
                JSONObject vDet = null;
                for (int i = 0; i < jaDt.length(); i++) {
                    vDet = jaDt.getJSONObject(i);
                    String ItemCode         = vDet.getString("ItemCode");
                    Double dQty             = vDet.getDouble("Qty");
                    String Qty              = String.format(Locale.US, "%,.2f", dQty);
                    String UOM              = vDet.getString("UOM").trim().replaceAll("\\s+", " ");
                    String DetailTaxCode    = vDet.getString("DetailTaxCode");
                    String Description      = vDet.getString("Description").trim().replaceAll("\\s+", " ");
                    Double dHCUnitCost      = vDet.getDouble("HCUnitCost");
                    String AlternateItem     = vDet.getString("AlternateItem").trim().replaceAll("\\s+", " ");
                    String HCUnitCost       = String.format(Locale.US, "%,.2f", dHCUnitCost);
                    Double dHCLineAmt       = vDet.getDouble("HCLineAmt");
                    String HCLineAmt        = String.format(Locale.US, "%,.2f", dHCLineAmt);
                    Double dDisRate1        = vDet.getDouble("DisRate1");
                    String DisRate1         = String.format(Locale.US, "%,.2f", dDisRate1);
                    Double dHCDiscount      = vDet.getDouble("HCDiscount");
                    String HCDiscount       = String.format(Locale.US, "%,.2f", dHCDiscount);
                    vLine14                 = str_pad(Description, 26, " ", "STR_PAD_RIGHT");
                    String vLine14_1        = str_pad(UOM, 6, " ", "STR_PAD_RIGHT");
                    String vLine14_2       = str_pad("", 6, " ", "STR_PAD_RIGHT");
                    String vLine14_3       = str_pad(AlternateItem, 20, " ", "STR_PAD_RIGHT");
                    String vLine15_1        = str_pad(Qty, 4, " ", "STR_PAD_LEFT");
                    String vLine15_2        = str_pad(HCUnitCost, 12, " ", "STR_PAD_LEFT");
                    String vLine15_4        = str_pad(HCLineAmt, 13, " ", "STR_PAD_LEFT");
                    strPrint                += vLine14+vLine14_1 + "\n";
                    if(AlternateItem.length()>1){
                        strPrint            += vLine14_3+vLine14_2+"\n";
                    }
                    if(HCUnitCost.length()>12 || HCLineAmt.length()>12) {
                        strPrint                += vLine15_1 + " x" + str_pad(HCUnitCost, 25, " ", "STR_PAD_LEFT") + "\n" ;
                        strPrint                += str_pad(HCLineAmt, 31, " ", "STR_PAD_LEFT") + "\n";

                    }else{
                        strPrint                += vLine15_1 + " x" + vLine15_2 + vLine15_4 + "\n";
                    }
                    //strPrint                += vLine15_1 + " x" + vLine15_2 + vLine15_4 + "\n";
                    if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                        Disc2 = "(" + HCDiscount + ")";
                        String vLine15_5 = str_pad(Disc2, 18, " ", "STR_PAD_LEFT");
                        String vLine15_6 = str_pad(" ", 13, " ", "STR_PAD_LEFT");
                        strPrint += vLine15_5 + vLine15_6 + "\n";
                    }
                }
            }else {
                Cursor rsDt = db.getQuery(qDetail);
                while (rsDt.moveToNext()) {
                    String ItemCode = rsDt.getString(0);
                    Double dQty = rsDt.getDouble(1);
                    String Qty = String.format(Locale.US, "%,.2f", dQty);
                    String UOM = rsDt.getString(2).trim().replaceAll("\\s+", " ");
                    String DetailTaxCode = rsDt.getString(3);
                    String Description = rsDt.getString(4).trim().replaceAll("\\s+", " ");
                    Double dHCUnitCost = rsDt.getDouble(5);
                    String HCUnitCost = String.format(Locale.US, "%,.2f", dHCUnitCost);
                    Double dHCLineAmt = rsDt.getDouble(6);
                    String HCLineAmt = String.format(Locale.US, "%,.2f", dHCLineAmt);
                    Double dDisRate1 = rsDt.getDouble(7);
                    String DisRate1 = String.format(Locale.US, "%,.2f", dDisRate1);
                    Double dHCDiscount = rsDt.getDouble(8);
                    String AlternateItem   = rsDt.getString(9).trim().replaceAll("\\s+", " ");
                    String HCDiscount = String.format(Locale.US, "%,.2f", dHCDiscount);
                    vLine14 = str_pad(Description, 26, " ", "STR_PAD_RIGHT");
                    String vLine14_1 = str_pad(UOM, 6, " ", "STR_PAD_RIGHT");
                    String vLine14_2 = str_pad("", 6, " ", "STR_PAD_RIGHT");
                    String vLine14_3  = str_pad(AlternateItem, 20, " ", "STR_PAD_RIGHT");
                    String vLine15_1 = str_pad(Qty, 4, " ", "STR_PAD_LEFT");
                    String vLine15_2 = str_pad(HCUnitCost, 12, " ", "STR_PAD_LEFT");
                    String vLine15_4 = str_pad(HCLineAmt, 13, " ", "STR_PAD_LEFT");
                    strPrint += vLine14+vLine14_1 + "\n";
                    if(AlternateItem.length()>1){
                        strPrint            += vLine14_3+vLine14_2+"\n";
                    }
                    if(HCUnitCost.length()>12 || HCLineAmt.length()>12) {
                        strPrint                += vLine15_1 + " x" + str_pad(HCUnitCost, 25, " ", "STR_PAD_LEFT") + "\n" ;
                        strPrint                += str_pad(HCLineAmt, 31, " ", "STR_PAD_LEFT") + "\n";

                    }else{
                        strPrint                += vLine15_1 + " x" + vLine15_2 + vLine15_4 + "\n";
                    }
                   // strPrint += vLine15_1 + " x" + vLine15_2 + vLine15_4 + "\n";
                    if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                        Disc2 = "(" + HCDiscount + ")";
                        String vLine15_5 = str_pad(Disc2, 18, " ", "STR_PAD_LEFT");
                        String vLine15_6 = str_pad(" ", 13, " ", "STR_PAD_LEFT");
                        strPrint += vLine15_5 + vLine15_6 + "\n";
                    }
                }
            }
            //end detail
            //start Total
            String SCAmt="";
            String qServiceCharges="select HCUnitCost,HCTax from stk_cus_inv_dt where Doc1No='" + Doc1No + "' " +
                    "and BlankLine='4' ";
            String SC=String.format(Locale.US, "%,.0f", dServiceCharges);
            Cursor rsSC=db.getQuery(qServiceCharges);
            while(rsSC.moveToNext()){
                Double dSCAmt    = rsSC.getDouble(0);
                SCAmt            = String.format(Locale.US, "%,.2f", dSCAmt);
            }
            String vLine16_1             =str_pad(SCAmt, 11, " ", "STR_PAD_LEFT");
            String qTotal = "SELECT IFNULL(H.HCNetAmt,0)AS HCNetAmt,IFNULL(H.HCDtTax,0) AS HCDtTax, "+
                    " IFNULL(R.CashAmt + R.ChangeAmt,0) AS CashAmt,  IFNULL(R.ChangeAmt,0) AS ChangeAmt, "+
                    " IFNULL(SUM(D.HCLineAmt)-SUM(D.HCTax),0) AS AmtExTax,IFNULL(R.BalanceAmount,0) AS BalanceAmount, "+
                    " IFNULL(R.CC1Amt,0) AS CC1Amt, IFNULL(R.CC1No,'') AS CC1No,  IFNULL(R.CC1Code,'')AS CC1Code, " +
                    " IFNULL(SUM(D.Qty),0) AS ItemTender, IFNULL(H.AdjAmt,0)as AdjAmt "+
                    " FROM stk_cus_inv_hd H LEFT JOIN stk_receipt2 R ON R.Doc1No=H.Doc1No INNER JOIN stk_cus_inv_dt D ON D.Doc1No=H.Doc1No "+
                    " where H.Doc1No='" + Doc1No + "' and D.ItemCode<>'M999999SC' Group By H.Doc1No ";

            Double dPayAmt=0.00;
            Double dCC1Amt=0.00;
            Double dBalAmt=0.00;
            String tCC1Code="";
            String vLine22_2="";
            String vLine22_3="";
            String vLine22_4="";
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", qTotal);
                jsonReq.put("action", "select");
                String rsTot = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rsTot);
                String resTot= jsonRes.getString("hasil");
                JSONArray jaTot = new JSONArray(resTot);
                JSONObject vTot = null;
                for (int i = 0; i < jaTot.length(); i++) {
                    vTot                = jaTot.getJSONObject(i);
                    Double dTotAmt      = vTot.getDouble("HCNetAmt");
                    String TotAmt       = String.format(Locale.US, "%,.2f", dTotAmt);
                    Double dTaxAmt      = vTot.getDouble("HCDtTax");
                    String TaxAmt       = String.format(Locale.US, "%,.2f", dTaxAmt);
                    dPayAmt             = vTot.getDouble("CashAmt");
                    String PayAmt       = String.format(Locale.US, "%,.2f", dPayAmt);
                    Double dChAmt       = vTot.getDouble("ChangeAmt");
                    String ChAmt        = String.format(Locale.US, "%,.2f", dChAmt);
                    Double dAmtExtTax   = vTot.getDouble("AmtExTax");
                    String AmtExtTax    = String.format(Locale.US, "%,.2f", dAmtExtTax);
                    dBalAmt             = vTot.getDouble("BalanceAmount");
                    String BalAmt       = String.format(Locale.US, "%,.2f", dBalAmt);
                    dCC1Amt             = vTot.getDouble("CC1Amt");
                    String CC1Amt = String.format(Locale.US, "%,.2f", dCC1Amt);
                    if (dCC1Amt > 0) {
                        String tCC1No   = vTot.getString("CC1No");
                        vCC1No          = tCC1No.substring(tCC1No.length() - 4);
                    }
                    String CC1Code      = vTot.getString("CC1Code");
                    Double dItemTender  = vTot.getDouble("ItemTender");
                    String ItemTender   = String.format(Locale.US, "%,.2f", dItemTender);
                    String AdjAmt       = String.format(Locale.US, "%,.2f", vTot.getDouble("AdjAmt"));

                    vLine16 = str_pad(replaceDec(AmtExtTax), 11, " ", "STR_PAD_LEFT");
                    vLine17 = str_pad(replaceDec(TaxAmt), 11, " ", "STR_PAD_LEFT");
                    vLine18 = str_pad(replaceDec(AdjAmt), 11, " ", "STR_PAD_LEFT");
                    vLine19 = str_pad(replaceDec(TotAmt), 11, " ", "STR_PAD_LEFT");
                    vLine20 = str_pad(replaceDec(PayAmt), 11, " ", "STR_PAD_LEFT");
                    vLine21 = str_pad(replaceDec(ChAmt), 11, " ", "STR_PAD_LEFT");
                    vLine22 = str_pad(replaceDec(BalAmt), 11, " ", "STR_PAD_LEFT");
                    vLine22_2 = str_pad(ItemTender, 11, " ", "STR_PAD_LEFT");
                    vLine22_3 = str_pad(replaceDec(CC1Amt), 11, " ", "STR_PAD_LEFT");
                    vLine22_4 = str_pad("xxxxx" + vCC1No, 11, " ", "STR_PAD_LEFT");
                    tCC1Code = str_pad(CC1Code, 18, " ", "STR_PAD_RIGHT");
                }
            }else {
                Cursor rsTot = db.getQuery(qTotal);
                while (rsTot.moveToNext()) {
                    Double dTotAmt = rsTot.getDouble(0);
                    String TotAmt = String.format(Locale.US, "%,.2f", dTotAmt);
                    Double dTaxAmt = rsTot.getDouble(1);
                    String TaxAmt = String.format(Locale.US, "%,.2f", dTaxAmt);
                    dPayAmt = rsTot.getDouble(2);
                    String PayAmt = String.format(Locale.US, "%,.2f", dPayAmt);
                    Double dChAmt = rsTot.getDouble(3);
                    String ChAmt = String.format(Locale.US, "%,.2f", dChAmt);
                    Double dAmtExtTax = rsTot.getDouble(4);
                    String AmtExtTax = String.format(Locale.US, "%,.2f", dAmtExtTax);
                    dBalAmt = rsTot.getDouble(5);
                    String BalAmt = String.format(Locale.US, "%,.2f", dBalAmt);
                    dCC1Amt = rsTot.getDouble(6);
                    String CC1Amt = String.format(Locale.US, "%,.2f", dCC1Amt);
                    if (dCC1Amt > 0) {
                        String tCC1No = rsTot.getString(7);
                        if(tCC1No.length()>4) {
                            vCC1No = tCC1No.substring(tCC1No.length() - 4);
                        }
                    }
                    String CC1Code      = rsTot.getString(8);
                    Double dItemTender  = rsTot.getDouble(9);
                    String ItemTender   = String.format(Locale.US, "%,.2f", dItemTender);
                    String AdjAmt   = String.format(Locale.US, "%,.2f", rsTot.getDouble(10));

                    vLine16 = str_pad(replaceDec(AmtExtTax), 11, " ", "STR_PAD_LEFT");
                    vLine17 = str_pad(replaceDec(TaxAmt), 11, " ", "STR_PAD_LEFT");
                    vLine18 = str_pad(replaceDec(AdjAmt), 11, " ", "STR_PAD_LEFT");
                    vLine19 = str_pad(replaceDec(TotAmt), 11, " ", "STR_PAD_LEFT");
                    vLine20 = str_pad(replaceDec(PayAmt), 11, " ", "STR_PAD_LEFT");
                    vLine21 = str_pad(replaceDec(ChAmt), 11, " ", "STR_PAD_LEFT");
                    vLine22 = str_pad(replaceDec(BalAmt), 11, " ", "STR_PAD_LEFT");
                    vLine22_2 = str_pad(ItemTender, 11, " ", "STR_PAD_LEFT");
                    vLine22_3 = str_pad(replaceDec(CC1Amt), 11, " ", "STR_PAD_LEFT");
                    vLine22_4 = str_pad("xxxxx" + vCC1No, 11, " ", "STR_PAD_LEFT");
                    tCC1Code = str_pad(CC1Code, 18, " ", "STR_PAD_RIGHT");
                }
            }
            strPrint += "________________________________\n\n";
            String tRefNo="Credit Card No    ";
            if(!PaymentType.equals("Credit Card")){
                tRefNo="Verification Code ";
            }
            if (!GSTNo.equals("NO")) {
                    strPrint += "Amount Exc Tax    : " + vLine16 + "\n";
                if(dServiceCharges>0){
                    strPrint += "Add "+SC+"% SC        : " + vLine16_1 + "\n";
                }
                strPrint += "Add Total Tax Amt : " + vLine17 + "\n";
                strPrint += "Rounding          : " + vLine18 + "\n";
                strPrint += "Total Amount Due  : " + vLine19 + "\n";

                //strPrint += "Paid Amount       : " + vLine20 + "\n";
                if (dPayAmt > 0) {
                    strPrint += "Cash              : " + vLine20 + "\n";
                    strPrint += "Change            : " + vLine21 + "\n";
                } else {
                    strPrint += tCC1Code + ": " + vLine22_3 + "\n";
                    strPrint += tRefNo+": " + vLine22_4 + "\n";
                }
            } else {
                strPrint += "Gross Amount      : " + vLine19 + "\n";
                if (dPayAmt > 0) {
                    strPrint += "Cash              : " + vLine20 + "\n";
                    strPrint += "Change            : " + vLine21 + "\n";
                }
                if (dCC1Amt > 0) {
                    strPrint += tCC1Code + ": " + vLine22_3 + "\n";
                    strPrint += tRefNo+": " + vLine22_4 + "\n";
                }
                strPrint += "Total Qty Tender  : " + vLine22_2 + "\n";

            }

            if (dBalAmt > 0) {
                strPrint += "Balance Amount    : " + vLine22 + "\n";
            }
            if (!GSTNo.equals("NO")) {
                strPrint += "________________________________\n";
                strPrint += "Tax Summary \n";
                strPrint += "Code  Rate   Goods Amt  Tax Amt\n";
            }
            //end total

            //start GST
            String qGST = "select IFNULL(TaxRate1,0)as TaxRate1, DetailTaxCode, IFNULL(sum(HCLineAmt)-sum(HCTax),0) as GoodAmt, " +
                    "IFNULL(sum(HCTax),0) as GSTAmt from stk_cus_inv_dt  where Doc1No='" + Doc1No + "' Group By DetailTaxCode  ";
            if(DBStatus.equals("2")) {
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", qGST);
                jsonReq.put("action", "select");
                String rsGST = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rsGST);
                String resGST= jsonRes.getString("hasil");
                JSONArray jaGST = new JSONArray(resGST);
                JSONObject vTax = null;
                for (int i = 0; i < jaGST.length(); i++) {
                    vTax = jaGST.getJSONObject(i);
                    Double dTaxRate  = vTax.getDouble("TaxRate1");
                    String TaxRate   = String.format(Locale.US, "%,.2f", dTaxRate);
                    String TaxCode   = vTax.getString("DetailTaxCode");
                    if(TaxCode.length()>0){
                        TaxCode=TaxCode.substring(0, 4);
                    }
                    Double dGoodAmt  = vTax.getDouble("GoodAmt");
                    String GoodAmt   = String.format(Locale.US, "%,.2f", dGoodAmt);
                    Double dGSTAmt   = vTax.getDouble("GSTAmt");
                    String GSTAmt    = String.format(Locale.US, "%,.2f", dGSTAmt);
                    String vLine23_1 = str_pad(TaxCode, 5, " ", "STR_PAD_LEFT");
                    String vLine23_2 = str_pad(TaxRate, 5, " ", "STR_PAD_LEFT");
                    String vLine23_3 = str_pad(GoodAmt, 11, " ", "STR_PAD_LEFT");
                    String vLine23_4 = str_pad(GSTAmt, 11, " ", "STR_PAD_LEFT");
                    vLine23 = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                    if (!GSTNo.equals("NO")) {
                        strPrint += vLine23 + "\n";
                    }
                }
            }else{
                Cursor rsGST = db.getQuery(qGST);
                while (rsGST.moveToNext()) {
                    Double dTaxRate = rsGST.getDouble(0);
                    String TaxRate = String.format(Locale.US, "%,.2f", dTaxRate);
                    String TaxCode = rsGST.getString(1);
                    if(TaxCode.length()>4){
                        TaxCode=TaxCode.substring(0, 4);
                    }
                    Double dGoodAmt = rsGST.getDouble(2);
                    String GoodAmt = String.format(Locale.US, "%,.2f", dGoodAmt);
                    Double dGSTAmt = rsGST.getDouble(3);
                    String GSTAmt = String.format(Locale.US, "%,.2f", dGSTAmt);
                    String vLine23_1 = str_pad(TaxCode, 5, " ", "STR_PAD_LEFT");
                    String vLine23_2 = str_pad(TaxRate, 5, " ", "STR_PAD_LEFT");
                    String vLine23_3 = str_pad(GoodAmt, 11, " ", "STR_PAD_LEFT");
                    String vLine23_4 = str_pad(GSTAmt, 11, " ", "STR_PAD_LEFT");
                    vLine23 = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                    if (!GSTNo.equals("NO")) {
                        strPrint += vLine23 + "\n";
                    }
                }
            }
            //end GST
            strPrint += "________________________________\n";
            if(!vFooter.isEmpty()) {
                strPrint += Footer+"\n";
            }else {
                strPrint += "*Goods sold non-returnable & non-refundable\n";
                strPrint += "Thank you, please come again! \n";
            }


            if(ReceiptType.equals("Format02")){
                strPrint += "________________________________\n";
                strPrint += vLine9 + "\n";
                strPrint += vLine11 + vLine11_1 + "\n";
                strPrint += vLine12 +"\n";
                strPrint += vLine12_2+"\n";
                if(!Doc2No.isEmpty()){
                    strPrint += vLine12_1;
                }
            }
            strPrint += "________________________________\n \n\n";
            Log.d("RESULT", "\r \n \r \n"+ strPrint);
            stringPrint = strPrint;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        //return stringPrint;
    }

    public void fngenerate78(String Doc1No){
        String vLine1,vLine2,vLine3,vLine4,vLine4_1,vLine5,vLine6,vLine7,vLine8,vLine9,vLine10,vLine11,vLine12,vLine13,vLine14,vLine15,vLine16,vLine17;
        String vLine18,vLine19,vLine20,vLine21,vLine22,vLine23,vLine24,vLine25;
        vLine1="";
        vLine2="";
        vLine3="";
        vLine4="";
        vLine4_1="";
        vLine5="";
        vLine6="";
        vLine7="";
        vLine8="";
        vLine9="";
        vLine10="";
        vLine11="";
        vLine12="";
        vLine13="";
        vLine14="";
        vLine15="";
        vLine16="";
        vLine17="";
        vLine18="";
        vLine19="";
        vLine20="";
        vLine21="";
        vLine22="";
        vLine23="";
        vLine24="";
        vLine25="";

        String strPrint="";
        String CompanyName="";
        String CompanyCode="";
        String GSTNo="";
        String Address="";
        String ComTown="";
        String ComState="";
        String ComCountry="";
        String Tel="";
        String Fax="";
        //String CusCode="";
       // String CusName="";
        String TelCus="";
        String AdddresCus="";
        //String SalesPersonCode="";
        String D_ateTime="";
        String Disc2="";
        String Minus2="";
        String vCC1No="";
        String vLine11_1="";
        String vFooter="";
        try {
            JSONObject jsonReq,jsonRes;
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String Company = "select CurCode, CompanyName, CompanyCode, " +
                    "GSTNo, Address, Tel1," +
                    "Fax1, CompanyEmail, ComTown, " +
                    "ComState, ComCountry, IFNULL(Footer_CR,'')as Footer_CR, " +
                    "IFNULL(PhotoFile,'')as PhotoFile FROM companysetup ";
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", Company);
                jsonReq.put("action", "select");
                String rsCom = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rsCom);
                String rsHeader = jsonRes.getString("hasil");
                JSONArray rsData = new JSONArray(rsHeader);
                JSONObject vData = null;
                for (int i = 0; i < rsData.length(); i++) {
                    vData = rsData.getJSONObject(i);
                    CompanyName = vData.getString("CompanyName");
                    CompanyCode = vData.getString("CompanyCode");
                    GSTNo       = vData.getString("GSTNo");
                    Address     = vData.getString("Address");
                    Tel         = vData.getString("Tel1");
                    Fax         = vData.getString("Fax1");
                    vFooter     = String.valueOf(Html.fromHtml(vData.getString("Footer_CR")));
                }
            }else {
                Cursor rsCom = db.getQuery(Company);
                while (rsCom.moveToNext()) {
                    CompanyName = rsCom.getString(1);
                    CompanyCode = rsCom.getString(2);
                    GSTNo = rsCom.getString(3);
                    Address = rsCom.getString(4);
                    Tel = rsCom.getString(5);
                    Fax = rsCom.getString(6);
                    ComTown = rsCom.getString(8);
                    ComState = rsCom.getString(9);
                    ComCountry = rsCom.getString(10);
                    vFooter = String.valueOf(Html.fromHtml(rsCom.getString(11)));
                }
            }
            vLine1 = str_pad(CompanyName, 48, " ", "STR_PAD_BOTH");
            vLine2 = str_pad("Co.Reg.No.: " + CompanyCode, 48, " ", "STR_PAD_BOTH");
            vLine3 = str_pad("GST Reg.No.: " + GSTNo, 48, " ", "STR_PAD_BOTH");
            String AddressAll = Address.replaceAll("(.{48})", "$1\n");
            String[] address = AddressAll.split("\n");
            String vAddress = "";
            for (String add : address) {
                vAddress += str_pad(add, 48, " ", "STR_PAD_BOTH");
            }
            vLine4 = vAddress;

            String FooterAll = vFooter.replaceAll("(.{47})", "$1\n");
            String [] footer=FooterAll.split("\n");
            String Footer="";
            for (String add: footer) {
                Footer +=str_pad(add, 47, " ", "STR_PAD_BOTH");
            }
            //vLine4   =str_pad(AddressAll, 48, " ", "STR_PAD_BOTH");
            vLine5 = str_pad("Tel.:" + Tel, 48, " ", "STR_PAD_BOTH");
            vLine6 = str_pad(Fax, 48, " ", "STR_PAD_BOTH");
            if (!GSTNo.equals("NO")) {
                vLine7 = str_pad("TAX INVOICE", 48, " ", "STR_PAD_BOTH");
            } else {
                vLine7 = str_pad(ReceiptHeader, 48, " ", "STR_PAD_BOTH");
            }
            //end query header

            //start customer
            String qCustomer = "select H.CusCode,C.CusName,C.Address from stk_cus_inv_hd H inner join customer C" +
                    " on H.CusCode=C.CusCode where H.Doc1No='" + Doc1No + "'";
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", qCustomer);
                jsonReq.put("action", "select");
                String rsCus = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rsCus);
                String resCus= jsonRes.getString("hasil");
                JSONArray jaCus = new JSONArray(resCus);
                JSONObject vCus = null;
                for (int i = 0; i < jaCus.length(); i++) {
                    vCus = jaCus.getJSONObject(i);
                    //CusCode = vCus.getString("CusCode");
                    //CusName = vCus.getString("CusName");
                    AdddresCus = vCus.getString("Address");
                }
            }else {
                Cursor rsCus = db.getQuery(qCustomer);
                while (rsCus.moveToNext()) {
                    //CusCode = rsCus.getString(0);
                   // CusName = rsCus.getString(1);
                    AdddresCus = rsCus.getString(2);
                }
            }

            vLine8 = str_pad("Bill #: " + Doc1No, 48, " ", "STR_PAD_RIGHT");
            vLine11 = str_pad(datedTime, 24, " ", "STR_PAD_RIGHT");
            if (TableNo.equals("0")) {
                vLine11_1 = str_pad("", 24, " ", "STR_PAD_RIGHT");
            } else {
                vLine11_1 = str_pad("Table No #: " + TableNo, 24, " ", "STR_PAD_RIGHT");
            }
            vLine12 = str_pad("TO: " + CusName, 48, " ", "STR_PAD_RIGHT");
            String vLine12_1 = str_pad("Doc2No: "+Doc2No, 24, " ", "STR_PAD_RIGHT");
            String vLine12_2 = str_pad("Salesman: "+SalesPersonCode, 48, " ", "STR_PAD_RIGHT");
            vLine13 = str_pad(AdddresCus, 48, " ", "STR_PAD_RIGHT");
            String vLine131 = str_pad("Quantity", 9, " ", "STR_PAD_RIGHT");
            String vLine132 = str_pad("Unit Price", 19, " ", "STR_PAD_BOTH");
            String vLine133 = str_pad("Amount (" + CurCode + ")", 20, " ", "STR_PAD_LEFT");
            //end query customer

            strPrint += vLine1 + "\n";
            if(!CompanyCode.isEmpty()){
                strPrint += vLine2 + "\n";
            }

            if(!GSTNo.equals("NO")) {
                strPrint += vLine3 + "\n";
            }
            strPrint += vLine4 + "\n";
            if (!Tel.equals("")) {
                strPrint += vLine5 + "\n";
            }
            if (!Fax.equals("")) {
                // strPrint += vLine6+"\n";
            }
            strPrint += "________________________________________________\n";
            strPrint += vLine7 + "\n";
            strPrint += "________________________________________________\n";
            //end header


            strPrint += vLine8 +"\n";
            strPrint += vLine11 + vLine11_1 +"\n";
            strPrint += vLine12 +"\n";
            strPrint += vLine12_2+"\n";
            if(!Doc2No.isEmpty()){
                strPrint += vLine12_1;
            }
            strPrint += "________________________________________________\n";
            strPrint += vLine131 + vLine132 + vLine133 + "\n";
            strPrint += "________________________________________________\n";
            //end customer

            //detail
            String qDetail = "select ItemCode,IFNULL(Qty,0) as Qty, UOM, DetailTaxCode, substr(Description,1,45) as Description," +
                    "IFNULL(HCUnitCost,0) as HCUnitCost,IFNULL(HCLineAmt,0) AS HCLineAmt,IFNULL(DisRate1,0) as DisRate1," +
                    "IFNULL(HCDiscount,0) as HCDiscount, AlternateItem " +
                    "from stk_cus_inv_dt Where Doc1No='" + Doc1No + "'  ";
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", qDetail);
                jsonReq.put("action", "select");
                String reqDet = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes         = new JSONObject(reqDet);
                String resDet   = jsonRes.getString("hasil");
                JSONArray jaDt = new JSONArray(resDet);
                JSONObject vDet = null;
                for (int i = 0; i < jaDt.length(); i++) {
                    vDet = jaDt.getJSONObject(i);
                    String ItemCode         = vDet.getString("ItemCode");
                    Double dQty             = vDet.getDouble("Qty");
                    String Qty              = String.format(Locale.US, "%,.2f", dQty);
                    String UOM              = vDet.getString("UOM").trim().replaceAll("\\s+", " ");
                    String DetailTaxCode    = vDet.getString("DetailTaxCode");
                    String Description      = vDet.getString("Description").trim().replaceAll("\\s+", " ");
                    Double dHCUnitCost      = vDet.getDouble("HCUnitCost");
                    String HCUnitCost       = String.format(Locale.US, "%,.2f", dHCUnitCost);
                    Double dHCLineAmt       = vDet.getDouble("HCLineAmt");
                    String HCLineAmt        = String.format(Locale.US, "%,.2f", dHCLineAmt);
                    Double dDisRate1        = vDet.getDouble("DisRate1");
                    String DisRate1         = String.format(Locale.US, "%,.2f", dDisRate1);
                    Double dHCDiscount      = vDet.getDouble("HCDiscount");
                    String HCDiscount       = String.format(Locale.US, "%,.2f", dHCDiscount);
                    if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                        Disc2 = "(" + HCDiscount + ")";
                    } else {
                        Disc2 = " ";
                    }
                    String AlternateItem =vDet.getString("AlternateItem").trim().replaceAll("\\s+", " ");
                    vLine14             = str_pad(Description, 42, " ", "STR_PAD_RIGHT");
                    String vLine14_1    = str_pad(UOM, 6, " ", "STR_PAD_RIGHT");
                    String vLine14_2    = str_pad("", 6, " ", "STR_PAD_RIGHT");
                    String vLine14_3    = str_pad(AlternateItem, 35, " ", "STR_PAD_RIGHT");
                    String vLine15_1    = str_pad(Qty, 6, " ", "STR_PAD_LEFT");
                    String vLine15_2    = str_pad(HCUnitCost, 13, " ", "STR_PAD_LEFT");
                    String vLine15_3    = str_pad(Disc2, 12, " ", "STR_PAD_LEFT");
                    String vLine15_4    = str_pad(HCLineAmt, 15, " ", "STR_PAD_LEFT");
                    strPrint            += vLine14+vLine14_1 + "\n";
                    if(AlternateItem.length()>1){
                        strPrint            += vLine14_3+vLine14_2+"\n";
                    }
                    strPrint            += "" + vLine15_1 + " x" + vLine15_2 + vLine15_3 + vLine15_4 + "\n";
                }
            }else {
                Cursor rsDt = db.getQuery(qDetail);
                while (rsDt.moveToNext()) {
                    String ItemCode = rsDt.getString(0);
                    Double dQty = rsDt.getDouble(1);
                    String Qty = String.format(Locale.US, "%,.2f", dQty);
                    String UOM = rsDt.getString(2).trim().replaceAll("\\s+", " ");
                    String DetailTaxCode = rsDt.getString(3);
                    String Description = rsDt.getString(4).trim().replaceAll("\\s+", " ");
                    Double dHCUnitCost = rsDt.getDouble(5);
                    String HCUnitCost = String.format(Locale.US, "%,.2f", dHCUnitCost);
                    Double dHCLineAmt = rsDt.getDouble(6);
                    String HCLineAmt = String.format(Locale.US, "%,.2f", dHCLineAmt);
                    Double dDisRate1 = rsDt.getDouble(7);
                    String DisRate1 = String.format(Locale.US, "%,.2f", dDisRate1);
                    Double dHCDiscount = rsDt.getDouble(8);
                    String HCDiscount = String.format(Locale.US, "%,.2f", dHCDiscount);
                    if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                        Disc2 = "(" + HCDiscount + ")";
                    } else {
                        Disc2 = " ";
                    }
                    String AlternateItem=rsDt.getString(9).trim().replaceAll("\\s+", " ");
                    vLine14 = str_pad(Description, 42, " ", "STR_PAD_RIGHT");
                    String vLine14_1 = str_pad(UOM, 6, " ", "STR_PAD_RIGHT");
                    String vLine14_2    = str_pad("", 6, " ", "STR_PAD_RIGHT");
                    String vLine14_3    = str_pad(AlternateItem, 35, " ", "STR_PAD_RIGHT");
                    String vLine15_1 = str_pad(Qty, 6, " ", "STR_PAD_LEFT");
                    String vLine15_2 = str_pad(HCUnitCost, 13, " ", "STR_PAD_LEFT");
                    String vLine15_3 = str_pad(Disc2, 12, " ", "STR_PAD_LEFT");
                    String vLine15_4 = str_pad(HCLineAmt, 15, " ", "STR_PAD_LEFT");
                    strPrint += vLine14+vLine14_1 + "\n";
                    if(AlternateItem.length()>1){
                        strPrint            += vLine14_3+vLine14_2+"\n";
                    }
                    strPrint += "" + vLine15_1 + " x" + vLine15_2 + vLine15_3 + vLine15_4 + "\n";
                }
            }
            //end detail

            String SCAmt="";
            String qServiceCharges="select HCUnitCost,HCTax from stk_cus_inv_dt where Doc1No='" + Doc1No + "' " +
                    "and BlankLine='4' ";
            String SC=String.format(Locale.US, "%,.0f", dServiceCharges);
            Cursor rsSC=db.getQuery(qServiceCharges);
            while(rsSC.moveToNext()){
                Double dSCAmt    = rsSC.getDouble(0);
                SCAmt            = String.format(Locale.US, "%,.2f", dSCAmt);
            }
            String vLine16_1     = str_pad(SCAmt, 22, " ", "STR_PAD_LEFT");
            //start Total
            String qTotal = "SELECT IFNULL(H.HCNetAmt,0)AS HCNetAmt, IFNULL(H.HCDtTax,0) AS HCDtTax, "+
                        " IFNULL(R.CashAmt + R.ChangeAmt,0) AS CashAmt, IFNULL(R.ChangeAmt,0) AS ChangeAmt, "+
                        " IFNULL(SUM(D.HCLineAmt)-SUM(D.HCTax),0) AS AmtExTax,IFNULL(R.BalanceAmount,0) AS BalanceAmount, "+
                        " IFNULL(R.CC1Amt,0) AS CC1Amt, IFNULL(R.CC1No,'') AS CC1No,  IFNULL(R.CC1Code,'')AS CC1Code, " +
                        " IFNULL(SUM(D.Qty),0) AS ItemTender, IFNULL(H.AdjAmt,0)as AdjAmt "+
                        " FROM stk_cus_inv_hd H LEFT JOIN stk_receipt2 R ON R.Doc1No=H.Doc1No INNER JOIN stk_cus_inv_dt D ON D.Doc1No=H.Doc1No "+
                        " where H.Doc1No='" + Doc1No + "' and D.ItemCode<>'M999999SC' Group By H.Doc1No ";
            Double dPayAmt=0.00;
            Double dCC1Amt=0.00;
            Double dBalAmt=0.00;
            String tCC1Code="";
            String vLine22_2="";
            String vLine22_3="";
            String vLine22_4="";
            if(DBStatus.equals("2")) {
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", qTotal);
                jsonReq.put("action", "select");
                String rsTot = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rsTot);
                String resTot= jsonRes.getString("hasil");
                JSONArray jaTot = new JSONArray(resTot);
                JSONObject vTot = null;
                for (int i = 0; i < jaTot.length(); i++) {
                    vTot = jaTot.getJSONObject(i);
                    Double dTotAmt = vTot.getDouble("HCNetAmt");
                    String TotAmt  = String.format(Locale.US, "%,.2f", dTotAmt);
                    Double dTaxAmt = vTot.getDouble("HCDtTax");
                    String TaxAmt   = String.format(Locale.US, "%,.2f", dTaxAmt);
                    dPayAmt         = vTot.getDouble("CashAmt");
                    String PayAmt   = String.format(Locale.US, "%,.2f", dPayAmt);
                    Double dChAmt   = vTot.getDouble("ChangeAmt");
                    String ChAmt = String.format(Locale.US, "%,.2f", dChAmt);
                    Double dAmtExtTax = vTot.getDouble("AmtExTax");
                    String AmtExtTax = String.format(Locale.US, "%,.2f", dAmtExtTax);
                    dBalAmt = vTot.getDouble("BalanceAmount");
                    String BalAmt = String.format(Locale.US, "%,.2f", dBalAmt);
                    dCC1Amt = vTot.getDouble("CC1Amt");
                    String CC1Amt = String.format(Locale.US, "%,.2f", dCC1Amt);
                    if (dCC1Amt > 0) {
                        String tCC1No = vTot.getString("CC1No");
                        if(tCC1No.length()>4) {
                            vCC1No = tCC1No.substring(tCC1No.length() - 4);
                        }
                    }
                    String CC1Code      ="";
                    CC1Code             = vTot.getString("CC1Code");
                    Double dItemTender  = vTot.getDouble("ItemTender");
                    String ItemTender   = String.format(Locale.US, "%,.2f", dItemTender);
                    String AdjAmt       = String.format(Locale.US, "%,.2f",  vTot.getDouble("AdjAmt"));
                    vLine16 = str_pad(AmtExtTax, 22, " ", "STR_PAD_LEFT");
                    vLine17 = str_pad(TaxAmt, 22, " ", "STR_PAD_LEFT");
                    vLine18 = str_pad(AdjAmt, 22, " ", "STR_PAD_LEFT");
                    vLine19 = str_pad(TotAmt, 22, " ", "STR_PAD_LEFT");
                    vLine20 = str_pad(PayAmt, 22, " ", "STR_PAD_LEFT");
                    vLine21 = str_pad(ChAmt, 22, " ", "STR_PAD_LEFT");
                    vLine22 = str_pad(BalAmt, 22, " ", "STR_PAD_LEFT");
                    vLine22_2 = str_pad(ItemTender, 22, " ", "STR_PAD_LEFT");
                    vLine22_3 = str_pad(CC1Amt, 22, " ", "STR_PAD_LEFT");
                    vLine22_4 = str_pad("xxxxxxxxxxx" + vCC1No, 22, " ", "STR_PAD_LEFT");
                    tCC1Code = str_pad(CC1Code, 24, " ", "STR_PAD_RIGHT");
                }
            }else {
                Cursor rsTot = db.getQuery(qTotal);
                while (rsTot.moveToNext()) {
                    Double dTotAmt = rsTot.getDouble(0);
                    String TotAmt = String.format(Locale.US, "%,.2f", dTotAmt);
                    Double dTaxAmt = rsTot.getDouble(1);
                    String TaxAmt = String.format(Locale.US, "%,.2f", dTaxAmt);
                    dPayAmt = rsTot.getDouble(2);
                    String PayAmt = String.format(Locale.US, "%,.2f", dPayAmt);
                    Double dChAmt = rsTot.getDouble(3);
                    String ChAmt = String.format(Locale.US, "%,.2f", dChAmt);
                    Double dAmtExtTax = rsTot.getDouble(4);
                    String AmtExtTax = String.format(Locale.US, "%,.2f", dAmtExtTax);
                    dBalAmt = rsTot.getDouble(5);
                    String BalAmt = String.format(Locale.US, "%,.2f", dBalAmt);
                    dCC1Amt = rsTot.getDouble(6);
                    String CC1Amt = String.format(Locale.US, "%,.2f", dCC1Amt);
                    if (dCC1Amt > 0) {
                        String tCC1No = rsTot.getString(7);
                        vCC1No = tCC1No.substring(tCC1No.length() - 4);
                    }
                    String CC1Code = rsTot.getString(8);
                    Double dItemTender = rsTot.getDouble(9);
                    String ItemTender = String.format(Locale.US, "%,.2f", dItemTender);
                    String AdjAmt       = String.format(Locale.US, "%,.2f", rsTot.getDouble(10));
                    vLine16 = str_pad(AmtExtTax, 22, " ", "STR_PAD_LEFT");
                    vLine17 = str_pad(TaxAmt, 22, " ", "STR_PAD_LEFT");
                    vLine18 = str_pad(AdjAmt, 22, " ", "STR_PAD_LEFT");
                    vLine19 = str_pad(TotAmt, 22, " ", "STR_PAD_LEFT");
                    vLine20 = str_pad(PayAmt, 22, " ", "STR_PAD_LEFT");
                    vLine21 = str_pad(ChAmt, 22, " ", "STR_PAD_LEFT");
                    vLine22 = str_pad(BalAmt, 22, " ", "STR_PAD_LEFT");
                    vLine22_2 = str_pad(ItemTender, 22, " ", "STR_PAD_LEFT");
                    vLine22_3 = str_pad(CC1Amt, 22, " ", "STR_PAD_LEFT");
                    vLine22_4 = str_pad("xxxxxxxxxxx" + vCC1No, 22, " ", "STR_PAD_LEFT");
                    tCC1Code = str_pad(CC1Code, 24, " ", "STR_PAD_RIGHT");
                }
            }
            String tRefNo="Credit Card No   ";
            if(!PaymentType.equals("Credit Card")){
                tRefNo="Verification Code";
            }
            strPrint += "________________________________________________\n\n";
            if (!GSTNo.equals("NO")) {
                    strPrint += "Amount Exc Tax         : " + vLine16 + "\n";
                if(dServiceCharges>0){
                    strPrint += "Add  "+SC+"% Service Charges : " + vLine16_1 + "\n";
                }
                strPrint += "Add Total Tax Amount   : " + vLine17 + "\n";
                strPrint += "Rounding               : " + vLine18 + "\n";
                strPrint += "Total Amount Due       : " + vLine19 + "\n";
                if (dCC1Amt > 0) {
                    strPrint += tCC1Code + vLine22_3 + "\n";
                    strPrint += tRefNo+"      : " + vLine22_4 + "\n";
                } else {
                    strPrint += "Cash                   : " + vLine20 + "\n";
                    strPrint += "Change                 : " + vLine21 + "\n";
                }
            } else {
                strPrint += "Gross Amount           : " + vLine19 + "\n";
                if (dPayAmt > 0) {
                    strPrint += "Cash                   : " + vLine20 + "\n";
                    strPrint += "Change                 : " + vLine21 + "\n";
                }
                if (dCC1Amt > 0) {
                    strPrint += tCC1Code + ":" + vLine22_3 + "\n";
                    strPrint += tRefNo+"      : " + vLine22_4 + "\n";
                }
                strPrint += "Total Quantity Tender  : " + vLine22_2 + "\n";

            }
            if (dBalAmt > 0) {
                strPrint += "Balance Amount         : " + vLine22 + "\n";
            }
            if (!GSTNo.equals("NO")) {
                strPrint += "________________________________________________\n";
                strPrint += "Tax Summary \n";
                strPrint += "Tax Code      Rate      Goods Amt      Tax Amt  \n";
            }
            //end total

            //start GST
            String qGST = "select IFNULL(TaxRate1,0) as TaxRate1, DetailTaxCode, IFNULL(sum(HCLineAmt)-sum(HCTax),0) as GoodAmt," +
                    " IFNULL(sum(HCTax),0) as GSTAmt from stk_cus_inv_dt  where Doc1No='" + Doc1No + "' Group By DetailTaxCode  ";
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", qGST);
                jsonReq.put("action", "select");
                String rsGST = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(rsGST);
                String resGST= jsonRes.getString("hasil");
                Log.d("JSON GST",resGST);
                JSONArray jaGST = new JSONArray(resGST);
                JSONObject vTax = null;
                for (int i = 0; i < jaGST.length(); i++) {
                    vTax = jaGST.getJSONObject(i);
                    Double dTaxRate = vTax.getDouble("TaxRate1");
                    String TaxRate = String.format(Locale.US, "%,.2f", dTaxRate);
                    String TaxCode = vTax.getString("DetailTaxCode");
                    Double dGoodAmt = vTax.getDouble("GoodAmt");
                    String GoodAmt = String.format(Locale.US, "%,.2f", dGoodAmt);
                    Double dGSTAmt = vTax.getDouble("GSTAmt");
                    String GSTAmt = String.format(Locale.US, "%,.2f", dGSTAmt);
                    String vLine23_1 = str_pad(TaxCode, 6, " ", "STR_PAD_LEFT");
                    String vLine23_2 = str_pad(TaxRate, 12, " ", "STR_PAD_LEFT");
                    String vLine23_3 = str_pad(GoodAmt, 15, " ", "STR_PAD_LEFT");
                    String vLine23_4 = str_pad(GSTAmt, 15, " ", "STR_PAD_LEFT");
                    if (!GSTNo.equals("NO")) {
                        vLine23 = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                        strPrint += vLine23 + "\n";
                    }
                }
            }else {
                Cursor rsGST = db.getQuery(qGST);
                while (rsGST.moveToNext()) {
                    Double dTaxRate = rsGST.getDouble(0);
                    String TaxRate = String.format(Locale.US, "%,.2f", dTaxRate);
                    String TaxCode = rsGST.getString(1);
                    Double dGoodAmt = rsGST.getDouble(2);
                    String GoodAmt = String.format(Locale.US, "%,.2f", dGoodAmt);
                    Double dGSTAmt = rsGST.getDouble(3);
                    String GSTAmt = String.format(Locale.US, "%,.2f", dGSTAmt);
                    String vLine23_1 = str_pad(TaxCode, 6, " ", "STR_PAD_LEFT");
                    String vLine23_2 = str_pad(TaxRate, 12, " ", "STR_PAD_LEFT");
                    String vLine23_3 = str_pad(GoodAmt, 15, " ", "STR_PAD_LEFT");
                    String vLine23_4 = str_pad(GSTAmt, 15, " ", "STR_PAD_LEFT");
                    if (!GSTNo.equals("NO")) {
                        vLine23 = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                        strPrint += vLine23 + "\n";
                    }
                }
            }
            //end GST
            strPrint += "________________________________________________\n";
            if(!vFooter.isEmpty()){
                strPrint += Footer+"\n";
            }else {
                strPrint += "*Goods sold non-returnable & non-refundable\n";
                strPrint += "Thank you, please come again! \n";
            }



            if(ReceiptType.equals("Format02")){
                strPrint += "________________________________________________\n";
                strPrint += vLine8 +"\n";
                strPrint += vLine11 + vLine11_1 +"\n";
                strPrint += vLine12 +"\n";
                strPrint += vLine12_2+"\n";
                if(!Doc2No.isEmpty()){
                    strPrint += vLine12_1;
                }

            }
            strPrint += "________________________________________________\n\n\n";
            //vPostGlobalTaxYN
            //Log.d("PostGlobalTaxYN", vPostGlobalTaxYN);
            stringPrint = strPrint;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        //return stringPrint;
    }

    public String str_pad(String input, int length, String pad, String    sense)
    {
        int resto_pad = length - input.length();
        String padded = "";

        if (resto_pad <= 0){ return input; }

        if(sense.equals("STR_PAD_RIGHT"))
        {
            padded  = input;
            padded += _fill_string(pad,resto_pad);
        }
        else if(sense.equals("STR_PAD_LEFT"))
        {
            padded  = _fill_string(pad, resto_pad);
            padded += input;
        }
        else // STR_PAD_BOTH
        {
            int pad_left  = (int) Math.ceil(resto_pad/2);
            int pad_right = resto_pad - pad_left;

            padded  = _fill_string(pad, pad_left);
            padded += input;
            padded += _fill_string(pad, pad_right);
        }
        return padded;
    }


    protected String _fill_string(String pad, int resto ) {
        boolean first = true;
        String padded = "";

        if (resto >= pad.length())
        {
            for (int i = resto; i >= 0; i = i - pad.length())
            {
                if (i  >= pad.length())
                {
                    if (first){ padded = pad; } else { padded += pad; }
                }
                else
                {
                    if (first){ padded = pad.substring(0, i); } else { padded += pad.substring(0, i); }
                }
                first = false;
            }
        }
        else
        {
            padded = pad.substring(0,resto);
        }
        return padded;
    }

    private String replaceDec(String value){
        String newValue="";
        if(value.length()>12) {
            newValue= value.substring(0, value.length() - 3) + "-";
        }else{
            newValue=value;
        }
        return newValue;
    }
    private String replaceChar(String text){
        String newtext=text.replaceAll("[-\\[\\]^,'*:.!><~@#$%+=?|\"\\\\()]+", "");
        return newtext;
    }
}
