// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   NullEntityResolver.java

package com.sun.xml.rpc.util.xml;

import java.io.StringReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class NullEntityResolver
    implements EntityResolver {

    public NullEntityResolver() {
    }

    public InputSource resolveEntity(String publicId, String systemId) {
        return new InputSource(new StringReader(""));
    }
}
