package skybiz.com.posoffline.ui_SalesOrder.m_Order;

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

        //ItemCodetxt     =(TextView) itemView.findViewById(R.id.ItemCodeTxt);
        //ItemGrouptxt    =(TextView) itemView.findViewById(R.id.ItemGroupTxt);
        Descriptiontxt  =(TextView) itemView.findViewById(R.id.DescriptionTxt);
        //UnitPricetxt    =(TextView) itemView.findViewById(R.id.UnitPriceTxt);
        txtQty          =(TextView) itemView.findViewById(R.id.txtQty);
        txtUnitPrice      =(TextView) itemView.findViewById(R.id.txtUnitPrice);
        //btnAdd          =(Button) itemView.findViewById(R.id.btnAdd);
        //btnMinus        =(Button) itemView.findViewById(R.id.btnMinus);
       // imageView       =(ImageView) itemView.findViewById(R.id.imageView);

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
