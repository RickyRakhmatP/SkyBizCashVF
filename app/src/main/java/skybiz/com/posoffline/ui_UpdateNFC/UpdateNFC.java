package skybiz.com.posoffline.ui_UpdateNFC;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;


public class UpdateNFC extends AppCompatActivity {

    String myLog = "myLog";

    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;
    Button button;
    TextView txtContent;
    public static final String ERROR_DETECTED   = "No NFC tag detected!";
    public static final String WRITE_SUCCESS    = "Text written to the NFC tag successfully!";
    public static final String WRITE_ERROR      = "Error during writing, is the NFC tag close enough to your device?";
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nfc);
       // button = (Button) findViewById(R.id.button);
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        txtContent=(TextView)findViewById(R.id.txtContent);

       /* button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyTask().execute();
            }
        });*/

        initData();
    }
    private void initData(){
        readNFC();
        new MyTask().execute();
    }

    private void readNFC(){
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }
        readFromIntent(getIntent());

        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
    }

    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }
    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;

        String text = "";
//        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }
        txtContent.setText("NFC Content: " + text);
        PushData pushData=new PushData(this,text);
        pushData.execute();
    }


    public class PushData extends AsyncTask<Void, Void, String>{
        Context c;
        String jsonData,vData;
        String IPAddress,UserName,Password,
                URL,DBName,z,
                Port,DBStatus,ItemConn,
                EncodeType,DateTimeSync;

        public PushData(Context c, String vData) {
            this.c = c;
            this.vData=vData;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fnpushdata();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("success")){
                outAnimation = new AlphaAnimation(1f, 0f);
                outAnimation.setDuration(200);
                progressBarHolder.setAnimation(outAnimation);
                progressBarHolder.setVisibility(View.GONE);
            }else if(result.equals("error")){

            }
        }

        private String fnpushdata(){
           /* try{
                DBAdapter db=new DBAdapter(c);
                db.openDB();

                db.openDB();
                String querySet="select ServerName,UserName,Password," +
                        " DBName,Port,DBStatus," +
                        " ItemConn,EncodeType, IFNULL(D_ateTimeSync,'') as D_ateTimeSync " +
                        " from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress   = curSet.getString(0);
                    UserName    = curSet.getString(1);
                    Password    = curSet.getString(2);
                    DBName      = curSet.getString(3);
                    Port        = curSet.getString(4);
                    DBStatus    = curSet.getString(5);
                    ItemConn    = curSet.getString(6);
                    EncodeType  = curSet.getString(7);
                    DateTimeSync= curSet.getString(8);
                }
                JSONArray ja=null;
                JSONObject jo=null;
                URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=UTF-8";
                Connection conn = Connector.connect(URL, UserName, Password);
                if (conn == null) {
                    z = "error";
                } else {
                    String sql      ="";
                    String CardNo   ="";
                    String Doc1No   ="";
                    String Credit   ="";
                    String Balance  ="";
                    Statement stmtInsert = conn.createStatement();
                    stmtInsert.execute(sql);


                    z="success";
                }
                db.closeDB();
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }*/
            return z;
        }
    }




    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            button.setEnabled(false);
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
            button.setEnabled(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (int i = 0; i < 5; i++) {
                    Log.d(myLog, "Emulating some task.. Step " + i);
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /******************************************************************************
     **********************************Write to NFC Tag****************************
     ******************************************************************************/
    private void write(String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = { createRecord(text) };
        NdefMessage message = new NdefMessage(records);
        // Get an instance of Ndef for the tag.
        Ndef ndef = Ndef.get(tag);
        // Enable I/O
        ndef.connect();
        // Write the message
        ndef.writeNdefMessage(message);
        // Close the connection
        ndef.close();
    }
    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "en";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

        return recordNFC;
    }



    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        readFromIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        WriteModeOff();
    }

    @Override
    public void onResume(){
        super.onResume();
        WriteModeOn();
    }



    /******************************************************************************
     **********************************Enable Write********************************
     ******************************************************************************/
    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }
    /******************************************************************************
     **********************************Disable Write*******************************
     ******************************************************************************/
    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }


    public void writeNFC(String message){
        try {
            if (myTag == null) {
                Toast.makeText(this, ERROR_DETECTED, Toast.LENGTH_LONG).show();
            } else {
                write(message, myTag);
                Toast.makeText(this, WRITE_SUCCESS, Toast.LENGTH_LONG ).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, WRITE_ERROR, Toast.LENGTH_LONG ).show();
            e.printStackTrace();
        } catch (FormatException e) {
            Toast.makeText(this, WRITE_ERROR, Toast.LENGTH_LONG ).show();
            e.printStackTrace();
        }
    }

}
