// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   WSDLWriter.java

package com.sun.xml.rpc.wsdl.parser;

import com.sun.xml.rpc.wsdl.document.Definitions;
import com.sun.xml.rpc.wsdl.document.WSDLDocument;
import com.sun.xml.rpc.wsdl.framework.WriterContext;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

// Referenced classes of package com.sun.xml.rpc.wsdl.parser:
//            SOAPExtensionHandler, HTTPExtensionHandler, MIMEExtensionHandler, SchemaExtensionHandler, 
//            ExtensionHandler

public class WSDLWriter {

    private Map _extensionHandlers;
    private static Map _commonPrefixes;
    private static final String TARGET_NAMESPACE_PREFIX = "tns";
    private static final String NEW_NAMESPACE_PREFIX_BASE = "ns";

    public WSDLWriter() throws IOException {
        _extensionHandlers = new HashMap();
        register(new SOAPExtensionHandler());
        register(new HTTPExtensionHandler());
        register(new MIMEExtensionHandler());
        register(new SchemaExtensionHandler());
    }

    public void register(ExtensionHandler h) {
        _extensionHandlers.put(h.getNamespaceURI(), h);
        h.setExtensionHandlers(_extensionHandlers);
    }

    public void unregister(ExtensionHandler h) {
        _extensionHandlers.put(h.getNamespaceURI(), null);
        h.setExtensionHandlers(null);
    }

    public void unregister(String uri) {
        _extensionHandlers.put(uri, null);
    }

    public void write(WSDLDocument document, OutputStream os) throws IOException {
        WriterContext context = new WriterContext(os);
        try {
            document.accept(new WSDLWriter$1(this, context, document));
            context.flush();
        }
        catch(Exception e) {
            if(e instanceof IOException)
                throw (IOException)e;
            if(e instanceof RuntimeException)
                throw (RuntimeException)e;
            else
                throw new IllegalStateException();
        }
    }

    private void initializePrefixes(WriterContext context, WSDLDocument document) throws IOException {
        String tnsURI = document.getDefinitions().getTargetNamespaceURI();
        if(tnsURI != null) {
            context.setTargetNamespaceURI(tnsURI);
            context.declarePrefix("tns", tnsURI);
        }
        context.declarePrefix("", "http://schemas.xmlsoap.org/wsdl/");
        Set namespaces = document.collectAllNamespaces();
        for(Iterator iter = namespaces.iterator(); iter.hasNext();) {
            String nsURI = (String)iter.next();
            if(context.getPrefixFor(nsURI) == null) {
                String prefix = (String)_commonPrefixes.get(nsURI);
                if(prefix == null)
                    prefix = context.findNewPrefix("ns");
                context.declarePrefix(prefix, nsURI);
            }
        }

    }

    static void access$000(WSDLWriter x0, WriterContext x1, WSDLDocument x2) throws IOException {
        x0.initializePrefixes(x1, x2);
    }

    static Map access$100(WSDLWriter x0) {
        return x0._extensionHandlers;
    }

    static  {
        _commonPrefixes = new HashMap();
        _commonPrefixes.put("http://schemas.xmlsoap.org/wsdl/", "wsdl");
        _commonPrefixes.put("http://schemas.xmlsoap.org/wsdl/soap/", "soap");
        _commonPrefixes.put("http://schemas.xmlsoap.org/wsdl/http/", "http");
        _commonPrefixes.put("http://schemas.xmlsoap.org/wsdl/mime/", "mime");
        _commonPrefixes.put("http://www.w3.org/2001/XMLSchema", "xsd");
        _commonPrefixes.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
    }
}
