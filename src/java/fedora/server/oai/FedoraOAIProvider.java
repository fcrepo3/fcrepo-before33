package fedora.server.oai;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fedora.oai.*; //FIXME:evil
import fedora.server.Logging;
import fedora.server.StdoutLogging;
import fedora.server.errors.ServerException;
import fedora.server.search.Condition;
import fedora.server.search.FieldSearch;

public class FedoraOAIProvider
        extends StdoutLogging
        implements OAIProvider { 

    private String m_repositoryName;
    private String m_baseURL;
    private Set m_adminEmails;
    private Set m_descriptions;
    private long m_maxSets;
    private long m_maxRecords;
    private long m_maxHeaders;
    private FieldSearch m_fieldSearch;
    private Set m_formats;
    private static Set s_emptySet=new HashSet();
        
    public FedoraOAIProvider(String repositoryName, String baseURL, 
            Set adminEmails, Set friendBaseURLs, String namespaceID, 
            long maxSets, long maxRecords, long maxHeaders, 
            FieldSearch fieldSearch, Logging logTarget) {
        super(logTarget);
        m_repositoryName=repositoryName;
        m_baseURL=baseURL;
        m_adminEmails=adminEmails;
        m_maxSets=maxSets;
        m_maxRecords=maxRecords;
        m_maxHeaders=maxHeaders;
        m_fieldSearch=fieldSearch;
        m_descriptions=new HashSet();
        StringBuffer buf=new StringBuffer();
        buf.append("      <oai-identifier xmlns=\"http://www.openarchives.org/OAI/2.0/oai-identifier\"\n");
        buf.append("          xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        buf.append("          xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai-identifier");
        buf.append("          http://www.openarchives.org/OAI/2.0/oai-identifier.xsd\">\n");
        buf.append("        <scheme>oai</scheme>\n");
        buf.append("        <repositoryIdentifier>" + namespaceID + "</repositoryIdentifier>\n");
        buf.append("        <delimiter>:</delimiter>\n");
        buf.append("        <sampleIdentifier>oai:" + namespaceID + ":7654</sampleIdentifier>\n");
        buf.append("      </oai-identifier>");
        m_descriptions.add(buf.toString());
        if (friendBaseURLs!=null && friendBaseURLs.size()>0) {
            buf=new StringBuffer(); 
            buf.append("      <friends xmlns=\"http://www.openarchives.org/OAI/2.0/friends/\"\n");
            buf.append("          xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            buf.append("          xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/friends/\n");
            buf.append("          http://www.openarchives.org/OAI/2.0/friends.xsd\">\n");
            Iterator iter=friendBaseURLs.iterator();
            while (iter.hasNext()) {
                buf.append("        <baseURL>" + (String) iter.next() + "</baseURL>\n");
            }
            buf.append("      </friends>");
            m_descriptions.add(buf.toString());
        }
        m_formats=new HashSet();
        m_formats.add(new SimpleMetadataFormat("oai_dc", 
                "http://www.openarchives.org/OAI/2.0/oai_dc.xsd", 
                "http://www.openarchives.org/OAI/2.0/oai_dc/"));
    }

    /**
     * Get a human readable name for the repository.
     */
    public String getRepositoryName() {
        return m_repositoryName;
    }
    
    /**
     * Get the HTTP endpoint for the OAI-PMH interface.
     */
    public String getBaseURL() {
        return m_baseURL;
    }
    
    /**
     * Get the version of the OAI-PMH supported by the repository.
     */
    public String getProtocolVersion() {
        return "2.0";
    }

    /**
     * Get a Date (in UTC) that is the guaranteed lower limit of all datestamps
     * recording changes, modifications, or deletions in the repository.
     * A repository must not use datestamps lower than this.
     */
    public Date getEarliestDatestamp() {
        return new Date();
    }
    
    /**
     * Get the manner in which the repository supports the notion of deleted
     * records.
     */
    public DeletedRecordSupport getDeletedRecordSupport() {
        return DeletedRecordSupport.NO;
    }
    
    /**
     * Get the finest harvesting granularity supported by the repository.
     */
    public DateGranularitySupport getDateGranularitySupport() {
        return DateGranularitySupport.SECONDS;
    }
    
    /**
     * Get the email addresses of administrators of the repository.
     *
     * This set must contain at least one item.
     */
    public Set getAdminEmails() {
        return m_adminEmails;
    }
    
    /**
     * Get the compression encodings supported by the repository.
     *
     * This set may be empty. Recommended values are those in RFC 2616 Section 
     * 14.11
     */
    public Set getSupportedCompressionEncodings() {
        return s_emptySet;
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
    public Set getDescriptions() {
        return m_descriptions;
    }

    /**
     * Get an individual metadata record from the repository.
     */
    public Record getRecord(String identifier, String metadataPrefix)
            throws CannotDisseminateFormatException, IDDoesNotExistException, 
            RepositoryException {
        if (!metadataPrefix.equals("oai_dc")) {
            throw new CannotDisseminateFormatException("Repository does not provide that format in OAI-PMH responses.");
        }
        throw new RepositoryException("getRecord not impld");
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
        if (!metadataPrefix.equals("oai_dc")) {
            throw new CannotDisseminateFormatException("Repository does not provide that format in OAI-PMH responses.");
        }
        throw new RepositoryException("getRecords not impld");
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
        if (1==2) {
            return null;
        }
        throw new RepositoryException("getRecords not impld");
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
        if (!metadataPrefix.equals("oai_dc")) {
            throw new CannotDisseminateFormatException("Repository does not provide that format in OAI-PMH responses.");
        }
        throw new RepositoryException("getHeaders not impld");
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
        throw new RepositoryException("getHeaders not impld");
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
        if (1==2) {
            return null;
        }
        throw new RepositoryException("getSets not impld");
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
        if (1==2) {
            return null;
        }
        throw new RepositoryException("getSets not impld");
    }

    /**
     * Get the MetadataFormats supported across the repository or for an
     * individual item in the repository.
     *
     * @param identifier The item identifier, or null, meaning "the entire repository"
     */
    public Set getMetadataFormats(String id)
            throws NoMetadataFormatsException, IDDoesNotExistException, 
            RepositoryException {
        if (id==null) {
            return m_formats;
        }
        if (!id.startsWith("oai:")) {
            throw new IDDoesNotExistException("For this repository, all identifiers in OAI requests should begin with oai:");
        }
        if (id.indexOf("'")!=-1) {
            throw new IDDoesNotExistException("For this repository, no identifiers contain the apostrophe character.");
        }
        String pid=id.substring(4);
        List l=null;
        try {
            l=m_fieldSearch.search(new String[] {"pid"}, Condition.getConditions("pid='" + pid + "' dcmDate>'1970-01-01'"));
        } catch (ServerException se) {
            throw new RepositoryException(se.getClass().getName() + ": " + se.getMessage());
        }
        if (l.size()>0) {
            return m_formats;
        }
        try {
            l=m_fieldSearch.search(new String[] {"pid"}, Condition.getConditions("pid='" + pid + "'"));
        } catch (ServerException se) {
            throw new RepositoryException(se.getClass().getName() + ": " + se.getMessage());
        }
        if (l.size()>0) {
            throw new NoMetadataFormatsException("The item doesn't even have dc_oai metadata.");
        } else {
            throw new IDDoesNotExistException("The provided id does not match any item in the repository.");
        }
    }

    /**
     * Get the maximum number of sets that are returned at a time.
     *
     * A negative value signifies no maximum.
     */
    public long getMaxSets()
            throws RepositoryException {
        return m_maxSets;
    }
            
    /**
     * Get the maximum number of records that are returned at a time.
     *
     * A negative value signifies no maximum.
     */
    public long getMaxRecords()
            throws RepositoryException {
        return m_maxRecords;
    }
            
    /**
     * Get the maximum number of headers that are returned at a time.
     *
     * A negative value signifies no maximum.
     */
    public long getMaxHeaders()
            throws RepositoryException {
        return m_maxHeaders;
    }
    
}