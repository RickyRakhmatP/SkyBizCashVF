package skybiz.com.posoffline.m_Ipay88.Merchant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.leansoft.nano.soap11.Fault;
import com.leansoft.nano.ws.SOAPServiceCallback;
import com.sunmi.scan.Config;
import com.sunmi.scan.Image;
import com.sunmi.scan.ImageScanner;
import com.sunmi.scan.Symbol;
import com.sunmi.scan.SymbolSet;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Ipay88.AeSimpleSHA1;
import skybiz.com.posoffline.m_Ipay88.DialogPrint;
import skybiz.com.posoffline.m_Ipay88.MySoap;
import skybiz.com.posoffline.m_Ipay88.PaymentResult;
import skybiz.com.posoffline.m_Ipay88.Service.FKJBasicHttpBinding_IGatewayService;
import skybiz.com.posoffline.m_Ipay88.Service.FKJBasicHttpsBinding_IGatewayService;
import skybiz.com.posoffline.m_Ipay88.Service.FKJClientRequestModel;
import skybiz.com.posoffline.m_Ipay88.Service.FKJClientResponseModel;
import skybiz.com.posoffline.m_Ipay88.Service.GatewayServiceClient;
import skybiz.com.posoffline.m_Ipay88.WebServiceServer.Client.GatewayService_SOAPClient;
import skybiz.com.posoffline.m_Ipay88.WebServiceServer.ClientRequestModel;
import skybiz.com.posoffline.m_Ipay88.WebServiceServer.ClientResponseModel;
import skybiz.com.posoffline.m_Ipay88.XML_Par.EntryPageFunctionality;
import skybiz.com.posoffline.m_Ipay88.XML_Par.requestModelObj;
import skybiz.com.posoffline.m_Scan.FinderView;
import skybiz.com.posoffline.m_Scan.SoundUtils;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class MerchantScan2 extends AppCompatActivity implements SurfaceHolder.Callback {

    private Camera mCamera;
    private SurfaceHolder mHolder;
    private SurfaceView surface_view;
    private ImageScanner scanner;// declare the scanner
    private Handler autoFocusHandler;
    private AsyncDecode asyncDecode;
    SoundUtils soundUtils;
    private boolean vibrate;
    public int decode_count = 0;

    private FinderView finder_view;
    private TextView textview;
    String Doc1No,Amount,CusName,MerchantCode,MerchantKey,PaymentId,PayType;
    public int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_scan2);
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
            }
        } else {
            Doc1No= (String) savedInstanceState.getSerializable("DOC1NO_KEY");
            Amount= (String) savedInstanceState.getSerializable("AMOUNT_KEY");
            PaymentId= (String) savedInstanceState.getSerializable("PAYID_KEY");
            PayType= (String) savedInstanceState.getSerializable("PAYTYPE_KEY");
            MerchantCode= (String) savedInstanceState.getSerializable("MERCHANTCODE_KEY");
            MerchantKey= (String) savedInstanceState.getSerializable("MERCHANTKEY_KEY");
        }
        init();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanner != null) {
            scanner.destroy();
        }
        if (soundUtils != null) {
            soundUtils.release();
        }
    }

    private void init() {
        surface_view = (SurfaceView) findViewById(R.id.surface_view);
        finder_view = (FinderView) findViewById(R.id.finder_view);
        textview = (TextView) findViewById(R.id.textview);
        mHolder = surface_view.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(this);

        scanner = new ImageScanner();//创建扫描器
        scanner.setConfig(0, Config.X_DENSITY, 2);//行扫描间隔
        scanner.setConfig(0, Config.Y_DENSITY, 2);//列扫描间隔
        scanner.setConfig(0, Config.ENABLE_MULTILESYMS, 0);//是否开启同一幅图一次解多个条码,0表示只解一个，1为多个
        scanner.setConfig(0, Config.ENABLE_INVERSE, 0);//是否解反色的条码
        scanner.setConfig(Symbol.PDF417, Config.ENABLE, 0);//是否禁止PDF417码，默认开启
        autoFocusHandler = new Handler();
        asyncDecode = new AsyncDecode(MerchantScan2.this);
        decode_count = 0;
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
        }
        try {
            //摄像头预览分辨率设置和图像放大参数设置，非必须，根据实际解码效果可取舍
//			Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setPreviewSize(800, 480);  //设置预览分辨率
            //     parameters.set("zoom", String.valueOf(27 / 10.0));//放大图像2.7倍
//            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);//竖屏显示
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(previewCallback);
            mCamera.startPreview();
            mCamera.autoFocus(autoFocusCallback);
        } catch (Exception e) {
            Log.d("DBG", "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * 预览数据
     */
    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (asyncDecode.isStoped()) {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPreviewSize();//获取预览分辨率

                //创建解码图像，并转换为原始灰度数据，注意图片是被旋转了90度的
                Image source = new Image(size.width, size.height, "Y800");
                Rect scanImageRect = finder_view.getScanImageRect(size.height, size.width);
                //图片旋转了90度，将扫描框的TOP作为left裁剪
                source.setCrop(scanImageRect.top, scanImageRect.left, scanImageRect.height(), scanImageRect.width());
                source.setData(data);//填充数据
                asyncDecode = new AsyncDecode(MerchantScan2.this);
                asyncDecode.execute(source);//调用异步执行解码
            }
        }
    };

    public class AsyncDecode extends AsyncTask<Image, Void, Void> {
        private boolean stoped = true;
        private String str = "";
        Context c;
        //int i=0;

        public AsyncDecode(Context c) {
            this.c = c;
        }

        @Override
        protected Void doInBackground(Image... params) {
            stoped = false;
            StringBuilder sb = new StringBuilder();
            Image src_data = params[0];//获取灰度数据

            long startTimeMillis = System.currentTimeMillis();

            //解码，返回值为0代表失败，>0表示成功
            int nsyms = scanner.scanImage(src_data);

            long endTimeMillis = System.currentTimeMillis();
            long cost_time = endTimeMillis - startTimeMillis;

            if (nsyms != 0) {
                playBeepSoundAndVibrate();//解码成功播放提示音

                decode_count++;
                //sb.append("计数: " + String.valueOf(decode_count) + ", 耗时: " + String.valueOf(cost_time) + " ms \n");

                SymbolSet syms = scanner.getResults();//获取解码结果
                for (Symbol sym : syms) {
                    sb.append(sym.getResult());
                }
            }
            //stoped = true;
            str = sb.toString();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            stoped = true;
            //i=i+1;
            if (null == str || str.equals("")) {

            } else {
                textview.setText(str);
               // scanner.destroy();
                mCamera.stopPreview();
                    RequestPayment requestPayment=new RequestPayment(c,str,PaymentId,
                            MerchantCode,MerchantKey,PayType);
                   requestPayment.execute();
                ///textview.setText(str);//显示解码结果
            }

        }

        public boolean isStoped() {
            return stoped;
        }
    }

    /**
     * 自动对焦回调
     */
    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 500);
        }
    };

    //自动对焦
    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (null == mCamera || null == autoFocusCallback) {
                return;
            }
            Camera.Parameters p = mCamera.getParameters();
            List<String> focusModes = p.getSupportedFocusModes();

            if(focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                //Phone supports autofocus!
                mCamera.autoFocus(autoFocusCallback);
            }
            else {
                //Phone does not support autofocus!
            }

        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            Log.d("DBG", "surfaceCreated: " + e.getMessage());
            mCamera = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private void initBeepSound() {
        if (soundUtils == null) {
            soundUtils = new SoundUtils(this, SoundUtils.RING_SOUND);
            soundUtils.putSound(0, R.raw.beep);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initBeepSound();
        vibrate = false;
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (soundUtils != null) {
            soundUtils.playSound(0, SoundUtils.SINGLE_PLAY);
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }
    String resultado;
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
                if(!rStatus.equals("0") && i==1){
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    openPrinter(rTransId,Amount,PayType,MerchantCode,Doc1No);
                }else{
                    Toast.makeText(getApplicationContext(), "Info Result: " + resultado, Toast.LENGTH_LONG).show();
                    finish();
                    if(i==1) {
                       // openPrinter("T202903670803", Amount, PayType, MerchantCode, Doc1No);
                    }
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
                i=i+1;
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
        i.putExtra("TRANSID_KEY", TransId);
        i.putExtra("AMOUNT_KEY", Amount);
        i.putExtra("PAYTYPE_KEY", PayType);
        i.putExtra("MERCHANTCODE_KEY", MerchantCode);
        i.putExtra("REFNO_KEY", Doc1No);
        startActivity(i);
    }

               /* final String NAMESPACE = "http://schemas.datacontract.org/2004/07/MHPHGatewayService.Model";
            final String URL = "https://payment.ipay88.com.my/ePayment/WebService/MHGatewayService/GatewayService.svc";
            final String METHOD_NAME = "EntryPageFunctionality";
            final String SOAP_ACTION = "https://www.mobile88.com/IGatewayService/EntryPageFunctionality";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("MerchantCode", "M15137");
            request.addProperty("PaymentId", "336");
            request.addProperty("RefNo", Doc1No);
            request.addProperty("Amount", Amount);
            request.addProperty("Currency", "MYR");
            request.addProperty("ProdDesc", "New Trn Cash Receipt");
            request.addProperty("UserName", "Cash Sales Account");
            request.addProperty("UserEmail", "admin@skybiz.com.my");
            request.addProperty("UserContact", "");
            request.addProperty("Remark", "new cash receipt");
            request.addProperty("Lang", "UTF-8");
            request.addProperty("BarcodeNo", BarcodeNo);
            request.addProperty("SignatureType", "SHA256");
            Log.d("AMOUNT",Amount);
            Double amt=(Double.parseDouble(Amount))*100;
            String signature= null;
            try {
                signature = AeSimpleSHA1.SHA1("Vx7AbhyzGKM15137"+Doc1No+amt+"MYR"+BarcodeNo);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            request.addProperty("Signature", signature);
           // SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
           // envelope.setOutputSoapObject(request);
            //envelope.dotNet=true;
            MySoap envelope = new MySoap (SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.addMapping(NAMESPACE,"EntryPageFunctionality", EntryPageFunctionality.class);
            envelope.addMapping(NAMESPACE,"requestModelObj", requestModelObj.class);
            envelope.implicitTypes = true;
            HttpTransportSE ht = new HttpTransportSE(URL);
            try {
                ht.call(SOAP_ACTION, envelope);
               // SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                //resultado = response.toString();
                //SoapObject result = (SoapObject) envelope.getResponse();
               // resultado = result.toString();
               // Log.i("Resultado: ", resultado);
            } catch (Exception e) {
                Log.i("Error: ", e.getMessage());
                e.printStackTrace();
                resultado=e.getMessage();
                return false;
            }
            if (envelope.bodyIn instanceof SoapFault) {
                String str= ((SoapFault) envelope.bodyIn).faultstring;
                Log.i("RESPON", str);
                resultado=str;
                // Another way to travers through the SoapFault object
            } else {
                SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
                Log.d("WS", String.valueOf(resultsRequestSOAP));
                resultado=String.valueOf(resultsRequestSOAP);
            }*/

           /* Double amt=(Double.parseDouble(Amount))*100;
            String signature= null;
            try {
                signature = AeSimpleSHA1.SHA1("Vx7AbhyzGKM15137"+Doc1No+amt+"MYR"+BarcodeNo);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            GatewayService_SOAPClient client = GatewayServiceClient.getSharedClient();
            client.setDebug(true); // enable soap message logging

            // build request
            ClientRequestModel request = new ClientRequestModel();
            try {
                request.MerchantCode = new String("M15137");
                request.PaymentId = new String("337");
                request.RefNo = new String(Doc1No);
                request.Amount=new String(Amount);
                request.Currency=new String("MYR");
                request.ProdDesc=new String("New Trn Cash Receipt");
                request.UserName=new String("Cash Sales Account");
                request.UserEmail=new String("admin@skybiz.com.my");
                request.UserContact=new String("");
                request.Remark=new String("new cash receipt");
                request.lang=new String("UTF-8");
                request.BarcodeNo=new String(BarcodeNo);
                request.Signature=new String(signature.trim());
                request.SignatureType=new String("SHA256");

            } catch (NumberFormatException ex) {
                Toast.makeText(c, "Invalid decimal number", Toast.LENGTH_LONG).show();
                //return;
            }

            // make API call and register callbacks
            client.clientRequestModel(request, new SOAPServiceCallback<ClientResponseModel>() {

                @Override
                public void onSuccess(ClientResponseModel responseObject) {
                    Log.d("INFO SUCCESS",responseObject.ErrDesc+responseObject.Status);// success
                    //Toast.makeText(c, responseObject.ErrDesc+responseObject.Status, Toast.LENGTH_LONG).show();
                    resultado=responseObject.ErrDesc+responseObject.Status;

                }

                @Override
                public void onFailure(Throwable error, String errorMessage) {
                    Log.d("INFO FAILURE",errorMessage);// http or parsing error
                    resultado=errorMessage;
                    //Toast.makeText(c, errorMessage, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSOAPFault(Object soapFault) { // soap fault
                    Fault fault = (Fault)soapFault;
                    Log.d("INFO FAULT",fault.faultstring);// h
                    resultado=fault.faultstring;
                   // Toast.makeText(c, fault.toString(), Toast.LENGTH_LONG).show();
                }
            });*/

}
