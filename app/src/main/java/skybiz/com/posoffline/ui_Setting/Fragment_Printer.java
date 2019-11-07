package skybiz.com.posoffline.ui_Setting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
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

public class Fragment_Printer extends Fragment {

    LinearLayout ln_Bluetooth,ln_WIFI,ln_Bluetooth2,ln_WIFI2;
    View view;
    Spinner spinnerType,spinnerBT,spinnerType2,
            spinnerBT2,spEachSlip,spPaperSize,
            spPaperSize2,spCopies;
    Button btnSave, btnCancel,btnCheck,btnSave2, btnCancel2,btnCheck2;
    EditText txtIP,txtPort, txtIP2,txtPort2;

    String TypePrinter,NamePrinter,IPPrinter="",
            UUID,isConnect,IPAddress,
            Prefix,LastNo,tRunNoCS,PrefixSO,LastNoSO,tRunNoSO;
    ArrayList<String>lsBt;
    ArrayList<String>lsBt2;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting_printer, container, false);
        tRunNoCS="0";
        tRunNoSO="0";
        //set linear layout
        ln_Bluetooth=(LinearLayout)view.findViewById(R.id.ln_Bluetooth) ;
        ln_WIFI=(LinearLayout)view.findViewById(R.id.ln_WIFI) ;
        spinnerType = (Spinner) view.findViewById(R.id.spTypePrinter);
        spinnerBT   = (Spinner) view.findViewById(R.id.spListBT);
        spEachSlip   = (Spinner) view.findViewById(R.id.spEachSlip);
        txtIP       =(EditText) view.findViewById(R.id.txtIPAddress);
        txtPort     =(EditText) view.findViewById(R.id.txtPort);
        btnCheck    =(Button) view.findViewById(R.id.btn_checkwifi) ;
        btnSave     =(Button) view.findViewById(R.id.btnp_save) ;
        btnCancel   =(Button) view.findViewById(R.id.btnp_cancel) ;
        spPaperSize   = (Spinner) view.findViewById(R.id.spPaperSize);

        ln_Bluetooth2   =(LinearLayout)view.findViewById(R.id.ln_Bluetooth2) ;
        ln_WIFI2        =(LinearLayout)view.findViewById(R.id.ln_WIFI2) ;
        spinnerType2    = (Spinner) view.findViewById(R.id.spTypePrinter2);
        spinnerBT2      = (Spinner) view.findViewById(R.id.spListBT2);
        txtIP2          =(EditText) view.findViewById(R.id.txtIPAddress2);
        txtPort2        =(EditText) view.findViewById(R.id.txtPort2);
        btnCheck2       =(Button) view.findViewById(R.id.btn_checkwifi2) ;
        btnSave2        =(Button) view.findViewById(R.id.btnp_save2) ;
        btnCancel2      =(Button) view.findViewById(R.id.btnp_cancel2) ;
        spPaperSize2   = (Spinner) view.findViewById(R.id.spPaperSize2);
        spCopies   = (Spinner) view.findViewById(R.id.spCopies);

        //populate printer type
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.printer_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
        spinnerType.setOnItemSelectedListener(new MyOnItemSelectedListener());

        spinnerType2.setAdapter(adapter);
        spinnerType2.setOnItemSelectedListener(new MyOnItemSelectedListener2());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* TypePrinter = spinnerType.getSelectedItem().toString();
                if(TypePrinter.equals("Bluetooth")) {
                    NamePrinter = spinnerBT.getSelectedItem().toString();
                }
                if(TypePrinter.equals("Wifi")) {
                    IPPrinter = txtIP.getText().toString();
                }
                if(TypePrinter.equals("Bluetooth Zebra")) {
                    NamePrinter = spinnerBT.getSelectedItem().toString();
                }
                saveSetting(TypePrinter,NamePrinter,IPPrinter,UUID);*/
                saveSetting();
            }
        });

        btnSave2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypePrinter = spinnerType2.getSelectedItem().toString();
                if(TypePrinter.equals("Bluetooth")) {
                    NamePrinter = spinnerBT2.getSelectedItem().toString();
                }
                if(TypePrinter.equals("Wifi")) {
                    IPPrinter = txtIP2.getText().toString();
                }
                if(TypePrinter.equals("Bluetooth Zebra")) {
                    NamePrinter = spinnerBT2.getSelectedItem().toString();
                }
                saveSetting2(TypePrinter,NamePrinter,IPPrinter,UUID);
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

        btnCheck2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FnCheckWifi fnCheckWifi=new FnCheckWifi();
                String vPort=txtPort2.getText().toString();
                String IPPrinter=txtIP2.getText().toString();
                int Port=Integer.parseInt(vPort);
                isConnect=fnCheckWifi.fncheck(getActivity(),IPPrinter,Port);
                if(isConnect.equals("success")){
                    Toast.makeText(getActivity(), "Wifi Printer Connected ", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(), "Wifi Printer Failure ", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnCancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
            }
        });
        loadEachSlip();
        loadPaper();
        loadCopies();
        retrieve();
        return view;
    }

    private void loadCopies(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.copies_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCopies.setAdapter(adapter);
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
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }
    public  class MyOnItemSelectedListener2 implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent,
                                   View view, int pos, long id) {
            String Item=parent.getItemAtPosition(pos).toString();
            hideLn2();
            if(Item.equals("Bluetooth")){
                ln_Bluetooth2.setVisibility(View.VISIBLE);
                listBT2();
            }
            if(Item.equals("Bluetooth Zebra")){
                ln_Bluetooth2.setVisibility(View.VISIBLE);
                listBT2();
            }
            if(Item.equals("Wifi")){
                ln_WIFI2.setVisibility(View.VISIBLE);
            }
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }
    public void hideLn(){
        ln_Bluetooth.setVisibility(View.GONE);
        ln_WIFI.setVisibility(View.GONE);
    }

    public void hideLn2(){
        ln_Bluetooth2.setVisibility(View.GONE);
        ln_WIFI2.setVisibility(View.GONE);
    }

    private void loadPaper(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.papersize, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaperSize.setAdapter(adapter);
        spPaperSize2.setAdapter(adapter);
    }
    private void loadEachSlip(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.eachslip_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEachSlip.setAdapter(adapter);
    }
    public void listBT(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        lsBt= new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices)
            lsBt.add(bt.getName());
        spinnerBT.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lsBt));
    }
    public void listBT2(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        lsBt2= new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices)
            lsBt2.add(bt.getName());
        spinnerBT2.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, lsBt2));
    }

    public void saveSetting() {
        TypePrinter = spinnerType.getSelectedItem().toString();
        if(TypePrinter.equals("Bluetooth")) {
            NamePrinter = spinnerBT.getSelectedItem().toString();
        }
        if(TypePrinter.equals("Wifi")) {
            IPPrinter = txtIP.getText().toString();
        }
        if(TypePrinter.equals("Bluetooth Zebra")) {
            NamePrinter = spinnerBT.getSelectedItem().toString();
        }

        String Port         =txtPort.getText().toString();
        String PaperSize    =spPaperSize.getSelectedItem().toString();
        String Copies       =spCopies.getSelectedItem().toString();
        DBAdapter db=new DBAdapter(this.getContext());
        db.openDB();
        String query="select count(*) as numrows from tb_settingprinter";
        Cursor rsData=db.getQuery(query);
        int numrows=0;
        while(rsData.moveToNext()){
            numrows=rsData.getInt(0);
        }
        if(numrows==0) {
            String QueryAdd="Insert Into tb_settingprinter(TypePrinter, NamePrinter, IPPrinter," +
                    "UUID, Port, PaperSize," +
                    "Copies)" +
                    " values('"+TypePrinter+"', '"+NamePrinter+"', '"+IPPrinter+"'," +
                    " '"+UUID+"', '"+Port+"', '"+PaperSize+"'," +
                    " '"+Copies+"')";
            long result=db.addQuery(QueryAdd);
            if (result > 0) {
                Toast.makeText(this.getContext(), "Setting Printer,You have a successful update ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.getContext(), "Setting Printer, Record Failed Update ", Toast.LENGTH_SHORT).show();
            }
        }else {
            String vDelete="delete from tb_settingprinter";
            long rsDelete = db.addQuery(vDelete);
            if (rsDelete != 0) {
                String QueryAdd="Insert Into tb_settingprinter(TypePrinter ,NamePrinter, IPPrinter, " +
                        "UUID, Port, PaperSize," +
                        "Copies )" +
                        " values('"+TypePrinter+"', '"+NamePrinter+"', '"+IPPrinter+"'," +
                        " '"+UUID+"', '"+Port+"', '"+PaperSize+"'," +
                        " '"+Copies+"')";
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
    }

    public void saveSetting2(String TypePrinter, String NamePrinter, String IPPrinter, String UUID) {
        String Port             =txtPort2.getText().toString();
        String EachSlip         =spEachSlip.getSelectedItem().toString();
        String PaperSize        =spPaperSize2.getSelectedItem().toString();
        DBAdapter db    =new DBAdapter(this.getContext());
        db.openDB();
        String query="select count(*) as numrows from tb_kitchenprinter";
        Cursor rsData=db.getQuery(query);
        int numrows=0;
        while(rsData.moveToNext()){
            numrows=rsData.getInt(0);
        }
        if(numrows==0) {
            String QueryAdd="Insert Into tb_kitchenprinter(TypePrinter, NamePrinter, IPPrinter, " +
                    "UUID, Port, EachSlip," +
                    "PaperSize)" +
                    " values('"+TypePrinter+"', '"+NamePrinter+"', '"+IPPrinter+"'," +
                    " '"+UUID+"','"+Port+"', '"+EachSlip+"'," +
                    " '"+PaperSize+"')";
            long result=db.addQuery(QueryAdd);
            if (result > 0) {
                Toast.makeText(this.getContext(), "Setting Kitchen Printer,You have a successful update ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this.getContext(), "Setting Kitchen Printer, Record Failed Update ", Toast.LENGTH_SHORT).show();
            }
        }else {
            String vDelete="delete from tb_kitchenprinter";
            long rsDelete = db.addQuery(vDelete);
            if (rsDelete != 0) {
                String QueryAdd="Insert Into tb_kitchenprinter(TypePrinter, NamePrinter, IPPrinter," +
                        "UUID, Port, EachSlip, " +
                        "PaperSize)" +
                        " values('"+TypePrinter+"', '"+NamePrinter+"', '"+IPPrinter+"', " +
                        " '"+UUID+"','"+Port+"', '"+EachSlip+"'," +
                        " '"+PaperSize+"')";
                long result=db.addQuery(QueryAdd);
                if (result > 0) {
                    Toast.makeText(this.getContext(), "Setting Kitchen Printer,You have a successful update ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this.getContext(), "Setting Kitchen Printer, Record Failed Update ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this.getContext(), "Setting Kitchen Printer, Error Delete Setting Printer ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //RETRIEVE
    private void retrieve() {
        DBAdapter db=new DBAdapter(this.getContext());
        db.openDB();
        String query = "select * from tb_settingprinter";
        Cursor c=db.getQuery(query);
        while (c.moveToNext()) {
            int RunNo       = c.getInt(0);
            TypePrinter     = c.getString(1);
            NamePrinter     = c.getString(2);
            IPPrinter       = c.getString(3);
            UUID            = c.getString(4);
            txtIP.setText(IPPrinter);
            txtPort.setText(c.getString(5));
            String PaperSize=c.getString(6);
            String Copies   =c.getString(7);

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
            ArrayList<String> paper=new ArrayList( Arrays.asList(getResources().getStringArray(R.array.papersize)) );
            int pos2= paper.indexOf(PaperSize);
            spPaperSize.setSelection(pos2);

            ArrayList<String> list3=new ArrayList( Arrays.asList(getResources().getStringArray(R.array.copies_array)) );
            int pos3= list3.indexOf(Copies);
            spCopies.setSelection(pos3);
        }

        String Query="select * from tb_kitchenprinter";
        Cursor rsP=db.getQuery(Query);
        while (rsP.moveToNext()) {
            TypePrinter = rsP.getString(1);
            NamePrinter = rsP.getString(2);
            IPPrinter   = rsP.getString(3);
            UUID        = rsP.getString(4);
            txtPort2.setText(rsP.getString(5));
            String EachSlip=rsP.getString(6);
            String PaperSize2=rsP.getString(7);
            txtIP2.setText(IPPrinter);
            ArrayList<String> list = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.printer_array)));
            int pos = list.indexOf(TypePrinter);
            spinnerType2.setSelection(pos);
            if (TypePrinter.equals("Bluetooth") || TypePrinter.equals("Bluetooth Zebra")) {
                listBT2();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int pos2 = lsBt2.indexOf(NamePrinter);
                        spinnerBT2.setSelection(pos2);
                    }
                }, 1000);
            }
            ArrayList<String> list2 = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.eachslip_array)));
            int pos2 = list2.indexOf(EachSlip);
            spEachSlip.setSelection(pos2);
            ArrayList<String> paper2=new ArrayList( Arrays.asList(getResources().getStringArray(R.array.papersize)) );
            int pos3= paper2.indexOf(PaperSize2);
            spPaperSize2.setSelection(pos3);
        }
        db.closeDB();
    }
}
