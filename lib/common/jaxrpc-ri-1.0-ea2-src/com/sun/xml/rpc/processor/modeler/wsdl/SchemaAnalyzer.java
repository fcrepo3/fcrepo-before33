// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   SchemaAnalyzer.java

package com.sun.xml.rpc.processor.modeler.wsdl;

import com.sun.xml.rpc.encoding.AttachmentConstants;
import com.sun.xml.rpc.encoding.simpletype.*;
import com.sun.xml.rpc.processor.config.*;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.generator.writer.SimpleTypeSerializerWriter;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.ModelException;
import com.sun.xml.rpc.processor.model.java.*;
import com.sun.xml.rpc.processor.model.literal.*;
import com.sun.xml.rpc.processor.model.soap.*;
import com.sun.xml.rpc.processor.modeler.ModelerConstants;
import com.sun.xml.rpc.processor.schema.*;
import com.sun.xml.rpc.wsdl.document.WSDLConstants;
import com.sun.xml.rpc.wsdl.document.schema.*;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;
import com.sun.xml.rpc.wsdl.framework.AbstractDocument;
import com.sun.xml.rpc.wsdl.framework.ValidationException;
import java.util.*;
import javax.xml.rpc.namespace.QName;

public class SchemaAnalyzer {

    private InternalSchema _schema;
    private WSDLModelInfo _modelInfo;
    private Set _typesBeingResolved;
    private Set _namePool;
    private Map _componentToSOAPTypeMap;
    private Map _componentToLiteralTypeMap;
    private Map _typeNameToCustomSOAPTypeMap;
    private Map _nillableSimpleTypeComponentToSOAPTypeMap;
    private Map _nillableSimpleTypeComponentToLiteralTypeMap;
    private boolean _noDataBinding;
    private int _nextUniqueID;
    private static Map _builtinSchemaTypeToJavaTypeMap;
    private static Map _builtinSchemaTypeToJavaWrapperTypeMap;
    private static Map _simpleTypeEncoderMap;

    public SchemaAnalyzer(AbstractDocument document, WSDLModelInfo modelInfo, Properties options) {
        _schema = (new InternalSchemaBuilder(document, options)).getSchema();
        _modelInfo = modelInfo;
        _typesBeingResolved = new HashSet();
        _namePool = new HashSet();
        _componentToSOAPTypeMap = new HashMap();
        _componentToLiteralTypeMap = new HashMap();
        _typeNameToCustomSOAPTypeMap = new HashMap();
        _nillableSimpleTypeComponentToSOAPTypeMap = new HashMap();
        _nillableSimpleTypeComponentToLiteralTypeMap = new HashMap();
        _nextUniqueID = 1;
        _noDataBinding = Boolean.valueOf(options.getProperty("noDataBinding")).booleanValue();
    }

    public SOAPType schemaTypeToSOAPType(QName typeName) {
        try {
            TypeDefinitionComponent component = _schema.findTypeDefinition(typeName);
            return schemaTypeToSOAPType(component);
        }
        catch(UnimplementedFeatureException unimplementedfeatureexception) {
            failUnsupported("U001", typeName);
        }
        return null;
    }

    public LiteralType schemaTypeToLiteralType(QName typeName) {
        try {
            TypeDefinitionComponent component = _schema.findTypeDefinition(typeName);
            return schemaTypeToLiteralType(component, typeName);
        }
        catch(UnimplementedFeatureException unimplementedfeatureexception) {
            LiteralType literalType = new LiteralFragmentType();
            literalType.setName(typeName);
            literalType.setSchemaTypeRef(typeName);
            literalType.setJavaType(ModelerConstants.SOAPELEMENT_JAVATYPE);
            return literalType;
        }
    }

    public LiteralType schemaElementTypeToLiteralType(QName elementName) {
        LiteralType literalType;
        try {
            ElementDeclarationComponent component = _schema.findElementDeclaration(elementName);
            literalType = schemaTypeToLiteralType(component.getTypeDefinition(), elementName);
            if(literalType.getName() == null)
                literalType.setName(getUniqueTypeNameForElement(elementName));
            return literalType;
        }
        catch(UnimplementedFeatureException unimplementedfeatureexception) {
            literalType = new LiteralFragmentType();
        }
        literalType.setName(elementName);
        literalType.setJavaType(ModelerConstants.SOAPELEMENT_JAVATYPE);
        return literalType;
    }

    protected SOAPType schemaTypeToSOAPType(TypeDefinitionComponent component) {
        SOAPType result = (SOAPType)_componentToSOAPTypeMap.get(component);
        if(result == null)
            try {
                if(component.isSimple())
                    result = simpleSchemaTypeToSOAPType((SimpleTypeDefinitionComponent)component);
                else
                if(component.isComplex())
                    result = complexSchemaTypeToSOAPType((ComplexTypeDefinitionComponent)component);
                else
                    throw new IllegalArgumentException();
                _componentToSOAPTypeMap.put(component, result);
            }
            finally { }
        return result;
    }

    protected SOAPType nillableSchemaTypeToSOAPType(TypeDefinitionComponent component) {
        JavaSimpleType javaType = (JavaSimpleType)_builtinSchemaTypeToJavaWrapperTypeMap.get(component.getName());
        if(javaType == null)
            return schemaTypeToSOAPType(component);
        SOAPSimpleType result = (SOAPSimpleType)_nillableSimpleTypeComponentToSOAPTypeMap.get(component);
        if(result != null) {
            return result;
        } else {
            result = new SOAPSimpleType(component.getName(), javaType);
            result.setSchemaTypeRef(component.getName());
            result.setReferenceable(false);
            _nillableSimpleTypeComponentToSOAPTypeMap.put(component, result);
            return result;
        }
    }

