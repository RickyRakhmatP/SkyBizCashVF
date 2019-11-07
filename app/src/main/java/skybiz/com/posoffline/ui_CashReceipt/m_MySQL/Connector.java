package skybiz.com.posoffline.ui_CashReceipt.m_MySQL;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by 7 on 27/10/2017.
 */

public class Connector {
    public static Connection connect(String URL, String User , String Password){
        Connection conn = null;

        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(URL, User, Password );
        }catch (SQLException se) {
            Log.d("ERROR", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.d("ERROR", e.getMessage());
        } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        }
        return conn;
    }
}
