
package fedora.server.storage.translation;

/**
 * Common unit tests for METSFedoraExt deserializers.
 * 
 * @author Chris Wilper
 */
public abstract class TestMETSFedoraExtDODeserializer
        extends TestXMLDODeserializer {

    TestMETSFedoraExtDODeserializer(DODeserializer deserializer,
                                    DOSerializer associatedSerializer) {
        super(deserializer, associatedSerializer);
    }

}
