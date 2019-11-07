package skybiz.com.posoffline.ui_QuickCash.m_OrderQuick;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.ItemClickListener;

/**
 * Created by 7 on 13/11/2017.
 */

public class OrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView txtDescription,txtUnitPrice;
    ItemClickListener itemClickListener;

    public OrderHolder(View itemView) {
        super(itemView);
        txtDescription  =(TextView) itemView.findViewById(R.id.txtDescription);
        txtUnitPrice    =(TextView) itemView.findViewById(R.id.txtUnitPrice);
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
