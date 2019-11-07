package skybiz.com.posoffline.ui_Listing.m_ListCusCN;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Trn;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.Dialog_Qty;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.ItemClickListener;


/**
 * Created by 7 on 13/11/2017.
 */

public class TrnAdapter extends RecyclerView.Adapter<TrnHolder> implements Filterable{
    Context c;
    ArrayList<Spacecraft_Trn> spacecrafts,filterList;
    CustomFilter filter;
    int clickcount;
    FragmentManager fm;
    public TrnAdapter(Context c, ArrayList<Spacecraft_Trn> spacecrafts, FragmentManager fm) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.filterList=spacecrafts;
        this.fm=fm;
    }

    @Override
    public TrnHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_list_trn,parent,false);
        return new TrnHolder(v);
    }

    @Override
    public void onBindViewHolder(final TrnHolder holder, int position) {
        final Spacecraft_Trn spacecraft=spacecrafts.get(position);
        final Double dQty=Double.parseDouble(spacecraft.getQty());
        final String Qty=String.format(Locale.US, "%,.2f",dQty);
        final Double dTotalAmt=Double.parseDouble(spacecraft.getHCNetAmt());
        final String TotalAmt=String.format(Locale.US, "%,.2f",dTotalAmt);
        holder.vDoc1No.setText(spacecraft.getDoc1No());
        holder.vDateTime.setText(spacecraft.getD_ateTime());
        holder.vQty.setText(Qty);
        holder.vTotalAmt.setText(TotalAmt);
        holder.vCusName.setText(spacecraft.getCusName());
        final String SynYN=spacecraft.getSynYN();
        if(SynYN.equals("0")){
            holder.vSynYN.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.sync_disabled, 0);
        }else{
            holder.vSynYN.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sync_success, 0);
        }
        //new
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {

            }
        });
        //end new

    }

    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new CustomFilter(filterList,this);
        }
        return filter;
    }
}
