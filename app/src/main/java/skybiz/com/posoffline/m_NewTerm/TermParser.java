package skybiz.com.posoffline.m_NewTerm;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Service;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Term;


/**
 * Created by 7 on 27/10/2017.
 */

public class TermParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData;
    RecyclerView rv;

    ArrayList<Spacecraft_Term> spacecrafts=new ArrayList<>();
    DialogTerm dialogTerm;

    public TermParser(Context c, String jsonData, RecyclerView rv, DialogTerm dialogTerm) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.dialogTerm=dialogTerm;
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
            TermAdapter adapter=new TermAdapter(c,spacecrafts,dialogTerm);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_Term spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                spacecraft=new Spacecraft_Term();
                spacecraft.setTermCode(jo.getString("TermCode"));
                spacecraft.setTermDesc(jo.getString("TermDesc"));
                spacecraft.setD_ay(jo.getString("D_ay"));
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
