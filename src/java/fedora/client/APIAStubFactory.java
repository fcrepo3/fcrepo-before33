package fedora.client;

import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.rpc.ServiceException;

import fedora.server.access.FedoraAPIA;
import fedora.server.access.FedoraAPIAServiceLocator;

public abstract class APIAStubFactory {

public static FedoraAPIA getStub(String host, int port, String username, String password) 
        throws MalformedURLException, ServiceException {
    FedoraAPIAServiceLocator locator=new FedoraAPIAServiceLocator();  // user/pass n/a on api-a (yet?) username, password);
    URL ourl=new URL(locator.getFedoraAPIAPortSOAPHTTPAddress());
    StringBuffer nurl=new StringBuffer();
    nurl.append("http://");
    nurl.append(host);
    nurl.append(':');
    nurl.append(port);
    nurl.append(ourl.getPath());
    if ((ourl.getQuery()!=null) && (!ourl.getQuery().equals("")) ) {
        nurl.append('?');
        nurl.append(ourl.getQuery());
    }
    if ((ourl.getRef()!=null) && (!ourl.getRef().equals("")) ) {
        nurl.append('#');
        nurl.append(ourl.getRef());
    }
    return locator.getFedoraAPIAPortSOAPHTTP(new URL(nurl.toString()));
}

}