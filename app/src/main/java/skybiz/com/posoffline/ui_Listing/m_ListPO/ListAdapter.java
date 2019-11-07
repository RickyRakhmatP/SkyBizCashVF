package skybiz.com.posoffline.ui_Listing.m_ListPO;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import  skybiz.com.posoffline.R;


import java.util.ArrayList;

import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Trn;


/**
 * Created by 7 on 27/10/2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListHolder> {
    String IPAddress,UserName,Password,DBName,Qty,DocType;
    Context c;
    ArrayList<Spacecraft_Trn> spacecrafts;
    int clickcount=0;

    public ListAdapter(Context c, String DocType, ArrayList<Spacecraft_Trn> spacecrafts) {
        this.c = c;
        this.DocType = DocType;
        this.spacecrafts = spacecrafts;
    }
    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_trn,parent,false);
        return new ListHolder(v);

    }
    @Override
    public void onBindViewHolder(final ListHolder holder, int position) {
        final Spacecraft_Trn spacecraft=spacecrafts.get(position);
        holder.vDoc1No.setText(spacecraft.getDoc1No());
        holder.vD_ate.setText(spacecraft.getD_ate());
        holder.vTotalAmt.setText(spacecraft.getHCNetAmt());
        holder.vQty.setText(spacecraft.getQty());
        //holder.vCusName.setText(spacecraft.getCusName());
        final String SyncYN=spacecraft.getSynYN();
        if(SyncYN.equals("0")){
            holder.vSyncYN.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.sync_disabled, 0);
        }else{
            holder.vSyncYN.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sync_success, 0);
        }
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }
}
