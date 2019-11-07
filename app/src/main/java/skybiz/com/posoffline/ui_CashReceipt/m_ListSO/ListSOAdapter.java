package skybiz.com.posoffline.ui_CashReceipt.m_ListSO;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_SO;
import skybiz.com.posoffline.ui_CashReceipt.m_Item.ItemHolder;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItem_ByMain;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.MinusItem;

/**
 * Created by 7 on 27/10/2017.
 */

public class ListSOAdapter extends RecyclerView.Adapter<ListSOHolder> {
    Context c;
    ArrayList<Spacecraft_SO> spacecrafts;
    Fragment_ListSO fragmentListSO;
    int clickcount=0;


    public ListSOAdapter(Context c, ArrayList<Spacecraft_SO> spacecrafts, Fragment_ListSO fragmentListSO) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.fragmentListSO=fragmentListSO;
    }


    @Override
    public ListSOHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_listso,parent,false);
        return new ListSOHolder(v);

    }

    @Override
    public void onBindViewHolder(final ListSOHolder holder, int position) {

        final Spacecraft_SO spacecraft = spacecrafts.get(position);
        final String vDoc2No=spacecraft.getDoc2No().replaceAll(";","/");
        holder.btnDoc1NoSO.setText(vDoc2No);
        holder.btnDoc1NoSO.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                fragmentListSO.setOrder(spacecraft.getDoc1No(),spacecraft.getDoc2No(),spacecraft.getAttention());
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
