package fedora.client.utility;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;

import fedora.client.APIMStubFactory;
import fedora.server.management.FedoraAPIM;

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

	/**
	 * Print error message and show usage for command-line interface.
	 */
	public static void showUsage(String msg) {
		System.err.println("Command: fedora-purge");
		System.err.println();
		System.err.println("Summary: Purges an object from the Fedora repository.");
		System.err.println();
		System.err.println("Syntax:");
		System.err.println("  fedora-purge HST:PRT USR PSS PID [LOG]");
		System.err.println();
		System.err.println("Where:");
		System.err.println("  HST  is the target repository hostname.");
		System.err.println("  PRT  is the target repository port number.");
		System.err.println("  USR  is the id of the target repository user.");
		System.err.println("  PSS  is the password of the target repository user.");
		System.err.println("  PID  is the id of the object to purge from the target repository.");
		System.err.println("  LOG  is a log message.");
		System.err.println();
		System.err.println("Example:");
		System.err.println("fedora-purge myrepo.com:80 jane janepw demo:5 \"my message\"");
		System.err.println();
		System.err.println("  Purges the object whose PID is demo:5 from the");
		System.err.println("  target repository at myrepo.com:80");
		System.err.println();
		System.err.println("ERROR  : " + msg);
		System.exit(1);
	}

    public static void main(String[] args) {
        try {
            if (args.length!=5) {
                AutoPurger.showUsage("You must provide five arguments.");
            } else {
				String[] hp=args[0].split(":");
                String hostName=hp[0];
                int portNum=Integer.parseInt(hp[1]);
                String pid=args[3];
                String logMessage=args[4];
                AutoPurger a=new AutoPurger(hostName, portNum, args[1], args[2]);
                a.purge(pid, logMessage);
            }
        } catch (Exception e) {
            AutoPurger.showUsage(e.getClass().getName() + " - "
                + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
        }
    }

}
