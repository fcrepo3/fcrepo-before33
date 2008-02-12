
package fedora.server.storage;

import java.io.InputStream;

import java.util.Date;

import fedora.server.errors.ServerException;
import fedora.server.storage.types.BMechDSBindSpec;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodDefOperationBind;
import fedora.server.storage.types.MethodParmDef;

/**
 * A partial implementation of {@link BMechReader} for use in unit tests. Add
 * more mocking to this class as needed, or override methods in sub-classes.
 * 
 * @author Jim Blake
 */
public class MockBMechReader
        extends MockDOReader
        implements BMechReader {

    // ----------------------------------------------------------------------
    // Mocking infrastructure
    // ----------------------------------------------------------------------

    public MockBMechReader(DigitalObject theObject) {
        super(theObject);
    }

    // ----------------------------------------------------------------------
    // Mocked methods
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Un-implemented methods
    // ----------------------------------------------------------------------

    public BMechDSBindSpec getServiceDSInputSpec(Date versDateTime)
            throws ServerException {
        throw new RuntimeException("MockBmechReader.getServiceDSInputSpec not implemented");
    }

    public MethodDefOperationBind[] getServiceMethodBindings(Date versDateTime)
            throws ServerException {
        throw new RuntimeException("MockBmechReader.getServiceMethodBindings not implemented");
    }

    public MethodParmDef[] getServiceMethodParms(String methodName,
                                                 Date versDateTime)
            throws ServerException {
        throw new RuntimeException("MockBmechReader.getServiceMethodParms not implemented");
    }

    public MethodDef[] getServiceMethods(Date versDateTime)
            throws ServerException {
        throw new RuntimeException("MockBmechReader.getServiceMethods not implemented");
    }

    public InputStream getServiceMethodsXML(Date versDateTime)
            throws ServerException {
        throw new RuntimeException("MockBmechReader.getServiceMethodsXML not implemented");
    }

}
