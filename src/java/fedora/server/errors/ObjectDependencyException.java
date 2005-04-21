package fedora.server.errors;

/**
 * <p><b>Title: </b>ObjectDependencyException.java</p>
 * <p><b>Description: </b>Signals that an object has one or more related objects that
 * depend on it. For example, a behavior definition or behavior mechanism
 * object can be shared by multiple objects. Any data objects that use a
 * specific behavior definition or behavior mechanism object "depend" on
 * those objects. To remove a dependent object, you must first remove all
 * related objects.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class ObjectDependencyException
        extends StorageException {

    /**
     * Creates an ObjectDependencyException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public ObjectDependencyException(String message) {
        super(message);
    }

}