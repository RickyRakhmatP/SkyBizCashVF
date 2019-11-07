package skybiz.com.posoffline.ui_CashReceipt.m_VoidBill;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Trn;

/**
 * Created by 7 on 27/10/2017.
 */

public class VoidAdapter extends RecyclerView.Adapter<VoidHolder> {
    Context c;
    ArrayList<Spacecraft_Trn> spacecrafts;
    DialogVoid dialogVoid;
    int row_index;

    public VoidAdapter(Context c, ArrayList<Spacecraft_Trn> spacecrafts, DialogVoid dialogVoid) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogVoid = dialogVoid;
    }

    @Override
    public VoidHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_void,parent,false);
        return new VoidHolder(v);
    }
    @Override
    public void onBindViewHolder(final VoidHolder holder, int position) {
        final Spacecraft_Trn spacecraft=spacecrafts.get(position);
        holder.vDoc1No.setText(spacecraft.getDoc1No());
        holder.vDateTime.setText(spacecraft.getD_ateTime());
        holder.vTotalAmt.setText(spacecraft.getHCNetAmt());
        //holder.vStatus.setText(spacecraft.getStatus());
        final String vStatus=spacecraft.getStatus();
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                row_index=spacecraft.getRunNo();
                notifyDataSetChanged();
                final String Doc1No=spacecraft.getDoc1No();
                final String Status=spacecraft.getStatus();
                final String PaymentCode=spacecraft.getPaymentCode();
                final String Amount=spacecraft.getHCNetAmt();
                if(!Status.equals("Void")){
                    dialogVoid.showDialogYN(Doc1No,PaymentCode,Amount);
                }
                //final String Doc1No=spacecraft.getDoc1No();
                //dialogVoid.setReprint(Doc1No);
            }
        });
        if(vStatus.equals("Void")){
            holder.lnTrn.setBackgroundResource(R.color.colorRedLighten3);
            holder.vDoc1No.setTextColor(Color.WHITE);
            holder.vDateTime.setTextColor(Color.WHITE);
            holder.vTotalAmt.setTextColor(Color.WHITE);
            //holder.vStatus.setTextColor(Color.WHITE);
        }
        if(row_index==position) {
            holder.lnTrn.setBackgroundResource(R.drawable.border_active);
        }else{
            holder.lnTrn.setBackgroundResource(R.drawable.border_sunmi);
        }

    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
