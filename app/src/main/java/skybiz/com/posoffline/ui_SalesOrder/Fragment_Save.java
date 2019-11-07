package skybiz.com.posoffline.ui_SalesOrder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_PayType;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.PayTypeAdapter;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.BaseApp;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_IposAIDL.Utils.IposAidlUtil;
import skybiz.com.posoffline.ui_SalesOrder.m_Save.CheckPaidAmt;
import skybiz.com.posoffline.ui_SalesOrder.m_Save.SaveSO;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 15/01/2018.
 */

public class Fragment_Save extends Fragment {
    View view;
    TextView txtAmountDue,txtDoc1No,txtTotalAmt,txtHCGbDiscount;
    LinearLayout lnCash,lnCredit;
    Button btnPay;
    String IPAddress,Password,DBName,UserName,isTotal,totalamt,Doc1No;
    String PaidAmount,ChangeAmount,NewDoc,CC1No,vBalance,vCC1Amt,CC1Code;
    String TypePrinter,NamePrinter,IPPrinter,UUID;
    Double CashAmt,ChangeAmt,CC1Amt,BalanceAmt;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView lv;
    ArrayList<Spacecraft_PayType> paytypes=new ArrayList<>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmentso_save, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipePayment);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPayment();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        txtDoc1No= (TextView) view.findViewById(R.id.txtDoc1No);
        txtAmountDue= (TextView) view.findViewById(R.id.txtAmountDue);
        txtTotalAmt = (TextView) view.findViewById(R.id.txtTotal);
        txtHCGbDiscount = (TextView) view.findViewById(R.id.txtHCGbDiscount);
        btnPay=(Button)view.findViewById(R.id.btnPay);
        lnCash= (LinearLayout) view.findViewById(R.id.ln_cash);
        //lnCredit= (LinearLayout) view.findViewById(R.id.ln_credit);
        DBAdapter db = new DBAdapter(getContext());
        db.openDB();
        Cursor c=db.getAllSeting();
        while (c.moveToNext()) {
            int RunNo=c.getInt(0);
            Log.d("RESULT", ""+RunNo);
            IPAddress = c.getString(1);
            UserName = c.getString(2);
            Password = c.getString(3);
            DBName = c.getString(4);
        }
        Cursor cPrinter=db.getSettingPrint();
        while (cPrinter.moveToNext()) {
            TypePrinter = cPrinter.getString(1);
            NamePrinter = cPrinter.getString(2);
            IPPrinter = cPrinter.getString(3);
            UUID = cPrinter.getString(4);
        }
        db.closeDB();
        // Toast.makeText(getActivity(), "Type Printer "+TypePrinter, Toast.LENGTH_LONG).show();
        if(TypePrinter.equals("AIDL")){
            AidlUtil.getInstance().connectPrinterService(getActivity());
            AidlUtil.getInstance().initPrinter();
        }else if(TypePrinter.equals("Ipos AIDL")){
            IposAidlUtil.getInstance().connectPrinterService(getActivity());
            //IposAidlUtil.getInstance().initPrinter();
        }else{

        }
        //btn pay cash
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPay.setEnabled(false);
                PaidAmount= (String) txtTotalAmt.getText();
                PaidAmount=PaidAmount.replaceAll(",","");
                if(PaidAmount.equals("0.00") || PaidAmount.length()==0){
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Total Amount Cannot Empty");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    btnPay.setEnabled(true);
                }else{
                    SaveSO fnsave=new SaveSO(getActivity(),Doc1No);
                    fnsave.execute();
                   /* if(!NewDoc.equals("error")){
                        totalamt="0.00";
                        Intent mainIntent = new Intent(getActivity(), SalesOrder.class);
                        startActivity(mainIntent);
                    } else{
                        btnPay.setEnabled(true);
                    }*/
                    btnPay.setEnabled(true);
                    txtDoc1No.setText("");
                }
            }
        });

        return view;
    }


    private void fnchecktotal(){
        String TotalAmt="";
        String TotalDiscount="";
        DBAdapter db = new DBAdapter(getContext());
        db.openDB();
        String vQuery="select sum(HCLineAmt + HCDiscount) as TotalAmt, sum(HCDiscount) as TotalDiscount from cloud_sales_order_dt ";
        Cursor cRunNo = db.getQuery(vQuery);
        while (cRunNo.moveToNext()) {
            Double dTotalAmt        = cRunNo.getDouble(0);
            Double dTotalDiscount   = cRunNo.getDouble(1);
            TotalAmt=String.format(Locale.US, "%,.2f", dTotalAmt);
            TotalDiscount=String.format(Locale.US, "%,.2f", dTotalDiscount);
        }
        txtTotalAmt.setText(TotalAmt);
        txtHCGbDiscount.setText(TotalDiscount);
    }
    private void retLastNo(){
        String Prefix="";
        String LastNo="";
        DBAdapter db = new DBAdapter(getContext());
        db.openDB();
        String vQuery="select Prefix,LastNo from sys_runno_dt where RunNoCode='SO' ";
        Cursor cRunNo = db.getQuery(vQuery);
        while (cRunNo.moveToNext()) {
            Prefix= cRunNo.getString(0);
            LastNo= cRunNo.getString(1);
        }
        Doc1No=Prefix+LastNo;
        txtDoc1No.setText(Doc1No);
        db.closeDB();
    }

    public void refreshPayment() {
        retLastNo();
        CheckPaidAmt chekpaid=new CheckPaidAmt(getActivity());
        fnchecktotal();
        isTotal=chekpaid.fncheckpaid();
        if(!isTotal.equals("error")){
            totalamt=isTotal;
        }else{
            totalamt="0.00";
        }
        txtAmountDue.setText(totalamt);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Fragment_Save.this.refreshPayment();
            InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            mImm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

}
