package skybiz.com.posoffline.m_NewSummary;

import android.content.Context;

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
