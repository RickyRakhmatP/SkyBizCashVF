package skybiz.com.posoffline.ui_CashReceipt.m_Order;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Locale;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_PriceMatrix.DownloadPriceMatrix;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_Modifier.DownloaderModifier;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_UOM.DownloaderUOM;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 14/12/2017.
 */

public class Dialog_Qty extends DialogFragment {
    View view;
    TextView txtItemCode,txtInput,txtQty,txtUnitPrice,txtDisRate,txtDisAmt,txtDescription,txtRunNo,tModifier,txtUOM;
    Button btnc0,btnc1,btnc2,btnc3,btnc4,btnc5,btnc6,btnc7,btnc8,btnc9,btncDel,btncdot,btnQty,btnUnitPrice,btnBack;
    Button btnDisRate,btnDisAmt,btnDelItem,
            btnOpenModifier,btnCloseModifier,btnSaveModifier,
            btnOpenUOM,btnCloseUOM,btnChPricingMatrix;
    String IPAddress,DBName,UserName,Password,Port;
    EditText txtModifier;
    LinearLayout lnInput,lnbtn1,lnbtn2,
            lnbtn3,lnbtn4,lnModifier,
            lnUOM,lnPriceMatrix;
    private GridLayoutManager lLayout;
    RecyclerView rvModifier,rvUOM,rvPriceMatrix;
    String ItemGroup,BlankLine,DetailTaxCode,
            UnitCost,ServiceChargeYN;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_qty, container, false);
        lnInput=(LinearLayout)view.findViewById(R.id.lnInput);
        lnbtn1=(LinearLayout)view.findViewById(R.id.lnbtn1);
        lnbtn2=(LinearLayout)view.findViewById(R.id.lnbtn2);
        lnbtn3=(LinearLayout)view.findViewById(R.id.lnbtn3);
        lnbtn4=(LinearLayout)view.findViewById(R.id.lnbtn4);
        lnModifier=(LinearLayout)view.findViewById(R.id.lnModifier);
        lnUOM=(LinearLayout)view.findViewById(R.id.lnUOM);
        lnPriceMatrix=(LinearLayout)view.findViewById(R.id.lnPriceMatrix);
        btnOpenModifier=(Button)view.findViewById(R.id.btnOpenModifier);
        btnCloseModifier=(Button)view.findViewById(R.id.btnCloseModifier);
        btnSaveModifier=(Button)view.findViewById(R.id.btnSaveModifier);
        btnOpenUOM=(Button)view.findViewById(R.id.btnOpenUOM);
        btnCloseUOM=(Button)view.findViewById(R.id.btnCloseUOM);
        btnChPricingMatrix=(Button)view.findViewById(R.id.btnChPricingMatrix);
        txtModifier=(EditText)view.findViewById(R.id.txtModifier);

        rvModifier=(RecyclerView)view.findViewById(R.id.rvModifier);
        rvUOM=(RecyclerView)view.findViewById(R.id.rvUOM);
        rvPriceMatrix=(RecyclerView) view.findViewById(R.id.rvPriceMatrix);
        tModifier=(TextView)view.findViewById(R.id.tModifier);
        txtRunNo=(TextView)view.findViewById(R.id.txtRunNo);
        txtItemCode=(TextView)view.findViewById(R.id.txtItemCode);
        txtDescription=(TextView)view.findViewById(R.id.txtDescription);
        txtQty=(TextView)view.findViewById(R.id.txtQty);
        txtUnitPrice=(TextView)view.findViewById(R.id.txtUnitPrice);
        txtInput=(TextView)view.findViewById(R.id.txtInput);
        txtDisAmt=(TextView)view.findViewById(R.id.txtDisAmt);
        txtDisRate=(TextView)view.findViewById(R.id.txtDisRate);
        txtUOM=(TextView)view.findViewById(R.id.txtUOM);

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
        String jsonData=this.getArguments().getString("JSONDATA_KEY");
        initData(jsonData);
       /* final String RunNo=this.getArguments().getString("RUNNO_KEY");
        final String ItemCode=this.getArguments().getString("CODE_KEY");
        final String Description=this.getArguments().getString("DESC_KEY");
        final String Qty=this.getArguments().getString("QTY_KEY");
        final String UnitPrice=this.getArguments().getString("UNITPRICE_KEY");
        final String HCDiscount=this.getArguments().getString("DISAMT_KEY");
        final String DisRate1=this.getArguments().getString("DISRATE_KEY");
        final String ModifierYN=this.getArguments().getString("YN_KEY");
        final String Modifier=this.getArguments().getString("DESC2_KEY");
        final String UOM=this.getArguments().getString("UOM_KEY");
        final String FactorQty=this.getArguments().getString("FACTORQTY_KEY");
        BlankLine=this.getArguments().getString("BLANKLINE_KEY");
        DetailTaxCode=this.getArguments().getString("DETAILTAXCODE_KEY");

        ItemGroup=this.getArguments().getString("GROUP_KEY");
        txtItemCode.setText(ItemCode);
        txtQty.setText(Qty);
        txtUOM.setText("("+UOM+")");
        txtUnitPrice.setText(UnitPrice);
        txtDisAmt.setText(HCDiscount);
        txtDisRate.setText(DisRate1);
        txtDescription.setText(Description);
        txtRunNo.setText(RunNo);
        tModifier.setText(Modifier);
        if(ModifierYN.equals("1")){
            activeModifier();
        }else{
            deactiveModifier();
        }*/

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
                final String vItemCode  = (String) txtItemCode.getText();
                String Description      = txtDescription.getText().toString();
                final String UnitPrice  = (String) txtInput.getText();
                final String Qty        = (String) txtQty.getText();
                String DisAmt           = (String) txtDisAmt.getText();
                DisAmt                  = DisAmt.replaceAll(",","");
                final String DisRate    = (String) txtDisRate.getText();
                fnedititem(getActivity(),vItemCode,Description,
                        Qty,UnitPrice,DisAmt,
                        DisRate);
                ((CashReceipt) getActivity()).refreshNext();
                dismiss();
            }
        });

        btnQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String vItemCode  = (String) txtItemCode.getText();
                String Description      = txtDescription.getText().toString();
                final String Qty        = (String) txtInput.getText();
                String UnitPrice        = (String) txtUnitPrice.getText();
                UnitPrice               = UnitPrice.replaceAll(",","");
                String DisAmt           = (String) txtDisAmt.getText();
                DisAmt                  = DisAmt.replaceAll(",","");
                final String DisRate    = (String) txtDisRate.getText();
                fnedititem(getActivity(),vItemCode,Description,
                        Qty,UnitPrice,DisAmt,DisRate);
                ((CashReceipt) getActivity()).refreshNext();
                dismiss();
            }
        });
        btnDisRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String vItemCode  = (String) txtItemCode.getText();
                String Description      = txtDescription.getText().toString();
                final String Qty        = (String) txtQty.getText();
                String UnitPrice        = (String) txtUnitPrice.getText();
                final String DisRate    = (String) txtInput.getText();
                UnitPrice               = UnitPrice.replaceAll(",","");
                fnedititem(getActivity(),vItemCode,Description,
                        Qty,UnitPrice,"0",
                        DisRate);
                ((CashReceipt) getActivity()).refreshNext();
                dismiss();
            }
        });

        btnDisAmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String vItemCode  = (String) txtItemCode.getText();
                String Description      = txtDescription.getText().toString();
                final String Qty        = (String) txtQty.getText();
                String UnitPrice        = (String) txtUnitPrice.getText();
                final String DisAmt     = (String) txtInput.getText();
                UnitPrice               = UnitPrice.replaceAll(",","");
                fnedititem(getActivity(),vItemCode,Description,
                        Qty,UnitPrice, DisAmt,
                        "0");
                ((CashReceipt) getActivity()).refreshNext();
                dismiss();
            }
        });


        btnDelItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String vRunNo= (String) txtRunNo.getText();
                fndelitem(getActivity(),vRunNo);
                ((CashReceipt) getActivity()).refreshNext();
                dismiss();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnOpenModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeModifier();
            }
        });
        btnCloseModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deactiveModifier();
            }
        });
        btnOpenUOM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeUOM();
            }
        });
        btnCloseUOM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deactiveUOM();
            }
        });
        btnSaveModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vModifier=txtModifier.getText().toString();
                setModifer(vModifier);
            }
        });
        btnChPricingMatrix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activePriceMatrix();
            }
        });
        getDialog().setTitle("EDIT PARTICULAR ITEM");
        return view;
    }
    private void initData(String jsonData){
        try{
            JSONArray ja=new JSONArray(jsonData);
            JSONObject jo=null;
            for (int i=0;i<ja.length();i++) {
                jo = ja.getJSONObject(i);
                final String RunNo          = jo.getString("RunNo");
                final String ItemCode       = jo.getString("ItemCode");
                final String Description    = jo.getString("Description");
                final String Qty            = jo.getString("Qty");
                final String UnitPrice      = jo.getString("HCUnitCost");
                final String HCDiscount     = jo.getString("HCDiscount");
                final String DisRate1       = jo.getString("DisRate1");
                final String ModifierYN     = jo.getString("ModifierYN");
                final String Modifier       = jo.getString("Description2");
                final String UOM            = jo.getString("UOM");
                final String FactorQty      = jo.getString("FactorQty");
                BlankLine                   = jo.getString("BlankLine");
                DetailTaxCode               = jo.getString("DetailTaxCode");
                ItemGroup                   = jo.getString("ItemGroup");
                String AnalysisCode2        = jo.getString("AnalysisCode2");
                UnitCost                    = jo.getString("UnitCost");
                ServiceChargeYN             =jo.getString("ServiceChargeYN");
                if(!AnalysisCode2.equals("0")) {
                    btnDelItem.setEnabled(false);
                }
                txtItemCode.setText(ItemCode);
                txtQty.setText(Qty);
                txtUOM.setText("(" + UOM + ")");
                txtUnitPrice.setText(UnitPrice);
                txtDisAmt.setText(HCDiscount);
                txtDisRate.setText(DisRate1);
                txtDescription.setText(Description);
                txtRunNo.setText(RunNo);
                tModifier.setText(Modifier);
                if (ModifierYN.equals("1")) {
                    activeModifier();
                } else {
                    deactiveModifier();
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void setPriceMatrix(String vPct,String vB_ase, String Criteria,
                               String vServiceChargeYN){
        Double Pct          =Double.parseDouble(vPct);
        Double UnitPrice    =Double.parseDouble(txtUnitPrice.getText().toString().replaceAll(",",""));
        Double newUnitPrice =0.00;
        String addPct       ="";
        Double HCDiscount   =0.00;
        Double DisRate1    = 0.00;
        ServiceChargeYN     =vServiceChargeYN;
        Double dUnitCost    =Double.parseDouble(UnitCost);
       // String TaxCode    =txtTaxCode.getText().toString();
        String vItemCode    =txtItemCode.getText().toString();
        String Qty          =txtQty.getText().toString();
        if (Criteria.equals("Discount from selling price")) {
            Double newPrice     = UnitPrice * (Pct / 100);
            newUnitPrice        = UnitPrice - newPrice;
            HCDiscount          = newPrice;
            DisRate1            = Pct;
            addPct              = "["+String.format(Locale.US, "%,.0f", Pct)+"%]";
        } else if (Criteria.equals("Discount from selling price (With Post To GL)")) {
            Double newPrice     = UnitPrice * (Pct / 100);
            newUnitPrice        = UnitPrice - newPrice;
            HCDiscount          = newPrice;
            DisRate1            = Pct;
            addPct              = "["+String.format(Locale.US, "%,.0f", Pct)+"%]";
        } else if (Criteria.equals("Exact selling price")) {
            newUnitPrice        = Pct;
            HCDiscount          = Math.abs(Pct-newUnitPrice);
            DisRate1            = 0.00;
            addPct              = "["+String.format(Locale.US, "%,.2f", HCDiscount)+"]";
        }else if (Criteria.equals("Markup cost price")) {
            Double Price        = dUnitCost * (Pct  /100);
            Double newPrice     = dUnitCost + Price;
            HCDiscount          = Math.abs(newPrice-UnitPrice);
            DisRate1            = 0.00;
            addPct              = "["+String.format(Locale.US, "%,.2f", HCDiscount)+"]";
        }else if(Criteria.equals("Revert Selling Price")){
            HCDiscount          = 0.00;
            DisRate1            = 0.00;
            addPct              = "";
        }
        String Description1     = txtDescription.getText().toString();
        if(Description1.contains("[")) {
            Description1 = Description1.substring(0, Description1.indexOf("[")).trim();
        }
        String Description       =Description1+" "+addPct;
        txtDescription.setText(Description);

        Log.d("HCDiscount Price Matrix",HCDiscount.toString()+ " /"+UnitPrice.toString());

        fnedititem(getActivity(),vItemCode,Description,
                Qty,UnitPrice.toString(),
                HCDiscount.toString(),DisRate1.toString());
        ((CashReceipt) getActivity()).refreshNext();
        /*fncalculate fncalc = new fncalculate(getActivity(), Qty, UnitPrice.toString(),
                HCDiscount.toString(), DisRate1.toString(), TaxCode,
                ItemCode,Description);
        fncalc.execute();*/

    }

    private void activePriceMatrix(){
        hideln();
        lnPriceMatrix.setVisibility(View.VISIBLE);
        retPriceMatrix();
    }
    private void hideln(){
        lnInput.setVisibility(View.GONE);
        lnbtn1.setVisibility(View.GONE);
        lnbtn2.setVisibility(View.GONE);
        lnbtn3.setVisibility(View.GONE);
        lnbtn4.setVisibility(View.GONE);
        lnUOM.setVisibility(View.GONE);
        lnModifier.setVisibility(View.GONE);
        lnPriceMatrix.setVisibility(View.GONE);
        btnCloseModifier.setVisibility(View.GONE);
    }
    private void activeButton(){
        lnInput.setVisibility(View.VISIBLE);
        lnbtn1.setVisibility(View.VISIBLE);
        lnbtn2.setVisibility(View.VISIBLE);
        lnbtn3.setVisibility(View.VISIBLE);
        lnbtn4.setVisibility(View.VISIBLE);
    }

    private void retPriceMatrix(){
        rvPriceMatrix.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rvPriceMatrix.setLayoutManager(lLayout);
        rvPriceMatrix.setItemAnimator(new DefaultItemAnimator());
        DownloadPriceMatrix dPriceMatrix=new DownloadPriceMatrix(getActivity(), rvPriceMatrix,Dialog_Qty.this);
        dPriceMatrix.execute();
    }
    private void retModifier(){
        rvModifier.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rvModifier.setLayoutManager(lLayout);
        rvModifier.setItemAnimator(new DefaultItemAnimator());
        DownloaderModifier dModifier=new DownloaderModifier(getActivity(),ItemGroup, rvModifier,Dialog_Qty.this);
        dModifier.execute();
    }
    private void retUOM(){
        String ItemCode=txtItemCode.getText().toString();
        rvUOM.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rvUOM.setLayoutManager(lLayout);
        rvUOM.setItemAnimator(new DefaultItemAnimator());
        DownloaderUOM dUOM=new DownloaderUOM(getActivity(),ItemCode, rvUOM,Dialog_Qty.this);
        dUOM.execute();
    }
    private void activeModifier(){
        retModifier();
        /*lnInput.setVisibility(View.GONE);
        lnbtn1.setVisibility(View.GONE);
        lnbtn2.setVisibility(View.GONE);
        lnbtn3.setVisibility(View.GONE);
        lnbtn4.setVisibility(View.GONE);
        */
        hideln();
        btnOpenModifier.setVisibility(View.GONE);
        btnCloseModifier.setVisibility(View.VISIBLE);
        lnModifier.setVisibility(View.VISIBLE);
    }
    private void deactiveModifier(){
        /*lnInput.setVisibility(View.VISIBLE);
        lnbtn1.setVisibility(View.VISIBLE);
        lnbtn2.setVisibility(View.VISIBLE);
        lnbtn3.setVisibility(View.VISIBLE);
        lnbtn4.setVisibility(View.VISIBLE);*/
        activeButton();
        btnOpenModifier.setVisibility(View.VISIBLE);
        btnCloseModifier.setVisibility(View.GONE);
        lnModifier.setVisibility(View.GONE);
    }

    private void activeUOM(){
        retUOM();
       /* lnInput.setVisibility(View.GONE);
        lnbtn1.setVisibility(View.GONE);
        lnbtn2.setVisibility(View.GONE);
        lnbtn3.setVisibility(View.GONE);
        lnbtn4.setVisibility(View.GONE);
        btnOpenUOM.setVisibility(View.GONE);
        lnModifier.setVisibility(View.GONE);*/
        hideln();
        btnOpenUOM.setVisibility(View.GONE);
        btnCloseUOM.setVisibility(View.VISIBLE);
        lnUOM.setVisibility(View.VISIBLE);
    }
    private void deactiveUOM(){
        /*lnInput.setVisibility(View.VISIBLE);
        lnbtn1.setVisibility(View.VISIBLE);
        lnbtn2.setVisibility(View.VISIBLE);
        lnbtn3.setVisibility(View.VISIBLE);
        lnbtn4.setVisibility(View.VISIBLE);*/
        activeButton();
        btnOpenUOM.setVisibility(View.VISIBLE);
        btnCloseUOM.setVisibility(View.GONE);
        lnUOM.setVisibility(View.GONE);
    }
    private void addNumberC(String NewText){
        //TextView t = (TextView) v.findViewById(R.id.txtPaidAmount);
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

    public void fnedititem(Context c, String vItemCode, String vDescription,
                           String vQty, String vUnitPrice, String vHCDiscount,
                           String vDisRate1){
        Log.d("CODE",vItemCode);
        if(BlankLine.equals("0")) {
            AddItem fnedit = new AddItem(c, vItemCode,vDescription, vQty, vUnitPrice, vHCDiscount, vDisRate1, ServiceChargeYN);
            fnedit.execute();
        }else if(BlankLine.equals("4")){
            String vUOM=txtUOM.getText().toString();
            EditMisc editMisc=new EditMisc(c,vItemCode,vQty,vUnitPrice,vUOM, DetailTaxCode,vHCDiscount,vDisRate1);
            editMisc.execute();
        }
      // fnedit.additem();
    }
    public void fndelitem(Context c, String vRunNo){
        DelOrder fndel=new DelOrder(c,vRunNo);
        fndel.execute();
    }
    public void setUOM(String UOM, String Price, String UOMFactor){
        final String RunNo= (String) txtRunNo.getText();
        final String vItemCode= (String) txtItemCode.getText();
        final String Qty= (String) txtQty.getText();
        String UnitPrice= Price;
        UnitPrice= UnitPrice.replaceAll(",","");
        String DisAmt= (String) txtDisAmt.getText();
        DisAmt= DisAmt.replaceAll(",","");
        final String DisRate= (String) txtDisRate.getText();
        EditItem editItem=new EditItem(getActivity(),RunNo,vItemCode, Qty, UnitPrice,DisAmt,DisRate,UOM, UOMFactor);
        editItem.execute();
        ((CashReceipt) getActivity()).refreshNext();
        dismiss();
    }
    public void setModifer(String Modifier){
        tModifier.append(Modifier+" - ");
        String vModifier=tModifier.getText().toString();
        String RunNo=txtRunNo.getText().toString();
        inModifier insert=new inModifier(getActivity(),RunNo,vModifier);
        insert.execute();
       /* tModifier.append(Modifier+" - ");
        String vModifier=tModifier.getText().toString();
        String RunNo=txtRunNo.getText().toString();
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String vUpdate="update cloud_cus_inv_dt set Description2='"+vModifier+"' where RunNo='"+RunNo+"' ";
        db.addQuery(vUpdate);
        db.closeDB();
        ((CashReceipt) getActivity()).refreshNext();
        dismiss();*/
    }
    public class inModifier extends AsyncTask<Void,Void,String>{
        Context c;
        String RunNo,Modifier;
        String IPAddress,UserName,Password,DBName,Port,URL,z,DBStatus;

        public inModifier(Context c,String RunNo, String modifier) {
            this.c = c;
            this.RunNo = RunNo;
            Modifier = modifier;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.downloadData();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("error")){
                Toast.makeText(c,"error,data insert", Toast.LENGTH_SHORT).show();
            }else{
                ((CashReceipt) getActivity()).refreshNext();
                dismiss();
                Toast.makeText(c,"succes, data insert", Toast.LENGTH_SHORT).show();
            }
        }

        private String downloadData() {
            try {
                JSONObject jsonReq,jsonRes;
                DBAdapter db=new DBAdapter(c);
                db.openDB();
                String querySet="select * from tb_setting";
                Cursor curSet = db.getQuery(querySet);
                while (curSet.moveToNext()) {
                    IPAddress = curSet.getString(1);
                    UserName = curSet.getString(2);
                    Password = curSet.getString(3);
                    DBName = curSet.getString(4);
                    Port = curSet.getString(5);
                    DBStatus=curSet.getString(7);
                }
                String vUpdate="update cloud_cus_inv_dt set Description2='"+Modifier+"' where RunNo='"+RunNo+"' ";
                if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal conn1 = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", vUpdate);
                    jsonReq.put("action", "insert");
                    String response = conn1.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes = new JSONObject(response);
                    String hasil = jsonRes.getString("success");
                    Log.d("RES", hasil);
                    z=hasil;
                }else{
                    db.addQuery(vUpdate);
                    z="success";
                }
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return z;
        }
    }
}
