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
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Item.ItemListing;
import skybiz.com.posoffline.ui_ItemGroup.ItemGroupList;
import skybiz.com.posoffline.ui_Listing.Listing;
import skybiz.com.posoffline.ui_Member.m_MemberList.MemberList;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 23/02/2018.
 */

public class DialogAdminPass extends DialogFragment {
    View view;
    Button btnOK;
    EditText txtPassword;
    MyKeyboard keyboard;
    String ItemCode,T_ype,UFrom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_password, container, false);
        btnOK=(Button)view.findViewById(R.id.btnOK);
        txtPassword=(EditText)view.findViewById(R.id.txtPassword);
        ItemCode=this.getArguments().getString("ITEMCODE_KEY");
        T_ype=this.getArguments().getString("TYPE_KEY");
        UFrom=this.getArguments().getString("UFROM_KEY");
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fncheck();
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

        initView();

        return view;
    }
    private  void initView(){
        InputConnection ic=txtPassword.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        txtPassword.setTextIsSelectable(true);
        txtPassword.setInputType(0);
        txtPassword.setRawInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        txtPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());

    }
    private void fnOK(){
        if(T_ype.equals("edit")){
           // fnedit();
        }else if(T_ype.equals("delete")){
            //fndelete();
        }
    }
    private void fncheck(){
        try {
            DBAdapter db=new DBAdapter(getActivity());
            db.openDB();
            String Password = txtPassword.getText().toString();
            String qPassword="select AdminPassword from tb_setting";
            Cursor rsPass=db.getQuery(qPassword);
            String AdminPassword="";
            while(rsPass.moveToNext()){
                AdminPassword=rsPass.getString(0);
            }
            if(AdminPassword.isEmpty()){
                Toast.makeText(getActivity(),"Please setting Admin Password", Toast.LENGTH_SHORT).show();
            }else{
                if(Password.equals(AdminPassword)){
                    if(UFrom.equals("item")) {
                        if(T_ype.equals("edit")) {
                            ((ItemListing) getActivity()).setEdit(ItemCode);
                        }else if(T_ype.equals("delete")){
                            ((ItemListing) getActivity()).setDeleteItem(ItemCode);
                        }
                    }else if(UFrom.equals("member")){
                        if(T_ype.equals("edit")) {

                        }else if(T_ype.equals("delete")){
                            ((MemberList) getActivity()).setDeleteMember(ItemCode);
                        }
                    }else if(UFrom.equals("itemgroup")){
                        if(T_ype.equals("edit")) {
                            ((ItemGroupList) getActivity()).setEditGroup(ItemCode);
                        }else if(T_ype.equals("delete")){
                            ((ItemGroupList) getActivity()).setDeleteGroup(ItemCode);
                        }
                    }else if(UFrom.equals("listing")){
                        if(T_ype.equals("delete")){
                            ((Listing) getActivity()).setDeleteList(ItemCode);
                        }
                    }
                    dismiss();
                }else{
                    Toast.makeText(getActivity(),"Invalid Admin Password", Toast.LENGTH_SHORT).show();
                }
            }
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private void fndelete(){
        try {
            DBAdapter db=new DBAdapter(getActivity());
            db.openDB();
            String Password = txtPassword.getText().toString();
            String qPassword="select AdminPassword from tb_setting";
            Cursor rsPass=db.getQuery(qPassword);
            String AdminPassword="";
            while(rsPass.moveToNext()){
                AdminPassword=rsPass.getString(0);
            }
            if(AdminPassword.isEmpty()){
                Toast.makeText(getActivity(),"Please setting Admin Password", Toast.LENGTH_SHORT).show();
            }else{
                if(Password.equals(AdminPassword)){
                    if(UFrom.equals("item")) {
                        ((ItemListing) getActivity()).setDeleteItem(ItemCode);
                    }else if(UFrom.equals("member")){
                        ((MemberList) getActivity()).setDeleteMember(ItemCode);
                    }
                    dismiss();
                }else{
                    Toast.makeText(getActivity(),"Invalid Admin Password", Toast.LENGTH_SHORT).show();
                }
            }
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
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
