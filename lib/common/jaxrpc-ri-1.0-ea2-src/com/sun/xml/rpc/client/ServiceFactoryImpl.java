// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ServiceFactoryImpl.java

package com.sun.xml.rpc.client;

import java.net.URL;
import javax.xml.rpc.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.client:
//            ServiceImpl

public class ServiceFactoryImpl extends ServiceFactory {

    public ServiceFactoryImpl() {
    }

    public Service createService(URL wsdlDocumentLocation, QName name) throws ServiceException {
        return new ServiceImpl(name, wsdlDocumentLocation);
    }

    public Service createService(QName name) throws ServiceException {
        return new ServiceImpl(name);
    }
}
