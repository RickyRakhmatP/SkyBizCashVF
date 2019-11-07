package skybiz.com.posoffline.ui_CashReceipt.m_Print.By_IposAIDL.Utils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;


import com.iposprinter.iposprinterservice.IPosPrinterCallback;
import com.iposprinter.iposprinterservice.IPosPrinterService;

import java.util.Random;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.ESCUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_IposAIDL.ThreadPoolManager;


public class IposAidlUtil {

    private static final String SERVICE＿PACKAGE = "com.iposprinter.iposprinterservice";
    private static final String SERVICE＿ACTION = "com.iposprinter.iposprinterservice.IPosPrintService";
    private static final String TAG                 = "IPosPrinterTestDemo";

    private final String  PRINTER_NORMAL_ACTION = "com.iposprinter.iposprinterservice.NORMAL_ACTION";
    private final String  PRINTER_PAPERLESS_ACTION = "com.iposprinter.iposprinterservice.PAPERLESS_ACTION";
    private final String  PRINTER_PAPEREXISTS_ACTION = "com.iposprinter.iposprinterservice.PAPEREXISTS_ACTION";
    private final String  PRINTER_THP_HIGHTEMP_ACTION = "com.iposprinter.iposprinterservice.THP_HIGHTEMP_ACTION";
    private final String  PRINTER_THP_NORMALTEMP_ACTION = "com.iposprinter.iposprinterservice.THP_NORMALTEMP_ACTION";
    private final String  PRINTER_MOTOR_HIGHTEMP_ACTION = "com.iposprinter.iposprinterservice.MOTOR_HIGHTEMP_ACTION";
    private final String  PRINTER_BUSY_ACTION = "com.iposprinter.iposprinterservice.BUSY_ACTION";
    private final String  PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION = "com.iposprinter.iposprinterservice.CURRENT_TASK_PRINT_COMPLETE_ACTION";

    private final int MSG_TEST                               = 1;
    private final int MSG_IS_NORMAL                          = 2;
    private final int MSG_IS_BUSY                            = 3;
    private final int MSG_PAPER_LESS                         = 4;
    private final int MSG_PAPER_EXISTS                       = 5;
    private final int MSG_THP_HIGH_TEMP                      = 6;
    private final int MSG_THP_TEMP_NORMAL                    = 7;
    private final int MSG_MOTOR_HIGH_TEMP                    = 8;
    private final int MSG_MOTOR_HIGH_TEMP_INIT_PRINTER       = 9;
    private final int MSG_CURRENT_TASK_PRINT_COMPLETE     = 10;

    /*循环打印类型*/
    private final int PRINTER_NORMAL = 0;
    private final int  MULTI_THREAD_LOOP_PRINT  = 1;
    private final int  INPUT_CONTENT_LOOP_PRINT = 2;
    private final int  DEMO_LOOP_PRINT          = 3;
    private final int  PRINT_DRIVER_ERROR_TEST  = 4;
    private final int  DEFAULT_LOOP_PRINT       = 0;

    //循环打印标志位
    private       int  loopPrintFlag            = DEFAULT_LOOP_PRINT;
    private       byte loopContent              = 0x00;
    private       int  printDriverTestCount     = 0;

    private HandlerUtils.MyHandler handler;
    private Random random = new Random();
    private int printerStatus = 0;
    private IPosPrinterService IposService;
    private IPosPrinterCallback callback = null;
    private static IposAidlUtil mIposUtil = new IposAidlUtil();
    private Context context;


