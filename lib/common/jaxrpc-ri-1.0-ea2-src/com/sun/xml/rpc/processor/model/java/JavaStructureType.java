// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JavaStructureType.java

package com.sun.xml.rpc.processor.model.java;

import com.sun.xml.rpc.processor.model.ModelException;
import java.util.*;

// Referenced classes of package com.sun.xml.rpc.processor.model.java:
//            JavaType, JavaStructureMember

public class JavaStructureType extends JavaType {

    private List _members;
    private Map _membersByName;

    public JavaStructureType(String name, boolean present) {
        super(name, present, "null");
        _members = new ArrayList();
        _membersByName = new HashMap();
    }

    public void add(JavaStructureMember m) {
        if(_membersByName.containsKey(m.getName())) {
            throw new ModelException("model.uniqueness");
        } else {
            _members.add(m);
            _membersByName.put(m.getName(), m);
            return;
        }
    }

    public JavaStructureMember getMemberByName(String name) {
        return (JavaStructureMember)_membersByName.get(name);
    }

    public Iterator getMembers() {
        return _members.iterator();
    }

    public int getMembersCount() {
        return _members.size();
    }
}
