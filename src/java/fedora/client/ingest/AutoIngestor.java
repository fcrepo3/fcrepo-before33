package fedora.client.ingest;

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

/**
 *
 * <p><b>Title:</b> AutoIngestor.java</p>
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
public class AutoIngestor {

    private FedoraAPIM m_apim;

    public AutoIngestor(String host, int port, String user, String pass)
            throws MalformedURLException, ServiceException {
        m_apim=APIMStubFactory.getStub(host, port, user, pass);
    }

    public String ingestAndCommit(InputStream in, String logMessage)
            throws RemoteException, IOException {
        return ingestAndCommit(m_apim, in, logMessage);
    }
    
	public String ingestAndCommit(InputStream in, String format, String logMessage)
			throws RemoteException, IOException {
		return ingestAndCommit(m_apim, in, format, logMessage);
	}

    public static String ingestAndCommit(FedoraAPIM skeleton, InputStream in, String logMessage)
            	throws RemoteException, IOException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        StreamUtility.pipeStream(in, out, 4096);
        String pid=skeleton.ingestObject(out.toByteArray(), logMessage);
        return pid;
    }
    
	public static String ingestAndCommit(FedoraAPIM skeleton, InputStream in, String format, 
		String logMessage)
			throws RemoteException, IOException {
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		StreamUtility.pipeStream(in, out, 4096);
		String pid=skeleton.ingest(out.toByteArray(), format, logMessage);
		return pid;
	}

    public static void showUsage(String errMessage) {
        System.out.println("Error: " + errMessage);
        System.out.println("");
        System.out.println(
			"Usage: AutoIngestor host port username password filename format \"log message\"");
    }

    public static void main(String[] args) {
        try {
            if (args.length!=7) {
                AutoIngestor.showUsage("You must provide seven arguments.");
            } else {
                String hostName=args[0];
                int portNum=Integer.parseInt(args[1]);
                String username=args[2];
                String password=args[3];
				String logMessage=args[6];
				String format=args[5];
                // arg==file... must exist
                File f=new File(args[4]);
                if (!f.exists()) {
                    AutoIngestor.showUsage("Fifth argument must be the path to an existing file.");
                } else {
                    if (f.isDirectory()) {
                        AutoIngestor.showUsage("Fifth argument must be a file path -- not a directory path.");
                    } else {
                        AutoIngestor a=new AutoIngestor(hostName, portNum, username, password);
                        System.out.println(a.ingestAndCommit(new FileInputStream(f), format, logMessage));
                    }
                }
            }
        } catch (Exception e) {
            AutoIngestor.showUsage(e.getClass().getName() + " - "
                + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
        }
    }

}