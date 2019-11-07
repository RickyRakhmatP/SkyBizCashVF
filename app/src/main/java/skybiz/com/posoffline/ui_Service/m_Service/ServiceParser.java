package skybiz.com.posoffline.ui_Service.m_Service;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Customer;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Service;


/**
 * Created by 7 on 27/10/2017.
 */

public class ServiceParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData;
    RecyclerView rv;

    ArrayList<Spacecraft_Service> spacecrafts=new ArrayList<>();
    DialogService dialogService;

    public ServiceParser(Context c, String jsonData, RecyclerView rv, DialogService dialogService) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.dialogService=dialogService;
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
            ServiceAdapter adapter=new ServiceAdapter(c,spacecrafts,dialogService);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_Service spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                String Particular=jo.getString("Particular");
                spacecraft=new Spacecraft_Service();
                spacecraft.setParticular(Particular);
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
