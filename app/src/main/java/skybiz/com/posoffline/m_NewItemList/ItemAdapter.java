package skybiz.com.posoffline.m_NewItemList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewObject.SetDefaultTax;
import skybiz.com.posoffline.m_NewSummary.AddItem;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;

/**
 * Created by 7 on 27/10/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
    Context c;
    ArrayList<Spacecraft> spacecrafts;
    DialogItem dialogItem;
    String DocType;

    public ItemAdapter(Context c, ArrayList<Spacecraft> spacecrafts,String DocType, DialogItem dialogItem) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.DocType=DocType;
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
        holder.vUnitPrice.setText(spacecraft.getUnitPrice());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String vQty= String.valueOf(spacecraft.getId());
                final String vUnitPrice=spacecraft.getUnitPrice();
                final String UnitPrice=vUnitPrice.replaceAll(",","");
                final String vItemCode=spacecraft.getItemCode();
                final String vDescription=charReplace(spacecraft.getDescription());
                final String vItemGroup=spacecraft.getItemGroup();
                final String vUOM=spacecraft.getUOM();
                final String vRetailTaxCode=spacecraft.getRetailTaxCode();
                final String vPurchaseTaxCode=spacecraft.getPurchaseTaxCode();
                final String vSalesTaxCode=spacecraft.getSalesTaxCode();
                final String vPoint=spacecraft.getPoint();
                final String vDetailTaxCode= SetDefaultTax.DetailTax(DocType,vRetailTaxCode,vPurchaseTaxCode,vSalesTaxCode);
                String vHCDiscount=spacecraft.getHCDiscount();
                String vDisRate1=spacecraft.getDisRate1();
                if(Double.parseDouble(vHCDiscount)>0 && Double.parseDouble(vDisRate1)>0){
                    vDisRate1="0.00";
                }else if(Double.parseDouble(vHCDiscount)>0 && Double.parseDouble(vDisRate1)==0){
                    vDisRate1="0.00";
                }else if(Double.parseDouble(vHCDiscount)==0 && Double.parseDouble(vDisRate1)>0){
                    vHCDiscount="0.00";
                }
                AddItem addItem =new AddItem(c,DocType,vItemCode,vDescription,
                        vItemGroup,"1",UnitPrice,
                        vUOM, vDetailTaxCode,vHCDiscount,
                        vDisRate1, "0",vPoint);
                addItem.execute();
                dialogItem.setItem();
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
