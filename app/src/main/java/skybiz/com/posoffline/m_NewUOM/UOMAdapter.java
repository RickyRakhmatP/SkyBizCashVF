package skybiz.com.posoffline.m_NewUOM;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewSummary.Dialog_Edit;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_UOM;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.Dialog_Qty;

/**
 * Created by 7 on 27/10/2017.
 */

public class UOMAdapter extends RecyclerView.Adapter<UOMHolder> {
    Context c;
    ArrayList<Spacecraft_UOM> spacecrafts;
    Dialog_Edit dialog_edit;

    public UOMAdapter(Context c, ArrayList<Spacecraft_UOM> spacecrafts, Dialog_Edit dialog_edit) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialog_edit=dialog_edit;
    }

    @Override
    public UOMHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_uom,parent,false);
        return new UOMHolder(v);
    }
    @Override
    public void onBindViewHolder(final UOMHolder holder, int position) {
        final Spacecraft_UOM spacecraft=spacecrafts.get(position);
        holder.vUOM.setText(spacecraft.getUOM());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String vUOM=spacecraft.getUOM();
                final String vUOMFactor=spacecraft.getUOMFactor();
                final String vUOMPrice=spacecraft.getUOMPrice();
                dialog_edit.setUOM(vUOM,vUOMPrice,vUOMFactor);
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
