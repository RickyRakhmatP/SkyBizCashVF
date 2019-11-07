package skybiz.com.posoffline.m_NewObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class Rounding {
    public static Double setRound(Context c, Double vValue){
        Double newValue=0.00;
        try{
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String query="SELECT T_ext FROM sys_general_setup4 where C_ode='RoundingCS' and ProgramName='Dis' ";
            Cursor rsSet=db.getQuery(query);
            String T_ext="";
            while(rsSet.moveToNext()){
                T_ext=rsSet.getString(0);
            }
            Log.d("TEXT",T_ext);
            if(T_ext.equals("Nearest 5 sen")){
                //Double scale = Math.pow(5, 10);
               // newValue=Math.round(vValue * scale) / scale;
                newValue=Double.valueOf(Math.round(vValue * 100.0 / 5.0) * 5.0 / 100.0);
                // newValue=Double.valueOf(Math.round(vValue * 20)/20);
            }else if(T_ext.equals("Nearest 10 sen")){
                newValue= ((double) (long) (vValue * 20 + 1.0)) / 20;
               // newValue= Double.valueOf(Math.max(Math.round(vValue * 10) / 10, 2.8));
            }else if(T_ext.equals("Rounding down nearest 10 sen")){
                newValue= ((double) (long) (vValue * 20 - 1.0)) / 20;
                //newValue=Double.valueOf((Math.floor(vValue *10) / 10));
            }else if(T_ext.equals("Rounding down nearest 5 sen")){
                newValue= ((double) (long) (vValue * 20 - 0.5)) / 20;
                 //newValue=Double.valueOf(Math.round(vValue * 20)/20);
            }else {
                newValue=vValue;
            }
            db.closeDB();
            return newValue;
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return newValue;
    }
}
