package skybiz.com.posoffline.ui_CashReceipt.m_Modifier;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Modifier;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.Dialog_Qty;

/**
 * Created by 7 on 27/10/2017.
 */

public class ModifierLAdapter extends RecyclerView.Adapter<ModifierLHolder> {
    Context c;
    ArrayList<Spacecraft_Modifier> spacecrafts;
    DialogModifier dialogModifier;

    public ModifierLAdapter(Context c, ArrayList<Spacecraft_Modifier> spacecrafts, DialogModifier dialogModifier) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogModifier=dialogModifier;
    }

    @Override
    public ModifierLHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_modifier2,parent,false);
        return new ModifierLHolder(v);
    }
    @Override
    public void onBindViewHolder(final ModifierLHolder holder, int position) {
        final Spacecraft_Modifier spacecraft=spacecrafts.get(position);
        holder.vModifier.setText(spacecraft.getModifier());
        holder.vModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String vModifier=spacecraft.getModifier();
                dialogModifier.setModifier(vModifier);
            }
        });
        holder.chkYN.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dialogModifier.addModifier(spacecraft.getModifier());
                }else{
                    dialogModifier.minModifier(spacecraft.getModifier());
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
