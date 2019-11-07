package skybiz.com.posoffline.m_NewSummary;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import skybiz.com.posoffline.R;

/**
 * Created by 7 on 13/11/2017.
 */

public class OrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView ItemCodetxt, ItemGrouptxt,Descriptiontxt,txtQty,txtUnitPrice;
    ImageView imageView;
    Button btnAdd,btnMinus;
    ItemClickListener itemClickListener;

    public OrderHolder(View itemView) {
        super(itemView);
        Descriptiontxt  =(TextView) itemView.findViewById(R.id.DescriptionTxt);
        txtQty          =(TextView) itemView.findViewById(R.id.txtQty);
        txtUnitPrice      =(TextView) itemView.findViewById(R.id.txtUnitPrice);
       // itemView.setOnClickListener(this);
    }
    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
    @Override
    public void onClick(View v){
        this.itemClickListener.onItemClick(this.getLayoutPosition());
    }
}
