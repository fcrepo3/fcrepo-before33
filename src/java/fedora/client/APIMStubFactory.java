package fedora.client;

import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.rpc.ServiceException;

import fedora.server.management.FedoraAPIM;
import fedora.server.management.FedoraAPIMServiceLocator;

/**
 *
 * <p><b>Title:</b> APIMStubFactory.java</p>
 * <p><b>Description:</b> </p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version 1.0
 */
public abstract class APIMStubFactory {

public static FedoraAPIM getStub(String host, int port, String username, String password)
        throws MalformedURLException, ServiceException {
    FedoraAPIMServiceLocator locator=new FedoraAPIMServiceLocator(username, password);
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