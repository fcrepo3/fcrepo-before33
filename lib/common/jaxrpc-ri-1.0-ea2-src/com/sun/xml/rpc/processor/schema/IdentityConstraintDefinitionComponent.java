// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   IdentityConstraintDefinitionComponent.java

package com.sun.xml.rpc.processor.schema;

import java.util.List;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            Component, ComponentVisitor, Symbol, AnnotationComponent

public class IdentityConstraintDefinitionComponent extends Component {

    private QName _name;
    private Symbol _identityConstraintCategory;
    private String _selector;
    private List _fields;
    private IdentityConstraintDefinitionComponent _referencedKey;
    private AnnotationComponent _annotation;

    public IdentityConstraintDefinitionComponent() {
    }

    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
