package skybiz.com.posoffline.ui_CashReceipt.m_DataObject;

/**
 * Created by 7 on 20/12/2017.
 */

public class Spacecraft_PayType {
    int RunNo;
    String PaymentCode,PaymentType,PaidByCompanyYN,Charges1,MerchantCode,MerchantKey;

    public int getRunNo() {
        return RunNo;
    }

    public void setRunNo(int runNo) {
        RunNo = runNo;
    }

    public String getPaymentCode() {
        return PaymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        PaymentCode = paymentCode;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }

    public String getPaidByCompanyYN() {
        return PaidByCompanyYN;
    }

    public void setPaidByCompanyYN(String paidByCompanyYN) {
        PaidByCompanyYN = paidByCompanyYN;
    }

    public String getCharges1() {
        return Charges1;
    }

    public void setCharges1(String charges1) {
        Charges1 = charges1;
    }

    public String getMerchantCode() {
        return MerchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        MerchantCode = merchantCode;
    }

    public String getMerchantKey() {
        return MerchantKey;
    }

    public void setMerchantKey(String merchantKey) {
        MerchantKey = merchantKey;
    }
}
