package skybiz.com.posoffline.m_NewCustomer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import skybiz.com.posoffline.R;


/**
 * Created by 7 on 27/10/2017.
 */

public class CustomerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vCusCode, vCusName,vTermCode, vD_ay,vSalesPersonCode;
    ItemClickListener itemClickListener;

    public CustomerHolder(View itemView) {
        super(itemView);
        vCusCode     =(TextView) itemView.findViewById(R.id.vCusCode);
        vCusName     =(TextView) itemView.findViewById(R.id.vCusName);
       // vTermCode    =(TextView) itemView.findViewById(R.id.vTermCode);
       // vD_ay     =(TextView) itemView.findViewById(R.id.vD_ay);
        vSalesPersonCode    =(TextView) itemView.findViewById(R.id.vSalesPersonCode);
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
