package fedora.server.oai;

import java.io.File;
import java.util.HashSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;

import fedora.oai.OAIResponder;
import fedora.oai.OAIProvider;
import fedora.oai.OAIProviderServlet;
import fedora.oai.RepositoryException;
import fedora.server.Server;

public class FedoraOAIProviderServlet 
        extends OAIProviderServlet {
        
    OAIResponder m_responder;

    public OAIResponder getResponder() 
            throws RepositoryException {
        if (m_responder==null) {
            try {
                Server server=Server.getInstance(new File(System.getProperty("fedora.home")));
                OAIProvider provider=(OAIProvider) server.getModule("fedora.oai.OAIProvider");
                m_responder=new OAIResponder(provider);
            } catch (Exception e) {
                throw new RepositoryException(e.getClass().getName() + ": " + e.getMessage());
            }
        }
        return m_responder;
    }
    
    public static void main(String[] args)
            throws Exception {
        new FedoraOAIProviderServlet().test(args);
    }
        
}
