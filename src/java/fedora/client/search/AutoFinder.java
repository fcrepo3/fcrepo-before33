package fedora.client.search;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.rpc.ServiceException;
import org.apache.axis.types.NonNegativeInteger;

import fedora.client.APIAStubFactory;
import fedora.server.access.FedoraAPIA;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;

public class AutoFinder {

    private FedoraAPIA m_apia;    

    public AutoFinder(String host, int port, String user, String pass) 
            throws MalformedURLException, ServiceException {
        m_apia=APIAStubFactory.getStub(host, port, user, pass);
    }

    public FieldSearchResult findObjects(String[] resultFields,
            int maxResults, FieldSearchQuery query)
            throws RemoteException {
        return findObjects(m_apia, resultFields, maxResults, query);
    }

    public FieldSearchResult resumeFindObjects(String sessionToken)
            throws RemoteException {
        return resumeFindObjects(m_apia, sessionToken);
    }

    public static FieldSearchResult findObjects(FedoraAPIA skeleton, 
            String[] resultFields, int maxResults, FieldSearchQuery query)
            throws RemoteException {
        return skeleton.findObjects(resultFields, 
                new NonNegativeInteger("" + maxResults), query);
    }

    public FieldSearchResult resumeFindObjects(FedoraAPIA skeleton, 
            String sessionToken)
            throws RemoteException {
        return skeleton.resumeFindObjects(sessionToken);
    }

    public static void main(String[] args) {
    }

}