// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AbstractType.java

package com.sun.xml.rpc.processor.model;

import com.sun.xml.rpc.processor.model.java.JavaType;
import javax.xml.rpc.namespace.QName;

public abstract class AbstractType {

    private QName name;
    private JavaType javaType;

    protected AbstractType() {
    }

    protected AbstractType(QName name) {
        this.name = name;
    }

    protected AbstractType(QName name, JavaType javaType) {
        this.name = name;
        this.javaType = javaType;
    }

    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public JavaType getJavaType() {
        return javaType;
    }

    public void setJavaType(JavaType javaType) {
        this.javaType = javaType;
    }

    public boolean isNillable() {
        return false;
    }

    public boolean isSOAPType() {
        return false;
    }

    public boolean isLiteralType() {
        return false;
    }
}
