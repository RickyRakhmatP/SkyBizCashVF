package skybiz.com.posoffline.m_NewSummary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import skybiz.com.posoffline.ui_CashReceipt.m_MySQL.ConnectorLocal;
import skybiz.com.posoffline.ui_CashReceipt.m_Order.CalculateLineAmt;
import skybiz.com.posoffline.ui_Setting.m_Local.DBAdapter;

/**
 * Created by 7 on 16/04/2018.
 */

public class EditItem2 extends AsyncTask<Void, Void, String> {
    Context c;
    String RunNo,vQty,vUnitPrice,vHCDiscount,vDisRate1,vUOM,vDetailTaxCode,vFactorQty,DocType;
    String IPAddress, UserName, Password,DBName,Port,DBStatus,ItemConn,URL,z;
    String deviceId;
    Double Qty,UnitPrice,HCTax, HCDiscount, HCLineAmt,dQty,DisRate1,FactorQty;
    TelephonyManager telephonyManager;
    public EditItem2(Context c, String DocType, String RunNo, String vQty, String vUnitPrice, String vUOM, String vFactorQty, String vDetailTaxCode, String vHCDiscount, String vDisRate1) {
        this.c = c;
        this.DocType = DocType;
        this.RunNo = RunNo;
        this.vQty = vQty;
        this.vUnitPrice = vUnitPrice;
        this.vUOM = vUOM;
        this.vFactorQty = vFactorQty;
        this.vDetailTaxCode = vDetailTaxCode;
        this.vHCDiscount = vHCDiscount;
        this.vDisRate1 = vDisRate1;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.fnadd();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result==null){
            Toast.makeText(c,"Edit Item Failure", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(c,"Edit Item Successfull", Toast.LENGTH_SHORT).show();
        }
    }
    private String fnadd(){
        try{
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
                ItemConn=curSet.getString(8);
            }
            String vData="";
            Qty         = Double.parseDouble(vQty);
            UnitPrice   = Double.parseDouble(vUnitPrice);
            HCDiscount  = Double.parseDouble(vHCDiscount);
            DisRate1    = Double.parseDouble(vDisRate1);
            FactorQty   = Double.parseDouble(vFactorQty);
            vData = updateItem(RunNo, Qty, UnitPrice, vDetailTaxCode, vUOM, FactorQty, HCDiscount, DisRate1);
            db.closeDB();
            return vData;
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return null;
    }

    //update item new
    private String updateItem(String RunNo, Double Qty, Double UnitPrice, String DetailTaxCode, String UOM, Double FactorQty, Double HCDiscount, Double DisRate1){
        String vUpdate="";
        Double TaxRate1=0.00;
        JSONObject jsonReq,jsonRes;
        try{
            DBAdapter db=new DBAdapter(c);
            db.openDB();
            String checkGST="select GSTNo from companysetup";
            String GSTYN="";
            Cursor rsGST=db.getQuery(checkGST);
            while(rsGST.moveToNext()){
                GSTYN=rsGST.getString(0);
            }
            if(GSTYN.equals("NO")){
                DetailTaxCode="";
            }
            CalculateLineAmt vDataAmt = fncalculate(c, Qty, UnitPrice, DisRate1, HCDiscount, DetailTaxCode);
            TaxRate1 = vDataAmt.getvR_ate();
            DisRate1 = vDataAmt.getvDisRate1();
            HCDiscount = vDataAmt.getvHCDiscount();
            HCTax = vDataAmt.getvHCTax();
            HCLineAmt = vDataAmt.getvHCLineAmt();
            String vSQLUpdate = "UPDATE cloud_cus_inv_dt SET Qty='" + Qty + "'," +
                    " HCUnitCost='" + UnitPrice + "', DisRate1='" + DisRate1 + "'," +
                    " HCDiscount='" + HCDiscount + "', HCTax='" + HCTax + "', " +
                    " HCLineAmt='" + HCLineAmt + "', UOM='"+UOM+"', FactorQty='"+FactorQty+"' " +
                    " where RunNo ='" + RunNo + "' ";
            Log.d("EDIT",vSQLUpdate);
            if(DBStatus.equals("2")){
                jsonReq=new JSONObject();
                ConnectorLocal connectorLocal = new ConnectorLocal();
                jsonReq.put("request", "request-connect-client");
                jsonReq.put("query", vSQLUpdate);
                jsonReq.put("action", "update");
                String response = connectorLocal.ConnectSocket(IPAddress, 8080, jsonReq.toString());
                jsonRes = new JSONObject(response);
                String hasil = jsonRes.getString("success");
                Log.d("RES", hasil);
            }else {
                db.updateQuery(vSQLUpdate);
            }
            db.closeDB();
            return vUpdate;
        }catch (JSONException e){
            e.printStackTrace();
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return vUpdate;
    }

    public static CalculateLineAmt fncalculate(Context context, Double Qty, Double HCUnitCost, Double DisRate1, Double HCDiscount, String DetailTaxCode) {
        Double vR_ate,vHCTax,vAmountB4Tax,vTempAmt,vHCLineAmt,vDisRate1,vHCDiscount;
        String vTaxType;
        vR_ate      =0.00;
        vHCTax      =0.00;
        vHCLineAmt  =0.00;
        vHCDiscount =HCDiscount;
        vDisRate1   =0.00;
        if(DisRate1>0){
            vDisRate1		=DisRate1/100;
            vHCDiscount	    =Qty*HCUnitCost*vDisRate1;
            vAmountB4Tax	=(Qty*HCUnitCost)-vHCDiscount;
        }else{
            if(HCDiscount>0){
                vDisRate1	    =(HCDiscount*100)/(Qty*HCUnitCost);
                vHCDiscount	    = HCDiscount;
                vAmountB4Tax	=(Qty*HCUnitCost)-vHCDiscount;
                DisRate1        =vDisRate1;
            }else{
                vHCDiscount	    =0.00;
                DisRate1	    =0.00;
                vAmountB4Tax	 =(Qty*HCUnitCost)-vHCDiscount;
            }
        }
        if(DetailTaxCode.isEmpty()) {
            vHCLineAmt   = vAmountB4Tax;
            vR_ate       = 0.00;
            vHCTax       = 0.00;
        }else{
            String strSQL = "select R_ate,TaxType from stk_tax where TaxCode='" + DetailTaxCode + "' ";
            DBAdapter db = new DBAdapter(context);
            db.openDB();
            Cursor resultSet2 = db.getQuery(strSQL);
            while (resultSet2.moveToNext()) {
               // vR_ate      = Double.parseDouble(resultSet2.getString(0));
                vR_ate      = 0.00;
                vTaxType    = resultSet2.getString(1);
                if (vTaxType.equals("0") || vTaxType.equals("2")) {
                    vHCTax      =vAmountB4Tax * (vR_ate / (vR_ate + 100));
                    vTempAmt    =vAmountB4Tax - vHCTax;
                    vHCLineAmt  =vTempAmt + vHCTax;
                } else if (vTaxType.equals("1") || vTaxType.equals("3")) {
                    vHCTax      = vAmountB4Tax * (vR_ate / 100);
                    vHCLineAmt  = vAmountB4Tax + vHCTax;
                }
            }
            db.closeDB();
        }
        Log.d("RESULT", "TAX: " + vHCTax.toString());
        return new CalculateLineAmt(DisRate1, vHCDiscount, vR_ate, vHCTax, vHCLineAmt);
    }

    private String encodeChar(String txt){
        String newText="";
        try{
            byte[] b=txt.getBytes("ISO-8859-1");
            newText=new String(b,"utf-8");
            return newText;
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return newText;
    }
}
