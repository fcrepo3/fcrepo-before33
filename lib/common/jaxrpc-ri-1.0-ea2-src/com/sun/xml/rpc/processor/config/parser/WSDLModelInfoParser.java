// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   WSDLModelInfoParser.java

package com.sun.xml.rpc.processor.config.parser;

import com.sun.xml.rpc.processor.config.ModelInfo;
import com.sun.xml.rpc.processor.config.WSDLModelInfo;
import com.sun.xml.rpc.streaming.XMLReader;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.config.parser:
//            ModelInfoParser, ParserUtil, Constants, HandlerChainInfoData

public class WSDLModelInfoParser extends ModelInfoParser {

    public WSDLModelInfoParser() {
    }

    public ModelInfo parse(XMLReader reader) {
        WSDLModelInfo modelInfo = new WSDLModelInfo();
        String name = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        modelInfo.setName(name);
        String location = ParserUtil.getMandatoryNonEmptyAttribute(reader, "location");
        modelInfo.setLocation(location);
        String packageName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "packageName");
        modelInfo.setJavaPackageName(packageName);
        boolean gotTypeMappingRegistry = false;
        boolean gotHandlerChains = false;
        while(reader.nextElementContent() != 2) 
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
}
