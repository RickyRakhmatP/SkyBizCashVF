package skybiz.com.posoffline.m_NewReprint;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Trn;


/**
 * Created by 7 on 27/10/2017.
 */

public class ReprintParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String DocType,jsonData;
    RecyclerView rv;

    ArrayList<Spacecraft_Trn> spacecrafts=new ArrayList<>();
    DialogReprint dialogReprint;

    public ReprintParser(Context c, String jsonData, RecyclerView rv, DialogReprint dialogReprint) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.dialogReprint=dialogReprint;
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
            ReprintAdapter adapter=new ReprintAdapter(c,spacecrafts,dialogReprint);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_Trn spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                String Doc1No=jo.getString("Doc1No");
                String D_ateTime=jo.getString("D_ateTime");
                String HCNetAmt=jo.getString("HCNetAmt");
                String Qty=jo.getString("Qty");
                spacecraft=new Spacecraft_Trn();
                spacecraft.setDoc1No(Doc1No);
                spacecraft.setD_ateTime(D_ateTime);
                spacecraft.setHCNetAmt(HCNetAmt);
                spacecraft.setQty(Qty);
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
