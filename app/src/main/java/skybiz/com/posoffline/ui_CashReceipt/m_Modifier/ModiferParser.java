package skybiz.com.posoffline.ui_CashReceipt.m_Modifier;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Modifier;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Trn;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.Dialog_Qty;
import skybiz.com.posoffline.ui_CashReceipt.m_Reprint.DialogReprint;


/**
 * Created by 7 on 27/10/2017.
 */

public class ModiferParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData;
    RecyclerView rv;

    ArrayList<Spacecraft_Modifier> spacecrafts=new ArrayList<>();
    Dialog_Qty dialogQty;

    public ModiferParser(Context c, String jsonData, RecyclerView rv, Dialog_Qty dialogQty) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.dialogQty=dialogQty;
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
            ModifierAdapter adapter=new ModifierAdapter(c,spacecrafts,dialogQty);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_Modifier spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                String Modifier=jo.getString("Modifier");
                spacecraft=new Spacecraft_Modifier();
                spacecraft.setModifier(Modifier);
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
