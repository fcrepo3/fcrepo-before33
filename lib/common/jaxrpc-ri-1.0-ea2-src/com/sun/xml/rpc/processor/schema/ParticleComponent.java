// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ParticleComponent.java

package com.sun.xml.rpc.processor.schema;


// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            Component, ComponentVisitor, ModelGroupComponent, WildcardComponent, 
//            ElementDeclarationComponent

public class ParticleComponent extends Component {

    public static final int TERM_MODEL_GROUP = 1;
    public static final int TERM_WILDCARD = 2;
    public static final int TERM_ELEMENT = 3;
    private int _minOccurs;
    private int _maxOccurs;
    private int _termTag;
    private ModelGroupComponent _modelGroupTerm;
    private WildcardComponent _wildcardTerm;
    private ElementDeclarationComponent _elementTerm;
    private static final int UNBOUNDED = -1;

    public ParticleComponent() {
    }

    public int getMinOccurs() {
        return _minOccurs;
    }

    public void setMinOccurs(int i) {
        _minOccurs = i;
    }

    public int getMaxOccurs() {
        if(_maxOccurs == -1)
            throw new IllegalStateException();
        else
            return _maxOccurs;
    }

    public void setMaxOccurs(int i) {
        _maxOccurs = i;
    }

    public boolean isMaxOccursUnbounded() {
        return _maxOccurs == -1;
    }

    public void setMaxOccursUnbounded() {
        _maxOccurs = -1;
    }

    public boolean doesNotOccur() {
        return _minOccurs == 0 && _maxOccurs == 0;
    }

    public boolean occursOnce() {
        return _minOccurs == 1 && _maxOccurs == 1;
    }

    public boolean occursAtMostOnce() {
        return _minOccurs <= 1 && _maxOccurs == 1;
    }

    public boolean occursAtLeastOnce() {
        return _minOccurs >= 1;
    }

    public boolean occursZeroOrMore() {
        return _minOccurs == 0 && _maxOccurs == -1;
    }

    public boolean occursOnceOrMore() {
        return _minOccurs == 1 && _maxOccurs == -1;
    }

    public boolean mayOccurMoreThanOnce() {
        return _maxOccurs > 1 || _maxOccurs == -1;
    }

    public int getTermTag() {
        return _termTag;
    }

    public void setTermTag(int i) {
        _termTag = i;
    }

    public ModelGroupComponent getModelGroupTerm() {
        return _modelGroupTerm;
    }

    public void setModelGroupTerm(ModelGroupComponent c) {
        _modelGroupTerm = c;
    }

    public ElementDeclarationComponent getElementTerm() {
        return _elementTerm;
    }

    public void setElementTerm(ElementDeclarationComponent c) {
        _elementTerm = c;
    }

    public WildcardComponent getWildcardTerm() {
        return _wildcardTerm;
    }

    public void setWildcardTerm(WildcardComponent c) {
        _wildcardTerm = c;
    }

    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
