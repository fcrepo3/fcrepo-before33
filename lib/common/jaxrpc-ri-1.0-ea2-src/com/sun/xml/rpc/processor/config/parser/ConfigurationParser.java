// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ConfigurationParser.java

package com.sun.xml.rpc.processor.config.parser;

import com.sun.xml.rpc.processor.config.*;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import com.sun.xml.rpc.streaming.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.config.parser:
//            RmiModelInfoParser, WSDLModelInfoParser, ModelInfoParser, Constants, 
//            ParserUtil

public class ConfigurationParser {

    private BatchEnvironment _env;
    private Map _modelInfoParsers;

    public ConfigurationParser(BatchEnvironment env) {
        _env = env;
        _modelInfoParsers = new HashMap();
        _modelInfoParsers.put(Constants.QNAME_RMI, new RmiModelInfoParser());
        _modelInfoParsers.put(Constants.QNAME_WSDL, new WSDLModelInfoParser());
    }

    public Configuration parse(InputStream is) {
        try {
            XMLReader reader = XMLReaderFactory.newInstance().createXMLReader(is);
            reader.next();
            return parseConfiguration(reader);
        }
        catch(XMLReaderException e) {
            throw new ConfigurationException("configuration.xmlReader", e);
        }
    }

    protected Configuration parseConfiguration(XMLReader reader) {
        if(!reader.getName().equals(Constants.QNAME_CONFIGURATION))
            ParserUtil.failWithLocalName("configuration.invalidElement", reader);
        Configuration configuration = new Configuration(_env);
        if(reader.nextElementContent() == 1)
            configuration.setModelInfo(parseModelInfo(reader));
        else
            ParserUtil.fail("configuration.missing.model", reader);
        if(reader.nextElementContent() != 2)
            ParserUtil.fail("configuration.unexpectedContent", reader);
        reader.close();
        return configuration;
    }

    protected ModelInfo parseModelInfo(XMLReader reader) {
        ModelInfoParser miParser = (ModelInfoParser)_modelInfoParsers.get(reader.getName());
        if(miParser != null) {
            return miParser.parse(reader);
        } else {
            ParserUtil.fail("configuration.unknown.modelInfo", reader);
            return null;
        }
    }
}