    protected SOAPType simpleSchemaTypeToSOAPType(SimpleTypeDefinitionComponent component) {
        if(component.getBaseTypeDefinition() == _schema.getSimpleUrType()) {
            if(component.getVarietyTag() == 1) {
                String nsURI = component.getName().getNamespaceURI();
                if(nsURI != null && (nsURI.equals("http://www.w3.org/2001/XMLSchema") || nsURI.equals("http://schemas.xmlsoap.org/soap/encoding/"))) {
                    if(!component.facets().hasNext()) {
                        JavaSimpleType javaType;
                        if(component.getName().equals(SchemaConstants.QNAME_TYPE_URTYPE)) {
                            SOAPAnyType anyType = new SOAPAnyType(component.getName());
                            javaType = (JavaSimpleType)_builtinSchemaTypeToJavaTypeMap.get(component.getName());
                            if(javaType == null)
                                failUnsupported("U002", component.getName());
                            anyType.setJavaType(javaType);
                            return anyType;
                        }
                        SOAPSimpleType simpleType = new SOAPSimpleType(component.getName());
                        simpleType.setSchemaTypeRef(component.getName());
                        javaType = (JavaSimpleType)_builtinSchemaTypeToJavaTypeMap.get(component.getName());
                        if(javaType == null)
                            failUnsupported("U002", component.getName());
                        simpleType.setJavaType(javaType);
                        simpleType.setReferenceable(false);
                        return simpleType;
                    }
                    failUnsupported("U003", component.getName());
                } else {
                    failUnsupported("U004", component.getName());
                }
            } else {
                failUnsupported("U005", component.getName());
            }
        } else {
            JavaSimpleType javaType = (JavaSimpleType)_builtinSchemaTypeToJavaTypeMap.get(component.getName());
            if(javaType != null) {
                SOAPSimpleType simpleType = new SOAPSimpleType(component.getName());
                simpleType.setSchemaTypeRef(component.getName());
                simpleType.setJavaType(javaType);
                simpleType.setReferenceable(true);
                return simpleType;
            }
            if(component.getVarietyTag() == 1) {
                Iterator iter = component.facets();
                if(iter.hasNext()) {
                    Facet facet = (Facet)iter.next();
                    if(iter.hasNext()) {
                        failUnsupported("U006", component.getName());
                    } else {
                        if(facet instanceof EnumerationFacet)
                            return enumerationToSOAPType(component, (EnumerationFacet)facet);
                        failUnsupported("U008", component.getName());
                    }
                } else {
                    failUnsupported("U009", component.getName());
                }
            } else {
                failUnsupported("U010", component.getName());
            }
        }
        return null;
    }

    protected SOAPType enumerationToSOAPType(SimpleTypeDefinitionComponent component, EnumerationFacet facet) {
        SimpleTypeDefinitionComponent baseType = component.getBaseTypeDefinition();
        SimpleTypeEncoder encoder = (SimpleTypeEncoder)_simpleTypeEncoderMap.get(baseType.getName());
        if(encoder != null) {
            JavaType javaEntryType = (JavaType)_builtinSchemaTypeToJavaTypeMap.get(baseType.getName());
            JavaEnumerationType javaEnumType = new JavaEnumerationType(makePackageQualified(Names.validJavaClassName(component.getName().getLocalPart())), javaEntryType, false);
            SOAPEnumerationType soapEnumType = new SOAPEnumerationType(component.getName(), schemaTypeToSOAPType(baseType), javaEnumType);
            boolean mustRename = false;
            for(Iterator values = facet.values(); values.hasNext();) {
                String value = (String)values.next();
                try {
                    JavaEnumerationEntry entry = new JavaEnumerationEntry(value, encoder.stringToObject(value, null), value);
                    if(!mustRename && isInvalidEnumerationLabel(value))
                        mustRename = true;
                    javaEnumType.add(entry);
                }
                catch(Exception exception) {
                    fail("model.schema.invalidLiteralInEnumeration", value, component.getName());
                }
            }

            if(mustRename) {
                int index = 1;
                for(Iterator iter = javaEnumType.getEntries(); iter.hasNext();) {
                    JavaEnumerationEntry entry = (JavaEnumerationEntry)iter.next();
                    entry.setName("value" + Integer.toString(index));
                    index++;
                }

            }
            return soapEnumType;
        } else {
            failUnsupported("U007", component.getName());
            return null;
        }
    }

    protected SOAPType complexSchemaTypeToSOAPType(ComplexTypeDefinitionComponent component) {
        if(component == _schema.getUrType()) {
            JavaSimpleType javaType;
            if(component.getName().equals(SchemaConstants.QNAME_TYPE_URTYPE)) {
                SOAPAnyType anyType = new SOAPAnyType(component.getName());
                javaType = (JavaSimpleType)_builtinSchemaTypeToJavaTypeMap.get(component.getName());
                if(javaType == null)
                    failUnsupported("U002", component.getName());
                anyType.setJavaType(javaType);
                return anyType;
            }
            SOAPSimpleType simpleType = new SOAPSimpleType(component.getName());
            simpleType.setSchemaTypeRef(component.getName());
            javaType = (JavaSimpleType)_builtinSchemaTypeToJavaTypeMap.get(component.getName());
            if(javaType == null)
                failUnsupported("U013", component.getName());
            simpleType.setJavaType(javaType);
            simpleType.setReferenceable(false);
            return simpleType;
        }
        if(component.getBaseTypeDefinition() == _schema.getUrType())
            return urTypeBasedComplexSchemaTypeToSOAPType(component);
        if(component.getBaseTypeDefinition().getName().equals(SOAPConstants.QNAME_TYPE_ARRAY)) {
            return soapArrayBasedComplexSchemaTypeToSOAPType(component);
        } else {
            failUnsupported("U011", component.getName());
            return null;
        }
    }

