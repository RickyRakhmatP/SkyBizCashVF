package skybiz.com.posoffline.ui_Listing.m_ItemSales;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewObject.Spacecraft_ItemSales;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Trn;
import skybiz.com.posoffline.ui_Listing.Listing;


/**
 * Created by 7 on 27/10/2017.
 */

public class ItemSalesAdapter extends RecyclerView.Adapter<ItemSalesHolder> {
    String IPAddress,UserName,Password,DBName,Qty,DocType;
    Context c;
    ArrayList<Spacecraft_ItemSales> spacecrafts;
    int clickcount=0;

    public ItemSalesAdapter(Context c, String DocType, ArrayList<Spacecraft_ItemSales> spacecrafts) {
        this.c = c;
        this.DocType = DocType;
        this.spacecrafts = spacecrafts;
    }
    @Override
    public ItemSalesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_itemsales,parent,false);
        return new ItemSalesHolder(v);
    }
    @Override
    public void onBindViewHolder(final ItemSalesHolder holder, int position) {
        final Spacecraft_ItemSales spacecraft=spacecrafts.get(position);
        holder.vItemCode.setText(spacecraft.getItemCode());
        holder.vQty.setText(spacecraft.getQty());
        holder.vAmount.setText(spacecraft.getAmount());
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }
}
