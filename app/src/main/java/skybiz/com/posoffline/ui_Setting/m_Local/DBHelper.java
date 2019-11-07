package skybiz.com.posoffline.ui_Setting.m_Local;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by 7 on 30/10/2017.
 */


public class DBHelper extends SQLiteOpenHelper {
    private static final long BYTES_IN_A_MEGABYTE = 1048576;
    /**
     * Maximum size of the database in bytes
     */
    private final long mMaxSize;

    public DBHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
        mMaxSize = BYTES_IN_A_MEGABYTE * 8;
    }
    //TABLE CREATION
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.setMaximumSize(mMaxSize);
            db.execSQL(Constants.CREATE_TB);
            db.execSQL(Constants.CREATE_TB2);
            db.execSQL(Constants.CREATE_TB3);
            db.execSQL(Constants.CREATE_TBPAYTYPE);
            db.execSQL(Constants.CREATE_STK_MASTER);
            db.execSQL(Constants.CREATE_STK_TAX);
            db.execSQL(Constants.CREATE_CLOUD);
            db.execSQL(Constants.CREATE_CLOUD2);
            db.execSQL(Constants.CREATE_STK_CUS_INV_DT);
            db.execSQL(Constants.CREATE_STK_CUS_INV_HD);
            db.execSQL(Constants.CREATE_STK_RECEIPT2);
            db.execSQL(Constants.CREATE_STK_DETAIL_TRN_OUT);
            db.execSQL(Constants.CREATE_SYS_RUNNO_DT);
            db.execSQL(Constants.CREATE_SYS_GENERAL_SETUP3);
            db.execSQL(Constants.CREATE_COMPANYSETUP);
            db.execSQL(Constants.CREATE_STK_COUNTER_TRN);
            db.execSQL(Constants.CREATE_STK_SALES_ORDER_HD);
            db.execSQL(Constants.CREATE_STK_SALES_ORDER_DT);
            db.execSQL(Constants.CREATE_KITCHENPRINTER);
            db.execSQL(Constants.CREATE_CUSTOMER);
            db.execSQL(Constants.CREATE_TBMEMBER);
            db.execSQL(Constants.CREATE_STK_GROUP);
            db.execSQL(Constants.ALTER_RECEIPT2);
            db.execSQL(Constants.ALTER_CLOUD_CUS_INV_DT);
            db.execSQL(Constants.ALTER_STK_SALES_ORDER_DT);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //TABLE UPGRADE
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("UPGRADE DB","Old Version "+String.valueOf(oldVersion));
        if(newVersion>oldVersion){
           /* before ver 3--
            db.execSQL(Constants.CREATE_TBMEMBER);*/

            /* ver 3 --
            db.execSQL(Constants.ALTER_RECEIPT2);
            db.execSQL(Constants.ALTER_CLOUD_CUS_INV_DT);
            db.execSQL(Constants.ALTER_STK_SALES_ORDER_DT);
            db.execSQL(Constants.CREATE_STK_GROUP);*/
        }
        onCreate(db);
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);
        try{
            //sqlDB.execSQL(Query);
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);
            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });
            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {
                alc.set(0,c);
                c.moveToFirst();
                return alc ;
            }
            return alc;
        } catch(SQLiteException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){
            Log.d("printing exception", ex.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }

    public ArrayList<Cursor> getData2(String Query, String T_ype){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);
        try{
            //sqlDB.execSQL(Query);
            if(T_ype.equals("select")) {
                String maxQuery = Query;
                Cursor c = sqlDB.rawQuery(maxQuery, null);
                Cursor2.addRow(new Object[]{"Success"});
                alc.set(1, Cursor2);
                if (null != c && c.getCount() > 0) {
                    alc.set(0, c);
                    c.moveToFirst();
                    return alc;
                }
            }else{
                String maxQuery1 = Query ;
                Cursor c = sqlDB.rawQuery(maxQuery1, null);
                Cursor2.addRow(new Object[]{"Success"});
                alc.set(1, Cursor2);
                if (null != c && c.getCount() > 0) {
                    alc.set(0, c);
                    c.moveToFirst();
                    return alc;
                }
            }
            return alc;
        } catch(SQLiteException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){
            Log.d("printing exception", ex.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }

}
