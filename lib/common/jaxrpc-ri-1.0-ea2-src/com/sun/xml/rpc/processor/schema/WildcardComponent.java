// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   WildcardComponent.java

package com.sun.xml.rpc.processor.schema;

import java.util.Set;

// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            Component, ComponentVisitor, Symbol, AnnotationComponent

public class WildcardComponent extends Component {

    public static final int NAMESPACE_CONSTRAINT_ANY = 1;
    public static final int NAMESPACE_CONSTRAINT_NOT = 2;
    public static final int NAMESPACE_CONSTRAINT_NOT_ABSENT = 3;
    public static final int NAMESPACE_CONSTRAINT_SET = 4;
    public static final int NAMESPACE_CONSTRAINT_SET_OR_ABSENT = 5;
    private Symbol _processContents;
    private int _namespaceConstraintTag;
    private String _namespaceName;
    private Set _namespaceSet;
    private AnnotationComponent _annotation;

    public WildcardComponent() {
    }

    public void setProcessContents(Symbol s) {
        _processContents = s;
    }

    public int getNamespaceConstraintTag() {
        return _namespaceConstraintTag;
    }

    public void setNamespaceConstraintTag(int i) {
        _namespaceConstraintTag = i;
    }

    public void setNamespaceName(String s) {
        _namespaceName = s;
    }

    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
