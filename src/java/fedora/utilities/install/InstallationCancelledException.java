package fedora.utilities.install;

/**
 * Signals that the user has intentionally cancelled installation.
 */
public class InstallationCancelledException extends Exception {

	private static final long serialVersionUID = 1L;
	
    public InstallationCancelledException() {
    }

    public InstallationCancelledException(String msg) {
        super(msg);
    }

}
