package fedora.client.purge;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;

import fedora.client.APIMStubFactory;
import fedora.server.management.FedoraAPIM;
import fedora.server.utilities.StreamUtility;

/**
 *
 * <p><b>Title:</b> AutoPurger.java</p>
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
public class AutoPurger {

    private FedoraAPIM m_apim;

    public AutoPurger(String host, int port, String user, String pass)
            throws MalformedURLException, ServiceException {
        m_apim=APIMStubFactory.getStub(host, port, user, pass);
    }

    public void purge(String pid, String logMessage) throws RemoteException, IOException {
        purge(m_apim, pid, logMessage);
    }

    public static void purge(FedoraAPIM skeleton, String pid, String logMessage)
            throws RemoteException, IOException {
        skeleton.purgeObject(pid, logMessage);
    }

    public static void showUsage(String errMessage) {
        System.out.println("Error: " + errMessage);
        System.out.println("");
        System.out.println("Usage: AutoPurger host port username password pid logMessage");
    }

    public static void main(String[] args) {
        try {
            if (args.length!=6) {
                AutoPurger.showUsage("You must provide six arguments.");
            } else {
                String hostName=args[0];
                int portNum=Integer.parseInt(args[1]);
                String pid=args[4];
                String logMessage=args[5];
                AutoPurger a=new AutoPurger(hostName, portNum, args[2], args[3]);
                a.purge(pid, logMessage);
            }
        } catch (Exception e) {
            AutoPurger.showUsage(e.getClass().getName() + " - "
                + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
        }
    }

}
