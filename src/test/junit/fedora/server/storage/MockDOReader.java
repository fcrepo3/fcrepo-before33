
package fedora.server.storage;

import java.io.InputStream;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import fedora.server.errors.ServerException;
import fedora.server.storage.types.AuditRecord;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.storage.types.RelationshipTuple;

/**
 * A partial implementation of {@link DOReader} for use in unit tests. Add more
 * mocking to this class as needed, or override methods in sub-classes.
 * 
 * @author Jim Blake
 */
public class MockDOReader
        implements DOReader {

    // ----------------------------------------------------------------------
    // Mocking infrastructure
    // ----------------------------------------------------------------------

    protected final DigitalObject theObject;

    public MockDOReader(DigitalObject theObject) {
        this.theObject = theObject;
    }

    // ----------------------------------------------------------------------
    // Mocked methods
    // ----------------------------------------------------------------------

    public Datastream GetDatastream(String datastreamID, Date versDateTime)
            throws ServerException {
        List<Datastream> datastreams = theObject.datastreams(datastreamID);
        if (datastreams.isEmpty()) {
            // If no datastreams, return null.
            return null;
        }

        // Sort versions from newest to oldest.
        Collections.sort(datastreams, new Comparator<Datastream>() {

            public int compare(Datastream o1, Datastream o2) {
                return o2.DSCreateDT.compareTo(o1.DSCreateDT);
            }
        });

        if (versDateTime == null) {
            // If no date specified, return the newest version.
            return datastreams.get(0);
        } else {
            // If date is specified, return the newest version that is older
            // than the specified date.
            for (Datastream datastream : datastreams) {
                if (datastream.DSCreateDT.before(versDateTime)) {
                    return datastream;
                }
            }
            // If none are old enough, return null.
            return null;
        }
    }

    public String GetObjectLabel() throws ServerException {
        return theObject.getLabel();
    }

    public String GetObjectPID() throws ServerException {
        return theObject.getPid();
    }

    public String GetObjectState() throws ServerException {
        return theObject.getState();
    }

    public String getContentModelId() throws ServerException {
        return theObject.getContentModelId();
    }

    public Date getCreateDate() throws ServerException {
        return theObject.getCreateDate();
    }

    public String getFedoraObjectTypes() throws ServerException {
        return theObject.getFedoraObjectTypes();
    }

    public Date getLastModDate() throws ServerException {
        return theObject.getLastModDate();
    }

    public String getOwnerId() throws ServerException {
        return theObject.getOwnerId();
    }

    // ----------------------------------------------------------------------
    // Un-implemented methods
    // ----------------------------------------------------------------------

    public InputStream ExportObject(String format, String exportContext)
            throws ServerException {
        throw new RuntimeException("MockDOReader.ExportObject not implemented");
    }

    public Datastream[] GetDatastreams(Date versDateTime, String state)
            throws ServerException {
        throw new RuntimeException("MockDOReader.GetDatastreams not implemented");
    }

    public InputStream GetObjectXML() throws ServerException {
        throw new RuntimeException("MockDOReader.GetObjectXML not implemented");
    }

    public String[] ListDatastreamIDs(String state) throws ServerException {
        throw new RuntimeException("MockDOReader.ListDatastreamIDs not implemented");
    }

    public List<AuditRecord> getAuditRecords() throws ServerException {
        throw new RuntimeException("MockDOReader.getAuditRecords not implemented");
    }

    public Datastream getDatastream(String datastreamID, String versionID)
            throws ServerException {
        throw new RuntimeException("MockDOReader.getDatastream not implemented");
    }

    public Date[] getDatastreamVersions(String datastreamID)
            throws ServerException {
        throw new RuntimeException("MockDOReader.getDatastreamVersions not implemented");
    }

    public String[] getObjectHistory(String PID) throws ServerException {
        throw new RuntimeException("MockDOReader.getObjectHistory not implemented");
    }

    public RelationshipTuple[] getRelationships(String relationship)
            throws ServerException {
        throw new RuntimeException("MockDOReader.getRelationships not implemented");
    }

    public boolean isFedoraObjectType(int type) throws ServerException {
        throw new RuntimeException("MockDOReader.isFedoraObjectType not implemented");
    }

    public ObjectMethodsDef[] listMethods(Date versDateTime)
            throws ServerException {
        throw new RuntimeException("MockDOReader.listMethods not implemented");
    }

}
