// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPSimpleType.java

package com.sun.xml.rpc.processor.model.soap;

import com.sun.xml.rpc.processor.model.java.JavaSimpleType;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model.soap:
//            SOAPType, SOAPTypeVisitor

public class SOAPSimpleType extends SOAPType {

    private QName schemaTypeRef;
    private boolean referenceable;

    public SOAPSimpleType(QName name) {
        this(name, null);
    }

    public SOAPSimpleType(QName name, JavaSimpleType javaType) {
        this(name, javaType, true);
    }

    public SOAPSimpleType(QName name, JavaSimpleType javaType, boolean referenceable) {
        super(name, javaType);
        this.referenceable = referenceable;
    }

    public QName getSchemaTypeRef() {
        return schemaTypeRef;
    }

    public void setSchemaTypeRef(QName n) {
        schemaTypeRef = n;
    }

    public boolean isReferenceable() {
        return referenceable;
    }

    public void setReferenceable(boolean b) {
        referenceable = b;
    }

    public void accept(SOAPTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
