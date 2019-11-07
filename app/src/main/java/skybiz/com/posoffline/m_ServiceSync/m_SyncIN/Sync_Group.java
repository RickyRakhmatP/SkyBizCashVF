package skybiz.com.posoffline.m_ServiceSync.m_SyncIN;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
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

public class Sync_Group extends AsyncTask<Void,Void,String> {

    Context c;

    String IPAddress,UserName,Password,URL,DBName,z,Port,DBStatus,ItemConn,EncodeType;
    public Sync_Group(Context c) {
        this.c = c;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(Void... params) {
        return this.retGroup();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("error")){
            Toast.makeText(c,"Failure sync in data group", Toast.LENGTH_SHORT).show();
        }else if(result.equals("success")){
            Toast.makeText(c,"Successfull sync in data group ", Toast.LENGTH_SHORT).show();
        }else if(result.equals("zero")){
            //data to sync in not found
        }
    }
    private String retGroup(){
        try{
            z="zero";
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName,UserName,Password," +
                    "DBName,Port,DBStatus," +
                    "ItemConn,EncodeType" +
                    " from tb_setting";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress = curSet.getString(0);
                UserName = curSet.getString(1);
                Password = curSet.getString(2);
                DBName = curSet.getString(3);
                Port = curSet.getString(4);
                DBStatus=curSet.getString(5);
                ItemConn=curSet.getString(6);
                EncodeType=curSet.getString(7);
            }
            if(DBStatus.equals("2")) {
                z = "success";
            }else {
                //URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=UTF-8";
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn == null) {
                    z = "error";
                } else {
                    String qSet2="select SMobileYN from tb_othersetting";
                    Cursor rsSet2=db.getQuery(qSet2);
                    String SMobileYN="0";
                    while(rsSet2.moveToNext()) {
                        SMobileYN = rsSet2.getString(0);
                    }
                    String vClause="";
                    if(SMobileYN.equals("0")){
                        vClause=" WHERE SMobileYN='1' ";
                    }

                    String del = "delete from stk_group";
                    db.addQuery(del);
                    String sql = "select ItemGroup, Description, '' as Description2," +
                            " Modifier1, Modifier2, L_ink, " +
                            " DATE_FORMAT(DateTimeModified,'%Y-%m-%d %H:%i:%s') as DateTimeModified, IFNULL(Printer,'') as Printer " +
                            " from stk_group "+vClause+" ";
                    Statement statement = conn.createStatement();
                    statement.executeQuery("SET NAMES 'LATIN1'");
                    statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                    statement.execute(sql);
                    ResultSet rsCus = statement.getResultSet();
                    int i = 1;
                    float vcounter = 0;
                    while (rsCus.next()) {
                        String QueryIn = "Insert into stk_group(ItemGroup, Description, Description2, " +
                                " Modifier1, Modifier2, L_ink, " +
                                " DateTimeModified, Printer)values(" +
                                " '" + charReplace(Encode.setChar(EncodeType,rsCus.getString("ItemGroup"))) + "', '" +  charReplace(Encode.setChar(EncodeType,rsCus.getString("Description"))) + "', '" + rsCus.getString("Description2") + "', " +
                                " '" + Encode.setChar(EncodeType,rsCus.getString("Modifier1")) + "', '" + rsCus.getString("Modifier2") + "', '" + rsCus.getString("L_ink") + "', " +
                                " '" + rsCus.getString("DateTimeModified") + "', '"+rsCus.getString("Printer")+"' )";
                       // Log.d("INSERT GROUP", QueryIn);
                        long addItem = db.addQuery(QueryIn);
                        if (addItem > 0) {
                            //publishProgress(i);
                        } else {
                            //Log.d("ERROR", rsCus.getString("ItemGroup"));
                        }
                        i++;
                    }

                    String delBranch = "delete from stk_branch";
                    db.addQuery(delBranch);
                    String sqlB = "select BranchCode, Description, Address," +
                            " AreaCode , Tel, Fax, " +
                            " L_ink, CusCode, UserDefine1," +
                            " UserDefine2, UserDefine3 " +
                            " from stk_branch ";
                    Statement stmtB = conn.createStatement();
                    stmtB.executeQuery("SET NAMES 'LATIN1'");
                    stmtB.executeQuery("SET CHARACTER SET 'LATIN1'");
                    stmtB.execute(sqlB);
                    ResultSet rsBranch = stmtB.getResultSet();
                    while (rsBranch.next()) {
                        String QueryIn = "Insert into stk_branch(BranchCode, Description, Address," +
                                " AreaCode , Tel, Fax, " +
                                " L_ink, CusCode, UserDefine1," +
                                " UserDefine2, UserDefine3)values(" +
                                " '" + charReplace(Encode.setChar(EncodeType,rsBranch.getString(1))) + "', '" +  charReplace(Encode.setChar(EncodeType,rsBranch.getString(2))) + "', '" + rsBranch.getString(3) + "', " +
                                " '" + charReplace(Encode.setChar(EncodeType,rsBranch.getString(4))) + "', '" + rsBranch.getString(5) + "', '" + rsBranch.getString(6) + "', " +
                                " '" + rsBranch.getString(7) + "', '"+rsBranch.getString(8)+"', '"+rsBranch.getString(9)+"'," +
                                " '"+rsBranch.getString(10)+"', '"+rsBranch.getString(11)+"' )";
                        //Log.d("INSERT BRANCH", QueryIn);
                        db.addQuery(QueryIn);
                    }
                    stmtB.close();


                    String delUOM = "delete from stk_uom";
                    db.addQuery(delUOM);
                    String sqlU = "select UOM, UOMPlural from stk_uom ";
                    Statement stmtU = conn.createStatement();
                    stmtU.executeQuery("SET NAMES 'LATIN1'");
                    stmtU.executeQuery("SET CHARACTER SET 'LATIN1'");
                    stmtU.execute(sqlU);
                    ResultSet rsUOM = stmtU.getResultSet();
                    while (rsUOM.next()) {
                        String QueryIn = "Insert into stk_uom(UOM, UOMPlural)values('"+Encode.setChar(EncodeType,rsUOM.getString(1))+"', '"+Encode.setChar(EncodeType,rsUOM.getString(2))+"' )";
                       // Log.d("INSERT UOM", QueryIn);
                        db.addQuery(QueryIn);
                    }
                    stmtU.close();
                    db.closeDB();
                    z = "success";
                }
            }
            return z;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return z;
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
            return newText;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return newText;
    }
    private String charReplace(String text){
        String newText=text.replaceAll("[\\.$|,|;|']","");
        return newText;
    }

}
