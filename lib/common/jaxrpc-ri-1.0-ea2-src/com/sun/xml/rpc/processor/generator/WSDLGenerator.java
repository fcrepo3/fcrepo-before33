// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   WSDLGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.ProcessorAction;
import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Fault;
import com.sun.xml.rpc.processor.model.Model;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.model.Parameter;
import com.sun.xml.rpc.processor.model.Port;
import com.sun.xml.rpc.processor.model.Service;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.wsdl.document.Binding;
import com.sun.xml.rpc.wsdl.document.BindingFault;
import com.sun.xml.rpc.wsdl.document.BindingInput;
import com.sun.xml.rpc.wsdl.document.BindingOperation;
import com.sun.xml.rpc.wsdl.document.BindingOutput;
import com.sun.xml.rpc.wsdl.document.Definitions;
import com.sun.xml.rpc.wsdl.document.Input;
import com.sun.xml.rpc.wsdl.document.Message;
import com.sun.xml.rpc.wsdl.document.MessagePart;
import com.sun.xml.rpc.wsdl.document.OperationStyle;
import com.sun.xml.rpc.wsdl.document.Output;
import com.sun.xml.rpc.wsdl.document.PortType;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;
import com.sun.xml.rpc.wsdl.document.schema.SchemaKinds;
import com.sun.xml.rpc.wsdl.document.soap.SOAPAddress;
import com.sun.xml.rpc.wsdl.document.soap.SOAPBinding;
import com.sun.xml.rpc.wsdl.document.soap.SOAPBody;
import com.sun.xml.rpc.wsdl.document.soap.SOAPFault;
import com.sun.xml.rpc.wsdl.document.soap.SOAPOperation;
import com.sun.xml.rpc.wsdl.document.soap.SOAPStyle;
import com.sun.xml.rpc.wsdl.document.soap.SOAPUse;
import com.sun.xml.rpc.wsdl.framework.DuplicateEntityException;
import com.sun.xml.rpc.wsdl.framework.GlobalEntity;
import com.sun.xml.rpc.wsdl.parser.Constants;
import com.sun.xml.rpc.wsdl.parser.WSDLWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import javax.xml.rpc.namespace.QName;
import sun.tools.java.Environment;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorException, WSDLTypeGenerator

