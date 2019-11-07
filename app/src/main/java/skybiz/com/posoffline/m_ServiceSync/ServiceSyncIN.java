package skybiz.com.posoffline.m_ServiceSync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;


import skybiz.com.posoffline.m_ServiceSync.m_SyncIN.Sync_Customer;
import skybiz.com.posoffline.m_ServiceSync.m_SyncIN.Sync_Group;
import skybiz.com.posoffline.m_ServiceSync.m_SyncIN.Sync_Item;
import skybiz.com.posoffline.m_ServiceSync.m_SyncIN.Sync_Misc;
import skybiz.com.posoffline.m_ServiceSync.m_SyncIN.Sync_PaymentType;
import skybiz.com.posoffline.m_ServiceSync.m_SyncIN.Sync_SysGeneralSetup;
import skybiz.com.posoffline.m_ServiceSync.m_SyncIN.Sync_Tax;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class ServiceSyncIN extends Service {

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
       // Log.d("QUERY SERVICE",query+"/"+AutoSyncYN);
        db.closeDB();
        if(AutoSyncYN.equals("1")){
            syncin();
        }
        // I don't want this service to stay in memory, so I stop it
        // immediately after doing what I wanted it to do.
       // Log.d("SERVICE 2","running");
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
                System.currentTimeMillis() + (59000*240),
                PendingIntent.getService(this, 0, new Intent(this, ServiceSyncIN.class), 0)
        );
    }

    public void syncin(){
        Sync_Item syncitem=new Sync_Item(this);
        syncitem.execute();

        Sync_Group syncgroup=new Sync_Group(this);
        syncgroup.execute();

        Sync_Customer sync_customer=new Sync_Customer(this);
        sync_customer.execute();

        Sync_Misc syncmisc=new Sync_Misc(this);
        syncmisc.execute();

        Sync_PaymentType sync_paymentType=new Sync_PaymentType(this);
        sync_paymentType.execute();

        Sync_SysGeneralSetup sync_sysGeneralSetup=new Sync_SysGeneralSetup(this);
        sync_sysGeneralSetup.execute();

        Sync_Tax syncTax=new Sync_Tax(this);
        syncTax.execute();

    }

}
