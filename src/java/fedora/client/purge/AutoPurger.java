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

import fedora.client.APIMSkeletonFactory;
import fedora.server.management.FedoraAPIM;
import fedora.server.utilities.StreamUtility;

public class AutoPurger {

    private FedoraAPIM m_apim;    

    public AutoPurger(String host, int port) 
            throws MalformedURLException, ServiceException {
        m_apim=APIMSkeletonFactory.getSkeleton(host, port);
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
        System.out.println("Usage: AutoPurger host port pid logMessage");
    }

    public static void main(String[] args) {
        try {
            if (args.length!=4) {
                AutoPurger.showUsage("You must provide four arguments.");
            } else {
                String hostName=args[0];
                int portNum=Integer.parseInt(args[1]);
                String pid=args[2];
                String logMessage=args[3];
                AutoPurger a=new AutoPurger(hostName, portNum);
                a.purge(pid, logMessage);
            }
        } catch (Exception e) {
            AutoPurger.showUsage(e.getClass().getName() + " - " 
                + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
        }
    }

}