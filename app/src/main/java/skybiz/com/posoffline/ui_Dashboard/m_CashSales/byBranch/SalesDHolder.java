package skybiz.com.posoffline.ui_Dashboard.m_CashSales.byBranch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import skybiz.com.posoffline.R;


/**
 * Created by 7 on 27/10/2017.
 */

public class SalesDHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vBranch, vTotal;
    ItemClickListener itemClickListener;

    public SalesDHolder(View itemView) {
        super(itemView);
        vBranch            =(TextView) itemView.findViewById(R.id.vBranch);
        vTotal            =(TextView) itemView.findViewById(R.id.vTotal);
        //itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
    @Override
    public void onClick(View v){
        this.itemClickListener.onItemClick(this.getLayoutPosition());
    }

}
