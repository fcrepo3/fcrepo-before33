// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPStructureMember.java

package com.sun.xml.rpc.processor.model.soap;

import com.sun.xml.rpc.processor.model.java.JavaStructureMember;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model.soap:
//            SOAPType

public class SOAPStructureMember {

    private QName _name;
    private SOAPType _type;
    private JavaStructureMember _javaStructureMember;

    public SOAPStructureMember(QName name, SOAPType type) {
        this(name, type, null);
    }

    public SOAPStructureMember(QName name, SOAPType type, JavaStructureMember javaStructureMember) {
        _name = name;
        _type = type;
        _javaStructureMember = javaStructureMember;
    }

    public QName getName() {
        return _name;
    }

    public SOAPType getType() {
        return _type;
    }

    public JavaStructureMember getJavaStructureMember() {
        return _javaStructureMember;
    }

    public void setJavaStructureMember(JavaStructureMember javaStructureMember) {
        _javaStructureMember = javaStructureMember;
    }
}
