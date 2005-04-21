package fedora.oai;

import java.util.Date;

/**
 *
 * <p><b>Title:</b> ResumptionToken.java</p>
 * <p><b>Description:</b> A token that can be used to retrieve the remaining
 * portion of an incomplete list response.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 * @see <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#FlowControl">
 *      http://www.openarchives.org/OAI/openarchivesprotocol.html#FlowControl</a>
 */
public interface ResumptionToken {

    /**
     * Get the value of the token.
     *
     * A null value indicates that the associated list is complete.
     */
    public abstract String getValue();

    /**
     * Get the expiration date of the token.
     *
     * A null value indicates an unknown or unprovided expiration date.
     */
    public abstract Date getExpirationDate();

    /**
     * Get the size of the list.
     *
     * A negative value indicates an unknown or unprovided list size.
     */
    public abstract long getCompleteListSize();

    /**
     * Get the position in the list that this record starts at.
     *
     * A negative value indicates an unknown or unprovided position.
     */
    public abstract long getCursor();

}