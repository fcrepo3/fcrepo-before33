// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   WSDLDocumentVisitor.java

package com.sun.xml.rpc.wsdl.document;

import com.sun.xml.rpc.wsdl.framework.ExtensionVisitor;

// Referenced classes of package com.sun.xml.rpc.wsdl.document:
//            Definitions, Import, Types, Message, 
//            MessagePart, PortType, Operation, Input, 
//            Output, Fault, Binding, BindingOperation, 
//            BindingInput, BindingOutput, BindingFault, Service, 
//            Port, Documentation

public interface WSDLDocumentVisitor
    extends ExtensionVisitor {

    public abstract void preVisit(Definitions definitions) throws Exception;

    public abstract void postVisit(Definitions definitions) throws Exception;

    public abstract void visit(Import import1) throws Exception;

    public abstract void preVisit(Types types) throws Exception;

    public abstract void postVisit(Types types) throws Exception;

    public abstract void preVisit(Message message) throws Exception;

    public abstract void postVisit(Message message) throws Exception;

    public abstract void visit(MessagePart messagepart) throws Exception;

    public abstract void preVisit(PortType porttype) throws Exception;

    public abstract void postVisit(PortType porttype) throws Exception;

    public abstract void preVisit(Operation operation) throws Exception;

    public abstract void postVisit(Operation operation) throws Exception;

    public abstract void preVisit(Input input) throws Exception;

    public abstract void postVisit(Input input) throws Exception;

    public abstract void preVisit(Output output) throws Exception;

    public abstract void postVisit(Output output) throws Exception;

    public abstract void preVisit(Fault fault) throws Exception;

    public abstract void postVisit(Fault fault) throws Exception;

    public abstract void preVisit(Binding binding) throws Exception;

    public abstract void postVisit(Binding binding) throws Exception;

    public abstract void preVisit(BindingOperation bindingoperation) throws Exception;

    public abstract void postVisit(BindingOperation bindingoperation) throws Exception;

    public abstract void preVisit(BindingInput bindinginput) throws Exception;

    public abstract void postVisit(BindingInput bindinginput) throws Exception;

    public abstract void preVisit(BindingOutput bindingoutput) throws Exception;

    public abstract void postVisit(BindingOutput bindingoutput) throws Exception;

    public abstract void preVisit(BindingFault bindingfault) throws Exception;

    public abstract void postVisit(BindingFault bindingfault) throws Exception;

    public abstract void preVisit(Service service) throws Exception;

    public abstract void postVisit(Service service) throws Exception;

    public abstract void preVisit(Port port) throws Exception;

    public abstract void postVisit(Port port) throws Exception;

    public abstract void visit(Documentation documentation) throws Exception;
}
