package skybiz.com.posoffline.ui_Sync.m_Resync;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.ItemClickListener;

/**
 * Created by 7 on 13/11/2017.
 */

public class ResyncHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView vDoc1No,vDateTime,vTotalAmt,vSynYN;
    CheckBox chkResync;
    ItemClickListener itemClickListener;


    public ResyncHolder(View itemView) {
        super(itemView);
        vDoc1No     =(TextView) itemView.findViewById(R.id.vDoc1No);
        vDateTime   =(TextView) itemView.findViewById(R.id.vDateTime);
        chkResync     =(CheckBox) itemView.findViewById(R.id.chkResync);
        vTotalAmt    =(TextView) itemView.findViewById(R.id.vTotalAmt);
        vSynYN        =(TextView) itemView.findViewById(R.id.vSynYN);
      //  itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
    @Override
    public void onClick(View v){
        this.itemClickListener.onItemClick(this.getLayoutPosition());
    }
}
