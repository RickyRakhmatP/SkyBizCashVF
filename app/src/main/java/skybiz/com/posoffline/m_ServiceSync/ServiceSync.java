package skybiz.com.posoffline.m_ServiceSync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import skybiz.com.posoffline.m_ServiceSync.m_SyncOut.SyncOUT_Detail;
import skybiz.com.posoffline.m_ServiceSync.m_SyncOut.SyncOUT_Detail2;
import skybiz.com.posoffline.m_ServiceSync.m_SyncOut.SyncOUT_DetailSO;
import skybiz.com.posoffline.m_ServiceSync.m_SyncOut.SyncOUT_Hd;
import skybiz.com.posoffline.m_ServiceSync.m_SyncOut.SyncOUT_HdSO;
import skybiz.com.posoffline.m_ServiceSync.m_SyncOut.SyncOUT_Receipt2;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class ServiceSync extends Service {

    String isDetail="",isDetail2="";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Query the database and show alarm if it applies

        DBAdapter db=new DBAdapter(this);
        db.openDB();
        String query="select AutoSyncYN from tb_setting ";
        Cursor rsSync=db.getQuery(query);
        String AutoSyncYN="0";
        while(rsSync.moveToNext()) {
            AutoSyncYN=rsSync.getString(0);
        }
        db.closeDB();
        if(AutoSyncYN.equals("1")){
            syncdetail();
            synheader();
        }
        // I don't want this service to stay in memory, so I stop it
        // immediately after doing what I wanted it to do.
       // Log.d("SERVICE","running");
        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // I want to restart this service again in one hour
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (59000*60),
                PendingIntent.getService(this, 0, new Intent(this, ServiceSync.class), 0)
        );
    }

//59000*2
    public void synheader(){
        SyncOUT_Hd synchd=new SyncOUT_Hd(this);
        synchd.execute();

        SyncOUT_Receipt2 syncreceipt2 = new SyncOUT_Receipt2(this);
        syncreceipt2.execute();

        SyncOUT_HdSO synchdso=new SyncOUT_HdSO(this);
        synchdso.execute();

    }
    public void syncdetail(){
        SyncOUT_Detail syncdetail=new SyncOUT_Detail(this);
        syncdetail.execute();

        SyncOUT_Detail2 syncdetail2=new SyncOUT_Detail2(this);
        syncdetail2.execute();

        SyncOUT_DetailSO syncdetailso=new SyncOUT_DetailSO(this);
        syncdetailso.execute();

        /*SyncOUT_Detail syncdetail=new SyncOUT_Detail(this);
        try {
            isDetail= syncdetail.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/
    }

    public void syncdetail2(){
        SyncOUT_Detail2 syncdetail2=new SyncOUT_Detail2(this);
        try {
            isDetail2= syncdetail2.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