    protected SOAPType urTypeBasedComplexSchemaTypeToSOAPType(ComplexTypeDefinitionComponent component) {
        if(component.getContentTag() == 4)
            if(component.hasNoAttributeUses()) {
                ParticleComponent particle = component.getParticleContent();
                if(particle.occursOnce()) {
                    if(particle.getTermTag() == 1) {
                        ModelGroupComponent modelGroup = particle.getModelGroupTerm();
                        if(modelGroup.getCompositor() == Symbol.ALL || modelGroup.getCompositor() == Symbol.SEQUENCE) {
                            SOAPStructureType structureType = null;
                            if(modelGroup.getCompositor() == Symbol.ALL)
                                structureType = new SOAPUnorderedStructureType(component.getName());
                            else
                                structureType = new SOAPOrderedStructureType(component.getName());
                            JavaStructureType javaStructureType = new JavaStructureType(makePackageQualified(Names.validJavaClassName(component.getName().getLocalPart())), false);
                            structureType.setJavaType(javaStructureType);
                            _componentToSOAPTypeMap.put(component, structureType);
                            for(Iterator iter = modelGroup.particles(); iter.hasNext();) {
                                ParticleComponent memberParticle = (ParticleComponent)iter.next();
                                if(memberParticle.occursOnce() || memberParticle.occursAtMostOnce()) {
                                    if(memberParticle.getTermTag() == 3) {
                                        ElementDeclarationComponent element = memberParticle.getElementTerm();
                                        SOAPType memberType;
                                        if(element.isNillable()) {
                                            if(element.getTypeDefinition().isSimple())
                                                memberType = nillableSchemaTypeToSOAPType(element.getTypeDefinition());
                                            else
                                                memberType = schemaTypeToSOAPType(element.getTypeDefinition());
                                        } else {
                                            memberType = schemaTypeToSOAPType(element.getTypeDefinition());
                                        }
                                        SOAPStructureMember member = new SOAPStructureMember(element.getName(), memberType);
                                        JavaStructureMember javaMember = new JavaStructureMember(Names.validJavaMemberName(element.getName().getLocalPart()), memberType.getJavaType(), member, false);
                                        javaMember.setReadMethod(Names.getJavaMemberReadMethod(javaMember));
                                        javaMember.setWriteMethod(Names.getJavaMemberWriteMethod(javaMember));
                                        member.setJavaStructureMember(javaMember);
                                        javaStructureType.add(javaMember);
                                        structureType.add(member);
                                    } else {
                                        return mustGetCustomTypeFor(component);
                                    }
                                } else {
                                    return mustGetCustomTypeFor(component);
                                }
                            }

                            structureType.setJavaType(javaStructureType);
                            return structureType;
                        } else {
                            return mustGetCustomTypeFor(component);
                        }
                    } else {
                        return mustGetCustomTypeFor(component);
                    }
                } else {
                    return mustGetCustomTypeFor(component);
                }
            } else {
                return mustGetCustomTypeFor(component);
            }
        if(component.getContentTag() == 1) {
            SOAPOrderedStructureType structureType = new SOAPOrderedStructureType(component.getName());
            return structureType;
        }
        if(component.getContentTag() == 2) {
            if(component.hasNoAttributeUses()) {
                if(component.getName().getNamespaceURI().equals("http://schemas.xmlsoap.org/soap/encoding/")) {
                    SOAPSimpleType simpleType = new SOAPSimpleType(component.getName());
                    simpleType.setSchemaTypeRef(component.getName());
                    JavaSimpleType javaType = (JavaSimpleType)_builtinSchemaTypeToJavaTypeMap.get(component.getName());
                    if(javaType == null)
                        failUnsupported("U013", component.getName());
                    simpleType.setJavaType(javaType);
                    simpleType.setReferenceable(false);
                    return simpleType;
                } else {
                    return mustGetCustomTypeFor(component);
                }
            } else {
                return mustGetCustomTypeFor(component);
            }
        } else {
            return mustGetCustomTypeFor(component);
        }
    }

