package skybiz.com.posoffline.ui_Listing.m_Listing;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;

import skybiz.com.posoffline.R;

/**
 * Created by 7 on 27/10/2017.
 */

public class ListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView vDoc1No, vD_ate,vCusName, vQty,vTotalAmt,vSyncYN,vDelete;
    LinearLayout lnTrn;
    ItemClickListener itemClickListener;

    public ListHolder(View itemView) {
        super(itemView);
        vDoc1No     =(TextView) itemView.findViewById(R.id.vDoc1No);
        vD_ate      =(TextView) itemView.findViewById(R.id.vDateTime);
       // vCusName    =(TextView) itemView.findViewById(R.id.vCusName);
        vQty        =(TextView) itemView.findViewById(R.id.vQty);
        vTotalAmt   =(TextView) itemView.findViewById(R.id.vTotalAmt);
        vSyncYN     =(TextView) itemView.findViewById(R.id.vSynYN);
        vDelete     =(TextView) itemView.findViewById(R.id.vDelete);
        lnTrn       =(LinearLayout) itemView.findViewById(R.id.lnTrn);
       // itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }

    @Override
    public void onClick(View v){
        this.itemClickListener.onItemClick();
    }

}
