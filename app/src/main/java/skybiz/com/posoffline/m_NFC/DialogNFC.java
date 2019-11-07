package skybiz.com.posoffline.m_NFC;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ipay.Ipay;
import com.ipay.IpayPayment;
import com.ipay.IpayResultDelegate;

import java.io.Serializable;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Ipay88.AeSimpleSHA1;
import skybiz.com.posoffline.m_Ipay88.Merchant.MerchantScan2;
import skybiz.com.posoffline.m_Ipay88.SendVoidSOAP;
import skybiz.com.posoffline.m_Ipay88.User.UserScan;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 23/02/2018.
 */

public class DialogNFC extends DialogFragment {
    View view;
    EditText txtAmount;
    String Amount,Doc1No,Doc2No,MerchantCode,MerchantKey,PaymentID,PayType;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_nfc, container, false);

        txtAmount=(EditText)view.findViewById(R.id.txtAmount);
        Amount=this.getArguments().getString("AMOUNT_KEY");
        Doc1No=this.getArguments().getString("DOC1NO_KEY");
        Doc2No=this.getArguments().getString("DOC2NO_KEY");
        PayType=this.getArguments().getString("PAYTYPE_KEY");
        MerchantKey=this.getArguments().getString("MERCHANTKEY_KEY");
        txtAmount.setText(Amount);
        txtAmount.setEnabled(false);
        return view;
    }
}
