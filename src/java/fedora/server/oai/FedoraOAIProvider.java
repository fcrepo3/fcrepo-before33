package fedora.server.oai;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fedora.oai.*; //FIXME:evil
import fedora.server.Logging;
import fedora.server.StdoutLogging;
import fedora.server.errors.ServerException;
import fedora.server.search.DCFields;
import fedora.server.search.ObjectFields;
import fedora.server.search.Condition;
import fedora.server.search.FieldSearch;
import fedora.server.search.FieldSearchQuery;

public class FedoraOAIProvider
        extends StdoutLogging
        implements OAIProvider { 

    private String m_repositoryName;
    private String m_baseURL;
    private Set m_adminEmails;
    private Set m_descriptions;
    private List m_setInfos;
    private long m_maxSets;
    private long m_maxRecords;
    private long m_maxHeaders;
    private FieldSearch m_fieldSearch;
    private Set m_formats;
    private static Set s_emptySet=new HashSet();
    private static String[] s_headerFields=new String[] {"pid", "dcmDate", 
            "fType"};
    private static String[] s_headerAndDCFields=new String[] {"pid", "dcmDate", 
            "fType", "title", "creator", "subject", "description", "publisher",
            "contributor", "date", "type", "format", "identifier", "source",
            "language", "relation", "coverage", "rights"};

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
        buf.append("        <repositoryIdentifier>fedora.info</repositoryIdentifier>\n");
        buf.append("        <delimiter>:</delimiter>\n");
        buf.append("        <sampleIdentifier>oai:fedora.info:" + namespaceID + ":7654</sampleIdentifier>\n");
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
        m_setInfos=new ArrayList();
        m_setInfos.add(new SimpleSetInfo("Regular Digital Objects", "objects", s_emptySet));
        m_setInfos.add(new SimpleSetInfo("Behavior Mechanism Objects", "bmechs", s_emptySet));
        m_setInfos.add(new SimpleSetInfo("Behavior Definition Objects", "bdefs", s_emptySet));
    }

    public String getRepositoryName() {
        return m_repositoryName;
    }
    
    public String getBaseURL() {
        return m_baseURL;
    }
    
    public String getProtocolVersion() {
        return "2.0";
    }

    public Date getEarliestDatestamp() {
        return new Date();
    }
    
    public DeletedRecordSupport getDeletedRecordSupport() {
        return DeletedRecordSupport.NO;
    }
    
    public DateGranularitySupport getDateGranularitySupport() {
        return DateGranularitySupport.SECONDS;
    }
    
    public Set getAdminEmails() {
        return m_adminEmails;
    }
    
    public Set getSupportedCompressionEncodings() {
        return s_emptySet;
    }
    
    public Set getDescriptions() {
        return m_descriptions;
    }

    public Record getRecord(String identifier, String metadataPrefix)
            throws CannotDisseminateFormatException, IDDoesNotExistException, 
            RepositoryException {
        if (!metadataPrefix.equals("oai_dc")) {
            throw new CannotDisseminateFormatException("Repository does not provide that format in OAI-PMH responses.");
        }
        String pid=getPID(identifier);
        List l=null;
        try {
            //FIXME: use maxResults from... config instead of hardcoding 100?
            l=m_fieldSearch.listObjectFields(s_headerAndDCFields, 100,
                    new FieldSearchQuery(Condition.getConditions("pid='" + pid 
                    + "' dcmDate>'2000-01-01'"))).objectFieldsList();
        } catch (ServerException se) {
            throw new RepositoryException(se.getClass().getName() + ": " + se.getMessage());
        }
        if (l.size()>0) {
            ObjectFields f=(ObjectFields) l.get(0);
            return new SimpleRecord(getHeader(f), getDCXML(f), s_emptySet);
        } else {
            // see if it exists
            try {
                l=m_fieldSearch.listObjectFields(new String[] {"pid"}, 1,
                        new FieldSearchQuery(Condition.getConditions("pid='" 
                        + pid + "'"))).objectFieldsList();
            } catch (ServerException se) {
                throw new RepositoryException(se.getClass().getName() + ": " + se.getMessage());
            }
            if (l.size()==0) {
                throw new IDDoesNotExistException("The provided id does not match any item in the repository.");
            } else {
                throw new CannotDisseminateFormatException("The item doesn't even have dc_oai metadata.");
            }
        }
    }
    
    public List getRecords(Date from, Date until, String metadataPrefix,
            String set)
            throws CannotDisseminateFormatException,
            NoRecordsMatchException, NoSetHierarchyException,
            RepositoryException {
        if (!metadataPrefix.equals("oai_dc")) {
            throw new CannotDisseminateFormatException("Repository does not provide that format in OAI-PMH responses.");
        }
        
        
        List l=null;
        try {
            //FIXME: use maxResults from... config instead of hardcoding 100?
            l=m_fieldSearch.listObjectFields(s_headerAndDCFields, 100,
                    new FieldSearchQuery(Condition.getConditions(
                    "dcmDate>'2000-01-01'" + getDatePart(from, until) 
                    + getFTypePart(set)))).objectFieldsList();
        } catch (ServerException se) {
            throw new RepositoryException(se.getClass().getName() + ": " + se.getMessage());
        }
        if (l.size()==0) {
            throw new NoRecordsMatchException("No records match the given criteria.");
        }
        ArrayList ret=new ArrayList();
        for (int i=0; i<l.size(); i++) {
            ObjectFields f=(ObjectFields) l.get(i);
            ret.add(new SimpleRecord(getHeader(f), getDCXML(f), s_emptySet));
        }
        return ret;
    }
    
    private Header getHeader(ObjectFields f) {
        String identifier="oai:fedora.info:" + f.getPid();
        Date datestamp=f.getDCMDate();
        HashSet setSpecs=new HashSet();
        String fType=f.getFType();
        if (fType.equals("D")) {
            setSpecs.add("bdefs");
        } else if (fType.equals("M")) {
            setSpecs.add("bmechs");
        } else {
            setSpecs.add("objects");
        }
        return new SimpleHeader(identifier, datestamp, setSpecs, true);
    }
    
    private String getDCXML(DCFields dc) {
        StringBuffer out=new StringBuffer();
        out.append("        <oai_dc:dc\n");
        out.append("            xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\"\n");
        out.append("            xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n"); 
        out.append("            xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"); 
        out.append("            xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/\n");
        out.append("            http://www.openarchives.org/OAI/2.0/oai_dc.xsd\">\n");
        for (int i=0; i<dc.titles().size(); i++) {
            out.append("          <dc:title>");
            out.append((String) dc.titles().get(i));
            out.append("          </dc:title>\n");
        }
        for (int i=0; i<dc.creators().size(); i++) {
            out.append("          <dc:creator>");
            out.append((String) dc.creators().get(i));
            out.append("          </dc:creator>\n");
        }
        for (int i=0; i<dc.subjects().size(); i++) {
            out.append("          <dc:subject>");
            out.append((String) dc.subjects().get(i));
            out.append("          </dc:subject>\n");
        }
        for (int i=0; i<dc.descriptions().size(); i++) {
            out.append("          <dc:description>");
            out.append((String) dc.descriptions().get(i));
            out.append("          </dc:description>\n");
        }
        for (int i=0; i<dc.publishers().size(); i++) {
            out.append("          <dc:publisher>");
            out.append((String) dc.publishers().get(i));
            out.append("          </dc:publisher>\n");
        }
        for (int i=0; i<dc.contributors().size(); i++) {
            out.append("          <dc:contributor>");
            out.append((String) dc.contributors().get(i));
            out.append("          </dc:contributor>\n");
        }
        for (int i=0; i<dc.dates().size(); i++) {
            String dateString=(String) dc.dates().get(i);
            out.append("          <dc:date>");
            out.append(dateString);
            out.append("          </dc:date>\n");
        }
        for (int i=0; i<dc.types().size(); i++) {
            out.append("          <dc:type>");
            out.append((String) dc.types().get(i));
            out.append("          </dc:type>\n");
        }
        for (int i=0; i<dc.formats().size(); i++) {
            out.append("          <dc:format>");
            out.append((String) dc.formats().get(i));
            out.append("          </dc:format>\n");
        }
        for (int i=0; i<dc.identifiers().size(); i++) {
            out.append("          <dc:identifier>");
            out.append((String) dc.identifiers().get(i));
            out.append("          </dc:identifier>\n");
        }
        for (int i=0; i<dc.sources().size(); i++) {
            out.append("          <dc:source>");
            out.append((String) dc.sources().get(i));
            out.append("          </dc:source>\n");
        }
        for (int i=0; i<dc.languages().size(); i++) {
            out.append("          <dc:language>");
            out.append((String) dc.languages().get(i));
            out.append("          </dc:language>\n");
        }
        for (int i=0; i<dc.relations().size(); i++) {
            out.append("          <dc:relation>");
            out.append((String) dc.relations().get(i));
            out.append("          </dc:relation>\n");
        }
        for (int i=0; i<dc.coverages().size(); i++) {
            out.append("          <dc:coverage>");
            out.append((String) dc.coverages().get(i));
            out.append("          </dc:coverage>\n");
        }
        for (int i=0; i<dc.rights().size(); i++) {
            out.append("          <dc:rights>");
            out.append((String) dc.rights().get(i));
            out.append("          </dc:rights>\n");
        }
        out.append("        </oai_dc:dc>");
        return out.toString();
    }

    public List getRecords(String resumptionToken)
            throws CannotDisseminateFormatException,
            NoRecordsMatchException, NoSetHierarchyException, 
            BadResumptionTokenException, RepositoryException {
        throw new BadResumptionTokenException("Not a known resumptionToken.");
    }
    
    private String getFTypePart(String set) 
            throws NoRecordsMatchException {
        if (set==null) {
            return "";
        }
        if (set.equals("objects")) {
            return " fType=O";
        } else if (set.equals("bdefs")) {
            return " fType=D";
        } else if (set.equals("bmechs")) {
            return " fType=M";
        } else {
            throw new NoRecordsMatchException("No such set: " + set);
        }
    }
    
    private String getDatePart(Date from, Date until) {
        if (from==null && until==null) {
            return "";
        }
        StringBuffer out=new StringBuffer();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        if (from!=null) {
            out.append(" dcmDate>='");
            out.append(formatter.format(from));
            out.append("'");
        }
        if (until!=null) {
            out.append(" dcmDate<='");
            out.append(formatter.format(until));
            out.append("'");
        }
        return out.toString();
    }
    

    public List getHeaders(Date from, Date until, String metadataPrefix,
            String set)
            throws CannotDisseminateFormatException, NoRecordsMatchException, 
            NoSetHierarchyException, RepositoryException {
        if (!metadataPrefix.equals("oai_dc")) {
            throw new CannotDisseminateFormatException("Repository does not provide that format in OAI-PMH responses.");
        }
        List l=null;
        try {
            //FIXME: use maxResults from... config instead of hardcoding 100?
            l=m_fieldSearch.listObjectFields(s_headerFields, 100,
                    new FieldSearchQuery(Condition.getConditions(
                    "dcmDate>'2000-01-01'" + getDatePart(from, until) 
                    + getFTypePart(set)))).objectFieldsList();
        } catch (ServerException se) {
            throw new RepositoryException(se.getClass().getName() + ": " + se.getMessage());
        }
        if (l.size()==0) {
            throw new NoRecordsMatchException("No records match the given criteria.");
        }
        ArrayList ret=new ArrayList();
        for (int i=0; i<l.size(); i++) {
            ObjectFields f=(ObjectFields) l.get(i);
            String identifier="oai:fedora.info:" + f.getPid();
            Date datestamp=f.getDCMDate();
            HashSet setSpecs=new HashSet();
            String fType=f.getFType();
            if (fType.equals("D")) {
                setSpecs.add("bdefs");
            } else if (fType.equals("M")) {
                setSpecs.add("bmechs");
            } else {
                setSpecs.add("objects");
            }
            ret.add(new SimpleHeader(identifier, datestamp, setSpecs, true));
        }
        return ret;
    }

    public List getHeaders(String resumptionToken)
            throws CannotDisseminateFormatException,
            NoRecordsMatchException, NoSetHierarchyException, 
            BadResumptionTokenException, RepositoryException {
        throw new BadResumptionTokenException("Not a known resumptionToken.");
    }
            
    public List getSets()
            throws NoSetHierarchyException, RepositoryException {
        return m_setInfos;
    }

    public List getSets(String resumptionToken)
            throws BadResumptionTokenException,
            NoSetHierarchyException, RepositoryException {
        throw new BadResumptionTokenException("Not a known resumptionToken.");
    }
    
    private String getPID(String id) 
            throws IDDoesNotExistException {
        if (!id.startsWith("oai:fedora.info:")) {
            throw new IDDoesNotExistException("For this repository, all identifiers in OAI requests should begin with oai:fedora.info:");
        }
        if (id.indexOf("'")!=-1) {
            throw new IDDoesNotExistException("For this repository, no identifiers contain the apostrophe character.");
        }
        return id.substring(16);
    }

    public Set getMetadataFormats(String id)
            throws NoMetadataFormatsException, IDDoesNotExistException, 
            RepositoryException {
        if (id==null) {
            return m_formats;
        }
        String pid=getPID(id);
        List l=null;
        try {
            l=m_fieldSearch.listObjectFields(new String[] {"pid"}, 1,
                    new FieldSearchQuery(Condition.getConditions("pid='" 
                    + pid + "' dcmDate>'2000-01-01'"))).objectFieldsList();
        } catch (ServerException se) {
            throw new RepositoryException(se.getClass().getName() + ": " + se.getMessage());
        }
        if (l.size()>0) {
            return m_formats;
        }
        try {
            l=m_fieldSearch.listObjectFields(new String[] {"pid"}, 1,
                    new FieldSearchQuery(Condition.getConditions("pid='" 
                    + pid + "'"))).objectFieldsList();
        } catch (ServerException se) {
            throw new RepositoryException(se.getClass().getName() + ": " + se.getMessage());
        }
        if (l.size()>0) {
            throw new NoMetadataFormatsException("The item doesn't even have dc_oai metadata.");
        } else {
            throw new IDDoesNotExistException("The provided id does not match any item in the repository.");
        }
    }

    public long getMaxSets()
            throws RepositoryException {
        return -1;
        //return m_maxSets;
    }
            
    public long getMaxRecords()
            throws RepositoryException {
        return -1;
        //return m_maxRecords;
    }
            
    public long getMaxHeaders()
            throws RepositoryException {
        return -1;
        //return m_maxHeaders;
    }
    
}