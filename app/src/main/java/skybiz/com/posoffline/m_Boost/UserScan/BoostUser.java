package skybiz.com.posoffline.m_Boost.UserScan;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Boost.SimpleSSLSocketFactory;

public class BoostUser extends AppCompatActivity {

    String Doc1No,Amount,CusName,MerchantCode,MerchantKey,PaymentId,PayType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boost_user);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                Doc1No= null;
            } else {
                Doc1No= extras.getString("DOC1NO_KEY");
                Amount= extras.getString("AMOUNT_KEY");
                PaymentId= extras.getString("PAYID_KEY");
                PayType= extras.getString("PAYTYPE_KEY");
                MerchantCode= extras.getString("MERCHANTCODE_KEY");
                MerchantKey= extras.getString("MERCHANTKEY_KEY");
            }
        } else {
            Doc1No= (String) savedInstanceState.getSerializable("DOC1NO_KEY");
            Amount= (String) savedInstanceState.getSerializable("AMOUNT_KEY");
            PaymentId= (String) savedInstanceState.getSerializable("PAYID_KEY");
            PayType= (String) savedInstanceState.getSerializable("PAYTYPE_KEY");
            MerchantCode= (String) savedInstanceState.getSerializable("MERCHANTCODE_KEY");
            MerchantKey= (String) savedInstanceState.getSerializable("MERCHANTKEY_KEY");
        }
        initData();
    }
    private void initData(){
        GenerateQR generateQR=new GenerateQR(this);
        generateQR.execute();
    }

    private class GenerateQR extends AsyncTask<Void,Void,String>{
        Context c;
        String z,URL;

        public GenerateQR(Context c) {
            this.c = c;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return this.fngenerate();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
        private String fngenerate(){
            try {

                HttpParams params = new BasicHttpParams();
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

                SSLSocketFactory sslFactory = null;
                sslFactory = new SimpleSSLSocketFactory(null);
                sslFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                // Register the HTTP and HTTPS Protocols. For HTTPS, register our custom SSL Factory object.
                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                registry.register(new Scheme("https", sslFactory, 443));

                ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
                MerchantCode="AB-123-4-87-HG-097";
                MerchantKey ="GF-GT56-3ERSD-T7-Q12-P75";
                URL         = "https://stage-wallet.boostorium.com";
                HttpClient httpClient = new DefaultHttpClient();
                // post header
                HttpPost httpPost = new HttpPost(URL);
                httpPost.addHeader("Host", "payment.ipay88.com.my");
                httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
                // add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("apiKey", MerchantCode));
                nameValuePairs.add(new BasicNameValuePair("apiSecret", MerchantKey));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // execute HTTP post request
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity resEntity = response.getEntity();

                if (resEntity != null) {
                    String responseStr = EntityUtils.toString(resEntity).trim();
                    z = responseStr;
                    Log.d("INFO", "Response Save: " + z);
                    // you can add an if statement here and do other actions based on the response
                }
                if (z.equals("0")) {
                    z = "success";
                }
                return z;
            }catch (IOException e){
                e.printStackTrace();
            }  catch (NoSuchAlgorithmException e) {
                 e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            }
            return z;
        }
    }
}
