// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Call.java

package javax.xml.rpc;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package javax.xml.rpc:
//            ParameterMode

public interface Call {

    public static final String USERNAME_PROPERTY = "javax.xml.rpc.security.auth.username";
    public static final String PASSWORD_PROPERTY = "javax.xml.rpc.security.auth.password";
    public static final String ENDPOINT_ADDRESS_PROPERTY = "javax.xml.rpc.service.endpoint.address";
    public static final String OPERATION_STYLE_PROPERTY = "javax.xml.rpc.soap.operation.style";
    public static final String SOAPACTION_USE_PROPERTY = "javax.xml.rpc.soap.http.soapaction.use";
    public static final String SOAPACTION_URI_PROPERTY = "javax.xml.rpc.soap.http.soapaction.uri";
    public static final String SESSION_MAINTAIN_PROPERTY = "javax.xml.rpc.http.session.maintain";

    public abstract boolean isParameterAndReturnSpecRequired(QName qname);

    public abstract void addParameter(String s, QName qname, ParameterMode parametermode);

    public abstract void addParameter(String s, QName qname, Class class1, ParameterMode parametermode);

    public abstract QName getParameterTypeByName(String s);

    public abstract void setReturnType(QName qname);

    public abstract void setReturnType(QName qname, Class class1);

    public abstract QName getReturnType();

    public abstract void removeAllParameters();

    public abstract QName getOperationName();

    public abstract void setOperationName(QName qname);

    public abstract QName getPortTypeName();

    public abstract void setPortTypeName(QName qname);

    public abstract void setTargetEndpointAddress(String s);

    public abstract String getTargetEndpointAddress();

    public abstract void setProperty(String s, Object obj);

    public abstract Object getProperty(String s);

    public abstract void removeProperty(String s);

    public abstract Iterator getPropertyNames();

    public abstract Object invoke(Object aobj[]) throws RemoteException;

    public abstract Object invoke(QName qname, Object aobj[]) throws RemoteException;

    public abstract void invokeOneWay(Object aobj[]);

    public abstract Map getOutputParams();
}
