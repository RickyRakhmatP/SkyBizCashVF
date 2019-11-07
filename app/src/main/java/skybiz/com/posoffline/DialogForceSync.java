package skybiz.com.posoffline;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.m_NewObject.Encode;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;
import skybiz.com.posoffline.ui_Sync.Sync;
//import skybiz.com.cashoff.m_Tax.DownloaderTax;


/**
 * Created by 7 on 14/12/2017.
 */

public class DialogForceSync extends DialogFragment {
    View view;
    Button btnCancel;
    String uFrom,SMobileYN="";
    TextView txtSyncItem;
    ProgressBar pbItem;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view            = inflater.inflate(R.layout.dialog_forcesync, container, false);
        btnCancel       = (Button)view.findViewById(R.id.btnCancel);
        txtSyncItem     = (TextView)view.findViewById(R.id.txtSyncItem);
        pbItem          = (ProgressBar)view.findViewById(R.id.pbItem);
        uFrom           = this.getArguments().getString("TYPE_KEY");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        initview();
        return view;
    }
    private void initview(){
        SyncInAll syncInAll=new SyncInAll(getActivity(),pbItem);
        syncInAll.execute();
    }
    private void SetProgressSync(String vValue){
        txtSyncItem.setText(vValue);
    }

    private class SyncInAll extends AsyncTask<Void,Integer,String>{
        Context c;
        ProgressBar pbItem;
        String IPAddress,UserName,Password,
                DBName,Port,URL,z,
                DBStatus,ItemConn,EncodeType;
        JSONObject jsonReq,jsonRes;
        Connection conn=null;
        DBAdapter db=null;
        int TotalRow=0;
        String T_ypeSync="";

        public SyncInAll(Context c, ProgressBar pbItem) {
            this.c = c;
            this.pbItem=pbItem;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fnshow();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                Toast.makeText(c,"Success, Force Sync Master Files", Toast.LENGTH_SHORT).show();
            }else if(result.equals("error")){
                Toast.makeText(c,"Error,  Force Sync Master Files", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (this.pbItem != null) {
                pbItem.setMax(TotalRow);
                pbItem.setProgress(values[0]);
                SetProgressSync(T_ypeSync+" "+values[0] +" of "+ TotalRow);
            }
        }

        private String fnshow(){
            try{
                z="error";
                db = new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName, UserName, Password," +
                        " DBName, Port, DBStatus," +
                        " ItemConn, EncodeType" +
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
                }
                curSet.close();

                String qSet2="select SMobileYN from tb_othersetting";
                Cursor rsSet2=db.getQuery(qSet2);
                while(rsSet2.moveToNext()) {
                    SMobileYN = rsSet2.getString(0);
                }
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                conn = Connector.connect(URL, UserName, Password);
                if(conn!=null){
                    fnsyncgroup();
                    fnsynccustomer();
                    fnsyncitem();
                    z="success";
                }else{
                    z="error";
                }

                db.closeDB();
                return z;
            }catch (SQLiteException e) {
                e.printStackTrace();
            }
            return z;
        }
        private  void fnsyncitem(){
            try{

                String vClause="";
                if(SMobileYN.equals("1")){
                    vClause=" AND SMobileYN='1' ";
                }
                String qDel="delete from stk_master";
                db.addQuery(qDel);
                T_ypeSync="Synchronize Item";
                TotalRow=getTotal("stk_master","where SuspendedYN='0' "+vClause+" ");
                String sqlItem = "SELECT ItemCode,Description,ItemGroup," +
                        " IFNULL(UnitPrice,0) as UnitPrice,IFNULL(UnitCost,0) as UnitCost, DefaultUOM," +
                        " UOM, UOM1, UOM2," +
                        " UOM3, UOM4, UOMPrice1," +
                        " UOMPrice2, UOMPrice3, UOMPrice4, " +
                        " UOMFactor1, UOMFactor2, UOMFactor3," +
                        " UOMFactor4, AnalysisCode1, AnalysisCode2," +
                        " AnalysisCode3, AnalysisCode4, AnalysisCode5," +
                        " MSP, MSP1, MSP2, " +
                        " MSP3, MSP4, BaseCode, " +
                        " UOMCode1, UOMCode2, UOMCode3, " +
                        " UOMCode4, MAXSP, MAXSP1, " +
                        " MAXSP2, MAXSP3, MAXSP4," +
                        " SalesTaxCode, RetailTaxCode,PurchaseTaxCode," +
                        " FixedPriceYN, AlternateItem, HCDiscount, " +
                        " DisRate1, '0' as Point " +
                        " FROM stk_master WHERE SuspendedYN='0' "+vClause+" ORDER BY ItemCode ";
                Statement stmtItem=conn.createStatement();
                stmtItem.executeQuery("SET NAMES 'LATIN1'");
                stmtItem.executeQuery("SET CHARACTER SET 'LATIN1'");
                stmtItem.execute(sqlItem);
                ResultSet rsItem=stmtItem.getResultSet();
                int i=1;
                while(rsItem.next()){
                    String insert="INSERT INTO stk_master(ItemCode, Description, ItemGroup, " +
                            " UnitPrice, UnitCost, DefaultUOM, " +
                            " UOM, UOM1, UOM2, " +
                            " UOM3, UOM4, UOMPrice1, " +
                            " UOMPrice2, UOMPrice3, UOMPrice4, " +
                            " UOMFactor1, UOMFactor2, UOMFactor3, " +
                            " UOMFactor4, AnalysisCode1, AnalysisCode2, " +
                            " AnalysisCode3, AnalysisCode4, AnalysisCode5, " +
                            " MSP, MSP1, MSP2, " +
                            " MSP3, MSP4, BaseCode, " +
                            " UOMCode1, UOMCode2, UOMCode3, " +
                            " UOMCode4, MAXSP, MAXSP1, " +
                            " MAXSP2, MAXSP3, MAXSP4, " +
                            " SalesTaxCode, RetailTaxCode,PurchaseTaxCode, " +
                            " FixedPriceYN, AlternateItem, HCDiscount, " +
                            " DisRate1, Point)values('"+rsItem.getString(1)+"', '"+ Encode.setChar(EncodeType,rsItem.getString(2))+"', '"+Encode.setChar(EncodeType,rsItem.getString(3))+"'," +
                            " '"+rsItem.getString(4)+"', '"+rsItem.getString(5)+"', '"+rsItem.getString(6)+"', " +
                            " '"+Encode.setChar(EncodeType,rsItem.getString(7))+"', '"+rsItem.getString(8)+"', '"+rsItem.getString(9)+"', " +
                            " '"+rsItem.getString(10)+"', '"+rsItem.getString(11)+"', '"+rsItem.getString(12)+"', " +
                            " '"+rsItem.getString(13)+"', '"+rsItem.getString(14)+"', '"+rsItem.getString(15)+"', " +
                            " '"+rsItem.getString(16)+"', '"+rsItem.getString(17)+"', '"+rsItem.getString(18)+"', " +
                            " '"+rsItem.getString(19)+"', '"+rsItem.getString(20)+"', '"+rsItem.getString(21)+"', " +
                            " '"+rsItem.getString(22)+"', '"+rsItem.getString(23)+"', '"+rsItem.getString(24)+"', " +
                            " '"+rsItem.getString(25)+"', '"+rsItem.getString(26)+"', '"+rsItem.getString(27)+"', " +
                            " '"+rsItem.getString(28)+"', '"+rsItem.getString(29)+"', '"+rsItem.getString(30)+"', " +
                            " '"+rsItem.getString(31)+"', '"+rsItem.getString(32)+"', '"+rsItem.getString(33)+"', " +
                            " '"+rsItem.getString(34)+"', '"+rsItem.getString(35)+"', '"+rsItem.getString(36)+"', " +
                            " '"+rsItem.getString(37)+"', '"+rsItem.getString(38)+"', '"+rsItem.getString(39)+"', " +
                            " '"+rsItem.getString(40)+"', '"+rsItem.getString(41)+"', '"+rsItem.getString(42)+"', " +
                            " '"+rsItem.getString(43)+"', '"+Encode.setChar(EncodeType,rsItem.getString(44))+"', '"+rsItem.getString(45)+"', " +
                            " '"+rsItem.getString(46)+"',  '"+rsItem.getString(47)+"')";
                    db.addQuery(insert);
                    publishProgress(i);
                    i++;
                }

                T_ypeSync = "Sync In Item Photo : ";
                TotalRow    = getTotal("stk_master_photo","");
                String delPhoto="delete from stk_master_photo";
                db.addQuery(delPhoto);
                String qPhoto="select ItemCode, IFNULL(PhotoFile,'')as PhotoFile from stk_master_photo";
                Statement stmtPh = conn.createStatement();
                stmtPh.execute(qPhoto);
                ResultSet rsPhoto = stmtPh.getResultSet();
                i=1;
                while (rsPhoto.next()) {
                    String PhotoFile    = rsPhoto.getString(2);
                    if(!PhotoFile.isEmpty()){
                        Blob test           =rsPhoto.getBlob(2);
                        int blobl           =(int)test.length();
                        byte[] blobasbyte   =test.getBytes(1,blobl);
                        Bitmap bmp          =BitmapFactory.decodeByteArray(blobasbyte,0,blobasbyte.length);
                        PhotoFile           =encodeBmp(bmp);
                    }
                    String insertPh="insert into stk_master_photo(ItemCode, PhotoFile)values(" +
                            "'"+rsPhoto.getString(1)+"', '"+PhotoFile+"')";
                    //Log.d("IN Photo",insertPh);
                    db.addQuery(insertPh);
                    publishProgress(i);
                    i++;
                }

                String delUOM = "delete from stk_uom";
                db.addQuery(delUOM);
                T_ypeSync   = "Sync In UOM : ";
                TotalRow    =  getTotal("stk_uom","");
                String sqlU = "select UOM, UOMPlural from stk_uom ";
                Statement stmtU = conn.createStatement();
                stmtU.executeQuery("SET NAMES 'LATIN1'");
                stmtU.executeQuery("SET CHARACTER SET 'LATIN1'");
                stmtU.execute(sqlU);
                ResultSet rsUOM = stmtU.getResultSet();
                i=1;
                while (rsUOM.next()) {
                    String QueryIn = "Insert into stk_uom(UOM, UOMPlural)values('"+Encode.setChar(EncodeType,rsUOM.getString(1))+"', '"+Encode.setChar(EncodeType,rsUOM.getString(2))+"' )";
                    //Log.d("INSERT UOM", QueryIn);
                    db.addQuery(QueryIn);
                    publishProgress(i);
                    i++;
                }

            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        private void fnsyncgroup(){
            try{

                String vClause="";
                if(SMobileYN.equals("0")){
                    vClause=" WHERE SMobileYN='1' ";
                }
                String qDel="delete from stk_group";
                db.addQuery(qDel);
                T_ypeSync="Synchronize Item Group";
                TotalRow=getTotal("stk_group",vClause);
                String sqlGroup = "select ItemGroup, Description, '' as Description2," +
                        " Modifier1, Modifier2, L_ink, " +
                        " DATE_FORMAT(DateTimeModified,'%Y-%m-%d %H:%i:%s') as DateTimeModified, IFNULL(Printer,'') as Printer " +
                        " from stk_group  "+vClause+" ";
                Statement stmtGroup = conn.createStatement();
                stmtGroup.executeQuery("SET NAMES 'LATIN1'");
                stmtGroup.executeQuery("SET CHARACTER SET 'LATIN1'");
                stmtGroup.execute(sqlGroup);
                ResultSet rsGroup = stmtGroup.getResultSet();
                int i = 1;
                while (rsGroup.next()) {
                    String QueryIn = "Insert into stk_group(ItemGroup, Description, Description2, " +
                            " Modifier1, Modifier2, L_ink, " +
                            " DateTimeModified, Printer)values(" +
                            " '" + charReplace(Encode.setChar(EncodeType,rsGroup.getString("ItemGroup"))) + "', '" +  charReplace(Encode.setChar(EncodeType,rsGroup.getString("Description"))) + "', '" + rsGroup.getString("Description2") + "', " +
                            " '" + Encode.setChar(EncodeType,rsGroup.getString("Modifier1")) + "', '" + rsGroup.getString("Modifier2") + "', '" + rsGroup.getString("L_ink") + "', " +
                            " '" + rsGroup.getString("DateTimeModified") + "', '"+rsGroup.getString("Printer")+"' )";
                   // Log.d("INSERT GROUP", QueryIn);
                    long addItem = db.addQuery(QueryIn);
                    if (addItem > 0) {
                        publishProgress(i);
                    } else {
                        Log.d("ERROR", rsGroup.getString("ItemGroup"));
                    }
                    i++;
                }

                T_ypeSync = "Sync In Price Matrix : ";
                TotalRow  = getTotal("stk_pricematrix","");
                String vDelP="delete from stk_pricematrix";
                db.addQuery(vDelP);
                String qPrice="select ItemCode, " +
                        " ItemGroup, CategoryCode, Description," +
                        " Pct, Criteria, Comparison," +
                        " Qty, PeriodYN, DATE_FORMAT(DateStart,'%Y-%m-%d') as DateStart," +
                        " DATE_FORMAT(DateEnd,'%Y-%m-%d') as DateEnd, QuotaYN, QuotaQty," +
                        " B_ase, CusCode, BranchCode," +
                        " UnitPriceFormula, CusAna1, CusAna2," +
                        " CusAna3, ItemAna3, LowerLimitQty," +
                        " UpperLimitQty, Y1, Y2," +
                        " Y3, Y4, Y5, " +
                        " Status, DATE_FORMAT(TimeStart,'%H:%i:%s') as TimeStart, DATE_FORMAT(TimeEnd,'%H:%i:%s') as TimeEnd," +
                        " L_ink, DATE_FORMAT(DateTimeModify,'%Y-%m-%d %H:%i:%s') as DateTimeModify, UserCode," +
                        " MatrixType, UOMType, SpecialPriceYN," +
                        " PriceCode, ServiceChargeYN, Memo from stk_pricematrix";
                Statement stmtMat = conn.createStatement();
                stmtMat.execute(qPrice);
                ResultSet rsMat=stmtMat.getResultSet();
                i=1;
                while(rsMat.next()){
                    String insertP="insert into stk_pricematrix(ItemCode," +
                            " ItemGroup, CategoryCode, Description," +
                            " Pct, Criteria, Comparison," +
                            " Qty, PeriodYN, DateStart," +
                            " DateEnd, QuotaYN, QuotaQty," +
                            " B_ase, CusCode, BranchCode, "+
                            " UnitPriceFormula, CusAna1, CusAna2, " +
                            " CusAna3, ItemAna3, LowerLimitQty, " +
                            " UpperLimitQty, Y1, Y2," +
                            " Y3, Y4, Y5, " +
                            " Status, TimeStart, TimeEnd, " +
                            " L_ink, DateTimeModify, UserCode, " +
                            " MatrixType, UOMType, SpecialPriceYN, " +
                            " PriceCode, ServiceChargeYN, Memo)values('"+rsMat.getString(1)+"'," +
                            " '"+rsMat.getString(2)+"', '"+rsMat.getString(3)+"', '"+rsMat.getString(4)+"', " +
                            " '"+rsMat.getString(5)+"', '"+rsMat.getString(6)+"', '"+rsMat.getString(7)+"', " +
                            " '"+rsMat.getString(8)+"', '"+rsMat.getString(9)+"', '"+rsMat.getString(10)+"', " +
                            " '"+rsMat.getString(11)+"', '"+rsMat.getString(12)+"', '"+rsMat.getString(13)+"', " +
                            " '"+rsMat.getString(14)+"', '"+rsMat.getString(15)+"', '"+rsMat.getString(16)+"', " +
                            " '"+rsMat.getString(17)+"', '"+rsMat.getString(18)+"', '"+rsMat.getString(19)+"', " +
                            " '"+rsMat.getString(20)+"', '"+rsMat.getString(21)+"', '"+rsMat.getString(22)+"', " +
                            " '"+rsMat.getString(23)+"', '"+rsMat.getString(24)+"', '"+rsMat.getString(25)+"', " +
                            " '"+rsMat.getString(26)+"', '"+rsMat.getString(27)+"', '"+rsMat.getString(28)+"', " +
                            " '"+rsMat.getString(29)+"', '"+rsMat.getString(30)+"', '"+rsMat.getString(31)+"', "+
                            " '"+rsMat.getString(32)+"', '"+rsMat.getString(33)+"', '"+rsMat.getString(34)+"', "+
                            " '"+rsMat.getString(35)+"', '"+rsMat.getString(36)+"', '"+rsMat.getString(35)+"', "+
                            " '"+rsMat.getString(36)+"','"+rsMat.getString(37)+"',  '"+rsMat.getString(38)+"')";
                    Log.d("INSERT PRICE MATRIX",insertP);
                    db.addQuery(insertP);
                    publishProgress(i);
                    i++;
                }

                T_ypeSync = "Sync In Miscellaneous : ";
                TotalRow  = getTotal("stk_othercharges","where SuspendedYN='0' ");
                String vDel="delete from stk_othercharges";
                db.addQuery(vDel);
                String sql = "select OCCode, Description, FORMAT(R_ate,2) as R_ate, T_ype, " +
                        " GLCode, L_ink, FORMAT(UnitCost,2) as UnitCost, UOM, " +
                        " PurchaseTaxCode, SalesTaxCode, RetailTaxCode, OCGroup," +
                        " OCAna1, OCAna2, OCAna3, SuspendedYN, " +
                        " MinimumCharge, PurchaseAccount, BasedGrossAmountYN, ImportServiceYN " +
                        " from stk_othercharges where SuspendedYN='0' ";
                Statement statement = conn.createStatement();
                statement.executeQuery("SET NAMES 'LATIN1'");
                statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                statement.execute(sql);
                ResultSet rsData = statement.getResultSet();
                i = 1;
                while(rsData.next()){
                    String qInsert="Insert into stk_othercharges(OCCode, Description, R_ate," +
                            " T_ype, GLCode, L_ink," +
                            " UnitCost, UOM, PurchaseTaxCode," +
                            " SalesTaxCode, RetailTaxCode, OCGroup, " +
                            " OCAna1, OCAna2, OCAna3, " +
                            " SuspendedYN, MinimumCharge, PurchaseAccount," +
                            " BasedGrossAmountYN, ImportServiceYN)values(" +
                            " '"+rsData.getString("OCCode")+"', '"+rsData.getString("Description")+"', '"+rsData.getString("R_ate")+"', " +
                            " '"+rsData.getString("T_ype")+"', '"+rsData.getString("GLCode")+"', '"+rsData.getString("L_ink")+"', " +
                            " '"+rsData.getString("UnitCost")+"', '"+rsData.getString("UOM")+"', '"+rsData.getString("PurchaseTaxCode")+"', " +
                            " '"+rsData.getString("SalesTaxCode")+"', '"+rsData.getString("RetailTaxCode")+"', '"+rsData.getString("OCGroup")+"', " +
                            " '"+rsData.getString("OCAna1")+"', '"+rsData.getString("OCAna2")+"', '"+rsData.getString("OCAna3")+"', " +
                            " '"+rsData.getString("SuspendedYN")+"', '"+rsData.getString("MinimumCharge")+"', '"+rsData.getString("PurchaseAccount")+"', " +
                            " '"+rsData.getString("BasedGrossAmountYN")+"', '"+rsData.getString("ImportServiceYN")+"' )";
                    db.addQuery(qInsert);
                    publishProgress(i);
                    i++;
                }

            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        private void fnsynccustomer(){
            try{
                T_ypeSync   = "Sync In Customer : ";
                TotalRow    = getTotal("customer","where StatusBadYN='0' ");
                String del = "delete from customer";
                db.addQuery(del);
                String sql = "select CusCode,CusName,FinCatCode," +
                        " AccountCode,Address,CurCode, " +
                        " TermCode,D_ay,SalesPersonCode," +
                        " Tel,Tel2,Fax," +
                        " Fax2,Contact,ContactTel," +
                        " Email,StatusBadYN, Town," +
                        " State,Country,PostCode," +
                        " L_ink, NRICNo, DATE_FORMAT(DOB,'%Y-%m-%d') as DOB, " +
                        " Sex, MemberType, CardNo," +
                        " PaymentCode, DATE_FORMAT(DateTimeModified,'%Y-%m-%d %H:%i:%s') as DateTimeModified, MembershipClass  " +
                        " from customer where StatusBadYN='0' ";
                Statement statement = conn.createStatement();
                statement.execute(sql);
                ResultSet rsCus = statement.getResultSet();
                int i = 1;
                while (rsCus.next()) {
                    String QueryIn = "Insert into customer(CusCode, CusName, FinCatCode, " +
                            " AccountCode, Address,CurCode, " +
                            " TermCode, D_ay, SalesPersonCode, " +
                            " Tel, Tel2, Fax, " +
                            " Fax2, Contact, ContactTel, " +
                            " Email, StatusBadYN, Town," +
                            " State, Country, PostCode," +
                            " L_ink, NRICNo, DOB, " +
                            " Sex, MemberType, CardNo," +
                            " PaymentCode, DateTimeModified, MembershipClass)values(" +
                            " '" + rsCus.getString("CusCode") + "', '" + charReplace(rsCus.getString("CusName")) + "', '" + rsCus.getString("FinCatCode") + "', " +
                            " '" + rsCus.getString("AccountCode") + "', '" + charReplace(rsCus.getString("Address")) + "', '" + rsCus.getString("CurCode") + "', " +
                            " '" + rsCus.getString("TermCode") + "', '" + rsCus.getString("D_ay") + "', '" + rsCus.getString("SalesPersonCode") + "'," +
                            " '" + rsCus.getString("Tel") + "', '" + rsCus.getString("Tel2") + "',  '" + rsCus.getString("Fax") + "', " +
                            " '" + rsCus.getString("Fax2") + "', '" + charReplace(rsCus.getString("Contact")) + "', '" + rsCus.getString("ContactTel") + "', " +
                            " '" + rsCus.getString("Email") + "', '" + rsCus.getString("StatusBadYN") + "', '" + rsCus.getString("Town") + "',  " +
                            " '" + rsCus.getString("State") + "', '" + rsCus.getString("Country") + "', '" + rsCus.getString("PostCode") + "',  " +
                            " '" + rsCus.getString("L_ink") + "', '" + rsCus.getString("NRICNo") + "', '" + rsCus.getString("DOB") + "',  " +
                            " '" + rsCus.getString("Sex") + "', '" + rsCus.getString("MemberType") + "', '" + rsCus.getString("CardNo") + "',  " +
                            " '" + rsCus.getString("PaymentCode") + "', '" + rsCus.getString("DateTimeModified") + "', '"+rsCus.getString("MembershipClass")+"')";
                    long addItem = db.addQuery(QueryIn);
                    if (addItem > 0) {
                        publishProgress(i);
                    }
                    i++;
                }

                T_ypeSync       = "Sync In Sales Person : ";
                TotalRow        = getTotal("stk_salesman","");
                String vDelSales="delete from stk_salesman";
                db.addQuery(vDelSales);
                String qSales="select SalesPersonCode, N_ame, Address, " +
                        "AreaCode, Tel, Fax, " +
                        "ParentCode, DetailYN, Status, " +
                        "R_atio, BranchCode from stk_salesman";
                Statement stmtSales = conn.createStatement();
                stmtSales.execute(qSales);
                ResultSet rsSales = stmtSales.getResultSet();
                i=1;
                while(rsSales.next()){
                    String insertSales="insert into stk_salesman(SalesPersonCode, N_ame, Address," +
                            " AreaCode, Tel, Fax," +
                            " ParentCode, DetailYN, Status," +
                            " R_atio, BranchCode)values(" +
                            " '"+rsSales.getString(1)+"', '"+rsSales.getString(2)+"', '"+rsSales.getString(3)+"'," +
                            " '"+rsSales.getString(4)+"', '"+rsSales.getString(5)+"', '"+rsSales.getString(6)+"'," +
                            " '"+rsSales.getString(7)+"', '"+rsSales.getString(8)+"', '"+rsSales.getString(9)+"'," +
                            " '"+rsSales.getString(10)+"', '"+rsSales.getString(11)+"' )";
                    db.addQuery(insertSales);
                    publishProgress(i);
                    i++;
                }

                T_ypeSync       = "Sync In Category : ";
                TotalRow        = getTotal("stk_category","");
                String vDelCateg="delete from stk_category";
                db.addQuery(vDelCateg);
                String qCateg="select CategoryCode, Description, AccountCode, " +
                        "L_ink, CategoryFor, SMobileYN from stk_category";
                Statement stmtCat = conn.createStatement();
                stmtCat.execute(qCateg);
                ResultSet rsCat = stmtCat.getResultSet();
                i=1;
                while(rsCat.next()){
                    String insertCat="insert into stk_category(CategoryCode, Description, AccountCode," +
                            " L_ink, CategoryFor, SMobileYN)values(" +
                            " '"+rsCat.getString(1)+"', '"+rsCat.getString(2)+"', '"+rsCat.getString(3)+"'," +
                            " '"+rsCat.getString(4)+"', '"+rsCat.getString(5)+"', '"+rsCat.getString(6)+"')";
                    db.addQuery(insertCat);
                    publishProgress(i);
                    i++;
                }

                T_ypeSync       = "Sync In Membership Class : ";
                TotalRow        = getTotal("ret_membership_class","");
                String vDelMember="delete from ret_membership_class";
                db.addQuery(vDelMember);
                String qMembership="select Class, " +
                        " Description, CollectionMethod, DateFrom, " +
                        " DateTo, RatioPoint, RatioAmount from ret_membership_class";
                Statement stmtM = conn.createStatement();
                stmtM.execute(qMembership);
                ResultSet rsMember = stmtM.getResultSet();
                i=1;
                while(rsMember.next()){
                    String insertMember="insert into ret_membership_class(Class, " +
                            " Description, CollectionMethod, DateFrom, " +
                            " DateTo, RatioPoint, RatioAmount)values('"+rsMember.getString(1)+"'," +
                            " '"+rsMember.getString(2)+"', '"+rsMember.getString(3)+"', '"+rsMember.getString(4)+"'," +
                            " '"+rsMember.getString(5)+"', '"+rsMember.getString(6)+"', '"+rsMember.getString(7)+"')";
                    db.addQuery(insertMember);
                    publishProgress(i);
                    i++;
                }

                T_ypeSync       = "Sync In Point : ";
                TotalRow        = getTotal("ret_point","");
                String delPoint="delete from ret_point";
                db.addQuery(delPoint);
                String qPoint="select Item, Point, B_ase, L_ink, DocType from ret_point";
                Statement stmtP = conn.createStatement();
                stmtP.execute(qPoint);
                ResultSet rsPoint = stmtP.getResultSet();
                i=1;
                while (rsPoint.next()) {
                    String insertP="insert into ret_point(Item, Point, B_ase, " +
                            "L_ink, DocType)values('"+rsPoint.getString(1)+"', '"+rsPoint.getString(2)+"', '"+rsPoint.getString(3)+"'," +
                            "'"+rsPoint.getString(4)+"', '"+rsPoint.getString(5)+"')";
                    Log.d("IN POINT",insertP);
                    db.addQuery(insertP);
                    publishProgress(i);
                    i++;
                }
                stmtP.close();


                T_ypeSync       = "Sync In Payment Type : ";
                TotalRow        = getTotal("ret_paymenttype"," WHERE PaymentCode <> '' AND Status = 'Active' ");
                db.DelAllPayType();
                String vQuery = "SELECT PaymentCode, PaymentType, CC_PaidByCompanyYN, " +
                        "Charges1, MerchantCode, MerchantKey " +
                        "FROM ret_paymenttype WHERE PaymentCode <> '' AND Status = 'Active' " +
                        "Order By PaymentType, PaymentCode";
                Statement stmtPay = conn.createStatement();
                if (stmtPay.execute(vQuery)) {
                    ResultSet rsPay = stmtPay.getResultSet();
                    while (rsPay.next()) {
                        String PaymentCode      = rsPay.getString("PaymentCode");
                        String PaymentType      = rsPay.getString("PaymentType");
                        String PaidByCompanyYN  = rsPay.getString("CC_PaidByCompanyYN");
                        String  Charges1        = rsPay.getString("Charges1");
                        String MerchantCode     = rsPay.getString("MerchantCode");
                        String MerchantKey      = rsPay.getString("MerchantKey");
                        db.addPayType(PaymentCode, PaymentType, Charges1, PaidByCompanyYN, MerchantCode, MerchantKey);
                    }
                    rsPay.close();
                }
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        private  int getTotal(String T_able, String Clause){
            int total=0;
            try{
                String qTotal="select count(*) as numrows from "+T_able+" "+Clause+" ";
                Statement stmtTotal=conn.createStatement();
                stmtTotal.execute(qTotal);
                ResultSet rsTotal=stmtTotal.getResultSet();
                while(rsTotal.next()){
                    total=rsTotal.getInt(1);
                }
                return total;
            }catch (SQLException e){
                e.printStackTrace();
            }
            return total;
        }

        private String charReplace(String text){
            String newText=text.replaceAll("[\\$|,|;|']","");
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
}
