package skybiz.com.posoffline.ui_CashReceipt.m_GuestCheck;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_SO;


/**
 * Created by 7 on 27/10/2017.
 */

public class GuestAdapter extends RecyclerView.Adapter<GuestHolder> {
    Context c;
    ArrayList<Spacecraft_SO> spacecrafts;
    DialogGuest dialogGuest;

    public GuestAdapter(Context c, ArrayList<Spacecraft_SO> spacecrafts, DialogGuest dialogGuest) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogGuest = dialogGuest;
    }

    @Override
    public GuestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_listso, parent, false);
        return new GuestHolder(v);

    }

    @Override
    public void onBindViewHolder(final GuestHolder holder, int position) {
        final Spacecraft_SO spacecraft = spacecrafts.get(position);
        holder.btnDoc1NoSO.setText(spacecraft.getDoc2No());
        holder.btnDoc1NoSO.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                dialogGuest.setPrint(spacecraft.getDoc1No(),spacecraft.getDoc2No());
                holder.btnDoc1NoSO.setEnabled(false);
                holder.btnDoc1NoSO.setBackgroundColor(Color.BLACK);
            }
        });
    }

    @Override
    public int getItemCount() {
        return spacecrafts.size();
    }


}
