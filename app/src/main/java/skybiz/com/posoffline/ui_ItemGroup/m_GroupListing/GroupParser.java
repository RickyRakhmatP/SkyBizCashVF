package skybiz.com.posoffline.ui_ItemGroup.m_GroupListing;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import skybiz.com.posoffline.m_NewObject.Spacecraft_Group;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;


/**
 * Created by 7 on 27/10/2017.
 */

public class GroupParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData,DocType;
    RecyclerView rv;

    ArrayList<Spacecraft_Group> spacecrafts=new ArrayList<>();

    public GroupParser(Context c, String jsonData, RecyclerView rv) {
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
            GroupAdapter adapter=new GroupAdapter(c,spacecrafts);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_Group spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                spacecraft=new Spacecraft_Group();
                spacecraft.setItemGroup(jo.getString("ItemGroup"));
                spacecraft.setDescription(jo.getString("Description"));
                spacecraft.setModifier1(jo.getString("Modifier1"));
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
