package fedora.server.management;

import java.io.InputStream;
import java.util.Calendar;

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.types.gen.ObjectInfo;
import fedora.server.storage.types.Datastream;

/**
 *
 * <p><b>Title:</b> Management.java</p>
 * <p><b>Description:</b> The management subsystem interface.</p>
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
public interface Management {

//    public String createObject(Context context) throws ServerException;

    public String ingestObject(Context context, InputStream serialization, String logMessage, String format, String encoding, boolean newPid) throws ServerException;

    public InputStream getObjectXML(Context context, String pid, String format, String encoding) throws ServerException;

//    public InputStream exportObject(Context context, String pid, String format, String encoding) throws ServerException;

//    public void withdrawObject(Context context, String pid, String logMessage) throws ServerException;

//    public void deleteObject(Context context, String pid, String logMessage) throws ServerException;

    public void purgeObject(Context context, String pid, String logMessage) throws ServerException;

//    public void obtainLock(Context context, String pid) throws ServerException;

//    public void releaseLock(Context context, String pid, String logMessage, boolean commit) throws ServerException;

    public ObjectInfo getObjectInfo(Context context, String pid) throws ServerException;

//    public AuditRecord[] getObjectAuditTrail(Context context, String pid) throws ServerException;

    public String[] listObjectPIDs(Context context, String pidPattern,
            String foType, String lockedByPattern, String state,
            String labelPattern, String contentModelIdPattern,
            Calendar createDateMin, Calendar createDateMax,
            Calendar lastModDateMin, Calendar lastModDateMax)
            throws ServerException;

//    public String addDatastreamExternal(Context context, String pid, String dsLabel, String dsLocation) throws ServerException;

//    public String addDatastreamManagedContent(Context context, String pid, String dsLabel, String MimeType, InputStream dsContent) throws ServerException;

//    public String addDatastreamXMLMetadata(Context context, String pid, String dsLabel, String MdType, InputStream dsInlineMetadata) throws ServerException;

    public void modifyDatastreamByReference(Context context, String pid, String datastreamId, String dsLabel, String logMessage, String dsLocation) throws ServerException;

    public void modifyDatastreamByValue(Context context, String pid, String datastreamId, String dsLabel, String logMessage, InputStream dsContent) throws ServerException;

//    public void withdrawDatastream(Context context, String pid, String datastreamId) throws ServerException;

//    public void withdrawDisseminator(Context context, String pid, String disseminatorId) throws ServerException;

//    public void deleteDatastream(Context context, String pid, String datastreamID) throws ServerException;

//    public Calendar[] purgeDatastream(Context context, String pid, String datastreamID, Calendar startDT, Calendar endDT) throws ServerException;

    public Datastream getDatastream(Context context, String pid, String datastreamID, Calendar asOfDateTime) throws ServerException;

//    public Datastream[] getDatastreams(Context context, String pid, Calendar asOfDateTime) throws ServerException;

    public String[] listDatastreamIDs(Context context, String pid, String state) throws ServerException;

//    public ComponentInfo[] getDatastreamHistory(Context context, String pid, String datastreamID) throws ServerException;

//    public String addDisseminator(Context context, String pid, String bMechPid, String dissLabel, DatastreamBindingMap bindingMap) throws ServerException;

//    public void modifyDisseminator(Context context, String pid, String disseminatorId, String bMechPid, String dissLabel, DatastreamBindingMap bindingMap) throws ServerException;

//    public void deleteDisseminator(Context context, String pid, String disseminatorId) throws ServerException;

//    public Calendar[] purgeDisseminator(Context context, String pid, String disseminatorId, Calendar startDateTime, Calendar endDateTime) throws ServerException;

//    public Disseminator getDisseminator(Context context, String pid, String disseminatorId, Calendar asOfDateTime) throws ServerException;

//    public Disseminator[] getDisseminators(Context context, String pid, Calendar asOfDateTime) throws ServerException;

//    public String[] listDisseminatorIDs(Context context, String pid, String state) throws ServerException;

//    public ComponentInfo[] getDisseminatorHistory(Context context, String pid, String disseminatorId) throws ServerException;

}