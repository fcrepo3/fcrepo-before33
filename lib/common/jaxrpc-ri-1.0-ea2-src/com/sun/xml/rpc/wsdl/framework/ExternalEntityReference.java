// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ExternalEntityReference.java

package com.sun.xml.rpc.wsdl.framework;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            AbstractDocument, Kind, GloballyKnown

public class ExternalEntityReference {

    private AbstractDocument _document;
    private Kind _kind;
    private QName _name;

    public ExternalEntityReference(AbstractDocument document, Kind kind, QName name) {
        _document = document;
        _kind = kind;
        _name = name;
    }

    public AbstractDocument getDocument() {
        return _document;
    }

    public Kind getKind() {
        return _kind;
    }

    public QName getName() {
        return _name;
    }

    public GloballyKnown resolve() {
        return _document.find(_kind, _name);
    }
}
