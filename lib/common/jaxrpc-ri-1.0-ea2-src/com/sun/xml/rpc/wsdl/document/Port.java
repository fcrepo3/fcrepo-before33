// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Port.java

package com.sun.xml.rpc.wsdl.document;

import com.sun.xml.rpc.wsdl.framework.*;
import java.util.Iterator;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document:
//            Binding, Kinds, WSDLConstants, WSDLDocumentVisitor, 
//            Documentation, Service

public class Port extends GlobalEntity
    implements Extensible {

    private ExtensibilityHelper _helper;
    private Documentation _documentation;
    private Service _service;
    private QName _binding;

    public Port(Defining defining) {
        super(defining);
        _helper = new ExtensibilityHelper();
    }

    public Service getService() {
        return _service;
    }

    public void setService(Service s) {
        _service = s;
    }

    public QName getBinding() {
        return _binding;
    }

    public void setBinding(QName n) {
        _binding = n;
    }

    public Binding resolveBinding(AbstractDocument document) {
        return (Binding)document.find(Kinds.BINDING, _binding);
    }

    public Kind getKind() {
        return Kinds.PORT;
    }

    public QName getElementName() {
        return WSDLConstants.QNAME_PORT;
    }

    public Documentation getDocumentation() {
        return _documentation;
    }

    public void setDocumentation(Documentation d) {
        _documentation = d;
    }

    public void withAllQNamesDo(QNameAction action) {
        super.withAllQNamesDo(action);
        if(_binding != null)
            action.perform(_binding);
    }

    public void withAllEntityReferencesDo(EntityReferenceAction action) {
        super.withAllEntityReferencesDo(action);
        if(_binding != null)
            action.perform(Kinds.BINDING, _binding);
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        visitor.preVisit(this);
        _helper.accept(visitor);
        visitor.postVisit(this);
    }

    public void validateThis() {
        if(getName() == null)
            failValidation("validation.missingRequiredAttribute", "name");
        if(_binding == null)
            failValidation("validation.missingRequiredAttribute", "binding");
    }

    public void addExtension(Extension e) {
        _helper.addExtension(e);
    }

    public Iterator extensions() {
        return _helper.extensions();
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        _helper.withAllSubEntitiesDo(action);
    }
}
