package fedora.client.export;

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

public class AutoExporter {

    private FedoraAPIM m_apim;    

    public AutoExporter(String host, int port) 
            throws MalformedURLException, ServiceException {
        m_apim=APIMSkeletonFactory.getSkeleton(host, port);
    }

    public void export(String pid, OutputStream outStream) throws RemoteException, IOException {
        export(m_apim, pid, outStream);
    }

    public static void export(FedoraAPIM skeleton, String pid, OutputStream outStream) 
            throws RemoteException, IOException {
        byte[] bytes=skeleton.getObjectXML(pid); 
        for (int i=0; i<bytes.length; i++) {
            outStream.write(bytes[i]);
        }
        outStream.close();
    }

    public static void showUsage(String errMessage) {
        System.out.println("Error: " + errMessage);
        System.out.println("");
        System.out.println("Usage: AutoExporter host port filename pid");
    }

    public static void main(String[] args) {
        try {
            if (args.length!=4) {
                AutoExporter.showUsage("You must provide four arguments.");
            } else {
                String hostName=args[0];
                int portNum=Integer.parseInt(args[1]);
                String pid=args[3];
                // third arg==file... must exist
                File f=new File(args[2]);
                if (f.exists()) {
                    AutoExporter.showUsage("Third argument must be the path to a non-existing file.");
                } else {
                    AutoExporter a=new AutoExporter(hostName, portNum);
                    a.export(pid, new FileOutputStream(f));
                }
            }
        } catch (Exception e) {
            AutoExporter.showUsage(e.getClass().getName() + " - " 
                + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
        }
    }

}