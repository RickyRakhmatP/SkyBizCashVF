package skybiz.com.posoffline.ui_Sync;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.concurrent.ExecutionException;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_Setting.m_Sync.Sync_Customer;
import skybiz.com.posoffline.ui_Setting.m_Sync.Sync_Group;
import skybiz.com.posoffline.ui_Setting.m_Sync.Sync_Item;
import skybiz.com.posoffline.ui_Setting.m_Sync.Sync_Misc;
import skybiz.com.posoffline.ui_Setting.m_Sync.Sync_PaymentType;
import skybiz.com.posoffline.ui_Setting.m_Sync.Sync_SysGeneralSetup;
import skybiz.com.posoffline.ui_Setting.m_Sync.Sync_Tax;

/**
 * Created by 7 on 29/12/2017.
 */

public class Fragment_SyncIN extends Fragment {
    View view;
    Button btnSyncIN;
    String synSuccess;
    ProgressBar bar1,barCustomer, barGroup, barGeneral;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sync_in, container, false);
        btnSyncIN=(Button)view.findViewById(R.id.btnSyncIN);
        bar1 = (ProgressBar) view.findViewById(R.id.barItem);
        barCustomer = (ProgressBar) view.findViewById(R.id.barCustomer);
        barGroup = (ProgressBar) view.findViewById(R.id.barGroup);
       // barPay = (ProgressBar) view.findViewById(R.id.barPay);
        // bar1.setVisibility(View.GONE);
        btnSyncIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSyncIN.setEnabled(false);
                // bar1.setVisibility(View.VISIBLE);
                Sync_Item sync_item=new Sync_Item(getActivity(),bar1);

                try {
                    synSuccess= sync_item.execute().get();
                    if(!synSuccess.equals("error")){
                        //bar1.setProgress(100);
                        synSuccess="1";
                    }else{
                        synSuccess="0";
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if(synSuccess.equals("1")){
                    //sync taxt
                  /*  Sync_Tax sync_tax=new Sync_Tax(getActivity(),barTax);
                    sync_tax.execute();

                    //sync general setup
                    Sync_SysGeneralSetup sysgeneralsetup=new Sync_SysGeneralSetup(getActivity(),barGeneral);
                    sysgeneralsetup.execute();

                    //sync paymentype
                    Sync_PaymentType paymentType=new Sync_PaymentType(getActivity(),barPay);
                    paymentType.execute();*/

                    //sync customer
                    Sync_Customer sync_customer=new Sync_Customer(getActivity(),barCustomer);
                    sync_customer.execute();

                    //sync customer
                    Sync_Group sync_group=new Sync_Group(getActivity(),barGroup);
                    sync_group.execute();

                    Sync_Misc sync_misc=new Sync_Misc(getActivity(),bar1);
                    sync_misc.execute();

                    btnSyncIN.setEnabled(true);

                }

            }
        });
        return view;
    }
}
