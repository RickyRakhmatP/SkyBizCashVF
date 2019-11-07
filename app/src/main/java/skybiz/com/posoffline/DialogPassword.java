package skybiz.com.posoffline;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.Fragment_Payment;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.FnAddOnOrder;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.FnSaveOrder;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 23/02/2018.
 */

public class DialogPassword extends DialogFragment {
    View view;
    Button btnOK;
    EditText txtPassword;
    MyKeyboard keyboard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_password, container, false);
        btnOK=(Button)view.findViewById(R.id.btnOK);
        txtPassword=(EditText)view.findViewById(R.id.txtPassword);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPassword();
            }
        });
        keyboard=(MyKeyboard)view.findViewById(R.id.keyboard);

        txtPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtPassword.setInputType(0);
                txtPassword.setRawInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                InputConnection ic2=txtPassword.onCreateInputConnection(new EditorInfo());
                keyboard.setInputConnection(ic2);
                return false;
            }
        });

        InputConnection ic=txtPassword.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        txtPassword.setTextIsSelectable(true);
        txtPassword.setInputType(0);
        txtPassword.setRawInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        txtPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        return view;
    }
    private void checkPassword(){
        String Password=txtPassword.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        Date date = new Date();
        String D_ate = sdf.format(date);
        if(Password.equals(D_ate)){
            ((MainActivity)getActivity()).openMainMenu();
            dismiss();
        }else{
            Toast.makeText(getContext(),"invalid password", Toast.LENGTH_SHORT).show();
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
}