    protected SOAPType soapArrayBasedComplexSchemaTypeToSOAPType(ComplexTypeDefinitionComponent component) {
        boolean found = false;
        for(Iterator iter = component.attributeUses(); iter.hasNext();) {
            AttributeUseComponent attributeUse = (AttributeUseComponent)iter.next();
            AttributeDeclarationComponent attributeDeclaration = attributeUse.getAttributeDeclaration();
            if(attributeDeclaration.getName() != null && attributeDeclaration.getName().equals(SOAPConstants.QNAME_ATTR_ARRAY_TYPE)) {
                if(found)
                    return mustGetCustomTypeFor(component);
                found = true;
                for(Iterator iter2 = attributeUse.getAnnotation().attributes(); iter2.hasNext();) {
                    SchemaAttribute attr = (SchemaAttribute)iter2.next();
                    if(attr.getQName().equals(WSDLConstants.QNAME_ATTR_ARRAY_TYPE)) {
                        String typeSpecifier = attr.getValue();
                        if(typeSpecifier == null)
                            throw new ModelException(new ValidationException("validation.invalidAttributeValue", new Object[] {
                                typeSpecifier, "arrayType"
                            }));
                        else
                            return processSOAPArrayType(component, attr.getParent(), attr.getValue());
                    }
                }

            }
        }

        if(component.getContentTag() == 4) {
            ParticleComponent particle = component.getParticleContent();
            if(particle.occursOnce()) {
                if(particle.getTermTag() == 1) {
                    ModelGroupComponent modelGroup = particle.getModelGroupTerm();
                    if(modelGroup.getCompositor() == Symbol.SEQUENCE) {
                        SOAPArrayType arrayType = new SOAPArrayType(component.getName());
                        found = false;
                        for(Iterator iter = modelGroup.particles(); iter.hasNext();) {
                            ParticleComponent memberParticle = (ParticleComponent)iter.next();
                            if(found)
                                return mustGetCustomTypeFor(component);
                            found = true;
                            if(memberParticle.occursZeroOrMore()) {
                                if(memberParticle.getTermTag() == 3) {
                                    ElementDeclarationComponent element = memberParticle.getElementTerm();
                                    SOAPType arrayElementType = schemaTypeToSOAPType(element.getTypeDefinition());
                                    arrayType.setElementName(element.getName());
                                    arrayType.setElementType(arrayElementType);
                                    arrayType.setRank(1);
                                    if(arrayElementType.getJavaType() != null) {
                                        JavaArrayType javaArrayType = new JavaArrayType(arrayElementType.getJavaType().getName() + "[]");
                                        javaArrayType.setElementType(arrayElementType.getJavaType());
                                        arrayType.setJavaType(javaArrayType);
                                    }
                                } else {
                                    return mustGetCustomTypeFor(component);
                                }
                            } else {
                                return mustGetCustomTypeFor(component);
                            }
                        }

                        if(found)
                            return arrayType;
                        else
                            return mustGetCustomTypeFor(component);
                    } else {
                        return mustGetCustomTypeFor(component);
                    }
                } else {
                    return mustGetCustomTypeFor(component);
                }
            } else {
                return mustGetCustomTypeFor(component);
            }
        } else {
            return mustGetCustomTypeFor(component);
        }
    }

