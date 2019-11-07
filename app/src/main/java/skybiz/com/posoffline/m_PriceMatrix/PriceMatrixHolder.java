package skybiz.com.posoffline.m_PriceMatrix;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import skybiz.com.posoffline.R;

/**
 * Created by 7 on 14/11/2017.
 */

public class PriceMatrixHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView vDescription,vPct,vCriteria;
    ItemClickListener itemClickListener;


    public PriceMatrixHolder(View itemView) {
        super(itemView);
        vDescription=(TextView) itemView.findViewById(R.id.vDescription);
        vPct        =(TextView) itemView.findViewById(R.id.vPct);
        vCriteria   =(TextView) itemView.findViewById(R.id.vCriteria);
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
