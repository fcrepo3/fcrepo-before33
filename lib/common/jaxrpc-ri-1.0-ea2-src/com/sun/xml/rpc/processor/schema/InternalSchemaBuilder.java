// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   InternalSchemaBuilder.java

package com.sun.xml.rpc.processor.schema;

import com.sun.xml.rpc.processor.model.ModelException;
import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.document.schema.*;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;
import com.sun.xml.rpc.wsdl.framework.AbstractDocument;
import com.sun.xml.rpc.wsdl.framework.ValidationException;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            InternalSchema, TypeDefinitionComponent, AttributeDeclarationComponent, ElementDeclarationComponent,
//            AttributeGroupDefinitionComponent, SimpleTypeDefinitionComponent, ComplexTypeDefinitionComponent, EnumerationFacet,
//            AttributeUseComponent, ParticleComponent, ModelGroupComponent, WildcardComponent,
//            AnnotationComponent, UnimplementedFeatureException, Symbol, ModelGroupDefinitionComponent

public class InternalSchemaBuilder {

    private AbstractDocument _document;
    private InternalSchema _schema;
    private Map _wellKnownTypes;
    private Map _wellKnownAttributes;
    private Map _wellKnownAttributeGroups;
    private Map _wellKnownElements;
    private ComplexTypeDefinitionComponent _urType;
    private SimpleTypeDefinitionComponent _simpleUrType;
    private Map _namedTypeComponentsBeingDefined;
    private static final Set _primitiveTypeNames;
    private static final Set _soapTypeNames;
    private static final Set _setEmpty = new HashSet();
    private static final Set _setExtRes;
    private static final Set _setExtResSub;
    private static final Set _setExtResListUnion;
    private static final Set _setLaxSkipStrict;

    public InternalSchemaBuilder(AbstractDocument document, Properties options) {
        _document = document;
        _schema = new InternalSchema(this);
        createWellKnownTypes();
        createWellKnownAttributes();
        createWellKnownAttributeGroups();
        createWellKnownElements();
    }

    public InternalSchema getSchema() {
        return _schema;
    }

    public TypeDefinitionComponent buildTypeDefinition(QName name) {
        boolean createdTypeComponentMap = false;
        if(_namedTypeComponentsBeingDefined == null) {
            _namedTypeComponentsBeingDefined = new HashMap();
            createdTypeComponentMap = true;
        }
        try {
            TypeDefinitionComponent component = (TypeDefinitionComponent)_wellKnownTypes.get(name);
            if(component != null) {
                TypeDefinitionComponent typedefinitioncomponent = component;
                return typedefinitioncomponent;
            }
            SchemaEntity entity = (SchemaEntity)_document.find(SchemaKinds.XSD_TYPE, name);
            SchemaElement element = entity.getElement();
            component = buildTopLevelTypeDefinition(element, _schema);
            _schema.add(component);
            TypeDefinitionComponent typedefinitioncomponent1 = component;
            return typedefinitioncomponent1;
        }
        catch(ValidationException e) {
            throw new ModelException(e);
        }
        finally {
            if(createdTypeComponentMap)
                _namedTypeComponentsBeingDefined = null;
        }
    }

    public AttributeDeclarationComponent buildAttributeDeclaration(QName name) {
        try {
            AttributeDeclarationComponent component = (AttributeDeclarationComponent)_wellKnownAttributes.get(name);
            if(component != null) {
                return component;
            } else {
                SchemaEntity entity = (SchemaEntity)_document.find(SchemaKinds.XSD_ATTRIBUTE, name);
                SchemaElement element = entity.getElement();
                component = buildTopLevelAttributeDeclaration(element, _schema);
                _schema.add(component);
                return component;
            }
        }
        catch(ValidationException e) {
            throw new ModelException(e);
        }
    }

    public ElementDeclarationComponent buildElementDeclaration(QName name) {
        try {
            ElementDeclarationComponent component = (ElementDeclarationComponent)_wellKnownElements.get(name);
            if(component != null) {
                return component;
            } else {
                SchemaEntity entity = (SchemaEntity)_document.find(SchemaKinds.XSD_ELEMENT, name);
                SchemaElement element = entity.getElement();
                component = buildTopLevelElementDeclaration(element, _schema);
                _schema.add(component);
                return component;
            }
        }
        catch(ValidationException e) {
            throw new ModelException(e);
        }
    }

    public AttributeGroupDefinitionComponent buildAttributeGroupDefinition(QName name) {
        try {
            AttributeGroupDefinitionComponent component = (AttributeGroupDefinitionComponent)_wellKnownAttributeGroups.get(name);
            if(component != null) {
                return component;
            } else {
                SchemaEntity entity = (SchemaEntity)_document.find(SchemaKinds.XSD_ATTRIBUTE_GROUP, name);
                SchemaElement element = entity.getElement();
                component = buildTopLevelAttributeGroupDefinition(element, _schema);
                _schema.add(component);
                return component;
            }
        }
        catch(ValidationException e) {
            throw new ModelException(e);
        }
    }

    public ModelGroupDefinitionComponent buildModelGroupDefinition(QName name) {
        failUnimplemented("F002");
        return null;
    }

    public ComplexTypeDefinitionComponent getUrType() {
        return _urType;
    }

    public SimpleTypeDefinitionComponent getSimpleUrType() {
        return _simpleUrType;
    }

    protected ElementDeclarationComponent buildTopLevelElementDeclaration(SchemaElement element, InternalSchema schema) {
        ElementDeclarationComponent component = internalBuildElementDeclaration(element, schema);
        String nameAttr = element.getValueOfMandatoryAttribute("name");
        component.setName(new QName(element.getSchema().getTargetNamespaceURI(), nameAttr));
        if(element.getValueOfAttributeOrNull("minOccurs") != null)
            failValidation("validation.invalidAttribute", "minOccurs", element.getLocalName());
        if(element.getValueOfAttributeOrNull("maxOccurs") != null)
            failValidation("validation.invalidAttribute", "maxOccurs", element.getLocalName());
        return component;
    }

