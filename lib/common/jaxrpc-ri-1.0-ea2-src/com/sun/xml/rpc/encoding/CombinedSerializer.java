// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   CombinedSerializer.java

package com.sun.xml.rpc.encoding;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            JAXRPCSerializer, JAXRPCDeserializer

public interface CombinedSerializer
    extends JAXRPCSerializer, JAXRPCDeserializer {

    public abstract QName getXmlType();

    public abstract boolean getEncodeType();

    public abstract boolean isNullable();

    public abstract String getEncodingStyle();

    public abstract CombinedSerializer getInnermostSerializer();
}
