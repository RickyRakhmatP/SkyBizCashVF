package skybiz.com.posoffline;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;
//import skybiz.com.cashoff.m_Tax.DownloaderTax;


/**
 * Created by 7 on 14/12/2017.
 */

public class Dialog_SpvPass extends DialogFragment {
    View view;
    Button btnCancel,btnConfirm;
    EditText txtPassword;
    String RunNo,Printer,T_ype;
    MyKeyboard keyboard;
    String jsonData;

    TextView txtHeader;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view        = inflater.inflate(R.layout.dialog_spv, container, false);
        btnCancel   =(Button)view.findViewById(R.id.btnCancel);
        btnConfirm  =(Button)view.findViewById(R.id.btnConfirm);
        txtPassword =(EditText) view.findViewById(R.id.txtPassword);
        keyboard    =(MyKeyboard)view.findViewById(R.id.keyboard);
        txtHeader   =(TextView)view.findViewById(R.id.txtHeader);
        T_ype       =this.getArguments().getString("TYPE_KEY");
        RunNo       = this.getArguments().getString("RUNNO_KEY");
        Printer     = this.getArguments().getString("PRINTER_KEY");
        jsonData    = this.getArguments().getString("JSONDATA_KEY");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPassword();
            }
        });
        txtPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //txtPassword.setInputType(0);
                txtPassword.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                txtPassword.setTextIsSelectable(true);
                InputConnection ic = txtPassword.onCreateInputConnection(new EditorInfo());
                keyboard.setInputConnection(ic);
                return false;
            }
        });
        initview();
        return view;
    }
    private void initview(){
        txtHeader.setText("Supervisor Password");
        //txtPassword.setInputType(0);
        InputConnection ic = txtPassword.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        txtPassword.setTextIsSelectable(true);
        // txtPassword.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        txtPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());

        txtPassword.setInputType(InputType.TYPE_NULL);
        //InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        // mgr.hideSoftInputFromWindow(txtPassword, 0);
        //txtNewPassword.setTransformationMethod(new ChangeAdminPassword.AsteriskPasswordTransformationMethod());

    }

    public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new AsteriskPasswordTransformationMethod.PasswordCharSequence(source);
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
    };

    private void checkPassword(){
        String Password=txtPassword.getText().toString();
        CheckPassword checkPassword=new CheckPassword(getActivity(),Password);
        checkPassword.execute();
    }

    public class CheckPassword extends AsyncTask<Void,Void,String> {
        Context c;
        String vPassword;
        String IPAddress,DBName,UserName,Password,URL,Port,DBStatus,UserCode,z;

        public CheckPassword(Context c,String vPassword) {
            this.c = c;
            this.vPassword=vPassword;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try{
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName,UserName,Password," +
                        "DBName,Port,DBStatus," +
                        "UserCode" +
                        " from tb_setting";
                Cursor cur1=db.getQuery(querySet);
                while (cur1.moveToNext()) {
                    IPAddress = cur1.getString(0);
                    UserName = cur1.getString(1);
                    Password = cur1.getString(2);
                    DBName = cur1.getString(3);
                    Port= cur1.getString(4);
                    DBStatus= cur1.getString(5);
                    UserCode= cur1.getString(6);
                }
                String qCheck="select count(*)as numrows from sys_userprofile_hd " +
                        "where Password2='"+vPassword+"' and DepartmentCode='Supervisor' ";
                int numrows=0;
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if(conn!=null) {
                        Statement stmtCheck = conn.createStatement();
                        stmtCheck.execute(qCheck);
                        ResultSet rsCheck=stmtCheck.getResultSet();
                        while(rsCheck.next()){
                            numrows=rsCheck.getInt(1);
                        }
                        if(numrows>0){
                            z="success";
                        }else{
                            z="error";
                        }
                    }
                }else{
                    Cursor rsCheck=db.getQuery(qCheck);
                    while(rsCheck.moveToNext()){
                        numrows=rsCheck.getInt(0);

                    }
                    if(numrows>0){
                        z="success";
                    }else{
                        z="error";
                    }
                }
                return z;
            }catch (Error e){
                e.printStackTrace();
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }
            return z;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                if(T_ype.equals("Delete Order")) {
                   // ((RetHold) c).fndelete(RunNo, Printer);
                }else if(T_ype.equals("Cancel Order")){
                   // ((RetHold)getActivity()).fncancel();
                }else if(T_ype.equals("Edit Order")){
                    ((CashReceipt)getActivity()).showEdit(jsonData);
                }
                dismiss();
            }else if(result.equals("error")){
                Toast.makeText(c,"Wrong Password !", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
