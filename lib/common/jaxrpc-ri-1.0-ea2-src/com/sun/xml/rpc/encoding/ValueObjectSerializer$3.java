// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ValueObjectSerializer.java

package com.sun.xml.rpc.encoding;

import java.lang.reflect.Field;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            GenericObjectSerializer, ValueObjectSerializer

class ValueObjectSerializer$3
    implements GenericObjectSerializer$GetterMethod {

    private final Field val$currentField; /* synthetic field */
    private final ValueObjectSerializer this$0; /* synthetic field */

    ValueObjectSerializer$3(ValueObjectSerializer this$0, Field val$currentField) {
        this.this$0 = this$0;
        this.val$currentField = val$currentField;
    }

    public Object get(Object instance) throws Exception {
        return val$currentField.get(instance);
    }
}
