package skybiz.com.posoffline.ui_QuickCash.m_ItemQuick;

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
                spacecraft=new Spacecraft();
                spacecraft.setItemCode(jo.getString("ItemCode"));
                spacecraft.setDescription(jo.getString("Description"));
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
