// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Definitions.java

package com.sun.xml.rpc.wsdl.document;

import com.sun.xml.rpc.wsdl.framework.*;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document:
//            Import, Message, PortType, Binding, 
//            Service, WSDLConstants, WSDLDocumentVisitor, Types, 
//            Documentation

public class Definitions extends Entity
    implements Defining, Extensible {

    private AbstractDocument _document;
    private ExtensibilityHelper _helper;
    private Documentation _documentation;
    private String _name;
    private String _targetNsURI;
    private Types _types;
    private List _messages;
    private List _portTypes;
    private List _bindings;
    private List _services;
    private List _imports;
    private Set _importedNamespaces;

    public Definitions(AbstractDocument document) {
        _document = document;
        _bindings = new ArrayList();
        _imports = new ArrayList();
        _messages = new ArrayList();
        _portTypes = new ArrayList();
        _services = new ArrayList();
        _importedNamespaces = new HashSet();
        _helper = new ExtensibilityHelper();
    }

    public String getName() {
        return _name;
    }

    public void setName(String s) {
        _name = s;
    }

    public String getTargetNamespaceURI() {
        return _targetNsURI;
    }

    public void setTargetNamespaceURI(String s) {
        _targetNsURI = s;
    }

    public void setTypes(Types t) {
        _types = t;
    }

    public Types getTypes() {
        return _types;
    }

    public void add(Message m) {
        _document.define(m);
        _messages.add(m);
    }

    public void add(PortType p) {
        _document.define(p);
        _portTypes.add(p);
    }

    public void add(Binding b) {
        _document.define(b);
        _bindings.add(b);
    }

    public void add(Service s) {
        _document.define(s);
        _services.add(s);
    }

    public void add(Import i) {
        if(_importedNamespaces.contains(i.getNamespace())) {
            throw new DuplicateEntityException(i, i.getNamespace());
        } else {
            _imports.add(i);
            return;
        }
    }

    public Iterator imports() {
        return _imports.iterator();
    }

    public Iterator services() {
        return _services.iterator();
    }

    public QName getElementName() {
        return WSDLConstants.QNAME_DEFINITIONS;
    }

    public Documentation getDocumentation() {
        return _documentation;
    }

    public void setDocumentation(Documentation d) {
        _documentation = d;
    }

    public void addExtension(Extension e) {
        _helper.addExtension(e);
    }

    public Iterator extensions() {
        return _helper.extensions();
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        if(_types != null)
            action.perform(_types);
        for(Iterator iter = _messages.iterator(); iter.hasNext(); action.perform((Entity)iter.next()));
        for(Iterator iter = _portTypes.iterator(); iter.hasNext(); action.perform((Entity)iter.next()));
        for(Iterator iter = _bindings.iterator(); iter.hasNext(); action.perform((Entity)iter.next()));
        for(Iterator iter = _services.iterator(); iter.hasNext(); action.perform((Entity)iter.next()));
        for(Iterator iter = _imports.iterator(); iter.hasNext(); action.perform((Entity)iter.next()));
        _helper.withAllSubEntitiesDo(action);
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        visitor.preVisit(this);
        for(Iterator iter = _imports.iterator(); iter.hasNext(); ((Import)iter.next()).accept(visitor));
        if(_types != null)
            _types.accept(visitor);
        for(Iterator iter = _messages.iterator(); iter.hasNext(); ((Message)iter.next()).accept(visitor));
        for(Iterator iter = _portTypes.iterator(); iter.hasNext(); ((PortType)iter.next()).accept(visitor));
        for(Iterator iter = _bindings.iterator(); iter.hasNext(); ((Binding)iter.next()).accept(visitor));
        for(Iterator iter = _services.iterator(); iter.hasNext(); ((Service)iter.next()).accept(visitor));
        _helper.accept(visitor);
        visitor.postVisit(this);
    }

    public void validateThis() {
    }
}
