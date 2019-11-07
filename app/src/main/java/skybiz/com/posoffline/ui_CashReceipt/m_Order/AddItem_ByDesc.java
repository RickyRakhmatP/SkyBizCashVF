package skybiz.com.posoffline.ui_CashReceipt.m_Order;

import android.content.ContentValues;
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_NewObject.Encode;
import skybiz.com.posoffline.m_NewObject.GetPoint;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class AddItem_ByDesc extends AsyncTask<Void, Void, String> {
    Context c;
    String ItemCode,Description,vQty,vUnitPrice,vHCDiscount,vDisRate1,isItem;
    String IPAddress, UserName, Password,
            DBName, Port, DBStatus,
            ItemConn, URL, z,
            EncodeType, BranchCode, LocationCode,
            DepartmentCode, GroupByItemYN, vItemGroup,
            vCategoryCode, UserCode, CounterCode,
            PriceMatrixDesc="",ServiceChargeYN="1";
    String deviceId,RunNo;
    Double Qty,UnitPrice,HCTax,
            HCDiscount, HCLineAmt,dQty,
            DisRate1, MSP, UnitCost;
    TelephonyManager telephonyManager;
    Connection conn=null;
    DBAdapter db=null;
    int LineNo;

    public AddItem_ByDesc(Context c, String Description, String vQty) {
        this.c = c;
        this.Description = Description;
        this.vQty = vQty;
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
            ((CashReceipt)c).refreshOrder();
        }
    }
    private String fnadd(){
        try{
            JSONObject jsonReq,jsonRes;
          //  telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
            //deviceId = telephonyManager.getDeviceId();
            db=new DBAdapter(c);
            db.openDB();
            String querySet="select ServerName, UserName, Password, " +
                    "DBName, Port, DBStatus, " +
                    "ItemConn, EncodeType, BranchCode," +
                    "LocationCode, DepartmentCode, GroupByItemYN, " +
                    "UserCode, CounterCode " +
                    "from tb_setting ";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress       = curSet.getString(0);
                UserName        = curSet.getString(1);
                Password        = curSet.getString(2);
                DBName          = curSet.getString(3);
                Port            = curSet.getString(4);
                DBStatus        = curSet.getString(5);
                ItemConn        = curSet.getString(6);
                EncodeType      = curSet.getString(7);
                BranchCode      = curSet.getString(8);
                LocationCode    = curSet.getString(9);
                DepartmentCode  = curSet.getString(10);
                GroupByItemYN   = curSet.getString(11);
                UserCode        = curSet.getString(12);
                CounterCode     = curSet.getString(13);
            }

            String qMember="select CategoryCode from tb_member";
            Cursor rsMember=db.getQuery(qMember);
            while(rsMember.moveToNext()){
                vCategoryCode=rsMember.getString(0);
            }
            String qCheckNo="select LineNo from cloud_cus_inv_dt where ComputerName='"+UserCode+"' Order By RunNo Desc ";
            Cursor rsCheckNo=db.getQuery(qCheckNo);
            while(rsCheckNo.moveToNext()){
                LineNo=rsCheckNo.getInt(0);
            }
            LineNo=LineNo+1;

            dQty=0.00;
            String vCheck="select RunNo, Qty, HCUnitCost, HCDiscount, DisRate1 from cloud_cus_inv_dt where " +
                    " Description='" + Description + "' and ComputerName='" + UserCode + "' ";
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
                        HCDiscount = joCheck.getDouble("HCDiscount");
                        DisRate1 = joCheck.getDouble("DisRate1");
                    }
                }
            }else {
                Cursor curCheck = db.getQuery(vCheck);
                while (curCheck.moveToNext()) {
                    RunNo = curCheck.getString(0);
                    dQty = curCheck.getDouble(1);
                    vUnitPrice = curCheck.getString(2);
                    vHCDiscount = curCheck.getString(3);
                    vDisRate1 = curCheck.getString(4);
                }
            }
            String vData="";
            if(dQty==0.00){
                Qty=1.00;
                vData = addItem(Description, Qty);
            }else{
                Qty=dQty+1.00;
                UnitPrice  = Double.parseDouble(vUnitPrice);
                HCDiscount = Double.parseDouble(vHCDiscount);
                DisRate1   = Double.parseDouble(vDisRate1);
                if(GroupByItemYN.equals("0")){
                    Qty=1.00;
                    vData = addItem(Description, Qty);
                }else{
                    vData = updateItem(RunNo, Description, Qty, UnitPrice, HCDiscount, DisRate1);
                }
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

    private String addItem(String Description, Double Qty){
        String vInsert = "";
        String vUnitPrice="0.00";
        try {
            JSONObject jsonReq,jsonRes;
            Double HCTax, HCDiscount, HCLineAmt, DisRate1, TaxRate1,
                     UOMPrice1, UOMPrice2, UOMPrice3, UOMPrice4,
                    UOMFactor1, UOMFactor2, UOMFactor3, UOMFactor4, FactorQty;
            String DetailTaxCode, ItemCode, DefaultUOM, UOM, UOM1,
                    UOM2, UOM3, UOM4, RetailTaxCode, PurchaseTaxCode,
                    SalesTaxCode;
            HCTax         =0.00;
            HCLineAmt     =0.00;
            UOMPrice1     =0.00;
            UOMPrice2     =0.00;
            UOMPrice3     =0.00;
            UOMPrice4     =0.00;
            UOMFactor1    =0.00;
            UOMFactor2    =0.00;
            UOMFactor3    =0.00;
            UOMFactor4    =0.00;
            FactorQty     =0.00;
            DefaultUOM    ="";
            UOM           ="";
            UOM1          ="";
            UOM2          ="";
            UOM3          ="";
            UOM4          ="";
            RetailTaxCode ="";
            ItemCode      ="";
            String AlternateItem="";
            Double UnitPrice=0.00;
            DisRate1 = 0.00;
            HCDiscount = 0.00;
            String CurCode="";
            String DUD6="";
            String Point="";
            String qPoint="";
            if(DBStatus.equals("1")){
                qPoint="IF(P.DocType = 'Collect', IFNULL(P.Point,0),0) AS POINT ";
            }else{
                qPoint="(CASE WHEN P.DocType = 'Collect' THEN P.Point ELSE '0' END) as POINT ";
            }
            String sql = "Select '' as RunNo,M.DefaultUOM,M.Description,UOM,UOM1," +
                    " M.UOM2,M.UOM3,M.UOM4,IFNULL(M.UOMFactor1,'0') as UOMFactor1,IFNULL(M.UOMFactor2,'0') as UOMFactor2," +
                    " IFNULL(M.UOMFactor3,'0') as UOMFactor3, IFNULL(M.UOMFactor4,'0') as UOMFactor4," +
                    " IFNULL(M.UOMPrice1,'0') as UOMPrice1, IFNULL(M.UOMPrice2,'0') as UOMPrice2, " +
                    " IFNULL(M.UOMPrice3,'0') as UOMPrice3, IFNULL(M.UOMPrice4,'0') UOMPrice4," +
                    " M.RetailTaxCode, M.PurchaseTaxCode, M.SalesTaxCode," +
                    " M.UnitPrice, M.ItemCode, M.AlternateItem," +
                    " IFNULL(M.HCDiscount,0)as HCDiscount, IFNULL(M.DisRate1,0)as DisRate1," +
                    " "+qPoint+" ," +
                    " IFNULL(M.MSP,0)as MSP, IFNULL(M.UnitCost,0)as UnitCost, M.ItemGroup" +
                    " from stk_master M left join ret_point P ON M.ItemCode=P.Item " +
                    " where M.Description='" + Description + "'  Group By M.ItemCode";
            Log.d("QUERY ITEM", sql);
            //DBAdapter db = new DBAdapter(c);
            //db.openDB();
            int ada=0;
            if (DBStatus.equals("0")) {
                Cursor rsData = db.getQuery(sql);
                while (rsData.moveToNext()) {
                    ItemCode = rsData.getString(20);
                    vUnitPrice = rsData.getString(19);
                    vUnitPrice = vUnitPrice.replaceAll(",", "");
                    DefaultUOM = rsData.getString(1);
                    Description = rsData.getString(2);
                    Description = Description.replace("'", "''");
                    UOM = rsData.getString(3);
                    UOM1 = rsData.getString(4);
                    UOM2 = rsData.getString(5);
                    UOM3 = rsData.getString(6);
                    UOM4 = rsData.getString(7);
                    UOMFactor1 = rsData.getDouble(8);
                    UOMFactor2 = rsData.getDouble(9);
                    UOMFactor3 = rsData.getDouble(10);
                    UOMFactor4 = rsData.getDouble(11);
                    UOMPrice1 = rsData.getDouble(12);
                    UOMPrice2 = rsData.getDouble(13);
                    UOMPrice3 = rsData.getDouble(14);
                    UOMPrice4 = rsData.getDouble(15);
                    RetailTaxCode = rsData.getString(16);
                    AlternateItem = rsData.getString(21);
                    HCDiscount = rsData.getDouble(22);
                    DisRate1 = rsData.getDouble(23);
                    Point = GetPoint.Value(c,ItemCode,"Collect");
                    UnitCost = rsData.getDouble(26);
                    vItemGroup = rsData.getString(27);
                    ada++;
                }
            } else if (DBStatus.equals("1")) {
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                conn = Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    Statement stmtItem = conn.createStatement();
                    stmtItem.execute(sql);
                    ResultSet rsItem = stmtItem.getResultSet();
                    while (rsItem.next()) {
                        ItemCode    = rsItem.getString(21);
                        vUnitPrice  = rsItem.getString(20);
                        vUnitPrice  = vUnitPrice.replaceAll(",", "");
                        DefaultUOM  = rsItem.getString(2);
                        Description = Encode.setChar(EncodeType,rsItem.getString(3));
                        Description = Description.replace("'", "''");
                        UOM         = Encode.setChar(EncodeType,rsItem.getString(4));
                        UOM1        = Encode.setChar(EncodeType,rsItem.getString(5));
                        UOM2        = Encode.setChar(EncodeType,rsItem.getString(6));
                        UOM3        = Encode.setChar(EncodeType,rsItem.getString(7));
                        UOM4        = Encode.setChar(EncodeType,rsItem.getString(8));
                        UOMFactor1  = rsItem.getDouble(9);
                        UOMFactor2  = rsItem.getDouble(10);
                        UOMFactor3 = rsItem.getDouble(1);
                        UOMFactor4 = rsItem.getDouble(12);
                        UOMPrice1 = rsItem.getDouble(13);
                        UOMPrice2 = rsItem.getDouble(14);
                        UOMPrice3 = rsItem.getDouble(15);
                        UOMPrice4 = rsItem.getDouble(16);
                        RetailTaxCode = rsItem.getString(17);
                        AlternateItem =  Encode.setChar(EncodeType,rsItem.getString(22));
                        HCDiscount = rsItem.getDouble(23);
                        DisRate1 = rsItem.getDouble(24);
                        Point = rsItem.getString(25);
                        MSP = rsItem.getDouble(26);
                        UnitCost = rsItem.getDouble(27);
                        vItemGroup = rsItem.getString(28);
                        ada++;
                    }
                }
            } else if (DBStatus.equals("2")) {
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", sql);
                jsonReq.put("action", "select");
                String response1 = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response1);
                String response = jsonRes.getString("hasil");
                JSONArray rsData = new JSONArray(response);
                JSONObject vData = null;

                for (int i = 0; i < rsData.length(); i++) {
                    vData           = rsData.getJSONObject(i);
                    ItemCode        = vData.getString("ItemCode");
                    vUnitPrice      = vData.getString("UnitPrice");
                    vUnitPrice      = vUnitPrice.replaceAll(",", "");
                    DefaultUOM      = vData.getString("DefaultUOM");
                    Description     = vData.getString("Description");
                    Description     = Description.replace("'", "''");
                    UOM             = vData.getString("UOM");
                    UOM1            = vData.getString("UOM1");
                    UOM2            = vData.getString("UOM2");
                    UOM3            = vData.getString("UOM3");
                    UOM4            = vData.getString("UOM4");
                    UOMFactor1      = vData.getDouble("UOMFactor1");
                    UOMFactor2      = vData.getDouble("UOMFactor2");
                    UOMFactor3      = vData.getDouble("UOMFactor3");
                    UOMFactor4      = vData.getDouble("UOMFactor4");
                    UOMPrice1       = vData.getDouble("UOMPrice1");
                    UOMPrice2       = vData.getDouble("UOMPrice2");
                    UOMPrice3       = vData.getDouble("UOMPrice3");
                    UOMPrice4       = vData.getDouble("UOMPrice4");
                    RetailTaxCode   = vData.getString("RetailTaxCode");
                    AlternateItem   = vData.getString("AlternateItem");
                    HCDiscount      = vData.getDouble("HCDiscount");
                    DisRate1        = vData.getDouble("DisRate1");
                    Point           = vData.getString("Point");
                    UnitCost        = vData.getDouble("UnitCost");
                    vItemGroup      = vData.getString("ItemGroup");
                    ada++;
                }
            }
            UnitPrice=Double.parseDouble(vUnitPrice);
            if (DefaultUOM.equals("0")) {
                FactorQty = 1.00;
            } else if (DefaultUOM.equals("1")) {
                UnitPrice = UOMPrice1;
                FactorQty = UOMFactor1;
                UOM = UOM1;
            }else if (DefaultUOM.equals("2")) {
                UnitPrice = UOMPrice2;
                FactorQty = UOMFactor2;
                UOM = UOM2;
            }else if (DefaultUOM.equals("3")) {
                UnitPrice = UOMPrice3;
                FactorQty = UOMFactor3;
                UOM = UOM3;
            }else if (DefaultUOM.equals("4")) {
                UnitPrice = UOMPrice4;
                FactorQty = UOMFactor4;
                UOM = UOM4;
            }

            String checkGST="select GSTNo, CurCode from companysetup";
            String GSTYN="";
            Cursor rsGST=db.getQuery(checkGST);
            while(rsGST.moveToNext()){
                GSTYN=rsGST.getString(0);
            }
            if(GSTYN.equals("NO")){
                DetailTaxCode="";
            }else{
                DetailTaxCode = RetailTaxCode;
            }

            if(HCDiscount>0 && DisRate1>0){
                DisRate1=0.00;
                DUD6="Discount by amount "+CurCode+HCDiscount;
            }else if(HCDiscount>0 && DisRate1==0){
                DisRate1=0.00;
                DUD6="Discount by percentage "+DisRate1+"%";
            }else if(HCDiscount==0 && DisRate1>0){
                HCDiscount=0.00;
                DUD6="Discount by amount "+CurCode+HCDiscount;
            }

           /* if(conn!=null){
                String PriceMatrixYN = "0";
                String vmatrix = "select PricingMatrixYN from sys_general_setup2";
                Statement stmtM = conn.createStatement();
                stmtM.execute(vmatrix);
                ResultSet rsM = stmtM.getResultSet();
                while (rsM.next()) {
                    PriceMatrixYN = rsM.getString("PricingMatrixYN");
                }
                if (PriceMatrixYN.equals("1")) {
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
                    Description=Description+" "+PriceMatrixDesc;
                    Log.d("UNITPRICE MATRIX", UnitPrice.toString());
                }
            }*/
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
                Description=Description+" "+PriceMatrixDesc;
            }

            CalculateLineAmt vDataAmt = fncalculate(c, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
            TaxRate1 = vDataAmt.getvR_ate();
            HCDiscount = vDataAmt.getvHCDiscount();
            DisRate1 = vDataAmt.getvDisRate1();
            HCTax = vDataAmt.getvHCTax();
            HCLineAmt = vDataAmt.getvHCLineAmt();
            SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String datedNow = DateCurr.format(date);
            String vSQLInsert = "INSERT INTO cloud_cus_inv_dt (Doc1No, N_o, ItemCode, Description, " +
                    "Qty, FactorQty, UOM, UOMSingular, " +
                    "HCUnitCost, DisRate1, HCDiscount, TaxRate1, " +
                    "HCTax, DetailTaxCode, HCLineAmt, BranchCode, " +
                    "DepartmentCode, ProjectCode, SalesPersonCode, LocationCode, " +
                    "WarrantyDate, BlankLine, DocType, AnalysisCode2," +
                    "ComputerName, SORunNo, AlternateItem,DUD6," +
                    "Point, UnitCost, ServiceChargeYN, LineNo," +
                    "ItemGroup)" +
                    "VALUES(" +
                    " '" + datedNow + "','a', '" + ItemCode + "', '" + Description + "'," +
                    "'" + Qty + "', '" + FactorQty + "', '" + UOM + "', '" + UOM + "'," +
                    " '" + UnitPrice + "', '" + DisRate1 + "', '" + HCDiscount + "', '" + TaxRate1 + "'," +
                    " '" + HCTax + "', '" + DetailTaxCode + "', '" + HCLineAmt + "', '"+BranchCode+"'," +
                    " '"+DepartmentCode+"', 'android', 'android', '"+LocationCode+"'," +
                    " '" + datedNow + "', '0', 'CS', '0', " +
                    " '"+UserCode+"', '0', '"+AlternateItem+"', '"+DUD6+"'," +
                    " '"+Point+"', '"+UnitCost+"', '"+ServiceChargeYN+"', '"+LineNo+"'," +
                    " '"+vItemGroup+"')";
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
            String result="";
            if(ada>0){
                result="success";
            }else{
                result="error";
            }
            //db.closeDB();
            return result;
        }catch (SQLException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return vInsert;
    }


    //update item new

    private String updateItem(String RunNo ,String Description, Double Qty, Double UnitPrice ,Double HCDiscount,Double DisRate1){
        Double HCTax, HCLineAmt,TaxRate1,UOMPrice1,UOMPrice2,UOMPrice3,UOMPrice4,UOMFactor1,UOMFactor2,UOMFactor3,UOMFactor4,FactorQty;
        String DetailTaxCode,ItemCode,DefaultUOM,UOM,UOM1,UOM2,UOM3,UOM4,RetailTaxCode,PurchaseTaxCode,SalesTaxCode;
        HCTax         =0.00;
        HCLineAmt     =0.00;
        UOMPrice1     =0.00;
        UOMPrice2     =0.00;
        UOMPrice3     =0.00;
        UOMPrice4     =0.00;
        UOMFactor1    =0.00;
        UOMFactor2    =0.00;
        UOMFactor3    =0.00;
        UOMFactor4    =0.00;
        DefaultUOM    ="";
        UOM           ="";
        UOM1          ="";
        UOM2          ="";
        UOM3          ="";
        UOM4          ="";
        RetailTaxCode ="";
        ItemCode      ="";
        String vUpdate="";
        JSONObject jsonReq,jsonRes;
        try{
            String sql="Select IFNULL(UnitPrice,0) as UnitPrice, DefaultUOM, Description," +
                    " UOM,UOM1,UOM2," +
                    " UOM3, UOM4, IFNULL(UOMFactor1,0) as UOMFactor1," +
                    " IFNULL(UOMFactor2,0) as UOMFactor2, IFNULL(UOMFactor3,0) as UOMFactor3, IFNULL(UOMFactor4,'0') as UOMFactor4, " +
                    " IFNULL(UOMPrice1,'0') as UOMPrice1, IFNULL(UOMPrice2,0) as UOMPrice2, IFNULL(UOMPrice3,'0') as UOMPrice3," +
                    " IFNULL(UOMPrice4,'0') UOMPrice4, RetailTaxCode, PurchaseTaxCode, " +
                    " SalesTaxCode, ItemCode, IFNULL(MSP,0)as MSP," +
                    " IFNULL(UnitCost,0)as UnitCost " +
                    " from stk_master where Description='"+Description+"' ";
           // DBAdapter db=new DBAdapter(c);
            //db.openDB();
            if(DBStatus.equals("0")){
                Cursor rsData=db.getQuery(sql);
                while(rsData.moveToNext()){
                    DefaultUOM      = rsData.getString(1);
                    Description     = rsData.getString(2);
                    Description     =Description.replace("'","''");
                    UOM             = rsData.getString(3);
                    UOM1            = rsData.getString(4);
                    UOM2            = rsData.getString(5);
                    UOM3            = rsData.getString(6);
                    UOM4            = rsData.getString(7);
                    UOMFactor1      = rsData.getDouble(8);
                    UOMFactor2      = rsData.getDouble(9);
                    UOMFactor3      = rsData.getDouble(10);
                    UOMFactor4      = rsData.getDouble(11);
                    UOMPrice1       = rsData.getDouble(12);
                    UOMPrice2       = rsData.getDouble(13);
                    UOMPrice3       = rsData.getDouble(14);
                    UOMPrice4       = rsData.getDouble(15);
                    RetailTaxCode   = rsData.getString(16);
                    ItemCode        = rsData.getString(19);
                }
            }else if(DBStatus.equals("1")){
                URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
                Connection conn= Connector.connect(URL, UserName, Password);
                //Connection conn= Connect_db.getConnection();
                if (conn != null) {
                    Statement stmtItem = conn.createStatement();
                    stmtItem.execute(sql);
                    ResultSet rsItem = stmtItem.getResultSet();
                    while (rsItem.next()) {
                        DefaultUOM = rsItem.getString(2);
                        Description = rsItem.getString(3);
                        Description = Description.replace("'", "''");
                        UOM = rsItem.getString(4);
                        UOM1 = rsItem.getString(5);
                        UOM2 = rsItem.getString(6);
                        UOM3 = rsItem.getString(7);
                        UOM4 = rsItem.getString(8);
                        UOMFactor1 = rsItem.getDouble(9);
                        UOMFactor2 = rsItem.getDouble(10);
                        UOMFactor3 = rsItem.getDouble(11);
                        UOMFactor4 = rsItem.getDouble(12);
                        UOMPrice1 = rsItem.getDouble(13);
                        UOMPrice2 = rsItem.getDouble(14);
                        UOMPrice3 = rsItem.getDouble(15);
                        UOMPrice4 = rsItem.getDouble(16);
                        RetailTaxCode = rsItem.getString(17);
                        ItemCode = rsItem.getString(20);
                        MSP = rsItem.getDouble(21);
                        UnitCost = rsItem.getDouble(22);
                        vItemGroup = rsItem.getString(23);
                    }
                }
            }else if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", sql);
                jsonReq.put("action", "select");
                String response1 = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response1);
                String response = jsonRes.getString("hasil");
                JSONArray rsData = new JSONArray(response);
                JSONObject vData = null;
                for (int i = 0; i < rsData.length(); i++) {
                    vData           = rsData.getJSONObject(i);
                    DefaultUOM      = vData.getString("DefaultUOM");
                    Description     = vData.getString("Description");
                    Description     = Description.replace("'", "''");
                    UOM             = vData.getString("UOM");
                    UOM1            = vData.getString("UOM1");
                    UOM2            = vData.getString("UOM2");
                    UOM3            = vData.getString("UOM3");
                    UOM4            = vData.getString("UOM4");
                    UOMFactor1      = vData.getDouble("UOMFactor1");
                    UOMFactor2      = vData.getDouble("UOMFactor2");
                    UOMFactor3      = vData.getDouble("UOMFactor3");
                    UOMFactor4      = vData.getDouble("UOMFactor4");
                    UOMPrice1       = vData.getDouble("UOMPrice1");
                    UOMPrice2       = vData.getDouble("UOMPrice2");
                    UOMPrice3       = vData.getDouble("UOMPrice3");
                    UOMPrice4       = vData.getDouble("UOMPrice4");
                    RetailTaxCode   = vData.getString("RetailTaxCode");
                }
            }
           /* if (DefaultUOM.equals("0")) {
                //UnitPrice   =UnitPrice;
                FactorQty = 1.00;
                //UOM         =UOM;
            } else if (DefaultUOM.equals("1")) {
                UnitPrice = UOMPrice1;
                FactorQty = UOMFactor1;
                UOM = UOM1;
            } else if (DefaultUOM.equals("2")) {
                UnitPrice = UOMPrice2;
                FactorQty = UOMFactor2;
                UOM = UOM2;
            } else if (DefaultUOM.equals("3")) {
                UnitPrice = UOMPrice3;
                FactorQty = UOMFactor3;
                UOM = UOM3;
            } else if (DefaultUOM.equals("4")) {
                UnitPrice = UOMPrice4;
                FactorQty = UOMFactor4;
                UOM = UOM4;
            }*/

            String checkGST="select GSTNo from companysetup";
            String GSTYN="";
            Cursor rsGST=db.getQuery(checkGST);
            while(rsGST.moveToNext()){
                GSTYN=rsGST.getString(0);
            }
            if(GSTYN.equals("NO")){
                DetailTaxCode="";
            }else{
                DetailTaxCode = RetailTaxCode;
            }
            CalculateLineAmt vDataAmt = fncalculate(c, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
            TaxRate1 = vDataAmt.getvR_ate();
            DisRate1 = vDataAmt.getvDisRate1();
            HCDiscount = vDataAmt.getvHCDiscount();
            HCTax = vDataAmt.getvHCTax();
            HCLineAmt = vDataAmt.getvHCLineAmt();
            String result="";
            String vSQLUpdate = "UPDATE cloud_cus_inv_dt SET Qty='" + Qty + "'," +
                    " HCUnitCost='" + UnitPrice + "', DisRate1='" + DisRate1 + "'," +
                    " HCDiscount='" + HCDiscount + "', HCTax='" + HCTax + "', " +
                    " HCLineAmt='" + HCLineAmt + "' " +
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
                result="success";
            }else {
                db.updateQuery(vSQLUpdate);
                result="success";
            }
            //db.closeDB();
            return result;
        }catch (SQLException e){
            e.printStackTrace();
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
                DisRate1        =vDisRate1;
            }else{
                vHCDiscount	    =0.00;
                DisRate1	    =0.00;
                vAmountB4Tax	 =(Qty*HCUnitCost)-vHCDiscount;
            }
        }
        //URL = "jdbc:mysql://" + IPAddress + "/" + DBName;

        if(DetailTaxCode.isEmpty()) {
            vHCLineAmt   = vAmountB4Tax;
            vR_ate       = 0.00;
            vHCTax       = 0.00;
        }else{
            vHCLineAmt   = vAmountB4Tax;
            String strSQL = "select R_ate,TaxType from stk_tax where TaxCode='" + DetailTaxCode + "' ";
            DBAdapter db = new DBAdapter(context);
            db.openDB();
            Cursor resultSet2 = db.getQuery(strSQL);
            while (resultSet2.moveToNext()) {
                vR_ate      = resultSet2.getDouble(0);
                //vR_ate      =0.00;
                vTaxType    = resultSet2.getString(1);
                Log.d("RESULT", "taxtype: " + vTaxType);
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
    //add new item
    /*private String fnadditem(String ItemCode, Double Qty, String deviceId){
        Double HCTax, HCDiscount, HCLineAmt, DisRate1,TaxRate1,UnitPrice,UOMPrice1,UOMPrice2,UOMPrice3,UOMPrice4,UOMFactor1,UOMFactor2,UOMFactor3,UOMFactor4,FactorQty;
        String DetailTaxCode,Description,DefaultUOM,UOM,UOM1,UOM2,UOM3,UOM4,RetailTaxCode,PurchaseTaxCode,SalesTaxCode;
        HCTax         =0.00;
        HCDiscount    =0.00;
        HCLineAmt     =0.00;
        UnitPrice     =0.00;
        UOMPrice1     =0.00;
        UOMPrice2     =0.00;
        UOMPrice3     =0.00;
        UOMPrice4     =0.00;
        UOMFactor1    =0.00;
        UOMFactor2    =0.00;
        UOMFactor3    =0.00;
        UOMFactor4    =0.00;
        FactorQty     =0.00;
        DefaultUOM    ="";
        UOM           ="";
        UOM1          ="";
        UOM2          ="";
        UOM3          ="";
        UOM4          ="";
        RetailTaxCode ="";
        Description   ="";
        String vItemCode="";
        String vUnitPrice="";
        String sql="Select RunNo,DefaultUOM,Description,UOM,UOM1,UOM2,UOM3,UOM4,ROUND(UOMFactor1,2) as UOMFactor1," +
                "ROUND(UOMFactor2,2) as UOMFactor2, ROUND(UOMFactor3,2) as UOMFactor3, ROUND(UOMFactor4,2) as UOMFactor4," +
                " ROUND(UOMPrice1,2) as UOMPrice1, ROUND(UOMPrice2,2) as UOMPrice2, ROUND(UOMPrice3,2) as UOMPrice3," +
                " ROUND(UOMPrice4,2) UOMPrice4,RetailTaxCode,PurchaseTaxCode, SalesTaxCode,UnitPrice, ItemCode" +
                " from stk_master where ItemCode='"+ItemCode+"'  ";
        DBAdapter db = new DBAdapter(c);
        db.openDB();
        Cursor resultSet = db.getQuery(sql);
        while (resultSet.moveToNext()) {
            vItemCode       =resultSet.getString(20);
            vUnitPrice       = resultSet.getString(19);
            vUnitPrice        =vUnitPrice.replaceAll(",","");
            DefaultUOM      = resultSet.getString(1);
            Description     = resultSet.getString(2);
            Description     =Description.replace("'","''");
            UOM             = resultSet.getString(3);
            UOM1            = resultSet.getString(4);
            UOM2            = resultSet.getString(5);
            UOM3            = resultSet.getString(6);
            UOM4            = resultSet.getString(7);
            UOMFactor1      = resultSet.getDouble(8);
            UOMFactor2      = resultSet.getDouble(9);
            UOMFactor3      = resultSet.getDouble(10);
            UOMFactor4      = resultSet.getDouble(11);
            UOMPrice1       = resultSet.getDouble(12);
            UOMPrice2       = resultSet.getDouble(13);
            UOMPrice3       = resultSet.getDouble(14);
            UOMPrice4       = resultSet.getDouble(15);
            RetailTaxCode   = resultSet.getString(16);

        }
        UnitPrice=Double.parseDouble(vUnitPrice);
        if (DefaultUOM.equals("0")) {
            FactorQty = 1.00;
        } else if (DefaultUOM.equals("1")) {
            UnitPrice = UOMPrice1;
            FactorQty = UOMFactor1;
            UOM = UOM1;
        }else if (DefaultUOM.equals("2")) {
            UnitPrice = UOMPrice2;
            FactorQty = UOMFactor2;
            UOM = UOM2;
        }else if (DefaultUOM.equals("3")) {
            UnitPrice = UOMPrice3;
            FactorQty = UOMFactor3;
            UOM = UOM3;
        }else if (DefaultUOM.equals("4")) {
            UnitPrice = UOMPrice4;
            FactorQty = UOMFactor4;
            UOM = UOM4;
        }

        DisRate1 = 0.00;
        HCDiscount = 0.00;
        String checkGST="select GSTNo from companysetup";
        String GSTYN="";
        Cursor rsGST=db.getQuery(checkGST);
        while(rsGST.moveToNext()){
            GSTYN=rsGST.getString(0);
        }
        if(GSTYN.equals("NO")){
            DetailTaxCode="";
        }else{
            DetailTaxCode = RetailTaxCode;
        }
        CalculateLineAmt vDataAmt = fncalculate(c, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
        TaxRate1 = vDataAmt.getvR_ate();
        DisRate1 = vDataAmt.getvDisRate1();
        HCTax = vDataAmt.getvHCTax();
        HCLineAmt = vDataAmt.getvHCLineAmt();
        SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String datedNow = DateCurr.format(date);
        String vSQLInsert = "INSERT INTO cloud_cus_inv_dt (Doc1No, N_o, ItemCode, Description, Qty, FactorQty, UOM, UOMSingular," +
                "HCUnitCost, DisRate1, HCDiscount, TaxRate1, HCTax, DetailTaxCode, HCLineAmt," +
                "BranchCode, DepartmentCode, ProjectCode, SalesPersonCode, LocationCode, WarrantyDate, BlankLine, DocType, AnalysisCode2, ComputerName, SORunNo)" +
                "VALUES(" +
                " '" + datedNow + "','a','" + vItemCode + "','" + Description + "','" + Qty + "','" + FactorQty + "','" + UOM + "','" + UOM + "'," +
                " '" + UnitPrice + "','" + DisRate1 + "','" + HCDiscount + "','" + TaxRate1 + "','" + HCTax + "','" + DetailTaxCode + "','" + HCLineAmt + "'," +
                " 'android','android','android','android','android'," +
                " '" + datedNow + "', '0', 'CS', '0', '"+deviceId+"', '0')";
        db.addQuery(vSQLInsert);
        db.closeDB();
        return vSQLInsert;
        //return new FnAddItem(HCLineAmt, HCTax, HCDiscount, Qty, ItemCode, Description);
    }



    //update item
    public String fnupdateitem(String RunNo ,String ItemCode, Double Qty, Double UnitPrice ,Double HCDiscount,Double DisRate1,String deviceId){
        Double HCTax, HCLineAmt,TaxRate1,UOMPrice1,UOMPrice2,UOMPrice3,UOMPrice4,UOMFactor1,UOMFactor2,UOMFactor3,UOMFactor4,FactorQty;
        String DetailTaxCode,Description,DefaultUOM,UOM,UOM1,UOM2,UOM3,UOM4,RetailTaxCode,PurchaseTaxCode,SalesTaxCode;
        HCTax         =0.00;
        HCLineAmt     =0.00;
        UOMPrice1     =0.00;
        UOMPrice2     =0.00;
        UOMPrice3     =0.00;
        UOMPrice4     =0.00;
        UOMFactor1    =0.00;
        UOMFactor2    =0.00;
        UOMFactor3    =0.00;
        UOMFactor4    =0.00;
        DefaultUOM    ="";
        UOM           ="";
        UOM1          ="";
        UOM2          ="";
        UOM3          ="";
        UOM4          ="";
        RetailTaxCode ="";
        Description   ="";

        String sql="Select ROUND(UnitPrice,2) as UnitPrice,DefaultUOM,Description,UOM,UOM1,UOM2,UOM3,UOM4," +
                "ROUND(UOMFactor1,2) as UOMFactor1,ROUND(UOMFactor2,2) as UOMFactor2, ROUND(UOMFactor3,2) as UOMFactor3," +
                " ROUND(UOMFactor4,2) as UOMFactor4," +
                " ROUND(UOMPrice1,2) as UOMPrice1, ROUND(UOMPrice2,2) as UOMPrice2, ROUND(UOMPrice3,2) as UOMPrice3," +
                " ROUND(UOMPrice4,2) UOMPrice4,RetailTaxCode,PurchaseTaxCode, SalesTaxCode " +
                "from stk_master where ItemCode='"+ItemCode+"'  ";
        DBAdapter db = new DBAdapter(c);
        db.openDB();
        Cursor resultSet = db.getQuery(sql);
        while (resultSet.moveToNext()) {
            DefaultUOM      = resultSet.getString(1);
            Description     = resultSet.getString(2);
            Description     =Description.replace("'","''");
            UOM             = resultSet.getString(3);
            UOM1            = resultSet.getString(4);
            UOM2            = resultSet.getString(5);
            UOM3            = resultSet.getString(6);
            UOM4            = resultSet.getString(7);
            UOMFactor1      = resultSet.getDouble(8);
            UOMFactor2      = resultSet.getDouble(9);
            UOMFactor3      = resultSet.getDouble(10);
            UOMFactor4      = resultSet.getDouble(11);
            UOMPrice1       = resultSet.getDouble(12);
            UOMPrice2       = resultSet.getDouble(13);
            UOMPrice3       = resultSet.getDouble(14);
            UOMPrice4       = resultSet.getDouble(15);
            RetailTaxCode   = resultSet.getString(16);
        }
        if (DefaultUOM.equals("0")) {
            //UnitPrice   =UnitPrice;
            FactorQty = 1.00;
            //UOM         =UOM;
        } else if (DefaultUOM.equals("1")) {
            UnitPrice = UOMPrice1;
            FactorQty = UOMFactor1;
            UOM = UOM1;
        } else if (DefaultUOM.equals("2")) {
            UnitPrice = UOMPrice2;
            FactorQty = UOMFactor2;
            UOM = UOM2;
        } else if (DefaultUOM.equals("3")) {
            UnitPrice = UOMPrice3;
            FactorQty = UOMFactor3;
            UOM = UOM3;
        } else if (DefaultUOM.equals("4")) {
            UnitPrice = UOMPrice4;
            FactorQty = UOMFactor4;
            UOM = UOM4;
        }

        String checkGST="select GSTNo from companysetup";
        String GSTYN="";
        Cursor rsGST=db.getQuery(checkGST);
        while(rsGST.moveToNext()){
            GSTYN=rsGST.getString(0);
        }
        if(GSTYN.equals("NO")){
            DetailTaxCode="";
        }else{
            DetailTaxCode = RetailTaxCode;
        }
        CalculateLineAmt vDataAmt = fncalculate(c, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
        TaxRate1 = vDataAmt.getvR_ate();
        DisRate1 = vDataAmt.getvDisRate1();
        HCDiscount=vDataAmt.getvHCDiscount();
        HCTax = vDataAmt.getvHCTax();
        HCLineAmt = vDataAmt.getvHCLineAmt();
        SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String datedNow = DateCurr.format(date);
        //String vSQLUpdate ="UPDATE cloud_cus_inv_dt SET Qty='"+Qty+"', HCUnitCost='"+UnitPrice+"', DisRate1='"+DisRate1+"', HCDiscount='"+HCDiscount+"', HCTax='"+HCTax+"', HCLineAmt='"+HCLineAmt+"' where ItemCode='"+ItemCode+"' and ComputerName='"+deviceId+"'";
        ContentValues cv=new ContentValues();
        cv.put("Qty", Qty);
        cv.put("HCUnitCost", UnitPrice);
        cv.put("DisRate1", DisRate1);
        cv.put("HCDiscount", HCDiscount);
        cv.put("HCTax", HCTax);
        cv.put("HCLineAmt", HCLineAmt);
        db.UpdateCloud(RunNo,cv);
       return RunNo;
    }


    //add new item
    public String fnadditem1(String ItemCode, Double Qty, String deviceId){
        Double HCTax, HCDiscount, HCLineAmt, DisRate1,TaxRate1,UnitPrice,UOMPrice1,UOMPrice2,UOMPrice3,UOMPrice4,UOMFactor1,UOMFactor2,UOMFactor3,UOMFactor4,FactorQty;
        String DetailTaxCode,Description,DefaultUOM,UOM,UOM1,UOM2,UOM3,UOM4,RetailTaxCode,PurchaseTaxCode,SalesTaxCode;
        HCTax         =0.00;
        HCDiscount    =0.00;
        HCLineAmt     =0.00;
        UnitPrice     =0.00;
        UOMPrice1     =0.00;
        UOMPrice2     =0.00;
        UOMPrice3     =0.00;
        UOMPrice4     =0.00;
        UOMFactor1    =0.00;
        UOMFactor2    =0.00;
        UOMFactor3    =0.00;
        UOMFactor4    =0.00;
        FactorQty     =0.00;
        DefaultUOM    ="";
        UOM           ="";
        UOM1          ="";
        UOM2          ="";
        UOM3          ="";
        UOM4          ="";
        RetailTaxCode ="";
        Description   ="";
        String vUnitPrice="0";
        String vItemCode="";
        try {
            DBAdapter db = new DBAdapter(c);
            db.openDB();
            URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
            Connection conn= Connector.connect(URL, UserName, Password);
            if (conn != null) {
                String sql = "Select DefaultUOM,Description,UOM,UOM1,UOM2,UOM3,UOM4,ROUND(UOMFactor1,2) as UOMFactor1," +
                        "ROUND(UOMFactor2,2) as UOMFactor2, ROUND(UOMFactor3,2) as UOMFactor3, ROUND(UOMFactor4,2) as UOMFactor4," +
                        " ROUND(UOMPrice1,2) as UOMPrice1, ROUND(UOMPrice2,2) as UOMPrice2, ROUND(UOMPrice3,2) as UOMPrice3," +
                        " ROUND(UOMPrice4,2) UOMPrice4,RetailTaxCode,PurchaseTaxCode, SalesTaxCode,UnitPrice " +
                        "from stk_master where ItemCode='" + ItemCode + "'  ";
                Statement stmtItem = conn.createStatement();
                stmtItem.execute(sql);
                ResultSet rsItem = stmtItem.getResultSet();
                while (rsItem.next()) {
                    vItemCode = rsItem.getString(20);
                    vUnitPrice = rsItem.getString(19);
                    vUnitPrice = vUnitPrice.replaceAll(",", "");
                    DefaultUOM = rsItem.getString(1);
                    Description = rsItem.getString(2);
                    Description = Description.replace("'", "''");
                    UOM = rsItem.getString(3);
                    UOM1 = rsItem.getString(4);
                    UOM2 = rsItem.getString(5);
                    UOM3 = rsItem.getString(6);
                    UOM4 = rsItem.getString(7);
                    UOMFactor1 = rsItem.getDouble(8);
                    UOMFactor2 = rsItem.getDouble(9);
                    UOMFactor3 = rsItem.getDouble(10);
                    UOMFactor4 = rsItem.getDouble(11);
                    UOMPrice1 = rsItem.getDouble(12);
                    UOMPrice2 = rsItem.getDouble(13);
                    UOMPrice3 = rsItem.getDouble(14);
                    UOMPrice4 = rsItem.getDouble(15);
                    RetailTaxCode = rsItem.getString(16);
                }
                UnitPrice = Double.parseDouble(vUnitPrice);
                if (DefaultUOM.equals("0")) {
                    FactorQty = 1.00;
                } else if (DefaultUOM.equals("1")) {
                    UnitPrice = UOMPrice1;
                    FactorQty = UOMFactor1;
                    UOM = UOM1;
                } else if (DefaultUOM.equals("2")) {
                    UnitPrice = UOMPrice2;
                    FactorQty = UOMFactor2;
                    UOM = UOM2;
                } else if (DefaultUOM.equals("3")) {
                    UnitPrice = UOMPrice3;
                    FactorQty = UOMFactor3;
                    UOM = UOM3;
                } else if (DefaultUOM.equals("4")) {
                    UnitPrice = UOMPrice4;
                    FactorQty = UOMFactor4;
                    UOM = UOM4;
                }

                DisRate1 = 0.00;
                HCDiscount = 0.00;
                DetailTaxCode = RetailTaxCode;
                CalculateLineAmt vDataAmt = fncalculate1(URL,UserName,Password, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
                TaxRate1 = vDataAmt.getvR_ate();
                DisRate1 = vDataAmt.getvDisRate1();
                HCTax = vDataAmt.getvHCTax();
                HCLineAmt = vDataAmt.getvHCLineAmt();
                SimpleDateFormat DateCurr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String datedNow = DateCurr.format(date);
                String vSQLInsert = "INSERT INTO cloud_cus_inv_dt (Doc1No, N_o, ItemCode, Description, Qty, FactorQty, UOM, UOMSingular," +
                        "HCUnitCost, DisRate1, HCDiscount, TaxRate1, HCTax, DetailTaxCode, HCLineAmt," +
                        "BranchCode, DepartmentCode, ProjectCode, SalesPersonCode, LocationCode, WarrantyDate, BlankLine, DocType, AnalysisCode2, ComputerName, SORunNo)" +
                        "VALUES(" +
                        " '" + datedNow + "','a','" + vItemCode + "','" + Description + "','" + Qty + "','" + FactorQty + "','" + UOM + "','" + UOM + "'," +
                        " '" + UnitPrice + "','" + DisRate1 + "','" + HCDiscount + "','" + TaxRate1 + "','" + HCTax + "','" + DetailTaxCode + "','" + HCLineAmt + "'," +
                        " 'android','android','android','android','android'," +
                        " '" + datedNow + "', '0', 'CS', '0', '" + deviceId + "', '0')";
                Log.d("INSERT",vSQLInsert);
                db.addQuery(vSQLInsert);
                db.closeDB();
                return vSQLInsert;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
        //return new FnAddItem(HCLineAmt, HCTax, HCDiscount, Qty, ItemCode, Description);
    }
    public static CalculateLineAmt fncalculate1(String URL, String UserName, String Password,
                                                Double Qty, Double HCUnitCost, Double DisRate1, Double HCDiscount, String DetailTaxCode) {
        Double vR_ate,vHCTax,vAmountB4Tax,vTempAmt,vHCLineAmt,vDisRate1,vHCDiscount;
        String vTaxType;
        vR_ate      =0.00;
        vHCTax      =0.00;
        vHCLineAmt  =0.00;
        vHCDiscount =HCDiscount;
        vDisRate1   =DisRate1;
        try {
            if(DisRate1>0){
                vDisRate1		=DisRate1/100;
                vHCDiscount	    =Qty*HCUnitCost*vDisRate1;
                vAmountB4Tax	=(Qty*HCUnitCost)-vHCDiscount;
            }else{
                if(HCDiscount>0){
                    vDisRate1	    =(HCDiscount*100)/(Qty*HCUnitCost);
                    vHCDiscount	    = HCDiscount;
                    vAmountB4Tax	 =(Qty*HCUnitCost)-vHCDiscount;
                }else{
                    vHCDiscount	    =0.00;
                    vDisRate1	    =0.00;
                    vAmountB4Tax	 =(Qty*HCUnitCost)-vHCDiscount;
                }
            }
            Log.d("RESULT", "DISC: " + vHCDiscount.toString());
            Connection conn = Connector.connect(URL, UserName, Password);
            if (conn == null) {

            }else {
                if (DetailTaxCode.isEmpty()) {
                    vHCLineAmt = vAmountB4Tax;
                    vR_ate = 0.00;
                    vHCTax = 0.00;
                } else {
                    String strSQL = "select ROUND(R_ate,2) as R_ate,TaxType from stk_tax where TaxCode='" + DetailTaxCode + "' ";
                    Statement statement2 = conn.createStatement();
                    if (statement2.execute(strSQL)) {
                        ResultSet resultSet2 = statement2.getResultSet();
                        while (resultSet2.next()) {
                            vR_ate = Double.parseDouble(resultSet2.getString("R_ate"));
                            vTaxType = resultSet2.getString("TaxType");
                            Log.d("RESULT", "taxtype: " + vTaxType);
                            //calculate tax
                            if (vTaxType.equals("0") || vTaxType.equals("2")) {
                                vHCTax = vAmountB4Tax * (vR_ate / (vR_ate + 100));
                                vTempAmt = vAmountB4Tax - vHCTax;
                                vHCLineAmt = vTempAmt + vHCTax;
                            } else if (vTaxType.equals("1") || vTaxType.equals("3")) {
                                vHCTax = vAmountB4Tax * (vR_ate / 100);
                                vHCLineAmt = vAmountB4Tax + vHCTax;
                            }
                        }
                    }
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return new CalculateLineAmt(DisRate1, vHCDiscount, vR_ate, vHCTax, vHCLineAmt);
    }


    public String fnupdateitem1(String RunNo , String ItemCode, Double Qty, Double UnitPrice,
                                Double HCDiscount,Double DisRate1,String deviceId){
        Double HCTax, HCLineAmt,TaxRate1,UOMPrice1,UOMPrice2,UOMPrice3,UOMPrice4,UOMFactor1,UOMFactor2,UOMFactor3,UOMFactor4,FactorQty;
        String DetailTaxCode,Description,DefaultUOM,UOM,UOM1,UOM2,UOM3,UOM4,RetailTaxCode,PurchaseTaxCode,SalesTaxCode;
        HCTax         =0.00;
        HCLineAmt     =0.00;
        UOMPrice1     =0.00;
        UOMPrice2     =0.00;
        UOMPrice3     =0.00;
        UOMPrice4     =0.00;
        UOMFactor1    =0.00;
        UOMFactor2    =0.00;
        UOMFactor3    =0.00;
        UOMFactor4    =0.00;
        DefaultUOM    ="";
        UOM           ="";
        UOM1          ="";
        UOM2          ="";
        UOM3          ="";
        UOM4          ="";
        RetailTaxCode ="";
        Description   ="";
        try{
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            URL = "jdbc:mysql://" + IPAddress +":"+Port+ "/" + DBName+"?useUnicode=yes&characterEncoding=ISO-8859-1";
            Connection conn= Connector.connect(URL, UserName, Password);
            if (conn != null) {
                String sql = "Select DefaultUOM,Description,UOM,UOM1," +
                        " UOM2,UOM3,UOM4,ROUND(UOMFactor1,2) as UOMFactor1,ROUND(UOMFactor2,2) as UOMFactor2, " +
                        " ROUND(UOMFactor3,2) as UOMFactor3, ROUND(UOMFactor4,2) as UOMFactor4," +
                        " ROUND(UOMPrice1,2) as UOMPrice1, ROUND(UOMPrice2,2) as UOMPrice2, " +
                        " ROUND(UOMPrice3,2) as UOMPrice3, ROUND(UOMPrice4,2) UOMPrice4," +
                        " RetailTaxCode,PurchaseTaxCode, SalesTaxCode,UnitPrice" +
                        " from stk_master where ItemCode='" + ItemCode + "' ";
                Statement stmtItem = conn.createStatement();
                stmtItem.execute(sql);
                ResultSet rsItem = stmtItem.getResultSet();
                while (rsItem.next()) {
                        DefaultUOM = rsItem.getString(1);
                        Description = rsItem.getString(2);
                        Description = Description.replace("'", "''");
                        UOM = rsItem.getString(3);
                        UOM1 = rsItem.getString(4);
                        UOM2 = rsItem.getString(5);
                        UOM3 = rsItem.getString(6);
                        UOM4 = rsItem.getString(7);
                        UOMFactor1 = rsItem.getDouble(8);
                        UOMFactor2 = rsItem.getDouble(9);
                        UOMFactor3 = rsItem.getDouble(10);
                        UOMFactor4 = rsItem.getDouble(11);
                        UOMPrice1 = rsItem.getDouble(12);
                        UOMPrice2 = rsItem.getDouble(13);
                        UOMPrice3 = rsItem.getDouble(14);
                        UOMPrice4 = rsItem.getDouble(15);
                        RetailTaxCode = rsItem.getString(16);
                    }
                    if (DefaultUOM.equals("0")) {
                        //UnitPrice   =UnitPrice;
                        FactorQty = 1.00;
                        //UOM         =UOM;
                    } else if (DefaultUOM.equals("1")) {
                        UnitPrice = UOMPrice1;
                        FactorQty = UOMFactor1;
                        UOM = UOM1;
                    } else if (DefaultUOM.equals("2")) {
                        UnitPrice = UOMPrice2;
                        FactorQty = UOMFactor2;
                        UOM = UOM2;
                    } else if (DefaultUOM.equals("3")) {
                        UnitPrice = UOMPrice3;
                        FactorQty = UOMFactor3;
                        UOM = UOM3;
                    } else if (DefaultUOM.equals("4")) {
                        UnitPrice = UOMPrice4;
                        FactorQty = UOMFactor4;
                        UOM = UOM4;
                    }

                    String checkGST="select GSTNo from companysetup";
                    String GSTYN="";
                    Cursor rsGST=db.getQuery(checkGST);
                    while(rsGST.moveToNext()){
                        GSTYN=rsGST.getString(0);
                    }
                    if(GSTYN.equals("NO")){
                        DetailTaxCode="";
                    }else{
                        DetailTaxCode = RetailTaxCode;
                    }
                    //CalculateLineAmt vDataAmt = fncalculate1(c, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
                    CalculateLineAmt vDataAmt = fncalculate1(URL, UserName, Password, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
                    TaxRate1 = vDataAmt.getvR_ate();
                    DisRate1 = vDataAmt.getvDisRate1();
                    HCDiscount = vDataAmt.getvHCDiscount();
                    HCTax = vDataAmt.getvHCTax();
                    HCLineAmt = vDataAmt.getvHCLineAmt();
                    String vSQLUpdate = "UPDATE cloud_cus_inv_dt SET Qty='" + Qty + "'," +
                            " HCUnitCost='" + UnitPrice + "', DisRate1='" + DisRate1 + "'," +
                            " HCDiscount='" + HCDiscount + "', HCTax='" + HCTax + "', " +
                            " HCLineAmt='" + HCLineAmt + "' " +
                            " where RunNo ='" + RunNo + "' ";
                    db.updateQuery(vSQLUpdate);
                    db.closeDB();
                    stmtItem.close();
                    Log.d("UPDATE",vSQLUpdate);
                return vSQLUpdate;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
      return null;
    }*/

}
