package skybiz.com.posoffline.ui_Service.m_List;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Blob;
import java.util.ArrayList;

import skybiz.com.posoffline.m_NewObject.Spacecraft_JS;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Customer;


/**
 * Created by 7 on 27/10/2017.
 */

public class ListJSParser extends AsyncTask<Void, Integer, Integer> {

    Context c;
    String jsonData;
    RecyclerView rv;

    ArrayList<Spacecraft_JS> spacecrafts=new ArrayList<>();
    DialogListJS dialogListJS;

    public ListJSParser(Context c, String jsonData, RecyclerView rv, DialogListJS dialogListJS) {
        this.c = c;
        this.jsonData = jsonData;
        this.rv = rv;
        this.dialogListJS=dialogListJS;
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
            ListJSAdapter adapter=new ListJSAdapter(c,spacecrafts,dialogListJS);
            rv.setAdapter(adapter);
        }
    }

    private int parseData(){
        try {
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            spacecrafts.clear();
            Spacecraft_JS spacecraft;
            for (int i=0;i<ja.length();i++){
                jo=ja.getJSONObject(i);
                spacecraft=new Spacecraft_JS();
                spacecraft.setDoc1No(jo.getString("Doc1No"));
                spacecraft.setCuscode(jo.getString("cuscode"));
                spacecraft.setCusname(jo.getString("cusname"));
                spacecraft.setMobileno(jo.getString("mobileno"));
                spacecraft.setReceiptno(jo.getString("receiptno"));
                spacecraft.setReceiptdate(jo.getString("receiptdate"));
                spacecraft.setRepairtype(jo.getString("repairtype"));
                spacecraft.setCasetype(jo.getString("casetype"));
                spacecraft.setEntryid(jo.getString("entryid"));
                spacecraft.setD_ate(jo.getString("d_ate"));
                spacecraft.setOutputid(jo.getString("outputid"));
                spacecraft.setOutputdate(jo.getString("outputdate"));
                spacecraft.setReceivemode(jo.getString("receivemode"));
                spacecraft.setTermcode(jo.getString("termcode"));
                spacecraft.setProductmodel(jo.getString("productmodel"));
                spacecraft.setPartno(jo.getString("partno"));
                spacecraft.setSerialno(jo.getString("serialno"));
                spacecraft.setSupplierserialno(jo.getString("supplierserialno"));
                spacecraft.setWarrantystatus(jo.getString("warrantystatus"));
                spacecraft.setWarrantydesc(jo.getString("warrantydesc"));
                spacecraft.setWarrantyexpirydate(jo.getString("warrantyexpirydate"));
                spacecraft.setAccessories(jo.getString("accessories"));
                spacecraft.setProblemdesc(jo.getString("problemdesc"));
                spacecraft.setCollectedby(jo.getString("collectedby"));
                spacecraft.setCollecteddate(jo.getString("collecteddate"));
                spacecraft.setSendtovendorYN(jo.getString("sendtovendorYN"));
                spacecraft.setSendtovendordate(jo.getString("sendtovendordate"));
                spacecraft.setVendorwarrantystatus(jo.getString("vendorwarrantystatus"));
                spacecraft.setVendorcode(jo.getString("vendorcode"));
                spacecraft.setVendorname(jo.getString("vendorname"));
                spacecraft.setVendortelno(jo.getString("vendortelno"));
                spacecraft.setBackfromvendorYN(jo.getString("backfromvendorYN"));
                spacecraft.setBackfromvendordate(jo.getString("backfromvendordate"));
                spacecraft.setReturnbackenduserYN(jo.getString("returnbackenduserYN"));
                spacecraft.setReturnbackenduserdate(jo.getString("returnbackenduserdate"));
                spacecraft.setReturnbackenduserby(jo.getString("returnbackenduserby"));
                spacecraft.setServicenoteremark(jo.getString("servicenoteremark"));
                spacecraft.setL_ink(jo.getString("L_ink"));
                spacecraft.setAddress(jo.getString("Address"));
                spacecraft.setContactTel(jo.getString("ContactTel"));
                spacecraft.setEmail(jo.getString("Email"));
                spacecraft.setServicestatus(jo.getString("servicestatus"));
                spacecraft.setTechnician(jo.getString("Technician"));
                spacecraft.setContact(jo.getString("Contact"));
                spacecraft.setT_ime(jo.getString("T_ime"));
                spacecraft.setInstallationDate(jo.getString("InstallationDate"));
                spacecraft.setTechnicalReport(jo.getString("TechnicalReport"));
                spacecraft.setDateTimeAttended(jo.getString("DateTimeAttended"));
                spacecraft.setPhotoFile(jo.getString("PhotoFile"));
                spacecraft.setPriority(jo.getString("Priority"));
                spacecraft.setPhotoFile2(jo.getString("PhotoFile2"));
                spacecraft.setPhotoFileName(jo.getString("PhotoFileName"));
                spacecraft.setPhotoFile2(jo.getString("PhotoFile2"));
                spacecraft.setActionTimeStart(jo.getString("ActionTimeStart"));
                spacecraft.setActionTimeEnd(jo.getString("ActionTimeEnd"));
                spacecrafts.add(spacecraft);
            }
            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
