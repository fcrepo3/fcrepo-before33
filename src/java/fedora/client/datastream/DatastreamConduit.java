package fedora.client.datastream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;

import fedora.client.APIMStubFactory;
import fedora.server.management.FedoraAPIM;
import fedora.server.utilities.StreamUtility;
import fedora.server.types.gen.Datastream;

/**
 *
 * <p><b>Title:</b> DatastreamConduit.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class DatastreamConduit {

    private FedoraAPIM m_apim;

    public DatastreamConduit(String host, int port, String user, String pass)
            throws MalformedURLException, ServiceException {
        m_apim=APIMStubFactory.getStub(host, port, user, pass);
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
        System.out.println("Usage: fedora-dsinfo host port username password pid");
    }

    public static void main(String[] args) {
        try {
            if (args.length!=5) {
                DatastreamConduit.showUsage("You must provide five arguments.");
            } else {
                String hostName=args[0];
                int portNum=Integer.parseInt(args[1]);
                String username=args[2];
                String password=args[3];
                String pid=args[4];
                DatastreamConduit c=new DatastreamConduit(hostName, portNum, username, password);
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