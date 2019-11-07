package skybiz.com.posoffline.m_Ipay88.XML_Par;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

public class EntryPageFunctionality implements KvmSerializable {

    private requestModelObj getrequest;
    @Override
    public Object getProperty(int i) {
        switch (i){
            case 0:
                return getrequest;
            default: return null;
        }

    }

    @Override
    public int getPropertyCount() {
        return 1;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i){
            case 0:
                getrequest = (requestModelObj) o;
                break;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i){
            case 0:
                propertyInfo.name = "EntryPageFunctionality";
                propertyInfo.type = requestModelObj.class;
                break;
        }
    }
}
