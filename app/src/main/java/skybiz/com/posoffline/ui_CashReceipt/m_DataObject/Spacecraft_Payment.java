package skybiz.com.posoffline.ui_CashReceipt.m_DataObject;

/**
 * Created by 7 on 29/11/2017.
 */

public class Spacecraft_Payment {
    int id;
    String Doc1No;
    Double HCGbTax,TotalAmt,GbTaxRate1,HCDtTax,HCGbDiscount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDoc1No() {
        return Doc1No;
    }

    public void setDoc1No(String doc1No) {
        Doc1No = doc1No;
    }

    public Double getHCGbTax() {
        return HCGbTax;
    }

    public void setHCGbTax(Double HCGbTax) {
        this.HCGbTax = HCGbTax;
    }

    public Double getTotalAmt() {
        return TotalAmt;
    }

    public void setTotalAmt(Double totalAmt) {
        TotalAmt = totalAmt;
    }

    public Double getGbTaxRate1() {
        return GbTaxRate1;
    }

    public void setGbTaxRate1(Double gbTaxRate1) {
        GbTaxRate1 = gbTaxRate1;
    }

    public Double getHCDtTax() {
        return HCDtTax;
    }

    public void setHCDtTax(Double HCDtTax) {
        this.HCDtTax = HCDtTax;
    }

    public Double getHCGbDiscount() {
        return HCGbDiscount;
    }

    public void setHCGbDiscount(Double HCGbDiscount) {
        this.HCGbDiscount = HCGbDiscount;
    }
}
