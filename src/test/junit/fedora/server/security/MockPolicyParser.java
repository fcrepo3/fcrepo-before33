package fedora.server.security;

import java.io.IOException;

import org.xml.sax.SAXException;

import fedora.server.utilities.StreamUtility;

public class MockPolicyParser
        extends PolicyParser {

    public MockPolicyParser() throws IOException, SAXException {
        super(StreamUtility.getStream(TestPolicyParser.SCHEMA_GOODENOUGH));
    }

}
