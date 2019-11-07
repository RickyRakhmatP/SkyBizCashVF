package skybiz.com.posoffline.m_ServiceSync.m_SyncIN;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_NewObject.Encode;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 21/12/2017.
 */

public class Sync_Item extends AsyncTask<Void,Void,String> {

    Context c;
    String IPAddress,UserName,Password,URL,
            DBName,z,Port,
            DBStatus,ItemConn,EncodeType,DateTimeSync;
    ProgressDialog pd;

    public Sync_Item(Context c){
        this.c = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(Void... params) {
        return this.downloadItem();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("error")){
            Toast.makeText(c,"Failure sync in data item", Toast.LENGTH_SHORT).show();
        }else if(result.equals("success")){
            Toast.makeText(c,"Succesfull sync in data item", Toast.LENGTH_SHORT).show();
        }else if(result.equals("zero")){
            //data to sync in not found
        }
    }

    private String downloadItem(){
        try{
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName,UserName,Password," +
                    " DBName,Port,DBStatus," +
                    " ItemConn,EncodeType, IFNULL(D_ateTimeSync,'') as D_ateTimeSync " +
                    " from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress   = curSet.getString(0);
                UserName    = curSet.getString(1);
                Password    = curSet.getString(2);
                DBName      = curSet.getString(3);
                Port        = curSet.getString(4);
                DBStatus    = curSet.getString(5);
                ItemConn    = curSet.getString(6);
                EncodeType  = curSet.getString(7);
                DateTimeSync= curSet.getString(8);
            }
            if(DBStatus.equals("2")) {
                z = "success";
            }else {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn == null) {
                    z = "error";
                } else {
                    String D_ate="";
                    String T_ime="";
                    if(!DateTimeSync.isEmpty()) {
                        String[] split = DateTimeSync.split(" ");
                        D_ate = split[0];
                        T_ime = split[1];
                    }
                    String qAuditLog="select Remark from sys_audit_log where " +
                            "D_ate>='"+D_ate+"' and T_ime>='"+T_ime+"' " +
                            "and EventType='Delete' and Sources='Item' ";
                    Statement stmtCA = conn.createStatement();
                    stmtCA.execute(qAuditLog);
                    ResultSet rsA=stmtCA.getResultSet();
                    while(rsA.next()){
                        String checkDel="select count(*)as numrows from stk_master where ItemCode='"+rsA.getString(1)+"' ";
                        Cursor rsDel=db.getQuery(checkDel);
                        while(rsDel.moveToNext()){
                            int numrows=rsDel.getInt(0);
                            if(numrows>0){
                                String delItem="delete from stk_master where ItemCode='"+rsA.getString(1)+"' ";
                                db.addQuery(delItem);
                            }
                        }
                    }
                    String qSet2="select SMobileYN from tb_othersetting";
                    Cursor rsSet2=db.getQuery(qSet2);
                    String SMobileYN="0";
                    while(rsSet2.moveToNext()) {
                        SMobileYN = rsSet2.getString(0);
                    }
                    String vClause="";
                    if(SMobileYN.equals("1")){
                        vClause=" AND SMobileYN='1' ";
                    }
                    //db.DelAllItem();
                    String sql = "select ItemCode,Description,ItemGroup," +
                            " FORMAT(UnitPrice,2) as UnitPrice,FORMAT(UnitCost,2) as UnitCost,DefaultUOM," +
                            " UOM,UOM1,UOM2," +
                            " UOM3,UOM4,UOMPrice1," +
                            " UOMPrice2,UOMPrice3,UOMPrice4, " +
                            " UOMFactor1,UOMFactor2,UOMFactor3," +
                            " UOMFactor4,AnalysisCode1,AnalysisCode2," +
                            " AnalysisCode3,AnalysisCode4,AnalysisCode5," +
                            " MSP, MSP1, MSP2, MSP3, MSP4," +
                            " BaseCode, UOMCode1, UOMCode2, UOMCode3, UOMCode4," +
                            " MAXSP, MAXSP1, MAXSP2," +
                            " MAXSP3, MAXSP4, SalesTaxCode," +
                            " RetailTaxCode, PurchaseTaxCode, FixedPriceYN," +
                            " AlternateItem, HCDiscount, DisRate1, '0' as Point," +
                            " DATE_FORMAT(DateTimeModified,'%Y-%m-%d %H:%i:%s') as DateTimeModified " +
                            " from stk_master where SuspendedYN='0' and  DateTimeModified>='"+DateTimeSync+"' " +
                            " "+vClause+" " +
                            " Order By ItemCode ";
                    //Log.d("QUERY",sql);
                    Statement statement = conn.createStatement();
                    statement.executeQuery("SET NAMES 'LATIN1'");
                    statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                    statement.execute(sql);
                    ResultSet resultSet = statement.getResultSet();
                    ContentValues cv = new ContentValues();
                    int i = 1;
                    float vcounter = 0;
                    while (resultSet.next()) {
                        cv.put("ItemCode", resultSet.getString("ItemCode"));
                        cv.put("Description", charReplace(Encode.setChar(EncodeType,resultSet.getString("Description"))));
                        cv.put("ItemGroup",  charReplace(Encode.setChar(EncodeType,resultSet.getString("ItemGroup"))));
                        cv.put("UnitPrice", resultSet.getString("UnitPrice"));
                        cv.put("UnitCost", resultSet.getString("UnitCost"));
                        cv.put("DefaultUOM", resultSet.getString("DefaultUOM"));
                        cv.put("UOM",  Encode.setChar(EncodeType,resultSet.getString("UOM")));
                        cv.put("UOM1", Encode.setChar(EncodeType,resultSet.getString("UOM1")));
                        cv.put("UOM2", Encode.setChar(EncodeType,resultSet.getString("UOM2")));
                        cv.put("UOM3", Encode.setChar(EncodeType,resultSet.getString("UOM3")));
                        cv.put("UOM4", Encode.setChar(EncodeType,resultSet.getString("UOM4")));
                        cv.put("UOMPrice1", resultSet.getString("UOMPrice1"));
                        cv.put("UOMPrice2", resultSet.getString("UOMPrice2"));
                        cv.put("UOMPrice3", resultSet.getString("UOMPrice3"));
                        cv.put("UOMPrice4", resultSet.getString("UOMPrice4"));
                        cv.put("UOMFactor1", resultSet.getString("UOMFactor1"));
                        cv.put("UOMFactor2", resultSet.getString("UOMFactor2"));
                        cv.put("UOMFactor3", resultSet.getString("UOMFactor3"));
                        cv.put("UOMFactor4", resultSet.getString("UOMFactor4"));
                        cv.put("AnalysisCode1", resultSet.getString("AnalysisCode1"));
                        cv.put("AnalysisCode2", resultSet.getString("AnalysisCode2"));
                        cv.put("AnalysisCode3", resultSet.getString("AnalysisCode3"));
                        cv.put("AnalysisCode4", resultSet.getString("AnalysisCode4"));
                        cv.put("AnalysisCode5", resultSet.getString("AnalysisCode5"));
                        cv.put("MSP", resultSet.getString("MSP"));
                        cv.put("MSP1", resultSet.getString("MSP1"));
                        cv.put("MSP2", resultSet.getString("MSP2"));
                        cv.put("MSP3", resultSet.getString("MSP3"));
                        cv.put("MSP4", resultSet.getString("MSP4"));
                        cv.put("BaseCode", resultSet.getString("BaseCode"));
                        cv.put("UOMCode1", resultSet.getString("UOMCode1"));
                        cv.put("UOMCode2", resultSet.getString("UOMCode2"));
                        cv.put("UOMCode3", resultSet.getString("UOMCode3"));
                        cv.put("UOMCode4", resultSet.getString("UOMCode4"));
                        cv.put("MAXSP", resultSet.getString("MAXSP"));
                        cv.put("MAXSP1", resultSet.getString("MAXSP1"));
                        cv.put("MAXSP2", resultSet.getString("MAXSP2"));
                        cv.put("MAXSP3", resultSet.getString("MAXSP3"));
                        cv.put("MAXSP4", resultSet.getString("MAXSP4"));
                        cv.put("SalesTaxCode", resultSet.getString("SalesTaxCode"));
                        cv.put("RetailTaxCode", resultSet.getString("RetailTaxCode"));
                        cv.put("PurchaseTaxCode", resultSet.getString("PurchaseTaxCode"));
                        cv.put("FixedPriceYN", resultSet.getString("FixedPriceYN"));
                        cv.put("AlternateItem", Encode.setChar(EncodeType,resultSet.getString("AlternateItem")));
                        cv.put("HCDiscount",resultSet.getString("HCDiscount"));
                        cv.put("DisRate1",resultSet.getString("DisRate1"));
                        cv.put("Point",resultSet.getString("Point"));
                        cv.put("DateTimeModified",resultSet.getString("DateTimeModified"));
                        long addItem = db.add_stk_master(cv);
                        if (addItem > 0) {
                            //Log.d("SUCCESS", charReplace(Encode.setChar(EncodeType,resultSet.getString("ItemGroup"))));

                        } else {
                           // Log.d("ERROR", resultSet.getString("ItemCode"));
                        }
                        i++;
                    }
                   // db.closeDB();
                    String delPoint="delete from ret_point";
                    db.addQuery(delPoint);
                    String qPoint="select Item, Point, B_ase, L_ink, DocType from ret_point";
                    Statement stmtP = conn.createStatement();
                    stmtP.execute(qPoint);
                    ResultSet rsPoint = stmtP.getResultSet();
                    while (rsPoint.next()) {
                        String insertP="insert into ret_point(Item, Point, B_ase, " +
                                "L_ink, DocType)values('"+rsPoint.getString(1)+"', '"+rsPoint.getString(2)+"', '"+rsPoint.getString(3)+"'," +
                                "'"+rsPoint.getString(4)+"', '"+rsPoint.getString(5)+"')";
                        //Log.d("IN POINT",insertP);
                        db.addQuery(insertP);
                    }
                    statement.close();
                    stmtP.close();


                    String delPhoto="delete from stk_master_photo";
                    db.addQuery(delPhoto);
                    String qPhoto="select ItemCode, IFNULL(PhotoFile,'')as PhotoFile from stk_master_photo";
                    Statement stmtPh = conn.createStatement();
                    stmtPh.execute(qPhoto);
                    ResultSet rsPhoto = stmtPh.getResultSet();
                    while (rsPhoto.next()) {
                        String PhotoFile=rsPhoto.getString(2);
                        if(!PhotoFile.isEmpty()){
                            Blob test=rsPhoto.getBlob(2);
                            int blobl=(int)test.length();
                            byte[] blobasbyte=test.getBytes(1,blobl);
                            Bitmap bmp=BitmapFactory.decodeByteArray(blobasbyte,0,blobasbyte.length);
                            PhotoFile=encodeBmp(bmp);
                        }
                        String insertPh="insert into stk_master_photo(ItemCode, PhotoFile)values(" +
                                "'"+rsPhoto.getString(1)+"', '"+PhotoFile+"')";
                        //Log.d("IN Photo",insertPh);
                        db.addQuery(insertPh);
                    }
                    statement.close();
                    stmtP.close();

                    z = "success";
                }
            }
            return z;
        }catch (SQLException e){
            e.printStackTrace();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return z;
    }
    private String decodeChar(String txt){
        String newText="";
        try{
            byte[] b=txt.getBytes("UTF-8");
            newText=new String(b,"ISO-8859-1");
            return newText;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return newText;
    }
    private String encodeChar(String txt){
        String newText="";
        try{
            byte[] b=txt.getBytes("ISO-8859-1");
            if(EncodeType.equals("UTF-8")) {
                newText = new String(b, "utf-8");
            }else if(EncodeType.equals("GBK")){
                newText = new String(b, "GBK");
            }
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return newText;
    }
    private String charReplace(String text){
        String newText=text.replaceAll("[\\.$|,|;|']","");
        return newText;
    }

    private String encodeBmp(Bitmap bmp){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,70,baos);
        byte[] b=baos.toByteArray();
        String base64= Base64.encodeToString(b,Base64.DEFAULT);
        return base64;
    }
}
