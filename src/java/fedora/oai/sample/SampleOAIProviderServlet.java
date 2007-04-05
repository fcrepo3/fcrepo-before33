/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.oai.sample;

import fedora.oai.OAIProviderServlet;
import fedora.oai.OAIResponder;
import fedora.oai.RepositoryException;

/**
 *
 * <p><b>Title:</b> SampleOAIProviderServlet.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class SampleOAIProviderServlet
        extends OAIProviderServlet {

	private static final long serialVersionUID = 1L;
	
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
