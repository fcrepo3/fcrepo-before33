// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   WSDLTypeGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.encoding.AttachmentConstants;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.JavaEnumerationEntry;
import com.sun.xml.rpc.processor.model.java.JavaEnumerationType;
import com.sun.xml.rpc.processor.model.soap.*;
import com.sun.xml.rpc.wsdl.document.WSDLConstants;
import com.sun.xml.rpc.wsdl.document.schema.*;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;
import java.util.Iterator;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            WSDLTypeGenerator

class WSDLTypeGenerator$1
    implements SOAPTypeVisitor {

    private final Schema val$schema; /* synthetic field */
    private final WSDLTypeGenerator this$0; /* synthetic field */

    WSDLTypeGenerator$1(WSDLTypeGenerator this$0, Schema val$schema) {
        this.this$0 = this$0;
        this.val$schema = val$schema;
    }

    public void visit(SOAPArrayType type) throws Exception {
        SchemaElement complexType = new SchemaElement(SchemaConstants.QNAME_COMPLEX_TYPE);
        val$schema.getContent().addChild(complexType);
        complexType.addAttribute("name", type.getName().getLocalPart());
        SchemaElement complexContent = new SchemaElement(SchemaConstants.QNAME_COMPLEX_CONTENT);
        SchemaElement restriction = new SchemaElement(SchemaConstants.QNAME_RESTRICTION);
        restriction.addAttribute("base", "soap-enc:Array");
        SchemaElement attribute = new SchemaElement(SchemaConstants.QNAME_ATTRIBUTE);
        attribute.addAttribute("ref", SOAPConstants.QNAME_ATTR_ARRAY_TYPE);
        restriction.addChild(attribute);
        complexContent.addChild(restriction);
        complexType.addChild(complexContent);
        val$schema.defineEntity(complexType, SchemaKinds.XSD_TYPE, type.getName());
        SchemaAttribute wsdlArrayTypeAttribute = new SchemaAttribute(WSDLConstants.QNAME_ATTR_ARRAY_TYPE.getLocalPart());
        wsdlArrayTypeAttribute.setNamespaceURI(WSDLConstants.QNAME_ATTR_ARRAY_TYPE.getNamespaceURI());
        String arrayTypeString = attribute.asString(type.getElementType().getName()) + "[]";
        wsdlArrayTypeAttribute.setValue(arrayTypeString);
        attribute.addAttribute(wsdlArrayTypeAttribute);
        WSDLTypeGenerator.access$000(this$0, type.getElementType());
    }

    public void visit(SOAPCustomType type) throws Exception {
        SchemaElement complexType = new SchemaElement(SchemaConstants.QNAME_COMPLEX_TYPE);
        val$schema.getContent().addChild(complexType);
        complexType.addAttribute("name", type.getName().getLocalPart());
        SchemaElement complexContent = new SchemaElement(SchemaConstants.QNAME_COMPLEX_CONTENT);
        SchemaElement restriction = new SchemaElement(SchemaConstants.QNAME_ANY);
        complexContent.addChild(restriction);
        complexType.addChild(complexContent);
        val$schema.defineEntity(complexType, SchemaKinds.XSD_TYPE, type.getName());
    }

    public void visit(SOAPEnumerationType type) throws Exception {
        SchemaElement simpleType = new SchemaElement(SchemaConstants.QNAME_SIMPLE_TYPE);
        val$schema.getContent().addChild(simpleType);
        simpleType.addAttribute("name", type.getName().getLocalPart());
        SchemaElement restriction = new SchemaElement(SchemaConstants.QNAME_RESTRICTION);
        restriction.addAttribute("base", type.getBaseType().getName());
        JavaEnumerationType javaType = (JavaEnumerationType)type.getJavaType();
        SchemaElement enumeration;
        for(Iterator iter = javaType.getEntries(); iter.hasNext(); restriction.addChild(enumeration)) {
            JavaEnumerationEntry entry = (JavaEnumerationEntry)iter.next();
            enumeration = new SchemaElement(SchemaConstants.QNAME_ENUMERATION);
            enumeration.addAttribute("value", entry.getLiteralValue());
        }

        simpleType.addChild(restriction);
        val$schema.defineEntity(simpleType, SchemaKinds.XSD_TYPE, type.getName());
    }

    public void visit(SOAPSimpleType type) throws Exception {
        if(type.getName().getNamespaceURI().equals("http://java.sun.com/jax-rpc-ri/internal")) {
            SchemaElement simpleType = new SchemaElement(SchemaConstants.QNAME_SIMPLE_TYPE);
            val$schema.getContent().addChild(simpleType);
            simpleType.addAttribute("name", type.getName().getLocalPart());
            SchemaElement restriction = new SchemaElement(SchemaConstants.QNAME_RESTRICTION);
            QName baseType = SchemaConstants.QNAME_TYPE_BASE64_BINARY;
            if(type.getName().equals(AttachmentConstants.QNAME_TYPE_SOURCE))
                baseType = SchemaConstants.QNAME_TYPE_STRING;
            restriction.addAttribute("base", baseType);
            simpleType.addChild(restriction);
            val$schema.defineEntity(simpleType, SchemaKinds.XSD_TYPE, type.getName());
        }
    }

    public void visit(SOAPAnyType soapanytype) throws Exception {
    }

    public void visit(SOAPOrderedStructureType type) throws Exception {
        visit(((SOAPStructureType) (type)));
    }

    public void visit(SOAPUnorderedStructureType type) throws Exception {
        visit(((SOAPStructureType) (type)));
    }

    public void visit(RPCRequestOrderedStructureType type) throws Exception {
        SOAPStructureMember member;
        for(Iterator iter = type.getMembers(); iter.hasNext(); WSDLTypeGenerator.access$000(this$0, member.getType()))
            member = (SOAPStructureMember)iter.next();

    }

    public void visit(RPCRequestUnorderedStructureType type) throws Exception {
        SOAPStructureMember member;
        for(Iterator iter = type.getMembers(); iter.hasNext(); WSDLTypeGenerator.access$000(this$0, member.getType()))
            member = (SOAPStructureMember)iter.next();

    }

    public void visit(RPCResponseStructureType type) throws Exception {
        SOAPStructureMember member;
        for(Iterator iter = type.getMembers(); iter.hasNext(); WSDLTypeGenerator.access$000(this$0, member.getType()))
            member = (SOAPStructureMember)iter.next();

    }

    protected void visit(SOAPStructureType type) throws Exception {
        SchemaElement complexType = new SchemaElement(SchemaConstants.QNAME_COMPLEX_TYPE);
        val$schema.getContent().addChild(complexType);
        complexType.addAttribute("name", type.getName().getLocalPart());
        SchemaElement sequence = new SchemaElement(SchemaConstants.QNAME_SEQUENCE);
        SchemaElement element;
        for(Iterator iter = type.getMembers(); iter.hasNext(); sequence.addChild(element)) {
            SOAPStructureMember member = (SOAPStructureMember)iter.next();
            element = new SchemaElement(SchemaConstants.QNAME_ELEMENT);
            element.addAttribute("name", member.getName().getLocalPart());
            element.addAttribute("type", member.getType().getName());
        }

        complexType.addChild(sequence);
        val$schema.defineEntity(complexType, SchemaKinds.XSD_TYPE, type.getName());
        SOAPStructureMember member;
        for(Iterator iter = type.getMembers(); iter.hasNext(); WSDLTypeGenerator.access$000(this$0, member.getType()))
            member = (SOAPStructureMember)iter.next();

    }
}
