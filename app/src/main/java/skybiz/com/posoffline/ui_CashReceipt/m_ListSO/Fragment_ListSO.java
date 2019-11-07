package skybiz.com.posoffline.ui_CashReceipt.m_ListSO;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.m_NewObject.Encode;
import skybiz.com.posoffline.m_NewObject.EncodeChar;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_SO;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.ScanTable;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 17/01/2018.
 */

public class Fragment_ListSO extends DialogFragment {

    View view;
    private GridLayoutManager lLayout;
    RecyclerView rv;
    LinearLayout lnScan;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_listso, container, false);
        lnScan=(LinearLayout)view.findViewById(R.id.lnScan);
        lnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScanner();
            }
        });
        getDialog().setTitle("List of Table Number");
        refresh();
        return view;
    }

    private void openScanner(){
        Intent i=new Intent(getActivity(), ScanTable.class);
        i.putExtra("DOCTYPE_KEY", "Add Dish");
        startActivity(i);
        dismiss();
    }
    public void refresh() {
        rv=(RecyclerView)view.findViewById(R.id.list_so);
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 3);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloaderListSO dlistSO=new DownloaderListSO(getActivity(),rv,Fragment_ListSO.this);
        dlistSO.execute();
    }
    public void setOrder(String Doc1No,String Doc2No, String Attention){
        AddDish addDish=new AddDish(getActivity(),Doc2No);
        addDish.execute();
        dismiss();
        //insertso(Doc1No,Doc2No,Attention);
       // InsertingSO insert=new InsertingSO(getActivity(),Doc1No);
       // insert.execute();
    }
    public void insertso(String Doc1No,String Doc2No, String Attention){
       /* try{
            DBAdapter db=new DBAdapter(getActivity());
            db.openDB();
            String qDel="delete from dum_stk_sales_order_hd";
            db.addQuery(qDel);
            String qInsert="insert into dum_stk_sales_order_hd(Doc1No,Doc2No,Attention)values(" +
                    " '"+Doc1No+"', '"+Doc2No+"', '"+Attention+"')";
            db.addQuery(qInsert);
            db.closeDB();
            InsertingSO insert=new InsertingSO(getActivity(),Doc1No);
            insert.execute();
        }catch (SQLiteException e){
            e.printStackTrace();
        }*/

    }

    private void afterSetSO(String Doc1No,String Doc2No,String Attention){
        try{
            DBAdapter db=new DBAdapter(getActivity());
            db.openDB();
            String qDel="delete from dum_stk_sales_order_hd";
            db.addQuery(qDel);
            String qInsert="insert into dum_stk_sales_order_hd(Doc1No,Doc2No,Attention)values(" +
                    " '"+Doc1No+"', '"+Doc2No+"', '"+Attention+"')";
            db.addQuery(qInsert);
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }



    public class InsertingSO extends AsyncTask<Void,Void,String>{
        Context c;
        String Doc1No,Doc2No,Attention,z;
        String IPAddress,UserName,Password,
                DBName,Port,URL,DBStatus,
                EncodeType,UserCode;
        TelephonyManager telephonyManager;
        JSONObject jsonReq,jsonRes;

        public InsertingSO(Context c, String Doc1No) {
            this.c = c;
            this.Doc1No = Doc1No;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.downloadData();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("error")){
                Toast.makeText(c,"Unsuccessfull, no data retrieve", Toast.LENGTH_SHORT).show();
            }else{
                afterSetSO(Doc1No,Doc2No,Attention);
                ((CashReceipt)getActivity()).refreshOrder();
                ((CashReceipt)getActivity()).setSubTitle(Doc2No);
                getDialog().dismiss();
                //Toast.makeText(c,"Success, data retrieve", Toast.LENGTH_SHORT).show();
            }
        }

        private String downloadData(){
            try{
               // telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
               // String deviceId = telephonyManager.getDeviceId();
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName,UserName,Password," +
                        "DBName,Port,DBStatus," +
                        "ItemConn,EncodeType,UserCode" +
                        " from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(0);
                    UserName = curSet.getString(1);
                    Password = curSet.getString(2);
                    DBName = curSet.getString(3);
                    Port = curSet.getString(4);
                    DBStatus=curSet.getString(5);
                    EncodeType=curSet.getString(7);
                    UserCode=curSet.getString(8);
                }

                String qCheckHd="select Doc2No, Attention from stk_sales_order_hd where Doc1No='"+Doc1No+"' ";


                String sqlDel="delete from cloud_cus_inv_dt where ComputerName='"+UserCode+"' ";
                ConnectorLocal connectorLocal = new ConnectorLocal();
                String sql="INSERT INTO cloud_cus_inv_dt (Doc1No, N_o, ItemCode, " +
                        " Description, Qty, FactorQty," +
                        " UOM,UOMSingular, HCUnitCost, " +
                        " DisRate1, HCDiscount, TaxRate1, " +
                        " HCTax, DetailTaxCode, HCLineAmt, " +
                        " BranchCode, DepartmentCode, ProjectCode, " +
                        " SalesPersonCode, LocationCode, BlankLine, " +
                        " DocType, ComputerName, SORunNo, " +
                        " Doc1NoSO, Printer, AlternateItem," +
                        " DUD6, AnalysisCode2, ServiceChargeYN," +
                        " UnitCost)" +
                        " SELECT D.Doc1No, 'a', D.ItemCode, " +
                        " D.Description, D.Qty, D.FactorQty, " +
                        " D.UOM, D.UOMSingular, D.HCUnitCost, " +
                        " D.DisRate1, D.HCDiscount, D.TaxRate1, " +
                        " D.HCTax, D.DetailTaxCode, D.HCLineAmt, " +
                        " D.BranchCode, D.DepartmentCode, D.ProjectCode, " +
                        " D.SalesPersonCode, D.LocationCode, D.BlankLine, " +
                        " 'CS', '"+UserCode+"', D.RunNo, " +
                        " D.Doc1No, D.Printer, D.AlternateItem," +
                        " D.DUD6, D.AnalysisCode2, D.ServiceChargeYN," +
                        " IFNULL(M.UnitCost,0)as UnitCost " +
                        " FROM stk_sales_order_dt D inner join stk_master M" +
                        " on D.ItemCode=M.ItemCode " +
                        " WHERE D.Doc1No='" + Doc1No + "' ";
                if(DBStatus.equals("1")){
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        db.addQuery(sqlDel);
                        String query="SELECT D.Doc1No, 'a', D.ItemCode, " +
                                " D.Description, D.Qty, D.FactorQty, " +
                                " D.UOM, D.UOMSingular, D.HCUnitCost, " +
                                " D.DisRate1, D.HCDiscount, D.TaxRate1, " +
                                " D.HCTax, D.DetailTaxCode, D.HCLineAmt, " +
                                " D.BranchCode, D.DepartmentCode, D.ProjectCode, " +
                                " D.SalesPersonCode, D.LocationCode, D.BlankLine, " +
                                " 'CS' as DocType , '"+UserCode+"' as ComputerName, D.RunNo, " +
                                " D.Doc1No, IFNULL(D.Printer,'') as Printer, IFNULL(M.AlternateItem,'')as AlternateItem, " +
                                " D.DUD6, D.AnalysisCode2, D.ServiceChargeYN, IFNULL(M.UnitCost,0)as UnitCost " +
                                " FROM stk_sales_order_dt D inner join stk_master M on D.ItemCode=M.ItemCode " +
                                " WHERE D.Doc1No='" + Doc1No + "' ";
                        Statement statement = conn.createStatement();
                        statement.executeQuery("SET NAMES 'LATIN1'");
                        statement.executeQuery("SET CHARACTER SET 'LATIN1'");
                        if (statement.execute(query)) {
                            ResultSet rsData = statement.getResultSet();
                            while (rsData.next()) {
                                String queryIn="INSERT INTO cloud_cus_inv_dt (Doc1No, N_o, ItemCode, " +
                                        "  Description, Qty, FactorQty, " +
                                        "  UOM, UOMSingular, HCUnitCost, " +
                                        "  DisRate1, HCDiscount, TaxRate1, " +
                                        "  HCTax, DetailTaxCode, HCLineAmt, " +
                                        "  BranchCode, DepartmentCode, ProjectCode, " +
                                        "  SalesPersonCode, LocationCode, BlankLine, " +
                                        "  DocType, ComputerName, SORunNo, " +
                                        "  Doc1NoSO, Printer, AlternateItem," +
                                        "  DUD6, AnalysisCode2, ServiceChargeYN," +
                                        "  UnitCost)values(" +
                                        "  '"+rsData.getString(1)+"', '"+rsData.getString(2)+"', '"+rsData.getString(3)+"', " +
                                        "  '"+Encode.setChar(EncodeType,rsData.getString(4))+"', '"+rsData.getString(5)+"', '"+rsData.getString(6)+"', " +
                                        "  '"+Encode.setChar(EncodeType,rsData.getString(7))+"', '"+Encode.setChar(EncodeType,rsData.getString(8))+"', '"+rsData.getString(9)+"', " +
                                        "  '"+rsData.getString(10)+"', '"+rsData.getString(11)+"', '"+rsData.getString(12)+"', " +
                                        "  '"+rsData.getString(13)+"', '"+rsData.getString(14)+"', '"+rsData.getString(15)+"', " +
                                        "  '"+rsData.getString(16)+"', '"+rsData.getString(17)+"', '"+rsData.getString(18)+"', " +
                                        "  '"+rsData.getString(19)+"', '"+rsData.getString(20)+"', '"+rsData.getString(21)+"', " +
                                        "  '"+rsData.getString(22)+"', '"+rsData.getString(23)+"', '"+rsData.getString(24)+"', " +
                                        "  '"+rsData.getString(25)+"', '"+rsData.getString(26)+"', '"+Encode.setChar(EncodeType,rsData.getString(27))+"'," +
                                        "  '"+rsData.getString(28)+"', '"+rsData.getString(29)+"', '"+rsData.getString(30)+"'," +
                                        " '"+rsData.getString(31)+"')";
                                Log.d("QUERY",queryIn);
                                db.addQuery(queryIn);
                            }
                            Statement stmtH = conn.createStatement();
                            stmtH.execute(qCheckHd);
                            ResultSet rsH = stmtH.getResultSet();
                            while (rsH.next()) {
                                Doc2No      =rsH.getString(1);
                                Attention   =rsH.getString(2);
                            }
                            z="success";
                        }
                    }
                }else if(DBStatus.equals("2")){
                    //delete
                    jsonReq=new JSONObject();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", sqlDel);
                    jsonReq.put("action", "delete");
                    String vDel = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(vDel);
                    String rsDel = jsonRes.getString("hasil");
                    //insert
                    jsonReq=new JSONObject();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", sql);
                    jsonReq.put("action", "insert");
                    String vInsert = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(vInsert);
                    String rsInsert = jsonRes.getString("hasil");
                    z=rsInsert;
                }else if(DBStatus.equals("0")){
                    Cursor rsH = db.getQuery(qCheckHd);
                    while (rsH.moveToNext()) {
                        Doc2No      =rsH.getString(0);
                        Attention   =rsH.getString(1);
                    }
                    db.addQuery(sqlDel);
                    db.addQuery(sql);
                    z="success";
                }
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
           return z;
        }
    }
}
