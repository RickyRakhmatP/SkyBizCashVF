package skybiz.com.posoffline.m_NewSalesPerson;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import skybiz.com.posoffline.m_NewObject.Spacecraft_SalesPerson;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Customer;


/**
 * Created by 7 on 27/10/2017.
 */

public class SalesPersonParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData;
    RecyclerView rv;

    ArrayList<Spacecraft_SalesPerson> spacecrafts=new ArrayList<>();
    DialogSalesPerson dialogSalesPerson;

    public SalesPersonParser(Context c, String jsonData, RecyclerView rv, DialogSalesPerson dialogSalesPerson) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.dialogSalesPerson = dialogSalesPerson;
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
            SalesPersonAdapter adapter=new SalesPersonAdapter(c,spacecrafts, dialogSalesPerson);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_SalesPerson spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                spacecraft=new Spacecraft_SalesPerson();
                spacecraft.setSalesPersonCode(jo.getString("SalesPersonCode"));
                spacecraft.setN_ame(jo.getString("N_ame"));
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
