// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPSerializationState.java

package com.sun.xml.rpc.encoding;


// Referenced classes of package com.sun.xml.rpc.encoding:
//            ReferenceableSerializer

public class SOAPSerializationState {

    private Object obj;
    private String id;
    private ReferenceableSerializer serializer;

    public SOAPSerializationState(Object obj, String id, ReferenceableSerializer serializer) {
        this.obj = obj;
        this.id = id;
        this.serializer = serializer;
    }

    public Object getObject() {
        return obj;
    }

    public String getID() {
        return id;
    }

    public ReferenceableSerializer getSerializer() {
        return serializer;
    }
}
