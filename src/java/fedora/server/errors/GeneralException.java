package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> GeneralException.java</p>
 * <p><b>Description:</b> A general exception indicating something went wrong
 * on the server.</p>
 *
 * <p>This type of exception doesn't characterize the error by java type,
 * but may still classify it by message and code.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public final class GeneralException
        extends ServerException {

    /**
     * Creates a GeneralException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public GeneralException(String message) {
        super(null, message, null, null, null);
    }

    public GeneralException(String message, Throwable cause) {
        super(null, message, null, null, cause);
    }

    public GeneralException(String bundleName, String code, String[] values,
            String[] details, Throwable cause) {
        super(bundleName, code, values, details, cause);
    }

}