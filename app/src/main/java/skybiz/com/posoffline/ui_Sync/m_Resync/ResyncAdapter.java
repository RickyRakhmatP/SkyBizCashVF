package skybiz.com.posoffline.ui_Sync.m_Resync;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Trn;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.Dialog_Qty;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.ItemClickListener;
import skybiz.com.posoffline.ui_Sync.Sync;


/**
 * Created by 7 on 13/11/2017.
 */

public class ResyncAdapter extends RecyclerView.Adapter<ResyncHolder> implements Filterable{
    Context c;
    ArrayList<Spacecraft_Trn> spacecrafts,filterList;
    ResyncFilter filter;
    int clickcount;
    FragmentManager fm;
    public ResyncAdapter(Context c, ArrayList<Spacecraft_Trn> spacecrafts, FragmentManager fm) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.filterList=spacecrafts;
        this.fm=fm;
    }

    @Override
    public ResyncHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_resync,parent,false);
        return new ResyncHolder(v);
    }

    @Override
    public void onBindViewHolder(final ResyncHolder holder, int position) {
        final Spacecraft_Trn spacecraft=spacecrafts.get(position);
        final Double dTotalAmt=Double.parseDouble(spacecraft.getHCNetAmt());
        final String TotalAmt=String.format(Locale.US, "%,.2f",dTotalAmt);
        holder.vDoc1No.setText(spacecraft.getDoc1No());
        holder.vDateTime.setText(spacecraft.getD_ateTime());
        holder.vTotalAmt.setText(TotalAmt);
        final String SynYN=spacecraft.getSynYN();
        if(SynYN.equals("0")){
            holder.vSynYN.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.sync_disabled, 0);
        }else{
            holder.vSynYN.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sync_success, 0);
        }
        holder.chkResync.setOnCheckedChangeListener(null);
        holder.chkResync.setChecked(spacecraft.isSelected());
        holder.chkResync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    holder.chkResync.setChecked(true);
                    spacecraft.setSelected(true);
                    ((Sync)c).changeStatus(spacecraft.getDoc1No(),"0");
                }else{
                    holder.chkResync.setChecked(false);
                    spacecraft.setSelected(false);
                    ((Sync)c).changeStatus(spacecraft.getDoc1No(),"1");
                }
            }
        });

    }

    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

    private void openDialogFragment(String ItemCode,String Description,String Qty, String UnitPrice, String HCDiscount, String DisRate1){
        Bundle b=new Bundle();
        b.putString("CODE_KEY",ItemCode);
        b.putString("DESC_KEY",Description);
        b.putString("QTY_KEY",Qty);
        b.putString("UNITPRICE_KEY",UnitPrice);
        b.putString("DISAMT_KEY",HCDiscount);
        b.putString("DISRATE_KEY",DisRate1);
        Dialog_Qty dialogQty=new Dialog_Qty();
        dialogQty.setArguments(b);
        dialogQty.show(fm,"mTag");
    }
    @Override
    public Filter getFilter() {
        if(filter==null) {
            filter=new ResyncFilter(filterList,this);
        }
        return filter;
    }
}
