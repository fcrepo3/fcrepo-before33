// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ServiceImpl.java

package com.sun.xml.rpc.client;

import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.client:
//            ServiceImpl

public class ServiceImpl$OperationInfo {

    String namespace;
    ArrayList parameterNames;
    ArrayList parameterXmlTypes;
    String endPointAddress;
    QName returnType;
    Map properties;

    public ServiceImpl$OperationInfo() {
        namespace = "";
        parameterNames = new ArrayList();
        parameterXmlTypes = new ArrayList();
        endPointAddress = "";
        returnType = null;
        properties = new HashMap();
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace() {
        return namespace;
    }

    public void addParameter(String parameterName, QName parameterXmlType) {
        parameterNames.add(parameterName);
        parameterXmlTypes.add(parameterXmlType);
    }

    public void setReturnType(QName returnXmlType) {
        returnType = returnXmlType;
    }

    public QName getReturnType() {
        return returnType;
    }

    public String[] getParameterNames() {
        return (String[])parameterNames.toArray(new String[parameterNames.size()]);
    }

    public QName[] getParameterXmlTypes() {
        return (QName[])parameterXmlTypes.toArray(new QName[parameterXmlTypes.size()]);
    }

    public void setEndPointAddress(String address) {
        endPointAddress = address;
    }

    public String getEndPointAddress() {
        return endPointAddress;
    }

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public Iterator getPropertyKeys() {
        return properties.keySet().iterator();
    }
}
