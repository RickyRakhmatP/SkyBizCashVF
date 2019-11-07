package skybiz.com.posoffline.m_NewItem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewObject.SetDefaultTax;
import skybiz.com.posoffline.m_NewSummary.AddItem;
import skybiz.com.posoffline.m_NewSummary.EditItem;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;

/**
 * Created by 7 on 27/10/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
    String IPAddress,UserName,Password,DBName,Qty,DocType;
    Context c;
    ArrayList<Spacecraft> spacecrafts;
    int clickcount=0;

    public ItemAdapter(Context c,String DocType, ArrayList<Spacecraft> spacecrafts) {
        this.c = c;
        this.DocType = DocType;
        this.spacecrafts = spacecrafts;
    }


    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item,parent,false);
        return new ItemHolder(v);

    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {

        final Spacecraft spacecraft=spacecrafts.get(position);
        holder.ItemCodetxt.setText(spacecraft.getItemCode());
        holder.Descriptiontxt.setText(spacecraft.getDescription());
        holder.txtUnitPrice.setText(spacecraft.getCurCode()+spacecraft.getUnitPrice());
        Qty=spacecraft.getBtnQty();
        final int dqty= Integer.parseInt(Qty);;
        if(Qty.equals("0")) {
            holder.btnAdd.setText("+");
        }else{
            holder.btnAdd.setText(Qty);
            spacecraft.setId(dqty);
        }
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                spacecraft.setId(spacecraft.getId()+1);
                final String vQty= String.valueOf(spacecraft.getId());
                final String vUnitPrice=spacecraft.getUnitPrice();
                final String UnitPrice=vUnitPrice.replaceAll(",","");
                final String vItemCode=spacecraft.getItemCode();
                final String vDescription=spacecraft.getDescription();
                final String vItemGroup=spacecraft.getItemGroup();
                final String vUOM=spacecraft.getUOM();
                final String vRetailTaxCode=spacecraft.getRetailTaxCode();
                final String vPurchaseTaxCode=spacecraft.getPurchaseTaxCode();
                final String vSalesTaxCode=spacecraft.getSalesTaxCode();
                final String vDetailTaxCode=SetDefaultTax.DetailTax(DocType,vRetailTaxCode,vPurchaseTaxCode,vSalesTaxCode);
                String vHCDiscount=spacecraft.getHCDiscount();
                String vDisRate1=spacecraft.getDisRate1();
                if(Double.parseDouble(vHCDiscount)>0 && Double.parseDouble(vDisRate1)>0){
                    vDisRate1="0.00";
                }else if(Double.parseDouble(vHCDiscount)>0 && Double.parseDouble(vDisRate1)==0){
                    vDisRate1="0.00";
                }else if(Double.parseDouble(vHCDiscount)==0 && Double.parseDouble(vDisRate1)>0){
                    vHCDiscount="0.00";
                }
                AddItem addItem =new AddItem(c,DocType,vItemCode,vDescription,vItemGroup,"1",UnitPrice,vUOM,vDetailTaxCode,vHCDiscount,vDisRate1,"0","0");
                addItem.execute();
                holder.btnAdd.setText(String.valueOf(spacecraft.getId()));
            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                final int qty= spacecraft.getId();
                final String vUnitPrice=spacecraft.getUnitPrice();
                final String UnitPrice=vUnitPrice.replaceAll(",","");
                final String vItemCode=spacecraft.getItemCode();
                final String vItemGroup=spacecraft.getItemGroup();
                final String vUOM=spacecraft.getUOM();
                final String vRetailTaxCode=spacecraft.getRetailTaxCode();
                final String vPurchaseTaxCode=spacecraft.getPurchaseTaxCode();
                final String vSalesTaxCode=spacecraft.getSalesTaxCode();
                final String vDetailTaxCode= SetDefaultTax.DetailTax(DocType,vRetailTaxCode,vPurchaseTaxCode,vSalesTaxCode);
                String vHCDiscount=spacecraft.getHCDiscount();
                String vDisRate1=spacecraft.getDisRate1();
                if(Double.parseDouble(vHCDiscount)>0 && Double.parseDouble(vDisRate1)>0){
                    vDisRate1="0.00";
                }else if(Double.parseDouble(vHCDiscount)>0 && Double.parseDouble(vDisRate1)==0){
                    vDisRate1="0.00";
                }else if(Double.parseDouble(vHCDiscount)==0 && Double.parseDouble(vDisRate1)>0){
                    vHCDiscount="0.00";
                }
                if(qty!=0){
                    EditItem editItem=new EditItem(c,DocType,vItemCode,"-1",UnitPrice,vUOM,"1",vDetailTaxCode,vHCDiscount,vDisRate1,"0");
                    editItem.execute();
                    spacecraft.setId(spacecraft.getId()-1);
                    holder.btnAdd.setText(String.valueOf(spacecraft.getId()));
                }else{
                    holder.btnAdd.setText("+");
                }
            }
        });
    }
    @Override
    public int getItemCount()
    {
        return spacecrafts.size();
    }
    /*public class ModifierYN extends AsyncTask<Void,Void,String>{
        Context c;
        String ItemCode, ItemGroup, Qty, UnitPrice;
        String IPAddress,UserName,Password,Port,DBName,URL,z,DBStatus;

        public ModifierYN(Context c, String itemCode, String itemGroup, String qty, String unitPrice) {
            this.c = c;
            ItemCode = itemCode;
            ItemGroup = itemGroup;
            Qty = qty;
            UnitPrice = unitPrice;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return this.fncheck();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(result.equals("0")){
                AddItem_ByMain fnadd=new AddItem_ByMain(c,ItemCode,Qty,UnitPrice,"0","0");
                fnadd.execute();
               // Toast.makeText(c,"Add Item Failure", Toast.LENGTH_SHORT).show();
            }else{
                ((CashReceipt)c).showModifier(ItemCode,ItemGroup);
               // Toast.makeText(c,"Add Item Successfull", Toast.LENGTH_SHORT).show();
            }
        }
        private String fncheck() {
            try {
                JSONObject jsonReq,jsonRes;
                DBAdapter db = new DBAdapter(c);
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
                String sql="select count(*)as numrows from stk_group where ItemGroup='"+ItemGroup+"' and Modifier1!='' ";
                String numrows="0";
                if(DBStatus.equals("0")){
                    Cursor rsData=db.getQuery(sql);
                    while(rsData.moveToNext()){
                        numrows=rsData.getString(0);
                    }
                }else if(DBStatus.equals("1")){
                    URL = "jdbc:mysql://" + IPAddress + ":" + Port + "/" + DBName + "?useUnicode=yes&characterEncoding=ISO-8859-1";
                    Connection conn = Connector.connect(URL, UserName, Password);
                    if (conn != null) {
                        Statement stmt = conn.createStatement();
                        stmt.execute(sql);
                        ResultSet rsData = stmt.getResultSet();
                        while (rsData.next()) {
                            numrows=rsData.getString(1);
                        }
                    }
                }else if(DBStatus.equals("2")){
                    jsonReq=new JSONObject();
                    ConnectorLocal connectorLocal = new ConnectorLocal();
                    jsonReq.put("request", "request-connect-client");
                    jsonReq.put("query", sql);
                    jsonReq.put("action", "select");
                    String response     = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                    jsonRes             = new JSONObject(response);
                    String resData      = jsonRes.getString("hasil");
                    JSONArray jaData   = new JSONArray(resData);
                    JSONObject joData  = null;
                    for (int i = 0; i < jaData.length(); i++) {
                        joData = jaData.getJSONObject(i);
                        numrows = joData.getString("numrows");
                    }
                }
                z=numrows;
                db.closeDB();
                return z;
            }catch (SQLiteException e){
                e.printStackTrace();
            }catch (SQLException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
            return z;
        }
    }*/
}
