package fedora.server.storage;

import fedora.server.errors.ValidationException;

import java.io.InputStream;
import java.io.IOException;

public interface StreamValidator {

    public void validate(InputStream in, String validationType)
            throws ValidationException;

}
