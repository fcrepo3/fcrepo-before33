package fedora.client;

import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.rpc.ServiceException;

import fedora.server.management.FedoraAPIM;
import fedora.server.management.FedoraAPIMServiceLocator;

public abstract class APIMSkeletonFactory {

public static FedoraAPIM getSkeleton(String host, int port) 
        throws MalformedURLException, ServiceException {
    FedoraAPIMServiceLocator locator=new FedoraAPIMServiceLocator();
    URL ourl=new URL(locator.getFedoraAPIMPortSOAPHTTPAddress());
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
    return locator.getFedoraAPIMPortSOAPHTTP(new URL(nurl.toString()));
}

}