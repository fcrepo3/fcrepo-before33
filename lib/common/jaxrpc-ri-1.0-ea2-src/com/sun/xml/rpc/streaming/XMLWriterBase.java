// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLWriterBase.java

package com.sun.xml.rpc.streaming;

import com.sun.xml.rpc.util.xml.CDATA;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.streaming:
//            XMLWriter, PrefixFactory

public abstract class XMLWriterBase
    implements XMLWriter {

    public XMLWriterBase() {
    }

    public void startElement(String localName) {
        startElement(localName, "");
    }

    public void startElement(QName name) {
        startElement(name.getLocalPart(), name.getNamespaceURI());
    }

    public void startElement(QName name, String prefix) {
        startElement(name.getLocalPart(), name.getNamespaceURI(), prefix);
    }

    public void writeAttribute(String localName, String value) {
        writeAttribute(localName, "", value);
    }

    public void writeAttribute(QName name, String value) {
        writeAttribute(name.getLocalPart(), name.getNamespaceURI(), value);
    }

    public void writeChars(CDATA chars) {
        writeChars(chars.getText());
    }

    public void writeComment(String s) {
    }

    public abstract void close();

    public abstract void flush();

    public abstract String getPrefix(String s);

    public abstract String getURI(String s);

    public abstract void setPrefixFactory(PrefixFactory prefixfactory);

    public abstract PrefixFactory getPrefixFactory();

    public abstract void endElement();

    public abstract void writeChars(String s);

    public abstract void writeNamespaceDeclaration(String s);

    public abstract void writeNamespaceDeclaration(String s, String s1);

    public abstract void writeAttribute(String s, String s1, String s2);

    public abstract void startElement(String s, String s1, String s2);

    public abstract void startElement(String s, String s1);
}
