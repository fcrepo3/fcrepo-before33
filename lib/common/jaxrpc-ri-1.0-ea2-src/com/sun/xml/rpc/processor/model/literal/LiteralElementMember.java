// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralElementMember.java

package com.sun.xml.rpc.processor.model.literal;

import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model.literal:
//            LiteralType

public class LiteralElementMember {

    private QName _name;
    private LiteralType _type;
    private JavaStructureMember _javaStructureMember;
    private boolean _nillable;
    private boolean _required;
    private boolean _repeated;

    public LiteralElementMember(QName name, LiteralType type) {
        this(name, type, null);
    }

    public LiteralElementMember(QName name, LiteralType type, JavaStructureMember javaStructureMember) {
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

    public boolean isNillable() {
        return _nillable;
    }

    public void setNillable(boolean b) {
        _nillable = b;
    }

    public boolean isRequired() {
        return _required;
    }

    public void setRequired(boolean b) {
        _required = b;
    }

    public boolean isRepeated() {
        return _repeated;
    }

    public void setRepeated(boolean b) {
        _repeated = b;
    }

    public JavaStructureMember getJavaStructureMember() {
        return _javaStructureMember;
    }

    public void setJavaStructureMember(JavaStructureMember javaStructureMember) {
        _javaStructureMember = javaStructureMember;
    }
}
