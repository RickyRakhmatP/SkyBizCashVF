package skybiz.com.posoffline.ui_TopUpNFC;

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
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Connection.Connect_db;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.Dialog_Qty;
import skybiz.com.posoffline.ui_RegisterNFC.IssueGiftCard;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class TopUpNFC extends AppCompatActivity {

    private static final String TAG = TopUpNFC.class.getSimpleName();
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mTechList;


    private boolean mDebug = true;
    private boolean mShowDataAsHexString;
    private boolean mReadAll,mWriteAll;
    private String mCharset = "ISO-8859-1";
    String mReadBlockIndex,mWriteBlockContent,mWriteBlockIndex;

    LinearLayout lnBalance,lnTopUp,lnAferTopUp;

    TextView txtCusName,txtContactTel,txtBalance,
            txtLastTrnDoc1No,txtLastTrnType,txtLastTrnDateTime,
            txtLastTrnAmount,txtNewBalance,txtCusCode;

    EditText txtTopUpAmount;
    Button btnCancel,btnConfirm;
    String CurCode,vCusCode,vMerchantKey;

    String vTopUpAmount="",vLastTrnDateTime="",vLastTrnDoc1No="",vLastTrnAmount="",vLastTrnType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_nfc);
        lnBalance=(LinearLayout)findViewById(R.id.lnBalance);
        lnTopUp=(LinearLayout)findViewById(R.id.lnTopUp);
        lnAferTopUp=(LinearLayout)findViewById(R.id.lnAfterTopUp);
        txtCusName=(TextView)findViewById(R.id.txtCusName);
        txtContactTel=(TextView)findViewById(R.id.txtContactTel);
        txtBalance=(TextView)findViewById(R.id.txtBalance);
        txtLastTrnDoc1No=(TextView)findViewById(R.id.txtLastTrnDoc1No);
        txtLastTrnType=(TextView)findViewById(R.id.txtLastTrnType);
        txtLastTrnDateTime=(TextView)findViewById(R.id.txtLastTrnDateTime);
        txtLastTrnAmount=(TextView)findViewById(R.id.txtLastTrnAmount);
        txtNewBalance=(TextView)findViewById(R.id.txtNewBalance);
        txtCusCode=(TextView)findViewById(R.id.txtCusCode);
        txtTopUpAmount=(EditText)findViewById(R.id.txtTopUpAmount);
        btnCancel=(Button)findViewById(R.id.btnCancel);
        btnConfirm=(Button)findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fntopup();
            }
        });


        mReadAll=true;
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
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            Cursor cur = db.getGeneralSetup();
            while (cur.moveToNext()) {
                CurCode = cur.getString(1);
            }
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private void afterTopUp(){
        mReadBlockIndex    ="9";
        mReadAll            =false;
        mWriteBlockContent  ="write";
        mWriteAll           =true;
        lnBalance.setVisibility(View.VISIBLE);
        lnTopUp.setVisibility(View.GONE);
    }
    private void afterTap(){
        if(mReadAll) {
            lnTopUp.setVisibility(View.VISIBLE);
            lnBalance.setVisibility(View.GONE);
        }else{
            lnTopUp.setVisibility(View.GONE);
            lnBalance.setVisibility(View.GONE);
            lnAferTopUp.setVisibility(View.VISIBLE);
        }
    }
    private void fntopup(){
        String Balance  =txtBalance.getText().toString().replaceAll(",","");
        String TopUpAmt =txtTopUpAmount.getText().toString();
        Double dBalance =Double.parseDouble(Balance);
        Double dTopUpAmt=Double.parseDouble(TopUpAmt);
        Double dAmount  =dBalance+dTopUpAmt;
        vTopUpAmount   =String.format(Locale.US, "%,.2f", dAmount);
        if(TopUpAmt.isEmpty()){
            Toast.makeText(this,"Top Up Amount Cannot Empty !", Toast.LENGTH_SHORT).show();
        }else{
            TopUpAmount topUpAmount=new TopUpAmount(this,vMerchantKey,vCusCode,TopUpAmt);
            topUpAmount.execute();
        }
    }
    private class TopUpAmount extends AsyncTask<Void,Void,String>{
        Context c;
        String MerchantKey,CusCode,Amount;
        String IPAddress,Password,CurCode,
                ItemConn,URL,UserName,
                DBName,DBStatus,EncodeType,
                Port,z,UserCode,
                CounterCode,D_ateTime,Doc1No,Remark;

        public TopUpAmount(Context c, String merchantKey, String cusCode, String amount) {
            this.c = c;
            MerchantKey = merchantKey;
            CusCode = cusCode;
            Amount = amount;
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
                vLastTrnAmount  =Amount;
                vLastTrnDateTime=D_ateTime;
                vLastTrnDoc1No  =Doc1No;
                vLastTrnType    =Remark;
                Toast.makeText(c,"Successful, Add New Trn ", Toast.LENGTH_SHORT).show();
                afterTopUp();
            }else if(result.equals("not found")){
                Toast.makeText(c,"Customer Not Found ", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(c,"Error", Toast.LENGTH_SHORT).show();
            }
        }

        private String fnsave(){
            try{
                String MacAddress= Settings.Secure.getString(c.getContentResolver(),Settings.Secure.ANDROID_ID);
                MacAddress=MacAddress.substring(0,4);
                SimpleDateFormat sdf    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sdf2   = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf3    = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();
                D_ateTime = sdf.format(date);
                String D_ate = sdf2.format(date);
                String T_ime = sdf3.format(date);
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName,UserName,Password," +
                        "DBName,Port,DBStatus," +
                        "ItemConn,EncodeType, UserCode," +
                        "CounterCode " +
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
                    UserCode=curSet.getString(8);
                    CounterCode=curSet.getString(9);
                }

                Doc1No    =D_ateTime+MacAddress;
                String qCheck    ="select count(*)as numrows from customer where CusCode='"+CusCode+"' ";
                Log.d("QUERY CHECK",qCheck);
                String T_ype     ="Cash In";
                Remark    ="Top Up Gift Card";
                String qPointAdj = "insert into ret_pointadjustment(cuscode," +
                        " D_ate, Point, DocType," +
                        " Remark, Screen, D_ateTime)values('" + CusCode + "'," +
                        " '" + D_ate + "', '" + Amount + "', 'Increase'," +
                        " '" + Doc1No + "', '"+Remark+"', '"+D_ateTime+"')";
                String qCashIn = "INSERT INTO stk_receipt2 (D_ate, T_ime, D_ateTime, " +
                        " Doc1No, CashAmt, CC1Code, " +
                        " CC1Amt, CC1No, CC1Expiry, " +
                        " CC1ChargesAmt, CC1ChargesRate,CC2Code," +
                        " CC2Amt, CC2No, CC2Expiry, " +
                        " CC2ChargesAmt, CC2ChargesRate, Cheque1Code, " +
                        " Cheque1Amt, Cheque1No, Cheque2Code," +
                        " Cheque2Amt, Cheque2No, PointAmt, " +
                        " VoucherAmt, CurCode, CurRate, " +
                        " FCAmt, CusCode, BalanceAmount, " +
                        " ChangeAmt, CounterCode, UserCode, " +
                        " DocType, VoidYN, Remark," +
                        " T_ype) VALUES (" +
                        " '"+D_ate+"', '"+T_ime+"', '"+D_ateTime+"'," +
                        " '"+ Doc1No +"' , '"+Amount+"', ''," +
                        " '0', '', '', " +
                        " '0', '0', '', " +
                        " '0', '', '', " +
                        " '0', '0', '', " +
                        " '0', '', '', " +
                        " '0', '', '0', " +
                        " '0', '"+CurCode+"', '1', " +
                        " '0', '', '0', " +
                        " '0',  '"+CounterCode+"', '"+UserCode+"', " +
                        " 'CF',  '0', '"+Remark+"', " +
                        " '"+T_ype+"') " ;
                int numrows=0;
                if(DBStatus.equals("1")){
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    //Connection conn= Connect_db.getConnection();
                    if (conn != null) {
                        Statement stmtCheck = conn.createStatement();
                        stmtCheck.execute(qCheck);
                        ResultSet rsCheck=stmtCheck.getResultSet();
                        while(rsCheck.next()){
                            numrows=rsCheck.getInt(1);
                        }
                        if(numrows>0) {
                            Statement stmtIn = conn.createStatement();
                            stmtIn.execute(qPointAdj);

                            Statement stmt = conn.createStatement();
                            stmt.execute(qCashIn);

                            z="success";
                        }else{
                            z="not found";
                        }
                    }else{
                        z="error";
                    }
                }else if(DBStatus.equals("0")){

                    Cursor rsCheck=db.getQuery(qCheck);
                    while(rsCheck.moveToNext()){
                        numrows=rsCheck.getInt(0);
                    }
                    if(numrows>0){
                        db.addQuery(qPointAdj);
                        db.addQuery(qCashIn);
                        z="success";
                    }else{
                        z="not found";
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
                    //showResult(result1);
                    debug(result1, true);
                } else {
                    debug(result1, true);
                   // showResult(result1);
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
            afterTap();
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
        String ret = "";
        if(mReadAll){
            boolean auth;
            int blockCount = mfc.getBlockCount();
            for (int i = 1; i < blockCount; i++) {
                if ((i + 1) % 4 == 0) {
                    continue;
                }
                mReadBlockIndex=String.valueOf(i);
                auth = mfc.authenticateSectorWithKeyA(mfc.blockToSector(i), MifareClassic.KEY_DEFAULT);
                if (auth) {
                    byte[] data = readBlock(mfc, i);
                    //ret = convertBytes2String(data);
                    StringBuilder sb = new StringBuilder();
                   // sb.append(LINE_SEPARATOR);
                    sb.append(convertBytes2String(data));
                    //sb.append(LINE_SEPARATOR);
                   // sb.append(LINE_SEPARATOR);
                   // sb.delete(sb.length() - 2, sb.length());
                    String ret1 = sb.toString();
                    showResult(i,ret1);
                }
            }

             /*if ((i + 1) % 4 == 0) {
                    continue;
                }
                int blockIndex =i;
                mReadBlockIndex=String.valueOf(blockIndex);
                int sectorIndex = mfc.blockToSector(blockIndex);
                boolean auth = mfc.authenticateSectorWithKeyA(sectorIndex, MifareClassic.KEY_DEFAULT);
                if (auth) {
                    byte[] data = readBlock(mfc, blockIndex);
                    ret = convertBytes2String(data);
                } else {
                    debug(getString(R.string.auth_failed), true);
                }*/
        }else{
            int blockIndex = Integer.parseInt("2");
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
       /* if (mReadAll) {
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
        }*/
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
        String content = "";
        if (mWriteAll) {
            for (int i = 8; i < 16; i++) {
                if ((i + 1) % 4 == 0) {
                    continue;
                }
                if(i==9){
                    content=vTopUpAmount;
                }
                if(i==10){
                    content=vLastTrnDoc1No;
                }
                if((i==11)){
                    content=vLastTrnType;
                }
                if((i==14)){
                    content=vLastTrnDateTime;
                }
                if((i==15)){
                    content=vLastTrnAmount;
                }
                boolean auth = mfc.authenticateSectorWithKeyA(mfc.blockToSector(i), MifareClassic.KEY_DEFAULT);
                if (auth) {
                    writeBlock(mfc, i, convertString2Bytes(content));
                } else {
                    ret.add(i);
                    debug(getString(R.string.auth_failed), true);
                }
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

    private void showResult(int i, String result){
        switch (i) {
            case 1:
                vMerchantKey=result;
            case 2:
                vCusCode    =result.trim();
                txtCusCode.setText(result.trim());
            case 5:
                txtCusName.setText(result);
                break;
            case 6:
                txtContactTel.setText(result);
                break;
            case 9:
                txtBalance.setText(result);
            case 10:
                txtLastTrnDoc1No.setText(result);
                break;
            case 11:
                txtLastTrnType.setText(result);
                break;
            case 14:
                txtLastTrnDateTime.setText(result);
                break;
            case 15:
                txtLastTrnAmount.setText(result);
                break;
        }
    }
    private void alert(String msg) {
        new AlertDialog.Builder(this).setMessage(msg).show();
    }

}


/*
Block 1= MerchantKey
Block 2= CusCode
Block 5= CusName;
Block 6= ContactTel
Block 9= Balance
Block 10= LastTrnDoc1No
Block 11= LastTrnType
Block 14= LastTrnDateTime
Block 15=LastTrnAmount
 */