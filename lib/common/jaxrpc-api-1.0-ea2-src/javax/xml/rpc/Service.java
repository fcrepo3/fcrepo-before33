// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Service.java

package javax.xml.rpc;

import java.net.URL;
import java.rmi.Remote;
import java.util.Iterator;
import javax.xml.rpc.encoding.TypeMappingRegistry;
import javax.xml.rpc.handler.HandlerRegistry;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package javax.xml.rpc:
//            ServiceException, Call

public interface Service {

    public abstract Remote getPort(QName qname, Class class1) throws ServiceException;

    public abstract Remote getPort(Class class1) throws ServiceException;

    public abstract Call createCall(QName qname) throws ServiceException;

    public abstract Call createCall(QName qname, QName qname1) throws ServiceException;

    public abstract Call createCall(QName qname, String s) throws ServiceException;

    public abstract Call createCall() throws ServiceException;

    public abstract QName getServiceName();

    public abstract Iterator getPorts();

    public abstract URL getWSDLDocumentLocation();

    public abstract TypeMappingRegistry getTypeMappingRegistry();

    public abstract HandlerRegistry getHandlerRegistry();
}
