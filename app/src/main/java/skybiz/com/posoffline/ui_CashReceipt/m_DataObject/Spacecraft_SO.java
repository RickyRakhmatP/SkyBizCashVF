package skybiz.com.posoffline.ui_CashReceipt.m_DataObject;

/**
 * Created by 7 on 18/01/2018.
 */

public class Spacecraft_SO {
    int id;
    String Doc1No,Doc2No,Attention;

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

    public String getDoc2No() {
        return Doc2No;
    }

    public void setDoc2No(String doc2No) {
        Doc2No = doc2No;
    }

    public String getAttention() {
        return Attention;
    }

    public void setAttention(String attention) {
        Attention = attention;
    }
}
