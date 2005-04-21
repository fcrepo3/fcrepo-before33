package fedora.oai;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import fedora.common.Constants;
import fedora.oai.OAIResponder;
import fedora.oai.RepositoryException;
import fedora.server.Context;
import fedora.server.ReadOnlyContext;
import fedora.server.errors.AuthzException;
import fedora.server.errors.NotAuthorizedException;

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

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HashMap params=new HashMap();
            Enumeration enum=request.getParameterNames();
            while (enum.hasMoreElements()) {
                String name=(String) enum.nextElement();
                params.put(name, request.getParameter(name));
            }
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            Context context = ReadOnlyContext.getContext(Constants.HTTP_REQUEST.REST.uri, request, false);
            try {
            	getResponder().respond(context, params, out);
            } catch (NotAuthorizedException e) {
            	request.getRequestDispatcher("/403.jsp").forward(request, response);
            } catch (AuthzException e) {
            	request.getRequestDispatcher("/100.jsp").forward(request, response);            	
            }
            try {
                response.setContentType("text/xml; charset=UTF-8");
                response.getWriter().print(new String(out.toByteArray(), "UTF-8"));
            } catch (UnsupportedEncodingException uee) {
                // won't happen, since all java impls support UTF-8           	
            }
        } catch (RepositoryException re) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, getMessage(re));
        }
    }

    private static String getMessage(RepositoryException re) {
        String msg=re.getMessage();
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
        Context context = ReadOnlyContext.getContext(Constants.HTTP_REQUEST.REST.uri, null, false);
        try {
			getResponder().respond(context,getAsParameterMap(args), out);
		} catch (NotAuthorizedException e) {
	        System.out.println("403");
		} catch (AuthzException e) {
	        System.out.println("100");	        
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
