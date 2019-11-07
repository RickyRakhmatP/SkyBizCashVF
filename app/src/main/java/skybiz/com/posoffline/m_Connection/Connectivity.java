package skybiz.com.posoffline.m_Connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class Connectivity {
    /*
     * These constants aren't yet available in my API level (7), but I need to
     * handle these cases if they come up, on newer versions
     */
    public static final int NETWORK_TYPE_EHRPD = 14; // Level 11
    public static final int NETWORK_TYPE_EVDO_B = 12; // Level 9
    public static final int NETWORK_TYPE_HSPAP = 15; // Level 13
    public static final int NETWORK_TYPE_IDEN = 11; // Level 8
    public static final int NETWORK_TYPE_LTE = 13; // Level 11

    /**
     * Check if there is any connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    /**
     * Check if there is fast connectivity
     *
     * @param context
     * @return
     */
    public static Boolean isConnectedFast(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if ((info != null && info.isConnected())) {
            return Connectivity.isConnectionFast(info.getType(),
                    info.getSubtype(),context);
        } else
            return false;

    }

    /**
     * Check if the connection is fast
     *
     * @param type
     * @param subType
     * @return
     */
    public static boolean isConnectionFast(int type, int subType, Context context) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            //System.out.println("CONNECTED VIA WIFI");
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            int numberOfLevels = 5;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
            switch (level)
            {
                case 0:
                    return false;
                case 1:
                    return false;
                case 2:
                    return false;
                case 3:
                    return true;
                   // break;
                case 4:
                    return true;
                    //break;
                case 5:
                    return true;
                    //Log.d("LEVEL","Excellent");
                    //break;
                default:
                    return true;
            }
            //return "CONNECTED VIA WIFI";
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false;
                    //return "NETWORK TYPE 1xRTT"; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false;
                    //return "NETWORK TYPE CDMA (3G) Speed: 2 Mbps"; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false;
                    //return "NETWORK TYPE EDGE (2.75G) Speed: 100-120 Kbps"; // ~
                // 50-100
                // kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true;
                    //return "NETWORK TYPE EVDO_0"; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true;
                    //return "NETWORK TYPE EVDO_A"; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return true;
                    //return "NETWORK TYPE GPRS (2.5G) Speed: 40-50 Kbps"; // ~ 100
                // kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true;
                    //return "NETWORK TYPE HSDPA (4G) Speed: 2-14 Mbps"; // ~ 2-14
                // Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true;
                    //return "NETWORK TYPE HSPA (4G) Speed: 0.7-1.7 Mbps"; // ~
                // 700-1700
                // kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true;
                   // return "NETWORK TYPE HSUPA (3G) Speed: 1-23 Mbps"; // ~ 1-23
                // Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true;
                   // return "NETWORK TYPE UMTS (3G) Speed: 0.4-7 Mbps"; // ~ 400-7000
                // kbps
                // NOT AVAILABLE YET IN API LEVEL 7
                case Connectivity.NETWORK_TYPE_EHRPD:
                    return true;
                    //return "NETWORK TYPE EHRPD"; // ~ 1-2 Mbps
                case Connectivity.NETWORK_TYPE_EVDO_B:
                    return true;
                    //return "NETWORK_TYPE_EVDO_B"; // ~ 5 Mbps
                case Connectivity.NETWORK_TYPE_HSPAP:
                    return true;
                    //return "NETWORK TYPE HSPA+ (4G) Speed: 10-20 Mbps"; // ~ 10-20
                // Mbps
                case Connectivity.NETWORK_TYPE_IDEN:
                    return true;
                   // return "NETWORK TYPE IDEN"; // ~25 kbps
                case Connectivity.NETWORK_TYPE_LTE:
                    return true;
                    //return "NETWORK TYPE LTE (4G) Speed: 10+ Mbps"; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return false;
                    //return "NETWORK TYPE UNKNOWN";
                default:
                    return false;
                   // return "";
            }
        } else {
            return false;
           // return "";
        }
    }
}
