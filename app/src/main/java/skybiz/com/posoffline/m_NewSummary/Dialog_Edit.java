package skybiz.com.posoffline.m_NewSummary;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewUOM.DownloaderUOM;
import skybiz.com.posoffline.ui_CreditNote.CreditNote;
import skybiz.com.posoffline.ui_SalesOrder.SalesOrder;

/**
 * Created by 7 on 14/12/2017.
 */

public class Dialog_Edit extends DialogFragment {
    View view;
    TextView txtItemCode,txtInput,txtQty,txtUnitPrice,txtDisRate,txtDisAmt,txtDescription,txtRunNo,txtUOM;
    Button btnc0,btnc1,btnc2,btnc3,btnc4,btnc5,btnc6,btnc7,btnc8,btnc9,btncDel,btncdot,btnQty,btnUnitPrice,btnBack,btnOpenUOM,btnCloseUOM;
    Button btnDisRate,btnDisAmt,btnDelItem;
    String DocType,BlankLine,DetailTaxCode,UOM,FactorQty;
    RecyclerView rvUOM;
    LinearLayout lnInput,lnbtn1,lnbtn2,lnbtn3,lnbtn4,lnModifier,lnUOM;
    private GridLayoutManager lLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_qty, container, false);
        rvUOM=(RecyclerView)view.findViewById(R.id.rvUOM);
        txtItemCode=(TextView)view.findViewById(R.id.txtItemCode);
        txtDescription=(TextView)view.findViewById(R.id.txtDescription);
        txtQty=(TextView)view.findViewById(R.id.txtQty);
        txtUnitPrice=(TextView)view.findViewById(R.id.txtUnitPrice);
        txtInput=(TextView)view.findViewById(R.id.txtInput);
        txtDisAmt=(TextView)view.findViewById(R.id.txtDisAmt);
        txtDisRate=(TextView)view.findViewById(R.id.txtDisRate);
        txtUOM=(TextView)view.findViewById(R.id.txtUOM);
        txtRunNo=(TextView)view.findViewById(R.id.txtRunNo);
        lnInput=(LinearLayout)view.findViewById(R.id.lnInput);
        lnbtn1=(LinearLayout)view.findViewById(R.id.lnbtn1);
        lnbtn2=(LinearLayout)view.findViewById(R.id.lnbtn2);
        lnbtn3=(LinearLayout)view.findViewById(R.id.lnbtn3);
        lnbtn4=(LinearLayout)view.findViewById(R.id.lnbtn4);
        lnModifier=(LinearLayout)view.findViewById(R.id.lnModifier);
        lnUOM=(LinearLayout)view.findViewById(R.id.lnUOM);

        btnc0= (Button) view.findViewById(R.id.btnc0);
        btnc1= (Button) view.findViewById(R.id.btnc1);
        btnc2= (Button) view.findViewById(R.id.btnc2);
        btnc3= (Button) view.findViewById(R.id.btnc3);
        btnc4= (Button) view.findViewById(R.id.btnc4);
        btnc5= (Button) view.findViewById(R.id.btnc5);
        btnc6= (Button) view.findViewById(R.id.btnc6);
        btnc7= (Button) view.findViewById(R.id.btnc7);
        btnc8= (Button) view.findViewById(R.id.btnc8);
        btnc9= (Button) view.findViewById(R.id.btnc9);
        btncdot= (Button) view.findViewById(R.id.btncdot);
        btncDel= (Button) view.findViewById(R.id.btncDel);
        btnQty= (Button) view.findViewById(R.id.btnQty);
        btnUnitPrice= (Button) view.findViewById(R.id.btnUnitPrice);
        btnBack= (Button) view.findViewById(R.id.btnBack);
        btnDelItem= (Button) view.findViewById(R.id.btnDelItem);
        btnDisAmt= (Button) view.findViewById(R.id.btnDisAmt);
        btnDisRate= (Button) view.findViewById(R.id.btnDisRate);
        btnOpenUOM=(Button) view.findViewById(R.id.btnOpenUOM);
        btnCloseUOM=(Button) view.findViewById(R.id.btnCloseUOM);
        final String ItemCode=this.getArguments().getString("CODE_KEY");
        final String Description=this.getArguments().getString("DESC_KEY");
        final String Qty=this.getArguments().getString("QTY_KEY");
        final String UnitPrice=this.getArguments().getString("UNITPRICE_KEY");
        final String HCDiscount=this.getArguments().getString("DISAMT_KEY");
        final String DisRate1=this.getArguments().getString("DISRATE_KEY");
        final String tUOM=this.getArguments().getString("UOM_KEY");
        final String RunNo=this.getArguments().getString("RUNNO_KEY");
        FactorQty=this.getArguments().getString("FACTORQTY_KEY");
        UOM=this.getArguments().getString("UOM_KEY");
        BlankLine=this.getArguments().getString("BLANKLINE_KEY");
        DetailTaxCode=this.getArguments().getString("DETAILTAXCODE_KEY");
        DocType=this.getArguments().getString("DOCTYPE_KEY");
        txtItemCode.setText(ItemCode);
        txtQty.setText(Qty);
        txtUOM.setText("("+tUOM+")");
        txtUnitPrice.setText(UnitPrice);
        txtDisAmt.setText(HCDiscount);
        txtDisRate.setText(DisRate1);
        txtDescription.setText(Description);
        txtRunNo.setText(RunNo);

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

        btnUnitPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String RunNo= (String) txtRunNo.getText();
                final String UnitPrice= (String) txtInput.getText();
                final String Qty= (String) txtQty.getText();
                String DisAmt= (String) txtDisAmt.getText();
                DisAmt= DisAmt.replaceAll(",","");
                final String DisRate= (String) txtDisRate.getText();
                fnedititem(getActivity(),RunNo,Qty,UnitPrice,DisAmt,DisRate);
               // fnedititem(getActivity(),vItemCode,Qty,UnitPrice,DisAmt,DisRate);
               // ((SalesOrder) getActivity()).refreshOrder();
               // dismiss();
            }
        });

        btnQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String RunNo= (String) txtRunNo.getText();
                final String Qty= (String) txtInput.getText();
                String UnitPrice= (String) txtUnitPrice.getText();
                UnitPrice= UnitPrice.replaceAll(",","");
                String DisAmt= (String) txtDisAmt.getText();
                DisAmt= DisAmt.replaceAll(",","");
                final String DisRate= (String) txtDisRate.getText();
                fnedititem(getActivity(),RunNo,Qty,UnitPrice,DisAmt,DisRate);
                //fnedititem(getActivity(),vItemCode,Qty,UnitPrice,DisAmt,DisRate);
              //  ((SalesOrder) getActivity()).refreshOrder();
              //  dismiss();
            }
        });
        btnDisRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String RunNo= (String) txtRunNo.getText();
                final String Qty= (String) txtQty.getText();
                String UnitPrice= (String) txtUnitPrice.getText();
                final String DisRate= (String) txtInput.getText();
                UnitPrice= UnitPrice.replaceAll(",","");
                fnedititem(getActivity(),RunNo,Qty,UnitPrice,"0",DisRate);
               // fnedititem(getActivity(),vItemCode,Qty,UnitPrice,"0",DisRate);
               // ((SalesOrder) getActivity()).refreshOrder();
              //  dismiss();
            }
        });

        btnDisAmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String RunNo= (String) txtRunNo.getText();
                final String Qty= (String) txtQty.getText();
                String UnitPrice= (String) txtUnitPrice.getText();
                final String DisAmt= (String) txtInput.getText();
                UnitPrice= UnitPrice.replaceAll(",","");
                fnedititem(getActivity(),RunNo,Qty,UnitPrice,DisAmt,"0");
               // fnedititem(getActivity(),vItemCode,Qty,UnitPrice,DisAmt,"0");
               // ((SalesOrder) getActivity()).refreshOrder();
               // dismiss();
            }
        });

        btnDelItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        btnOpenUOM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BlankLine.equals("0")) {
                    activeUOM();
                }else{
                    Toast.makeText(getContext(),"Item Miscellaneous cannot change UOM", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCloseUOM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deactiveUOM();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        getDialog().setTitle("EDIT PARTICULAR ITEM");
        return view;
    }
    private void retUOM(){
        String ItemCode=txtItemCode.getText().toString();
        rvUOM.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rvUOM.setLayoutManager(lLayout);
        rvUOM.setItemAnimator(new DefaultItemAnimator());
        DownloaderUOM downloaderUOM=new DownloaderUOM(getActivity(),ItemCode,rvUOM,Dialog_Edit.this);
        downloaderUOM.execute();
    }
    private void activeUOM(){
        retUOM();
        lnInput.setVisibility(View.GONE);
        lnbtn1.setVisibility(View.GONE);
        lnbtn2.setVisibility(View.GONE);
        lnbtn3.setVisibility(View.GONE);
        lnbtn4.setVisibility(View.GONE);
        btnOpenUOM.setVisibility(View.GONE);
        btnCloseUOM.setVisibility(View.VISIBLE);
        lnModifier.setVisibility(View.GONE);
        lnUOM.setVisibility(View.VISIBLE);
    }
    private void deactiveUOM(){
        lnInput.setVisibility(View.VISIBLE);
        lnbtn1.setVisibility(View.VISIBLE);
        lnbtn2.setVisibility(View.VISIBLE);
        lnbtn3.setVisibility(View.VISIBLE);
        lnbtn4.setVisibility(View.VISIBLE);
        btnOpenUOM.setVisibility(View.VISIBLE);
        btnCloseUOM.setVisibility(View.GONE);
        lnUOM.setVisibility(View.GONE);
    }
    public void setUOM(String UOM, String Price, String UOMFactor){
        final String RunNo= (String) txtRunNo.getText();
        final String vQty= (String) txtQty.getText();
        String vUnitPrice= Price;
        vUnitPrice=vUnitPrice.replaceAll(",","");
        String vHCDiscount= (String) txtDisAmt.getText();
        vHCDiscount= vHCDiscount.replaceAll(",","");
        final String vDisRate1= (String) txtDisRate.getText();
        EditItem2 editItem=new EditItem2(getActivity(),DocType,RunNo,vQty,vUnitPrice,UOM,UOMFactor,DetailTaxCode,vHCDiscount,vDisRate1);
        editItem.execute();
        refreshData();
       // EditItem editItem=new EditItem(c,RunNo,vItemCode, Qty, UnitPrice,DisAmt,DisRate,UOM,UOMFactor);
       // editItem.execute();
       // ((SalesOrder) getActivity()).refreshNext();
      //  dismiss();
    }
    private void addNumberC(String NewText){
        String OldText= (String) txtInput.getText();
        if(OldText.equals("0.00")){
            OldText="";
        }
        txtInput.setText(OldText+NewText);
    }

    private void minNumberC(){
        String OldText= (String) txtInput.getText();
        if (OldText != null && OldText.length() > 0 ) {
            OldText = OldText.substring(0, OldText.length() - 1);
        }else{
            OldText ="0";
        }
        txtInput.setText(OldText);
    }
    public void fnedititem(Context c, String RunNo,String vQty, String vUnitPrice, String vHCDiscount, String vDisRate1){
        EditItem2 editItem=new EditItem2(c,DocType,RunNo,vQty,vUnitPrice,UOM,FactorQty,DetailTaxCode,vHCDiscount,vDisRate1);
        editItem.execute();
        refreshData();
    }
    private void delete(){
        String RunNo=txtRunNo.getText().toString();
        DelOrder fndel=new DelOrder(getActivity(),RunNo);
        fndel.execute();
        refreshData();
    }
    private void refreshData(){
        if(DocType.equals("CusCN")){
            ((CreditNote) getActivity()).refreshOrder();
        }else if(DocType.equals("SO")){
            ((SalesOrder) getActivity()).refreshOrder();
        }
        dismiss();
    }
}
