package fedora.server.search;

import java.util.Date;
import java.util.List;

public interface FieldSearchResult {

    public List objectFieldsList();

    public String getToken();
    
    public long getCursor();
    
    public long getCompleteListSize();
    
    public Date getExpirationDate();
    
}    

