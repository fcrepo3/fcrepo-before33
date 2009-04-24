/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.lowlevel.akubra;

import java.io.InputStream;

import java.util.Iterator;
import java.util.Map;

import org.fedoracommons.akubra.BlobStore;

import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.LowlevelStorageException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.storage.lowlevel.IListable;
import fedora.server.storage.lowlevel.ILowlevelStorage;

/**
 * Wraps an {@link AkubraLowlevelStore} instance as a {@link Module}.
 *
 * @author Chris Wilper
 */
public class AkubraLowlevelStorageModule
        extends Module
        implements ILowlevelStorage, IListable {

    private ILowlevelStorage impl;

    public AkubraLowlevelStorageModule(Map<String, String> moduleParameters,
                                       Server server,
                                       String role)
            throws ModuleInitializationException {
        super(moduleParameters, server, role);
    }

    @Override
    public void postInitModule() throws ModuleInitializationException {
        // TODO: Instantiate via spring
        BlobStore objectStore = null;
        BlobStore datastreamStore = null;
        boolean forceSafeObjectOverwrites = true;
        boolean forceSafeDatastreamOverwrites = true;
        impl = new AkubraLowlevelStorage(objectStore,
                                         datastreamStore,
                                         forceSafeObjectOverwrites,
                                         forceSafeDatastreamOverwrites);
    }

    public void addObject(String pid, InputStream content)
            throws LowlevelStorageException {
        impl.addObject(pid, content);
    }

    public void replaceObject(String pid, InputStream content)
            throws LowlevelStorageException {
        impl.replaceObject(pid, content);
    }

    public InputStream retrieveObject(String pid)
            throws LowlevelStorageException {
        return impl.retrieveObject(pid);
    }

    public void removeObject(String pid) throws LowlevelStorageException {
        impl.removeObject(pid);
    }

    public void rebuildObject() throws LowlevelStorageException {
        impl.rebuildObject();
    }

    public void auditObject() throws LowlevelStorageException {
        impl.auditObject();
    }

    public void addDatastream(String pid, InputStream content)
            throws LowlevelStorageException {
        impl.addDatastream(pid, content);
    }

    public void replaceDatastream(String pid, InputStream content)
            throws LowlevelStorageException {
        impl.replaceDatastream(pid, content);
    }

    public InputStream retrieveDatastream(String pid)
            throws LowlevelStorageException {
        return impl.retrieveDatastream(pid);
    }

    public void removeDatastream(String pid) throws LowlevelStorageException {
        impl.removeDatastream(pid);
    }

    public void rebuildDatastream() throws LowlevelStorageException {
        impl.rebuildDatastream();
    }

    public void auditDatastream() throws LowlevelStorageException {
        impl.auditDatastream();
    }

    // IListable methods

    public Iterator<String> listObjects() {
        return ((IListable) impl).listObjects();
    }

    public Iterator<String> listDatastreams() {
        return ((IListable) impl).listDatastreams();
    }
}
