package fedora.server.security;

import fedora.server.errors.BackendSecurityParserException;
import fedora.server.security.BackendSecuritySpec;

/**
*
* <p><b>Title:</b> BackendSecurity.java</p>
* <p><b>Description:</b> An interface for accessing backend service security info.</p>
*
* @author rlw@virginia.edu
* @version $Id$
*/
public interface BackendSecurity {
    
    /**
     * <p>Gets the current instance of BackendSecuritySpec.</p>
     * 
     * @return Current instance of backendSecuritySpec.
     */
    public BackendSecuritySpec getBackendSecuritySpec();
    
    /**
     * <p>Sets the current instance of BackendSecuritySpec by parsing the
     * beSecurity configuration file.</p>
     * 
     * @throws BackendSecurityParserException If an error occurs in parsing the
     *         beSecurity configuration file.
     */
    public void setBackendSecuritySpec() throws BackendSecurityParserException;
    
    /**
     * <p>Parses the beSecurity configuration file and stores the results in an
     * instance of BackendSecuritySpec.</p>
     * 
     * @return An instance of BackendSecuritySpec.
     * @throws BackendSecurityParserException If an error occursin parsing the
     *         beSecurity configuration file.
     */
    public BackendSecuritySpec parseBeSecurity() throws BackendSecurityParserException;
    
    /**
     * <p>Reloads the backend service security info by reparsing the beSecurity
     * configuration file and storing results in an instance of BackendSecuritySpec.</p>
     * 
     * @return An instance of BackendSecuritySpec.
     * @throws BackendSecurityParserException If an error occurs in trying to reparse the
     *         beSecurity configuration file.
     */
    public BackendSecuritySpec reloadBeSecurity() throws BackendSecurityParserException;

}
