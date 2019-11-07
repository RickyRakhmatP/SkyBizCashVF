package skybiz.com.posoffline.ui_QuickCash.m_ItemQuick;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;
import skybiz.com.posoffline.ui_Item.ItemListing;
import skybiz.com.posoffline.ui_Item.m_ItemListing.ItemClickListener;
import skybiz.com.posoffline.ui_QuickCash.QuickCash;

/**
 * Created by 7 on 27/10/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
    Context c;
    ArrayList<Spacecraft> spacecrafts;
    int row_index;

    public ItemAdapter(Context c, ArrayList<Spacecraft> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_itemquick,parent,false);
        return new ItemHolder(v);
    }
    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        final Spacecraft spacecraft=spacecrafts.get(position);
        holder.vDescription.setText(spacecraft.getDescription());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                ((QuickCash)c).setItem(spacecraft.getItemCode(),spacecraft.getDescription());
                row_index=pos;
                notifyDataSetChanged();
            }
        });
        if(row_index==position){
            holder.vDescription.setBackgroundResource(R.color.color_warning);
            holder.vDescription.setTextColor(Color.WHITE);
            ((QuickCash)c).setItem(spacecraft.getItemCode(),spacecraft.getDescription());
        } else{
            holder.vDescription.setBackgroundResource(R.color.colorWhite);
            holder.vDescription.setTextColor(Color.BLACK);
        }
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

    private String charReplace(String text){
        String newText=text.replaceAll("[\\.$|,|;|']","");
        return newText;
    }
}
