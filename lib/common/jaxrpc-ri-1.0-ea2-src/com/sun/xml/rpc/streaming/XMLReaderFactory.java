// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLReaderFactory.java

package com.sun.xml.rpc.streaming;

import java.io.InputStream;
import javax.xml.parsers.FactoryConfigurationError;
import org.xml.sax.InputSource;

// Referenced classes of package com.sun.xml.rpc.streaming:
//            XMLReader

public abstract class XMLReaderFactory {

    private static XMLReaderFactory _instance;

    protected XMLReaderFactory() {
    }

    public static XMLReaderFactory newInstance() {
        if(_instance == null) {
            String factoryImplName = System.getProperty("com.sun.xml.rpc.streaming.XMLReaderFactory", "com.sun.xml.rpc.streaming.XMLReaderFactoryImpl");
            try {
                Class clazz = Class.forName(factoryImplName);
                _instance = (XMLReaderFactory)clazz.newInstance();
            }
            catch(ClassNotFoundException e) {
                throw new FactoryConfigurationError(e);
            }
            catch(IllegalAccessException e) {
                throw new FactoryConfigurationError(e);
            }
            catch(InstantiationException e) {
                throw new FactoryConfigurationError(e);
            }
        }
        return _instance;
    }

    public abstract XMLReader createXMLReader(InputStream inputstream);

    public abstract XMLReader createXMLReader(InputSource inputsource);
}
