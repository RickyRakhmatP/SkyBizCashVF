package skybiz.com.posoffline.m_NewObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.io.UnsupportedEncodingException;

import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class DecodeChar {
    public static String setChar(Context c, String Text){
        String newText="";
        try{
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String query="Select EncodeType from tb_setting";
            Cursor rsSet=db.getQuery(query);
            String EncodeType="";
            while(rsSet.moveToNext()){
                EncodeType=rsSet.getString(0);
            }
            byte[] b=null;
            if(EncodeType.equals("UTF-8")) {
                b=Text.getBytes("UTF-8");
            }else if(EncodeType.equals("GBK")){
                b=Text.getBytes("GBK");
            }
            newText=new String(b,"ISO-8859-1");
            db.closeDB();
            return newText;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return newText;
    }
}
