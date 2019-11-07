package skybiz.com.posoffline.m_Ipay88;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.ksoap2.HeaderProperty;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Ipay88.Service.FKJBasicHttpsBinding_IGatewayService;
import skybiz.com.posoffline.m_Ipay88.Service.FKJClientRequestModel;
import skybiz.com.posoffline.m_Ipay88.Service.FKJClientResponseModel;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.SaveCS;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class PaymentResult extends AppCompatActivity {

    String Doc1No,TransId,Amount,PayType,MerchantCode,MerchantKey,PaymentId,RefNo,BarcodeNo;
    String resultado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                Doc1No= null;
            } else {
                Doc1No= extras.getString("DOC1NO_KEY");
                Amount= extras.getString("AMOUNT_KEY");
                PaymentId= extras.getString("PAYID_KEY");
                PayType= extras.getString("PAYTYPE_KEY");
                MerchantCode= extras.getString("MERCHANTCODE_KEY");
                MerchantKey= extras.getString("MERCHANTKEY_KEY");
                BarcodeNo= extras.getString("BARCODENO_KEY");
            }
        } else {
            Doc1No= (String) savedInstanceState.getSerializable("DOC1NO_KEY");
            Amount= (String) savedInstanceState.getSerializable("AMOUNT_KEY");
            PaymentId= (String) savedInstanceState.getSerializable("PAYID_KEY");
            PayType= (String) savedInstanceState.getSerializable("PAYTYPE_KEY");
            MerchantCode= (String) savedInstanceState.getSerializable("MERCHANTCODE_KEY");
            MerchantKey= (String) savedInstanceState.getSerializable("MERCHANTKEY_KEY");
            BarcodeNo= (String) savedInstanceState.getSerializable("BARCODENO_KEY");
        }
        requestPay();
    }

   private void requestPay(){
       RequestPayment requestPayment=new RequestPayment(this,BarcodeNo,PaymentId,
               MerchantCode,MerchantKey,PayType);
       requestPayment.execute();
   }
    public class RequestPayment extends AsyncTask<Void, Void, Boolean> {
        Context c;
        String BarcodeNo,PaymentId,MerchantCode,MerchantKey,PayType;
        String rStatus="",rTransId="",rErrorDesc="";

        public RequestPayment(Context c, String barcodeNo,String PaymentId,
                              String MerchantCode,String MerchantKey, String PayType) {
            this.c = c;
            this.BarcodeNo = barcodeNo;
            this.PaymentId = PaymentId;
            this.MerchantCode = MerchantCode;
            this.MerchantKey = MerchantKey;
            this.PayType = PayType;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            resultado="";
            // TODO: attempt authentication against a network service.
            //WebService - Opciones
            return this.sendrequest();

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success == false) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            } else {
                // Toast.makeText(getApplicationContext(), "Info Result: " + resultado, Toast.LENGTH_LONG).show();
                if(!rStatus.equals("0")){
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    openPrinter(rTransId,Amount,PayType,MerchantCode,Doc1No);
                }else{
                    Toast.makeText(getApplicationContext(), "Info Result: " + resultado, Toast.LENGTH_LONG).show();
                    openPrinter("T202903670803",Amount,PayType,MerchantCode,Doc1No);
                }
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }

        private Boolean sendrequest(){
            try {
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String qMember = "select CusName,ContactTel,Email from tb_member";
                Cursor rsMember = db.getQuery(qMember);
                String CusName = "";
                String ContactTel = "";
                String Email = "";
                while (rsMember.moveToNext()) {
                    CusName = rsMember.getString(0);
                    ContactTel = rsMember.getString(1);
                    Email = rsMember.getString(2);
                }
                Double amt = (Double.parseDouble(Amount)) * 100;
                String newAmt = String.format(Locale.US, "%.0f", amt);
                Log.d("Amount", newAmt + " " + PaymentId);
                String signature = null;
                String toSign = MerchantKey + MerchantCode + Doc1No + newAmt + "MYR" + BarcodeNo;
                signature = AeSimpleSHA1.SHA1(toSign);
                Log.d("SIGNATURE", toSign + signature);
                FKJClientRequestModel param0 = new FKJClientRequestModel();
                param0.setProperty(1, Amount);
                param0.setProperty(3, BarcodeNo);
                param0.setProperty(11, "MYR");
                param0.setProperty(14, MerchantCode);
                param0.setProperty(15, PaymentId);
                param0.setProperty(16, "New Cash Receipt");
                param0.setProperty(17, Doc1No);
                param0.setProperty(19, signature);
                param0.setProperty(20, "SHA256");
                param0.setProperty(23, ContactTel);
                param0.setProperty(24, Email);
                param0.setProperty(25, CusName);
                param0.setProperty(27, "UTF-8");
                FKJBasicHttpsBinding_IGatewayService service = new
                        FKJBasicHttpsBinding_IGatewayService(
                        "https://payment.ipay88.com.my/ePayment/WebService/MHGatewayService/GatewayService.svc", 300000);
                service.httpHeaders.add(new HeaderProperty("Authorization", "Basic QWxhZGRpbjpPcGVuU2VzYW1l"));
                FKJClientResponseModel res = service.EntryPageFunctionality(param0);
                rStatus = res.getProperty(29).toString();
                rTransId = res.getProperty(31).toString();
                rErrorDesc = res.getProperty(12).toString();
                resultado = " Status: " + rStatus + " | " + rErrorDesc;
                Log.d("RESPON", res.Status + " " + res.ErrDesc);
                db.closeDB();
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public void openPrinter(String TransId, String Amount, String PayType,String MerchantCode,String Doc1No){
        finish();
        Intent i=new Intent(this,CashReceipt.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("TRANSID_KEY", TransId);
        i.putExtra("AMOUNT_KEY", Amount);
        i.putExtra("PAYTYPE_KEY", PayType);
        i.putExtra("MERCHANTCODE_KEY", MerchantCode);
        i.putExtra("REFNO_KEY", Doc1No);
        startActivity(i);
    }
}
