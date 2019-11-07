package skybiz.com.posoffline.ui_CashReceipt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ipay.Ipay;
import com.ipay.IpayPayment;
import com.ipay.IpayResultDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpTransportSE;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Locale;

import skybiz.com.posoffline.KeyboardPay;
import skybiz.com.posoffline.MyBounceInterpolator;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_Boost.DialogBoost;
import skybiz.com.posoffline.m_Ipay88.AeSimpleSHA1;
import skybiz.com.posoffline.m_Ipay88.DialogIpay88;
import skybiz.com.posoffline.m_Ipay88.DialogPrint;
import skybiz.com.posoffline.m_Ipay88.MySoap;
import skybiz.com.posoffline.m_Ipay88.SendVoidSOAP;
import skybiz.com.posoffline.m_NewObject.Rounding;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_PayType;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddMisc;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.DialogTable;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.PayTypeAdapter;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.SaveCS;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.BaseApp;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_IposAIDL.Utils.IposAidlUtil;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 11/12/2017.
 */

public class Fragment_Payment extends Fragment {

    View rootView;
    TextView txtAmountDue,txtDoc1No,txtChangeAmt,
            txtTotalAmt,txtTotalQty,txtCC1Code,
            txtTypePay,txtCurCode,tTotalQty,txtAdjAmt;
    Button btnPay,btnExact;
    String TypePrinter,NamePrinter,IPPrinter,
            UUID,MerchantCode,MerchantKey,
            CurCode,PayType,vPort="",PaperSize="",PrintYN="1";
    Double CashAmt,ChangeAmt,CC1Amt,BalanceAmt;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView lv;
    ArrayList<Spacecraft_PayType> paytypes=new ArrayList<>();
    EditText txtDoc2No,txtDoc3No,txtPaidAmount,txtCC1No,txtSalesPersonCode;
    Double dServiceCharges=0.00;
    KeyboardPay keyboardPay;
    InputConnection ic,ic2;
    static Context context;
    Boolean CCYN=false;
    ProgressBar pbSave;
    int iNumRows=0;
    boolean allowSave=false,DialogPrintYN=false;
    LinearLayout lnKeyboard;
    private Button mButton1,mButton2,mButton3,mButton4,mButton5,mButton6,mButton7,mButton8,mButton9,
            mButton0,mButtonDot,mButton10,mButton20,mButton50,mButton100;
    private LinearLayout lnDel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView        = inflater.inflate(R.layout.fragmentcs_payment, container, false);
        keyboardPay     = (KeyboardPay) rootView.findViewById(R.id.keyboardpay);
        txtPaidAmount   = (EditText)rootView.findViewById(R.id.txtPaidAmount);
        txtDoc1No       = (TextView) rootView.findViewById(R.id.txtDoc1No);
        txtAmountDue    = (TextView) rootView.findViewById(R.id.txtAmountDue);
        txtChangeAmt    = (TextView) rootView.findViewById(R.id.txtChange);
        txtTotalAmt     = (TextView) rootView.findViewById(R.id.txtTotal);
        txtTotalQty     = (TextView) rootView.findViewById(R.id.txtTotalQty);
        tTotalQty       = (TextView) rootView.findViewById(R.id.tTotalQty);
        txtTypePay      = (TextView) rootView.findViewById(R.id.txtTypePay);
        txtAdjAmt       = (TextView) rootView.findViewById(R.id.txtAdjAmt);
        txtCC1Code      = (TextView) rootView.findViewById(R.id.txtCC1Code);
        txtCurCode      = (TextView) rootView.findViewById(R.id.txtCurCode);
        txtDoc2No       = (EditText)rootView.findViewById(R.id.txtDoc2No);
        txtDoc3No       = (EditText)rootView.findViewById(R.id.txtDoc3No);
        txtCC1No        = (EditText) rootView.findViewById(R.id.txtCC1No);
        btnPay          = (Button)rootView.findViewById(R.id.btnPay);
        btnExact        = (Button)rootView.findViewById(R.id.btnExact);
        txtSalesPersonCode =(EditText) rootView.findViewById(R.id.txtSalesPersonCode);
        pbSave =(ProgressBar) rootView.findViewById(R.id.pbSave);
        lnKeyboard =(LinearLayout) rootView.findViewById(R.id.lnKeyboard);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipePayment);


        mButton1=(Button)rootView.findViewById(R.id.btn1);
        mButton2=(Button)rootView.findViewById(R.id.btn2);
        mButton3=(Button)rootView.findViewById(R.id.btn3);
        mButton4=(Button)rootView.findViewById(R.id.btn4);
        mButton5=(Button)rootView.findViewById(R.id.btn5);
        mButton6=(Button)rootView.findViewById(R.id.btn6);
        mButton7=(Button)rootView.findViewById(R.id.btn7);
        mButton8=(Button)rootView.findViewById(R.id.btn8);
        mButton9=(Button)rootView.findViewById(R.id.btn9);
        mButton0=(Button)rootView.findViewById(R.id.btn0);
        mButtonDot=(Button)rootView.findViewById(R.id.btnDot);
        lnDel=(LinearLayout) rootView.findViewById(R.id.lnDel);
        mButton10=(Button)rootView.findViewById(R.id.btn10);
        mButton20=(Button)rootView.findViewById(R.id.btn20);
        mButton50=(Button)rootView.findViewById(R.id.btn50);
        mButton100=(Button)rootView.findViewById(R.id.btn100);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
                initPaid();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        txtTypePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadretpayment();
            }
        });
        btnExact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                didTapButton(btnExact);
                payFull();
            }
        });

       txtPaidAmount.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void afterTextChanged(Editable editable) {

               /*if(PaidAmount.equals(".") && PaidAmount.length()<1){
                   PaidAmount="0.";
               }*/
               /*String PaidAmount=txtPaidAmount.getText().toString();
               if(PaidAmount.length()>0) {
                   String str       = editable.toString();
                   String lastchar  = str.substring(str.length() - 1);
                   if (PaidAmount.contains(".") && lastchar.equals(".")){
                       txtPaidAmount.setText("0");
                   }else{
                       txtPaidAmount.setText(PaidAmount);
                   }
                   PaidAmount = txtPaidAmount.getText().toString();
               }*/
               String PaidAmount=txtPaidAmount.getText().toString();
               if(CCYN==false) {
                   calChange(PaidAmount);
               }
           }
       });
       btnPay.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               didTapButton(btnPay);
               checkDialog();
               //saveCS();

           }
       });
           /* txtDoc1No= (TextView) rootView.findViewById(R.id.txtDoc1No);
        txtAmountDue= (TextView) rootView.findViewById(R.id.txtAmountDue);
        txtPaidAmount = (TextView) rootView.findViewById(R.id.txtPaidAmount);
        txtChangeAmt = (TextView) rootView.findViewById(R.id.txtChange);
        txtCC1Code = (TextView) rootView.findViewById(R.id.txtCC1Code);
        txtCurCode = (TextView) rootView.findViewById(R.id.txtCurCode);
        txtTotalAmt = (TextView) rootView.findViewById(R.id.txtTotal);
        txtHCGbDiscount = (TextView) rootView.findViewById(R.id.txtHCGbDiscount);
        lnCash= (LinearLayout) rootView.findViewById(R.id.ln_cash);
        lnCredit= (LinearLayout) rootView.findViewById(R.id.ln_credit);
        txtcardno = (TextView) rootView.findViewById(R.id.txtcardno);
        txtAmountDuec= (TextView) rootView.findViewById(R.id.txtAmountDuec);
        txtbalance=(TextView) rootView.findViewById(R.id.txtbalance);
        btnPrint= (Button) rootView.findViewById(R.id.btnPrint);
        btnFull= (Button) rootView.findViewById(R.id.btnFull);
        btn0= (Button) rootView.findViewById(R.id.btn0);
        btn1= (Button) rootView.findViewById(R.id.btn1);
        btn2= (Button) rootView.findViewById(R.id.btn2);
        btn3= (Button) rootView.findViewById(R.id.btn3);
        btn4= (Button) rootView.findViewById(R.id.btn4);
        btn5= (Button) rootView.findViewById(R.id.btn5);
        btn6= (Button) rootView.findViewById(R.id.btn6);
        btn7= (Button) rootView.findViewById(R.id.btn7);
        btn8= (Button) rootView.findViewById(R.id.btn8);
        btn9= (Button) rootView.findViewById(R.id.btn9);
        btn10= (Button) rootView.findViewById(R.id.btn10);
        btn20= (Button) rootView.findViewById(R.id.btn20);
        btn50= (Button) rootView.findViewById(R.id.btn50);
        btn100= (Button) rootView.findViewById(R.id.btn100);
        btndot= (Button) rootView.findViewById(R.id.btndot);
        btnDel= (Button) rootView.findViewById(R.id.btnDel);
        btnPay= (Button) rootView.findViewById(R.id.btnPay);
        btnCash= (Button) rootView.findViewById(R.id.btn_cash);
        btnCredit= (Button) rootView.findViewById(R.id.btn_credit);

        btnc0= (Button) rootView.findViewById(R.id.btnc0);
        btnc1= (Button) rootView.findViewById(R.id.btnc1);
        btnc2= (Button) rootView.findViewById(R.id.btnc2);
        btnc3= (Button) rootView.findViewById(R.id.btnc3);
        btnc4= (Button) rootView.findViewById(R.id.btnc4);
        btnc5= (Button) rootView.findViewById(R.id.btnc5);
        btnc6= (Button) rootView.findViewById(R.id.btnc6);
        btnc7= (Button) rootView.findViewById(R.id.btnc7);
        btnc8= (Button) rootView.findViewById(R.id.btnc8);
        btnc9= (Button) rootView.findViewById(R.id.btnc9);
        btncdot= (Button) rootView.findViewById(R.id.btncdot);
        btncDel= (Button) rootView.findViewById(R.id.btncDel);
        btnCheck= (Button) rootView.findViewById(R.id.btnCheck);
        btnPayCredit=(Button) rootView.findViewById(R.id.btnPayCredit);
        btnOrder=(Button) rootView.findViewById(R.id.btnOrder);
        btnOrder2=(Button) rootView.findViewById(R.id.btnOrder2);
        btnIpay88=(Button) rootView.findViewById(R.id.btnIpay88);
        btnIpay88_2=(Button) rootView.findViewById(R.id.btnIpay88_2);
        txtDoc2No=(EditText)rootView.findViewById(R.id.txtDoc2No);
        txtDoc3No=(EditText)rootView.findViewById(R.id.txtDoc3No);

        btnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnCredit.setVisibility(View.GONE);
                lnCash.setVisibility(View.VISIBLE);
            }
        });

        btnCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnCash.setVisibility(View.GONE);
                lnCredit.setVisibility(View.VISIBLE);
                txtAmountDuec.setText(txtAmountDue.getText().toString());
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTable();
            }
        });
        btnOrder2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTable();
            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnloadpaytype();
            }
        });
        btnPayCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPayCredit.setEnabled(false);
                vCC1Amt= (String) txtAmountDuec.getText();
                CC1Code=(String) txtCC1Code.getText();
                CC1No= (String) txtcardno.getText();
                String Doc2No=txtDoc2No.getText().toString();
                String Doc3No=txtDoc3No.getText().toString();
                vCC1Amt=vCC1Amt.replaceAll(",","");
                if(vCC1Amt.equals("0.00") || vCC1Amt.length()==0){
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Paid Amount Cannot Empty");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    btnPayCredit.setEnabled(true);
                }else{
                    CC1Amt = Double.parseDouble(vCC1Amt);
                    if(CC1Code.equals("")){
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Credit Card Type Cannot Empty");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        btnPayCredit.setEnabled(true);
                    }
                    BalanceAmt  = 0.00;
                    CashAmt     = 0.00;
                    ChangeAmt   = 0.00;
                    SaveCS fnsave=new SaveCS(getActivity(),Doc1No,Doc2No,
                            Doc3No,CC1Code,CC1No,
                            CashAmt,ChangeAmt,CC1Amt,
                            BalanceAmt);
                    fnsave.execute();
                }
            }
        });

        //btn pay cash
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPay.setEnabled(false);
                PaidAmount= (String) txtPaidAmount.getText();
                PaidAmount=PaidAmount.replaceAll(",","");
                CashAmt=Double.parseDouble(PaidAmount);
                String AmtDue= (String) txtAmountDue.getText();
                AmtDue=AmtDue.replaceAll(",","");
                Double AmountDue=Double.parseDouble(AmtDue);
                String Doc2No=txtDoc2No.getText().toString();
                String Doc3No=txtDoc3No.getText().toString();
                if(PaidAmount.equals("0.00") || PaidAmount.length()==0){
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Paid Amount Cannot Empty");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    btnPay.setEnabled(true);
                }else if(CashAmt<AmountDue){
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Paid Amount Less Than Amount Due");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }else{
                    ChangeAmount= (String) txtChangeAmt.getText();
                    ChangeAmount=ChangeAmount.replaceAll(",","");
                    ChangeAmt=Double.parseDouble(ChangeAmount);
                    Double vCashAmt=CashAmt-ChangeAmt;
                    CC1Amt      =0.00;
                    BalanceAmt  =0.00;
                    CC1No       ="";
                    CC1Code     ="";
                    SaveCS fnsave=new SaveCS(getActivity(),Doc1No,Doc2No,
                            Doc3No,CC1Code,CC1No,
                            vCashAmt,ChangeAmt,CC1Amt,
                            BalanceAmt);
                    fnsave.execute();
                    txtAmountDue.setText(totalamt);
                    txtPaidAmount.setText(totalamt);
                    btnFull.setText("RM"+totalamt);
                    txtDoc1No.setText(NewDoc);
                }
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumber("1");
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumber("2");
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumber("3");
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumber("4");
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumber("5");
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumber("6");
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumber("7");
            }
        });
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumber("8");
            }
        });
        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumber("9");
            }
        });
        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumber("0");
            }
        });
        btndot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumber(".");
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minNumber();
            }
        });


        btnc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberC("1");
            }
        });
        btnc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberC("2");
            }
        });
        btnc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberC("3");
            }
        });
        btnc4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberC("4");
            }
        });
        btnc5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberC("5");
            }
        });
        btnc6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberC("6");
            }
        });
        btnc7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberC("7");
            }
        });
        btnc8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberC("8");
            }
        });
        btnc9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberC("9");
            }
        });
        btnc0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberC("0");
            }
        });
        btncdot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberC(".");
            }
        });
        btncDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minNumberC();
            }
        });

        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberFix("10");
            }
        });
        btn20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberFix("20");
            }
        });
        btn50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberFix("50");
            }
        });
        btn100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberFix("100");
            }
        });
        btnFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Amt=txtAmountDue.getText().toString();
                addNumberFix(Amt);
            }
        });
        btnIpay88.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //testIpay88();
                openIpay88();
            }
        });
        btnIpay88_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //testIpay88();
                //sendSOAP();
                openIpay88();
                //sendsoap();
            }
        });

        fncheckmodules();*/
        initData();
        initPaid();
        return rootView;
    }
    private void readySave(){
        if(allowSave==false){
            btnExact.setEnabled(false);
            btnPay.setEnabled(false);
            mButton1.setEnabled(false);
            mButton2.setEnabled(false);
            mButton3.setEnabled(false);
            mButton4.setEnabled(false);
            mButton5.setEnabled(false);
            mButton6.setEnabled(false);
            mButton7.setEnabled(false);
            mButton8.setEnabled(false);
            mButton9.setEnabled(false);
            mButton0.setEnabled(false);
            mButtonDot.setEnabled(false);
            lnDel.setEnabled(false);
            mButton10.setEnabled(false);
            mButton20.setEnabled(false);
            mButton50.setEnabled(false);
            mButton100.setEnabled(false);
            //lnKeyboard.setVisibility(View.GONE);
        }else if(allowSave==true){
            String PaidAmount=txtPaidAmount.getText().toString();
            if(CCYN==false) {
                calChange(PaidAmount);
            }
            btnExact.setEnabled(true);
            btnPay.setEnabled(true);
            mButton1.setEnabled(true);
            mButton2.setEnabled(true);
            mButton3.setEnabled(true);
            mButton4.setEnabled(true);
            mButton5.setEnabled(true);
            mButton6.setEnabled(true);
            mButton7.setEnabled(true);
            mButton8.setEnabled(true);
            mButton9.setEnabled(true);
            mButton0.setEnabled(true);
            mButtonDot.setEnabled(true);
            lnDel.setEnabled(true);
            mButton10.setEnabled(true);
            mButton20.setEnabled(true);
            mButton50.setEnabled(true);
            mButton100.setEnabled(true);
           // lnKeyboard.setVisibility(View.VISIBLE);
        }
    }

    private void initData(){
        readyPrinter();
        retLastNo();
        retSales();
        fntotal();
    }
    private void fntotal(){
        String TypePaid=txtTypePay.getText().toString();
        totalHeader totalH=new totalHeader(getActivity(),TypePaid);
        totalH.execute();
    }
    private void initPaid(){
        ic=txtPaidAmount.onCreateInputConnection(new EditorInfo());
        keyboardPay.setInputConnection(ic);
        txtPaidAmount.setTextIsSelectable(true);
        txtPaidAmount.setInputType(0);
        txtPaidAmount.setRawInputType(InputType.TYPE_CLASS_TEXT);
    }
    private void initCredit(){
        ic2=txtCC1No.onCreateInputConnection(new EditorInfo());
        keyboardPay.setInputConnection(ic2);
        txtCC1No.setTextIsSelectable(true);
        txtCC1No.setInputType(0);
        txtCC1No.setRawInputType(InputType.TYPE_CLASS_TEXT);
    }
    private  void initView(){
        ic=txtPaidAmount.onCreateInputConnection(new EditorInfo());
        keyboardPay.setInputConnection(ic);
        txtPaidAmount.setTextIsSelectable(true);
        txtPaidAmount.setInputType(0);
        txtPaidAmount.setRawInputType(InputType.TYPE_CLASS_TEXT);

        ic2=txtCC1No.onCreateInputConnection(new EditorInfo());
        keyboardPay.setInputConnection(ic2);
        txtCC1No.setTextIsSelectable(true);
        txtCC1No.setInputType(0);
        txtCC1No.setRawInputType(InputType.TYPE_CLASS_TEXT);

    }
    public void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
    }
    private void payFull(){
        //initView();
        txtPaidAmount.requestFocus();
        Double doubleNew    =0.00;
        String AmountDue    = txtAmountDue.getText().toString().replaceAll(",","");
        if(AmountDue.length()>0) {
            doubleNew = Double.parseDouble(AmountDue);
        }
        String New1         = String.format(Locale.US, "%,.2f", doubleNew);
        if(AmountDue.equals("0.00") ) {

        }else{
            ic.deleteSurroundingText(100, 0);
            ic.commitText(New1, 1);
            //txtPaidAmount.setText(New1);
        }
        //calChange(New1);
    }

    private void retSales(){
        try{
            String IPAddress, UserName, Password,
                    DBName,Port,DBStatus="",
                    ItemConn,URL,z,
                    EncodeType,BranchCode,LocationCode,
                    DepartmentCode,vCategoryCode="",UserCode,
                    CounterCode;
            DBAdapter db=new DBAdapter(getActivity());
            db.openDB();
            String querySet="select ServerName, UserName, Password, " +
                    "DBName, Port, DBStatus, " +
                    "ItemConn, EncodeType, BranchCode," +
                    "LocationCode, DepartmentCode, UserCode," +
                    "CounterCode from tb_setting ";
            Cursor curSet = db.getQuery(querySet);
            while (curSet.moveToNext()) {
                IPAddress       = curSet.getString(0);
                UserName        = curSet.getString(1);
                Password        = curSet.getString(2);
                DBName          = curSet.getString(3);
                Port            = curSet.getString(4);
                DBStatus        =curSet.getString(5);
                ItemConn        =curSet.getString(6);
                EncodeType      =curSet.getString(7);
                BranchCode      =curSet.getString(8);
                LocationCode    =curSet.getString(9);
                DepartmentCode  =curSet.getString(10);
                UserCode        =curSet.getString(11);
                CounterCode     =curSet.getString(12);
            }
            String SalesPersonCode = "";
           // if(DBStatus.equals("0")) {
                int numrows = 0;
                String qCheck = "select count(*)as numrows from tb_salesperson";
                Cursor rsCheck = db.getQuery(qCheck);
                while (rsCheck.moveToNext()) {
                    numrows = rsCheck.getInt(0);
                }
                if (numrows == 0) {
                    String qSetting = "select IFNULL(SalesPersonCode,'')as SalesPersonCode from tb_setting";
                    Cursor rsSetting = db.getQuery(qSetting);
                    while (rsSetting.moveToNext()) {
                        SalesPersonCode = rsSetting.getString(0);
                    }
                    if (!SalesPersonCode.isEmpty()) {
                        String qDel = "delete from tb_salesperson";
                        db.addQuery(qDel);
                        String qInsert = "insert into tb_salesperson(SalesPersonCode,SalesPersonName)values" +
                                "('" + SalesPersonCode + "', '')";
                        db.addQuery(qInsert);
                    }
                } else {
                    String qSales = "select IFNULL(SalesPersonCode,'')as  SalesPersonCode from tb_salesperson";
                    Cursor rsSales = db.getQuery(qSales);
                    while (rsSales.moveToNext()) {
                        SalesPersonCode = rsSales.getString(0);
                    }
                }
           // }else if(DBStatus.equals("2")){

          //  }
            txtSalesPersonCode.setText(SalesPersonCode);
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }/*catch (JSONException e){
            e.printStackTrace();
        }*/
    }
    private void calChange(String OldText){
        String AmountDue    =txtAmountDue.getText().toString().replaceAll(",","");
        OldText             =OldText.replaceAll(",","");
        OldText             =OldText.replaceAll("\\.+", ".");
        Log.d("OLD TEXT", OldText);
        Double Paid         =0.00;
        Double dAmount      =0.00;

        if(AmountDue.length()>0) {
             dAmount = Double.parseDouble(AmountDue);
        }
        if (OldText.length() == 0 ) {
            Paid   =0.00;
        }else{
            Paid   =Double.parseDouble(OldText);
        }
        Double ChangeAmt    =Paid-dAmount;
        String Changes=String.format(Locale.US, "%,.2f", ChangeAmt);
        txtChangeAmt.setText(Changes);
    }

    private void retLastNo(){
        String Prefix="";
        String LastNo="";
        DBAdapter db = new DBAdapter(getContext());
        db.openDB();
        String vQuery="select Prefix,LastNo from sys_runno_dt where RunNoCode='CS' ";
        Cursor cRunNo = db.getQuery(vQuery);
        while (cRunNo.moveToNext()) {
            Prefix= cRunNo.getString(0);
            LastNo= cRunNo.getString(1);
        }
        String Doc1No=Prefix+LastNo;
        txtDoc1No.setText(Doc1No);
        db.closeDB();
    }

    public void loadretpayment(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_paytype, null);
        builderSingle.setView(dialogView);
        lv = (ListView) dialogView.findViewById(R.id.lsPayType);
        DBAdapter db=new DBAdapter(getContext());
        db.openDB();
        Cursor c=db.getPayType();
        paytypes.clear();
        Spacecraft_PayType s=null;
        Spacecraft_PayType s1=null;
        s1=new Spacecraft_PayType();
        s1.setRunNo(100);
        s1.setPaymentType("Cash");
        s1.setPaymentCode("Cash");
        s1.setCharges1("0");
        s1.setPaidByCompanyYN("0");
        paytypes.add(s1);
        while (c.moveToNext()) {
            int RunNo=c.getInt(0);
            String tPaymentCode = c.getString(1);
            String tPaymentType= c.getString(2);
            String tCharges1= c.getString(3);
            String tPaidByCompanyYN= c.getString(4);
            String MerchantCode= c.getString(5);
            String MerchantKey= c.getString(6);
            s=new Spacecraft_PayType();
            s.setRunNo(RunNo);
            s.setPaymentType(tPaymentType);
            s.setPaymentCode(tPaymentCode);
            s.setCharges1(tCharges1);
            s.setPaidByCompanyYN(tPaidByCompanyYN);
            s.setMerchantCode(MerchantCode);
            s.setMerchantKey(MerchantKey);
            paytypes.add(s);
        }
        PayTypeAdapter adapter=new PayTypeAdapter(getActivity(),paytypes);
        lv.setAdapter(adapter);
        db.closeDB();
        final AlertDialog alertDialog = builderSingle.create();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub
                String  CC1Code     = paytypes.get(position).getPaymentCode();
                PayType     = paytypes.get(position).getPaymentType();
                MerchantCode        = paytypes.get(position).getMerchantCode();
                MerchantKey         = paytypes.get(position).getMerchantKey();
                //String  Charges1        = paytypes.get(position).getCharges1();
                // String  PaidByCompanyYN = paytypes.get(position).getPaidByCompanyYN();
                String typepay="";
                if(!PayType.equals("Cash")) {
                    typepay=PayType+"\n"+CC1Code;
                }else{
                    typepay=PayType;
                }
                txtTypePay.setText(typepay);
                txtCC1Code.setText(CC1Code);
                //txtCharges1.setText(Charges1);
                //txtPaidByCompanyYN.setText(PaidByCompanyYN);
                if(PayType.equals("Credit Card")){
                    initCredit();
                    CCYN=true;
                    txtCC1No.setVisibility(View.VISIBLE);
                    txtPaidAmount.setVisibility(View.GONE);
                    txtCurCode.setText("");
                }else if(PayType.equals("E-Wallet")){
                    CCYN=false;
                    txtCC1No.setVisibility(View.GONE);
                    txtPaidAmount.setVisibility(View.VISIBLE);
                    openIpay88();
                }else if(PayType.equals("Boost")){
                    CCYN=false;
                    txtCC1No.setVisibility(View.GONE);
                    txtPaidAmount.setVisibility(View.VISIBLE);
                    openBoost();
                }else if(PayType.equals("Gift Card")) {
                    CCYN = false;
                    txtCC1No.setVisibility(View.GONE);
                    txtPaidAmount.setVisibility(View.VISIBLE);
                    openTapNFC();
                }else if(PayType.equals("Debit Card")){
                    initCredit();
                    CCYN=true;
                    txtCC1No.setVisibility(View.VISIBLE);
                    txtPaidAmount.setVisibility(View.GONE);
                    txtCurCode.setText("");
                }else{
                    initPaid();
                    txtCC1No.setVisibility(View.GONE);
                    txtPaidAmount.setVisibility(View.VISIBLE);
                    txtCurCode.setText(CurCode);
                    CCYN=false;
                }

                fntotal();
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void saveCS() {
        btnPay.setEnabled(false);
        pbSave.setVisibility(View.VISIBLE);
        String TyePay       = txtTypePay.getText().toString();
        String Doc1No       = txtDoc1No.getText().toString();
        String PaidAmount   = txtPaidAmount.getText().toString().replaceAll(",", "");
        String AmtDue       = txtAmountDue.getText().toString().replaceAll(",", "");
        String ChangeAmount = txtChangeAmt.getText().toString().replaceAll(",", "");
        String Doc2No       = txtDoc2No.getText().toString();
        String Doc3No       = txtDoc3No.getText().toString();
        String AdjAmt       = txtAdjAmt.getText().toString();
        String vCC1Amt = "0.00";
        String CC1Code = "0.00";
        String CC1No = "0.00";
        String SalesPersonCode = txtSalesPersonCode.getText().toString();
        if(AmtDue==null) {
            Toast.makeText(getActivity(),"Amount Due Cannot Null", Toast.LENGTH_SHORT).show();
            btnPay.setEnabled(true);
            pbSave.setVisibility(View.GONE);
            //txtSalesPersonCode.requestFocus();
        }else if (SalesPersonCode.isEmpty()) {
            Toast.makeText(getActivity(), "Sales Person Cannot Empty", Toast.LENGTH_SHORT).show();
            txtSalesPersonCode.requestFocus();
            btnPay.setEnabled(true);
            pbSave.setVisibility(View.GONE);
        }else if(iNumRows==0){
            btnPay.setEnabled(true);
            pbSave.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Orders is Empty", Toast.LENGTH_SHORT).show();
        }else {
            if (!TyePay.equals("Cash")) {
                vCC1Amt = txtAmountDue.getText().toString().replaceAll(",", "");
                CC1Code = txtCC1Code.getText().toString();
                CC1No = txtCC1No.getText().toString();
                if(vCC1Amt.length()>0) {
                    CC1Amt = Double.parseDouble(vCC1Amt);
                }
                BalanceAmt  = 0.00;
                CashAmt     = 0.00;
                ChangeAmt   = 0.00;
                /*if (vCC1Amt.equals("0.00") || vCC1Amt.length() == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Paid Amount Cannot Empty");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    btnPay.setEnabled(true);
                    pbSave.setVisibility(View.VISIBLE);
                } else {*/
                    if (CC1Code.equals("")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Alert");
                        alertDialog.setMessage("Credit Card Type Cannot Empty");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        btnPay.setEnabled(true);
                        pbSave.setVisibility(View.GONE);
                    }else {
                        CCYN = false;
                        SaveCS fnsave = new SaveCS(getActivity(), "CR", Doc1No, Doc2No,
                                Doc3No, CC1Code, CC1No,
                                CashAmt, ChangeAmt, CC1Amt,
                                BalanceAmt, "0.00", PrintYN);
                        fnsave.execute();
                    }
              //  }
            } else {
                Double AmountDue    = 0.00;
                CashAmt             = 0.00;
                ChangeAmt           = 0.00;
                if(PaidAmount.length()>0) {
                    CashAmt = Double.parseDouble(PaidAmount);
                }
                if(AmtDue.length()>0) {
                     AmountDue = Double.parseDouble(AmtDue);
                }
                if(ChangeAmount.length()>0) {
                    ChangeAmt = Double.parseDouble(ChangeAmount);
                }
                Double vCashAmt = CashAmt - ChangeAmt;
                CC1Amt = 0.00;
                BalanceAmt = 0.00;
                CC1No = "";
                CC1Code = "";
               /* if (PaidAmount.equals("0.00") || PaidAmount.length() == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Paid Amount Cannot Empty");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    btnPay.setEnabled(true);
                    pbSave.setVisibility(View.VISIBLE);

                } else */if (CashAmt < AmountDue) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Paid Amount Less Than Amount Due");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    btnPay.setEnabled(true);
                    pbSave.setVisibility(View.GONE);
                } else {
                    SaveCS fnsave = new SaveCS(getActivity(),"CR", Doc1No, Doc2No,
                            Doc3No, CC1Code, CC1No,
                            vCashAmt, ChangeAmt, CC1Amt,
                            BalanceAmt,AdjAmt,PrintYN);
                    fnsave.execute();
                }
            }
        }
    }

    public class totalHeader extends AsyncTask<Void,Void, String>{
        Context c;
        String z,TypePaid;
        String deviceId,AdjAmt;
        String AmountDue,TotalDiscount,GrossAmt,TotalQty,SCAmt;
        TelephonyManager telephonyManager;
        String IPAddress, UserName, Password,
                DBName,Port,DBStatus,
                ItemConn,URL,
                EncodeType,BranchCode,LocationCode,
                DepartmentCode,vCategoryCode="",UserCode,
                CounterCode;
        int inumrows=0;
        public totalHeader(Context c, String TypePaid) {
            this.c = c;
            this.TypePaid=TypePaid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.totalH();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("error")){
                Toast.makeText(c,"error, ret total header", Toast.LENGTH_SHORT).show();
            }else{
                txtAmountDue.setText(AmountDue);
                if(dServiceCharges>0){
                    txtTotalQty.setText(TotalQty+"\n"+SCAmt);
                    tTotalQty.setText("Qty.Tender\nSvc.Charges");
                }else {
                    txtTotalQty.setText(TotalQty);
                }
                txtAdjAmt.setText(AdjAmt);
                txtTotalAmt.setText(GrossAmt+"\n("+TotalDiscount+")");
                iNumRows=inumrows;
                if(inumrows==0){
                    allowSave=false;
                }else{
                    allowSave=true;
                }
                readySave();
            }
        }
        private String totalH(){
            try{
                //telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
               // deviceId = telephonyManager.getDeviceId();
                JSONObject jsonReq,jsonRes;
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String querySet="select ServerName, UserName, Password, " +
                        "DBName, Port, DBStatus, " +
                        "ItemConn, EncodeType, BranchCode," +
                        "LocationCode, DepartmentCode, UserCode," +
                        "CounterCode from tb_setting ";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress       = curSet.getString(0);
                    UserName        = curSet.getString(1);
                    Password        = curSet.getString(2);
                    DBName          = curSet.getString(3);
                    Port            = curSet.getString(4);
                    DBStatus        =curSet.getString(5);
                    ItemConn        =curSet.getString(6);
                    EncodeType      =curSet.getString(7);
                    BranchCode      =curSet.getString(8);
                    LocationCode    =curSet.getString(9);
                    DepartmentCode  =curSet.getString(10);
                    UserCode        =curSet.getString(11);
                    CounterCode     =curSet.getString(12);
                }

                String sqlTotal="SELECT IFNULL(sum(HCLineAmt),0) as vAmountDue, " +
                        "IFNULL(sum(HCDiscount),0) as vTotalDiscount, " +
                        "IFNULL(sum(HCTax),0) as vGSTAmount, " +
                        "sum(HCLineAmt+HCDiscount) as vGrossAmt, " +
                        "sum(Qty) as vTotalQty " +
                        "FROM cloud_cus_inv_dt " +
                        " WHERE ComputerName='"+UserCode+"' " +
                        " Group By ComputerName ";

                String qServiceCharges="select HCUnitCost,HCTax from cloud_cus_inv_dt where ComputerName='" + UserCode + "' " +
                        "and BlankLine='4' ";
                Cursor rsSC=db.getQuery(qServiceCharges);

                if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", sqlTotal);
                    jsonReq.put("action", "select");
                    ConnectorLocal connect=new ConnectorLocal();
                    String response=connect.ConnectSocket(IPAddress,8080,jsonReq.toString());
                    jsonRes             = new JSONObject(response);
                    String result       = jsonRes.getString("hasil");
                    Log.d("JSON",result);
                    Double dAmountDue   =0.00;
                    Double dTotalDisc   =0.00;
                    Double dGSTAmt      =0.00;
                    Double dGrossAmt    =0.00;
                    Double dTotalQty    =0.00;
                    if(!result.equals("0")) {
                        JSONArray rsData = new JSONArray(result);
                        JSONObject vData = null;
                        for (int i = 0; i < rsData.length(); i++) {
                            vData       = rsData.getJSONObject(i);
                            dAmountDue  = vData.getDouble("vAmountDue");
                            dTotalDisc  = vData.getDouble("vTotalDiscount");
                            dGSTAmt     = vData.getDouble("vGSTAmount");
                            dGrossAmt   = vData.getDouble("vGrossAmt");
                            dTotalQty   = vData.getDouble("vTotalQty");
                            inumrows++;
                        }
                    }
                    //AmountDue       = String.format(Locale.US, "%,.2f", dAmountDue);
                    TotalDiscount       = String.format(Locale.US, "%,.2f", dTotalDisc);
                    GrossAmt            = String.format(Locale.US, "%,.2f", dGrossAmt);
                    TotalQty            = String.format(Locale.US, "%,.2f", dTotalQty);
                    Double RoundingAmt  = Rounding.setRound(c, dAmountDue);
                    Double dAdjAmt      = 0.00;
                    if(!TypePaid.equals("Credit Card")){
                        dAdjAmt     = RoundingAmt-dAmountDue;
                        AmountDue   = String.format(Locale.US, "%,.2f", RoundingAmt);
                    }else{
                        AmountDue   = String.format(Locale.US, "%,.2f", dAmountDue);
                    }
                    AdjAmt       = String.format(Locale.US, "%,.2f", dAdjAmt);

                    z="success";

                }else{
                    Log.d("QUERY H",sqlTotal);
                    Cursor rsData=db.getQuery(sqlTotal);
                    Double dAmountDue=0.00;
                    Double dGSTAmt=0.00;
                    while(rsData.moveToNext()){
                        dAmountDue          = rsData.getDouble(0);
                        Double dTotalDisc   = rsData.getDouble(1);
                        dGSTAmt             = rsData.getDouble(2);
                        Double dGrossAmt    = rsData.getDouble(3);
                        Double dTotalQty    = rsData.getDouble(4);

                        TotalDiscount       = String.format(Locale.US,"%,.2f",dTotalDisc);
                        GrossAmt            = String.format(Locale.US, "%,.2f", dGrossAmt);
                        TotalQty            = String.format(Locale.US, "%,.2f", dTotalQty);
                        inumrows++;
                    }

                   while(rsSC.moveToNext()){
                        Double dSCAmt    = rsSC.getDouble(0);
                        SCAmt            = String.format(Locale.US, "%,.2f", dSCAmt);
                    }
                    //AmountDue           = String.format(Locale.US,"%,.2f",dAmountDue);
                    Double RoundingAmt  = Rounding.setRound(c, dAmountDue);
                    Double dAdjAmt      =0.00;
                    if(!TypePaid.equals("Credit Card")){
                        dAdjAmt     = RoundingAmt-dAmountDue;
                        AmountDue   = String.format(Locale.US, "%,.2f", RoundingAmt);
                    }else{
                        AmountDue   = String.format(Locale.US, "%,.2f", dAmountDue);
                    }
                    AdjAmt       = String.format(Locale.US, "%,.2f", dAdjAmt);
                    Log.d("AMOUNT DUE",AmountDue);
                    z="success";
                }
                return z;
            }catch (SQLiteException e) {
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return z;
        }
    }
    private void openTapNFC(){
        getActivity().finish();
        String AmountDue    = txtAmountDue.getText().toString();
        String Doc1No       = txtDoc1No.getText().toString();
        String Doc2No       = txtDoc2No.getText().toString();
        String PayType      = txtTypePay.getText().toString();
        String PaymentCode  =txtCC1Code.getText().toString();
        Intent i=new Intent(getActivity(),CashReceipt.class);
        i.putExtra("TRANSID_KEY", Doc1No);
        i.putExtra("AMOUNT_KEY", AmountDue);
        i.putExtra("PAYTYPE_KEY", PayType);
        i.putExtra("PAYMENTCODE_KEY", PaymentCode);
        i.putExtra("MERCHANTCODE_KEY", MerchantKey);
        i.putExtra("REFNO_KEY", Doc1No);
        i.putExtra("DOC2NO_KEY", Doc2No);

        startActivity(i);
    }

    private void openIpay88(){
        try {
            DBAdapter db = new DBAdapter(getActivity());
            db.openDB();
            String checkMember = "select count(*)as numrows from tb_member";
            Cursor rsCheck = db.getQuery(checkMember);
            int numrows = 0;
            while (rsCheck.moveToNext()) {
                numrows = rsCheck.getInt(0);
            }

            if (numrows==0) {
                Toast.makeText(getContext(),"Member Cannot Empty", Toast.LENGTH_SHORT).show();
            } else{
                String AmountDue = txtAmountDue.getText().toString();
                String Doc1No = txtDoc1No.getText().toString();
                String Doc2No = txtDoc2No.getText().toString();
                String PaymentCode = txtCC1Code.getText().toString();
                Bundle b = new Bundle();
                b.putString("AMOUNT_KEY", AmountDue);
                b.putString("DOC1NO_KEY", Doc1No);
                b.putString("DOC2NO_KEY", Doc2No);
                b.putString("PAYTYPE_KEY", PaymentCode);
                b.putString("MERCHANTCODE_KEY", MerchantCode);
                b.putString("MERCHANTKEY_KEY", MerchantKey);
                DialogIpay88 dialogItem = new DialogIpay88();
                dialogItem.setArguments(b);
                dialogItem.show(getActivity().getSupportFragmentManager(), "mListItem");
            }
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private void openBoost(){
        try {
            DBAdapter db = new DBAdapter(getActivity());
            db.openDB();
            String checkMember = "select count(*)as numrows from tb_member";
            Cursor rsCheck = db.getQuery(checkMember);
            int numrows = 0;
            while (rsCheck.moveToNext()) {
                numrows = rsCheck.getInt(0);
            }

            if (numrows==0) {
                Toast.makeText(getContext(),"Member Cannot Empty", Toast.LENGTH_SHORT).show();
            } else{
                String AmountDue = txtAmountDue.getText().toString();
                String Doc1No = txtDoc1No.getText().toString();
                String Doc2No = txtDoc2No.getText().toString();
                String PaymentCode = txtCC1Code.getText().toString();
                Bundle b = new Bundle();
                b.putString("AMOUNT_KEY", AmountDue);
                b.putString("DOC1NO_KEY", Doc1No);
                b.putString("DOC2NO_KEY", Doc2No);
                b.putString("PAYTYPE_KEY", PaymentCode);
                b.putString("MERCHANTCODE_KEY", MerchantCode);
                b.putString("MERCHANTKEY_KEY", MerchantKey);
                DialogBoost dialogBoost = new DialogBoost();
                dialogBoost.setArguments(b);
                dialogBoost.show(getActivity().getSupportFragmentManager(), "mPayBoost");
            }
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    private void readyPrinter(){
        try {
            DBAdapter db = new DBAdapter(getActivity());
            db.openDB();
            String qCom = "select CurCode,GSTNo from companysetup";
            Cursor rsCom = db.getQuery(qCom);
            while (rsCom.moveToNext()) {
                CurCode =rsCom.getString(0);
                txtCurCode.setText(rsCom.getString(0));
            }

            //Cursor cPrinter = db.getSettingPrint();
            String query = "select * from tb_settingprinter";
            Cursor cPrint=db.getQuery(query);
            while (cPrint.moveToNext()) {
                TypePrinter     = cPrint.getString(1);
                NamePrinter     = cPrint.getString(2);
                IPPrinter       = cPrint.getString(3);
                vPort           = cPrint.getString(5);
                PaperSize       = cPrint.getString(6);
                String vCopies  = cPrint.getString(7);
                if(vCopies.contains("=")) {
                    DialogPrintYN=true;
                }else{
                    DialogPrintYN=false;
                }
            }
            /*while (cPrinter.moveToNext()) {
                TypePrinter = cPrinter.getString(1);
                NamePrinter = cPrinter.getString(2);
                IPPrinter = cPrinter.getString(3);
                UUID = cPrinter.getString(4);
            }*/

            String qOtherSet="select ServiceCharges from tb_othersetting";
            Cursor rsOther=db.getQuery(qOtherSet);
            while(rsOther.moveToNext()){
                dServiceCharges=rsOther.getDouble(0);
                //tServiceCharges.append(" "+dServiceCharges+"%");
                if(dServiceCharges>0){
                    AddMisc addMisc=new AddMisc(getActivity(),"CS","0");
                    addMisc.execute();
                }
            }

            db.closeDB();
            if (TypePrinter.equals("AIDL")) {
                AidlUtil.getInstance().connectPrinterService(getActivity());
                AidlUtil.getInstance().initPrinter();
            } else if (TypePrinter.equals("Ipos AIDL")) {
                IposAidlUtil.getInstance().connectPrinterService(getActivity());
                IposAidlUtil.getInstance().initPrinter();
            } else {

            }

        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private void checkDialog(){
        if(DialogPrintYN){
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Confirmation");
            alertDialog.setMessage("Do you want to print receipt ?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            PrintYN="1";
                            saveCS();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            PrintYN="0";
                            saveCS();
                        }
                    });
            alertDialog.show();
        }else{
            saveCS();
        }
    }
    /*private void openTable(){
        DialogTable dialogTable=new DialogTable();
        dialogTable.show(getFragmentManager(),"mTag");
    }
    private void addNumberC(String NewText){
        //TextView t = (TextView) v.findViewById(R.id.txtPaidAmount);
        String OldText= (String) txtcardno.getText();
        if(OldText.equals("0.00")){
            OldText="";
        }
        txtcardno.setText(OldText+NewText);
    }

    private void minNumberC(){
        String OldText= (String) txtcardno.getText();
        if (OldText != null && OldText.length() > 0 ) {
            OldText = OldText.substring(0, OldText.length() - 1);
        }else{
            OldText ="0";
        }
        txtcardno.setText(OldText);
    }
    private void addNumber(String NewText){
        //TextView t = (TextView) v.findViewById(R.id.txtPaidAmount);
        String OldText= (String) txtPaidAmount.getText();
        if(OldText.equals("0.00")){
            OldText="";
        }
        txtPaidAmount.setText(OldText+NewText);
        calChange(OldText+NewText);
    }
    private void addNumberFix(String NewText){
        String OldText= (String) txtPaidAmount.getText();
        if(OldText.length()==0){
            OldText="0.00";
        }
        OldText=OldText.replaceAll(",","");
        Double doubleOld=Double.parseDouble(OldText);
        NewText=NewText.replaceAll(",","");
        Double doubleNew=Double.parseDouble(NewText);
        Double doubleText=doubleOld+doubleNew;
        String New1= String.format(Locale.US, "%,.2f", doubleText);
       // String New1=doubleText.toString();
        txtPaidAmount.setText(New1);
        calChange(New1);
    }
    private void minNumber(){
        String OldText= (String) txtPaidAmount.getText();
        if (OldText != null && OldText.length() > 0 ) {
            OldText = OldText.substring(0, OldText.length() - 1);
            Log.d("OLDTEXT",OldText);
            calChange(OldText);
            // calChange(v,OldText);
        }else{
            OldText ="0.00";
        }
        txtPaidAmount.setText(OldText);
    }

    private void fnchecktotal(){
        String TotalAmt="";
        String TotalDiscount="";
        DBAdapter db = new DBAdapter(getContext());
        db.openDB();
        String vQuery="select sum(HCLineAmt + HCDiscount) as TotalAmt," +
                " sum(HCDiscount) as TotalDiscount from cloud_cus_inv_dt ";
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
        String vQuery="select Prefix,LastNo from sys_runno_dt where RunNoCode='CS' ";
        Cursor cRunNo = db.getQuery(vQuery);
        while (cRunNo.moveToNext()) {
            Prefix= cRunNo.getString(0);
            LastNo= cRunNo.getString(1);
        }
        Doc1No=Prefix+LastNo;
        txtDoc1No.setText(Doc1No);
        db.closeDB();
    }


    private void calChange(String OldText){
        String AmountDue1    =(String) txtAmountDue.getText();
        AmountDue1=AmountDue1.replaceAll(",","");
        OldText=OldText.replaceAll(",","");
        Log.d("AMOUNT",AmountDue1);
        Double Paid=0.00;
        Double Amount1      =Double.parseDouble(AmountDue1);
        if (OldText.length() == 0 ) {
            Paid   =0.00;
        }else{
            Paid   =Double.parseDouble(OldText);
        }
        Double ChangeAmt    =Paid-Amount1;
        String Changes=String.format(Locale.US, "%,.2f", ChangeAmt);
        txtChangeAmt.setText(Changes);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
           // Fragment_Payment.this.refreshPayment();
            //InputMethodManager mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
           // mImm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    public void fnloadpaytype(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_paytype, null);
        builderSingle.setView(dialogView);
        lv = (ListView) dialogView.findViewById(R.id.lsPayType);
        DBAdapter db=new DBAdapter(getContext());
        db.openDB();
        Cursor c=db.getPayType();
        paytypes.clear();
        Spacecraft_PayType s=null;
        while (c.moveToNext()) {
            int RunNo=c.getInt(0);
            String tPaymentCode = c.getString(1);
            String tPaymentType= c.getString(2);
            String tCharges1= c.getString(3);
            String tPaidByCompanyYN= c.getString(4);
            s=new Spacecraft_PayType();
            s.setRunNo(RunNo);
            s.setPaymentType(tPaymentType);
            s.setPaymentCode(tPaymentCode);
            s.setCharges1(tCharges1);
            s.setPaidByCompanyYN(tPaidByCompanyYN);
            paytypes.add(s);
        }
        PayTypeAdapter adapter=new PayTypeAdapter(getActivity(),paytypes);
        lv.setAdapter(adapter);
        db.closeDB();
        final AlertDialog alertDialog = builderSingle.create();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                // TODO Auto-generated method stub
                String  CC1Code         = paytypes.get(position).getPaymentCode();
                //String  Charges1        = paytypes.get(position).getCharges1();
               // String  PaidByCompanyYN = paytypes.get(position).getPaidByCompanyYN();
                txtCC1Code.setText(CC1Code);
                //txtCharges1.setText(Charges1);
                //txtPaidByCompanyYN.setText(PaidByCompanyYN);
                alertDialog.cancel();
            }
        });
        alertDialog.show();
    }

    public class totalHeader extends AsyncTask<Void,Void, String>{
        Context c;
        String z;
        String deviceId;
        String AmountDue,TotalDiscount,GrossAmt;
        TelephonyManager telephonyManager;
        String IPAddress,UserName,Password,DBName,Port,URL,DBStatus;

        public totalHeader(Context c) {
            this.c = c;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.totalH();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("error")){
                Toast.makeText(c,"error, ret total header", Toast.LENGTH_SHORT).show();
            }else{
                txtAmountDue.setText(AmountDue);
                txtAmountDuec.setText(AmountDue);
                btnFull.setText(CurCode+AmountDue);
                txtHCGbDiscount.setText(TotalDiscount);
                txtTotalAmt.setText(GrossAmt);
                totalamt=GrossAmt;
            }
        }
        private String totalH(){
            try{
                telephonyManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
                deviceId = telephonyManager.getDeviceId();
                JSONObject jsonReq,jsonRes;
                DBAdapter db = new DBAdapter(c);
                db.openDB();
                String querySet="select * from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(1);
                    UserName = curSet.getString(2);
                    Password = curSet.getString(3);
                    DBName = curSet.getString(4);
                    Port = curSet.getString(5);
                    DBStatus = curSet.getString(7);
                }
                String sqlTotal="SELECT IFNULL(sum(HCLineAmt),0) as vAmountDue, " +
                        "IFNULL(sum(HCDiscount),0) as vTotalDiscount, " +
                        "IFNULL(sum(HCTax),0) as vGSTAmount, " +
                        "sum(HCLineAmt+HCDiscount) as vGrossAmt " +
                        "FROM cloud_cus_inv_dt WHERE ComputerName='"+deviceId+"' Group By '' ";
                if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", sqlTotal);
                    jsonReq.put("action", "select");
                    ConnectorLocal connect=new ConnectorLocal();
                    String response=connect.ConnectSocket(IPAddress,8080,jsonReq.toString());
                    jsonRes             = new JSONObject(response);
                    String result       = jsonRes.getString("hasil");
                    Log.d("JSON",result);
                    Double dAmountDue   =0.00;
                    Double dTotalDisc   =0.00;
                    Double dGSTAmt      =0.00;
                    Double dGrossAmt    =0.00;
                    if(!result.equals("0")) {
                        JSONArray rsData = new JSONArray(result);
                        JSONObject vData = null;
                        for (int i = 0; i < rsData.length(); i++) {
                            vData      = rsData.getJSONObject(i);
                            dAmountDue = vData.getDouble("vAmountDue");
                            dTotalDisc = vData.getDouble("vTotalDiscount");
                            dGSTAmt = vData.getDouble("vGSTAmount");
                            dGrossAmt = vData.getDouble("vGrossAmt");
                        }
                    }
                    AmountDue = String.format(Locale.US, "%,.2f", dAmountDue);
                    TotalDiscount = String.format(Locale.US, "%,.2f", dTotalDisc);
                    GrossAmt = String.format(Locale.US, "%,.2f", dGrossAmt);
                    z="success";
                }else{
                    Log.d("QUERY H",sqlTotal);
                    Cursor rsData=db.getQuery(sqlTotal);
                    while(rsData.moveToNext()){
                        Double dAmountDue   = rsData.getDouble(0);
                        Double dTotalDisc   = rsData.getDouble(1);
                        Double dGSTAmt      = rsData.getDouble(2);
                        Double dGrossAmt    = rsData.getDouble(3);
                        AmountDue           = String.format(Locale.US,"%,.2f",dAmountDue);
                        TotalDiscount       = String.format(Locale.US,"%,.2f",dTotalDisc);
                        GrossAmt            = String.format(Locale.US, "%,.2f", dGrossAmt);
                        Log.d("AMOUNT DUE",AmountDue);
                    }
                    z="success";
                }
                return z;
            }catch (SQLiteException e) {
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return z;
        }
    }

    public void fncheckmodules(){
        try{
            DBAdapter db=new DBAdapter(getActivity());
            db.openDB();
            String qModule="select Modules,CompCustomerYN from tb_setting";
            Cursor rsModule=db.getQuery(qModule);
            String lisModule="";
            String CompCustomerYN="0";
            while(rsModule.moveToNext()){
                lisModule=rsModule.getString(0);
                CompCustomerYN=rsModule.getString(1);
            }
            String[] modules = lisModule.split(";");
            for (String add : modules) {
                showModule(add);
            }
            Log.d("CompCustomerYN",CompCustomerYN);
            if(CompCustomerYN.equals("1")){
               // checkMember();
            }

            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private void showModule(String module){
        switch (module) {
            case "Cash Receipt - Take Order":
                btnPay.setVisibility(View.GONE);
                btnPayCredit.setVisibility(View.GONE);
                break;
        }

    }
    */
}
