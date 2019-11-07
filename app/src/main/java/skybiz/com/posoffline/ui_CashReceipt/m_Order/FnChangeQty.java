package skybiz.com.posoffline.ui_CashReceipt.m_Order;

/**
 * Created by 7 on 14/11/2017.
 */

public class FnChangeQty {
    String Qty,ItemCode;

    public FnChangeQty(String qty, String itemCode) {
        Qty = qty;
        ItemCode = itemCode;
    }

    public String getQty() {
        return Qty;
    }

    public String getItemCode() {
        return ItemCode;
    }


}
