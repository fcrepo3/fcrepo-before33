package fedora.server.search;

import java.util.List;

public class FieldSearchQuery {

    public final static int CONDITIONS_TYPE=1;
    public final static int TERMS_TYPE=2;

    private List m_conditions;
    private String m_terms;
    private int m_type;

    public FieldSearchQuery(List conditions) {
        m_conditions=conditions;
        m_type=CONDITIONS_TYPE;
    }
    
    public FieldSearchQuery(String terms) {
        m_terms=terms;
        m_type=TERMS_TYPE;
    }
    
    public int getType() {
        return m_type;
    }
    
    public List getConditions() {
        return m_conditions;
    }
    
    public String getTerms() {
        return m_terms;
    }
    
}    

