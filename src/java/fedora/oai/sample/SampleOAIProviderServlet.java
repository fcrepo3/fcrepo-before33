package fedora.oai.sample;

import java.io.ByteArrayOutputStream;

import fedora.oai.OAIProviderServlet;
import fedora.oai.OAIResponder;
import fedora.oai.RepositoryException;

public class SampleOAIProviderServlet 
        extends OAIProviderServlet {
        
    private OAIResponder m_responder;
        
    public OAIResponder getResponder() 
            throws RepositoryException {
        if (m_responder==null) {
            m_responder=new OAIResponder(new SampleOAIProvider());
        }
        return m_responder;
    }
        
    public static void main(String[] args)
            throws Exception {
        new SampleOAIProviderServlet().test(args);
    }
    
}
