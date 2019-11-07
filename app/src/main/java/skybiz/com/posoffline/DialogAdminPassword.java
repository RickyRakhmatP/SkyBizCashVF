package skybiz.com.posoffline;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.MainActivity;
import skybiz.com.posoffline.MyKeyboard;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 23/02/2018.
 */

public class DialogAdminPassword extends DialogFragment {
    View view;
    Button btnOK;
    EditText txtNewPassword,txtCurrentPassword;
    MyKeyboard keyboard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_adminpassword, container, false);
        btnOK=(Button)view.findViewById(R.id.btnOK);
        txtNewPassword=(EditText)view.findViewById(R.id.txtNewPassword);
        txtCurrentPassword=(EditText)view.findViewById(R.id.txtCurrentPassword);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changepassword();
            }
        });
        keyboard=(MyKeyboard)view.findViewById(R.id.keyboard);

        txtNewPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtNewPassword.setInputType(0);
                txtNewPassword.setRawInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                InputConnection ic2=txtNewPassword.onCreateInputConnection(new EditorInfo());
                keyboard.setInputConnection(ic2);
                return false;
            }
        });

        txtCurrentPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtCurrentPassword.setInputType(0);
                txtCurrentPassword.setRawInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                InputConnection ic2=txtCurrentPassword.onCreateInputConnection(new EditorInfo());
                keyboard.setInputConnection(ic2);
                return false;
            }
        });

        initView();

        return view;
    }
    private  void initView(){
        InputConnection ic=txtNewPassword.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        txtNewPassword.setTextIsSelectable(true);
        txtNewPassword.setInputType(0);
        txtNewPassword.setRawInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        txtNewPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());

        InputConnection ic2=txtCurrentPassword.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic2);
        txtCurrentPassword.setTextIsSelectable(true);
        txtCurrentPassword.setInputType(0);
        txtCurrentPassword.setRawInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        txtCurrentPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
    }
    private void changepassword(){
        String NewPassword=txtNewPassword.getText().toString();
        String CurrentPassword=txtCurrentPassword.getText().toString();
        if(!NewPassword.isEmpty()) {
            try{
                DBAdapter db=new DBAdapter(getActivity());
                db.openDB();
                String checkPass    ="select IFNULL(AdminPassword,'')as AdminPassword from tb_setting";
                Cursor rsCheck      =db.getQuery(checkPass);
                String OldPassword="";
                while (rsCheck.moveToNext()) {
                    OldPassword =rsCheck.getString(0);
                }
                if(CurrentPassword.equals(OldPassword)) {
                    String qUpdate="update tb_setting set AdminPassword='"+NewPassword+"' ";
                    db.addQuery(qUpdate);
                    Toast.makeText(getActivity(),"Change Admin Password Successful", Toast.LENGTH_SHORT).show();
                    dismiss();
                }else{
                    Toast.makeText(getActivity(),"Invalid Current Password", Toast.LENGTH_SHORT).show();
                }
                db.closeDB();
            }catch (SQLiteException e){
                e.printStackTrace();
            }
           // ChangeAdminPassword change = new ChangeAdminPassword(getActivity(), CurrentPassword, NewPassword);
            //change.execute();
        }else{
            Toast.makeText(getActivity(),"New Password Password cannot Empty", Toast.LENGTH_SHORT).show();
        }
    }
    public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;

            public PasswordCharSequence(CharSequence source) {
                mSource = source; // Store char sequence
            }

            public char charAt(int index) {
                return '*'; // This is the important part
            }

            public int length() {
                return mSource.length(); // Return default
            }

            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end); // Return default
            }
        }
    }

    public class ChangeAdminPassword extends AsyncTask<Void,Void,String>{
        Context c;
        String CurrentPassword,NewPassword;
        String IPAddress,DBName,Password,Port,z,URL,UserName,MacAddress;

        public ChangeAdminPassword(Context c, String currentPassword, String newPassword) {
            this.c = c;
            CurrentPassword = currentPassword;
            NewPassword = newPassword;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.fnchangepass();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("error")){
                Toast.makeText(c,"Connection Failure", Toast.LENGTH_SHORT).show();
            }else if(result.equals("invalid")){
                Toast.makeText(c,"Invalid Current Password", Toast.LENGTH_SHORT).show();
            }else if(result.equals("success")){
                Toast.makeText(c,"Admin Password has been update", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }
        private String fnchangepass(){
            try{
                MacAddress= Settings.Secure.getString(getActivity().getContentResolver(),Settings.Secure.ANDROID_ID);
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
                    String checkPass        ="select count(*)as numrows from androidlicense where MACAddress='"+MacAddress+"' and AdminPassword='"+CurrentPassword+"' ";
                    Statement stmtCheck     = conn.createStatement();
                    stmtCheck.execute(checkPass);
                    ResultSet rsPass = stmtCheck.getResultSet();
                    int numrows=0;
                    while (rsPass.next()) {
                        numrows=rsPass.getInt(1);
                    }
                    if(numrows>0) {
                        String update="update androidlicense set AdminPassword='"+NewPassword+"' where MACAddress='"+MacAddress+"'  ";
                        Statement stmtUp = conn.createStatement();
                        stmtUp.execute(update);
                        String qUpdate="update tb_setting set AdminPassword='"+NewPassword+"' ";
                        db.addQuery(qUpdate);
                        z="success";
                    }else{
                        z="invalid";
                    }
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
