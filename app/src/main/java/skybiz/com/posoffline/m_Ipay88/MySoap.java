package skybiz.com.posoffline.m_Ipay88;

import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

public class MySoap extends SoapSerializationEnvelope {

    public MySoap (int version) {
        super(version);
    }

    @Override
    public void write(XmlSerializer writer) throws IOException {
        env = "http://schemas.xmlsoap.org/soap/envelope/";
        String tem = "https://www.mobile88.com";
        writer.startDocument("UTF-8", true);
        writer.setPrefix("SOAP-ENV", env);
        writer.setPrefix("ns1", tem);
        writer.startTag(env, "Envelope");
        writer.startTag(env, "Body");
        writer.startTag(tem, "VoidTransaction");
        writeBody(writer);
        writer.endTag(tem, "VoidTransaction");
        writer.endTag(env, "Body");
        writer.endTag(env, "Envelope");
        writer.endDocument();
    }
}
