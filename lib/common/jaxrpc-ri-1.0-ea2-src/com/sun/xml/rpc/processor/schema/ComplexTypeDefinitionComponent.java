// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ComplexTypeDefinitionComponent.java

package com.sun.xml.rpc.processor.schema;

import java.util.*;

// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            TypeDefinitionComponent, AttributeUseComponent, AttributeGroupDefinitionComponent, ComponentVisitor, 
//            Symbol, WildcardComponent, SimpleTypeDefinitionComponent, ParticleComponent

public class ComplexTypeDefinitionComponent extends TypeDefinitionComponent {

    public static final int CONTENT_EMPTY = 1;
    public static final int CONTENT_SIMPLE = 2;
    public static final int CONTENT_MIXED = 3;
    public static final int CONTENT_ELEMENT_ONLY = 4;
    private TypeDefinitionComponent _baseTypeDefinition;
    private Symbol _derivationMethod;
    private Set _final;
    private boolean _abstract;
    private List _attributeUses;
    private WildcardComponent _attributeWildcard;
    private int _contentTag;
    private SimpleTypeDefinitionComponent _simpleTypeContent;
    private ParticleComponent _particleContent;
    private Set _prohibitedSubstitutions;
    private List _annotations;

    public ComplexTypeDefinitionComponent() {
        _attributeUses = new ArrayList();
    }

    public boolean isComplex() {
        return true;
    }

    public TypeDefinitionComponent getBaseTypeDefinition() {
        return _baseTypeDefinition;
    }

    public void setBaseTypeDefinition(TypeDefinitionComponent c) {
        _baseTypeDefinition = c;
    }

    public void setDerivationMethod(Symbol s) {
        _derivationMethod = s;
    }

    public void setProhibitedSubstitutions(Set s) {
        _prohibitedSubstitutions = s;
    }

    public void setFinal(Set s) {
        _final = s;
    }

    public boolean isAbstract() {
        return _abstract;
    }

    public void setAbstract(boolean b) {
        _abstract = b;
    }

    public Iterator attributeUses() {
        return _attributeUses.iterator();
    }

    public boolean hasNoAttributeUses() {
        return _attributeUses.size() == 0;
    }

    public void addAttributeUse(AttributeUseComponent c) {
        _attributeUses.add(c);
    }

    public void addAttributeGroup(AttributeGroupDefinitionComponent c) {
        AttributeUseComponent a;
        for(Iterator iter = c.attributeUses(); iter.hasNext(); addAttributeUse(a))
            a = (AttributeUseComponent)iter.next();

    }

    public int getContentTag() {
        return _contentTag;
    }

    public void setContentTag(int i) {
        _contentTag = i;
    }

    public SimpleTypeDefinitionComponent getSimpleTypeContent() {
        return _simpleTypeContent;
    }

    public void setSimpleTypeContent(SimpleTypeDefinitionComponent c) {
        _simpleTypeContent = c;
    }

    public ParticleComponent getParticleContent() {
        return _particleContent;
    }

    public void setParticleContent(ParticleComponent c) {
        _particleContent = c;
    }

    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
