// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ServiceException.java

package javax.xml.rpc;


public class ServiceException extends Exception {

    private Throwable cause;

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public ServiceException(Throwable cause) {
        super(cause != null ? cause.toString() : null);
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
