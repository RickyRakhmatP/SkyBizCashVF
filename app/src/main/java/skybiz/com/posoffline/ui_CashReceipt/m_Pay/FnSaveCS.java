package skybiz.com.posoffline.ui_CashReceipt.m_Pay;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.BaseApp;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.BytesUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothPrinter;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_USB.PrintingUSB;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI.PrintingWifi;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 30/11/2017.
 */

public class FnSaveCS extends AsyncTask<Void,Void,String> {
    Context c;
    String IPAddress,Password,DBName,UserName,URL,CurCode,vPostGlobalTaxYN,Doc1No,vPort;
    String GlobalTaxCode,TaxType,z, CusCode,CC1Code,CC1No,CC2Code,CC2No,LastNo,NewDoc,RunNo,stringPrint;
    String TypePrinter,NamePrinter,IPPrinter,UUIDs,isDuplicate;
    Double CashAmt,ChangeAmt,CC1Amt,CC2Amt,BalanceAmount;
    Double R_ate,HCGbTax,HCDtTax,HCGbDiscount,TotalAmt,GbTaxRate1;
    private boolean isBold, isUnderLine;
    private int record;
    Boolean isBT;
    int NewNo;
    TelephonyManager telephonyManager;

    public FnSaveCS(Context c, String Doc1No,Double CashAmt,Double ChangeAmt,Double CC1Amt,Double BalanceAmount,String CC1No, String CC1Code) {
        this.c = c;
        this.Doc1No = Doc1No;
        this.CashAmt = CashAmt;
        this.ChangeAmt=ChangeAmt;
        this.CC1Amt=CC1Amt;
        this.BalanceAmount=BalanceAmount;
        this.CC1No=CC1No;
        this.CC1Code=CC1Code;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //pd=new ProgressDialog(c);
       // pd.setTitle("Load");
       // pd.setMessage("Saving... Please Wait");
       // pd.show();
    }

    @Override
    protected String doInBackground(Void... params)
    {
        return this.fnsavecs();
    }

