package skybiz.com.posoffline.ui_CashReceipt.m_Reprint;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import skybiz.com.posoffline.R;


/**
 * Created by 7 on 27/10/2017.
 */

public class ReprintHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vDoc1No, vDateTime, vQty, vReprint, vTotalAmt;
    ItemClickListener itemClickListener;
    LinearLayout lnReprint;

    public ReprintHolder(View itemView) {
        super(itemView);
        vDoc1No         =(TextView) itemView.findViewById(R.id.vDoc1No);
        vDateTime       =(TextView) itemView.findViewById(R.id.vDateTime);
        vQty            =(TextView) itemView.findViewById(R.id.vQty);
        vTotalAmt       =(TextView) itemView.findViewById(R.id.vTotalAmt);
        vReprint        =(TextView) itemView.findViewById(R.id.vReprint);
        lnReprint       =(LinearLayout) itemView.findViewById(R.id.lnReprint);
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
