// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ValueObjectSerializer.java

package com.sun.xml.rpc.encoding;

import java.lang.reflect.Method;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            GenericObjectSerializer, ValueObjectSerializer

class ValueObjectSerializer$2
    implements GenericObjectSerializer$SetterMethod {

    private final Method val$setterMethod; /* synthetic field */
    private final ValueObjectSerializer this$0; /* synthetic field */

    ValueObjectSerializer$2(ValueObjectSerializer this$0, Method val$setterMethod) {
        this.this$0 = this$0;
        this.val$setterMethod = val$setterMethod;
    }

    public void set(Object instance, Object value) throws Exception {
        val$setterMethod.invoke(instance, new Object[] {
            value
        });
    }
}
