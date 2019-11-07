package skybiz.com.posoffline.ui_Service.m_Service;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import skybiz.com.posoffline.R;


/**
 * Created by 7 on 27/10/2017.
 */

public class ServiceHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView vParticular;
    ItemClickListener itemClickListener;

    public ServiceHolder(View itemView) {

        super(itemView);
        vParticular         =(TextView) itemView.findViewById(R.id.vParticular);
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
