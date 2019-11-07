package skybiz.com.posoffline.ui_Listing;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.widget.Toast;

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

public class PrintItemSales extends AsyncTask<Void,Void,String> {
    Context c;
    String DocType,DateFrom,DateTo,ItemGroup;
    String z,TypePrinter,IPPrinter,
            NamePrinter,vPort,PaperSize;
    String IPAddress,UserName,Password,
            DBName,Port,URL,DBStatus,Mgt01YN,
            CurCode;
    Boolean isBT;

    public PrintItemSales(Context c, String dateFrom, String dateTo, String ItemGroup) {
        this.c = c;
        DateFrom = dateFrom;
        DateTo = dateTo;
        this.ItemGroup = ItemGroup;
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

            int Line1_1=20;
            int Line1_2=4;
            int Line1_3=24;
            int Line2=48;
            int LineD=24;
            if(PaperSize.equals("58mm")){
                Line1_1=12;
                Line1_2=4;
                Line1_3=16;
                Line2=32;
                LineD=16;
            }
            String strPrint="";

            String vLine1       =str_pad("*** Product Sales ***", Line2, " ", "STR_PAD_BOTH");
            String vLine2       =str_pad("Date From ", LineD, " ", "STR_PAD_RIGHT");
            String vLine2_1     =str_pad(DateFrom, LineD, " ", "STR_PAD_RIGHT");
            String vLine3       =str_pad("Date To ", LineD, " ", "STR_PAD_RIGHT");
            String vLine3_1     =str_pad(DateTo, LineD, " ", "STR_PAD_RIGHT");
            strPrint            +=vLine1+"\n\n";
            strPrint            +=vLine2+vLine2_1+"\n";
            strPrint            +=vLine3+vLine3_1+"\n";
            String vLine        =str_pad("_", Line2, "_", "STR_PAD_BOTH");
            String Dated="";
            strPrint            += vLine+"\n";
            String vClause="";
            if(!ItemGroup.isEmpty()){
                vClause =" and M.ItemGroup='"+ItemGroup+"' ";
            }
            String sql = "select D.ItemCode, SUM(D.Qty)as Qty, SUM(D.HCLineAmt) as Amount," +
                    " D.Description " +
                    " from stk_cus_inv_hd H inner join stk_cus_inv_dt D ON H.Doc1No=D.Doc1No" +
                    " inner join stk_master M ON D.ItemCode=M.ItemCode " +
                    " where H.DocType='CS' and H.Status2<>'Void' " +
                    " and H.D_ate>='" + DateFrom + "' and H.D_ate<='" + DateTo + "' " +
                    " "+vClause+" " +
                    " Group By D.ItemCode Order By D.ItemCode ";
            Double dTotalAmount=0.00;
            if(DBStatus.equals("0")) {
                Cursor rsData = db.getQuery(sql);
                while (rsData.moveToNext()) {
                    Double dAmount  = rsData.getDouble(2);
                    String Amount   = String.format(Locale.US, "%,.2f", dAmount);
                    Double dQty     = rsData.getDouble(1);
                    String Qty      = String.format(Locale.US, "%,.0f", dQty);
                    String vLine4_1 = str_pad(rsData.getString(0), Line1_1, " ", "STR_PAD_RIGHT");
                    String vLine4_2 = str_pad(Qty, Line1_2," ", "STR_PAD_LEFT");
                    String vLine4_3 = str_pad(Amount, Line1_3," ", "STR_PAD_LEFT");
                    String vLine5 = str_pad(rsData.getString(3), Line2," ", "STR_PAD_RIGHT");
                    strPrint    += vLine5 + "\n";
                    strPrint    += vLine4_1 + vLine4_2  + vLine4_3+"\n";
                    strPrint    += vLine + "\n";
                    dTotalAmount +=dAmount;
                }
            }else if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    Statement statement = conn.createStatement();
                    statement.execute(sql);
                    ResultSet rsData = statement.getResultSet();
                    while (rsData.next()) {
                        Double dAmount  = rsData.getDouble(3);
                        String Amount   = String.format(Locale.US, "%,.2f", dAmount);
                        Double dQty     = rsData.getDouble(2);
                        String Qty      = String.format(Locale.US, "%,.0f", dQty);
                        String vLine4_1   = str_pad(rsData.getString(1), Line1_1, " ", "STR_PAD_RIGHT");
                        String vLine4_2 = str_pad(Qty, Line1_2," ", "STR_PAD_LEFT");
                        String vLine4_3 = str_pad(Amount, Line1_3," ", "STR_PAD_LEFT");
                        String vLine5 = str_pad(rsData.getString(4), Line2," ", "STR_PAD_RIGHT");
                        strPrint    += vLine5 + "\n";
                        strPrint    += vLine4_1 + vLine4_2  + vLine4_3+"\n";
                        strPrint    += vLine + "\n";
                        dTotalAmount +=dAmount;
                    }
                }
            }
            String TotalAmount   = String.format(Locale.US, "%,.2f", dTotalAmount);
            String vLine7       =str_pad("Grand Total : ", Line1_2+Line1_1, " ", "STR_PAD_LEFT");
            String vLine71       =str_pad(TotalAmount, Line1_3, " ", "STR_PAD_LEFT");

            strPrint            += vLine7+vLine71+"\n";
            strPrint            += vLine + "\n";
            String vLine6       =str_pad("*** End Print ***", Line2, " ", "STR_PAD_BOTH");
            strPrint            += "\n";
            strPrint            += vLine6+"\n\n\n";
            if (TypePrinter.equals("AIDL")) {
                AidlUtil.getInstance().printText(strPrint);
            }else if(TypePrinter.equals("Ipos AIDL")){
                IposAidlUtil.getInstance().setPrint(strPrint);
            } else if (TypePrinter.equals("Bluetooth")) {
                BluetoothPrinter fncheck = new BluetoothPrinter();
                isBT = fncheck.fnBluetooth(c, NamePrinter, strPrint);
                if (isBT == false) {
                    z = "error print";
                }
            } else if (TypePrinter.equals("Bluetooth Zebra")) {
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
