// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SimpleTypeDefinitionComponent.java

package com.sun.xml.rpc.processor.schema;

import com.sun.xml.rpc.util.NullIterator;
import java.util.*;

// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            TypeDefinitionComponent, ComponentVisitor, AnnotationComponent, Facet, 
//            FundamentalFacet

public class SimpleTypeDefinitionComponent extends TypeDefinitionComponent {

    public static final int VARIETY_ATOMIC = 1;
    public static final int VARIETY_LIST = 2;
    public static final int VARIETY_UNION = 3;
    private SimpleTypeDefinitionComponent _baseTypeDefinition;
    private List _facets;
    private List _fundamentalFacets;
    private Set _final;
    private int _varietyTag;
    private SimpleTypeDefinitionComponent _primitiveTypeDefinition;
    private SimpleTypeDefinitionComponent _itemTypeDefinition;
    private List _memberTypeDefinitions;
    private AnnotationComponent _annotation;

    public SimpleTypeDefinitionComponent() {
    }

    public boolean isSimple() {
        return true;
    }

    public SimpleTypeDefinitionComponent getBaseTypeDefinition() {
        return _baseTypeDefinition;
    }

    public void setBaseTypeDefinition(SimpleTypeDefinitionComponent c) {
        _baseTypeDefinition = c;
    }

    public SimpleTypeDefinitionComponent getPrimitiveTypeDefinition() {
        return _primitiveTypeDefinition;
    }

    public void setPrimitiveTypeDefinition(SimpleTypeDefinitionComponent c) {
        _primitiveTypeDefinition = c;
    }

    public SimpleTypeDefinitionComponent getItemTypeDefinition() {
        return _itemTypeDefinition;
    }

    public void setItemTypeDefinition(SimpleTypeDefinitionComponent c) {
        _itemTypeDefinition = c;
    }

    public void setFinal(Set s) {
        _final = s;
    }

    public int getVarietyTag() {
        return _varietyTag;
    }

    public void setVarietyTag(int i) {
        _varietyTag = i;
    }

    public void addFacet(Facet f) {
        if(_facets == null)
            _facets = new ArrayList();
        _facets.add(f);
    }

    public Iterator facets() {
        return ((Iterator) (_facets != null ? _facets.iterator() : new NullIterator()));
    }

    public void addFundamentalFacet(FundamentalFacet f) {
        if(_fundamentalFacets == null)
            _fundamentalFacets = new ArrayList();
        _fundamentalFacets.add(f);
    }

    public Iterator fundamentalFacets() {
        return ((Iterator) (_fundamentalFacets != null ? _fundamentalFacets.iterator() : new NullIterator()));
    }

    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
