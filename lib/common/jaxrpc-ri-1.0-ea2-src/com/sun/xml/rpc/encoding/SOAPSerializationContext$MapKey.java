// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPSerializationContext.java

package com.sun.xml.rpc.encoding;


// Referenced classes of package com.sun.xml.rpc.encoding:
//            SOAPSerializationContext

class SOAPSerializationContext$MapKey {

    Object obj;

    public SOAPSerializationContext$MapKey(Object obj) {
        this.obj = obj;
    }

    public boolean equals(Object o) {
        if(!(o instanceof SOAPSerializationContext$MapKey))
            return false;
        else
            return obj == ((SOAPSerializationContext$MapKey)o).obj;
    }

    public int hashCode() {
        return System.identityHashCode(obj);
    }
}
