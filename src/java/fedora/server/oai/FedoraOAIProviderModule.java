package fedora.server.oai;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fedora.oai.OAIProvider;
import fedora.oai.*;
import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.search.FieldSearch;

/**
 *
 * <p><b>Title:</b> FedoraOAIProviderModule.java</p>
 * <p><b>Description:</b> An OAIProvider that acts as a server module and wraps
 * FedoraOAIProvider.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
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
        String repositoryDomainName=getParameter("repositoryDomainName");
        if (repositoryDomainName==null) {
            throw new ModuleInitializationException("repositoryDomainName must be specified.", getRole());
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
                friends.add(f);
            } else {
                String[] fs=f.split(" ");
                for (int i=0; i<fs.length; i++) {
                    friends.add(fs[i]);
                }
            }
        }
        FieldSearch fieldSearch=(FieldSearch) getServer().getModule("fedora.server.search.FieldSearch");
        if (fieldSearch==null) {
            throw new ModuleInitializationException("FieldSearch module was not loaded, but is required.", getRole());
        }
        Module fsModule=(Module) getServer().getModule("fedora.server.search.FieldSearch");

        if (fsModule.getParameter("maxResults")==null) {
            throw new ModuleInitializationException(
                "maxResults parameter must be specified in FieldSearch module's configuration.", getRole());
        }
        int maxResults=0;
        try {
            maxResults=Integer.parseInt(fsModule.getParameter("maxResults"));
            if (maxResults<1) {
                throw new NumberFormatException("");
            }
        } catch (NumberFormatException nfe) {
            throw new ModuleInitializationException(
                "maxResults specified in FieldSearch module's configuration must be a positive integer.", getRole());
        }

        long maxSets=100; // unused for now, but passed in the constructor anyway
        long maxRecords=maxResults;
        long maxHeaders=maxResults;
/* unused for now
        String maxSetsString=getParameter("maxSets");
        if (maxSetsString!=null) {
            try {
                maxSets=Long.parseLong(maxSetsString);
            } catch (NumberFormatException nfe) {
                throw new ModuleInitializationException("maxSets value is invalid.", getRole());
            }
        }
*/
        String maxRecordsString=getParameter("maxRecords");
        if (maxRecordsString!=null) {
            try {
                maxRecords=Long.parseLong(maxRecordsString);
                if (maxRecords>maxResults) {
                    logWarning("maxRecords was over the limit given by the FieldSearch module, using highest possible value: " + maxResults);
                    maxRecords=maxResults;
                }
            } catch (NumberFormatException nfe) {
                throw new ModuleInitializationException("maxRecords value is invalid.", getRole());
            }
        }
        String maxHeadersString=getParameter("maxHeaders");
        if (maxHeadersString!=null) {
            try {
                maxHeaders=Long.parseLong(maxHeadersString);
                if (maxHeaders>maxResults) {
                    logWarning("maxHeaders was over the limit given by the FieldSearch module, using highest possible value: " + maxResults);
                    maxHeaders=maxResults;
                }
            } catch (NumberFormatException nfe) {
                throw new ModuleInitializationException("maxHeaders value is invalid.", getRole());
            }
        }
        m_wrappedOAIProvider=new FedoraOAIProvider(repositoryName, 
                repositoryDomainName, host, "/fedora/oai", 
		   		adminEmails, friends, pidNamespace, maxSets, 
                maxRecords, maxHeaders, fieldSearch, this);
    }

    public String getRepositoryName() {
        return m_wrappedOAIProvider.getRepositoryName();
    }

    public String getBaseURL(String protocol, String port) {
        return m_wrappedOAIProvider.getBaseURL(protocol, port);
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