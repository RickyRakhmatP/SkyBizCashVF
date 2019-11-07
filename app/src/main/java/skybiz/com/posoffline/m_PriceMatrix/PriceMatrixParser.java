package skybiz.com.posoffline.m_PriceMatrix;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import skybiz.com.posoffline.m_NewObject.Spacecraft_PriceMatrix;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.Dialog_Qty;


/**
 * Created by 7 on 14/11/2017.
 */

public class PriceMatrixParser extends AsyncTask<Void, Integer, Integer> {
    Context c;
    String jsonData;
    RecyclerView rv;

    ArrayList<Spacecraft_PriceMatrix> spacecrafts=new ArrayList<>();
    Dialog_Qty dialog_qty;

    public PriceMatrixParser(Context c, String jsonData, RecyclerView rv, Dialog_Qty dialog_qty) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.dialog_qty=dialog_qty;
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
            Toast.makeText(c,"Unable to parse",Toast.LENGTH_SHORT).show();
        }else{
            //bind data to recycleview
            PriceMatrixAdapter adapter=new PriceMatrixAdapter(c,spacecrafts,dialog_qty);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_PriceMatrix spacecraft;

            spacecraft=new Spacecraft_PriceMatrix();
            spacecraft.setItemCode("");
            spacecraft.setItemGroup("");
            spacecraft.setCategoryCode("");
            spacecraft.setDescription("Revert to Normal Selling Price");
            spacecraft.setPct("0.00");
            spacecraft.setCriteria("Revert Selling Price");
            spacecraft.setPeriodYN("");
            spacecraft.setB_ase("");
            spacecraft.setTimeStart("");
            spacecraft.setTimeEnd("");
            spacecraft.setStatus("");
            spacecraft.setServiceChargeYN("1");
            spacecraft.setQty("");
            spacecraft.setMemo("");
            spacecrafts.add(spacecraft);

            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                spacecraft=new Spacecraft_PriceMatrix();
                spacecraft.setItemCode(jo.getString("ItemCode"));
                spacecraft.setItemGroup(jo.getString("ItemGroup"));
                spacecraft.setCategoryCode(jo.getString("CategoryCode"));
                spacecraft.setDescription(jo.getString("Description"));
                spacecraft.setPct(jo.getString("Pct"));
                spacecraft.setCriteria(jo.getString("Criteria"));
                spacecraft.setPeriodYN(jo.getString("PeriodYN"));
                spacecraft.setB_ase(jo.getString("B_ase"));
                spacecraft.setTimeStart(jo.getString("TimeStart"));
                spacecraft.setTimeEnd(jo.getString("TimeEnd"));
                spacecraft.setStatus(jo.getString("Status"));
                spacecraft.setServiceChargeYN(jo.getString("ServiceChargeYN"));
                spacecraft.setMemo(jo.getString("Memo"));
                spacecraft.setQty(jo.getString("Qty"));
                spacecrafts.add(spacecraft);
            }
            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

