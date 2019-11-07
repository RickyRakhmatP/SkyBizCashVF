package skybiz.com.posoffline.ui_CashReceipt.m_Print.By_Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLinkOs;
import com.zebra.sdk.printer.discovery.BluetoothDiscoverer;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by 7 on 12/12/2017.
 */

public class BluetoothZebra {

    Context c;
    String MACAddress;
    BluetoothDevice mmDevice;
    BluetoothAdapter mBluetoothAdapter;
    private boolean printerFound;
    private int SEARCH_NEW_PRINTERS = -1;
    private final int MAX_RETRY_ATTEMPTS = 5;

    public Boolean fnBluetooth(Context c, String NameBT, String msg){
        if (findBT(NameBT)) {
            try {
                final String txtPrint=msg;
                connectBT(txtPrint);
                //sendText(txtPrint);
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage());
                e.printStackTrace();
            }
        } else {
            Log.e("ERROR"," Bluetooth Device Not Found: " + msg);
        }
        return true;
    }


    boolean findBT(String name) {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Log.d("RESULT", "No bluetooth adapter available");
            }
            if (!mBluetoothAdapter.isEnabled()) {
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    //Log.d("RESULT", "Bluetooth Device Found: " + device.getName());
                    if (device.getName().equalsIgnoreCase(name)) {
                        MACAddress=device.getAddress();
                        Log.d("RESULT", "Bluetooth Device Found: " +MACAddress);
                        mmDevice = mBluetoothAdapter
                                .getRemoteDevice(MACAddress);
                        return true;
                    }
                }
            }
            //Log.d("RESULT", "Bluetooth Device Found: " + mmDevice.getName());
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.d("ERROR", errMsg);
            e.printStackTrace();
        }
        return false;
    }

    boolean connectBT(String txtPrint)throws IOException{
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                Connection thePrinterConn = new BluetoothConnection(MACAddress);
                thePrinterConn.open();
                ZebraPrinter printer = ZebraPrinterFactory.getInstance(thePrinterConn);
                String printerLanguage = SGD.GET("device.languages", thePrinterConn);

                if (!printerLanguage.contains("zpl")) {
                    SGD.SET("device.languages", "hybrid_xml_zpl", thePrinterConn);
                }
                boolean isPrinterReady = getPrinterStatus(printer, 0);
                if (isPrinterReady) {
                    //printer.printImage(txtPrint,0,0);
                    printImageTheOldWay(txtPrint, printer, thePrinterConn, 0);
                } else {
                    printImageTheOldWay(txtPrint, printer, thePrinterConn, 0);
                }
                Thread.sleep(500);
                Log.d("ZEBRA", "Closing the connection...");
                thePrinterConn.close();
            }
            return true;
        }catch (ConnectionException e){
                if (e.getMessage().toLowerCase().contains("socket might closed")) {
                    // callbackContext.error(SEARCH_NEW_PRINTERS);
                } else {
                    // callbackContext.error(e.getMessage());
                }
        } catch (Exception e) {
            String errMsg = e.getMessage();
            Log.e("ERROR", errMsg);
            e.printStackTrace();
        }
        return false;
        //http://www.truiton.com/2015/04/android-bluetooth-low-energy-ble-example/
    }


    private void printImageTheOldWay(String txtPrint, ZebraPrinter printer, Connection thePrinterConn, int retryAttempts) throws Exception {
        boolean printerReady = getPrinterStatus(printer, 0);
        if (printerReady) {
           /* String cpcl = "! 0 200 200 ";
            //cpclData +="! 0 200 200 4099 1\r\n";
            cpcl += " 1\r\n";
            cpcl += "PW 750\r\nTONE 0\r\nSPEED 6\r\nSETFF 203 5\r\nON - FEED FEED\r\nAUTO - PACE\r\nJOURNAL\r\n";
            cpcl += "FORM\r\n";
            cpcl += "PRINT\r\n";
            String strPrint=cpcl+txtPrint;
            thePrinterConn.write(strPrint.getBytes());
            Log.d("ZEBRA", "Printing text...");*/
            //https://dan.iftodi.com/2016/01/english-mobile-printing-with-android-and-zebra-printer/
           /* String cpclData="";
            String[] toPrint = txtPrint.split("\n");;
            int vLength=toPrint.length;
            int i=0;
            int iHeight=25*(vLength+2);
            cpclData +="! 0 200 200 "+iHeight+" 1\r\n";
            for (String add : toPrint) {
                cpclData += "TEXT 7 0 0 "+i+" "+add+"\r\n";
                i= i+25;
            }*/
            String cpclData ="";
            cpclData +="! 0 200 200 4099 1\r\n";
            cpclData +="ML 47\r\n";
            cpclData +="TEXT 5 0 0 20\r\n";
            cpclData +=txtPrint;
            cpclData +="FORM\r\n";
            cpclData +="PRINT\r\n";
            Log.d("ZEBRA", "Printing text..."+cpclData);
            thePrinterConn.write(cpclData.getBytes());
        }

    }

    private boolean getPrinterStatus(ZebraPrinter printer, int retryAttempts) throws Exception {
        PrinterStatus printerStatus = null;
        try {
            printerStatus = printer.getCurrentStatus();

            if (printerStatus.isReadyToPrint) {
                return true;
            } else {
                if (retryAttempts < MAX_RETRY_ATTEMPTS) {
                    if (printerStatus.isPaused) {
                        throw new Exception("Printer is gepauzeerd. Gelieve deze eerst te activeren.");
                    } else if (printerStatus.isHeadOpen) {
                        throw new Exception("Printer staat open. Gelieve deze eerst te sluiten.");
                    } else if (printerStatus.isPaperOut) {
                        throw new Exception("Gelieve eerst de etiketten aan te vullen.");
                    } else {
                        return getPrinterStatus(printer, ++retryAttempts);
                    }
                } else {
                    throw new Exception("Onbekende printerfout opgetreden.");
                }

            }

        } catch (ConnectionException e) {
            Log.d("ZEBRA","ConnectionException: " + e.getMessage());
            if (retryAttempts < MAX_RETRY_ATTEMPTS) {
                Log.d("ZEBRA", "printer not ready, gonna retry...");
                Thread.sleep(3000);
                return getPrinterStatus(printer, ++retryAttempts);
            } else {
                throw new Exception("Onbekende printerfout opgetreden.");
            }
        }

    }
    public void pairPrinter(final String strPrint)  {
        final UUID SerialPortServiceClass_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        final BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();
        // final String PrinterBsid = "AC:3F:A4:74:D9:A1";

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream sOut;
                BluetoothSocket socket;
                BA.cancelDiscovery();
                BluetoothDevice BD = BA.getRemoteDevice(MACAddress);
                try {
                    socket = BD.createInsecureRfcommSocketToServiceRecord(SerialPortServiceClass_UUID);
                    if (!socket.isConnected()) {
                        socket.connect();
                        int i = 3000;
                        while ( ( !socket.isConnected() ) || ( i > 0 ) )
                            i--;
                    }
                    /*if (!socket.isConnected()) {
                        socket.connect();
                        Thread.sleep(1000); // <-- WAIT FOR SOCKET
                    }*/
                    sOut = socket.getOutputStream();
                    String cpclData="";;
                    cpclData +="! 0 200 200 210 1\r\n";
                    cpclData +=strPrint;
                    cpclData +="FORM\r\n";
                    cpclData +="PRINT\r\n";
                    /*String cpclData = "! 0 200 200 210 1\r\n"
                            + "TEXT 4 0 30 40 This is a CPCL test.\r\n"
                            + "FORM\r\n"
                            + "PRINT\r\n";*/
                    sOut.write(cpclData.getBytes());
                    sOut.close();

                    socket.close();
                    BA.cancelDiscovery();
                } catch (InterruptedIOException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e("", "IOException");
                    e.printStackTrace();
                    return;
                }
            }
        });
        t.start();
    }

    private void sendText(final String txtPrint) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (bluetoothAdapter.isEnabled()) {
                        Connection thePrinterConn = new BluetoothConnection(MACAddress);
                        thePrinterConn.open();
                        ZebraPrinter printer = ZebraPrinterFactory.getInstance(thePrinterConn);
                        String printerLanguage = SGD.GET("device.languages", thePrinterConn);
                        if (!printerLanguage.contains("zpl")) {
                            SGD.SET("device.languages", "hybrid_xml_zpl", thePrinterConn);
                        }
                        boolean isPrinterReady = getPrinterStatus(printer, 0);
                        if(isPrinterReady) {
                            Looper.prepare();
                            printImageTheOldWay(txtPrint, printer, thePrinterConn, 0);
                            Thread.sleep(1000);
                            thePrinterConn.close();
                            Looper.myLooper().quit();
                        }else{
                            Looper.prepare();
                            printImageTheOldWay(txtPrint, printer, thePrinterConn, 0);
                            Thread.sleep(1000);
                            thePrinterConn.close();
                            Looper.myLooper().quit();
                        }
                    } else {
                        Log.d("INFO","Zebra error state");
                        //callbackContext.error("Bluetooth staat niet aan.");
                    }
                } catch (ConnectionException e) {
                    if (e.getMessage().toLowerCase().contains("socket might closed")) {
                        Log.d("Error",e.getMessage());
                    } else {
                        Log.d("Error",e.getMessage());
                    }
                } catch (Exception e) {
                    Log.d("Error",e.getMessage());
                }
            }
        }).start();
    }

}
