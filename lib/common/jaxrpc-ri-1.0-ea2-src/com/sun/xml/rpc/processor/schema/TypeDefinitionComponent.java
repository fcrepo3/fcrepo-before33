// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   TypeDefinitionComponent.java

package com.sun.xml.rpc.processor.schema;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            Component

public abstract class TypeDefinitionComponent extends Component {

    private QName _name;

    public TypeDefinitionComponent() {
    }

    public QName getName() {
        return _name;
    }

    public void setName(QName name) {
        _name = name;
    }

    public boolean isSimple() {
        return false;
    }

    public boolean isComplex() {
        return false;
    }
}
