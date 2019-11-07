package skybiz.com.posoffline.ui_Item.m_ItemListing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewItemList.DialogItem;
import skybiz.com.posoffline.m_NewObject.SetDefaultTax;
import skybiz.com.posoffline.m_NewSummary.AddItem;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;
import skybiz.com.posoffline.ui_Item.ItemListing;

/**
 * Created by 7 on 27/10/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
    Context c;
    ArrayList<Spacecraft> spacecrafts;
    String DocType;

    public ItemAdapter(Context c, ArrayList<Spacecraft> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_itemlisting,parent,false);
        return new ItemHolder(v);
    }
    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        final Spacecraft spacecraft=spacecrafts.get(position);
        holder.vItemCode.setText(spacecraft.getItemCode());
        holder.vDescription.setText(spacecraft.getDescription());
        holder.vUnitPrice.setText(spacecraft.getUnitPrice());
        holder.vEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ItemListing)c).edititem(spacecraft.getItemCode());
            }
        });
        holder.vDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ItemListing)c).deleteitem(spacecraft.getItemCode());
            }
        });
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
