// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SchemaParser.java

package com.sun.xml.rpc.wsdl.parser;

import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.xml.*;
import com.sun.xml.rpc.wsdl.document.schema.*;
import com.sun.xml.rpc.wsdl.framework.*;
import java.io.IOException;
import java.util.Iterator;
import javax.xml.parsers.*;
import javax.xml.rpc.namespace.QName;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// Referenced classes of package com.sun.xml.rpc.wsdl.parser:
//            Util

public class SchemaParser {

    private boolean _followImports;
    private static final String PREFIX_XMLNS = "xmlns";
    private static final String PREFIX_XMLNS_COLON = "xmlns:";

    public SchemaParser() {
    }

    public boolean getFollowImports() {
        return _followImports;
    }

    public void setFollowImports(boolean b) {
        _followImports = b;
    }

    public SchemaDocument parse(InputSource source) {
        SchemaDocument schemaDocument = new SchemaDocument();
        schemaDocument.setSystemId(source.getSystemId());
        ParserContext context = new ParserContext(schemaDocument, null);
        context.setFollowImports(_followImports);
        schemaDocument.setSchema(parseSchema(context, source, null));
        return schemaDocument;
    }

    public Schema parseSchema(ParserContext context, InputSource source, String expectedTargetNamespaceURI) {
        Schema schema = parseSchemaNoImport(context, source, expectedTargetNamespaceURI);
        schema.defineAllEntities();
        processImports(context, schema);
        return schema;
    }

    public Schema parseSchema(ParserContext context, Element e, String expectedTargetNamespaceURI) {
        Schema schema = parseSchemaNoImport(context, e, expectedTargetNamespaceURI);
        schema.defineAllEntities();
        processImports(context, schema);
        return schema;
    }

    protected void processImports(ParserContext context, Schema schema) {
        for(Iterator iter = schema.getContent().children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement)iter.next();
            if(child.getQName().equals(SchemaConstants.QNAME_IMPORT)) {
                String location = child.getValueOfAttributeOrNull("location");
                String namespace = child.getValueOfAttributeOrNull("namespace");
                if(location != null && !context.getDocument().isImportedDocument(location)) {
                    context.getDocument().addImportedDocument(location);
                    String adjustedLocation = context.getDocument().getSystemId() != null ? Util.processSystemIdWithBase(context.getDocument().getSystemId(), location) : location;
                    context.getDocument().addImportedEntity(parseSchema(context, new InputSource(adjustedLocation), namespace));
                }
            } else
            if(child.getQName().equals(SchemaConstants.QNAME_INCLUDE))
                Util.fail("validation.unsupportedSchemaFeature", "include");
            else
            if(child.getQName().equals(SchemaConstants.QNAME_REDEFINE))
                Util.fail("validation.unsupportedSchemaFeature", "redefine");
        }

    }

    protected Schema parseSchemaNoImport(ParserContext context, InputSource source, String expectedTargetNamespaceURI) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            builderFactory.setValidating(false);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            builder.setErrorHandler(new SchemaParser$1(this));
            builder.setEntityResolver(new NullEntityResolver());
            try {
                Document document = builder.parse(source);
                return parseSchemaNoImport(context, document, expectedTargetNamespaceURI);
            }
            catch(IOException e) {
                throw new ParseException("parsing.ioException", new LocalizableExceptionAdapter(e));
            }
            catch(SAXException e) {
                throw new ParseException("parsing.saxException", new LocalizableExceptionAdapter(e));
            }
        }
        catch(ParserConfigurationException e) {
            throw new ParseException("parsing.parserConfigException", new LocalizableExceptionAdapter(e));
        }
        catch(FactoryConfigurationError e) {
            throw new ParseException("parsing.factoryConfigException", new LocalizableExceptionAdapter(e));
        }
    }

    protected Schema parseSchemaNoImport(ParserContext context, Document doc, String expectedTargetNamespaceURI) {
        Element root = doc.getDocumentElement();
        Util.verifyTagNSRootElement(root, SchemaConstants.QNAME_SCHEMA);
        return parseSchemaNoImport(context, root, expectedTargetNamespaceURI);
    }

    protected Schema parseSchemaNoImport(ParserContext context, Element e, String expectedTargetNamespaceURI) {
        Schema schema = new Schema(context.getDocument());
        String targetNamespaceURI = XmlUtil.getAttributeOrNull(e, "targetNamespace");
        if(expectedTargetNamespaceURI != null && !expectedTargetNamespaceURI.equals(targetNamespaceURI))
            throw new ValidationException("validation.incorrectTargetNamespace", new Object[] {
                targetNamespaceURI, expectedTargetNamespaceURI
            });
        schema.setTargetNamespaceURI(targetNamespaceURI);
        String prefix;
        String nsURI;
        for(Iterator iter = context.getPrefixes(); iter.hasNext(); schema.addPrefix(prefix, nsURI)) {
            prefix = (String)iter.next();
            nsURI = context.getNamespaceURI(prefix);
            if(nsURI == null)
                throw new ParseException("parsing.shouldNotHappen");
        }

        context.push();
        context.registerNamespaces(e);
        SchemaElement schemaElement = new SchemaElement(SchemaConstants.QNAME_SCHEMA);
        copyNamespaceDeclarations(schemaElement, e);
        copyAttributesNoNs(schemaElement, e);
        copyElementContent(schemaElement, e);
        schema.setContent(schemaElement);
        schemaElement.setSchema(schema);
        context.pop();
        context.fireDoneParsingEntity(SchemaConstants.QNAME_SCHEMA, schema);
        return schema;
    }

    protected void copyAttributesNoNs(SchemaElement target, Element source) {
        for(Iterator iter = new NamedNodeMapIterator(source.getAttributes()); iter.hasNext();) {
            Attr attr = (Attr)iter.next();
            if(!attr.getName().equals("xmlns") && !attr.getName().startsWith("xmlns:")) {
                SchemaAttribute attribute = new SchemaAttribute(attr.getLocalName());
                attribute.setNamespaceURI(attr.getNamespaceURI());
                attribute.setValue(attr.getValue());
                target.addAttribute(attribute);
            }
        }

    }

    protected void copyNamespaceDeclarations(SchemaElement target, Element source) {
        for(Iterator iter = new NamedNodeMapIterator(source.getAttributes()); iter.hasNext();) {
            Attr attr = (Attr)iter.next();
            if(attr.getName().equals("xmlns")) {
                target.addPrefix("", attr.getValue());
            } else {
                String prefix = XmlUtil.getPrefix(attr.getName());
                if(prefix != null && prefix.equals("xmlns")) {
                    String nsPrefix = XmlUtil.getLocalPart(attr.getName());
                    String uri = attr.getValue();
                    target.addPrefix(nsPrefix, uri);
                }
            }
        }

    }

    protected void copyElementContent(SchemaElement target, Element source) {
        SchemaElement newElement;
        for(Iterator iter = XmlUtil.getAllChildren(source); iter.hasNext(); newElement.setParent(target)) {
            Element e2 = Util.nextElementIgnoringCharacterContent(iter);
            if(e2 == null)
                break;
            newElement = new SchemaElement(e2.getLocalName());
            newElement.setNamespaceURI(e2.getNamespaceURI());
            copyNamespaceDeclarations(newElement, e2);
            copyAttributesNoNs(newElement, e2);
            copyElementContent(newElement, e2);
            target.addChild(newElement);
        }

    }
}
