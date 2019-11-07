package skybiz.com.posoffline.ui_Listing.m_PayDaily;

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
import skybiz.com.posoffline.m_NewObject.Spacecraft_PayDaily;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Trn;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.Dialog_Qty;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.ItemClickListener;
import skybiz.com.posoffline.ui_Listing.m_ListCS.CustomFilter;


/**
 * Created by 7 on 13/11/2017.
 */

public class PayAdapter extends RecyclerView.Adapter<PayHolder>{
    Context c;
    ArrayList<Spacecraft_PayDaily> spacecrafts;
    int clickcount;

    public PayAdapter(Context c, ArrayList<Spacecraft_PayDaily> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }

    @Override
    public PayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_paydaily,parent,false);
        return new PayHolder(v);
    }

    @Override
    public void onBindViewHolder(final PayHolder holder, int position) {
        final Spacecraft_PayDaily spacecraft=spacecrafts.get(position);
        holder.vCC1Amt.setText(spacecraft.getCC1Amt());
        holder.vCC1Code.setText(spacecraft.getCC1Code());
    }

    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
