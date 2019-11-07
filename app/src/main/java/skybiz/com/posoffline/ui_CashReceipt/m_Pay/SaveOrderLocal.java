package skybiz.com.posoffline.ui_CashReceipt.m_Pay;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class SaveOrderLocal extends AsyncTask<Void, Void, Void> {
    String IPAddress;
    int Port;
    String Message;
    String response="";

    public SaveOrderLocal(String IPAddress, int port, String message) {
        this.IPAddress = IPAddress;
        Port = port;
        Message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Socket socket=null;
        DataOutputStream dataOutputStream=null;
        DataInputStream dataInputStream=null;

        try{
            socket=new Socket(IPAddress,Port);
            dataOutputStream=new DataOutputStream(socket.getOutputStream());
            dataInputStream= new DataInputStream(socket.getInputStream());
            if(Message!=null){
                dataOutputStream.writeUTF(Message);
            }
            response=dataInputStream.readUTF();
        }catch (UnknownHostException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(socket!=null){
                try {
                    socket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if(dataInputStream!=null){
                try {
                    dataInputStream.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.d("RESPONSE",response);
        super.onPostExecute(result);
    }
}
