// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Message.java

package com.sun.xml.rpc.wsdl.document;

import com.sun.xml.rpc.wsdl.framework.*;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document:
//            MessagePart, Kinds, WSDLConstants, WSDLDocumentVisitor, 
//            Documentation

public class Message extends GlobalEntity {

    private Documentation _documentation;
    private List _parts;
    private Map _partsByName;

    public Message(Defining defining) {
        super(defining);
        _parts = new ArrayList();
        _partsByName = new HashMap();
    }

    public void add(MessagePart part) {
        if(_partsByName.get(part.getName()) != null) {
            throw new ValidationException("validation.duplicateName", part.getName());
        } else {
            _partsByName.put(part.getName(), part);
            _parts.add(part);
            return;
        }
    }

    public Iterator parts() {
        return _parts.iterator();
    }

    public MessagePart getPart(String name) {
        return (MessagePart)_partsByName.get(name);
    }

    public Kind getKind() {
        return Kinds.MESSAGE;
    }

    public QName getElementName() {
        return WSDLConstants.QNAME_MESSAGE;
    }

    public Documentation getDocumentation() {
        return _documentation;
    }

    public void setDocumentation(Documentation d) {
        _documentation = d;
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        super.withAllSubEntitiesDo(action);
        for(Iterator iter = _parts.iterator(); iter.hasNext(); action.perform((Entity)iter.next()));
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        visitor.preVisit(this);
        for(Iterator iter = _parts.iterator(); iter.hasNext(); ((MessagePart)iter.next()).accept(visitor));
        visitor.postVisit(this);
    }

    public void validateThis() {
        if(getName() == null)
            failValidation("validation.missingRequiredAttribute", "name");
    }
}
