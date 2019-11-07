package skybiz.com.posoffline.ui_Setting;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import skybiz.com.posoffline.MainActivity;
import skybiz.com.posoffline.MyBounceInterpolator;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 11/12/2017.
 */

public class Fragment_CompanySetup extends Fragment {

    View view;
    EditText txtCompanyName,txtCompanyCode,txtGSTNo,
            txtAddress,txtTel1,txtFax1,
            txtTown,txtState,txtCountry,
            txtCurCode,txtFooterCR;
    Button btnBack,btnSave;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_company_setup, container, false);
        txtCompanyName      =(EditText) view.findViewById(R.id.txtCompanyName);
        txtCompanyCode      =(EditText) view.findViewById(R.id.txtCompanyCode);
        txtGSTNo            =(EditText) view.findViewById(R.id.txtGSTNo);
        txtAddress          =(EditText) view.findViewById(R.id.txtAddress);
        txtTel1             =(EditText) view.findViewById(R.id.txtTel1);
        txtFax1             =(EditText) view.findViewById(R.id.txtFax1);
        txtTown             =(EditText) view.findViewById(R.id.txtTown);
        txtState            =(EditText) view.findViewById(R.id.txtState);
        txtCountry          =(EditText) view.findViewById(R.id.txtCountry);
        txtCurCode          =(EditText) view.findViewById(R.id.txtCurCode);
        txtFooterCR         =(EditText) view.findViewById(R.id.txtFooterCR);
        btnBack             =(Button) view.findViewById(R.id.btnBack) ;
        btnSave             =(Button) view.findViewById(R.id.btnSave) ;

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapButton(btnSave);
                fnsave();
            }
        });
        initData();
        return view;
    }

    public void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
    }
    private void initData(){
        try {
            DBAdapter db = new DBAdapter(getActivity());
            db.openDB();
            String query1 = "select  CompanyCode, CompanyName, Address, " +
                    " ComTown, ComState, ComCountry, " +
                    " Tel1, Fax1, CompanyEmail, " +
                    " IFNULL(GSTNo,'') as GSTNo, CurCode, " +
                    " IFNULL(Footer_CR,'') as Footer_CR from companysetup ";
            Cursor rsCom = db.getQuery(query1);
            while (rsCom.moveToNext()) {
                txtCompanyCode.setText(rsCom.getString(0));
                txtCompanyName.setText(rsCom.getString(1));
                txtAddress.setText(rsCom.getString(2));
                txtTown.setText(rsCom.getString(3));
                txtState.setText(rsCom.getString(4));
                txtCountry.setText(rsCom.getString(5));
                txtTel1.setText(rsCom.getString(6));
                txtFax1.setText(rsCom.getString(7));
                txtGSTNo.setText(rsCom.getString(9));
                txtCurCode.setText(rsCom.getString(10));
                txtFooterCR.setText(rsCom.getString(11));
            }


            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }
    private void fnsave(){
        try {
            DBAdapter db = new DBAdapter(getActivity());
            db.openDB();
            String CompanyName  = txtCompanyName.getText().toString();
            String CompanyCode  = txtCompanyCode.getText().toString();
            String GSTNo        = txtGSTNo.getText().toString();
            String Address      = txtAddress.getText().toString();
            String Tel1         = txtTel1.getText().toString();
            String Fax1         = txtFax1.getText().toString();
            String Town         = txtTown.getText().toString();
            String State        = txtState.getText().toString();
            String Country      = txtCountry.getText().toString();
            String CurCode      = txtCurCode.getText().toString();
            String FooterCR     = txtFooterCR.getText().toString();

            String qUpdate="update companysetup set CompanyCode='"+CompanyCode+"'," +
                    "CompanyName='"+CompanyName+"'," +
                    "GSTNo='"+GSTNo+"'," +
                    "Address='"+Address+"'," +
                    "Tel1='"+Tel1+"'," +
                    "Fax1='"+Fax1+"'," +
                    "ComTown='"+Town+"'," +
                    "ComState='"+State+"'," +
                    "ComCountry='"+Country+"'," +
                    "CurCode='"+CurCode+"'," +
                    "Footer_CR='"+FooterCR+"'    ";
            long update=db.addQuery(qUpdate);
            if(update>0){
                Toast.makeText(getActivity(),"Company Setup has been updated", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(),"Failure, data cannot updated", Toast.LENGTH_SHORT).show();
            }
            db.closeDB();
        }catch (SQLiteException e){
            e.printStackTrace();
        }

    }

}
