package skybiz.com.posoffline.m_NewSalesPerson;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewObject.Spacecraft_SalesPerson;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Customer;

/**
 * Created by 7 on 27/10/2017.
 */

public class SalesPersonAdapter extends RecyclerView.Adapter<SalesPersonHolder> {
    Context c;
    ArrayList<Spacecraft_SalesPerson> spacecrafts;
    DialogSalesPerson dialogSalesPerson;

    public SalesPersonAdapter(Context c, ArrayList<Spacecraft_SalesPerson> spacecrafts, DialogSalesPerson dialogSalesPerson) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogSalesPerson = dialogSalesPerson;
    }

    @Override
    public SalesPersonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_salesperson,parent,false);
        return new SalesPersonHolder(v);
    }
    @Override
    public void onBindViewHolder(final SalesPersonHolder holder, int position) {
        final Spacecraft_SalesPerson spacecraft=spacecrafts.get(position);
        holder.vSalesPersonCode.setText(spacecraft.getSalesPersonCode());
        holder.vN_ame.setText(spacecraft.getN_ame());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                dialogSalesPerson.setSalesPerson(spacecraft.getSalesPersonCode(),spacecraft.getN_ame() );
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
