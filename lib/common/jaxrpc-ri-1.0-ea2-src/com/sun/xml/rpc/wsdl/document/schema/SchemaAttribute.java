// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SchemaAttribute.java

package com.sun.xml.rpc.wsdl.document.schema;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document.schema:
//            SchemaElement

public class SchemaAttribute {

    private String _nsURI;
    private String _localName;
    private String _value;
    private QName _qnameValue;
    private SchemaElement _parent;

    public SchemaAttribute() {
    }

    public SchemaAttribute(String localName) {
        _localName = localName;
    }

    public String getNamespaceURI() {
        return _nsURI;
    }

    public void setNamespaceURI(String s) {
        _nsURI = s;
    }

    public String getLocalName() {
        return _localName;
    }

    public void setLocalName(String s) {
        _localName = s;
    }

    public QName getQName() {
        return new QName(_nsURI, _localName);
    }

    public String getValue() {
        if(_qnameValue != null) {
            if(_parent == null)
                throw new IllegalStateException();
            else
                return _parent.asString(_qnameValue);
        } else {
            return _value;
        }
    }

    public void setValue(String s) {
        _value = s;
    }

    public void setValue(QName name) {
        _qnameValue = name;
    }

    public SchemaElement getParent() {
        return _parent;
    }

    public void setParent(SchemaElement e) {
        _parent = e;
    }
}
