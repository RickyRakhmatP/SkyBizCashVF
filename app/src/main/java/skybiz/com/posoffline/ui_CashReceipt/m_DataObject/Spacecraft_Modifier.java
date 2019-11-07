package skybiz.com.posoffline.ui_CashReceipt.m_DataObject;

/**
 * Created by 7 on 09/05/2018.
 */

public class Spacecraft_Modifier {
    int id;
    String ItemGroup,Modifier,Remark;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemGroup() {
        return ItemGroup;
    }

    public void setItemGroup(String itemGroup) {
        ItemGroup = itemGroup;
    }

    public String getModifier() {
        return Modifier;
    }

    public void setModifier(String modifier) {
        Modifier = modifier;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }
}
