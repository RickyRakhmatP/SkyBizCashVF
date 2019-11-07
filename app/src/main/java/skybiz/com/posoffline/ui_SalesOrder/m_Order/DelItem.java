package skybiz.com.posoffline.ui_SalesOrder.m_Order;

import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;

import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 22/12/2017.
 */

public class DelItem {
    Context context;
    String ItemCode,deviceId,RunNo;
    Double dQty;
    TelephonyManager telephonyManager;
    public DelItem(Context context, String itemCode) {
        this.context = context;
        ItemCode = itemCode;
    }

    public void delitem(){
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = telephonyManager.getDeviceId();
        DBAdapter db = new DBAdapter(context);
        dQty=0.00;
        db.openDB();
        String vCheck="select RunNo,Qty,HCUnitCost from cloud_sales_order_dt where ItemCode='" + ItemCode + "' and ComputerName='" + deviceId + "' ";
        Cursor curCheck = db.getQuery(vCheck);
        while (curCheck.moveToNext()) {
            RunNo      =curCheck.getString(0);
        }
        String vDelete="delete from cloud_sales_order_dt where RunNo='"+RunNo+"'  ";
        long delete=db.addQuery(vDelete);
        if(delete>0){

        }
        db.closeDB();
    }

}
