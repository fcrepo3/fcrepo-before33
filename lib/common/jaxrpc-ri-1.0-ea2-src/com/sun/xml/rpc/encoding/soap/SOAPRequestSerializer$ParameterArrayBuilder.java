// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPRequestSerializer.java

package com.sun.xml.rpc.encoding.soap;

import com.sun.xml.rpc.encoding.DeserializationException;
import com.sun.xml.rpc.encoding.SOAPInstanceBuilder;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

// Referenced classes of package com.sun.xml.rpc.encoding.soap:
//            SOAPRequestSerializer

public class SOAPRequestSerializer$ParameterArrayBuilder
    implements SOAPInstanceBuilder {

    Object instance[];

    SOAPRequestSerializer$ParameterArrayBuilder(Object instance[]) {
        this.instance = null;
        this.instance = instance;
    }

    public int memberGateType(int memberIndex) {
        return 6;
    }

    public void construct() {
    }

    public void setMember(int index, Object memberValue) {
        try {
            instance[index] = memberValue;
        }
        catch(Exception e) {
            throw new DeserializationException("nestedSerializationError", new LocalizableExceptionAdapter(e));
        }
    }

    public void initialize() {
    }

    public void setInstance(Object instance) {
        instance = ((Object) ((Object[])instance));
    }

    public Object getInstance() {
        return ((Object) (instance));
    }
}
