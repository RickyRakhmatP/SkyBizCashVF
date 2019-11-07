package skybiz.com.posoffline.ui_Listing.m_ItemSales;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import skybiz.com.posoffline.m_NewObject.Spacecraft_ItemSales;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Trn;

/**
 * Created by 7 on 27/10/2017.
 */
public class ItemSalesParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData,DocType;
    RecyclerView rv;
    ArrayList<Spacecraft_ItemSales> spacecrafts=new ArrayList<>();

    public ItemSalesParser(Context c, String DocType, String jsonData, RecyclerView rv) {
        this.c = c;
        this.DocType = DocType;
        this.jsonData = jsonData;
        this.rv = rv;
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
            ItemSalesAdapter adapter=new ItemSalesAdapter(c,DocType,spacecrafts);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            Double dTotalAmount=0.00;
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_ItemSales spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                spacecraft=new Spacecraft_ItemSales();
                spacecraft.setItemCode(jo.getString("ItemCode"));
                spacecraft.setQty(jo.getString("Qty"));
                spacecraft.setAmount(jo.getString("Amount"));
                Double dAmount=Double.parseDouble(jo.getString("Amount").replaceAll(",",""));
                dTotalAmount+=dAmount;
                spacecrafts.add(spacecraft);
            }
            String TotalAmount=String.format(Locale.US, "%,.2f", dTotalAmount);
            spacecraft=new Spacecraft_ItemSales();
            spacecraft.setItemCode("Grand Total");
            spacecraft.setQty(":");
            spacecraft.setAmount(TotalAmount);
            spacecrafts.add(spacecraft);
            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
