package skybiz.com.posoffline.ui_CashReceipt.m_ItemGroup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;

/**
 * Created by 7 on 14/11/2017.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupHolder> {
    Context c;
    ArrayList<Spacecraft> spacecrafts;
    int row_index;

    RecyclerView rv;
    public GroupAdapter(Context c, ArrayList<Spacecraft> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }

    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_itemgroup,parent,false);
        return new GroupHolder(v);

    }

    @Override
    public void onBindViewHolder(GroupHolder holder, final int position) {
        final Spacecraft spacecraft=spacecrafts.get(position);
        holder.vGroup.setText(spacecraft.getDescription());
        //RotateAnimation rotate= (RotateAnimation) AnimationUtils.loadAnimation(c,R.anim.rotateanimation);
       // holder.vGroup.setAnimation(rotate);

        holder.vGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
               ((CashReceipt) c).retItem(spacecraft.getItemGroup());
                row_index=position;
                notifyDataSetChanged();
            }
        });
       /* holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick() {
                ((CashReceipt) c).retItem(spacecraft.getItemGroup());
                row_index=position;
                notifyDataSetChanged();
            }
        });*/

        if(row_index==position){
            holder.vGroup.setBackgroundColor(Color.parseColor("#000000"));
            ((CashReceipt) c).retItem(spacecraft.getItemGroup());
        } else{
            holder.vGroup.setBackgroundColor(Color.parseColor("#689f38"));
        }
    }

    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }
}
