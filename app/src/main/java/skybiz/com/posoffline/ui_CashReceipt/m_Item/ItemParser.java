package skybiz.com.posoffline.ui_CashReceipt.m_Item;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;

/**
 * Created by 7 on 27/10/2017.
 */

public class ItemParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData;
    RecyclerView rv;

    ArrayList<Spacecraft> spacecrafts=new ArrayList<>();
    ItemAdapter adapter;

    public ItemParser(Context c, String jsonData, RecyclerView rv) {
        this.c = c;
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
            ItemAdapter adapter=new ItemAdapter(c,spacecrafts);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                String CurCode=jo.getString("CurCode");
                String Qty=jo.getString("Qty");
                String ItemCode=jo.getString("ItemCode");
                String ItemGroup=jo.getString("ItemGroup");
                String Description=jo.getString("Description");
                String UnitPrice=jo.getString("UnitPrice");
                String Printer=jo.getString("Printer");
                spacecraft=new Spacecraft();
                spacecraft.setItemCode(ItemCode);
                spacecraft.setItemGroup(ItemGroup);
                spacecraft.setDescription(Description);
                spacecraft.setUnitPrice(UnitPrice);
                spacecraft.setPrinter(Printer);
                spacecraft.setBtnQty(Qty);
                spacecraft.setCurCode(CurCode);
                spacecraft.setPrinter(jo.getString("Printer"));
                spacecraft.setModifier(jo.getString("Modifier1"));
                spacecraft.setRetailTaxCode(jo.getString("RetailTaxCode"));
                spacecraft.setDefaultUOM(jo.getString("DefaultUOM"));
                spacecraft.setUOM(jo.getString("UOM"));
                spacecraft.setUOM1(jo.getString("UOM1"));
                spacecraft.setUOM2(jo.getString("UOM2"));
                spacecraft.setUOM3(jo.getString("UOM3"));
                spacecraft.setUOM4(jo.getString("UOM4"));
                spacecraft.setUOMFactor1(jo.getString("UOMFactor1"));
                spacecraft.setUOMFactor2(jo.getString("UOMFactor2"));
                spacecraft.setUOMFactor3(jo.getString("UOMFactor3"));
                spacecraft.setUOMFactor4(jo.getString("UOMFactor4"));
                spacecraft.setUOMPrice1(jo.getString("UOMPrice1"));
                spacecraft.setUOMPrice2(jo.getString("UOMPrice2"));
                spacecraft.setUOMPrice3(jo.getString("UOMPrice3"));
                spacecraft.setUOMPrice4(jo.getString("UOMPrice4"));
                spacecraft.setAlternateItem(jo.getString("AlternateItem"));
                spacecraft.setHCDiscount(jo.getString("HCDiscount"));
                spacecraft.setDisRate1(jo.getString("DisRate1"));
                spacecraft.setPoint(jo.getString("Point"));
                spacecraft.setPhotoFile(jo.getString("PhotoFile"));
                spacecraft.setMSP(jo.getString("MSP"));
                spacecraft.setUnitCost(jo.getString("UnitCost"));
                spacecrafts.add(spacecraft);
            }
            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
