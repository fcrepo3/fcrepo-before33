package edu.cornell.dlrg.oai;

/**
 * An exception occuring as a result of an OAI-PMH request.
 */
public abstract class OAIException 
        extends Exception {

    private String m_code;

    protected OAIException(String code, String message) {
        super(message);
        m_code=code;
    }
    
    public String getCode() {
        return m_code;
    }
    
}