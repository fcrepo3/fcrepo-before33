package fedora.client;

import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.rpc.ServiceException;

import fedora.server.access.FedoraAPIA;
import fedora.server.access.FedoraAPIAServiceLocator;

/**
 *
 * <p><b>Title:</b> APIAStubFactory.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
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
    if (Administrator.INSTANCE==null) {
        // if running without Administrator, don't wrap it with the statusbar stuff
        return locator.getFedoraAPIAPortSOAPHTTP(new URL(nurl.toString()));
    } else {
        return new APIAStubWrapper(locator.getFedoraAPIAPortSOAPHTTP(new URL(nurl.toString())));
    }
}

}