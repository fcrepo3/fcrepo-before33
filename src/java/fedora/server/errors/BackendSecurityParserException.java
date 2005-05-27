package fedora.server.errors;

/**
*
* <p><b>Title:</b> BackendSecurityParserException.java</p>
* <p><b>Description:</b> A parser exception indicating something went wrong
* while parsing the backend security configuration file.</p>
*
* @author rlw@virginia.edu
* @version $Id$
*/
public final class BackendSecurityParserException
       extends ServerException {

       /**
        * Creates a BackendSecurityParserException.
        *
        * @param message An informative message explaining what happened and
        *                (possibly) how to fix it.
        */
       public BackendSecurityParserException(String message) {
           super(null, message, null, null, null);
       }

       public BackendSecurityParserException(String message, Throwable cause) {
           super(null, message, null, null, cause);
       }

       public BackendSecurityParserException(String bundleName, String code, String[] values,
               String[] details, Throwable cause) {
           super(bundleName, code, values, details, cause);
       }
}
