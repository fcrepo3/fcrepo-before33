// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JAXRPCServlet.java

package com.sun.xml.rpc.server.http;

import com.sun.xml.rpc.util.localization.LocalizableMessageFactory;
import com.sun.xml.rpc.util.localization.Localizer;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogSource;

// Referenced classes of package com.sun.xml.rpc.server.http:
//            ServletDelegate

public class JAXRPCServlet extends HttpServlet {

    protected ServletDelegate _flddelegate;
    private LocalizableMessageFactory messageFactory;
    private Localizer localizer;
    private static final String DELEGATE_PROPERTY = "delegate";
    private static final String DEFAULT_DELEGATE_CLASS_NAME = "com.sun.xml.rpc.server.http.JAXRPCServletDelegate";
    private static final Log logger = LogSource.getInstance("com.sun.xml.rpc.server.http");

    public JAXRPCServlet() {
        _flddelegate = null;
    }

    public void init(ServletConfig servletConfig) throws ServletException {
        localizer = new Localizer();
        messageFactory = new LocalizableMessageFactory("com.sun.xml.rpc.resources.jaxrpcservlet");
        try {
            String delegateClassName = servletConfig.getInitParameter("delegate");
            if(delegateClassName == null)
                delegateClassName = "com.sun.xml.rpc.server.http.JAXRPCServletDelegate";
            Class delegateClass = Class.forName(delegateClassName, true, Thread.currentThread().getContextClassLoader());
            _flddelegate = (ServletDelegate)delegateClass.newInstance();
            _flddelegate.init(servletConfig);
        }
        catch(ServletException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        catch(Throwable e) {
            String message = localizer.localize(messageFactory.getMessage("error.servlet.caughtThrowableInInit", new Object[] {
                e
            }));
            logger.error(message, e);
            throw new ServletException(message);
        }
    }

    public void destroy() {
        if(_flddelegate != null)
            _flddelegate.destroy();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if(_flddelegate != null)
            _flddelegate.doPost(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        if(_flddelegate != null)
            _flddelegate.doGet(request, response);
    }

}
