package fedora.server.management;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Date;

import org.apache.axis.types.NonNegativeInteger;

import fedora.server.Context;
import fedora.server.ReadOnlyContext;
import fedora.server.Server;
import fedora.server.errors.GeneralException;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ServerException;
import fedora.server.errors.ServerInitializationException;
import fedora.server.errors.StorageDeviceException;
import fedora.server.errors.authorization.AuthzException;
import fedora.server.utilities.AxisUtility;
import fedora.server.utilities.DateUtility;
import fedora.server.utilities.TypeUtility;

/**
 *
 * <p><b>Title:</b> FedoraAPIMBindingSOAPHTTPImpl.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class FedoraAPIMBindingSOAPHTTPImpl
        implements FedoraAPIM {
	
	private static final org.apache.log4j.Logger logger = 
    	org.apache.log4j.Logger.getLogger(FedoraAPIM.class);
	
    /** The Fedora Server instance */
    private static Server s_server;

    /** Whether the service has initialized... true if we got a good Server instance. */
    private static boolean s_initialized;

    /** The exception indicating that initialization failed. */
    private static InitializationException s_initException;

    private static Management s_management;

    /** Before fulfilling any requests, make sure we have a server instance. */
    static {
        try {
            String fedoraHome=System.getProperty("fedora.home");
            if (fedoraHome==null) {
                s_initialized=false;
                s_initException=new ServerInitializationException(
                    "Server failed to initialize: The 'fedora.home' "
                    + "system property was not set.");
            } else {
                s_server=Server.getInstance(new File(fedoraHome));
                s_initialized=true;
                s_management=(Management) s_server.getModule("fedora.server.management.Management");
            }
        } catch (InitializationException ie) {
            System.err.println(ie.getMessage());
            s_initialized=false;
            s_initException=ie;
        }
    }

    private static void logStackTrace(Throwable e) {
        StringBuffer out = new StringBuffer();
        addException(e, out);
        e = e.getCause();
        while (e != null) {
            out.append("Caused by: ");
            addException(e, out);
            e = e.getCause();
        }
        s_server.logFiner("Error carried up to API-M level:\n" + out.toString());
    }

    private static void addException(Throwable e, StringBuffer out) {
        out.append(e.getClass().getName());
        String message = e.getMessage();
        if (message == null) message = "(no detail provided)";
        out.append(": " + message + "\n");
        StackTraceElement[] el = e.getStackTrace();
        boolean skip = false;
        for (int i = 0; i < el.length; i++) {
            String line = el[i].toString();
            if (line.indexOf("FedoraAPIMBindingSOAPHTTPSkeleton") != -1) return;
            out.append("        at " + line + "\n");
        }
    }

    /**
     * @deprecated This remains in Fedora 2.0 for backward compatibility. 
     *    It assumes METS-Fedora as the ingest format.
     *    It will be removed in a future version.
     *    Replaced by {@link #ingest(byte[], String, String)}
     */
    public String ingestObject(byte[] METSXML, String logMessage) throws java.rmi.RemoteException {
        assertInitialized();
		return ingest(METSXML, "metslikefedora1", logMessage);
    }
    
	public String ingest(byte[] XML, String format, String logMessage) throws java.rmi.RemoteException {
		logger.debug("start: ingest");
		assertInitialized();
		try {
		  // always gens pid, unless pid in stream starts with "test:" "demo:"
		  // or other prefix that is configured in the retainPIDs parameter of fedora.fcfg
			return s_management.ingestObject(ReadOnlyContext.getSoapContext(),
					new ByteArrayInputStream(XML), logMessage, format, "UTF-8", true);
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a); 			
		} catch (ServerException se) {
			logStackTrace(se);
			throw AxisUtility.getFault(se);
		} catch (Exception e) {
			logStackTrace(e);
			throw AxisUtility.getFault(e);
		} finally {
			logger.debug("end: ingest");
		}
	}

    public String modifyObject(String PID, String state, String label,
            String logMessage)
            throws RemoteException {
    	logger.debug("start: modifyObject, " + PID);
        assertInitialized();
        try {
            return DateUtility.convertDateToString(
                    s_management.modifyObject(
                            ReadOnlyContext.getSoapContext(), PID, state, label, logMessage));
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a);             
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        } catch (Exception e) {
            logStackTrace(e);
            throw AxisUtility.getFault(e);
        } finally {
        	logger.debug("end: modifyObject, " + PID);
        }
    }

	public fedora.server.types.gen.Property[] getObjectProperties(String PID)
			throws RemoteException {
		assertInitialized();
		try {
			fedora.server.storage.types.Property[] properties=
					s_management.getObjectProperties(
							ReadOnlyContext.getSoapContext(), PID);
			return TypeUtility.convertPropertyArrayToGenPropertyArray(properties);
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a); 			
		} catch (ServerException se) {
			logStackTrace(se);
			throw AxisUtility.getFault(se);
		} catch (Exception e) {
			throw AxisUtility.getFault(new ServerInitializationException(e.getClass().getName() + ": " + e.getMessage()));
		}				
				
	}

    public fedora.server.types.gen.UserInfo describeUser(String id)
            throws RemoteException {
    	Context context = ReadOnlyContext.getSoapContext();
    	try {
			s_management.adminPing(context);
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a); 			
		} catch (ServerException e) {
            throw AxisUtility.getFault(new GeneralException("Unrecognized user: " + id));			
		}
        fedora.server.types.gen.UserInfo inf=new fedora.server.types.gen.UserInfo();
        inf.setId(id);
        //so, for the purposes of this method, an administrator is whoever is permitted action "adminPing"
        //and only administrators can be "described"
        inf.setAdministrator(true);
        return inf;
    }


    public byte[] getObjectXML(String PID)
            throws RemoteException {
        assertInitialized();
        try {
            InputStream in=s_management.getObjectXML(ReadOnlyContext.getSoapContext(), PID, "UTF-8");
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            pipeStream(in, out);
            return out.toByteArray();
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a);     
        } catch (ServerException se) {
            throw AxisUtility.getFault(se);
        } catch (Exception e) {
            throw AxisUtility.getFault(new ServerInitializationException(e.getClass().getName() + ": " + e.getMessage()));
        }
    }

    public byte[] exportObject(String PID)
            throws RemoteException {
        assertInitialized();
        try {
			InputStream in=s_management.exportObject(ReadOnlyContext.getSoapContext(), PID, null, null, "UTF-8");
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            pipeStream(in, out);
            return out.toByteArray();
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a);             
        } catch (ServerException se) {
            throw AxisUtility.getFault(se);
        } catch (Exception e) {
            throw AxisUtility.getFault(new ServerInitializationException(e.getClass().getName() + ": " + e.getMessage()));
        }
    }
    
	public byte[] export(String PID, String format, String exportContext)
			throws RemoteException {
		assertInitialized();
		try {
			InputStream in=s_management.exportObject(ReadOnlyContext.getSoapContext(), PID, format, exportContext, "UTF-8");
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			pipeStream(in, out);
			return out.toByteArray();
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a); 			
		} catch (ServerException se) {
			throw AxisUtility.getFault(se);
		} catch (Exception e) {
			throw AxisUtility.getFault(new ServerInitializationException(e.getClass().getName() + ": " + e.getMessage()));
		}
	}

    // temporarily here
    private void pipeStream(InputStream in, OutputStream out)
            throws StorageDeviceException {
        try {
            byte[] buf = new byte[4096];
            int len;
            while ( ( len = in.read( buf ) ) != -1 ) {
                out.write( buf, 0, len );
            }
        } catch (IOException ioe) {
            throw new StorageDeviceException("Error writing to stream");
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException closeProb) {
              // ignore problems while closing
            }
        }
    }

    public String purgeObject(String PID, 
                              String logMessage,
                              boolean force) throws java.rmi.RemoteException {
    	logger.debug("start: purgeObject, " + PID);
        assertInitialized();
        try {
            return DateUtility.convertDateToString(
                    s_management.purgeObject(ReadOnlyContext.getSoapContext(), 
                                             PID, 
                                             logMessage,
                                             force));
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a);             
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        } finally {
        	logger.debug("end: purgeObject, " + PID);
        }
    }

    public String addDatastream(String pid,
                                String dsID,
                                String[] altIds,
                                String label,
                                boolean versionable,
                                String MIMEType,
                                String formatURI,
                                String location,
                                String controlGroup,
                                String dsState,
                                String logMessage) throws RemoteException {
    	logger.debug("start: addDatastream, " + pid + ", " + dsID);
        assertInitialized();
        try {
            return s_management.addDatastream(ReadOnlyContext.getSoapContext(), 
                                                 pid, 
                                                 dsID,
                                                 altIds,
                                                 label, 
                                                 versionable,
                                                 MIMEType,
                                                 formatURI,
                                                 location,
                                                 controlGroup,
                                                 dsState,
                                                 logMessage);
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a); 
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        } finally {
        	logger.debug("end: addDatastream, " + pid + ", " + dsID);
        }
    }

    public String modifyDatastreamByReference(String PID, 
                                              String datastreamID,
                                              String[] altIDs,
                                              String dsLabel, 
                                              boolean versionable,
                                              String mimeType,
                                              String formatURI,
                                              String dsLocation, 
                                              String dsState,
                                              String logMessage, 
                                              boolean force)
            throws java.rmi.RemoteException {
    	logger.debug("start: modifyDatastreamByReference, " + PID + ", " + datastreamID);
        assertInitialized();
        try {
            return DateUtility.convertDateToString(
                    s_management.modifyDatastreamByReference(
                            ReadOnlyContext.getSoapContext(), 
                            PID,
                            datastreamID, 
                            altIDs,
                            dsLabel, 
                            versionable,
                            mimeType,
                            formatURI,
                            dsLocation, 
                            dsState,
                            logMessage, 
                            force));
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a);             
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        } finally {
        	logger.debug("end: modifyDatastreamByReference, " + PID + ", " + datastreamID);
        }
    }

    public String modifyDatastreamByValue(String PID, 
                                          String datastreamID, 
                                          String[] altIDs,
                                          String dsLabel, 
                                          boolean versionable,
                                          String mimeType,
                                          String formatURI,
                                          byte[] dsContent, 
                                          String dsState,
                                          String logMessage, 
                                          boolean force) 
                throws java.rmi.RemoteException {
    	logger.debug("start: modifyDatastreamByValue, " + PID + ", " + datastreamID);
        assertInitialized();
        try {
            ByteArrayInputStream byteStream = null;
            if (dsContent!=null && dsContent.length>0) {
                byteStream = new ByteArrayInputStream(dsContent);
            }
            return DateUtility.convertDateToString(
                    s_management.modifyDatastreamByValue(ReadOnlyContext.getSoapContext(), 
                                                         PID,
                                                         datastreamID, 
                                                         altIDs,
                                                         dsLabel, 
                                                         versionable,
                                                         mimeType,
                                                         formatURI,
                                                         byteStream, 
                                                         dsState,
                                                         logMessage, 
                                                         force));
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a);             
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        } finally {
        	logger.debug("end: modifyDatastreamByValue, " + PID + ", " + datastreamID);
        }
    }

    public String setDatastreamState(String PID, 
                                     String datastreamID, 
                                     String dsState, 
                                     String logMessage) throws java.rmi.RemoteException {
        assertInitialized();
        try {
            return DateUtility.convertDateToString(
                    s_management.setDatastreamState(ReadOnlyContext.getSoapContext(), 
                                                    PID,
                                                    datastreamID, 
                                                    dsState, 
                                                    logMessage));
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a); 
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        }
    }

    public String setDisseminatorState(String PID, 
                                       String disseminatorID, 
                                       String dissState, 
                                       String logMessage) throws java.rmi.RemoteException {
        assertInitialized();
        try {
            return DateUtility.convertDateToString(
                    s_management.setDisseminatorState(ReadOnlyContext.getSoapContext(), 
                                                      PID,
                                                      disseminatorID, 
                                                      dissState, 
                                                      logMessage));
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a);             
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        }
    }

    public String[] purgeDatastream(String PID, 
                                    String datastreamID, 
                                    String endDT,
                                    String logMessage,
                                    boolean force) 
            throws java.rmi.RemoteException {
    	logger.debug("start: purgeDatastream, " + PID + ", " + datastreamID);
        assertInitialized();
        try {
            return toStringArray(
                    s_management.purgeDatastream(
                            ReadOnlyContext.getSoapContext(), 
                            PID, 
                            datastreamID,
                            DateUtility.convertStringToDate(endDT),
                            logMessage,
                            force));
        } catch (AuthzException a) {
            logStackTrace(a);
            throw AxisUtility.getFault(a);             
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        } catch (Exception e) {
            throw AxisUtility.getFault(new ServerInitializationException(e.getClass().getName() + ": " + e.getMessage()));
        } finally {
        	logger.debug("end: purgeDatastream, " + PID + ", " + datastreamID);
        }
    }

    private String[] toStringArray(Date[] dates) throws Exception {
        String[] out = new String[dates.length];
        for (int i = 0; i < dates.length; i++) {
            out[i] = DateUtility.convertDateToString(dates[i]);
        }
        return out;
    }

    public fedora.server.types.gen.Datastream getDatastream(String PID, 
                                                            String datastreamID, 
                                                            String asOfDateTime) 
            throws java.rmi.RemoteException {
        assertInitialized();
        try {
            fedora.server.storage.types.Datastream ds=
                    s_management.getDatastream(
                            ReadOnlyContext.getSoapContext(), 
                            PID, 
                            datastreamID, 
                            DateUtility.convertStringToDate(asOfDateTime));
            return TypeUtility.convertDatastreamToGenDatastream(ds);
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a);             
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        } catch (Exception e) {
            throw AxisUtility.getFault(new ServerInitializationException(e.getClass().getName() + ": " + e.getMessage()));
        }
    }

    public fedora.server.types.gen.Datastream[] getDatastreams(String PID, 
                                                               String asOfDateTime, 
                                                               String state) 
            throws java.rmi.RemoteException {
        assertInitialized();
        try {
            fedora.server.storage.types.Datastream[] intDatastreams=
                    s_management.getDatastreams(
                            ReadOnlyContext.getSoapContext(), 
                            PID, 
                            DateUtility.convertStringToDate(asOfDateTime),
                            state);
            return getGenDatastreams(intDatastreams);
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a);             
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        } catch (Exception e) {
            throw AxisUtility.getFault(new ServerInitializationException(e.getClass().getName() + ": " + e.getMessage()));
        }
    }

    private fedora.server.types.gen.Datastream[] getGenDatastreams(
            fedora.server.storage.types.Datastream[] intDatastreams) {
        fedora.server.types.gen.Datastream[] genDatastreams=
                new fedora.server.types.gen.Datastream[intDatastreams.length];
    	for (int i=0; i<intDatastreams.length; i++) {
    	    genDatastreams[i]=TypeUtility.convertDatastreamToGenDatastream(
   		        intDatastreams[i]);
		}
        return genDatastreams;
    }

    private fedora.server.types.gen.Disseminator[] getGenDisseminators(
            fedora.server.storage.types.Disseminator[] intDisseminators) {
        fedora.server.types.gen.Disseminator[] genDisseminators=
                new fedora.server.types.gen.Disseminator[intDisseminators.length];
            for (int i=0; i<intDisseminators.length; i++) {
                genDisseminators[i]=TypeUtility.convertDisseminatorToGenDisseminator(
                           intDisseminators[i]);
                }
        return genDisseminators;
    }

    public fedora.server.types.gen.Datastream[] getDatastreamHistory(String PID, String datastreamID)
            throws java.rmi.RemoteException {
        assertInitialized();
        try {
            fedora.server.storage.types.Datastream[] intDatastreams=
                    s_management.getDatastreamHistory(
                            ReadOnlyContext.getSoapContext(),
                            PID,
                            datastreamID);
            return getGenDatastreams(intDatastreams);
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a);             
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        } catch (Exception e) {
            throw AxisUtility.getFault(new ServerInitializationException(e.getClass().getName() + ": " + e.getMessage()));
        }
    }

	public String addDisseminator(String PID, 
	                              String bDefPID, 
	                              String bMechPID, 
	                              String dissLabel,  
	                              fedora.server.types.gen.DatastreamBindingMap bindingMap, 
	                              String dissState,
	                              String logMessage) 
            throws java.rmi.RemoteException {
		assertInitialized();
		try {
			return s_management.addDisseminator(
			        ReadOnlyContext.getSoapContext(), 
		            PID, 
	                bDefPID, 
                    bMechPID, 
                    dissLabel, 
                    TypeUtility.convertGenDatastreamBindingMapToDSBindingMap(bindingMap), 
                    dissState,
                    logMessage);
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a); 			
		} catch (ServerException se) {
			logStackTrace(se);
			throw AxisUtility.getFault(se);
	}
}

    public String[] purgeDisseminator(String PID,
                                      String disseminatorID, 
                                      String endDT,
                                      String logMessage)
            throws java.rmi.RemoteException {
        assertInitialized();
        try {
            return toStringArray(
                    s_management.purgeDisseminator(
                            ReadOnlyContext.getSoapContext(), 
                            PID, 
                            disseminatorID, 
                            DateUtility.convertStringToDate(endDT),
                            logMessage));
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a);             
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        } catch (Exception e) {
            throw AxisUtility.getFault(new ServerInitializationException(e.getClass().getName() + ": " + e.getMessage()));
        }
    }

    public fedora.server.types.gen.Disseminator[] getDisseminatorHistory(String PID, 
                                                                         String disseminatorID) 
              throws java.rmi.RemoteException {
      assertInitialized();
      try {
          fedora.server.storage.types.Disseminator[] intDisseminators=
                  s_management.getDisseminatorHistory(
                          ReadOnlyContext.getSoapContext(),
                          PID,
                          disseminatorID);
          return getGenDisseminators(intDisseminators);
      } catch (AuthzException a) {
          throw AxisUtility.getFault(a);           
      } catch (ServerException se) {
          logStackTrace(se);
          throw AxisUtility.getFault(se);
      } catch (Exception e) {
          throw AxisUtility.getFault(new ServerInitializationException(e.getClass().getName() + ": " + e.getMessage()));
        }
    }

    public fedora.server.types.gen.Disseminator getDisseminator(String PID, 
                                                                String disseminatorID, 
                                                                String asOfDateTime) 
            throws java.rmi.RemoteException {
        assertInitialized();
        try {
            fedora.server.storage.types.Disseminator diss=
                    s_management.getDisseminator(
                            ReadOnlyContext.getSoapContext(), 
                            PID, 
                            disseminatorID, 
                            DateUtility.convertStringToDate(asOfDateTime));
            return TypeUtility.convertDisseminatorToGenDisseminator(diss);
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a);             
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        } catch (Exception e) {
            throw AxisUtility.getFault(new ServerInitializationException(e.getClass().getName() + ": " + e.getMessage()));
        }
    }

    public fedora.server.types.gen.Disseminator[] getDisseminators(String PID, 
                                                                   String asOfDateTime, 
                                                                   String dissState) 
              throws java.rmi.RemoteException {
        assertInitialized();
        try {
            fedora.server.storage.types.Disseminator[] intDisseminators=
                    s_management.getDisseminators(
                            ReadOnlyContext.getSoapContext(), 
                            PID, 
                            DateUtility.convertStringToDate(asOfDateTime),
                            dissState);
            return getGenDisseminators(intDisseminators);
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a);             
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        } catch (Exception e) {
            throw AxisUtility.getFault(new ServerInitializationException(e.getClass().getName() + ": " + e.getMessage()));
        }
    }

    public String modifyDisseminator(String PID, 
                                     String disseminatorID, 
                                     String bMechPID, 
                                     String dissLabel,  
                                     fedora.server.types.gen.DatastreamBindingMap bindingMap, 
                                     String dissState,
                                     String logMessage,
                                     boolean force)
            throws java.rmi.RemoteException {
        assertInitialized();
        try {
            return DateUtility.convertDateToString(
                    s_management.modifyDisseminator(
                            ReadOnlyContext.getSoapContext(), 
                            PID,
                            disseminatorID, 
                            bMechPID, 
                            dissLabel, 
                            TypeUtility.convertGenDatastreamBindingMapToDSBindingMap(bindingMap),
                            dissState,
                            logMessage,
                            force));
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a);             
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        }
    }

    public java.lang.String[] getNextPID(NonNegativeInteger numPIDs,
            String namespace)
            throws java.rmi.RemoteException {
    	logger.debug("start: getNextPID");
        assertInitialized();
        try {
            if(numPIDs==null) numPIDs=new NonNegativeInteger("1");
            return s_management.getNextPID(ReadOnlyContext.getSoapContext(), numPIDs.intValue(), namespace);
        } catch (AuthzException a) {
            throw AxisUtility.getFault(a);             
        } catch (ServerException se) {
            logStackTrace(se);
            throw AxisUtility.getFault(se);
        } catch (Exception e) {
            throw AxisUtility.getFault(new ServerInitializationException(e.getClass().getName() + ": " + e.getMessage()));
        } finally {
        	logger.debug("end: getNextPID");
        }
    }


    private void assertInitialized()
            throws java.rmi.RemoteException {
        if (!s_initialized) {
            AxisUtility.throwFault(s_initException);
        }
    }

}
