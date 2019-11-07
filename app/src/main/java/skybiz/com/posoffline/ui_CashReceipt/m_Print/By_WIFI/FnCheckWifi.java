package skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI;

import android.content.Context;
import android.util.Log;

/**
 * Created by 7 on 12/12/2017.
 */

public class FnCheckWifi {
    Context c;
    String IPAddress,z;
    private Socketmanager mSockManager;


   public String fncheck(Context c,String IPAddress, int Port){
       mSockManager=new Socketmanager(c);
       mSockManager.mPort=Port;
       mSockManager.mstrIp=IPAddress;
       mSockManager.threadconnect();
       try {
           Thread.sleep(100);
           if (mSockManager.getIstate()) {
               z="success";
           }
           else {
              z="error";
           }
           return z;
       } catch (Exception e) {
           String errMsg = e.getMessage();
           Log.e("ERROR", errMsg);
           e.printStackTrace();
       }
       return z;
   }


}
