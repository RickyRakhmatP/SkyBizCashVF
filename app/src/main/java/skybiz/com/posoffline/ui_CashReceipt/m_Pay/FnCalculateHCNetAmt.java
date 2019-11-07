package skybiz.com.posoffline.ui_CashReceipt.m_Pay;

/**
 * Created by 7 on 16/11/2017.
 */

public class FnCalculateHCNetAmt {
    private final Double HCNetAmt;
    private final Double HCDtTax;
    private final Double HCGbTax;
    private final Double HCGbDiscount;
    private final Double GbTaxRate1;
    private final String GlobalTaxCode;
    private final Double TotalPoint;

    public FnCalculateHCNetAmt(Double HCNetAmt, Double HCDtTax, Double HCGbTax,
                               Double HCGbDiscount, Double gbTaxRate1, String globalTaxCode,
                               Double TotalPoint) {
        this.HCNetAmt = HCNetAmt;
        this.HCDtTax = HCDtTax;
        this.HCGbTax = HCGbTax;
        this.HCGbDiscount = HCGbDiscount;
        GbTaxRate1 = gbTaxRate1;
        GlobalTaxCode = globalTaxCode;
        this.TotalPoint = TotalPoint;
    }

    public Double getHCNetAmt() {
        return HCNetAmt;
    }

    public Double getHCDtTax() {
        return HCDtTax;
    }

    public Double getHCGbTax() {
        return HCGbTax;
    }

    public Double getHCGbDiscount() {
        return HCGbDiscount;
    }

    public Double getGbTaxRate1() {
        return GbTaxRate1;
    }

    public String getGlobalTaxCode() {
        return GlobalTaxCode;
    }

    public Double getTotalPoint() {
        return TotalPoint;
    }
}
