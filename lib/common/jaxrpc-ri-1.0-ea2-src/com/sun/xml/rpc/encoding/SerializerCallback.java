// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SerializerCallback.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.streaming.XMLWriter;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            SerializationException, SOAPSerializationContext

public interface SerializerCallback {

    public abstract void onStartTag(Object obj, QName qname, XMLWriter xmlwriter, SOAPSerializationContext soapserializationcontext) throws SerializationException;
}
