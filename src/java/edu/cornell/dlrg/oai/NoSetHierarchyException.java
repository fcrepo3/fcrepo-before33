package edu.cornell.dlrg.oai;

/**
 * Signals that the repository does not support sets.
 *
 * This may occur while fulfilling a ListSets, ListIdentifiers, or ListRecords
 * request.
 */
public class NoSetHierarchyException 
        extends OAIException {
        
    public NoSetHierarchyException() {
        super("noSetHierarchy", null);
    }

    public NoSetHierarchyException(String message) {
        super("noSetHierarchy", message);
    }
    
}