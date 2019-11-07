package skybiz.com.posoffline.ui_CashReceipt.m_ItemGroup;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.VerticalTextView;

/**
 * Created by 7 on 14/11/2017.
 */

public class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    //VerticalTextView vGroup ;
    Button vGroup;
    // RecyclerView rvItem ;
    ItemClickListener itemClickListener;


    public GroupHolder(View itemView) {
        super(itemView);
        // rvItem=(RecyclerView) itemView.findViewById(R.id.rec_list);
       // vGroup=(VerticalTextView) itemView.findViewById(R.id.vGroup);
        vGroup=(Button) itemView.findViewById(R.id.btnGroup);
        //itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
    @Override
    public void onClick(View v){
        this.itemClickListener.onItemClick();
    }
}
