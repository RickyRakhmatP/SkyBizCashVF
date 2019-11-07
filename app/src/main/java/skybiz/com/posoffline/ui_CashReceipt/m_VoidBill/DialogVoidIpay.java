package skybiz.com.posoffline.ui_CashReceipt.m_VoidBill;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import skybiz.com.posoffline.KeyboardNew;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Ipay88.AeSimpleSHA1;
import skybiz.com.posoffline.m_Ipay88.SendVoidSOAP;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 23/02/2018.
 */

public class DialogVoidIpay extends DialogFragment {
    View view;
    Button btnOK;
    EditText txtTransId;
    KeyboardNew keyboarnew;
    String Doc1No,PaymentCode,Amount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_voidipay88, container, false);
        btnOK=(Button)view.findViewById(R.id.btnOK);
        txtTransId=(EditText)view.findViewById(R.id.txtTransId);
        Doc1No=this.getArguments().getString("REFNO_KEY");
        PaymentCode=this.getArguments().getString("PAYTYPE_KEY");
        Amount=this.getArguments().getString("AMOUNT_KEY");
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnOK();
            }
        });
        keyboarnew=(KeyboardNew) view.findViewById(R.id.keyboardnew);

        txtTransId.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtTransId.setInputType(0);
                txtTransId.setRawInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                InputConnection ic2=txtTransId.onCreateInputConnection(new EditorInfo());
                keyboarnew.setInputConnection(ic2);
                return false;
            }
        });

        txtTransId.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)){
                    String ItemCode=txtTransId.getText().toString();
                    if(!ItemCode.isEmpty()){
                        fnOK();
                    }else{
                        txtTransId.requestFocus();
                    }
                }
                return false;
            }
        });

        initView();
        return view;
    }
    private  void initView(){
        InputConnection ic=txtTransId.onCreateInputConnection(new EditorInfo());
        keyboarnew.setInputConnection(ic);
        txtTransId.setTextIsSelectable(true);
        txtTransId.setInputType(0);
        //txtTransId.setRawInputType(InputType.TYPE_CLASS_TEXT |
           //     InputType.TYPE_TEXT_VARIATION_PASSWORD);
       // txtPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());

    }
    private void fnOK(){
        try{
            String MerchantCode=""
                    ,MerchantKey="";
            String TransId=txtTransId.getText().toString();
            DBAdapter db=new DBAdapter(getActivity());
            db.openDB();
            String qPay= "select MerchantCode,MerchantKey from ret_paymenttype where PaymentCode='"+PaymentCode+"' ";
            Cursor rsPay=db.getQuery(qPay);
            while(rsPay.moveToNext()){
                MerchantCode=rsPay.getString(0);
                MerchantKey=rsPay.getString(1);
            }
            if(TransId.isEmpty()){
                Toast.makeText(getActivity(),"Transaction ID cannot empty ", Toast.LENGTH_SHORT).show();
            }else{
                Double amt = (Double.parseDouble(Amount)) * 100;
                String newAmt = String.format(Locale.US, "%.0f", amt);
                String signature = AeSimpleSHA1.encrypt(MerchantKey+MerchantCode+TransId+newAmt+"MYR");
                Log.d("SIGNATURE",signature);
                SendVoidSOAP sendSOAP=new SendVoidSOAP(getActivity(),MerchantCode,Doc1No,
                        Amount,"MYR",signature);
                try {
                    String vResult=  sendSOAP.execute().get();
                    if(!vResult.equals("error")){

                    }else{
                        VoidBill voidBill = new VoidBill(getActivity(), Doc1No);
                        voidBill.execute();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            dismiss();
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }


}
