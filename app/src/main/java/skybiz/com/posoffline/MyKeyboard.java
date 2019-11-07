package skybiz.com.posoffline;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by 7 on 27/04/2018.
 */

public class MyKeyboard extends LinearLayout implements View.OnClickListener {
    public MyKeyboard(Context context){this(context,null,0);}
    public MyKeyboard(Context context, AttributeSet attrs){ this(context,attrs,0);}
    public MyKeyboard(Context context,AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        init(context, attrs);
    }
    private Button mButton1,mButton2,mButton3,mButton4,mButton5,mButton6,mButton7,mButton8,mButton9,
    mButton0,mButtonDot,mButtonDel;

    SparseArray<String> keyValues=new SparseArray<>();
    InputConnection inputConnection;
    private  void init(Context context, AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.button,this,true);
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
        mButtonDel=(Button)findViewById(R.id.btnDel);

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
        mButtonDel.setOnClickListener(this);

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
    }

    @Override
    public void onClick(View v){
        if(inputConnection==null)return;

        if(v.getId()==R.id.btnDel){
            CharSequence selectedText=inputConnection.getSelectedText(0);
            if(TextUtils.isEmpty(selectedText)){
                inputConnection.deleteSurroundingText(1,0);
            }else{
                inputConnection.commitText("",1);
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