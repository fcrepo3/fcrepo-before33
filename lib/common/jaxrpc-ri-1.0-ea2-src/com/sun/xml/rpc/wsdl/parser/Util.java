// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Util.java

package com.sun.xml.rpc.wsdl.parser;

import com.sun.xml.rpc.util.xml.XmlUtil;
import com.sun.xml.rpc.wsdl.framework.ParseException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import javax.xml.rpc.namespace.QName;
import org.w3c.dom.*;

public class Util {

    public Util() {
    }

    public static String getRequiredAttribute(Element element, String name) {
        String result = XmlUtil.getAttributeOrNull(element, name);
        if(result == null)
            fail("parsing.missingRequiredAttribute", element.getTagName(), name);
        return result;
    }

    public static void verifyTag(Element element, String tag) {
        if(!element.getLocalName().equals(tag))
            fail("parsing.invalidTag", element.getTagName(), tag);
    }

    public static void verifyTagNS(Element element, String tag, String nsURI) {
        if(!element.getLocalName().equals(tag) || element.getNamespaceURI() != null && !element.getNamespaceURI().equals(nsURI))
            fail("parsing.invalidTagNS", new Object[] {
                element.getTagName(), element.getNamespaceURI(), tag, nsURI
            });
    }

    public static void verifyTagNS(Element element, QName name) {
        if(!element.getLocalName().equals(name.getLocalPart()) || element.getNamespaceURI() != null && !element.getNamespaceURI().equals(name.getNamespaceURI()))
            fail("parsing.invalidTagNS", new Object[] {
                element.getTagName(), element.getNamespaceURI(), name.getLocalPart(), name.getNamespaceURI()
            });
    }

    public static void verifyTagNSRootElement(Element element, QName name) {
        if(!element.getLocalName().equals(name.getLocalPart()) || element.getNamespaceURI() != null && !element.getNamespaceURI().equals(name.getNamespaceURI()))
            fail("parsing.incorrectRootElement", new Object[] {
                element.getTagName(), element.getNamespaceURI(), name.getLocalPart(), name.getNamespaceURI()
            });
    }

    public static Element nextElementIgnoringCharacterContent(Iterator iter) {
        while(iter.hasNext())  {
            Node n = (Node)iter.next();
            if(!(n instanceof Text) && !(n instanceof Comment)) {
                if(!(n instanceof Element))
                    fail("parsing.elementExpected");
                return (Element)n;
            }
        }
        return null;
    }

    public static Element nextElement(Iterator iter) {
        while(iter.hasNext())  {
            Node n = (Node)iter.next();
            if(n instanceof Text) {
                Text t = (Text)n;
                if(t.getData().trim().length() == 0)
                    continue;
                fail("parsing.nonWhitespaceTextFound", t.getData().trim());
            }
            if(!(n instanceof Comment)) {
                if(!(n instanceof Element))
                    fail("parsing.elementExpected");
                return (Element)n;
            }
        }
        return null;
    }

    public static String processSystemIdWithBase(String baseSystemId, String systemId) {
        try {
            URL base = null;
            try {
                base = new URL(baseSystemId);
            }
            catch(MalformedURLException malformedurlexception1) {
                base = (new File(baseSystemId)).toURL();
            }
            try {
                URL url = new URL(base, systemId);
                return url.toString();
            }
            catch(MalformedURLException malformedurlexception2) {
                fail("parsing.invalidURI", systemId);
            }
        }
        catch(MalformedURLException malformedurlexception) {
            fail("parsing.invalidURI", baseSystemId);
        }
        return null;
    }

    public static void fail(String key) {
        throw new ParseException(key);
    }

    public static void fail(String key, String arg) {
        throw new ParseException(key, arg);
    }

    public static void fail(String key, String arg1, String arg2) {
        throw new ParseException(key, new Object[] {
            arg1, arg2
        });
    }

    public static void fail(String key, Object args[]) {
        throw new ParseException(key, args);
    }
}
