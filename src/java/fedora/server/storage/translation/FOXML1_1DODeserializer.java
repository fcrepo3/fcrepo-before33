/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.translation;

import static fedora.common.Constants.FOXML1_1;

/**
 * Deserializes objects in FOXML 1.1 format.
 * 
 * @author cwilper@cs.cornell.edu
 */
public class FOXML1_1DODeserializer
        extends FOXMLDODeserializer {

    /**
     * Constructs an instance.
     */
    public FOXML1_1DODeserializer() {
        super(FOXML1_1);
    }
}