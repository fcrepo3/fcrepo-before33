// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Facet.java

package com.sun.xml.rpc.processor.schema;

import javax.xml.rpc.namespace.QName;

public abstract class Facet {

    private QName name;

    public Facet(QName name) {
        this.name = name;
    }
}
