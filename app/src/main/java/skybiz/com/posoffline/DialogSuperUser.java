package skybiz.com.posoffline;

import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.Date;


import skybiz.com.posoffline.m_dbms.AndroidDatabaseManager;
//import skybiz.com.cashoff.m_Tax.DownloaderTax;


/**
 * Created by 7 on 14/12/2017.
 */

public class DialogSuperUser extends DialogFragment {
    View view;
    Button btnOK;
    EditText txtPassword;
    MyKeyboard keyboard;
    String T_ype,Code,UFrom;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_password, container, false);
        btnOK=(Button)view.findViewById(R.id.btnOK);
        txtPassword=(EditText) view.findViewById(R.id.txtPassword);
        keyboard=(MyKeyboard)view.findViewById(R.id.keyboard);
        T_ype=this.getArguments().getString("TYPE_KEY");
        Code=this.getArguments().getString("CODE_KEY");
        UFrom=this.getArguments().getString("UFROM_KEY");
        btnOK.setOnClickListener(new View.OnClickListener() {
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
        InputConnection ic = txtPassword.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        txtPassword.setTextIsSelectable(true);
        txtPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        txtPassword.setInputType(InputType.TYPE_NULL);
    }

    private void checkPassword(){
        SimpleDateFormat sdf = new SimpleDateFormat("yy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM");
        SimpleDateFormat sdf3 = new SimpleDateFormat("dd");
        Date date = new Date();
        String Years    = sdf.format(date);
        String Months   = sdf2.format(date);
        String Dates    = sdf3.format(date);
        String Password =txtPassword.getText().toString();
        if(!Password.isEmpty()) {
            long lYear = Long.parseLong(Years);
            long lMonth = Long.parseLong(Months);
            long lDates = Long.parseLong(Dates);
            long securePass = lYear * lMonth * lDates;
            long lPass = Long.parseLong(Password);
            if (lPass == securePass) {
                if (UFrom.equals("mainactivity")) {
                    dismiss();
                    Intent dbmanager = new Intent(getActivity(), AndroidDatabaseManager.class);
                    startActivity(dbmanager);
                }
            } else {
                Toast.makeText(getContext(), "Password Not Match, Access Denied ", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getContext(), "Password Cannot Empty ", Toast.LENGTH_SHORT).show();
        }
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

}
