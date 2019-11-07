package skybiz.com.posoffline.ui_Setting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import skybiz.com.posoffline.MainActivity;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_WIFI.FnCheckWifi;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 11/12/2017.
 */

public class Fragment_Printer2 extends Fragment {

    LinearLayout ln_Bluetooth,ln_WIFI,ln_USB;
    View view;
    Spinner spinnerType,spinnerBT;
    Button btnSave, btnCancel,btnCheck;
    EditText txtIP,txtPort;
    String TypePrinter,NamePrinter,IPPrinter,UUID,isConnect,IPAddress,ConnYN;
    ArrayList<String>lsBt;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kitchen_printer, container, false);
        //set linear layout
        ln_Bluetooth=(LinearLayout)view.findViewById(R.id.ln_Bluetooth) ;
        ln_WIFI=(LinearLayout)view.findViewById(R.id.ln_WIFI) ;
        spinnerType = (Spinner) view.findViewById(R.id.spTypePrinter);
        spinnerBT   = (Spinner) view.findViewById(R.id.spListBT);
        txtIP       =(EditText) view.findViewById(R.id.txtIPAddress);
        txtPort       =(EditText) view.findViewById(R.id.txtPort);
        btnCheck    =(Button) view.findViewById(R.id.btn_checkwifi) ;
        btnSave    =(Button) view.findViewById(R.id.btnp_save) ;
        btnCancel   =(Button) view.findViewById(R.id.btnp_cancel) ;

        //populate printer type
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.printer_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
        spinnerType.setOnItemSelectedListener(new MyOnItemSelectedListener());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypePrinter = spinnerType.getSelectedItem().toString();
                if(TypePrinter.equals("Bluetooth")) {
                    NamePrinter = spinnerBT.getSelectedItem().toString();
                }
                if(TypePrinter.equals("Wifi")) {
                    IPPrinter = txtIP.getText().toString();
                }
                saveSetting(TypePrinter,NamePrinter,IPPrinter,UUID);
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FnCheckWifi fnCheckWifi=new FnCheckWifi();
                String vPort=txtPort.getText().toString();
                String IPPrinter=txtIP.getText().toString();
                int Port=Integer.parseInt(vPort);
                isConnect=fnCheckWifi.fncheck(getActivity(),IPPrinter,Port);
                if(isConnect.equals("success")){
                    Toast.makeText(getActivity(), "Wifi Printer Connected ", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(), "Wifi Printer Failure ", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
                //((MainActivity)getActivity()).fnchecksetting();
            }
        });
        retrieve();
        return view;
    }

    public  class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            String Item=parent.getItemAtPosition(pos).toString();
            hideLn();
            if(Item.equals("Bluetooth")){
                ln_Bluetooth.setVisibility(View.VISIBLE);
                listBT();
            }
            if(Item.equals("Bluetooth Zebra")){
                ln_Bluetooth.setVisibility(View.VISIBLE);
                listBT();
            }
            if(Item.equals("Wifi")){
                ln_WIFI.setVisibility(View.VISIBLE);
            }
           // Toast.makeText(parent.getContext(), "Item is " +
                   // parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }
    public void hideLn(){
        ln_Bluetooth.setVisibility(View.GONE);
        ln_WIFI.setVisibility(View.GONE);
    }

    public void listBT(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        lsBt = new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices)
            lsBt.add(bt.getName());
        spinnerBT.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,lsBt));
    }

    public void saveSetting(String TypePrinter, String NamePrinter, String IPPrinter, String UUID) {
        String Port=txtPort.getText().toString();
        DBAdapter db=new DBAdapter(this.getContext());
        db.openDB();
        IPAddress="zero";
        String Query="select * from tb_kitchenprinter";
        Cursor cPrint=db.getQuery(Query);
        while (cPrint.moveToNext()) {
            IPAddress = cPrint.getString(1);
        }
        //Toast.makeText(this.getContext(), "Type Printer"+IPAddress, Toast.LENGTH_SHORT).show();
        if(IPAddress.equals("zero")) {
            String QueryAdd="Insert Into tb_kitchenprinter(TypePrinter,NamePrinter,IPPrinter,UUID,Port)" +
                    " values('"+TypePrinter+"', '"+NamePrinter+"', '"+IPPrinter+"', '"+UUID+"','"+Port+"')";
            long result=db.addQuery(QueryAdd);
            if (result > 0) {
                Toast.makeText(this.getContext(), "Setting Printer,You have a successful update ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.getContext(), "Setting Printer, Record Failed Update ", Toast.LENGTH_SHORT).show();
            }
        }else {
            String QueryDel="Delete from tb_kitchenprinter";
            long rsDelete = db.addQuery(QueryDel);
            if (rsDelete != 0) {
                String QueryAdd="Insert Into tb_kitchenprinter(TypePrinter,NamePrinter,IPPrinter,UUID,Port)" +
                        " values('"+TypePrinter+"', '"+NamePrinter+"', '"+IPPrinter+"', '"+UUID+"','"+Port+"')";

                long result=db.addQuery(QueryAdd);
                if (result > 0) {
                    Toast.makeText(this.getContext(), "Setting Printer,You have a successful update ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this.getContext(), "Setting Printer, Record Failed Update ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this.getContext(), "Setting Printer, Error Delete Setting Printer ", Toast.LENGTH_SHORT).show();
            }
        }
        db.closeDB();
    }

    //RETRIEVE
    private void retrieve() {
        DBAdapter db=new DBAdapter(this.getContext());
        db.openDB();
        String Query="select * from tb_kitchenprinter";
        Cursor c=db.getQuery(Query);
        while (c.moveToNext()) {
            int RunNo=c.getInt(0);
            TypePrinter = c.getString(1);
            NamePrinter= c.getString(2);
            IPPrinter = c.getString(3);
            UUID = c.getString(4);
            txtIP.setText(IPPrinter);
            txtPort.setText(c.getString(5));
                ArrayList<String> list=new ArrayList( Arrays.asList(getResources().getStringArray(R.array.printer_array)) );
            int pos= list.indexOf(TypePrinter);
            spinnerType.setSelection(pos);
            if(TypePrinter.equals("Bluetooth") || TypePrinter.equals("Bluetooth Zebra")){
                listBT();
                final Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int pos2=lsBt.indexOf(NamePrinter);
                        spinnerBT.setSelection(pos2);
                    }
                },1000);
            }
        }
    }
}
