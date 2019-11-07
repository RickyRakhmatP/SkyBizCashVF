package skybiz.com.posoffline.ui_CashReceipt.m_GuestCheck;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_SO;
import skybiz.com.posoffline.ui_CashReceipt.m_Item.ItemAdapter;
import skybiz.com.posoffline.ui_CashReceipt.m_ListSO.Fragment_ListSO;
import skybiz.com.posoffline.ui_CashReceipt.m_ListSO.ListSOAdapter;

/**
 * Created by 7 on 27/10/2017.
 */

public class GuestParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData;
    RecyclerView rv;
    DialogGuest dialogGuest;
    ArrayList<Spacecraft_SO> spacecrafts=new ArrayList<>();
    ItemAdapter adapter;


    public GuestParser(Context c, String jsonData, RecyclerView rv, DialogGuest dialogGuest) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.dialogGuest=dialogGuest;
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
            GuestAdapter adapter=new GuestAdapter(c,spacecrafts,dialogGuest);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_SO spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                String Doc1No=jo.getString("Doc1No");
                String Doc2No=jo.getString("Doc2No");
                spacecraft=new Spacecraft_SO();
                spacecraft.setDoc1No(Doc1No);
                spacecraft.setDoc2No(Doc2No);
                spacecrafts.add(spacecraft);
            }
            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
