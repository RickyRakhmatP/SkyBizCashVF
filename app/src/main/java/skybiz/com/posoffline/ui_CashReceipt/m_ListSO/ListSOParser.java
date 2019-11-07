package skybiz.com.posoffline.ui_CashReceipt.m_ListSO;

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

/**
 * Created by 7 on 27/10/2017.
 */

public class ListSOParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData;
    RecyclerView rv;
    Fragment_ListSO fragment_listSO;
    ArrayList<Spacecraft_SO> spacecrafts=new ArrayList<>();
    ItemAdapter adapter;


    public ListSOParser(Context c, String jsonData, RecyclerView rv,Fragment_ListSO fragment_listSO) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.fragment_listSO=fragment_listSO;
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
            ListSOAdapter adapter=new ListSOAdapter(c,spacecrafts,fragment_listSO);
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
                spacecraft.setAttention(jo.getString("Attention"));
                spacecrafts.add(spacecraft);
            }
            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
