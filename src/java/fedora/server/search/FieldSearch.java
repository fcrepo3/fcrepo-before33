package fedora.server.search;

import java.util.List;

import fedora.server.errors.ServerException;
import fedora.server.storage.DOReader;

public interface FieldSearch {

    public void update(DOReader reader) 
            throws ServerException;
    
    public boolean delete(String pid) 
            throws ServerException;

    public List search(String[] resultFields, String terms) 
            throws ServerException;
    
    public List search(String[] resultFields, List conditions) 
            throws ServerException;
}