package skybiz.com.posoffline.m_NewSupplier;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Customer;


/**
 * Created by 7 on 27/10/2017.
 */

public class SupplierParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData;
    RecyclerView rv;

    ArrayList<Spacecraft_Customer> spacecrafts=new ArrayList<>();
    DialogSupplier dialogSupplier;

    public SupplierParser(Context c, String jsonData, RecyclerView rv, DialogSupplier dialogSupplier) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.dialogSupplier=dialogSupplier;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        return this.parseData();
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if(result==0){
            Toast.makeText(c,"Unable to parse", Toast.LENGTH_SHORT).show();
        }else{
            SupplierAdapter adapter=new SupplierAdapter(c,spacecrafts,dialogSupplier);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_Customer spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                String CusCode=jo.getString("CusCode");
                String CusName=jo.getString("CusName");
                String TermCode=jo.getString("TermCode");
                String D_ay=jo.getString("D_ay");
                String SalesPersonCode=jo.getString("SalesPersonCode");
                spacecraft=new Spacecraft_Customer();
                spacecraft.setCusCode(CusCode);
                spacecraft.setCusName(CusName);
                spacecraft.setTermCode(TermCode);
                spacecraft.setD_ay(D_ay);
                spacecraft.setSalesPersonCode(SalesPersonCode);
                spacecraft.setTel(jo.getString("Tel"));
                spacecraft.setFax(jo.getString("Fax"));
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
