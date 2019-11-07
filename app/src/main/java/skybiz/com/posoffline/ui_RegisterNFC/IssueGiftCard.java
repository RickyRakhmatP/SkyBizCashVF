package skybiz.com.posoffline.ui_RegisterNFC;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.vision.text.Line;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class IssueGiftCard extends AppCompatActivity {

    private static final String TAG = IssueGiftCard.class.getSimpleName();
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    private boolean mDebug = true;

    private String mCharset = "ISO-8859-1";
    private boolean mShowDataAsHexString;
    private boolean mReadAll;


    private boolean mWriteToBlock;
    private boolean mWriteAll;

    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mTechList;

    String mReadBlockIndex="1",mWriteBlockContent,mWriteBlockIndex;

    EditText txtCusName,txtContactTel,txtCusCode;
    Button btnRegister;
    String vMerchantKey,vCusCode,vCusName,vContactTel;
    LinearLayout lnWrite,lnRegister,lnTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_gift_card);

        txtCusName=(EditText)findViewById(R.id.txtCusName);
        txtContactTel=(EditText)findViewById(R.id.txtContactTel);
        txtCusCode=(EditText)findViewById(R.id.txtCusCode);
        btnRegister=(Button)findViewById(R.id.btnRegister);
        lnWrite=(LinearLayout) findViewById(R.id.lnWrite);
        lnRegister=(LinearLayout) findViewById(R.id.lnRegister);
        lnTemp=(LinearLayout) findViewById(R.id.lnTemp);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            this.finish();
            return;
        }

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            filter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        mIntentFilters = new IntentFilter[]{filter};
        mTechList = new String[][]{new String[]{MifareClassic.class.getName()}};
        initData();
    }

    private void initData(){
        GetLastCusCode getCusCode=new GetLastCusCode(this);
        getCusCode.execute();
    }

    private void save(){
        String CusCode      =txtCusCode.getText().toString();
        String CusName      =txtCusName.getText().toString();
        String ContactTel   =txtContactTel.getText().toString();
        if(CusCode.isEmpty()){
            Toast.makeText(this,"Customer Code Cannot Empty", Toast.LENGTH_SHORT).show();
        }else if(CusName.isEmpty()){
            Toast.makeText(this,"Customer Name Cannot Empty", Toast.LENGTH_SHORT).show();
            txtCusName.requestFocus();
        }else if(ContactTel.isEmpty()){
            Toast.makeText(this,"Handphone No Cannot Empty", Toast.LENGTH_SHORT).show();
            txtContactTel.requestFocus();
        }else{
            SaveCustomer saveCus=new SaveCustomer(this,CusCode,CusName,ContactTel);
            saveCus.execute();
        }
    }

    private void showTap(){
        lnWrite.setVisibility(View.VISIBLE);
        lnRegister.setVisibility(View.GONE);
        lnTemp.setVisibility(View.GONE);
    }
    private class SaveCustomer extends AsyncTask<Void,Void,String>{
        Context c;
        String CusCode,CusName,ContactTel;
        String IPAddress,Password,CurCode,
                ItemConn,URL,UserName,
                DBName,DBStatus,EncodeType,
                Port,z;

        public SaveCustomer(Context c, String cusCode, String cusName, String contactTel) {
            this.c = c;
            CusCode = cusCode;
            CusName = cusName;
            ContactTel = contactTel;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fnsave();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                Toast.makeText(c,"Succes, add new customer", Toast.LENGTH_SHORT).show();
                vCusCode=CusCode;
                vCusName=CusName;
                vContactTel=ContactTel;
                showTap();
                mWriteBlockContent="test write";
                mWriteAll=true;
            }else {
                Toast.makeText(c,"Error, add new customer", Toast.LENGTH_SHORT).show();
            }
        }

        private String fnsave(){
            try{
                SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sdf2   = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String D_ateTime = sdf.format(date);
                String D_ate = sdf2.format(date);
                Calendar cal = Calendar.getInstance();
                Date today = cal.getTime();
                cal.add(Calendar.YEAR, 1); // to get previous year add -1
                Date nextYear = cal.getTime();
                String expdate = sdf.format(nextYear);
                String DOB="1990-01-01";

                DBAdapter db = new DBAdapter(c);
                db.openDB();
                Cursor cur = db.getGeneralSetup();
                while (cur.moveToNext()) {
                    CurCode = cur.getString(1);
                }
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
                    EncodeType=curSet.getString(7);
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
                        "'B55-0000', '', ''," +
                        "'1', '0', ''," +
                        "'', '', ''," +
                        "'', '', '"+ContactTel+"'," +
                        "'', '0', ''," +
                        "'', '', ''," +
                        "'1', '', '"+DOB+"'," +
                        "'', '', '"+CusCode+"'," +
                        "'', '"+D_ateTime+"', ''," +
                        "'"+D_ate+"', '"+expdate+"', 'Other'," +
                        "'Other', '"+D_ate+"'," +
                        "'123')";
                Log.d("QUERY",insert);
                if(DBStatus.equals("1")) {
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    if (conn != null) {
                        Statement stmtCus = conn.createStatement();
                        stmtCus.execute(insert);
                    }
                    z="success";
                }else if(DBStatus.equals("0")){
                    db.addQuery(insert);
                    z="success";
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

    private class GetLastCusCode extends AsyncTask<Void,Void,String>{
        Context c;
        String z,newCusCode,MerchantKey;
        String IPAddress,Password,CurCode,
                ItemConn,URL,UserName,
                DBName,DBStatus,EncodeType,Port;

        public GetLastCusCode(Context c) {
            this.c = c;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fnlastcustomer();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                txtCusCode.setText(newCusCode);
                vMerchantKey=MerchantKey;
            }else{
                txtCusCode.setText(newCusCode);
                vMerchantKey=MerchantKey;
                Toast.makeText(c,"Error, get last customer", Toast.LENGTH_SHORT).show();
            }
        }
        private String fnlastcustomer(){
            try{
                z="error";
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                Cursor cur = db.getGeneralSetup();
                while (cur.moveToNext()) {
                    CurCode = cur.getString(1);
                }
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
                    EncodeType=curSet.getString(7);
                }
                newCusCode          ="";
                String oldCusCode   ="";
                String sqlCus = "select CusCode from customer order by CusCode Desc limit 1";
                String qPayType="select MerchantKey from ret_paymenttype where PaymentType='Gift Card' ";
                if(DBStatus.equals("1")){
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    if (conn != null) {
                        Statement stmtCus = conn.createStatement();
                        stmtCus.execute(sqlCus);
                        ResultSet rsCus = stmtCus.getResultSet();
                        while (rsCus.next()) {
                            oldCusCode = rsCus.getString("CusCode");
                        }
                        if(!oldCusCode.isEmpty()){
                            oldCusCode          = "1"+oldCusCode;
                            int NewNo           = (Integer.parseInt(oldCusCode)) + 1;
                            String NewCusCode   = String.valueOf(NewNo);
                            newCusCode          = NewCusCode.substring(1,NewCusCode.length());
                        }else{
                            newCusCode          ="00000001";
                        }
                        Statement stmtPay = conn.createStatement();
                        stmtPay.execute(qPayType);
                        ResultSet rsPay = stmtPay.getResultSet();
                        while (rsPay.next()) {
                            MerchantKey = rsPay.getString("MerchantKey");
                        }
                        z="success";
                    }else{
                        z="error";
                    }
                }else if(DBStatus.equals("0")){
                    Cursor rsCus=db.getQuery(sqlCus);
                    while(rsCus.moveToNext()){
                        oldCusCode = rsCus.getString(0);
                    }
                    if(!oldCusCode.isEmpty()){
                        oldCusCode          = "1"+oldCusCode;
                        int NewNo           = (Integer.parseInt(oldCusCode)) + 1;
                        String NewCusCode   = String.valueOf(NewNo);
                        newCusCode          = NewCusCode.substring(1,NewCusCode.length());
                    }else{
                        newCusCode          ="00000001";
                    }
                    Cursor rsPay=db.getQuery(qPayType);
                    while(rsPay.moveToNext()){
                        MerchantKey = rsPay.getString(0);
                    }
                    z="success";
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


    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mTechList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (!NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            return;
        }
        resolveIntent(intent);
    }

    private void resolveIntent(Intent intent) {
        debug("resolveIntent...", false);
        if (mReadBlockIndex == null && mWriteBlockContent == null && !mReadAll) {
            debug(getString(R.string.toast_need_data), true);
            return;
        }

        MifareClassic mfc = MifareClassic.get((Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
        if (mfc == null) {
            debug(getString(R.string.toast_not_mifare_classic), true);
            return;
        }

       // disableAllOptions();
        try {
            String result1 = performRead(mfc);
            if (result1 != null) {
                if (mReadAll) {
                    alert(result1);
                } else {
                    debug(result1, true);
                }
            }

            List<Integer> result2 = performWrite(mfc);
            if (result2 != null) {
                if (result2.size() == 0) {
                    debug(getString(R.string.toast_write_success), true);
                } else if (result2.size() == 1 && !mReadAll) {
                    debug(getString(R.string.toast_write_fail), true);
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(getString(R.string.result_failed_the_following));
                    sb.append(LINE_SEPARATOR);
                    for (int i = 0; i < result2.size(); i++) {
                        sb.append(result2.get(i));
                        if (i != 0 && i % 5 == 0) {
                            sb.append(LINE_SEPARATOR);
                        } else {
                            sb.append(", ");
                        }
                    }
                    sb.delete(sb.length() - 2, sb.length());
                    alert(sb.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //enableAllOptions();
            try {
                mfc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private void debug(String info, boolean toast) {
        if (mDebug) {
            Log.d(TAG, info);
            if (toast) {
                showToast(info);
            }
        }
    }

    private void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    private String performRead(MifareClassic mfc) throws IOException {
        if (mReadBlockIndex == null && !mReadAll) {
            return null;
        }
        if (!mfc.isConnected()) {
            mfc.connect();
        }

        String ret = null;
        if (mReadAll) {
            StringBuilder sb = new StringBuilder();
            boolean auth;
            int blockCount = mfc.getBlockCount();
            for (int i = 0; i < blockCount; i++) {
                auth = mfc.authenticateSectorWithKeyA(mfc.blockToSector(i), MifareClassic.KEY_DEFAULT);
                if (auth) {
                    byte[] data = readBlock(mfc, i);
                        sb.append(String.format(getString(R.string.block_index_dynamic), i));
                    sb.append(LINE_SEPARATOR);
                    sb.append(convertBytes2String(data));
                    sb.append(LINE_SEPARATOR);
                    sb.append(LINE_SEPARATOR);
                }
            }
            sb.delete(sb.length() - 2, sb.length());
            ret = sb.toString();
        } else {
            int blockIndex = Integer.parseInt("1");
            if (!validateBlockIndex(mfc, blockIndex)) {
                debug(getString(R.string.err_block_index_out_of_bound), true);
                return ret;
            }
            int sectorIndex = mfc.blockToSector(blockIndex);
            boolean auth = mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_DEFAULT);
            if (auth) {
                byte[] data = readBlock(mfc, blockIndex);
                ret = convertBytes2String(data);
            } else {
                debug(getString(R.string.auth_failed), true);
            }
        }
        return ret;
    }

    private List<Integer> performWrite(MifareClassic mfc) throws IOException {
        if (mWriteBlockContent == null) {
            return null;
        }

        if (!mfc.isConnected()) {
            mfc.connect();
        }

        List<Integer> ret = new ArrayList<Integer>();
        String content = mWriteBlockContent;
        if (mWriteAll) {
            for (int i = 1; i < 10; i++) {
                if ((i + 1) % 4 == 0) {
                    continue;
                }
                if(i==1){
                    content=vMerchantKey;
                }
                if(i==2){
                    content=vCusCode;
                }
                if((i==5)){
                    content=vCusName;
                }
                if((i==6)){
                    content=vContactTel;
                }
                if((i==9)){
                    content="0";
                }
                boolean auth = mfc.authenticateSectorWithKeyA(mfc.blockToSector(i), MifareClassic.KEY_DEFAULT);
                if (auth) {
                    writeBlock(mfc, i, convertString2Bytes(content));
                } else {
                    ret.add(i);
                    debug(getString(R.string.auth_failed), true);
                }
            }
        } else {
            int blockIndex;
            if (mWriteToBlock && mWriteBlockIndex != null) {
                blockIndex = Integer.parseInt(mWriteBlockIndex);
                if (!validateBlockIndex(mfc, blockIndex)) {
                    debug(getString(R.string.err_block_index_out_of_bound), true);
                    return null;
                }
                if (blockIndex == 0 || (blockIndex + 1) % 4 == 0) {
                    debug(getString(R.string.err_write_to_forbidden_block), true);
                    return null;
                }
            } else {
                blockIndex = randomBlockIndex(mfc);
            }
            debug(String.format(getString(R.string.write_random_dynamic), blockIndex), false);
            boolean auth = mfc.authenticateSectorWithKeyA(mfc.blockToSector(blockIndex), MifareClassic.KEY_DEFAULT);
            if (auth) {
                writeBlock(mfc, blockIndex, convertString2Bytes(content));
            } else {
                ret.add(blockIndex);
                debug(getString(R.string.auth_failed), true);
            }
        }
        return ret;
    }

    private byte[] readBlock(MifareClassic mfc, int blockIndex) throws IOException {
        return mfc.readBlock(blockIndex);
    }

    private void writeBlock(MifareClassic mfc, int blockIndex, byte[] data) throws IOException {
        mfc.writeBlock(blockIndex, data);
    }

    private String convertBytes2String(byte[] data) throws UnsupportedEncodingException {
        String ret;
        if (mShowDataAsHexString) {
            StringBuilder sb = new StringBuilder();
            for (byte b : data) {
                int i = (int) b;
                sb.append(Integer.toHexString(i).toUpperCase());
            }
            ret = sb.toString();
        } else {
            int pos = data.length;
            for (int i = data.length - 1; i >= 0; i--) {
                if (data[i] != 0) {
                    break;
                }
                pos = i;
            }
            ret = new String(data, 0, pos, mCharset);
        }
        return ret;
    }

    private byte[] convertString2Bytes(String content) throws UnsupportedEncodingException {
        byte[] ret = new byte[16];
        byte[] buf = content.getBytes(mCharset);
        int retLen = ret.length;
        int bufLen = buf.length;
        boolean b = retLen > bufLen;

        for (int i = 0; i < retLen; i++) {
            if (b && i >= bufLen) {
                ret[i] = 0;
                continue;
            }
            ret[i] = buf[i];
        }
        return ret;
    }

    private String getText(EditText et) {
        if (TextUtils.isEmpty(et.getText())) {
            return null;
        }
        return et.getText().toString().trim();
    }

    private int randomBlockIndex(MifareClassic mfc) {
        int i = new Random().nextInt(mfc.getBlockCount());
        if (i == 0 || (i + 1) % 4 == 0) {
            return randomBlockIndex(mfc);
        }
        return i;
    }

    private boolean validateBlockIndex(MifareClassic mfc, int blockIndex) {
        if (blockIndex >= mfc.getBlockCount()) {
            return false;
        }
        return true;
    }

    private void alert(String msg) {
        new AlertDialog.Builder(this).setMessage(msg).show();
    }
}
