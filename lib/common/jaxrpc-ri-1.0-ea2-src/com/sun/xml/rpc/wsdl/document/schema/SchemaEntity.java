// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SchemaEntity.java

package com.sun.xml.rpc.wsdl.document.schema;

import com.sun.xml.rpc.wsdl.framework.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document.schema:
//            SchemaElement, Schema

public class SchemaEntity extends Entity
    implements GloballyKnown {

    private Schema _parent;
    private SchemaElement _element;
    private Kind _kind;
    private QName _name;

    public SchemaEntity(Schema parent, SchemaElement element, Kind kind, QName name) {
        _parent = parent;
        _element = element;
        _kind = kind;
        _name = name;
    }

    public SchemaElement getElement() {
        return _element;
    }

    public QName getElementName() {
        return _element.getQName();
    }

    public String getName() {
        return _name.getLocalPart();
    }

    public Kind getKind() {
        return _kind;
    }

    public Schema getSchema() {
        return _parent;
    }

    public Defining getDefining() {
        return _parent;
    }

    public void validateThis() {
    }
}
