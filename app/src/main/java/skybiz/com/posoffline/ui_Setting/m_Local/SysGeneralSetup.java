package skybiz.com.posoffline.ui_Setting.m_Local;

/**
 * Created by 7 on 16/11/2017.
 */

public class SysGeneralSetup {
    String CurCode,GSTNo,CompanyName,RoundingCS,LayawayAsSalesYN,vPostGlobalTaxYN,Doc1No;
    int RunNo;

    public SysGeneralSetup(String curCode, String GSTNo, String companyName, String roundingCS, String layawayAsSalesYN, String vPostGlobalTaxYN, String doc1No, int runNo) {
        CurCode = curCode;
        this.GSTNo = GSTNo;
        CompanyName = companyName;
        RoundingCS = roundingCS;
        LayawayAsSalesYN = layawayAsSalesYN;
        this.vPostGlobalTaxYN = vPostGlobalTaxYN;
        Doc1No = doc1No;
        RunNo = runNo;
    }

    public String getCurCode() {
        return CurCode;
    }

    public void setCurCode(String curCode) {
        CurCode = curCode;
    }

    public String getGSTNo() {
        return GSTNo;
    }

    public void setGSTNo(String GSTNo) {
        this.GSTNo = GSTNo;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getRoundingCS() {
        return RoundingCS;
    }

    public void setRoundingCS(String roundingCS) {
        RoundingCS = roundingCS;
    }

    public String getLayawayAsSalesYN() {
        return LayawayAsSalesYN;
    }

    public void setLayawayAsSalesYN(String layawayAsSalesYN) {
        LayawayAsSalesYN = layawayAsSalesYN;
    }

    public String getvPostGlobalTaxYN() {
        return vPostGlobalTaxYN;
    }

    public void setvPostGlobalTaxYN(String vPostGlobalTaxYN) {
        this.vPostGlobalTaxYN = vPostGlobalTaxYN;
    }

    public String getDoc1No() {
        return Doc1No;
    }

    public void setDoc1No(String doc1No) {
        Doc1No = doc1No;
    }

    public int getRunNo() {
        return RunNo;
    }

    public void setRunNo(int runNo) {
        RunNo = runNo;
    }
}
