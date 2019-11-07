package skybiz.com.posoffline.ui_CashReceipt.m_Modifier;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import skybiz.com.posoffline.R;


/**
 * Created by 7 on 27/10/2017.
 */

public class ModifierHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vModifier, vRemark;
    ItemClickListener itemClickListener;

    public ModifierHolder(View itemView) {
        super(itemView);
        vModifier     =(TextView) itemView.findViewById(R.id.vModifier);
        vRemark       =(TextView) itemView.findViewById(R.id.vRemark);
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
