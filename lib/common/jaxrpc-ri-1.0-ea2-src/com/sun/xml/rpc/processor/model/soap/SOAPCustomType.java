// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPCustomType.java

package com.sun.xml.rpc.processor.model.soap;

import com.sun.xml.rpc.processor.schema.TypeDefinitionComponent;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model.soap:
//            SOAPType, SOAPTypeVisitor

public class SOAPCustomType extends SOAPType {

    private TypeDefinitionComponent schemaType;

    public SOAPCustomType(QName name) {
        super(name);
    }

    public TypeDefinitionComponent getSchemaType() {
        return schemaType;
    }

    public void setSchemaType(TypeDefinitionComponent t) {
        schemaType = t;
    }

    public void accept(SOAPTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
