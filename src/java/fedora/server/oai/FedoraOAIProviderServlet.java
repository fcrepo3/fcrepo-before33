package fedora.server.oai;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;

import fedora.oai.OAIResponder;
import fedora.oai.OAIProviderServlet;
import fedora.oai.RepositoryException;

public class FedoraOAIProviderServlet 
        extends OAIProviderServlet {

    public OAIResponder getResponder() 
            throws RepositoryException {
        return new OAIResponder(new FedoraOAIProvider());
    }
    
    public static void main(String[] args)
            throws Exception {
        new FedoraOAIProviderServlet().test(args);
    }
        
}
