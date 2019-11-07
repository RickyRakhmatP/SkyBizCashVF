package skybiz.com.posoffline.ui_QuickCash;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import skybiz.com.posoffline.KeyboardQuick;
import skybiz.com.posoffline.MyBounceInterpolator;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewObject.Rounding;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItemM;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItemNew;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItemQuick;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddMisc;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.SaveCS;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_IposAIDL.Utils.IposAidlUtil;
import skybiz.com.posoffline.ui_QuickCash.m_ItemQuick.DownloaderItem;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

public class QuickCash extends AppCompatActivity {

    KeyboardQuick keyboard;
    EditText txtAmountDue,txtInfo,txtInput,txtCalculate;
    Button btnCash;
    TextView txtDoc1No,txtCurCode;
    Double dServiceCharges=0.00;

    private static final char ADDITION = '+';
    private static final char SUBTRACTION = '-';
    private static final char MULTIPLICATION = '*';
    private static final char DIVISION = '/';

    private char CURRENT_ACTION='0';

    private double valueOne = Double.NaN;
    private double valueTwo;

    private DecimalFormat decimalFormat;
    Button btnPlus,btnMinus,btnMultiplication,btnClear,btnEqual;
    LinearLayout lnDel;
    RecyclerView rvItem;
    private GridLayoutManager lLayout;
    String ItemCode="Quick Cash",Description="Quick Cash";
   // private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding = DataBinding.setContentView(this, R.layout.activity_main);
        setContentView(R.layout.activity_quick_cash);
        getSupportActionBar().setTitle("Quick Cash");
        keyboard=(KeyboardQuick)findViewById(R.id.keyboardquick);
        txtAmountDue=(EditText)findViewById(R.id.txtAmountDue);
        txtInfo=(EditText)findViewById(R.id.txtInfo);
        txtInput=(EditText)findViewById(R.id.txtInput);
        txtCalculate=(EditText) findViewById(R.id.txtCalculate);
        txtDoc1No=(TextView) findViewById(R.id.txtDoc1No);
        txtCurCode=(TextView) findViewById(R.id.txtCurCode);
        btnCash=(Button) findViewById(R.id.btnCash);
        btnPlus=(Button) findViewById(R.id.btnPlus);
        btnMinus=(Button) findViewById(R.id.btnMinus);
        btnMultiplication=(Button) findViewById(R.id.btnMultiplication);
        btnClear=(Button) findViewById(R.id.btnClear);
        btnEqual=(Button) findViewById(R.id.btnEqual);
        lnDel=(LinearLayout)findViewById(R.id.lnDel);
        rvItem=(RecyclerView)findViewById(R.id.rvItem);
        decimalFormat = new DecimalFormat("#.##########");

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CURRENT_ACTION = '0';
                fnadditem();
                computeCalculation();
                CURRENT_ACTION = ADDITION;
               // txtCalculate.setText(decimalFormat.format(valueOne) + "+");
                txtAmountDue.setText(String.format(Locale.US, "%,.2f", valueOne));
                txtInput.setText(null);
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // CURRENT_ACTION = '0';
                fnadditem();
                computeCalculation();
                CURRENT_ACTION = SUBTRACTION;
               // txtCalculate.setText(decimalFormat.format(valueOne) + "-");
                txtAmountDue.setText(String.format(Locale.US, "%,.2f", valueOne));
                txtInput.setText(null);

            }
        });


        btnMultiplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // CURRENT_ACTION = '0';
                fnadditem();
                computeCalculation();
                CURRENT_ACTION = MULTIPLICATION;
                //txtCalculate.setText(decimalFormat.format(valueOne) + "*");
                txtAmountDue.setText(String.format(Locale.US, "%,.2f", valueOne));
                txtInput.setText(null);

            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnclear();
            }
        });
        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CURRENT",CURRENT_ACTION+"");
                if(CURRENT_ACTION!='0');{
                    fnadditem();
                    computeCalculation();
                    txtAmountDue.setText(String.format(Locale.US, "%,.2f", valueOne));
                   // txtInfo.getText().clear();
                    txtInfo.append(  " = " + decimalFormat.format(valueOne));
                    txtInput.setText(null);
                   // valueOne = Double.NaN;
                    CURRENT_ACTION = '0';
                }

            }
        });
        /*lnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Total=txtInput.getText().toString();
                if(Total.length()>0) {
                   // computeCalculation();
                   // valueOne = Double.NaN;
                    CURRENT_ACTION = '0';
                }

            }
        });*/
        txtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String vInfo=txtInfo.getText().toString();
                if(vInfo.length()==0) {
                    txtAmountDue.setText(txtInput.getText().toString());
                }
            }
        });

        txtCurCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrder();
            }
        });

        InputConnection ic=txtInput.onCreateInputConnection(new EditorInfo());
        keyboard.setInputConnection(ic);
        txtInput.setTextIsSelectable(true);
        txtInput.setInputType(0);
        txtInput.setRawInputType(InputType.TYPE_CLASS_TEXT);

        btnCash.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapButton(btnCash);
                fnsavecs();
            }
        });
        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        txtInfo.setOnFocusChangeListener(ofcListener);
        initData();
    }

    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus){

            if(v.getId() == R.id.txtInfo && !hasFocus) {

                InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        }
    }
    public void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
    }
    private void showOrder(){
        DialogOrder dialogOrder = new DialogOrder();
        dialogOrder.show(getSupportFragmentManager(), "mListOrder");
    }
    private void fnclear(){
        txtInfo.getText().clear();
        txtInput.setText(null);
        ItemCode="Quick Cash";
        Description="Quick Cash";
        txtAmountDue.getText().clear();
        valueOne = Double.NaN;
        valueTwo = Double.NaN;
        fndeldum();

    }
    private void computeCalculation() {
        if(!Double.isNaN(valueOne)) {
            String Total = txtInput.getText().toString();
           /* if (Total.length()>0 || CURRENT_ACTION!='0'){
                Double dTotal=Double.parseDouble(Total);
                if(dTotal>0) {
                    valueTwo = Double.parseDouble(txtInput.getText().toString());
                    txtInfo.append(CURRENT_ACTION + txtInput.getText().toString());
                }
            }else {
                valueTwo=0.00;
            }*/
            if(Total.length()>0) {
                valueTwo = Double.parseDouble(txtInput.getText().toString());
                if(CURRENT_ACTION=='0') {
                    txtInfo.append(txtInput.getText().toString());
                }else{
                    txtInfo.append(CURRENT_ACTION + txtInput.getText().toString());
                }
                txtInput.setText(null);
            }else{
                valueTwo=0;
            }

            if(CURRENT_ACTION == ADDITION)
                valueOne = this.valueOne + valueTwo;
            else if(CURRENT_ACTION == SUBTRACTION)
                valueOne = this.valueOne - valueTwo;
            else if(CURRENT_ACTION == MULTIPLICATION)
                valueOne = this.valueOne * valueTwo;
            else if(CURRENT_ACTION == DIVISION)
                valueOne = this.valueOne / valueTwo;
        }
        else {
            try {
                txtInfo.append(txtInput.getText().toString());
                valueOne = Double.parseDouble(txtInput.getText().toString());
                //txtAmountDue.setText(""+valueOne);
                txtInput.setText(null);
            }
            catch (Exception e){}
        }
    }
    public void setItem(String vItemCode,String vDescription){
        ItemCode        =vItemCode;
        Description     =vDescription;
    }
    private void initData(){
        fndeldum();
        retLastNo();
        readyPrinter();
        retSalesPerson();
        retItemList();
    }
    private void fndeldum(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String del="delete from cloud_cus_inv_dt";
            db.addQuery(del);
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    private void fnadditem(){
        String UnitPrice=txtInput.getText().toString().replaceAll(",","");
        if(CURRENT_ACTION=='-'){
            UnitPrice="-"+UnitPrice;
        }
        if(UnitPrice.length()>0) {
            AddItemQuick fnadd = new AddItemQuick(this, ItemCode, Description,
                    "Quick Cash", "1", UnitPrice,
                    "Unit", "1", "0.00",
                    "0.00", "", "",
                    "", "0.00", "0.00",
                    "0.00");
            fnadd.execute();
        }
       /* if(dServiceCharges>0){
            AddMisc addMisc     =new AddMisc(this,"CS","0");
            String SvcChgAmount ="";
            try {
                SvcChgAmount= addMisc.execute().get();
                if(SvcChgAmount.length()>0) {
                    ServiceAmt = Double.parseDouble(SvcChgAmount);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }*/
    }
    private void retItemList(){
        rvItem.setHasFixedSize(true);
        lLayout = new GridLayoutManager(this, 4);
        rvItem.setLayoutManager(lLayout);
        rvItem.setItemAnimator(new DefaultItemAnimator());
        DownloaderItem dItem=new DownloaderItem(this,rvItem);
        dItem.execute();
    }
    private void readyPrinter(){
        try {
            String TypePrinter="";
            DBAdapter db = new DBAdapter(this);
            db.openDB();
            String qCom = "select CurCode,GSTNo from companysetup";
            Cursor rsCom = db.getQuery(qCom);
            while (rsCom.moveToNext()) {
                txtCurCode.setText(rsCom.getString(0));
            }
            Cursor cPrinter = db.getSettingPrint();
            while (cPrinter.moveToNext()) {
                TypePrinter = cPrinter.getString(1);
            }

            String qOtherSet="select ServiceCharges from tb_othersetting";
            Cursor rsOther=db.getQuery(qOtherSet);
            while(rsOther.moveToNext()){
                dServiceCharges=rsOther.getDouble(0);

            }

            db.closeDB();
            if (TypePrinter.equals("AIDL")) {
                AidlUtil.getInstance().connectPrinterService(this);
                AidlUtil.getInstance().initPrinter();
            } else if (TypePrinter.equals("Ipos AIDL")) {
                IposAidlUtil.getInstance().connectPrinterService(this);
                //IposAidlUtil.getInstance().initPrinter();
            } else {

            }

        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }


    private void retLastNo(){
        try {
            String Prefix = "";
            String LastNo = "";
            DBAdapter db = new DBAdapter(this);
            db.openDB();
            String vQuery = "select Prefix,LastNo from sys_runno_dt where RunNoCode='CS' ";
            Cursor cRunNo = db.getQuery(vQuery);
            while (cRunNo.moveToNext()) {
                Prefix = cRunNo.getString(0);
                LastNo = cRunNo.getString(1);
            }
            String Doc1No = Prefix + LastNo;
            txtDoc1No.setText(Doc1No);
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    private void retSalesPerson(){
        try{
            DBAdapter db=new DBAdapter(this);
            db.openDB();
            String SalesPersonCode="";
            int numrows=0;
            String qCheck="select count(*)as numrows from tb_salesperson";
            Cursor rsCheck=db.getQuery(qCheck);
            while(rsCheck.moveToNext()){
                numrows=rsCheck.getInt(0);
            }
            if(numrows==0) {
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
            }else {
                String qSales = "select IFNULL(SalesPersonCode,'')as  SalesPersonCode from tb_salesperson";
                Cursor rsSales = db.getQuery(qSales);
                while (rsSales.moveToNext()) {
                    SalesPersonCode = rsSales.getString(0);
                }
            }
           // txtSalesPersonCode.setText(SalesPersonCode);
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    private void fnsavecs(){
        String AmountDue    =txtAmountDue.getText().toString().replaceAll(",","");
        String PaidAmount   =txtInput.getText().toString().replaceAll(",","");
        String Doc1No       =txtDoc1No.getText().toString().replaceAll(",","");
        Double ServiceAmt   =0.00;
        Double CashAmt      =0.00;
        Double ChangeAmt    =0.00;
        Double dAmountDue   =Double.parseDouble(AmountDue);
        Double dPaidAmount  =0.00;

        if(AmountDue.isEmpty()){
            Toast.makeText(this, "Amount Due Cannot Empty", Toast.LENGTH_LONG).show();
        }else{
            if(PaidAmount.length()>0 ){
                 dPaidAmount  =Double.parseDouble(PaidAmount);
            }
            if(dPaidAmount>0 && dPaidAmount>dAmountDue) {
                CashAmt              = dAmountDue;
                ChangeAmt             =dPaidAmount-dAmountDue;
            }else{
                CashAmt              = dAmountDue;
            }

            txtInfo.setText(dPaidAmount+" - "+dAmountDue);
            txtAmountDue.setText("Change = "+ChangeAmt);
            /*Double CashAmt      = Amount+ServiceAmt;
            Double RoundingAmt  = Rounding.setRound(this, CashAmt);
            Double dAdjAmt      = RoundingAmt-CashAmt;
            String AdjAmt       = String.format(Locale.US, "%,.2f", dAdjAmt);*/
            SaveCS fnsave = new SaveCS(this, "QC",Doc1No, "", "",
                    "", "", CashAmt,
                    ChangeAmt, 0.00, 0.00,
                   "0.00","1");
            fnsave.execute();
        }
    }

    public void refreshNext(){
        finish();
        Intent mainIntent = new Intent(QuickCash.this, QuickCash.class);
        //mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }
}
