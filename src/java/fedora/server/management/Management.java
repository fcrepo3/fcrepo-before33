/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.management;

import java.io.InputStream;

import java.util.Date;

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.Property;
import fedora.server.storage.types.RelationshipTuple;

/**
 * The management subsystem interface.
 * 
 * @author Chris Wilper
 */
public interface Management {

	public String ingest(Context context,
                         InputStream serialization,
                         String logMessage,
                         String format,
                         String encoding,
                         boolean newPid) throws ServerException;

    /**
     * @deprecated in Fedora 3.0, use ingest() instead 
     */
	@Deprecated
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
                             String ownerId,
                             String logMessage) throws ServerException;

    public Property[] getObjectProperties(Context context, String pid)
            throws ServerException;

    public InputStream getObjectXML(Context context, String pid, String encoding)
            throws ServerException;

    public InputStream export(Context context,
                                    String pid,
                                    String format,
                                    String exportContext,
                                    String encoding) throws ServerException;   
    
    /**
     * @deprecated in Fedora 3.0, use export() instead 
     */
    @Deprecated
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
                                String checksumType,
                                String checksum,
                                String logMessage) throws ServerException;

    public Date modifyDatastreamByReference(Context context,
                                            String pid,
                                            String datastreamID,
                                            String[] altIDs,
                                            String dsLabel,
                                            String mimeType,
                                            String formatURI,
                                            String dsLocation,
                                            String checksumType,
                                            String checksum,
                                            String logMessage,
                                            boolean force)
            throws ServerException;

    public Date modifyDatastreamByValue(Context context,
                                        String pid,
                                        String datastreamID,
                                        String[] altIDs,
                                        String dsLabel,
                                        String mimeType,
                                        String formatURI,
                                        InputStream dsContent,
                                        String checksumType,
                                        String checksum,
                                        String logMessage,
                                        boolean force) throws ServerException;

    public Date[] purgeDatastream(Context context,
                                  String pid,
                                  String datastreamID,
                                  Date startDT,
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
                                             String datastreamID)
            throws ServerException;

    public String putTempStream(Context context, InputStream in)
            throws ServerException;

    public InputStream getTempStream(String id) throws ServerException;

    public Date setDatastreamState(Context context,
                                   String pid,
                                   String dsID,
                                   String dsState,
                                   String logMessage) throws ServerException;

    public Date setDatastreamVersionable(Context context,
                                         String pid,
                                         String dsID,
                                         boolean versionable,
                                         String logMessage)
            throws ServerException;

    public String compareDatastreamChecksum(Context context,
                                            String pid,
                                            String dsID,
                                            Date asOfDateTime)
            throws ServerException;

    public String[] getNextPID(Context context, int numPIDs, String namespace)
            throws ServerException;

    public RelationshipTuple[] getRelationships(Context context,
                                                String pid,
                                                String relationship)
            throws ServerException;

    public boolean addRelationship(Context context,
                                   String pid,
                                   String relationship,
                                   String object,
                                   boolean isLiteral,
                                   String datatype) throws ServerException;

    public boolean purgeRelationship(Context context,
                                     String pid,
                                     String relationship,
                                     String object,
                                     boolean isLiteral,
                                     String datatype) throws ServerException;

}
