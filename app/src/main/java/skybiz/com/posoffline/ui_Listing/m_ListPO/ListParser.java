package skybiz.com.posoffline.ui_Listing.m_ListPO;

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
public class ListParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData,DocType;
    RecyclerView rv;
    ArrayList<Spacecraft_Trn> spacecrafts=new ArrayList<>();

    public ListParser(Context c, String DocType, String jsonData, RecyclerView rv) {
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
            ListAdapter adapter=new ListAdapter(c,DocType,spacecrafts);
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
                String D_ate=jo.getString("D_ate");
                String HCNetAmt=jo.getString("HCNetAmt");
                String TotalQty=jo.getString("TotalQty");
                String CusName=jo.getString("CusName");
                String SyncYN=jo.getString("SyncYN");
                spacecraft=new Spacecraft_Trn();
                spacecraft.setDoc1No(Doc1No);
                spacecraft.setD_ate(D_ate);
                spacecraft.setHCNetAmt(HCNetAmt);
                spacecraft.setQty(TotalQty);
                spacecraft.setCusName(CusName);
                spacecraft.setSynYN(SyncYN);
                spacecrafts.add(spacecraft);
            }
            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
