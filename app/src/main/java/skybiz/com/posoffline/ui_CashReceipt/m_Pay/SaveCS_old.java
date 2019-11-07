package skybiz.com.posoffline.ui_CashReceipt.m_Pay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.BytesUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothPrinter;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothZebra;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_USB.PrintingUSB;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI.PrintingWifi;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 22/12/2017.
 */

public class SaveCS_old {
    Context c;
    String Doc1No, CC1Code, CC1No,z,CurCode,vPostGlobalTaxYN,vPort,datedTime ;
    Double CashAmt, ChangeAmt, CC1Amt,CC2Amt, BalanceAmount;
    String TypePrinter,NamePrinter,IPPrinter,IPAddress,UUIDs,isDuplicate,GlobalTaxCode,CC2Code,CC2No,LastNo,NewDoc,RunNo,stringPrint,CusCode,TaxType;
    Double R_ate,HCGbTax,HCDtTax,HCGbDiscount,TotalAmt,GbTaxRate1;
    Boolean isBT;
    int NewNo;
    TelephonyManager telephonyManager;
    public SaveCS_old(Context c, String doc1No, String CC1Code, String CC1No, Double cashAmt, Double changeAmt, Double CC1Amt, Double balanceAmount) {
        this.c = c;
        Doc1No = doc1No;
        this.CC1Code = CC1Code;
        this.CC1No = CC1No;
        CashAmt = cashAmt;
        ChangeAmt = changeAmt;
        this.CC1Amt = CC1Amt;
        BalanceAmount = balanceAmount;
    }

