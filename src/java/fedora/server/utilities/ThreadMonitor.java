package fedora.server.utilities;

public interface ThreadMonitor 
        extends Runnable {
    
    public void requestStop();
    
}