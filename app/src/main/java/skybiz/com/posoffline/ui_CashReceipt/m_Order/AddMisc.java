package skybiz.com.posoffline.ui_CashReceipt.m_Order;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;


public class AddMisc extends AsyncTask<Void,Void,String> {
    Context c;
    String OCCode,DocType;
    Double UnitPrice,dServiceCharges;
    String IPAddress,DBName,UserName,
            Password,URL,Port,z,
            DBStatus,UserCode,EncodeType,
            LocationCode,BranchCode,GroupByItemYN,
            CounterCode,AnalysisCode2;
    int LineNo=0;

    public AddMisc(Context c, String DocType, String AnalysisCode2) {
        this.c                 = c;
        this.DocType           = DocType;
        this.AnalysisCode2    = AnalysisCode2;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return this.fnaddmisc();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
    private String fnaddmisc(){
        try{
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName, UserName, Password, " +
                    "DBName, Port, DBStatus, " +
                    "ItemConn, EncodeType, UserCode, " +
                    "BranchCode, LocationCode, GroupByItemYN," +
                    "CounterCode " +
                    " from tb_setting";
            Cursor cur1=db.getQuery(querySet);
            while (cur1.moveToNext()) {
                IPAddress       = cur1.getString(0);
                UserName        = cur1.getString(1);
                Password        = cur1.getString(2);
                DBName          = cur1.getString(3);
                Port            = cur1.getString(4);
                DBStatus        = cur1.getString(5);
                EncodeType      = cur1.getString(7);
                UserCode        = cur1.getString(8);
                BranchCode      = cur1.getString(9);
                LocationCode    = cur1.getString(10);
                GroupByItemYN   = cur1.getString(11);
                CounterCode     = cur1.getString(12);
            }
            String qOtherSet="select ServiceCharges from tb_othersetting";
            Cursor rsOther=db.getQuery(qOtherSet);
            while(rsOther.moveToNext()){
                dServiceCharges=rsOther.getDouble(0);
            }

            String qCheckNo="select LineNo from cloud_cus_inv_dt where Computer='"+UserCode+"' Order By RunNo Desc ";
            Cursor rsCheckNo=db.getQuery(qCheckNo);
            while(rsCheckNo.moveToNext()){
                LineNo=rsCheckNo.getInt(0);
            }
            LineNo=LineNo+1;
            String sqlTotal = "select IFNULL(sum(HCLineAmt),'0')as AmountDue, " +
                    "IFNULL(sum(HCDiscount),'0') as TotalDiscount," +
                    "IFNULL(sum(HCTax),'0') as GSTAmount, " +
                    "sum(HCLineAmt+HCDiscount) as GrossAmt " +
                    "from cloud_cus_inv_dt where ComputerName='" + UserCode + "' " +
                    " and BlankLine='0' and ServiceChargeYN<>'0' ";
            Cursor rsTot = db.getQuery(sqlTotal);
            int inumRows=rsTot.getCount();
            while (rsTot.moveToNext()) {
                Double dAmountDue       = rsTot.getDouble(0);
                Double dHCGbDiscount    = rsTot.getDouble(1);
                Double dGSTAmt          = rsTot.getDouble(2);
                Double dGrossAmt        = rsTot.getDouble(3);
                UnitPrice               = (dServiceCharges / 100) * (dAmountDue-dGSTAmt);
            }

            OCCode="M999999SC";
            String Description  ="";
            String DetailTaxCode="";
            String qMisc="select Description,RetailTaxCode from stk_othercharges where OCCode='"+OCCode+"' ";
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1&rewriteBatchedStatements=true";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if(conn==null){

                }else{
                    Statement stmtMisc=conn.createStatement();
                    stmtMisc.execute(qMisc);
                    ResultSet rsMisc=stmtMisc.getResultSet();
                    while(rsMisc.next()){
                        Description     = rsMisc.getString(1);
                        DetailTaxCode   = rsMisc.getString(2);
                    }
                    if(!AnalysisCode2.equals("0") && DocType.equals("SO")){
                        String qDelSC="DELETE D.* FROM stk_sales_order_hd H " +
                                "INNER JOIN stk_sales_order_dt D ON H.Doc1No=D.Doc1No " +
                                " WHERE D.ItemCode='M999999SC' " +
                                " AND D.AnalysisCode2='"+AnalysisCode2+"' " +
                                " AND H.Status='Waiting' ";
                        Log.d("QUERY",qDelSC);
                        Statement stmtDelSC = conn.createStatement();
                        stmtDelSC.execute(qDelSC);
                        AnalysisCode2="0";
                    }
                }
            }else if(DBStatus.equals("0")){
                Cursor rsMisc=db.getQuery(qMisc);
                while(rsMisc.moveToNext()){
                    Description     = rsMisc.getString(0);
                    DetailTaxCode   = rsMisc.getString(1);
                }
                if(!AnalysisCode2.equals("0") && DocType.equals("SO")){
                    String qDelSC="DELETE D.* FROM stk_sales_order_hd H " +
                            " INNER JOIN stk_sales_order_dt D ON H.Doc1No=D.Doc1No " +
                            " WHERE D.ItemCode='M999999SC' " +
                            " AND D.AnalysisCode2='"+AnalysisCode2+"'" +
                            " AND H.Status='Waiting' ";
                    db.addQuery(qDelSC);
                    AnalysisCode2="0";
                }
            }
            Double FactorQty=1.00;
            Double Qty=1.0;
            Log.d("TAX CODE", DetailTaxCode);
            CalculateLineAmt vDataAmt = fncalculate(c, 1.00, UnitPrice, 0.00, 0.00, DetailTaxCode);
            Double  TaxRate1    = vDataAmt.getvR_ate();
            Double  HCDiscount  = vDataAmt.getvHCDiscount();
            Double  DisRate1    = vDataAmt.getvDisRate1();
            Double  HCTax       = vDataAmt.getvHCTax();
            Double  HCLineAmt   = vDataAmt.getvHCLineAmt();

            SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String datedNow = DateCurr.format(date);
            String datedShort = sdf2.format(date);
            String del="delete from cloud_cus_inv_dt where BlankLine='4' and ItemCode='"+OCCode+"' ";
            db.addQuery(del);

            String vSQLInsert = "INSERT INTO cloud_cus_inv_dt (Doc1No, N_o, ItemCode, " +
                    "Description, Qty, FactorQty, " +
                    "UOM, UOMSingular, HCUnitCost, " +
                    "DisRate1, HCDiscount, TaxRate1," +
                    "HCTax, DetailTaxCode, HCLineAmt," +
                    "BranchCode, DepartmentCode, ProjectCode, " +
                    "SalesPersonCode, LocationCode,WarrantyDate, " +
                    "BlankLine, DocType, AnalysisCode2, " +
                    "ComputerName, SORunNo, ItemGroup," +
                    "Printer, AlternateItem, DUD6," +
                    "MSP, ServiceChargeYN, LineNo)" +
                    "VALUES(" +
                    " '" + datedNow + "', 'a', '" + OCCode + "', " +
                    " '" + Description.replaceAll("'","") + "', '" + Qty + "', '1', " +
                    " '', '', '" + UnitPrice + "', " +
                    " '" + DisRate1 + "', '" + HCDiscount + "', '" + TaxRate1 + "'," +
                    " '" + HCTax + "', '" + DetailTaxCode + "','" + HCLineAmt + "'," +
                    " '"+BranchCode+"', 'android', 'android'," +
                    " 'android', '"+LocationCode+"', '" + datedShort + "'," +
                    " '4', 'CS', '"+AnalysisCode2+"', " +
                    " '" + UserCode + "', '0', ''," +
                    " '', '', ''," +
                    " '', '1', '"+LineNo+"')";
            z=HCLineAmt.toString();
            Log.d("QUERY MISC", vSQLInsert);
            db.addQuery(vSQLInsert);
            db.closeDB();
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
    }

