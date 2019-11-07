package skybiz.com.posoffline.ui_Service.m_Service;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Customer;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Service;

/**
 * Created by 7 on 27/10/2017.
 */

public class ServiceAdapter extends RecyclerView.Adapter<ServiceHolder> {
    Context c;
    ArrayList<Spacecraft_Service> spacecrafts;
    DialogService dialogService;

    public ServiceAdapter(Context c, ArrayList<Spacecraft_Service> spacecrafts, DialogService dialogService) {
        this.c = c;
        this.spacecrafts = spacecrafts;
        this.dialogService=dialogService;
    }

    @Override
    public ServiceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_service,parent,false);
        return new ServiceHolder(v);
    }
    @Override
    public void onBindViewHolder(final ServiceHolder holder, int position) {
        final Spacecraft_Service spacecraft=spacecrafts.get(position);
        holder.vParticular.setText(spacecraft.getParticular());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final String Particular=spacecraft.getParticular();
                dialogService.setService(Particular);
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }

}
