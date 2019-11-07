package skybiz.com.posoffline.m_NewObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.io.UnsupportedEncodingException;

import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class Decode {
    public static String setChar(String EncodeType, String Text){
        String newText="";
        try{
            byte[] b=null;
            if(EncodeType.equals("UTF-8")) {
                b=Text.getBytes("UTF-8");
            }else if(EncodeType.equals("GBK")){
                b=Text.getBytes("GBK");
            }
            newText=new String(b,"ISO-8859-1");
            return newText;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return newText;
    }
}
