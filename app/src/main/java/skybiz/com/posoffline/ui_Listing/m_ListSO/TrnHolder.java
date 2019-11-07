package skybiz.com.posoffline.ui_Listing.m_ListSO;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.ItemClickListener;

/**
 * Created by 7 on 13/11/2017.
 */

public class TrnHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView vDoc1No,vDateTime,vQty,vTotalAmt,vSynYN;
    ItemClickListener itemClickListener;


    public TrnHolder(View itemView) {
        super(itemView);
        vDoc1No     =(TextView) itemView.findViewById(R.id.vDoc1No);
        vDateTime   =(TextView) itemView.findViewById(R.id.vDateTime);
        vQty        =(TextView) itemView.findViewById(R.id.vQty);
        vTotalAmt    =(TextView) itemView.findViewById(R.id.vTotalAmt);
        vSynYN        =(TextView) itemView.findViewById(R.id.vSynYN);
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
