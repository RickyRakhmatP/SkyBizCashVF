package skybiz.com.posoffline.m_PriceMatrix;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewObject.Spacecraft_PriceMatrix;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.Dialog_Qty;

/**
 * Created by 7 on 14/11/2017.
 */

public class PriceMatrixAdapter extends RecyclerView.Adapter<PriceMatrixHolder> {
    Context c;
    ArrayList<Spacecraft_PriceMatrix> spacecrafts;
    Dialog_Qty dialog_qty;

    RecyclerView rv;
    public PriceMatrixAdapter(Context c, ArrayList<Spacecraft_PriceMatrix> spacecrafts, Dialog_Qty dialog_qty) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialog_qty=dialog_qty;

    }

    @Override
    public PriceMatrixHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_pricematrix,parent,false);
        return new PriceMatrixHolder(v);
    }

    @Override
    public void onBindViewHolder(PriceMatrixHolder holder, int position) {
        final Spacecraft_PriceMatrix spacecraft=spacecrafts.get(position);
        holder.vPct.setText(spacecraft.getPct());
        holder.vDescription.setText(spacecraft.getDescription());
        holder.vCriteria.setText(spacecraft.getCriteria());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String vPct               =spacecraft.getPct();
                final String vB_ase             =spacecraft.getB_ase();
                final String vCriteria          =spacecraft.getCriteria();
                final String vServiceChargeYN   =spacecraft.getServiceChargeYN();
                //Toast.makeText(c, "RESULT" +vPct +vB_ase+vCriteria, Toast.LENGTH_SHORT).show();
                dialog_qty.setPriceMatrix(vPct, vB_ase ,vCriteria, vServiceChargeYN);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }
    private String convertChar(String txt){
        String newText="";
        try{
            byte[] b=txt.getBytes("ISO-8859-1");
            newText=new String(b);
            return newText;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return newText;
    }
}