public class WSDLGenerator
    implements Constants, ProcessorAction {

    private File destDir;
    private BatchEnvironment env;
    private Model model;

    public WSDLGenerator() {
        destDir = null;
        env = null;
        model = null;
    }

    public void perform(Model model, Configuration config, Properties properties) {
        BatchEnvironment env = config.getEnvironment();
        String key = "destinationDirectory";
        String dirPath = properties.getProperty(key);
        File destDir = new File(dirPath);
        WSDLGenerator generator = new WSDLGenerator(env, destDir, model);
        generator.doGeneration();
    }

    private WSDLGenerator(BatchEnvironment env, File destDir, Model model) {
        this.env = env;
        this.model = model;
        this.destDir = destDir;
    }

    private void doGeneration() {
        try {
            doGeneration(model);
        }
        catch(Exception e) {
            throw new GeneratorException("generator.nestedGeneratorError", new LocalizableExceptionAdapter(e));
        }
    }

    private void doGeneration(Model model) throws Exception {
        File wsdlFile = new File(destDir, model.getName().getLocalPart() + ".wsdl");
        WSDLDocument document = generateDocument(model);
        try {
            WSDLWriter writer = new WSDLWriter();
            FileOutputStream fos = new FileOutputStream(wsdlFile);
            writer.write(document, fos);
            fos.close();
        }
        catch(IOException ioexception) {
            env.error(0L, "cant.write", wsdlFile);
        }
    }

    private WSDLDocument generateDocument(Model model) throws Exception {
        WSDLDocument document = new WSDLDocument();
        Definitions definitions = new Definitions(document);
        definitions.setName(model.getName().getLocalPart());
        definitions.setTargetNamespaceURI(model.getTargetNamespaceURI());
        document.setDefinitions(definitions);
        generateTypes(model, document);
        generateMessages(model, definitions);
        generatePortTypes(model, definitions);
        generateBindings(model, definitions);
        generateServices(model, definitions);
        return document;
    }

    private void generateTypes(Model model, WSDLDocument document) throws Exception {
        WSDLTypeGenerator typeGenerator = new WSDLTypeGenerator(model, document);
        typeGenerator.run();
    }

    private void generateMessages(Model model, Definitions definitions) throws Exception {
        for(Iterator services = model.getServices(); services.hasNext();) {
            Service service = (Service)services.next();
            for(Iterator ports = service.getPorts(); ports.hasNext();) {
                Port port = (Port)ports.next();
                PortType wsdlPortType = new PortType(definitions);
                wsdlPortType.setName(getPortTypeName(port.getName().getLocalPart()));
                for(Iterator operations = port.getOperations(); operations.hasNext();) {
                    Operation operation = (Operation)operations.next();
                    String localOperationName = operation.getName().getLocalPart();
                    com.sun.xml.rpc.processor.model.Request request = operation.getRequest();
                    Message wsdlRequestMessage = new Message(definitions);
                    wsdlRequestMessage.setName(getInputMessageName(localOperationName));
                    fillInMessageParts(request, wsdlRequestMessage);
                    com.sun.xml.rpc.processor.model.Response response = operation.getResponse();
                    Message wsdlResponseMessage = new Message(definitions);
                    wsdlResponseMessage.setName(getOutputMessageName(localOperationName));
                    fillInMessageParts(response, wsdlResponseMessage);
                    definitions.add(wsdlRequestMessage);
                    definitions.add(wsdlResponseMessage);
                    for(Iterator faults = operation.getFaults(); faults.hasNext();) {
                        Fault fault = (Fault)faults.next();
                        Message wsdlFaultMessage = new Message(definitions);
                        wsdlFaultMessage.setName(getFaultMessageName(fault.getName()));
                        MessagePart part = new MessagePart();
                        part.setName(fault.getBlock().getName().getLocalPart());
                        AbstractType type = fault.getBlock().getType();
                        if(type.isSOAPType()) {
                            part.setDescriptorKind(SchemaKinds.XSD_TYPE);
                            part.setDescriptor(type.getName());
                        }
                        wsdlFaultMessage.add(part);
                        try {
                            definitions.add(wsdlFaultMessage);
                        }
                        catch(DuplicateEntityException duplicateentityexception) { }
                    }

                }

            }

        }

    }

    private void fillInMessageParts(com.sun.xml.rpc.processor.model.Message message, Message wsdlMessage) throws Exception {
        for(Iterator parameters = message.getParameters(); parameters.hasNext();) {
            Parameter parameter = (Parameter)parameters.next();
            MessagePart part = new MessagePart();
            part.setName(parameter.getName());
            AbstractType type = parameter.getType();
            if(type.getName() != null) {
                if(type.isSOAPType()) {
                    part.setDescriptorKind(SchemaKinds.XSD_TYPE);
                    part.setDescriptor(type.getName());
                }
                wsdlMessage.add(part);
            }
        }

    }

    private void generatePortTypes(Model model, Definitions definitions) throws Exception {
        for(Iterator services = model.getServices(); services.hasNext();) {
            Service service = (Service)services.next();
            PortType wsdlPortType;
            for(Iterator ports = service.getPorts(); ports.hasNext(); definitions.add(wsdlPortType)) {
                Port port = (Port)ports.next();
                wsdlPortType = new PortType(definitions);
                wsdlPortType.setName(getPortTypeName(port.getName().getLocalPart()));
                com.sun.xml.rpc.wsdl.document.Operation wsdlOperation;
                for(Iterator operations = port.getOperations(); operations.hasNext(); wsdlPortType.add(wsdlOperation)) {
                    Operation operation = (Operation)operations.next();
                    String localOperationName = operation.getName().getLocalPart();
                    wsdlOperation = new com.sun.xml.rpc.wsdl.document.Operation();
                    wsdlOperation.setName(localOperationName);
                    wsdlOperation.setStyle(OperationStyle.REQUEST_RESPONSE);
                    Input input = new Input();
                    input.setMessage(new QName(model.getTargetNamespaceURI(), getInputMessageName(localOperationName)));
                    wsdlOperation.setInput(input);
                    Output output = new Output();
                    output.setMessage(new QName(model.getTargetNamespaceURI(), getOutputMessageName(localOperationName)));
                    wsdlOperation.setOutput(output);
                    com.sun.xml.rpc.wsdl.document.Fault wsdlFault;
                    for(Iterator faults = operation.getFaults(); faults.hasNext(); wsdlOperation.addFault(wsdlFault)) {
                        Fault fault = (Fault)faults.next();
                        wsdlFault = new com.sun.xml.rpc.wsdl.document.Fault();
                        wsdlFault.setName(fault.getName());
                        wsdlFault.setMessage(new QName(model.getTargetNamespaceURI(), getFaultMessageName(fault.getName())));
                    }

                }

            }

        }

    }

    private void generateBindings(Model model, Definitions definitions) throws Exception {
        for(Iterator services = model.getServices(); services.hasNext();) {
            Service service = (Service)services.next();
            Binding wsdlBinding;
            for(Iterator ports = service.getPorts(); ports.hasNext(); definitions.add(wsdlBinding)) {
                Port port = (Port)ports.next();
                boolean isMixed = false;
                SOAPStyle defaultStyle = null;
                for(Iterator operations = port.getOperations(); operations.hasNext();) {
                    Operation operation = (Operation)operations.next();
                    if(operation.getStyle() == null)
                        operation.setStyle(SOAPStyle.RPC);
                    if(defaultStyle == null)
                        defaultStyle = operation.getStyle();
                    else
                    if(defaultStyle != operation.getStyle())
                        isMixed = true;
                }

                String localPortName = port.getName().getLocalPart();
                wsdlBinding = new Binding(definitions);
                wsdlBinding.setName(getBindingName(localPortName));
                wsdlBinding.setPortType(new QName(model.getTargetNamespaceURI(), getPortTypeName(localPortName)));
                SOAPBinding soapBinding = new SOAPBinding();
                if(defaultStyle != null && !isMixed)
                    soapBinding.setStyle(defaultStyle);
                soapBinding.setTransport("http://schemas.xmlsoap.org/soap/http");
                wsdlBinding.addExtension(soapBinding);
                BindingOperation wsdlOperation;
                for(Iterator operations = port.getOperations(); operations.hasNext(); wsdlBinding.add(wsdlOperation)) {
                    Operation operation = (Operation)operations.next();
                    wsdlOperation = new BindingOperation();
                    wsdlOperation.setName(operation.getName().getLocalPart());
                    wsdlOperation.setStyle(OperationStyle.REQUEST_RESPONSE);
                    SOAPOperation soapOperation = new SOAPOperation();
                    soapOperation.setSOAPAction(operation.getSOAPAction());
                    if(!operation.getStyle().equals(defaultStyle))
                        soapOperation.setStyle(operation.getStyle());
                    wsdlOperation.addExtension(soapOperation);
                    com.sun.xml.rpc.processor.model.Request request = operation.getRequest();
                    BindingInput input = new BindingInput();
                    SOAPBody soapBody = new SOAPBody();
                    if(isBodyEmpty(request)) {
                        if(operation.getStyle() == SOAPStyle.RPC) {
                            soapBody.setUse(SOAPUse.ENCODED);
                            soapBody.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
                            soapBody.setNamespace(model.getTargetNamespaceURI());
                        } else {
                            soapBody.setUse(SOAPUse.LITERAL);
                        }
                    } else
                    if(isBodyEncoded(request)) {
                        soapBody.setUse(SOAPUse.ENCODED);
                        soapBody.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
                        soapBody.setNamespace(model.getTargetNamespaceURI());
                    } else {
                        soapBody.setUse(SOAPUse.LITERAL);
                    }
                    input.addExtension(soapBody);
                    wsdlOperation.setInput(input);
                    com.sun.xml.rpc.processor.model.Response response = operation.getResponse();
                    BindingOutput output = new BindingOutput();
                    soapBody = new SOAPBody();
                    if(isBodyEmpty(response)) {
                        if(operation.getStyle() == SOAPStyle.RPC) {
                            soapBody.setUse(SOAPUse.ENCODED);
                            soapBody.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
                            soapBody.setNamespace(model.getTargetNamespaceURI());
                        } else {
                            soapBody.setUse(SOAPUse.LITERAL);
                        }
                    } else
                    if(isBodyEncoded(response)) {
                        soapBody.setUse(SOAPUse.ENCODED);
                        soapBody.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
                        soapBody.setNamespace(model.getTargetNamespaceURI());
                    } else {
                        soapBody.setUse(SOAPUse.LITERAL);
                    }
                    output.addExtension(soapBody);
                    wsdlOperation.setOutput(output);
                    BindingFault bindingFault;
                    for(Iterator faults = operation.getFaults(); faults.hasNext(); wsdlOperation.addFault(bindingFault)) {
                        Fault fault = (Fault)faults.next();
                        bindingFault = new BindingFault();
                        bindingFault.setName(fault.getName());
                        SOAPFault soapFault = new SOAPFault();
                        if(fault.getBlock().getType().isSOAPType()) {
                            soapFault.setUse(SOAPUse.ENCODED);
                            soapFault.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
                            soapFault.setNamespace(model.getTargetNamespaceURI());
                        } else {
                            soapFault.setUse(SOAPUse.LITERAL);
                        }
                        bindingFault.addExtension(soapFault);
                    }

                }

            }

        }

    }

    private boolean isBodyEmpty(com.sun.xml.rpc.processor.model.Message message) {
        return !message.getBodyBlocks().hasNext();
    }

    private boolean isBodyEncoded(com.sun.xml.rpc.processor.model.Message message) {
        boolean isEncoded = true;
        for(Iterator iter = message.getBodyBlocks(); iter.hasNext();) {
            Block bodyBlock = (Block)iter.next();
            if(!bodyBlock.getType().isSOAPType())
                isEncoded = false;
        }

        return isEncoded;
    }

    private void generateServices(Model model, Definitions definitions) throws Exception {
        com.sun.xml.rpc.wsdl.document.Service wsdlService;
        for(Iterator services = model.getServices(); services.hasNext(); definitions.add(wsdlService)) {
            Service service = (Service)services.next();
            wsdlService = new com.sun.xml.rpc.wsdl.document.Service(definitions);
            wsdlService.setName(service.getName().getLocalPart());
            com.sun.xml.rpc.wsdl.document.Port wsdlPort;
            for(Iterator ports = service.getPorts(); ports.hasNext(); wsdlService.add(wsdlPort)) {
                Port port = (Port)ports.next();
                String localPortName = port.getName().getLocalPart();
                wsdlPort = new com.sun.xml.rpc.wsdl.document.Port(definitions);
                wsdlPort.setName(getPortName(localPortName));
                wsdlPort.setBinding(new QName(model.getTargetNamespaceURI(), getBindingName(localPortName)));
                SOAPAddress soapAddress = new SOAPAddress();
                soapAddress.setLocation(port.getAddress() != null ? port.getAddress() : "REPLACE_WITH_ACTUAL_URL");
                wsdlPort.addExtension(soapAddress);
            }

        }

    }

    private String getBaseName(String s) {
        if(s.endsWith("Port"))
            return s.substring(0, s.length() - 4);
        else
            return s;
    }

    private String getPortName(String s) {
        return getBaseName(s) + "Port";
    }

    private String getBindingName(String s) {
        return getBaseName(s) + "Binding";
    }

    private String getPortTypeName(String s) {
        return s;
    }

    private String getInputMessageName(String s) {
        return s;
    }

    private String getOutputMessageName(String s) {
        return s + "Response";
    }

    private String getFaultMessageName(String faultName) {
        return faultName;
    }
}
