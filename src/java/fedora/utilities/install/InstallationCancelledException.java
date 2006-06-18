package fedora.utilities.install;

/**
 * Signals that the user has intentionally cancelled installation.
 */
public class InstallationCancelledException extends Exception {

    public InstallationCancelledException() {
    }

    public InstallationCancelledException(String msg) {
        super(msg);
    }

}
