package skybiz.com.posoffline.ui_Member.m_ItemList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewObject.SetDefaultTax;
import skybiz.com.posoffline.m_NewObject.SetUOM;
import skybiz.com.posoffline.m_NewSummary.AddItem;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;
import skybiz.com.posoffline.ui_Member.m_PointRedemption.PointRedeem;

/**
 * Created by 7 on 27/10/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
    Context c;
    ArrayList<Spacecraft> spacecrafts;
    DialogItem dialogItem;

    public ItemAdapter(Context c, ArrayList<Spacecraft> spacecrafts, DialogItem dialogItem) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogItem=dialogItem;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_itemlist,parent,false);
        return new ItemHolder(v);
    }
    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        final Spacecraft spacecraft=spacecrafts.get(position);
        holder.vItemCode.setText(spacecraft.getItemCode());
        holder.vDescription.setText(spacecraft.getDescription());
        holder.vUnitPrice.setText(spacecraft.getUnitPrice() +"\n"+spacecraft.getPoint());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String vQty= String.valueOf(spacecraft.getId());
                final String vItemCode=spacecraft.getItemCode();
                final String vDescription=charReplace(spacecraft.getDescription());
                final String vPoint=spacecraft.getPoint();
                SetUOM vData=SetUOM.set(spacecraft.getUnitPrice(),spacecraft.getDefaultUOM(),spacecraft.getUOM(),
                        spacecraft.getUOM1(),spacecraft.getUOM2(),spacecraft.getUOM3(),
                        spacecraft.getUOM4(),spacecraft.getUOMFactor1(),spacecraft.getUOMFactor2(),
                        spacecraft.getUOMFactor3(),spacecraft.getUOMFactor4(),spacecraft.getUOMPrice1(),
                        spacecraft.getUOMPrice2(),spacecraft.getUOMPrice3(),spacecraft.getUOMPrice4());
                final String vUOM=vData.getUOM();
                final String vUnitPrice=vData.getUnitPrice();
                final String vFactorQty=vData.getFactorQty();
                ((PointRedeem)c).setItemRedeem(vItemCode,vDescription,vUnitPrice,vUOM,vFactorQty,vPoint);
                dialogItem.dismiss();
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

    private String charReplace(String text){
        String newText=text.replaceAll("[\\.$|,|;|']","");
        return newText;
    }
}
