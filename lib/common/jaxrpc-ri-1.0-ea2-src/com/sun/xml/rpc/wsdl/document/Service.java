// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Service.java

package com.sun.xml.rpc.wsdl.document;

import com.sun.xml.rpc.wsdl.framework.*;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document:
//            Port, Kinds, WSDLConstants, WSDLDocumentVisitor, 
//            Documentation

public class Service extends GlobalEntity
    implements Extensible {

    private ExtensibilityHelper _helper;
    private Documentation _documentation;
    private List _ports;

    public Service(Defining defining) {
        super(defining);
        _ports = new ArrayList();
        _helper = new ExtensibilityHelper();
    }

    public void add(Port port) {
        port.setService(this);
        _ports.add(port);
    }

    public Iterator ports() {
        return _ports.iterator();
    }

    public Kind getKind() {
        return Kinds.SERVICE;
    }

    public QName getElementName() {
        return WSDLConstants.QNAME_SERVICE;
    }

    public Documentation getDocumentation() {
        return _documentation;
    }

    public void setDocumentation(Documentation d) {
        _documentation = d;
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        for(Iterator iter = _ports.iterator(); iter.hasNext(); action.perform((Entity)iter.next()));
        _helper.withAllSubEntitiesDo(action);
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        visitor.preVisit(this);
        for(Iterator iter = _ports.iterator(); iter.hasNext(); ((Port)iter.next()).accept(visitor));
        _helper.accept(visitor);
        visitor.postVisit(this);
    }

    public void validateThis() {
        if(getName() == null)
            failValidation("validation.missingRequiredAttribute", "name");
    }

    public void addExtension(Extension e) {
        _helper.addExtension(e);
    }

    public Iterator extensions() {
        return _helper.extensions();
    }
}
