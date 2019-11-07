package skybiz.com.posoffline.ui_Setting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import skybiz.com.posoffline.MainActivity;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI.FnCheckWifi;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 11/12/2017.
 */

public class Fragment_Prefix extends Fragment {

    View view;
    EditText txtPrefixCS,txtLastNoCS,txtPrefixSO,
            txtLastNoSO,txtPrefixCusCN,txtLastNoCusCN,
            txtPrefixService,txtLastNoService,txtAdminPassword,
            txtServiceCharges,txtReceiptHeader;
    Button btnBack,btnSave;
    Switch swPost,swEncodeType,
    swAutoSyncYN,swNewUOMYN,swSMobileYN;
    CheckBox chkGroupByItemYN,chkHidePayment;
    Spinner spReceiptType;
    TextView txtSMobileYN;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting_prefix, container, false);
        txtPrefixSO      =(EditText) view.findViewById(R.id.txtPrefixSO);
        txtLastNoSO      =(EditText) view.findViewById(R.id.txtLastNoSO);
        txtPrefixCS      =(EditText) view.findViewById(R.id.txtPrefixCS);
        txtLastNoCS      =(EditText) view.findViewById(R.id.txtLastNoCS);
        txtPrefixCusCN   =(EditText) view.findViewById(R.id.txtPrefixCusCN);
        txtLastNoCusCN   =(EditText) view.findViewById(R.id.txtLastNoCusCN);
        txtPrefixService  =(EditText) view.findViewById(R.id.txtPrefixService);
        txtLastNoService  =(EditText) view.findViewById(R.id.txtLastNoService);
        txtAdminPassword  =(EditText) view.findViewById(R.id.txtAdminPassword);
        txtServiceCharges  =(EditText) view.findViewById(R.id.txtServiceCharges);
        txtReceiptHeader  =(EditText) view.findViewById(R.id.txtReceiptHeader);
        btnBack          =(Button) view.findViewById(R.id.btnBack) ;
        btnSave          =(Button) view.findViewById(R.id.btnSave) ;
        swPost           =(Switch)view.findViewById(R.id.swPost);
        swEncodeType     =(Switch)view.findViewById(R.id.swEncodeType);
        spReceiptType    =(Spinner) view.findViewById(R.id.spReceiptType);
        swAutoSyncYN    =(Switch)view.findViewById(R.id.swAutoSyncYN);
        swNewUOMYN    =(Switch)view.findViewById(R.id.swNewUOMYN);
        swSMobileYN    =(Switch)view.findViewById(R.id.swSMobileYN);
        txtSMobileYN    =(TextView) view.findViewById(R.id.txtSMobileYN);
        chkGroupByItemYN=(CheckBox)view.findViewById(R.id.chkGroupByItemYN);
        chkHidePayment=(CheckBox)view.findViewById(R.id.chkHidePayment);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePrefix();
            }
        });
        swPost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    swPost.setText("Cash Sales");
                }else{
                    swPost.setText("Cash Receipt");
                }
            }
        });
        swEncodeType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    swEncodeType.setText("GBK");
                }else{
                    swEncodeType.setText("UTF-8");
                }
            }
        });

        swAutoSyncYN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    swAutoSyncYN.setText("YES");
                }else{
                    swAutoSyncYN.setText("NO");
                }
            }
        });
        swNewUOMYN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    swNewUOMYN.setText("New UOM");
                }else{
                    swNewUOMYN.setText("Old UOM");
                }
            }
        });
        swSMobileYN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                   // swSMobileYN.setText("Yes");
                    txtSMobileYN.setText("Sync In By Item Y/N?");
                }else{
                   // swSMobileYN.setText("No");
                    txtSMobileYN.setText("Sync In By Item Group Y/N?");
                }
            }
        });
        loadReceiptType();
        retPrefix();
        return view;
    }
    private void loadReceiptType(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    getActivity(), R.array.FormatReceipt, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spReceiptType.setAdapter(adapter);
    }
    private void retPrefix(){
        try {
            DBAdapter db = new DBAdapter(getActivity());
            db.openDB();
            String query1 = "select RunNo,Prefix,LastNo from sys_runno_dt where RunNoCode='CS' ";
            Cursor rsData1 = db.getQuery(query1);
            while (rsData1.moveToNext()) {
                String Prefix = rsData1.getString(1);
                String LastNo = rsData1.getString(2);
                txtPrefixCS.setText(Prefix);
                txtLastNoCS.setText(LastNo);
            }

            rsData1.close();
            String query2 = "select RunNo,Prefix,LastNo from sys_runno_dt where RunNoCode='SO' ";
            Cursor rsData2 = db.getQuery(query2);
            while (rsData2.moveToNext()) {
                String Prefix = rsData2.getString(1);
                String LastNo = rsData2.getString(2);
                txtPrefixSO.setText(Prefix);
                txtLastNoSO.setText(LastNo);
            }
            rsData2.close();

            String query3 = "select RunNo,Prefix,LastNo from sys_runno_dt where RunNoCode='CusCN' ";
            Cursor rsData3 = db.getQuery(query3);
            while (rsData3.moveToNext()) {
                String Prefix = rsData3.getString(1);
                String LastNo = rsData3.getString(2);
                txtPrefixCusCN.setText(Prefix);
                txtLastNoCusCN.setText(LastNo);
            }
            rsData3.close();

            String query4 = "select RunNo,Prefix,LastNo from sys_runno_dt where RunNoCode='Service' ";
            Cursor rsData4 = db.getQuery(query4);
            while (rsData4.moveToNext()) {
                String Prefix = rsData4.getString(1);
                String LastNo = rsData4.getString(2);
                txtPrefixService.setText(Prefix);
                txtLastNoService.setText(LastNo);
            }
            rsData4.close();

            String sqlPost = "select PostAs, EncodeType, ReceiptType" +
                    ",GroupByItemYN,AdminPassword,AutoSyncYN from tb_setting";
            Cursor rsP = db.getQuery(sqlPost);
            while (rsP.moveToNext()) {
                String PostAs = rsP.getString(0);
                String EncodeType = rsP.getString(1);
                String ReceiptType = rsP.getString(2);
                String GroupByItemYN = rsP.getString(3);
                String AdminPassword = rsP.getString(4);
                String AutoSyncYN = rsP.getString(5);
                if (PostAs.equals("1")) {
                    swPost.setChecked(true);
                } else {
                    swPost.setChecked(false);
                }
                if (EncodeType.equals("GBK")) {
                    swEncodeType.setChecked(true);
                } else {
                    swEncodeType.setChecked(false);
                }
                /*if (ReceiptType.equals("Customize")) {
                    swReceiptType.setChecked(true);
                } else {
                    swReceiptType.setChecked(false);
                }*/
                if (GroupByItemYN.equals("1")) {
                    chkGroupByItemYN.setChecked(true);
                } else {
                    chkGroupByItemYN.setChecked(false);
                }
                if (AutoSyncYN.equals("1")) {
                    swAutoSyncYN.setChecked(true);
                } else {
                    swAutoSyncYN.setChecked(false);
                }
                txtAdminPassword.setText(AdminPassword);

                ArrayList<String> list = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.FormatReceipt)));
                int pos = list.indexOf(ReceiptType);
                spReceiptType.setSelection(pos);
            }
            rsP.close();
            String qOtherSet = "select ServiceCharges, ReceiptHeader, NewUOMYN, " +
                    "HidePaymentYN, SMobileYN from tb_othersetting ";
            Cursor rsOther = db.getQuery(qOtherSet);
            while (rsOther.moveToNext()) {
                String ServiceCharge    = rsOther.getString(0);
                txtServiceCharges.setText(ServiceCharge);
                txtReceiptHeader.setText(rsOther.getString(1));
                String NewUOMYN         = rsOther.getString(2);
                String HidePaymentYN    = rsOther.getString(3);
                String SMobileYN        = rsOther.getString(4);
                if (NewUOMYN.equals("1")) {
                    swNewUOMYN.setChecked(true);
                } else {
                    swNewUOMYN.setChecked(false);
                }
                if (HidePaymentYN.equals("1")) {
                    chkHidePayment.setChecked(true);
                } else {
                    chkHidePayment.setChecked(false);
                }
                Log.d("SMOBILE YN", SMobileYN);
                if (SMobileYN.equals("1")) {
                    swSMobileYN.setChecked(true);
                  //  swSMobileYN.setText("Yes");
                    txtSMobileYN.setText("Sync In By Item Y/N?");
                } else {
                    swSMobileYN.setChecked(false);
                   // swSMobileYN.setText("No");
                    txtSMobileYN.setText("Sync In By Item Group Y/N?");
                }
            }
            rsOther.close();

            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    private void savePrefix(){
        try {
            DBAdapter db = new DBAdapter(getActivity());
            db.openDB();
            String PrefixSO = txtPrefixSO.getText().toString();
            String LastNoSO = txtLastNoSO.getText().toString();
            String PrefixCS = txtPrefixCS.getText().toString();
            String LastNoCS = txtLastNoCS.getText().toString();
            String PrefixCusCN = txtPrefixCusCN.getText().toString();
            String LastNoCusCN = txtLastNoCusCN.getText().toString();
            String PrefixService = txtPrefixService.getText().toString();
            String LastNoService = txtLastNoService.getText().toString();
            String AdminPassword = txtAdminPassword.getText().toString();
            String ServiceCharges = txtServiceCharges.getText().toString();
            String ReceiptHeader = txtReceiptHeader.getText().toString();
            String sqlCheck = "select count(*)as numrows from sys_runno_dt where RunNoCode='SO' ";
            Cursor rsSO = db.getQuery(sqlCheck);
            int numSO = 0;
            while (rsSO.moveToNext()) {
                numSO = rsSO.getInt(0);
            }
            if (numSO == 0) {
                String insert = "insert into sys_runno_dt(RunNoCode,Prefix,LastNo)values('SO','" + PrefixSO + "','" + LastNoSO + "')";
                db.addQuery(insert);
            } else {
                String update = "update sys_runno_dt set Prefix='" + PrefixSO + "',LastNo='" + LastNoSO + "' where RunNoCode='SO' ";
                db.addQuery(update);
            }

            String sqlCheck2 = "select count(*)as numrows from sys_runno_dt where RunNoCode='CS' ";
            Cursor rsCS = db.getQuery(sqlCheck2);
            int numCS = 0;
            while (rsCS.moveToNext()) {
                numCS = rsCS.getInt(0);
            }
            if (numCS == 0) {
                String insert = "insert into sys_runno_dt(RunNoCode,Prefix,LastNo)values('CS','" + PrefixCS + "','" + LastNoCS + "')";
                db.addQuery(insert);
            } else {
                String update = "update sys_runno_dt set Prefix='" + PrefixCS + "',LastNo='" + LastNoCS + "' where RunNoCode='CS' ";
                db.addQuery(update);
            }

            String sqlCheck3 = "select count(*)as numrows from sys_runno_dt where RunNoCode='CusCN' ";
            Cursor rsCusCN = db.getQuery(sqlCheck3);
            int numCusCN = 0;
            while (rsCusCN.moveToNext()) {
                numCusCN = rsCusCN.getInt(0);
            }
            if (numCusCN == 0) {
                String insert = "insert into sys_runno_dt(RunNoCode,Prefix,LastNo)values('CusCN','" + PrefixCusCN + "','" + LastNoCusCN + "')";
                long add = db.addQuery(insert);
                if (add > 0) {
                    Toast.makeText(this.getContext(), "Setting Prefix successfull ", Toast.LENGTH_SHORT).show();
                }
            } else {
                String update = "update sys_runno_dt set Prefix='" + PrefixCusCN + "',LastNo='" + LastNoCusCN + "' where RunNoCode='CusCN' ";
                long add = db.addQuery(update);
                if (add > 0) {
                    Toast.makeText(this.getContext(), "Setting Prefix successfull ", Toast.LENGTH_SHORT).show();
                }
            }

            String sqlCheck4 = "select count(*)as numrows from sys_runno_dt where RunNoCode='Service' ";
            Cursor rsService = db.getQuery(sqlCheck4);
            int numService = 0;
            while (rsService.moveToNext()) {
                numService = rsService.getInt(0);
            }
            if (numService == 0) {
                String insert = "insert into sys_runno_dt(RunNoCode,Prefix,LastNo)values('Service','" + PrefixService + "','" + LastNoService + "')";
                db.addQuery(insert);
            } else {
                String update = "update sys_runno_dt set Prefix='" + PrefixService + "',LastNo='" + LastNoService + "' where RunNoCode='Service' ";
                db.addQuery(update);
            }

            String PostAs = "0";
            if (swPost.isChecked()) {
                PostAs = "1";
            } else {
                PostAs = "0";
            }
            String EncodeType = "0";
            if (swEncodeType.isChecked()) {
                EncodeType = "GBK";
            } else {
                EncodeType = "UTF-8";
            }
            String ReceiptType =spReceiptType.getSelectedItem().toString();
           /* if (swReceiptType.isChecked()) {
                ReceiptType = "Customize";
            } else {
                ReceiptType = "Normal";
            }*/
            String GroupByItemYN = "1";
            if (chkGroupByItemYN.isChecked()) {
                GroupByItemYN = "1";
            } else {
                GroupByItemYN = "0";
            }

            String AutoSyncYN = "1";
            if (swAutoSyncYN.isChecked()) {
                AutoSyncYN = "1";
            } else {
                AutoSyncYN = "0";
            }

            String NewUOMYN = "1";
            if (swNewUOMYN.isChecked()) {
                NewUOMYN = "1";
            } else {
                NewUOMYN = "0";
            }

            String HidePaymentYN = "0";
            if (chkHidePayment.isChecked()) {
                HidePaymentYN = "1";
            } else {
                HidePaymentYN = "0";
            }
            String SMobileYN = "0";
            if (swSMobileYN.isChecked()) {
                SMobileYN = "1";
            } else {
                SMobileYN = "0";
            }

            String update = "update tb_setting set PostAs='" + PostAs + "', EncodeType='" + EncodeType + "', ReceiptType='" + ReceiptType + "'," +
                    " GroupByItemYN='" + GroupByItemYN + "', AdminPassword='" + AdminPassword + "', AutoSyncYN='" + AutoSyncYN + "'   ";
            db.addQuery(update);

            String qOtherSet = "select * from tb_othersetting ";
            Cursor rsOther = db.getQuery(qOtherSet);
            int numOther = 0;
            while (rsOther.moveToNext()) {
                numOther++;
            }
            if (numOther > 0) {
                String qUpdate = "update tb_othersetting set ServiceCharges='" + ServiceCharges + "', ReceiptHeader='"+ReceiptHeader+"'," +
                        " NewUOMYN='"+NewUOMYN+"', HidePaymentYN='"+HidePaymentYN+"', SMobileYN='"+SMobileYN+"'     ";
                Log.d("UPDATE",qUpdate);
                db.addQuery(qUpdate);
            } else {
                String qInsert = "insert into tb_othersetting(ServiceCharges,Remark, ReceiptHeader," +
                        "NewUOMYN, HidePaymentYN, SMobileYN)values('" + ServiceCharges + "', '', '"+ReceiptHeader+"'," +
                        "'"+NewUOMYN+"', '"+HidePaymentYN+"', '"+SMobileYN+"') ";
                Log.d("INSERT",qInsert);
                db.addQuery(qInsert);

            }
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }

    }

}
