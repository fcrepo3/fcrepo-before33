package fedora.server.search;

import fedora.server.errors.ServerException;
import fedora.server.storage.DOReader;

public interface FieldSearch {

    public void update(DOReader reader) throws ServerException;
    
    public void delete(String pid) throws ServerException;

    public Object[][] search(String[] resultFields, String condition, int firstResultIndex, int lastResultIndex) throws ServerException;
    
    public int count(String condition) throws ServerException;
}