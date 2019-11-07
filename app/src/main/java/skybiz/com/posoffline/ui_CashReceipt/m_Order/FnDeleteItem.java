package skybiz.com.posoffline.ui_CashReceipt.m_Order;

/**
 * Created by 7 on 10/11/2017.
 */

public class FnDeleteItem {
    String ItemCode;
    int RunNo;

    public FnDeleteItem(String itemCode, int runNo) {
        ItemCode = itemCode;
        RunNo = runNo;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public int getRunNo() {
        return RunNo;
    }
}
