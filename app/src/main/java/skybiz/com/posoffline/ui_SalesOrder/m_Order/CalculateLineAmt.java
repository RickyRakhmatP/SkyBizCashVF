package skybiz.com.posoffline.ui_SalesOrder.m_Order;

/**
 * Created by 7 on 08/11/2017.
 */

public class CalculateLineAmt {
    private final Double vDisRate1;
    private final Double vHCDiscount;
    private final Double vR_ate;
    private final Double vHCTax;
    private final Double vHCLineAmt;

    public CalculateLineAmt(Double vDisRate1, Double vHCDiscount, Double vR_ate, Double vHCTax, Double vHCLineAmt) {
        this.vDisRate1 = vDisRate1;
        this.vHCDiscount = vHCDiscount;
        this.vR_ate = vR_ate;
        this.vHCTax = vHCTax;
        this.vHCLineAmt = vHCLineAmt;
    }

    public Double getvDisRate1() {
        return vDisRate1;
    }

    public Double getvHCDiscount() {
        return vHCDiscount;
    }

    public Double getvR_ate() {
        return vR_ate;
    }

    public Double getvHCTax() {
        return vHCTax;
    }

    public Double getvHCLineAmt() {
        return vHCLineAmt;
    }
}


