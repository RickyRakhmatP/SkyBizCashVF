package skybiz.com.posoffline.ui_Member.m_MemberList.m_Customer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewCustomer.DialogCustomer;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Customer;
import skybiz.com.posoffline.ui_Member.m_MemberList.MemberList;

/**
 * Created by 7 on 27/10/2017.
 */

public class CustomerAdapter extends RecyclerView.Adapter<CustomerHolder> {
    Context c;
    ArrayList<Spacecraft_Customer> spacecrafts;


    public CustomerAdapter(Context c, ArrayList<Spacecraft_Customer> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }

    @Override
    public CustomerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_customerlisting,parent,false);
        return new CustomerHolder(v);
    }

    @Override
    public void onBindViewHolder(final CustomerHolder holder, int position) {
        final Spacecraft_Customer spacecraft=spacecrafts.get(position);
        holder.vCusCode.setText(spacecraft.getCusCode());
        holder.vCusName.setText(spacecraft.getCusName());
        holder.vSalesPersonCode.setText(spacecraft.getCategoryCode());
        holder.vDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MemberList)c).setDelete(spacecraft.getCusCode());
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
