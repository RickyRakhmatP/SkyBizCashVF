package skybiz.com.posoffline.ui_CashReceipt.m_Modifier;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Modifier;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Trn;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.Dialog_Qty;
import skybiz.com.posoffline.ui_CashReceipt.m_Reprint.DialogReprint;

/**
 * Created by 7 on 27/10/2017.
 */

public class ModifierAdapter extends RecyclerView.Adapter<ModifierHolder> {
    Context c;
    ArrayList<Spacecraft_Modifier> spacecrafts;
    Dialog_Qty dialogQty;

    public ModifierAdapter(Context c, ArrayList<Spacecraft_Modifier> spacecrafts, Dialog_Qty dialogQty) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogQty=dialogQty;
    }

    @Override
    public ModifierHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_modifier,parent,false);
        return new ModifierHolder(v);
    }
    @Override
    public void onBindViewHolder(final ModifierHolder holder, int position) {
        final Spacecraft_Modifier spacecraft=spacecrafts.get(position);
        holder.vModifier.setText(spacecraft.getModifier());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String vModifier=spacecraft.getModifier();
                dialogQty.setModifer(vModifier);
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
