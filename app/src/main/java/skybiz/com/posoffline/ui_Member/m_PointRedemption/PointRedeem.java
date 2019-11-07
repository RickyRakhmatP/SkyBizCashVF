package skybiz.com.posoffline.ui_Member.m_PointRedemption;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_NewCustomer.DialogCustomer;
import skybiz.com.posoffline.m_NewObject.Decode;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Member.m_ItemList.DialogItem;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;


public class PointRedeem extends AppCompatActivity {

    TextView txtDateBF,txtPointBF,txtDateCF,txtPointCF,
    txtRemark,txtD_ateTime,txtPoint,txtDescription;
    Button btnReset,btnSave;
    String ItemCode,Description,UnitPrice,
            UOM,FactorQty,PointRedeem,
            CusCode;
    LinearLayout lnButton,lnCF,lnRedeem,lnParticulars;
    Double dPointBF=0.00,dPointRedeem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_redeem);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Point Redemption");
        txtDateBF=(TextView)findViewById(R.id.txtDateBF);
        txtPointBF=(TextView)findViewById(R.id.txtPointBF);
        txtDateCF=(TextView)findViewById(R.id.txtDateCF);
        txtPointCF=(TextView)findViewById(R.id.txtPointCF);
        txtRemark=(TextView)findViewById(R.id.txtRemark);
        txtD_ateTime=(TextView)findViewById(R.id.txtD_ateTime);
        txtDescription=(TextView)findViewById(R.id.txtDescription);
        txtPoint=(TextView)findViewById(R.id.txtPoint);
        btnReset=(Button)findViewById(R.id.btnReset);
        btnSave=(Button)findViewById(R.id.btnSave);
        lnButton=(LinearLayout)findViewById(R.id.lnButton);
        lnRedeem=(LinearLayout)findViewById(R.id.lnRedeem);
        lnCF=(LinearLayout)findViewById(R.id.lnCF);
        lnParticulars=(LinearLayout)findViewById(R.id.lnParticulars);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnreset();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnsave();
            }
        });
       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_redeem, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        /*if (id == R.id.mnCustomer) {
            Bundle b=new Bundle();
            b.putString("DOCTYPE_KEY","PR");
            DialogCustomer dialogCustomer = new DialogCustomer();
            dialogCustomer.setArguments(b);
            dialogCustomer.show(getSupportFragmentManager(), "mListCustomer");
            return true;*/
         if(id==R.id.mnItem){
            DialogItem dialogItem = new DialogItem();
            dialogItem.show(getSupportFragmentManager(), "mListItem");
        }else if(id==R.id.mnMemberCode){
            DialogMemberCode dialogMemberCode = new DialogMemberCode();
            dialogMemberCode.show(getSupportFragmentManager(), "mMemberCode");
        }
        return super.onOptionsItemSelected(item);
    }

    private void fnsave(){
        String PointCF=minRight(txtPointCF.getText().toString().replaceAll(",",""));
        Double dPointCF=Double.parseDouble(PointCF);
        if(dPointCF<0) {
            Toast.makeText(this,"C/F Point less than 0", Toast.LENGTH_SHORT).show();
        }else {
            SaveRedeem saveRedeem = new SaveRedeem(this, CusCode, ItemCode, Description, UnitPrice, UOM, FactorQty, PointRedeem);
            saveRedeem.execute();
        }
    }
    private  void fnreset(){
        hideLn();
        txtPointBF.setText("0P");
        getSupportActionBar().setTitle("Point Redemption");
    }
    public void retBF(String vCusCode, String CusName){
        CusCode=vCusCode;
        getSupportActionBar().setTitle(CusName);
        RetBF retBF=new RetBF(this,vCusCode);
        retBF.execute();
        hideLn();
    }
    public void setItemRedeem(String vItemCode,String vDescription,String vUnitPrice,
                              String vUOM, String vFactorQty,String vPoint){
        ItemCode=vItemCode;
        Description=vDescription;
        UnitPrice=vUnitPrice;
        UOM=vUOM;
        FactorQty=vFactorQty;
        PointRedeem=vPoint;
        txtPoint.setText(vPoint+"P");
        txtRemark.setText("Item Redemption");
        txtD_ateTime.setText(datedNow());
        txtDescription.setText(vDescription+" x 1"+vUOM);
        retCF(vPoint);
        showLn();
    }
    private void retCF(String PointRedeem){
        Double dPointRedeem=Double.parseDouble(PointRedeem);
        Double dPointCF=dPointBF-dPointRedeem;
        String PointCF=zeroDecimal(dPointCF);
        txtDateCF.setText(datedNow());
        txtPointCF.setText(PointCF +"P");

    }
    private void showLn(){
        lnCF.setVisibility(View.VISIBLE);
        lnRedeem.setVisibility(View.VISIBLE);
        lnButton.setVisibility(View.VISIBLE);
        lnParticulars.setVisibility(View.VISIBLE);
    }
    private void hideLn(){
        lnCF.setVisibility(View.GONE);
        lnRedeem.setVisibility(View.GONE);
        lnButton.setVisibility(View.GONE);
        lnParticulars.setVisibility(View.GONE);
    }
    public class RetBF extends AsyncTask<Void,Void,String>{
        Context c;
        String CusCode;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn,EncodeType;
        String PointBF;
        public RetBF(Context c, String cusCode) {
            this.c = c;
            CusCode = cusCode;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.calcBF();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result==null){
                Toast.makeText(c,"Failure, calculate Point B/F", Toast.LENGTH_SHORT).show();
            }else{
                txtDateBF.setText(datedNow());
                txtPointBF.setText(PointBF+"P");
                dPointBF=Double.parseDouble(PointBF.replace(",",""));
            }
        }
        private String calcBF(){
            try{
                DBAdapter db = new DBAdapter(c);
                db.openDB();
               /* Cursor cur = db.getGeneralSetup();
                while (cur.moveToNext()) {
                    CurCode = cur.getString(1);
                }*/
                String querySet="select ServerName,UserName,Password," +
                        "DBName,Port,DBStatus, " +
                        "ItemConn,EncodeType " +
                        "from tb_setting";
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
                Double dTotalPoint=0.00;
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        String sql ="Select DATE_FORMAT(P.D_ateTime,'%Y-%m-%d %H:%i:%s') as D_ateTime,IFNULL(P.Point,0) as Point,P.DocType," +
                                " P.Remark,C.CusCode,C.CusName "+
                                " from ret_pointadjustment P inner join customer C " +
                                " on P.cuscode=C.CusCode where C.CusCode='"+CusCode+"' order by P.RunNo desc ";
                        Statement statement = conn.createStatement();
                        if (statement.execute(sql)) {
                            ResultSet rsData = statement.getResultSet();
                            while (rsData.next()) {
                                String DocType=rsData.getString(3);
                                if(DocType.equals("Increase")) {
                                    dTotalPoint += rsData.getDouble(2);
                                }else{
                                    dTotalPoint -= rsData.getDouble(2);
                                }
                            }
                            PointBF=zeroDecimal(dTotalPoint);
                            z="success";
                        }
                    }
                }else if(DBStatus.equals("0")){
                    String sql ="Select P.D_ateTime,IFNULL(P.Point,0) as Point,P.DocType," +
                            " P.Remark, C.CusCode, C.CusName "+
                            " from ret_pointadjustment P inner join customer C " +
                            " on P.cuscode=C.CusCode where C.CusCode='"+CusCode+"' order by P.RunNo desc ";
                    Cursor rsData=db.getQuery(sql);
                    while(rsData.moveToNext()){
                        String DocType=rsData.getString(2);
                        if(DocType.equals("Increase")) {
                            dTotalPoint += rsData.getDouble(1);
                        }else{
                            dTotalPoint -= rsData.getDouble(1);
                        }
                    }
                    PointBF=zeroDecimal(dTotalPoint);
                    z="success";
                }
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return z;
        }
    }

    public class SaveRedeem extends AsyncTask<Void,Void,String>{
        Context c;
        String CusCode,ItemCode,Description,UnitPrice,UOM,FactorQty,Point;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn,EncodeType,CurCode;

        public SaveRedeem(Context c, String cusCode, String itemCode,
                          String description, String unitPrice, String UOM,
                          String factorQty, String point) {
            this.c = c;
            CusCode = cusCode;
            ItemCode = itemCode;
            Description = description;
            UnitPrice = unitPrice;
            this.UOM = UOM;
            FactorQty = factorQty;
            Point = point;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.calcBF();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result==null){
                Toast.makeText(c,"Failure, Save Redeem", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(c,"Successfull, Save Redeem", Toast.LENGTH_SHORT).show();
                fnreset();
            }
        }
        private String calcBF() {
            try {
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                Cursor cur = db.getGeneralSetup();
                while (cur.moveToNext()) {
                    CurCode = cur.getString(1);
                }
                String querySet = "select ServerName,UserName,Password," +
                        "DBName,Port,DBStatus, " +
                        "ItemConn,EncodeType " +
                        "from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(0);
                    UserName = curSet.getString(1);
                    Password = curSet.getString(2);
                    DBName = curSet.getString(3);
                    Port = curSet.getString(4);
                    DBStatus = curSet.getString(5);
                    ItemConn = curSet.getString(6);
                    EncodeType = curSet.getString(7);
                }
                String D_ateTime=datedNow();
                String D_ate=datedShort();
                String Doc1No=datedTime();
                if (DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        String insertHd="Insert into ret_pointredeem_hd(Doc1No,D_ate," +
                                "Remark,ClientCode,L_ink)values('"+Doc1No+"', '"+D_ateTime+"'," +
                                "'', '"+CusCode+"', '1')";
                        Statement statement = conn.createStatement();
                        statement.execute(insertHd);

                        String insertDt="Insert into ret_pointredeem_dt(Doc1No,ItemCode,Point," +
                                "UnitCost,Qty,FactorQty," +
                                "UOM,Description)values('"+Doc1No+"', '"+ItemCode+"', '"+Point+"'," +
                                "'"+UnitPrice+"', '1', '1'," +
                                "'"+UOM+"', '"+Decode.setChar(EncodeType,Description)+"')";
                        Statement stmtDt = conn.createStatement();
                        stmtDt.execute(insertDt);

                        String vRemark="Item :"+Description+" 1 x "+CurCode+UnitPrice;
                        String insertAdj="Insert into ret_pointadjustment(cuscode,D_ate,Point," +
                                "DocType,Remark,D_ateTime)values('"+CusCode+"', '"+D_ate+"', '"+Point+"'," +
                                "'Decrease', '"+vRemark+"', '"+D_ateTime+"')";
                        Statement stmtAdj = conn.createStatement();
                        stmtAdj.execute(insertAdj);
                        z="success";
                    }
                }else if(DBStatus.equals("0")){
                    String insertHd="Insert into ret_pointredeem_hd(Doc1No,D_ate,T_ime," +
                            "Remark,ClientCode,L_ink," +
                            "SynYN)values('"+Doc1No+"', '"+D_ateTime+"', '00:00:00'," +
                            "'', '"+CusCode+"', '1'," +
                            "'0')";
                    db.addQuery(insertHd);

                    String insertDt="Insert into ret_pointredeem_dt(Doc1No,ItemCode,Point," +
                            "UnitCost,Qty,FactorQty," +
                            "UOM,Description,SynYN)values('"+Doc1No+"', '"+ItemCode+"', '"+Point+"'," +
                            "'"+UnitPrice+"', '1', '1'," +
                            "'"+UOM+"', '"+Description+"','0')";
                   db.addQuery(insertDt);

                   String vRemark="Item :"+Description+" 1 x "+CurCode+UnitPrice;
                   String insertAdj="Insert into ret_pointadjustment(cuscode,D_ate,Point," +
                            "DocType,Remark,D_ateTime," +
                            "SynYN)values('"+CusCode+"', '"+D_ate+"', '"+Point+"'," +
                            "'Decrease', '"+vRemark+"', '"+D_ateTime+"'," +
                            "'0')";
                   db.addQuery(insertAdj);
                   z="success";
                }
                return z;
            } catch (SQLiteException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return z;
        }
    }

    private String zeroDecimal(Double values){
        String textDecimal="";
        textDecimal=String.format(Locale.US, "%,.0f", values);
        return textDecimal;
    }

    private String datedNow(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String D_ateTime = sdf.format(date);
        return D_ateTime;
    }
    private String datedShort(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String D_ateTime = sdf.format(date);
        return D_ateTime;
    }
    private String datedTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String D_ateTime = sdf.format(date);
        return D_ateTime;
    }

    public String minRight(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == 'P') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
}

