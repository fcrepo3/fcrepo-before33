// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   RPCRequestUnorderedStructureType.java

package com.sun.xml.rpc.processor.model.soap;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model.soap:
//            SOAPUnorderedStructureType, SOAPTypeVisitor

public class RPCRequestUnorderedStructureType extends SOAPUnorderedStructureType {

    public RPCRequestUnorderedStructureType(QName name) {
        super(name);
    }

    public void accept(SOAPTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
