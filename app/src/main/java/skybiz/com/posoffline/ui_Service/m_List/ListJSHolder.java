package skybiz.com.posoffline.ui_Service.m_List;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import skybiz.com.posoffline.R;


/**
 * Created by 7 on 27/10/2017.
 */

public class ListJSHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vDoc1No, vCusName,vCaseType, vD_ate,vPriority,vContactTel;
    ItemClickListener itemClickListener;

    public ListJSHolder(View itemView) {
        super(itemView);
        vDoc1No         =(TextView) itemView.findViewById(R.id.vDoc1No);
        vCusName        =(TextView) itemView.findViewById(R.id.vCusName);
        vCaseType       =(TextView) itemView.findViewById(R.id.vCaseType);
        vD_ate          =(TextView) itemView.findViewById(R.id.vD_ate);
        vContactTel     =(TextView) itemView.findViewById(R.id.vContactTel);
        vPriority     =(TextView) itemView.findViewById(R.id.vPriority);
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
