package fedora.client.datastream;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;

import fedora.client.FedoraClient;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.Datastream;

/**
 *
 * <p><b>Title:</b> DatastreamConduit.java</p>
 * <p><b>Description:</b> </p>
 *
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class DatastreamConduit {

    private FedoraAPIM m_apim;

    //public DatastreamConduit(String protocol, String host, int port, String user, String pass)
    //        throws MalformedURLException, ServiceException {
    //    m_apim=APIMStubFactory.getStub(protocol, host, port, user, pass);
    //}
    
	public DatastreamConduit(FedoraAPIM apim)
			throws MalformedURLException, ServiceException {
		m_apim=apim;
	}

    public static Datastream getDatastream(FedoraAPIM skeleton, String pid,
            String dsId, String asOfDateTime)
            throws RemoteException {
        return skeleton.getDatastream(pid, dsId, asOfDateTime);
    }

    public Datastream getDatastream(String pid, String dsId,
            String asOfDateTime)
            throws RemoteException {
        return getDatastream(m_apim, pid, dsId, asOfDateTime);
    }

    public static Datastream[] getDatastreams(FedoraAPIM skeleton, String pid,
            String asOfDateTime, String state)
            throws RemoteException {
        return skeleton.getDatastreams(pid, asOfDateTime, state);
    }

    public Datastream[] getDatastreams(String pid, String asOfDateTime, 
            String state)
            throws RemoteException {
        return getDatastreams(m_apim, pid, asOfDateTime, state);
    }

    public static void modifyDatastreamByReference(FedoraAPIM skeleton,
                                                   String pid, 
                                                   String dsId, 
                                                   String[] altIDs, 
                                                   String dsLabel, 
                                                   boolean versionable, 
                                                   String mimeType, 
                                                   String formatURI,
                                                   String location, 
                                                   String state, 
                                                   String logMessage,
                                                   boolean force)
            throws RemoteException {
        skeleton.modifyDatastreamByReference(pid, 
                                             dsId, 
                                             altIDs,
                                             dsLabel, 
                                             versionable,
                                             mimeType,
                                             formatURI,
                                             location, 
                                             state,
                                             logMessage,
                                             force);
    }

    public void modifyDatastreamByReference(String pid, 
                                            String dsId, 
                                            String[] altIDs, 
                                            String dsLabel, 
                                            boolean versionable, 
                                            String mimeType, 
                                            String formatURI,
                                            String location, 
                                            String state, 
                                            String logMessage,
                                            boolean force)
            throws RemoteException {
        modifyDatastreamByReference(m_apim, 
                                    pid, 
                                    dsId, 
                                    altIDs,
                                    dsLabel, 
                                    versionable,
                                    mimeType,
                                    formatURI,
                                    location, 
                                    state,
                                    logMessage,
                                    force);
    }

    public static void modifyDatastreamByValue(FedoraAPIM skeleton,
                                               String pid, 
                                               String dsId, 
                                               String[] altIDs,
                                               String dsLabel, 
                                               boolean versionable,
                                               String mimeType,
                                               String formatURI,
                                               byte[] content, 
                                               String state,
                                               String logMessage,
                                               boolean force)
            throws RemoteException {
        skeleton.modifyDatastreamByValue(pid, 
                                         dsId, 
                                         altIDs,
                                         dsLabel, 
                                         versionable,
                                         mimeType,
                                         formatURI,
                                         content, 
                                         state,
                                         logMessage,
                                         force);
    }

    public void modifyDatastreamByValue(String pid, 
                                        String dsId, 
                                        String[] altIDs,
                                        String dsLabel, 
                                        boolean versionable,
                                        String mimeType,
                                        String formatURI,
                                        byte[] content, 
                                        String state,
                                        String logMessage,
                                        boolean force)
            throws RemoteException {
        modifyDatastreamByValue(m_apim,
                                pid, 
                                dsId, 
                                altIDs,
                                dsLabel, 
                                versionable,
                                mimeType,
                                formatURI,
                                content, 
                                state,
                                logMessage,
                                force);
    }

    public static String[] purgeDatastream(FedoraAPIM skeleton,
                                           String pid, 
                                           String dsId, 
                                           String endDT, 
                                           String logMessage, 
                                           boolean force)
            throws RemoteException {
        return skeleton.purgeDatastream(pid, dsId, endDT, logMessage, force);
    }

    public String[] purgeDatastream(String pid, 
                                    String dsId, 
                                    String endDT, 
                                    String logMessage, 
                                    boolean force)
            throws RemoteException {
        return purgeDatastream(m_apim, pid, dsId, endDT, logMessage, force);
    }

    public static Datastream[] getDatastreamHistory(FedoraAPIM skeleton,
            String pid, String dsId)
            throws RemoteException {
        return skeleton.getDatastreamHistory(pid, dsId);
    }

    public Datastream[] getDatastreamHistory(String pid, String dsId)
            throws RemoteException {
        return getDatastreamHistory(m_apim, pid, dsId);
    }

    public static void showUsage(String errMessage) {
        System.out.println("Error: " + errMessage);
        System.out.println("");
        System.out.println("Usage: fedora-dsinfo host port username password pid protocol");
		System.out.println("Note: protocol must be either http or https.");
    }

    public static void main(String[] args) {
        try {
            if (args.length!=6) {
                DatastreamConduit.showUsage("You must provide six arguments.");
            } else {
                String hostName=args[0];
                int portNum=Integer.parseInt(args[1]);
                String username=args[2];
                String password=args[3];
                String pid=args[4];
				String protocol=args[5];
                //DatastreamConduit c=new DatastreamConduit(protocol, hostName, portNum, username, password);
                
				// ******************************************
				// NEW: use new client utility class
				// FIXME:  Get around hardcoding the path in the baseURL
				String baseURL = protocol + "://" + hostName + ":" + portNum + "/fedora";
				FedoraClient fc = new FedoraClient(baseURL, username, password);
				FedoraAPIM sourceRepoAPIM=fc.getAPIM();
				//*******************************************
				DatastreamConduit c=new DatastreamConduit(sourceRepoAPIM);
				
                Datastream[] datastreams=c.getDatastreams(pid, null, null);
                for (int i=0; i<datastreams.length; i++) {
                    System.out.println("   Datastream : " + datastreams[i].getID());
                    Datastream ds=datastreams[i];
                    System.out.println("Control Group : " + ds.getControlGroup().toString());
                    System.out.println("  Versionable : " + ds.isVersionable());
                    System.out.println("    Mime Type : " + ds.getMIMEType());
                    System.out.println("   Format URI : " + ds.getFormatURI());
                    String[] altIDs = ds.getAltIDs();
                    if (altIDs != null) {
                        for (int idNum = 0; idNum < altIDs.length; idNum++) {
                            System.out.println(" Alternate ID : " + altIDs[idNum]);
                        }
                    }
                    System.out.println("        State : " + ds.getState());
                    // print version id, create date, and label for each version
                    Datastream[] versions=c.getDatastreamHistory(pid, datastreams[i].getID());
                    for (int j=0; j<versions.length; j++) {
                        Datastream ver=versions[j];
                        System.out.println("      VERSION : " + ver.getVersionID());
                        System.out.println("        Created : " + ver.getCreateDate());
                        System.out.println("          Label : " + ver.getLabel());
                        System.out.println("       Location : " + ver.getLocation());
                    }
                    System.out.println("");
                }
            }
        } catch (Exception e) {
            DatastreamConduit.showUsage(e.getClass().getName() + " - "
                + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
        }
    }

}