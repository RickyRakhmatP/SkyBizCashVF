package skybiz.com.posoffline.ui_CashReceipt.m_Misc;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import skybiz.com.posoffline.R;

/**
 * Created by 7 on 27/10/2017.
 */

public class MiscHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView ItemCodetxt, ItemGrouptxt,Descriptiontxt, txtUnitPrice;
   // EditText txtQty,txtUnitPrice;
    ImageView imageView;
    Button btnAdd,btnMinus;
    ItemClickListener itemClickListener;


    public MiscHolder(View itemView) {
        super(itemView);

        ItemCodetxt     =(TextView) itemView.findViewById(R.id.ItemCodeTxt);
        Descriptiontxt  =(TextView) itemView.findViewById(R.id.DescriptionTxt);
        txtUnitPrice    =(TextView) itemView.findViewById(R.id.txtUnitPrice);
        btnAdd          =(Button) itemView.findViewById(R.id.btnAdd);
        //txtQty          =(EditText) itemView.findViewById(R.id.txtQty);
        btnMinus        =(Button) itemView.findViewById(R.id.btnMinus);
       // imageView       =(ImageView) itemView.findViewById(R.id.imageView);

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
