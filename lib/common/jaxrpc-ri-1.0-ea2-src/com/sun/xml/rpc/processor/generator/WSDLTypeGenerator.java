// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   WSDLTypeGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.model.*;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.wsdl.document.*;
import com.sun.xml.rpc.wsdl.document.schema.*;
import java.util.*;
import javax.xml.rpc.namespace.QName;

public class WSDLTypeGenerator {

    private Model model;
    private WSDLDocument document;
    private Definitions definitions;
    private Set generatedTypes;
    private Map nsSchemaMap;

    public WSDLTypeGenerator(Model model, WSDLDocument document) {
        this.model = model;
        this.document = document;
        definitions = document.getDefinitions();
        generatedTypes = new HashSet();
        nsSchemaMap = new HashMap();
    }

    public void run() throws Exception {
        Types types = new Types();
        for(Iterator services = model.getServices(); services.hasNext();) {
            com.sun.xml.rpc.processor.model.Service service = (com.sun.xml.rpc.processor.model.Service)services.next();
            for(Iterator ports = service.getPorts(); ports.hasNext();) {
                com.sun.xml.rpc.processor.model.Port port = (com.sun.xml.rpc.processor.model.Port)ports.next();
                for(Iterator operations = port.getOperations(); operations.hasNext();) {
                    com.sun.xml.rpc.processor.model.Operation operation = (com.sun.xml.rpc.processor.model.Operation)operations.next();
                    processTypesInMessage(operation.getRequest());
                    processTypesInMessage(operation.getResponse());
                    com.sun.xml.rpc.processor.model.Fault fault;
                    for(Iterator faults = operation.getFaults(); faults.hasNext(); processType(fault.getBlock().getType()))
                        fault = (com.sun.xml.rpc.processor.model.Fault)faults.next();

                }

            }

        }

        for(Iterator iter = nsSchemaMap.values().iterator(); iter.hasNext();) {
            Schema schema = (Schema)iter.next();
            Iterator definedEntities = schema.definedEntities();
            if(definedEntities.hasNext())
                types.addExtension(schema);
        }

        definitions.setTypes(types);
    }

    private void processTypesInMessage(com.sun.xml.rpc.processor.model.Message message) throws Exception {
        if(message == null)
            return;
        AbstractType type;
        for(Iterator iter = message.getBodyBlocks(); iter.hasNext(); processType(type)) {
            Block block = (Block)iter.next();
            type = block.getType();
        }

    }

    private void processType(AbstractType type) throws Exception {
        if(type.getName() == null || generatedTypes.contains(type.getName()))
            return;
        if(type.getName().getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema") || type.getName().getNamespaceURI().equals("http://schemas.xmlsoap.org/soap/encoding/"))
            return;
        Schema schema = (Schema)nsSchemaMap.get(type.getName().getNamespaceURI());
        if(schema == null) {
            schema = new Schema(document);
            schema.setTargetNamespaceURI(type.getName().getNamespaceURI());
            SchemaElement schemaElement = new SchemaElement(SchemaConstants.QNAME_SCHEMA);
            schemaElement.addAttribute("targetNamespace", schema.getTargetNamespaceURI());
            schemaElement.addPrefix("", "http://www.w3.org/2001/XMLSchema");
            schemaElement.addPrefix("wsdl", "http://schemas.xmlsoap.org/wsdl/");
            schemaElement.addPrefix("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            schemaElement.addPrefix("soap-enc", "http://schemas.xmlsoap.org/soap/encoding/");
            schemaElement.addPrefix("tns", schema.getTargetNamespaceURI());
            schema.setContent(schemaElement);
            nsSchemaMap.put(type.getName().getNamespaceURI(), schema);
        }
        if(type.isLiteralType()) {
            throw new IllegalArgumentException();
        } else {
            generatedTypes.add(type.getName());
            processType((SOAPType)type, schema);
            return;
        }
    }

    private void processType(SOAPType type, Schema schema) throws Exception {
        type.accept(new WSDLTypeGenerator$1(this, schema));
    }

    static void access$000(WSDLTypeGenerator x0, AbstractType x1) throws Exception {
        x0.processType(x1);
    }
}
