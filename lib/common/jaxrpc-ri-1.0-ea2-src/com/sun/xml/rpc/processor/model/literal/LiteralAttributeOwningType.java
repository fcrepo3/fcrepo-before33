// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralAttributeOwningType.java

package com.sun.xml.rpc.processor.model.literal;

import com.sun.xml.rpc.processor.model.ModelException;
import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model.literal:
//            LiteralType, LiteralAttributeMember

public abstract class LiteralAttributeOwningType extends LiteralType {

    private List _attributeMembers;
    private Map _attributeMembersByName;

    public LiteralAttributeOwningType(QName name) {
        this(name, null);
    }

    public LiteralAttributeOwningType(QName name, JavaStructureType javaType) {
        super(name, javaType);
        _attributeMembers = new ArrayList();
        _attributeMembersByName = new HashMap();
    }

    public void add(LiteralAttributeMember m) {
        if(_attributeMembersByName.containsKey(m.getName())) {
            throw new ModelException("model.uniqueness");
        } else {
            _attributeMembers.add(m);
            _attributeMembersByName.put(m.getName(), m);
            return;
        }
    }

    public Iterator getAttributeMembers() {
        return _attributeMembers.iterator();
    }

    public int getAttributeMembersCount() {
        return _attributeMembers.size();
    }
}
