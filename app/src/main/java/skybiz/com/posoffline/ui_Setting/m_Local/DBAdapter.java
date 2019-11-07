package skybiz.com.posoffline.ui_Setting.m_Local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * Created by 7 on 30/10/2017.
 */

public class DBAdapter {
    Context c;
    SQLiteDatabase db;
    DBHelper helper;

    public DBAdapter(Context c) {
        this.c = c;
        helper = new DBHelper(c);
    }

    //OPEN DATABASE
    public DBAdapter openDB() {
        try {
            db = helper.getWritableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return this;
    }

    //CLOSE DATABASE
    public void closeDB() {
        try {
            helper.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    //INSERT
    public long add(String ServerName, String UserName, String Password, String DBName, String Port, String ConnYN) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(Constants.ServerName, ServerName);
            cv.put(Constants.UserName, UserName);
            cv.put(Constants.Password, Password);
            cv.put(Constants.DBName, DBName);
            cv.put(Constants.Port, Port);
            cv.put(Constants.ConnYN, ConnYN);
            return db.insert(Constants.TB_NAME, Constants.RunNo, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long addGeneral(String CurCode, String GSTNo, String CompanyName, String RoundingCS, String LayawayAsSalesYN, String vPostGlobalTaxYN, String Doc1No) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(Constants.CurCode, CurCode);
            cv.put(Constants.GSTNo, GSTNo);
            cv.put(Constants.CompanyName, CompanyName);
            cv.put(Constants.RoundingCS, RoundingCS);
            cv.put(Constants.LayawayAsSalesYN, LayawayAsSalesYN);
            cv.put(Constants.vPostGlobalTaxYN, vPostGlobalTaxYN);
            cv.put(Constants.Doc1No, Doc1No);
            return db.insert(Constants.TB_NAME2, Constants.RunNo, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long addPrinterSet(String TypePrinter, String NamePrinter, String IPPrinter, String UUID) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(Constants.TypePrinter, TypePrinter);
            cv.put(Constants.NamePrinter, NamePrinter);
            cv.put(Constants.IPPrinter, IPPrinter);
            cv.put(Constants.UUID, UUID);
            return db.insert(Constants.TB_PRINTER, Constants.RunNo, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long addPayType(String PaymentCode, String PaymentType,String Charges1,
                           String PaidByCompanyYN, String MerchantCode, String MerchantKey) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(Constants.PaymentCode, PaymentCode);
            cv.put(Constants.PaymentType, PaymentType);
            cv.put(Constants.Charges1, Charges1);
            cv.put(Constants.PaidByCompanyYN, PaidByCompanyYN);
            cv.put("MerchantCode", MerchantCode);
            cv.put("MerchantKey", MerchantKey);
            return db.insert(Constants.TB_PAYTYPE, Constants.RunNo, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    //add stk_master
    public long add_stk_master(ContentValues cv) {
        try {
            return db.insert(Constants.TB_STK_MASTER, Constants.RunNo, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public long add_stk_tax(ContentValues cv) {
        try {
            return db.insert(Constants.TB_STK_TAX, Constants.RunNo, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long addCloud(ContentValues cv) {
        try {
            return db.insert(Constants.TB_CLOUD, Constants.RunNo, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long addStkCusInvHd(ContentValues cv) {
        try {
            return db.insert(Constants.TB_STK_CUS_INV_HD, Constants.RunNo, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public long addStkCusInvDt(ContentValues cv) {
        try {
            return db.insert(Constants.TB_STK_CUS_INV_DT, Constants.RunNo, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public long addDetailTrnOunt(ContentValues cv) {
        try {
            return db.insert(Constants.TB_STK_DETAIL_TRN_OUT, Constants.RunNo, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long addStkReceipt2(ContentValues cv) {
        try {
            return db.insert(Constants.TB_STK_RECEIPT2, Constants.RunNo, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long addSysRunNo(ContentValues cv) {
        try {
            return db.insert(Constants.TB_SYS_RUNNO_DT, Constants.RunNo, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public long addGen3(ContentValues cv) {
        try {
            return db.insert(Constants.TB_SYS_GENERAL_SETUP3, Constants.RunNo, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long addComSetup(ContentValues cv) {
        try {
            return db.insert(Constants.TB_COMPANYSETUP, Constants.RunNo, cv);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long addQuery(String query){
        try {
            db.execSQL(query);
            //return db.insert(Constants.TB_COMPANYSETUP, Constants.RunNo, cv);
            return 1;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //RETRIEVE
    public Cursor getGeneralSetup() {
        String[] columns = {Constants.RunNo, Constants.CurCode, Constants.GSTNo, Constants.CompanyName, Constants.RoundingCS, Constants.LayawayAsSalesYN,Constants.vPostGlobalTaxYN,Constants.Doc1No};
        return db.query(Constants.TB_NAME2, columns, null, null, null, null, null);
    }


    //RETRIEVE
    public Cursor getAllSeting() {
        String[] columns = {Constants.RunNo, Constants.ServerName, Constants.UserName, Constants.Password, Constants.DBName, Constants.Port, Constants.ConnYN};
        return db.query(Constants.TB_NAME, columns, null, null, null, null, null);
    }

    //RETRIEVE
    public Cursor getSettingPrint() {
        String[] columns = {Constants.RunNo, Constants.TypePrinter, Constants.NamePrinter, Constants.IPPrinter, Constants.UUID, Constants.Port, Constants.PaperSize};
        return db.query(Constants.TB_PRINTER, columns, null, null, null, null, null);
    }

    public Cursor getPayType() {
        return db.rawQuery("SELECT * FROM " + Constants.TB_PAYTYPE + " Order By RunNo Desc ", null);
    }

    public Cursor getAllItem() {
        return db.rawQuery("SELECT * FROM " + Constants.TB_STK_MASTER + " Order By ItemCode asc ", null);
    }

    public Cursor getAllItemGroup() {
        return db.rawQuery("SELECT Distinct ItemGroup FROM " + Constants.TB_STK_MASTER + " Order By ItemGroup asc ", null);
    }

    public Cursor getAllCloud() {
        return db.rawQuery("SELECT * FROM " + Constants.TB_CLOUD + " Order By RunNo desc ", null);
    }

    public Cursor getRowCloud(String RunNo){
        return db.rawQuery("SELECT * FROM " + Constants.TB_CLOUD + " Where RunNo='"+RunNo+"' ", null);
    }
    public Cursor getAllTax() {
        return db.rawQuery("SELECT * FROM " + Constants.TB_STK_TAX + " ", null);
    }

    public Cursor getStkCusInvHd() {
        return db.rawQuery("SELECT * FROM " + Constants.TB_STK_CUS_INV_HD + " Order By RunNo ", null);
    }

    public Cursor getStkCusInvDt() {
        return db.rawQuery("SELECT * FROM " + Constants.TB_STK_CUS_INV_DT + " Order By RunNo ", null);
    }

    public Cursor getStkReceipt2() {
        return db.rawQuery("SELECT * FROM " + Constants.TB_STK_RECEIPT2 + " Order By RunNo ", null);
    }

    public Cursor getDetailTrnOut() {
        return db.rawQuery("SELECT * FROM " + Constants.TB_STK_DETAIL_TRN_OUT + " Order By RunNo ", null);
    }

    public Cursor getQuery(String Query) {
        return db.rawQuery(Query, null);
    }

    public Cursor getRowPayType(String CC1Code) {
        return db.rawQuery("SELECT Charges1,PaidByCompanyYN,PaymentType FROM " + Constants.TB_PAYTYPE + " where PaymentCode='"+CC1Code+"'  Order By RunNo Desc ", null);
    }

    public long updateQuery(String query){
        try {
            db.execSQL(query);
            return 1;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }


    //UPDATE
    public long UpdateCloud(String RunNo, ContentValues cv)
    {
        try {
            return db.update(Constants.TB_CLOUD,cv,Constants.RunNo+" =?",new String[]{RunNo});
        }catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //UPDATE
    public long UPDATE_PRINT(int RunNo, String TypePrinter, String NamePrinter , String IPPrinter, String UUID)
    {
        try
        {
            ContentValues cv=new ContentValues();
            cv.put(Constants.TypePrinter, TypePrinter);
            cv.put(Constants.NamePrinter, NamePrinter);
            cv.put(Constants.IPPrinter, IPPrinter);
            cv.put(Constants.UUID, UUID);
            return db.update(Constants.TB_PRINTER,cv,Constants.RunNo+" =?",new String[]{String.valueOf(RunNo)});
        }catch (SQLiteException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    //UPDATE
    public long UPDATE(int RunNo, String ServerName, String UserName , String Password, String DBName, String Port, String ConnYN) {
        try {
            ContentValues cv=new ContentValues();
            cv.put(Constants.ServerName, ServerName);
            cv.put(Constants.UserName, UserName);
            cv.put(Constants.Password, Password);
            cv.put(Constants.DBName, DBName);
            cv.put(Constants.Port, Port);
            cv.put(Constants.ConnYN, ConnYN);
            return db.update(Constants.TB_NAME,cv,Constants.RunNo+" =?",new String[]{String.valueOf(RunNo)});
        }catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    //update cloud http://camposha.info/source/android-sqlite-recyclerview-source/
    public long UpdateCloud(ContentValues cv,String RunNo) {
        try {
            return db.update(Constants.TB_CLOUD,cv,Constants.RunNo+" =?",new String[]{RunNo});
        }catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long UpdateSysRunNo(ContentValues cv,String RunNo) {
        try {
            return db.update(Constants.TB_SYS_RUNNO_DT,cv,Constants.RunNo+" =?",new String[]{RunNo});
        }catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //DELETE
    public long DeleteSetting()
    {
        try
        {
            //return db.delete(Constants.TB_NAME,Constants.RunNo+" =?",new String[]{String.valueOf(RunNo)});
            return db.delete(Constants.TB_NAME, null,null);
        }catch (SQLiteException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    //DELETE
    public long DeleteGeneralSetup()
    {
        try
        {
            //return db.delete(Constants.TB_NAME,Constants.RunNo+" =?",new String[]{String.valueOf(RunNo)});
            return db.delete(Constants.TB_NAME2, null,null);
        }catch (SQLiteException e)
        {
            e.printStackTrace();
        }
        return 0;
    }
    public long DeleteSettingPrint() {
        try {
            return db.delete(Constants.TB_PRINTER, null,null);
        }catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public long DelAllPayType() {
        try {
            return db.delete(Constants.TB_PAYTYPE, null,null);
        }catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public long DelAllItem() {
        try {
            return db.delete(Constants.TB_STK_MASTER, null,null);
        }catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public long DelTax() {
        try {
            return db.delete(Constants.TB_STK_TAX, null,null);
        }catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long DelAllCloud() {
        try {
            return db.delete(Constants.TB_CLOUD, null,null);
        }catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long DelRowCloud(String RunNo) {
        try {
            return db.delete(Constants.TB_CLOUD,Constants.RunNo+" =?",new String[]{RunNo});
            //return db.delete(Constants.TB_NAME2, null,null);
        }catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long DelCompany() {
        try {
            //return db.delete(Constants.TB_COMPANYSETUP,Constants.RunNo+" =?",new String[]{RunNo});
            return db.delete(Constants.TB_COMPANYSETUP, null,null);
        }catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long DelGen3() {
        try {
            return db.delete(Constants.TB_SYS_GENERAL_SETUP3, null,null);
        }catch (SQLiteException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public boolean isColumnExists(String table, String column){
        //Cursor cursor=db.rawQuery("select "+column+" from "+table, null);
        Cursor cursor=db.rawQuery("PRAGMA table_info("+table+")", null);
        if(cursor != null){
            while(cursor.moveToNext()) {
                //String name = cursor.getColumnName(0);
                String name = cursor.getString(cursor.getColumnIndex("name"));
                if(column.equalsIgnoreCase(name)){
                    return true;
                }
            }
        }
        return false;
    }

}
