// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Extension.java

package com.sun.xml.rpc.wsdl.framework;


// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            Entity, ExtensionVisitor, Extensible

public abstract class Extension extends Entity {

    private Extensible _parent;

    public Extension() {
    }

    public Extensible getParent() {
        return _parent;
    }

    public void setParent(Extensible parent) {
        _parent = parent;
    }

    public void accept(ExtensionVisitor visitor) throws Exception {
        visitor.preVisit(this);
        visitor.postVisit(this);
    }
}
