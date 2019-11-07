package skybiz.com.posoffline.m_Ipay88;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ipay.Ipay;
import com.ipay.IpayPayment;
import com.ipay.IpayResultDelegate;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.Result;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Ipay88.Merchant.MerchantScan2;
import skybiz.com.posoffline.m_Ipay88.User.UserScan;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.BitmapUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.Utils;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothPrinter;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth.BluetoothZebra;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_IposAIDL.Utils.IposAidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_USB.PrintingUSB;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI.PrintingWifi;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 23/02/2018.
 */

public class DialogPrint extends DialogFragment {
    View view;
    Button btnOK,btnNO;
    TextView txtHeader;
    ProgressBar pbPrinting;
    String TransId,Amount,PayType,MerchantCode,D_ateTime,ResultPrint,RefNo;
    Boolean isBT;
    LinearLayout lnButton;
    ImageView imgLogo,imgBarcode;
    String TypePrinter="";
    String NamePrinter="";
    String IPPrinter="";
    String PortPrinter="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_print, container, false);
        btnOK=(Button)view.findViewById(R.id.btnOK);
        btnNO=(Button)view.findViewById(R.id.btnNO);
        txtHeader=(TextView)view.findViewById(R.id.txtHeader);
        pbPrinting=(ProgressBar)view.findViewById(R.id.pbPrinting);
        lnButton=(LinearLayout) view.findViewById(R.id.lnButton);
        imgLogo=(ImageView)view.findViewById(R.id.imgLogo);
        imgBarcode=(ImageView)view.findViewById(R.id.imgBarcode);
        TransId=this.getArguments().getString("TRANSID_KEY");
        Amount=this.getArguments().getString("AMOUNT_KEY");
        PayType=this.getArguments().getString("PAYTYPE_KEY");
        MerchantCode=this.getArguments().getString("MERCHANTCODE_KEY");
        pbPrinting.setVisibility(View.VISIBLE);
        RefNo=this.getArguments().getString("REFNO_KEY");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        D_ateTime = sdf.format(date);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              fnOK();
            }
        });
        btnNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fnNO();
            }
        });
        fnprint();
        return view;
    }
    private void fnprint(){
        Bitmap bmpBarcode=BitmapUtil.generateBitmap(TransId,8,100,30);
        Drawable image = new BitmapDrawable(Bitmap.createScaledBitmap(bmpBarcode, 270, 145, true));
        imgBarcode.setImageDrawable(image);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final String strPrint=genPrint();
                setPrint(strPrint);
            }
        }, 2000);
    }
    private void fnOK(){
        final String strPrint=genPrint();
        setPrint(strPrint);
        ((CashReceipt)getActivity()).setPayment(RefNo,Amount,PayType);
        dismiss();
    }
    private void fnNO(){
        ((CashReceipt)getActivity()).setPayment(RefNo,Amount,PayType);
        dismiss();
    }
    private String genPrint(){
        String strPrint="";
        try{
            DBAdapter db=new DBAdapter(getContext());
            db.openDB();
            Cursor cPrint=db.getSettingPrint();
            while (cPrint.moveToNext()) {
                TypePrinter = cPrint.getString(1);
                NamePrinter = cPrint.getString(2);
                IPPrinter   = cPrint.getString(3);
                PortPrinter = cPrint.getString(5);
            }
            int len=48;
            if(TypePrinter.equals("AIDL") || TypePrinter.equals("Ipos AIDL")){
                len=32;
            }
            txtHeader.setText("Process Printing ...");
            String vLine1=str_pad("iPay88", len, " ", "STR_PAD_BOTH");
            String vLine2=str_pad("MERCHANT CODE: " +MerchantCode, len, " ", "STR_PAD_RIGHT");
            String vLine3=str_pad("TERMINAL ID  : " +MerchantCode, len, " ", "STR_PAD_RIGHT");
            String vLine4=str_pad("SALE", len, " ", "STR_PAD_BOTH");
            String vLine5=str_pad("TRANS ID   : " +TransId, len, " ", "STR_PAD_RIGHT");
            String vLine6=str_pad("REF NO     : " +RefNo, len, " ", "STR_PAD_RIGHT");
            String vLine7=str_pad("DATETIME   : " +D_ateTime, len, " ", "STR_PAD_RIGHT");
            String vLine8=str_pad(PayType  , len, " ", "STR_PAD_BOTH");
            String vLine9=str_pad("AMOUNT      : " +Amount, len, " ", "STR_PAD_RIGHT");
            strPrint    = vLine1+"\n\n";
            strPrint     +=vLine2+"\n";
            strPrint     +=vLine3+"\n\n";
            strPrint     +=vLine4+"\n";
            strPrint     +=vLine5+"\n";
            strPrint     +=vLine6+"\n";
            strPrint     +=vLine7+"\n";
            strPrint     +=vLine8+"\n";
            if(TypePrinter.equals("AIDL") || TypePrinter.equals("Ipos AIDL")) {
                strPrint += vLine9 + "\n";
            }else{
                strPrint += vLine9 + "\n\n\n";
            }

            return strPrint;
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return strPrint;
    }

    private void setPrint(String stringPrint){
            Bitmap ipaylogo = ((BitmapDrawable) imgLogo.getDrawable()).getBitmap();
            byte[] command = Utils.decodeBitmap(ipaylogo);
            Bitmap bmpBarcode = ((BitmapDrawable) imgBarcode.getDrawable()).getBitmap();
            byte[] bBarcode = Utils.decodeBitmap(bmpBarcode);
           // Bitmap bmpBarcode=BitmapUtil.generateBitmap(TransId,8,80,26);
           // byte[] bBarcode = Utils.decodeBitmap(bmpBarcode);
            if (TypePrinter.equals("AIDL")) {
                AidlUtil.getInstance().sendRawData(command);
                AidlUtil.getInstance().printText(stringPrint);
                AidlUtil.getInstance().printBarCode(TransId, 8, 80, 26, 2);
                ResultPrint = "success";
            }else if(TypePrinter.equals("Ipos AIDL")){
                IposAidlUtil.getInstance().sendRawData(command);
                IposAidlUtil.getInstance().setPrint(stringPrint);
                IposAidlUtil.getInstance().printBarCode(TransId, 8, 80, 26, 2);
                ResultPrint = "success";
            } else if (TypePrinter.equals("Bluetooth")) {
                BluetoothPrinter fncheck = new BluetoothPrinter();
                isBT = fncheck.fnBluetooth3(getActivity(), NamePrinter,stringPrint ,command,bBarcode);
               // isBT = fncheck.fnBluetooth(getActivity(), NamePrinter, stringPrint);
                if (isBT == false) {
                    ResultPrint = "error print";
                }else{
                    ResultPrint="success";
                }
            } else if (TypePrinter.equals("Bluetooth Zebra")) {
                //Log.d("STR PRINT"+NamePrinter,stringPrint);
                BluetoothZebra fncheck = new BluetoothZebra();
                isBT = fncheck.fnBluetooth(getActivity(), NamePrinter, stringPrint);
                if (isBT == false) {
                    ResultPrint = "error print";
                }else{
                    ResultPrint="success";
                }
            } else if (TypePrinter.equals("Wifi")) {
                int Port = Integer.parseInt(PortPrinter);
                PrintingWifi fnprintw = new PrintingWifi();
                isBT = fnprintw.fnprintwifi3(getActivity(), IPPrinter, Port, stringPrint,command,bBarcode);
                if (isBT == false) {
                    ResultPrint = "error print";
                }else{
                    ResultPrint="success";
                }
            } else if (TypePrinter.equals("USB")) {
                PrintingUSB fnprintu = new PrintingUSB();
                isBT = fnprintu.fnprintusb(getActivity(), stringPrint);
                if (isBT == false) {
                    ResultPrint = "error print";
                }else{
                    ResultPrint="success";
                }
            } else {
                dismiss();
                //No Printer
            }
            if(ResultPrint.equals("success")){
                txtHeader.setText("Print receipt copy for Customer ?");
                lnButton.setVisibility(View.VISIBLE);
                pbPrinting.setVisibility(View.GONE);
            }
    }

    public String str_pad(String input, int length, String pad, String    sense)
    {
        int resto_pad = length - input.length();
        String padded = "";

        if (resto_pad <= 0){ return input; }

        if(sense.equals("STR_PAD_RIGHT"))
        {
            padded  = input;
            padded += _fill_string(pad,resto_pad);
        }
        else if(sense.equals("STR_PAD_LEFT"))
        {
            padded  = _fill_string(pad, resto_pad);
            padded += input;
        }
        else // STR_PAD_BOTH
        {
            int pad_left  = (int) Math.ceil(resto_pad/2);
            int pad_right = resto_pad - pad_left;

            padded  = _fill_string(pad, pad_left);
            padded += input;
            padded += _fill_string(pad, pad_right);
        }
        return padded;
    }


    protected String _fill_string(String pad, int resto ) {
        boolean first = true;
        String padded = "";

        if (resto >= pad.length())
        {
            for (int i = resto; i >= 0; i = i - pad.length())
            {
                if (i  >= pad.length())
                {
                    if (first){ padded = pad; } else { padded += pad; }
                }
                else
                {
                    if (first){ padded = pad.substring(0, i); } else { padded += pad.substring(0, i); }
                }
                first = false;
            }
        }
        else
        {
            padded = pad.substring(0,resto);
        }
        return padded;
    }

}
