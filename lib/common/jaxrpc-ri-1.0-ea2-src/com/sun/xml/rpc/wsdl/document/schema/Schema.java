// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Schema.java

package com.sun.xml.rpc.wsdl.document.schema;

import com.sun.xml.rpc.wsdl.framework.*;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document.schema:
//            SchemaElement, SchemaEntity, SchemaConstants, SchemaKinds

public class Schema extends Extension
    implements Defining {

    private AbstractDocument _document;
    private String _targetNamespaceURI;
    private SchemaElement _content;
    private List _definedEntities;
    private Map _nsPrefixes;

    public Schema(AbstractDocument document) {
        _document = document;
        _nsPrefixes = new HashMap();
        _definedEntities = new ArrayList();
    }

    public QName getElementName() {
        return SchemaConstants.QNAME_SCHEMA;
    }

    public SchemaElement getContent() {
        return _content;
    }

    public void setContent(SchemaElement entity) {
        _content = entity;
        _content.setSchema(this);
    }

    public void setTargetNamespaceURI(String uri) {
        _targetNamespaceURI = uri;
    }

    public String getTargetNamespaceURI() {
        return _targetNamespaceURI;
    }

    public void addPrefix(String prefix, String uri) {
        _nsPrefixes.put(prefix, uri);
    }

    public String getURIForPrefix(String prefix) {
        return (String)_nsPrefixes.get(prefix);
    }

    public Iterator prefixes() {
        return _nsPrefixes.keySet().iterator();
    }

    public void defineAllEntities() {
        if(_content == null)
            throw new ValidationException("validation.shouldNotHappen", "missing schema content");
        for(Iterator iter = _content.children(); iter.hasNext();) {
            SchemaElement child = (SchemaElement)iter.next();
            if(child.getQName().equals(SchemaConstants.QNAME_ATTRIBUTE)) {
                QName name = new QName(_targetNamespaceURI, child.getValueOfMandatoryAttribute("name"));
                defineEntity(child, SchemaKinds.XSD_ATTRIBUTE, name);
            } else
            if(child.getQName().equals(SchemaConstants.QNAME_ATTRIBUTE_GROUP)) {
                QName name = new QName(_targetNamespaceURI, child.getValueOfMandatoryAttribute("name"));
                defineEntity(child, SchemaKinds.XSD_ATTRIBUTE_GROUP, name);
            } else
            if(child.getQName().equals(SchemaConstants.QNAME_ELEMENT)) {
                QName name = new QName(_targetNamespaceURI, child.getValueOfMandatoryAttribute("name"));
                defineEntity(child, SchemaKinds.XSD_ELEMENT, name);
            } else
            if(child.getQName().equals(SchemaConstants.QNAME_GROUP)) {
                QName name = new QName(_targetNamespaceURI, child.getValueOfMandatoryAttribute("name"));
                defineEntity(child, SchemaKinds.XSD_GROUP, name);
            } else
            if(child.getQName().equals(SchemaConstants.QNAME_COMPLEX_TYPE)) {
                QName name = new QName(_targetNamespaceURI, child.getValueOfMandatoryAttribute("name"));
                defineEntity(child, SchemaKinds.XSD_TYPE, name);
            } else
            if(child.getQName().equals(SchemaConstants.QNAME_SIMPLE_TYPE)) {
                QName name = new QName(_targetNamespaceURI, child.getValueOfMandatoryAttribute("name"));
                defineEntity(child, SchemaKinds.XSD_TYPE, name);
            }
        }

    }

    public void defineEntity(SchemaElement element, Kind kind, QName name) {
        SchemaEntity entity = new SchemaEntity(this, element, kind, name);
        _document.define(entity);
        _definedEntities.add(entity);
    }

    public Iterator definedEntities() {
        return _definedEntities.iterator();
    }

    public void validateThis() {
        if(_content == null)
            throw new ValidationException("validation.shouldNotHappen", "missing schema content");
        else
            return;
    }

    public String asString(QName name) {
        if(name.getNamespaceURI().equals(""))
            return name.getLocalPart();
        for(Iterator iter = prefixes(); iter.hasNext();) {
            String prefix = (String)iter.next();
            if(prefix.equals(name.getNamespaceURI()))
                return prefix + ":" + name.getLocalPart();
        }

        return null;
    }
}
