// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   TypeMapping.java

package javax.xml.rpc.encoding;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package javax.xml.rpc.encoding:
//            SerializerFactory, DeserializerFactory

public interface TypeMapping {

    public abstract String[] getSupportedNamespaces();

    public abstract void setSupportedNamespaces(String as[]);

    public abstract boolean isRegistered(Class class1, QName qname);

    public abstract void register(Class class1, QName qname, SerializerFactory serializerfactory, DeserializerFactory deserializerfactory);

    public abstract SerializerFactory getSerializer(Class class1, QName qname);

    public abstract DeserializerFactory getDeserializer(Class class1, QName qname);

    public abstract void removeSerializer(Class class1, QName qname);

    public abstract void removeDeserializer(Class class1, QName qname);
}
