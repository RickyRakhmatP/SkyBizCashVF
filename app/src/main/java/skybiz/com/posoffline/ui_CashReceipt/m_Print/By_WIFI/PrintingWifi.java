package skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

/**
 * Created by 7 on 13/12/2017.
 */

public class PrintingWifi {
    private Socketmanager mSockManager;
    // Context c;
    // String IPAddress,txtPrint;

    public boolean fnprintwifi(Context c, String IPAddress,int Port, String txtPrint){
        try {
            if(connWifi(c,IPAddress,Port)){
               try{
                   printWifi(txtPrint);
               } catch (IOException e) {
                   Log.e("ERROR PRINT", e.getMessage());
                   e.printStackTrace();
               }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean fnprintwifi3(Context c, String IPAddress,int Port, String txtPrint, byte[] data, byte[] bbarcode){
        try {
            if(connWifi(c,IPAddress,Port)){
                try{
                    printWifi2(txtPrint,data,bbarcode);
                } catch (IOException e) {
                    Log.e("ERROR PRINT", e.getMessage());
                    e.printStackTrace();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean connWifi(Context c, String IPAddress, int Port) throws IOException {
        mSockManager=new Socketmanager(c);
        mSockManager.mPort=Port;
        mSockManager.mstrIp=IPAddress;
        mSockManager.threadconnect();
        try {
            Thread.sleep(100);
            if (mSockManager.getIstate()) {
                Log.e("RESULT", "Success Open Connection");
                //callbackContext.success("Success Open Connection" +msg);
            }
            else {
                Log.e("RESULT", "Error Open Connection");
                //callbackContext.success("Error Open Connection" +msg);
            }
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
            //callbackContext.error(errMsg);
        }
        return false;
    }

    boolean printWifi(String msg) throws IOException {
        mSockManager.threadconnectwrite(msg.getBytes("GBK"));
        try {
            Thread.sleep(100);
            if (mSockManager.getIstate()) {
                byte SendCut[]={0x0a,0x0a,0x1d,0x56,0x01};
                mSockManager.threadconnectwrite(SendCut);
            }
            else {
                Log.e("ERROR", "Error printing via Wifi");
                //callbackContext.success("Error Print via Wifi");
            }
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
            //callbackContext.error(errMsg);
        }
        return false;
    }
    boolean printWifi2(String msg,byte[]data,byte[] bbarcode) throws IOException {
        mSockManager.threadconnectwrite(data);
        mSockManager.threadconnectwrite(msg.getBytes("GBK"));
        mSockManager.threadconnectwrite(bbarcode);
        String strLen="\n\n\n";
        mSockManager.threadconnectwrite(strLen.getBytes());
        try {
            Thread.sleep(100);
            if (mSockManager.getIstate()) {
                byte SendCut[]={0x0a,0x0a,0x1d,0x56,0x01};
                mSockManager.threadconnectwrite(SendCut);
            }
            else {
                Log.e("ERROR", "Error printing via Wifi");
                //callbackContext.success("Error Print via Wifi");
            }
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
            //callbackContext.error(errMsg);
        }
        return false;
    }
}
