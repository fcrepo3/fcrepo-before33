package edu.cornell.dlrg.oai;

import java.util.Set;

public interface Repository { 

    /**
     * Get a human readable name for the repository.
     */
    public abstract String getRepositoryName();
    
    /**
     * Get the HTTP endpoint for a OAI-PMH listener.
     */
    public abstract String getBaseURL();
    
    /**
     * Get the version of the OAI-PMH supported by the repository.
     */
    public abstract String getProtocolVersion();

    /**
     * Get a UTCDateTime that is the guaranteed lower limit of all datestamps
     * recording changes, modifications, or deletions in the repository.
     * A repository must not use datestamps lower than this.
     */
    public abstract Date getEarliestDatestamp();
    
    /**
     * Get the manner in which the repository supports the notion of deleted
     * records.
     */
    public abstract DeletedRecordSupport getDeletedRecordSupport();
    
    /**
     * Get the finest harvesting granularity supported by the repository.
     */
    public abstract DateGranularitySupport getDateGranularitySupport();
    
    /**
     * Get the email addresses of administrators of the repository.
     * This set may contain 1...inf members.
     */
    public abstract Set getAdminEmails();
    
    /**
     * Get the compression encodings supported by the repository.
     * This set may contain 0...inf members.
     * Recommended values are those in RFC 2616 Section 14.11
     */
    public abstract Set getSupportedCompressionEncodings();
    
    /**
     * Get XML descriptions of the repository.
     * This set may contain 1...inf members. 
     * Each description
     * must contain the URL of an XML schema describing its structure.
     * See http://www.openarchives.org/OAI/2.0/guidelines.htm for guidelines
     * regarding descriptions.
     */
    public abstract Set getDescriptions();

    /**
     * Get an individual metadata record from the repository.
     */
    public abstract Set getRecord(String id, String prefix);
    public abstract Set getMetadataFormats(String id);


}