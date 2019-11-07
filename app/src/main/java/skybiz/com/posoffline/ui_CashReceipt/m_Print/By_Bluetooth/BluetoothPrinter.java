package skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by 7 on 12/12/2017.
 */

public class BluetoothPrinter{
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStreamBT;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    Boolean isSuccess;

    Context c;
    String ActionBT;
    BluetoothSocket btsocket;
    OutputStream btoutputstream;
    String mDeviceAddress;

    public Boolean fnBluetooth(Context c, String NameBT, String msg){
        if (findBT(NameBT)) {
            try {
                final String txtPrint=msg;
                if(connectBT()){
                   try {
                       printText(txtPrint);
                   } catch (IOException e) {
                       Log.e("ERROR PRINT", e.getMessage());
                       e.printStackTrace();
                   }
                }else{

                }
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.e("ERROR"," Bluetooth Device Not Found: " + msg);
            //callbackContext.error("Bluetooth Device Not Found: " + name);
        }
        return true;
    }


    public Boolean fnBluetooth2(String NameBT,Bitmap bmp){
        if (findBT(NameBT)) {
            try {
                final Bitmap bitmap=bmp;
                if(connectBT()){
                    try {
                        printBmp(bitmap);
                    } catch (IOException e) {
                        Log.e("ERROR PRINT", e.getMessage());
                        e.printStackTrace();
                    }
                }else{

                }
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.e("ERROR"," Bluetooth Device Not Found: " + bmp.toString());
            //callbackContext.error("Bluetooth Device Not Found: " + name);
        }
        return true;
    }

    public Boolean fnBluetooth3(Context c, String NameBT, String msg, byte[] data, byte[] bbarcode){
        if (findBT(NameBT)) {
            try {
                if(connectBT()){
                    final byte[] vData=data;
                    final String vmsg=msg;
                    final byte[] vbarcode=bbarcode;

                    try {
                        printBytes(vmsg,vData,vbarcode);
                    } catch (IOException e) {
                        Log.e("ERROR PRINT", e.getMessage());
                        e.printStackTrace();
                    }
                }else{

                }
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.e("ERROR"," Bluetooth Device Not Found");
            //callbackContext.error("Bluetooth Device Not Found: " + name);
        }
        return true;
    }

    public Boolean fnBtImg(Bitmap bitmap){
        try {
            printBmp(bitmap);
        } catch (IOException e) {
            Log.e("ERROR PRINT", e.getMessage());
            e.printStackTrace();
        }
        return true;
    }


    boolean findBT(String name) {
        //BluetoothAdapter mBluetoothAdapter = null;
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Log.d("RESULT", "No bluetooth adapter available");
            }
            if (!mBluetoothAdapter.isEnabled()) {
               // Intent enableBluetooth = new Intent(
                      //  BluetoothAdapter.ACTION_REQUEST_ENABLE);
               // startActivityForResult(enableBluetooth, 0);
                //Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
               /// this.startActivityForResult(enableBluetooth, 2);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    //Log.d("RESULT", "Bluetooth Device Found: " + device.getName());
                    if (device.getName().equalsIgnoreCase(name)) {
                        mDeviceAddress=device.getAddress();
                        Log.d("RESULT", "Bluetooth Device Found: " +mDeviceAddress);
                        mmDevice = mBluetoothAdapter
                                .getRemoteDevice(mDeviceAddress);
                       // mmDevice = device;
                        return true;
                    }
                }

            }
            Log.d("RESULT", "Bluetooth Device Found: " + mmDevice.getName());
            //printText("Bluetooth Device Found:");
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.d("ERROR", errMsg);
            e.printStackTrace();
            //callbackContext.error(errMsg);
        }
        return false;
    }

    boolean connectBT()throws IOException{
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStreamBT = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            Log.d("OPEN", "Bluetooth Opened: " + mmDevice.getName());
            beginListenForData();
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
            //callbackContext.error(errMsg);
        }
        return false;
    }

    void beginListenForData() {
        try {
           // final Handler handler = new Handler();
            // This is the ASCII code for a newline character
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
										/*final String data = new String(encodedBytes, "US-ASCII");
										readBufferPosition = 0;
										handler.post(new Runnable() {
											public void run() {
                                                Log.d("BEGIN",data);
												//myLabel.setText(data);
											}
										});*/

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    boolean printText(String msg) throws IOException {
        try {
            mmOutputStreamBT.write(msg.getBytes("GBK"));
            stopWorker = true;
            mmOutputStreamBT.close();
            mmInputStream.close();
            mmSocket.close();
            return true;
            } catch (Exception e) {
                String errMsg = e.getMessage();
                Log.e("ERROR", errMsg);
                e.printStackTrace();
            }
        return false;
    }

    boolean printBytes(String strmsg,byte[] data, byte[] barcode) throws IOException {
        try {
            mmOutputStreamBT.write(data);
            mmOutputStreamBT.write(strmsg.getBytes("GBK"));
            mmOutputStreamBT.write(barcode);
            String strLen="\n\n\n\n";
            mmOutputStreamBT.write(strLen.getBytes());
            stopWorker = true;
            mmOutputStreamBT.close();
            mmInputStream.close();
            mmSocket.close();
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
        }
        return false;
    }

    boolean printBmp(Bitmap bmp) throws IOException {
        try {
            byte[] bmap=decodeBitmap(bmp);
            mmOutputStreamBT.write(bmap);
            stopWorker = true;
            mmOutputStreamBT.close();
            mmInputStream.close();
            mmSocket.close();
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
        }
        return false;
    }

    boolean disconnectBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStreamBT.close();
            mmInputStream.close();
            mmSocket.close();
            return true;
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
        }
        return false;
    }
    //public boolean isConnected() { return mmSocket == SocketState.CONNECTED; }
    public void write(int oneByte) throws IOException {
        byte b[] = new byte[1];
        b[0] = (byte)oneByte;
       mmOutputStreamBT.write(b, 0, 1);
    }

    public static byte[] decodeBitmap(Bitmap bmp) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<>();
        StringBuffer sb;
        int zeroCount = bmpWidth % 8;
        String zeroStr = "";
        if (zeroCount > 0) {
            for (int i = 0; i < (8 - zeroCount); i++) zeroStr = zeroStr + "0";
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;
                if (r > 160 && g > 160 && b > 160) sb.append("0");
                else sb.append("1");
            }
            if (zeroCount > 0) sb.append(zeroStr);
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8 : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<>();
        commandList.add(commandHexString + widthHexString + heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    private static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<>();
        for (String binaryStr : list) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);
                String hexString = strToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;
    }

    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = {"0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111"};

    private static String strToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    private static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<>();
        for (String hexStr : list) commandList.add(hexStringToBytes(hexStr));
        return sysCopy(commandList);
    }

    private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) return null;
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }

        return destArray;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
