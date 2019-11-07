package skybiz.com.posoffline;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Locale;

import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 27/04/2018.
 */

public class KeyboardPay extends LinearLayout implements View.OnClickListener {
    public KeyboardPay(Context context){this(context,null,0);}
    public KeyboardPay(Context context, AttributeSet attrs){ this(context,attrs,0);}
    public KeyboardPay(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        init(context, attrs);
    }
    private Button mButton1,mButton2,mButton3,mButton4,mButton5,mButton6,mButton7,mButton8,mButton9,
    mButton0,mButtonDot,mButton10,mButton20,mButton50,mButton100;
    private LinearLayout lnDel;

    private String FixButton1="",FixButton2="",FixButton3="",FixButton4="";
    private String FixButtonVal1="",FixButtonVal2="",FixButtonVal3="",FixButtonVal4="";

    SparseArray<String> keyValues=new SparseArray<>();
    InputConnection inputConnection;
    private  void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.button_payment,this,true);
        mButton1=(Button)findViewById(R.id.btn1);
        mButton2=(Button)findViewById(R.id.btn2);
        mButton3=(Button)findViewById(R.id.btn3);
        mButton4=(Button)findViewById(R.id.btn4);
        mButton5=(Button)findViewById(R.id.btn5);
        mButton6=(Button)findViewById(R.id.btn6);
        mButton7=(Button)findViewById(R.id.btn7);
        mButton8=(Button)findViewById(R.id.btn8);
        mButton9=(Button)findViewById(R.id.btn9);
        mButton0=(Button)findViewById(R.id.btn0);
        mButtonDot=(Button)findViewById(R.id.btnDot);
        lnDel=(LinearLayout) findViewById(R.id.lnDel);
        mButton10=(Button)findViewById(R.id.btn10);
        mButton20=(Button)findViewById(R.id.btn20);
        mButton50=(Button)findViewById(R.id.btn50);
        mButton100=(Button)findViewById(R.id.btn100);

        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        mButton5.setOnClickListener(this);
        mButton6.setOnClickListener(this);
        mButton7.setOnClickListener(this);
        mButton8.setOnClickListener(this);
        mButton9.setOnClickListener(this);
        mButton0.setOnClickListener(this);
        mButtonDot.setOnClickListener(this);
        lnDel.setOnClickListener(this);
        mButton10.setOnClickListener(this);
        mButton20.setOnClickListener(this);
        mButton50.setOnClickListener(this);
        mButton100.setOnClickListener(this);

        setCurCode(context);
        keyValues.put(R.id.btn1,"1");
        keyValues.put(R.id.btn2,"2");
        keyValues.put(R.id.btn3,"3");
        keyValues.put(R.id.btn4,"4");
        keyValues.put(R.id.btn5,"5");
        keyValues.put(R.id.btn6,"6");
        keyValues.put(R.id.btn7,"7");
        keyValues.put(R.id.btn8,"8");
        keyValues.put(R.id.btn9,"9");
        keyValues.put(R.id.btn0,"0");
        keyValues.put(R.id.btnDot,".");
        keyValues.put(R.id.btn10,FixButtonVal1);
        keyValues.put(R.id.btn20,FixButtonVal2);
        keyValues.put(R.id.btn50,FixButtonVal3);
        keyValues.put(R.id.btn100,FixButtonVal4);

    }

    private void setCurCode(Context c){
        try{
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String qCom="select CurCode,GSTNo from companysetup";
            Cursor rsCom=db.getQuery(qCom);
            String CurCode="";
            while(rsCom.moveToNext()){
                CurCode=rsCom.getString(0);

            }
            if(CurCode.equals("RP")){
                FixButton1="10K";
                FixButton2="20K";
                FixButton3="50K";
                FixButton4="100K";
                FixButtonVal1="10000";
                FixButtonVal2="20000";
                FixButtonVal3="50000";
                FixButtonVal4="100000";
            }else{
                FixButton1="10";
                FixButton2="20";
                FixButton3="50";
                FixButton4="100";
                FixButtonVal1="10";
                FixButtonVal2="20";
                FixButtonVal3="50";
                FixButtonVal4="100";
            }
            mButton10.setText(CurCode+FixButton1);
            mButton20.setText(CurCode+FixButton2);
            mButton50.setText(CurCode+FixButton3);
            mButton100.setText(CurCode+FixButton4);
            db.closeDB();

        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v){
        if(inputConnection==null)return;

        if(v.getId()==R.id.lnDel) {
            CharSequence selectedText = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.deleteSurroundingText(1, 0);
            } else {
                inputConnection.commitText("", 1);
            }
        } else if(v.getId()==R.id.btn10){
            CharSequence selectedText = inputConnection.getTextBeforeCursor(100,0);
            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.commitText(FixButtonVal1, 1);
            } else {
                CharSequence oldText = inputConnection.getTextBeforeCursor(100,0);
                //Log.d("NOT EMPTY",oldText.toString());
                Double dAdd=Double.parseDouble(oldText.toString().replaceAll(",",""))+
                        Double.parseDouble(FixButtonVal1.toString().replaceAll(",",""));
                String newNum=String.format(Locale.US, "%,.2f", dAdd);
                inputConnection.deleteSurroundingText(100, 0);
                inputConnection.commitText(String.valueOf(newNum), 1);

            }
        } else if(v.getId()==R.id.btn20){
            CharSequence selectedText = inputConnection.getTextBeforeCursor(100,0);
            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.commitText(FixButtonVal2, 1);
            } else {
                CharSequence oldText = inputConnection.getTextBeforeCursor(100,0);
                Double dAdd=Double.parseDouble(oldText.toString().replaceAll(",",""))+
                Double.parseDouble(FixButtonVal2.toString().replaceAll(",",""));
                String newNum=String.format(Locale.US, "%,.2f", dAdd);
                inputConnection.deleteSurroundingText(100, 0);
                inputConnection.commitText(String.valueOf(newNum), 1);

            }
        } else if(v.getId()==R.id.btn50){
            CharSequence selectedText = inputConnection.getTextBeforeCursor(100,0);
            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.commitText(FixButtonVal3, 1);
            } else {
                CharSequence oldText = inputConnection.getTextBeforeCursor(100,0);
                Double dAdd=Double.parseDouble(oldText.toString().replaceAll(",",""))+
                Double.parseDouble(FixButtonVal3.toString().replaceAll(",",""));
                String newNum=String.format(Locale.US, "%,.2f", dAdd);
                inputConnection.deleteSurroundingText(100, 0);
                inputConnection.commitText(String.valueOf(newNum), 1);

            }
        } else if(v.getId()==R.id.btn100) {
            CharSequence selectedText = inputConnection.getTextBeforeCursor(100, 0);
            Log.d("BEFORE TEXT", "Before " + selectedText.toString());
            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.commitText(FixButtonVal4, 1);
            } else {
                CharSequence oldText = inputConnection.getTextBeforeCursor(100, 0);
                Double dAdd = Double.parseDouble(oldText.toString().replaceAll(",", ""))+
                Double.parseDouble(FixButtonVal4.toString().replaceAll(",",""));
                String newNum = String.format(Locale.US, "%,.2f", dAdd);
                inputConnection.deleteSurroundingText(100, 0);
                inputConnection.commitText(String.valueOf(newNum), 1);

            }
        }else if(v.getId()==R.id.btnDot){
                CharSequence selectedText = inputConnection.getTextBeforeCursor(100,0);
                Log.d("BEFORE TEXT", "Before "+selectedText.toString());
                if (TextUtils.isEmpty(selectedText)) {
                    inputConnection.commitText("0.", 1);
                }else{
                    inputConnection.commitText(".", 1);
                }
        }else{
            String value=keyValues.get(v.getId());
            inputConnection.commitText(value,1);
        }
    }
    public void setInputConnection(InputConnection ic){ this.inputConnection=ic;}
}

/*
https://stackoverflow.com/questions/9577304/how-to-make-an-android-custom-keyboard?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
 */