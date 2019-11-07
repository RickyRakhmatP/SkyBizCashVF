package skybiz.com.posoffline.ui_Setting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import skybiz.com.posoffline.MainActivity;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.FnCheckConnection;
import skybiz.com.posoffline.ui_Member.m_MemberList.Fragment_AddNew;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;
import skybiz.com.posoffline.ui_Setting.m_Local.SettingDB;

/**
 * Created by 7 on 11/12/2017.
 */

public class Fragment_Setting extends Fragment {
    EditText ServerName_txt,UserName_txt,
            Password_txt, DBName_txt, Port_txt,
            txtBranchCode,txtLocationCode,txtUserCode,
    txtCounterCode;
    Button btnSave, btnCancel,btnCheck,btnBackup;
    TextView txtMAC,txtCompanyName,txtMgt01YN,txtModules;
    String isConnected;
    ArrayList<SettingDB> settingdb=new ArrayList<>();
    TelephonyManager telephonyManager;
    String deviceId,BranchCode,LocationCode,CounterCode,
            DepartmentCode,CompCustomerYN,AdminPassword="",
            CategoryCode,DirectPrintYN,SalesPersonCode,
            CompanyAddress,CloudSettingYN,FastKeypadYN="0";
    Switch swDBStatus,swItemConn;
    Spinner spDBStatus;
    String OldAdminPassword="",NewAdminPassword="";

