package fedora.utilities.install;

/**
 * Signals that installation failed.
 */
public class InstallationFailedException extends Exception {

    public InstallationFailedException(String msg) {
        super(msg);
    }

    public InstallationFailedException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
