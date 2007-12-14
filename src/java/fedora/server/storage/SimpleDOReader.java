/*
 * The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.jrdf.graph.Literal;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.Triple;
import org.trippi.RDFFormat;
import org.trippi.TripleIterator;
import org.trippi.TrippiException;

import fedora.common.Constants;
import fedora.server.Context;
import fedora.server.errors.DisseminationException;
import fedora.server.errors.GeneralException;
import fedora.server.errors.MethodNotFoundException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StorageException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.management.DefaultManagement;
import fedora.server.storage.translation.DOTranslationUtility;
import fedora.server.storage.translation.DOTranslator;
import fedora.server.storage.types.AuditRecord;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.storage.types.RelationshipTuple;
import fedora.server.utilities.DateUtility;

/**
 * A DOReader backed by a DigitalObject.
 * 
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class SimpleDOReader implements DOReader {

    /** Logger for this class. */
    private static final Logger LOG = Logger.getLogger(SimpleDOReader.class
            .getName());

    protected DigitalObject m_obj;
    private Context m_context;
    private RepositoryReader m_repoReader;
    private DOTranslator m_translator;
    private String m_exportFormat;
    private String m_storageFormat;
    private String m_encoding;

    private SimpleDateFormat m_formatter = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public SimpleDOReader(Context context, RepositoryReader repoReader,
            DOTranslator translator, String exportFormat, String storageFormat,
            String encoding, InputStream serializedObject)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException {
        m_context = context;
        m_repoReader = repoReader;
        m_translator = translator;
        m_exportFormat = exportFormat;
        m_storageFormat = storageFormat;
        m_encoding = encoding;
        m_obj = new BasicDigitalObject();
        m_translator.deserialize(serializedObject, m_obj, m_storageFormat,
                encoding, DOTranslationUtility.DESERIALIZE_INSTANCE);
    }

    /**
     * Alternate constructor for when a DigitalObject is already available for
     * some reason.
     */
    public SimpleDOReader(Context context, RepositoryReader repoReader,
            DOTranslator translator, String exportFormat, String encoding,
            DigitalObject obj) {
        m_context = context;
        m_repoReader = repoReader;
        m_translator = translator;
        m_exportFormat = exportFormat;
        m_encoding = encoding;
        m_obj = obj;
    }

    public String getFedoraObjectTypes() {
        return (m_obj.getFedoraObjectTypes());
    }

    public boolean isFedoraObjectType(int type) {
        return (m_obj.isFedoraObjectType(type));
    }

    public String getContentModelId() {
        return m_obj.getContentModelId();
    }

    public Date getCreateDate() {
        return m_obj.getCreateDate();
    }

    public Date getLastModDate() {
        return m_obj.getLastModDate();
    }

    public String getOwnerId() {
        return m_obj.getOwnerId();
    }

    public List<AuditRecord> getAuditRecords() {
        return m_obj.getAuditRecords();
    }

    /**
     * Return the object as an XML input stream in the internal serialization
     * format.
     */
    public InputStream GetObjectXML() throws ObjectIntegrityException,
            StreamIOException, UnsupportedTranslationException, ServerException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        m_translator.serialize(m_obj, bytes, m_storageFormat, "UTF-8",
                DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL);
        return new ByteArrayInputStream(bytes.toByteArray());
    }

    /**
     * Return the object as an XML input stream in the specified XML format and
     * in the specified export context.
     * 
     * See DOTranslationUtility.class for description of export contexts
     * (translation contexts).
     * 
     * @param format
     *            The format to export the object in. If null or "default", will
     *            use the repository's configured default export format.
     * @param exportContext
     *            The use case for export (public, migrate, archive) which
     *            results in different ways of representing datastream URLs or
     *            datastream content in the output.
     */
    public InputStream ExportObject(String format, String exportContext)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException, ServerException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        int transContext;
        // first, set the translation context...
        LOG.debug("ExportObject export context: " + exportContext);

        if (exportContext == null || exportContext.equals("")
                || exportContext.equalsIgnoreCase("default")) {
            // null and default is set to PUBLIC translation
            transContext = DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC;
        } else if (exportContext.equalsIgnoreCase("public")) {
            transContext = DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC;
        } else if (exportContext.equalsIgnoreCase("migrate")) {
            transContext = DOTranslationUtility.SERIALIZE_EXPORT_MIGRATE;
        } else if (exportContext.equalsIgnoreCase("archive")) {
            transContext = DOTranslationUtility.SERIALIZE_EXPORT_ARCHIVE;
        } else {
            throw new UnsupportedTranslationException("Export context "
                    + exportContext + " is not valid.");
        }
        // now serialize for export in the proper XML format...
        if (format == null || format.equals("")
                || format.equalsIgnoreCase("default")) {
            LOG.debug("ExportObject in default format: " + m_exportFormat);
            m_translator.serialize(m_obj, bytes, m_exportFormat, "UTF-8",
                    transContext);
        } else {
            LOG.debug("ExportObject in format: " + format);
            m_translator.serialize(m_obj, bytes, format, "UTF-8", transContext);
        }

        return new ByteArrayInputStream(bytes.toByteArray());
    }

    public String GetObjectPID() {
        return m_obj.getPid();
    }

    public String GetObjectLabel() {
        return m_obj.getLabel();
    }

    public String GetObjectState() {
        if (m_obj.getState() == null)
            return "A"; // shouldn't happen, but if it does don't die
        return m_obj.getState();
    }

    public String[] ListDatastreamIDs(String state) {
        Iterator<String> iter = m_obj.datastreamIdIterator();
        ArrayList<String> al = new ArrayList<String>();
        while (iter.hasNext()) {
            String dsId = iter.next();
            if (state == null) {
                al.add(dsId);
            } else {
                // below should never return null -- already know id exists,
                // and am asking for any the latest existing one.
                Datastream ds = GetDatastream(dsId, null);
                if (ds.DSState.equals(state)) {
                    al.add(dsId);
                }
            }
        }
        iter = al.iterator();
        String[] out = new String[al.size()];
        int i = 0;
        while (iter.hasNext()) {
            out[i] = iter.next();
            i++;
        }
        return out;
    }

    // returns null if can't find
    public Datastream getDatastream(String dsID, String versionID) {
        List allVersions = m_obj.datastreams(dsID);
        for (int i = 0; i < allVersions.size(); i++) {
            Datastream ds = (Datastream) allVersions.get(i);
            if (ds.DSVersionID.equals(versionID)) {
                return ds;
            }
        }
        return null;
    }

    // returns null if can't find
    public Datastream GetDatastream(String datastreamID, Date versDateTime) {
        List allVersions = m_obj.datastreams(datastreamID);
        if (allVersions.size() == 0) {
            return null;
        }
        // get the one with the closest creation date
        // without going over
        Iterator dsIter = allVersions.iterator();
        Datastream closestWithoutGoingOver = null;
        Datastream latestCreated = null;
        long bestTimeDifference = -1;
        long latestCreateTime = -1;
        long vTime = -1;
        if (versDateTime != null) {
            vTime = versDateTime.getTime();
        }
        while (dsIter.hasNext()) {
            Datastream ds = (Datastream) dsIter.next();
            if (versDateTime == null) {
                if (ds.DSCreateDT.getTime() > latestCreateTime) {
                    latestCreateTime = ds.DSCreateDT.getTime();
                    latestCreated = ds;
                }
            } else {
                long diff = vTime - ds.DSCreateDT.getTime();
                if (diff >= 0) {
                    if ((diff < bestTimeDifference)
                            || (bestTimeDifference == -1)) {
                        bestTimeDifference = diff;
                        closestWithoutGoingOver = ds;
                    }
                }
            }
        }
        if (versDateTime == null) {
            return latestCreated;
        } else {
            return closestWithoutGoingOver;
        }
    }

    public Date[] getDatastreamVersions(String datastreamID) {
        List l = m_obj.datastreams(datastreamID);
        Date[] versionDates = new Date[l.size()];
        for (int i = 0; i < l.size(); i++) {
            versionDates[i] = ((Datastream) l.get(i)).DSCreateDT;
        }
        return versionDates;
    }

    public Datastream[] GetDatastreams(Date versDateTime, String state) {
        String[] ids = ListDatastreamIDs(null);
        ArrayList<Datastream> al = new ArrayList<Datastream>();
        for (int i = 0; i < ids.length; i++) {
            Datastream ds = GetDatastream(ids[i], versDateTime);
            if (ds != null && (state == null || ds.DSState.equals(state))) {
                al.add(ds);
            }
        }
        Datastream[] out = new Datastream[al.size()];
        Iterator<Datastream> iter = al.iterator();
        int i = 0;
        while (iter.hasNext()) {
            out[i] = iter.next();
            i++;
        }
        return out;
    }

    /**
     * <p>
     * Gets the change history of an object by returning a list of timestamps
     * that correspond to modification dates of components. This currently
     * includes changes to datastreams and disseminators.
     * </p>
     * 
     * @param PID
     *            The persistent identifier of the digitla object.
     * @return An Array containing the list of timestamps indicating when
     *         changes were made to the object.
     */
    public String[] getObjectHistory(String PID) {
        String[] dsIDs = ListDatastreamIDs("A");
        TreeSet<String> modDates = new TreeSet<String>();
        for (int i = 0; i < dsIDs.length; i++) {
            Date[] dsDates = getDatastreamVersions(dsIDs[i]);
            for (int j = 0; j < dsDates.length; j++) {
                modDates.add(DateUtility.convertDateToString(dsDates[j]));
            }
        }
        return (String[]) modDates.toArray(new String[0]);
    }

    public MethodDef[] listMethods(String bDefPID, BMechReader bmechreader,
            Date versDateTime) throws MethodNotFoundException, ServerException {
        if (bDefPID.equalsIgnoreCase("fedora-system:1")
                || bDefPID.equalsIgnoreCase("fedora-system:3")) {
            throw new MethodNotFoundException("[getObjectMethods] The object, "
                    + m_obj.getPid()
                    + ", will not report on dynamic method definitions "
                    + "at this time (fedora-system:1 and fedora-system:3.");
        }

        if (bmechreader == null) {
            return null;
        }
        MethodDef[] methods = bmechreader.getServiceMethods(versDateTime);
        // Filter out parms that are internal to the mechanism and not part
        // of the abstract method definition. We just want user parms.
        for (int i = 0; i < methods.length; i++) {
            methods[i].methodParms = filterParms(methods[i]);
        }
        return methods;
    }

    /**
     * Filter out mechanism-specific parms (system default parms and datastream
     * input parms) so that what is returned is only method parms that reflect
     * abstract method definitions. Abstract method definitions only expose
     * user-supplied parms.
     * 
     * @param method
     * @return
     */
    public MethodParmDef[] filterParms(MethodDef method) {
        ArrayList filteredParms = new ArrayList();
        MethodParmDef[] parms = method.methodParms;
        for (int i = 0; i < parms.length; i++) {
            if (parms[i].parmType.equalsIgnoreCase(MethodParmDef.USER_INPUT)) {
                filteredParms.add(parms[i]);
            }
        }
        return (MethodParmDef[]) filteredParms.toArray(new MethodParmDef[0]);
    }

    protected String getWhenString(Date versDateTime) {
        if (versDateTime != null) {
            return m_formatter.format(versDateTime);
        } else {
            return "the current time";
        }
    }

    public ObjectMethodsDef[] listMethods(Date versDateTime)
            throws ServerException {
        ArrayList methodList = new ArrayList();
        ArrayList bDefIDList = new ArrayList();

        BMechReader bmechreader = null;
        RelationshipTuple cmPIDs[] = getRelationships(Constants.RELS_EXT.HAS_FORMAL_CONTENT_MODEL.uri);
        if (cmPIDs != null && cmPIDs.length > 0) {
            for (int i = 0; i < cmPIDs.length; i++) {
                DOReader cmReader;
                String cModelPid = cmPIDs[i].getObjectPID();
                if (cModelPid.equals("self")) {
                    cmReader = this;
                    cModelPid = GetObjectPID();
                } else {
                    try {
                        cmReader = m_repoReader.getReader(false, m_context,
                                cModelPid);
                    } catch (StorageException e) {
                        throw new DisseminationException(null,
                                "Content Model Object " + cModelPid
                                        + " does not exist.", null, null, e);
                    }
                }
                RelationshipTuple bDefPIDs[] = cmReader
                        .getRelationships(Constants.RELS_EXT.HAS_BDEF.uri);
                if (bDefPIDs != null && bDefPIDs.length > 0) {
                    boolean initialized = false;
                    for (int j = 0; j < bDefPIDs.length; j++) {
                        String bDefPid = bDefPIDs[j].getObjectPID();
                        DOManager manager = null;
                        MethodDef[] methods = null;
                        if (m_repoReader instanceof DOManager) {
                            manager = (DOManager) m_repoReader;
                            if (!initialized) {
                                manager.initializeCModelBmechHashMap(m_context);
                                initialized = true;
                            }
                            String bMechPid = manager.lookupBmechForCModel(
                                    cModelPid, bDefPid);
                            if (bMechPid == null) {
                                throw new DisseminationException(
                                        "No BMech defined as Contractor for Content Model "
                                                + cModelPid);
                            }
                            try {
                                bmechreader = m_repoReader.getBMechReader(
                                        false, m_context, bMechPid);
                            } catch (StorageException se) {
                                throw new DisseminationException(
                                        "BMech "
                                                + bMechPid
                                                + " defined as Contractor for Content Model "
                                                + cModelPid + " not found.");
                            }
                            methods = listMethods(bDefPIDs[j].getObjectPID(),
                                    bmechreader, versDateTime);
                        }
                        if (methods != null) {
                            for (int k = 0; k < methods.length; k++) {
                                methodList.add(methods[k]);
                                bDefIDList.add(bDefPIDs[j].getObjectPID());
                            }
                        }
                    }
                }
            }
        }

        ObjectMethodsDef[] ret = new ObjectMethodsDef[methodList.size()];
        for (int i = 0; i < methodList.size(); i++) {
            MethodDef def = (MethodDef) methodList.get(i);
            ret[i] = new ObjectMethodsDef();
            ret[i].PID = GetObjectPID();
            ret[i].bDefPID = (String) bDefIDList.get(i);
            ret[i].methodName = def.methodName;
            ret[i].methodParmDefs = def.methodParms;
            ret[i].asOfDate = versDateTime;
        }
        return ret;
    }

    public RelationshipTuple[] getRelationships(String relationship)
            throws ServerException {
        String datastreamID = DefaultManagement.s_RelsExt_Datastream; // RELS-EXT
        Datastream ds = GetDatastream(datastreamID, null);
        if (ds == null) {
            return new RelationshipTuple[0];
        }

        ArrayList<RelationshipTuple> tuples = new ArrayList<RelationshipTuple>();
        InputStream dsContent = ds.getContentStream();
        TripleIterator iter = null;
        try {
            iter = TripleIterator.fromStream(dsContent, RDFFormat.RDF_XML);
            Triple triple;
            ObjectNode objectNode;
            boolean isLiteral;
            URI datatypeURI;
            String subject, predicate, object, datatype;
            while (iter.hasNext()) {
                triple = iter.next();
                if (relationship == null
                        || relationship.length() == 0
                        || triple.getPredicate().toString()
                                .equals(relationship)) {
                    subject = triple.getSubject().toString();
                    predicate = triple.getPredicate().toString();
                    objectNode = triple.getObject();
                    isLiteral = objectNode instanceof Literal;
                    datatype = null;
                    if (isLiteral) {
                        object = ((Literal)objectNode).getLexicalForm();
                        datatypeURI = ((Literal)objectNode).getDatatypeURI();
                        if (datatypeURI != null) {
                            datatype = datatypeURI.toString();
                        }
                    } else {
                        object = triple.getObject().toString();
                    }
                    tuples.add(new RelationshipTuple(subject, predicate,
                            object, isLiteral, datatype));
                }
            }
        } catch (TrippiException e) {
            throw new GeneralException(e.getMessage(), e);
        } finally {
            if (iter != null) {
                try {
                    iter.close();
                } catch (TrippiException e) {
                    throw new GeneralException(e.getMessage(), e);
                }
            }
        }
        return tuples.toArray(new RelationshipTuple[tuples.size()]);
    }
}