    View view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view            = inflater.inflate(R.layout.fragment_setting_server, container, false);
        UserName_txt    = (EditText) view.findViewById(R.id.UserName_txt);
        ServerName_txt  = (EditText) view.findViewById(R.id.ServerName_txt);
        Password_txt    = (EditText) view.findViewById(R.id.Password_txt);
        DBName_txt      = (EditText) view.findViewById(R.id.DBName_txt);
        Port_txt        = (EditText) view.findViewById(R.id.Port_txt);
        txtBranchCode   = (EditText) view.findViewById(R.id.txtBranchCode);
        txtLocationCode = (EditText) view.findViewById(R.id.txtLocationCode);
        txtUserCode     = (EditText) view.findViewById(R.id.txtUserCode);
        txtCounterCode  = (EditText) view.findViewById(R.id.txtCounterCode);
        btnSave         = (Button) view.findViewById(R.id.btn_save);
        btnCancel       = (Button) view.findViewById(R.id.btn_cancel);
        btnCheck        = (Button) view.findViewById(R.id.btn_checkconnect);
        btnBackup       = (Button) view.findViewById(R.id.btnBackup);
        txtMAC          =(TextView)view.findViewById(R.id.txtMAC);
        txtCompanyName  =(TextView)view.findViewById(R.id.txtCompanyName);
        txtMgt01YN      =(TextView)view.findViewById(R.id.txtMgt01YN);
        txtModules      =(TextView)view.findViewById(R.id.txtModules);
        spDBStatus      =(Spinner)view.findViewById(R.id.spDBStatus);
        swItemConn      =(Switch)view.findViewById(R.id.swItemConn);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String MacAddress=txtMAC.getText().toString();
                String vStatus= spDBStatus.getSelectedItem().toString();
                FnCheckConnection fncheck=new FnCheckConnection(getActivity(),ServerName_txt.getText().toString(),
                        DBName_txt.getText().toString(),UserName_txt.getText().toString(),
                        Password_txt.getText().toString(),Port_txt.getText().toString(),vStatus,
                        MacAddress);
                try {
                    isConnected=fncheck.execute().get();
                    Log.d("CHECK",isConnected);
                    if(isConnected.equals("success")){
                        btnSave.setEnabled(true);
                    }else{
                        btnSave.setEnabled(false);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
            }
        });
        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnbackup();
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.db_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDBStatus.setAdapter(adapter);
        swItemConn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    swItemConn.setText("Item Server");
                }else {
                    swItemConn.setText("Item Local");
                }
            }
        });
        retMac();
        getConn();
        retrieve();
        return view;
    }
    private void fnbackup(){
        String MacAddress=txtMAC.getText().toString();
        backupSetting backup=new backupSetting(getActivity(),MacAddress);
        backup.execute();
    }
    private void retMac(){
        deviceId= Settings.Secure.getString(getActivity().getContentResolver(),Settings.Secure.ANDROID_ID);
        txtMAC.setText(deviceId);
    }
    private void getConn(){
        ConnLicense connLicense=new ConnLicense(getActivity());
        connLicense.execute();
    }
    public class CheckLicenese extends AsyncTask<Void,Void,String>{
        Context c;
        String z,IPAddress,UserName,Password,DBName,Port;
        String vIPAddress,vUserName,vPassword,
                vDBName,vPort,vCompanyName,
                vOnlineYN,vMgt01YN,vBranchCode,
                vLocationCode,vModules,vDepartmentCode,
                vCompCustomerYN,vAdminPassword,vCategoryCode,
                vDirectPrintYN,vSalesPersonCode,vUserCode,vCounterCode,
                vCompanyAddress,vCloudSettingYN,vFastKeypadYN;
       // TelephonyManager telephonyManager;

        public CheckLicenese(Context c, String IPAddress, String userName, String password, String DBName, String port) {
            this.c = c;
            this.IPAddress = IPAddress;
            UserName = userName;
            Password = password;
            this.DBName = DBName;
            Port = port;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
           return this.checkLicense();
        }

        @Override
        protected void onPostExecute(String isConnect) {
            super.onPostExecute(isConnect);
            if(isConnect.equals("error")){
                Toast.makeText(c,"get conn license failure", Toast.LENGTH_SHORT).show();
            }else if(isConnect.equals("ada")){
                Toast.makeText(c,"Success get setting server", Toast.LENGTH_SHORT).show();
                ServerName_txt.setText(vIPAddress);
                UserName_txt.setText(vUserName);
                Port_txt.setText(vPort);
                Password_txt.setText(vPassword);
                DBName_txt.setText(vDBName);
                txtCompanyName.setText(vCompanyName);
                txtMgt01YN.setText(vMgt01YN);
                txtBranchCode.setText(vBranchCode);
                txtLocationCode.setText(vLocationCode);
                txtUserCode.setText(vUserCode);
                txtCounterCode.setText(vCounterCode);
                txtModules.setText(vModules);
                DepartmentCode=vDepartmentCode;
                CompCustomerYN=vCompCustomerYN;
                NewAdminPassword=vAdminPassword;
                CategoryCode=vCategoryCode;
                SalesPersonCode=vSalesPersonCode;
                DirectPrintYN=vDirectPrintYN;
                setSpinnerDB(vOnlineYN);
                CompanyAddress=vCompanyAddress;
                CloudSettingYN=vCloudSettingYN;
                FastKeypadYN=vFastKeypadYN;
                if(CloudSettingYN.equals("1")) {
                    btnCheck.setEnabled(true);
                    btnSave.setEnabled(false);
                }else if(CloudSettingYN.equals("0")){
                    btnCheck.setEnabled(false);
                    btnSave.setEnabled(true);
                    isConnected="success";
                }
            }else if (isConnect.equals("kosong")){
                Toast.makeText(c,"Information not available, please request vendor to register the device!", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(false);
            }else if(isConnect.equals("expired")){
                Toast.makeText(c,"Expiry Date, please request vendor to update renewal expiry date !", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(false);
            }
        }
        private String  checkLicense(){
            try{
                //telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
                //String deviceId = telephonyManager.getDeviceId();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String datedNow = sdf.format(date);
                String URLc="jdbc:mysql://"+IPAddress+":"+Port+"/"+DBName;
                Connection conn= Connector.connect(URLc, UserName, Password);
                if (conn == null) {
                    Log.d("ERROR",URLc+UserName+Password);
                    z = "error";
                }else{
                    BranchCode="";
                    LocationCode ="";
                    CounterCode ="";
                    String qExp="select count(*)as expyn from androidlicense where MACAddress='"+deviceId+"' and NextRenewalDate>='"+datedNow+"' ";
                    Statement stmtExp     = conn.createStatement();
                    stmtExp.execute(qExp);
                    ResultSet rsExp      = stmtExp.getResultSet();
                    int expyn=0;
                    while(rsExp.next()){
                        expyn=rsExp.getInt(1);
                    }
                    if(expyn>0) {
                        String numrow = "0";
                        String vCheck = "SELECT COUNT(MACAddress)as NumRows, DB_IP, DB_ID, " +
                                "DB_Password, DB_Port, DatabaseName," +
                                "OnlineYN, CompanyName, Mgt01YN," +
                                "IFNULL(BranchCode,'')as BranchCode, IFNULL(LocationCode,'') as LocationCode, " +
                                "IFNULL(CounterCode,'') as CounterCode, IFNULL(Modules,'') as Modules, " +
                                "IFNULL(DepartmentCode, '')as DepartmentCode," +
                                "CompCustomerYN, AdminPassword, CategoryCode, " +
                                "SalesPersonCode, DirectPrintYN, IFNULL(UserCode,'')as UserCode," +
                                "IFNULL(CompanyAddress,'')as CompanyAddress, CloudSettingYN, " +
                                "FastKeypadYN " +
                                "from androidlicense where MACAddress='" + deviceId + "'  ";
                        Log.d("QUERY",vCheck);
                        Statement statement = conn.createStatement();
                        statement.execute(vCheck);
                        ResultSet rsDB = statement.getResultSet();
                        while (rsDB.next()) {
                            numrow = rsDB.getString("NumRows");
                            if (!numrow.equals("0")) {
                                z = "ada";
                                vIPAddress  = rsDB.getString("DB_IP");
                                vUserName   = rsDB.getString("DB_ID");
                                vPort       = rsDB.getString("DB_Port");
                                vPassword   = rsDB.getString("DB_Password");
                                vDBName     = rsDB.getString("DatabaseName");
                                vOnlineYN   = rsDB.getString("OnlineYN");
                                vCompanyName = rsDB.getString("CompanyName");
                                vMgt01YN    = rsDB.getString("Mgt01YN");
                                vBranchCode = rsDB.getString("BranchCode");
                                vLocationCode = rsDB.getString("LocationCode");
                                vModules = rsDB.getString("Modules");
                                vDepartmentCode = rsDB.getString("DepartmentCode");
                                vCompCustomerYN = rsDB.getString("CompCustomerYN");
                                vAdminPassword = rsDB.getString("AdminPassword");
                                vCategoryCode = rsDB.getString("CategoryCode");
                                vSalesPersonCode = rsDB.getString("SalesPersonCode");
                                vDirectPrintYN = rsDB.getString("DirectPrintYN");
                                vUserCode = rsDB.getString("UserCode");
                                vCounterCode = rsDB.getString("CounterCode");
                                vCloudSettingYN = rsDB.getString("CloudSettingYN");
                                vCompanyAddress = rsDB.getString("CompanyAddress");
                                vFastKeypadYN = rsDB.getString("FastKeypadYN");
                            } else {
                                z = "kosong";
                            }
                        }
                    }else{
                        z="expired";
                    }
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
            return z;
        }
    }
    public class ConnLicense extends AsyncTask<Void,Void,String>{
        Context c;
        String z,IPAddress,UserName,Password,DBName,Port;
       // TelephonyManager telephonyManager;
        public ConnLicense(Context c) {
            this.c = c;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                z="success";
                URL url = new URL("http://skybiz.com.my/userlicensesetting/androidlicense.txt");
                // Read all the text returned by the server
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String str;
                z="";
                int i=0;
                while ((str = in.readLine()) != null) {
                    Log.d("STRING",str);
                    fngetStr(i,str);
                    z +=str;
                    i++;
                    // str is one line of text; readLine() strips the newline character(s)
                }
                in.close();
                return z;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return z;
        }

        @Override
        protected void onPostExecute(String isConnect) {
            super.onPostExecute(isConnect);
            if(isConnect.equals("error")){
                Toast.makeText(c,"Get Conn License Failure", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(c,"Success Conn License", Toast.LENGTH_SHORT).show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        CheckLicenese checkLicenese=new CheckLicenese(c,IPAddress,UserName,Password,DBName,Port);
                        checkLicenese.execute();
                    }
                }, 600);
            }
        }
        private void fngetStr(int no,String str){
            int position = str.indexOf("=")+1;
            switch (no) {
                case 0:
                    IPAddress=str.substring(position,str.length());
                    break;
                case 1:
                    Log.d("STR",str+String.valueOf(position)+String.valueOf(str.length()-position));
                    Port=str.substring(position,str.length());
                    break;
                case 2:
                    UserName=str.substring(position,str.length());
                    break;
                case 3:
                    Password=str.substring(position,str.length());
                    break;
                case 4:
                    DBName=str.substring(position,str.length());
                    break;
            }
        }

    }


    private void save() {
        try {
            if (OldAdminPassword.isEmpty()) {
                AdminPassword = NewAdminPassword;
            } else {
                AdminPassword = OldAdminPassword;
            }
            String MacAddress = txtMAC.getText().toString();
            String ServerName = ServerName_txt.getText().toString();
            String UserName = UserName_txt.getText().toString();
            String Password = Password_txt.getText().toString();
            String DBName = DBName_txt.getText().toString();
            String Port = Port_txt.getText().toString();
            String Mgt01YN = txtMgt01YN.getText().toString();
            BranchCode = txtBranchCode.getText().toString();
            LocationCode = txtLocationCode.getText().toString();
            String UserCode = txtUserCode.getText().toString();
            String CounterCode = txtCounterCode.getText().toString();
            String Modules = txtModules.getText().toString();
            String ConnYN = isConnected;
            String ItemConn = "";
            String DBStatus = "";
            String vStatus = spDBStatus.getSelectedItem().toString();
            String CompanyName = txtCompanyName.getText().toString();
            if (vStatus.equals("Localhost")) {
                DBStatus = "0";
            } else if (vStatus.equals("Cloud Server")) {
                DBStatus = "1";
            } else if (vStatus.equals("Android Server")) {
                DBStatus = "2";
            }
            if (swItemConn.isChecked()) {
                ItemConn = "1";
            } else {
                ItemConn = "0";
            }
            DBAdapter db = new DBAdapter(this.getContext());
            db.openDB();
            String vDelete = "delete from tb_setting";
            db.addQuery(vDelete);
            String query = "insert into tb_setting(ServerName,UserName,Password," +
                    "DBName,Port,ConnYN," +
                    "DBStatus,ItemConn,PostAs," +
                    "EncodeType,ReceiptType,Mgt01YN," +
                    "BranchCode, LocationCode, Modules," +
                    "DepartmentCode,CompCustomerYN,AdminPassword," +
                    "CategoryCode,SalesPersonCode,DirectPrintYN," +
                    "UserCode,CounterCode,FastKeypadYN)" +
                    "values('" + ServerName + "', '" + UserName + "', '" + Password + "'," +
                    " '" + DBName + "', '" + Port + "', '" + ConnYN + "'," +
                    " '" + DBStatus + "', '" + ItemConn + "','0'," +
                    " 'UTF-8', 'Normal','" + Mgt01YN + "'," +
                    " '" + BranchCode + "', '" + LocationCode + "', '" + Modules + "'," +
                    " '" + DepartmentCode + "','" + CompCustomerYN + "', '" + AdminPassword + "'," +
                    " '" + CategoryCode + "','" + SalesPersonCode + "', '" + DirectPrintYN + "'," +
                    " '" + UserCode + "', '" + CounterCode + "', '"+FastKeypadYN+"')";
            Log.d("INSERT SETTING", query);

            if (CloudSettingYN.equals("0")) {
                String qDel = "delete from companysetup";
                db.addQuery(qDel);
                String qInsertCom = "insert into companysetup (CurCode,CompanyName,CompanyCode," +
                        " GSTNo, Address, Tel1, " +
                        " Fax1, CompanyEmail, ComTown, " +
                        " ComState, ComCountry, Footer_CR)values(" +
                        " 'RM', '" + CompanyName + "', '', " +
                        " 'NO', '" + CompanyAddress + "', ''," +
                        " '', '', ''," +
                        " '', '', '')";
                db.addQuery(qInsertCom);
                db.DeleteGeneralSetup();
                db.addGeneral("RM", "NO", CompanyName, "", "", "", "");
            }


            long result = db.addQuery(query);
            if (result > 0) {
                Toast.makeText(getContext(), "Setting,You have a successful update ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Setting, Record Failed Update ", Toast.LENGTH_SHORT).show();
            }
            //saveSetting saveSet=new saveSetting(getActivity(),MacAddress);
            //saveSet.execute();
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }

    }
    //RETRIEVE
    private void retrieve() {
        DBAdapter db=new DBAdapter(this.getContext());
        db.openDB();
        String sql="select ServerName,UserName,Password, " +
                " DBName, Port, ConnYN, " +
                " DBStatus, ItemConn, BranchCode, " +
                " LocationCode, Modules, AdminPassword, " +
                " UserCode,CounterCode"+
                " from tb_setting";
        Cursor c=db.getQuery(sql);
        while (c.moveToNext()) {
            //int RunNo=c.getInt(0);
            String ServerName   = c.getString(0);
            String UserName     = c.getString(1);
            String Password     = c.getString(2);
            String DBName       = c.getString(3);
            String Port         = c.getString(4);
            String ConnYN       = c.getString(5);
            String DBStatus     = c.getString(6);
            String ItemConn     = c.getString(7);
            String BranchCode   = c.getString(8);
            String LocationCode = c.getString(9);
            String UserCode     = c.getString(12);
            String CounterCode  = c.getString(13);
            ServerName_txt.setText(ServerName);
            UserName_txt.setText(UserName);
            Password_txt.setText(Password);
            DBName_txt.setText(DBName);
            Port_txt.setText(Port);
            txtModules.setText(c.getString(10));
            OldAdminPassword= c.getString(11);
            txtUserCode.setText(UserCode);
            txtCounterCode.setText(CounterCode);
            //txtLo.setText(Port);
           // SettingDB p=new SettingDB(RunNo,ServerName,UserName,Password,DBName,Port,ConnYN);
            if(ConnYN.equals("success")){
                btnSave.setEnabled(true);
            }else{
                btnSave.setEnabled(false);
            }
            Log.d("DBStatus LOCAL",DBStatus);
            setSpinnerDB(DBStatus);

            if(ItemConn.equals("1")){
                swItemConn.setChecked(true);
            }else{
                swItemConn.setChecked(false);
            }
            //settingdb.add(p);
        }
    }

    private void setSpinnerDB(String vStatus){
        String aStatus="";
        if(vStatus.equals("0")){
            aStatus="Localhost";
        }else if(vStatus.equals("1")){
            aStatus="Cloud Server";
        }else if(vStatus.equals("2")){
            aStatus="Android Server";
        }
        final String finalAStatus = aStatus;
        Log.d("DBStatus 2",finalAStatus);
        final ArrayList<String> list=new ArrayList( Arrays.asList(getResources().getStringArray(R.array.db_array)) );
        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int pos= list.indexOf(finalAStatus);
                spDBStatus.setSelection(pos);
            }
        },1000);
    }

    public class backupSetting extends AsyncTask<Void,Void,String>{
        Context c;
        String z,IPAddress,UserName,Password,DBName,Port,URL,MacAddress;
        public backupSetting(Context c, String MacAddress) {
            this.c = c;
            this.MacAddress = MacAddress;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.fngetServer();
        }

        @Override
        protected void onPostExecute(String isConnect) {
            super.onPostExecute(isConnect);
            if(isConnect.equals("error")){
                Toast.makeText(c,"Failure Backup Setting", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(c,"Success Backup Setting", Toast.LENGTH_SHORT).show();
            }
        }
        private String fngetServer(){
            try {
                DBAdapter db=new DBAdapter(c);
                db.openDB();
                URL url = new URL("http://skybiz.com.my/userlicensesetting/androidlicense.txt");
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String str;
                z="";
                int i=0;
                while ((str = in.readLine()) != null) {
                    Log.d("STRING",str);
                    fngetStr(i,str);
                    z +=str;
                    i++;
                }
                in.close();
                URL="jdbc:mysql://"+IPAddress+":"+Port+"/"+DBName;
                Connection conn= Connector.connect(URL, UserName, Password);
                if (conn == null) {
                    Log.d("ERROR",URL+UserName+Password);
                    z = "error";
                }else{
                    String query1="select RunNo,Prefix,LastNo from sys_runno_dt where RunNoCode='CS' ";
                    Cursor rsData1=db.getQuery(query1);
                    String PrefixCS="";
                    String LastNoCS="";
                    String PrefixSO="";
                    String LastNoSO="";
                    String PrefixCusCN="";
                    String LastNoCusCN="";
                    while (rsData1.moveToNext()) {
                        PrefixCS = rsData1.getString(1);
                        LastNoCS = rsData1.getString(2);

                    }
                    String query2="select RunNo,Prefix,LastNo from sys_runno_dt where RunNoCode='SO' ";
                    Cursor rsData2=db.getQuery(query2);
                    while (rsData2.moveToNext()) {
                        PrefixSO = rsData2.getString(1);
                        LastNoSO  = rsData2.getString(2);
                    }
                    String query3="select RunNo,Prefix,LastNo from sys_runno_dt where RunNoCode='CusCN' ";
                    Cursor rsData3=db.getQuery(query3);
                    while (rsData3.moveToNext()) {
                        PrefixCusCN = rsData3.getString(1);
                        LastNoCusCN = rsData3.getString(2);
                    }
                    String MainPrinterType="";
                    String MainPrinterIP="";
                    String MainPrinterPort="";
                    String qPrinter1="select TypePrinter, IFNULL(IPPrinter,'')IPPrinter, IFNULL(Port,'')as Port from tb_settingprinter ";
                    Cursor rsP=db.getQuery(qPrinter1);
                    while (rsP.moveToNext()) {
                        MainPrinterType = rsP.getString(0);
                        MainPrinterIP = rsP.getString(1);
                        if(MainPrinterIP.equals("null")){
                            MainPrinterIP="";
                        }
                        MainPrinterPort = rsP.getString(2);
                    }

                    String KitchenPrinterType="";
                    String KitchenPrinterIP="";
                    String KitchenPrinterPort="";
                    String qPrinter2="select TypePrinter, IFNULL(IPPrinter,'')IPPrinter, IFNULL(Port,'')as Port from tb_kitchenprinter ";
                    Cursor rsP2=db.getQuery(qPrinter2);
                    while (rsP2.moveToNext()) {
                        KitchenPrinterType = rsP2.getString(0);
                        KitchenPrinterIP = rsP2.getString(1);
                        if(KitchenPrinterIP.equals("null")){
                            KitchenPrinterIP="";
                        }
                        KitchenPrinterPort = rsP2.getString(2);
                    }

                    String ReceiptType="";
                    String ChineseCharSet="";
                    String CSPostingMode="";
                    String qSetting="select IFNULL(ReceiptType,'')as ReceiptType, IFNULL(EncodeType,'')as EncodeType, " +
                            "IFNULL(PostAs,'0')as  PostAs from tb_setting ";
                    Cursor rsSet=db.getQuery(qSetting);
                    while (rsSet.moveToNext()) {
                        ReceiptType = rsSet.getString(0);
                        ChineseCharSet = rsSet.getString(1);
                        CSPostingMode = rsSet.getString(2);
                    }

                    String update="update androidlicense set PrefixSO='"+PrefixSO+"', LastNoSO='"+LastNoSO+"'," +
                            " PrefixCS='"+PrefixCS+"', LastNoCS='"+LastNoCS+"', " +
                            " PrefixCusCN='"+PrefixCusCN+"', LastNoCusCN='"+LastNoCusCN+"'," +
                            " MainPrinterType='"+MainPrinterType+"', MainPrinterIP='"+MainPrinterIP+"'," +
                            " MainPrinterPort='"+MainPrinterPort+"', KitchenPrinterType='"+KitchenPrinterType+"'," +
                            " KitchenPrinterIP='"+KitchenPrinterIP+"', KitchenPrinterPort='"+KitchenPrinterPort+"',  " +
                            " ReceiptType='"+ReceiptType+"', ChineseCharSet='"+ChineseCharSet+"'," +
                            " CSPostingMode='"+CSPostingMode+"'   " +
                            " where MacAddress='"+MacAddress+"'  ";
                    Log.d("QUERY UP", update);
                    Statement stmtUp     = conn.createStatement();
                    stmtUp.execute(update);
                    z="success";
                }
                db.closeDB();
                return z;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return z;
        }
        private void fngetStr(int no,String str){
            int position = str.indexOf("=")+1;
            switch (no) {
                case 0:
                    IPAddress=str.substring(position,str.length());
                    break;
                case 1:
                    Log.d("STR",str+String.valueOf(position)+String.valueOf(str.length()-position));
                    Port=str.substring(position,str.length());
                    break;
                case 2:
                    UserName=str.substring(position,str.length());
                    break;
                case 3:
                    Password=str.substring(position,str.length());
                    break;
                case 4:
                    DBName=str.substring(position,str.length());
                    break;
            }
        }

    }


    public class saveSetting extends AsyncTask<Void,Void,String>{
        Context c;
        String z,IPAddress,UserName,Password,DBName,Port,URL,MacAddress;
        public saveSetting(Context c, String MacAddress) {
            this.c = c;
            this.MacAddress = MacAddress;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.fngetServer();
        }

        @Override
        protected void onPostExecute(String isConnect) {
            super.onPostExecute(isConnect);
            if(isConnect.equals("error")){
                Toast.makeText(c,"Failure Save Setting", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(c,"Success Save Setting", Toast.LENGTH_SHORT).show();
            }
        }
        private String fngetServer(){
            try {
                DBAdapter db=new DBAdapter(c);
                db.openDB();
                URL url = new URL("http://skybiz.com.my/userlicensesetting/androidlicense.txt");
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String str;
                z="";
                int i=0;
                while ((str = in.readLine()) != null) {
                    Log.d("STRING",str);
                    fngetStr(i,str);
                    z +=str;
                    i++;
                }
                in.close();
                URL="jdbc:mysql://"+IPAddress+":"+Port+"/"+DBName;
                Connection conn= Connector.connect(URL, UserName, Password);
                if (conn == null) {
                    Log.d("ERROR",URL+UserName+Password);
                    z = "error";
                }else{

                    String qSetting="select PrefixSO, LastNoSO, " +
                            " PrefixCS, LastNoCS, " +
                            " PrefixCusCN, LastNoCusCN," +
                            " MainPrinterType, MainPrinterIP," +
                            " MainPrinterPort, KitchenPrinterType," +
                            " KitchenPrinterIP, KitchenPrinterPort, "+
                            " ReceiptType, ChineseCharSet, " +
                            " CSPostingMode,SalesPersonCode, DirectPrintYN  " +
                            " from androidlicense  where MacAddress='"+MacAddress+"' ";
                    Statement stmtSet = conn.createStatement();
                    stmtSet.execute(qSetting);
                    ResultSet rsSet = stmtSet.getResultSet();
                    while (rsSet.next()) {
                        String PrefixSO=rsSet.getString(1);
                        String LastNoSO=rsSet.getString(2);
                        String PrefixCS=rsSet.getString(3);
                        String LastNoCS=rsSet.getString(4);
                        String PrefixCusCN=rsSet.getString(5);
                        String LastNoCusCN=rsSet.getString(6);
                        String MainPrinterType=rsSet.getString(7);
                        String MainPrinterIP=rsSet.getString(8);
                        String MainPrinterPort=rsSet.getString(9);
                        String KitchenPrinterType=rsSet.getString(10);
                        String KitchenPrinterIP=rsSet.getString(11);
                        String KitchenPrinterPort=rsSet.getString(12);
                        String ReceiptType=rsSet.getString(13);
                        String EncodeType=rsSet.getString(14);
                        String PostAs=rsSet.getString(15);
                        String sqlCheck="select count(*)as numrows from sys_runno_dt where RunNoCode='SO' ";
                        Cursor rsSO=db.getQuery(sqlCheck);
                        int numSO=0;
                        while (rsSO.moveToNext()) {
                            numSO = rsSO.getInt(0);
                        }
                        if(numSO==0){
                            String insert="insert into sys_runno_dt(RunNoCode,Prefix,LastNo)values('SO','"+PrefixSO+"','"+LastNoSO+"')";
                            db.addQuery(insert);
                        }else{
                            String update="update sys_runno_dt set Prefix='"+PrefixSO+"',LastNo='"+LastNoSO+"' where RunNoCode='SO' ";
                            db.addQuery(update);
                        }

                        String sqlCheck2="select count(*)as numrows from sys_runno_dt where RunNoCode='CS' ";
                        Cursor rsCS=db.getQuery(sqlCheck2);
                        int numCS=0;
                        while (rsCS.moveToNext()) {
                            numCS = rsCS.getInt(0);
                        }
                        if(numCS==0){
                            String insert="insert into sys_runno_dt(RunNoCode,Prefix,LastNo)values('CS','"+PrefixCS+"','"+LastNoCS+"')";
                            db.addQuery(insert);
                        }else{
                            String update="update sys_runno_dt set Prefix='"+PrefixCS+"',LastNo='"+LastNoCS+"' where RunNoCode='CS' ";
                            db.addQuery(update);
                        }

                        String sqlCheck3="select count(*)as numrows from sys_runno_dt where RunNoCode='CusCN' ";
                        Cursor rsCusCN=db.getQuery(sqlCheck3);
                        int numCusCN=0;
                        while (rsCusCN.moveToNext()) {
                            numCusCN = rsCusCN.getInt(0);
                        }
                        if(numCusCN==0){
                            String insert="insert into sys_runno_dt(RunNoCode,Prefix,LastNo)values('CusCN','"+PrefixCusCN+"','"+LastNoCusCN+"')";
                            db.addQuery(insert);
                        }else{
                            String update="update sys_runno_dt set Prefix='"+PrefixCusCN+"',LastNo='"+LastNoCusCN+"' where RunNoCode='CusCN' ";
                            db.addQuery(update);
                        }

                        String query="select count(*) as numrows from tb_settingprinter";
                        Cursor rsData=db.getQuery(query);
                        int numrows=0;
                        while(rsData.moveToNext()){
                            numrows=rsData.getInt(0);
                        }
                        if(numrows==0) {
                            String QueryAdd="Insert Into tb_settingprinter(TypePrinter,NamePrinter,IPPrinter,UUID,Port)" +
                                    " values('"+MainPrinterType+"', '', '"+MainPrinterIP+"', '', '"+MainPrinterPort+"')";
                            db.addQuery(QueryAdd);
                        }else {
                            String vDelete="delete from tb_settingprinter";
                            long rsDelete = db.addQuery(vDelete);
                            if (rsDelete != 0) {
                                String QueryAdd="Insert Into tb_settingprinter(TypePrinter,NamePrinter,IPPrinter,UUID,Port)" +
                                        " values('"+MainPrinterType+"', '', '"+MainPrinterIP+"', '', '"+MainPrinterPort+"')";
                                db.addQuery(QueryAdd);
                            }
                        }

                        String query2="select count(*) as numrows from tb_kitchenprinter";
                        Cursor rsData2=db.getQuery(query);
                        int numrows2=0;
                        while(rsData.moveToNext()){
                            numrows2=rsData2.getInt(0);
                        }
                        if(numrows2==0) {
                            String QueryAdd="Insert Into tb_kitchenprinter(TypePrinter,NamePrinter,IPPrinter,UUID,Port)" +
                                    " values('"+KitchenPrinterType+"', '', '"+KitchenPrinterIP+"', '', '"+KitchenPrinterPort+"')";
                            db.addQuery(QueryAdd);
                        }else {
                            String vDelete="delete from tb_kitchenprinter";
                            long rsDelete = db.addQuery(vDelete);
                            if (rsDelete != 0) {
                                String QueryAdd="Insert Into tb_kitchenprinter(TypePrinter,NamePrinter,IPPrinter,UUID,Port)" +
                                        " values('"+KitchenPrinterType+"', '', '"+KitchenPrinterIP+"', '', '"+KitchenPrinterPort+"')";
                                db.addQuery(QueryAdd);
                            }
                        }
                        String update="update tb_setting set PostAs='"+PostAs+"', EncodeType='"+EncodeType+"', " +
                                "ReceiptType='"+ReceiptType+"'  ";
                        db.addQuery(update);

                    }
                    z="success";
                }
                db.closeDB();
                return z;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return z;
        }
        private void fngetStr(int no,String str){
            int position = str.indexOf("=")+1;
            switch (no) {
                case 0:
                    IPAddress=str.substring(position,str.length());
                    break;
                case 1:
                    Log.d("STR",str+String.valueOf(position)+String.valueOf(str.length()-position));
                    Port=str.substring(position,str.length());
                    break;
                case 2:
                    UserName=str.substring(position,str.length());
                    break;
                case 3:
                    Password=str.substring(position,str.length());
                    break;
                case 4:
                    DBName=str.substring(position,str.length());
                    break;
            }
        }

    }

}
