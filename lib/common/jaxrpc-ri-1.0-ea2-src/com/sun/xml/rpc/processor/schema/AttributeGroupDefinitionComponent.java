// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AttributeGroupDefinitionComponent.java

package com.sun.xml.rpc.processor.schema;

import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            Component, AttributeUseComponent, ComponentVisitor, WildcardComponent, 
//            AnnotationComponent

public class AttributeGroupDefinitionComponent extends Component {

    private QName _name;
    private List _attributeUses;
    private WildcardComponent _attributeWildcard;
    private AnnotationComponent _annotation;

    public AttributeGroupDefinitionComponent() {
        _attributeUses = new ArrayList();
    }

    public QName getName() {
        return _name;
    }

    public void setName(QName name) {
        _name = name;
    }

    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }

    public Iterator attributeUses() {
        return _attributeUses.iterator();
    }

    public void addAttributeUse(AttributeUseComponent c) {
        _attributeUses.add(c);
    }

    public void addAttributeGroup(AttributeGroupDefinitionComponent c) {
        AttributeUseComponent a;
        for(Iterator iter = c.attributeUses(); iter.hasNext(); addAttributeUse(a))
            a = (AttributeUseComponent)iter.next();

    }
}
