package fedora.server.errors;

/**
 * <p>Title: ObjectDependencyException.java</p>
 * <p>Description: Signals that an object has one or more related objects that
 * depend on it. For example, a behavior definition or behavior mechanism
 * object can be shared by multiple objects. Any data objects that use a
 * specific behavior definition or behavior mechanism object "depend" on
 * those objects. To remove a dependent object, you must first remove all
 * related objects.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
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