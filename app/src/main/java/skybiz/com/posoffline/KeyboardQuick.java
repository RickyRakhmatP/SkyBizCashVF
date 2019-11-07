package skybiz.com.posoffline;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by 7 on 27/04/2018.
 */

public class KeyboardQuick extends LinearLayout implements View.OnClickListener {
    public KeyboardQuick(Context context){this(context,null,0);}
    public KeyboardQuick(Context context, AttributeSet attrs){ this(context,attrs,0);}
    public KeyboardQuick(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        init(context, attrs);
    }
    private Button mButton1,mButton2,mButton3,mButton4,mButton5,mButton6,mButton7,mButton8,mButton9,
            mButton0,mButtonDot,mButtonnMinus,mButtonPlus,mButtonMultiplication,mButtonDel2;

    Boolean CapsYN=false;
    private LinearLayout lnDel,lnHideKey,lnCaps,lnUnCaps,ln1,ln2,lnKeyboard;

    SparseArray<String> keyValues=new SparseArray<>();
    InputConnection inputConnection;
    private  void init(Context context, AttributeSet attrs){

        LayoutInflater.from(context).inflate(R.layout.button_quick ,this,true);
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
        mButtonnMinus=(Button)findViewById(R.id.btnMinus);
        mButtonPlus=(Button)findViewById(R.id.btnPlus);
        mButtonMultiplication=(Button)findViewById(R.id.btnMultiplication);

        lnDel=(LinearLayout)findViewById(R.id.lnDel);
        lnHideKey=(LinearLayout)findViewById(R.id.lnHideKey);
        lnCaps=(LinearLayout)findViewById(R.id.lnCaps);
        lnUnCaps=(LinearLayout)findViewById(R.id.lnUnCaps);
        ln1=(LinearLayout)findViewById(R.id.ln1);
        ln2=(LinearLayout)findViewById(R.id.ln2);

        lnKeyboard=(LinearLayout)findViewById(R.id.lnKeyboard);

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
        mButtonPlus.setOnClickListener(this);
        mButtonnMinus.setOnClickListener(this);
        mButtonMultiplication.setOnClickListener(this);
        lnDel.setOnClickListener(this);

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
        keyValues.put(R.id.btnPlus,"+");
        keyValues.put(R.id.btnMinus,"-");
        keyValues.put(R.id.btnMultiplication,"*");

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
        } else {
            String value="";
            value = keyValues.get(v.getId());
            inputConnection.commitText(value, 1);
        }
    }
    public void setInputConnection(InputConnection ic){ this.inputConnection=ic;}

    private void setNormal(){

    }
    private void setCaps(){

    }

    public static void hideSoftKeyboard(KeyboardQuick activity) {
        activity.onFinishInflate();
    }
}

/*
https://stackoverflow.com/questions/9577304/how-to-make-an-android-custom-keyboard?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
 */