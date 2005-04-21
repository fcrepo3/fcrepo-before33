package fedora.oai;

import java.util.Set;

/**
 *
 * <p><b>Title:</b> SetInfo.java</p>
 * <p><b>Description:</b> Describes a set in the repository.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 * @see <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#ListSets">
 *      http://www.openarchives.org/OAI/openarchivesprotocol.html#ListSets</a>
 */
public interface SetInfo {

    /**
     * Get the name of the set.
     */
    public abstract String getName();

    /**
     * Get the setSpec of the set.
     */
    public abstract String getSpec();

    /**
     * Get the descriptions of the set.
     */
    public abstract Set getDescriptions();

}