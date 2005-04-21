package fedora.server;

/**
 *
 * <p><b>Title:</b> Logging.java</p>
 * <p><b>Description:</b> A class that has methods for logging.</p>
 *
 * <p>The methods starting with <i>logging</i> can be used to quickly
 * check whether a certain type of logging is enabled.  This
 * helps in situations where the process of building the log
 * message (before calling <i>logXXX</i>) takes enough time that
 * it should not run unless necessary.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public interface Logging {

    public abstract void logSevere(String message);
    public abstract void logWarning(String message);
    public abstract void logInfo(String message);
    public abstract void logConfig(String message);
    public abstract void logFine(String message);
    public abstract void logFiner(String message);
    public abstract void logFinest(String message);

    public abstract boolean loggingSevere();
    public abstract boolean loggingWarning();
    public abstract boolean loggingInfo();
    public abstract boolean loggingConfig();
    public abstract boolean loggingFine();
    public abstract boolean loggingFiner();
    public abstract boolean loggingFinest();

}