// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   RmiModelInfo.java

package com.sun.xml.rpc.processor.config;

import com.sun.xml.rpc.processor.modeler.Modeler;
import com.sun.xml.rpc.processor.modeler.rmi.RmiModeler;
import java.util.*;

// Referenced classes of package com.sun.xml.rpc.processor.config:
//            ModelInfo, RmiServiceInfo

public class RmiModelInfo extends ModelInfo {

    private String targetNamespaceURI;
    private String typeNamespaceURI;
    private List services;

    public RmiModelInfo() {
        services = new ArrayList();
    }

    public String getTargetNamespaceURI() {
        return targetNamespaceURI;
    }

    public void setTargetNamespaceURI(String s) {
        targetNamespaceURI = s;
    }

    public String getTypeNamespaceURI() {
        return typeNamespaceURI;
    }

    public void setTypeNamespaceURI(String s) {
        typeNamespaceURI = s;
    }

    public void add(RmiServiceInfo i) {
        services.add(i);
        i.setParent(this);
    }

    public Iterator getRmiServices() {
        return services.iterator();
    }

    protected Modeler getModeler(Properties options) {
        return new RmiModeler(this);
    }
}
