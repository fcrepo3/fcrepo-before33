package fedora.client.list;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.rpc.ServiceException;

import fedora.client.APIMSkeletonFactory;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.ObjectInfo;

public class AutoLister {

    private FedoraAPIM m_apim;    

    public AutoLister(String host, int port) 
            throws MalformedURLException, ServiceException {
        m_apim=APIMSkeletonFactory.getSkeleton(host, port);
    }

    public Map list(String fedoraObjectType) throws RemoteException {
        return list(m_apim, fedoraObjectType);
    }

    public static Map list(FedoraAPIM skeleton, String fedoraObjectType) 
            throws RemoteException {
        String[] pids=skeleton.listObjectPIDs(null, fedoraObjectType,
                null, null, null, null, null, null, null, null);
/*
            String pidPattern, 
            String foType, String lockedByPattern, String state, 
            String labelPattern, String contentModelIdPattern, 
            Calendar createDateMin, Calendar createDateMax, 
            Calendar lastModDateMin, Calendar lastModDateMax        
*/
        HashMap oi=new HashMap();
        for (int i=0; i<pids.length; i++) {
            oi.put(pids[i], skeleton.getObjectInfo(pids[i]));
        }
        return oi;
    }

    public static void showUsage(String errMessage) {
        System.out.println("Error: " + errMessage);
        System.out.println("");
        System.out.println("Usage: AutoLister host port D|M|O");
    }

    public static void main(String[] args) {
        try {
            if (args.length!=3) {
                AutoLister.showUsage("You must provide three arguments.");
            } else {
                String hostName=args[0];
                int portNum=Integer.parseInt(args[1]);
                AutoLister a=new AutoLister(hostName, portNum);
                Map m=a.list(args[2]);
                Iterator pidIter=m.keySet().iterator();
                while (pidIter.hasNext()) {
                    String pid=(String) pidIter.next();
                    ObjectInfo inf=(ObjectInfo) m.get(pid);
                    System.out.println(pid);
                    System.out.println("  label=" + inf.getLabel());
                    System.out.println("  contentModelId=" + inf.getContentModelId());
                    System.out.println("  fedora object type=" + inf.getFoType());
                    System.out.println("  state=" + inf.getState());
                    String lb=inf.getLockedBy();
                    if (lb.equals("")) {
                        System.out.println("  locked by=<not locked>");
                    } else {
                        System.out.println("  locked by=" + inf.getLockedBy());
                    }
                    SimpleDateFormat df=new SimpleDateFormat();
                    System.out.println("  created=" + df.format(inf.getCreateDate().getTime()));
                    System.out.println("  last modified=" + df.format(inf.getLastModDate().getTime()));
                }
            }
        } catch (Exception e) {
            AutoLister.showUsage(e.getClass().getName() + " - " 
                + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
        }
    }

}