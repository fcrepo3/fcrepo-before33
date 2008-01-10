/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.utility;

import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URLEncoder;

import java.rmi.RemoteException;

import java.util.HashSet;
import java.util.Iterator;

import javax.xml.rpc.ServiceException;

import org.apache.axis.types.NonNegativeInteger;

import fedora.client.Downloader;
import fedora.client.FedoraClient;
import fedora.client.search.SearchResultParser;

import fedora.server.access.FedoraAPIA;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ListSession;
import fedora.server.types.gen.ObjectFields;

/**
 * @author Chris Wilper
 */
public class AutoFinder {

    private final FedoraAPIA m_apia;

    public AutoFinder(FedoraAPIA apia)
            throws MalformedURLException, ServiceException {
        m_apia = apia;
    }

    public FieldSearchResult findObjects(String[] resultFields,
                                         int maxResults,
                                         FieldSearchQuery query)
            throws RemoteException {
        return findObjects(m_apia, resultFields, maxResults, query);
    }

    public FieldSearchResult resumeFindObjects(String sessionToken)
            throws RemoteException {
        return resumeFindObjects(m_apia, sessionToken);
    }

    public static FieldSearchResult findObjects(FedoraAPIA skeleton,
                                                String[] resultFields,
                                                int maxResults,
                                                FieldSearchQuery query)
            throws RemoteException {
        return skeleton.findObjects(resultFields, new NonNegativeInteger(""
                + maxResults), query);
    }

    public static FieldSearchResult resumeFindObjects(FedoraAPIA skeleton,
                                                      String sessionToken)
            throws RemoteException {
        return skeleton.resumeFindObjects(sessionToken);
    }

    // fieldQuery is the syntax used by API-A-Lite,
    // such as "fType=O pid~demo*".  Leave blank to match all.
    public static String[] getPIDs(String protocol,
                                   String host,
                                   int port,
                                   String fieldQuery) throws Exception {
        String firstPart =
                protocol + "://" + host + ":" + port
                        + "/fedora/search?xml=true";
        Downloader dLoader = new Downloader(host, port, "na", "na");
        String url =
                firstPart + "&pid=true&query="
                        + URLEncoder.encode(fieldQuery, "UTF-8");
        InputStream in = dLoader.get(url);
        String token = "";
        SearchResultParser resultParser;
        HashSet<String> pids = new HashSet<String>();
        while (token != null) {
            resultParser = new SearchResultParser(in);
            if (resultParser.getToken() != null) {
                // resumeFindObjects
                token = resultParser.getToken();
                in = dLoader.get(firstPart + "&sessionToken=" + token);
            } else {
                token = null;
            }
            pids.addAll(resultParser.getPIDs());
        }
        String[] result = new String[pids.size()];
        int i = 0;
        Iterator iter = pids.iterator();
        while (iter.hasNext()) {
            result[i++] = (String) iter.next();
        }
        return result;
    }

    public static void showUsage(String message) {
        System.err.println(message);
        System.err
                .println("Usage: fedora-find host port fields phrase protocol");
        System.err.println("");
        System.err
                .println("    hostname - The Fedora server host or ip address.");
        System.err.println("        port - The Fedora server port.");
        System.err.println("      fields - Space-delimited list of fields.");
        System.err
                .println("      phrase - Phrase to search for in any field (with ? and * wildcards)");
        System.err
                .println("    protocol - The protocol to communication with the Fedora server (http|https)");
    }

    public static void printValue(String name, String value) {
        if (value != null) {
            System.out.println("   " + name + "  " + value);
        }
    }

    public static void printValue(String name, String[] value) {
        if (value != null) {
            for (String element : value) {
                AutoFinder.printValue(name, element);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 3) {
            // just list all pids
            System.out.println("Doing query...");
            String[] pids =
                    AutoFinder.getPIDs(args[4], args[0], Integer
                            .parseInt(args[1]), args[2]);
            System.out.println("All PIDs in " + args[0] + ":"
                    + Integer.parseInt(args[1]) + " with field query "
                    + args[2]);
            for (String element : pids) {
                System.out.println(element);
            }
            System.out.println(pids.length + " total.");
            System.exit(0);
        }
        if (args.length != 5) {
            AutoFinder.showUsage("Five arguments required.");
        }
        try {
            // ******************************************
            // NEW: use new client utility class
            // FIXME:  Get around hardcoding the path in the baseURL
            String baseURL =
                    args[4] + "://" + args[0] + ":" + Integer.parseInt(args[1])
                            + "/fedora";
            FedoraClient fc = new FedoraClient(baseURL, null, null);
            AutoFinder finder = new AutoFinder(fc.getAPIA());
            //*******************************************

            FieldSearchQuery query = new FieldSearchQuery();
            query.setTerms(args[3]);
            FieldSearchResult result =
                    finder.findObjects(args[2].split(" "), 20, query);
            int matchNum = 0;
            while (result != null) {
                for (int i = 0; i < result.getResultList().length; i++) {
                    ObjectFields o = result.getResultList()[i];
                    matchNum++;
                    System.out.println("#" + matchNum);
                    AutoFinder.printValue("pid        ", o.getPid());
                    AutoFinder.printValue("fType      ", o.getFType());
                    AutoFinder.printValue("cModel     ", o.getCModel());
                    AutoFinder.printValue("state      ", o.getState());
                    AutoFinder.printValue("ownerId     ", o.getOwnerId());
                    AutoFinder.printValue("cDate      ", o.getCDate());
                    AutoFinder.printValue("mDate      ", o.getMDate());
                    AutoFinder.printValue("dcmDate    ", o.getDcmDate());
                    AutoFinder.printValue("bDef       ", o.getBDef());
                    AutoFinder.printValue("bMech      ", o.getBMech());
                    AutoFinder.printValue("title      ", o.getTitle());
                    AutoFinder.printValue("creator    ", o.getCreator());
                    AutoFinder.printValue("subject    ", o.getSubject());
                    AutoFinder.printValue("description", o.getDescription());
                    AutoFinder.printValue("publisher  ", o.getPublisher());
                    AutoFinder.printValue("contributor", o.getContributor());
                    AutoFinder.printValue("date       ", o.getDate());
                    AutoFinder.printValue("type       ", o.getType());
                    AutoFinder.printValue("format     ", o.getFormat());
                    AutoFinder.printValue("identifier ", o.getIdentifier());
                    AutoFinder.printValue("source     ", o.getSource());
                    AutoFinder.printValue("language   ", o.getLanguage());
                    AutoFinder.printValue("relation   ", o.getRelation());
                    AutoFinder.printValue("coverage   ", o.getCoverage());
                    AutoFinder.printValue("rights     ", o.getRights());
                    System.out.println("");
                }
                ListSession sess = result.getListSession();
                if (sess != null) {
                    result = finder.resumeFindObjects(sess.getToken());
                } else {
                    result = null;
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getClass().getName()
                    + (e.getMessage() == null ? "" : ": " + e.getMessage()));
        }
    }

}
