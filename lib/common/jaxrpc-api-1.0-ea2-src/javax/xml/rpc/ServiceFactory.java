// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ServiceFactory.java

package javax.xml.rpc;

import java.net.URL;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package javax.xml.rpc:
//            ServiceException, Service

public abstract class ServiceFactory {

    private static final String SERVICEFACTORY_PROPERTY = "javax.xml.rpc.ServiceFactory";
    private static final String DEFAULT_SERVICEFACTORY = "com.sun.xml.rpc.client.ServiceFactoryImpl";

    protected ServiceFactory() {
    }

    public static ServiceFactory newInstance() throws ServiceException {
        String factoryImplName = System.getProperty("javax.xml.rpc.ServiceFactory", "com.sun.xml.rpc.client.ServiceFactoryImpl");
        try {
            Class clazz = Class.forName(factoryImplName);
            return (ServiceFactory)clazz.newInstance();
        }
        catch(ClassNotFoundException e) {
            throw new ServiceException(e);
        }
        catch(IllegalAccessException e) {
            throw new ServiceException(e);
        }
        catch(InstantiationException e) {
            throw new ServiceException(e);
        }
    }

    public abstract Service createService(URL url, QName qname) throws ServiceException;

    public abstract Service createService(QName qname) throws ServiceException;
}
