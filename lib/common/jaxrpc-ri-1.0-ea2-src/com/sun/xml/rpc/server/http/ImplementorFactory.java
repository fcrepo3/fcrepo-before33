// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ImplementorFactory.java

package com.sun.xml.rpc.server.http;

import com.sun.xml.rpc.server.Tie;
import com.sun.xml.rpc.soap.message.Handler;
import java.io.InputStream;
import java.rmi.Remote;
import java.util.*;
import javax.servlet.*;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.server.ServiceLifecycle;

// Referenced classes of package com.sun.xml.rpc.server.http:
//            ImplementorRegistry, JAXRPCServletException, ImplementorInfo

public class ImplementorFactory {

    protected ServletConfig _servletConfig;
    protected ImplementorRegistry _registry;
    protected Map _cachedImplementors;

    public ImplementorFactory(ServletConfig servletConfig) {
        _registry = new ImplementorRegistry();
        _cachedImplementors = new HashMap();
        _servletConfig = servletConfig;
    }

    public ImplementorFactory(ServletConfig servletConfig, String configFilePath) {
        _registry = new ImplementorRegistry();
        _cachedImplementors = new HashMap();
        if(configFilePath == null) {
            throw new JAXRPCServletException("error.implementorFactory.noConfiguration");
        } else {
            _registry.readFrom(configFilePath);
            _servletConfig = servletConfig;
            return;
        }
    }

    public ImplementorFactory(ServletConfig servletConfig, InputStream configInputStream) {
        _registry = new ImplementorRegistry();
        _cachedImplementors = new HashMap();
        if(configInputStream == null) {
            throw new IllegalArgumentException("error.implementorFactory.noInputStream");
        } else {
            _registry.readFrom(configInputStream);
            _servletConfig = servletConfig;
            return;
        }
    }

    public Handler getImplementorFor(String name) {
        synchronized(this) {
            Handler implementor = (Handler)_cachedImplementors.get(name);
            if(implementor != null) {
                _cachedImplementors.remove(name);
                Handler handler = implementor;
                return handler;
            }
        }
        try {
            ImplementorInfo info = _registry.getImplementorInfo(name);
            Tie tie = (Tie)info.getTieClass().newInstance();
            Remote servant = (Remote)info.getServantClass().newInstance();
            tie.setTarget(servant);
            if(_servletConfig != null)
                if(servant instanceof ServiceLifecycle)
                    ((ServiceLifecycle)servant).init(_servletConfig.getServletContext());
                else
                if(servant instanceof Servlet)
                    ((Servlet)servant).init(_servletConfig);
            return (Handler)tie;
        }
        catch(IllegalAccessException illegalaccessexception) {
            throw new JAXRPCServletException("error.implementorFactory.newInstanceFailed", name);
        }
        catch(InstantiationException instantiationexception) {
            throw new JAXRPCServletException("error.implementorFactory.newInstanceFailed", name);
        }
        catch(ServletException servletexception) {
            throw new JAXRPCServletException("error.implementorFactory.servantInitFailed", name);
        }
        catch(JAXRPCServletException e) {
            throw e;
        }
        catch(JAXRPCException jaxrpcexception) {
            throw new JAXRPCServletException("error.implementorFactory.servantInitFailed", name);
        }
    }

    public void releaseImplementor(String name, Handler handler) {
        synchronized(this) {
            if(!_cachedImplementors.containsKey(name))
                _cachedImplementors.put(name, handler);
        }
    }

    public Iterator names() {
        return _registry.names();
    }

    public void destroy() {
        if(_servletConfig != null) {
            for(Iterator iter = _cachedImplementors.values().iterator(); iter.hasNext();) {
                Handler implementor = (Handler)iter.next();
                if(implementor instanceof Servlet)
                    ((Servlet)implementor).destroy();
                else
                if(implementor instanceof ServiceLifecycle)
                    ((ServiceLifecycle)implementor).destroy();
                else
                if(implementor instanceof Tie) {
                    Remote servant = ((Tie)implementor).getTarget();
                    if(servant != null)
                        if(servant instanceof ServiceLifecycle)
                            ((ServiceLifecycle)servant).destroy();
                        else
                        if(servant instanceof Servlet)
                            ((Servlet)servant).destroy();
                    ((Tie)implementor).destroy();
                }
            }

        }
        try {
            _cachedImplementors.clear();
        }
        catch(UnsupportedOperationException unsupportedoperationexception) { }
    }
}
