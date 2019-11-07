package skybiz.com.posoffline.ui_CashReceipt.m_Item;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import skybiz.com.posoffline.R;
import skybiz.com.posoffline.m_NewObject.SetUOM;
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItem;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItemNew;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItem_ByMain;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItem_Old;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.MinusItem;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 27/10/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
    String IPAddress,UserName,Password,DBName,Qty;
    Context c;
    ArrayList<Spacecraft> spacecrafts;
    int clickcount=0;

    public ItemAdapter(Context c, ArrayList<Spacecraft> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }


    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item2,parent,false);
        return new ItemHolder(v);

    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        final Spacecraft spacecraft=spacecrafts.get(position);
        holder.vItemCode.setText(spacecraft.getItemCode());
        holder.vDescription.setText(spacecraft.getDescription()+"\n"+spacecraft.getAlternateItem());
        holder.vUnitPrice.setText(spacecraft.getCurCode()+spacecraft.getUnitPrice());
        Qty=spacecraft.getBtnQty();
        final int dqty= Integer.parseInt(Qty);;
       if(Qty.equals("0")) {
            holder.btnAdd.setText("+");
        }else{
            holder.btnAdd.setText(Qty);
            spacecraft.setId(dqty);
        }
        final String PhotoFile=spacecraft.getPhotoFile();
        if(!PhotoFile.isEmpty()){
            holder.vItemCode.setTextColor(Color.parseColor("#ffffff"));
            holder.vDescription.setTextColor(Color.parseColor("#ffffff"));
            holder.vUnitPrice.setTextColor(Color.parseColor("#CC0000"));
            holder.vPhotoFile.setVisibility(View.VISIBLE);
            final byte[] imgStr=Base64.decode(spacecraft.getPhotoFile(),Base64.DEFAULT);
            Bitmap bmp=BitmapFactory.decodeByteArray(imgStr,0,imgStr.length);
            Drawable img=new BitmapDrawable(Bitmap.createScaledBitmap(bmp,120,125,true));
            holder.vPhotoFile.setImageDrawable(img);
        }else{
            holder.vPhotoFile.setVisibility(View.GONE);
            holder.vPhotoFile.setImageBitmap(null);
            holder.vPhotoFile.setImageDrawable(null);
            holder.lnDesc.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.vItemCode.setTextColor(Color.parseColor("#000000"));
            holder.vDescription.setTextColor(Color.parseColor("#000000"));
            holder.vUnitPrice.setTextColor(Color.parseColor("#000000"));
        }

       // holder.
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                spacecraft.setId(spacecraft.getId()+1);
                final String vQty= String.valueOf(spacecraft.getId());
                final String vItemCode=spacecraft.getItemCode();
                final String vItemGroup=spacecraft.getItemGroup();
                final String vPrinter=spacecraft.getPrinter();
                final String vDetailTaxCode=spacecraft.getRetailTaxCode();
                final String vAlternateItem=spacecraft.getAlternateItem();
                SetUOM vData=SetUOM.set(spacecraft.getUnitPrice(),spacecraft.getDefaultUOM(),spacecraft.getUOM(),
                        spacecraft.getUOM1(),spacecraft.getUOM2(),spacecraft.getUOM3(),
                        spacecraft.getUOM4(),spacecraft.getUOMFactor1(),spacecraft.getUOMFactor2(),
                        spacecraft.getUOMFactor3(),spacecraft.getUOMFactor4(),spacecraft.getUOMPrice1(),
                        spacecraft.getUOMPrice2(),spacecraft.getUOMPrice3(),spacecraft.getUOMPrice4());
                final String vUOM=vData.getUOM();
                final String vUnitPrice=vData.getUnitPrice();
                final String vFactorQty=vData.getFactorQty();
                final String vDescription=spacecraft.getDescription();
                final String vModifier=spacecraft.getModifier();
                final String vPoint=spacecraft.getPoint();
                final String vMSP=spacecraft.getMSP();
                final String vUnitCost=spacecraft.getUnitCost();
                String vHCDiscount=spacecraft.getHCDiscount();
                String vDisRate1=spacecraft.getDisRate1();
                Log.d("HCDISCOUNT",vHCDiscount+"/"+vDisRate1);
                if(Double.parseDouble(vHCDiscount)>0 && Double.parseDouble(vDisRate1)>0){
                    vDisRate1="0.00";
                }else if(Double.parseDouble(vHCDiscount)>0 && Double.parseDouble(vDisRate1)==0){
                    vDisRate1="0.00";
                }else if(Double.parseDouble(vHCDiscount)==0 && Double.parseDouble(vDisRate1)>0){
                    vHCDiscount="0.00";
                }
                if(vModifier.length()>3){
                    ((CashReceipt)c).showModifier(vItemCode,vItemGroup,vPrinter,vHCDiscount,vDisRate1,vPoint);
                }else{
                    AddItemNew fnadd=new AddItemNew(c,vItemCode,vDescription,
                            vItemGroup,"1",vUnitPrice,
                            vUOM,vFactorQty,vHCDiscount,
                            vDisRate1,vDetailTaxCode,vPrinter,
                            vAlternateItem,vPoint,vMSP,
                            vUnitCost);
                    fnadd.execute();
                }
                holder.btnAdd.setText(String.valueOf(spacecraft.getId()));
            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                final int qty= spacecraft.getId();
                final String vQty= String.valueOf(spacecraft.getId());
                final String vUnitPrice=spacecraft.getUnitPrice();
                final String UnitPrice=vUnitPrice.replaceAll(",","");
                if(qty!=0){
                    MinusItem fnminus=new MinusItem(c,spacecraft.getItemCode());
                    fnminus.execute();
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
    public class ModifierYN extends AsyncTask<Void,Void,String>{
        Context c;
        String ItemCode, ItemGroup, Qty, UnitPrice;
        String IPAddress,UserName,Password,Port,DBName,URL,z,DBStatus,Printer;

        public ModifierYN(Context c, String itemCode, String itemGroup,
                          String qty, String unitPrice, String Printer) {
            this.c = c;
            ItemCode = itemCode;
            ItemGroup = itemGroup;
            Qty = qty;
            UnitPrice = unitPrice;
            this.Printer = Printer;
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
                AddItem_ByMain fnadd=new AddItem_ByMain(c,ItemCode,Qty,UnitPrice,"0","0",Printer,"0");
                fnadd.execute();
            }else{
               // ((CashReceipt)c).showModifier(ItemCode,ItemGroup,Printer);
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
    }
}
