package fedora.server.search;

import fedora.server.errors.ServerException;
import fedora.server.storage.types.DigitalObject;

public interface Search {

    public void update(DigitalObject obj) throws ServerException;
    
    public void delete(String pid) throws ServerException;

    

}