package skybiz.com.posoffline.ui_CashReceipt.m_Pay;

import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sunmi.scan.Config;
import com.sunmi.scan.Image;
import com.sunmi.scan.ImageScanner;
import com.sunmi.scan.Symbol;
import com.sunmi.scan.SymbolSet;

import java.util.List;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Scan.FinderView;
import skybiz.com.posoffline.m_Scan.SoundUtils;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;

public class ScanTable extends AppCompatActivity  implements SurfaceHolder.Callback{

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
    String DocType;
    public int i=1;
    Button btnReScan,btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_table);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                DocType= null;
            } else {
                DocType= extras.getString("DOCTYPE_KEY");

            }
        } else {
            DocType= (String) savedInstanceState.getSerializable("DOCTYPE_KEY");
        }
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnReScan = (Button) findViewById(R.id.btnReScan);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnconfirm();
            }
        });
        btnReScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnrescan();
            }
        });
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
        asyncDecode = new AsyncDecode(this);
        decode_count = 0;
        btnConfirm.setEnabled(false);
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
                asyncDecode = new AsyncDecode(ScanTable.this);
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
                btnConfirm.setEnabled(true);
                //sendBarcode(str);
                i++;
                // RequestPayment requestPayment=new RequestPayment(c,str,PaymentId,
                // MerchantCode,MerchantKey,PayType);
                // requestPayment.execute();
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
    private void sendBarcode(String BarCode){
            finish();
            Intent i = new Intent(this, CashReceipt.class);
            i.putExtra("TRANSID_KEY", "");
            i.putExtra("AMOUNT_KEY", "");
            i.putExtra("PAYTYPE_KEY", DocType);
            i.putExtra("MERCHANTCODE_KEY", "");
            i.putExtra("REFNO_KEY", BarCode);
            startActivity(i);
    }

    private void fnconfirm(){
        btnConfirm.setEnabled(false);
        String Barcode=textview.getText().toString().trim();
        sendBarcode(Barcode);
    }

    private void fnrescan(){
        try {
            textview.setText("");
            btnConfirm.setEnabled(false);
            mCamera.setDisplayOrientation(90);//竖屏显示
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(previewCallback);
            mCamera.startPreview();
            mCamera.autoFocus(autoFocusCallback);
        } catch (Exception e) {
            Log.d("DBG", "Error starting camera preview: " + e.getMessage());
        }
       // init();
        //super.onResume();
       // finish();
      //  Intent i=new Intent(this, ScanTable.class);
       // i.putExtra("DOCTYPE_KEY", DocType);
       // startActivity(i);
        //onResume();
    }
}
