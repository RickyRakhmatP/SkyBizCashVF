package skybiz.com.posoffline.m_Backup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class BackupDB extends AsyncTask<Void,Void,String> {
    Context c;
    String z,NameDir,D_ateTime,D_ate;

    public BackupDB(Context c) {
        this.c = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.fnbackup();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("success")){
            Toast.makeText(c,"Succesfull Backup", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Failure Backup", Toast.LENGTH_SHORT).show();
        }
    }

    private String fnbackup() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        D_ateTime = sdf.format(date);
        D_ate = sdf2.format(date);
        NameDir = "SkyBizCash_Backup/" + D_ate;
        backup_hd();
        backup_dt();
        backup_hdso();
        backup_dtso();
        backup_receipt2();
        backup_trn_out();
        backup_item();
        backup_customer();
        z="success";
        return z;
    }
    private void backup_hd(){
        try {
            File exportDir = new File(Environment.getExternalStorageDirectory(), NameDir);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file = new File(exportDir, "stk_cus_inv_hd_"+D_ateTime+".csv");
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            String query="select Doc1No, Doc2No, Doc3No," +
                    " D_ate,D_ateTime, CusCode, " +
                    " MemberNo, DueDate, TaxDate, " +
                    " CurCode, CurRate1,CurRate2," +
                    " CurRate3, TermCode, D_ay, " +
                    " Attention, AddCode, BatchCode," +
                    " GbDisRate1, GbDisRate2, GbDisRate3," +
                    " HCGbDiscount, GbTaxRate1, GbTaxRate2," +
                    " GbTaxRate3, HCGbTax, GlobalTaxCode, " +
                    " HCDtTax,HCNetAmt, AdjAmt, " +
                    " GbOCCode, GbOCRate, GbOCAmt, " +
                    " DocType, ApprovedYN, RetailYN," +
                    " UDRunNo, L_ink, Status," +
                    " Status2 from stk_cus_inv_hd";
            Cursor curCSV = db.getQuery(query);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext()) {
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),
                        curCSV.getString(3),curCSV.getString(4), curCSV.getString(5),
                        curCSV.getString(6),curCSV.getString(7), curCSV.getString(8),
                        curCSV.getString(9),curCSV.getString(10), curCSV.getString(11),
                        curCSV.getString(12),curCSV.getString(13), curCSV.getString(14),
                        curCSV.getString(15),curCSV.getString(16), curCSV.getString(17),
                        curCSV.getString(18),curCSV.getString(19), curCSV.getString(20),
                        curCSV.getString(21),curCSV.getString(22), curCSV.getString(23),
                        curCSV.getString(24),curCSV.getString(25), curCSV.getString(26),
                        curCSV.getString(27),curCSV.getString(28), curCSV.getString(29),
                        curCSV.getString(30),curCSV.getString(31), curCSV.getString(32),
                        curCSV.getString(33),curCSV.getString(34), curCSV.getString(35),
                        curCSV.getString(36),curCSV.getString(37), curCSV.getString(38),
                        curCSV.getString(39)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void backup_dt(){
        try {
            File exportDir = new File(Environment.getExternalStorageDirectory(), NameDir);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file = new File(exportDir, "stk_cus_inv_dt_"+D_ateTime+".csv");
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            String query="select Doc1No, N_o, ItemCode, " +
                    " Description, Qty, FactorQty, " +
                    " UOM, UOMSingular, HCUnitCost, " +
                    " DisRate1, HCDiscount, TaxRate1, " +
                    " HCTax, DetailTaxCode, HCLineAmt," +
                    " BranchCode, DepartmentCode, ProjectCode, " +
                    " SalesPersonCode, LocationCode, WarrantyDate, " +
                    " LineNo, BlankLine, DocType," +
                    " AnalysisCode2, DUD6 from stk_cus_inv_dt ";
            Cursor curCSV = db.getQuery(query);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext()) {
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),
                        curCSV.getString(3),curCSV.getString(4), curCSV.getString(5),
                        curCSV.getString(6),curCSV.getString(7), curCSV.getString(8),
                        curCSV.getString(9),curCSV.getString(10), curCSV.getString(11),
                        curCSV.getString(12),curCSV.getString(13), curCSV.getString(14),
                        curCSV.getString(15),curCSV.getString(16), curCSV.getString(17),
                        curCSV.getString(18),curCSV.getString(19), curCSV.getString(20),
                        curCSV.getString(21),curCSV.getString(22), curCSV.getString(23),
                        curCSV.getString(24),curCSV.getString(25)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void backup_hdso(){
        try {
            File exportDir = new File(Environment.getExternalStorageDirectory(), NameDir);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file = new File(exportDir, "stk_sales_order_hd"+D_ateTime+".csv");
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            String query="select Doc1No, Doc2No, Doc3No, " +
                    " D_ate, CusCode, DueDate, " +
                    " CurCode, CurRate1, CurRate2, " +
                    " CurRate3, TermCode, D_ay," +
                    " Attention, GbDisRate1, GbDisRate2, " +
                    " GbDisRate3, HCGbDiscount, GbTaxRate1, " +
                    " GbTaxRate2, GbTaxRate3, HCGbTax," +
                    " GlobalTaxCode, HCDtTax, HCNetAmt, " +
                    " GbOCCode, GbOCRate, GbOCAmt, " +
                    " DepositNo, Deposit, DepositAccountCode, " +
                    " DocType, ApprovedYN, UDRunNo, " +
                    " L_ink from stk_sales_order_hd ";
            Cursor curCSV = db.getQuery(query);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext()) {
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),
                        curCSV.getString(3),curCSV.getString(4), curCSV.getString(5),
                        curCSV.getString(6),curCSV.getString(7), curCSV.getString(8),
                        curCSV.getString(9),curCSV.getString(10), curCSV.getString(11),
                        curCSV.getString(12),curCSV.getString(13), curCSV.getString(14),
                        curCSV.getString(15),curCSV.getString(16), curCSV.getString(17),
                        curCSV.getString(18),curCSV.getString(19), curCSV.getString(20),
                        curCSV.getString(21),curCSV.getString(22), curCSV.getString(23),
                        curCSV.getString(24),curCSV.getString(25), curCSV.getString(26),
                        curCSV.getString(27),curCSV.getString(28), curCSV.getString(29),
                        curCSV.getString(30),curCSV.getString(31), curCSV.getString(32),
                        curCSV.getString(33)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void backup_dtso(){
        try {
            File exportDir = new File(Environment.getExternalStorageDirectory(), NameDir);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file = new File(exportDir, "stk_sales_order_dt"+D_ateTime+".csv");
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            String query="select Doc1No, N_o, ItemCode, " +
                    " Description, Qty, FactorQty, " +
                    " UOM, UOMSingular, HCUnitCost, " +
                    " DisRate1, HCDiscount, TaxRate1, " +
                    " HCTax, DetailTaxCode, HCLineAmt," +
                    " BranchCode, DepartmentCode, ProjectCode, " +
                    " SalesPersonCode, LocationCode, LineNo, " +
                    " BlankLine, DUD6 from stk_sales_order_dt ";
            Cursor curCSV = db.getQuery(query);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext()) {
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),
                        curCSV.getString(3),curCSV.getString(4), curCSV.getString(5),
                        curCSV.getString(6),curCSV.getString(7), curCSV.getString(8),
                        curCSV.getString(9),curCSV.getString(10), curCSV.getString(11),
                        curCSV.getString(12),curCSV.getString(13), curCSV.getString(14),
                        curCSV.getString(15),curCSV.getString(16), curCSV.getString(17),
                        curCSV.getString(18),curCSV.getString(19), curCSV.getString(20),
                        curCSV.getString(21),curCSV.getString(22)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void backup_receipt2(){
        try {
            File exportDir = new File(Environment.getExternalStorageDirectory(), NameDir);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file = new File(exportDir, "stk_receipt2"+D_ateTime+".csv");
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            String query="select D_ate, T_ime, D_ateTime, " +
                    " Doc1No, CashAmt, CC1Code, " +
                    " CC1Amt, CC1No, CC1Expiry, " +
                    " CC1ChargesAmt, CC1ChargesRate,CC2Code, " +
                    " CC2Amt, CC2No, CC2Expiry," +
                    " CC2ChargesAmt, CC2ChargesRate, Cheque1Code," +
                    " Cheque1Amt, Cheque1No, Cheque2Code," +
                    " Cheque2Amt, Cheque2No, PointAmt, " +
                    " VoucherAmt, CurCode, CurRate, " +
                    " FCAmt, CusCode, BalanceAmount, " +
                    " ChangeAmt, CounterCode, UserCode, " +
                    " DocType, VoidYN from stk_receipt2 ";
            Cursor curCSV = db.getQuery(query);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext()) {
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),
                        curCSV.getString(3),curCSV.getString(4), curCSV.getString(5),
                        curCSV.getString(6),curCSV.getString(7), curCSV.getString(8),
                        curCSV.getString(9),curCSV.getString(10), curCSV.getString(11),
                        curCSV.getString(12),curCSV.getString(13), curCSV.getString(14),
                        curCSV.getString(15),curCSV.getString(16), curCSV.getString(17),
                        curCSV.getString(18),curCSV.getString(19), curCSV.getString(20),
                        curCSV.getString(21),curCSV.getString(22), curCSV.getString(23),
                        curCSV.getString(24),curCSV.getString(25), curCSV.getString(26),
                        curCSV.getString(27),curCSV.getString(28), curCSV.getString(29),
                        curCSV.getString(30),curCSV.getString(31), curCSV.getString(32),
                        curCSV.getString(33),curCSV.getString(34)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void backup_trn_out(){
        try {
            File exportDir = new File(Environment.getExternalStorageDirectory(), NameDir);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file = new File(exportDir, "stk_detail_trn_out"+D_ateTime+".csv");
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            String query="select ItemCode, Doc3No, D_ate," +
                    " QtyOUT, FactorQty, UOM," +
                    " UnitPrice, CusCode, DocType3," +
                    " Doc3NoRunNo, LocationCode, L_ink, " +
                    " HCTax, BookDate from stk_detail_trn_out ";
            Cursor curCSV = db.getQuery(query);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext()) {
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),
                        curCSV.getString(3),curCSV.getString(4), curCSV.getString(5),
                        curCSV.getString(6),curCSV.getString(7), curCSV.getString(8),
                        curCSV.getString(9),curCSV.getString(10), curCSV.getString(11),
                        curCSV.getString(12),curCSV.getString(13)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void backup_item(){
        try {
            File exportDir = new File(Environment.getExternalStorageDirectory(), NameDir);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file = new File(exportDir, "stk_master"+D_ateTime+".csv");
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            String query="select ItemCode, Description, ItemGroup," +
                    " UnitPrice, UnitCost, DefaultUOM," +
                    " UOM, UOM1, UOM2, "+
                    " UOM3, UOM4, UOMPrice1, " +
                    " UOMPrice2, UOMPrice3, UOMPrice4, " +
                    " UOMFactor1, UOMFactor2, UOMFactor3, " +
                    " UOMFactor4, AnalysisCode1, AnalysisCode2," +
                    " AnalysisCode3, AnalysisCode4, AnalysisCode5, " +
                    " MSP, MSP1, MSP2," +
                    " MSP3, MSP4, BaseCode, " +
                    " UOMCode1, UOMCode2, UOMCode3, " +
                    " UOMCode4, MAXSP, MAXSP1, " +
                    " MAXSP2, MAXSP3, MAXSP4," +
                    " SalesTaxCode, RetailTaxCode, PurchaseTaxCode," +
                    " FixedPriceYN, AlternateItem from stk_master ";
            Cursor curCSV = db.getQuery(query);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext()) {
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),
                        curCSV.getString(3),curCSV.getString(4), curCSV.getString(5),
                        curCSV.getString(6),curCSV.getString(7), curCSV.getString(8),
                        curCSV.getString(9),curCSV.getString(10), curCSV.getString(11),
                        curCSV.getString(12),curCSV.getString(13), curCSV.getString(14),
                        curCSV.getString(15),curCSV.getString(16), curCSV.getString(17),
                        curCSV.getString(18),curCSV.getString(19), curCSV.getString(20),
                        curCSV.getString(21),curCSV.getString(22), curCSV.getString(23),
                        curCSV.getString(24),curCSV.getString(25), curCSV.getString(26),
                        curCSV.getString(27),curCSV.getString(28), curCSV.getString(29),
                        curCSV.getString(30),curCSV.getString(31), curCSV.getString(32),
                        curCSV.getString(33),curCSV.getString(34), curCSV.getString(35),
                        curCSV.getString(36),curCSV.getString(37), curCSV.getString(38),
                        curCSV.getString(39),curCSV.getString(40), curCSV.getString(41),
                        curCSV.getString(42),curCSV.getString(43)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void backup_customer(){
        try {
            File exportDir = new File(Environment.getExternalStorageDirectory(), NameDir);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file = new File(exportDir, "customer"+D_ateTime+".csv");
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            String query="select CusCode, CusName, FinCatCode, " +
                    " AccountCode, Address, CurCode, " +
                    " TermCode, D_ay, SalesPersonCode, " +
                    " Tel, Tel2, Fax, " +
                    " Fax2, Contact, ContactTel, " +
                    " Email, StatusBadYN, Town," +
                    " State, Country, PostCode, " +
                    " L_ink, NRICNo, DOB, " +
                    " Sex, MemberType, CardNo, " +
                    " PaymentCode, DateTimeModified, MembershipClass " +
                    " from customer ";
            Cursor curCSV = db.getQuery(query);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext()) {
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),
                        curCSV.getString(3),curCSV.getString(4), curCSV.getString(5),
                        curCSV.getString(6),curCSV.getString(7), curCSV.getString(8),
                        curCSV.getString(9),curCSV.getString(10), curCSV.getString(11),
                        curCSV.getString(12),curCSV.getString(13), curCSV.getString(14),
                        curCSV.getString(15),curCSV.getString(16), curCSV.getString(17),
                        curCSV.getString(18),curCSV.getString(19), curCSV.getString(20),
                        curCSV.getString(21),curCSV.getString(22), curCSV.getString(23),
                        curCSV.getString(24),curCSV.getString(25), curCSV.getString(26),
                        curCSV.getString(27),curCSV.getString(28), curCSV.getString(29)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
