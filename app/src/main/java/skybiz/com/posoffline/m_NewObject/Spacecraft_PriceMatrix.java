package skybiz.com.posoffline.m_NewObject;

public class Spacecraft_PriceMatrix {
    int id;
    String ItemCode,ItemGroup,CategoryCode,
            Description,Pct,Criteria,
            PeriodYN,B_ase,TimeStart,
            TimeEnd,Status,ServiceChargeYN,
            Memo,Qty;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getItemGroup() {
        return ItemGroup;
    }

    public void setItemGroup(String itemGroup) {
        ItemGroup = itemGroup;
    }

    public String getCategoryCode() {
        return CategoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        CategoryCode = categoryCode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPct() {
        return Pct;
    }

    public void setPct(String pct) {
        Pct = pct;
    }

    public String getCriteria() {
        return Criteria;
    }

    public void setCriteria(String criteria) {
        Criteria = criteria;
    }

    public String getPeriodYN() {
        return PeriodYN;
    }

    public void setPeriodYN(String periodYN) {
        PeriodYN = periodYN;
    }

    public String getB_ase() {
        return B_ase;
    }

    public void setB_ase(String b_ase) {
        B_ase = b_ase;
    }

    public String getTimeStart() {
        return TimeStart;
    }

    public void setTimeStart(String timeStart) {
        TimeStart = timeStart;
    }

    public String getTimeEnd() {
        return TimeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        TimeEnd = timeEnd;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getServiceChargeYN() {
        return ServiceChargeYN;
    }

    public void setServiceChargeYN(String serviceChargeYN) {
        ServiceChargeYN = serviceChargeYN;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }
}
