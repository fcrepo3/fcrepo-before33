// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SchemaWriter.java

package com.sun.xml.rpc.wsdl.parser;

import com.sun.xml.rpc.wsdl.document.schema.*;
import com.sun.xml.rpc.wsdl.framework.WriterContext;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

public class SchemaWriter {

    public SchemaWriter() {
    }

    public void write(SchemaDocument document, OutputStream os) throws IOException {
        WriterContext context = new WriterContext(os);
        writeSchema(context, document.getSchema());
        context.flush();
    }

    public void writeSchema(WriterContext context, Schema schema) throws IOException {
        context.push();
        try {
            writeTopSchemaElement(context, schema);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            context.pop();
        }
    }

    protected void writeTopSchemaElement(WriterContext context, Schema schema) throws IOException {
        SchemaElement schemaElement = schema.getContent();
        javax.xml.rpc.namespace.QName name = schemaElement.getQName();
        for(Iterator iter = schema.prefixes(); iter.hasNext();) {
            String prefix = (String)iter.next();
            String expectedURI = schema.getURIForPrefix(prefix);
            if(!expectedURI.equals(context.getNamespaceURI(prefix)))
                context.declarePrefix(prefix, expectedURI);
        }

        String prefix;
        String uri;
        for(Iterator iter = schemaElement.prefixes(); iter.hasNext(); context.declarePrefix(prefix, uri)) {
            prefix = (String)iter.next();
            uri = schemaElement.getURIForPrefix(prefix);
        }

        context.writeStartTag(name);
        for(Iterator iter = schemaElement.attributes(); iter.hasNext();) {
            SchemaAttribute attribute = (SchemaAttribute)iter.next();
            if(attribute.getNamespaceURI() == null)
                context.writeAttribute(attribute.getLocalName(), attribute.getValue());
            else
                context.writeAttribute(context.getQNameString(attribute.getQName()), attribute.getValue());
        }

        context.writeAllPendingNamespaceDeclarations();
        SchemaElement child;
        for(Iterator iter = schemaElement.children(); iter.hasNext(); writeSchemaElement(context, child))
            child = (SchemaElement)iter.next();

        context.writeEndTag(name);
    }

    protected void writeSchemaElement(WriterContext context, SchemaElement schemaElement) throws IOException {
        javax.xml.rpc.namespace.QName name = schemaElement.getQName();
        if(schemaElement.declaresPrefixes())
            context.push();
        context.writeStartTag(name);
        if(schemaElement.declaresPrefixes()) {
            String prefix;
            String uri;
            for(Iterator iter = schemaElement.prefixes(); iter.hasNext(); context.declarePrefix(prefix, uri)) {
                prefix = (String)iter.next();
                uri = schemaElement.getURIForPrefix(prefix);
                context.writeNamespaceDeclaration(prefix, uri);
            }

        }
        for(Iterator iter = schemaElement.attributes(); iter.hasNext();) {
            SchemaAttribute attribute = (SchemaAttribute)iter.next();
            if(attribute.getNamespaceURI() == null)
                context.writeAttribute(attribute.getLocalName(), attribute.getValue());
            else
                context.writeAttribute(context.getQNameString(attribute.getQName()), attribute.getValue());
        }

        SchemaElement child;
        for(Iterator iter = schemaElement.children(); iter.hasNext(); writeSchemaElement(context, child))
            child = (SchemaElement)iter.next();

        context.writeEndTag(name);
        if(schemaElement.declaresPrefixes())
            context.pop();
    }
}
