package fedora.oai;

/**
 * Signals that the value of the identifier argument is unknown or illegal in
 * this repository.
 *
 * This may occur while fulfilling a GetRecord or ListMetadataFormats request.
 */

/**
 *
 * <p><b>Title:</b> </p>
 * <p><b>Description:</b> Signals that the value of the identifier argument is
 * unknown or illegal in this repository.</p>
 *
 * <p> may occur while fulfilling a GetRecord or ListMetadataFormats request.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version 1.0
 */
public class IDDoesNotExistException
        extends OAIException {

    public IDDoesNotExistException() {
        super("idDoesNotExist", null);
    }

    public IDDoesNotExistException(String message) {
        super("idDoesNotExist", message);
    }

}