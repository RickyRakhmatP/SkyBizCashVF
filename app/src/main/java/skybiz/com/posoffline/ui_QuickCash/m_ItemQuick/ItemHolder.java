package skybiz.com.posoffline.ui_QuickCash.m_ItemQuick;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_Item.m_ItemListing.ItemClickListener;


/**
 * Created by 7 on 27/10/2017.
 */

public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView  vDescription;
    ItemClickListener itemClickListener;

    public ItemHolder(View itemView) {
        super(itemView);
        vDescription  =(TextView) itemView.findViewById(R.id.vDescription);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
    @Override
    public void onClick(View v){
        this.itemClickListener.onItemClick(this.getLayoutPosition());
    }

}
