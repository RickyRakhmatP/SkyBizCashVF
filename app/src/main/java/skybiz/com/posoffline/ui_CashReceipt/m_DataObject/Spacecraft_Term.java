package skybiz.com.posoffline.ui_CashReceipt.m_DataObject;

public class Spacecraft_Term {

    int id;
    String TermCode,TermDesc,D_ay;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTermCode() {
        return TermCode;
    }

    public void setTermCode(String termCode) {
        TermCode = termCode;
    }

    public String getTermDesc() {
        return TermDesc;
    }

    public void setTermDesc(String termDesc) {
        TermDesc = termDesc;
    }

    public String getD_ay() {
        return D_ay;
    }

    public void setD_ay(String d_ay) {
        D_ay = d_ay;
    }
}
