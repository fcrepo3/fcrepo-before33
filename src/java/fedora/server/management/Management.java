package fedora.server.management;

import java.io.InputStream;
import java.util.Date;

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.Disseminator;
import fedora.server.storage.types.DSBindingMap;
import fedora.server.storage.types.Property;

/**
 *
 * <p><b>Title:</b> Management.java</p>
 * <p><b>Description:</b> The management subsystem interface.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public interface Management {

    public String ingestObject(Context context, 
                               InputStream serialization, 
                               String logMessage, 
                               String format, 
                               String encoding, 
                               boolean newPid) throws ServerException;

    public Date modifyObject(Context context, 
                             String pid, 
                             String state,
                             String label, 
                             String logMessage) throws ServerException;

    
	public Property[] getObjectProperties(Context context, 
										  String pid) throws ServerException;
			
    public InputStream getObjectXML(Context context, 
                                    String pid, 
                                    String encoding) throws ServerException;

    public InputStream exportObject(Context context, 
                                    String pid, 
                                    String format,
                                    String exportContext, 
                                    String encoding) throws ServerException;

    public Date purgeObject(Context context, 
                            String pid, 
                            String logMessage,
                            boolean force) throws ServerException;

    public String addDatastream(Context context,
                                   String pid,
                                   String dsID,
                                   String[] altIDs,
                                   String dsLabel,
                                   boolean versionable,
                                   String MIMEType,
                                   String formatURI,
                                   String location,
                                   String controlGroup,
                                   String dsState,
                                   String logMessage) throws ServerException;

    public Date modifyDatastreamByReference(Context context, 
                                            String pid, 
                                            String datastreamID, 
                                            String[] altIDs,
                                            String dsLabel, 
                                            boolean versionable,
                                            String mimeType,
                                            String formatURI,
                                            String dsLocation, 
                                            String dsState,
                                            String logMessage, 
                                            boolean force) throws ServerException;

    public Date modifyDatastreamByValue(Context context, 
                                        String pid, 
                                        String datastreamID, 
                                        String[] altIDs,
                                        String dsLabel, 
                                        boolean versionable,
                                        String mimeType,
                                        String formatURI,
                                        InputStream dsContent, 
                                        String dsState,
                                        String logMessage,
                                        boolean force) throws ServerException;

    public Date[] purgeDatastream(Context context, 
                                  String pid, 
                                  String datastreamID, 
                                  Date endDT,
                                  String logMessage,
                                  boolean force) throws ServerException;

    public Datastream getDatastream(Context context, 
                                    String pid, 
                                    String datastreamID, 
                                    Date asOfDateTime) throws ServerException;

    public Datastream[] getDatastreams(Context context, 
                                       String pid, 
                                       Date asOfDateTime, 
                                       String dsState) throws ServerException;

    public Datastream[] getDatastreamHistory(Context context, 
                                             String pid, 
                                             String datastreamID) throws ServerException;

	public String addDisseminator(Context context,
								  String pid,
								  String bDefPID,
								  String bMechPid,
								  String dissLabel,
								  DSBindingMap bindingMap,
								  String dissState,
								  String logMessage) throws ServerException;

    public Date modifyDisseminator(Context context, 
                                   String pid, 
                                   String disseminatorID, 
                                   String bMechPid, 
                                   String dissLabel, 
                                   DSBindingMap bindingMap, 
                                   String dissState,
                                   String logMessage,
                                   boolean force) throws ServerException;

    public Date[] purgeDisseminator(Context context, 
                                    String pid, 
                                    String disseminatorID, 
                                    Date endDT,
                                    String logMessage) throws ServerException;

    public Disseminator getDisseminator(Context context, 
                                        String pid, 
                                        String disseminatorID, 
                                        Date asOfDateTime) throws ServerException;

    public Disseminator[] getDisseminators(Context context, 
                                           String pid, 
                                           Date asOfDateTime, 
                                           String dissState) throws ServerException;

    public Disseminator[] getDisseminatorHistory(Context context, 
                                                 String pid, 
                                                 String disseminatorID) throws ServerException;

    public String putTempStream(Context context, InputStream in) throws ServerException;

    public InputStream getTempStream(String id) throws ServerException;

    public Date setDatastreamState(Context context, 
                                   String pid, 
                                   String dsID, 
                                   String dsState, 
                                   String logMessage) throws ServerException;

    public Date setDisseminatorState(Context context,
                                     String pid, 
                                     String dsID, 
                                     String dsState, 
                                     String logMessage) throws ServerException;

    public String[] getNextPID(Context context, 
                               int numPIDs, 
                               String namespace) throws ServerException;

    public boolean adminPing(Context context) throws ServerException;
    
}
