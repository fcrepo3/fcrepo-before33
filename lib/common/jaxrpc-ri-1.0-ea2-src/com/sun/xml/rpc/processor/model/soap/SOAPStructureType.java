// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPStructureType.java

package com.sun.xml.rpc.processor.model.soap;

import com.sun.xml.rpc.processor.model.ModelException;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model.soap:
//            SOAPType, SOAPStructureMember

public abstract class SOAPStructureType extends SOAPType {

    private List _members;
    private Map _membersByName;

    protected SOAPStructureType(QName name) {
        super(name);
        _members = new ArrayList();
        _membersByName = new HashMap();
    }

    public void add(SOAPStructureMember m) {
        if(_membersByName.containsKey(m.getName())) {
            throw new ModelException("model.uniqueness");
        } else {
            _members.add(m);
            _membersByName.put(m.getName(), m);
            return;
        }
    }

    public Iterator getMembers() {
        return _members.iterator();
    }

    public int getMembersCount() {
        return _members.size();
    }
}
