package fedora.oai;

/**
 * An exception occuring as a result of a problem in the underlying repository
 * system.
 */
public class RepositoryException 
        extends Exception {

    public RepositoryException(String message) {
        super(message);
    }
    
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
    
}