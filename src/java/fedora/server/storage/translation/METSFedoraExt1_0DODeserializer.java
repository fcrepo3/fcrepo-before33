/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.translation;

/**
 * Deserializes objects in METS_EXT 1.0 format.
 * 
 * @author Chris Wilper
 */
public class METSFedoraExt1_0DODeserializer
        extends METSFedoraExtDODeserializer {

    /**
     * Constructs an instance.
     */
    public METSFedoraExt1_0DODeserializer() {
        super(METS_EXT1_0);
    }
    
    //@Override
    /* FIXME: No longer relevant.  OK to remove completely? */
    //int parseObjectType(String objType) {
    //    if (objType.indexOf("FedoraBDefObject") != -1) {
    //        return DigitalObject.FEDORA_SERVICE_DEFINITION_OBJECT;
    //    }
    //    if (objType.equalsIgnoreCase("FedoraBMechObject")) {
    //        return DigitalObject.FEDORA_SERVICE_DEPLOYMENT_OBJECT;
    //    }
    //    if (objType.equalsIgnoreCase("FedoraCModelObject")) {
    //        return DigitalObject.FEDORA_CONTENT_MODEL_OBJECT;
    //    }
    //    if (objType.equalsIgnoreCase("FedoraObject")) {
    //        return DigitalObject.FEDORA_OBJECT;
    //   }

    //    return -1;
    //}
}