package fedora.server.access;

import java.net.URL;
import javax.xml.rpc.Service;
import org.apache.axis.AxisFault;

/**
 *
 * <p><b>Title:</b> APIAStub.java</p>
 * <p><b>Description:</b> This is the auto-generated client stub, but with a
 * new constructor that takes a user/pass combo, and creates its calls with
 * those set as properties.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class APIAStub
        extends FedoraAPIABindingSOAPHTTPStub {

    public APIAStub()
            throws AxisFault {
        super(null);
    }

    public APIAStub(URL endpointURL, Service service)
            throws AxisFault {
        super(service);
        super.cachedEndpoint = endpointURL;
    }

    public APIAStub(URL endpointURL, Service service,
            String username, String password)
            throws AxisFault {
         super(service);
         super.cachedEndpoint = endpointURL;
         super.cachedUsername = username;
         super.cachedPassword = password;
    }

}
