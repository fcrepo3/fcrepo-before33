// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPType.java

package com.sun.xml.rpc.processor.model.soap;

import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model.soap:
//            SOAPTypeVisitor

public abstract class SOAPType extends AbstractType {

    protected SOAPType(QName name) {
        super(name);
    }

    protected SOAPType(QName name, JavaType javaType) {
        super(name, javaType);
    }

    public boolean isNillable() {
        return true;
    }

    public boolean isReferenceable() {
        return true;
    }

    public boolean isSOAPType() {
        return true;
    }

    public abstract void accept(SOAPTypeVisitor soaptypevisitor) throws Exception;
}
