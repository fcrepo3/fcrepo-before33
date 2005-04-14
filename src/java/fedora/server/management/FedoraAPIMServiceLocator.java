package fedora.server.management;

/**
 *
 * <p><b>Title:</b> FedoraAPIMServiceLocator.java</p>
 * <p><b>Description:</b> This file was originally auto-generated from the API-M WSDL
 * by the Apache Axis WSDL2Java emitter.  The generated file was then modified
 * so that it has a constructor that takes username and password, so that 
 * the service stub class can have username and passord.  
 * The following methods were modified:
 * 	getFedoraAPIMPortSOAPHTTP - custom stub (fedora.server.management.FedoraAPIM)</p>
 *
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class FedoraAPIMServiceLocator extends org.apache.axis.client.Service implements fedora.server.management.FedoraAPIMService {

	// Use to get a proxy class for FedoraAPIMPortSOAPHTTP and FedoraAPIMPortSOAPHTTPS (secure)
    private final java.lang.String FedoraAPIMPortSOAPHTTP_address = "http://localhost:8080/fedora/services/management";
	private final java.lang.String FedoraAPIMPortSOAPHTTPS_address = "https://localhost:8443/fedora/services/management";
	
    private String username=null;
    private String password=null;

    public FedoraAPIMServiceLocator(String user, String pass) {
        username=user;
        password=pass;
    }

    public java.lang.String getFedoraAPIMPortSOAPHTTPAddress() {
        return FedoraAPIMPortSOAPHTTP_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String FedoraAPIMPortSOAPHTTPWSDDServiceName = "FedoraAPIMPortSOAPHTTP";

    public java.lang.String getFedoraAPIMPortSOAPHTTPWSDDServiceName() {
        return FedoraAPIMPortSOAPHTTPWSDDServiceName;
    }

    public void setFedoraAPIMPortSOAPHTTPWSDDServiceName(java.lang.String name) {
        FedoraAPIMPortSOAPHTTPWSDDServiceName = name;
    }

    public fedora.server.management.FedoraAPIM getFedoraAPIMPortSOAPHTTP() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(FedoraAPIMPortSOAPHTTP_address);
        }
        catch (java.net.MalformedURLException e) {
            return null; // unlikely as URL was validated in WSDL2Java
        }
        return getFedoraAPIMPortSOAPHTTP(endpoint);
    }

    public fedora.server.management.FedoraAPIM getFedoraAPIMPortSOAPHTTP(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            fedora.server.management.APIMStub _stub = new fedora.server.management.APIMStub(portAddress, this, username, password);
            _stub.setPortName(getFedoraAPIMPortSOAPHTTPWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null; // ???
        }
    }
    
    //SDP - HTTPS

	public java.lang.String getFedoraAPIMPortSOAPHTTPSAddress() {
		return FedoraAPIMPortSOAPHTTPS_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String FedoraAPIMPortSOAPHTTPSWSDDServiceName = "FedoraAPIMPortSOAPHTTPS";

	public java.lang.String getFedoraAPIMPortSOAPHTTPSWSDDServiceName() {
		return FedoraAPIMPortSOAPHTTPSWSDDServiceName;
	}

	public void setFedoraAPIMPortSOAPHTTPSWSDDServiceName(java.lang.String name) {
		FedoraAPIMPortSOAPHTTPSWSDDServiceName = name;
	}

	public fedora.server.management.FedoraAPIM getFedoraAPIMPortSOAPHTTPS() throws javax.xml.rpc.ServiceException {
	   java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(FedoraAPIMPortSOAPHTTPS_address);
		}
		catch (java.net.MalformedURLException e) {
			return null; // unlikely as URL was validated in WSDL2Java
		}
		return getFedoraAPIMPortSOAPHTTPS(endpoint);
	}

	public fedora.server.management.FedoraAPIM getFedoraAPIMPortSOAPHTTPS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			fedora.server.management.APIMStub _stub = new fedora.server.management.APIMStub(portAddress, this, username, password);
			_stub.setPortName(getFedoraAPIMPortSOAPHTTPSWSDDServiceName());
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
            if (fedora.server.management.FedoraAPIM.class.isAssignableFrom(serviceEndpointInterface)) {
                fedora.server.management.APIMStub _stub = new fedora.server.management.APIMStub(new java.net.URL(FedoraAPIMPortSOAPHTTP_address), this, username, password);
                _stub.setPortName(getFedoraAPIMPortSOAPHTTPWSDDServiceName());
                return _stub;
            }
            //SDP - HTTPS (added second port for https)
			if (fedora.server.management.FedoraAPIM.class.isAssignableFrom(serviceEndpointInterface)) {
				fedora.server.management.APIMStub _stub = new fedora.server.management.APIMStub(new java.net.URL(FedoraAPIMPortSOAPHTTPS_address), this, username, password);
				_stub.setPortName(getFedoraAPIMPortSOAPHTTPSWSDDServiceName());
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
        
		//SDP - HTTPS
		//commented out old code in lieu of newly generated code for two ports.
        //java.rmi.Remote _stub = getPort(serviceEndpointInterface);
        //((org.apache.axis.client.Stub) _stub).setPortName(portName);
        //return _stub;
        
        //
		if (portName == null) {
			return getPort(serviceEndpointInterface);
		}
		String inputPortName = portName.getLocalPart();
		if ("FedoraAPIMPortSOAPHTTPS".equals(inputPortName)) {
			return getFedoraAPIMPortSOAPHTTPS();
		}
		else if ("FedoraAPIMPortSOAPHTTP".equals(inputPortName)) {
			return getFedoraAPIMPortSOAPHTTP();
		}
		else  {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.fedora.info/definitions/1/0/api/", "Fedora-API-M-Service");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("FedoraAPIMPortSOAPHTTP"));
			ports.add(new javax.xml.namespace.QName("FedoraAPIMPortSOAPHTTPS"));
        }
        return ports.iterator();
    }

}
