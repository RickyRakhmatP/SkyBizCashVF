package skybiz.com.posoffline.m_ServiceSync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStartOut extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, ServiceSync.class));
    }
}
