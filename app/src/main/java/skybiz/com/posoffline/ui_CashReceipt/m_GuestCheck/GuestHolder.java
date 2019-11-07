package skybiz.com.posoffline.ui_CashReceipt.m_GuestCheck;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import skybiz.com.posoffline.R;

/**
 * Created by 7 on 27/10/2017.
 */

public class GuestHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    Button btnDoc1NoSO;
    ItemClickListener itemClickListener;


    public GuestHolder(View itemView) {
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
