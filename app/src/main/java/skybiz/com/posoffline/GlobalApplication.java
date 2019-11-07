package skybiz.com.posoffline;

import android.app.Application;
import android.content.Context;

import skybiz.com.posoffline.m_Connection.ConnectivityReceiver;

public class GlobalApplication extends Application {
    private static Context appContext;
    private static GlobalApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        mInstance = this;

        /* If you has other classes that need context object to initialize when application is created,
         you can use the appContext here to process. */
    }
    public static Context getAppContext() {
        return appContext;
    }
    public static synchronized GlobalApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}
