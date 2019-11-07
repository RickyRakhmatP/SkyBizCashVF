package skybiz.com.posoffline.ui_Member.m_MemberList;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.james.mime4j.field.address.ASTaddr_spec;
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
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Order;
import skybiz.com.posoffline.ui_CashReceipt.m_ListSO.Fragment_ListSO;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItem_ByDesc;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItem_BySearch;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.DownloaderOrder;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.OrderAdapter;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 11/12/2017.
 */

public class Fragment_AddNew extends Fragment {
    View view;
    Spinner spMemberType,spCategoryCode,spSex,spRace,spMaritialStatus;
    EditText txtCardNo,txtCusName,txtContactTel,txtAddress,txtEmail,txtPassword;
    TextView txtDOB,txtRegistrationDate,txtExpirationDate;
    DatePickerDialog datePickerDialog;
    Button btnSave;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_addnew_member, container, false);
        spMemberType=(Spinner)view.findViewById(R.id.spMemberType);
        spCategoryCode=(Spinner)view.findViewById(R.id.spCategoryCode);
        spSex=(Spinner)view.findViewById(R.id.spSex);
        spRace=(Spinner)view.findViewById(R.id.spRace);
        spMaritialStatus=(Spinner)view.findViewById(R.id.spMaritialStatus);
        txtCardNo=(EditText)view.findViewById(R.id.txtCardNo);
        txtCusName=(EditText)view.findViewById(R.id.txtCusName);
        txtContactTel=(EditText)view.findViewById(R.id.txtContactTel);
        txtAddress=(EditText)view.findViewById(R.id.txtAddress);
        txtEmail=(EditText)view.findViewById(R.id.txtEmail);
        txtPassword=(EditText)view.findViewById(R.id.txtPassword);
        txtDOB=(TextView) view.findViewById(R.id.txtDOB);
        txtRegistrationDate=(TextView) view.findViewById(R.id.txtRegistrationDate);
        txtExpirationDate=(TextView) view.findViewById(R.id.txtExpirationDate);
        btnSave=(Button)view.findViewById(R.id.btnSave);
        txtDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c=Calendar.getInstance();
                int mYear=c.get(Calendar.YEAR);
                int mMonth=c.get(Calendar.MONTH);
                int mDay=c.get(Calendar.DAY_OF_MONTH);
                final DecimalFormat mFormat= new DecimalFormat("00");
                datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        final Double dMonth=(monthOfYear+1)*1.00;
                        final Double dDay=dayOfMonth*1.00;
                        txtDOB.setText(year+"-"+mFormat.format(dMonth)+"-"+mFormat.format(dDay));
                    }
                },mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });
        txtRegistrationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c=Calendar.getInstance();
                int mYear=c.get(Calendar.YEAR);
                int mMonth=c.get(Calendar.MONTH);
                int mDay=c.get(Calendar.DAY_OF_MONTH);
                final DecimalFormat mFormat= new DecimalFormat("00");
                datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        final Double dMonth=(monthOfYear+1)*1.00;
                        final Double dDay=dayOfMonth*1.00;
                        txtRegistrationDate.setText(year+"-"+mFormat.format(dMonth)+"-"+mFormat.format(dDay));
                    }
                },mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });
        txtExpirationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c=Calendar.getInstance();
                int mYear=c.get(Calendar.YEAR);
                int mMonth=c.get(Calendar.MONTH);
                int mDay=c.get(Calendar.DAY_OF_MONTH);
                final DecimalFormat mFormat= new DecimalFormat("00");
                datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        final Double dMonth=(monthOfYear+1)*1.00;
                        final Double dDay=dayOfMonth*1.00;
                        txtExpirationDate.setText(year+"-"+mFormat.format(dMonth)+"-"+mFormat.format(dDay));
                    }
                },mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnsave();
            }
        });
        initData();
        return view;
    }

    private void initData(){
        fnloadmembertype();
        fnloadmarried();
        fnloadsex();
        fnloadras();
        fnloadcategory();
        fnloadmemberid();
        //fnloadretclass();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dated = sdf.format(date);
        txtRegistrationDate.setText(dated);

        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, 1); // to get previous year add -1
        Date nextYear = cal.getTime();
        String expdate = sdf.format(nextYear);
        txtExpirationDate.setText(expdate);
    }
    private void fnloadmembertype(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.membertype_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMemberType.setAdapter(adapter);
    }
     private void fnloadmarried(){
         ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                 getActivity(), R.array.married_array, android.R.layout.simple_spinner_item);
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
         spMaritialStatus.setAdapter(adapter);
     }

    private void fnloadsex(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.sex_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSex.setAdapter(adapter);
    }

    private void fnloadras(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.ras_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRace.setAdapter(adapter);
    }

    private void fnloadretclass(){
        RetMemberClass retMemberClass=new RetMemberClass(getActivity());
        retMemberClass.execute();
    }

    private void fnloadmemberid(){
        RetLastMemberID retLastMemberID=new RetLastMemberID(getActivity());
        retLastMemberID.execute();
    }
    private  void fnloadcategory(){
        RetCategory retCategory=new RetCategory(getActivity());
        retCategory.execute();
    }
    private void fnreset(){
        txtCusName.getText().clear();
        txtAddress.getText().clear();
        txtContactTel.getText().clear();
        txtCardNo.getText().clear();
        txtCusName.requestFocus();
    }

    private void fnsave(){
        try {
            String CusCode = txtCardNo.getText().toString();
            String CusName=txtCusName.getText().toString();
            String CategoryCode=spCategoryCode.getSelectedItem().toString();
            String Sex=spSex.getSelectedItem().toString();
            String Race=spRace.getSelectedItem().toString();
            String MaritialStatus=spMaritialStatus.getSelectedItem().toString();
            JSONArray results = new JSONArray();
            JSONObject row = new JSONObject();
            row.put("CusCode", CusCode);
            row.put("CusName", CusName);
            row.put("Address", txtAddress.getText().toString());
            row.put("ContactTel", txtContactTel.getText().toString());
            row.put("CategoryCode",CategoryCode);
            row.put("Sex", Sex);
            row.put("Race", Race);
            row.put("MemberType", "Silver Member");
            row.put("MaritialStatus", MaritialStatus);
            row.put("DOB", txtDOB.getText().toString());
            row.put("RegistrationDate", txtRegistrationDate.getText().toString());
            row.put("ExpirationDate", txtExpirationDate.getText().toString());
            row.put("Email", txtEmail.getText().toString());
            row.put("Password", txtPassword.getText().toString());
            results.put(row);
            if(CusCode.isEmpty()){
                Toast.makeText(getActivity(),"Card No Cannot Empty", Toast.LENGTH_SHORT).show();
                txtCardNo.requestFocus();
            }else if(CusName.isEmpty()){
                Toast.makeText(getActivity(),"Customer Name Cannot Empty", Toast.LENGTH_SHORT).show();
                txtCusName.requestFocus();
            }else if(CategoryCode.isEmpty()){
                Toast.makeText(getActivity(),"Category Code Cannot Empty", Toast.LENGTH_SHORT).show();
                spCategoryCode.requestFocus();
            }else if(Sex.isEmpty()){
                Toast.makeText(getActivity(),"Sex Cannot Empty", Toast.LENGTH_SHORT).show();
                spSex.requestFocus();
            }else if(Race.isEmpty()){
                Toast.makeText(getActivity(),"Race Cannot Empty", Toast.LENGTH_SHORT).show();
                spRace.requestFocus();
            }else if(MaritialStatus.isEmpty()){
                Toast.makeText(getActivity(),"Maritial Status Cannot Empty", Toast.LENGTH_SHORT).show();
                spMaritialStatus.requestFocus();
            }else {
                AddNewMember add = new AddNewMember(getActivity(), results.toString());
                add.execute();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
       // String vData=results.toString();
    }

    public class AddNewMember extends AsyncTask<Void,Void,String>{
        Context c;
        String vData;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;

        public AddNewMember(Context c, String vData) {
            this.c = c;
            this.vData = vData;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.savemember();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                Toast.makeText(c,"successfull add new member", Toast.LENGTH_SHORT).show();
                fnreset();
            }else{
                Toast.makeText(c,"failed add new member", Toast.LENGTH_SHORT).show();
            }
        }

        private String savemember() {
            try {
                z="error";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String D_ateTime = sdf.format(date);
                String D_ate = sdf2.format(date);
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
                }
                JSONArray ja=new JSONArray(vData);
                JSONObject jo=null;
                String CusCode="";
                String CusName="";
                String Address="";
                String CategoryCode="";
                String ContactTel="";
                String MemberType="";
                String DOB="";
                String Race="";
                String MaritialStatus="";
                String Sex="";
                String RegistrationDate="";
                String ExpirationDate="";
                String Email="";
                String vPassword="";
                for (int i=0;i<ja.length();i++) {
                    jo = ja.getJSONObject(i);
                    CusCode = jo.getString("CusCode");
                    CusName = jo.getString("CusName");
                    Address = jo.getString("Address");
                    CategoryCode = jo.getString("CategoryCode");
                    ContactTel = jo.getString("ContactTel");
                    MemberType = jo.getString("MemberType");
                    DOB = jo.getString("DOB");
                    Race = jo.getString("Race");
                    Sex = jo.getString("Sex");
                    RegistrationDate = jo.getString("RegistrationDate");
                    ExpirationDate = jo.getString("ExpirationDate");
                    MaritialStatus = jo.getString("MaritialStatus");
                    Email = jo.getString("Email");
                    vPassword = jo.getString("Password");
                }
                String insert="insert into customer(CusCode,CusName,FinCatCode," +
                        "AccountCode,Address,CurCode," +
                        "TermCode,D_ay, SalesPersonCode," +
                        "Tel,Tel2,Fax," +
                        "Fax2,Contact,ContactTel," +
                        "Email,StatusBadYN, Town," +
                        "State,Country,PostCode," +
                        "L_ink,NRICNo,DOB," +
                        "Sex,MemberType,CardNo," +
                        "PaymentCode,DateTimeModified,CategoryCode," +
                        "RegistrationDate,ExpirationDate,MaritialStatus," +
                        "Race,DateStart," +
                        "P_assword)values('"+CusCode+"', '"+CusName+"', 'B55'," +
                        "'B55-0000', '"+Address+"', ''," +
                        "'1', '0', ''," +
                        "'', '', ''," +
                        "'', '', '"+ContactTel+"'," +
                        "'"+Email+"', '0', ''," +
                        "'', '', ''," +
                        "'1', '', '"+DOB+"'," +
                        "'"+Sex+"', '"+MemberType+"', '"+CusCode+"'," +
                        "'', '"+D_ateTime+"', '"+CategoryCode+"'," +
                        "'"+RegistrationDate+"', '"+ExpirationDate+"', '"+MaritialStatus+"'," +
                        "'"+Race+"', '"+D_ate+"'," +
                        "'"+vPassword+"')";
                Log.d("QUERY",insert);
                if (DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement stmtInsert = conn.createStatement();
                        stmtInsert.execute(insert);
                        z="success";
                    }else{
                        z="error";
                    }
                }else{
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
    public class RetMemberClass extends AsyncTask<Void,Void,String>{
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn;
        ArrayList lsMemberClass=new ArrayList<String>();

        public RetMemberClass(Context c) {
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
                spCategoryCode.setAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,lsMemberClass));
            }else{
                Toast.makeText(c,"ret membership class empty", Toast.LENGTH_SHORT).show();
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
                String sql ="select Class, Description from ret_membership_class Order By Class ";
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
                            lsMemberClass.add(resultSet.getString(1));
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
                        lsMemberClass.add(rsGroup.getString(0));
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

    public class RetLastMemberID extends AsyncTask<Void,Void,String>{
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus;
        String LastMemberID="";

        public RetLastMemberID(Context c) {
            this.c = c;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fngetlastno();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("success")){
                txtCardNo.setText(LastMemberID);
                Toast.makeText(c,"new Member ID "+LastMemberID, Toast.LENGTH_SHORT).show();
            }else if(s.equals("error")){
                Toast.makeText(c,"error get last member id", Toast.LENGTH_SHORT).show();
            }
        }

        private String fngetlastno(){
            try{
                DBAdapter db=new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName,UserName,Password," +
                        "DBName,Port,DBStatus," +
                        "CategoryCode from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(0);
                    UserName = curSet.getString(1);
                    Password = curSet.getString(2);
                    DBName = curSet.getString(3);
                    Port = curSet.getString(4);
                    DBStatus=curSet.getString(5);

                }
                String sql ="select CusCode from customer where FinCatCode='B55'  " +
                        "  order by RunNo Desc limit 1  ";
                String OldMember="";
                String NewMember="100001";
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement stmt = conn.createStatement();
                        stmt.execute(sql);
                        ResultSet rsData = stmt.getResultSet();
                        int i = 0;
                        while (rsData.next()) {
                            OldMember=rsData.getString(1);
                            i++;
                        }
                        if(i>0){
                            String vNewMember   = "1"+OldMember;
                            int iNewNo          = (Integer.parseInt(vNewMember)) + 1;
                            NewMember           = String.valueOf(iNewNo);
                            LastMemberID        = NewMember.substring(1,NewMember.length());
                        }else{
                            LastMemberID=NewMember;
                        }
                        z="success";
                    }

                }else if(DBStatus.equals("0")){
                    Cursor rsData=db.getQuery(sql);
                    int i = 0;
                    while (rsData.moveToNext()) {
                        OldMember=rsData.getString(0);
                        i++;
                    }
                    if(i>0){
                        String vNewMember   = "1"+OldMember;
                        int iNewNo          = (Integer.parseInt(vNewMember)) + 1;
                        NewMember           = String.valueOf(iNewNo);
                        LastMemberID        = NewMember.substring(1,NewMember.length());
                    }else{
                        LastMemberID=NewMember;
                    }
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

    public class RetCategory extends AsyncTask<Void,Void,String>{
        Context c;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus,ItemConn,CategoryCode;
        ArrayList lsCategory=new ArrayList<String>();

        public RetCategory(Context c) {
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
                spCategoryCode.setAdapter(new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item,lsCategory));
            }else{
                Toast.makeText(c,"ret membership class empty", Toast.LENGTH_SHORT).show();
            }
        }

        private String downloadData() {
            try {
                DBAdapter db=new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName,UserName,Password," +
                        "DBName,Port,DBStatus," +
                        "CategoryCode from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(0);
                    UserName = curSet.getString(1);
                    Password = curSet.getString(2);
                    DBName = curSet.getString(3);
                    Port = curSet.getString(4);
                    DBStatus=curSet.getString(5);
                    CategoryCode=curSet.getString(6);
                }
                String sql ="select CategoryCode from stk_category where CategoryFor='Customer'  ";
                lsCategory.add(CategoryCode);
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
                            lsCategory.add(resultSet.getString(1));
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
                        lsCategory.add(rsGroup.getString(0));
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
}
