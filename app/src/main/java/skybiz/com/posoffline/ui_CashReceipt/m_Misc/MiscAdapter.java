package skybiz.com.posoffline.ui_CashReceipt.m_Misc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
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
import skybiz.com.posoffline.ui_CashReceipt.CashReceipt;
import skybiz.com.posoffline.ui_CashReceipt.m_DataObject.Spacecraft;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.Connector;
import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddItem_ByMain;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.AddMisc_ByMain;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.EditMisc;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.MinusItem;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 27/10/2017.
 */

public class MiscAdapter extends RecyclerView.Adapter<MiscHolder> {
    String IPAddress,UserName,Password,DBName,Qty;
    Context c;
    ArrayList<Spacecraft> spacecrafts;
    int clickcount=0;

    public MiscAdapter(Context c, ArrayList<Spacecraft> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }


    @Override
    public MiscHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model_item,parent,false);
        return new MiscHolder(v);

    }

    @Override
    public void onBindViewHolder(final MiscHolder holder, int position) {

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
                final String vUOM=spacecraft.getUOM();
                final String vDetailTaxCode=spacecraft.getRetailTaxCode();
                AddMisc_ByMain fnadd=new AddMisc_ByMain(c,vItemCode,vDescription,vQty,UnitPrice,vUOM,vDetailTaxCode,"0","0");
                fnadd.execute();
                holder.btnAdd.setText(String.valueOf(spacecraft.getId()));
            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                final int qty= spacecraft.getId();
                final String vQty= String.valueOf(spacecraft.getId());
                final String vUnitPrice=spacecraft.getUnitPrice();
                final String UnitPrice=vUnitPrice.replaceAll(",","");
                final String vItemCode=spacecraft.getItemCode();
                final String vDescription=spacecraft.getDescription();
                final String vUOM=spacecraft.getUOM();
                final String vDetailTaxCode=spacecraft.getRetailTaxCode();
                if(qty!=0){
                   // MinusItem fnminus=new MinusItem(c,spacecraft.getItemCode());
                   // fnminus.execute();
                    EditMisc editMisc=new EditMisc(c,vItemCode,"-1",vUnitPrice,vUOM,vDetailTaxCode,"0","0");
                    editMisc.execute();
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
}
