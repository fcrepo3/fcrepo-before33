// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   NameFactoryImpl.java

package com.sun.xml.rpc.soap.message;

import com.sun.xml.messaging.soap.dom4j.NameImpl;
import javax.xml.soap.Name;

// Referenced classes of package com.sun.xml.rpc.soap.message:
//            NameFactory

public class NameFactoryImpl
    implements NameFactory {

    public NameFactoryImpl() {
    }

    public Name createName(String localName, String prefix, String uri) {
        return NameImpl.create(localName, prefix, uri);
    }

    public Name createName(String localName) {
        return NameImpl.create(localName);
    }
}
