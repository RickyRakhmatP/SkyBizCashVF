package skybiz.com.posoffline.m_NewMisc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewObject.SetDefaultTax;
import skybiz.com.posoffline.m_NewSummary.AddItem;
import skybiz.com.posoffline.m_NewSummary.EditItem;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;
/**
 * Created by 7 on 27/10/2017.
 */

public class MiscAdapter extends RecyclerView.Adapter<MiscHolder> {
    String DocType,Qty;
    Context c;
    ArrayList<Spacecraft> spacecrafts;
    int clickcount=0;

    public MiscAdapter(Context c,String DocType, ArrayList<Spacecraft> spacecrafts) {
        this.c = c;
        this.DocType=DocType;
        this.spacecrafts = spacecrafts;
    }

    @Override
    public MiscHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item,parent,false);
        return new MiscHolder(v);

    }

    @Override
    public void onBindViewHolder(final MiscHolder holder, int position) {

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
                final String vItemCode=spacecraft.getItemCode();
                final String vItemGroup=spacecraft.getItemGroup();
                final String vDescription=spacecraft.getDescription();
                final String vUOM=spacecraft.getUOM();
                final String vRetailTaxCode=spacecraft.getRetailTaxCode();
                final String vPurchaseTaxCode=spacecraft.getPurchaseTaxCode();
                final String vSalesTaxCode=spacecraft.getSalesTaxCode();
                final String vDetailTaxCode= SetDefaultTax.DetailTax(DocType,vRetailTaxCode,vPurchaseTaxCode,vSalesTaxCode);
                AddItem addItem =new AddItem(c,DocType,vItemCode,vDescription,vItemGroup,"1",UnitPrice,vUOM,vDetailTaxCode,"0","0","4","0");
                addItem.execute();
                holder.btnAdd.setText(String.valueOf(spacecraft.getId()));
            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                final int qty= spacecraft.getId();
                final String vUnitPrice=spacecraft.getUnitPrice();
                final String UnitPrice=vUnitPrice.replaceAll(",","");
                final String vItemCode=spacecraft.getItemCode();
                final String vUOM=spacecraft.getUOM();
                final String vRetailTaxCode=spacecraft.getRetailTaxCode();
                final String vPurchaseTaxCode=spacecraft.getPurchaseTaxCode();
                final String vSalesTaxCode=spacecraft.getSalesTaxCode();
                final String vDetailTaxCode=SetDefaultTax.DetailTax(DocType,vRetailTaxCode,vPurchaseTaxCode,vSalesTaxCode);
                if(qty!=0){
                    EditItem editItem=new EditItem(c,DocType,vItemCode,"-1",UnitPrice,vUOM,"1",vDetailTaxCode,"","","0");
                    editItem.execute();
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

   /* private String setDefaultTax(String DocType,String RetailTaxCode,String PurchaseTaxCode,String SalesTaxCode){
        String DefaultTax="";
        switch(DocType){
            case "SO"       : DefaultTax=SalesTaxCode; break;
            case "CS"       : DefaultTax=RetailTaxCode; break;
            case "CusCN"    : DefaultTax=RetailTaxCode; break;
        }
        return DefaultTax;
    }*/
}
