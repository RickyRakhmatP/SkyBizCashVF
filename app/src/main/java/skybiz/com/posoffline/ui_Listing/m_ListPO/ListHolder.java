package skybiz.com.posoffline.ui_Listing.m_ListPO;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import skybiz.com.posoffline.R;

/**
 * Created by 7 on 27/10/2017.
 */

public class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView vDoc1No, vD_ate,vCusName, vQty,vTotalAmt,vSyncYN;
    ItemClickListener itemClickListener;

    public ListHolder(View itemView) {
        super(itemView);
        vDoc1No     =(TextView) itemView.findViewById(R.id.vDoc1No);
        vD_ate      =(TextView) itemView.findViewById(R.id.vDateTime);
       // vCusName    =(TextView) itemView.findViewById(R.id.vCusName);
        vQty        =(TextView) itemView.findViewById(R.id.vQty);
        vTotalAmt   =(TextView) itemView.findViewById(R.id.vTotalAmt);
        vSyncYN     =(TextView) itemView.findViewById(R.id.vSynYN);
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
