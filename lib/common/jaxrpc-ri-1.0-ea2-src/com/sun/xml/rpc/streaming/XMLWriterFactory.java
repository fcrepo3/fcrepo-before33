// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLWriterFactory.java

package com.sun.xml.rpc.streaming;

import java.io.OutputStream;
import javax.xml.parsers.FactoryConfigurationError;

// Referenced classes of package com.sun.xml.rpc.streaming:
//            XMLWriter

public abstract class XMLWriterFactory {

    private static XMLWriterFactory _instance;

    protected XMLWriterFactory() {
    }

    public static XMLWriterFactory newInstance() {
        if(_instance == null) {
            String factoryImplName = System.getProperty("com.sun.xml.rpc.streaming.XMLWriterFactory", "com.sun.xml.rpc.streaming.XMLWriterFactoryImpl");
            try {
                Class clazz = Class.forName(factoryImplName);
                _instance = (XMLWriterFactory)clazz.newInstance();
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

    public abstract XMLWriter createXMLWriter(OutputStream outputstream);

    public abstract XMLWriter createXMLWriter(OutputStream outputstream, String s);

    public abstract XMLWriter createXMLWriter(OutputStream outputstream, String s, boolean flag);
}
