package edu.cornell.dlrg.oai;

import java.util.Set;

/**
 * Describes a set in the repository.
 * 
 * @see http://www.openarchives.org/OAI/openarchivesprotocol.html#ListSets
 */
public interface SetInfo {

    /**
     * Get the name of the set.
     */
    public abstract String getName();
    
    /**
     * Get the setSpec of the set.
     */
    public abstract String getSpec();
    
    /**
     * Get the descriptions of the set.
     */
    public abstract Set getDescriptions();
    
}