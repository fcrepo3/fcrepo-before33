// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Binding.java

package com.sun.xml.rpc.wsdl.document;

import com.sun.xml.rpc.wsdl.framework.*;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document:
//            PortType, BindingOperation, Kinds, WSDLConstants, 
//            WSDLDocumentVisitor, Documentation

public class Binding extends GlobalEntity
    implements Extensible {

    private ExtensibilityHelper _helper;
    private Documentation _documentation;
    private QName _portType;
    private List _operations;

    public Binding(Defining defining) {
        super(defining);
        _operations = new ArrayList();
        _helper = new ExtensibilityHelper();
    }

    public void add(BindingOperation operation) {
        _operations.add(operation);
    }

    public Iterator operations() {
        return _operations.iterator();
    }

    public QName getPortType() {
        return _portType;
    }

    public void setPortType(QName n) {
        _portType = n;
    }

    public PortType resolvePortType(AbstractDocument document) {
        return (PortType)document.find(Kinds.PORT_TYPE, _portType);
    }

    public Kind getKind() {
        return Kinds.BINDING;
    }

    public QName getElementName() {
        return WSDLConstants.QNAME_BINDING;
    }

    public Documentation getDocumentation() {
        return _documentation;
    }

    public void setDocumentation(Documentation d) {
        _documentation = d;
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        for(Iterator iter = _operations.iterator(); iter.hasNext(); action.perform((Entity)iter.next()));
        _helper.withAllSubEntitiesDo(action);
    }

    public void withAllQNamesDo(QNameAction action) {
        super.withAllQNamesDo(action);
        if(_portType != null)
            action.perform(_portType);
    }

    public void withAllEntityReferencesDo(EntityReferenceAction action) {
        super.withAllEntityReferencesDo(action);
        if(_portType != null)
            action.perform(Kinds.PORT_TYPE, _portType);
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        visitor.preVisit(this);
        for(Iterator iter = _operations.iterator(); iter.hasNext(); ((BindingOperation)iter.next()).accept(visitor));
        _helper.accept(visitor);
        visitor.postVisit(this);
    }

    public void validateThis() {
        if(getName() == null)
            failValidation("validation.missingRequiredAttribute", "name");
        if(_portType == null)
            failValidation("validation.missingRequiredAttribute", "type");
    }

    public void addExtension(Extension e) {
        _helper.addExtension(e);
    }

    public Iterator extensions() {
        return _helper.extensions();
    }
}
