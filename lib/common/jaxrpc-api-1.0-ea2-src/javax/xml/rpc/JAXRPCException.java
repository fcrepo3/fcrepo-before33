// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JAXRPCException.java

package javax.xml.rpc;


public class JAXRPCException extends RuntimeException {

    private Throwable cause;

    public JAXRPCException() {
    }

    public JAXRPCException(String message) {
        super(message);
    }

    public JAXRPCException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public JAXRPCException(Throwable cause) {
        super(cause != null ? cause.toString() : null);
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}
