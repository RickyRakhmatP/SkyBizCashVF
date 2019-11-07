package skybiz.com.posoffline.ui_CashReceipt.m_Pay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_PayType;

/**
 * Created by 7 on 07/12/2017.
 */

public class PayTypeAdapter extends BaseAdapter {
    Context c;
    ArrayList<Spacecraft_PayType> spacecrafts;
    LayoutInflater inflater;

    public PayTypeAdapter(Context c, ArrayList<Spacecraft_PayType> spacecrafts) {
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
        return spacecrafts.get(position).getRunNo();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView=inflater.inflate(R.layout.model_paytype,parent,false);
        }
        TextView txtCC1Code= (TextView) convertView.findViewById(R.id.txtPaymentCode);
        txtCC1Code.setText(spacecrafts.get(position).getPaymentCode());
        //descTxt.setText(spacecrafts.get(position).getDescription());
        //ITEM CLICKS
        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c,spacecrafts.get(position).getDoc1No(),Toast.LENGTH_SHORT).show();

            }
        });*/
        return convertView;
    }
}
