package fedora.server.oai;

import java.util.Date;
import java.util.List;
import java.util.Set;

import fedora.server.search.FieldSearch;
import fedora.oai.*; //FIXME:evil

public class FedoraOAIProvider
        implements OAIProvider { 
        
    public FedoraOAIProvider(String repositoryName, String baseURL, 
        Set adminEmails, Set friendBaseURLs, String nsid, FieldSearch fieldSearch) {
    }
        

    /**
     * Get a human readable name for the repository.
     */
    public String getRepositoryName()
            throws RepositoryException {
        return null;
    }
    
    /**
     * Get the HTTP endpoint for the OAI-PMH interface.
     */
    public String getBaseURL()
            throws RepositoryException {
        return null;
    }
    
    /**
     * Get the version of the OAI-PMH supported by the repository.
     */
    public String getProtocolVersion()
            throws RepositoryException {
        return null;
    }

    /**
     * Get a Date (in UTC) that is the guaranteed lower limit of all datestamps
     * recording changes, modifications, or deletions in the repository.
     * A repository must not use datestamps lower than this.
     */
    public Date getEarliestDatestamp()
            throws RepositoryException {
        return null;
    }
    
    /**
     * Get the manner in which the repository supports the notion of deleted
     * records.
     */
    public DeletedRecordSupport getDeletedRecordSupport()
            throws RepositoryException {
        return null;
    }
    
    /**
     * Get the finest harvesting granularity supported by the repository.
     */
    public DateGranularitySupport getDateGranularitySupport()
            throws RepositoryException {
        return null;
    }
    
    /**
     * Get the email addresses of administrators of the repository.
     *
     * This set must contain at least one item.
     */
    public Set getAdminEmails()
            throws RepositoryException {
        return null;
    }
    
    /**
     * Get the compression encodings supported by the repository.
     *
     * This set may be empty. Recommended values are those in RFC 2616 Section 
     * 14.11
     */
    public Set getSupportedCompressionEncodings()
            throws RepositoryException {
        return null;
    }
    
    /**
     * Get XML descriptions of the repository.
     *
     * Each Set element must be a String containing a description according 
     * to some W3C schema, where the xsi:schemaLocation attribute is used
     * on the root element.
     *
     * See http://www.openarchives.org/OAI/2.0/guidelines.htm for guidelines
     * regarding these repository-level descriptions.
     */
    public Set getDescriptions()
            throws RepositoryException {
        return null;
    }

    /**
     * Get an individual metadata record from the repository.
     */
    public Record getRecord(String identifier, String metadataPrefix)
            throws CannotDisseminateFormatException, IDDoesNotExistException, 
            RepositoryException {
        return null;
    }

    /**
     * Get the Records in the repository matching the given criteria.
     * Any of the arguments (except metadataPrefix) may be null, indicating
     * "any".
     *
     * If the size of the returned list is over getMaxRecords(), the last element
     * is a resumptionToken (a String) which can be used to get the rest of the 
     * list.
     */
    public List getRecords(Date from, Date until, String metadataPrefix,
            String set)
            throws CannotDisseminateFormatException,
            NoRecordsMatchException, NoSetHierarchyException,
            RepositoryException {
        return null;
    }

    /**
     * Get the remaining portion of a set of Records.
     *
     * If the size of the returned list is over getMaxRecords(), the last element
     * is another resumptionToken (a String) which can be used to get the rest 
     * of the list.
     */
    public List getRecords(String resumptionToken)
            throws CannotDisseminateFormatException,
            NoRecordsMatchException, NoSetHierarchyException, 
            BadResumptionTokenException, RepositoryException {
        return null;
    }

    /**
     * Just like getRecords, but returns Header objects.
     *
     * If the size of the returned list is over getMaxHeaders(), the last element
     * is a resumptionToken (a String) which can be used to get the rest of the 
     * list.
     */
    public List getHeaders(Date from, Date until, String metadataPrefix,
            String set)
            throws CannotDisseminateFormatException, NoRecordsMatchException, 
            NoSetHierarchyException, RepositoryException {
        return null;
    }

    /**
     * Get the remaining portion of a set of Headers.
     *
     * If the size of the returned list is over getMaxHeaders() the last element
     * is another resumptionToken (a String) which can be used to get the rest 
     * of the list.
     */
    public List getHeaders(String resumptionToken)
            throws CannotDisseminateFormatException,
            NoRecordsMatchException, NoSetHierarchyException, 
            BadResumptionTokenException, RepositoryException {
        return null;
    }
            
    /**
     * Get the setSpecs, setNames, and setDescriptions of sets in the
     * repository.  Each set has a setSpec, a name, a zero or more
     * descriptions, held by a SetInfo object.
     *
     * If the size of the returned list is over getMaxSets(), the last element
     * is a resumptionToken (a String) which can be used to get the rest 
     * of the list.
     */
    public List getSets()
            throws NoSetHierarchyException, RepositoryException {
        return null;
    }

    /**
     * Get the remaining portion of a set of Sets.
     *
     * If the size of the returned list is over getMaxSets(), the last element
     * is another resumptionToken (a String) which can be used to get the rest 
     * of the list.
     */
    public List getSets(String resumptionToken)
            throws BadResumptionTokenException,
            NoSetHierarchyException, RepositoryException {
        return null;
    }

    /**
     * Get the MetadataFormats supported across the repository or for an
     * individual item in the repository.
     *
     * @param identifier The item identifier, or null, meaning "the entire repository"
     */
    public Set getMetadataFormats(String id)
            throws RepositoryException {
        return null;
    }

    /**
     * Get the maximum number of sets that are returned at a time.
     *
     * A negative value signifies no maximum.
     */
    public long getMaxSets()
            throws RepositoryException {
        return -1;
    }
            
    /**
     * Get the maximum number of records that are returned at a time.
     *
     * A negative value signifies no maximum.
     */
    public long getMaxRecords()
            throws RepositoryException {
        return -1;
    }
            
    /**
     * Get the maximum number of headers that are returned at a time.
     *
     * A negative value signifies no maximum.
     */
    public long getMaxHeaders()
            throws RepositoryException {
        return -1;
    }
    
}