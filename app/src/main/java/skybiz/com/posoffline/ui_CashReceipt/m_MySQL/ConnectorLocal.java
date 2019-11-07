package skybiz.com.posoffline.ui_CashReceipt.m_MySQL;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 27/10/2017.
 */

public class ConnectorLocal {

    Socket socket = null;
    DataInputStream dataInputStream = null;
    DataOutputStream dataOutputStream = null;
    String results;

    public String  ConnectSocket(String IPAddress, int Port , String Request){
        try{

            socket = new Socket(IPAddress, Port);
            dataOutputStream = new DataOutputStream(
                    socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            // transfer JSONObject as String to the server
            dataOutputStream.writeUTF(Request);
            Log.i("SOCKET", "waiting for response from host");
            // Thread will wait till server replies
            results = dataInputStream.readUTF();
            return results;
        }catch (UnknownHostException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close socket
            if (socket != null) {
                try {
                    Log.i("SOCKET", "closing the socket");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // close input stream
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // close output stream
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return results;
    }
}
