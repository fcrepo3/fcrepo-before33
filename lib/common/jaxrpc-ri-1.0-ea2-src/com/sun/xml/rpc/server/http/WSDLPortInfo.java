// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   WSDLPortInfo.java

package com.sun.xml.rpc.server.http;


public class WSDLPortInfo {

    private String targetNamespace;
    private String serviceName;
    private String portName;

    public WSDLPortInfo(String targetNamespace, String serviceName, String portName) {
        this.targetNamespace = targetNamespace;
        this.serviceName = serviceName;
        this.portName = portName;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getPortName() {
        return portName;
    }
}
