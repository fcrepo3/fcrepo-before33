// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AttributesEx.java

package com.sun.xml.rpc.sp;

import org.xml.sax.Attributes;

public interface AttributesEx
    extends Attributes {

    public abstract boolean isSpecified(int i);

    public abstract String getDefault(int i);

    public abstract String getIdAttributeName();
}
