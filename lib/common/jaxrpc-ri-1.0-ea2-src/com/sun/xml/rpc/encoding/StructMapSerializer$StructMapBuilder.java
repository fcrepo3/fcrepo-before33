// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StructMapSerializer.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.util.StructMap;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            DeserializationException, SOAPInstanceBuilder, StructMapSerializer

public class StructMapSerializer$StructMapBuilder
    implements SOAPInstanceBuilder {

    StructMap instance;
    private final StructMapSerializer this$0; /* synthetic field */

    StructMapSerializer$StructMapBuilder(StructMapSerializer this$0, StructMap instance) {
        this.this$0 = this$0;
        this.instance = instance;
    }

    public int memberGateType(int memberIndex) {
        return 6;
    }

    public void construct() {
    }

    public void setMember(int index, Object memberValue) {
        try {
            instance.set(index, memberValue);
        }
        catch(Exception e) {
            throw new DeserializationException("nestedSerializationError", new LocalizableExceptionAdapter(e));
        }
    }

    public void initialize() {
    }

    public void setInstance(Object instance) {
        instance = (StructMap)instance;
    }

    public Object getInstance() {
        return instance;
    }
}
