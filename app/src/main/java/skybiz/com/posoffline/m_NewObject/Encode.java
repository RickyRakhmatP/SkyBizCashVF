package skybiz.com.posoffline.m_NewObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.io.UnsupportedEncodingException;

import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class Encode {
    public static String setChar(String EncodeType, String Text){
        String newText="";
        try{
            byte[] b=Text.getBytes("ISO-8859-1");
            if(EncodeType.equals("UTF-8")) {
                newText = new String(b, "utf-8");
            }else if(EncodeType.equals("GBK")){
                newText = new String(b, "GBK");
            }
            return newText;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return newText;
    }
}
