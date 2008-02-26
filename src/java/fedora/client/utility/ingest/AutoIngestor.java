/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.utility.ingest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;

import java.rmi.RemoteException;

import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.rpc.ServiceException;

import fedora.common.Constants;

import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.RepositoryInfo;
import fedora.server.utilities.StreamUtility;

/**
 * Makes API-M SOAP calls to ingest objects into the repository.
 * 
 * @author Chris Wilper
 */
public class AutoIngestor
        implements Constants {

    private final FedoraAPIA m_apia;

    private final FedoraAPIM m_apim;

    private static HashMap<FedoraAPIA, RepositoryInfo> s_repoInfo =
            new HashMap<FedoraAPIA, RepositoryInfo>();

    public AutoIngestor(FedoraAPIA apia, FedoraAPIM apim)
            throws MalformedURLException, ServiceException {
        m_apia = apia;
        m_apim = apim;
    }

    /**
     * @deprecated use ingestAndCommit(in, ingestFormat, logMessage) instead.
     */
    @Deprecated
    public String ingestAndCommit(InputStream in, String logMessage)
            throws RemoteException, IOException {
        return ingestAndCommit(m_apia, m_apim, in, logMessage);
    }

    /**
     * For backward compatibility: assumes format METS_EXT1_0.uri
     * 
     * @deprecated use ingestAndCommit(apia, apim, in, ingestFormat, logMessage)
     *             instead.
     */
    @Deprecated
    public static String ingestAndCommit(FedoraAPIA apia,
                                         FedoraAPIM apim,
                                         InputStream in,
                                         String logMessage)
            throws RemoteException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamUtility.pipeStream(in, out, 4096);
        String pid = apim.ingest(out.toByteArray(), METS_EXT1_0.uri, logMessage);
        return pid;
    }

    public String ingestAndCommit(InputStream in,
                                  String ingestFormat,
                                  String logMessage) throws RemoteException,
            IOException {
        return ingestAndCommit(m_apia, m_apim, in, ingestFormat, logMessage);
    }

    public static String ingestAndCommit(FedoraAPIA apia,
                                         FedoraAPIM apim,
                                         InputStream in,
                                         String ingestFormat,
                                         String logMessage)
            throws RemoteException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamUtility.pipeStream(in, out, 4096);

        // For backward compatibility:
        // For pre-2.0 repositories, the only valid ingest format is
        // METS_EXT1_0.uri (formerly known as metslikefedora1)
        RepositoryInfo repoInfo = s_repoInfo.get(apia);
        if (repoInfo == null) {
            repoInfo = apia.describeRepository();
            s_repoInfo.put(apia, repoInfo);
        }
        StringTokenizer stoken =
                new StringTokenizer(repoInfo.getRepositoryVersion(), ".");
        if (new Integer(stoken.nextToken()).intValue() < 2) {
            if (!ingestFormat.equals(METS_EXT1_0.uri)) {
                throw new IOException("You are connected to a pre-2.0 Fedora repository "
                        + "which will only accept the format \""
                        + METS_EXT1_0.uri + "\" for ingest.");
            } else {
                return apim.ingest(out.toByteArray(), METS_EXT1_0.uri, logMessage);
            }

        } else {
            return apim.ingest(out.toByteArray(), ingestFormat, logMessage);
        }
    }
}