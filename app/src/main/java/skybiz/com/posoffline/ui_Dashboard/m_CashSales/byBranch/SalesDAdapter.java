package skybiz.com.posoffline.ui_Dashboard.m_CashSales.byBranch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewObject.Spacecraft_SalesD;
import skybiz.com.posoffline.ui_CashReceipt.m_Customer.DialogCustomer;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Customer;

/**
 * Created by 7 on 27/10/2017.
 */

public class SalesDAdapter extends RecyclerView.Adapter<SalesDHolder> {
    Context c;
    ArrayList<Spacecraft_SalesD> spacecrafts;
    String uFrom;

    public SalesDAdapter(Context c, ArrayList<Spacecraft_SalesD> spacecrafts,String uFrom) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.uFrom = uFrom;
    }

    @Override
    public SalesDHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_salesd,parent,false);
        return new SalesDHolder(v);
    }
    @Override
    public void onBindViewHolder(final SalesDHolder holder, int position) {
        final Spacecraft_SalesD spacecraft=spacecrafts.get(position);
        String BranchCode=spacecraft.getBranchCode();
        if(uFrom.equals("By Date")){
            holder.vBranch.setText(spacecraft.getD_ate());
        }else{
            holder.vBranch.setText(spacecraft.getBranchCode());
        }

        holder.vTotal.setText(spacecraft.getCurCode()+spacecraft.getTotal());
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
