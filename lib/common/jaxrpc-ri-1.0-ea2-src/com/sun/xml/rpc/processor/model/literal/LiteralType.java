// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralType.java

package com.sun.xml.rpc.processor.model.literal;

import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.schema.TypeDefinitionComponent;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model.literal:
//            LiteralTypeVisitor

public abstract class LiteralType extends AbstractType {

    private TypeDefinitionComponent _schemaType;
    private QName _schemaTypeRef;

    public LiteralType() {
    }

    protected LiteralType(QName name, JavaType javaType) {
        super(name, javaType);
    }

    public TypeDefinitionComponent getSchemaType() {
        return _schemaType;
    }

    public void setSchemaType(TypeDefinitionComponent t) {
        _schemaType = t;
    }

    public QName getSchemaTypeRef() {
        return _schemaTypeRef;
    }

    public void setSchemaTypeRef(QName n) {
        _schemaTypeRef = n;
    }

    public boolean isLiteralType() {
        return true;
    }

    public abstract void accept(LiteralTypeVisitor literaltypevisitor) throws Exception;
}
