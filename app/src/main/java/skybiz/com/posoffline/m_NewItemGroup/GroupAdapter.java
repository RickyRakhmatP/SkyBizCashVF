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
import skybiz.com.posoffline.ui_CreditNote.CreditNote;

/**
 * Created by 7 on 14/11/2017.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupHolder> {
    Context c;
    String DocType;
    ArrayList<Spacecraft> spacecrafts;

    RecyclerView rv;
    public GroupAdapter(Context c,String DocType, ArrayList<Spacecraft> spacecrafts) {
        this.c = c;
        this.DocType = DocType;
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
        holder.btnGroup.setText(spacecraft.getDescription());
        holder.btnGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(DocType.equals("CR")) {
                    ((CashReceipt) c).retItem(spacecraft.getItemGroup());
                }else if(DocType.equals("CusCN")){
                    ((CreditNote) c).retItem(spacecraft.getItemGroup());
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }
}
