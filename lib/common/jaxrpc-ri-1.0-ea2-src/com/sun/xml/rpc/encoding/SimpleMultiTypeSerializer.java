// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SimpleMultiTypeSerializer.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.encoding.simpletype.SimpleTypeEncoder;
import java.util.HashSet;
import java.util.Set;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            SimpleTypeSerializer

public class SimpleMultiTypeSerializer extends SimpleTypeSerializer {

    private Set supportedTypes;

    public SimpleMultiTypeSerializer(QName type, boolean encodeType, boolean isNullable, String encodingStyle, SimpleTypeEncoder encoder, QName types[]) {
        super(type, encodeType, isNullable, encodingStyle, encoder);
        supportedTypes = new HashSet();
        for(int i = 0; i < types.length; i++)
            supportedTypes.add(types[i]);

    }

    protected boolean isAcceptableType(QName actualType) {
        return supportedTypes.contains(actualType);
    }
}
