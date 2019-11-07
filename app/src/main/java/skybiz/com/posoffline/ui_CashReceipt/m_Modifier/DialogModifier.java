package skybiz.com.posoffline.ui_CashReceipt.m_Modifier;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import skybiz.com.posoffline.MyKeyboard;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.Fragment_Payment;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItemM;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.FnAddOnOrder;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.FnSaveOrder;

/**
 * Created by 7 on 23/02/2018.
 */

public class DialogModifier extends DialogFragment {
    View view;
    Button btnCancel,btnConfirm;
    TextView tModifier;
    RecyclerView rv;
    String ItemCode,ItemGroup,Printer,
            HCDiscount,DisRate1,Point;
    private GridLayoutManager lLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_modifier, container, false);
        btnCancel=(Button)view.findViewById(R.id.btnCancel);
        btnConfirm=(Button)view.findViewById(R.id.btnConfirm);
        tModifier=(TextView)view.findViewById(R.id.tModifier);
        rv=(RecyclerView)view.findViewById(R.id.rvModifier);
        ItemGroup=this.getArguments().getString("ITEMGROUP_KEY");
        ItemCode=this.getArguments().getString("ITEMCODE_KEY");
        HCDiscount=this.getArguments().getString("HCDISCOUNT_KEY");
        DisRate1=this.getArguments().getString("DISRATE1_KEY");
        Printer=this.getArguments().getString("PRINTER_KEY");
        Point=this.getArguments().getString("POINT_KEY");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnConfirm();
            }
        });
        retModifier();
        return view;
    }
    private void retModifier(){
        rv.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rv.setLayoutManager(lLayout);
        rv.setItemAnimator(new DefaultItemAnimator());
        DownloaderModifierL dModifier=new DownloaderModifierL(getActivity(),ItemGroup, rv,DialogModifier.this);
        dModifier.execute();
    }
    public void setModifier(String vModifier){
        AddItemM add=new AddItemM(getActivity(),ItemCode, "1",
                "0","0","0",
                vModifier,Printer,Point);
        add.execute();
        dismiss();
    }
    public void addModifier(String vModifier){
        tModifier.append(vModifier+" - ");
    }
    public void minModifier(String vModifier){
        String oldM=tModifier.getText().toString();
        String newM=oldM.replace(vModifier+" - ","");
        tModifier.setText(newM);
    }
    private void fnConfirm(){
        String vModifier=tModifier.getText().toString();
        AddItemM add=new AddItemM(getActivity(),ItemCode,"1",
                "0",HCDiscount,DisRate1,
                vModifier, Printer, Point);
        add.execute();
        dismiss();
    }
}
