// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLModelWriter.java

package com.sun.xml.rpc.processor.util;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.*;
import com.sun.xml.rpc.processor.model.java.*;
import com.sun.xml.rpc.processor.model.literal.*;
import com.sun.xml.rpc.processor.model.soap.*;
import com.sun.xml.rpc.streaming.PrefixFactoryImpl;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;
import java.io.OutputStream;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.util:
//            PrettyPrintingXMLWriterFactoryImpl, XMLModelConstants

public class XMLModelWriter extends ExtendedModelVisitor
    implements ProcessorAction, SOAPTypeVisitor, LiteralTypeVisitor, XMLModelConstants {

    private Set _visitedComplexTypes;
    private XMLWriter _writer;

    public XMLModelWriter(XMLWriter w) {
        _writer = w;
    }

    public XMLModelWriter(OutputStream out) {
        _writer = (new PrettyPrintingXMLWriterFactoryImpl()).createXMLWriter(out);
        _writer.setPrefixFactory(new PrefixFactoryImpl("ns"));
    }

    public void write(Model model) {
        try {
            _visitedComplexTypes = new HashSet();
            visit(model);
            _writer.close();
        }
        catch(Exception exception) { }
        finally {
            _visitedComplexTypes = null;
        }
    }

    public void perform(Model model, Configuration config, Properties options) {
        write(model);
    }

    protected void preVisit(Model model) throws Exception {
        _writer.startElement(XMLModelConstants.QNAME_MODEL);
        _writer.writeNamespaceDeclaration("soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
        _writer.writeNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(model.getName()));
        _writer.writeAttribute(XMLModelConstants.ATTR_TARGET_NAMESPACE, model.getTargetNamespaceURI());
    }

    protected void postVisit(Model model) throws Exception {
        _writer.endElement();
    }

    protected void preVisit(Service service) throws Exception {
        _writer.startElement(XMLModelConstants.QNAME_SERVICE);
        _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(service.getName()));
        _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_INTERFACE_NAME, service.getJavaInterface().getName());
    }

    protected void postVisit(Service service) throws Exception {
        _writer.endElement();
    }

    protected void preVisit(Port port) throws Exception {
        _writer.startElement(XMLModelConstants.QNAME_PORT);
        _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(port.getName()));
        _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_INTERFACE_NAME, port.getJavaInterface().getName());
        if(port.getAddress() != null)
            _writer.writeAttribute(XMLModelConstants.ATTR_ADDRESS, port.getAddress());
    }

    protected void postVisit(Port port) throws Exception {
        _writer.endElement();
    }

    protected void preVisit(Operation operation) throws Exception {
        _writer.startElement(XMLModelConstants.QNAME_OPERATION);
        _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(operation.getName()));
        _writer.writeAttribute(XMLModelConstants.ATTR_OVERLOADED, getStringFor(operation.isOverloaded()));
        _writer.writeAttribute(XMLModelConstants.ATTR_UNIQUE_NAME, operation.getUniqueName());
        if(operation.getStyle() != null)
            if(operation.getStyle().equals(SOAPStyle.RPC))
                _writer.writeAttribute(XMLModelConstants.ATTR_STYLE, "rpc");
            else
            if(operation.getStyle().equals(SOAPStyle.DOCUMENT))
                _writer.writeAttribute(XMLModelConstants.ATTR_STYLE, "document");
        if(operation.getSOAPAction() != null)
            _writer.writeAttribute(XMLModelConstants.ATTR_SOAP_ACTION, operation.getSOAPAction());
    }

    protected void postVisit(Operation operation) throws Exception {
        _writer.endElement();
    }

    protected void preVisit(Request request) throws Exception {
        _writer.startElement(XMLModelConstants.QNAME_REQUEST);
    }

    protected void postVisit(Request request) throws Exception {
        _writer.endElement();
    }

    protected void preVisit(Response response) throws Exception {
        _writer.startElement(XMLModelConstants.QNAME_RESPONSE);
    }

    protected void postVisit(Response response) throws Exception {
        _writer.endElement();
    }

    protected void preVisit(Fault fault) throws Exception {
        _writer.startElement(XMLModelConstants.QNAME_FAULT);
        _writer.writeAttribute(XMLModelConstants.ATTR_NAME, fault.getName());
        _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_EXCEPTION_NAME, fault.getJavaException().getName());
    }

    protected void postVisit(Fault fault) throws Exception {
        _writer.endElement();
    }

    protected void visitBodyBlock(Block block) throws Exception {
        visitBlock(block, XMLModelConstants.QNAME_BODY_BLOCK);
    }

    protected void visitHeaderBlock(Block block) throws Exception {
        visitBlock(block, XMLModelConstants.QNAME_HEADER_BLOCK);
    }

    protected void visitFaultBlock(Block block) throws Exception {
        visitBlock(block, XMLModelConstants.QNAME_FAULT_BLOCK);
    }

    protected void visitBlock(Block block, QName name) throws Exception {
        _writer.startElement(name);
        _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(block.getName()));
        if(block.getType().isLiteralType())
            describe((LiteralType)block.getType());
        else
        if(block.getType().isSOAPType())
            describe((SOAPType)block.getType());
        _writer.endElement();
    }

    protected void visit(Parameter parameter) throws Exception {
        _writer.startElement(XMLModelConstants.QNAME_PARAMETER);
        _writer.writeAttribute(XMLModelConstants.ATTR_NAME, parameter.getName());
        _writer.writeAttribute(XMLModelConstants.ATTR_BLOCK_NAME, getStringFor(parameter.getBlock().getName()));
        _writer.writeAttribute(XMLModelConstants.ATTR_EMBEDDED, getStringFor(parameter.isEmbedded()));
        if(parameter.getJavaParameter() != null && parameter.getJavaParameter().getName() != null)
            _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_NAME, parameter.getJavaParameter().getName());
        if(parameter.getType().isLiteralType())
            describe((LiteralType)parameter.getType());
        else
        if(parameter.getType().isSOAPType())
            describe((SOAPType)parameter.getType());
        _writer.endElement();
    }

    private void describe(LiteralType type) throws Exception {
        type.accept(this);
    }

    public void visit(LiteralSimpleType type) throws Exception {
        _writer.startElement(XMLModelConstants.QNAME_LITERAL_SIMPLE_TYPE);
        _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(type.getName()));
        _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_TYPE_NAME, type.getJavaType().getName());
        _writer.endElement();
    }

    public void visit(LiteralSequenceType type) throws Exception {
        visitLiteralStructuredType(type, XMLModelConstants.QNAME_LITERAL_SEQUENCE_TYPE, true);
    }

    public void visit(LiteralAllType type) throws Exception {
        visitLiteralStructuredType(type, XMLModelConstants.QNAME_LITERAL_ALL_TYPE, true);
    }

    private void visitLiteralStructuredType(LiteralStructuredType type, QName name, boolean detailed) throws Exception {
        boolean alreadySeen = _visitedComplexTypes.contains(type);
        _writer.startElement(name);
        if(alreadySeen) {
            _writer.writeAttribute(XMLModelConstants.ATTR_REF, getStringFor(type.getName()));
        } else {
            _visitedComplexTypes.add(type);
            _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(type.getName()));
            _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_TYPE_NAME, type.getJavaType().getName());
            if(detailed) {
                for(Iterator iter = type.getAttributeMembers(); iter.hasNext(); _writer.endElement()) {
                    LiteralAttributeMember attribute = (LiteralAttributeMember)iter.next();
                    _writer.startElement(XMLModelConstants.QNAME_ATTRIBUTE);
                    _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(attribute.getName()));
                    _writer.writeAttribute(XMLModelConstants.ATTR_REQUIRED, getStringFor(attribute.isRequired()));
                    _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_NAME, attribute.getJavaStructureMember().getName());
                    describe(attribute.getType());
                }

                for(Iterator iter = type.getElementMembers(); iter.hasNext(); _writer.endElement()) {
                    LiteralElementMember element = (LiteralElementMember)iter.next();
                    _writer.startElement(XMLModelConstants.QNAME_ELEMENT);
                    _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(element.getName()));
                    _writer.writeAttribute(XMLModelConstants.ATTR_NILLABLE, getStringFor(element.isNillable()));
                    _writer.writeAttribute(XMLModelConstants.ATTR_REQUIRED, getStringFor(element.isRequired()));
                    _writer.writeAttribute(XMLModelConstants.ATTR_REPEATED, getStringFor(element.isRepeated()));
                    _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_NAME, element.getJavaStructureMember().getName());
                    describe(element.getType());
                }

            }
        }
        _writer.endElement();
    }

    public void visit(LiteralArrayType type) throws Exception {
        _writer.startElement(XMLModelConstants.QNAME_LITERAL_ARRAY_TYPE);
        _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(type.getName()));
        _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_TYPE_NAME, type.getJavaType().getName());
        describe(type.getElementType());
        _writer.endElement();
    }

    public void visit(LiteralFragmentType type) throws Exception {
        _writer.startElement(XMLModelConstants.QNAME_LITERAL_FRAGMENT_TYPE);
        _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(type.getName()));
        _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_TYPE_NAME, type.getJavaType().getName());
        _writer.endElement();
    }

    private void describe(SOAPType type) throws Exception {
        type.accept(this);
    }

    public void visit(SOAPArrayType type) throws Exception {
        _writer.startElement(XMLModelConstants.QNAME_SOAP_ARRAY_TYPE);
        _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(type.getName()));
        _writer.writeAttribute(XMLModelConstants.ATTR_RANK, Integer.toString(type.getRank()));
        _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_TYPE_NAME, type.getJavaType().getName());
        describe(type.getElementType());
        _writer.endElement();
    }

    public void visit(SOAPCustomType type) throws Exception {
        _writer.startElement(XMLModelConstants.QNAME_SOAP_CUSTOM_TYPE);
        _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(type.getName()));
        _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_TYPE_NAME, type.getJavaType().getName());
        _writer.endElement();
    }

    public void visit(SOAPEnumerationType type) throws Exception {
        _writer.startElement(XMLModelConstants.QNAME_SOAP_ENUMERATION_TYPE);
        _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(type.getName()));
        _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_TYPE_NAME, type.getJavaType().getName());
        describe(type.getBaseType());
        _writer.endElement();
    }

    public void visit(SOAPSimpleType type) throws Exception {
        if(type.getName() != null) {
            _writer.startElement(XMLModelConstants.QNAME_SOAP_SIMPLE_TYPE);
            _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(type.getName()));
            _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_TYPE_NAME, type.getJavaType().getName());
            _writer.endElement();
        } else {
            _writer.startElement(XMLModelConstants.QNAME_VOID_TYPE);
            _writer.endElement();
        }
    }

    public void visit(SOAPAnyType type) throws Exception {
        _writer.startElement(XMLModelConstants.QNAME_SOAP_ANY_TYPE);
        _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(type.getName()));
        _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_TYPE_NAME, type.getJavaType().getName());
        _writer.endElement();
    }

    public void visit(SOAPOrderedStructureType type) throws Exception {
        visitSOAPStructureType(type, XMLModelConstants.QNAME_SOAP_ORDERED_STRUCTURE_TYPE, true);
    }

    public void visit(SOAPUnorderedStructureType type) throws Exception {
        visitSOAPStructureType(type, XMLModelConstants.QNAME_SOAP_UNORDERED_STRUCTURE_TYPE, true);
    }

    public void visit(RPCRequestOrderedStructureType type) throws Exception {
        visitSOAPStructureType(type, XMLModelConstants.QNAME_RPC_REQUEST_ORDERED_STRUCTURE_TYPE, true);
    }

    public void visit(RPCRequestUnorderedStructureType type) throws Exception {
        visitSOAPStructureType(type, XMLModelConstants.QNAME_RPC_REQUEST_UNORDERED_STRUCTURE_TYPE, true);
    }

    public void visit(RPCResponseStructureType type) throws Exception {
        visitSOAPStructureType(type, XMLModelConstants.QNAME_RPC_RESPONSE_STRUCTURE_TYPE, true);
    }

    private void visitSOAPStructureType(SOAPStructureType type, QName name, boolean detailed) throws Exception {
        boolean alreadySeen = _visitedComplexTypes.contains(type);
        _writer.startElement(name);
        if(alreadySeen) {
            _writer.writeAttribute(XMLModelConstants.ATTR_REF, getStringFor(type.getName()));
        } else {
            _visitedComplexTypes.add(type);
            _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(type.getName()));
            _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_TYPE_NAME, type.getJavaType().getName());
            if(detailed) {
                for(Iterator iter = type.getMembers(); iter.hasNext(); _writer.endElement()) {
                    SOAPStructureMember member = (SOAPStructureMember)iter.next();
                    _writer.startElement(XMLModelConstants.QNAME_MEMBER);
                    _writer.writeAttribute(XMLModelConstants.ATTR_NAME, getStringFor(member.getName()));
                    if(member.getJavaStructureMember() != null)
                        _writer.writeAttribute(XMLModelConstants.ATTR_JAVA_NAME, member.getJavaStructureMember().getName());
                    describe(member.getType());
                }

            }
        }
        _writer.endElement();
    }

    private String getStringFor(QName name) {
        String uri = name.getNamespaceURI();
        if(uri.equals(""))
            return name.getLocalPart();
        String prefix = _writer.getPrefix(uri);
        if(prefix == null) {
            _writer.writeNamespaceDeclaration(name.getNamespaceURI());
            prefix = _writer.getPrefix(uri);
        }
        return prefix + ":" + name.getLocalPart();
    }

    private String getStringFor(boolean b) {
        return b ? "true" : "false";
    }
}
