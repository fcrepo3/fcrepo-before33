// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ModelInfoParser.java

package com.sun.xml.rpc.processor.config.parser;

import com.sun.xml.rpc.processor.config.*;
import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.util.xml.XmlUtil;
import java.util.Map;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.config.parser:
//            HandlerChainInfoData, Constants, ParserUtil

public abstract class ModelInfoParser {

    public ModelInfoParser() {
    }

    public abstract ModelInfo parse(XMLReader xmlreader);

    protected TypeMappingRegistryInfo parseTypeMappingRegistryInfo(XMLReader reader) {
        TypeMappingRegistryInfo typeMappingRegistryInfo = new TypeMappingRegistryInfo();
        while(reader.nextElementContent() != 2) 
            if(reader.getName().equals(Constants.QNAME_TYPE_MAPPING))
                parseTypeMapping(typeMappingRegistryInfo, reader);
            else
                ParserUtil.failWithLocalName("configuration.invalidElement", reader);
        return typeMappingRegistryInfo;
    }

    private void parseTypeMapping(TypeMappingRegistryInfo typeMappingRegistryInfo, XMLReader reader) {
        String encodingStyle = ParserUtil.getMandatoryAttribute(reader, "encodingStyle");
        while(reader.nextElementContent() != 2) 
            if(reader.getName().equals(Constants.QNAME_ENTRY))
                parseEntry(typeMappingRegistryInfo, encodingStyle, reader);
            else
                ParserUtil.failWithLocalName("configuration.invalidElement", reader);
    }

    private void parseEntry(TypeMappingRegistryInfo typeMappingRegistryInfo, String encodingStyle, XMLReader reader) {
        String rawSchemaType = ParserUtil.getMandatoryNonEmptyAttribute(reader, "schemaType");
        String javaTypeName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "javaType");
        String serializerFactoryName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "serializerFactory");
        String deserializerFactoryName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "deserializerFactory");
        ParserUtil.ensureNoContent(reader);
        String prefix = XmlUtil.getPrefix(rawSchemaType);
        String uri = prefix != null ? reader.getURI(prefix) : null;
        if(prefix != null && uri == null)
            ParserUtil.failWithLocalName("configuration.configuration.invalid.attribute.value", reader, rawSchemaType);
        String localPart = XmlUtil.getLocalPart(rawSchemaType);
        QName xmlType = new QName(uri, localPart);
        TypeMappingInfo i = new TypeMappingInfo(encodingStyle, xmlType, javaTypeName, serializerFactoryName, deserializerFactoryName);
        typeMappingRegistryInfo.addMapping(i);
    }

    protected HandlerChainInfoData parseHandlerChainInfoData(XMLReader reader) {
        HandlerChainInfoData data = new HandlerChainInfoData();
        boolean gotClient = false;
        boolean gotServer = false;
        while(reader.nextElementContent() != 2) 
            if(reader.getName().equals(Constants.QNAME_CHAIN)) {
                String runatAttr = ParserUtil.getMandatoryNonEmptyAttribute(reader, "runAt");
                if(runatAttr.equals("client")) {
                    if(gotClient) {
                        ParserUtil.failWithLocalName("configuration.handlerChain.duplicate", reader, runatAttr);
                    } else {
                        data.setClientHandlerChainInfo(parseHandlerChainInfo(reader));
                        gotClient = true;
                    }
                } else
                if(runatAttr.equals("server")) {
                    if(gotServer) {
                        ParserUtil.failWithLocalName("configuration.handlerChain.duplicate", reader, runatAttr);
                    } else {
                        data.setServerHandlerChainInfo(parseHandlerChainInfo(reader));
                        gotServer = true;
                    }
                } else {
                    ParserUtil.failWithLocalName("configuration.invalidAttributeValue", reader, "runAt");
                }
            } else {
                ParserUtil.failWithLocalName("configuration.invalidElement", reader);
            }
        return data;
    }

    protected HandlerChainInfo parseHandlerChainInfo(XMLReader reader) {
        HandlerChainInfo chain = new HandlerChainInfo();
        while(reader.nextElementContent() != 2) 
            if(reader.getName().equals(Constants.QNAME_HANDLER))
                chain.add(parseHandlerInfo(reader));
            else
                ParserUtil.failWithLocalName("configuration.invalidElement", reader);
        return chain;
    }

    protected HandlerInfo parseHandlerInfo(XMLReader reader) {
        HandlerInfo handler = new HandlerInfo();
        String className = ParserUtil.getMandatoryNonEmptyAttribute(reader, "className");
        handler.setHandlerClassName(className);
        Map properties = handler.getProperties();
        while(reader.nextElementContent() != 2) 
            if(reader.getName().equals(Constants.QNAME_PROPERTY)) {
                String name = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
                String value = ParserUtil.getMandatoryAttribute(reader, "value");
                properties.put(name, value);
                ParserUtil.ensureNoContent(reader);
            } else {
                ParserUtil.failWithLocalName("configuration.invalidElement", reader);
            }
        return handler;
    }
}
