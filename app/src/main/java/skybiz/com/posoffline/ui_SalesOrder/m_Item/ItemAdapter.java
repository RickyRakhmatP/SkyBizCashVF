package skybiz.com.posoffline.ui_SalesOrder.m_Item;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;
import skybiz.com.posoffline.ui_SalesOrder.m_Order.AddItem;
import skybiz.com.posoffline.ui_SalesOrder.m_Order.MinusItem;


/**
 * Created by 7 on 27/10/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
    String Qty;
    Context c;
    ArrayList<Spacecraft> spacecrafts;
    int clickcount=0;

    public ItemAdapter(Context c, ArrayList<Spacecraft> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }


    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item,parent,false);
        return new ItemHolder(v);

    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {

        final Spacecraft spacecraft=spacecrafts.get(position);
        holder.ItemCodetxt.setText(spacecraft.getItemCode());
        holder.Descriptiontxt.setText(spacecraft.getDescription());
        holder.txtUnitPrice.setText(spacecraft.getCurCode()+spacecraft.getUnitPrice());
        Qty=spacecraft.getBtnQty();
        final int dqty= Integer.parseInt(Qty);;
       if(Qty.equals("0")) {
            holder.btnAdd.setText("+");
        }else{
            holder.btnAdd.setText(Qty);
            spacecraft.setId(dqty);
        }
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                spacecraft.setId(spacecraft.getId()+1);
                final String vQty= String.valueOf(spacecraft.getId());
                final String vUnitPrice=spacecraft.getUnitPrice();
                final String UnitPrice=vUnitPrice.replaceAll(",","");
                //Log.d("Unit Price",UnitPrice);
                String vHCDiscount=spacecraft.getHCDiscount();
                String vDisRate1=spacecraft.getDisRate1();

                if(Double.parseDouble(vHCDiscount)>0 && Double.parseDouble(vDisRate1)>0){
                    vDisRate1="0.00";
                }else if(Double.parseDouble(vHCDiscount)>0 && Double.parseDouble(vDisRate1)==0){
                    vDisRate1="0.00";
                }else if(Double.parseDouble(vHCDiscount)==0 && Double.parseDouble(vDisRate1)>0){
                    vHCDiscount="0.00";
                }
                AddItem fnadd=new AddItem(c , spacecraft.getItemCode(),vQty,UnitPrice,vHCDiscount,vDisRate1);
                fnadd.execute();
                holder.btnAdd.setText(String.valueOf(spacecraft.getId()));
            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                final int qty= spacecraft.getId();
                final String vQty= String.valueOf(spacecraft.getId());
                final String vUnitPrice=spacecraft.getUnitPrice();
                final String UnitPrice=vUnitPrice.replaceAll(",","");
                if(qty!=0){
                    MinusItem fnminus=new MinusItem(c,spacecraft.getItemCode());
                    fnminus.execute();
                    spacecraft.setId(spacecraft.getId()-1);
                    holder.btnAdd.setText(String.valueOf(spacecraft.getId()));
                }else{
                    holder.btnAdd.setText("+");
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
