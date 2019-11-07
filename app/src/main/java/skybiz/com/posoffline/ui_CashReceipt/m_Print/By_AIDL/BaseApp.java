package skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL;

import android.app.Application;

import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;

//import com.sunmi.printerhelper.utils.AidlUtil;

/**
 * Created by Administrator on 2017/4/27.
 */

public class BaseApp extends Application {
    private boolean isAidl;

    public boolean isAidl() {
        return isAidl;
    }

    public void setAidl(boolean aidl) {
        isAidl = aidl;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isAidl = true;
        AidlUtil.getInstance().connectPrinterService(this);
    }
}
