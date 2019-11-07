package skybiz.com.posoffline.ui_Listing.m_ItemSales;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import skybiz.com.posoffline.R;

/**
 * Created by 7 on 27/10/2017.
 */

public class ItemSalesHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView vItemCode, vQty,vAmount;
    ItemClickListener itemClickListener;

    public ItemSalesHolder(View itemView) {
        super(itemView);
        vItemCode     =(TextView) itemView.findViewById(R.id.vItemCode);
        vQty     =(TextView) itemView.findViewById(R.id.vQty);
        vAmount     =(TextView) itemView.findViewById(R.id.vAmount);
       // itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }

    @Override
    public void onClick(View v){
        this.itemClickListener.onItemClick();
    }

}
