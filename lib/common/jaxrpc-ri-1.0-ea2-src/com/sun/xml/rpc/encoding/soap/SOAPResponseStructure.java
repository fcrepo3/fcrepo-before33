// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPResponseStructure.java

package com.sun.xml.rpc.encoding.soap;

import java.util.HashMap;
import java.util.Map;

public class SOAPResponseStructure {

    public Object returnValue;
    public Map outParameters;

    public SOAPResponseStructure() {
        outParameters = new HashMap();
    }
}
