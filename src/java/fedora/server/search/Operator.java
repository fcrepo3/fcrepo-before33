/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.search;

/**
 * @author Chris Wilper
 */
public class Operator {

    private final String m_symbol;

    protected Operator(String symbol, String abbreviation) {
        m_symbol = symbol;
    }

    public String getSymbol() {
        return m_symbol;
    }

    public String getAbbreviation() {
        return m_symbol;
    }

}
