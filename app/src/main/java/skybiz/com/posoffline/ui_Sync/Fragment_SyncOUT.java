package skybiz.com.posoffline.ui_Sync;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.concurrent.ExecutionException;

import skybiz.com.posoffline.MyBounceInterpolator;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_Sync.m_SyncOUT.SyncOUT_Detail;
import skybiz.com.posoffline.ui_Sync.m_SyncOUT.SyncOUT_Detail2;
import skybiz.com.posoffline.ui_Sync.m_SyncOUT.SyncOUT_Hd;
import skybiz.com.posoffline.ui_Sync.m_SyncOUT.SyncOUT_Receipt2;
import skybiz.com.posoffline.ui_Sync.m_SyncOUT.SyncOut;
import skybiz.com.posoffline.ui_Sync.m_SyncOUT_SO.SyncOUT_DetailSO;
import skybiz.com.posoffline.ui_Sync.m_SyncOUT_SO.SyncOUT_HdSO;

/**
 * Created by 7 on 29/12/2017.
 */

public class Fragment_SyncOUT extends Fragment {
    View view;
    Button btnSyncOUT;
    Boolean isExecute;
    String isDetail,isDetail2,isHd,isReceipt;
    ProgressBar pbSO,pbCS;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sync_out, container, false);
        btnSyncOUT=(Button)view.findViewById(R.id.btnSyncOUT);
        pbSO = (ProgressBar) view.findViewById(R.id.pbSO);
        pbCS = (ProgressBar) view.findViewById(R.id.pbCS);
        btnSyncOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               fnsync();
                /*syncdetail();
                syncdetailso();
                if (isDetail.equals("success")){
                    syncdetail2();
                }

                synchd();
                if (isHd.equals("success")){
                    syncreceipt2();
                }*/
            }
        });

        return view;
    }

    private void fnsync(){
        didTapButton(btnSyncOUT);
        btnSyncOUT.setEnabled(false);
        syncCS();
        syncdetailso();
    }
    private void syncCS(){
        SyncOut syncOut=new SyncOut(getActivity(),pbCS);
        syncOut.execute();
    }

    public void syncdetailso(){
        SyncOUT_DetailSO syncdetailso=new SyncOUT_DetailSO(getActivity(),pbSO);
        syncdetailso.execute();
        SyncOUT_HdSO synchdso=new SyncOUT_HdSO(getActivity(),pbSO);
        synchdso.execute();
        btnSyncOUT.setEnabled(true);
    }
    public void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
    }

}
