package skybiz.com.posoffline.ui_CashReceipt.m_Order;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import skybiz.com.posoffline.DialogShowImage;
import skybiz.com.posoffline.Dialog_SpvPass;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Order;


/**
 * Created by 7 on 13/11/2017.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderHolder> {
    Context c;
    ArrayList<Spacecraft_Order> spacecrafts;
    FragmentManager fm;
    int row_index;

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
        final Double dQty           =Double.parseDouble(spacecraft.getBtnQty());
        final String Qty            =String.format(Locale.US, "%,.2f",dQty);
        final Double dUnitPrice     =Double.parseDouble(spacecraft.getHCUnitCost());
        final String UnitPrice      =String.format(Locale.US, "%,.2f",dUnitPrice);
        final Double dHCDiscount    =Double.parseDouble(spacecraft.getHCDiscount());
        final String HCDiscount     =String.format(Locale.US,"%,.2f",dHCDiscount);
        final String UOM            =spacecraft.getUOM();
        final String FactorQty      =spacecraft.getFactorQty();
        final String BlankLine      =spacecraft.getBlankLine();
        final String DetailTaxCode  =spacecraft.getDetailTaxCode();
        final int RunNo             =spacecraft.getRunNo();
        final String ItemCode       =spacecraft.getItemCode();
        final String ItemGroup      =spacecraft.getItemGroup();
        final String Description    =spacecraft.getDescription();
        final String DisRate1       =spacecraft.getDisRate1();
        final String Description2   =spacecraft.getDescription2();
        final String UnitCost       =spacecraft.getUnitCost();
        final String MSP            =spacecraft.getMSP();
        final String AnalysisCode2  =spacecraft.getAnalysisCode2();
        final String HCUnitCost     =spacecraft.getHCUnitCost();
        final String ServiceChargeYN     =spacecraft.getServiceChargeYN();
        final JSONArray results = new JSONArray();
        holder.Descriptiontxt.setText(spacecraft.getDescription());
        holder.txtQty.setText(Qty);
        if(HCDiscount.equals("0.00")){
            holder.txtUnitPrice.setText(UnitPrice);
        }else {
            holder.txtUnitPrice.setText(UnitPrice+"\n ("+HCDiscount+")");
        }

        holder.Descriptiontxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index=spacecraft.getId();
                notifyDataSetChanged();
                try{
                    JSONObject row = new JSONObject();
                    row.put("ModifierYN", "1");
                    row.put("Qty",  Qty);
                    row.put("RunNo", RunNo);
                    row.put("ItemCode", ItemCode);
                    row.put("ItemGroup", ItemGroup);
                    row.put("Description",Description);
                    row.put("HCUnitCost",HCUnitCost);
                    row.put("HCDiscount", HCDiscount);
                    row.put("DisRate1",  DisRate1);
                    row.put("HCTax",  "");
                    row.put("DetailTaxCode", DetailTaxCode);
                    row.put("AnalysisCode2", AnalysisCode2);
                    row.put("Description2",Description2);
                    row.put("UOM",UOM);
                    row.put("FactorQty", FactorQty);
                    row.put("AlternateItem", "");
                    row.put("BlankLine", BlankLine);
                    // row.put("DUD6", DUD6);
                    row.put("MSP", MSP);
                    row.put("UnitCost", UnitCost);
                    row.put("ServiceChargeYN", ServiceChargeYN);
                    results.put(row);
                }catch (JSONException e){
                    e.printStackTrace();
                }

                if(AnalysisCode2.equals("0")){
                    showEditOder(results.toString());
                }else{
                    editAddDish(results.toString());
                }

               /* final String ItemCode=spacecraft.getItemCode();
                openDialogFragment(RunNo,ItemCode,ItemCode+" - "+spacecraft.getDescription(),spacecraft.getBtnQty(),
                        spacecraft.getHCUnitCost(),spacecraft.getHCDiscount(),spacecraft.getDisRate1(),spacecraft.getItemGroup(),
                        "1", spacecraft.getDescription2(),UOM,FactorQty,BlankLine,DetailTaxCode);*/
            }
        });
        holder.txtQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index=spacecraft.getId();
                notifyDataSetChanged();
                try{

                    JSONObject row = new JSONObject();
                    row.put("ModifierYN", "0");
                    row.put("Qty",  Qty);
                    row.put("RunNo", RunNo);
                    row.put("ItemCode", ItemCode);
                    row.put("ItemGroup", ItemGroup);
                    row.put("Description",Description);
                    row.put("HCUnitCost",HCUnitCost);
                    row.put("HCDiscount", HCDiscount);
                    row.put("DisRate1",  DisRate1);
                    row.put("HCTax",  "");
                    row.put("DetailTaxCode", DetailTaxCode);
                    row.put("AnalysisCode2", AnalysisCode2);
                    row.put("Description2",Description2);
                    row.put("UOM",UOM);
                    row.put("FactorQty", FactorQty);
                    row.put("AlternateItem", "");
                    row.put("BlankLine", BlankLine);
                    // row.put("DUD6", DUD6);
                    row.put("MSP", MSP);
                    row.put("UnitCost", UnitCost);
                    row.put("ServiceChargeYN", ServiceChargeYN);
                    results.put(row);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                if(AnalysisCode2.equals("0")){
                    showEditOder(results.toString());
                }else{
                    editAddDish(results.toString());
                }
                /*final String ItemCode=spacecraft.getItemCode();
                openDialogFragment(RunNo,ItemCode,ItemCode+" - "+spacecraft.getDescription(),spacecraft.getBtnQty(),
                        spacecraft.getHCUnitCost(),spacecraft.getHCDiscount(),spacecraft.getDisRate1(),spacecraft.getItemGroup(),
                        "0",spacecraft.getDescription2(),UOM,FactorQty,BlankLine,DetailTaxCode);*/
            }
        });
        holder.txtUnitPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index=spacecraft.getId();
                notifyDataSetChanged();
                try{
                    JSONObject row = new JSONObject();
                    row.put("ModifierYN", "0");
                    row.put("Qty",  Qty);
                    row.put("RunNo", RunNo);
                    row.put("ItemCode", ItemCode);
                    row.put("ItemGroup", ItemGroup);
                    row.put("Description",Description);
                    row.put("HCUnitCost",HCUnitCost);
                    row.put("HCDiscount", HCDiscount);
                    row.put("DisRate1",  DisRate1);
                    row.put("HCTax",  "");
                    row.put("DetailTaxCode", DetailTaxCode);
                    row.put("AnalysisCode2", AnalysisCode2);
                    row.put("Description2",Description2);
                    row.put("UOM",UOM);
                    row.put("FactorQty", FactorQty);
                    row.put("AlternateItem", "");
                    row.put("BlankLine", BlankLine);
                    // row.put("DUD6", DUD6);
                    row.put("MSP", MSP);
                    row.put("UnitCost", UnitCost);
                    row.put("ServiceChargeYN", ServiceChargeYN);
                    results.put(row);
                }catch (JSONException e){
                    e.printStackTrace();
                }
                if(AnalysisCode2.equals("0")){
                    showEditOder(results.toString());
                }else{
                    editAddDish(results.toString());
                }
                /*final String ItemCode=spacecraft.getItemCode();
                openDialogFragment(RunNo,ItemCode,ItemCode+" - "+spacecraft.getDescription(),spacecraft.getBtnQty(),
                        spacecraft.getHCUnitCost(),spacecraft.getHCDiscount(),spacecraft.getDisRate1(),spacecraft.getItemGroup(),
                        "0",spacecraft.getDescription2(),UOM,FactorQty,BlankLine,DetailTaxCode);*/
            }
        });
        holder.vImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index=spacecraft.getId();
                notifyDataSetChanged();
                showImage(spacecraft.getItemCode(),spacecraft.getDescription());
            }
        });
        if(row_index==position) {
            holder.lnOrder.setBackgroundResource(R.drawable.border_active);
        }else{
            holder.lnOrder.setBackgroundResource(R.drawable.border_sunmi);
        }
    }

    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

    private void showEditOder(String jsonData){
        Bundle b=new Bundle();
        b.putString("JSONDATA_KEY",jsonData);
        Dialog_Qty dialogQty=new Dialog_Qty();
        dialogQty.setArguments(b);
        dialogQty.show(fm,"mTag");
    }

    private void editAddDish(String jsonData){
        Bundle b=new Bundle();
        b.putString("TYPE_KEY","Edit Order");
        b.putString("RUNNO_KEY","");
        b.putString("JSONDATA_KEY",jsonData);
        Dialog_SpvPass dialog_spvPass=new Dialog_SpvPass();
        dialog_spvPass.setArguments(b);
        dialog_spvPass.show(fm,"mDelete");
    }

    private void showImage(String ItemCode, String Description){
        Bundle b=new Bundle();
        b.putString("FROM_KEY","CS");
        b.putString("ITEMCODE_KEY",ItemCode);
        b.putString("DESC_KEY",Description);
        DialogShowImage dialogShowImage=new DialogShowImage();
        dialogShowImage.setArguments(b);
        dialogShowImage.show(fm,"mShowImage");
    }

    private void openDialogFragment(int RunNo,String ItemCode,String Description,String Qty, String UnitPrice,
                                    String HCDiscount, String DisRate1, String ItemGroup,String ModifierYN,
                                    String Description2,String UOM, String FactorQty,String BlankLine,
                                    String DetailTaxCode){
        Bundle b=new Bundle();
        b.putString("RUNNO_KEY",String.valueOf(RunNo));
        b.putString("GROUP_KEY",ItemGroup);
        b.putString("CODE_KEY",ItemCode);
        b.putString("DESC_KEY",Description);
        b.putString("DESC2_KEY",Description2);
        b.putString("QTY_KEY",Qty);
        b.putString("UNITPRICE_KEY",UnitPrice);
        b.putString("DISAMT_KEY",HCDiscount);
        b.putString("DISRATE_KEY",DisRate1);
        b.putString("YN_KEY",ModifierYN);
        b.putString("UOM_KEY",UOM);
        b.putString("FACTORQTY_KEY",FactorQty);
        b.putString("BLANKLINE_KEY",BlankLine);
        b.putString("DETAILTAXCODE_KEY",DetailTaxCode);
        Dialog_Qty dialogQty=new Dialog_Qty();
        dialogQty.setArguments(b);
        dialogQty.show(fm,"mTag");
    }

}