    protected AttributeGroupDefinitionComponent buildTopLevelAttributeGroupDefinition(SchemaElement element, InternalSchema schema) {
        AttributeGroupDefinitionComponent component = new AttributeGroupDefinitionComponent();
        String nameAttr = element.getValueOfMandatoryAttribute("name");
        component.setName(new QName(element.getSchema().getTargetNamespaceURI(), nameAttr));
        for(Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement)iter.next();
            if(child.getQName().equals(SchemaConstants.QNAME_ATTRIBUTE))
                component.addAttributeUse(buildAttributeUse(child, null, schema));
            else
            if(child.getQName().equals(SchemaConstants.QNAME_ATTRIBUTE_GROUP)) {
                String refAttr = child.getValueOfMandatoryAttribute("ref");
                component.addAttributeGroup(schema.findAttributeGroupDefinition(child.asQName(refAttr)));
            } else
            if(child.getQName().equals(SchemaConstants.QNAME_ANY_ATTRIBUTE))
                failUnimplemented("F003");
            else
            if(!child.getQName().equals(SchemaConstants.QNAME_ANNOTATION))
                failValidation("validation.invalidElement", child.getLocalName());
        }

        return component;
    }

    protected AttributeDeclarationComponent buildTopLevelAttributeDeclaration(SchemaElement element, InternalSchema schema) {
        AttributeDeclarationComponent component = new AttributeDeclarationComponent();
        String nameAttr = element.getValueOfMandatoryAttribute("name");
        String formAttr = element.getValueOfAttributeOrNull("form");
        if(formAttr == null) {
            formAttr = element.getRoot().getValueOfAttributeOrNull("attributeFormDefault");
            if(formAttr == null)
                formAttr = "";
        }
        if(formAttr.equals("qualified"))
            component.setName(new QName(element.getSchema().getTargetNamespaceURI(), nameAttr));
        else
            component.setName(new QName(nameAttr));
        component.setScope(null);
        boolean foundType = false;
        for(Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement)iter.next();
            if(child.getQName().equals(SchemaConstants.QNAME_SIMPLE_TYPE)) {
                if(foundType)
                    failValidation("validation.invalidElement", element.getLocalName());
                component.setTypeDefinition(buildSimpleTypeDefinition(child, schema));
                foundType = true;
            } else
            if(!child.getQName().equals(SchemaConstants.QNAME_ANNOTATION))
                failValidation("validation.invalidElement", child.getLocalName());
        }

        if(foundType) {
            assertNoAttribute(element, "type");
        } else {
            String typeAttr = element.getValueOfAttributeOrNull("type");
            if(typeAttr == null) {
                component.setTypeDefinition(getSimpleUrType());
            } else {
                TypeDefinitionComponent typeComponent = schema.findTypeDefinition(element.asQName(typeAttr));
                if(typeComponent instanceof SimpleTypeDefinitionComponent)
                    component.setTypeDefinition((SimpleTypeDefinitionComponent)typeComponent);
                else
                    failValidation("validation.notSimpleType", component.getName().getLocalPart());
            }
        }
        component.setAnnotation(buildNonSchemaAttributesAnnotation(element));
        String defaultAttr = element.getValueOfAttributeOrNull("default");
        String fixedAttr = element.getValueOfAttributeOrNull("fixed");
        if(defaultAttr != null && fixedAttr != null)
            fail("validation.exclusiveAttributes", "default", "fixed");
        if(defaultAttr != null) {
            component.setValue(defaultAttr);
            component.setValueKind(Symbol.DEFAULT);
        }
        if(fixedAttr != null) {
            component.setValue(defaultAttr);
            component.setValueKind(Symbol.FIXED);
        }
        return component;
    }

    protected void processElementParticle(SchemaElement element, ParticleComponent component, ComplexTypeDefinitionComponent scope, InternalSchema schema) {
        component.setTermTag(3);
        ElementDeclarationComponent term = internalBuildElementDeclaration(element, schema);
        String refAttr = element.getValueOfAttributeOrNull("ref");
        if(refAttr != null)
            failUnimplemented("F004");
        String nameAttr = element.getValueOfMandatoryAttribute("name");
        String formAttr = element.getValueOfAttributeOrNull("form");
        if(formAttr == null) {
            formAttr = element.getRoot().getValueOfAttributeOrNull("elementFormDefault");
            if(formAttr == null)
                formAttr = "";
        }
        if(formAttr.equals("qualified"))
            term.setName(new QName(element.getSchema().getTargetNamespaceURI(), nameAttr));
        else
            term.setName(new QName(nameAttr));
        term.setScope(scope);
        component.setTermTag(3);
        component.setElementTerm(term);
    }

    protected ElementDeclarationComponent internalBuildElementDeclaration(SchemaElement element, InternalSchema schema) {
        ElementDeclarationComponent component = new ElementDeclarationComponent();
        boolean foundType = false;
        for(Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement)iter.next();
            if(child.getQName().equals(SchemaConstants.QNAME_SIMPLE_TYPE)) {
                if(foundType)
                    failValidation("validation.invalidElement", element.getLocalName());
                component.setTypeDefinition(buildSimpleTypeDefinition(child, schema));
                foundType = true;
            } else
            if(child.getQName().equals(SchemaConstants.QNAME_COMPLEX_TYPE)) {
                if(foundType)
                    failValidation("validation.invalidElement", element.getLocalName());
                component.setTypeDefinition(buildComplexTypeDefinition(child, schema));
                foundType = true;
            }
        }

        if(foundType) {
            assertNoAttribute(element, "type");
            assertNoAttribute(element, "substitutionGroup");
        } else {
            String typeAttr = element.getValueOfAttributeOrNull("type");
            String substitutionGroupAttr = element.getValueOfAttributeOrNull("substitutionGroup");
            if(typeAttr == null && substitutionGroupAttr == null)
                component.setTypeDefinition(getUrType());
            else
            if(typeAttr != null && substitutionGroupAttr != null)
                failValidation("validation.exclusiveAttributes", "type", "substitutionGroup");
            else
            if(typeAttr != null)
                component.setTypeDefinition(schema.findTypeDefinition(element.asQName(typeAttr)));
            else
                failUnimplemented("F005");
        }
        component.setNillable(element.getValueOfBooleanAttributeOrDefault("nillable", false));
        String defaultAttr = element.getValueOfAttributeOrNull("default");
        String fixedAttr = element.getValueOfAttributeOrNull("fixed");
        if(defaultAttr != null && fixedAttr != null)
            fail("validation.exclusiveAttributes", "default", "fixed");
        if(defaultAttr != null) {
            component.setValue(defaultAttr);
            component.setValueKind(Symbol.DEFAULT);
            if(component.getTypeDefinition() instanceof ComplexTypeDefinitionComponent)
                failValidation("validation.notSimpleType", component.getName().getLocalPart());
        }
        if(fixedAttr != null) {
            component.setValue(defaultAttr);
            component.setValueKind(Symbol.FIXED);
            if(component.getTypeDefinition() instanceof ComplexTypeDefinitionComponent)
                failValidation("validation.notSimpleType", component.getName().getLocalPart());
        }
        for(Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement)iter.next();
            if(child.getQName().equals(SchemaConstants.QNAME_KEY) || child.getQName().equals(SchemaConstants.QNAME_KEYREF) || child.getQName().equals(SchemaConstants.QNAME_UNIQUE))
                failUnimplemented("F006");
        }

        QName substitutionGroupAttr = element.getValueOfQNameAttributeOrNull("substitutionGroup");
        if(substitutionGroupAttr != null)
            failUnimplemented("F007");
        String blockAttr = element.getValueOfAttributeOrNull("block");
        if(blockAttr == null) {
            blockAttr = element.getRoot().getValueOfAttributeOrNull("blockDefault");
            if(blockAttr == null)
                blockAttr = "";
        }
        if(blockAttr.equals(""))
            component.setDisallowedSubstitutions(_setEmpty);
        else
        if(blockAttr.equals("#all")) {
            component.setDisallowedSubstitutions(_setExtResSub);
        } else {
            component.setDisallowedSubstitutions(parseSymbolSet(blockAttr, _setExtResSub));
            failUnimplemented("F008");
        }
        String finalAttr = element.getValueOfAttributeOrNull("final");
        if(finalAttr == null) {
            finalAttr = element.getRoot().getValueOfAttributeOrNull("finalDefault");
            if(finalAttr == null)
                finalAttr = "";
        }
        if(finalAttr.equals(""))
            component.setSubstitutionsGroupExclusions(_setEmpty);
        else
        if(finalAttr.equals("#all")) {
            component.setSubstitutionsGroupExclusions(_setExtRes);
        } else {
            component.setSubstitutionsGroupExclusions(parseSymbolSet(finalAttr, _setExtRes));
            failUnimplemented("F009");
        }
        component.setAbstract(element.getValueOfBooleanAttributeOrDefault("abstract", false));
        return component;
    }

    protected TypeDefinitionComponent buildTopLevelTypeDefinition(SchemaElement element, InternalSchema schema) {
        TypeDefinitionComponent component = null;
        if(element.getQName().equals(SchemaConstants.QNAME_SIMPLE_TYPE))
            component = buildSimpleTypeDefinition(element, schema);
        else
        if(element.getQName().equals(SchemaConstants.QNAME_COMPLEX_TYPE))
            component = buildComplexTypeDefinition(element, schema);
        else
            failValidation("validation.invalidElement", element.getLocalName());
        return component;
    }

    protected SimpleTypeDefinitionComponent buildSimpleTypeDefinition(SchemaElement element, InternalSchema schema) {
        SimpleTypeDefinitionComponent component = new SimpleTypeDefinitionComponent();
        String nameAttr = element.getValueOfAttributeOrNull("name");
        if(nameAttr != null) {
            component.setName(new QName(element.getSchema().getTargetNamespaceURI(), nameAttr));
            _namedTypeComponentsBeingDefined.put(component.getName(), component);
        }
        boolean gotOne = false;
        for(Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement)iter.next();
            if(gotOne)
                failValidation("validation.invalidElement", child.getLocalName());
            if(child.getQName().equals(SchemaConstants.QNAME_RESTRICTION)) {
                buildRestrictionSimpleTypeDefinition(component, child, schema);
                gotOne = true;
            } else
            if(child.getQName().equals(SchemaConstants.QNAME_LIST)) {
                buildListSimpleTypeDefinition(component, child, schema);
                gotOne = true;
            } else
            if(child.getQName().equals(SchemaConstants.QNAME_UNION))
                failUnimplemented("F011");
            else
                failValidation("validation.invalidElement", child.getLocalName());
        }

        return component;
    }

    protected void buildRestrictionSimpleTypeDefinition(SimpleTypeDefinitionComponent component, SchemaElement element, InternalSchema schema) {
        String baseAttr = element.getValueOfAttributeOrNull("base");
        if(baseAttr != null) {
            TypeDefinitionComponent base = schema.findTypeDefinition(element.asQName(baseAttr));
            if(base.isSimple())
                component.setBaseTypeDefinition((SimpleTypeDefinitionComponent)base);
            else
                failValidation("validation.notSimpleType", base.getName().getLocalPart());
        } else {
            failUnimplemented("F012");
        }
        component.setVarietyTag(component.getBaseTypeDefinition().getVarietyTag());
        component.setPrimitiveTypeDefinition(component.getBaseTypeDefinition().getPrimitiveTypeDefinition());
        String finalAttr = element.getValueOfAttributeOrNull("final");
        if(finalAttr == null) {
            finalAttr = element.getRoot().getValueOfAttributeOrNull("finalDefault");
            if(finalAttr == null)
                finalAttr = "";
        }
        if(finalAttr.equals(""))
            component.setFinal(_setEmpty);
        else
        if(finalAttr.equals("#all")) {
            component.setFinal(_setExtResListUnion);
        } else {
            component.setFinal(parseSymbolSet(finalAttr, _setExtResListUnion));
            failUnimplemented("F013");
        }
        boolean gotOne = false;
        EnumerationFacet enumeration = new EnumerationFacet();
        for(Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement)iter.next();
            gotOne = true;
            if(child.getQName().equals(SchemaConstants.QNAME_ENUMERATION)) {
                String valueAttr = child.getValueOfAttributeOrNull("value");
                if(valueAttr == null)
                    failValidation("validation.missingRequiredAttribute", "value", child.getQName().getLocalPart());
                enumeration.addValue(valueAttr);
            } else {
                failUnimplemented("F014");
            }
        }

        component.addFacet(enumeration);
    }

    protected void buildListSimpleTypeDefinition(SimpleTypeDefinitionComponent component, SchemaElement element, InternalSchema schema) {
        component.setBaseTypeDefinition(getSimpleUrType());
        String itemTypeAttr = element.getValueOfAttributeOrNull("itemType");
        if(itemTypeAttr != null) {
            TypeDefinitionComponent itemType = schema.findTypeDefinition(element.asQName(itemTypeAttr));
            if(itemType.isSimple())
                component.setItemTypeDefinition((SimpleTypeDefinitionComponent)itemType);
            else
                failValidation("validation.notSimpleType", itemType.getName().getLocalPart());
        } else {
            SchemaElement simpleTypeElement = getOnlyChildIgnoring(element, SchemaConstants.QNAME_ANNOTATION);
            if(!simpleTypeElement.getQName().equals(SchemaConstants.QNAME_SIMPLE_TYPE))
                failValidation("validation.invalidElement", simpleTypeElement.getLocalName());
            TypeDefinitionComponent itemType = buildSimpleTypeDefinition(simpleTypeElement, schema);
            component.setItemTypeDefinition((SimpleTypeDefinitionComponent)itemType);
        }
        component.setVarietyTag(2);
        String finalAttr = element.getValueOfAttributeOrNull("final");
        if(finalAttr == null) {
            finalAttr = element.getRoot().getValueOfAttributeOrNull("finalDefault");
            if(finalAttr == null)
                finalAttr = "";
        }
        if(finalAttr.equals(""))
            component.setFinal(_setEmpty);
        else
        if(finalAttr.equals("#all")) {
            component.setFinal(_setExtResListUnion);
        } else {
            component.setFinal(parseSymbolSet(finalAttr, _setExtResListUnion));
            failUnimplemented("F013");
        }
    }

    protected ComplexTypeDefinitionComponent buildComplexTypeDefinition(SchemaElement element, InternalSchema schema) {
        for(Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement)iter.next();
            if(child.getQName().equals(SchemaConstants.QNAME_SIMPLE_CONTENT))
                return buildSimpleContentComplexTypeDefinition(element, schema);
            if(child.getQName().equals(SchemaConstants.QNAME_COMPLEX_CONTENT)) {
                boolean mixedContent = element.getValueOfBooleanAttributeOrDefault("mixed", false);
                return buildExplicitComplexContentComplexTypeDefinition(element, mixedContent, schema);
            }
        }

        boolean mixedContent = element.getValueOfBooleanAttributeOrDefault("mixed", false);
        return buildImplicitComplexContentComplexTypeDefinition(element, mixedContent, schema);
    }

    protected ComplexTypeDefinitionComponent commonBuildComplexTypeDefinition(SchemaElement element, InternalSchema schema) {
        ComplexTypeDefinitionComponent component = new ComplexTypeDefinitionComponent();
        String nameAttr = element.getValueOfAttributeOrNull("name");
        if(nameAttr != null) {
            component.setName(new QName(element.getSchema().getTargetNamespaceURI(), nameAttr));
            _namedTypeComponentsBeingDefined.put(component.getName(), component);
        }
        component.setAbstract(element.getValueOfBooleanAttributeOrDefault("abstract", false));
        String blockAttr = element.getValueOfAttributeOrNull("block");
        if(blockAttr == null) {
            blockAttr = element.getRoot().getValueOfAttributeOrNull("blockDefault");
            if(blockAttr == null)
                blockAttr = "";
        }
        if(blockAttr.equals(""))
            component.setProhibitedSubstitutions(_setEmpty);
        else
        if(blockAttr.equals("#all")) {
            component.setProhibitedSubstitutions(_setExtRes);
        } else {
            component.setProhibitedSubstitutions(parseSymbolSet(blockAttr, _setExtRes));
            failUnimplemented("F015");
        }
        String finalAttr = element.getValueOfAttributeOrNull("final");
        if(finalAttr == null) {
            finalAttr = element.getRoot().getValueOfAttributeOrNull("finalDefault");
            if(finalAttr == null)
                finalAttr = "";
        }
        if(finalAttr.equals(""))
            component.setFinal(_setEmpty);
        else
        if(finalAttr.equals("#all")) {
            component.setFinal(_setExtRes);
        } else {
            component.setFinal(parseSymbolSet(finalAttr, _setExtRes));
            failUnimplemented("F016");
        }
        return component;
    }

    protected ComplexTypeDefinitionComponent buildSimpleContentComplexTypeDefinition(SchemaElement element, InternalSchema schema) {
        ComplexTypeDefinitionComponent component = commonBuildComplexTypeDefinition(element, schema);
        SchemaElement simpleContentElement = getOnlyChildIgnoring(element, SchemaConstants.QNAME_ANNOTATION);
        if(!simpleContentElement.getQName().equals(SchemaConstants.QNAME_SIMPLE_CONTENT))
            failValidation("validation.invalidElement", simpleContentElement.getLocalName());
        SchemaElement derivationElement = getOnlyChildIgnoring(simpleContentElement, SchemaConstants.QNAME_ANNOTATION);
        boolean isRestriction = true;
        if(!derivationElement.getQName().equals(SchemaConstants.QNAME_RESTRICTION))
            if(derivationElement.getQName().equals(SchemaConstants.QNAME_EXTENSION))
                isRestriction = false;
            else
                failValidation("validation.invalidElement", derivationElement.getLocalName());
        String baseAttr = derivationElement.getValueOfAttributeOrNull("base");
        if(baseAttr == null)
            component.setBaseTypeDefinition(getUrType());
        else
            component.setBaseTypeDefinition(schema.findTypeDefinition(element.asQName(baseAttr)));
        component.setDerivationMethod(isRestriction ? Symbol.RESTRICTION : Symbol.EXTENSION);
        failUnimplemented("F017");
        return component;
    }

    protected ComplexTypeDefinitionComponent buildExplicitComplexContentComplexTypeDefinition(SchemaElement element, boolean mixedContent, InternalSchema schema) {
        ComplexTypeDefinitionComponent component = commonBuildComplexTypeDefinition(element, schema);
        SchemaElement complexContentElement = getOnlyChildIgnoring(element, SchemaConstants.QNAME_ANNOTATION);
        if(!complexContentElement.getQName().equals(SchemaConstants.QNAME_COMPLEX_CONTENT))
            failValidation("validation.invalidElement", complexContentElement.getLocalName());
        boolean mixed = complexContentElement.getValueOfBooleanAttributeOrDefault("mixed", mixedContent);
        SchemaElement derivationElement = getOnlyChildIgnoring(complexContentElement, SchemaConstants.QNAME_ANNOTATION);
        boolean isRestriction = true;
        if(!derivationElement.getQName().equals(SchemaConstants.QNAME_RESTRICTION))
            if(derivationElement.getQName().equals(SchemaConstants.QNAME_EXTENSION))
                isRestriction = false;
            else
                failValidation("validation.invalidElement", derivationElement.getLocalName());
        if(isRestriction) {
            String baseAttr = derivationElement.getValueOfMandatoryAttribute("base");
            TypeDefinitionComponent baseType = schema.findTypeDefinition(derivationElement.asQName(baseAttr));
            component.setBaseTypeDefinition(baseType);
            if(mixed)
                component.setContentTag(3);
            else
                component.setContentTag(4);
            processRestrictionComplexTypeDefinition(derivationElement, component, schema);
        } else {
            failUnimplemented("F018");
        }
        return component;
    }

    protected ComplexTypeDefinitionComponent buildImplicitComplexContentComplexTypeDefinition(SchemaElement element, boolean mixedContent, InternalSchema schema) {
        ComplexTypeDefinitionComponent component = commonBuildComplexTypeDefinition(element, schema);
        component.setBaseTypeDefinition(getUrType());
        if(mixedContent)
            component.setContentTag(3);
        else
            component.setContentTag(4);
        processRestrictionComplexTypeDefinition(element, component, schema);
        return component;
    }

    protected void processRestrictionComplexTypeDefinition(SchemaElement element, ComplexTypeDefinitionComponent component, InternalSchema schema) {
        boolean gotContent = false;
        for(Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement)iter.next();
            if(child.getQName().equals(SchemaConstants.QNAME_ATTRIBUTE)) {
                AttributeUseComponent attribute = buildAttributeUse(child, component, schema);
                if(attribute == null)
                    failUnimplemented("F019");
                else
                    component.addAttributeUse(attribute);
            } else
            if(child.getQName().equals(SchemaConstants.QNAME_ATTRIBUTE_GROUP)) {
                String refAttr = child.getValueOfMandatoryAttribute("ref");
                component.addAttributeGroup(schema.findAttributeGroupDefinition(child.asQName(refAttr)));
            } else
            if(child.getQName().equals(SchemaConstants.QNAME_ANY_ATTRIBUTE))
                failUnimplemented("F020");
            else
            if(!child.getQName().equals(SchemaConstants.QNAME_ANNOTATION)) {
                if(gotContent)
                    failValidation("validation.invalidElement", child.getLocalName());
                gotContent = true;
                if(child.getQName().equals(SchemaConstants.QNAME_GROUP))
                    failUnimplemented("F021");
                else
                    component.setParticleContent(buildParticle(child, component, schema));
            }
        }

        if(!gotContent)
            component.setContentTag(1);
    }

    protected AttributeUseComponent buildAttributeUse(SchemaElement element, ComplexTypeDefinitionComponent scope, InternalSchema schema) {
        AttributeUseComponent component = new AttributeUseComponent();
        String useAttr = element.getValueOfAttributeOrNull("use");
        if(useAttr != null)
            if(useAttr.equals("required"))
                component.setRequired(true);
            else
            if(useAttr.equals("prohibited"))
                return null;
        String refAttr = element.getValueOfAttributeOrNull("ref");
        if(refAttr != null) {
            assertNoAttribute(element, "name");
            assertNoAttribute(element, "type");
            component.setAttributeDeclaration(schema.findAttributeDeclaration(element.asQName(refAttr)));
            component.setAnnotation(buildNonSchemaAttributesAnnotation(element));
        } else {
            AttributeDeclarationComponent declaration = new AttributeDeclarationComponent();
            String nameAttr = element.getValueOfMandatoryAttribute("name");
            String formAttr = element.getValueOfAttributeOrNull("form");
            if(formAttr == null) {
                formAttr = element.getRoot().getValueOfAttributeOrNull("attributeFormDefault");
                if(formAttr == null)
                    formAttr = "";
            }
            if(formAttr.equals("qualified"))
                declaration.setName(new QName(element.getSchema().getTargetNamespaceURI(), nameAttr));
            else
                declaration.setName(new QName(nameAttr));
            declaration.setScope(scope);
            boolean foundType = false;
            for(Iterator iter = element.children(); iter.hasNext();) {
                SchemaElement child = (SchemaElement)iter.next();
                if(child.getQName().equals(SchemaConstants.QNAME_SIMPLE_TYPE)) {
                    if(foundType)
                        failValidation("validation.invalidElement", element.getLocalName());
                    declaration.setTypeDefinition(buildSimpleTypeDefinition(child, schema));
                    foundType = true;
                } else
                if(!child.getQName().equals(SchemaConstants.QNAME_ANNOTATION))
                    failValidation("validation.invalidElement", child.getLocalName());
            }

            if(foundType) {
                assertNoAttribute(element, "type");
            } else {
                String typeAttr = element.getValueOfAttributeOrNull("type");
                if(typeAttr == null) {
                    declaration.setTypeDefinition(getSimpleUrType());
                } else {
                    TypeDefinitionComponent typeComponent = schema.findTypeDefinition(element.asQName(typeAttr));
                    if(typeComponent instanceof SimpleTypeDefinitionComponent)
                        declaration.setTypeDefinition((SimpleTypeDefinitionComponent)typeComponent);
                    else
                        failValidation("validation.notSimpleType", declaration.getName().getLocalPart());
                }
            }
            declaration.setAnnotation(buildNonSchemaAttributesAnnotation(element));
            component.setAttributeDeclaration(declaration);
        }
        String defaultAttr = element.getValueOfAttributeOrNull("default");
        String fixedAttr = element.getValueOfAttributeOrNull("fixed");
        if(defaultAttr != null && fixedAttr != null)
            fail("validation.exclusiveAttributes", "default", "fixed");
        if(defaultAttr != null) {
            component.setValue(defaultAttr);
            component.setValueKind(Symbol.DEFAULT);
        }
        if(fixedAttr != null) {
            component.setValue(defaultAttr);
            component.setValueKind(Symbol.FIXED);
        }
        return component;
    }

    protected ParticleComponent buildParticle(SchemaElement element, ComplexTypeDefinitionComponent scope, InternalSchema schema) {
        ParticleComponent component = new ParticleComponent();
        int minOccurs = element.getValueOfIntegerAttributeOrDefault("minOccurs", 1);
        component.setMinOccurs(minOccurs);
        String maxOccursAttr = element.getValueOfAttributeOrNull("maxOccurs");
        if(maxOccursAttr == null)
            component.setMaxOccurs(1);
        else
        if(maxOccursAttr.equals("unbounded"))
            component.setMaxOccursUnbounded();
        else
            try {
                int i = Integer.parseInt(maxOccursAttr);
                if(i < 0 || i < minOccurs)
                    failValidation("validation.invalidAttributeValue", "maxOccurs", maxOccursAttr);
                component.setMaxOccurs(i);
            }
            catch(NumberFormatException numberformatexception) {
                failValidation("validation.invalidAttributeValue", "maxOccurs", maxOccursAttr);
            }
        if(element.getQName().equals(SchemaConstants.QNAME_ELEMENT))
            processElementParticle(element, component, scope, schema);
        else
        if(element.getQName().equals(SchemaConstants.QNAME_ALL) || element.getQName().equals(SchemaConstants.QNAME_CHOICE) || element.getQName().equals(SchemaConstants.QNAME_SEQUENCE)) {
            component.setTermTag(1);
            component.setModelGroupTerm(buildModelGroup(element, scope, schema));
        } else
        if(element.getQName().equals(SchemaConstants.QNAME_ANY)) {
            component.setTermTag(2);
            component.setWildcardTerm(buildAnyWildcard(element, scope, schema));
        } else {
            failValidation("validation.invalidElement", element.getLocalName());
        }
        return component;
    }

    protected ModelGroupComponent buildModelGroup(SchemaElement element, ComplexTypeDefinitionComponent scope, InternalSchema schema) {
        ModelGroupComponent component = new ModelGroupComponent();
        if(element.getQName().equals(SchemaConstants.QNAME_ALL))
            component.setCompositor(Symbol.ALL);
        else
        if(element.getQName().equals(SchemaConstants.QNAME_CHOICE))
            component.setCompositor(Symbol.CHOICE);
        else
        if(element.getQName().equals(SchemaConstants.QNAME_SEQUENCE))
            component.setCompositor(Symbol.SEQUENCE);
        else
            failValidation("validation.invalidElement", element.getLocalName());
        for(Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement)iter.next();
            if(!child.getQName().equals(SchemaConstants.QNAME_ANNOTATION)) {
                ParticleComponent particle = buildParticle(child, scope, schema);
                component.addParticle(particle);
            }
        }

        return component;
    }

    protected WildcardComponent buildAnyWildcard(SchemaElement element, ComplexTypeDefinitionComponent scope, InternalSchema schema) {
        WildcardComponent component = new WildcardComponent();
        String processContentsAttr = element.getValueOfAttributeOrNull("processContents");
        if(processContentsAttr != null) {
            Symbol processContents = parseSymbolInSet(processContentsAttr, _setLaxSkipStrict);
            if(processContents == null)
                failValidation("validation.invalidAttribute", "processContents", element.getLocalName());
            component.setProcessContents(processContents);
        } else {
            component.setProcessContents(Symbol.STRICT);
        }
        String namespaceAttr = element.getValueOfAttributeOrNull("namespace");
        if(namespaceAttr != null) {
            if(namespaceAttr.equals("##any"))
                component.setNamespaceConstraintTag(1);
            else
            if(namespaceAttr.equals("##other")) {
                String targetNamespaceURI = element.getSchema().getTargetNamespaceURI();
                if(targetNamespaceURI == null || targetNamespaceURI.equals("")) {
                    component.setNamespaceConstraintTag(3);
                } else {
                    component.setNamespaceConstraintTag(2);
                    component.setNamespaceName(targetNamespaceURI);
                }
            } else {
                failUnimplemented("F022");
            }
        } else {
            component.setNamespaceConstraintTag(1);
        }
        return component;
    }

    protected AnnotationComponent buildNonSchemaAttributesAnnotation(SchemaElement element) {
        AnnotationComponent annotation = null;
        for(Iterator iter = element.attributes(); iter.hasNext();) {
            SchemaAttribute attribute = (SchemaAttribute)iter.next();
            if(attribute.getNamespaceURI() != null && !attribute.getNamespaceURI().equals("") && !attribute.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema")) {
                if(annotation == null)
                    annotation = new AnnotationComponent();
                annotation.addAttribute(attribute);
            }
        }

        return annotation;
    }

    public TypeDefinitionComponent getTypeDefinitionComponentBeingDefined(QName name) {
        if(_namedTypeComponentsBeingDefined != null)
            return (TypeDefinitionComponent)_namedTypeComponentsBeingDefined.get(name);
        else
            return null;
    }

    protected void createWellKnownTypes() {
        _wellKnownTypes = new HashMap();
        _urType = new ComplexTypeDefinitionComponent();
        _urType.setName(SchemaConstants.QNAME_TYPE_URTYPE);
        _urType.setBaseTypeDefinition(_urType);
        _urType.setFinal(_setEmpty);
        _urType.setProhibitedSubstitutions(_setEmpty);
        _urType.setDerivationMethod(Symbol.RESTRICTION);
        _urType.setContentTag(3);
        ParticleComponent utp = new ParticleComponent();
        utp.setMinOccurs(1);
        utp.setMaxOccurs(1);
        ModelGroupComponent utpmg = new ModelGroupComponent();
        utpmg.setCompositor(Symbol.SEQUENCE);
        ParticleComponent utpmgp = new ParticleComponent();
        utpmgp.setMinOccurs(0);
        utpmgp.setMaxOccursUnbounded();
        WildcardComponent utpmgpw = new WildcardComponent();
        utpmgpw.setNamespaceConstraintTag(1);
        utpmgp.setTermTag(2);
        utpmgp.setWildcardTerm(utpmgpw);
        utpmg.addParticle(utpmgp);
        utp.setTermTag(1);
        utp.setModelGroupTerm(utpmg);
        _urType.setParticleContent(utp);
        _wellKnownTypes.put(_urType.getName(), _urType);
        _simpleUrType = new SimpleTypeDefinitionComponent();
        _simpleUrType.setName(SchemaConstants.QNAME_TYPE_SIMPLE_URTYPE);
        _simpleUrType.setBaseTypeDefinition(_simpleUrType);
        _simpleUrType.setFinal(_setEmpty);
        _wellKnownTypes.put(_simpleUrType.getName(), _simpleUrType);
        SimpleTypeDefinitionComponent type;
        for(Iterator iter = _primitiveTypeNames.iterator(); iter.hasNext(); _wellKnownTypes.put(type.getName(), type)) {
            QName name = (QName)iter.next();
            type = new SimpleTypeDefinitionComponent();
            type.setName(name);
            type.setVarietyTag(1);
            type.setFinal(_setEmpty);
            type.setBaseTypeDefinition(_simpleUrType);
            type.setPrimitiveTypeDefinition(type);
        }

        for(Iterator iter = _soapTypeNames.iterator(); iter.hasNext();) {
            QName name = (QName)iter.next();
            ComplexTypeDefinitionComponent type2 = new ComplexTypeDefinitionComponent();
            type2.setName(name);
            type2.setBaseTypeDefinition(_urType);
            type2.setContentTag(2);
            QName xName = new QName("http://www.w3.org/2001/XMLSchema", name.getLocalPart());
            SimpleTypeDefinitionComponent xComponent = (SimpleTypeDefinitionComponent)_wellKnownTypes.get(xName);
            if(xComponent != null) {
                type2.setSimpleTypeContent(xComponent);
                _wellKnownTypes.put(type2.getName(), type2);
            }
        }

        SimpleTypeDefinitionComponent base64Type = new SimpleTypeDefinitionComponent();
        base64Type.setName(SOAPConstants.QNAME_TYPE_BASE64);
        base64Type.setVarietyTag(1);
        base64Type.setFinal(_setEmpty);
        base64Type.setBaseTypeDefinition(_simpleUrType);
        base64Type.setPrimitiveTypeDefinition(base64Type);
        _wellKnownTypes.put(base64Type.getName(), base64Type);
        ComplexTypeDefinitionComponent arrayType = new ComplexTypeDefinitionComponent();
        arrayType.setName(SOAPConstants.QNAME_TYPE_ARRAY);
        arrayType.setBaseTypeDefinition(_urType);
        arrayType.setDerivationMethod(Symbol.RESTRICTION);
        arrayType.setContentTag(4);
        ParticleComponent atp = new ParticleComponent();
        atp.setMinOccurs(1);
        atp.setMaxOccurs(1);
        ModelGroupComponent atpmg = new ModelGroupComponent();
        atpmg.setCompositor(Symbol.SEQUENCE);
        ParticleComponent atpmgp = new ParticleComponent();
        atpmgp.setMinOccurs(0);
        atpmgp.setMaxOccursUnbounded();
        WildcardComponent atpmgpw = new WildcardComponent();
        atpmgpw.setNamespaceConstraintTag(1);
        atpmgp.setTermTag(2);
        atpmgp.setWildcardTerm(atpmgpw);
        atpmg.addParticle(atpmgp);
        atp.setTermTag(1);
        atp.setModelGroupTerm(atpmg);
        arrayType.setParticleContent(atp);
        _wellKnownTypes.put(arrayType.getName(), arrayType);
    }

    protected void createWellKnownAttributes() {
        _wellKnownAttributes = new HashMap();
        AttributeDeclarationComponent arrayTypeAttr = new AttributeDeclarationComponent();
        arrayTypeAttr.setName(SOAPConstants.QNAME_ATTR_ARRAY_TYPE);
        arrayTypeAttr.setTypeDefinition((SimpleTypeDefinitionComponent)_wellKnownTypes.get(SchemaConstants.QNAME_TYPE_STRING));
        _wellKnownAttributes.put(arrayTypeAttr.getName(), arrayTypeAttr);
        AttributeDeclarationComponent offsetAttr = new AttributeDeclarationComponent();
        offsetAttr.setName(SOAPConstants.QNAME_ATTR_OFFSET);
        offsetAttr.setTypeDefinition((SimpleTypeDefinitionComponent)_wellKnownTypes.get(SchemaConstants.QNAME_TYPE_STRING));
        _wellKnownAttributes.put(offsetAttr.getName(), offsetAttr);
        AttributeDeclarationComponent xmlLangAttr = new AttributeDeclarationComponent();
        xmlLangAttr.setName(new QName("http://www.w3.org/XML/1998/namespace", "lang"));
        xmlLangAttr.setTypeDefinition((SimpleTypeDefinitionComponent)_wellKnownTypes.get(SchemaConstants.QNAME_TYPE_STRING));
        _wellKnownAttributes.put(xmlLangAttr.getName(), xmlLangAttr);
    }

    protected void createWellKnownAttributeGroups() {
        _wellKnownAttributeGroups = new HashMap();
        AttributeGroupDefinitionComponent commonAttributesAttrGroup = new AttributeGroupDefinitionComponent();
        commonAttributesAttrGroup.setName(SOAPConstants.QNAME_ATTR_GROUP_COMMON_ATTRIBUTES);
        AttributeDeclarationComponent idAttr = new AttributeDeclarationComponent();
        idAttr.setName(SOAPConstants.QNAME_ATTR_ID);
        idAttr.setTypeDefinition((SimpleTypeDefinitionComponent)_wellKnownTypes.get(SchemaConstants.QNAME_TYPE_ID));
        AttributeUseComponent idAttrUse = new AttributeUseComponent();
        idAttrUse.setAttributeDeclaration(idAttr);
        commonAttributesAttrGroup.addAttributeUse(idAttrUse);
        AttributeDeclarationComponent hrefAttr = new AttributeDeclarationComponent();
        hrefAttr.setName(SOAPConstants.QNAME_ATTR_HREF);
        hrefAttr.setTypeDefinition((SimpleTypeDefinitionComponent)_wellKnownTypes.get(SchemaConstants.QNAME_TYPE_ANY_URI));
        AttributeUseComponent hrefAttrUse = new AttributeUseComponent();
        hrefAttrUse.setAttributeDeclaration(hrefAttr);
        commonAttributesAttrGroup.addAttributeUse(hrefAttrUse);
        _wellKnownAttributeGroups.put(commonAttributesAttrGroup.getName(), commonAttributesAttrGroup);
    }

    protected void createWellKnownElements() {
        _wellKnownElements = new HashMap();
    }

    private Set parseSymbolSet(String s, Set values) {
        if(s.equals("#all"))
            return values;
        Set result = new HashSet();
        List tokens = XmlUtil.parseTokenList(s);
        for(Iterator iter = tokens.iterator(); iter.hasNext();) {
            String v = (String)iter.next();
            Symbol sym = Symbol.named(v);
            if(sym != null && values.contains(sym))
                result.add(sym);
        }

        return result;
    }

    private Symbol parseSymbolInSet(String s, Set values) {
        Symbol sym = Symbol.named(s);
        if(sym != null && values.contains(sym))
            return sym;
        else
            return null;
    }

    private SchemaElement getOnlyChildIgnoring(SchemaElement element, QName name) {
        SchemaElement result = null;
        for(Iterator iter = element.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement)iter.next();
            if(!child.getQName().equals(name)) {
                if(result != null)
                    failValidation("validation.invalidElement", child.getLocalName());
                result = child;
            }
        }

        if(result == null)
            failValidation("validation.invalidElement", element.getLocalName());
        return result;
    }

    private void assertNoAttribute(SchemaElement element, String name) {
        String value = element.getValueOfAttributeOrNull(name);
        if(value != null)
            failValidation("validation.invalidAttribute", name);
    }

    private void fail(String key) {
        throw new ModelException(key);
    }

    private void fail(String key, QName name) {
        fail(key, toString(name));
    }

    private void fail(String key, String arg) {
        throw new ModelException(key, arg);
    }

    private void fail(String key, String arg1, String arg2) {
        throw new ModelException(key, new Object[] {
            arg1, arg2
        });
    }

    private void failUnimplemented(String arg) {
        throw new UnimplementedFeatureException(arg);
    }

    private void failValidation(String key) {
        throw new ValidationException(key);
    }

    private void failValidation(String key, String arg) {
        throw new ValidationException(key, arg);
    }

    private void failValidation(String key, String arg1, String arg2) {
        throw new ValidationException(key, new Object[] {
            arg1, arg2
        });
    }

    private String toString(QName name) {
        return name.getLocalPart() + " (" + name.getNamespaceURI() + ")";
    }

    static  {
        _setExtRes = new HashSet();
        _setExtRes.add(Symbol.EXTENSION);
        _setExtRes.add(Symbol.RESTRICTION);
        _setExtResSub = new HashSet();
        _setExtResSub.add(Symbol.EXTENSION);
        _setExtResSub.add(Symbol.RESTRICTION);
        _setExtResSub.add(Symbol.SUBSTITUTION);
        _setExtResListUnion = new HashSet();
        _setExtResListUnion.add(Symbol.EXTENSION);
        _setExtResListUnion.add(Symbol.RESTRICTION);
        _setExtResListUnion.add(Symbol.LIST);
        _setExtResListUnion.add(Symbol.UNION);
        _setLaxSkipStrict = new HashSet();
        _setLaxSkipStrict.add(Symbol.LAX);
        _setLaxSkipStrict.add(Symbol.SKIP);
        _setLaxSkipStrict.add(Symbol.STRICT);
        _primitiveTypeNames = new HashSet();
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_STRING);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NORMALIZED_STRING);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_TOKEN);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_BYTE);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_UNSIGNED_BYTE);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_BASE64_BINARY);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_HEX_BINARY);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_INTEGER);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_POSITIVE_INTEGER);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NEGATIVE_INTEGER);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NON_POSITIVE_INTEGER);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_INT);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_UNSIGNED_INT);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_LONG);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_UNSIGNED_LONG);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_SHORT);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_UNSIGNED_SHORT);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_DECIMAL);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_FLOAT);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_DOUBLE);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_BOOLEAN);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_TIME);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_DATE_TIME);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_DURATION);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_DATE);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_G_MONTH);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_G_YEAR);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_G_YEAR_MONTH);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_G_DAY);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_G_MONTH_DAY);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NAME);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_QNAME);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NCNAME);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_ANY_URI);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_ID);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_IDREF);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_IDREFS);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_ENTITY);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_ENTITIES);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NOTATION);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NMTOKEN);
        _primitiveTypeNames.add(SchemaConstants.QNAME_TYPE_NMTOKENS);
        _soapTypeNames = new HashSet();
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_STRING);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NORMALIZED_STRING);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_TOKEN);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_BYTE);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_UNSIGNED_BYTE);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_BASE64_BINARY);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_HEX_BINARY);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_INTEGER);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_POSITIVE_INTEGER);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NEGATIVE_INTEGER);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NON_POSITIVE_INTEGER);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_INT);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_UNSIGNED_INT);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_LONG);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_UNSIGNED_LONG);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_SHORT);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_UNSIGNED_SHORT);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_DECIMAL);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_FLOAT);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_DOUBLE);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_BOOLEAN);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_TIME);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_DATE_TIME);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_DURATION);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_DATE);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_G_MONTH);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_G_YEAR);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_G_YEAR_MONTH);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_G_DAY);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_G_MONTH_DAY);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NAME);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_QNAME);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NCNAME);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_ANY_URI);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_ID);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_IDREF);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_IDREFS);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_ENTITY);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_ENTITIES);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NOTATION);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NMTOKEN);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_NMTOKENS);
        _soapTypeNames.add(SOAPConstants.QNAME_TYPE_BASE64);
    }
}
