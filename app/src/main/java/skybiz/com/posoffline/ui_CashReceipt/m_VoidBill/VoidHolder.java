package skybiz.com.posoffline.ui_CashReceipt.m_VoidBill;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import skybiz.com.posoffline.R;


/**
 * Created by 7 on 27/10/2017.
 */

public class VoidHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView vDoc1No, vDateTime, vTotalAmt,vStatus;
    LinearLayout lnTrn;
    Button btnVoid;
    ItemClickListener itemClickListener;

    public VoidHolder(View itemView) {
        super(itemView);
        vDoc1No        =(TextView) itemView.findViewById(R.id.vDoc1No);
        vDateTime      =(TextView) itemView.findViewById(R.id.vDateTime);
        vTotalAmt      =(TextView) itemView.findViewById(R.id.vTotalAmt);
        vStatus        =(TextView) itemView.findViewById(R.id.vStatus);
        lnTrn          =(LinearLayout) itemView.findViewById(R.id.lnTrn);
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
