package fedora.client.datastream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
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
    public static SimpleDateFormat FORMATTER=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

    public DatastreamConduit(String host, int port, String user, String pass)
            throws MalformedURLException, ServiceException {
        m_apim=APIMStubFactory.getStub(host, port, user, pass);
    }

    public static String[] listDatastreamIDs(FedoraAPIM skeleton, String pid,
            String state)
            throws RemoteException {
        return skeleton.listDatastreamIDs(pid, state);
    }

    public String[] listDatastreamIDs(String pid, String state)
            throws RemoteException {
        return listDatastreamIDs(m_apim, pid, state);
    }

    public static Datastream getDatastream(FedoraAPIM skeleton, String pid,
            String dsId, Calendar asOfDateTime)
            throws RemoteException {
        return skeleton.getDatastream(pid, dsId, asOfDateTime);
    }

    public Datastream getDatastream(String pid, String dsId,
            Calendar asOfDateTime)
            throws RemoteException {
        return getDatastream(m_apim, pid, dsId, asOfDateTime);
    }

    public static void modifyDatastreamByReference(FedoraAPIM skeleton,
            String pid, String dsId, String dsLabel, String logMessage,
            String location)
            throws RemoteException {
        skeleton.modifyDatastreamByReference(pid, dsId, dsLabel, logMessage,
                location);
    }

    public void modifyDatastreamByReference(String pid, String dsId,
            String dsLabel, String logMessage, String location)
            throws RemoteException {
        modifyDatastreamByReference(m_apim, pid, dsId, dsLabel, logMessage,
                location);
    }

    public static void modifyDatastreamByValue(FedoraAPIM skeleton,
            String pid, String dsId, String dsLabel, String logMessage,
            byte[] content)
            throws RemoteException {
        skeleton.modifyDatastreamByValue(pid, dsId, dsLabel, logMessage,
                content);
    }

    public void modifyDatastreamByValue(String pid, String dsId,
            String dsLabel, String logMessage, byte[] content)
            throws RemoteException {
        modifyDatastreamByValue(m_apim, pid, dsId, dsLabel, logMessage,
                content);
    }

//
    public static void deleteDatastream(FedoraAPIM skeleton,
            String pid, String dsId, String logMessage)
            throws RemoteException {
        skeleton.deleteDatastream(pid, dsId, logMessage);
    }

    public void deleteDatastream(String pid, String dsId,
            String logMessage)
            throws RemoteException {
        deleteDatastream(m_apim, pid, dsId, logMessage);
    }
//

//
    public static void withdrawDatastream(FedoraAPIM skeleton,
            String pid, String dsId, String logMessage)
            throws RemoteException {
        skeleton.withdrawDatastream(pid, dsId, logMessage);
    }

    public void withdrawDatastream(String pid, String dsId,
            String logMessage)
            throws RemoteException {
        withdrawDatastream(m_apim, pid, dsId, logMessage);
    }
//

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
                String[] ids=c.listDatastreamIDs(pid, "A");
                for (int i=0; i<ids.length; i++) {
                    System.out.println("Datastream : " + ids[i]);
                    Datastream ds=c.getDatastream(pid, ids[i], null);
                    System.out.println("State : " + ds.getState());
                    System.out.println("Version ID : " + ds.getVersionID());
                    System.out.println("Create Date : " + FORMATTER.format(ds.getCreateDate().getTime()));
                    System.out.println("Control Group : " + ds.getControlGroup().toString());
                    System.out.println("Label : " + ds.getLabel());
                    System.out.println("Info Type : " + ds.getInfoType());
                    System.out.println("Mime Type : " + ds.getMIMEType());
                    if ((!ds.getControlGroup().toString().equals("X"))
                            && (!ds.getControlGroup().toString().equals("X"))) {
                        System.out.println("Location : " + ds.getLocation());
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