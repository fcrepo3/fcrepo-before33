// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JAXRPCSerializer.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.streaming.XMLWriter;
import javax.xml.rpc.encoding.Serializer;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            SerializerCallback, SOAPSerializationContext

public interface JAXRPCSerializer
    extends Serializer {

    public abstract void serialize(Object obj, QName qname, SerializerCallback serializercallback, XMLWriter xmlwriter, SOAPSerializationContext soapserializationcontext);
}
