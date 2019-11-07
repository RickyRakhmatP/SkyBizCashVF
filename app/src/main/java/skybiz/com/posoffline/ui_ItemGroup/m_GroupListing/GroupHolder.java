package skybiz.com.posoffline.ui_ItemGroup.m_GroupListing;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import skybiz.com.posoffline.R;


/**
 * Created by 7 on 27/10/2017.
 */

public class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vItemCode, vDescription,vUnitPrice,vEdit,vDelete;
    ItemClickListener itemClickListener;

    public GroupHolder(View itemView) {
        super(itemView);
        vItemCode     =(TextView) itemView.findViewById(R.id.vItemCode);
        vDescription  =(TextView) itemView.findViewById(R.id.vDescription);
        vUnitPrice    =(TextView) itemView.findViewById(R.id.vUnitPrice);
        vEdit         =(TextView) itemView.findViewById(R.id.vEdit);
        vDelete       =(TextView) itemView.findViewById(R.id.vDelete);
       // itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
    @Override
    public void onClick(View v){
        this.itemClickListener.onItemClick(this.getLayoutPosition());
    }

}
