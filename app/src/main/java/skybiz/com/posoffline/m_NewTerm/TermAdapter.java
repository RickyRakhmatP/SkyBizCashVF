package skybiz.com.posoffline.m_NewTerm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Service;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Term;

/**
 * Created by 7 on 27/10/2017.
 */

public class TermAdapter extends RecyclerView.Adapter<TermHolder> {
    Context c;
    ArrayList<Spacecraft_Term> spacecrafts;
    DialogTerm dialogTerm;

    public TermAdapter(Context c, ArrayList<Spacecraft_Term> spacecrafts, DialogTerm dialogTerm) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogTerm=dialogTerm;
    }

    @Override
    public TermHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_term,parent,false);
        return new TermHolder(v);
    }
    @Override
    public void onBindViewHolder(final TermHolder holder, int position) {
        final Spacecraft_Term spacecraft=spacecrafts.get(position);
        holder.vTermCode.setText(spacecraft.getTermCode());
        holder.vTermDesc.setText(spacecraft.getTermDesc());
        holder.vD_ay.setText(spacecraft.getD_ay());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String TermCode=spacecraft.getTermCode();
                final String TermDesc=spacecraft.getTermDesc();
                final String D_ay=spacecraft.getD_ay();
                dialogTerm.setTerm(TermCode,TermDesc,D_ay);
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
