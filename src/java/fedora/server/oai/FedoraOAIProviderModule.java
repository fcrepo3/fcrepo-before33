package fedora.server.oai;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fedora.oai.OAIProvider;
import fedora.oai.*;
import fedora.server.Logging;
import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;
import fedora.server.errors.ServerException;
import fedora.server.search.FieldSearch;

/**
 * An OAIProvider that acts as a server module and wraps FedoraOAIProvider.
 */
public class FedoraOAIProviderModule
        extends Module
        implements OAIProvider {
        
    private FedoraOAIProvider m_wrappedOAIProvider;

    public FedoraOAIProviderModule(Map params, Server server, String role) 
            throws ModuleInitializationException {
        super(params, server, role);
    }
    
    public void postInitModule() 
            throws ModuleInitializationException {
        String repositoryName=getParameter("repositoryName");
        if (repositoryName==null) {
            throw new ModuleInitializationException("repositoryName must be specified.", getRole());
        }
        String host=getServer().getParameter("fedoraServerHost");
        if (host==null) {
            throw new ModuleInitializationException("fedoraServerHost must be specified as primary server config element.", getRole());
        }
        String port=getServer().getParameter("fedoraServerPort");
        if (port==null) {
            throw new ModuleInitializationException("fedoraServerPort must be specified as primary server config element.", getRole());
        }
        Module mgr=(Module) getServer().getModule("fedora.server.storage.DOManager");
        if (mgr==null) {
            throw new ModuleInitializationException("DOManager is required (for pidNamespace param), but isn't loaded.", getRole());
        }
        String pidNamespace=mgr.getParameter("pidNamespace");
        if (pidNamespace==null) {
            throw new ModuleInitializationException("DOManager did not specify a pidNamespace, but this module requires that it does.", getRole());
        }
        String aes=getParameter("adminEmails");
        if (aes==null) {
            throw new ModuleInitializationException("adminEmails must be specified.", getRole());
        }
        HashSet adminEmails=new HashSet();
        if (aes.indexOf(" ")==-1) {
            adminEmails.add(aes);
        } else {
            String[] emails=aes.split(" ");
            for (int i=0; i<emails.length; i++) {
                adminEmails.add(emails[i]);
            }
        }
        HashSet friends=new HashSet();
        if (getParameter("friends")!=null) {
            String f=getParameter("friends");
            if (f.indexOf(" ")==-1) {
                adminEmails.add(f);
            } else {
                String[] fs=f.split(" ");
                for (int i=0; i<fs.length; i++) {
                    friends.add(fs[i]);
                }
            }
        }
        long maxSets=100;
        long maxRecords=50;
        long maxHeaders=150;
        String maxSetsString=getParameter("maxSets");
        if (maxSetsString!=null) {
            try {
                maxSets=Long.parseLong(maxSetsString);
            } catch (NumberFormatException nfe) {
                throw new ModuleInitializationException("maxSets value is invalid.", getRole());
            }
        }
        String maxRecordsString=getParameter("maxRecords");
        if (maxRecordsString!=null) {
            try {
                maxRecords=Long.parseLong(maxRecordsString);
            } catch (NumberFormatException nfe) {
                throw new ModuleInitializationException("maxRecords value is invalid.", getRole());
            }
        }
        String maxHeadersString=getParameter("maxHeaders");
        if (maxHeadersString!=null) {
            try {
                maxHeaders=Long.parseLong(maxHeadersString);
            } catch (NumberFormatException nfe) {
                throw new ModuleInitializationException("maxHeaders value is invalid.", getRole());
            }
        }
        FieldSearch fieldSearch=(FieldSearch) getServer().getModule("fedora.server.search.FieldSearch");
        if (fieldSearch==null) {
            throw new ModuleInitializationException("FieldSearch module was not loaded, but is required.", getRole());
        }
        m_wrappedOAIProvider=new FedoraOAIProvider(repositoryName, 
                "http://" + host + ":" + port + "/fedora/oai", adminEmails, 
                friends, pidNamespace, maxSets, maxRecords, maxHeaders, 
                fieldSearch, this);
    }
    
    public String getRepositoryName() {
        return m_wrappedOAIProvider.getRepositoryName();
    }
    
    public String getBaseURL() {
        return m_wrappedOAIProvider.getBaseURL();
    }
    
    public String getProtocolVersion() {
        return m_wrappedOAIProvider.getProtocolVersion();
    }

    public Date getEarliestDatestamp() {
        return m_wrappedOAIProvider.getEarliestDatestamp();
    }
    
    public DeletedRecordSupport getDeletedRecordSupport() {
        return m_wrappedOAIProvider.getDeletedRecordSupport();
    }
    
    public DateGranularitySupport getDateGranularitySupport() {
        return m_wrappedOAIProvider.getDateGranularitySupport();
    }
    
    public Set getAdminEmails() {
        return m_wrappedOAIProvider.getAdminEmails();
    }
    
    public Set getSupportedCompressionEncodings() {
        return m_wrappedOAIProvider.getSupportedCompressionEncodings();
    }
    
    public Set getDescriptions() {
        return m_wrappedOAIProvider.getDescriptions();
    }

    public Record getRecord(String identifier, String metadataPrefix)
            throws CannotDisseminateFormatException, IDDoesNotExistException, 
            RepositoryException {
        return m_wrappedOAIProvider.getRecord(identifier, metadataPrefix);
    }

    public List getRecords(Date from, Date until, String metadataPrefix,
            String set)
            throws CannotDisseminateFormatException,
            NoRecordsMatchException, NoSetHierarchyException,
            RepositoryException {
        return m_wrappedOAIProvider.getRecords(from, until, metadataPrefix, set);
    }

    public List getRecords(String resumptionToken)
            throws CannotDisseminateFormatException,
            NoRecordsMatchException, NoSetHierarchyException, 
            BadResumptionTokenException, RepositoryException {
        return m_wrappedOAIProvider.getRecords(resumptionToken);
    }

    public List getHeaders(Date from, Date until, String metadataPrefix,
            String set)
            throws CannotDisseminateFormatException, NoRecordsMatchException, 
            NoSetHierarchyException, RepositoryException {
        return m_wrappedOAIProvider.getHeaders(from, until, metadataPrefix, set);
    }

    public List getHeaders(String resumptionToken)
            throws CannotDisseminateFormatException,
            NoRecordsMatchException, NoSetHierarchyException, 
            BadResumptionTokenException, RepositoryException {
        return m_wrappedOAIProvider.getHeaders(resumptionToken);
    }
            
    public List getSets()
            throws NoSetHierarchyException, RepositoryException {
        return m_wrappedOAIProvider.getSets();
    }

    public List getSets(String resumptionToken)
            throws BadResumptionTokenException,
            NoSetHierarchyException, RepositoryException {
        return m_wrappedOAIProvider.getSets(resumptionToken);
    }

    public Set getMetadataFormats(String id)
            throws NoMetadataFormatsException, IDDoesNotExistException, 
            RepositoryException {
        return m_wrappedOAIProvider.getMetadataFormats(id);
    }

    public long getMaxSets()
            throws RepositoryException {
        return m_wrappedOAIProvider.getMaxSets();
    }
            
    public long getMaxRecords()
            throws RepositoryException {
        return m_wrappedOAIProvider.getMaxRecords();
    }
            
    public long getMaxHeaders()
            throws RepositoryException {
        return m_wrappedOAIProvider.getMaxHeaders();
    }
    
}