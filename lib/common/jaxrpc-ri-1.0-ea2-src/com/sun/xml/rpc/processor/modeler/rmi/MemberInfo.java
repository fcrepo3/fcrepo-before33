// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   MemberInfo.java

package com.sun.xml.rpc.processor.modeler.rmi;

import sun.tools.java.Type;

public class MemberInfo {

    private Type _type;
    private boolean _isPublic;
    private String _readMethod;
    private String _writeMethod;
    private String _name;

    private MemberInfo() {
        _isPublic = false;
    }

    public MemberInfo(String name, Type type, boolean isPublic) {
        _isPublic = false;
        _type = type;
        _isPublic = isPublic;
        _name = name;
    }

    public MemberInfo(Type type, boolean isPublic) {
        _isPublic = false;
        _type = type;
        _isPublic = isPublic;
    }

    public Type getType() {
        return _type;
    }

    public boolean isPublic() {
        return _isPublic;
    }

    public String getReadMethod() {
        return _readMethod;
    }

    public void setReadMethod(String readMethod) {
        _readMethod = readMethod;
    }

    public String getWriteMethod() {
        return _writeMethod;
    }

    public void setWriteMethod(String writeMethod) {
        _writeMethod = writeMethod;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }
}
