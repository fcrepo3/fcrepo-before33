// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ValueObjectSerializer.java

package com.sun.xml.rpc.encoding;

import java.lang.reflect.Method;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            GenericObjectSerializer, ValueObjectSerializer

class ValueObjectSerializer$1
    implements GenericObjectSerializer$GetterMethod {

    private final Method val$getterMethod; /* synthetic field */
    private final ValueObjectSerializer this$0; /* synthetic field */

    ValueObjectSerializer$1(ValueObjectSerializer this$0, Method val$getterMethod) {
        this.this$0 = this$0;
        this.val$getterMethod = val$getterMethod;
    }

    public Object get(Object instance) throws Exception {
        return val$getterMethod.invoke(instance, new Object[0]);
    }
}
