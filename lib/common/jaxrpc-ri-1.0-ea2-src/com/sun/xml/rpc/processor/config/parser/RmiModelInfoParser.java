// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   RmiModelInfoParser.java

package com.sun.xml.rpc.processor.config.parser;

import com.sun.xml.rpc.processor.config.*;
import com.sun.xml.rpc.streaming.XMLReader;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.config.parser:
//            ModelInfoParser, ParserUtil, Constants, HandlerChainInfoData

public class RmiModelInfoParser extends ModelInfoParser {

    public RmiModelInfoParser() {
    }

    public ModelInfo parse(XMLReader reader) {
        RmiModelInfo modelInfo = new RmiModelInfo();
        String name = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        modelInfo.setName(name);
        String targetNamespaceURI = ParserUtil.getMandatoryNonEmptyAttribute(reader, "targetNamespace");
        modelInfo.setTargetNamespaceURI(targetNamespaceURI);
        String typeNamespaceURI = ParserUtil.getMandatoryNonEmptyAttribute(reader, "typeNamespace");
        modelInfo.setTypeNamespaceURI(typeNamespaceURI);
        boolean gotTypeMappingRegistry = false;
        boolean gotHandlerChains = false;
        while(reader.nextElementContent() != 2) 
            if(reader.getName().equals(Constants.QNAME_SERVICE)) {
                if(gotTypeMappingRegistry)
                    ParserUtil.failWithLocalName("configuration.invalidElement", reader);
                else
                    modelInfo.add(parseServiceInfo(reader));
            } else
            if(reader.getName().equals(Constants.QNAME_TYPE_MAPPING_REGISTRY)) {
                if(gotTypeMappingRegistry) {
                    ParserUtil.failWithLocalName("configuration.invalidElement", reader);
                } else {
                    modelInfo.setTypeMappingRegistry(parseTypeMappingRegistryInfo(reader));
                    gotTypeMappingRegistry = true;
                }
            } else
            if(reader.getName().equals(Constants.QNAME_HANDLER_CHAINS)) {
                if(gotHandlerChains) {
                    ParserUtil.failWithLocalName("configuration.invalidElement", reader);
                } else {
                    HandlerChainInfoData data = parseHandlerChainInfoData(reader);
                    modelInfo.setClientHandlerChainInfo(data.getClientHandlerChainInfo());
                    modelInfo.setServerHandlerChainInfo(data.getServerHandlerChainInfo());
                    gotHandlerChains = true;
                }
            } else {
                ParserUtil.failWithLocalName("configuration.invalidElement", reader);
            }
        return modelInfo;
    }

    private RmiServiceInfo parseServiceInfo(XMLReader reader) {
        RmiServiceInfo serviceInfo = new RmiServiceInfo();
        String name = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        serviceInfo.setName(name);
        String packageName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "packageName");
        serviceInfo.setJavaPackageName(packageName);
        boolean gotHandlerChains = false;
        while(reader.nextElementContent() != 2) 
            if(reader.getName().equals(Constants.QNAME_INTERFACE))
                serviceInfo.add(parseInterfaceInfo(reader));
            else
            if(reader.getName().equals(Constants.QNAME_HANDLER_CHAINS)) {
                if(gotHandlerChains) {
                    ParserUtil.failWithLocalName("configuration.invalidElement", reader);
                } else {
                    HandlerChainInfoData data = parseHandlerChainInfoData(reader);
                    serviceInfo.setClientHandlerChainInfo(data.getClientHandlerChainInfo());
                    serviceInfo.setServerHandlerChainInfo(data.getServerHandlerChainInfo());
                    gotHandlerChains = true;
                }
            } else {
                ParserUtil.failWithLocalName("configuration.invalidElement", reader);
            }
        return serviceInfo;
    }

    private RmiInterfaceInfo parseInterfaceInfo(XMLReader reader) {
        RmiInterfaceInfo interfaceInfo = new RmiInterfaceInfo();
        String name = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        interfaceInfo.setName(name);
        String servantName = ParserUtil.getAttribute(reader, "servantName");
        interfaceInfo.setServantName(servantName);
        String soapAction = ParserUtil.getAttribute(reader, "soapAction");
        interfaceInfo.setSOAPAction(soapAction);
        String soapActionBase = ParserUtil.getAttribute(reader, "soapActionBase");
        interfaceInfo.setSOAPActionBase(soapActionBase);
        boolean gotHandlerChains = false;
        while(reader.nextElementContent() != 2) 
            if(reader.getName().equals(Constants.QNAME_HANDLER_CHAINS)) {
                if(gotHandlerChains) {
                    ParserUtil.failWithLocalName("configuration.invalidElement", reader);
                } else {
                    HandlerChainInfoData data = parseHandlerChainInfoData(reader);
                    interfaceInfo.setClientHandlerChainInfo(data.getClientHandlerChainInfo());
                    interfaceInfo.setServerHandlerChainInfo(data.getServerHandlerChainInfo());
                    gotHandlerChains = true;
                }
            } else {
                ParserUtil.failWithLocalName("configuration.invalidElement", reader);
            }
        return interfaceInfo;
    }
}
