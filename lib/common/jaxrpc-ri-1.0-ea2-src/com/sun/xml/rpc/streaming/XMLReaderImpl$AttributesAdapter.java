// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLReaderImpl.java

package com.sun.xml.rpc.streaming;

import com.sun.xml.rpc.sp.AttributesEx;
import com.sun.xml.rpc.util.xml.XmlUtil;
import javax.xml.rpc.namespace.QName;
import org.xml.sax.Attributes;

// Referenced classes of package com.sun.xml.rpc.streaming:
//            Attributes, XMLReaderImpl

class XMLReaderImpl$AttributesAdapter
    implements com.sun.xml.rpc.streaming.Attributes {

    private AttributesEx _attr;
    private static final String XMLNS_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";
    private final XMLReaderImpl this$0; /* synthetic field */

    public XMLReaderImpl$AttributesAdapter(XMLReaderImpl this$0) {
        this.this$0 = this$0;
    }

    public void setTarget(AttributesEx attr) {
        _attr = attr;
    }

    public int getLength() {
        return _attr.getLength();
    }

    public boolean isNamespaceDeclaration(int index) {
        return _attr.getURI(index) == "http://www.w3.org/2000/xmlns/";
    }

    public QName getName(int index) {
        return new QName(getURI(index), getLocalName(index));
    }

    public String getURI(int index) {
        return _attr.getURI(index);
    }

    public String getLocalName(int index) {
        return _attr.getLocalName(index);
    }

    public String getPrefix(int index) {
        String qname = _attr.getQName(index);
        if(qname == null)
            return null;
        else
            return XmlUtil.getPrefix(qname);
    }

    public String getValue(int index) {
        return _attr.getValue(index);
    }

    public int getIndex(QName name) {
        return _attr.getIndex(name.getNamespaceURI(), name.getLocalPart());
    }

    public int getIndex(String uri, String localName) {
        return _attr.getIndex(uri, localName);
    }

    public int getIndex(String localName) {
        return _attr.getIndex(localName);
    }

    public String getValue(QName name) {
        return _attr.getValue(name.getNamespaceURI(), name.getLocalPart());
    }

    public String getValue(String uri, String localName) {
        return _attr.getValue(uri, localName);
    }

    public String getValue(String localName) {
        return _attr.getValue(localName);
    }

    public String toString() {
        StringBuffer attributes = new StringBuffer();
        for(int i = 0; i < getLength(); i++) {
            if(i != 0)
                attributes.append("\n");
            attributes.append(getURI(i) + ":" + getLocalName(i) + " = " + getValue(i));
        }

        return attributes.toString();
    }
}
