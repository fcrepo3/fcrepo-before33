// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralAttributeMember.java

package com.sun.xml.rpc.processor.model.literal;

import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model.literal:
//            LiteralType

public class LiteralAttributeMember {

    private QName _name;
    private LiteralType _type;
    private JavaStructureMember _javaStructureMember;
    private boolean _required;

    public LiteralAttributeMember(QName name, LiteralType type) {
        this(name, type, null);
    }

    public LiteralAttributeMember(QName name, LiteralType type, JavaStructureMember javaStructureMember) {
        _name = name;
        _type = type;
        _javaStructureMember = javaStructureMember;
    }

    public QName getName() {
        return _name;
    }

    public LiteralType getType() {
        return _type;
    }

    public JavaStructureMember getJavaStructureMember() {
        return _javaStructureMember;
    }

    public void setJavaStructureMember(JavaStructureMember javaStructureMember) {
        _javaStructureMember = javaStructureMember;
    }

    public boolean isRequired() {
        return _required;
    }

    public void setRequired(boolean b) {
        _required = b;
    }
}
