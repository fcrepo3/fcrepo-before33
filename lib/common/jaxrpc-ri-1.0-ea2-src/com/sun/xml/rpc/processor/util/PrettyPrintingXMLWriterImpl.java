// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   PrettyPrintingXMLWriterImpl.java

package com.sun.xml.rpc.processor.util;

import com.sun.xml.rpc.sp.NamespaceSupport;
import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.xml.PrettyPrintingXmlWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Stack;

public class PrettyPrintingXMLWriterImpl extends XMLWriterBase {

    private PrettyPrintingXmlWriter _writer;
    private NamespaceSupport _nsSupport;
    private Stack _elemStack;
    private PrefixFactory _prefixFactory;

    public PrettyPrintingXMLWriterImpl(OutputStream out, String enc, boolean declare) {
        _nsSupport = new NamespaceSupport();
        _elemStack = new Stack();
        try {
            _writer = new PrettyPrintingXmlWriter(out, enc, declare);
        }
        catch(IOException e) {
            throw wrapException(e);
        }
    }

    public void startElement(String localName, String uri) {
        try {
            _nsSupport.pushContext();
            if(!uri.equals("")) {
                String aPrefix = null;
                boolean mustDeclarePrefix = false;
                String defaultNamespaceURI = _nsSupport.getPrefix("");
                if(defaultNamespaceURI != null && uri.equals(defaultNamespaceURI))
                    aPrefix = "";
                aPrefix = _nsSupport.getPrefix(uri);
                if(aPrefix == null) {
                    mustDeclarePrefix = true;
                    if(_prefixFactory != null)
                        aPrefix = _prefixFactory.getPrefix(uri);
                    if(aPrefix == null)
                        throw new XMLWriterException("xmlwriter.noPrefixForURI", uri);
                }
                String rawName = aPrefix.equals("") ? localName : aPrefix + ":" + localName;
                _writer.start(rawName);
                _elemStack.push(rawName);
                if(mustDeclarePrefix)
                    writeNamespaceDeclaration(aPrefix, uri);
            } else {
                _writer.start(localName);
                _elemStack.push(localName);
            }
        }
        catch(IOException e) {
            throw wrapException(e);
        }
    }

    public void startElement(String localName, String uri, String prefix) {
        try {
            _nsSupport.pushContext();
            if(!uri.equals("")) {
                String aPrefix = null;
                boolean mustDeclarePrefix = false;
                String defaultNamespaceURI = _nsSupport.getPrefix("");
                if(defaultNamespaceURI != null && uri.equals(defaultNamespaceURI))
                    aPrefix = "";
                aPrefix = _nsSupport.getPrefix(uri);
                if(aPrefix == null) {
                    mustDeclarePrefix = true;
                    aPrefix = prefix;
                    if(aPrefix == null)
                        throw new XMLWriterException("xmlwriter.noPrefixForURI", uri);
                }
                String rawName = aPrefix.equals("") ? localName : aPrefix + ":" + localName;
                _writer.start(rawName);
                _elemStack.push(rawName);
                if(mustDeclarePrefix)
                    writeNamespaceDeclaration(aPrefix, uri);
            } else {
                _writer.start(localName);
                _elemStack.push(localName);
            }
        }
        catch(IOException e) {
            throw wrapException(e);
        }
    }

    public void writeNamespaceDeclaration(String prefix, String uri) {
        try {
            _nsSupport.declarePrefix(prefix, uri);
            String rawName = "xmlns";
            if(prefix != null && !prefix.equals(""))
                rawName = rawName + ":" + prefix;
            _writer.attribute(rawName, uri);
        }
        catch(IOException e) {
            throw wrapException(e);
        }
    }

    public void writeNamespaceDeclaration(String uri) {
        if(_prefixFactory == null) {
            throw new XMLWriterException("xmlwriter.noPrefixForURI", uri);
        } else {
            String aPrefix = _prefixFactory.getPrefix(uri);
            writeNamespaceDeclaration(aPrefix, uri);
            return;
        }
    }

    public void writeAttribute(String localName, String uri, String value) {
        try {
            if(!uri.equals("")) {
                String aPrefix = null;
                boolean mustDeclarePrefix = false;
                String defaultNamespaceURI = _nsSupport.getPrefix("");
                if(defaultNamespaceURI != null && uri.equals(defaultNamespaceURI))
                    aPrefix = "";
                aPrefix = _nsSupport.getPrefix(uri);
                if(aPrefix == null) {
                    mustDeclarePrefix = true;
                    if(_prefixFactory != null)
                        aPrefix = _prefixFactory.getPrefix(uri);
                    if(aPrefix == null)
                        throw new XMLWriterException("xmlwriter.noPrefixForURI", uri);
                }
                String rawName = aPrefix + ":" + localName;
                _writer.attribute(rawName, value);
                if(mustDeclarePrefix)
                    writeNamespaceDeclaration(aPrefix, uri);
            } else {
                _writer.attribute(localName, value);
            }
        }
        catch(IOException e) {
            throw wrapException(e);
        }
    }

    public void writeChars(String chars) {
        try {
            _writer.chars(chars);
        }
        catch(IOException e) {
            throw wrapException(e);
        }
    }

    public void endElement() {
        try {
            String rawName = (String)_elemStack.pop();
            _writer.end(rawName);
            _nsSupport.popContext();
        }
        catch(IOException e) {
            throw wrapException(e);
        }
    }

    public PrefixFactory getPrefixFactory() {
        return _prefixFactory;
    }

    public void setPrefixFactory(PrefixFactory factory) {
        _prefixFactory = factory;
    }

    public String getURI(String prefix) {
        return _nsSupport.getURI(prefix);
    }

    public String getPrefix(String uri) {
        return _nsSupport.getPrefix(uri);
    }

    public void flush() {
        try {
            _writer.flush();
        }
        catch(IOException e) {
            throw wrapException(e);
        }
    }

    public void close() {
        try {
            _writer.close();
        }
        catch(IOException e) {
            throw wrapException(e);
        }
    }

    private XMLWriterException wrapException(IOException e) {
        return new XMLWriterException("xmlwriter.ioException", new LocalizableExceptionAdapter(e));
    }
}
