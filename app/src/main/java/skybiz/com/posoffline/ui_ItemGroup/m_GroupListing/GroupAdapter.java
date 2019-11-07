package skybiz.com.posoffline.ui_ItemGroup.m_GroupListing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewObject.Spacecraft_Group;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;
import skybiz.com.posoffline.ui_Item.ItemListing;
import skybiz.com.posoffline.ui_ItemGroup.ItemGroupList;

/**
 * Created by 7 on 27/10/2017.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupHolder> {
    Context c;
    ArrayList<Spacecraft_Group> spacecrafts;

    public GroupAdapter(Context c, ArrayList<Spacecraft_Group> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }

    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_itemlisting,parent,false);
        return new GroupHolder(v);
    }
    @Override
    public void onBindViewHolder(final GroupHolder holder, int position) {
        final Spacecraft_Group spacecraft=spacecrafts.get(position);
        holder.vItemCode.setText(spacecraft.getItemGroup());
        holder.vDescription.setText(spacecraft.getDescription());
        holder.vUnitPrice.setText(spacecraft.getModifier1());
        holder.vEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ItemGroupList)c).edititem(spacecraft.getItemGroup());
            }
        });
        holder.vDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ItemGroupList)c).deleteitem(spacecraft.getItemGroup());
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
