package skybiz.com.posoffline.ui_CashReceipt.m_Order;

/**
 * Created by 7 on 08/11/2017.
 */

public class FnAddItem {
    Double HCLineAmt, HCTax, HCDiscount, Qty;
    String ItemCode, Description;

    public FnAddItem(Double HCLineAmt, Double HCTax, Double HCDiscount, Double qty, String itemCode, String description) {

        this.HCLineAmt = HCLineAmt;
        this.HCTax = HCTax;
        this.HCDiscount = HCDiscount;
        Qty = qty;
        ItemCode = itemCode;
        Description = description;
    }

    public Double getHCLineAmt() {
        return HCLineAmt;
    }

    public Double getHCTax() {
        return HCTax;
    }

    public Double getHCDiscount() {
        return HCDiscount;
    }

    public Double getQty() {
        return Qty;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public String getDescription() {
        return Description;
    }
}
