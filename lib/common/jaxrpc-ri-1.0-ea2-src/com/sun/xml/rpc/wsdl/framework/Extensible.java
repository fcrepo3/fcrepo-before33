// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Extensible.java

package com.sun.xml.rpc.wsdl.framework;

import java.util.Iterator;

// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            Elemental, Extension

public interface Extensible
    extends Elemental {

    public abstract void addExtension(Extension extension);

    public abstract Iterator extensions();
}
