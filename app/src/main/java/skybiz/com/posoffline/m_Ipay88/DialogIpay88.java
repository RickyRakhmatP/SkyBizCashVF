package skybiz.com.posoffline.m_Ipay88;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ipay.Ipay;
import com.ipay.IpayPayment;
import com.ipay.IpayResultDelegate;

import java.io.Serializable;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Ipay88.Merchant.MerchantScan2;
import skybiz.com.posoffline.m_Ipay88.User.UserScan;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 23/02/2018.
 */

public class DialogIpay88 extends DialogFragment {
    View view;
    Button btnOnline,btnMerchant,btnCustomer,btnVoid;
    EditText txtAmount;
    String Amount,Doc1No,Doc2No,MerchantCode,MerchantKey,PaymentID,PayType;

    private final int RESULT_OK=0;
    private final int RESULT_CANCELLED=1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_ipay88, container, false);
        btnOnline=(Button)view.findViewById(R.id.btnOnline);
        btnMerchant=(Button)view.findViewById(R.id.btnMerchant);
        btnCustomer=(Button)view.findViewById(R.id.btnCustomer);
        txtAmount=(EditText)view.findViewById(R.id.txtAmount);
        btnVoid=(Button)view.findViewById(R.id.btnVoid);
        Amount=this.getArguments().getString("AMOUNT_KEY");
        Doc1No=this.getArguments().getString("DOC1NO_KEY");
        Doc2No=this.getArguments().getString("DOC2NO_KEY");
        PayType=this.getArguments().getString("PAYTYPE_KEY");
        MerchantCode=this.getArguments().getString("MERCHANTCODE_KEY");
        MerchantKey=this.getArguments().getString("MERCHANTKEY_KEY");
        txtAmount.setText(Amount);
        txtAmount.setEnabled(false);
        btnMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanMerchant();
            }
        });
        btnOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOnline();
            }
        });
        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanUser();
            }
        });
        btnVoid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVoid();
            }
        });
        return view;
    }

    private String getMerchantPayID(){
        String PayID="";
        switch (PayType) {
            case "Alipay ":
                PayID="234";
               break;
            case "Boost":
                PayID="320";
                break;
            case "Touch N Go":
                PayID="336";
                break;
            case "Mcash":
                PayID="329";
                break;
            case "UnionPay QR":
                PayID="338";
                break;
            default:
                return null;
        }
        return PayID;
    }
    private String getUserPayID(){
        String PayID="";
        switch (PayType) {
            case "Alipay":
                PayID="233";
                break;
            case "Boost":
                PayID="327";
                break;
            case "Touch N Go":
                PayID="337";
                break;
            case "Mcash":
                PayID="328";
                break;
            case "UnionPay QR":
                PayID="339";
                break;
            case "Nets QR":
                PayID="348";
                break;
            case "CIMB Pay":
                PayID="346";
                break;
            case "MBB QR":
                PayID="345";
                break;
            case "Grabpay":
                PayID="347";
                break;
            default:
                return null;
        }
        return PayID;
    }
    private void sendVoid(){
        String signature = AeSimpleSHA1.encrypt(MerchantKey+MerchantCode+Doc2No+"100MYR");
        Log.d("SIGNATURE",signature);
        SendVoidSOAP sendSOAP=new SendVoidSOAP(getActivity(),"M15137",Doc2No,
                "1.00","MYR",signature);
        sendSOAP.execute();
        dismiss();
    }
    private void scanMerchant(){
       // startActivity(new Intent(getActivity(), MerchantScan.class));
        String PaymentId=getMerchantPayID();
        //Log.d("Payment ID",PaymentId);
        Intent i=new Intent(getActivity(),MerchantScan2.class);
        i.putExtra("DOC1NO_KEY", Doc1No);
        i.putExtra("AMOUNT_KEY", Amount);
        i.putExtra("PAYID_KEY", PaymentId);
        i.putExtra("PAYTYPE_KEY", PayType);
        i.putExtra("MERCHANTCODE_KEY", MerchantCode);
        i.putExtra("MERCHANTKEY_KEY", MerchantKey);
        startActivity(i);
        dismiss();
    }
    private void scanUser(){
        // startActivity(new Intent(getActivity(), MerchantScan.class));
        String PaymentId=getUserPayID();
        Intent i=new Intent(getActivity(),UserScan.class);
        i.putExtra("DOC1NO_KEY", Doc1No);
        i.putExtra("AMOUNT_KEY", Amount);
        i.putExtra("PAYID_KEY", PaymentId);
        i.putExtra("MERCHANTCODE_KEY", MerchantCode);
        i.putExtra("MERCHANTKEY_KEY", MerchantKey);
        startActivity(i);
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
    static Context context;

    static class ResultDelegate implements IpayResultDelegate, Serializable {
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
            // fnsaveipay88("success",r_status);
            sendResult(context, r_referenceNo, r_amount);
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

    static void sendResult(Context c, String RefId, String AmountDue){
        ((CashReceipt)c).setPayment(RefId,AmountDue,"iPay88");
    }

    private void sendOnline(){
        try {
            DBAdapter db=new DBAdapter(getActivity());
            db.openDB();
            String qMember="select CusName from tb_member";
            Cursor rsMember=db.getQuery(qMember);
            String CusName="Cash Sales Account";
            while(rsMember.moveToNext()){
                CusName=rsMember.getString(0);
            }
            String PayId=getMerchantPayID();
            IpayPayment payment = new IpayPayment();
            payment.setMerchantKey(MerchantKey);
            payment.setMerchantCode(MerchantKey);
            payment.setPaymentId(PayId);
            payment.setCurrency("MYR");
            payment.setRefNo(Doc1No);
            payment.setAmount(Amount);
            payment.setProdDesc("Cash Receipt");
            payment.setUserName(CusName);
            payment.setUserEmail("admin@skybiz.com.my");
            payment.setUserContact("011234568");
            payment.setRemark("new cash receipt");
            payment.setLang("UTF-8");
            payment.setCountry("MY");
            // payment.setBackendPostURL("https://www.skybiz.com.my/payment/backend_response.php");
            //payment.setBackendPostURL(" https://www.mobile88.com/ePayment/WebService/M15137/GatewayService.svc");
            iPayDelegate = new ResultDelegate();
            Intent checkoutIntent = Ipay.getInstance().checkout(payment, getActivity(), iPayDelegate);
            checkoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(checkoutIntent, 1);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

}
