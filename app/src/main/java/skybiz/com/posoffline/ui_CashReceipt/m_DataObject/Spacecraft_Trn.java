package skybiz.com.posoffline.ui_CashReceipt.m_DataObject;

/**
 * Created by 7 on 03/11/2017.
 */

public class Spacecraft_Trn {
    String Qty,Doc1No,D_ate,
            D_ateTime,HCNetAmt,HCGbTax,
            HCDtTax,SynYN,Status,
            CusCode,CusName,CurCode,
            PaymentCode;
    int RunNo;
    private boolean Selected;

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getDoc1No() {
        return Doc1No;
    }

    public void setDoc1No(String doc1No) {
        Doc1No = doc1No;
    }

    public String getD_ate() {
        return D_ate;
    }

    public void setD_ate(String d_ate) {
        D_ate = d_ate;
    }

    public String getD_ateTime() {
        return D_ateTime;
    }

    public void setD_ateTime(String d_ateTime) {
        D_ateTime = d_ateTime;
    }

    public String getHCNetAmt() {
        return HCNetAmt;
    }

    public void setHCNetAmt(String HCNetAmt) {
        this.HCNetAmt = HCNetAmt;
    }

    public String getHCGbTax() {
        return HCGbTax;
    }

    public void setHCGbTax(String HCGbTax) {
        this.HCGbTax = HCGbTax;
    }

    public String getHCDtTax() {
        return HCDtTax;
    }

    public void setHCDtTax(String HCDtTax) {
        this.HCDtTax = HCDtTax;
    }

    public String getSynYN() {
        return SynYN;
    }

    public void setSynYN(String synYN) {
        SynYN = synYN;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCusCode() {
        return CusCode;
    }

    public void setCusCode(String cusCode) {
        CusCode = cusCode;
    }

    public String getCusName() {
        return CusName;
    }

    public void setCusName(String cusName) {
        CusName = cusName;
    }

    public String getCurCode() {
        return CurCode;
    }

    public void setCurCode(String curCode) {
        CurCode = curCode;
    }

    public String getPaymentCode() {
        return PaymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        PaymentCode = paymentCode;
    }

    public int getRunNo() {
        return RunNo;
    }

    public void setRunNo(int runNo) {
        RunNo = runNo;
    }

    public boolean isSelected() {
        return Selected;
    }

    public void setSelected(boolean selected) {
        Selected = selected;
    }
}
