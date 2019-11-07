package skybiz.com.posoffline.ui_SalesOrder.m_Order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Order;


/**
 * Created by 7 on 13/11/2017.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderHolder> {
    Context c;
    ArrayList<Spacecraft_Order> spacecrafts;
    int clickcount;
    FragmentManager fm;
    public OrderAdapter(Context c, ArrayList<Spacecraft_Order> spacecrafts,FragmentManager fm) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.fm=fm;
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_order,parent,false);
        return new OrderHolder(v);

    }

    @Override
    public void onBindViewHolder(final OrderHolder holder, int position) {
        final Spacecraft_Order spacecraft=spacecrafts.get(position);
        final Double dQty=Double.parseDouble(spacecraft.getBtnQty());
        final String Qty=String.format(Locale.US, "%,.2f",dQty);
        final Double dUnitPrice=Double.parseDouble(spacecraft.getHCUnitCost());
        final String UnitPrice=String.format(Locale.US, "%,.2f",dUnitPrice);
        final String UOM=spacecraft.getUOM();
        final String RunNo=String.valueOf(spacecraft.getRunNo());
        holder.Descriptiontxt.setText(spacecraft.getDescription());
        holder.txtQty.setText(Qty);
        holder.txtUnitPrice.setText(UnitPrice);
        //new
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String ItemCode=spacecraft.getItemCode();
                openDialogFragment(RunNo,ItemCode,ItemCode+" - "+spacecraft.getDescription(),spacecraft.getBtnQty(),
                        spacecraft.getHCUnitCost(),spacecraft.getHCDiscount(),spacecraft.getDisRate1(),UOM);
            }
        });
        //end new
    }

    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

    private void openDialogFragment(String RunNo,String ItemCode,String Description,String Qty, String UnitPrice, String HCDiscount, String DisRate1, String UOM){
        Bundle b=new Bundle();
        b.putString("CODE_KEY",ItemCode);
        b.putString("DESC_KEY",Description);
        b.putString("QTY_KEY",Qty);
        b.putString("UNITPRICE_KEY",UnitPrice);
        b.putString("DISAMT_KEY",HCDiscount);
        b.putString("DISRATE_KEY",DisRate1);
        b.putString("UOM_KEY",UOM);
        b.putString("RUNNO_KEY",RunNo);
        Dialog_Qty dialogQty=new Dialog_Qty();
        dialogQty.setArguments(b);
        dialogQty.show(fm,"mTag");
    }

}
