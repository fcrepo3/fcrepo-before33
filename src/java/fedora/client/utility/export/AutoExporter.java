/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.utility.export;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.net.MalformedURLException;

import java.rmi.RemoteException;

import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.ServiceException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import org.w3c.dom.Document;

import fedora.common.Constants;

import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.RepositoryInfo;

/**
 * Utility class for exporting objects from a Fedora repository.
 * 
 * @author Chris Wilper
 */
public class AutoExporter
        implements Constants {

    private final FedoraAPIM m_apim;

    private final FedoraAPIA m_apia;

    private static HashMap<FedoraAPIA, RepositoryInfo> s_repoInfo =
            new HashMap<FedoraAPIA, RepositoryInfo>();

    public AutoExporter(FedoraAPIA apia, FedoraAPIM apim)
            throws MalformedURLException, ServiceException {
        m_apia = apia;
        m_apim = apim;
    }

    public void export(String pid,
                       String format,
                       String exportContext,
                       OutputStream outStream) throws RemoteException,
            IOException {
        export(m_apia, m_apim, pid, format, exportContext, outStream);
    }

    public static void export(FedoraAPIA apia,
                              FedoraAPIM apim,
                              String pid,
                              String format,
                              String exportContext,
                              OutputStream outStream) throws RemoteException,
            IOException {

        // Get the repository info from the hash for the repository that APIA is using,
        // unless it isn't in the hash yet... in which case, ask the server.
        RepositoryInfo repoinfo = s_repoInfo.get(apia);
        if (repoinfo == null) {
            repoinfo = apia.describeRepository();
            s_repoInfo.put(apia, repoinfo);
        }

        byte[] bytes;
        // For backward compatibility:
        // For pre-2.0 repositories, use the "exportObject" APIM method. 
        // Also pre-2.0 repositories will only export "metslikefedora1".
        // For 2.0+ repositories use the "export" method which takes a format arg.
        StringTokenizer stoken =
                new StringTokenizer(repoinfo.getRepositoryVersion(), ".");
        int majorVersion = new Integer(stoken.nextToken()).intValue();
        if (majorVersion < 2) {
            if (format == null || format.equals(METS_EXT1_1.uri)
                    || format.equals(METS_EXT1_0.uri)
                    || format.equals("metslikefedora1")
                    || format.equals("default")) {
                if (format.equals(METS_EXT1_1.uri)) {
                    System.out
                            .println("WARNING: Repository does not support METS Fedora Extension 1.1; exporting older format (v1.0) instead");
                }
                bytes = apim.exportObject(pid);
            } else {
                throw new IOException("You are connected to a pre-2.0 Fedora repository "
                        + "which will only export the XML format \"metslikefedora1\".");
            }
        } else {
            if (majorVersion < 3) {
                if (format != null) {
                    if (format.equals(FOXML1_1.uri)) {
                        System.out
                                .println("WARNING: Repository does not support FOXML 1.1; exporting older format (v1.0) instead");
                        format = "foxml1.0";
                    } else if (format.equals(METS_EXT1_1.uri)) {
                        System.out
                                .println("WARNING: Repository does not support METS Fedora Extension 1.1; exporting older format (v1.0) instead");
                        format = "metslikefedora1";
                    }
                }
            } else {
                if (format != null) {
                    if (format.equals(FOXML1_0.uri)
                            || format.equals("foxml1.0")) {
                        System.out
                                .println("WARNING: Repository does not support FOXML 1.0; exporting newer format (v1.1) instead");
                        format = FOXML1_1.uri;
                    } else if (format.equals(METS_EXT1_0.uri)
                            || format.equals("metslikefedora1")) {
                        System.out
                                .println("WARNING: Repository does not support METS Fedora Extension 1.0; exporting newer format (v1.1) instead");
                        format = METS_EXT1_1.uri;
                    }
                }
                validateFormat(format);
            }
            bytes = apim.export(pid, format, exportContext);
        }
        try {
            // use xerces to pretty print the xml, assuming it's well formed
            OutputFormat fmt = new OutputFormat("XML", "UTF-8", true);
            fmt.setIndent(2);
            fmt.setLineWidth(120);
            fmt.setPreserveSpace(false);
            XMLSerializer ser = new XMLSerializer(outStream, fmt);
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(bytes));
            ser.serialize(doc);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + " : "
                    + e.getMessage());
        } finally {
            outStream.close();
        }
    }

    public void getObjectXML(String pid, OutputStream outStream)
            throws RemoteException, IOException {
        getObjectXML(m_apia, m_apim, pid, outStream);
    }

    public static void getObjectXML(FedoraAPIA apia,
                                    FedoraAPIM apim,
                                    String pid,
                                    OutputStream outStream)
            throws RemoteException, IOException {

        // Get the repository info from the hash for the repository that APIA is using,
        // unless it isn't in the hash yet... in which case, ask the server.
        RepositoryInfo repoinfo = s_repoInfo.get(apia);
        if (repoinfo == null) {
            repoinfo = apia.describeRepository();
            s_repoInfo.put(apia, repoinfo);
        }
        // get the object XML as it exists in the repository's 
        // persitent storage area.		
        byte[] bytes = apim.getObjectXML(pid);
        try {
            // use xerces to pretty print the xml, assuming it's well formed
            OutputFormat fmt = new OutputFormat("XML", "UTF-8", true);
            fmt.setIndent(2);
            fmt.setLineWidth(120);
            fmt.setPreserveSpace(false);
            XMLSerializer ser = new XMLSerializer(outStream, fmt);
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(bytes));
            ser.serialize(doc);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + " : "
                    + e.getMessage());
        } finally {
            outStream.close();
        }
    }

    public static void validateFormat(String format) throws IOException {
        if (format == null) {
            return;
        }
        if (!format.equals(FOXML1_1.uri) && !format.equals(METS_EXT1_1.uri)
                && !format.equals("default")) {
            throw new IOException("Invalid export format. Valid FORMAT values are: "
                    + "'"
                    + FOXML1_1.uri
                    + "' '"
                    + METS_EXT1_1.uri
                    + "' and 'default'");
        }
    }
}
