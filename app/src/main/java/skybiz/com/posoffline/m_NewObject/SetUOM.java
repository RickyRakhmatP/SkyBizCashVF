package skybiz.com.posoffline.m_NewObject;

public class SetUOM {
    String UOM,FactorQty, UnitPrice;

    public SetUOM(String UOM, String factorQty, String unitPrice) {
        this.UOM = UOM;
        FactorQty = factorQty;
        UnitPrice = unitPrice;
    }

    public static SetUOM set(String UnitPrice, String DefaultUOM, String UOM, String UOM1, String UOM2,
                             String UOM3, String UOM4, String UOMFactor1, String UOMFactor2,
                             String UOMFactor3, String UOMFactor4, String UOMPrice1, String UOMPrice2,
                             String UOMPrice3, String UOMPrice4){
        String vUOM="";
        String vFactorQty="";
        String vUnitPrice="";
        if(DefaultUOM.equals("0")){
            vUOM=UOM;
            vFactorQty="1";
            vUnitPrice=UnitPrice;
        }else if(DefaultUOM.equals("1")){
            vUOM=UOM1;
            vFactorQty=UOMFactor1;
            vUnitPrice=UOMPrice1;
        }else if(DefaultUOM.equals("2")){
            vUOM=UOM2;
            vFactorQty=UOMFactor2;
            vUnitPrice=UOMPrice2;
        }else if(DefaultUOM.equals("3")){
            vUOM=UOM3;
            vFactorQty=UOMFactor3;
            vUnitPrice=UOMPrice3;
        }else if(DefaultUOM.equals("4")){
            vUOM=UOM4;
            vFactorQty=UOMFactor4;
            vUnitPrice=UOMPrice4;
        }
        return new SetUOM(vUOM,vFactorQty,vUnitPrice);
    }
    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getFactorQty() {
        return FactorQty;
    }

    public void setFactorQty(String factorQty) {
        FactorQty = factorQty;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }
}