    public String fnsavecs(){
        telephonyManager    = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId     = telephonyManager.getDeviceId();
        CusCode             ="999999";
        z                   ="error";
        GlobalTaxCode       = "";
        DBAdapter db        = new DBAdapter(c);
        db.openDB();
        Cursor cur = db.getGeneralSetup();
        while (cur.moveToNext()) {
            int RunNo = cur.getInt(0);
            CurCode = cur.getString(1);
            vPostGlobalTaxYN = cur.getString(6);
        }
        Cursor cPrint=db.getSettingPrint();
        while (cPrint.moveToNext()) {
            TypePrinter = cPrint.getString(1);
            NamePrinter = cPrint.getString(2);
            IPPrinter = cPrint.getString(3);
            vPort= cPrint.getString(4);
        }

        String vDuplicate="select count(*) as jumlah from stk_cus_inv_hd where Doc1No='"+Doc1No+"' ";
        Cursor rsDup=db.getQuery(vDuplicate);
        while(rsDup.moveToNext()) {
            isDuplicate=rsDup.getString(0);
        }
        if(!isDuplicate.equals("0")){
            fnsavelastno(c);
            z="error";
        }else {
            if (vPostGlobalTaxYN.equals("1")) {
                String vDefault = "SELECT a.RetailTaxCode,b.R_ate,b.TaxType,b.TaxCode FROM sys_general_setup3 a INNER JOIN stk_tax b ON a.RetailTaxCode=b.TaxCode";
                Cursor rsDef= db.getQuery(vDefault);
                while (rsDef.moveToNext()) {
                    GlobalTaxCode   = rsDef.getString(0);
                    R_ate           = Double.parseDouble(rsDef.getString(1));
                    TaxType         = rsDef.getString(2);
                }
            } else {
                TaxType = "0";
                R_ate = 6.00;
            }
            FnCalculateHCNetAmt vNetAmt = fncalculatehcnetamt(c, deviceId, GlobalTaxCode, TaxType, R_ate);
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
            datedTime = DateCurr1.format(date);
            String vTime = DateCurr2.format(date);

            //insert stk_cus_inv_hd
            ContentValues cvHd=new ContentValues();
            cvHd.put("Doc1No",Doc1No);
            cvHd.put("Doc2No","");
            cvHd.put("Doc3No","");
            cvHd.put("D_ate",datedShort);
            cvHd.put("D_ateTime",datedTime);
            cvHd.put("CusCode",CusCode);
            cvHd.put("MemberNo",CusCode);
            cvHd.put("DueDate",datedShort);
            cvHd.put("TaxDate",datedShort);
            cvHd.put("CurCode",CurCode);
            cvHd.put("CurRate1","1");
            cvHd.put("CurRate2","1");
            cvHd.put("CurRate3","1");
            cvHd.put("TermCode","1");
            cvHd.put("D_ay","0");
            cvHd.put("Attention","android_pos");
            cvHd.put("AddCode","");
            cvHd.put("BatchCode","");
            cvHd.put("GbDisRate1","0");
            cvHd.put("GbDisRate2","0");
            cvHd.put("GbDisRate3","0");
            cvHd.put("HCGbDiscount","0");
            cvHd.put("GbTaxRate1","0");
            cvHd.put("GbTaxRate2","0");
            cvHd.put("GbTaxRate3","0");
            cvHd.put("HCGbTax","0");
            cvHd.put("GlobalTaxCode","");
            cvHd.put("HCDtTax",HCDtTax);
            cvHd.put("HCNetAmt",TotalAmt);
            cvHd.put("AdjAmt","0");
            cvHd.put("GbOCCode","");
            cvHd.put("GbOCRate","0");
            cvHd.put("GbOCAmt","0");
            cvHd.put("DocType","CS");
            cvHd.put("ApprovedYN","1");
            cvHd.put("RetailYN","1");
            cvHd.put("UDRunNo","0");
            cvHd.put("L_ink","1");
            cvHd.put("Status","Used");
            cvHd.put("SynYN","0");
            long addHd=db.addStkCusInvHd(cvHd);
            if(addHd>0){
                Log.d("SUCCESS HD","inserted");
            }else{
                Log.d("ERROR HD","failed");
            }

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
            CC2Amt = 0.00;
            CC2Code = "";
            CC2No = "";
            ContentValues cvReceipt=new ContentValues();
            cvReceipt.put("D_ate",datedShort);
            cvReceipt.put("T_ime",vTime);
            cvReceipt.put("D_ateTime",datedTime);
            cvReceipt.put("Doc1No",Doc1No);
            cvReceipt.put("CashAmt",CashAmt);
            cvReceipt.put("CC1Code",CC1Code);
            cvReceipt.put("CC1Amt",CC1Amt);
            cvReceipt.put("CC1No",CC1No);
            cvReceipt.put("CC1Expiry","");
            cvReceipt.put("CC1ChargesAmt",vCC1ChargesAmt);
            cvReceipt.put("CC1ChargesRate",vCC1ChargesRate);
            cvReceipt.put("CC2Code","");
            cvReceipt.put("CC2Amt","0");
            cvReceipt.put("CC2No","");
            cvReceipt.put("CC2Expiry","");
            cvReceipt.put("CC2ChargesAmt","0");
            cvReceipt.put("CC2ChargesRate","0");
            cvReceipt.put("Cheque1Code","");
            cvReceipt.put("Cheque1Amt","0");
            cvReceipt.put("Cheque1No","");
            cvReceipt.put("Cheque2Code","");
            cvReceipt.put("Cheque2Amt","0");
            cvReceipt.put("Cheque2No","");
            cvReceipt.put("PointAmt","0");
            cvReceipt.put("VoucherAmt","0");
            cvReceipt.put("CurCode",CurCode);
            cvReceipt.put("CurRate","1");
            cvReceipt.put("FCAmt","0");
            cvReceipt.put("CusCode",CusCode);
            cvReceipt.put("BalanceAmount",BalanceAmount);
            cvReceipt.put("ChangeAmt",ChangeAmt);
            cvReceipt.put("CounterCode","");
            cvReceipt.put("UserCode","");
            cvReceipt.put("DocType","CS");
            cvReceipt.put("SynYN","0");
            //db.addStkReceipt2(cvReceipt);

            long addReceipt=db.addStkReceipt2(cvReceipt);
            if(addReceipt>0){
                Log.d("SUCCESS RECEIPT","inserted");
            }else{
                Log.d("ERROR RECEIPT","failed");
            }

            //insert stk_cus_inv detail
            String vDetail = "INSERT INTO stk_cus_inv_dt (Doc1No, N_o, ItemCode, Description, Qty, FactorQty, UOM, UOMSingular, " +
                    "HCUnitCost, DisRate1, HCDiscount, TaxRate1, HCTax, DetailTaxCode, HCLineAmt, " +
                    "BranchCode, DepartmentCode, ProjectCode, SalesPersonCode, LocationCode,WarrantyDate, LineNo, BlankLine, DocType, AnalysisCode2, SORunNo, SynYN )" +
                    "SELECT '"+Doc1No+"', N_o, ItemCode, Description, Qty, FactorQty, UOM, UOMSingular, " +
                    "HCUnitCost, DisRate1, HCDiscount, TaxRate1, HCTax, DetailTaxCode, HCLineAmt, " +
                    "BranchCode, DepartmentCode, ProjectCode, SalesPersonCode, LocationCode,WarrantyDate, LineNo, BlankLine, 'CS', AnalysisCode2, SORunNo,  '0' " +
                    "FROM cloud_cus_inv_dt WHERE ComputerName='" + deviceId + "' ";

            long addDetail= db.addQuery(vDetail);
            if(addDetail>0){
                Log.d("SUCCESS DETAIL","inserted");
            }else{
                Log.d("ERROR DETAIL","failed");
            }

            String vCheckSO="select SORunNo,Doc1No from cloud_cus_inv_dt where ComputerName='"+deviceId+"' ";
            Cursor rsSO=db.getQuery(vCheckSO);
            while(rsSO.moveToNext()){
                String SORunNo=rsSO.getString(0);
                String Doc1NoSO=rsSO.getString(1);
                if(!SORunNo.equals("0")){
                    String vUpHdSO="Update stk_sales_order_hd  set Status='Used'  where Doc1No='" + Doc1NoSO + "' ";
                    db.addQuery(vUpHdSO);
                }
            }
            //insert stk_detail_trn_out
            String vdetailOut = "insert into stk_detail_trn_out(ItemCode,Doc3No,D_ate,QtyOUT,FactorQty,UOM,UnitPrice,CusCode,DocType3,Doc3NoRunNo,LocationCode,L_ink,HCTax,BookDate, SynYN)" +
                    "SELECT ItemCode,Doc1No,'" + datedShort + "',Qty,FactorQty,UOMSingular,HCUnitCost,'" + CusCode + "',DocType,RunNo, LocationCode,'1', HCTax,'" + datedShort + "', '0' from stk_cus_inv_dt where Doc1No='" + Doc1No + "'  ";
           db.addQuery(vdetailOut);
            //exeDetailOut.close();
            fnsavelastno(c);
            NewDoc ="xxxxxx";
            db.DelAllCloud();
           // db.closeDB();
            if (TypePrinter.equals("AIDL")) {
                fngenerate58(Doc1No);
            } else {
                fngenerate78(Doc1No);
            }

            if (TypePrinter.equals("AIDL")) {
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
            } else if (TypePrinter.equals("Bluetooth Zebra")) {
                //Log.d("STR PRINT"+NamePrinter,stringPrint);
                BluetoothZebra fncheck = new BluetoothZebra();
                isBT = fncheck.fnBluetooth(c, NamePrinter, stringPrint);
                if (isBT == false) {
                    NewDoc = "error print";
                }
            } else if (TypePrinter.equals("Wifi")) {
                int Port =Integer.parseInt(vPort);
                PrintingWifi fnprintw = new PrintingWifi();
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
    }

    public void fngenerate58(String Doc1No){
        String vLine1,vLine2,vLine3,vLine4,vLine4_1,vLine5,vLine6,vLine7,vLine8,vLine9,vLine10,vLine11,vLine12,vLine13,vLine14,vLine15,vLine16,vLine17;
        String vLine18,vLine19,vLine20,vLine21,vLine22,vLine23,vLine24,vLine25,vLine4_2,vLine4_3,vLine4_4;
        vLine1="";
        vLine2="";
        vLine3="";
        vLine4="";
        vLine4_1="";
        vLine4_2="";
        vLine4_3="";
        vLine4_4="";
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
        String vCC1No="";
        DBAdapter db = new DBAdapter(c);
        db.openDB();

        String Company="select CurCode,CompanyName,CompanyCode,GSTNo,Address,Tel1,Fax1,CompanyEmail,ComTown,ComState,ComCountry FROM companysetup ";
        Cursor rsCom=db.getQuery(Company);
        while (rsCom.moveToNext()) {
            CompanyName = rsCom.getString(1);
            CompanyCode = rsCom.getString(2);
            GSTNo       = rsCom.getString(3);
            Address     = rsCom.getString(4);
            Tel         = rsCom.getString(5);
            Fax         = rsCom.getString(6);
            ComTown     = rsCom.getString(8);
            ComState    = rsCom.getString(9);
            ComCountry  = rsCom.getString(10);

        }

        vLine1   =str_pad(CompanyName, 32, " ", "STR_PAD_BOTH");
        vLine2   =str_pad("Co.Reg.No.: "+CompanyCode, 32, " ", "STR_PAD_BOTH");
        vLine3   =str_pad("GST Reg.No.: "+GSTNo, 32, " ", "STR_PAD_BOTH");
        String AddressAll = Address.replaceAll("(.{32})", "$1;");
        String [] address=AddressAll.split(";");
        String vAddress="";
        for(String add:address){
            vAddress += str_pad(add,32," ", "STR_PAD_BOTH");
        }
        vLine4=vAddress;
        vLine5   =str_pad(Tel, 32, " ", "STR_PAD_BOTH");
        vLine6   =str_pad(Fax, 32, " ", "STR_PAD_BOTH");
        if(vPostGlobalTaxYN.equals("0")) {
            vLine7 = str_pad("TAX INVOICE", 32, " ", "STR_PAD_BOTH");
        }else{
            vLine7 = str_pad("INVOICE", 32, " ", "STR_PAD_BOTH");
        }

        //end query header

        //start query customer
       // vLine9=str_pad("CASH SALES", 32, " ", "STR_PAD_RIGHT");

        vLine9  = str_pad("Bill #  : "+Doc1No, 32, " ", "STR_PAD_RIGHT");
        vLine11 = str_pad(datedTime, 32, " ", "STR_PAD_RIGHT");
        vLine12 = str_pad("BILL TO : CASH SALES ", 32, " ", "STR_PAD_RIGHT");
        //vLine13 = str_pad(CurCode+" ", 32, " ", "STR_PAD_LEFT");
        String vLine131=str_pad("Qty", 7, " ", "STR_PAD_RIGHT");
        String vLine132=str_pad("Price", 11, " ", "STR_PAD_BOTH");
        String vLine133=str_pad("Amount ("+CurCode+")", 14, " ", "STR_PAD_LEFT");

            //end query customer
        strPrint += vLine1+"\n";
        strPrint += vLine2+"\n";
        strPrint += vLine3+"\n";
        strPrint += vLine4+"\n";
        strPrint += "________________________________\n";
        strPrint += vLine7+"\n";
        strPrint += "________________________________\n";
        //end header

        //customer
        strPrint += vLine9+"\n";
        strPrint += vLine11+"\n";
        strPrint += vLine12+"\n";
        strPrint += "________________________________\n";
        strPrint += vLine131+vLine132+vLine133+"\n";
        strPrint += "________________________________\n";
            //end customer

            //detail
        String qDetail="select ItemCode,round(Qty,2) as Qty,UOM,DetailTaxCode,substr(Description,1,45) as Description,round(HCUnitCost,2) as HCUnitCost,round(HCLineAmt,2) AS HCLineAmt,round(DisRate1,2) as DisRate1,round(HCDiscount,2) as HCDiscount from stk_cus_inv_dt Where Doc1No='"+Doc1No+"' ";
        Cursor rsDt=db.getQuery(qDetail);
        while (rsDt.moveToNext()) {
            String ItemCode         = rsDt.getString(0);
            Double dQty             = rsDt.getDouble(1);
            String Qty              = String.format(Locale.US, "%,.2f",dQty );
            String UOM              = rsDt.getString(2);
            String DetailTaxCode    = rsDt.getString(3);
            String Description      = rsDt.getString(4);
            Double dHCUnitCost      =rsDt.getDouble(5);
            String HCUnitCost       = String.format(Locale.US, "%,.2f",dHCUnitCost);
            Double dHCLineAmt       = rsDt.getDouble(6);
            String HCLineAmt        = String.format(Locale.US, "%,.2f",dHCLineAmt);
            Double dDisRate1        = rsDt.getDouble(7);
            String DisRate1         = String.format(Locale.US, "%,.2f",dDisRate1);
            Double dHCDiscount      = rsDt.getDouble(8);
            String HCDiscount       = String.format(Locale.US, "%,.2f",dHCDiscount);
            if(!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")){
                Disc2		    ="("+HCDiscount+")";
            }else{
                Disc2			=" ";
            }

            vLine14                 =str_pad(Description, 32, " ", "STR_PAD_RIGHT");
            String vLine15_1        =str_pad(Qty, 4, " ", "STR_PAD_LEFT");
            String vLine15_2        =str_pad(HCUnitCost, 9, " ", "STR_PAD_LEFT");
            String vLine15_3        =str_pad(Disc2, 6, " ", "STR_PAD_LEFT");
            String vLine15_4        =str_pad(HCLineAmt, 10, " ", "STR_PAD_LEFT");
            strPrint 				+= vLine14+"\n";
            strPrint 				+= ""+vLine15_1+" x"+vLine15_2+vLine15_3+vLine15_4+"\n";

        }
        //end detail

            //start Total
       // String qTotal="select round(H.HCNetAmt,2)as HCNetAmt,round(H.HCDtTax,2) as HCDtTax,round(R.CashAmt,2) as CashAmt, round(R.ChangeAmt,2) as ChangeAmt,round(sum(D.HCLineAmt)-sum(D.HCTax),2) as AmtExTax,round(R.BalanceAmount,2) as BalanceAmount from stk_cus_inv_hd H inner join stk_receipt2 R on R.Doc1No=H.Doc1No inner join stk_cus_inv_dt D ON D.Doc1No=H.Doc1No where H.Doc1No='"+Doc1No+"' Group By H.Doc1No ";
        String qTotal="select round(H.HCNetAmt,2)as HCNetAmt,round(H.HCDtTax,2) as HCDtTax,round(R.CashAmt + R.ChangeAmt, 2) as CashAmt, round(R.ChangeAmt,2) as ChangeAmt,round(sum(D.HCLineAmt)-sum(D.HCTax),2) as AmtExTax,round(R.BalanceAmount,2) as BalanceAmount, round(R.CC1Amt,2) as CC1Amt, R.CC1No, R.CC1Code, round(Sum(D.Qty),2) as ItemTender from stk_cus_inv_hd H inner join stk_receipt2 R on R.Doc1No=H.Doc1No inner join stk_cus_inv_dt D ON D.Doc1No=H.Doc1No where H.Doc1No='"+Doc1No+"' Group By H.Doc1No ";
        Cursor rsTot=db.getQuery(qTotal);
        while (rsTot.moveToNext()) {
            Double dTotAmt      =rsTot.getDouble(0);
            String TotAmt       = String.format(Locale.US, "%,.2f",dTotAmt);
            Double dTaxAmt      =rsTot.getDouble(1);
            String TaxAmt       =String.format(Locale.US, "%,.2f",dTaxAmt);
            Double dPayAmt      =rsTot.getDouble(2);
            String PayAmt       =String.format(Locale.US, "%,.2f",dPayAmt);
            Double dChAmt       =rsTot.getDouble(3);
            String ChAmt        =String.format(Locale.US, "%,.2f",dChAmt);
            Double dAmtExtTax   =rsTot.getDouble(4);
            String AmtExtTax    =String.format(Locale.US, "%,.2f",dAmtExtTax);
            Double dBalAmt      =rsTot.getDouble(5);
            String BalAmt       =String.format(Locale.US, "%,.2f",dBalAmt);
            Double dCC1Amt       =rsTot.getDouble(6);
            String CC1Amt       =String.format(Locale.US, "%,.2f",dCC1Amt);
            if(dCC1Amt>0) {
                String tCC1No   =rsTot.getString(7);
                vCC1No          = tCC1No.substring(tCC1No.length() -4);
            }
            String CC1Code      =rsTot.getString(8);
            Double dItemTender  =rsTot.getDouble(9);
            String ItemTender   =String.format(Locale.US, "%,.2f",dItemTender);
            vLine16             =str_pad(AmtExtTax, 11, " ", "STR_PAD_LEFT");
            vLine17             =str_pad(TaxAmt, 11, " ", "STR_PAD_LEFT");
            vLine18             =str_pad("0.00", 11, " ", "STR_PAD_LEFT");
            vLine19             =str_pad(TotAmt, 11, " ", "STR_PAD_LEFT");
            vLine20             =str_pad(PayAmt, 11, " ", "STR_PAD_LEFT");
            vLine21             =str_pad(ChAmt, 11, " ", "STR_PAD_LEFT");
            vLine22             =str_pad(BalAmt, 11, " ", "STR_PAD_LEFT");
            String vLine22_2    =str_pad(ItemTender, 11, " ", "STR_PAD_LEFT");
            String vLine22_3    =str_pad(CC1Amt, 11, " ", "STR_PAD_LEFT");
            String vLine22_4    =str_pad("xxxxx"+vCC1No, 11, " ", "STR_PAD_LEFT");
            String tCC1Code     =str_pad(CC1Code, 19, " ", "STR_PAD_RIGHT");
            strPrint 			+= "________________________________\n\n";
            if(vPostGlobalTaxYN.equals("0")) {
                strPrint += "Amount Exc Tax    : " + vLine16 + "\n";
                strPrint += "Add Total GST Amt : " + vLine17 + "\n";
                strPrint += "Rounding          : " + vLine18 + "\n";
                strPrint += "Total Amount Due  : " + vLine19 + "\n";
                strPrint += "Paid Amount       : " + vLine20 + "\n";
                if(dPayAmt>0) {
                    strPrint += "Paid Amount       : " + vLine20 + "\n";
                    strPrint += "Changed Amount    : " + vLine21 + "\n";
                }else{
                    strPrint += tCC1Code+":"+vLine22_3+"\n";
                    strPrint += "Credit Card No    : " + vLine22_4 + "\n";
                }
            }else{
                strPrint += "Total Amt Payable : " + vLine19 + "\n";
                if(dPayAmt>0) {
                    strPrint += "Paid Cash Amt     : " + vLine20 + "\n";
                    strPrint += "Changed Amount    : " + vLine21 + "\n";
                }
                if(dCC1Amt>0){
                    strPrint += tCC1Code+":"+vLine22_3+"\n";
                    strPrint += "Credit Card No    : " + vLine22_4 + "\n";
                }
                    strPrint += "Total Qty Tender  : " + vLine22_2 + "\n";

            }

            if(dBalAmt>0){
                strPrint += "Balance Amount    : "+vLine22+"\n";
            }
                if(vPostGlobalTaxYN.equals("0")) {
                    strPrint += "________________________________\n";
                    strPrint += "GST Summary \n";
                    strPrint += "Code  Rate   Goods Amt  GST Amt\n";
                }
            }
            //end total
            //start GST
            String qGST="select round(TaxRate1,2)as TaxRate1,DetailTaxCode,round(sum(HCLineAmt)-sum(HCTax),2) as GoodAmt,round(sum(HCTax),2) as GSTAmt from stk_cus_inv_dt  Where Doc1No='"+Doc1No+"' Group By DetailTaxCode  ";
            Cursor rsGST=db.getQuery(qGST);
            while (rsGST.moveToNext()) {
                Double dTaxRate      =rsGST.getDouble(0);
                String TaxRate       =String.format(Locale.US, "%,.2f",dTaxRate);
                String TaxCode       =rsGST.getString(1);
                Double dGoodAmt      =rsGST.getDouble(2);
                String GoodAmt       =String.format(Locale.US, "%,.2f",dGoodAmt);
                Double dGSTAmt       =rsGST.getDouble(3);
                String GSTAmt        =String.format(Locale.US, "%,.2f",dGSTAmt);
                String vLine23_1     =str_pad(TaxCode, 4, " ", "STR_PAD_LEFT");
                String vLine23_2     =str_pad(TaxRate, 6, " ", "STR_PAD_LEFT");
                String vLine23_3     =str_pad(GoodAmt, 11, " ", "STR_PAD_LEFT");
                String vLine23_4     =str_pad(GSTAmt, 11, " ", "STR_PAD_LEFT");
                vLine23              =vLine23_1+vLine23_2+vLine23_3+vLine23_4;
                if(vPostGlobalTaxYN.equals("0")) {
                    strPrint += vLine23 + "\n";
                }
            }
            //end GST
            strPrint 				+= "________________________________\n";
            strPrint 				+= "*Goods sold non-returnable & non-refundable\n";
            strPrint 				+= "Thank you, please come again! \n";
            strPrint 				+= "________________________________\n \n\n";

            Log.d("RESULT",strPrint);
            stringPrint=strPrint;
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
        String CusCode="";
        String CusName="";
        String TelCus="";
        String AdddresCus="";
        String SalesPersonCode="";
        String D_ateTime="";
        String Disc2="";
        String Minus2="";
        String vCC1No="";
        DBAdapter db = new DBAdapter(c);
        db.openDB();

        String Company="select CurCode,CompanyName,CompanyCode,GSTNo,Address,Tel1,Fax1,CompanyEmail,ComTown,ComState,ComCountry FROM companysetup ";
        Cursor rsCom=db.getQuery(Company);
        while (rsCom.moveToNext()) {
            CompanyName = rsCom.getString(1);
            CompanyCode = rsCom.getString(2);
            GSTNo       = rsCom.getString(3);
            Address     = rsCom.getString(4);
            Tel         = rsCom.getString(5);
            Fax         = rsCom.getString(6);
            ComTown     = rsCom.getString(8);
            ComState    = rsCom.getString(9);
            ComCountry  = rsCom.getString(10);

        }
        vLine1   =str_pad(CompanyName, 48, " ", "STR_PAD_BOTH");
        vLine2   =str_pad("Co.Reg.No.: "+CompanyCode, 48, " ", "STR_PAD_BOTH");
        vLine3   =str_pad("GST Reg.No.: "+GSTNo, 48, " ", "STR_PAD_BOTH");
        String AddressAll = Address.replaceAll("(.{48})", "$1;");
        String [] address=AddressAll.split(";");
        String vAddress="";
        for(String add:address){
            vAddress += str_pad(add,48," ", "STR_PAD_BOTH");
        }
        vLine4=vAddress;
        //vLine4   =str_pad(AddressAll, 48, " ", "STR_PAD_BOTH");
        vLine5   =str_pad("Tel.:"+Tel, 48, " ", "STR_PAD_BOTH");
        vLine6   =str_pad(Fax, 48, " ", "STR_PAD_BOTH");
        if(vPostGlobalTaxYN.equals("0")) {
            vLine7 = str_pad("TAX INVOICE", 48, " ", "STR_PAD_BOTH");
        }else{
            vLine7 = str_pad("INVOICE", 48, " ", "STR_PAD_BOTH");
        }

        //end query header

        //start query customer
        /*String qCustomer="SELECT H.Doc1No,H.D_ate,H.Attention,H.D_ateTime,C.CusCode,C.CusName,C.Tel,C.Address,C.SalesPersonCode FROM stk_cus_inv_hd H inner join customer C ON C.CusCode=H.CusCode where H.Doc1No='"+Doc1No+"' ";
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
        */
        //vLine9=str_pad("CASH SALES", 48, " ", "STR_PAD_RIGHT");

        vLine8  =str_pad("Bill #: "+Doc1No, 48, " ", "STR_PAD_RIGHT");
        vLine11 =str_pad(datedTime, 48, " ", "STR_PAD_RIGHT");
        vLine12 =str_pad("BILL TO :  CASH SALES" , 48, " ", "STR_PAD_RIGHT");
        //vLine13=str_pad(CurCode+" ", 48, " ", "STR_PAD_LEFT");
        String vLine131=str_pad("Quantity", 9, " ", "STR_PAD_RIGHT");
        String vLine132=str_pad("Unit Price", 19, " ", "STR_PAD_BOTH");
        String vLine133=str_pad("Amount ("+CurCode+")", 20, " ", "STR_PAD_LEFT");
        //end query customer

        strPrint += vLine1+"\n";
        strPrint += vLine2+"\n";
        strPrint += vLine3+"\n";
        strPrint += vLine4+"\n";
        if(!Tel.equals("")) {
            strPrint += vLine5 + "\n";
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
        //strPrint += vLine9+"\n";
        //strPrint += vLine10+"\n";
        strPrint += vLine11+"\n";
        strPrint += vLine12+"\n";
        strPrint += "________________________________________________\n";
        strPrint += vLine131+vLine132+vLine133+"\n";
        strPrint += "________________________________________________\n";
        //end customer

        //detail
        String qDetail="select ItemCode,round(Qty,2) as Qty,UOM,DetailTaxCode,substr(Description,1,45) as Description,round(HCUnitCost,2) as HCUnitCost,round(HCLineAmt,2) AS HCLineAmt,round(DisRate1,2) as DisRate1,round(HCDiscount,2) as HCDiscount from stk_cus_inv_dt Where Doc1No='"+Doc1No+"' ";
       // Log.d("QUERY",qDetail);
        Cursor rsDt=db.getQuery(qDetail);
       // int numrows=rsDt.getCount();
        //Log.d("NUMROWS",String.valueOf(numrows));
        while (rsDt.moveToNext()) {
            String ItemCode         = rsDt.getString(0);
            Double dQty             = rsDt.getDouble(1);
            String Qty              = String.format(Locale.US, "%,.2f",dQty );
            String UOM              = rsDt.getString(2);
            String DetailTaxCode    = rsDt.getString(3);
            String Description      = rsDt.getString(4);
            Double dHCUnitCost      =rsDt.getDouble(5);
            String HCUnitCost       = String.format(Locale.US, "%,.2f",dHCUnitCost);
            Double dHCLineAmt       = rsDt.getDouble(6);
            String HCLineAmt        = String.format(Locale.US, "%,.2f",dHCLineAmt);
            Double dDisRate1        = rsDt.getDouble(7);
            String DisRate1         = String.format(Locale.US, "%,.2f",dDisRate1);
            Double dHCDiscount      = rsDt.getDouble(8);
            String HCDiscount       = String.format(Locale.US, "%,.2f",dHCDiscount);
            if(!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")){
                Disc2		    ="("+HCDiscount+")";
            }else{
                Disc2			=" ";
            }

            vLine14                 =str_pad(Description, 48, " ", "STR_PAD_RIGHT");
            String vLine15_1        =str_pad(Qty, 6, " ", "STR_PAD_LEFT");
            String vLine15_2        =str_pad(HCUnitCost, 13, " ", "STR_PAD_LEFT");
            String vLine15_3        =str_pad(Disc2, 12, " ", "STR_PAD_LEFT");
            String vLine15_4        =str_pad(HCLineAmt, 15, " ", "STR_PAD_LEFT");
            strPrint 				+= vLine14+"\n";
            strPrint 				+= ""+vLine15_1+" x"+vLine15_2+vLine15_3+vLine15_4+"\n";

        }
        //end detail

        //start Total
        String qTotal="select round(H.HCNetAmt,2)as HCNetAmt,round(H.HCDtTax,2) as HCDtTax,round(R.CashAmt + R.ChangeAmt, 2) as CashAmt, round(R.ChangeAmt,2) as ChangeAmt,round(sum(D.HCLineAmt)-sum(D.HCTax),2) as AmtExTax,round(R.BalanceAmount,2) as BalanceAmount, round(R.CC1Amt,2) as CC1Amt, R.CC1No, R.CC1Code, round(Sum(D.Qty),2) as ItemTender from stk_cus_inv_hd H inner join stk_receipt2 R on R.Doc1No=H.Doc1No inner join stk_cus_inv_dt D ON D.Doc1No=H.Doc1No where H.Doc1No='"+Doc1No+"' Group By H.Doc1No ";
        Log.d("QUERY",qTotal);
        Cursor rsTot=db.getQuery(qTotal);
        while (rsTot.moveToNext()) {
            Double dTotAmt      =rsTot.getDouble(0);
            String TotAmt       = String.format(Locale.US, "%,.2f",dTotAmt);
            Double dTaxAmt      =rsTot.getDouble(1);
            String TaxAmt       =String.format(Locale.US, "%,.2f",dTaxAmt);
            Double dPayAmt      =rsTot.getDouble(2);
            String PayAmt       =String.format(Locale.US, "%,.2f",dPayAmt);
            Double dChAmt       =rsTot.getDouble(3);
            String ChAmt        =String.format(Locale.US, "%,.2f",dChAmt);
            Double dAmtExtTax   =rsTot.getDouble(4);
            String AmtExtTax    =String.format(Locale.US, "%,.2f",dAmtExtTax);
            Double dBalAmt      =rsTot.getDouble(5);
            String BalAmt       =String.format(Locale.US, "%,.2f",dBalAmt);
            Double dCC1Amt       =rsTot.getDouble(6);
            String CC1Amt       =String.format(Locale.US, "%,.2f",dCC1Amt);
            if(dCC1Amt>0) {
                String tCC1No   =rsTot.getString(7);
                vCC1No          = tCC1No.substring(tCC1No.length() -4);
            }
            String CC1Code       =rsTot.getString(8);
            Double dItemTender  =rsTot.getDouble(9);
            String ItemTender   =String.format(Locale.US, "%,.2f",dItemTender);
            vLine16             =str_pad(AmtExtTax, 22, " ", "STR_PAD_LEFT");
            vLine17             =str_pad(TaxAmt, 22, " ", "STR_PAD_LEFT");
            vLine18             =str_pad("0.00", 22, " ", "STR_PAD_LEFT");
            vLine19             =str_pad(TotAmt, 22, " ", "STR_PAD_LEFT");
            vLine20             =str_pad(PayAmt, 22, " ", "STR_PAD_LEFT");
            vLine21             =str_pad(ChAmt, 22, " ", "STR_PAD_LEFT");
            vLine22             =str_pad(BalAmt, 22, " ", "STR_PAD_LEFT");
            String vLine22_2    =str_pad(ItemTender, 22, " ", "STR_PAD_LEFT");
            String vLine22_3    =str_pad(CC1Amt, 22, " ", "STR_PAD_LEFT");
            String vLine22_4    =str_pad("xxxxxxxxxxx"+vCC1No, 22, " ", "STR_PAD_LEFT");
            String tCC1Code     =str_pad(CC1Code, 24, " ", "STR_PAD_RIGHT");
            strPrint            += "________________________________________________\n\n";
            if(vPostGlobalTaxYN.equals("0")) {
                strPrint += "Amount Exc Tax         : " + vLine16 + "\n";
                strPrint += "Add Total GST Amount   : " + vLine17 + "\n";
                strPrint += "Rounding               : " + vLine18 + "\n";
                strPrint += "Total Amount Due       : " + vLine19 + "\n";
                if(dCC1Amt>0){
                    strPrint += tCC1Code+vLine22_3+"\n";
                    strPrint += "Credit Card No         : " + vLine22_4 + "\n";
                }else {
                    strPrint += "Paid Amount            : " + vLine20 + "\n";
                    strPrint += "Changed Amount         : " + vLine21 + "\n";
                }
            }else{
                strPrint += "Total Amount Payable   : " + vLine19 + "\n";
                if(dPayAmt>0) {
                    strPrint += "Paid Cash Amount       : " + vLine20 + "\n";
                    strPrint += "Changed Amount         : " + vLine21 + "\n";
                }
                if(dCC1Amt>0){
                    strPrint += tCC1Code+":"+vLine22_3+"\n";
                    strPrint += "Credit Card No         : " + vLine22_4 + "\n";
                }
                strPrint += "Total Quantity Tender  : " + vLine22_2 + "\n";

            }
            if(dBalAmt>0){
                strPrint 		+= "Balance Amount         : "+vLine22+"\n";
            }
            if(vPostGlobalTaxYN.equals("0")) {
                strPrint          += "________________________________________________\n";
                strPrint          += "GST Summary \n";
                strPrint          += "Tax Code      Rate      Goods Amt      GST Amt  \n";
            }
        }
        //end total
        //start GST
        String qGST="select round(TaxRate1,2)as TaxRate1,DetailTaxCode,round(sum(HCLineAmt)-sum(HCTax),2) as GoodAmt,round(sum(HCTax),2) as GSTAmt from stk_cus_inv_dt  Where Doc1No='"+Doc1No+"' Group By DetailTaxCode  ";
        Cursor rsGST=db.getQuery(qGST);
        while (rsGST.moveToNext()) {
            Double dTaxRate      =rsGST.getDouble(0);
            String TaxRate       =String.format(Locale.US, "%,.2f",dTaxRate);
            String TaxCode       =rsGST.getString(1);
            Double dGoodAmt       =rsGST.getDouble(2);
            String GoodAmt       =String.format(Locale.US, "%,.2f",dGoodAmt);
            Double dGSTAmt        =rsGST.getDouble(3);
            String GSTAmt       =String.format(Locale.US, "%,.2f",dGSTAmt);
            String vLine23_1     =str_pad(TaxCode, 6, " ", "STR_PAD_LEFT");
            String vLine23_2     =str_pad(TaxRate, 12, " ", "STR_PAD_LEFT");
            String vLine23_3     =str_pad(GoodAmt, 15, " ", "STR_PAD_LEFT");
            String vLine23_4     =str_pad(GSTAmt, 15, " ", "STR_PAD_LEFT");
            if(vPostGlobalTaxYN.equals("0")) {
                vLine23 = vLine23_1 + vLine23_2 + vLine23_3 + vLine23_4;
                strPrint += vLine23 + "\n";
            }
        }
        //end GST
        strPrint                += "________________________________________________\n";
        strPrint 				+= "*Goods sold non-returnable & non-refundable\n";
        strPrint 				+= "Thank you, please come again! \n";
        strPrint                += "________________________________________________\n\n\n";


        //vPostGlobalTaxYN
        Log.d("PostGlobalTaxYN",vPostGlobalTaxYN);
        stringPrint=strPrint;
        //return stringPrint;
    }

    public static FnCalculateHCNetAmt fncalculatehcnetamt(Context c, String UUID,String vGlobalTaxCode, String TaxType, Double R_ate){
        Double vHCTax,HCGbDiscount,vHCLineAmt,HCDtTax,HCGbTax,HCNetAmt,GbTaxRate1,AmountB4Tax;
        vHCLineAmt=0.00;
        vHCTax=0.00;
        HCGbDiscount=0.00;
        HCDtTax=0.00;
        HCGbTax=0.00;
        HCNetAmt=0.00;
        GbTaxRate1=0.00;
        DBAdapter db = new DBAdapter(c);
        db.openDB();
        String strSQL="SELECT  ROUND(sum(HCTax),2) as vHCTax, ROUND(sum(HCLineAmt),2) as vHCLineAmt, ROUND(sum(HCDiscount),2) as HCGbDiscount FROM cloud_cus_inv_dt WHERE ComputerName = '"+UUID+"' GROUP BY '' ";
        Cursor rsNetAmt=db.getQuery(strSQL);
        while (rsNetAmt.moveToNext()) {
            vHCTax          = rsNetAmt.getDouble(0);
            vHCLineAmt      = rsNetAmt.getDouble(1);
            HCGbDiscount    = rsNetAmt.getDouble(2);
        }

        if(vGlobalTaxCode.equals("")){
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
        return new FnCalculateHCNetAmt(HCNetAmt, HCDtTax, HCGbTax, HCGbDiscount, GbTaxRate1, vGlobalTaxCode,0.00);
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

        //Log.d("LastNewNo", NewDoc);
        ContentValues cv=new ContentValues();
        cv.put("LastNo",vNewDoc);
        dbAdapter.UpdateSysRunNo(cv,RunNo);
       // db.closeDB();
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
