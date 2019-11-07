package skybiz.com.posoffline.ui_Service.m_Save;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.BytesUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothPrinter;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothZebra;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_IposAIDL.Utils.IposAidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_USB.PrintingUSB;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI.PrintingWifi;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.ImgUtils;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class SaveService extends AsyncTask<Void,Void,String> {

    Context c;
    String IPAddress,UserName,Password,
            Port,DBName,URL,
            z,DBStatus,EncodeType,
            BranchCode,LocationCode,Doc1No;
    Connection conn;
    Bitmap bmpSignature;
    String TypePrinter,NamePrinter,IPPrinter,vPort,vFooter,NewDoc;
    Boolean isBT;
    public SaveService(Context c, String Doc1No) {
        this.c = c;
        this.Doc1No = Doc1No;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.fnSave();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result==null){
            //Toast.makeText(c,"Unsuccessfull, No data retrieve", Toast.LENGTH_SHORT).show();
        }else{
           // ItemParser p=new ItemParser(c,DocType,result,rv);
           // p.execute();
        }
    }
    private String fnSave(){
        try{
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName, UserName, Password, " +
                    "DBName, Port, DBStatus, " +
                    "EncodeType, BranchCode, LocationCode " +
                    "from tb_setting ";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(0);
                UserName = curSet.getString(1);
                Password = curSet.getString(2);
                DBName = curSet.getString(3);
                Port = curSet.getString(4);
                DBStatus=curSet.getString(5);
                EncodeType=curSet.getString(6);
                BranchCode=curSet.getString(7);
                LocationCode=curSet.getString(8);
            }
            Cursor cPrint=db.getSettingPrint();
            while (cPrint.moveToNext()) {
                TypePrinter = cPrint.getString(1);
                NamePrinter = cPrint.getString(2);
                IPPrinter = cPrint.getString(3);
                vPort= cPrint.getString(5);
            }
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                conn = Connector.connect(URL, UserName, Password);
                //conn= Connect_db.getConnection();
                if (conn != null) {
                    String qHeader="select Doc1No, cuscode, " +
                            " cusname, mobileno, receiptno, " +
                            " receiptdate, repairtype, casetype, " +
                            " entryid, d_ate, outputid, " +
                            " outputdate, receivemode, termcode, " +
                            " productmodel, partno, serialno, " +
                            " supplierserialno, warrantystatus, warrantydesc, " +
                            " warrantyexpirydate, accessories, problemdesc, " +
                            " collectedby, collecteddate, sendtovendorYN, " +
                            " sendtovendordate, vendorwarrantystatus, vendorcode, " +
                            " vendorname, vendortelno, backfromvendorYN, " +
                            " backfromvendordate, returnbackenduserYN, returnbackenduserdate, " +
                            " returnbackenduserby, servicenoteremark, L_ink, " +
                            " Address, ContactTel, Email," +
                            " servicestatus, ImgService, Signature," +
                            " Technician " +
                            "from stk_service_hd_temp ";
                    Cursor rsTemp=db.getQuery(qHeader);
                    while(rsTemp.moveToNext()){
                        String Signature                =rsTemp.getString(43);
                        final String pureBase64Encoded  = Signature.substring(Signature.indexOf(",")  + 1);
                        final byte[] decodedBytes       = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
                        bmpSignature                    = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        String insertH="insert into stk_service_hd(Doc1No, cuscode, " +
                                " cusname, mobileno, receiptno, " +
                                " receiptdate, repairtype, casetype, " +
                                " entryid, d_ate, outputid, " +
                                " outputdate, receivemode, termcode, " +
                                " productmodel, partno, serialno, " +
                                " supplierserialno, warrantystatus, warrantydesc, " +
                                " warrantyexpirydate, accessories, problemdesc, " +
                                " collectedby, collecteddate, sendtovendorYN, " +
                                " sendtovendordate, vendorwarrantystatus, vendorcode, " +
                                " vendorname, vendortelno, backfromvendorYN, " +
                                " backfromvendordate, returnbackenduserYN, returnbackenduserdate, " +
                                " returnbackenduserby, servicenoteremark, L_ink, " +
                                " Address, ContactTel, Email," +
                                " servicestatus, Technician, PhotoFile)values('"+rsTemp.getString(0)+"', '"+rsTemp.getString(1)+"'," +
                                " '"+rsTemp.getString(2)+"', '"+rsTemp.getString(3)+"', '"+rsTemp.getString(4)+"'," +
                                " '"+rsTemp.getString(5)+"', '"+rsTemp.getString(6)+"', '"+rsTemp.getString(7)+"'," +
                                " '"+rsTemp.getString(8)+"', '"+rsTemp.getString(9)+"', '"+rsTemp.getString(10)+"'," +
                                " '"+rsTemp.getString(11)+"', '"+rsTemp.getString(12)+"', '"+rsTemp.getString(13)+"'," +
                                " '"+rsTemp.getString(14)+"', '"+rsTemp.getString(15)+"', '"+rsTemp.getString(16)+"'," +
                                " '"+rsTemp.getString(17)+"', '"+rsTemp.getString(18)+"', '"+rsTemp.getString(19)+"'," +
                                " '"+rsTemp.getString(20)+"', '"+rsTemp.getString(21)+"', '"+rsTemp.getString(22)+"'," +
                                " '"+rsTemp.getString(23)+"', '"+rsTemp.getString(24)+"', '"+rsTemp.getString(25)+"'," +
                                " '"+rsTemp.getString(26)+"', '"+rsTemp.getString(27)+"', '"+rsTemp.getString(28)+"'," +
                                " '"+rsTemp.getString(29)+"', '"+rsTemp.getString(30)+"', '"+rsTemp.getString(31)+"'," +
                                " '"+rsTemp.getString(32)+"', '"+rsTemp.getString(33)+"', '"+rsTemp.getString(34)+"'," +
                                " '"+rsTemp.getString(35)+"', '"+rsTemp.getString(36)+"', '"+rsTemp.getString(37)+"'," +
                                " '"+rsTemp.getString(38)+"', '"+rsTemp.getString(39)+"', '"+rsTemp.getString(40)+"'," +
                                " '"+rsTemp.getString(41)+"',  '"+rsTemp.getString(44)+"', '"+rsTemp.getString(42)+"') ";
                        Log.d("INSERT HD", insertH);
                        Statement stmtH = conn.createStatement();
                        stmtH.execute(insertH);
                    }
                }

                String qDetail="select '"+Doc1No+"', ItemCode, Description, " +
                        " '',  Qty, HCUnitCost, " +
                        " HCLineAmt, '1', '',  " +
                        " BlankLine, N_o, '', " +
                        " '', '0', FactorQty, " +
                        " UOM, UOMSingular, '0', " +
                        " DisRate1, '0', '0', " +
                        " HCDiscount, TaxRate1, '0'," +
                        " '0', DetailTaxCode, '' ," +
                        " HCTax, '0', DocType, " +
                        " '' " +
                        " from cloud_cus_inv_dt where DocType='Service' ";
                Cursor rsDet=db.getQuery(qDetail);
                while(rsDet.moveToNext()){
                    String insertDt="insert into stk_service_dt(Doc1No, repairpartscode, repairpartsdesc, " +
                            " repairpartsserialno, repairpartsqty, repairunitcost," +
                            " repairlineamount, L_ink," +
                            " BlankLine, N_o, ItemCode, " +
                            " Description, Qty, FactorQty, " +
                            " UOM, UOMSingular, HCUnitCost, " +
                            " DisRate1, DisRate2, DisRate3, " +
                            " HCDiscount, TaxRate1, TaxRate2, " +
                            " TaxRate3, DetailTaxCode, DetailTaxType, " +
                            " HCTax, HCLineAmt, DocType, " +
                            " DefectCode) values('"+rsDet.getString(0)+"', '"+rsDet.getString(1)+"', '"+rsDet.getString(2)+"'," +
                            " '"+rsDet.getString(3)+"', '"+rsDet.getString(4)+"', '"+rsDet.getString(5)+"'," +
                            " '"+rsDet.getString(6)+"', '"+rsDet.getString(7)+"' ," +
                            " '"+rsDet.getString(9)+"', '"+rsDet.getString(10)+"', '"+rsDet.getString(11)+"'," +
                            " '"+rsDet.getString(12)+"', '"+rsDet.getString(4)+"', '"+rsDet.getString(14)+"'," +
                            " '"+rsDet.getString(15)+"', '"+rsDet.getString(16)+"', '"+rsDet.getString(17)+"'," +
                            " '"+rsDet.getString(18)+"', '"+rsDet.getString(19)+"', '"+rsDet.getString(20)+"'," +
                            " '"+rsDet.getString(21)+"', '"+rsDet.getString(22)+"', '"+rsDet.getString(23)+"'," +
                            " '"+rsDet.getString(24)+"', '"+rsDet.getString(25)+"', '"+rsDet.getString(26)+"'," +
                            " '"+rsDet.getString(27)+"', '"+rsDet.getString(28)+"', '"+rsDet.getString(29)+"'," +
                            " '"+rsDet.getString(30)+"')";
                    Log.d("INSERT DT", insertDt);
                    Statement stmtDet = conn.createStatement();
                    stmtDet.executeQuery("SET NAMES 'LATIN1'");
                    stmtDet.executeQuery("SET CHARACTER SET 'LATIN1'");
                    stmtDet.execute(insertDt);
                }
            }

            String insertHd_local="insert into stk_service_hd(Doc1No, cuscode, " +
                    " cusname, mobileno, receiptno, " +
                    " receiptdate, repairtype, casetype, " +
                    " entryid, d_ate, outputid, " +
                    " outputdate, receivemode, termcode, " +
                    " productmodel, partno, serialno, " +
                    " supplierserialno, warrantystatus, warrantydesc, " +
                    " warrantyexpirydate, accessories, problemdesc, " +
                    " collectedby, collecteddate, sendtovendorYN, " +
                    " sendtovendordate, vendorwarrantystatus, vendorcode, " +
                    " vendorname, vendortelno, backfromvendorYN, " +
                    " backfromvendordate, returnbackenduserYN, returnbackenduserdate, " +
                    " returnbackenduserby, servicenoteremark, L_ink, " +
                    " Address, ContactTel, Email, " +
                    " servicestatus, ImgService, Signature, " +
                    " Technician, SynYN)" +
                    " select Doc1No, cuscode, " +
                    " cusname, mobileno, receiptno, " +
                    " receiptdate, repairtype, casetype, " +
                    " entryid, d_ate, outputid, " +
                    " outputdate, receivemode, termcode, " +
                    " productmodel, partno, serialno, " +
                    " supplierserialno, warrantystatus, warrantydesc, " +
                    " warrantyexpirydate, accessories, problemdesc, " +
                    " collectedby, collecteddate, sendtovendorYN, " +
                    " sendtovendordate, vendorwarrantystatus, vendorcode, " +
                    " vendorname, vendortelno, backfromvendorYN, " +
                    " backfromvendordate, returnbackenduserYN, returnbackenduserdate, " +
                    " returnbackenduserby, servicenoteremark, L_ink, " +
                    " Address, ContactTel, Email, " +
                    " servicestatus, ImgService, Signature," +
                    " Technician, '"+DBStatus+"' from stk_service_hd_temp ";
            db.addQuery(insertHd_local);

            String insertDt_local="insert into stk_service_dt(Doc1No, repairpartscode, repairpartsdesc, " +
                    " repairpartsserialno, repairpartsqty, repairunitcost, " +
                    " repairlineamount, L_ink, " +
                    " BlankLine, N_o, ItemCode, " +
                    " Description, Qty, FactorQty, " +
                    " UOM, UOMSingular, HCUnitCost, " +
                    " DisRate1, DisRate2, DisRate3, " +
                    " HCDiscount, TaxRate1, TaxRate2, " +
                    " TaxRate3, DetailTaxCode, DetailTaxType, " +
                    " HCTax, HCLineAmt, DocType, " +
                    " DefectCode, SynYN)" +
                    " select '"+Doc1No+"', ItemCode, Description, " +
                    " '',  Qty, HCUnitCost, " +
                    " HCLineAmt, '1', " +
                    " BlankLine, N_o, '', " +
                    " '', Qty, FactorQty, " +
                    " UOM, UOMSingular, '0', " +
                    " DisRate1, '0', '0', " +
                    " HCDiscount, TaxRate1, '0', " +
                    " '0', DetailTaxCode, '' , " +
                    " HCTax, '0' , DocType, " +
                    " '', '"+DBStatus+"' from cloud_cus_inv_dt where DocType='Service' ";
           long addLocal= db.addQuery(insertDt_local);
           if(addLocal>0){
               fnsavelastno();
               String qDel="delete from cloud_cus_inv_dt";
               String qDel2="delete from stk_service_hd_temp";
               db.addQuery(qDel);
               db.addQuery(qDel2);
               String stringPrint=genPrint();
               if (TypePrinter.equals("AIDL")) {
                   AidlUtil.getInstance().printText(stringPrint);
                   AidlUtil.getInstance().printBitmap(bmpSignature);
                   AidlUtil.getInstance().printText(vFooter);
               }else if(TypePrinter.equals("Ipos AIDL")){
                   IposAidlUtil.getInstance().setPrint(stringPrint);
                   IposAidlUtil.getInstance().printBitmap(bmpSignature);
                   IposAidlUtil.getInstance().setPrint(vFooter);
               } else if (TypePrinter.equals("Bluetooth")) {
                   BluetoothPrinter fncheck = new BluetoothPrinter();
                   isBT = fncheck.fnBluetooth(c, NamePrinter, stringPrint);
                   if (isBT == false) {
                       NewDoc = "error print";
                   }else{
                       BluetoothPrinter fncheck2 = new BluetoothPrinter();
                       Boolean isBT2 = fncheck2.fnBluetooth2(NamePrinter,bmpSignature);
                   }
               } else if (TypePrinter.equals("Bluetooth Zebra")) {
                   BluetoothZebra fncheck = new BluetoothZebra();
                   isBT = fncheck.fnBluetooth(c, NamePrinter, stringPrint);
                   if (isBT == false) {
                       NewDoc = "error print";
                   }
               } else if (TypePrinter.equals("Wifi")) {
                   int Port = Integer.parseInt(vPort);
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
               }
               z="success";
           }else{
               z="error";
           }
           db.closeDB();

        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }

    private String genPrint(){
        String strPrint="";
        try{
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            int len=48;
            int len1=9;
            int len1_1=19;
            int len1_2=20;
            int len2=6;
            int len2_1=42;
            int len3=6;
            int len3_1=13;
            int len3_2=12;
            int len3_3=15;
            int len4=33;
            int len4_1=15;

            String CompanyName="";
            String CompanyCode="";
            String GSTNo="";
            String Address="";
            String Fax="";
            String Tel="";
            String ComTown="";
            String ComState="";
            String ComCountry="";
            String CusName="";
            String CusCode="";
            String AdddresCus="";
            String Disc2="";
            String Title="";
            String receiptdate="";
            String productmodel="";
            String serialno="";
            String warrantystatus="";
            String Signature="";
            String Techinican="";
            String Company="select CurCode,CompanyName,CompanyCode," +
                    "GSTNo,Address,Tel1," +
                    "Fax1,CompanyEmail,ComTown," +
                    "ComState,ComCountry FROM companysetup ";
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
            String vLine    = str_pad("_", len, "_", "STR_PAD_BOTH");
            String vLine1   = str_pad(CompanyName, len, " ", "STR_PAD_BOTH");
            String vLine2   = str_pad("Co.Reg.No.: "+CompanyCode, len, " ", "STR_PAD_BOTH");
            String vLine3   = str_pad("GST Reg.No.: "+GSTNo, len, " ", "STR_PAD_BOTH");
            String AddressAll = Address.replaceAll("(.{"+len+"})", "$1;");
            String [] address=AddressAll.split(";");
            String vAddress="";
            for(String add:address){
                vAddress += str_pad(add,len," ", "STR_PAD_BOTH");
            }
            String vLine4      = vAddress;
            String vLine5      = str_pad(Tel, len, " ", "STR_PAD_BOTH");
            String vLine6      = str_pad(Fax, len, " ", "STR_PAD_BOTH");
            String vLine7      = str_pad("SERVICE", len, " ", "STR_PAD_BOTH");

            String qHeader="select Address,cuscode, " +
                    " cusname, mobileno, receiptno, " +
                    " receiptdate, repairtype, casetype, " +
                    " entryid, d_ate, outputid, " +
                    " outputdate, receivemode, termcode, " +
                    " productmodel, partno, serialno, " +
                    " supplierserialno, warrantystatus, warrantydesc, " +
                    " warrantyexpirydate, accessories, problemdesc," +
                    " servicestatus,  ImgService, Signature," +
                    " Technician " +
                    " from stk_service_hd where Doc1No='"+Doc1No+"' ";
            Cursor rsCus     = db.getQuery(qHeader);
            while (rsCus.moveToNext()) {
                AdddresCus          =rsCus.getString(0);
                CusCode             =rsCus.getString(1);
                CusName             =rsCus.getString(2);
                receiptdate         =rsCus.getString(5);
                productmodel        =rsCus.getString(14);
                serialno            =rsCus.getString(16);
                warrantystatus      =rsCus.getString(18);
                Signature           =rsCus.getString(25);
                Techinican          =rsCus.getString(26);
            }
            if(warrantystatus.equals("1")){
                warrantystatus="YES";
            }else{
                warrantystatus="NO";
            }
            String vLine8 =str_pad(CusName , len, " ", "STR_PAD_RIGHT");
            String vLine9 =str_pad("Outltet ", len, " ", "STR_PAD_RIGHT");
            String vLine10 =str_pad(AdddresCus , len, " ", "STR_PAD_RIGHT");
            String vLine11=str_pad(receiptdate , len, " ", "STR_PAD_RIGHT");
            String vLine12=str_pad("Model     : "+productmodel , len, " ", "STR_PAD_RIGHT");
            String vLine13=str_pad("Serial No : "+serialno , len, " ", "STR_PAD_RIGHT");
            String vLine14=str_pad("Warranty  : " +warrantystatus , len, " ", "STR_PAD_RIGHT");

            strPrint    +=vLine1+"\n";
            strPrint    +=vLine2+"\n";
            strPrint    +=vLine3+"\n";
            strPrint    +=vLine4+"\n";
            strPrint    +=vLine+"\n";
            strPrint    +=vLine7+"\n";
            strPrint    +=vLine+"\n";
            strPrint    +=vLine8+"\n";
            strPrint    +=vLine9+"\n";
            strPrint    +=vLine10+"\n";
            strPrint    +=vLine11+"\n";
            strPrint    +=vLine12+"\n";
            strPrint    +=vLine13+"\n";
            strPrint    +=vLine14+"\n";
            strPrint    +=vLine+"\n";

            String qDetail = "select ItemCode,IFNULL(repairpartsqty,0) as Qty, UOM, DetailTaxCode, substr(repairpartsdesc,1,45) as Description," +
                    "IFNULL(repairunitcost,0) as HCUnitCost,IFNULL(repairlineamount,0) AS HCLineAmt,IFNULL(DisRate1,0) as DisRate1," +
                    "IFNULL(HCDiscount,0) as HCDiscount, repairpartsserialno from stk_service_dt Where Doc1No='" + Doc1No + "' ";
            Cursor rsDt=db.getQuery(qDetail);
            Double dTotalAmt=0.00;
            while(rsDt.moveToNext()){
                String ItemCode         = rsDt.getString(0);
                String Qty              = twoDecimal(rsDt.getDouble(1));
                String UOM              = rsDt.getString(2);
                String DetailTaxCode    = rsDt.getString(3);
                String Description      = rsDt.getString(4);
                String HCUnitCost       = twoDecimal(rsDt.getDouble(5));
                dTotalAmt               +=rsDt.getDouble(6);
                String HCLineAmt        = twoDecimal(rsDt.getDouble(6));
                String DisRate1         = twoDecimal(rsDt.getDouble(7));
                String HCDiscount       = twoDecimal(rsDt.getDouble(8));
                if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                    Disc2 = "(" + HCDiscount + ")";
                } else {
                    Disc2 = " ";
                }
                String AlternateItem    = rsDt.getString(9);
                String vLine15           = str_pad(Description, len2_1, " ", "STR_PAD_RIGHT");
                String vLine15_1        = str_pad(UOM, len2, " ", "STR_PAD_RIGHT");
                String vLine16_1        = str_pad("", len2, " ", "STR_PAD_RIGHT");
                String vLine16_2        = str_pad(AlternateItem, len2_1, " ", "STR_PAD_RIGHT");
                String vLine17_1        = str_pad(Qty, len3, " ", "STR_PAD_LEFT");
                String vLine17_2        = str_pad(HCUnitCost, len3_1, " ", "STR_PAD_LEFT");
                String vLine17_3        = str_pad(Disc2, len3_2, " ", "STR_PAD_LEFT");
                String vLine17_4        = str_pad(HCLineAmt, len3_3, " ", "STR_PAD_LEFT");
                strPrint                += vLine15+vLine15_1 + "\n";
                if(AlternateItem.length()>1){
                    strPrint            += vLine16_1+vLine16_2+"\n";
                }
                strPrint                +=  vLine17_1 + " x" + vLine17_2 + vLine17_3 + vLine17_4 + "\n";
            }
            strPrint                    +=vLine+"\n";
            String vLine18              = str_pad("Grand Total :", len4, " ", "STR_PAD_LEFT");
            String vLine18_1            = str_pad(twoDecimal(dTotalAmt), len4_1, " ", "STR_PAD_LEFT");
            strPrint                    +=vLine18+vLine18_1+"\n";
            strPrint                    +=vLine+"\n";

            String vLine19                  = str_pad("Techinician Name", len, " ", "STR_PAD_RIGHT");
            String vLine20                  = str_pad(Techinican, len, " ", "STR_PAD_RIGHT");
            String vLine21                  = str_pad("Checked and accepted ", len, " ", "STR_PAD_RIGHT");
            String vLine22                  = str_pad(  "Checked By     : ", len, " ", "STR_PAD_RIGHT");
            String vLine23                  = str_pad(  "Company Stamp  : ", len, " ", "STR_PAD_RIGHT");
            String vLine24                  = str_pad(  "Date           : ", len, " ", "STR_PAD_RIGHT");
            strPrint                        +=vLine19+"\n";
            strPrint                        +=vLine20+"\n\n";
            String Footer                   ="";
            Footer                         += vLine21+"\n";
            Footer                         += vLine22+"\n";
            Footer                         += vLine23+"\n";
            Footer                         += vLine24+"\n";
            vFooter                        =Footer;
            db.closeDB();
            return strPrint;
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return strPrint ;
    }

    private void fnsavelastno(){
        String RunNo="";
        String queryLast="Select RunNo,LastNo from sys_runno_dt where RunNoCode='Service' ";
        DBAdapter dbAdapter  = new DBAdapter(c);
        dbAdapter.openDB();
        Cursor rsLast   =dbAdapter.getQuery(queryLast);
        int NewNo=0;
        while (rsLast.moveToNext()) {
            RunNo   = rsLast.getString(0);
            String LastNo  = "1"+rsLast.getString(1);
            NewNo   = (Integer.parseInt(LastNo)) + 1;
        }
        NewDoc = String.valueOf(NewNo);
        String vNewDoc = NewDoc.substring(1,NewDoc.length());
        ContentValues cv=new ContentValues();
        cv.put("LastNo",vNewDoc);
        dbAdapter.UpdateSysRunNo(cv,RunNo);
    }
    public String str_pad(String input, int length, String pad, String    sense) {
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
        else {
            padded = pad.substring(0,resto);
        }
        return padded;
    }
    private String twoDecimal(Double values){
        String textDecimal="";
        textDecimal=String.format(Locale.US, "%,.2f", values);
        return textDecimal;
    }
}
