/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.utility.validate.remote;

import java.io.IOException;

import java.rmi.RemoteException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.rpc.ServiceException;

import fedora.client.FedoraClient;
import fedora.client.utility.validate.ObjectSource;
import fedora.client.utility.validate.ObjectSourceException;
import fedora.client.utility.validate.ValidationObject;

import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.search.FieldSearchQuery;
import fedora.server.storage.types.DatastreamDef;
import fedora.server.storage.types.RelationshipTuple;

/**
 * An {@link ObjectSource} that is based on a {@link FedoraClient} link to a
 * remote server.
 * 
 * @author Jim Blake
 */
public class RemoteObjectSource
        implements ObjectSource {

    private final FedoraAPIA apia;

    private final FedoraAPIM apim;

    /**
     * @param parms
     * @throws IOException
     * @throws ServiceException
     */
    public RemoteObjectSource(ServiceInfo serviceInfo)
            throws ServiceException, IOException {
        FedoraClient fc =
                new FedoraClient(serviceInfo.getBaseUrlString(), serviceInfo
                        .getUsername(), serviceInfo.getPassword());
        apia = fc.getAPIA();
        apim = fc.getAPIM();
    }

    public Iterator<String> findObjectPids(FieldSearchQuery query)
            throws ObjectSourceException {
        return new RemotePidIterator(apia, query);
    }

    /**
     * {@inheritDoc}
     */
    public ValidationObject getValidationObject(String pid)
            throws ObjectSourceException {
        List<RelationshipTuple> relations = getRelationships(pid);
        Set<DatastreamDef> dsDefs = getDatastreamDefs(pid);
        return new ValidationObject(pid, relations, dsDefs);
    }

    private Set<DatastreamDef> getDatastreamDefs(String pid)
            throws ObjectSourceException {
        try {
            fedora.server.types.gen.DatastreamDef[] datastreams =
                    apia.listDatastreams(pid, null);
            return new HashSet<DatastreamDef>(TypeUtility
                    .convertGenDatastreamDefArrayToDatastreamDefList(datastreams));
        } catch (RemoteException e) {
            throw new ObjectSourceException(e);
        }
    }

    private List<RelationshipTuple> getRelationships(String pid)
            throws ObjectSourceException {
        try {
            fedora.server.types.gen.RelationshipTuple[] tuples =
                    apim.getRelationships(pid, null);
            return TypeUtility.convertGenRelsTupleArrayToRelsTupleList(tuples);
        } catch (RemoteException e) {
            throw new ObjectSourceException(e);
        }
    }
}
