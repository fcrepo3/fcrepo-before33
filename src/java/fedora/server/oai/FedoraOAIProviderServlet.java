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

/**
 *
 * <p><b>Title:</b> FedoraOAIProviderServlet.java</p>
 * <p><b>Description:</b> </p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
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
