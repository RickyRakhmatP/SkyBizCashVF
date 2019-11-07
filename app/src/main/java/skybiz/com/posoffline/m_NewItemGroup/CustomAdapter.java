package skybiz.com.posoffline.m_NewItemGroup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;

/**
 * Created by 7 on 14/11/2017.
 */

public class CustomAdapter extends RecyclerView.Adapter<GroupHolder> {
    String IPAddress,UserName,Password,DBName;
    Context c;
    ArrayList<Spacecraft> spacecrafts;

    RecyclerView rv;
    public CustomAdapter(Context c, ArrayList<Spacecraft> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }

    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_itemgroup,parent,false);
        return new GroupHolder(v);

    }

    @Override
    public void onBindViewHolder(GroupHolder holder, int position) {
        final Spacecraft spacecraft=spacecrafts.get(position);
        holder.btnGroup.setText(spacecraft.getItemGroup());
        holder.btnGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
               ((CashReceipt) c).refreshContent(spacecraft.getItemGroup());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }
}
