// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   NotationDeclarationComponent.java

package com.sun.xml.rpc.processor.schema;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            Component, ComponentVisitor, AnnotationComponent

public class NotationDeclarationComponent extends Component {

    private QName _name;
    private String _systemIdentifier;
    private String _publicIdentifier;
    private AnnotationComponent _annotation;

    public NotationDeclarationComponent() {
    }

    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
