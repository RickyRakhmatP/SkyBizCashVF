package skybiz.com.posoffline.m_Ipay88;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import skybiz.com.posoffline.ui_CashReceipt.m_VoidBill.DialogVoid;

public class SendVoidSOAP extends AsyncTask<Void,Void,String> {
    Context c;
    String MerchantCode,CCTransId,Amount,Currency,Signature;
    String URL,z;

    public SendVoidSOAP(Context c, String MerchantCode, String CCTransId, String Amount, String Currency, String Signature) {
        this.c = c;
        this.MerchantCode = MerchantCode;
        this.CCTransId = CCTransId;
        this.Amount = Amount;
        this.Currency = Currency;
        this.Signature = Signature;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.fnsend();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result.equals("success")){
            Toast.makeText(c,"Result Void "+z, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Info " +z, Toast.LENGTH_SHORT).show();
        }
    }

    private String sendvoid(){
        try{
           // z="success";
           URL="https://www.mobile88.com/ePayment/WebService/VoidFunction/VoidFunction.asmx";
            //URL="https://payment.ipay88.com.my/epayment/webservice/voidapi/voidfunction.asmx";
           HttpClient httpClient = new DefaultHttpClient();
            // post header
            HttpPost httpPost = new HttpPost(URL);
            httpPost.addHeader("Host" , "payment.ipay88.com.my");
            httpPost.addHeader("Content-Type" , "application/x-www-form-urlencoded");
            // add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair("merchantcode", MerchantCode));
            nameValuePairs.add(new BasicNameValuePair("cctransid", CCTransId));
            nameValuePairs.add(new BasicNameValuePair("amount", Amount));
            nameValuePairs.add(new BasicNameValuePair("currency", Currency));
            nameValuePairs.add(new BasicNameValuePair("signature", Signature));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // execute HTTP post request
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();

            if (resEntity != null) {
                String responseStr = EntityUtils.toString(resEntity).trim();
                z=responseStr;
                Log.d("INFO", "Response Save: " +  z);
                // you can add an if statement here and do other actions based on the response
            }
            if(z.equals("0")){
                z="success";
            }
            return z;
        }catch (IOException e){
            e.printStackTrace();
        }
        return z;
    }
    private String fnsend() {
        try {
            URL="https://www.mobile88.com/ePayment/WebService/VoidFunction/VoidFunction.asmx";

            String body="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
            body    += "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
                    " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" +
                    " xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"> \n";
            body +=" <soap:Body>\n";
            body +=" <VoidTransaction xmlns=\"https://www.mobile88.com\"> \n";
            body +=" <merchantcode>"+MerchantCode+"</merchantcode> \n";
            body +=" <cctransid>"+CCTransId+"</cctransid> \n";
            body +=" <amount>"+Amount+"</amount> \n";
            body +=" <currency>"+Currency+"</currency> \n";
            body +=" <signature>"+Signature.trim()+"</signature>\n";
            body +=" </VoidTransaction>\n";
            body +=" </soap:Body> \n";
            body +="</soap:Envelope> \n";
            Log.d("INFO", "postURL: " + URL);
            // HttpClient
            HttpClient httpClient = new DefaultHttpClient();
            // post header
            HttpPost httpPost = new HttpPost(URL);
            httpPost.setHeader("Host", "payment.ipay88.com.my");
            httpPost.setHeader("Content-Type", "text/xml; charset=utf-8");
           // httpPost.setHeader("Content-Length", "length");
            httpPost.setHeader("SOAPAction", "https://www.mobile88.com/VoidTransaction");

            final StringBuffer soap = new StringBuffer();
            // this is a sample data..you have create your own required data  BEGIN
            soap.append(body);
            Log.d("SOAP",soap.toString());
            HttpEntity entity = new StringEntity(soap.toString(),HTTP.UTF_8);
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                String responseStr = EntityUtils.toString(resEntity).trim();
                String searchResult = parseXMLForTag(responseStr, "VoidTransactionResponse");
                z = parseXMLForTag(searchResult, "VoidTransactionResult");
                //z=responseStr;
                // you can add an if statement here and do other actions based on the response
            }
            if(z.equals("0")){
                z="success";
            }
            Log.d("INFO", "Response: " +  z);
            return z;
        }catch (IOException e){
            e.printStackTrace();
        }
        return z;
    }

    public static String parseXMLForTag(String xml, String tag) {
        try {
            // Create XMLPullParserFactory & XMLPullParser
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            // boolean to indicate desired tag has been found
            boolean foundTag = false;
            // variable to fill contents
            StringBuilder tagContents = new StringBuilder();

            // loop over document
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(tag)) {
                            // Found tag, start appending to tagContents
                            foundTag = true;
                        } else if (foundTag) {
                            // New start tag inside desired tag
                            tagContents.append("<" + parser.getName() + ">");
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(tag)) {
                            // Finished gathering text for tag
                            return tagContents.toString();
                        } else if (foundTag) {
                            // end tag inside desired tag
                            tagContents.append("</" + parser.getName() + ">");
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (foundTag) {
                            // text inside desired tag
                            tagContents.append(parser.getText());
                        }
                        break;
                }
                // Get next event type
                eventType = parser.next();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
