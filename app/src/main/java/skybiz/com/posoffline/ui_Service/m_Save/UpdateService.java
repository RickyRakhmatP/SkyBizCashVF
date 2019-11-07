package skybiz.com.posoffline.ui_Service.m_Save;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothPrinter;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothZebra;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_IposAIDL.Utils.IposAidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_USB.PrintingUSB;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI.PrintingWifi;
import skybiz.com.posoffline.ui_Service.MService;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class UpdateService extends AsyncTask<Void,Void,String> {
    Context c;
    String Doc1No,ActionTimeStart,ActionTimeEnd,
            ServiceNoteRemark,ServiceStatus,PhotoFile,PhotoFile2;
   // File PhotoFile2;
    String IPAddress,UserName,Password,
            Port,DBName,URL,
            z,DBStatus,EncodeType,
            BranchCode,LocationCode;

    String TypePrinter,NamePrinter,IPPrinter,vPort,vFooter,NewDoc;

    Connection conn;
    Boolean isBT;

    public UpdateService(Context c, String Doc1No, String ActionTimeStart,
                         String ActionTimeEnd, String ServiceNoteRemark, String ServiceStatus,
                         String PhotoFile, String photoFile2) {
        this.c = c;
        this.Doc1No = Doc1No;
        this.ActionTimeStart = ActionTimeStart;
        this.ActionTimeEnd = ActionTimeEnd;
        this.ServiceNoteRemark = ServiceNoteRemark;
        this.ServiceStatus = ServiceStatus;
        this.PhotoFile = PhotoFile;
        this.PhotoFile2 = photoFile2;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.fnupdate();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("error")){
            Toast.makeText(c,"Failure update service hd", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Success update service hd", Toast.LENGTH_SHORT).show();
            final Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((MService)c).refreshNext();
                }
            },500);
        }
    }
    private String fnupdate(){
        try{
            z="error";
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName, UserName, Password, " +
                    "DBName, Port, DBStatus, " +
                    "EncodeType, BranchCode, LocationCode " +
                    "from tb_setting ";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress       = curSet.getString(0);
                UserName        = curSet.getString(1);
                Password        = curSet.getString(2);
                DBName          = curSet.getString(3);
                Port            = curSet.getString(4);
                DBStatus        = curSet.getString(5);
                EncodeType      = curSet.getString(6);
                BranchCode      = curSet.getString(7);
                LocationCode    = curSet.getString(8);
            }
            Cursor cPrint=db.getSettingPrint();
            while (cPrint.moveToNext()) {
                TypePrinter = cPrint.getString(1);
                NamePrinter = cPrint.getString(2);
                IPPrinter = cPrint.getString(3);
                vPort= cPrint.getString(5);
            }
            if(DBStatus.equals("1")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                conn = Connector.connect(URL, UserName, Password);
                //conn= Connect_db.getConnection();
                if (conn != null) {
                    //, PhotoFile2="+PhotoFile2+"
                    /*String update="update stk_service_hd set PhotoFile='"+PhotoFile+"' " +
                            "where Doc1No='"+Doc1No+"' ";
                    Log.d("UPDATE",update);
                    Statement stmtUp = conn.createStatement();
                    stmtUp.execute(update);*/
                    FileInputStream fin=null;
                    File imgfile=null;
                    if(!PhotoFile2.isEmpty()) {
                        imgfile = new File(PhotoFile2);
                        fin = new FileInputStream(imgfile);
                    }
                    PreparedStatement pre =
                            conn.prepareStatement("update stk_service_hd set " +
                                    "ActionTimeStart=?, ActionTimeEnd=?, " +
                                    "servicenoteremark=?, servicestatus=?, " +
                                    "PhotoFile=?, PhotoFile2=?, " +
                                    "PhotoFileName=?  where Doc1No =?");
                    pre.setString(1,ActionTimeStart);
                    pre.setString(2,ActionTimeEnd);
                    pre.setString(3,ServiceNoteRemark);
                    pre.setString(4,ServiceStatus);
                    pre.setString(5,PhotoFile);
                    pre.setBlob(6, fin, (int) imgfile.length());
                   /* if(!PhotoFile2.isEmpty()) {
                        pre.setBlob(6, fin, (int) imgfile.length());
                    }else{
                        pre.setNull(6,0);
                    }*/
                    pre.setString(7,PhotoFile2);
                    pre.setString(8,Doc1No);
                    pre.executeUpdate();

                    String check="select count(*)as numrows from cloud_cus_inv_dt where DocType='Service' ";
                    Cursor rsCheck=db.getQuery(check);
                    int numrows=0;
                    while(rsCheck.moveToNext()){
                        numrows=rsCheck.getInt(0);
                    }
                    if(numrows>0) {
                        String Delete="delete from stk_service_dt where Doc1No='"+Doc1No+"' ";
                        Statement stmtDel = conn.createStatement();
                        stmtDel.execute(Delete);
                        String qDetail = "select '" + Doc1No + "', ItemCode, Description, " +
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
                                " from cloud_cus_inv_dt where DocType='Service' and N_o='a' ";
                        Cursor rsDet = db.getQuery(qDetail);
                        while (rsDet.moveToNext()) {
                            String insertDt = "insert into stk_service_dt(Doc1No, repairpartscode, repairpartsdesc, " +
                                    " repairpartsserialno, repairpartsqty, repairunitcost," +
                                    " repairlineamount, L_ink," +
                                    " BlankLine, N_o, ItemCode, " +
                                    " Description, Qty, FactorQty, " +
                                    " UOM, UOMSingular, HCUnitCost, " +
                                    " DisRate1, DisRate2, DisRate3, " +
                                    " HCDiscount, TaxRate1, TaxRate2, " +
                                    " TaxRate3, DetailTaxCode, DetailTaxType, " +
                                    " HCTax, HCLineAmt, DocType, " +
                                    " DefectCode) values('" + rsDet.getString(0) + "', '" + rsDet.getString(1) + "', '" + rsDet.getString(2) + "'," +
                                    " '" + rsDet.getString(3) + "', '" + rsDet.getString(4) + "', '" + rsDet.getString(5) + "'," +
                                    " '" + rsDet.getString(6) + "', '" + rsDet.getString(7) + "' ," +
                                    " '" + rsDet.getString(9) + "', '" + rsDet.getString(10) + "', '" + rsDet.getString(11) + "'," +
                                    " '" + rsDet.getString(12) + "', '" + rsDet.getString(4) + "', '" + rsDet.getString(14) + "'," +
                                    " '" + rsDet.getString(15) + "', '" + rsDet.getString(16) + "', '" + rsDet.getString(17) + "'," +
                                    " '" + rsDet.getString(18) + "', '" + rsDet.getString(19) + "', '" + rsDet.getString(20) + "'," +
                                    " '" + rsDet.getString(21) + "', '" + rsDet.getString(22) + "', '" + rsDet.getString(23) + "'," +
                                    " '" + rsDet.getString(24) + "', '" + rsDet.getString(25) + "', '" + rsDet.getString(26) + "'," +
                                    " '" + rsDet.getString(27) + "', '" + rsDet.getString(28) + "', '" + rsDet.getString(29) + "'," +
                                    " '" + rsDet.getString(30) + "')";
                            Log.d("INSERT DT", insertDt);
                            Statement stmtDet = conn.createStatement();
                            stmtDet.executeQuery("SET NAMES 'LATIN1'");
                            stmtDet.executeQuery("SET CHARACTER SET 'LATIN1'");
                            stmtDet.execute(insertDt);
                        }

                    }else{

                    }
                    z="success";
                }else{
                    z="success";
                }
                if(z.equals("success")){
                    String qDel="delete from cloud_cus_inv_dt";
                    String qDel2="delete from stk_service_hd_temp";
                    db.addQuery(qDel);
                    db.addQuery(qDel2);
                    String stringPrint=genPrint();
                    if (TypePrinter.equals("AIDL")) {
                        AidlUtil.getInstance().printText(stringPrint);
                        AidlUtil.getInstance().printText(vFooter);
                    }else if(TypePrinter.equals("Ipos AIDL")){
                        IposAidlUtil.getInstance().setPrint(stringPrint);
                        IposAidlUtil.getInstance().setPrint(vFooter);
                    }else if(TypePrinter.equals("Ipos AIDL")){
                    } else if (TypePrinter.equals("Bluetooth")) {
                        BluetoothPrinter fncheck = new BluetoothPrinter();
                        isBT = fncheck.fnBluetooth(c, NamePrinter, stringPrint);
                        if (isBT == false) {
                            NewDoc = "error print";
                        }else{
                           // BluetoothPrinter fncheck2 = new BluetoothPrinter();
                            //Boolean isBT2 = fncheck2.fnBluetooth2(NamePrinter,bmpSignature);
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
                }
            }
            db.closeDB();
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }catch (Exception e){
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
            int len10_2=24;
            if(TypePrinter.equals("AIDL") || TypePrinter.equals("Ipos AIDL")){
                 len=32;
                 len1=9;
                 len1_1=19;
                 len1_2=20;
                 len2=6;
                 len2_1=26;
                 len3=6;
                 len3_1=8;
                 len3_2=8;
                 len3_3=10;
                 len4=20;
                 len4_1=12;
                 len10_2=16;
            }
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
            String Contact="";
            String ContactTel="";
            String D_ate="";
            String T_ime="";
            String SoNo="";
            String AssetNo="";
            String InstallationDate="";
            String TimeIn="";
            String TimeOut="";
            String CaseType="";
            String ProblemDesc="";
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
                    " DATE_FORMAT(T_ime,'%Y-%m-%d')as receiptdate, repairtype, casetype, " +
                    " entryid, DATE_FORMAT(T_ime,'%Y-%m-%d')as d_ate, outputid, " +
                    " outputdate, receivemode, termcode, " +
                    " productmodel, partno, serialno, " +
                    " supplierserialno, warrantystatus, warrantydesc, " +
                    " DATE_FORMAT(T_ime,'%Y-%m-%d')as warrantyexpirydate, accessories, problemdesc," +
                    " servicestatus,  PhotoFile2, ''," +
                    " Technician, Contact, DATE_FORMAT(T_ime,'%H:%i:%s')as T_ime," +
                    " DATE_FORMAT(T_ime,'%Y-%m-%d %H:%i:%s')as InstallationDate, DATE_FORMAT(T_ime,'%H:%i:%s')as ActionTimeStart, " +
                    " DATE_FORMAT(T_ime,'%H:%i:%s')as ActionTimeEnd  " +
                    " from stk_service_hd where Doc1No='"+Doc1No+"' ";
            if(DBStatus.equals("0")) {
                Cursor rsCus = db.getQuery(qHeader);
                while (rsCus.moveToNext()) {
                    AdddresCus = rsCus.getString(0);
                    CusCode = rsCus.getString(1);
                    CusName = rsCus.getString(2);
                    ContactTel = rsCus.getString(3);
                    SoNo = rsCus.getString(4);
                    receiptdate = rsCus.getString(5);
                    CaseType = rsCus.getString(7);
                    D_ate = rsCus.getString(9);
                    productmodel = rsCus.getString(14);
                    serialno = rsCus.getString(16);
                    warrantystatus = rsCus.getString(18);
                    ProblemDesc = rsCus.getString(22);
                    Signature = rsCus.getString(25);
                    Techinican = rsCus.getString(26);
                    Contact = rsCus.getString(27);
                    T_ime = rsCus.getString(28);
                    AssetNo = rsCus.getString(15);
                    InstallationDate = rsCus.getString(29);
                    TimeIn = rsCus.getString(30);
                    TimeOut = rsCus.getString(31);
                }
            }else{
                if (conn != null) {
                    Statement stmtCus = conn.createStatement();
                    stmtCus.execute(qHeader);
                    ResultSet rsCus = stmtCus.getResultSet();
                    while (rsCus.next()) {
                        AdddresCus = rsCus.getString(1);
                        CusCode = rsCus.getString(2);
                        CusName = rsCus.getString(3);
                        ContactTel = rsCus.getString(4);
                        SoNo = rsCus.getString(5);
                        receiptdate = rsCus.getString(6);
                        CaseType = rsCus.getString(8);
                        D_ate = rsCus.getString(10);
                        productmodel = rsCus.getString(15);
                        serialno = rsCus.getString(17);
                        warrantystatus = rsCus.getString(19);
                        ProblemDesc = rsCus.getString(23);
                        Signature = rsCus.getString(26);
                        Techinican = rsCus.getString(27);
                        Contact = rsCus.getString(28);
                        T_ime = rsCus.getString(29);
                        AssetNo = rsCus.getString(16);
                        InstallationDate = rsCus.getString(30);
                        TimeIn = rsCus.getString(31);
                        TimeOut = rsCus.getString(32);
                    }
                }
            }
            if(warrantystatus.equals("1")){
                warrantystatus="YES";
            }else{
                warrantystatus="NO";
            }
            String vLine8 =str_pad(CusName , len, " ", "STR_PAD_RIGHT");
            String vLine9 =str_pad("Outltet ", len, " ", "STR_PAD_RIGHT");
            String vLine10 =str_pad(AdddresCus , len, " ", "STR_PAD_RIGHT");
            String vLine10_1 =str_pad("Contact      :"+ Contact, len, " ", "STR_PAD_RIGHT");
            String vLine10_2 =str_pad("Contact No   :"+ ContactTel, len, " ", "STR_PAD_RIGHT");
            String vLine10_3 =str_pad("Date  : "+ D_ate, len10_2, " ", "STR_PAD_RIGHT");
            String vLine10_4 =str_pad("Time  : "+ T_ime, len10_2, " ", "STR_PAD_RIGHT");
            String vLine10_5 =str_pad("SO No.       :"+ SoNo, len10_2, " ", "STR_PAD_RIGHT");
           // String vLine11=str_pad(receiptdate , len, " ", "STR_PAD_RIGHT");
            String vLine12=str_pad("Model           : "+productmodel , len, " ", "STR_PAD_RIGHT");
            String vLine13=str_pad("Serial No       : "+serialno , len, " ", "STR_PAD_RIGHT");
            String vLine14=str_pad("Asset No        : "+AssetNo , len, " ", "STR_PAD_RIGHT");
            String vLine18=str_pad("Installation Date : "+InstallationDate , len, " ", "STR_PAD_RIGHT");
            String vLine19=str_pad("Warranty        : " +warrantystatus , len, " ", "STR_PAD_RIGHT");
            String vLine20=str_pad("Priority        : " +warrantystatus , len, " ", "STR_PAD_RIGHT");
            String vLine20_1=str_pad("Types Of Job  : " +CaseType , len, " ", "STR_PAD_RIGHT");
            String vLine20_2=str_pad("Customer complaints  : " +ProblemDesc , len, " ", "STR_PAD_RIGHT");
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
            strPrint    +=vLine10_1+"\n";
            strPrint    +=vLine10_2+"\n";
            strPrint    +=vLine10_3+vLine10_4+"\n";
            strPrint    +=vLine10_5+"\n";
            strPrint    +=vLine12+"\n";
            strPrint    +=vLine13+"\n";
            strPrint    +=vLine14+"\n";
            strPrint    +=vLine18+"\n";
            strPrint    +=vLine19+"\n";
            strPrint    +=vLine20+"\n";
            strPrint    +=vLine20_1+"\n";
            strPrint    +=vLine20_2+"\n";
            strPrint    +=vLine+"\n";

            String qDetail = "select ItemCode,IFNULL(repairpartsqty,0) as Qty, UOM, DetailTaxCode, substr(repairpartsdesc,1,45) as Description," +
                    "IFNULL(repairunitcost,0) as HCUnitCost,IFNULL(repairlineamount,0) AS HCLineAmt,IFNULL(DisRate1,0) as DisRate1," +
                    "IFNULL(HCDiscount,0) as HCDiscount, repairpartsserialno from stk_service_dt Where Doc1No='" + Doc1No + "' ";
            Double dTotalAmt=0.00;
            if(DBStatus.equals("0")){
                Cursor rsDt=db.getQuery(qDetail);
                while (rsDt.moveToNext()) {
                    String ItemCode = rsDt.getString(0);
                    String Qty = twoDecimal(rsDt.getDouble(1));
                    String UOM = rsDt.getString(2);
                    String DetailTaxCode = rsDt.getString(3);
                    String Description = rsDt.getString(4);
                    String HCUnitCost = twoDecimal(rsDt.getDouble(5));
                    dTotalAmt += rsDt.getDouble(6);
                    String HCLineAmt = twoDecimal(rsDt.getDouble(6));
                    String DisRate1 = twoDecimal(rsDt.getDouble(7));
                    String HCDiscount = twoDecimal(rsDt.getDouble(8));
                    if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                        Disc2 = "(" + HCDiscount + ")";
                    } else {
                        Disc2 = " ";
                    }
                    String AlternateItem = rsDt.getString(9);
                    String vLine15 = str_pad(Description, len2_1, " ", "STR_PAD_RIGHT");
                    String vLine15_1 = str_pad(UOM, len2, " ", "STR_PAD_RIGHT");
                    String vLine16_1 = str_pad("", len2, " ", "STR_PAD_RIGHT");
                    String vLine16_2 = str_pad(AlternateItem, len2_1, " ", "STR_PAD_RIGHT");
                    String vLine17_1 = str_pad(Qty, len3, " ", "STR_PAD_LEFT");
                    String vLine17_2 = str_pad(HCUnitCost, len3_1, " ", "STR_PAD_LEFT");
                    String vLine17_3 = str_pad(Disc2, len3_2, " ", "STR_PAD_LEFT");
                    String vLine17_4 = str_pad(HCLineAmt, len3_3, " ", "STR_PAD_LEFT");
                    strPrint += vLine15 + vLine15_1 + "\n";
                    if (AlternateItem.length() > 1) {
                        strPrint += vLine16_1 + vLine16_2 + "\n";
                    }
                    strPrint += vLine17_1 + " x" + vLine17_2 + vLine17_3 + vLine17_4 + "\n";
                }
            }else {
                if (conn != null) {
                    Statement stmtDt = conn.createStatement();
                    stmtDt.execute(qDetail);
                    ResultSet rsDt = stmtDt.getResultSet();
                    while (rsDt.next()) {
                        String ItemCode = rsDt.getString(1);
                        String Qty = twoDecimal(rsDt.getDouble(2));
                        String UOM = rsDt.getString(3);
                        String DetailTaxCode = rsDt.getString(4);
                        String Description = rsDt.getString(5);
                        String HCUnitCost = twoDecimal(rsDt.getDouble(6));
                        dTotalAmt += rsDt.getDouble(7);
                        String HCLineAmt = twoDecimal(rsDt.getDouble(7));
                        String DisRate1 = twoDecimal(rsDt.getDouble(8));
                        String HCDiscount = twoDecimal(rsDt.getDouble(9));
                        if (!DisRate1.equals("0.00") || !HCDiscount.equals("0.00")) {
                            Disc2 = "(" + HCDiscount + ")";
                        } else {
                            Disc2 = " ";
                        }
                        String AlternateItem = rsDt.getString(10);
                        String vLine15 = str_pad(Description, len2_1, " ", "STR_PAD_RIGHT");
                        String vLine15_1 = str_pad(UOM, len2, " ", "STR_PAD_RIGHT");
                        String vLine16_1 = str_pad("", len2, " ", "STR_PAD_RIGHT");
                        String vLine16_2 = str_pad(AlternateItem, len2_1, " ", "STR_PAD_RIGHT");
                        String vLine17_1 = str_pad(Qty, len3, " ", "STR_PAD_LEFT");
                        String vLine17_2 = str_pad(HCUnitCost, len3_1, " ", "STR_PAD_LEFT");
                        String vLine17_3 = str_pad(Disc2, len3_2, " ", "STR_PAD_LEFT");
                        String vLine17_4 = str_pad(HCLineAmt, len3_3, " ", "STR_PAD_LEFT");
                        strPrint += vLine15 + vLine15_1 + "\n";
                        if (AlternateItem.length() > 1) {
                            strPrint += vLine16_1 + vLine16_2 + "\n";
                        }
                        strPrint += vLine17_1 + " x" + vLine17_2 + vLine17_3 + vLine17_4 + "\n";
                    }
                }
            }
            strPrint                    +=vLine+"\n\n\n";
            String vLine21              = str_pad("Grand Total :", len4, " ", "STR_PAD_LEFT");
            String vLine21_1            = str_pad(twoDecimal(dTotalAmt), len4_1, " ", "STR_PAD_LEFT");
            strPrint                    +=vLine21+vLine21_1+"\n";
            strPrint                    +=vLine+"\n";

            //footer
            String vLine22                  = str_pad("Techinician Name : ", len, " ", "STR_PAD_RIGHT");
            String vLine23                  = str_pad("Time In          : "+ActionTimeStart, len, " ", "STR_PAD_RIGHT");
            String vLine24                  = str_pad("Time Out         : "+ActionTimeEnd, len, " ", "STR_PAD_RIGHT");
           // String vLine25                  = str_pad(Techinican, len, " ", "STR_PAD_RIGHT");
            String vLine26                  = str_pad("Checked and accepted ", len, " ", "STR_PAD_RIGHT");
            String vLine27                  = str_pad(  "Checked By     : ", len, " ", "STR_PAD_RIGHT");
            String vLine28                  = str_pad(  "Company Stamp  : ", len, " ", "STR_PAD_RIGHT");
            String vLine29                  = str_pad(  "Date           : "+D_ate, len, " ", "STR_PAD_RIGHT");
            strPrint                        +=vLine22+"\n";
            strPrint                        +=vLine23+"\n";
            strPrint                        +=vLine24+"\n";
           // strPrint                        +=vLine25+"\n";
            strPrint                        +=vLine26+"\n";
            strPrint                        +=vLine27+"\n";
            strPrint                        +=vLine28+"\n";
            strPrint                        +=vLine29+"\n\n\n";
            //String Footer                   ="";
           // Footer                         += vLine24+"\n";
            //Footer                         += vLine25+"\n";
           // Footer                         += vLine26+"\n";
           // Footer                         += vLine27+"\n";
           // vFooter                        =Footer;
            db.closeDB();
            return strPrint;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return strPrint ;
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
