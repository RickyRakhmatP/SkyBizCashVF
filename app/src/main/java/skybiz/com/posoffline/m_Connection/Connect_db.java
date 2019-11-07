package skybiz.com.posoffline.m_Connection;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import skybiz.com.posoffline.GlobalApplication;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class Connect_db {
    static Connection conn=null;
    static Context c = GlobalApplication.getAppContext();
    public static Connection getConnection() {
        try {
           // Log.d("Connection","NEW 1");
            if (conn != null) return conn;
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String IPAddress = "", UserName = "", Password = "", DBName = "", Port = "";
            String querySet = "select ServerName ,UserName, Password," +
                    "DBName, Port, DBStatus," +
                    "ItemConn, EncodeType" +
                    " from tb_setting";
            Cursor cur1 = db.getQuery(querySet);
            while (cur1.moveToNext()) {
                IPAddress   = cur1.getString(0);
                UserName    = cur1.getString(1);
                Password    = cur1.getString(2);
                DBName      = cur1.getString(3);
                Port        = cur1.getString(4);
            }
            String URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1&rewriteBatchedStatements=true";
            //Log.d("Connection","NEW 2");
            return getConnection(URL, UserName, Password);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return conn;
    }

    private static Connection getConnection(String URL,String User,String Password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(URL, User, Password );
            //conn= DriverManager.getConnection("jdbc:mysql://localhost/"+db_name+"?user="+user_name+"&password="+password);
            //return conn;
        }catch (SQLException se) {
            Log.d("ERROR SQL", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.d("ERROR CLASS", e.getMessage());
        } catch (Exception e) {
            Log.d("ERROR EXCPETION", e.getMessage());
        }
        return conn;
    }
}
