package skybiz.com.posoffline.m_NewSalesPerson;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import skybiz.com.posoffline.R;


/**
 * Created by 7 on 27/10/2017.
 */

public class SalesPersonHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vSalesPersonCode, vN_ame;
    ItemClickListener itemClickListener;

    public SalesPersonHolder(View itemView) {
        super(itemView);
        vN_ame               =(TextView) itemView.findViewById(R.id.vN_ame);
        vSalesPersonCode     =(TextView) itemView.findViewById(R.id.vSalesPersonCode);
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
