package skybiz.com.posoffline.ui_Listing;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothPrinter;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothZebra;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_IposAIDL.Utils.IposAidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_USB.PrintingUSB;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI.PrintingWifi;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class PrintList extends AsyncTask<Void,Void,String> {
    Context c;
    String DocType,DateFrom,DateTo,T_ype;
    String z,TypePrinter,IPPrinter,
            NamePrinter,vPort,PaperSize;
    String IPAddress,UserName,Password,
            DBName,Port,URL,
            DBStatus,Mgt01YN,CurCode;
    Boolean isBT;

    public PrintList(Context c, String docType,String T_ype, String dateFrom, String dateTo) {
        this.c = c;
        DocType = docType;
        this.T_ype = T_ype;
        DateFrom = dateFrom;
        DateTo = dateTo;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.fnprint();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("error print")){
            Toast.makeText(c,"Print Failure", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Print successful", Toast.LENGTH_SHORT).show();
        }
    }

    private String fnprint() {
        try {
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String qCom="select CurCode from companysetup";
            Cursor rsCom=db.getQuery(qCom);
            while(rsCom.moveToNext()){
                CurCode=rsCom.getString(0);
            }
            String querySet="select ServerName,UserName,Password," +
                    "DBName,Port,DBStatus," +
                    "Mgt01YN" +
                    " from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(0);
                UserName = curSet.getString(1);
                Password = curSet.getString(2);
                DBName = curSet.getString(3);
                Port = curSet.getString(4);
                DBStatus=curSet.getString(5);
                Mgt01YN=curSet.getString(6);
            }
            Cursor cPrint=db.getSettingPrint();
            while (cPrint.moveToNext()) {
                TypePrinter = cPrint.getString(1);
                NamePrinter = cPrint.getString(2);
                IPPrinter   = cPrint.getString(3);
                vPort       = cPrint.getString(5);
                PaperSize   = cPrint.getString(6);
            }

            String TableHd="",TableDt="";
            String Header="";
            if(DocType.equals("SO")){
                TableHd="stk_sales_order_hd";
                TableDt="stk_sales_order_dt";
                Header="Sales Order Listing";
            }else if(DocType.equals("CS")){
                TableHd="stk_cus_inv_hd";
                TableDt="stk_cus_inv_dt";
                Header="Cash Receipt Listing";
            }else if(DocType.equals("CusCN")){
                TableHd="stk_cus_inv_hd";
                TableDt="stk_cus_inv_dt";
                Header="Credit Note Listing";
            }
            int Long=48;
            int LongDate1=15;
            int LongDate2=33;
            int LongRef1=10;
            int LongRef2=25;
            int LongRef3=13;
            int LongCus=38;
            if(PaperSize.equals("58mm")){
                 Long=32;
                 LongDate1=10;
                 LongDate2=22;
                 LongRef1=9;
                 LongRef2=13;
                 LongRef3=10;
                 LongCus=22;
            }
            String strPrint="";
            String vLine1="";
            String vLine2="";
            String vLine2_1="";
            String vLine3="";
            String vLine3_1="";
            String vLine4="";
            String vLine5="";
            String vLine5_1="";
            String vLine5_2="";
            String vLine6="";
            String vLine6_1="";
            String vLine6_2="";
            String vLine7="";
            String vLine7_1="";
            String vLine8="";
            String vLine9="";
            String vLine9_1="";
            vLine1              =str_pad("*** "+Header+" ***", Long, " ", "STR_PAD_BOTH");
            vLine2              =str_pad("Date From ", LongDate1, " ", "STR_PAD_RIGHT");
            vLine2_1            =str_pad(DateFrom, LongDate2, " ", "STR_PAD_RIGHT");
            vLine3              =str_pad("Date To ", LongDate1, " ", "STR_PAD_RIGHT");
            vLine3_1            =str_pad(DateTo, LongDate2, " ", "STR_PAD_RIGHT");
            strPrint            +=vLine1+"\n\n";
            strPrint            +=vLine2+vLine2_1+"\n";
            strPrint            +=vLine3+vLine3_1+"\n";
            String vLine        =str_pad("_", Long, "_", "STR_PAD_BOTH");
            String Dated="";
            strPrint            += vLine+"\n";
            if(DocType.equals("CS")){
                Dated="H.D_ateTime";
            }else{
                Dated="H.D_ate";
            }
            String query = "select H.Doc1No,H.D_ate,H.HCNetAmt, " +
                    " '1' as SynYN,Sum(D.Qty) as Qty, IFNULL(C.CusName,'') as CusName," +
                    " "+Dated+", IFNULL(C.CusCode,'') as CusName, H.Doc2No " +
                    " from " + TableHd + " H inner join " + TableDt + " D on D.Doc1No=H.Doc1No " +
                    " left join customer C ON H.CusCode=C.CusCode " +
                    " where H.DocType='" + DocType + "' and H.D_ate>='" + DateFrom + "' and H.D_ate<='" + DateTo + "' " +
                    " Group By H.Doc1No Order By H.D_ate Desc";
            if(DBStatus.equals("0")) {
                Cursor rsData = db.getQuery(query);
                while (rsData.moveToNext()) {
                    final Double dQty = Double.parseDouble(rsData.getString(4));
                    final String Qty = String.format(Locale.US, "%,.2f", dQty);
                    final Double dTotalAmt = Double.parseDouble(rsData.getString(2));
                    final String TotalAmt = String.format(Locale.US, "%,.2f", dTotalAmt);
                    vLine5      = str_pad(rsData.getString(0)+";"+rsData.getString(8), LongDate2, " ", "STR_PAD_RIGHT");
                    vLine5_1    = str_pad(CurCode+TotalAmt, LongDate1, " ", "STR_PAD_RIGHT");
                   // vLine5_2    = str_pad(Qty, LongRef3, " ", "STR_PAD_LEFT");
                    vLine6      = str_pad(rsData.getString(6)+";"+Qty, Long, " ", "STR_PAD_RIGHT");
                    //vLine6_1    = str_pad(rsData.getString(1), LongRef2, " ", "STR_PAD_RIGHT");
                   // vLine6_2    = str_pad(TotalAmt, LongRef3, " ", "STR_PAD_LEFT");
                    vLine7      = str_pad(rsData.getString(7)+";"+rsData.getString(5), Long, " ", "STR_PAD_RIGHT");
                   // vLine7_1    = str_pad(rsData.getString(5), LongCus, " ", "STR_PAD_RIGHT");
                   // vLine9      = str_pad("DateTime", LongRef1, " ", "STR_PAD_RIGHT");
                   // vLine9_1    = str_pad(rsData.getString(6), LongCus, " ", "STR_PAD_RIGHT");
                    strPrint    += vLine5 + vLine5_1  + "\n";
                    strPrint    += vLine6 + "\n";
                    strPrint    += vLine7 + "\n";

                    if(T_ype.equals("Detail")) {
                        String qDetail = "select ItemCode, SUBSTR(Description,1,"+LongDate2+")as Description, Qty," +
                                "HCUnitCost,HCDiscount from " + TableDt + " where Doc1No='" + rsData.getString(0) + "' ";
                        Cursor rsDt = db.getQuery(qDetail);
                        while (rsDt.moveToNext()) {
                            String ItemCode = rsDt.getString(0);
                            String Description = rsDt.getString(1);
                            String qty          = String.format(Locale.US, "%,.0f", rsDt.getDouble(2));
                            String UnitCost     = String.format(Locale.US, "%,.2f", rsDt.getDouble(3));
                            String HCDiscount   = String.format(Locale.US, "%,.2f", rsDt.getDouble(4));
                            String vLine10 = str_pad(ItemCode + ";", LongDate1, " ", "STR_PAD_RIGHT");
                            String vLine10_1 = str_pad(Description, LongDate2, " ", "STR_PAD_RIGHT");
                            String vLine11 = str_pad(qty + ";", LongRef1, " ", "STR_PAD_RIGHT");
                            String vLine11_1 = str_pad(UnitCost + ";", LongRef2, " ", "STR_PAD_LEFT");
                            String vLine11_2 = str_pad("(" + HCDiscount + ")", LongRef3, " ", "STR_PAD_LEFT");
                            strPrint += vLine10 + vLine10_1 + "\n";
                            strPrint += vLine11 + vLine11_1 + vLine11_2 + "\n";
                        }
                    }

                    strPrint    += vLine + "\n";
                }
            }else if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    Statement statement = conn.createStatement();
                    statement.execute(query);
                    ResultSet rsData = statement.getResultSet();
                    while (rsData.next()) {
                        final Double dQty = Double.parseDouble(rsData.getString(5));
                        final String Qty = String.format(Locale.US, "%,.2f", dQty);
                        final Double dTotalAmt = Double.parseDouble(rsData.getString(3));
                        final String TotalAmt = String.format(Locale.US, "%,.2f", dTotalAmt);
                        vLine5      = str_pad(rsData.getString(1)+";"+rsData.getString(9), LongDate2, " ", "STR_PAD_RIGHT");
                        vLine5_1    = str_pad(CurCode+TotalAmt, LongDate1, " ", "STR_PAD_RIGHT");
                        vLine6      = str_pad(rsData.getString(7)+";"+Qty, Long, " ", "STR_PAD_RIGHT");
                        vLine7      = str_pad(rsData.getString(8)+";"+rsData.getString(6), Long, " ", "STR_PAD_RIGHT");
                        strPrint    += vLine5 + vLine5_1  + "\n";
                        strPrint    += vLine6 + "\n";
                        strPrint    += vLine7 + "\n";
                       /* vLine5      = str_pad("Ref #", LongRef1, " ", "STR_PAD_RIGHT");
                        vLine5_1    = str_pad(rsData.getString(1), LongRef2, " ", "STR_PAD_RIGHT");
                        vLine5_2    = str_pad(Qty, LongRef3, " ", "STR_PAD_LEFT");
                        vLine6      = str_pad("Date", LongRef1, " ", "STR_PAD_RIGHT");
                        vLine6_1    = str_pad(rsData.getString(2), LongRef2, " ", "STR_PAD_RIGHT");
                        vLine6_2    = str_pad(TotalAmt, LongRef3, " ", "STR_PAD_LEFT");
                        vLine7      = str_pad(rsData.getString(8), LongRef1, " ", "STR_PAD_RIGHT");
                        vLine7_1    = str_pad(rsData.getString(6), LongCus, " ", "STR_PAD_RIGHT");
                        vLine9      = str_pad("DateTime", LongRef1, " ", "STR_PAD_RIGHT");
                        vLine9_1    = str_pad(rsData.getString(7), LongCus, " ", "STR_PAD_RIGHT");
                        strPrint    += vLine5 + vLine5_1 + vLine5_2 + "\n";
                        strPrint    += vLine6 + vLine6_1 + vLine6_2 + "\n";
                        strPrint    += vLine7 + vLine7_1 + "\n";
                        if(DocType.equals("CS")) {
                            strPrint += vLine9 + vLine9_1 + "\n";
                        }*/
                        if(T_ype.equals("Detail")) {
                            String qDetail = "select ItemCode, SUBSTR(Description,1,"+LongDate2+")as Description, Qty," +
                                    "HCUnitCost, HCDiscount from " + TableDt + " where Doc1No='" + rsData.getString(1) + "' ";
                            Statement stmtDt = conn.createStatement();
                            stmtDt.execute(qDetail);
                            ResultSet rsDt = stmtDt.getResultSet();
                            while (rsDt.next()) {
                                String ItemCode     = rsDt.getString(1);
                                String Description  = rsDt.getString(2);
                                String qty          = String.format(Locale.US, "%,.0f", rsDt.getDouble(3));
                                String UnitCost     = String.format(Locale.US, "%,.2f", rsDt.getDouble(4));
                                String HCDiscount   = String.format(Locale.US, "%,.2f", rsDt.getDouble(5));
                                String vLine10 = str_pad(ItemCode + ";", LongDate1, " ", "STR_PAD_RIGHT");
                                String vLine10_1 = str_pad(Description, LongDate2, " ", "STR_PAD_RIGHT");
                                String vLine11 = str_pad(qty + ";", LongRef1, " ", "STR_PAD_RIGHT");
                                String vLine11_1 = str_pad(UnitCost + ";", LongRef2, " ", "STR_PAD_LEFT");
                                String vLine11_2 = str_pad("(" + HCDiscount + ")", LongRef3, " ", "STR_PAD_LEFT");
                                strPrint += vLine10 + vLine10_1 + "\n";
                                strPrint += vLine11 + vLine11_1 + vLine11_2 + "\n";
                            }
                        }

                        strPrint    += vLine + "\n";
                    }
                }
            }
            vLine8              =str_pad(" End Print ", Long, " ", "STR_PAD_BOTH");
            strPrint            += "\n";
            strPrint            += vLine8+"\n\n\n";

            if (TypePrinter.equals("AIDL")) {
                AidlUtil.getInstance().printText(strPrint);
            }else if(TypePrinter.equals("Ipos AIDL")){
                IposAidlUtil.getInstance().setPrint(strPrint);
            } else if (TypePrinter.equals("Bluetooth")) {
                //Log.d("STR PRINT"+NamePrinter,stringPrint);
                BluetoothPrinter fncheck = new BluetoothPrinter();
                isBT = fncheck.fnBluetooth(c, NamePrinter, strPrint);
                if (isBT == false) {
                    z = "error print";
                }
            } else if (TypePrinter.equals("Bluetooth Zebra")) {
                //Log.d("STR PRINT"+NamePrinter,stringPrint);
                BluetoothZebra fncheck = new BluetoothZebra();
                isBT = fncheck.fnBluetooth(c, NamePrinter, strPrint);
                if (isBT == false) {
                    z = "error print";
                }
            } else if (TypePrinter.equals("Wifi")) {
                int Port = Integer.parseInt(vPort);
                PrintingWifi fnprintw = new PrintingWifi();
                isBT = fnprintw.fnprintwifi(c, IPPrinter, Port, strPrint);
                if (isBT == false) {
                    z = "error print";
                }
            } else if (TypePrinter.equals("USB")) {
                PrintingUSB fnprintu = new PrintingUSB();
                isBT = fnprintu.fnprintusb(c, strPrint);
                if (isBT == false) {
                    z = "error print";
                }
            } else {
                //No Printer
            }
            z="success";
            db.closeDB();
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
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
