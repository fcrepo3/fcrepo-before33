/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.translation;

import static fedora.common.Constants.METS_EXT1_1;

/**
 * Deserializes objects in METS_EXT 1.1 format.
 * 
 * @author cwilper@cs.cornell.edu
 */
public class METSFedoraExt1_1DODeserializer
        extends METSFedoraExtDODeserializer {

    /**
     * Constructs an instance.
     */
    public METSFedoraExt1_1DODeserializer() {
        super(METS_EXT1_1);
    }
}