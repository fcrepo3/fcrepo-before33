package fedora.client.list;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.rpc.ServiceException;

import fedora.client.APIMStubFactory;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.ObjectInfo;

/**
 *
 * <p><b>Title:</b> AutoLister.java</p>
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
 * @version 1.0
 */
public class AutoLister {

    private FedoraAPIM m_apim;

    public AutoLister(String host, int port, String user, String pass)
            throws MalformedURLException, ServiceException {
        m_apim=APIMStubFactory.getStub(host, port, user, pass);
    }

    public Map list(String pidPattern, String foType, String lockedByPattern,
            String state, String labelPattern, String contentModelIdPattern,
            Calendar createDateMin, Calendar createDateMax,
            Calendar lastModDateMin, Calendar lastModDateMax)
            throws RemoteException {
        return list(m_apim, pidPattern, foType, lockedByPattern, state,
                labelPattern, contentModelIdPattern, createDateMin,
                createDateMax, lastModDateMin, lastModDateMax);
    }

    public static Map list(FedoraAPIM skeleton, String pidPattern,
            String foType, String lockedByPattern,
            String state, String labelPattern, String contentModelIdPattern,
            Calendar createDateMin, Calendar createDateMax,
            Calendar lastModDateMin, Calendar lastModDateMax)
            throws RemoteException {
        String[] pids=skeleton.listObjectPIDs(pidPattern, foType,
                lockedByPattern, state, labelPattern, contentModelIdPattern,
                createDateMin, createDateMax, lastModDateMin, lastModDateMax);
        HashMap oi=new HashMap();
        for (int i=0; i<pids.length; i++) {
            oi.put(pids[i], skeleton.getObjectInfo(pids[i]));
        }
        return oi;
    }

    public static void showUsage(String errMessage) {
        System.out.println("Error: " + errMessage);
        System.out.println("");
        System.out.println("Usage: AutoLister host port username password D|M|O");
    }

    public static void main(String[] args) {
        try {
            if (args.length!=5) {
                AutoLister.showUsage("You must provide five arguments.");
            } else {
                String hostName=args[0];
                int portNum=Integer.parseInt(args[1]);
                AutoLister a=new AutoLister(hostName, portNum, args[2], args[3]);
                Map m=a.list(null, args[4], null, null, null, null, null, null,
                        null, null);
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
                    SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
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