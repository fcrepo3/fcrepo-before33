// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   PortType.java

package com.sun.xml.rpc.wsdl.document;

import com.sun.xml.rpc.wsdl.framework.*;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.document:
//            Operation, Kinds, WSDLConstants, WSDLDocumentVisitor, 
//            Documentation

public class PortType extends GlobalEntity {

    private Documentation _documentation;
    private List _operations;
    private Set _operationKeys;

    public PortType(Defining defining) {
        super(defining);
        _operations = new ArrayList();
        _operationKeys = new HashSet();
    }

    public void add(Operation operation) {
        String key = operation.getUniqueKey();
        if(_operationKeys.contains(key)) {
            throw new ValidationException("validation.ambiguousName", operation.getName());
        } else {
            _operationKeys.add(key);
            _operations.add(operation);
            return;
        }
    }

    public Iterator operations() {
        return _operations.iterator();
    }

    public Set getOperationsNamed(String s) {
        Set result = new HashSet();
        for(Iterator iter = _operations.iterator(); iter.hasNext();) {
            Operation operation = (Operation)iter.next();
            if(operation.getName().equals(s))
                result.add(operation);
        }

        return result;
    }

    public Kind getKind() {
        return Kinds.PORT_TYPE;
    }

    public QName getElementName() {
        return WSDLConstants.QNAME_PORT_TYPE;
    }

    public Documentation getDocumentation() {
        return _documentation;
    }

    public void setDocumentation(Documentation d) {
        _documentation = d;
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        super.withAllSubEntitiesDo(action);
        for(Iterator iter = _operations.iterator(); iter.hasNext(); action.perform((Entity)iter.next()));
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        visitor.preVisit(this);
        for(Iterator iter = _operations.iterator(); iter.hasNext(); ((Operation)iter.next()).accept(visitor));
        visitor.postVisit(this);
    }

    public void validateThis() {
        if(getName() == null)
            failValidation("validation.missingRequiredAttribute", "name");
    }
}
