// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   WriterContext.java

package com.sun.xml.rpc.wsdl.framework;

import com.sun.xml.rpc.sp.NamespaceSupport;
import com.sun.xml.rpc.util.xml.PrettyPrintingXmlWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import javax.xml.rpc.namespace.QName;

public class WriterContext {

    private PrettyPrintingXmlWriter _writer;
    private NamespaceSupport _nsSupport;
    private String _targetNamespaceURI;
    private int _newPrefixCount;
    private List _pendingNamespaceDeclarations;

    public WriterContext(OutputStream os) throws IOException {
        _writer = new PrettyPrintingXmlWriter(os);
        _nsSupport = new NamespaceSupport();
        _newPrefixCount = 2;
    }

    public void flush() throws IOException {
        _writer.flush();
    }

    public void close() throws IOException {
        _writer.close();
    }

    public void push() {
        if(_pendingNamespaceDeclarations != null) {
            throw new IllegalStateException("prefix declarations are pending");
        } else {
            _nsSupport.pushContext();
            return;
        }
    }

    public void pop() {
        _nsSupport.popContext();
        _pendingNamespaceDeclarations = null;
    }

    public String getNamespaceURI(String prefix) {
        return _nsSupport.getURI(prefix);
    }

    public Iterator getPrefixes() {
        return _nsSupport.getPrefixes();
    }

    public String getDefaultNamespaceURI() {
        return getNamespaceURI("");
    }

    public void declarePrefix(String prefix, String uri) {
        _nsSupport.declarePrefix(prefix, uri);
        if(_pendingNamespaceDeclarations == null)
            _pendingNamespaceDeclarations = new ArrayList();
        _pendingNamespaceDeclarations.add(new String[] {
            prefix, uri
        });
    }

    public String getPrefixFor(String uri) {
        if(getDefaultNamespaceURI().equals(uri))
            return "";
        else
            return _nsSupport.getPrefix(uri);
    }

    public String findNewPrefix(String base) {
        return base + Integer.toString(_newPrefixCount++);
    }

    public String getTargetNamespaceURI() {
        return _targetNamespaceURI;
    }

    public void setTargetNamespaceURI(String uri) {
        _targetNamespaceURI = uri;
    }

    public void writeStartTag(QName name) throws IOException {
        _writer.start(getQNameString(name));
    }

    public void writeEndTag(QName name) throws IOException {
        _writer.end(getQNameString(name));
    }

    public void writeAttribute(String name, String value) throws IOException {
        if(value != null)
            _writer.attribute(name, value);
    }

    public void writeAttribute(String name, QName value) throws IOException {
        if(value != null)
            _writer.attribute(name, getQNameString(value));
    }

    public void writeAttribute(String name, boolean value) throws IOException {
        writeAttribute(name, value ? "true" : "false");
    }

    public void writeAttribute(String name, Boolean value) throws IOException {
        if(value != null)
            writeAttribute(name, value.booleanValue());
    }

    public void writeAttribute(String name, int value) throws IOException {
        writeAttribute(name, Integer.toString(value));
    }

    public void writeAttribute(String name, Object value, Map valueToXmlMap) throws IOException {
        String actualValue = (String)valueToXmlMap.get(value);
        writeAttribute(name, actualValue);
    }

    public void writeNamespaceDeclaration(String prefix, String uri) throws IOException {
        _writer.attribute(getNamespaceDeclarationAttributeName(prefix), uri);
    }

    public void writeAllPendingNamespaceDeclarations() throws IOException {
        if(_pendingNamespaceDeclarations != null) {
            String pair[];
            for(Iterator iter = _pendingNamespaceDeclarations.iterator(); iter.hasNext(); writeNamespaceDeclaration(pair[0], pair[1]))
                pair = (String[])iter.next();

        }
        _pendingNamespaceDeclarations = null;
    }

    private String getNamespaceDeclarationAttributeName(String prefix) {
        if(prefix.equals(""))
            return "xmlns";
        else
            return "xmlns:" + prefix;
    }

    public void writeTag(QName name, String value) throws IOException {
        _writer.leaf(getQNameString(name), value);
    }

    public String getQNameString(QName name) {
        String prefix = getPrefixFor(name.getNamespaceURI());
        if(prefix == null)
            throw new IllegalArgumentException();
        if(prefix.equals(""))
            return name.getLocalPart();
        else
            return prefix + ":" + name.getLocalPart();
    }

    public String getQNameStringWithTargetNamespaceCheck(QName name) {
        if(name.getNamespaceURI().equals(_targetNamespaceURI))
            return name.getLocalPart();
        else
            return getQNameString(name);
    }
}
