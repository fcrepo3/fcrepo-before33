package fedora.server.access;

/**
 *
 * <p><b>Title:</b> FedoraAPIAServiceLocator.java</p>
 * <p><b>Description:</b> This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter</p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class FedoraAPIAServiceLocator extends org.apache.axis.client.Service implements fedora.server.access.FedoraAPIAService {

    // Use to get a proxy class for FedoraAPIAPortSOAPHTTP
    private final java.lang.String FedoraAPIAPortSOAPHTTP_address = "http://localhost:8080/fedora/access/soap"; //wdn5e:wdn5e@

    private String username=null;
    private String password=null;

    public FedoraAPIAServiceLocator(String user, String pass) {
        username=user;
        password=pass;
    }
    
    public FedoraAPIAServiceLocator() { // for AccessConsole
        username="nobody";
        password="nobody";
    }

    public java.lang.String getFedoraAPIAPortSOAPHTTPAddress() {
        return FedoraAPIAPortSOAPHTTP_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String FedoraAPIAPortSOAPHTTPWSDDServiceName = "FedoraAPIAPortSOAPHTTP";

    public java.lang.String getFedoraAPIAPortSOAPHTTPWSDDServiceName() {
        return FedoraAPIAPortSOAPHTTPWSDDServiceName;
    }

    public void setFedoraAPIAPortSOAPHTTPWSDDServiceName(java.lang.String name) {
        FedoraAPIAPortSOAPHTTPWSDDServiceName = name;
    }

    public fedora.server.access.FedoraAPIA getFedoraAPIAPortSOAPHTTP() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(FedoraAPIAPortSOAPHTTP_address);
        }
        catch (java.net.MalformedURLException e) {
            return null; // unlikely as URL was validated in WSDL2Java
        }
        return getFedoraAPIAPortSOAPHTTP(endpoint);
    }

    public fedora.server.access.FedoraAPIA getFedoraAPIAPortSOAPHTTP(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            fedora.server.access.APIAStub _stub = new fedora.server.access.APIAStub(portAddress, this, username, password);
            _stub.setPortName(getFedoraAPIAPortSOAPHTTPWSDDServiceName());
            // _stub._setProperty("httpclient.authentication.preemptive","true");
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null; // ???
        }
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (fedora.server.access.FedoraAPIA.class.isAssignableFrom(serviceEndpointInterface)) {
                fedora.server.access.APIAStub _stub = new fedora.server.access.APIAStub(new java.net.URL(FedoraAPIAPortSOAPHTTP_address), this, username, password);
                _stub.setPortName(getFedoraAPIAPortSOAPHTTPWSDDServiceName());
                // _stub._setProperty("httpclient.authentication.preemptive","true");                
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        java.rmi.Remote _stub = getPort(serviceEndpointInterface);
        ((org.apache.axis.client.Stub) _stub).setPortName(portName);
        return _stub;
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.fedora.info/definitions/1/0/api/", "Fedora-API-M-Service");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("FedoraAPIAPortSOAPHTTP"));
        }
        return ports.iterator();
    }

}
