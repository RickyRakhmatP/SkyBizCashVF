package skybiz.com.posoffline.ui_CashReceipt.m_Order;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 22/12/2017.
 */

public class DelItem {
    Context context;
    String RunNo;

    public DelItem(Context context, String RunNo) {
        this.context = context;
        this.RunNo = RunNo;
    }

    public void delitem(){
        DBAdapter db = new DBAdapter(context);
        db.openDB();
        String Delete="delete from cloud_cus_inv_dt where RunNo='"+RunNo+"' ";
        long del=db.addQuery(Delete);
        if(del>0){
        }
        db.closeDB();
    }

}
