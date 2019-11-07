package skybiz.com.posoffline.ui_Setting.m_Local;

/**
 * Created by 7 on 12/12/2017.
 */

public class SettingPrint {
    int RunNo;
    String TypePrinter,NamePrinter,IPPrinter,UUID;

    public SettingPrint(int runNo, String typePrinter, String namePrinter, String IPPrinter, String UUID) {
        RunNo = runNo;
        TypePrinter = typePrinter;
        NamePrinter = namePrinter;
        this.IPPrinter = IPPrinter;
        this.UUID = UUID;
    }

    public int getRunNo() {
        return RunNo;
    }

    public void setRunNo(int runNo) {
        RunNo = runNo;
    }

    public String getTypePrinter() {
        return TypePrinter;
    }

    public void setTypePrinter(String typePrinter) {
        TypePrinter = typePrinter;
    }

    public String getNamePrinter() {
        return NamePrinter;
    }

    public void setNamePrinter(String namePrinter) {
        NamePrinter = namePrinter;
    }

    public String getIPPrinter() {
        return IPPrinter;
    }

    public void setIPPrinter(String IPPrinter) {
        this.IPPrinter = IPPrinter;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}
