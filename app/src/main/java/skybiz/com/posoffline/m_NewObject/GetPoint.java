package skybiz.com.posoffline.m_NewObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class GetPoint {
    public static String Value(Context c,String ItemCode, String DocType){
        String point="0";
        try{
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String qPoint="select IFNULL(Point,0)as Point from ret_point where Item='"+ItemCode+"' and DocType='"+DocType+"' ";
            Cursor rsPoint=db.getQuery(qPoint);
            while(rsPoint.moveToNext()){
                point=rsPoint.getString(0);
            }
            db.closeDB();
            return point;
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return point;
    }
}
