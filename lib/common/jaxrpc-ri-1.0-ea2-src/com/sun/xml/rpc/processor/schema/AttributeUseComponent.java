// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AttributeUseComponent.java

package com.sun.xml.rpc.processor.schema;


// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            Component, ComponentVisitor, AttributeDeclarationComponent, Symbol, 
//            AnnotationComponent

public class AttributeUseComponent extends Component {

    private boolean _required;
    private AttributeDeclarationComponent _attributeDeclaration;
    private String _value;
    private Symbol _valueKind;
    private AnnotationComponent _annotation;

    public AttributeUseComponent() {
    }

    public boolean isRequired() {
        return _required;
    }

    public void setRequired(boolean b) {
        _required = b;
    }

    public AttributeDeclarationComponent getAttributeDeclaration() {
        return _attributeDeclaration;
    }

    public void setAttributeDeclaration(AttributeDeclarationComponent c) {
        _attributeDeclaration = c;
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
