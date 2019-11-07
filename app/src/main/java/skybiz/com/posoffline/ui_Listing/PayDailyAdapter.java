package skybiz.com.posoffline.ui_Listing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewObject.Spacecraft_PayDaily;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_PayType;

/**
 * Created by 7 on 07/12/2017.
 */

public class PayDailyAdapter extends BaseAdapter {
    Context c;
    ArrayList<Spacecraft_PayDaily> spacecrafts;
    LayoutInflater inflater;

    public PayDailyAdapter(Context c, ArrayList<Spacecraft_PayDaily> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;

        //INITIALIE
        inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return spacecrafts.size();
    }

    @Override
    public Object getItem(int position) {
        return spacecrafts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return spacecrafts.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView=inflater.inflate(R.layout.model_paydaily,parent,false);
        }
        TextView txtCC1Code= (TextView) convertView.findViewById(R.id.vCC1Code);
        TextView txtCC1Amt= (TextView) convertView.findViewById(R.id.vCC1Amt);
        txtCC1Code.setText(spacecrafts.get(position).getCC1Code());
        txtCC1Amt.setText(spacecrafts.get(position).getCC1Amt());
        return convertView;
    }
}
