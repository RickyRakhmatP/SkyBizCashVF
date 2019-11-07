package skybiz.com.posoffline.ui_CashReceipt.m_Order;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Order;


/**
 * Created by 7 on 27/10/2017.
 */

public class OrderParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData;
    RecyclerView rv;
    FragmentManager fm;
    ArrayList<Spacecraft_Order> spacecrafts=new ArrayList<>();

    public OrderParser(Context c, String jsonData, RecyclerView rv, FragmentManager fm) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.fm=fm;
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
            OrderAdapter adapter=new OrderAdapter(c,spacecrafts,fm);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_Order spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                int RunNo=jo.getInt("RunNo");
                String ItemCode=jo.getString("ItemCode");
                String ItemGroup=jo.getString("ItemGroup");
                String Description=jo.getString("Description");
                String UnitPrice=jo.getString("HCUnitCost");
                String HCDiscount=jo.getString("HCDiscount");
                String DisRate1=jo.getString("DisRate1");
                String Description2=jo.getString("Description2");
                String Qty=jo.getString("QTY");
                String UOM=jo.getString("UOM");
                String FactorQty=jo.getString("FactorQty");
                String BlankLine=jo.getString("BlankLine");
                String DetailTaxCode=jo.getString("DetailTaxCode");
                spacecraft=new Spacecraft_Order();
                spacecraft.setId(i);
                spacecraft.setRunNo(RunNo);
                spacecraft.setItemCode(ItemCode);
                spacecraft.setItemGroup(ItemGroup);
                spacecraft.setDescription(Description);
                spacecraft.setHCUnitCost(UnitPrice);
                spacecraft.setHCDiscount(HCDiscount);
                spacecraft.setDisRate1(DisRate1);
                spacecraft.setDescription2(Description2);
                spacecraft.setBtnQty(Qty);
                spacecraft.setUOM(UOM);
                spacecraft.setFactorQty(FactorQty);
                spacecraft.setBlankLine(BlankLine);
                spacecraft.setDetailTaxCode(DetailTaxCode);
                spacecraft.setAnalysisCode2(jo.getString("AnalysisCode2"));
                spacecraft.setUnitCost(jo.getString("UnitCost"));
                spacecraft.setServiceChargeYN(jo.getString("ServiceChargeYN"));
                spacecrafts.add(spacecraft);
            }
            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
