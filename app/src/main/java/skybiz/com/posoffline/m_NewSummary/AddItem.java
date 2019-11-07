package skybiz.com.posoffline.m_NewSummary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.CalculateLineAmt;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class AddItem extends AsyncTask<Void, Void, String> {
    Context c;
    String ItemCode,vQty,vUnitPrice,
            vHCDiscount,vDisRate1,vUOM,
            vDetailTaxCode,vDescription,DocType,
            BlankLine,vTable,vPoint;
    String IPAddress, UserName, Password,
            DBName,Port,DBStatus,
            ItemConn,URL,z,
            BranchCode,LocationCode,EncodeType;

    String deviceId,RunNo,CurCode,
            DUD6,UserCode,PriceMatrixDesc="",
            ServiceChargeYN,vCategoryCode,vItemGroup;
    Double Qty,UnitPrice,HCTax,
            HCDiscount, HCLineAmt,dQty,
            DisRate1, UnitCost;
    //TelephonyManager telephonyManager;
    Connection conn=null;
    DBAdapter db=null;
    int LineNo=0;

    public AddItem(Context c, String DocType, String itemCode,
                   String vDescription, String vItemGroup, String vQty, String vUnitPrice,
                   String vUOM, String vDetailTaxCode, String vHCDiscount,
                   String vDisRate1, String BlankLine, String vPoint) {
        this.c              = c;
        this.DocType        = DocType;
        ItemCode            = itemCode;
        this.vDescription   = vDescription;
        this.vItemGroup     = vItemGroup;
        this.vQty           = vQty;
        this.vUnitPrice     = vUnitPrice;
        this.vUOM           = vUOM;
        this.vDetailTaxCode = vDetailTaxCode;
        this.vHCDiscount    = vHCDiscount;
        this.vDisRate1      = vDisRate1;
        this.BlankLine      = BlankLine;
        this.vPoint         = vPoint;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.fnadd();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result==null){
            Toast.makeText(c,"Add Item Failure", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Add Item Successfull", Toast.LENGTH_SHORT).show();
        }
    }
    private String fnadd(){
        try{
            JSONObject jsonReq,jsonRes;
            //telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
            //deviceId = telephonyManager.getDeviceId();
            db=new DBAdapter(c);
            db.openDB();
            String qCom="select CurCode from companysetup";
            Cursor rsCom = db.getQuery(qCom);
            while (rsCom.moveToNext()) {
                CurCode = rsCom.getString(0);
            }
            String qMember="select CategoryCode from tb_member";
            Cursor rsMember=db.getQuery(qMember);
            while(rsMember.moveToNext()){
                vCategoryCode=rsMember.getString(0);
            }
            String querySet="select ServerName, UserName, Password, " +
                    "DBName, Port, DBStatus, " +
                    "ItemConn, EncodeType, BranchCode," +
                    "LocationCode, UserCode from tb_setting ";
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
                BranchCode=curSet.getString(8);
                LocationCode=curSet.getString(9);
                UserCode=curSet.getString(10);
            }
            if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                conn = Connector.connect(URL, UserName, Password);
            }
            dQty=0.00;
            Log.d("DocType",DocType);
            if(DocType.equals("SO")){
                vTable="cloud_sales_order_dt";
            }else{
                vTable="cloud_cus_inv_dt";

                String qCheckNo="select LineNo from cloud_cus_inv_dt where ComputerName='"+UserCode+"' Order By RunNo Desc ";
                Cursor rsCheckNo=db.getQuery(qCheckNo);
                while(rsCheckNo.moveToNext()){
                    LineNo=rsCheckNo.getInt(0);
                }
            }


            String vCheck="select RunNo,Qty,HCUnitCost from "+vTable+" where" +
                    " ItemCode='" + ItemCode + "' and ComputerName='" + deviceId + "' and DocType='"+DocType+"'  ";
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vCheck);
                jsonReq.put("action", "select");
                String response1 = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response1);
                String res = jsonRes.getString("hasil");
                if(!res.equals("0")) {
                    JSONArray jaCheck = new JSONArray(res);
                    JSONObject joCheck = null;
                    for (int i = 0; i < jaCheck.length(); i++) {
                        joCheck = jaCheck.getJSONObject(i);
                        RunNo = joCheck.getString("RunNo");
                        dQty = joCheck.getDouble("QTY");
                        UnitPrice = joCheck.getDouble("HCUnitCost");
                    }
                }
            }else {
                Cursor curCheck = db.getQuery(vCheck);
                while (curCheck.moveToNext()) {
                    RunNo = curCheck.getString(0);
                    dQty = curCheck.getDouble(1);
                    UnitPrice = curCheck.getDouble(2);
                }
            }
            String vData="";
            UnitPrice  = Double.parseDouble(vUnitPrice);
            HCDiscount = Double.parseDouble(vHCDiscount);
            DisRate1   = Double.parseDouble(vDisRate1);
            if(dQty==0.00){
                Qty=1.00;
                if(HCDiscount>0){
                    DUD6="Discount by amount "+CurCode+HCDiscount;
                }else if(DisRate1>0){
                    DUD6="Discount by percentage "+DisRate1+"%";
                }
                vData = addItem();
            }else{
                Qty = dQty+1.00;
                vData = updateItem(RunNo, vDescription,Qty, UnitPrice,vDetailTaxCode,vUOM,1.00, HCDiscount, DisRate1);
            }
            db.closeDB();
            return vData;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    //add item new structure
    private String addItem(){
        String vInsert = "";
        Double TaxRate1=0.00,FactorQty=1.00;
        JSONObject jsonReq,jsonRes;
        try {
           // DBAdapter db=new DBAdapter(c);
            //db.openDB();
            String checkGST="select GSTNo from companysetup";
            String GSTYN="";
            Cursor rsGST=db.getQuery(checkGST);
            while(rsGST.moveToNext()){
                GSTYN=rsGST.getString(0);
            }
            if(GSTYN.equals("NO")){
                vDetailTaxCode="";
            }
            String PriceMatrixYN = "0";
            String vmatrix = "select PricingMatrixYN from sys_general_setup2";
            if (ItemConn.equals("0")) {
                Cursor rsM=db.getQuery(vmatrix);
                while(rsM.moveToNext()){
                    PriceMatrixYN=rsM.getString(0);
                }
            }else if(ItemConn.equals("1")) {
                Statement stmtM = conn.createStatement();
                stmtM.execute(vmatrix);
                ResultSet rsM = stmtM.getResultSet();
                while (rsM.next()) {
                    PriceMatrixYN = rsM.getString("PricingMatrixYN");
                }
            }
            if(PriceMatrixYN.equals("1")){
                int numrow1 = getCountMatrix("5", ItemCode, vCategoryCode);
                if (numrow1 > 0) {
                    getDataMatrix("5", ItemCode, vCategoryCode);
                } else {
                    int numrow2 = getCountMatrix("4", vItemGroup, vCategoryCode);
                    if (numrow2 > 0) {
                        getDataMatrix("4", vItemGroup, vCategoryCode);
                    } else {
                        int numrow3 = getCountMatrix("3", vCategoryCode, "");
                        if (numrow3 > 0) {
                            getDataMatrix("3", vCategoryCode, "");
                        } else {
                            int numrow4 = getCountMatrix("2", ItemCode, "");
                            if (numrow4 > 0) {
                                getDataMatrix("2", ItemCode, "");
                            } else {
                                int numrow5 = getCountMatrix("1", vItemGroup, "");
                                if (numrow5 > 0) {
                                    getDataMatrix("1", vItemGroup, "");
                                }
                            }
                        }
                    }
                }
                vDescription=vDescription+" "+PriceMatrixDesc;
            }

            CalculateLineAmt vDataAmt = fncalculate(c, Qty, UnitPrice, DisRate1, HCDiscount, vDetailTaxCode);
            TaxRate1 = vDataAmt.getvR_ate();
            HCDiscount = vDataAmt.getvHCDiscount();
            DisRate1 = vDataAmt.getvDisRate1();
            HCTax = vDataAmt.getvHCTax();
            HCLineAmt = vDataAmt.getvHCLineAmt();
            SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String datedNow = DateCurr.format(date);
            String vSQLInsert = "INSERT INTO "+vTable+" (Doc1No, N_o, ItemCode, Description, " +
                    "Qty, FactorQty, UOM, UOMSingular, " +
                    "HCUnitCost, DisRate1, HCDiscount, TaxRate1, " +
                    "HCTax, DetailTaxCode, HCLineAmt, BranchCode, " +
                    "DepartmentCode, ProjectCode, SalesPersonCode, LocationCode, " +
                    "WarrantyDate, BlankLine, DocType, AnalysisCode2, " +
                    "ComputerName, SORunNo ,DUD6, Point, " +
                    "UnitCost, ServiceChargeYN, LineNo, ItemGroup)" +
                    "VALUES(" +
                    " '" + datedNow + "','a','" + ItemCode + "','" + vDescription + "'," +
                    " '" + Qty + "','" + FactorQty + "','" + vUOM + "','" + vUOM + "'," +
                    " '" + UnitPrice + "','" + DisRate1 + "','" + HCDiscount + "','" + TaxRate1 + "'," +
                    " '" + HCTax + "','" + vDetailTaxCode + "','" + HCLineAmt + "'," +
                    " '"+BranchCode+"','android','android','android','"+LocationCode+"'," +
                    " '" + datedNow + "', '"+BlankLine+"', '"+DocType+"', '0', " +
                    "'"+UserCode+"', '0', '"+DUD6+"','"+vPoint+"'," +
                    " '', '', '"+LineNo+"', '"+vItemGroup+"')";
            Log.d("QUERY INSERT",vSQLInsert);
            /*
             String vSQLInsert = "INSERT INTO cloud_cus_inv_dt (Doc1No, N_o, ItemCode, Description, " +
                    "Qty, FactorQty, UOM, UOMSingular, " +
                    "HCUnitCost, DisRate1, HCDiscount, TaxRate1, " +
                    "HCTax, DetailTaxCode, HCLineAmt, BranchCode, " +
                    "DepartmentCode, ProjectCode, SalesPersonCode, LocationCode, " +
                    "WarrantyDate, BlankLine, DocType, AnalysisCode2," +
                    "ComputerName, SORunNo, UnitCost," +
                    "ServiceChargeYN)" +
                    "VALUES(" +
                    " '" + datedNow + "','a','" + ItemCode + "','" + Description + "'," +
                    " '" + Qty + "','" + FactorQty + "','" + UOM + "','" + UOM + "'," +
                    " '" + UnitPrice + "','" + DisRate1 + "','" + HCDiscount + "','" + TaxRate1 + "'," +
                    " '" + HCTax + "','" + DetailTaxCode + "','" + HCLineAmt + "', '"+BranchCode+"'," +
                    " '"+DepartmentCode+"', 'android', 'android', '"+LocationCode+"'," +
                    " '" + datedNow + "', '0', 'CS', '0', " +
                    " '"+UserCode+"', '0', '"+UnitCost+"'," +
                    " '"+ServiceChargeYN+"')";
             */
            Log.d("CLOUD DT",vSQLInsert);
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal conn1 = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vSQLInsert);
                jsonReq.put("action", "insert");
                String response = conn1.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response);
                String hasil = jsonRes.getString("success");
                Log.d("RES", hasil);
            }else {
                db.addQuery(vSQLInsert);
            }
           // db.closeDB();
            return vInsert;
        }catch (JSONException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return vInsert;
    }


    //update item new

    private String updateItem(String RunNo ,String Description, Double Qty, Double UnitPrice, String DetailTaxCode, String UOM, Double FactorQty, Double HCDiscount, Double DisRate1){
        String vUpdate="";
        Double TaxRate1=0.00;
        JSONObject jsonReq,jsonRes;
        try{
           // DBAdapter db=new DBAdapter(c);
           // db.openDB();
            String checkGST="select GSTNo from companysetup";
            String GSTYN="";
            Cursor rsGST=db.getQuery(checkGST);
            while(rsGST.moveToNext()){
                GSTYN=rsGST.getString(0);
            }
            if(GSTYN.equals("NO")){
                DetailTaxCode="";
            }
            CalculateLineAmt vDataAmt = fncalculate(c, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
            TaxRate1 = vDataAmt.getvR_ate();
            DisRate1 = vDataAmt.getvDisRate1();
            HCDiscount = vDataAmt.getvHCDiscount();
            HCTax = vDataAmt.getvHCTax();
            HCLineAmt = vDataAmt.getvHCLineAmt();
            String vSQLUpdate = "UPDATE "+vTable+" SET Qty='" + Qty + "'," +
                    " HCUnitCost='" + UnitPrice + "', DisRate1='" + DisRate1 + "'," +
                    " HCDiscount='" + HCDiscount + "', HCTax='" + HCTax + "', " +
                    " HCLineAmt='" + HCLineAmt + "', Description='"+Description+"'  " +
                    " where RunNo ='" + RunNo + "' ";
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vSQLUpdate);
                jsonReq.put("action", "update");
                String response = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response);
                String hasil = jsonRes.getString("success");
                Log.d("RES", hasil);
            }else {
                db.updateQuery(vSQLUpdate);
            }
            //db.closeDB();
            return vUpdate;
        }catch (JSONException e){
            e.printStackTrace();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return vUpdate;
    }

    public static CalculateLineAmt fncalculate(Context context, Double Qty, Double HCUnitCost, Double DisRate1, Double HCDiscount, String DetailTaxCode) {
        Double vR_ate,vHCTax,vAmountB4Tax,vTempAmt,vHCLineAmt,vDisRate1,vHCDiscount;
        String vTaxType;
        vR_ate      =0.00;
        vHCTax      =0.00;
        vHCLineAmt  =0.00;
        vHCDiscount =HCDiscount;
        vDisRate1   =0.00;
        if(DisRate1>0){
            vDisRate1		=DisRate1/100;
            vHCDiscount	    =Qty*HCUnitCost*vDisRate1;
            vAmountB4Tax	=(Qty*HCUnitCost)-vHCDiscount;
        }else{
            if(HCDiscount>0){
                vDisRate1	    =(HCDiscount*100)/(Qty*HCUnitCost);
                vHCDiscount	    = HCDiscount;
                vAmountB4Tax	=(Qty*HCUnitCost)-vHCDiscount;
                DisRate1        = vDisRate1;
            }else{
                vHCDiscount	    =0.00;
                DisRate1	    =0.00;
                vAmountB4Tax	 =(Qty*HCUnitCost)-vHCDiscount;
            }
        }
        if(DetailTaxCode.isEmpty()) {
            vHCLineAmt   = vAmountB4Tax;
            vR_ate       = 0.00;
            vHCTax       = 0.00;
        }else{
            String strSQL = "select R_ate,TaxType from stk_tax where TaxCode='" + DetailTaxCode + "' ";
            DBAdapter db = new DBAdapter(context);
            db.openDB();
            Cursor resultSet2 = db.getQuery(strSQL);
            while (resultSet2.moveToNext()) {
               // vR_ate      = Double.parseDouble(resultSet2.getString(0));
                vR_ate      = 0.00;
                vTaxType    = resultSet2.getString(1);
                if (vTaxType.equals("0") || vTaxType.equals("2")) {
                    vHCTax      =vAmountB4Tax * (vR_ate / (vR_ate + 100));
                    vTempAmt    =vAmountB4Tax - vHCTax;
                    vHCLineAmt  =vTempAmt + vHCTax;
                } else if (vTaxType.equals("1") || vTaxType.equals("3")) {
                    vHCTax      = vAmountB4Tax * (vR_ate / 100);
                    vHCLineAmt  = vAmountB4Tax + vHCTax;
                }
            }
            db.closeDB();
        }
        Log.d("RESULT", "TAX: " + vHCTax.toString());
        return new CalculateLineAmt(DisRate1, vHCDiscount, vR_ate, vHCTax, vHCLineAmt);
    }

    private  int getCountMatrix(String B_ase,String C_ode, String C_ode2){
        int numrows=0;
        try {
            String vClause = "";
            if (B_ase.equals("5")) {
                vClause = "ItemCode='" + C_ode + "' and CategoryCode='" + C_ode2 + "' ";
            } else if (B_ase.equals("4")) {
                vClause = "ItemGroup='" + C_ode + "' and CategoryCode='" + C_ode2 + "' ";
            } else if (B_ase.equals("3")) {
                vClause = "CategoryCode='" + C_ode + "' ";
            } else if (B_ase.equals("2")) {
                vClause = "ItemCode='" + C_ode + "' ";
            } else if (B_ase.equals("1")) {
                vClause = "ItemGroup='" + C_ode + "' ";
            }
            String checkMatrix = "select count(*)as numrows " +
                    "from  stk_pricematrix where " + vClause + " and B_ase='" + B_ase + "' and Status='1' ";
            Log.d("COUNT MATRIX", checkMatrix);
            if(DBStatus.equals("1")) {
                Statement stmtMat = conn.createStatement();
                stmtMat.execute(checkMatrix);
                ResultSet rsMat = stmtMat.getResultSet();
                while (rsMat.next()) {
                    numrows = rsMat.getInt(1);
                }
            }else if(DBStatus.equals("0")){
                Cursor rsMat=db.getQuery(checkMatrix);
                while(rsMat.moveToNext()){
                    numrows = rsMat.getInt(0);
                }
            }

            return numrows;
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return numrows;
    }

    private  void getDataMatrix(String B_ase,String C_ode, String C_ode2){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date date    = new Date();
            String vT_ime = sdf.format(date);
            String vClause  = "";
            String Criteria = "";
            Double Pct      = 0.00;
            if(B_ase.equals("5")) {
                vClause = "ItemCode='" + C_ode + "' and CategoryCode='" + C_ode2 + "' ";
            }else if(B_ase.equals("4")) {
                vClause = "ItemGroup='" + C_ode + "' and CategoryCode='" + C_ode2 + "' ";
            }else if(B_ase.equals("3")){
                vClause ="CategoryCode='"+C_ode+"' ";
            }else  if(B_ase.equals("2")){
                vClause ="ItemCode='"+C_ode+"' ";
            }else if(B_ase.equals("1")){
                vClause ="ItemGroup='"+C_ode+"' ";
            }
            String checkMatrix1 = "select ItemCode, Criteria, Pct, " +
                    " PeriodYN, TimeStart, TimeEnd," +
                    " Description, ServiceChargeYN " +
                    " from  stk_pricematrix where "+vClause+" and B_ase='"+B_ase+"' and Status='1' ";
            Log.d("DATA MATRIX",checkMatrix1);
            Double oldUnitPrice = HCDiscount;
            Double newUnitPrice = 0.00;
            String addPct       = "";
            String PeriodYN     = "";
            String vTimeStart   = "";
            String vTimeEnd     ="";
            if(ItemConn.equals("1")){
                Statement stmtMat1 = conn.createStatement();
                stmtMat1.execute(checkMatrix1);
                ResultSet rsMat1    = stmtMat1.getResultSet();
                while (rsMat1.next()) {
                    Criteria        = rsMat1.getString("Criteria");
                    Pct             = rsMat1.getDouble("Pct");
                    PeriodYN        = rsMat1.getString("PeriodYN");
                    vTimeStart      = rsMat1.getString("TimeStart");
                    vTimeEnd        = rsMat1.getString("TimeEnd");
                    ServiceChargeYN = rsMat1.getString("ServiceChargeYN");
                }
            }else if(ItemConn.equals("0")){
                Cursor rsMat1=db.getQuery(checkMatrix1);
                while(rsMat1.moveToNext()){
                    Criteria        = rsMat1.getString(1);
                    Pct             = rsMat1.getDouble(2);
                    PeriodYN        = rsMat1.getString(3);
                    vTimeStart      = rsMat1.getString(4);
                    vTimeEnd        = rsMat1.getString(5);
                    ServiceChargeYN = rsMat1.getString(7);
                }
            }
            if (Criteria.equals("Discount from selling price")) {
                Double newPrice     = UnitPrice * (Pct / 100);
                //UnitPrice           = UnitPrice - newPrice;
                HCDiscount          = newPrice;
                newUnitPrice        = HCDiscount;
                addPct              = String.format(Locale.US, "%,.0f", Pct)+"%";
            } else if (Criteria.equals("Discount from selling price (With Post To GL)")) {
                Double newPrice     = UnitPrice * (Pct / 100);
                //UnitPrice           = UnitPrice - newPrice;
                HCDiscount          = newPrice;
                DisRate1            = Pct;
                newUnitPrice        = HCDiscount;
                addPct              = String.format(Locale.US, "%,.0f", Pct)+"%";
            } else if (Criteria.equals("Exact selling price")) {
                //UnitPrice           = Pct;
                HCDiscount          = Math.abs(Pct-UnitPrice);
                newUnitPrice        = HCDiscount;
                addPct              = String.format(Locale.US, "%,.2f", HCDiscount);
            }else if (Criteria.equals("Markup cost price")) {
                Double newPrice     = UnitCost * (Pct  /100);
                newPrice            = UnitCost + newPrice;
                newUnitPrice        = Math.abs(newPrice-UnitPrice);
                HCDiscount          = newUnitPrice;
                addPct              = String.format(Locale.US, "%,.2f", HCDiscount);
            }
            PriceMatrixDesc     = "["+addPct+"]";
            if(PeriodYN.equals("2")) {
                Date T_ime      =parseDate(vT_ime);
                Date TimeStart  =parseDate(vTimeStart);
                Date TimeEnd    =parseDate(vTimeEnd);
                if( TimeStart.before(T_ime) && TimeEnd.after(T_ime)) {
                    HCDiscount          =newUnitPrice;
                }else{
                    HCDiscount          =oldUnitPrice;
                    PriceMatrixDesc     = "";
                    Log.d("PRICE MATRIX", vT_ime+" not range period time");
                }
            }else{
                HCDiscount           =newUnitPrice;
                // PriceMatrixDesc     = "";
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private Date parseDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return sdf.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }
    private String encodeChar(String txt){
        String newText="";
        try{
            byte[] b=txt.getBytes("ISO-8859-1");
            newText=new String(b,"utf-8");
            return newText;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return newText;
    }
}
