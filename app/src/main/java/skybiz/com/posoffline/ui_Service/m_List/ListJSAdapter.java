package skybiz.com.posoffline.ui_Service.m_List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewObject.Spacecraft_JS;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Customer;
import skybiz.com.posoffline.ui_Service.MService;

/**
 * Created by 7 on 27/10/2017.
 */

public class ListJSAdapter extends RecyclerView.Adapter<ListJSHolder> {
    Context c;
    ArrayList<Spacecraft_JS> spacecrafts;
    DialogListJS dialogListJS;

    public ListJSAdapter(Context c, ArrayList<Spacecraft_JS> spacecrafts, DialogListJS dialogListJS) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogListJS=dialogListJS;
    }

    @Override
    public ListJSHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_js,parent,false);
        return new ListJSHolder(v);
    }
    @Override
    public void onBindViewHolder(final ListJSHolder holder, int position) {
        final Spacecraft_JS spacecraft=spacecrafts.get(position);
        holder.vDoc1No.setText(spacecraft.getDoc1No());
        holder.vCusName.setText(spacecraft.getCusname());
        holder.vCaseType.setText(spacecraft.getCasetype());
        holder.vD_ate.setText(spacecraft.getD_ate()+"\n"+spacecraft.getT_ime());
        holder.vPriority.setText(spacecraft.getPriority());
        holder.vContactTel.setText(spacecraft.getContactTel());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                try {
                    JSONArray results=new JSONArray();
                    JSONObject row = new JSONObject();
                    row.put("Doc1No", spacecraft.getDoc1No());
                    row.put("cuscode", spacecraft.getCuscode());
                    row.put("cusname", spacecraft.getCusname());
                    row.put("mobileno", spacecraft.getMobileno());
                    row.put("receiptno",spacecraft.getReceiptno());
                    row.put("receiptdate", spacecraft.getReceiptdate());
                    row.put("repairtype", spacecraft.getRepairtype());
                    row.put("casetype", spacecraft.getCasetype());
                    row.put("entryid", spacecraft.getEntryid());
                    row.put("d_ate",spacecraft.getD_ate());
                    row.put("outputid", spacecraft.getOutputid());
                    row.put("outputdate", spacecraft.getOutputdate());
                    row.put("receivemode", spacecraft.getReceivemode());
                    row.put("termcode", spacecraft.getTermcode());
                    row.put("productmodel", spacecraft.getProductmodel());
                    row.put("partno", spacecraft.getPartno());
                    row.put("serialno",spacecraft.getSerialno());
                    row.put("supplierserialno", spacecraft.getSupplierserialno());
                    row.put("warrantystatus", spacecraft.getWarrantystatus());
                    row.put("warrantydesc", spacecraft.getWarrantydesc());
                    row.put("warrantyexpirydate", spacecraft.getWarrantyexpirydate());
                    row.put("accessories", spacecraft.getAccessories());
                    row.put("problemdesc",spacecraft.getProblemdesc());
                    row.put("collectedby", spacecraft.getCollectedby());
                    row.put("collecteddate", spacecraft.getCollecteddate());
                    row.put("sendtovendorYN", spacecraft.getSendtovendorYN());
                    row.put("sendtovendordate", spacecraft.getSendtovendordate());
                    row.put("vendorwarrantystatus", spacecraft.getVendorwarrantystatus());
                    row.put("vendorcode", spacecraft.getVendorcode());
                    row.put("vendorname", spacecraft.getVendorname());
                    row.put("vendortelno", spacecraft.getVendortelno());
                    row.put("backfromvendorYN",spacecraft.getBackfromvendorYN());
                    row.put("backfromvendordate", spacecraft.getBackfromvendordate());
                    row.put("returnbackenduserYN", spacecraft.getReturnbackenduserYN());
                    row.put("returnbackenduserdate", spacecraft.getReturnbackenduserdate());
                    row.put("returnbackenduserby", spacecraft.getReturnbackenduserby());
                    row.put("servicenoteremark", spacecraft.getServicenoteremark());
                    row.put("L_ink", spacecraft.getL_ink());
                    row.put("Address", spacecraft.getAddress());
                    row.put("ContactTel", spacecraft.getContactTel());
                    row.put("Email", spacecraft.getEmail());
                    row.put("servicestatus", spacecraft.getServicestatus());
                    row.put("Technician",spacecraft.getTechnician());
                    row.put("Contact", spacecraft.getContact());
                    row.put("Priority", spacecraft.getPriority());
                    row.put("T_ime", spacecraft.getT_ime());
                    row.put("InstallationDate", spacecraft.getInstallationDate());
                    row.put("TechnicalReport", spacecraft.getTechnicalReport());
                    row.put("DateTimeAttended", spacecraft.getDateTimeAttended());
                    row.put("PhotoFile", spacecraft.getPhotoFile());
                    row.put("PhotoFile2", spacecraft.getPhotoFile2());
                    row.put("PhotoFileName", spacecraft.getPhotoFileName());
                    row.put("ActionTimeStart", spacecraft.getActionTimeStart());
                    row.put("ActionTimeEnd", spacecraft.getActionTimeEnd());
                    results.put(row);
                    ((MService) c).setOnClick(results.toString());
                    dialogListJS.dismiss();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
