package fedora.server.management;

import java.io.InputStream;
import java.util.Calendar;

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.types.gen.AuditRecord;
import fedora.server.types.gen.ComponentInfo;
import fedora.server.types.gen.Datastream;
import fedora.server.types.gen.DatastreamBindingMap;
import fedora.server.types.gen.Disseminator;
import fedora.server.types.gen.ObjectInfo;

/**
 * The management subsystem interface.
 */
public interface Management {

    public String createObject(Context context) throws ServerException;

    public String ingestObject(Context context, InputStream serialization, String format, String encoding, boolean newPid) throws ServerException;

    public InputStream getObjectXML(Context context, String pid, String format, String encoding) throws ServerException;

    public InputStream exportObject(Context context, String pid, String format, String encoding) throws ServerException;

    public void withdrawObject(Context context, String pid, String logMessage) throws ServerException;

    public void deleteObject(Context context, String pid, String logMessage) throws ServerException;

    public void purgeObject(Context context, String pid, String logMessage) throws ServerException;

    public void obtainLock(Context context, String pid) throws ServerException;

    public void releaseLock(Context context, String pid, String logMessage, boolean commit) throws ServerException;

    public ObjectInfo getObjectInfo(Context context, String pid) throws ServerException;

    public AuditRecord[] getObjectAuditTrail(Context context, String pid) throws ServerException;

    public String[] listObjectPIDs(Context context, String foType) throws ServerException;

    public String addDatastreamExternal(Context context, String pid, String dsLabel, String dsLocation) throws ServerException;

    public String addDatastreamManagedContent(Context context, String pid, String dsLabel, String MimeType, InputStream dsContent) throws ServerException;

    public String addDatastreamXMLMetadata(Context context, String pid, String dsLabel, String MdType, InputStream dsInlineMetadata) throws ServerException;

    public void modifyDatastreamExternal(Context context, String pid, String datastreamId, String dsLabel, String dsLocation) throws ServerException;

    public void modifyDatastreamManagedContent(Context context, String pid, String datastreamId, String dsLabel, String MimeType, InputStream dsContent) throws ServerException;

    public void modifyDatastreamXMLMetadata(Context context, String pid, String datastreamId, String dsLabel, String MdType, InputStream dsInlineMetadata) throws ServerException;

    public void withdrawDatastream(Context context, String pid, String datastreamId) throws ServerException;

    public void withdrawDisseminator(Context context, String pid, String disseminatorId) throws ServerException;

    public void deleteDatastream(Context context, String pid, String datastreamID) throws ServerException;

    public Calendar[] purgeDatastream(Context context, String pid, String datastreamID, Calendar startDT, Calendar endDT) throws ServerException;

    public Datastream getDatastream(Context context, String pid, String datastreamID, Calendar asOfDateTime) throws ServerException;

    public Datastream[] getDatastreams(Context context, String pid, Calendar asOfDateTime) throws ServerException;

    public String[] listDatastreamIDs(Context context, String pid, String state) throws ServerException;

    public ComponentInfo[] getDatastreamHistory(Context context, String pid, String datastreamID) throws ServerException;

    public String addDisseminator(Context context, String pid, String bMechPid, String dissLabel, DatastreamBindingMap bindingMap) throws ServerException;

    public void modifyDisseminator(Context context, String pid, String disseminatorId, String bMechPid, String dissLabel, DatastreamBindingMap bindingMap) throws ServerException;

    public void deleteDisseminator(Context context, String pid, String disseminatorId) throws ServerException;

    public Calendar[] purgeDisseminator(Context context, String pid, String disseminatorId, Calendar startDateTime, Calendar endDateTime) throws ServerException;

    public Disseminator getDisseminator(Context context, String pid, String disseminatorId, Calendar asOfDateTime) throws ServerException;

    public Disseminator[] getDisseminators(Context context, String pid, Calendar asOfDateTime) throws ServerException;

    public String[] listDisseminatorIDs(Context context, String pid, String state) throws ServerException;

    public ComponentInfo[] getDisseminatorHistory(Context context, String pid, String disseminatorId) throws ServerException;

}