// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPBlockInfo.java

package com.sun.xml.rpc.soap.message;

import com.sun.xml.rpc.encoding.JAXRPCSerializer;
import javax.xml.rpc.namespace.QName;

public class SOAPBlockInfo {

    private QName _name;
    private Object _value;
    private JAXRPCSerializer _serializer;

    public SOAPBlockInfo(QName name) {
        _name = name;
    }

    public QName getName() {
        return _name;
    }

    public Object getValue() {
        return _value;
    }

    public void setValue(Object value) {
        _value = value;
    }

    public JAXRPCSerializer getSerializer() {
        return _serializer;
    }

    public void setSerializer(JAXRPCSerializer s) {
        _serializer = s;
    }
}
