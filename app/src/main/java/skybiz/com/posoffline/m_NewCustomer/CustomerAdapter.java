package skybiz.com.posoffline.m_NewCustomer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Customer;

/**
 * Created by 7 on 27/10/2017.
 */

public class CustomerAdapter extends RecyclerView.Adapter<CustomerHolder> {
    Context c;
    ArrayList<Spacecraft_Customer> spacecrafts;
    DialogCustomer dialogCustomer;

    public CustomerAdapter(Context c, ArrayList<Spacecraft_Customer> spacecrafts,DialogCustomer dialogCustomer) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogCustomer=dialogCustomer;
    }

    @Override
    public CustomerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_customer,parent,false);
        return new CustomerHolder(v);
    }
    @Override
    public void onBindViewHolder(final CustomerHolder holder, int position) {
        final Spacecraft_Customer spacecraft=spacecrafts.get(position);
        holder.vCusCode.setText(spacecraft.getCusCode());
        holder.vCusName.setText(spacecraft.getCusName());
        //holder.vTermCode.setText(spacecraft.getTermCode());
       // holder.vD_ay.setText(spacecraft.getD_ay());
        holder.vSalesPersonCode.setText(spacecraft.getSalesPersonCode());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String CusCode=spacecraft.getCusCode();
                final String CusName=spacecraft.getCusName();
                final String TermCode=spacecraft.getTermCode();
                final String Address=spacecraft.getAddress();
                final String D_ay=spacecraft.getD_ay();
                final String TermDesc=spacecraft.getTermDesc();
                final String SalesPersonCode=spacecraft.getSalesPersonCode();
                dialogCustomer.setCustomer(CusCode,CusName,TermCode,D_ay,Address,TermDesc,SalesPersonCode );
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
