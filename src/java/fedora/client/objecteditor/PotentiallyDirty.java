package fedora.client.objecteditor;

/**
 * Interface for containers that report on dirtiness of sub-components.
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public interface PotentiallyDirty {

    /**
     * Have my editable components changed since being loaded from the server?
     */
    public boolean isDirty();

}