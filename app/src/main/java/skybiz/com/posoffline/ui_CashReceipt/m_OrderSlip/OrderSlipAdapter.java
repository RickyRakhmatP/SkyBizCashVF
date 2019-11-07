package skybiz.com.posoffline.ui_CashReceipt.m_OrderSlip;

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

public class OrderSlipAdapter extends RecyclerView.Adapter<OrderSlipHolder> {
    Context c;
    ArrayList<Spacecraft_SO> spacecrafts;
    Dialog_OrderSlip dialogOrderSlip;
    int clickcount=0;

    public OrderSlipAdapter(Context c, ArrayList<Spacecraft_SO> spacecrafts, Dialog_OrderSlip dialogOrderSlip) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogOrderSlip=dialogOrderSlip;
    }

    @Override
    public OrderSlipHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_listso,parent,false);
        return new OrderSlipHolder(v);
    }
    @Override
    public void onBindViewHolder(final OrderSlipHolder holder, int position) {

        final Spacecraft_SO spacecraft = spacecrafts.get(position);
        final String vDoc2No=spacecraft.getDoc2No().replaceAll(";","/");
        holder.btnDoc1NoSO.setText(vDoc2No);
        holder.btnDoc1NoSO.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                dialogOrderSlip.print(spacecraft.getDoc1No());
                holder.btnDoc1NoSO.setEnabled(false);
                holder.btnDoc1NoSO.setBackgroundColor(Color.BLACK);
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }
}
