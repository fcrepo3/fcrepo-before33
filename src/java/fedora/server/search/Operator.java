package fedora.server.search;

public class Operator {

    private String m_symbol;
    private String m_abbreviation;

    protected Operator(String symbol, String abbreviation) {
        m_abbreviation=abbreviation;
        m_symbol=symbol;
    }
        
    public String getSymbol() {
        return m_symbol;
    }
        
    public String getAbbreviation() {
        return m_symbol;
    }
        
}    

