package skybiz.com.posoffline.ui_CashReceipt.m_Print.By_USB;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

/**
 * Created by 7 on 13/12/2017.
 */

public class PrintingUSB {
   // Context c;
    private UsbAdmin mUsbAdmin=null;

    public boolean fnprintusb(Context c, String txtPrint){
        try {
            if(openUsb(c)){
                try{
                    printUsb(txtPrint);
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
    boolean openUsb(Context c) throws IOException {
        try {
            //Intent intentPrint = new Intent();
            mUsbAdmin=new UsbAdmin(c);
            mUsbAdmin.Openusb();
            if(!mUsbAdmin.GetUsbStatus()) {
                Log.e("RESULT", "Error Open USB");
                //callbackContext.success("Error Open USB");
                //return false;
            } else {
                Log.e("RESULT", "Success Open USB");
                //callbackContext.success("Success Open USB");
                //return true;
            }
            return true;

        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
           // callbackContext.error(errMsg);
        }
        return false;
    }
    boolean printUsb(String msg) throws IOException {
        try {
            if(!mUsbAdmin.sendCommand(msg.getBytes())) {
                Log.e("RESULT", "Error Print Data Sent");
               // callbackContext.success("Error Print Data Sent");
                //return false;
            } else {
                Log.e("RESULT", "Success Print Data Sent");
               // callbackContext.success("Success Print Data Sent");
                byte SendCut[]={0x0a,0x0a,0x1d,0x56,0x01};
                mUsbAdmin.sendCommand(SendCut);
                //return true;
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
