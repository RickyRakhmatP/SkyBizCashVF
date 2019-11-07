package skybiz.com.posoffline.ui_CashReceipt.m_ListSO;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_SO;


/**
 * Created by 7 on 27/10/2017.
 */

public class ListSOAdapterOld extends RecyclerView.Adapter<ListSOHolder> {
    Context c;
    ArrayList<Spacecraft_SO> spacecrafts;
    Fragment_ListSO listSO;

    public ListSOAdapterOld(Context c, ArrayList<Spacecraft_SO> spacecrafts, Fragment_ListSO listSO) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.listSO = listSO;
    }

    @Override
    public ListSOHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_listso, parent, false);
        return new ListSOHolder(v);

    }

    @Override
    public void onBindViewHolder(final ListSOHolder holder, int position) {

        final Spacecraft_SO spacecraft = spacecrafts.get(position);
        holder.btnDoc1NoSO.setText(spacecraft.getDoc2No());
        holder.btnDoc1NoSO.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //listSO.setOrder(spacecraft.getDoc1No());
            }
        });
    }

    @Override
    public int getItemCount() {
        return spacecrafts.size();
    }


}
