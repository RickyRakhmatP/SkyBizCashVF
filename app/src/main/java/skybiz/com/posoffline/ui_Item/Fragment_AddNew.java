package skybiz.com.posoffline.ui_Item;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 11/12/2017.
 */

public class Fragment_AddNew extends Fragment {
    View view;
    Spinner spItemGroup,spDefaultUOM,spUOM,spUOM1,spUOM2,spUOM3,spUOM4;
    EditText txtItemCode,txtDescription,txtAlternateItem,txtSupplierItemCode,
    txtUOMFactor,txtUOMFactor1,txtUOMFactor2,txtUOMFactor3,txtUOMFactor4,txtUnitPrice,
    txtUOMPrice1,txtUOMPrice2,txtUOMPrice3,txtUOMPrice4,txtBaseCode,txtUOMCode1,txtUOMCode2,
            txtUOMCode3,txtUOMCode4,txtMSP,txtMSP1,txtMSP2,txtMSP3,txtMSP4,txtMAXSP,txtMAXSP1,
    txtMAXSP2,txtMAXSP3,txtMAXSP4,txtMPP,txtUnitCost,txtAnalysisCode1,txtAnalysisCode2,txtAnalysisCode3,txtAnalysisCode4;
    CheckBox chkSuspendedYN;
    Button btnSave;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_addnew_item, container, false);
        spItemGroup=(Spinner)view.findViewById(R.id.spItemGroup);
        spDefaultUOM=(Spinner)view.findViewById(R.id.spCategoryCode);
        spUOM=(Spinner)view.findViewById(R.id.spUOM);
        spUOM1=(Spinner)view.findViewById(R.id.spUOM1);
        spUOM2=(Spinner)view.findViewById(R.id.spUOM2);
        spUOM3=(Spinner)view.findViewById(R.id.spUOM3);
        spUOM4=(Spinner)view.findViewById(R.id.spUOM4);
        spDefaultUOM=(Spinner)view.findViewById(R.id.spDefaultUOM);
        txtItemCode=(EditText)view.findViewById(R.id.txtItemCode);
        txtDescription=(EditText)view.findViewById(R.id.txtDescription);
        txtAlternateItem=(EditText)view.findViewById(R.id.txtAlternateItem);
        txtSupplierItemCode=(EditText)view.findViewById(R.id.txtSupplierItemCode);
        txtUOMFactor=(EditText)view.findViewById(R.id.txtUOMFactor);
        txtUOMFactor1=(EditText)view.findViewById(R.id.txtUOMFactor1);
        txtUOMFactor2=(EditText)view.findViewById(R.id.txtUOMFactor2);
        txtUOMFactor3=(EditText)view.findViewById(R.id.txtUOMFactor3);
        txtUOMFactor4=(EditText)view.findViewById(R.id.txtUOMFactor4);
        txtUOMPrice1=(EditText)view.findViewById(R.id.txtUOMPrice1);
        txtUOMPrice2=(EditText)view.findViewById(R.id.txtUOMPrice2);
        txtUOMPrice3=(EditText)view.findViewById(R.id.txtUOMPrice3);
        txtUOMPrice4=(EditText)view.findViewById(R.id.txtUOMPrice4);
        txtUOMCode1=(EditText)view.findViewById(R.id.txtUOMCode1);
        txtUOMCode2=(EditText)view.findViewById(R.id.txtUOMCode2);
        txtUOMCode3=(EditText)view.findViewById(R.id.txtUOMCode3);
        txtUOMCode4=(EditText)view.findViewById(R.id.txtUOMCode4);
        txtBaseCode=(EditText)view.findViewById(R.id.txtBaseCode);
        txtMSP=(EditText)view.findViewById(R.id.txtMSP);
        txtMSP1=(EditText)view.findViewById(R.id.txtMSP1);
        txtMSP2=(EditText)view.findViewById(R.id.txtMSP2);
        txtMSP3=(EditText)view.findViewById(R.id.txtMSP3);
        txtMSP4=(EditText)view.findViewById(R.id.txtMSP4);
        txtMAXSP=(EditText)view.findViewById(R.id.txtMAXSP);
        txtMAXSP1=(EditText)view.findViewById(R.id.txtMAXSP1);
        txtMAXSP2=(EditText)view.findViewById(R.id.txtMAXSP2);
        txtMAXSP3=(EditText)view.findViewById(R.id.txtMAXSP3);
        txtMAXSP4=(EditText)view.findViewById(R.id.txtMAXSP4);
        txtMPP=(EditText)view.findViewById(R.id.txtMPP);
        txtUnitPrice=(EditText)view.findViewById(R.id.txtUnitPrice);
        txtUnitCost=(EditText)view.findViewById(R.id.txtUnitCost);
        txtAnalysisCode1=(EditText)view.findViewById(R.id.txtAnalysisCode1);
        txtAnalysisCode2=(EditText)view.findViewById(R.id.txtAnalysisCode2);
        txtAnalysisCode3=(EditText)view.findViewById(R.id.txtAnalysisCode3);
        txtAnalysisCode4=(EditText)view.findViewById(R.id.txtAnalysisCode4);
        chkSuspendedYN=(CheckBox) view.findViewById(R.id.chkSuspendedYN);
        btnSave=(Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnOK();
            }
        });
        initData();
        return view;
    }

    private void initData(){
        fnloadgroup();
        fnloaduom();
        fndefuom();
        //txtRegistrationDate.setText(dated);
    }
    private void fnloadgroup(){
        RetGroup retGroup=new RetGroup(getActivity());
        retGroup.execute();
    }
    private void fnloaduom(){
        RetUOM retUOM=new RetUOM(getActivity());
        retUOM.execute();
    }
     private void fndefuom(){
         ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                 getActivity(), R.array.defuom_array, android.R.layout.simple_spinner_item);
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         spDefaultUOM.setAdapter(adapter);
     }

    private void fnreset(){
        txtItemCode.getText().clear();
        txtDescription.getText().clear();
        txtAlternateItem.getText().clear();
        txtSupplierItemCode.getText().clear();
        txtUnitPrice.getText().clear();
        txtUOMPrice1.getText().clear();
        txtUOMPrice2.getText().clear();
        txtUOMPrice3.getText().clear();
        txtUOMPrice4.getText().clear();
        txtMAXSP.getText().clear();
        txtMAXSP1.getText().clear();
        txtMAXSP2.getText().clear();
        txtMAXSP3.getText().clear();
        txtMAXSP4.getText().clear();
        txtMSP.getText().clear();
        txtMSP1.getText().clear();
        txtMSP2.getText().clear();
        txtMSP3.getText().clear();
        txtMSP4.getText().clear();
        txtMPP.getText().clear();
        txtUnitCost.getText().clear();
        txtAnalysisCode1.getText().clear();
        txtAnalysisCode2.getText().clear();
        txtAnalysisCode3.getText().clear();
        txtAnalysisCode4.getText().clear();
        txtUOMCode1.getText().clear();
        txtUOMCode2.getText().clear();
        txtUOMCode3.getText().clear();
        txtUOMCode4.getText().clear();
    }
    private void fnOK(){
        String Btn=btnSave.getText().toString();
        if(Btn.equals("ADD NEW")){
            fnsave();
        }else if(Btn.equals("UPDATE")){
            fnupdate();
        }
    }

    private void fnupdate(){
        try {
            String ItemCode = txtItemCode.getText().toString();
            String SuspendedYN="0";
            if(chkSuspendedYN.isChecked()){
                SuspendedYN="1";
            }
            String DefUOM = spDefaultUOM.getSelectedItem().toString();
            if(DefUOM.equals("Base")){
                DefUOM="0";
            }else if(DefUOM.equals("Level 1")){
                DefUOM="1";
            }else if(DefUOM.equals("Level 2")){
                DefUOM="2";
            }else if(DefUOM.equals("Level 3")){
                DefUOM="3";
            }else if(DefUOM.equals("Level 4")){
                DefUOM="4";
            }
            JSONArray results = new JSONArray();
            JSONObject row = new JSONObject();
            row.put("ItemCode", ItemCode);
            row.put("Description", txtDescription.getText().toString());
            row.put("ItemGroup", spItemGroup.getSelectedItem().toString());
            row.put("AlternateItem", txtAlternateItem.getText().toString());
            row.put("SupplierItemCode", txtSupplierItemCode.getText().toString());
            row.put("UOM", spUOM.getSelectedItem().toString());
            row.put("UOM1", spUOM1.getSelectedItem().toString());
            row.put("UOM2", spUOM2.getSelectedItem().toString());
            row.put("UOM3", spUOM3.getSelectedItem().toString());
            row.put("UOM4", spUOM4.getSelectedItem().toString());
            row.put("UOMFactor", txtUOMFactor.getText().toString());
            row.put("UOMFactor1", txtUOMFactor1.getText().toString());
            row.put("UOMFactor2", txtUOMFactor2.getText().toString());
            row.put("UOMFactor3", txtUOMFactor3.getText().toString());
            row.put("UOMFactor4", txtUOMFactor4.getText().toString());
            row.put("UnitPrice", txtUnitPrice.getText().toString());
            row.put("UOMPrice1", checkNull(txtUOMPrice1.getText().toString()));
            row.put("UOMPrice2", checkNull(txtUOMPrice2.getText().toString()));
            row.put("UOMPrice3", checkNull(txtUOMPrice3.getText().toString()));
            row.put("UOMPrice4", checkNull(txtUOMPrice4.getText().toString()));
            row.put("BaseCode", txtBaseCode.getText().toString());
            row.put("UOMCode1", txtUOMCode1.getText().toString());
            row.put("UOMCode2", txtUOMCode2.getText().toString());
            row.put("UOMCode3", txtUOMCode3.getText().toString());
            row.put("UOMCode4", txtUOMCode4.getText().toString());
            row.put("MSP", checkNull(txtMSP.getText().toString()));
            row.put("MSP1",checkNull(txtMSP1.getText().toString()));
            row.put("MSP2",checkNull(txtMSP2.getText().toString()));
            row.put("MSP3", checkNull(txtMSP3.getText().toString()));
            row.put("MSP4", checkNull(txtMSP4.getText().toString()));
            row.put("MAXSP", checkNull(txtMSP.getText().toString()));
            row.put("MAXSP1",checkNull(txtMAXSP1.getText().toString()));
            row.put("MAXSP2", checkNull(txtMAXSP2.getText().toString()));
            row.put("MAXSP3", checkNull(txtMAXSP3.getText().toString()));
            row.put("MAXSP4", checkNull(txtMAXSP4.getText().toString()));
            row.put("MPP", checkNull(txtMPP.getText().toString()));
            row.put("UnitCost", checkNull(txtUnitCost.getText().toString()));
            row.put("AnalysisCode1", txtAnalysisCode1.getText().toString());
            row.put("AnalysisCode2", txtAnalysisCode2.getText().toString());
            row.put("AnalysisCode3", txtAnalysisCode3.getText().toString());
            row.put("AnalysisCode4", txtAnalysisCode4.getText().toString());
            row.put("SuspendedYN", SuspendedYN);
            row.put("DefaultUOM", DefUOM);
            results.put(row);
            UpdateItem updateItem=new UpdateItem(getActivity(),results.toString());
            updateItem.execute();
        }catch (JSONException e){
            e.printStackTrace();
        }
        // String vData=results.toString();
    }
    private void fnsave(){
        try {
            String ItemCode = txtItemCode.getText().toString();
            String SuspendedYN="0";
            if(chkSuspendedYN.isChecked()){
                SuspendedYN="1";
            }
            String DefUOM = spDefaultUOM.getSelectedItem().toString();
            if(DefUOM.equals("Base")){
                DefUOM="0";
            }else if(DefUOM.equals("Level 1")){
                DefUOM="1";
            }else if(DefUOM.equals("Level 2")){
                DefUOM="2";
            }else if(DefUOM.equals("Level 3")){
                DefUOM="3";
            }else if(DefUOM.equals("Level 4")){
                DefUOM="4";
            }
            JSONArray results = new JSONArray();
            JSONObject row = new JSONObject();
            row.put("ItemCode", ItemCode);
            row.put("Description", txtDescription.getText().toString());
            row.put("ItemGroup", spItemGroup.getSelectedItem().toString());
            row.put("AlternateItem", txtAlternateItem.getText().toString());
            row.put("SupplierItemCode", txtSupplierItemCode.getText().toString());
            row.put("UOM", spUOM.getSelectedItem().toString());
            row.put("UOM1", spUOM1.getSelectedItem().toString());
            row.put("UOM2", spUOM2.getSelectedItem().toString());
            row.put("UOM3", spUOM3.getSelectedItem().toString());
            row.put("UOM4", spUOM4.getSelectedItem().toString());
            row.put("UOMFactor", txtUOMFactor.getText().toString());
            row.put("UOMFactor1", txtUOMFactor1.getText().toString());
            row.put("UOMFactor2", txtUOMFactor2.getText().toString());
            row.put("UOMFactor3", txtUOMFactor3.getText().toString());
            row.put("UOMFactor4", txtUOMFactor4.getText().toString());
            row.put("UnitPrice", txtUnitPrice.getText().toString());
            row.put("UOMPrice1", checkNull(txtUOMPrice1.getText().toString()));
            row.put("UOMPrice2", checkNull(txtUOMPrice2.getText().toString()));
            row.put("UOMPrice3", checkNull(txtUOMPrice3.getText().toString()));
            row.put("UOMPrice4", checkNull(txtUOMPrice4.getText().toString()));
            row.put("BaseCode", txtBaseCode.getText().toString());
            row.put("UOMCode1", txtUOMCode1.getText().toString());
            row.put("UOMCode2", txtUOMCode2.getText().toString());
            row.put("UOMCode3", txtUOMCode3.getText().toString());
            row.put("UOMCode4", txtUOMCode4.getText().toString());
            row.put("MSP", checkNull(txtMSP.getText().toString()));
            row.put("MSP1",checkNull(txtMSP1.getText().toString()));
            row.put("MSP2",checkNull(txtMSP2.getText().toString()));
            row.put("MSP3", checkNull(txtMSP3.getText().toString()));
            row.put("MSP4", checkNull(txtMSP4.getText().toString()));
            row.put("MAXSP", checkNull(txtMSP.getText().toString()));
            row.put("MAXSP1",checkNull(txtMAXSP1.getText().toString()));
            row.put("MAXSP2", checkNull(txtMAXSP2.getText().toString()));
            row.put("MAXSP3", checkNull(txtMAXSP3.getText().toString()));
            row.put("MAXSP4", checkNull(txtMAXSP4.getText().toString()));
            row.put("MPP", checkNull(txtMPP.getText().toString()));
            row.put("UnitCost", checkNull(txtUnitCost.getText().toString()));
            row.put("AnalysisCode1", txtAnalysisCode1.getText().toString());
            row.put("AnalysisCode2", txtAnalysisCode2.getText().toString());
            row.put("AnalysisCode3", txtAnalysisCode3.getText().toString());
            row.put("AnalysisCode4", txtAnalysisCode4.getText().toString());
            row.put("SuspendedYN", SuspendedYN);
            row.put("DefaultUOM", DefUOM);
            results.put(row);
            AddNewItem add=new AddNewItem(getActivity(),results.toString());
            add.execute();
        }catch (JSONException e){
            e.printStackTrace();
        }
       // String vData=results.toString();
    }

    public class AddNewItem extends AsyncTask<Void,Void,String>{
        Context c;
        String vData;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;

        public AddNewItem(Context c, String vData) {
            this.c = c;
            this.vData = vData;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.saveitem();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                Toast.makeText(c,"successfull add new item", Toast.LENGTH_SHORT).show();
                fnreset();
            }else{
                Toast.makeText(c,"failed add new item", Toast.LENGTH_SHORT).show();
            }
        }

        private String saveitem() {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String D_ateTime = sdf.format(date);
                String D_ate = sdf2.format(date);
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String querySet = "select * from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(1);
                    UserName = curSet.getString(2);
                    Password = curSet.getString(3);
                    DBName = curSet.getString(4);
                    Port = curSet.getString(5);
                    DBStatus = curSet.getString(7);
                    ItemConn = curSet.getString(8);
                }
                JSONArray ja=new JSONArray(vData);
                JSONObject jo=null;
                String ItemCode="";
                String Description="";
                String ItemGroup="";
                String AlternateItem="";
                String SupplierItemCode="";
                String SuspendedYN="";
                String UOM="";
                String UOM1="";
                String UOM2="";
                String UOM3="";
                String UOM4="";
                String UOMFactor="";
                String UOMFactor1="";
                String UOMFactor2="";
                String UOMFactor3="";
                String UOMFactor4="";
                String UnitPrice="";
                String UOMPrice1="";
                String UOMPrice2="";
                String UOMPrice3="";
                String UOMPrice4="";
                String MSP="";
                String MSP1="";
                String MSP2="";
                String MSP3="";
                String MSP4="";
                String MAXSP="";
                String MAXSP1="";
                String MAXSP2="";
                String MAXSP3="";
                String MAXSP4="";
                String MPP="";
                String DefaultUOM="";
                String UnitCost="";
                String AnalysisCode1="";
                String AnalysisCode2="";
                String AnalysisCode3="";
                String AnalysisCode4="";
                String BaseCode="";
                String UOMCode1="";
                String UOMCode2="";
                String UOMCode3="";
                String UOMCode4="";
                for (int i=0;i<ja.length();i++) {
                    jo = ja.getJSONObject(i);
                    ItemCode = jo.getString("ItemCode");
                    Description = jo.getString("Description");
                    ItemGroup = jo.getString("ItemGroup");
                    AlternateItem = jo.getString("AlternateItem");
                    SupplierItemCode = jo.getString("SupplierItemCode");
                    SuspendedYN = jo.getString("SuspendedYN");
                    UOM = jo.getString("UOM");
                    UOM1 = jo.getString("UOM1");
                    UOM2 = jo.getString("UOM2");
                    UOM3 = jo.getString("UOM3");
                    UOM4 = jo.getString("UOM4");
                    UOMFactor = jo.getString("UOMFactor");
                    UOMFactor1 = jo.getString("UOMFactor1");
                    UOMFactor2 = jo.getString("UOMFactor2");
                    UOMFactor3 = jo.getString("UOMFactor3");
                    UOMFactor4 = jo.getString("UOMFactor4");
                    UnitPrice = jo.getString("UnitPrice");
                    UOMPrice1 = jo.getString("UOMPrice1");
                    UOMPrice2 = jo.getString("UOMPrice2");
                    UOMPrice3 = jo.getString("UOMPrice3");
                    UOMPrice4 = jo.getString("UOMPrice4");
                    BaseCode = jo.getString("BaseCode");
                    UOMCode1 = jo.getString("UOMCode1");
                    UOMCode2 = jo.getString("UOMCode2");
                    UOMCode3 = jo.getString("UOMCode3");
                    UOMCode4 = jo.getString("UOMCode4");
                    MSP = jo.getString("MSP");
                    MSP1 = jo.getString("MSP1");
                    MSP2 = jo.getString("MSP2");
                    MSP3 = jo.getString("MSP3");
                    MSP4 = jo.getString("MSP4");
                    MAXSP = jo.getString("MAXSP");
                    MAXSP1 = jo.getString("MAXSP1");
                    MAXSP2 = jo.getString("MAXSP2");
                    MAXSP3 = jo.getString("MAXSP3");
                    MAXSP4 = jo.getString("MAXSP4");
                    MPP = jo.getString("MPP");
                    UnitCost = jo.getString("UnitCost");
                    AnalysisCode1 = jo.getString("AnalysisCode1");
                    AnalysisCode2 = jo.getString("AnalysisCode2");
                    AnalysisCode3 = jo.getString("AnalysisCode3");
                    AnalysisCode4 = jo.getString("AnalysisCode4");
                    DefaultUOM = jo.getString("DefaultUOM");
                }
                String insert="insert into stk_master(ItemCode,Description,ItemGroup," +
                        "AlternateItem,SupplierItemCode,SuspendedYN," +
                        "UOM,UOM1, UOM2," +
                        "UOM3,UOM4,UOMFactor1," +
                        "UOMFactor2,UOMFactor3,UOMFactor4," +
                        "UnitPrice,UOMPrice1, UOMPrice2," +
                        "UOMPrice3,UOMPrice4,BaseCode," +
                        "UOMCode1,UOMCode2,UOMCode3," +
                        "UOMCode4,MSP," +
                        "MSP1,MSP2,MSP3," +
                        "MSP4,MAXSP,MAXSP1," +
                        "MAXSP2,MAXSP3,MAXSP4," +
                        "MPP,UnitCost,DefaultUOM," +
                        "AnalysisCode1,AnalysisCode2,AnalysisCode3," +
                        "AnalysisCode4,DateTimeModified, RetailTaxCode," +
                        "SalesTaxCode,PurchaseTaxCode,HCDiscount," +
                        "DisRate1)values('"+ItemCode +"', '"+Description+"', '"+ItemGroup+"'," +
                        "'"+AlternateItem+"', '"+SupplierItemCode+"', '"+SuspendedYN+"'," +
                        "'"+UOM+"', '"+UOM1+"', '"+UOM2+"'," +
                        "'"+UOM3+"', '"+UOM4+"', '"+UOMFactor1+"'," +
                        "'"+UOMFactor2+"', '"+UOMFactor3+"', '"+UOMFactor4+"'," +
                        "'"+UnitPrice+"', '"+UOMPrice1+"', '"+UOMPrice2+"'," +
                        "'"+UOMPrice3+"', '"+UOMPrice4+"', '"+BaseCode+"', " +
                        "'"+UOMCode1+"', '"+UOMCode2+"', '"+UOMCode3+"', " +
                        "'"+UOMCode4+"', '"+MSP+"'," +
                        "'"+MSP1+"', '"+MSP2+"', '"+MSP3+"'," +
                        "'"+MSP4+"', '"+MAXSP+"', '"+MAXSP1+"'," +
                        "'"+MAXSP2+"', '"+MAXSP3+"', '"+MAXSP4+"'," +
                        "'"+MPP+"', '"+UnitCost+"', '"+DefaultUOM+"'," +
                        "'"+AnalysisCode1+"', '"+AnalysisCode2+"', '"+AnalysisCode3+"'," +
                        "'"+AnalysisCode4+"', '"+D_ateTime+"', ''," +
                        "'', '', '0', " +
                        "'0')";
                Log.d("QUERY",insert);
                if (DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement stmtInsert = conn.createStatement();
                        stmtInsert.execute(insert);
                        z="success";
                    }
                }else{
                   /* URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    if (conn != null) {
                        Statement stmtInsert = conn.createStatement();
                        stmtInsert.execute(insert);
                       // z="success";
                    }*/
                    long add=db.addQuery(insert);
                    if(add>0){
                        z="success";
                    }else{
                        z="error";
                    }
                }
                db.closeDB();
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return z;
        }
    }
    public class UpdateItem extends AsyncTask<Void,Void,String>{
        Context c;
        String vData;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;

        public UpdateItem(Context c, String vData) {
            this.c = c;
            this.vData = vData;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.updateitem();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                Toast.makeText(c,"successfull update item", Toast.LENGTH_SHORT).show();
                fnreset();
                txtItemCode.setEnabled(true);
                btnSave.setText("ADD NEW");
            }else{
                Toast.makeText(c,"failed update item", Toast.LENGTH_SHORT).show();
            }
        }

        private String updateitem() {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String D_ateTime = sdf.format(date);
                String D_ate = sdf2.format(date);
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String querySet = "select * from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(1);
                    UserName = curSet.getString(2);
                    Password = curSet.getString(3);
                    DBName = curSet.getString(4);
                    Port = curSet.getString(5);
                    DBStatus = curSet.getString(7);
                    ItemConn = curSet.getString(8);
                }
                JSONArray ja=new JSONArray(vData);
                JSONObject jo=null;
                String ItemCode="";
                String Description="";
                String ItemGroup="";
                String AlternateItem="";
                String SupplierItemCode="";
                String SuspendedYN="";
                String UOM="";
                String UOM1="";
                String UOM2="";
                String UOM3="";
                String UOM4="";
                String UOMFactor="";
                String UOMFactor1="";
                String UOMFactor2="";
                String UOMFactor3="";
                String UOMFactor4="";
                String UnitPrice="";
                String UOMPrice1="";
                String UOMPrice2="";
                String UOMPrice3="";
                String UOMPrice4="";
                String MSP="";
                String MSP1="";
                String MSP2="";
                String MSP3="";
                String MSP4="";
                String MAXSP="";
                String MAXSP1="";
                String MAXSP2="";
                String MAXSP3="";
                String MAXSP4="";
                String MPP="";
                String DefaultUOM="";
                String UnitCost="";
                String AnalysisCode1="";
                String AnalysisCode2="";
                String AnalysisCode3="";
                String AnalysisCode4="";
                String BaseCode="";
                String UOMCode1="";
                String UOMCode2="";
                String UOMCode3="";
                String UOMCode4="";
                for (int i=0;i<ja.length();i++) {
                    jo = ja.getJSONObject(i);
                    ItemCode = jo.getString("ItemCode");
                    Description = jo.getString("Description");
                    ItemGroup = jo.getString("ItemGroup");
                    AlternateItem = jo.getString("AlternateItem");
                    SupplierItemCode = jo.getString("SupplierItemCode");
                    SuspendedYN = jo.getString("SuspendedYN");
                    UOM = jo.getString("UOM");
                    UOM1 = jo.getString("UOM1");
                    UOM2 = jo.getString("UOM2");
                    UOM3 = jo.getString("UOM3");
                    UOM4 = jo.getString("UOM4");
                    UOMFactor = jo.getString("UOMFactor");
                    UOMFactor1 = jo.getString("UOMFactor1");
                    UOMFactor2 = jo.getString("UOMFactor2");
                    UOMFactor3 = jo.getString("UOMFactor3");
                    UOMFactor4 = jo.getString("UOMFactor4");
                    UnitPrice = jo.getString("UnitPrice");
                    UOMPrice1 = jo.getString("UOMPrice1");
                    UOMPrice2 = jo.getString("UOMPrice2");
                    UOMPrice3 = jo.getString("UOMPrice3");
                    UOMPrice4 = jo.getString("UOMPrice4");
                    BaseCode = jo.getString("BaseCode");
                    UOMCode1 = jo.getString("UOMCode1");
                    UOMCode2 = jo.getString("UOMCode2");
                    UOMCode3 = jo.getString("UOMCode3");
                    UOMCode4 = jo.getString("UOMCode4");
                    MSP = jo.getString("MSP");
                    MSP1 = jo.getString("MSP1");
                    MSP2 = jo.getString("MSP2");
                    MSP3 = jo.getString("MSP3");
                    MSP4 = jo.getString("MSP4");
                    MAXSP = jo.getString("MAXSP");
                    MAXSP1 = jo.getString("MAXSP1");
                    MAXSP2 = jo.getString("MAXSP2");
                    MAXSP3 = jo.getString("MAXSP3");
                    MAXSP4 = jo.getString("MAXSP4");
                    MPP = jo.getString("MPP");
                    UnitCost = jo.getString("UnitCost");
                    AnalysisCode1 = jo.getString("AnalysisCode1");
                    AnalysisCode2 = jo.getString("AnalysisCode2");
                    AnalysisCode3 = jo.getString("AnalysisCode3");
                    AnalysisCode4 = jo.getString("AnalysisCode4");
                    DefaultUOM = jo.getString("DefaultUOM");
                }
                String update="update stk_master set Description='"+Description+"', ItemGroup='"+ItemGroup+"' ," +
                        "AlternateItem='"+AlternateItem+"', SupplierItemCode='"+SupplierItemCode+"', SuspendedYN='"+SuspendedYN+"'," +
                        "UOM='"+UOM+"', UOM1='"+UOM1+"', UOM2='"+UOM2+"'," +
                        "UOM3='"+UOM3+"', UOM4='"+UOM4+"', UOMFactor1='"+UOMFactor1+"'," +
                        "UOMFactor2='"+UOMFactor2+"',UOMFactor3='"+UOMFactor3+"', UOMFactor4='"+UOMFactor4+"'," +
                        "UnitPrice='"+UnitPrice+"', UOMPrice1='"+UOMPrice1+"', UOMPrice2='"+UOMPrice2+"'," +
                        "UOMPrice3='"+UOMPrice3+"', UOMPrice4='"+UOMPrice4+"', BaseCode= '"+BaseCode+"'," +
                        "UOMCode1='"+UOMCode1+"', UOMCode2='"+UOMCode2+"', UOMCode3= '"+UOMCode3+"'," +
                        "UOMCode4='"+UOMCode4+"', MSP='"+MSP+"'," +
                        "MSP1='"+MSP1+"', MSP2='"+MSP2+"', MSP3='"+MSP3+"'," +
                        "MSP4='"+MSP4+"', MAXSP='"+MAXSP+"', MAXSP1='"+MAXSP1+"'," +
                        "MAXSP2='"+MAXSP2+"', MAXSP3='"+MAXSP3+"', MAXSP4='"+MAXSP4+"'," +
                        "MPP='"+MPP+"', UnitCost='"+UnitCost+"', DefaultUOM='"+DefaultUOM+"'," +
                        "AnalysisCode1='"+AnalysisCode1+"', AnalysisCode2='"+AnalysisCode2+"', AnalysisCode3='"+AnalysisCode3+"'," +
                        "AnalysisCode4='"+AnalysisCode4+"', DateTimeModified='"+D_ateTime+"' where ItemCode='"+ItemCode+"' ";

                Log.d("QUERY Update",update);
                if (DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement stmtUpdate = conn.createStatement();
                        stmtUpdate.execute(update);
                        z="success";
                    }
                }else{
                   /* URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    if (conn != null) {
                        Statement stmtUpdate = conn.createStatement();
                        stmtUpdate.execute(update);
                    }*/
                    long add=db.addQuery(update);
                    if(add>0){
                        z="success";
                    }else{
                        z="error";
                    }
                }
                db.closeDB();
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return z;
        }

    }
    public class RetGroup extends AsyncTask<Void,Void,String>{
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
        ArrayList lsGroup=new ArrayList<String>();

        public RetGroup(Context c) {
            this.c = c;
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
            if(result.equals("success")){
                spItemGroup.setAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,lsGroup));
            }else{
                Toast.makeText(c,"Group is empty", Toast.LENGTH_SHORT).show();
            }
        }

        private String downloadData() {
            try {
                DBAdapter db=new DBAdapter(c);
                db.openDB();
                String querySet="select * from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(1);
                    UserName = curSet.getString(2);
                    Password = curSet.getString(3);
                    DBName = curSet.getString(4);
                    Port = curSet.getString(5);
                    DBStatus=curSet.getString(7);
                    ItemConn=curSet.getString(8);
                }
                String sql ="select ItemGroup, Description from stk_group Order By ItemGroup ";
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {

                        Statement stmt = conn.createStatement();
                        stmt.execute(sql);
                        ResultSet resultSet = stmt.getResultSet();
                        int i=0;
                        while (resultSet.next()) {
                            lsGroup.add(resultSet.getString(1));
                            i++;
                        }
                        if(i>0){
                            z="success";
                        }else{
                            z="error";
                        }
                    }
                }else{
                    Cursor rsGroup=db.getQuery(sql);
                    int i=0;
                    while(rsGroup.moveToNext()){
                        lsGroup.add(rsGroup.getString(0));
                        i++;
                    }
                    if(i>0){
                        z="success";
                    }else{
                        z="error";
                    }
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

    public class RetUOM extends AsyncTask<Void,Void,String>{
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
        ArrayList lsUOM=new ArrayList<String>();

        public RetUOM(Context c) {
            this.c = c;
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
            if(result.equals("success")){
                spUOM.setAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,lsUOM));

                spUOM1.setAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,lsUOM));

                spUOM2.setAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,lsUOM));

                spUOM3.setAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,lsUOM));

                spUOM4.setAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,lsUOM));
            }else{
                Toast.makeText(c,"UOM is Empty", Toast.LENGTH_SHORT).show();
            }
        }

        private String downloadData() {
            try {
                DBAdapter db=new DBAdapter(c);
                db.openDB();
                String querySet="select * from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(1);
                    UserName = curSet.getString(2);
                    Password = curSet.getString(3);
                    DBName = curSet.getString(4);
                    Port = curSet.getString(5);
                    DBStatus=curSet.getString(7);
                    ItemConn=curSet.getString(8);
                }
                String sql ="select UOM from stk_uom";
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement stmt = conn.createStatement();
                        stmt.execute(sql);
                        ResultSet resultSet = stmt.getResultSet();
                        int i=0;
                        lsUOM.add("");
                        while (resultSet.next()) {
                            lsUOM.add(resultSet.getString(1));
                            i++;
                        }
                        if(i>0){
                            z="success";
                        }else{
                            z="error";
                        }
                    }
                }else if(DBStatus.equals("0")){
                    Cursor rsUOM=db.getQuery(sql);
                    int i=0;
                    lsUOM.add("");
                    while(rsUOM.moveToNext()){
                        lsUOM.add(rsUOM.getString(0));
                        i++;
                    }
                    if(i>0){
                        z="success";
                    }else{
                        lsUOM.add("Bag");
                        lsUOM.add("Box");
                        lsUOM.add("Piece");
                        lsUOM.add("Unit");
                        lsUOM.add("Bottle");
                        z="success";
                    }
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

    private String checkNull(String valText){
        String newText="";
        if(valText.isEmpty()){
            newText="0.00";
        }else{
            newText=valText;
        }
        return newText;
    }
}
