/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.search;


/**
 *
 * <p><b>Title:</b> Operator.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
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

