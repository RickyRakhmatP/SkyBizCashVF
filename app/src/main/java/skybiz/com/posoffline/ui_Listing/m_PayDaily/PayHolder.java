package skybiz.com.posoffline.ui_Listing.m_PayDaily;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.ItemClickListener;

/**
 * Created by 7 on 13/11/2017.
 */

public class PayHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView vCC1Amt,vCC1Code;
    ItemClickListener itemClickListener;


    public PayHolder(View itemView) {
        super(itemView);
        vCC1Amt     =(TextView) itemView.findViewById(R.id.vCC1Amt);
        vCC1Code       =(TextView) itemView.findViewById(R.id.vCC1Code);
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
