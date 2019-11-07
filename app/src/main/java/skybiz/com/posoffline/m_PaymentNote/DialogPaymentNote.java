package skybiz.com.posoffline.m_PaymentNote;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewCustomer.DownloaderCustomer;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CreditNote.CreditNote;
import skybiz.com.posoffline.ui_Member.m_PointLedger.HistoryPoint;
import skybiz.com.posoffline.ui_Member.m_PointRedemption.PointRedeem;
import skybiz.com.posoffline.ui_SalesOrder.SalesOrder;
import skybiz.com.posoffline.ui_Service.MService;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 17/01/2018.
 */

public class DialogPaymentNote extends DialogFragment {
    View view;
    EditText txtDoc2No,txtDoc3No;
    Button btnApply,btnBack;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view        = inflater.inflate(R.layout.dialog_paymentnote, container, false);
        txtDoc2No   =(EditText)view.findViewById(R.id.txtDoc2No);
        txtDoc3No   =(EditText)view.findViewById(R.id.txtDoc3No);
        btnApply    =(Button) view.findViewById(R.id.btnApply);
        btnBack     =(Button) view.findViewById(R.id.btnBack);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnapply();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        initData();
        return view;
    }
    private void initData(){
        try{
            DBAdapter db=new DBAdapter(getActivity());
            db.openDB();
            String sql="select Doc2No, Doc3No from cloud_cus_inv_hd";
            Cursor rsData=db.getQuery(sql);
            while(rsData.moveToNext()){
                txtDoc2No.setText(rsData.getString(0));
                txtDoc3No.setText(rsData.getString(1));
            }
            rsData.close();
            db.closeDB();

        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private void fnapply(){
        try{
            String Doc2No=txtDoc2No.getText().toString();
            String Doc3No=txtDoc3No.getText().toString();
            DBAdapter db=new DBAdapter(getActivity());
            db.openDB();
            String qCheck="select count(*)as numrows from cloud_cus_inv_hd";
            Cursor rsCheck=db.getQuery(qCheck);
            int numrows=0;
            while(rsCheck.moveToNext()){
                numrows=rsCheck.getInt(0);
            }
            rsCheck.close();
            if(numrows==0){
                String qInsert="insert into cloud_cus_inv_hd(Doc2No,Doc3No)values('"+Doc2No+"', '"+Doc3No+"')";
                db.addQuery(qInsert);
            }else{
                String qUpadate="update cloud_cus_inv_hd set Doc2No='"+Doc2No+"', Doc3No='"+Doc3No+"' ";
                db.addQuery(qUpadate);
            }
            Toast.makeText(getActivity(),"Payment Note Has been Changed", Toast.LENGTH_SHORT).show();
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

}
