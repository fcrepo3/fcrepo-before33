package fedora.server.storage;

import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.Disseminator;

/**
 * The standard interface for write operations on digital objects.
 * <p></p>
 * FIXME: need to throw appropriate "all encompassing" exceptions,
 * and need to figure out how the datastream object's stream is
 * passed... not necessarily a byte array?  Or is this an interface
 * implementation specific thing?  Seems like a default impl would be
 * nice at any rate.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface DOWriter {

    public void withdraw();
    
    public void delete();
    
    public void purge();
    
    public void lock();
    
    public void unlock();
    
    public String addDatastream(Datastream datastream);
    
    public void modifyDatastream(String id, Datastream datastream);
    
    public void withdrawDatastream(String id);
    
    public void deleteDatastream(String id);
    
    public void purgeDatastream(String id);

    public String addDisseminator(Disseminator disseminator);
    
    public void modifyDisseminator(String id, Disseminator disseminator);
    
    public void withdrawDisseminator(String id);
    
    public void deleteDisseminator(String id);
    
    public void purgeDisseminator(String id);

}