package skybiz.com.posoffline;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import skybiz.com.posoffline.ui_CashReceipt.Fragment_Summary;

/**
 * Created by 7 on 27/04/2018.
 */

public class KeyboardNew extends LinearLayout implements View.OnClickListener {
    public KeyboardNew(Context context){this(context,null,0);}
    public KeyboardNew(Context context, AttributeSet attrs){ this(context,attrs,0);}
    public KeyboardNew(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        init(context, attrs);
    }
    private Button mButton1,mButton2,mButton3,mButton4,mButton5,mButton6,mButton7,mButton8,mButton9,
            mButton0,mButtonDot,mButton3Zero,mButton2Zero,mButtonAlphabet,mButtonDel2;

    private Button mButtonA,mButtonB, mButtonC,mButtonD,mButtonE,mButtonF,
            mButtonG, mButtonH,mButtonI, mButtonJ,mButtonK,mButtonL,mButtonM,
            mButtonN,mButtonO,mButtonP,mButtonQ,mButtonR,mButtonS,mButtonT,mButtonU,
            mButtonV,mButtonW, mButtonX, mButtonY,mButtonZ,mButtonSpace,mButtonUnderScore,
            mButtonComma, mButtonSlash,mButtonBackSlash,mButtonMinus,mButtonOpenBracket,mButtonCloseBracket,
            mButtonOpenBracket2,mButtonCloseBracket2,mButtonAt,mButtonEqual,mButtonPlus,
            mButtonColon,mButtonQuote,mButtonSingleQuote,mButtonExc,mButtonNum,mButtonDollar,
            mButtonDiv,mButtonHat,mButtonAmp,mButtonAst,mButtonLess,mButtonMore,mButtonNumeric,
            mButtonOpenBr,mButtonCloseBr,mButtonVerBar;

    Boolean CapsYN=false;
    private LinearLayout lnDel,lnHideKey,lnCaps,lnUnCaps,ln1,ln2,lnKeyboard;

