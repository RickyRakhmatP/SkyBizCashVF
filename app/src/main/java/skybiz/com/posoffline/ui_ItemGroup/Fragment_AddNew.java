package skybiz.com.posoffline.ui_ItemGroup;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    EditText txtItemGroup,txtDescription,txtModifier1,txtSupplierItemCode,
    txtUOMFactor,txtUOMFactor1,txtUOMFactor2,txtUOMFactor3,txtUOMFactor4,txtUnitPrice;
    Button btnSave;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_addnew_group, container, false);
        txtItemGroup=(EditText)view.findViewById(R.id.txtItemGroup);
        txtDescription=(EditText)view.findViewById(R.id.txtDescription);
        txtModifier1=(EditText)view.findViewById(R.id.txtModifier1);
        btnSave=(Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnsave();
            }
        });
        return view;
    }
    private void fnreset(){
        txtItemGroup.getText().clear();
        txtDescription.getText().clear();
        txtModifier1.getText().clear();
    }
    private void fnsave(){
        try {
            String Btn=btnSave.getText().toString();
            String ItemGroup = txtItemGroup.getText().toString();
            JSONArray results = new JSONArray();
            JSONObject row = new JSONObject();
            row.put("ItemGroup", ItemGroup);
            row.put("Description", txtDescription.getText().toString());
            row.put("Modifier1", txtModifier1.getText().toString());
            results.put(row);
            if(Btn.equals("ADD NEW")){
                AddNewItem addNewItem=new AddNewItem(getActivity(),results.toString());
                addNewItem.execute();
            }else if(Btn.equals("UPDATE")){
                UpdateGroup updateGroup=new UpdateGroup(getActivity(),results.toString());
                updateGroup.execute();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
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
                Toast.makeText(c,"successfull add new group", Toast.LENGTH_SHORT).show();
                fnreset();
            }else{
                Toast.makeText(c,"failed add new group", Toast.LENGTH_SHORT).show();
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
                String Description="";
                String ItemGroup="";
                String Modifier1="";
                for (int i=0;i<ja.length();i++) {
                    jo = ja.getJSONObject(i);
                    ItemGroup = jo.getString("ItemGroup");
                    Description = jo.getString("Description");
                    Modifier1 = jo.getString("Modifier1");

                }
                String insert="insert into stk_group(ItemGroup, Description, Modifier1," +
                        "DateTimeModified)" +
                        "values('"+ItemGroup +"', '"+Description+"', '"+Modifier1+"', '"+D_ateTime+"')";
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
    public class UpdateGroup extends AsyncTask<Void,Void,String>{
        Context c;
        String vData;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;

        public UpdateGroup(Context c, String vData) {
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
                Toast.makeText(c,"successfull update group", Toast.LENGTH_SHORT).show();
                fnreset();
                txtItemGroup.setEnabled(true);
                btnSave.setText("ADD NEW");
            }else{
                Toast.makeText(c,"failed update group", Toast.LENGTH_SHORT).show();
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
                String ItemGroup="";
                String Description="";
                String Modifier1="";
                for (int i=0;i<ja.length();i++) {
                    jo = ja.getJSONObject(i);
                    ItemGroup = jo.getString("ItemGroup");
                    Description = jo.getString("Description");
                    Modifier1 = jo.getString("Modifier1");

                }
                String update="update stk_group set Description='"+Description+"', Modifier1='"+Modifier1+"', " +
                            " DateTimeModified='"+D_ateTime+"' where ItemGroup='"+ItemGroup+"' ";
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

                    /*URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    if (conn != null) {
                        Statement stmtUpdate = conn.createStatement();
                        stmtUpdate.execute(update);
                       // z="success";
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
}
