package fedora.client.utility.ingest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;
import java.util.HashMap;
import java.util.StringTokenizer;

import fedora.client.APIAStubFactory;
import fedora.client.APIMStubFactory;
import fedora.server.management.FedoraAPIM;
import fedora.server.access.FedoraAPIA;
import fedora.server.utilities.StreamUtility;
import fedora.server.types.gen.RepositoryInfo;

/**
 *
 * <p><b>Title:</b> AutoIngestor.java</p>
 * <p><b>Description:  Utility class to make API-M SOAP calls to ingest
 * objects into the repository.</b> </p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class AutoIngestor {

	private FedoraAPIA m_apia;
    private FedoraAPIM m_apim;
    private static HashMap s_repoInfo=new HashMap();

    public AutoIngestor(String host, int port, String user, String pass)
            throws MalformedURLException, ServiceException {
		m_apia=APIAStubFactory.getStub(host, port, user, pass);
        m_apim=APIMStubFactory.getStub(host, port, user, pass);
    }

	// DEPRECATED.
	// This assumes the ingest format is 'metslikefedora1' for pre-2.0 repositories.
    public String ingestAndCommit(InputStream in, String logMessage)
            throws RemoteException, IOException {
        return ingestAndCommit(m_apia, m_apim, in, logMessage);
    }
    
	// DEPRECATED.
	// This assumes the ingest format is 'metslikefedora1' for pre-2.0 repositories.
	public static String ingestAndCommit(FedoraAPIA apia, FedoraAPIM apim, InputStream in, String logMessage)
				throws RemoteException, IOException {
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		StreamUtility.pipeStream(in, out, 4096);
		String pid=apim.ingestObject(out.toByteArray(), logMessage);
		return pid;
	}
    
	public String ingestAndCommit(InputStream in, String ingestFormat, String logMessage)
			throws RemoteException, IOException {
		return ingestAndCommit(m_apia, m_apim, in, ingestFormat, logMessage);
	}
    
	public static String ingestAndCommit(FedoraAPIA apia, FedoraAPIM apim, InputStream in, 
		String ingestFormat, String logMessage)
			throws RemoteException, IOException {
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		StreamUtility.pipeStream(in, out, 4096);
		
		// For backward compatibility:
		// For pre-2.0 repositories, the only valid ingest format is "metslikefedora1"
		// and there only exists the 'ingestObject' APIM method which assumes this format.
		RepositoryInfo repoInfo = (RepositoryInfo) s_repoInfo.get(apia);
        if (repoInfo == null) {
            repoInfo = apia.describeRepository();
            s_repoInfo.put(apia, repoInfo);
        }
		StringTokenizer stoken = new StringTokenizer(repoInfo.getRepositoryVersion(), ".");
		if (new Integer(stoken.nextToken()).intValue() < 2) {
			if (!ingestFormat.equals("metslikefedora1")){
				throw new IOException("You are connected to a pre-2.0 Fedora repository " +
					"which will only accept the format \"metslikefedora1\" for ingest.");
			} else {
				return apim.ingestObject(out.toByteArray(), logMessage);				
			}

		} else {
			return apim.ingest(out.toByteArray(), ingestFormat, logMessage);
		}
	}
}