    private HandlerUtils.IHandlerIntent iHandlerIntent = new HandlerUtils.IHandlerIntent()
    {
        @Override
        public void handlerIntent(Message msg)
        {
            switch (msg.what)
            {
                case MSG_TEST:
                    break;
                case MSG_IS_NORMAL:
                    if(getPrinterStatus() == PRINTER_NORMAL)
                    {
                        loopPrint(loopPrintFlag);
                    }
                    break;
                case MSG_IS_BUSY:
                    Toast.makeText(context, "Printer is Bussy", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_LESS:
                    loopPrintFlag = DEFAULT_LOOP_PRINT;
                    Toast.makeText(context,"Printer Loop", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_PAPER_EXISTS:
                    Toast.makeText(context,"Paper Exist", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_THP_HIGH_TEMP:
                    Toast.makeText(context,"MSH TEMP HIGH", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_MOTOR_HIGH_TEMP:
                    loopPrintFlag = DEFAULT_LOOP_PRINT;
                    Toast.makeText(context,"MSG_MOTOR_HIGH_TEM", Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessageDelayed(MSG_MOTOR_HIGH_TEMP_INIT_PRINTER, 180000);  //马达高温报警，等待3分钟后复位打印机
                    break;
                case MSG_MOTOR_HIGH_TEMP_INIT_PRINTER:
                    initPrinter();
                    break;
                case MSG_CURRENT_TASK_PRINT_COMPLETE:
                  //  Toast.makeText(IPosPrinterTestDemo.this, R.string.printer_current_task_print_complete, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    private BroadcastReceiver IPosPrinterStatusListener = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if(action == null)
            {
                Log.d(TAG,"IPosPrinterStatusListener onReceive action = null");
                return;
            }
            Log.d(TAG,"IPosPrinterStatusListener action = "+action);
            if(action.equals(PRINTER_NORMAL_ACTION))
            {
                handler.sendEmptyMessageDelayed(MSG_IS_NORMAL,0);
            }
            else if (action.equals(PRINTER_PAPERLESS_ACTION))
            {
                handler.sendEmptyMessageDelayed(MSG_PAPER_LESS,0);
            }
            else if (action.equals(PRINTER_BUSY_ACTION))
            {
                handler.sendEmptyMessageDelayed(MSG_IS_BUSY,0);
            }
            else if (action.equals(PRINTER_PAPEREXISTS_ACTION))
            {
                handler.sendEmptyMessageDelayed(MSG_PAPER_EXISTS,0);
            }
            else if (action.equals(PRINTER_THP_HIGHTEMP_ACTION))
            {
                handler.sendEmptyMessageDelayed(MSG_THP_HIGH_TEMP,0);
            }
            else if (action.equals(PRINTER_THP_NORMALTEMP_ACTION))
            {
                handler.sendEmptyMessageDelayed(MSG_THP_TEMP_NORMAL,0);
            }
            else if (action.equals(PRINTER_MOTOR_HIGHTEMP_ACTION))  //此时当前任务会继续打印，完成当前任务后，请等待2分钟以上时间，继续下一个打印任务
            {
                handler.sendEmptyMessageDelayed(MSG_MOTOR_HIGH_TEMP,0);
            }
            else if(action.equals(PRINTER_CURRENT_TASK_PRINT_COMPLETE_ACTION))
            {
                handler.sendEmptyMessageDelayed(MSG_CURRENT_TASK_PRINT_COMPLETE,0);
            }
            else
            {
                handler.sendEmptyMessageDelayed(MSG_TEST,0);
            }
        }
    };

    /**
     * 绑定服务实例
     */
    private ServiceConnection connectService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IposService = IPosPrinterService.Stub.asInterface(service);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            IposService = null;
        }
    };


    private IposAidlUtil() {
    }

    public static IposAidlUtil getInstance() {
        return mIposUtil;
    }

    public void connectPrinterService(Context context) {
        this.context = context.getApplicationContext();
        handler = new HandlerUtils.MyHandler(iHandlerIntent);
        callback = new IPosPrinterCallback.Stub() {
            @Override
            public void onRunResult(final boolean isSuccess) throws RemoteException {
                Log.i(TAG,"result:" + isSuccess + "\n");
            }

            @Override
            public void onReturnString(final String value) throws RemoteException {
                Log.i(TAG,"result:" + value + "\n");
            }
        };
        Intent intent = new Intent();
        intent.setPackage("com.iposprinter.iposprinterservice");
        intent.setAction("com.iposprinter.iposprinterservice.IPosPrintService");
       // context.getApplicationContext().startService(intent);
        context.getApplicationContext().bindService(intent, connectService, Context.BIND_AUTO_CREATE);
        IntentFilter printerStatusFilter = new IntentFilter();
        printerStatusFilter.addAction(PRINTER_NORMAL_ACTION);
        printerStatusFilter.addAction(PRINTER_PAPERLESS_ACTION);
        printerStatusFilter.addAction(PRINTER_PAPEREXISTS_ACTION);
        printerStatusFilter.addAction(PRINTER_THP_HIGHTEMP_ACTION);
        printerStatusFilter.addAction(PRINTER_THP_NORMALTEMP_ACTION);
        printerStatusFilter.addAction(PRINTER_MOTOR_HIGHTEMP_ACTION);
        printerStatusFilter.addAction(PRINTER_BUSY_ACTION);
        context.getApplicationContext().registerReceiver(IPosPrinterStatusListener,printerStatusFilter);
    }

    /**
     * 断开服务
     *
     * @param context context
     */
    public void disconnectPrinterService(Context context) {
        if (IposService != null) {
            context.getApplicationContext().unbindService(connectService);
            IposService = null;
        }
    }

    public boolean isConnect() {
        return IposService != null;
    }

   /* public IPosPrinterCallback generateCB(final PrinterCallback printerCallback){
        return new IPosPrinterCallback.Stub(){
            @Override
            public void onRunResult(boolean isSuccess) throws RemoteException {

            }

            @Override
            public void onReturnString(String result) throws RemoteException {
                callback.onReturnString(result);
            }
        };
    }*/

    /**
     * 设置打印浓度
     */
    private int[] darkness = new int[]{0x0600, 0x0500, 0x0400, 0x0300, 0x0200, 0x0100, 0,
            0xffff, 0xfeff, 0xfdff, 0xfcff, 0xfbff, 0xfaff};

    public void setDarkness(int index) {
        if (IposService == null) {
            Log.d(TAG,"Service Printer Not Connected");
            //Toast.makeText(context,"服务已断开！", Toast.LENGTH_LONG).show();
            return;
        }

        int k = darkness[index];
        try {
            IposService.printRawData(ESCUtil.setPrinterDarkness(k), null);
            //IposService.printer(null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得打印机系统信息，放在list中
     *
     * @return list
     */

    /**
     * 初始化打印机
     */
    public void initPrinter() {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try{
                    IposService.printerInit(callback);
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 打印二维码
     */
    public void printQr(String data, int modulesize, int errorlevel) {
        if (IposService == null) {
            Log.d(TAG,"Service Printer Not Connected");
          //  Toast.makeText(context,"服务已断开！", Toast.LENGTH_LONG).show();
            return;
        }


        try {
            IposService.printQRCode(data, modulesize, errorlevel, null);
            IposService.printBlankLines(1, 10, callback);
            IposService.printerPerformPrint(160,  callback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印条形码
     */
    public void printBarCode(final String data, final int symbology, final int height, final int width, final int textposition) {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try{
                    IposService.printBarCode(data, symbology, height, width, textposition, callback);
                    IposService.printBlankLines(1, 10, callback);
                    IposService.printerPerformPrint(160,  callback);
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 打印文字
     */
    /*
    *打印图片
     */
    public void printBitmap(final Bitmap bitmap) {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try{
                    // IposService.printRawData(BytesUtil.BlackBlockData(height),null);
                    // IposService.printerPerformPrint(160,  callback);
                    IposService.printerInit(callback);
                    IposService.setPrinterPrintAlignment(1, callback);
                    IposService.printBitmap(0,500,bitmap,callback);
                    IposService.printBlankLines(1, 10, callback);
                    IposService.printerPerformPrint(160,  callback);
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 打印表格
     */

    public void sendRawData(final byte[] data) {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try{
                    // IposService.printRawData(BytesUtil.BlackBlockData(height),null);
                    // IposService.printerPerformPrint(160,  callback);
                    IposService.printerInit(callback);
                    IposService.printRawData(data, callback);
                    IposService.printBlankLines(1, 10, callback);
                    IposService.printerPerformPrint(160,  callback);
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void setPrint(String strPrint){
        if(getPrinterStatus()==PRINTER_NORMAL){
            printText(strPrint);
        }
    }
    public void printText(final String strPrint) {
        ThreadPoolManager.getInstance().executeTask(new Runnable() {
            @Override
            public void run() {
                try{
                    IposService.printerInit(callback);
                    IposService.printSpecifiedTypeText(strPrint, "ST", 24, callback);
                    IposService.printBlankLines(1, 10, callback);
                    IposService.printerPerformPrint(160,  callback);
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public int getPrinterStatus(){

        Log.i(TAG,"***** printerStatus"+printerStatus);
        try{
            printerStatus = IposService.getPrinterStatus();
        }catch (RemoteException e){
            e.printStackTrace();
        }
        Log.i(TAG,"#### printerStatus"+printerStatus);
        return  printerStatus;
    }

    /**
     * 循环打印
     */

    public void loopPrint(int flag)
    {
        switch (flag)
        {
            case MULTI_THREAD_LOOP_PRINT:
               // multiThreadLoopPrint();
                break;
            case DEMO_LOOP_PRINT:
                demoLoopPrint();
                break;
            case INPUT_CONTENT_LOOP_PRINT:
                //bigDataPrintTest(127, loopContent);
                break;
            case PRINT_DRIVER_ERROR_TEST:
                printDriverTest();
                break;
            default:
                break;
        }
    }

    /**
     * 并发多线程打印
     */

    public void demoLoopPrint()
    {
        Log.e(TAG, "发起演示模式 --> ");
        switch (random.nextInt(7))
        {
            case 0:
             //   printKoubeiBill();
                break;
            case 1:
              //  printBarcode();
                break;
            case 2:
              //  printBaiduBill();
                break;
            case 3:
               // printBitmap();
                break;
            case 4:
               // printErlmoBill();
                break;
            case 5:
              //  printQRCode();
                break;
            case 6:
              //  printMeiTuanBill();
                break;
            default:
                break;
        }
    }

    /**
     * 每次下发内容以64k为单位递增，最大512k
     */
    public void printDriverTest()
    {
        if (printDriverTestCount >= 8)
        {
            loopPrintFlag = DEFAULT_LOOP_PRINT;
            printDriverTestCount = 0;
        }
        else
        {
            printDriverTestCount++;
           // bigDataPrintTest(printDriverTestCount * 16, (byte) 0x11);
        }
    }


}
