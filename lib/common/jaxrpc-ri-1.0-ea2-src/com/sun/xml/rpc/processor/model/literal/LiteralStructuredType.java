// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralStructuredType.java

package com.sun.xml.rpc.processor.model.literal;

import com.sun.xml.rpc.processor.model.ModelException;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model.literal:
//            LiteralAttributeOwningType, LiteralElementMember

public abstract class LiteralStructuredType extends LiteralAttributeOwningType {

    private List _elementMembers;
    private Map _elementMembersByName;

    public LiteralStructuredType(QName name) {
        this(name, null);
    }

    public LiteralStructuredType(QName name, JavaStructureType javaType) {
        super(name, javaType);
        _elementMembers = new ArrayList();
        _elementMembersByName = new HashMap();
    }

    public void add(LiteralElementMember m) {
        if(_elementMembersByName.containsKey(m.getName())) {
            throw new ModelException("model.uniqueness");
        } else {
            _elementMembers.add(m);
            _elementMembersByName.put(m.getName(), m);
            return;
        }
    }

    public Iterator getElementMembers() {
        return _elementMembers.iterator();
    }

    public int getElementMembersCount() {
        return _elementMembers.size();
    }
}
