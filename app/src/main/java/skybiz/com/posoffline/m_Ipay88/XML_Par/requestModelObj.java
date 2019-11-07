package skybiz.com.posoffline.m_Ipay88.XML_Par;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import java.util.Hashtable;

public class requestModelObj implements KvmSerializable {

    private String Amount;
    private String BarcodeNo;
    private String Currency;
    private String MerchantCode;
    private String PaymentId;
    private String ProdDesc;
    private String RefNo;
    private String Remark;
    private String Signature;
    private String SignatureType;
    private String UserEmail;
    private String UserName;
    private String lang;


    @Override
    public Object getProperty(int i) {
        switch (i){
            case 0: return Amount;
            case 1: return BarcodeNo;
            case 2: return Currency;
            case 3: return MerchantCode;
            case 4: return PaymentId;
            case 5: return ProdDesc;
            case 6: return RefNo;
            case 7: return Remark;
            case 8: return Signature;
            case 9: return SignatureType;
            case 10: return UserEmail;
            case 11: return UserName;
            case 12: return lang;
            default: return null;
        }
    }

    @Override
    public int getPropertyCount() {
        return 12;
    }

    @Override
    public void setProperty(int i, Object o) {
        switch (i){
            case 0:
                Amount = o.toString();
                break;
            case 1:
                BarcodeNo = o.toString();
                break;
            case 2:
                Currency = o.toString();
                break;
            case 3:
                MerchantCode = o.toString();
                break;
            case 4:
                PaymentId = o.toString();
                break;
            case 5:
                ProdDesc = o.toString();
                break;
            case 6:
                RefNo = o.toString();
                break;
            case 7:
                Remark = o.toString();
                break;
            case 8:
                Signature = o.toString();
                break;
            case 9:
                SignatureType = o.toString();
                break;
            case 10:
                UserEmail = o.toString();
                break;
            case 11:
                UserName = o.toString();
                break;
            case 12:
                lang = o.toString();
                break;
            default:
                break;
        }
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        switch (i){
            case 0:
                propertyInfo.name = "Amount";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 1:
                propertyInfo.name = "BarCodeNo";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 2:
                propertyInfo.name = "Currency";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 3:
                propertyInfo.name = "MerchantCode";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 4:
                propertyInfo.name = "PaymentId";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 5:
                propertyInfo.name = "ProdDesc";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 6:
                propertyInfo.name = "RefNo";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 7:
                propertyInfo.name = "Remark";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 8:
                propertyInfo.name = "Signature";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 9:
                propertyInfo.name = "SignatureType";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 10:
                propertyInfo.name = "UserEmail";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 11:
                propertyInfo.name = "UserName";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            case 12:
                propertyInfo.name = "lang";
                propertyInfo.type = PropertyInfo.STRING_CLASS;
                break;
            default:
                break;
        }

    }
}
