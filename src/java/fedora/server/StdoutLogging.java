package fedora.server;

/**
 * A basic logging implementation that goes to stdout.
 * <p></p>
 * Useful for testing modules standalone.
 *
 * @author cwilper@cs.cornell.edu
 */
public abstract class StdoutLogging
        implements Logging { 
    
    public static final int SEVERE=0;
    public static final int WARNING=1;
    public static final int INFO=2;
    public static final int CONFIG=3;
    public static final int FINE=4;
    public static final int FINER=5;
    public static final int FINEST=6;
    
    private int m_level=0;

    public void setLogLevel(int level) {
        m_level=level;
    }

    public void logSevere(String message) {
        System.out.println("SEVERE: " + message);
    }
    
    public void logWarning(String message) {
        System.out.println("WARNING: " + message);
    }
    
    public void logInfo(String message) {
        System.out.println("INFO: " + message);
    }
    
    public void logConfig(String message) {
        System.out.println("CONFIG: " + message);
    }
    
    public void logFine(String message) {
        System.out.println("FINE: " + message);
    }
    
    public void logFiner(String message) {
        System.out.println("FINER: " + message);
    }
    
    public void logFinest(String message) {
        System.out.println("FINEST: " + message);
    }
    
    public boolean loggingSevere() {
        return m_level>=SEVERE;
    }
    
    public boolean loggingWarning() {
        return m_level>=WARNING;
    }
    
    public boolean loggingInfo() {
        return m_level>=INFO;
    }
    
    public boolean loggingConfig() {
        return m_level>=CONFIG;
    }
    
    public boolean loggingFine() {
        return m_level>=FINE;
    }
    
    public boolean loggingFiner() {
        return m_level>=FINER;
    }
    
    public boolean loggingFinest() {
        return m_level>=FINEST;
    }

}