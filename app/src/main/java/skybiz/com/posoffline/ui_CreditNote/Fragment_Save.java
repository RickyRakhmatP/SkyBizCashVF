package skybiz.com.posoffline.ui_CreditNote;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ipay.Ipay;
import com.ipay.IpayPayment;
import com.ipay.IpayResultDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CreditNote.m_Save.CheckPaidAmt;
import skybiz.com.posoffline.ui_CreditNote.m_Save.SaveTrn;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class Fragment_Save extends Fragment {
    View view;
    TextView txtAmountDue,txtDoc1No,txtTotalAmt,txtHCGbDiscount;
    LinearLayout lnCash,lnCredit;
    Button btnPay;
    String IPAddress,Password,DBName,UserName,isTotal,totalamt,Doc1No;
    String PaidAmount,ChangeAmount,NewDoc,CC1No,vBalance,vCC1Amt,CC1Code;
    String TypePrinter,NamePrinter,IPPrinter,UUID;
    Double CashAmt,ChangeAmt,CC1Amt,BalanceAmt;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentcn_save, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipePayment);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPayment();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        txtDoc1No= (TextView) view.findViewById(R.id.txtDoc1No);
        txtAmountDue= (TextView) view.findViewById(R.id.txtAmountDue);
        txtTotalAmt = (TextView) view.findViewById(R.id.txtTotal);
        txtHCGbDiscount = (TextView) view.findViewById(R.id.txtHCGbDiscount);
        btnPay=(Button)view.findViewById(R.id.btnPay);
        lnCash= (LinearLayout) view.findViewById(R.id.ln_cash);
       // lnCredit= (LinearLayout) view.findViewById(R.id.ln_credit);
        //btn pay cash
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPay.setEnabled(false);
                PaidAmount= (String) txtTotalAmt.getText();
                PaidAmount=PaidAmount.replaceAll(",","");
                if(PaidAmount.equals("0.00") || PaidAmount.length()==0){
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Total Amount Cannot Empty");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    btnPay.setEnabled(true);
                }else{
                    SaveTrn fnsave=new SaveTrn(getActivity(),Doc1No);
                    fnsave.execute();
                    //testIpay88();
                    btnPay.setEnabled(true);
                    txtDoc1No.setText("");
                }
            }
        });
        readyPrinter();
        return view;
    }
    private void readyPrinter(){
        DBAdapter db = new DBAdapter(getContext());
        db.openDB();
        Cursor c=db.getAllSeting();
        while (c.moveToNext()) {
            int RunNo=c.getInt(0);
            Log.d("RESULT", ""+RunNo);
            IPAddress = c.getString(1);
            UserName = c.getString(2);
            Password = c.getString(3);
            DBName = c.getString(4);
        }
        Cursor cPrinter=db.getSettingPrint();
        while (cPrinter.moveToNext()) {
            TypePrinter = cPrinter.getString(1);
            NamePrinter = cPrinter.getString(2);
            IPPrinter = cPrinter.getString(3);
            UUID = cPrinter.getString(4);
        }
        if(TypePrinter.equals("AIDL")){
            AidlUtil.getInstance().connectPrinterService(getActivity());
            AidlUtil.getInstance().initPrinter();
        }else{
            //
        }
        db.closeDB();
    }
    private void fnchecktotal(){
        String TotalAmt="";
        String TotalDiscount="";
        DBAdapter db = new DBAdapter(getContext());
        db.openDB();
        String vQuery="select sum(HCLineAmt + HCDiscount) as TotalAmt, sum(HCDiscount) as TotalDiscount " +
                " from cloud_cus_inv_dt where DocType='CusCN' ";
        Cursor cRunNo = db.getQuery(vQuery);
        while (cRunNo.moveToNext()) {
            Double dTotalAmt        = cRunNo.getDouble(0);
            Double dTotalDiscount   = cRunNo.getDouble(1);
            TotalAmt=String.format(Locale.US, "%,.2f", dTotalAmt);
            TotalDiscount=String.format(Locale.US, "%,.2f", dTotalDiscount);
        }
        txtTotalAmt.setText(TotalAmt);
        txtHCGbDiscount.setText(TotalDiscount);
    }
    private void retLastNo(){
        String Prefix="";
        String LastNo="";
        DBAdapter db = new DBAdapter(getContext());
        db.openDB();
        String vQuery="select Prefix,LastNo from sys_runno_dt where RunNoCode='CusCN' ";
        Cursor cRunNo = db.getQuery(vQuery);
        while (cRunNo.moveToNext()) {
            Prefix= cRunNo.getString(0);
            LastNo= cRunNo.getString(1);
        }
        Doc1No=Prefix+LastNo;
        txtDoc1No.setText(Doc1No);
        db.closeDB();
    }

    public void refreshPayment() {
        retLastNo();
        CheckPaidAmt chekpaid=new CheckPaidAmt(getActivity());
        fnchecktotal();
        isTotal=chekpaid.fncheckpaid();
        if(!isTotal.equals("error")){
            totalamt=isTotal;
        }else{
            totalamt="0.00";
        }
        txtAmountDue.setText(totalamt);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Fragment_Save.this.refreshPayment();
            InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            mImm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }


    public static int IPAY88_ACT_REQUEST_CODE = 88;
    public static int REQUEST_PERMISSION_REQUEST_CODE = 87;

    public static boolean isInProgress = false;
    public static int     r_status;
    public static String  r_transactionId;
    public static String  r_referenceNo;
    public static String  r_amount;
    public static String  r_remarks;
    public static String  r_authCode;
    public static String  r_err;

    private ResultDelegate iPayDelegate;
    static public class ResultDelegate implements IpayResultDelegate, Serializable {

        private static final long serialVersionUID = 5963066398271211659L;


        public final static int STATUS_OK = 1;
        public final static int STATUS_FAILED = 2;
        public final static int STATUS_CANCELED = 0;

        public void onPaymentSucceeded (String transId, String refNo, String amount, String remarks, String auth)
        {
            r_status = STATUS_OK;
            r_transactionId = transId;
            r_referenceNo = refNo;
            r_amount = amount;
            r_remarks = remarks;
            r_authCode = auth;
            Log.d("Respon Success",r_err);
        }

        public void onPaymentFailed (String transId, String refNo, String amount, String remarks, String err)
        {
            r_status = STATUS_FAILED;
            r_transactionId = transId;
            r_referenceNo = refNo;
            r_amount = amount;
            r_remarks = remarks;
            r_err = err;
            Log.d("Respon Failed",r_err);
        }

        public void onPaymentCanceled (String transId, String refNo, String amount, String remarks, String errDesc)
        {
           r_status = STATUS_CANCELED;
           r_transactionId = transId;
           r_referenceNo = refNo;
           r_amount = amount;
           r_remarks = remarks;
           r_err = "canceled";
           Log.d("Respon Canceled",errDesc);
        }

        public void onRequeryResult (String merchantCode, String refNo, String amount, String result)
        {
            // TODO warning, this is a stub to satisfy superclass interface
            // requirements. We do not yet have any meaningful support for
            // requery in this Cordova library yet.
        }
    }




    private void testIpay88(){
        String AmountDue=txtAmountDue.getText().toString();
        Log.d("Amount Due",AmountDue);
        IpayPayment payment = new IpayPayment();
        payment.setMerchantKey("Vx7AbhyzGK");
        payment.setMerchantCode("M15137");
        payment.setPaymentId("");
        payment.setCurrency("MYR");
        payment.setRefNo(txtDoc1No.getText().toString());
        payment.setAmount(txtAmountDue.getText().toString());
        payment.setProdDesc("Credit Note");
        payment.setUserName("SkyBiz Ricky");
        payment.setUserEmail("ricky.rakhmatpr@gmail.com");
        payment.setUserContact("011234568");
        payment.setRemark("new credit note");
        payment.setLang ("UTF-8");
        payment.setCountry("MY");
        payment.setBackendPostURL ("https://www.skybiz.com.my/payment/backend_response.php");
        //payment.setBackendPostURL("https://www.mobile88.com/ePayment/WebService/M10379/GatewayService.svc");
        iPayDelegate = new ResultDelegate();
        Intent checkoutIntent = Ipay.getInstance().checkout(payment, getActivity(), iPayDelegate);
        checkoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(checkoutIntent, 1);
       // getActivity().startActivity(checkoutIntent);
      // startActivityForResult(getActivity(), checkoutIntent, IPAY88_ACT_REQUEST_CODE);
        /// startActivityForResult(checkoutIntent, 1);
    }

   /* @Override
    public void onActivityResult (int requestCode, int resultCode, Intent intent)
    {
        if(requestCode == IPAY88_ACT_REQUEST_CODE) {
            try {
                JSONObject resp = new JSONObject();
                resp.put("transactionId", r_transactionId);
                resp.put("referenceNo", r_referenceNo);
                resp.put("amount", r_amount);
                resp.put("remarks", r_remarks);
                switch(r_status) {
                    case ResultDelegate.STATUS_OK:
                        resp.put("authCode", r_authCode);
                        sendEvent(getActivity(),resp.toString());
                       // cordovaCallbackContext.success(resp);
                        break;
                    case ResultDelegate.STATUS_FAILED:
                    case ResultDelegate.STATUS_CANCELED:
                        resp.put("err", r_err);
                        sendEvent(getActivity(),resp.toString());
                        break;
                    default:
                        sendEvent(getActivity(),resp.toString());
                }
            } catch (Exception e) {
               Log.d("Error","Unexpected failure in iPay88 plugin: "+e.getMessage());
            } finally {
                isInProgress = false;
            }
        }
    }

    static void sendEvent(Context context, String jsonData) {
        try {
            JSONArray ja = new JSONArray(jsonData);
            JSONObject jo = null;
            for (int i = 0; i < ja.length(); i++) {
                jo = ja.getJSONObject(i);
                //jo.getString("ItemGroup");
                Toast.makeText(context, "Respon " +  jo,Toast.LENGTH_SHORT).show();
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }*/

    private static final String PHONE = Manifest.permission.READ_PHONE_STATE;


}
