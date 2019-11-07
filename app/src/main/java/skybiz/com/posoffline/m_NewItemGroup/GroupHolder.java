package skybiz.com.posoffline.m_NewItemGroup;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import skybiz.com.posoffline.R;

/**
 * Created by 7 on 14/11/2017.
 */

public class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    Button btnGroup ;
    // RecyclerView rvItem ;
    ItemClickListener itemClickListener;


    public GroupHolder(View itemView) {
        super(itemView);
        // rvItem=(RecyclerView) itemView.findViewById(R.id.rec_list);
        btnGroup=(Button) itemView.findViewById(R.id.btnGroup);

    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener=itemClickListener;
    }
    @Override
    public void onClick(View v){
        this.itemClickListener.onItemClick();
    }
}