    public static CalculateLineAmt fncalculate(Context c, Double Qty, Double HCUnitCost, Double DisRate1, Double HCDiscount, String DetailTaxCode) {
        Double vR_ate,vHCTax,vAmountB4Tax,vTempAmt,vHCLineAmt,vDisRate1,vHCDiscount;
        String vTaxType;
        vR_ate      =0.00;
        vHCTax      =0.00;
        vHCLineAmt  =0.00;
        vHCDiscount =HCDiscount;
        vDisRate1   =0.00;

        if(DisRate1>0){
            vDisRate1		=DisRate1/100;
            vHCDiscount	    =Qty*HCUnitCost*vDisRate1;
            vAmountB4Tax	=(Qty*HCUnitCost)-vHCDiscount;
        }else{
            if(HCDiscount>0){
                vDisRate1	    =(HCDiscount*100)/(Qty*HCUnitCost);
                vHCDiscount	    = HCDiscount;
                vAmountB4Tax	 =(Qty*HCUnitCost)-vHCDiscount;
                DisRate1	    =vDisRate1;
            }else{
                vHCDiscount	    =0.00;
                DisRate1	    =0.00;
                vAmountB4Tax	=(Qty*HCUnitCost)-vHCDiscount;
            }
        }
        Log.d("DetailTaxCode",DetailTaxCode);
        if(DetailTaxCode.length()==0) {
            vHCLineAmt   = vAmountB4Tax;
            vR_ate       = 0.00;
            vHCTax       = 0.00;
        }else{
            vHCLineAmt   = vAmountB4Tax;
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String strSQL   = "select IFNULL(R_ate,0) as R_ate,TaxType from stk_tax where TaxCode='" + DetailTaxCode + "' ";
            Cursor rsTax    = db.getQuery(strSQL);
            while (rsTax.moveToNext()) {
                vR_ate      = rsTax.getDouble(0);
                //vR_ate      = 0.00;
                vTaxType    = rsTax.getString(1);
                //  Log.d("RESULT", "taxtype: " + vTaxType);
                //calculate tax
                if (vTaxType.equals("0") || vTaxType.equals("2")) {
                    vHCTax      =vAmountB4Tax * (vR_ate / (vR_ate + 100));
                    vTempAmt    =vAmountB4Tax - vHCTax;
                    vHCLineAmt  =vTempAmt + vHCTax;
                } else if (vTaxType.equals("1") || vTaxType.equals("3")) {
                    vHCTax      = vAmountB4Tax * (vR_ate / 100);
                    vHCLineAmt  = vAmountB4Tax + vHCTax;
                }

            }

            db.closeDB();
        }
        return new CalculateLineAmt(DisRate1, vHCDiscount, vR_ate, vHCTax, vHCLineAmt);
    }
}
