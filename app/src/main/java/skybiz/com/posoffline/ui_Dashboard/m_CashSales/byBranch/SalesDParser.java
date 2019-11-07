package skybiz.com.posoffline.ui_Dashboard.m_CashSales.byBranch;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import skybiz.com.posoffline.m_NewObject.Spacecraft_SalesD;
import skybiz.com.posoffline.ui_CashReceipt.m_Customer.DialogCustomer;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Customer;


/**
 * Created by 7 on 27/10/2017.
 */

public class SalesDParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData,uFrom;
    RecyclerView rv;
    ArrayList<Spacecraft_SalesD> spacecrafts=new ArrayList<>();

    public SalesDParser(Context c, String jsonData, RecyclerView rv,String uFrom) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.uFrom=uFrom;
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
            SalesDAdapter adapter=new SalesDAdapter(c,spacecrafts,uFrom);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_SalesD spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);;
                spacecraft=new Spacecraft_SalesD();
                spacecraft.setBranchCode(jo.getString("BranchCode"));
                spacecraft.setTotal(jo.getString("Total"));
                spacecraft.setCurCode(jo.getString("CurCode"));
                spacecraft.setD_ate(jo.getString("D_ate"));
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
