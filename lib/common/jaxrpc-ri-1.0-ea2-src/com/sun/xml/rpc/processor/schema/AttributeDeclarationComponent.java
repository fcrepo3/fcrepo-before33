// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AttributeDeclarationComponent.java

package com.sun.xml.rpc.processor.schema;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            Component, ComponentVisitor, SimpleTypeDefinitionComponent, ComplexTypeDefinitionComponent, 
//            Symbol, AnnotationComponent

public class AttributeDeclarationComponent extends Component {

    private QName _name;
    private SimpleTypeDefinitionComponent _typeDefinition;
    private ComplexTypeDefinitionComponent _scope;
    private String _value;
    private Symbol _valueKind;
    private AnnotationComponent _annotation;

    public AttributeDeclarationComponent() {
    }

    public QName getName() {
        return _name;
    }

    public void setName(QName name) {
        _name = name;
    }

    public SimpleTypeDefinitionComponent getTypeDefinition() {
        return _typeDefinition;
    }

    public void setTypeDefinition(SimpleTypeDefinitionComponent c) {
        _typeDefinition = c;
    }

    public ComplexTypeDefinitionComponent getScope() {
        return _scope;
    }

    public void setScope(ComplexTypeDefinitionComponent c) {
        _scope = c;
    }

    public void setValue(String s) {
        _value = s;
    }

    public void setValueKind(Symbol s) {
        _valueKind = s;
    }

    public AnnotationComponent getAnnotation() {
        return _annotation;
    }

    public void setAnnotation(AnnotationComponent c) {
        _annotation = c;
    }

    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