    @Override
    protected void onPostExecute(String vData) {
        super.onPostExecute(vData);
       // pd.dismiss();
        if(vData.equals("error")){
            Toast.makeText(c,"Failed, data cannot save", Toast.LENGTH_SHORT).show();
        }else{
           // Toast.makeText(c,"Successful, save cash receipt", Toast.LENGTH_SHORT).show();
            //parse
            //OrderParser p=new OrderParser(c,jsonData,rv);
            //p.execute();
        }
    }
    private String fnsavecs(){
        telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        CusCode="999999";
        z="error";
        isBold = true;
        isUnderLine = false;
        record = 17;
        try{
            GlobalTaxCode = "";
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            Cursor cur = db.getGeneralSetup();
            //get generalsetup
            while (cur.moveToNext()) {
                int RunNo = cur.getInt(0);
                CurCode = cur.getString(1);
                vPostGlobalTaxYN = cur.getString(6);
            }
            //get setting
            Cursor cur1=db.getAllSeting();
            while (cur1.moveToNext()) {
                IPAddress = cur1.getString(1);
                UserName = cur1.getString(2);
                Password = cur1.getString(3);
                DBName = cur1.getString(4);
            }

            Cursor cPrint=db.getSettingPrint();
            while (cPrint.moveToNext()) {
                TypePrinter = cPrint.getString(1);
                NamePrinter = cPrint.getString(2);
                IPPrinter = cPrint.getString(3);
                vPort = cPrint.getString(5);
            }

            URL = "jdbc:mysql://" + IPAddress + "/" + DBName;
            Connection conn = Connector.connect(URL, UserName, Password);
            if (conn == null) {
                z="error";
                //return  null;
            }


                String vDuplicate="select count(*) as jumlah from stk_cus_inv_hd where Doc1No='"+Doc1No+"' ";
                Statement stmtDup = conn.createStatement();
                stmtDup.execute(vDuplicate);
                ResultSet rsDup = stmtDup.getResultSet();
                //Log.d("SQL",vDuplicate);
                while(rsDup.next()) {
                    isDuplicate=rsDup.getString("jumlah");
                }
                //Log.d("HASIL",isDuplicate);
                stmtDup.close();
                if(!isDuplicate.equals("0")){
                    fnsavelastno();
                    z="error";
                }else {
                   // z="success";
                    if (vPostGlobalTaxYN.equals("1")) {
                        String vDefault = "SELECT a.RetailTaxCode,b.R_ate,b.TaxType,b.TaxCode FROM sys_general_setup3 a INNER JOIN stk_tax b ON a.RetailTaxCode=b.TaxCode";
                        Statement stmtDef = conn.createStatement();
                        stmtDef.execute(vDefault);
                        ResultSet rsDef = stmtDef.getResultSet();
                        while (rsDef.next()) {
                            GlobalTaxCode = rsDef.getString("RetailTaxCode");
                            TaxType = rsDef.getString("TaxType");
                            R_ate = Double.parseDouble(rsDef.getString("TaxType"));
                        }
                        stmtDef.close();
                    } else {

                        TaxType = "0";
                        R_ate = 6.00;
                    }
                    FnCalculateHCNetAmt vNetAmt = fncalculatehcnetamt(deviceId, GlobalTaxCode, TaxType, R_ate, URL, UserName, Password);
                    HCGbTax = vNetAmt.getHCGbTax();
                    TotalAmt = vNetAmt.getHCNetAmt();
                    GbTaxRate1 = vNetAmt.getGbTaxRate1();
                    HCDtTax = vNetAmt.getHCDtTax();
                    HCGbDiscount = vNetAmt.getHCGbDiscount();

                    SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat DateCurr1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat DateCurr2 = new SimpleDateFormat("HH:mm:ss");
                    Date date = new Date();
                    String datedShort = DateCurr.format(date);
                    String datedTime = DateCurr1.format(date);
                    String vTime = DateCurr2.format(date);

                    //insert stk_cus_inv_hd
                    String vHeader = "INSERT INTO stk_cus_inv_hd(Doc1No,Doc2No,Doc3No," +
                            " D_ate,D_ateTime,CusCode,MemberNo,DueDate,TaxDate," +
                            " CurCode, CurRate1, CurRate2, CurRate3," +
                            " TermCode, D_ay, Attention, AddCode," +
                            " BatchCode, GbDisRate1, GbDisRate2, GbDisRate3, HCGbDiscount," +
                            " GbTaxRate1, GbTaxRate2, GbTaxRate3, HCGbTax, GlobalTaxCode, HCDtTax," +
                            " HCNetAmt, AdjAmt, GbOCCode, GbOCRate, GbOCAmt," +
                            " DocType, ApprovedYN,RetailYN,UDRunNo," +
                            " CusName, Address, ContactTel, Email, NRICNo, GSTNo, L_ink, Status)Values (" +
                            " '" + Doc1No + "', '', '', " +
                            " '" + datedShort + "', '" + datedTime + "', '" + CusCode + "', '" + CusCode + "', '" + datedShort + "', '" + datedShort + "'," +
                            " '" + CurCode + "', '1', '1', '1', " +
                            " '1', '0', 'android pos', ''," +
                            " '', '0', '0', '0', '" + HCGbDiscount + "'," +
                            " '" + GbTaxRate1 + "', '0', '0', '" + HCGbTax + "', '" + GlobalTaxCode + "', '" + HCDtTax + "', " +
                            " '" + TotalAmt + "', '0', '', '0', '0'," +
                            " 'CS','1', '1', '0', " +
                            " 'Cash Sales', '', '', '', '', '', '1','Used')";
                    Statement stmtHeader = conn.createStatement();
                    stmtHeader.execute(vHeader);
                    stmtHeader.close();

                    //insert stk_receipt2
                    //CC1Amt=0.00;
                    String vCC1ChargesRate="0";
                    String vCC1ChargesAmt="0";
                    if(CC1Amt>0 || !CC1No.equals("")){
                        Cursor cPayType=db.getRowPayType(CC1Code);
                        while (cPayType.moveToNext()) {
                            String Charges1=cPayType.getString(0);
                            String PaidByCompanyYN=cPayType.getString(1);
                            Double CC1ChargesRate=Double.parseDouble(Charges1);
                            Double CC1ChargesAmt=CC1Amt*CC1ChargesRate/100;
                            if(PaidByCompanyYN.equals("0")){
                                CC1Amt=CC1Amt+CC1ChargesAmt;
                            }
                            vCC1ChargesAmt=CC1ChargesAmt.toString();
                            vCC1ChargesRate=CC1ChargesRate.toString();
                        }
                    }
                    db.closeDB();
                    CC2Amt = 0.00;
                    CC2Code = "";
                    CC2No = "";
                    //BalanceAmount=0.00;
                    String vReceipt = "INSERT INTO stk_receipt2 (D_ate, T_ime, D_ateTime, Doc1No, CashAmt," +
                            " CC1Code, CC1Amt, CC1No, CC1Expiry, CC1ChargesAmt, CC1ChargesRate," +
                            " CC2Code, CC2Amt, CC2No, CC2Expiry, CC2ChargesAmt, CC2ChargesRate," +
                            " Cheque1Code, Cheque1Amt, Cheque1No," +
                            " Cheque2Code, Cheque2Amt, Cheque2No," +
                            " PointAmt, VoucherAmt, CurCode, CurRate, FCAmt," +
                            " CusCode, BalanceAmount, ChangeAmt, CounterCode, UserCode, DocType )" +
                            " VALUES ('" + datedShort + "', '" + vTime + "', '" + datedTime + "', '" + Doc1No + "', '" + CashAmt + "'," +
                            " '" + CC1Code + "', '" + CC1Amt + "', '" + CC1No + "', '', '"+vCC1ChargesAmt+"', '"+vCC1ChargesRate+"', " +
                            " '" + CC2Code + "', '" + CC2Amt + "', '" + CC2No + "', '', '0', '0', " +
                            " '', '0', '', " +
                            " '', '0', '', " +
                            "  '0', '0', '" + CurCode + "', '1', '0'," +
                            " '" + CusCode + "', '" + BalanceAmount + "', '" + ChangeAmt + "', '', '', 'CS' )";
                    Statement stmtReceipt = conn.createStatement();
                    stmtReceipt.execute(vReceipt);
                    stmtReceipt.close();
                    //insert stk_cus_inv detail
                    String vDetail = "INSERT INTO stk_cus_inv_dt (Doc1No, N_o, ItemCode, Description, Qty, FactorQty, UOM, UOMSingular, " +
                            "HCUnitCost, DisRate1, HCDiscount, TaxRate1, HCTax, DetailTaxCode, HCLineAmt, " +
                            "BranchCode, DepartmentCode, ProjectCode, SalesPersonCode, LocationCode,WarrantyDate,LineNo, BlankLine, DocType, AnalysisCode2 )" +
                            "SELECT '" + Doc1No + "', N_o, ItemCode, Description, Qty, FactorQty, UOM, UOMSingular, " +
                            "HCUnitCost, DisRate1, HCDiscount, TaxRate1, HCTax, DetailTaxCode, HCLineAmt, " +
                            "BranchCode, DepartmentCode, ProjectCode, SalesPersonCode, LocationCode,WarrantyDate,LineNo, BlankLine, 'CS', AnalysisCode2 " +
                            "FROM cloud_cus_inv_dt WHERE ComputerName='" + deviceId + "' ";
                    Statement stmtDetail = conn.createStatement();
                    stmtDetail.execute(vDetail);
                    stmtDetail.close();
                    //insert stk_detail_trn_out
                    String vdetailOut = "insert into stk_detail_trn_out(ItemCode,Doc3No,D_ate,QtyOUT,FactorQty,UOM,UnitPrice,CusCode,DocType3,Doc3NoRunNo,LocationCode,L_ink,HCTax,BookDate)" +
                            "SELECT ItemCode,Doc1No,'" + datedShort + "',Qty,FactorQty,UOMSingular,HCUnitCost,'" + CusCode + "',DocType,RunNo, LocationCode,'1',HCTax,'" + datedShort + "' from stk_cus_inv_dt where Doc1No='" + Doc1No + "'  ";
                    Statement stmtDetail2 = conn.createStatement();
                    stmtDetail2.execute(vdetailOut);
                    stmtDetail2.close();
                    fnsavelastno();
                    //fnsavelastno
                    /*String vLastNo = "SELECT LastNo,RunNo FROM sys_runno_dt WHERE '" + datedShort + "' >= DateFrom AND '" + datedShort + "' <= DateTo AND RunnoCode='CS' ORDER BY RunNo ASC LIMIT 1 ";
                    Statement stmtLastNo = conn.createStatement();
                    stmtLastNo.execute(vLastNo);
                    ResultSet rsLast = stmtLastNo.getResultSet();
                    while (rsLast.next()) {
                        LastNo = rsLast.getString("LastNo");
                        RunNo = rsLast.getString("RunNo");
                        NewNo = (Integer.parseInt(LastNo)) + 1;
                    }

                    NewDoc = String.valueOf(NewNo);
                    Log.d("LastNewNo", NewDoc);

                    //stmtLastNo.close();
                    String vUpdate = "update sys_runno_dt set LastNo='" + NewDoc + "' where RunNo='" + RunNo + "' ";
                    Statement stmtUp = conn.createStatement();
                    stmtUp.execute(vUpdate);

                    */
                    NewDoc ="xxxxxx";
                    String vDelete = "DELETE FROM cloud_cus_inv_dt where ComputerName='" + deviceId + "' ";
                    Log.d("DEL", vDelete);
                    Statement stmtDel1 = conn.createStatement();
                    stmtDel1.execute(vDelete);

                    if (TypePrinter.equals("AIDL")) {
                        stringPrint = fngenerate58(Doc1No, URL, UserName, Password);
                    } else {
                        stringPrint = fngenerate78(Doc1No, URL, UserName, Password);
                    }


                    if (TypePrinter.equals("AIDL")) {
                        //Log.d("STR PRINT",stringPrint);
                        byte[] bytes = stringPrint.getBytes();
                        String content2 = BytesUtil.getHexStringFromBytes(bytes);
                        AidlUtil.getInstance().sendRawData(BytesUtil.getBytesFromHexString(content2));
                    } else if (TypePrinter.equals("Bluetooth")) {
                        //Log.d("STR PRINT"+NamePrinter,stringPrint);
                        BluetoothPrinter fncheck = new BluetoothPrinter();
                        isBT = fncheck.fnBluetooth(c, NamePrinter, stringPrint);
                        if (isBT == false) {
                            NewDoc = "error print";
                        }
                    } else if (TypePrinter.equals("Wifi")) {
                        PrintingWifi fnprintw = new PrintingWifi();
                        int Port=Integer.parseInt(vPort);
                        isBT = fnprintw.fnprintwifi(c, IPPrinter, Port, stringPrint);
                        if (isBT == false) {
                            NewDoc = "error print";
                        }
                    } else if (TypePrinter.equals("USB")) {
                        PrintingUSB fnprintu = new PrintingUSB();
                        isBT = fnprintu.fnprintusb(c, stringPrint);
                        if (isBT == false) {
                            NewDoc = "error print";
                        }
                    } else {
                        //No Printer
                    }
                    //fnprint(c,Doc1No,URL,UserName,Password);
                    z = NewDoc;
                }
            return z;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }
    public void fnsavelastno(){
        try {
            Connection conn = Connector.connect(URL, UserName, Password);
            if (conn == null) {
                z="error";
            }else {
                SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String datedShort = DateCurr.format(date);
                String vLastNo = "SELECT LastNo,RunNo FROM sys_runno_dt WHERE '" + datedShort + "' >= DateFrom AND '" + datedShort + "' <= DateTo AND RunnoCode='CS' ORDER BY RunNo ASC LIMIT 1 ";
                Statement stmtLastNo = conn.createStatement();
                stmtLastNo.execute(vLastNo);
                ResultSet rsLast = stmtLastNo.getResultSet();
                while (rsLast.next()) {
                    LastNo = rsLast.getString("LastNo");
                    RunNo = rsLast.getString("RunNo");
                    NewNo = (Integer.parseInt(LastNo)) + 1;
                }
                NewDoc = String.valueOf(NewNo);
                Log.d("LastNewNo", NewDoc);
                String vUpdate = "update sys_runno_dt set LastNo='" + NewDoc + "' where RunNo='" + RunNo + "' ";
                Statement stmtUp = conn.createStatement();
                stmtUp.execute(vUpdate);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public String fngenerate58(String Doc1No,String URL, String UserName,String Password){
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
        String CusCode="";
        String CusName="";
        String TelCus="";
        String AdddresCus="";
        String SalesPersonCode="";
        String D_ateTime="";
        String Disc2="";
        String Minus2="";
        try{
            Connection conn = Connector.connect(URL, UserName, Password);
            if (conn == null) {

            }
            //start query header
            String Company="select CurCode,CompanyName,CompanyCode,GSTNo,SUBSTRING(Address,1,32) as Address,Tel1,Fax1,CompanyEmail,ComTown,ComState,ComCountry FROM companysetup ";
            Statement stmtCom = conn.createStatement();
            stmtCom.execute(Company);
            ResultSet rsCom = stmtCom.getResultSet();
            while (rsCom.next()) {
                CompanyName = rsCom.getString("CompanyName");
                CompanyCode = rsCom.getString("CompanyCode");
                GSTNo       = rsCom.getString("GSTNo");
                Address     = rsCom.getString("Address");
                //Address     = Address.substring(0, 32);
                ComTown     = rsCom.getString("ComTown");
                ComState    = rsCom.getString("ComState");
                ComCountry  = rsCom.getString("ComCountry");
                Tel         = rsCom.getString("Tel1");
                Fax         = rsCom.getString("Fax1");
            }
            vLine1   =str_pad(CompanyName, 32, " ", "STR_PAD_BOTH");
            vLine2   =str_pad("Co.Reg.No.: "+CompanyCode, 32, " ", "STR_PAD_BOTH");
            vLine3   =str_pad("GST Reg.No.: "+GSTNo, 32, " ", "STR_PAD_BOTH");
            vLine4   =str_pad(Address, 32, " ", "STR_PAD_BOTH");
            // vLine4_1  str_pad(Address, 32, " ", "STR_PAD_BOTH");
            vLine4_1   =str_pad(ComTown+" "+ComState, 32, " ", "STR_PAD_BOTH");
            vLine5   =str_pad(Tel, 32, " ", "STR_PAD_BOTH");
            vLine6   =str_pad(Fax, 32, " ", "STR_PAD_BOTH");
            vLine7   =str_pad("TAX INVOICE", 32, " ", "STR_PAD_BOTH");
            vLine8   =str_pad("BILL TO : ", 32, " ", "STR_PAD_RIGHT");
            //end query header

            //start query customer
            String qCustomer="SELECT H.Doc1No,H.D_ate,H.Attention,H.D_ateTime,C.CusCode,C.CusName,C.Tel,C.Address,C.SalesPersonCode FROM stk_cus_inv_hd H inner join customer C ON C.CusCode=H.CusCode where H.Doc1No='"+Doc1No+"' ";
            Statement stmtCus = conn.createStatement();
            stmtCus.execute(qCustomer);
            ResultSet rsCus = stmtCus.getResultSet();
            while (rsCus.next()) {
                CusCode         = rsCus.getString("CusCode");
                CusName         = rsCus.getString("CusName");
                AdddresCus      = rsCus.getString("Address");
                SalesPersonCode = rsCus.getString("SalesPersonCode");
                TelCus          = rsCus.getString("Tel");
                D_ateTime        = rsCus.getString("D_ateTime");
            }
            vLine9=str_pad(CusName, 32, " ", "STR_PAD_RIGHT");
            //vLine10=str_pad(AdddresCus, 32, " ", "STR_PAD_RIGHT");
            vLine11=str_pad(D_ateTime, 32, " ", "STR_PAD_RIGHT");
            vLine12=str_pad(Doc1No, 32, " ", "STR_PAD_RIGHT");
            vLine13=str_pad(CurCode+" ", 32, " ", "STR_PAD_LEFT");
            //end query customer

                strPrint += vLine1+"\n";
                strPrint += vLine2+"\n";
                strPrint += vLine3+"\n";
                strPrint += vLine4+"\n";
                strPrint += vLine4_1+"\n";
                if(!Tel.equals("")) {
                    // strPrint += vLine5 + "\n";
                }
                if(!Fax.equals("")){
                    // strPrint += vLine6+"\n";
                }
                strPrint += "________________________________\n";
                strPrint += vLine7+"\n";
                strPrint += "________________________________\n";
                //end header

                //customer
                strPrint += vLine8+"\n";
                strPrint += vLine9+"\n";
                strPrint += vLine10+"\n";
                strPrint += vLine11+"\n";
                strPrint += vLine12+"\n";
                strPrint += "________________________________\n";
                strPrint += vLine13+"\n";
                strPrint += "________________________________\n";
                //end customer

                //detail
                String qDetail="select ItemCode,FORMAT(Qty,2) as QTY,UOM,DetailTaxCode,SUBSTRING(Description,1,45) as Description,FORMAT(HCUnitCost,2) as HCUnitCost,FORMAT(HCLineAmt,2) AS HCLineAmt,FORMAT(DisRate1,2) as DisRate1,FORMAT(HCDiscount,2) as HCDiscount from stk_cus_inv_dt Where Doc1No='"+Doc1No+"' ";
                Statement stmtDt = conn.createStatement();
                stmtDt.execute(qDetail);
                ResultSet rsDt = stmtDt.getResultSet();
                while (rsDt.next()) {
                    String ItemCode         = rsDt.getString("ItemCode");
                    String Qty              = rsDt.getString("Qty");
                    String UOM              = rsDt.getString("UOM");
                    String DetailTaxCode    = rsDt.getString("DetailTaxCode");
                    String Description      = rsDt.getString("Description");
                    String HCUnitCost       = rsDt.getString("HCUnitCost");
                    String HCLineAmt        = rsDt.getString("HCLineAmt");
                    String DisRate1         = rsDt.getString("DisRate1");
                    String HCDiscount       = rsDt.getString("HCDiscount");
                    if(!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")){
                        Disc2		    =""+HCDiscount+" =";
                        Minus2	        =" - ";
                    }else{
                        Disc2			=" =";
                        Minus2	        ="   ";
                    }

                    vLine14                 =str_pad(Description, 32, " ", "STR_PAD_RIGHT");
                    String vLine15_1        =str_pad(Qty, 4, " ", "STR_PAD_LEFT");
                    String vLine15_2        =str_pad(HCUnitCost, 9, " ", "STR_PAD_LEFT");
                    String vLine15_3        =str_pad(Disc2, 4, " ", "STR_PAD_LEFT");
                    String vLine15_4        =str_pad(HCLineAmt, 9, " ", "STR_PAD_LEFT");
                    strPrint 				+= vLine14+"\n";
                    strPrint 				+= ""+vLine15_1+" x"+vLine15_2+Minus2+vLine15_3+vLine15_4+"\n";

                }
                //end detail

                //start Total
                String qTotal="select FORMAT(H.HCNetAmt,2)as HCNetAmt,FORMAT(H.HCDtTax,2) as HCDtTax,FORMAT(R.CashAmt,2) as CashAmt, FORMAT(R.ChangeAmt,2) as ChangeAmt,FORMAT(sum(D.HCLineAmt)-sum(D.HCTax),2) as AmtExTax,FORMAT(R.BalanceAmount,2) as BalanceAmount from stk_cus_inv_hd H inner join stk_receipt2 R on R.Doc1No=H.Doc1No inner join stk_cus_inv_dt D ON D.Doc1No=H.Doc1No where H.Doc1No='"+Doc1No+"' Group By H.Doc1No ";
                Statement stmtTotal = conn.createStatement();
                stmtTotal.execute(qTotal);
                ResultSet rsTot = stmtTotal.getResultSet();
                while (rsTot.next()) {
                    String AmtExtTax    =rsTot.getString("AmtExTax");
                    String TaxAmt       =rsTot.getString("HCDtTax");
                    String TotAmt       =rsTot.getString("HCNetAmt");
                    String PayAmt       =rsTot.getString("CashAmt");
                    String ChAmt        =rsTot.getString("ChangeAmt");
                    String BalAmt       =rsTot.getString("BalanceAmount");
                    vLine16             =str_pad(AmtExtTax, 11, " ", "STR_PAD_LEFT");
                    vLine17             =str_pad(TaxAmt, 11, " ", "STR_PAD_LEFT");
                    vLine18             =str_pad("0.00", 11, " ", "STR_PAD_LEFT");
                    vLine19             =str_pad(TotAmt, 11, " ", "STR_PAD_LEFT");
                    vLine20             =str_pad(PayAmt, 11, " ", "STR_PAD_LEFT");
                    vLine21             =str_pad(ChAmt, 11, " ", "STR_PAD_LEFT");
                    vLine22             =str_pad(BalAmt, 11, " ", "STR_PAD_LEFT");
                    strPrint 			+= "________________________________\n\n";
                    strPrint 			+= "Amount Exc Tax    : "+vLine16+"\n";
                    strPrint 			+= "Add Total GST Amt : "+vLine17+"\n";
                    strPrint 			+= "Rounding          : "+vLine18+"\n";
                    strPrint 			+= "Total Amount Due  : "+vLine19+"\n";
                    strPrint 			+= "Paid Amount       : "+vLine20+"\n";
                    strPrint 			+= "Change Amount     : "+vLine21+"\n";
                    if(BalAmt.length()>0){
                        strPrint 		+= "Balance Amount    : "+vLine22+"\n";
                    }
                    strPrint 			+= "________________________________\n";
                    strPrint 			+= "GST Summary \n";
                    strPrint 			+= "Code  Rate   Goods Amt  GST Amt\n";

                }
                //end total

                //start GST
                String qGST="select FORMAT(TaxRate1,2)as TaxRate1,DetailTaxCode,FORMAT(sum(HCLineAmt)-sum(HCTax),2) as GoodAmt,FORMAT(sum(HCTax),2) as GSTAmt from stk_cus_inv_dt  Where Doc1No='"+Doc1No+"' Group By DetailTaxCode  ";
                Statement stmtGST = conn.createStatement();
                stmtGST.execute(qGST);
                ResultSet rsGST = stmtGST.getResultSet();
                while (rsGST.next()) {
                    String TaxRate       =rsGST.getString("TaxRate1");
                    String TaxCode       =rsGST.getString("DetailTaxCode");
                    String GoodAmt       =rsGST.getString("GoodAmt");
                    String GSTAmt        =rsGST.getString("GSTAmt");
                    String vLine23_1     =str_pad(TaxCode, 4, " ", "STR_PAD_LEFT");
                    String vLine23_2     =str_pad(TaxRate, 6, " ", "STR_PAD_LEFT");
                    String vLine23_3     =str_pad(GoodAmt, 11, " ", "STR_PAD_LEFT");
                    String vLine23_4     =str_pad(GSTAmt, 11, " ", "STR_PAD_LEFT");
                    vLine23              =vLine23_1+vLine23_2+vLine23_3+vLine23_4;
                    strPrint 			 += vLine23+"\n";
                }
                //end GST
                strPrint 				+= "________________________________\n";
                strPrint 				+= "*Goods sold non-returnable & non-refundable\n";
                strPrint 				+= "Thank you, please come again! \n";
                strPrint 				+= "________________________________\n \n\n";
               // Log.d("PRINT",strPrint);
                //byte[] bytes = strPrint.getBytes();
               // String content2= BytesUtil.getHexStringFromBytes(bytes);
                //AidlUtil.getInstance().sendRawData(BytesUtil.getBytesFromHexString(content2));
            stringPrint=strPrint;
            return stringPrint;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return stringPrint;
    }



    public String fngenerate78(String Doc1No,String URL, String UserName,String Password){
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
        String CusCode="";
        String CusName="";
        String TelCus="";
        String AdddresCus="";
        String SalesPersonCode="";
        String D_ateTime="";
        String Disc2="";
        String Minus2="";
        try{
            Connection conn = Connector.connect(URL, UserName, Password);
            if (conn == null) {

            }
            //start query header
            String Company="select CurCode,CompanyName,CompanyCode,GSTNo,SUBSTRING(Address,1,48) as Address,Tel1,Fax1,CompanyEmail,ComTown,ComState,ComCountry FROM companysetup ";
            Statement stmtCom = conn.createStatement();
            stmtCom.execute(Company);
            ResultSet rsCom = stmtCom.getResultSet();
            while (rsCom.next()) {
                CompanyName = rsCom.getString("CompanyName");
                CompanyCode = rsCom.getString("CompanyCode");
                GSTNo       = rsCom.getString("GSTNo");
                Address     = rsCom.getString("Address");
                //Address     = Address.substring(1, 47);
                ComTown     = rsCom.getString("ComTown");
                ComState    = rsCom.getString("ComState");
                ComCountry  = rsCom.getString("ComCountry");
                Tel         = rsCom.getString("Tel1");
                Fax         = rsCom.getString("Fax1");
            }
            vLine1   =str_pad(CompanyName, 48, " ", "STR_PAD_BOTH");
            vLine2   =str_pad("Co.Reg.No.: "+CompanyCode, 48, " ", "STR_PAD_BOTH");
            vLine3   =str_pad("GST Reg.No.: "+GSTNo, 48, " ", "STR_PAD_BOTH");
            vLine4   =str_pad(Address, 48, " ", "STR_PAD_BOTH");
            // vLine4_1  str_pad(Address, 32, " ", "STR_PAD_BOTH");
            vLine4_1   =str_pad(ComTown+" "+ComState, 48, " ", "STR_PAD_BOTH");
            vLine5   =str_pad(Tel, 48, " ", "STR_PAD_BOTH");
            vLine6   =str_pad(Fax, 48, " ", "STR_PAD_BOTH");
            vLine7   =str_pad("TAX INVOICE", 48, " ", "STR_PAD_BOTH");
            vLine8   =str_pad("BILL TO : ", 48, " ", "STR_PAD_RIGHT");
            //end query header

            //start query customer
            String qCustomer="SELECT H.Doc1No,H.D_ate,H.Attention,H.D_ateTime,C.CusCode,C.CusName,C.Tel,C.Address,C.SalesPersonCode FROM stk_cus_inv_hd H inner join customer C ON C.CusCode=H.CusCode where H.Doc1No='"+Doc1No+"' ";
            Statement stmtCus = conn.createStatement();
            stmtCus.execute(qCustomer);
            ResultSet rsCus = stmtCus.getResultSet();
            while (rsCus.next()) {
                CusCode         = rsCus.getString("CusCode");
                CusName         = rsCus.getString("CusName");
                AdddresCus      = rsCus.getString("Address");
                SalesPersonCode = rsCus.getString("SalesPersonCode");
                TelCus          = rsCus.getString("Tel");
                D_ateTime        = rsCus.getString("D_ateTime");
            }
            vLine9=str_pad(CusName, 48, " ", "STR_PAD_RIGHT");
            //vLine10=str_pad(AdddresCus, 32, " ", "STR_PAD_RIGHT");
            vLine11=str_pad(D_ateTime, 48, " ", "STR_PAD_RIGHT");
            vLine12=str_pad(Doc1No, 48, " ", "STR_PAD_RIGHT");
            vLine13=str_pad(CurCode+" ", 48, " ", "STR_PAD_LEFT");
            //end query customer

            strPrint += vLine1+"\n";
            strPrint += vLine2+"\n";
            strPrint += vLine3+"\n";
            strPrint += vLine4+"\n";
            strPrint += vLine4_1+"\n";
            if(!Tel.equals("")) {
                // strPrint += vLine5 + "\n";
            }
            if(!Fax.equals("")){
                // strPrint += vLine6+"\n";
            }
            strPrint += "________________________________________________\n";
            strPrint += vLine7+"\n";
            strPrint += "________________________________________________\n";
            //end header

            //customer
            strPrint += vLine8+"\n";
            strPrint += vLine9+"\n";
            strPrint += vLine10+"\n";
            strPrint += vLine11+"\n";
            strPrint += vLine12+"\n";
            strPrint += "________________________________________________\n";
            strPrint += vLine13+"\n";
            strPrint += "________________________________________________\n";
            //end customer

            //detail
            String qDetail="select ItemCode,FORMAT(Qty,2) as QTY,UOM,DetailTaxCode,SUBSTRING(Description,1,45) as Description,FORMAT(HCUnitCost,2) as HCUnitCost,FORMAT(HCLineAmt,2) AS HCLineAmt,FORMAT(DisRate1,2) as DisRate1,FORMAT(HCDiscount,2) as HCDiscount from stk_cus_inv_dt Where Doc1No='"+Doc1No+"' ";
            Statement stmtDt = conn.createStatement();
            stmtDt.execute(qDetail);
            ResultSet rsDt = stmtDt.getResultSet();
            while (rsDt.next()) {
                String ItemCode         = rsDt.getString("ItemCode");
                String Qty              = rsDt.getString("Qty");
                String UOM              = rsDt.getString("UOM");
                String DetailTaxCode    = rsDt.getString("DetailTaxCode");
                String Description      = rsDt.getString("Description");
                String HCUnitCost       = rsDt.getString("HCUnitCost");
                String HCLineAmt        = rsDt.getString("HCLineAmt");
                String DisRate1         = rsDt.getString("DisRate1");
                String HCDiscount       = rsDt.getString("HCDiscount");
                if(!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")){
                    Disc2		    =""+HCDiscount+" =";
                    Minus2	        =" - ";
                }else{
                    Disc2			=" =";
                    Minus2	        ="   ";
                }

                vLine14                 =str_pad(Description, 48, " ", "STR_PAD_RIGHT");
                String vLine15_1        =str_pad(Qty, 6, " ", "STR_PAD_LEFT");
                String vLine15_2        =str_pad(HCUnitCost, 13, " ", "STR_PAD_LEFT");
                String vLine15_3        =str_pad(Disc2, 10, " ", "STR_PAD_LEFT");
                String vLine15_4        =str_pad(HCLineAmt, 13, " ", "STR_PAD_LEFT");
                strPrint 				+= vLine14+"\n";
                strPrint 				+= ""+vLine15_1+" x"+vLine15_2+Minus2+vLine15_3+vLine15_4+"\n";

            }
            //end detail

            //start Total
            String qTotal="select FORMAT(H.HCNetAmt,2)as HCNetAmt,FORMAT(H.HCDtTax,2) as HCDtTax,FORMAT(R.CashAmt,2) as CashAmt, FORMAT(R.ChangeAmt,2) as ChangeAmt,FORMAT(sum(D.HCLineAmt)-sum(D.HCTax),2) as AmtExTax,FORMAT(R.BalanceAmount,2) as BalanceAmount from stk_cus_inv_hd H inner join stk_receipt2 R on R.Doc1No=H.Doc1No inner join stk_cus_inv_dt D ON D.Doc1No=H.Doc1No where H.Doc1No='"+Doc1No+"' Group By H.Doc1No ";
            Statement stmtTotal = conn.createStatement();
            stmtTotal.execute(qTotal);
            ResultSet rsTot = stmtTotal.getResultSet();
            while (rsTot.next()) {
                String AmtExtTax    =rsTot.getString("AmtExTax");
                String TaxAmt       =rsTot.getString("HCDtTax");
                String TotAmt       =rsTot.getString("HCNetAmt");
                String PayAmt       =rsTot.getString("CashAmt");
                String ChAmt        =rsTot.getString("ChangeAmt");
                String BalAmt       =rsTot.getString("BalanceAmount");
                vLine16             =str_pad(AmtExtTax, 22, " ", "STR_PAD_LEFT");
                vLine17             =str_pad(TaxAmt, 22, " ", "STR_PAD_LEFT");
                vLine18             =str_pad("0.00", 22, " ", "STR_PAD_LEFT");
                vLine19             =str_pad(TotAmt, 22, " ", "STR_PAD_LEFT");
                vLine20             =str_pad(PayAmt, 22, " ", "STR_PAD_LEFT");
                vLine21             =str_pad(ChAmt, 22, " ", "STR_PAD_LEFT");
                vLine22             =str_pad(BalAmt, 22, " ", "STR_PAD_LEFT");
                strPrint            += "________________________________________________\n";
                strPrint 			+= "Amount Exc Tax         : "+vLine16+"\n";
                strPrint 			+= "Add Total GST Amt      : "+vLine17+"\n";
                strPrint 			+= "Rounding               : "+vLine18+"\n";
                strPrint 			+= "Total Amount Due       : "+vLine19+"\n";
                strPrint 			+= "Paid Amount            : "+vLine20+"\n";
                strPrint 			+= "Change Amount          : "+vLine21+"\n";
                if(BalAmt.length()>0){
                    strPrint 		+= "Balance Amount         : "+vLine22+"\n";
                }
                strPrint            += "________________________________________________\n";
                strPrint 			+= "GST Summary \n";
                strPrint 			+= "   Code       Rate      Goods Amt      GST Amt  \n";

            }
            //end total

            //start GST
            String qGST="select FORMAT(TaxRate1,2)as TaxRate1,DetailTaxCode,FORMAT(sum(HCLineAmt)-sum(HCTax),2) as GoodAmt,FORMAT(sum(HCTax),2) as GSTAmt from stk_cus_inv_dt  Where Doc1No='"+Doc1No+"' Group By DetailTaxCode  ";
            Statement stmtGST = conn.createStatement();
            stmtGST.execute(qGST);
            ResultSet rsGST = stmtGST.getResultSet();
            while (rsGST.next()) {
                String TaxRate       =rsGST.getString("TaxRate1");
                String TaxCode       =rsGST.getString("DetailTaxCode");
                String GoodAmt       =rsGST.getString("GoodAmt");
                String GSTAmt        =rsGST.getString("GSTAmt");
                String vLine23_1     =str_pad(TaxCode, 6, " ", "STR_PAD_LEFT");
                String vLine23_2     =str_pad(TaxRate, 12, " ", "STR_PAD_LEFT");
                String vLine23_3     =str_pad(GoodAmt, 15, " ", "STR_PAD_LEFT");
                String vLine23_4     =str_pad(GSTAmt, 15, " ", "STR_PAD_LEFT");
                vLine23              =vLine23_1+vLine23_2+vLine23_3+vLine23_4;
                strPrint 			 += vLine23+"\n";
            }
            //end GST
            strPrint                += "________________________________________________\n";
            strPrint 				+= "*Goods sold non-returnable & non-refundable\n";
            strPrint 				+= "Thank you, please come again! \n";
            strPrint 				+= "________________________________________________\n \n\n";
            stringPrint=strPrint;
            return stringPrint;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return stringPrint;
    }





    public void fnprint(Context c,String Doc1No,String URL, String UserName,String Password){
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
        String CusCode="";
        String CusName="";
        String TelCus="";
        String AdddresCus="";
        String SalesPersonCode="";
        String D_ateTime="";
        String Disc2="";
        String Minus2="";
        try{
            Connection conn = Connector.connect(URL, UserName, Password);
            if (conn == null) {

            }
            //start query header
            String Company="select CurCode,CompanyName,CompanyCode,GSTNo,Address,Tel1,Fax1,CompanyEmail,ComTown,ComState,ComCountry FROM companysetup ";
            Statement stmtCom = conn.createStatement();
            stmtCom.execute(Company);
            ResultSet rsCom = stmtCom.getResultSet();
            while (rsCom.next()) {
                CompanyName = rsCom.getString("CompanyName");
                CompanyCode = rsCom.getString("CompanyCode");
                GSTNo       = rsCom.getString("GSTNo");
                Address     = rsCom.getString("Address");
                Address     = Address.substring(0, 32);
                ComTown     = rsCom.getString("ComTown");
                ComState    = rsCom.getString("ComState");
                ComCountry  = rsCom.getString("ComCountry");
                Tel         = rsCom.getString("Tel1");
                Fax         = rsCom.getString("Fax1");
            }
            vLine1   =str_pad(CompanyName, 32, " ", "STR_PAD_BOTH");
            vLine2   =str_pad("Co.Reg.No.: "+CompanyCode, 32, " ", "STR_PAD_BOTH");
            vLine3   =str_pad("GST Reg.No.: "+GSTNo, 32, " ", "STR_PAD_BOTH");
            vLine4   =str_pad(Address, 32, " ", "STR_PAD_BOTH");
           // vLine4_1  str_pad(Address, 32, " ", "STR_PAD_BOTH");
            vLine4_1   =str_pad(ComTown+" "+ComState, 32, " ", "STR_PAD_BOTH");
            vLine5   =str_pad(Tel, 32, " ", "STR_PAD_BOTH");
            vLine6   =str_pad(Fax, 32, " ", "STR_PAD_BOTH");
            vLine7   =str_pad("TAX INVOICE", 32, " ", "STR_PAD_BOTH");
            vLine8   =str_pad("BILL TO : ", 32, " ", "STR_PAD_RIGHT");
            //end query header

            //start query customer
            String qCustomer="SELECT H.Doc1No,H.D_ate,H.Attention,H.D_ateTime,C.CusCode,C.CusName,C.Tel,C.Address,C.SalesPersonCode FROM stk_cus_inv_hd H inner join customer C ON C.CusCode=H.CusCode where H.Doc1No='"+Doc1No+"' ";
            Statement stmtCus = conn.createStatement();
            stmtCus.execute(qCustomer);
            ResultSet rsCus = stmtCus.getResultSet();
            while (rsCus.next()) {
                CusCode         = rsCus.getString("CusCode");
                CusName         = rsCus.getString("CusName");
                AdddresCus      = rsCus.getString("Address");
                SalesPersonCode = rsCus.getString("SalesPersonCode");
                TelCus          = rsCus.getString("Tel");
                D_ateTime        = rsCus.getString("D_ateTime");
            }
            vLine9=str_pad(CusName, 32, " ", "STR_PAD_RIGHT");
            //vLine10=str_pad(AdddresCus, 32, " ", "STR_PAD_RIGHT");
            vLine11=str_pad(D_ateTime, 32, " ", "STR_PAD_RIGHT");
            vLine12=str_pad(Doc1No, 32, " ", "STR_PAD_RIGHT");
            vLine13=str_pad(CurCode+" ", 32, " ", "STR_PAD_LEFT");
            //end query customer

            if(AidlUtil.getInstance().isConnect()){
                strPrint += vLine1+"\n";
                strPrint += vLine2+"\n";
                strPrint += vLine3+"\n";
                strPrint += vLine4+"\n";
                strPrint += vLine4_1+"\n";
                if(!Tel.equals("")) {
                   // strPrint += vLine5 + "\n";
                }
                if(!Fax.equals("")){
                   // strPrint += vLine6+"\n";
                }
                strPrint += "________________________________\n";
                strPrint += vLine7+"\n";
                strPrint += "________________________________\n";
                //end header

                //customer
                strPrint += vLine8+"\n";
                strPrint += vLine9+"\n";
                strPrint += vLine10+"\n";
                strPrint += vLine11+"\n";
                strPrint += vLine12+"\n";
                strPrint += "________________________________\n";
                strPrint += vLine13+"\n";
                strPrint += "________________________________\n";
                //end customer

                //detail
                String qDetail="select ItemCode,FORMAT(Qty,2) as QTY,UOM,DetailTaxCode,SUBSTRING(Description,1,45) as Description,FORMAT(HCUnitCost,2) as HCUnitCost,FORMAT(HCLineAmt,2) AS HCLineAmt,FORMAT(DisRate1,2) as DisRate1,FORMAT(HCDiscount,2) as HCDiscount from stk_cus_inv_dt Where Doc1No='"+Doc1No+"' ";
                Statement stmtDt = conn.createStatement();
                stmtDt.execute(qDetail);
                ResultSet rsDt = stmtDt.getResultSet();
                while (rsDt.next()) {
                    String ItemCode         = rsDt.getString("ItemCode");
                    String Qty              = rsDt.getString("Qty");
                    String UOM              = rsDt.getString("UOM");
                    String DetailTaxCode    = rsDt.getString("DetailTaxCode");
                    String Description      = rsDt.getString("Description");
                    String HCUnitCost       = rsDt.getString("HCUnitCost");
                    String HCLineAmt        = rsDt.getString("HCLineAmt");
                    String DisRate1         = rsDt.getString("DisRate1");
                    String HCDiscount       = rsDt.getString("HCDiscount");
                    if(!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")){
                        Disc2		    =""+HCDiscount+" =";
                        Minus2	        =" - ";
                    }else{
                        Disc2			=" =";
                        Minus2	        ="   ";
                    }

                    vLine14                 =str_pad(Description, 32, " ", "STR_PAD_RIGHT");
                    String vLine15_1        =str_pad(Qty, 4, " ", "STR_PAD_LEFT");
                    String vLine15_2        =str_pad(HCUnitCost, 9, " ", "STR_PAD_LEFT");
                    String vLine15_3        =str_pad(Disc2, 4, " ", "STR_PAD_LEFT");
                    String vLine15_4        =str_pad(HCLineAmt, 9, " ", "STR_PAD_LEFT");
                    strPrint 				+= vLine14+"\n";
                    strPrint 				+= ""+vLine15_1+" x"+vLine15_2+Minus2+vLine15_3+vLine15_4+"\n";

                }
                //end detail

                //start Total
                String qTotal="select FORMAT(H.HCNetAmt,2)as HCNetAmt,FORMAT(H.HCDtTax,2) as HCDtTax,FORMAT(R.CashAmt,2) as CashAmt, FORMAT(R.ChangeAmt,2) as ChangeAmt,FORMAT(sum(D.HCLineAmt)-sum(D.HCTax),2) as AmtExTax,FORMAT(R.BalanceAmount,2) as BalanceAmount from stk_cus_inv_hd H inner join stk_receipt2 R on R.Doc1No=H.Doc1No inner join stk_cus_inv_dt D ON D.Doc1No=H.Doc1No where H.Doc1No='"+Doc1No+"' Group By H.Doc1No ";
                Statement stmtTotal = conn.createStatement();
                stmtTotal.execute(qTotal);
                ResultSet rsTot = stmtTotal.getResultSet();
                while (rsTot.next()) {
                    String AmtExtTax    =rsTot.getString("AmtExTax");
                    String TaxAmt       =rsTot.getString("HCDtTax");
                    String TotAmt       =rsTot.getString("HCNetAmt");
                    String PayAmt       =rsTot.getString("CashAmt");
                    String ChAmt        =rsTot.getString("ChangeAmt");
                    String BalAmt       =rsTot.getString("BalanceAmount");
                    vLine16             =str_pad(AmtExtTax, 11, " ", "STR_PAD_LEFT");
                    vLine17             =str_pad(TaxAmt, 11, " ", "STR_PAD_LEFT");
                    vLine18             =str_pad("0.00", 11, " ", "STR_PAD_LEFT");
                    vLine19             =str_pad(TotAmt, 11, " ", "STR_PAD_LEFT");
                    vLine20             =str_pad(PayAmt, 11, " ", "STR_PAD_LEFT");
                    vLine21             =str_pad(ChAmt, 11, " ", "STR_PAD_LEFT");
                    vLine22             =str_pad(BalAmt, 11, " ", "STR_PAD_LEFT");
                    strPrint 			+= "________________________________\n\n";
                    strPrint 			+= "Amount Exc Tax    : "+vLine16+"\n";
                    strPrint 			+= "Add Total GST Amt : "+vLine17+"\n";
                    strPrint 			+= "Rounding          : "+vLine18+"\n";
                    strPrint 			+= "Total Amount Due  : "+vLine19+"\n";
                    strPrint 			+= "Paid Amount       : "+vLine20+"\n";
                    strPrint 			+= "Change Amount     : "+vLine21+"\n";
                    if(BalAmt.length()>0){
                        strPrint 		+= "Balance Amount    : "+vLine22+"\n";
                    }
                    strPrint 			+= "________________________________\n";
                    strPrint 			+= "GST Summary \n";
                    strPrint 			+= "Code  Rate   Goods Amt  GST Amt\n";

                }
                //end total

                //start GST
                String qGST="select FORMAT(TaxRate1,2)as TaxRate1,DetailTaxCode,FORMAT(sum(HCLineAmt)-sum(HCTax),2) as GoodAmt,FORMAT(sum(HCTax),2) as GSTAmt from stk_cus_inv_dt  Where Doc1No='"+Doc1No+"' Group By DetailTaxCode  ";
                Statement stmtGST = conn.createStatement();
                stmtGST.execute(qGST);
                ResultSet rsGST = stmtGST.getResultSet();
                while (rsGST.next()) {
                    String TaxRate       =rsGST.getString("TaxRate1");
                    String TaxCode       =rsGST.getString("DetailTaxCode");
                    String GoodAmt       =rsGST.getString("GoodAmt");
                    String GSTAmt        =rsGST.getString("GSTAmt");
                    String vLine23_1     =str_pad(TaxCode, 4, " ", "STR_PAD_LEFT");
                    String vLine23_2     =str_pad(TaxRate, 6, " ", "STR_PAD_LEFT");
                    String vLine23_3     =str_pad(GoodAmt, 11, " ", "STR_PAD_LEFT");
                    String vLine23_4     =str_pad(GSTAmt, 11, " ", "STR_PAD_LEFT");
                    vLine23              =vLine23_1+vLine23_2+vLine23_3+vLine23_4;
                    strPrint 			 += vLine23+"\n";
                }
                //end GST
                strPrint 				+= "________________________________\n";
                strPrint 				+= "*Goods sold non-returnable & non-refundable\n";
                strPrint 				+= "Thank you, please come again! \n";
                strPrint 				+= "________________________________\n \n\n";
                Log.d("PRINT",strPrint);
                //byte[] bytes = new byte[0];
                byte[] bytes = strPrint.getBytes();
                String content2= BytesUtil.getHexStringFromBytes(bytes);
                //end Print
                //float size = Integer.parseInt("14");
                //AidlUtil.getInstance().sendRawData(BytesUtil.getBytesFromHexString(strPrint));
               // AidlUtil.getInstance().printText(strPrint, size, isBold, isUnderLine);
                AidlUtil.getInstance().sendRawData(BytesUtil.getBytesFromHexString(content2));
            }else{
                Log.d("AIDL","Not supported");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    /*private void printByBluTooth(String content) {
        try {
            if (isBold) {
                BluetoothUtil.sendData(ESCUtil.boldOn());
            } else {
                BluetoothUtil.sendData(ESCUtil.boldOff());
            }

            if (isUnderLine) {
                BluetoothUtil.sendData(ESCUtil.underlineWithOneDotWidthOn());
            } else {
                BluetoothUtil.sendData(ESCUtil.underlineOff());
            }

            if (record < 17) {
                BluetoothUtil.sendData(ESCUtil.singleByte());
                BluetoothUtil.sendData(ESCUtil.setCodeSystemSingle(codeParse(record)));
            } else {
                BluetoothUtil.sendData(ESCUtil.singleByteOff());
                BluetoothUtil.sendData(ESCUtil.setCodeSystem(codeParse(record)));
            }

            BluetoothUtil.sendData(content.getBytes(mStrings[record]));
            BluetoothUtil.sendData(ESCUtil.nextLine(3));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private byte codeParse(int value) {
        byte res = 0x00;
        switch (value) {
            case 0:
                res = 0x00;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                res = (byte) (value + 1);
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                res = (byte) (value + 8);
                break;
            case 12:
                res = 21;
                break;
            case 13:
                res = 33;
                break;
            case 14:
                res = 34;
                break;
            case 15:
                res = 36;
                break;
            case 16:
                res = 37;
                break;
            case 17:
            case 18:
            case 19:
                res = (byte) (value - 17);
                break;
            case 20:
                res = (byte) 0xff;
                break;
        }
        return (byte) res;
    }

    public static FnCalculateHCNetAmt fncalculatehcnetamt(String UUID,String GlobalTaxCode, String TaxType, Double R_ate, String URL, String UserName, String Password){
        Double vHCTax,HCGbDiscount,vHCLineAmt,HCDtTax,HCGbTax,HCNetAmt,GbTaxRate1,AmountB4Tax;
        vHCLineAmt=0.00;
        vHCTax=0.00;
        HCGbDiscount=0.00;
        HCDtTax=0.00;
        HCGbTax=0.00;
        HCNetAmt=0.00;
        GbTaxRate1=0.00;
        try {
            //Log.d("RESULT", "URL: " + URL + ", UserName: " + UserName +", Password: "+Password);
            Connection conn = Connector.connect(URL, UserName, Password);
            if (conn == null) {

            }
            String strSQL="SELECT  ROUND(sum(HCTax),2) as vHCTax, ROUND(sum(HCLineAmt),2) as vHCLineAmt, ROUND(sum(HCDiscount),2) as HCGbDiscount FROM cloud_cus_inv_dt WHERE ComputerName = '"+UUID+"' GROUP BY '' ";
            Statement stmtSum = conn.createStatement();
            stmtSum.execute(strSQL);
            ResultSet rsNetAmt= stmtSum.getResultSet();
            while (rsNetAmt.next()) {
                vHCTax= Double.parseDouble(rsNetAmt.getString("vHCTax"));
                vHCLineAmt=Double.parseDouble(rsNetAmt.getString("vHCLineAmt"));
                HCGbDiscount=Double.parseDouble(rsNetAmt.getString("HCGbDiscount"));
            }

            if(GlobalTaxCode.equals("")){
                HCDtTax 		= vHCTax;
                HCGbTax 		= 0.00;
                HCNetAmt 		= vHCLineAmt;
                GbTaxRate1		= 0.00;
            }else{
                AmountB4Tax 	= vHCLineAmt;
                HCDtTax			= 0.00;
                GbTaxRate1		= R_ate;
                //0,2 is inclusive
                if(TaxType.equals("0") || TaxType.equals("2")){
                    HCGbTax	= AmountB4Tax *(GbTaxRate1/(GbTaxRate1 + 100));
                    HCNetAmt	= AmountB4Tax;
                }else if(TaxType.equals("1") || TaxType.equals("3")){
                    HCGbTax	= AmountB4Tax *(GbTaxRate1/100);
                    HCNetAmt	= AmountB4Tax + HCGbTax;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return new FnCalculateHCNetAmt(HCNetAmt, HCDtTax, HCGbTax, HCGbDiscount, GbTaxRate1, GlobalTaxCode,0.00);
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


    protected String _fill_string(String pad, int resto )
    {
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


}
