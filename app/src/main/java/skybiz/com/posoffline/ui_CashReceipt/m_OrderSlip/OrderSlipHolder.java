package skybiz.com.posoffline.ui_CashReceipt.m_OrderSlip;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_ListSO.ItemClickListener;

/**
 * Created by 7 on 27/10/2017.
 */

public class OrderSlipHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    Button btnDoc1NoSO;
    ItemClickListener itemClickListener;


    public OrderSlipHolder(View itemView) {
        super(itemView);
        btnDoc1NoSO        =(Button) itemView.findViewById(R.id.btnDoc1NoSO);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
    @Override
    public void onClick(View v){
        this.itemClickListener.onItemClick();
    }

}
