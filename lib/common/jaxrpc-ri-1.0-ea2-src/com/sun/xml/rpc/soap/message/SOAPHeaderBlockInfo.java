// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPHeaderBlockInfo.java

package com.sun.xml.rpc.soap.message;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.soap.message:
//            SOAPBlockInfo

public class SOAPHeaderBlockInfo extends SOAPBlockInfo {

    private String _actor;
    private boolean _mustUnderstand;

    public SOAPHeaderBlockInfo(QName name, String actor, boolean mustUnderstand) {
        super(name);
        _actor = actor;
        _mustUnderstand = mustUnderstand;
    }
}
