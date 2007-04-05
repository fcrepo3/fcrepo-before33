/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.oai;

/**
 *
 * <p><b>Title:</b> OAIException.java</p>
 * <p><b>Description:</b> An exception occuring as a result of an OAI-PMH
 * request.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
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