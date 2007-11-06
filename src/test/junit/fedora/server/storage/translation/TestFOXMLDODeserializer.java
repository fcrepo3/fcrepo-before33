package fedora.server.storage.translation;

/**
 * Common unit tests for FOXML deserializers.
 *
 * @author Chris Wilper
 */
public abstract class TestFOXMLDODeserializer
        extends TestXMLDODeserializer {
    
    TestFOXMLDODeserializer(DODeserializer deserializer,
            DOSerializer associatedSerializer) {
        super(deserializer, associatedSerializer);
    }
    
}