    SparseArray<String> keyValues=new SparseArray<>();
    SparseArray<String> keyValuesCaps = new SparseArray<>();
    InputConnection inputConnection;
    private  void init(Context context, AttributeSet attrs){

        LayoutInflater.from(context).inflate(R.layout.button_new ,this,true);
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
        mButtonDel2=(Button)findViewById(R.id.btnDel2);
        mButton2Zero=(Button)findViewById(R.id.btnDoubleZero);
        mButton3Zero=(Button)findViewById(R.id.btnTripleZero);
        mButtonAlphabet=(Button)findViewById(R.id.btnAlphabet);
        mButtonA = (Button) findViewById(R.id.btnA);
        mButtonB = (Button) findViewById(R.id.btnB);
        mButtonC = (Button) findViewById(R.id.btnC);
        mButtonD = (Button) findViewById(R.id.btnD);
        mButtonE = (Button) findViewById(R.id.btnE);
        mButtonF = (Button) findViewById(R.id.btnF);
        mButtonG = (Button) findViewById(R.id.btnG);
        mButtonH = (Button) findViewById(R.id.btnH);
        mButtonI = (Button) findViewById(R.id.btnI);
        mButtonJ = (Button) findViewById(R.id.btnJ);
        mButtonK = (Button) findViewById(R.id.btnK);
        mButtonL = (Button) findViewById(R.id.btnL);
        mButtonM = (Button) findViewById(R.id.btnM);
        mButtonN = (Button) findViewById(R.id.btnN);
        mButtonO = (Button) findViewById(R.id.btnO);
        mButtonP = (Button) findViewById(R.id.btnP);
        mButtonQ = (Button) findViewById(R.id.btnQ);
        mButtonR = (Button) findViewById(R.id.btnR);
        mButtonS = (Button) findViewById(R.id.btnS);
        mButtonT = (Button) findViewById(R.id.btnT);
        mButtonU = (Button) findViewById(R.id.btnU);
        mButtonV = (Button) findViewById(R.id.btnV);
        mButtonW = (Button) findViewById(R.id.btnW);
        mButtonX = (Button) findViewById(R.id.btnX);
        mButtonY = (Button) findViewById(R.id.btnY);
        mButtonZ = (Button) findViewById(R.id.btnZ);
        mButtonSpace = (Button) findViewById(R.id.btnSpace);
        mButtonUnderScore   = (Button) findViewById(R.id.btnUnderScore);
        mButtonComma        = (Button) findViewById(R.id.btnComma);
        mButtonSlash        = (Button) findViewById(R.id.btnSlash);
        mButtonBackSlash    = (Button) findViewById(R.id.btnBackSlash);
        mButtonMinus        = (Button) findViewById(R.id.btnMinus);
        mButtonOpenBracket  = (Button) findViewById(R.id.btnOpenBracket);
        mButtonCloseBracket  = (Button) findViewById(R.id.btnCloseBracket);
        mButtonOpenBracket2  = (Button) findViewById(R.id.btnOpenBracket2);
        mButtonCloseBracket2 = (Button) findViewById(R.id.btnCloseBracket2);
        mButtonAt            = (Button) findViewById(R.id.btnAt);
        mButtonEqual         = (Button) findViewById(R.id.btnEqual);
        mButtonPlus          = (Button) findViewById(R.id.btnPlus);
        mButtonColon         = (Button) findViewById(R.id.btnColon);
        mButtonQuote         = (Button) findViewById(R.id.btnQuote);
        mButtonSingleQuote   = (Button) findViewById(R.id.btnSingleQuote);
        mButtonExc           = (Button) findViewById(R.id.btnExc);
        mButtonNum          = (Button) findViewById(R.id.btnNum);
        mButtonDollar       = (Button) findViewById(R.id.btnDollar);
        mButtonDiv          = (Button) findViewById(R.id.btnDiv);
        mButtonHat          = (Button) findViewById(R.id.btnHat);
        mButtonAmp          = (Button) findViewById(R.id.btnAmp);
        mButtonAst          = (Button) findViewById(R.id.btnAst);
        mButtonLess         = (Button) findViewById(R.id.btnLess);
        mButtonMore         = (Button) findViewById(R.id.btnMore);
        mButtonNumeric      = (Button) findViewById(R.id.btnNumeric);
        mButtonOpenBr       = (Button) findViewById(R.id.btnOpenBr);
        mButtonCloseBr      = (Button) findViewById(R.id.btnCloseBr);
        mButtonVerBar       = (Button) findViewById(R.id.btnVerBar);

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
        mButtonDel2.setOnClickListener(this);
        mButton2Zero.setOnClickListener(this);
        mButton3Zero.setOnClickListener(this);
        mButton3Zero.setOnClickListener(this);
        lnDel.setOnClickListener(this);
        lnHideKey.setOnClickListener(this);
        lnCaps.setOnClickListener(this);
        lnUnCaps.setOnClickListener(this);
        mButtonAlphabet.setOnClickListener(this);
        mButtonA.setOnClickListener(this);
        mButtonB.setOnClickListener(this);
        mButtonC.setOnClickListener(this);
        mButtonD.setOnClickListener(this);
        mButtonE.setOnClickListener(this);
        mButtonF.setOnClickListener(this);
        mButtonG.setOnClickListener(this);
        mButtonH.setOnClickListener(this);
        mButtonI.setOnClickListener(this);
        mButtonJ.setOnClickListener(this);
        mButtonK.setOnClickListener(this);
        mButtonL.setOnClickListener(this);
        mButtonM.setOnClickListener(this);
        mButtonN.setOnClickListener(this);
        mButtonO.setOnClickListener(this);
        mButtonP.setOnClickListener(this);
        mButtonQ.setOnClickListener(this);
        mButtonR.setOnClickListener(this);
        mButtonS.setOnClickListener(this);
        mButtonT.setOnClickListener(this);
        mButtonU.setOnClickListener(this);
        mButtonV.setOnClickListener(this);
        mButtonW.setOnClickListener(this);
        mButtonX.setOnClickListener(this);
        mButtonY.setOnClickListener(this);
        mButtonZ.setOnClickListener(this);

        mButtonSpace.setOnClickListener(this);
        mButtonUnderScore.setOnClickListener(this);
        mButtonComma.setOnClickListener(this);
        mButtonSlash.setOnClickListener(this);
        mButtonBackSlash.setOnClickListener(this);
        mButtonMinus.setOnClickListener(this);
        mButtonOpenBracket.setOnClickListener(this);
        mButtonCloseBracket.setOnClickListener(this);
        mButtonOpenBracket2.setOnClickListener(this);
        mButtonCloseBracket2.setOnClickListener(this);
        mButtonAt.setOnClickListener(this);
        mButtonEqual.setOnClickListener(this);
        mButtonPlus.setOnClickListener(this);
        mButtonColon.setOnClickListener(this);
        mButtonQuote.setOnClickListener(this);
        mButtonSingleQuote.setOnClickListener(this);
        mButtonExc.setOnClickListener(this);
        mButtonNum.setOnClickListener(this);
        mButtonDollar.setOnClickListener(this);
        mButtonDiv.setOnClickListener(this);
        mButtonHat.setOnClickListener(this);
        mButtonAmp.setOnClickListener(this);
        mButtonAst.setOnClickListener(this);
        mButtonLess.setOnClickListener(this);
        mButtonMore.setOnClickListener(this);
        mButtonNumeric.setOnClickListener(this);
        mButtonOpenBr.setOnClickListener(this);
        mButtonCloseBr.setOnClickListener(this);
        mButtonVerBar.setOnClickListener(this);

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
        keyValues.put(R.id.btnDoubleZero,"00");
        keyValues.put(R.id.btnTripleZero,"000");
        keyValues.put(R.id.btnA, "a");
        keyValues.put(R.id.btnB, "b");
        keyValues.put(R.id.btnC, "c");
        keyValues.put(R.id.btnD, "d");
        keyValues.put(R.id.btnE, "e");
        keyValues.put(R.id.btnF, "f");
        keyValues.put(R.id.btnG, "g");
        keyValues.put(R.id.btnH, "h");
        keyValues.put(R.id.btnI, "i");
        keyValues.put(R.id.btnJ, "j");
        keyValues.put(R.id.btnK, "k");
        keyValues.put(R.id.btnL, "l");
        keyValues.put(R.id.btnM, "m");
        keyValues.put(R.id.btnN, "n");
        keyValues.put(R.id.btnO, "o");
        keyValues.put(R.id.btnP, "p");
        keyValues.put(R.id.btnQ, "q");
        keyValues.put(R.id.btnR, "r");
        keyValues.put(R.id.btnS, "s");
        keyValues.put(R.id.btnT, "t");
        keyValues.put(R.id.btnU, "u");
        keyValues.put(R.id.btnV, "v");
        keyValues.put(R.id.btnW, "w");
        keyValues.put(R.id.btnX, "x");
        keyValues.put(R.id.btnY, "y");
        keyValues.put(R.id.btnZ, "z");


        keyValues.put(R.id.btnSpace, " ");
        keyValues.put(R.id.btnUnderScore, "_");
        keyValues.put(R.id.btnComma, ",");
        keyValues.put(R.id.btnSlash, "/");
        keyValues.put(R.id.btnBackSlash, "\\");
        keyValues.put(R.id.btnMinus, "-");
        keyValues.put(R.id.btnOpenBracket, "(");
        keyValues.put(R.id.btnCloseBracket, ")");
        keyValues.put(R.id.btnOpenBracket2, "{");
        keyValues.put(R.id.btnCloseBracket2, "}");
        keyValues.put(R.id.btnAt, "@");
        keyValues.put(R.id.btnEqual, "=");
        keyValues.put(R.id.btnColon, ":");
        keyValues.put(R.id.btnQuote, "\"");
        keyValues.put(R.id.btnSingleQuote, "'");
        keyValues.put(R.id.btnExc, "!");
        keyValues.put(R.id.btnNum, "#");
        keyValues.put(R.id.btnDollar, "$");
        keyValues.put(R.id.btnDiv, "%");
        keyValues.put(R.id.btnHat, "^");
        keyValues.put(R.id.btnHat, "&");
        keyValues.put(R.id.btnAst, "*");
        keyValues.put(R.id.btnLess, "<");
        keyValues.put(R.id.btnMore, ">");
        keyValues.put(R.id.btnOpenBr, "[");
        keyValues.put(R.id.btnCloseBr, "]");
        keyValues.put(R.id.btnVerBar, "|");

        keyValuesCaps.put(R.id.btnA, "A");
        keyValuesCaps.put(R.id.btnB, "B");
        keyValuesCaps.put(R.id.btnC, "C");
        keyValuesCaps.put(R.id.btnD, "D");
        keyValuesCaps.put(R.id.btnE, "E");
        keyValuesCaps.put(R.id.btnF, "F");
        keyValuesCaps.put(R.id.btnG, "G");
        keyValuesCaps.put(R.id.btnH, "H");
        keyValuesCaps.put(R.id.btnI, "I");
        keyValuesCaps.put(R.id.btnJ, "J");
        keyValuesCaps.put(R.id.btnK, "K");
        keyValuesCaps.put(R.id.btnL, "L");
        keyValuesCaps.put(R.id.btnM, "M");
        keyValuesCaps.put(R.id.btnN, "N");
        keyValuesCaps.put(R.id.btnO, "O");
        keyValuesCaps.put(R.id.btnP, "P");
        keyValuesCaps.put(R.id.btnQ, "Q");
        keyValuesCaps.put(R.id.btnR, "R");
        keyValuesCaps.put(R.id.btnS, "S");
        keyValuesCaps.put(R.id.btnT, "T");
        keyValuesCaps.put(R.id.btnU, "U");
        keyValuesCaps.put(R.id.btnV, "V");
        keyValuesCaps.put(R.id.btnW, "Q");
        keyValuesCaps.put(R.id.btnX, "X");
        keyValuesCaps.put(R.id.btnY, "Y");
        keyValuesCaps.put(R.id.btnZ, "Z");

        keyValuesCaps.put(R.id.btnSpace, " ");
        keyValuesCaps.put(R.id.btnUnderScore, "_");
        keyValuesCaps.put(R.id.btnComma, ",");
        keyValuesCaps.put(R.id.btnSlash, "/");
        keyValuesCaps.put(R.id.btnBackSlash, "\\");
        keyValuesCaps.put(R.id.btnMinus, "-");
        keyValuesCaps.put(R.id.btnOpenBracket, "(");
        keyValuesCaps.put(R.id.btnCloseBracket, ")");
        keyValuesCaps.put(R.id.btnOpenBracket2, "{");
        keyValuesCaps.put(R.id.btnCloseBracket2, "}");
        keyValuesCaps.put(R.id.btnAt, "@");
        keyValuesCaps.put(R.id.btnEqual, "=");
        keyValuesCaps.put(R.id.btnColon, ":");
        keyValuesCaps.put(R.id.btnQuote, "\"");
        keyValuesCaps.put(R.id.btnSingleQuote, "'");
        keyValuesCaps.put(R.id.btnExc, "!");
        keyValuesCaps.put(R.id.btnNum, "#");
        keyValuesCaps.put(R.id.btnDollar, "$");
        keyValuesCaps.put(R.id.btnDiv, "%");
        keyValuesCaps.put(R.id.btnHat, "^");
        keyValuesCaps.put(R.id.btnHat, "&");
        keyValuesCaps.put(R.id.btnAst, "*");
        keyValuesCaps.put(R.id.btnLess, "<");
        keyValuesCaps.put(R.id.btnMore, ">");
        keyValuesCaps.put(R.id.btnOpenBr, "[");
        keyValuesCaps.put(R.id.btnCloseBr, "]");
        keyValuesCaps.put(R.id.btnVerBar, "|");
        keyValuesCaps.put(R.id.btn1,"1");
        keyValuesCaps.put(R.id.btn2,"2");
        keyValuesCaps.put(R.id.btn3,"3");
        keyValuesCaps.put(R.id.btn4,"4");
        keyValuesCaps.put(R.id.btn5,"5");
        keyValuesCaps.put(R.id.btn6,"6");
        keyValuesCaps.put(R.id.btn7,"7");
        keyValuesCaps.put(R.id.btn8,"8");
        keyValuesCaps.put(R.id.btn9,"9");
        keyValuesCaps.put(R.id.btn0,"0");
        keyValuesCaps.put(R.id.btnDot,".");
        keyValuesCaps.put(R.id.btnDoubleZero,"00");
        keyValuesCaps.put(R.id.btnTripleZero,"000");

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
        }else if(v.getId()==R.id.btnDel2) {
                CharSequence selectedText = inputConnection.getSelectedText(0);
                if (TextUtils.isEmpty(selectedText)) {
                    inputConnection.deleteSurroundingText(1, 0);
                } else {
                    inputConnection.commitText("", 1);
                }
        } else if (v.getId() == R.id.lnCaps) {
            CapsYN=true;
            setCaps();
        }else if (v.getId() == R.id.lnUnCaps) {
            CapsYN=false;
            setNormal();
        }else if (v.getId() == R.id.btnAlphabet) {
            CapsYN=false;
            setNormal();
            ln1.setVisibility(View.GONE);
            ln2.setVisibility(View.VISIBLE);
        }else if (v.getId() == R.id.btnNumeric) {
            ln1.setVisibility(View.VISIBLE);
            ln2.setVisibility(View.GONE);
        }else if(v.getId() == R.id.lnHideKey){
            //lnKeyboard.setVisibility(View.GONE);
            //hideSoftKeyboard(this);
        } else {
            String value="";
            if (CapsYN==true){
                value=keyValuesCaps.get(v.getId());
            }else{
                value = keyValues.get(v.getId());
            }
            inputConnection.commitText(value, 1);
        }
    }
    public void setInputConnection(InputConnection ic){ this.inputConnection=ic;}

    private void setNormal(){
        mButtonA.setText("a");
        mButtonB.setText("b");
        mButtonC.setText("c");
        mButtonD.setText("d");
        mButtonE.setText("e");
        mButtonF.setText("f");
        mButtonG.setText("g");
        mButtonH.setText("h");
        mButtonI.setText("i");
        mButtonJ.setText("j");
        mButtonK.setText("k");
        mButtonL.setText("l");
        mButtonM.setText("m");
        mButtonN.setText("n");
        mButtonO.setText("o");
        mButtonP.setText("p");
        mButtonQ.setText("q");
        mButtonR.setText("r");
        mButtonS.setText("s");
        mButtonT.setText("t");
        mButtonU.setText("u");
        mButtonV.setText("v");
        mButtonW.setText("w");
        mButtonX.setText("x");
        mButtonY.setText("y");
        mButtonZ.setText("z");
        lnCaps.setVisibility(View.VISIBLE);
        lnUnCaps.setVisibility(View.GONE);
    }
    private void setCaps(){
        mButtonA.setText("A");
        mButtonB.setText("B");
        mButtonC.setText("C");
        mButtonD.setText("D");
        mButtonE.setText("E");
        mButtonF.setText("F");
        mButtonG.setText("G");
        mButtonH.setText("H");
        mButtonI.setText("I");
        mButtonJ.setText("J");
        mButtonK.setText("K");
        mButtonL.setText("L");
        mButtonM.setText("M");
        mButtonN.setText("N");
        mButtonO.setText("O");
        mButtonP.setText("P");
        mButtonQ.setText("Q");
        mButtonR.setText("R");
        mButtonS.setText("S");
        mButtonT.setText("T");
        mButtonU.setText("U");
        mButtonV.setText("V");
        mButtonW.setText("W");
        mButtonX.setText("X");
        mButtonY.setText("Y");
        mButtonZ.setText("Z");
        lnUnCaps.setVisibility(View.VISIBLE);
        lnCaps.setVisibility(View.GONE);

    }

    public static void hideSoftKeyboard(KeyboardNew activity) {
        activity.onFinishInflate();
    }
}

/*
https://stackoverflow.com/questions/9577304/how-to-make-an-android-custom-keyboard?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
 */