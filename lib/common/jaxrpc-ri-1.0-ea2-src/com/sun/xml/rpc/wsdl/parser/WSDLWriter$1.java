// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   WSDLWriter.java

package com.sun.xml.rpc.wsdl.parser;

import com.sun.xml.rpc.wsdl.document.*;
import com.sun.xml.rpc.wsdl.document.schema.SchemaKinds;
import com.sun.xml.rpc.wsdl.framework.*;
import java.util.Map;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.parser:
//            ExtensionHandler, WSDLWriter

class WSDLWriter$1
    implements WSDLDocumentVisitor {

    private final WriterContext val$context; /* synthetic field */
    private final WSDLDocument val$document; /* synthetic field */
    private final WSDLWriter this$0; /* synthetic field */

    WSDLWriter$1(WSDLWriter this$0, WriterContext val$context, WSDLDocument val$document) {
        this.this$0 = this$0;
        this.val$context = val$context;
        this.val$document = val$document;
    }

    public void preVisit(Definitions definitions) throws Exception {
        val$context.push();
        WSDLWriter.access$000(this$0, val$context, val$document);
        val$context.writeStartTag(definitions.getElementName());
        val$context.writeAttribute("name", definitions.getName());
        val$context.writeAttribute("targetNamespace", definitions.getTargetNamespaceURI());
        val$context.writeAllPendingNamespaceDeclarations();
    }

    public void postVisit(Definitions definitions) throws Exception {
        val$context.writeEndTag(definitions.getElementName());
        val$context.pop();
    }

    public void visit(Import i) throws Exception {
        val$context.writeStartTag(i.getElementName());
        val$context.writeAttribute("namespace", i.getNamespace());
        val$context.writeAttribute("location", i.getLocation());
        val$context.writeEndTag(i.getElementName());
    }

    public void preVisit(Types types) throws Exception {
        val$context.writeStartTag(types.getElementName());
    }

    public void postVisit(Types types) throws Exception {
        val$context.writeEndTag(types.getElementName());
    }

    public void preVisit(Message message) throws Exception {
        val$context.writeStartTag(message.getElementName());
        val$context.writeAttribute("name", message.getName());
    }

    public void postVisit(Message message) throws Exception {
        val$context.writeEndTag(message.getElementName());
    }

    public void visit(MessagePart part) throws Exception {
        val$context.writeStartTag(part.getElementName());
        val$context.writeAttribute("name", part.getName());
        QName dname = part.getDescriptor();
        com.sun.xml.rpc.wsdl.framework.Kind dkind = part.getDescriptorKind();
        if(dname != null && dkind != null)
            if(dkind.equals(SchemaKinds.XSD_ELEMENT))
                val$context.writeAttribute("element", dname);
            else
            if(dkind.equals(SchemaKinds.XSD_TYPE))
                val$context.writeAttribute("type", dname);
        val$context.writeEndTag(part.getElementName());
    }

    public void preVisit(PortType portType) throws Exception {
        val$context.writeStartTag(portType.getElementName());
        val$context.writeAttribute("name", portType.getName());
    }

    public void postVisit(PortType portType) throws Exception {
        val$context.writeEndTag(portType.getElementName());
    }

    public void preVisit(Operation operation) throws Exception {
        val$context.writeStartTag(operation.getElementName());
        val$context.writeAttribute("name", operation.getName());
        val$context.writeAttribute("parameterOrder", operation.getParameterOrder());
    }

    public void postVisit(Operation operation) throws Exception {
        val$context.writeEndTag(operation.getElementName());
    }

    public void preVisit(Input input) throws Exception {
        val$context.writeStartTag(input.getElementName());
        val$context.writeAttribute("name", input.getName());
        val$context.writeAttribute("message", input.getMessage());
    }

    public void postVisit(Input input) throws Exception {
        val$context.writeEndTag(input.getElementName());
    }

    public void preVisit(Output output) throws Exception {
        val$context.writeStartTag(output.getElementName());
        val$context.writeAttribute("name", output.getName());
        val$context.writeAttribute("message", output.getMessage());
    }

    public void postVisit(Output output) throws Exception {
        val$context.writeEndTag(output.getElementName());
    }

    public void preVisit(Fault fault) throws Exception {
        val$context.writeStartTag(fault.getElementName());
        val$context.writeAttribute("name", fault.getName());
        val$context.writeAttribute("message", fault.getMessage());
    }

    public void postVisit(Fault fault) throws Exception {
        val$context.writeEndTag(fault.getElementName());
    }

    public void preVisit(Binding binding) throws Exception {
        val$context.writeStartTag(binding.getElementName());
        val$context.writeAttribute("name", binding.getName());
        val$context.writeAttribute("type", binding.getPortType());
    }

    public void postVisit(Binding binding) throws Exception {
        val$context.writeEndTag(binding.getElementName());
    }

    public void preVisit(BindingOperation operation) throws Exception {
        val$context.writeStartTag(operation.getElementName());
        val$context.writeAttribute("name", operation.getName());
    }

    public void postVisit(BindingOperation operation) throws Exception {
        val$context.writeEndTag(operation.getElementName());
    }

    public void preVisit(BindingInput input) throws Exception {
        val$context.writeStartTag(input.getElementName());
        val$context.writeAttribute("name", input.getName());
    }

    public void postVisit(BindingInput input) throws Exception {
        val$context.writeEndTag(input.getElementName());
    }

    public void preVisit(BindingOutput output) throws Exception {
        val$context.writeStartTag(output.getElementName());
        val$context.writeAttribute("name", output.getName());
    }

    public void postVisit(BindingOutput output) throws Exception {
        val$context.writeEndTag(output.getElementName());
    }

    public void preVisit(BindingFault fault) throws Exception {
        val$context.writeStartTag(fault.getElementName());
        val$context.writeAttribute("name", fault.getName());
    }

    public void postVisit(BindingFault fault) throws Exception {
        val$context.writeEndTag(fault.getElementName());
    }

    public void preVisit(Service service) throws Exception {
        val$context.writeStartTag(service.getElementName());
        val$context.writeAttribute("name", service.getName());
    }

    public void postVisit(Service service) throws Exception {
        val$context.writeEndTag(service.getElementName());
    }

    public void preVisit(Port port) throws Exception {
        val$context.writeStartTag(port.getElementName());
        val$context.writeAttribute("name", port.getName());
        val$context.writeAttribute("binding", port.getBinding());
    }

    public void postVisit(Port port) throws Exception {
        val$context.writeEndTag(port.getElementName());
    }

    public void preVisit(Extension extension) throws Exception {
        ExtensionHandler h = (ExtensionHandler)WSDLWriter.access$100(this$0).get(extension.getElementName().getNamespaceURI());
        h.doHandleExtension(val$context, extension);
    }

    public void postVisit(Extension extension1) throws Exception {
    }

    public void visit(Documentation documentation) throws Exception {
        val$context.writeTag(WSDLConstants.QNAME_DOCUMENTATION, null);
    }
}
