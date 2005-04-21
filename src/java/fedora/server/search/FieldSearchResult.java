package fedora.server.search;

import java.util.Date;
import java.util.List;

/**
 *
 * <p><b>Title:</b> FieldSearchResult.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public interface FieldSearchResult {

    public List objectFieldsList();

    public String getToken();

    public long getCursor();

    public long getCompleteListSize();

    public Date getExpirationDate();

}

