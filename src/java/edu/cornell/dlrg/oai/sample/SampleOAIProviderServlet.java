package edu.cornell.dlrg.oai.sample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import edu.cornell.dlrg.oai.OAIResponder;
import edu.cornell.dlrg.oai.RepositoryException;

public class SampleOAIProviderServlet 
        extends HttpServlet {
        
    private OAIResponder m_responder;
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/xml");
        try {
            m_responder.respond(request.getParameterMap(), response.getOutputStream());
            response.flushBuffer();
        } catch (RepositoryException re) {
            String msg=re.getMessage();
            if (msg==null) {
                msg="An unexpected error occured in the underlying repository. "
                        + "No further information is available.";
            }
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
        }
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    public void init() {
        m_responder=new OAIResponder(new SampleOAIProvider());
    }
        
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

    public static void main(String[] args) 
            throws RepositoryException {
        OAIResponder responder=new OAIResponder(new SampleOAIProvider());
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        responder.respond(getAsParameterMap(args), out);
        System.out.println(new String(out.toByteArray()));
    }
    
}
