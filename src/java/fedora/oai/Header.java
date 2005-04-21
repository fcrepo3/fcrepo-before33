package fedora.oai;

import java.util.Date;
import java.util.Set;

/**
 *
 * <p><b>Title:</b> Header.java</p>
 * <p><b>Description:</b> Describes a record in the repository with the
 * associated item identifier, record datestamp, item set membership, and
 * record deletion indicator.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 * @see <a href="http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Record">
 *      http://www.openarchives.org/OAI/2.0/openarchivesprotocol.htm#Record</a>
 */
public interface Header {

    /**
     * Get the unique identifier of the item.
     */
    public abstract String getIdentifier();

    /**
     * Get the date of creation, modification or deletion of the record (in UTC)
     * for the purpose of selective harvesting.
     */
    public abstract Date getDatestamp();

    /**
     * Get a (possibly empty) Set of Strings indicating the repository 'set'
     * membership of the item, for the purpose of selective harvesting.
     */
    public abstract Set getSetSpecs();

    /**
     * Tells whether the record is currently available.
     *
     * This should only return false if the repository supports deletions.
     */
    public abstract boolean isAvailable();

}