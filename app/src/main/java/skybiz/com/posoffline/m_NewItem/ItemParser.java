package skybiz.com.posoffline.m_NewItem;

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
    String jsonData,DocType;
    RecyclerView rv;

    ArrayList<Spacecraft> spacecrafts=new ArrayList<>();
    ItemAdapter adapter;

    public ItemParser(Context c,String DocType, String jsonData, RecyclerView rv) {
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
            ItemAdapter adapter=new ItemAdapter(c,DocType,spacecrafts);
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
                //int id=jo.getInt("Id");
                String CurCode=jo.getString("CurCode");
                String Qty=jo.getString("Qty");
                String ItemCode=jo.getString("ItemCode");
                String ItemGroup=jo.getString("ItemGroup");
                String Description=jo.getString("Description");
                String UnitPrice=jo.getString("UnitPrice");
                String UOM=jo.getString("UOM");
                String RetailTaxCode=jo.getString("RetailTaxCode");
                String PurchaseTaxCode=jo.getString("PurchaseTaxCode");
                String SalesTaxCode=jo.getString("SalesTaxCode");
                spacecraft=new Spacecraft();
                spacecraft.setItemCode(ItemCode);
                spacecraft.setItemGroup(ItemGroup);
                spacecraft.setDescription(Description);
                spacecraft.setUnitPrice(UnitPrice);
                spacecraft.setUOM(UOM);
                spacecraft.setBtnQty(Qty);
                spacecraft.setCurCode(CurCode);
                spacecraft.setRetailTaxCode(RetailTaxCode);
                spacecraft.setPurchaseTaxCode(PurchaseTaxCode);
                spacecraft.setSalesTaxCode(SalesTaxCode);
                spacecraft.setHCDiscount(jo.getString("HCDiscount"));
                spacecraft.setDisRate1(jo.getString("DisRate1"));
                spacecrafts.add(spacecraft);
            }
            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
