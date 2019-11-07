package skybiz.com.posoffline.ui_CashReceipt;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import skybiz.com.posoffline.KeyboardNew;
import skybiz.com.posoffline.R;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft_Order;
import skybiz.com.posoffline.ui_CashReceipt.m_ItemGroup.DownloaderGroup;
import skybiz.com.posoffline.ui_CashReceipt.m_ListSO.Fragment_ListSO;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItem_ByDesc;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItem_BySearch;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.DownloaderOrder;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.OrderAdapter;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.DialogTable;
import skybiz.com.posoffline.ui_CashReceipt.m_Pay.ScanTable;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_AIDL.utils.AidlUtil;
import skybiz.com.posoffline.ui_CashReceipt.m_Print.By_IposAIDL.Utils.IposAidlUtil;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 11/12/2017.
 */

public class Fragment_Summary extends Fragment {

    private GridLayoutManager lLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchView search,searchDesc;
    View view;
    Button btnKeyboard,btnListSO,btnSearch,btnHideKeyboard,btnTakeOrder;
    EditText txtSearch,txtSearchDesc;
    RecyclerView rvOrder;
    OrderAdapter adapter;
    ArrayList<Spacecraft_Order> orders=new ArrayList<>();
    Switch swSearch;
    KeyboardNew keyboardnew;
    LinearLayout ln1,ln2,ln3,lnKeyboard,lnScan;
   // FragmentManager fm
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_order, container, false);
       // search=(SearchView)view.findViewById(R.id.search);
        //searchDesc=(SearchView)view.findViewById(R.id.searchDesc);
        swSearch=(Switch)view.findViewById(R.id.swSearch);
        btnListSO=(Button) view.findViewById(R.id.btnListSO);
        btnKeyboard=(Button)view.findViewById(R.id.btnKeyboard);
        btnHideKeyboard=(Button)view.findViewById(R.id.btnHideKeyboard);
        btnTakeOrder=(Button)view.findViewById(R.id.btnTakeOrder);
        txtSearch=(EditText)view.findViewById(R.id.txtSearch);
        txtSearchDesc=(EditText)view.findViewById(R.id.txtSearchDesc);
        btnSearch=(Button)view.findViewById(R.id.btnSearch);
        keyboardnew=(KeyboardNew) view.findViewById(R.id.keyboardnew);
        ln1=(LinearLayout)view.findViewById(R.id.ln1) ;
        ln2=(LinearLayout)view.findViewById(R.id.ln2) ;
        ln3=(LinearLayout)view.findViewById(R.id.ln3) ;
        lnKeyboard=(LinearLayout)view.findViewById(R.id.lnKeyboard) ;
        lnScan=(LinearLayout)view.findViewById(R.id.lnScan) ;
        btnKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activeKeyboard();
            }
        });
        btnHideKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deactivceKeyboard();
            }
        });
        btnTakeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTable();
            }
        });
       // search.setIconified(false);
       // search.requestFocusFromTouch();
       /* search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String ItemCode) {
                callSearch(ItemCode);
                search.setQuery("", true);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(),"CLICK, Search View", Toast.LENGTH_SHORT).show();
                return true;
            }
            public void callSearch(String ItemCode) {
                fnAddSearch(ItemCode);
            }
        });

        searchDesc.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String Desc) {
                callSearchDesc(Desc);
                searchDesc.setQuery("", true);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //Toast.makeText(getActivity(),"CLICK, Search View", Toast.LENGTH_SHORT).show();
                return true;
            }
            public void callSearchDesc(String Desc) {
                fnByDesc(Desc);
            }
        });*/

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vSearchDesc=txtSearchDesc.getText().toString();
                String vSearch=txtSearch.getText().toString();
                if(!vSearchDesc.isEmpty()){
                    fnByDesc(vSearchDesc);
                }else if(!vSearch.isEmpty()){
                    fnAddSearch(vSearch);
                }

            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        btnListSO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_ListSO fgso = new Fragment_ListSO();
                fgso.show(getActivity().getSupportFragmentManager(), "List of Table Number");
            }
        });

        swSearch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    swSearch.setText("By Description");
                    activeByDesc();
                }else {
                    deactiveByDesc();
                    swSearch.setText("By Item Code");
                }
            }
        });

        txtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtSearch.setInputType(0);
                txtSearch.setRawInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                InputConnection ic2=txtSearch.onCreateInputConnection(new EditorInfo());
                keyboardnew.setInputConnection(ic2);
                //activeKeyboard();
                return false;
            }
        });

        txtSearchDesc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                txtSearchDesc.setInputType(0);
                txtSearchDesc.setRawInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                InputConnection ic2=txtSearchDesc.onCreateInputConnection(new EditorInfo());
                keyboardnew.setInputConnection(ic2);
                //activeKeyboard();
                return false;
            }
        });

       // txtSearch.seton
        txtSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    String vSearch=     txtSearch.getText().toString();
                    fnAddSearch(vSearch);
                    //txtSearch.requestFocus();
                    return true;
                }
                return false;
            }
        });
        txtSearchDesc.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    String vSearchDesc=txtSearchDesc.getText().toString();
                    fnByDesc(vSearchDesc);
                    //txtSearch.requestFocus();
                    return true;
                }
                return false;
            }
        });
        lnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScanner();
            }
        });
        initView();
        refresh();
        readyPrinter();
        return view;
    }
    private void openScanner(){
        Intent i=new Intent(getActivity(), ScanTable.class);
        i.putExtra("DOCTYPE_KEY", "Search Item");
        startActivity(i);
    }
    private void activeKeyboard(){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.0f);
        ln3.setLayoutParams(lp);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
               0,
                1.5f);
        lnKeyboard.setLayoutParams(lp2);
        lnKeyboard.setVisibility(View.VISIBLE);
        btnKeyboard.setVisibility(View.GONE);
        btnHideKeyboard.setVisibility(View.VISIBLE);
    }
    private void deactivceKeyboard(){
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
               0,
                2.5f);
        ln3.setLayoutParams(lp);
        lnKeyboard.setVisibility(View.GONE);
        btnKeyboard.setVisibility(View.VISIBLE);
        btnHideKeyboard.setVisibility(View.GONE);

    }
    private  void initView(){
        InputConnection ic=txtSearch.onCreateInputConnection(new EditorInfo());
        keyboardnew.setInputConnection(ic);
        txtSearch.setTextIsSelectable(true);
        txtSearch.setInputType(0);

        InputConnection ic2=txtSearchDesc.onCreateInputConnection(new EditorInfo());
        keyboardnew.setInputConnection(ic2);
        txtSearchDesc.setTextIsSelectable(true);
        txtSearchDesc.setInputType(0);
    }
    private void activeByDesc(){
        txtSearch.setVisibility(View.GONE);
        txtSearchDesc.setVisibility(View.VISIBLE);
    }
    public void deactiveByDesc(){
        txtSearch.setVisibility(View.VISIBLE);
        txtSearchDesc.setVisibility(View.GONE);
    }
    public void refresh() {
        rvOrder=(RecyclerView)view.findViewById(R.id.rec_order);
        rvOrder.setHasFixedSize(true);
        lLayout = new GridLayoutManager(getActivity(), 1);
        rvOrder.setLayoutManager(lLayout);
        rvOrder.setItemAnimator(new DefaultItemAnimator());
        DownloaderOrder dOrder=new DownloaderOrder(getActivity(),rvOrder,getActivity().getSupportFragmentManager());
        dOrder.execute();
       // adapter=new OrderAdapter(getActivity(),orders,getActivity().getSupportFragmentManager());
       // retOrder();
    }
    private void openTable(){
        DialogTable dialogTable=new DialogTable();
        dialogTable.show(getFragmentManager(),"mTag");
    }
    public void retOrder(){
        refresh();
      /*  orders.clear();
        DBAdapter db=new DBAdapter(getActivity());
        db.openDB();
        String vQuery="select c.Qty,c.Doc1No,m.ItemCode,m.ItemGroup,m.Description, " +
                "ROUND(c.HCUnitCost,2) as HCUnitCost,ROUND(c.HCDiscount,2)as HCDiscount,ROUND(c.DisRate1,2)as DisRate1, c.RunNo," +
                " c.Description2 " +
                " from cloud_cus_inv_dt c inner join stk_master m on c.ItemCode=m.ItemCode  Order By c.RunNo desc";
        Cursor c=db.getQuery(vQuery);
        while (c.moveToNext()) {
            String Qty=c.getString(0);
            String ItemCode=c.getString(2);
            String ItemGroup=c.getString(3);
            String ItemDesc=c.getString(4);
            Double dUnitPrice=c.getDouble(5);
            Double dHCDiscount=c.getDouble(6);
            Double dDisRate1=c.getDouble(7);
            int runno=c.getInt(8);
            String Description2=c.getString(9);
            String UnitPrice=dUnitPrice.toString();
            String HCDiscount=dHCDiscount.toString();
            String DisRate1=dDisRate1.toString();
            Spacecraft_Order s=new Spacecraft_Order();
            s.setRunNo(runno);
            s.setItemCode(ItemCode);
            s.setItemGroup(ItemGroup);
            s.setDescription(ItemDesc);
            s.setHCUnitCost(UnitPrice);
            s.setHCDiscount(HCDiscount);
            s.setDisRate1(DisRate1);
            s.setBtnQty(Qty);
            s.setDescription2(Description2);
            orders.add(s);
        }
        //CHECK IF ARRAYLIST ISNT EMPTY
        if(!(orders.size()<1)) {
            rvOrder.setAdapter(adapter);
        }
        db.closeDB();*/
    }
    private void fnByDesc(String Desc){
        String vQty="1";
        AddItem_ByDesc addItemByDesc=new AddItem_ByDesc(getActivity(), Desc, vQty);
        try {
            String result= addItemByDesc.execute().get();
            if(result.equals("success")){
                txtSearchDesc.getText().clear();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        txtSearchDesc.requestFocus();
                    }
                }, 500);

            }else{
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        txtSearchDesc.requestFocus();
                    }
                }, 500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //addItemByDesc.execute();
       // txtSearchDesc.
    }
    public void fnAddSearch(String ItemCode){
        String vQty="1";
        AddItem_BySearch fnaddsearch=new AddItem_BySearch(getActivity() , ItemCode,vQty);
        //fnaddsearch.execute();
        try {
            String result= fnaddsearch.execute().get();
            Log.d("RESULT",result);
            if(result.equals("Add Item Successfull")){
                txtSearch.getText().clear();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        txtSearch.requestFocus();
                    }
                }, 500);
            }else{
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        txtSearch.requestFocus();
                    }
                }, 500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

       // refresh();
    }
    private void readyPrinter(){
        try {
            DBAdapter db = new DBAdapter(getActivity());
            db.openDB();
            String TypePrinter = "";
            Cursor cPrinter = db.getSettingPrint();
            while (cPrinter.moveToNext()) {
                TypePrinter = cPrinter.getString(1);
            }
            String qSet="select FastKeypadYN from tb_setting";
            Cursor rsSet = db.getQuery(qSet);
            while (rsSet.moveToNext()) {
                String FastKeypadYN = rsSet.getString(0);
                if(FastKeypadYN.equals("1")){
                    activeKeyboard();
                }else{
                    deactivceKeyboard();
                }
            }
            db.closeDB();
            if (TypePrinter.equals("AIDL")) {
                AidlUtil.getInstance().connectPrinterService(getActivity());
                AidlUtil.getInstance().initPrinter();
            } else if (TypePrinter.equals("Ipos AIDL")) {
                IposAidlUtil.getInstance().connectPrinterService(getActivity());
                //IposAidlUtil.getInstance().initPrinter();
            } else {

            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }



    /*
    1. Cloud
    2. Android Server
    3. Localhost / device v1_1, v1_2,
     */
}
