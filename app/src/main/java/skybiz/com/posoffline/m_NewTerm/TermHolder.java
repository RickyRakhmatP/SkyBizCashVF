package skybiz.com.posoffline.m_NewTerm;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import skybiz.com.posoffline.R;


/**
 * Created by 7 on 27/10/2017.
 */

public class TermHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView vTermCode,vTermDesc,vD_ay;
    ItemClickListener itemClickListener;

    public TermHolder(View itemView) {

        super(itemView);
        vTermCode         =(TextView) itemView.findViewById(R.id.vTermCode);
        vTermDesc         =(TextView) itemView.findViewById(R.id.vTermDesc);
        vD_ay         =(TextView) itemView.findViewById(R.id.vD_ay);
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
