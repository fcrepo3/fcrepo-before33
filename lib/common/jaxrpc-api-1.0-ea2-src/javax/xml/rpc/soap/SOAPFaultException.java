// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPFaultException.java

package javax.xml.rpc.soap;

import javax.xml.rpc.namespace.QName;
import javax.xml.soap.Detail;

public class SOAPFaultException extends Exception {

    private QName faultcode;
    private String faultstring;
    private String faultactor;
    private Detail detail;

    public SOAPFaultException(QName faultcode, String faultstring, String faultactor, Detail detail) {
        super(faultstring);
        this.faultcode = faultcode;
        this.faultstring = faultstring;
        this.faultactor = faultactor;
        this.detail = detail;
    }

    public void setFaultCode(QName faultcode) {
        this.faultcode = faultcode;
    }

    public QName getFaultCode() {
        return faultcode;
    }

    public void setFaultString(String faultstring) {
        this.faultstring = faultstring;
    }

    public String getFaultString() {
        return faultstring;
    }

    public void setFaultActor(String faultactor) {
        this.faultactor = faultactor;
    }

    public String getFaultActor() {
        return faultactor;
    }

    public Detail getDetail() {
        return detail;
    }
}
