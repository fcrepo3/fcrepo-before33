package fedora.oai;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import fedora.common.Constants;
import fedora.oai.OAIResponder;
import fedora.oai.RepositoryException;
import fedora.server.Context;
import fedora.server.ReadOnlyContext;
import fedora.server.errors.authorization.AuthzDeniedException;
import fedora.server.errors.authorization.AuthzException;
import fedora.server.errors.authorization.AuthzOperationalException;
import fedora.server.errors.authorization.AuthzPermittedException;
import fedora.server.errors.servletExceptionExtensions.InternalError500Exception;
import fedora.server.errors.servletExceptionExtensions.RootException;

/**
 *
 * <p><b>Title:</b> OAIProviderServlet.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public abstract class OAIProviderServlet
        extends HttpServlet {

    OAIResponder m_responder;

    public OAIProviderServlet() {
    }
    
    public static final String ACTION_LABEL = "OAI";

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	String actionLabel = "OAI request";
        try {
            HashMap params=new HashMap();
            Enumeration enum=request.getParameterNames();
            while (enum.hasMoreElements()) {
                String name=(String) enum.nextElement();
                params.put(name, request.getParameter(name));
            }
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            Context context = ReadOnlyContext.getContext(Constants.HTTP_REQUEST.REST.uri, request);
            try {
            	getResponder().respond(context, params, out);
        	} catch (AuthzException ae) {            
                throw RootException.getServletException (ae, request, ACTION_LABEL, new String[0]);		   	    			
            }
            response.setContentType("text/xml; charset=UTF-8");
            response.getWriter().print(new String(out.toByteArray(), "UTF-8"));
        } catch (Throwable t) {
        	throw new InternalError500Exception("", t, request, ACTION_LABEL, "", new String[0]);            
        }
    }

    private static String getMessage(Throwable t) {
        String msg=t.getMessage();
        if (msg==null) {
            msg="Unexpected repository error.";
        }
        return msg;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public void init()
            throws ServletException {
        try {
            m_responder=getResponder();
        } catch (RepositoryException re) {
            throw new ServletException(getMessage(re));
        }
    }

    public void test(String[] args)
            throws OAIException, RepositoryException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        Context context = ReadOnlyContext.getContext(Constants.HTTP_REQUEST.REST.uri, null);
        try {
			getResponder().respond(context,getAsParameterMap(args), out);
		} catch (AuthzOperationalException aoe) {
	        System.out.println("403 - operational");
		} catch (AuthzDeniedException ade) {
	        System.out.println("403");
		} catch (AuthzPermittedException ape) {
	        System.out.println("100");	 
		} catch (AuthzException ae) {
	        System.out.println("403 - general");	        
        }			
        System.out.println(new String(out.toByteArray()));
    }

    public abstract OAIResponder getResponder()
            throws RepositoryException;

    public static HashMap getAsParameterMap(String[] args) {
        HashMap h=new HashMap();
        for (int i=0; i<args.length; i++) {
            String arg=args[i];
            int pos=arg.indexOf("=");
            if (pos!=-1) {
                String name=arg.substring(0, pos);
                String value=arg.substring(pos+1);
                h.put(name, value);
            }
        }
        return h;
    }

}