    protected SOAPType processSOAPArrayType(TypeDefinitionComponent component, SchemaElement element, String typeSpecifier) {
        try {
            int openingBracketIndex = typeSpecifier.indexOf('[');
            if(openingBracketIndex == -1)
                throw new ValidationException("validation.invalidAttributeValue", new Object[] {
                    typeSpecifier, "arrayType"
                });
            int currentRank = 0;
            String typeName = typeSpecifier.substring(0, openingBracketIndex).trim();
            QName typeQName = element.asQName(typeName);
            SOAPType elementType = schemaTypeToSOAPType(typeQName);
            if(elementType instanceof SOAPArrayType)
                currentRank = ((SOAPArrayType)elementType).getRank();
            int closingBracketIndex;
            do {
                closingBracketIndex = typeSpecifier.indexOf(']', openingBracketIndex);
                if(closingBracketIndex == -1)
                    throw new ValidationException("validation.invalidAttributeValue", new Object[] {
                        typeSpecifier, "arrayType"
                    });
                int commaIndex = typeSpecifier.indexOf(',', openingBracketIndex + 1);
                if(commaIndex == -1 || commaIndex > closingBracketIndex) {
                    int size[] = null;
                    if(closingBracketIndex - openingBracketIndex > 1) {
                        int i = Integer.parseInt(typeSpecifier.substring(openingBracketIndex + 1, closingBracketIndex));
                        size = (new int[] {
                            i
                        });
                    }
                    SOAPArrayType arrayType = new SOAPArrayType(component.getName());
                    arrayType.setElementName(null);
                    arrayType.setElementType(elementType);
                    arrayType.setRank(++currentRank);
                    arrayType.setSize(size);
                    if(elementType.getJavaType() != null) {
                        JavaArrayType javaArrayType = new JavaArrayType(elementType.getJavaType().getName() + "[]");
                        javaArrayType.setElementType(elementType.getJavaType());
                        arrayType.setJavaType(javaArrayType);
                    }
                    elementType = arrayType;
                } else {
                    List sizeList = null;
                    boolean allowSizeSpecifiers = true;
                    boolean timeToGo = false;
                    int rank = 0;
                    int contentIndex = openingBracketIndex + 1;
                    do {
                        rank++;
                        if(commaIndex - contentIndex > 0) {
                            if(!allowSizeSpecifiers)
                                throw new ValidationException("validation.invalidAttributeValue", new Object[] {
                                    typeSpecifier, "arrayType"
                                });
                            int i = Integer.parseInt(typeSpecifier.substring(contentIndex, commaIndex));
                            if(sizeList == null)
                                sizeList = new ArrayList();
                            sizeList.add(new Integer(i));
                        } else {
                            if(sizeList != null)
                                throw new ValidationException("validation.invalidAttributeValue", new Object[] {
                                    typeSpecifier, "arrayType"
                                });
                            allowSizeSpecifiers = false;
                        }
                        if(timeToGo)
                            break;
                        contentIndex = commaIndex + 1;
                        commaIndex = typeSpecifier.indexOf(',', contentIndex);
                        if(commaIndex == -1 || commaIndex > closingBracketIndex) {
                            commaIndex = closingBracketIndex;
                            timeToGo = true;
                        }
                    } while(true);
                    SOAPArrayType arrayType = new SOAPArrayType(component.getName());
                    arrayType.setElementName(null);
                    arrayType.setElementType(elementType);
                    currentRank += rank;
                    arrayType.setRank(currentRank);
                    int size[] = null;
                    if(allowSizeSpecifiers && sizeList != null) {
                        size = new int[sizeList.size()];
                        for(int i = 0; i < size.length; i++)
                            size[i] = ((Integer)sizeList.get(i)).intValue();

                    }
                    arrayType.setSize(size);
                    if(elementType.getJavaType() != null) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(elementType.getJavaType().getName());
                        for(int i = 0; i < rank; i++)
                            sb.append("[]");

                        String javaArrayTypeName = sb.toString();
                        JavaArrayType javaArrayType = new JavaArrayType(javaArrayTypeName);
                        javaArrayType.setElementType(elementType.getJavaType());
                        arrayType.setJavaType(javaArrayType);
                    }
                    elementType = arrayType;
                }
                openingBracketIndex = typeSpecifier.indexOf('[', closingBracketIndex + 1);
            } while(openingBracketIndex != -1);
            if(closingBracketIndex != typeSpecifier.length() - 1)
                throw new ValidationException("validation.invalidAttributeValue", new Object[] {
                    typeSpecifier, "arrayType"
                });
            else
                return elementType;
        }
        catch(NumberFormatException numberformatexception) {
            throw new ModelException(new ValidationException("validation.invalidAttributeValue", new Object[] {
                typeSpecifier, "arrayType"
            }));
        }
        catch(ValidationException e) {
            throw new ModelException(e);
        }
    }

    protected LiteralType schemaTypeToLiteralType(TypeDefinitionComponent component, QName nameHint) {
        LiteralType result = (LiteralType)_componentToLiteralTypeMap.get(component);
        if(result == null)
            try {
                if(_noDataBinding)
                    result = getLiteralFragmentTypeFor(component, nameHint);
                else
                if(component.isSimple())
                    result = simpleSchemaTypeToLiteralType((SimpleTypeDefinitionComponent)component, nameHint);
                else
                if(component.isComplex())
                    result = complexSchemaTypeToLiteralType((ComplexTypeDefinitionComponent)component, nameHint);
                else
                    throw new IllegalArgumentException();
                _componentToLiteralTypeMap.put(component, result);
            }
            finally { }
        return result;
    }

    protected LiteralType simpleSchemaTypeToLiteralType(SimpleTypeDefinitionComponent component, QName nameHint) {
        if(component.getBaseTypeDefinition() == _schema.getSimpleUrType())
            if(component.getVarietyTag() == 1) {
                String nsURI = component.getName().getNamespaceURI();
                if(nsURI != null && nsURI.equals("http://www.w3.org/2001/XMLSchema")) {
                    if(!component.facets().hasNext()) {
                        if(component.getName().equals(SchemaConstants.QNAME_TYPE_URTYPE))
                            return getLiteralFragmentTypeFor(component, nameHint);
                        LiteralSimpleType simpleType = new LiteralSimpleType(component.getName());
                        simpleType.setSchemaTypeRef(component.getName());
                        simpleType.setSchemaType(component);
                        JavaSimpleType javaType = (JavaSimpleType)_builtinSchemaTypeToJavaTypeMap.get(component.getName());
                        if(javaType == null) {
                            return getLiteralFragmentTypeFor(component, nameHint);
                        } else {
                            simpleType.setJavaType(javaType);
                            return simpleType;
                        }
                    } else {
                        return getLiteralSimpleStringTypeFor(component, nameHint);
                    }
                } else {
                    return getLiteralSimpleStringTypeFor(component, nameHint);
                }
            } else {
                return getLiteralSimpleStringTypeFor(component, nameHint);
            }
        SimpleTypeDefinitionComponent baseTypeComponent = component.getBaseTypeDefinition();
        if(component.getVarietyTag() == 1) {
            String nsURI = baseTypeComponent.getName().getNamespaceURI();
            if(nsURI != null && nsURI.equals("http://www.w3.org/2001/XMLSchema")) {
                LiteralType baseType = schemaTypeToLiteralType(baseTypeComponent, nameHint);
                return baseType;
            } else {
                return getLiteralSimpleStringTypeFor(component, nameHint);
            }
        } else {
            return getLiteralSimpleStringTypeFor(component, nameHint);
        }
    }

    protected LiteralType complexSchemaTypeToLiteralType(ComplexTypeDefinitionComponent component, QName nameHint) {
        if(component == _schema.getUrType()) {
            if(component.getName().equals(SchemaConstants.QNAME_TYPE_URTYPE)) {
                LiteralType literalType = new LiteralFragmentType();
                literalType.setName(component.getName());
                literalType.setSchemaType(component);
                literalType.setJavaType(ModelerConstants.SOAPELEMENT_JAVATYPE);
                return literalType;
            }
            LiteralSimpleType simpleType = new LiteralSimpleType(component.getName());
            simpleType.setSchemaTypeRef(component.getName());
            simpleType.setSchemaType(component);
            JavaSimpleType javaType = (JavaSimpleType)_builtinSchemaTypeToJavaTypeMap.get(component.getName());
            if(javaType == null) {
                return getLiteralFragmentTypeFor(component, nameHint);
            } else {
                simpleType.setJavaType(javaType);
                return simpleType;
            }
        }
        if(component.getBaseTypeDefinition() == _schema.getUrType())
            return urTypeBasedComplexSchemaTypeToLiteralType(component, nameHint);
        else
            return getLiteralFragmentTypeFor(component, nameHint);
    }

    protected LiteralType urTypeBasedComplexSchemaTypeToLiteralType(ComplexTypeDefinitionComponent component, QName nameHint) {
        if(component.getContentTag() == 4) {
            ParticleComponent particle = component.getParticleContent();
            if(particle.occursOnce()) {
                if(particle.getTermTag() == 1) {
                    ModelGroupComponent modelGroup = particle.getModelGroupTerm();
                    if(modelGroup.getCompositor() == Symbol.ALL || modelGroup.getCompositor() == Symbol.SEQUENCE) {
                        LiteralStructuredType structureType = null;
                        if(modelGroup.getCompositor() == Symbol.ALL)
                            structureType = new LiteralAllType(getUniqueQNameFor(component, nameHint));
                        else
                            structureType = new LiteralSequenceType(getUniqueQNameFor(component, nameHint));
                        JavaStructureType javaStructureType = new JavaStructureType(makePackageQualified(Names.validJavaClassName(structureType.getName().getLocalPart())), false);
                        structureType.setJavaType(javaStructureType);
                        _componentToLiteralTypeMap.put(component, structureType);
                        LiteralAttributeMember member;
                        for(Iterator iter = component.attributeUses(); iter.hasNext(); structureType.add(member)) {
                            AttributeUseComponent attributeUse = (AttributeUseComponent)iter.next();
                            AttributeDeclarationComponent attributeDeclaration = attributeUse.getAttributeDeclaration();
                            LiteralType attributeType = schemaTypeToLiteralType(attributeDeclaration.getTypeDefinition(), getAttributeQNameHint(attributeDeclaration, nameHint));
                            if(SimpleTypeSerializerWriter.getTypeEncoder(attributeType) == null)
                                return getLiteralFragmentTypeFor(component, nameHint);
                            member = new LiteralAttributeMember(attributeDeclaration.getName(), attributeType);
                            if(attributeUse.isRequired())
                                member.setRequired(true);
                            JavaStructureMember javaMember = new JavaStructureMember(Names.validJavaMemberName(attributeDeclaration.getName().getLocalPart()), attributeType.getJavaType(), member, false);
                            javaMember.setReadMethod(Names.getJavaMemberReadMethod(javaMember));
                            javaMember.setWriteMethod(Names.getJavaMemberWriteMethod(javaMember));
                            member.setJavaStructureMember(javaMember);
                            javaStructureType.add(javaMember);
                        }

                        for(Iterator iter = modelGroup.particles(); iter.hasNext();) {
                            ParticleComponent memberParticle = (ParticleComponent)iter.next();
                            if(!memberParticle.doesNotOccur())
                                if(memberParticle.getTermTag() == 3) {
                                    ElementDeclarationComponent element = memberParticle.getElementTerm();
                                    LiteralType memberType = schemaTypeToLiteralType(element.getTypeDefinition(), getElementQNameHint(element, nameHint));
                                    LiteralElementMember member2 = new LiteralElementMember(element.getName(), memberType);
                                    JavaType javaMemberType = null;
                                    if(element.isNillable()) {
                                        member2.setNillable(true);
                                        JavaSimpleType javaType = (JavaSimpleType)_builtinSchemaTypeToJavaWrapperTypeMap.get(element.getTypeDefinition().getName());
                                        if(javaType != null) {
                                            LiteralSimpleType result = (LiteralSimpleType)_nillableSimpleTypeComponentToLiteralTypeMap.get(element.getTypeDefinition());
                                            if(result == null) {
                                                result = new LiteralSimpleType(element.getTypeDefinition().getName(), javaType);
                                                result.setSchemaTypeRef(element.getTypeDefinition().getName());
                                                result.setSchemaType(element.getTypeDefinition());
                                                _nillableSimpleTypeComponentToLiteralTypeMap.put(element.getTypeDefinition(), result);
                                            }
                                            memberType = result;
                                        }
                                    }
                                    if(memberParticle.occursAtLeastOnce())
                                        member2.setRequired(true);
                                    if(memberParticle.mayOccurMoreThanOnce()) {
                                        member2.setRepeated(true);
                                        JavaArrayType javaArrayType = new JavaArrayType(memberType.getJavaType().getName() + "[]");
                                        javaArrayType.setElementType(memberType.getJavaType());
                                        javaMemberType = javaArrayType;
                                    } else {
                                        javaMemberType = memberType.getJavaType();
                                    }
                                    JavaStructureMember javaMember = new JavaStructureMember(Names.validJavaMemberName(element.getName().getLocalPart()), javaMemberType, member2, false);
                                    javaMember.setReadMethod(Names.getJavaMemberReadMethod(javaMember));
                                    javaMember.setWriteMethod(Names.getJavaMemberWriteMethod(javaMember));
                                    member2.setJavaStructureMember(javaMember);
                                    javaStructureType.add(javaMember);
                                    structureType.add(member2);
                                } else {
                                    return getLiteralFragmentTypeFor(component, nameHint);
                                }
                        }

                        return structureType;
                    } else {
                        return getLiteralFragmentTypeFor(component, nameHint);
                    }
                } else {
                    return getLiteralFragmentTypeFor(component, nameHint);
                }
            } else {
                return getLiteralFragmentTypeFor(component, nameHint);
            }
        }
        if(component.getContentTag() == 1) {
            LiteralSequenceType structureType = new LiteralSequenceType(getUniqueQNameFor(component, nameHint));
            JavaStructureType javaStructureType = new JavaStructureType(makePackageQualified(Names.validJavaClassName(structureType.getName().getLocalPart())), false);
            structureType.setJavaType(javaStructureType);
            _componentToLiteralTypeMap.put(component, structureType);
            LiteralAttributeMember member;
            for(Iterator iter = component.attributeUses(); iter.hasNext(); structureType.add(member)) {
                AttributeUseComponent attributeUse = (AttributeUseComponent)iter.next();
                AttributeDeclarationComponent attributeDeclaration = attributeUse.getAttributeDeclaration();
                LiteralType attributeType = schemaTypeToLiteralType(attributeDeclaration.getTypeDefinition(), getAttributeQNameHint(attributeDeclaration, nameHint));
                if(SimpleTypeSerializerWriter.getTypeEncoder(attributeType) == null)
                    return getLiteralFragmentTypeFor(component, nameHint);
                member = new LiteralAttributeMember(attributeDeclaration.getName(), attributeType);
                if(attributeUse.isRequired())
                    member.setRequired(true);
                JavaStructureMember javaMember = new JavaStructureMember(Names.validJavaMemberName(attributeDeclaration.getName().getLocalPart()), attributeType.getJavaType(), member, false);
                javaMember.setReadMethod(Names.getJavaMemberReadMethod(javaMember));
                javaMember.setWriteMethod(Names.getJavaMemberWriteMethod(javaMember));
                member.setJavaStructureMember(javaMember);
                javaStructureType.add(javaMember);
            }

            return structureType;
        } else {
            return getLiteralFragmentTypeFor(component, nameHint);
        }
    }

    protected LiteralFragmentType getLiteralFragmentTypeFor(TypeDefinitionComponent component, QName nameHint) {
        LiteralFragmentType literalType = new LiteralFragmentType();
        literalType.setName(getUniqueQNameFor(component, nameHint));
        literalType.setSchemaType(component);
        literalType.setJavaType(ModelerConstants.SOAPELEMENT_JAVATYPE);
        return literalType;
    }

    protected LiteralType getLiteralSimpleStringTypeFor(TypeDefinitionComponent component, QName nameHint) {
        LiteralSimpleType literalType = new LiteralSimpleType(getUniqueQNameFor(component, nameHint));
        literalType.setSchemaType(component);
        literalType.setJavaType(ModelerConstants.STRING_JAVATYPE);
        return literalType;
    }

    protected String makePackageQualified(String s) {
        if(_modelInfo.getJavaPackageName() != null && !_modelInfo.getJavaPackageName().equals(""))
            return _modelInfo.getJavaPackageName() + "." + s;
        else
            return s;
    }

    protected QName makePackageQualified(QName name) {
        return new QName(name.getNamespaceURI(), makePackageQualified(name.getLocalPart()));
    }

    protected SOAPCustomType getCustomTypeFor(TypeDefinitionComponent component) {
        QName typeName = component.getName();
        if(typeName == null)
            return null;
        SOAPCustomType customType = (SOAPCustomType)_typeNameToCustomSOAPTypeMap.get(typeName);
        if(customType == null) {
            TypeMappingInfo tmi = _modelInfo.getTypeMappingRegistry().getTypeMappingInfo("http://schemas.xmlsoap.org/soap/encoding/", typeName);
            if(tmi != null) {
                customType = new SOAPCustomType(typeName);
                customType.setSchemaType(component);
                JavaCustomType javaCustomType = new JavaCustomType(tmi.getJavaTypeName(), tmi);
                customType.setJavaType(javaCustomType);
                _typeNameToCustomSOAPTypeMap.put(typeName, customType);
            }
        }
        return customType;
    }

    protected SOAPCustomType mustGetCustomTypeFor(TypeDefinitionComponent component) {
        SOAPCustomType type = getCustomTypeFor(component);
        if(type == null)
            failUnsupported("U012", component.getName());
        return type;
    }

    protected boolean isInvalidEnumerationLabel(String s) {
        if(s == null || s.equals("") || !Character.isJavaIdentifierStart(s.charAt(0)))
            return true;
        for(int i = 1; i < s.length(); i++)
            if(!Character.isJavaIdentifierPart(s.charAt(i)))
                return true;

        return Names.isJavaReservedWord(s);
    }

    protected void fail(String key, String code, QName arg) {
        if(arg == null)
            throw new ModelException(key + ".anonymous", code);
        else
            throw new ModelException(key, new Object[] {
                code, arg.getLocalPart(), arg.getNamespaceURI()
            });
    }

    protected void failUnsupported(String code, QName arg) {
        fail("model.schema.unsupportedType", code, arg);
    }

    protected QName getElementQNameHint(ElementDeclarationComponent component, QName nameHint) {
        QName componentName = component.getName();
        if(!componentName.getNamespaceURI().equals(""))
            return componentName;
        else
            return new QName(nameHint.getNamespaceURI(), nameHint.getLocalPart() + "-" + componentName.getLocalPart());
    }

    protected QName getAttributeQNameHint(AttributeDeclarationComponent component, QName nameHint) {
        return new QName(nameHint.getNamespaceURI(), nameHint.getLocalPart() + "-" + component.getName().getLocalPart());
    }

    protected QName getUniqueLiteralArrayTypeQNameFor(QName subTypeName, QName nameHint) {
        return new QName(subTypeName.getNamespaceURI(), subTypeName.getLocalPart() + "-Array-" + getUniqueID());
    }

    protected QName getUniqueTypeNameForElement(QName elementName) {
        return new QName(elementName.getNamespaceURI(), elementName.getLocalPart() + "-AnonType-" + getUniqueID());
    }

    protected String getUniqueNCNameFor(TypeDefinitionComponent component) {
        if(component.getName() != null)
            return component.getName().getLocalPart();
        else
            return "genType-" + getUniqueID();
    }

    protected QName getUniqueQNameFor(TypeDefinitionComponent component, QName nameHint) {
        if(component.getName() != null)
            return component.getName();
        QName result = null;
        if(nameHint != null) {
            if(!_namePool.contains(nameHint))
                result = nameHint;
            else
                result = new QName(nameHint.getNamespaceURI(), nameHint.getLocalPart() + "-gen-" + getUniqueID());
        } else
        if(!_namePool.contains(nameHint))
            result = nameHint;
        else
            result = new QName(nameHint.getLocalPart() + "-gen-" + getUniqueID());
        _namePool.add(result);
        return result;
    }

    protected int getUniqueID() {
        return _nextUniqueID++;
    }

    static  {
        _builtinSchemaTypeToJavaTypeMap = new HashMap();
        _builtinSchemaTypeToJavaTypeMap.put(BuiltInTypes.STRING, ModelerConstants.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(BuiltInTypes.INTEGER, ModelerConstants.BIG_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(BuiltInTypes.INT, ModelerConstants.INT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(BuiltInTypes.LONG, ModelerConstants.LONG_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(BuiltInTypes.SHORT, ModelerConstants.SHORT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(BuiltInTypes.DECIMAL, ModelerConstants.DECIMAL_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(BuiltInTypes.FLOAT, ModelerConstants.FLOAT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(BuiltInTypes.DOUBLE, ModelerConstants.DOUBLE_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(BuiltInTypes.BOOLEAN, ModelerConstants.BOOLEAN_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(BuiltInTypes.BYTE, ModelerConstants.BYTE_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(BuiltInTypes.QNAME, ModelerConstants.QNAME_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(BuiltInTypes.DATE_TIME, ModelerConstants.CALENDAR_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(BuiltInTypes.BASE64_BINARY, ModelerConstants.BYTE_ARRAY_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(BuiltInTypes.HEX_BINARY, ModelerConstants.BYTE_ARRAY_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SchemaConstants.QNAME_TYPE_URTYPE, ModelerConstants.OBJECT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SOAPConstants.QNAME_TYPE_STRING, ModelerConstants.STRING_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SOAPConstants.QNAME_TYPE_INTEGER, ModelerConstants.BIG_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SOAPConstants.QNAME_TYPE_INT, ModelerConstants.BOXED_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SOAPConstants.QNAME_TYPE_LONG, ModelerConstants.BOXED_LONG_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SOAPConstants.QNAME_TYPE_SHORT, ModelerConstants.BOXED_SHORT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SOAPConstants.QNAME_TYPE_DECIMAL, ModelerConstants.DECIMAL_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SOAPConstants.QNAME_TYPE_FLOAT, ModelerConstants.BOXED_FLOAT_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SOAPConstants.QNAME_TYPE_DOUBLE, ModelerConstants.BOXED_DOUBLE_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SOAPConstants.QNAME_TYPE_BOOLEAN, ModelerConstants.BOXED_BOOLEAN_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SOAPConstants.QNAME_TYPE_BYTE, ModelerConstants.BOXED_BYTE_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SOAPConstants.QNAME_TYPE_QNAME, ModelerConstants.QNAME_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SOAPConstants.QNAME_TYPE_DATE_TIME, ModelerConstants.CALENDAR_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SOAPConstants.QNAME_TYPE_BASE64_BINARY, ModelerConstants.BYTE_ARRAY_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SOAPConstants.QNAME_TYPE_HEX_BINARY, ModelerConstants.BYTE_ARRAY_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(SOAPConstants.QNAME_TYPE_BASE64, ModelerConstants.BYTE_ARRAY_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(AttachmentConstants.QNAME_TYPE_IMAGE, ModelerConstants.IMAGE_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(AttachmentConstants.QNAME_TYPE_MIME_MULTIPART, ModelerConstants.MIME_MULTIPART_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(AttachmentConstants.QNAME_TYPE_SOURCE, ModelerConstants.SOURCE_JAVATYPE);
        _builtinSchemaTypeToJavaTypeMap.put(AttachmentConstants.QNAME_TYPE_DATA_HANDLER, ModelerConstants.DATA_HANDLER_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap = new HashMap();
        _builtinSchemaTypeToJavaWrapperTypeMap.put(BuiltInTypes.INT, ModelerConstants.BOXED_INTEGER_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(BuiltInTypes.LONG, ModelerConstants.BOXED_LONG_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(BuiltInTypes.SHORT, ModelerConstants.BOXED_SHORT_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(BuiltInTypes.FLOAT, ModelerConstants.BOXED_FLOAT_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(BuiltInTypes.DOUBLE, ModelerConstants.BOXED_DOUBLE_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(BuiltInTypes.BOOLEAN, ModelerConstants.BOXED_BOOLEAN_JAVATYPE);
        _builtinSchemaTypeToJavaWrapperTypeMap.put(BuiltInTypes.BYTE, ModelerConstants.BOXED_BYTE_JAVATYPE);
        _simpleTypeEncoderMap = new HashMap();
        _simpleTypeEncoderMap.put(BuiltInTypes.STRING, XSDStringEncoder.getInstance());
        _simpleTypeEncoderMap.put(BuiltInTypes.INTEGER, XSDIntegerEncoder.getInstance());
        _simpleTypeEncoderMap.put(BuiltInTypes.INT, XSDIntEncoder.getInstance());
        _simpleTypeEncoderMap.put(BuiltInTypes.LONG, XSDLongEncoder.getInstance());
        _simpleTypeEncoderMap.put(BuiltInTypes.SHORT, XSDShortEncoder.getInstance());
        _simpleTypeEncoderMap.put(BuiltInTypes.DECIMAL, XSDDecimalEncoder.getInstance());
        _simpleTypeEncoderMap.put(BuiltInTypes.FLOAT, XSDFloatEncoder.getInstance());
        _simpleTypeEncoderMap.put(BuiltInTypes.DOUBLE, XSDDoubleEncoder.getInstance());
        _simpleTypeEncoderMap.put(BuiltInTypes.BYTE, XSDByteEncoder.getInstance());
    }
}
