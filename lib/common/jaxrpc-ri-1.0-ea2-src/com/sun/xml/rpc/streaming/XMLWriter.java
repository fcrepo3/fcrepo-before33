// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLWriter.java

package com.sun.xml.rpc.streaming;

import com.sun.xml.rpc.util.xml.CDATA;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.streaming:
//            PrefixFactory

public interface XMLWriter {

    public abstract void startElement(QName qname);

    public abstract void startElement(QName qname, String s);

    public abstract void startElement(String s);

    public abstract void startElement(String s, String s1);

    public abstract void startElement(String s, String s1, String s2);

    public abstract void writeAttribute(QName qname, String s);

    public abstract void writeAttribute(String s, String s1);

    public abstract void writeAttribute(String s, String s1, String s2);

    public abstract void writeNamespaceDeclaration(String s, String s1);

    public abstract void writeNamespaceDeclaration(String s);

    public abstract void writeChars(String s);

    public abstract void writeChars(CDATA cdata);

    public abstract void writeComment(String s);

    public abstract void endElement();

    public abstract PrefixFactory getPrefixFactory();

    public abstract void setPrefixFactory(PrefixFactory prefixfactory);

    public abstract String getURI(String s);

    public abstract String getPrefix(String s);

    public abstract void flush();

    public abstract void close();
}
