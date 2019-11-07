package skybiz.com.posoffline.ui_RegisterNFC;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NFC.util.CommonTask;
import skybiz.com.posoffline.m_NFC.util.NFCHammer;

public class RegisterNFC extends AppCompatActivity {

    NfcAdapter mAdapter;
    PendingIntent mPendingIntent;
    ProgressDialog progressDialog;
    AlertDialog writeAlertDialog;
    final protected static char[] hexArray = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_nfc);
        checkNfcIsExistsOrNot();
        Initialization();
    }

    private void checkNfcIsExistsOrNot() {

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            if (!mAdapter.isEnabled()) {
                CommonTask.showWirelessSettingsDialog(this);
            }
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.disableForegroundDispatch(this);
        }
    }

    private void Initialization() {
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            CommonTask
                    .showMessage(this, R.string.no_nfc_found, R.string.no_nfc);
            // finish();
            return;
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        showProgressDialog();
        super.onNewIntent(intent);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                setIntent(intent);
                resolveIntent(intent);
            }
        }, 0);

    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            getTagInfo(intent);
        }
    }

    private void getTagInfo(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        String[] techList = tag.getTechList();
        for (int i = 0; i < techList.length; i++) {
            if (techList[i].equals(MifareClassic.class.getName())) {
                MifareClassic mifareClassicTag = MifareClassic.get(tag);
                switch (mifareClassicTag.getType()) {
                    case MifareClassic.TYPE_CLASSIC:
                        MifareClassic mfc = MifareClassic.get(tag);
                        // resolveIntentClassic(mfc);
                       // boolean result = NFCHammer.ReadClassic1kValue(this, mfc);
                        boolean result =true;
                        if (result) {
                            findViewById(R.id.pbBar).setVisibility(
                                    View.GONE);
                            Intent UC1kintent = new Intent(this,
                                    IssueGiftCard.class);
                            UC1kintent
                                    .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(UC1kintent);
                            overridePendingTransition(android.R.anim.slide_in_left,
                                    android.R.anim.slide_out_right);
                            // overridePendingTransition(R.anim.push_up_in,
                            // R.anim.push_up_out);
                        } else {
                            findViewById(R.id.pbBar).setVisibility(
                                    View.GONE);
                            CommonTask.createToast("Tap The card again!!!", this,
                                    Color.RED);
                            // Toast.makeText(this, "Tap The card again!!!",
                            // Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case MifareClassic.TYPE_PLUS:
                        CommonTask
                                .createToast(
                                        "This Tag is Mifare Classic Plus. We will Add this type in next version",
                                        this, Color.GREEN);
                        break;
                    case MifareClassic.TYPE_PRO:
                        CommonTask
                                .createToast(
                                        "This Tag is Mifare Classic Pro. We will Add this type in next version",
                                        this, Color.GREEN);
                        break;
                }
            } else if (techList[i].equals(MifareUltralight.class.getName())) {
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        boolean result1 = NFCHammer.readUltraLightValue(this, tag);
                        if (result1) {
                           /* findViewById(R.id.incProgressBar).setVisibility(
                                    View.GONE);
                            Intent Callintent = new Intent(this,
                                    MifareUltralightActivity.class);
                            Callintent
                                    .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(Callintent);
                            overridePendingTransition(android.R.anim.slide_in_left,
                                    android.R.anim.slide_out_right);*/
                        } else {
                           // findViewById(R.id.incProgressBar).setVisibility(
                                 //   View.GONE);
                            CommonTask.createToast("Tap The card again!!!", this,
                                    Color.RED);
                        }
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:

                       /* boolean result = NFCHammer.ReadULCValue(this, tag);
                        if (result) {
                            findViewById(R.id.incProgressBar).setVisibility(
                                    View.GONE);
                            Intent Callintent = new Intent(this,
                                    MifareUltralightCActivity.class);
                            Callintent
                                    .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(Callintent);
                            overridePendingTransition(android.R.anim.slide_in_left,
                                    android.R.anim.slide_out_right);
                        } else {
                            findViewById(R.id.incProgressBar).setVisibility(
                                    View.GONE);
                            CommonTask.createToast("Tap The card again!!!", this,
                                    Color.RED);
                        }*/
                        break;
                }
            } else if (techList[i].equals(IsoDep.class.getName())) {
                @SuppressWarnings("unused")
                IsoDep isoDepTag = IsoDep.get(tag);

                CommonTask
                        .createToast(
                                "This Tag is IsoDep tag. We will Add this type in next version",
                                this, Color.GREEN);

                // info[0] += "IsoDep \n";
             //   findViewById(R.id.incProgressBar).setVisibility(View.GONE);
            } else if (techList[i].equals(Ndef.class.getName())) {
                // This Is Topaz Tag
                NFCHammer hammer = new NFCHammer();
                if (hammer.readTopazCard(tag)) {
                  //  findViewById(R.id.incProgressBar).setVisibility(View.GONE);
                }

            } else if (techList[i].equals(NdefFormatable.class.getName())) {
                @SuppressWarnings("unused")
                NdefFormatable ndefFormatableTag = NdefFormatable.get(tag);
               // findViewById(R.id.incProgressBar).setVisibility(View.GONE);
                
              /*  CommonTask
                        .createToast(
                                "This Tag is NDEF formatable Tag. We will Add this type in next version",
                                this, Color.GREEN);*/

            }
        }
    }

    public void showProgressDialog() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                findViewById(R.id.pbBar).setVisibility(View.VISIBLE);
            }
        });
    }
}
