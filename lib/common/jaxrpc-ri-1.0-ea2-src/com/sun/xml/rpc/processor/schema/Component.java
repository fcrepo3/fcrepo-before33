// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Component.java

package com.sun.xml.rpc.processor.schema;


// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            ComponentVisitor

public abstract class Component {

    public Component() {
    }

    public abstract void accept(ComponentVisitor componentvisitor) throws Exception;
}
