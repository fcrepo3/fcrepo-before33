// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   GenericObjectSerializer.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.util.List;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            DeserializationException, SOAPInstanceBuilder, GenericObjectSerializer

public class GenericObjectSerializer$SOAPGenericObjectInstanceBuilder
    implements SOAPInstanceBuilder {

    Object instance;
    private final GenericObjectSerializer this$0; /* synthetic field */

    GenericObjectSerializer$SOAPGenericObjectInstanceBuilder(GenericObjectSerializer this$0, Object instance) {
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
            ((GenericObjectSerializer$MemberInfo)this$0.members.get(index)).setter.set(instance, memberValue);
        }
        catch(Exception e) {
            throw new DeserializationException("nestedSerializationError", new LocalizableExceptionAdapter(e));
        }
    }

    public void initialize() {
    }

    public void setInstance(Object instance) {
        instance = instance;
    }

    public Object getInstance() {
        return instance;
    }
}
