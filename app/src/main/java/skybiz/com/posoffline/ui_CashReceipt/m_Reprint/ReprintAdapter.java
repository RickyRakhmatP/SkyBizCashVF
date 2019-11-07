package skybiz.com.posoffline.ui_CashReceipt.m_Reprint;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Customer;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Trn;

/**
 * Created by 7 on 27/10/2017.
 */

public class ReprintAdapter extends RecyclerView.Adapter<ReprintHolder> {
    Context c;
    ArrayList<Spacecraft_Trn> spacecrafts;
    DialogReprint dialogReprint;
    int row_index;

    public ReprintAdapter(Context c, ArrayList<Spacecraft_Trn> spacecrafts, DialogReprint dialogReprint) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogReprint=dialogReprint;
    }

    @Override
    public ReprintHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_reprint,parent,false);
        return new ReprintHolder(v);
    }
    @Override
    public void onBindViewHolder(final ReprintHolder holder, int position) {
        final Spacecraft_Trn spacecraft=spacecrafts.get(position);
        holder.vDoc1No.setText(spacecraft.getDoc1No());
        holder.vDateTime.setText(spacecraft.getD_ateTime());
        holder.vTotalAmt.setText(spacecraft.getHCNetAmt());
        holder.vQty.setText(spacecraft.getQty());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                row_index=spacecraft.getRunNo();
                notifyDataSetChanged();
                final String Doc1No=spacecraft.getDoc1No();
                dialogReprint.setReprint(Doc1No);
            }
        });
        if(row_index==position) {
            holder.lnReprint.setBackgroundResource(R.drawable.border_active);
        }else{
            holder.lnReprint.setBackgroundResource(R.drawable.border_sunmi);
        }
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
