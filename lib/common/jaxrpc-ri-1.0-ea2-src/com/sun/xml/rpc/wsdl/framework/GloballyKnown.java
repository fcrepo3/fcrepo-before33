// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   GloballyKnown.java

package com.sun.xml.rpc.wsdl.framework;


// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            Elemental, Kind, Defining

public interface GloballyKnown
    extends Elemental {

    public abstract String getName();

    public abstract Kind getKind();

    public abstract Defining getDefining();
}
