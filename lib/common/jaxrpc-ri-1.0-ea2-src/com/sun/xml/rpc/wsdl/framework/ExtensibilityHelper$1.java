// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ExtensibilityHelper.java

package com.sun.xml.rpc.wsdl.framework;

import java.util.Iterator;
import java.util.NoSuchElementException;

// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            ExtensibilityHelper

class ExtensibilityHelper$1
    implements Iterator {

    private final ExtensibilityHelper this$0; /* synthetic field */

    ExtensibilityHelper$1(ExtensibilityHelper this$0) {
        this.this$0 = this$0;
    }

    public boolean hasNext() {
        return false;
    }

    public Object next() {
        throw new NoSuchElementException();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
