package skybiz.com.posoffline.ui_Listing.m_Listing;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Trn;
import skybiz.com.posoffline.ui_Listing.Listing;


/**
 * Created by 7 on 27/10/2017.
 */

public class ListAdapter extends RecyclerView.Adapter<ListHolder> {
    String IPAddress,UserName,Password,DBName,Qty,DocType;
    Context c;
    ArrayList<Spacecraft_Trn> spacecrafts;
    int clickcount=0;

    public ListAdapter(Context c, String DocType, ArrayList<Spacecraft_Trn> spacecrafts) {
        this.c = c;
        this.DocType = DocType;
        this.spacecrafts = spacecrafts;
    }
    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_trn,parent,false);
        return new ListHolder(v);

    }
    @Override
    public void onBindViewHolder(final ListHolder holder, int position) {
        final Spacecraft_Trn spacecraft=spacecrafts.get(position);
        holder.vDoc1No.setText(spacecraft.getDoc1No());
        holder.vD_ate.setText(spacecraft.getD_ate());
        holder.vTotalAmt.setText(spacecraft.getHCNetAmt());
        holder.vQty.setText(spacecraft.getCusCode()+"\n"+spacecraft.getCusName());
        final String vStatus=spacecraft.getStatus();
        if(DocType.equals("CS") && vStatus.equals("Void")){
            holder.lnTrn.setBackgroundResource(R.color.colorRedLighten3);
            holder.vDoc1No.setTextColor(Color.WHITE);
            holder.vD_ate.setTextColor(Color.WHITE);
            holder.vTotalAmt.setTextColor(Color.WHITE);
            holder.vSyncYN.setTextColor(Color.WHITE);
            holder.vQty.setTextColor(Color.WHITE);
            holder.vDelete.setTextColor(Color.WHITE);
        }
        //holder.vCusName.setText(spacecraft.getCusName());
        final String SyncYN=spacecraft.getSynYN();
        if(SyncYN.equals("0")){
            holder.vSyncYN.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.sync_disabled, 0);
        }else{
            holder.vSyncYN.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sync_success, 0);
        }
        holder.vDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Listing)c).delItem(DocType,spacecraft.getDoc1No());
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }
}
