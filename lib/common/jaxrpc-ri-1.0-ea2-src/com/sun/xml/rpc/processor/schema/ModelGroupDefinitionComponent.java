// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ModelGroupDefinitionComponent.java

package com.sun.xml.rpc.processor.schema;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            Component, ComponentVisitor, ModelGroupComponent, AnnotationComponent

public class ModelGroupDefinitionComponent extends Component {

    private QName _name;
    private ModelGroupComponent _modelGroup;
    private AnnotationComponent _annotation;

    public ModelGroupDefinitionComponent() {
    }

    public QName getName() {
        return _name;
    }

    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
