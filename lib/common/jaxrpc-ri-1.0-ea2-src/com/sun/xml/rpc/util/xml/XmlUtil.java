// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XmlUtil.java

package com.sun.xml.rpc.util.xml;

import com.sun.xml.messaging.util.ByteInputStream;
import java.io.*;
import java.util.*;
import javax.xml.rpc.namespace.QName;
import javax.xml.transform.*;
import org.w3c.dom.*;

// Referenced classes of package com.sun.xml.rpc.util.xml:
//            NodeListIterator, NamedNodeMapIterator

public class XmlUtil {

    static TransformerFactory transformerFactory = null;

    public XmlUtil() {
    }

    public static String getPrefix(String s) {
        int i = s.indexOf(':');
        if(i == -1)
            return null;
        else
            return s.substring(0, i);
    }

    public static String getLocalPart(String s) {
        int i = s.indexOf(':');
        if(i == -1)
            return s;
        else
            return s.substring(i + 1);
    }

    public static void dumpElement(Element e) {
        System.err.println("** ELEMENT " + e.getTagName() + " :: " + e.getNamespaceURI());
        Attr a;
        for(Iterator iter = getAllAttributes(e); iter.hasNext(); System.err.println("   ATTR " + a.getName() + " :: " + a.getNamespaceURI() + " :: " + a.getValue()))
            a = (Attr)iter.next();

    }

    public static String getAttributeOrNull(Element e, String name) {
        Attr a = e.getAttributeNode(name);
        if(a == null)
            return null;
        else
            return a.getValue();
    }

    public static String getAttributeNSOrNull(Element e, String name, String nsURI) {
        Attr a = e.getAttributeNodeNS(nsURI, name);
        if(a == null)
            return null;
        else
            return a.getValue();
    }

    public static boolean matchesTagNS(Element e, String tag, String nsURI) {
        return e.getLocalName().equals(tag) && e.getNamespaceURI().equals(nsURI);
    }

    public static boolean matchesTagNS(Element e, QName name) {
        return e.getLocalName().equals(name.getLocalPart()) && e.getNamespaceURI().equals(name.getNamespaceURI());
    }

    public static Iterator getAllChildren(Element element) {
        return new NodeListIterator(element.getChildNodes());
    }

    public static Iterator getAllAttributes(Element element) {
        return new NamedNodeMapIterator(element.getAttributes());
    }

    public static List parseTokenList(String tokenList) {
        List result = new ArrayList();
        for(StringTokenizer tokenizer = new StringTokenizer(tokenList, " "); tokenizer.hasMoreTokens(); result.add(tokenizer.nextToken()));
        return result;
    }

    public static String getTextForNode(Node node) {
        StringBuffer sb = new StringBuffer();
        NodeList children = node.getChildNodes();
        if(children.getLength() == 0)
            return null;
        for(int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if(n instanceof Text)
                sb.append(n.getNodeValue());
            else
            if(n instanceof EntityReference) {
                String s = getTextForNode(n);
                if(s == null)
                    return null;
                sb.append(s);
            } else {
                return null;
            }
        }

        return sb.toString();
    }

    public static InputStream getUTF8Stream(String s) {
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            Writer w = new OutputStreamWriter(bas, "utf-8");
            w.write(s);
            w.close();
            byte ba[] = bas.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(ba);
            return bis;
        }
        catch(IOException ioexception) {
            throw new RuntimeException("should not happen");
        }
    }

    public static ByteInputStream getUTF8ByteInputStream(String s) {
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            Writer w = new OutputStreamWriter(bas, "utf-8");
            w.write(s);
            w.close();
            byte ba[] = bas.toByteArray();
            ByteInputStream bis = new ByteInputStream(ba, ba.length);
            return bis;
        }
        catch(IOException ioexception) {
            throw new RuntimeException("should not happen");
        }
    }

    public static Transformer newTransformer() {
        Transformer t = null;
        if(transformerFactory == null)
            transformerFactory = TransformerFactory.newInstance();
        try {
            t = transformerFactory.newTransformer();
        }
        catch(TransformerConfigurationException tex) {
            System.err.println("Unable to create a JAXP transformer");
            tex.printStackTrace();
        }
        return t;
    }

}
