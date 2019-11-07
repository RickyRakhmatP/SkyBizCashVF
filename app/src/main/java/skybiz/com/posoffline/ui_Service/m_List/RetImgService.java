package skybiz.com.posoffline.ui_Service.m_List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Service.MService;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class RetImgService extends AsyncTask<Void,Void,String> {
    Context c;
    String Doc1No;
    String IPAddress,UserName,Password,
            Port,DBName,URL,
            z,DBStatus,EncodeType,
            BranchCode,LocationCode;
    Bitmap bmp;

    public RetImgService(Context c, String doc1No) {
        this.c = c;
        Doc1No = doc1No;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.fnretimage();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("success")){
            ((MService)c).setImgService(bmp);
        }else{
            Toast.makeText(c,"failure downloading image", Toast.LENGTH_SHORT).show();
        }
    }
    private String fnretimage(){
        try{
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName, UserName, Password, " +
                    "DBName, Port, DBStatus, " +
                    "EncodeType, BranchCode, LocationCode " +
                    "from tb_setting ";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress       = curSet.getString(0);
                UserName        = curSet.getString(1);
                Password        = curSet.getString(2);
                DBName          = curSet.getString(3);
                Port            = curSet.getString(4);
                DBStatus        = curSet.getString(5);
                EncodeType      = curSet.getString(6);
                BranchCode      = curSet.getString(7);
                LocationCode    = curSet.getString(8);
            }
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    String sql ="Select PhotoFile2 from stk_service_hd where Doc1No='"+Doc1No+"' ";
                    Statement statement = conn.createStatement();
                    statement.execute(sql);
                    ResultSet rsData = statement.getResultSet();
                    while (rsData.next()) {
                        //byte[] byteArray = rsData.get(columnIndex);
                       //Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
                        Blob test=rsData.getBlob(1);
                        int blobLength = (int) test.length();
                        byte[] blobAsBytes = test.getBytes(1, blobLength);
                        bmp = BitmapFactory.decodeByteArray(blobAsBytes, 0 ,blobAsBytes.length);
                    }
                }
                z="success";
            }else{
                z="success";
            }
            db.closeDB();
            return z;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
       return z;
    }
}
