// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPFaultInfo.java

package com.sun.xml.rpc.soap.message;

import javax.xml.rpc.namespace.QName;

public class SOAPFaultInfo {

    private QName code;
    private String string;
    private String actor;
    private Object detail;

    public SOAPFaultInfo(QName code, String string, String actor) {
        this(code, string, actor, null);
    }

    public SOAPFaultInfo(QName code, String string, String actor, Object detail) {
        this.code = code;
        this.string = string;
        this.actor = actor;
        this.detail = detail;
    }

    public QName getCode() {
        return code;
    }

    public void setCode(QName code) {
        this.code = code;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public Object getDetail() {
        return detail;
    }

    public void setDetail(Object detail) {
        this.detail = detail;
    }
}
