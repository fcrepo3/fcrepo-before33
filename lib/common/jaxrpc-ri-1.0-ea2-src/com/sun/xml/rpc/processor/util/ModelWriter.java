// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ModelWriter.java

package com.sun.xml.rpc.processor.util;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.*;
import com.sun.xml.rpc.processor.model.java.JavaInterface;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.*;
import com.sun.xml.rpc.processor.model.soap.*;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;
import java.io.*;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.util:
//            ComponentWriter, IndentingWriter

public class ModelWriter extends ExtendedModelVisitor
    implements ProcessorAction, SOAPTypeVisitor, LiteralTypeVisitor {

    private IndentingWriter _writer;
    private ComponentWriter _componentWriter;
    private String _currentNamespaceURI;
    private Set _visitedComplexTypes;
    private static final boolean writeComponentInformation = false;

    public ModelWriter(IndentingWriter w) {
        _writer = w;
        _componentWriter = new ComponentWriter(_writer);
    }

    public ModelWriter(OutputStream out) {
        this(new IndentingWriter(new OutputStreamWriter(out), 2));
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
            _currentNamespaceURI = null;
        }
    }

    public void perform(Model model, Configuration config, Properties options) {
        write(model);
    }

    protected void preVisit(Model model) throws Exception {
        _writer.p("MODEL ");
        writeQName(model.getName());
        _writer.pln();
        _writer.pI();
        _currentNamespaceURI = model.getTargetNamespaceURI();
        if(_currentNamespaceURI != null) {
            _writer.p("TARGET-NAMESPACE ");
            _writer.pln(_currentNamespaceURI);
        }
    }

    protected void postVisit(Model model) throws Exception {
        _writer.pO();
    }

    protected void preVisit(Service service) throws Exception {
        _writer.p("SERVICE ");
        writeQName(service.getName());
        _writer.p(" INTERFACE ");
        _writer.p(service.getJavaInterface().getName());
        _writer.pln();
        _writer.pI();
        _currentNamespaceURI = service.getName().getNamespaceURI();
    }

    protected void postVisit(Service service) throws Exception {
        _writer.pO();
    }

    protected void preVisit(Port port) throws Exception {
        _writer.p("PORT ");
        writeQName(port.getName());
        _writer.p(" INTERFACE ");
        _writer.p(port.getJavaInterface().getName());
        _writer.pln();
        _writer.pI();
        _currentNamespaceURI = port.getName().getNamespaceURI();
    }

    protected void postVisit(Port port) throws Exception {
        _writer.pO();
    }

    protected void preVisit(Operation operation) throws Exception {
        _writer.p("OPERATION ");
        writeQName(operation.getName());
        if(operation.isOverloaded())
            _writer.p(" (OVERLOADED)");
        if(operation.getStyle() != null)
            if(operation.getStyle().equals(SOAPStyle.RPC))
                _writer.p(" (RPC)");
            else
            if(operation.getStyle().equals(SOAPStyle.DOCUMENT))
                _writer.p(" (DOCUMENT)");
        _writer.pln();
        _writer.pI();
    }

    protected void postVisit(Operation operation) throws Exception {
        _writer.pO();
    }

    protected void preVisit(Request request) throws Exception {
        _writer.plnI("REQUEST");
    }

    protected void postVisit(Request request) throws Exception {
        _writer.pO();
    }

    protected void preVisit(Response response) throws Exception {
        _writer.plnI("RESPONSE");
    }

    protected void postVisit(Response response) throws Exception {
        _writer.pO();
    }

    protected void preVisit(Fault fault) throws Exception {
        _writer.p("FAULT ");
        _writer.p(fault.getName());
        _writer.pln();
        _writer.pI();
    }

    protected void postVisit(Fault fault) throws Exception {
        _writer.pO();
    }

    protected void visitBodyBlock(Block block) throws Exception {
        _writer.p("BODY-BLOCK ");
        writeQName(block.getName());
        _writer.p(" TYPE ");
        writeQName(block.getType().getName());
        if(block.getType().isLiteralType()) {
            _writer.pln(" (LITERAL)");
            describe((LiteralType)block.getType());
        } else
        if(block.getType().isSOAPType()) {
            _writer.pln(" (ENCODED)");
            describe((SOAPType)block.getType());
        }
    }

    protected void visitHeaderBlock(Block block) throws Exception {
        _writer.p("HEADER-BLOCK ");
        writeQName(block.getName());
        _writer.p(" TYPE ");
        writeQName(block.getType().getName());
        if(block.getType().isLiteralType()) {
            _writer.pln(" (LITERAL)");
            describe((LiteralType)block.getType());
        } else
        if(block.getType().isSOAPType()) {
            _writer.pln(" (ENCODED)");
            describe((SOAPType)block.getType());
        }
    }

    protected void visitFaultBlock(Block block) throws Exception {
        _writer.p("FAULT-BLOCK ");
        writeQName(block.getName());
        _writer.p(" TYPE ");
        writeQName(block.getType().getName());
        if(block.getType().isLiteralType()) {
            _writer.pln(" (LITERAL)");
            describe((LiteralType)block.getType());
        } else
        if(block.getType().isSOAPType()) {
            _writer.pln(" (ENCODED)");
            describe((SOAPType)block.getType());
        }
    }

    protected void visit(Parameter parameter) throws Exception {
        _writer.p("PARAMETER ");
        _writer.p(parameter.getName());
        _writer.p(" TYPE ");
        writeQName(parameter.getType().getName());
        if(parameter.isEmbedded())
            _writer.p(" (EMBEDDED)");
        if(parameter.getType().isLiteralType()) {
            _writer.pln(" (LITERAL)");
            describe((LiteralType)parameter.getType());
        } else
        if(parameter.getType().isSOAPType()) {
            _writer.pln(" (ENCODED)");
            describe((SOAPType)parameter.getType());
        }
    }

    private void describe(LiteralType type) throws Exception {
        _writer.pI();
        type.accept(this);
        _writer.pO();
    }

    public void visit(LiteralSimpleType type) throws Exception {
        _writer.p("LITERAL-SIMPLE-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
    }

    public void visit(LiteralSequenceType type) throws Exception {
        visitLiteralStructuredType(type, "LITERAL-SEQUENCE-TYPE ", true);
    }

    public void visit(LiteralAllType type) throws Exception {
        visitLiteralStructuredType(type, "LITERAL-ALL-TYPE ", true);
    }

    private void visitLiteralStructuredType(LiteralStructuredType type, String header, boolean detailed) throws Exception {
        boolean alreadySeen = _visitedComplexTypes.contains(type);
        _writer.p(header);
        writeQName(type.getName());
        if(alreadySeen)
            _writer.p(" (REF)");
        else
            _visitedComplexTypes.add(type);
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
        if(alreadySeen)
            return;
        if(detailed) {
            _writer.pI();
            LiteralAttributeMember attribute;
            for(Iterator iter = type.getAttributeMembers(); iter.hasNext(); describe(attribute.getType())) {
                attribute = (LiteralAttributeMember)iter.next();
                _writer.p("ATTRIBUTE ");
                _writer.p(attribute.getName().getLocalPart());
                if(attribute.isRequired())
                    _writer.p(" (REQUIRED)");
                _writer.pln();
            }

            LiteralElementMember element;
            for(Iterator iter = type.getElementMembers(); iter.hasNext(); describe(element.getType())) {
                element = (LiteralElementMember)iter.next();
                _writer.p("ELEMENT ");
                _writer.p(element.getName().getLocalPart());
                if(element.isNillable())
                    _writer.p(" (NILLABLE)");
                if(element.isRequired())
                    _writer.p(" (REQUIRED)");
                if(element.isRepeated())
                    _writer.p(" (REPEATED)");
                _writer.pln();
            }

            _writer.pO();
        }
    }

    public void visit(LiteralArrayType type) throws Exception {
        _writer.p("LITERAL-ARRAY-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
        describe(type.getElementType());
    }

    public void visit(LiteralFragmentType type) throws Exception {
        _writer.p("LITERAL-FRAGMENT-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
    }

    private void describe(SOAPType type) throws Exception {
        _writer.pI();
        type.accept(this);
        _writer.pO();
    }

    public void visit(SOAPArrayType type) throws Exception {
        _writer.p("SOAP-ARRAY-TYPE ");
        writeQName(type.getName());
        _writer.p(" RANK ");
        _writer.p(Integer.toString(type.getRank()));
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
        describe(type.getElementType());
    }

    public void visit(SOAPCustomType type) throws Exception {
        _writer.p("SOAP-CUSTOM-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
        _writer.pI();
        _writer.pO();
    }

    public void visit(SOAPEnumerationType type) throws Exception {
        _writer.p("SOAP-ENUMERATION-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
        describe(type.getBaseType());
    }

    public void visit(SOAPSimpleType type) throws Exception {
        _writer.p("SOAP-SIMPLE-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
    }

    public void visit(SOAPAnyType type) throws Exception {
        _writer.p("SOAP-ANY-TYPE ");
        writeQName(type.getName());
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
    }

    public void visit(SOAPOrderedStructureType type) throws Exception {
        visitSOAPStructureType(type, "SOAP-ORDERED-STRUCTURE-TYPE", true);
    }

    public void visit(SOAPUnorderedStructureType type) throws Exception {
        visitSOAPStructureType(type, "SOAP-UNORDERED-STRUCTURE-TYPE", true);
    }

    public void visit(RPCRequestOrderedStructureType type) throws Exception {
        visitSOAPStructureType(type, "RPC-REQUEST-ORDERED-STRUCTURE-TYPE", false);
    }

    public void visit(RPCRequestUnorderedStructureType type) throws Exception {
        visitSOAPStructureType(type, "RPC-REQUEST-UNORDERED-STRUCTURE-TYPE", false);
    }

    public void visit(RPCResponseStructureType type) throws Exception {
        visitSOAPStructureType(type, "RPC-RESPONSE-STRUCTURE-TYPE", false);
    }

    private void visitSOAPStructureType(SOAPStructureType type, String header, boolean detailed) throws Exception {
        boolean alreadySeen = _visitedComplexTypes.contains(type);
        _writer.p(header);
        _writer.p(" ");
        writeQName(type.getName());
        if(alreadySeen)
            _writer.p(" (REF)");
        else
            _visitedComplexTypes.add(type);
        _writer.p(" JAVA-TYPE ");
        _writer.p(type.getJavaType().getName());
        _writer.pln();
        if(alreadySeen)
            return;
        if(detailed) {
            _writer.pI();
            SOAPStructureMember member;
            for(Iterator iter = type.getMembers(); iter.hasNext(); describe(member.getType())) {
                member = (SOAPStructureMember)iter.next();
                _writer.p("MEMBER ");
                _writer.p(member.getName().getLocalPart());
                _writer.pln();
            }

            _writer.pO();
        }
    }

    private void writeQName(QName name) throws IOException {
        if(name == null) {
            _writer.p("null");
        } else {
            String nsURI = name.getNamespaceURI();
            if(!nsURI.equals(_currentNamespaceURI) && nsURI.length() > 0)
                if(nsURI.equals("http://schemas.xmlsoap.org/wsdl/"))
                    _writer.p("{wsdl}");
                else
                if(nsURI.equals("http://www.w3.org/2001/XMLSchema")) {
                    _writer.p("{xsd}");
                } else {
                    _writer.p("{");
                    _writer.p(nsURI);
                    _writer.p("}");
                }
            _writer.p(name.getLocalPart());
        }
    }
}
