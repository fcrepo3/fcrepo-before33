package fedora.server.validation;

import fedora.server.errors.ObjectValidityException;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ServerException;

import java.io.InputStream;
import java.io.File;

public interface DOValidator {

  /**
   * <p>Validates a digital object.</p>
   *
   * @param in The digital object provided as a bytestream.
   * @param validationLevel The level of validation to perform on the digital object.
   *        This is an integer from 0-3 with the following meanings:
   *        0 = do all validation levels
   *        1 = perform only XML Schema validation
   *        2 = perform only Schematron Rules validation
   *        3 = perform only referential integrity checks for the object
   * @param workFlowPhase The stage in the work flow for which the validation should be contextualized.
   *        "ingest" = the object is in the submission format for the ingest stage phase
   *        "store" = the object is in the authoritative format for the final storage phase
   * @throws ServerException If validation fails for any reason.
   */
    public void validate(InputStream in, int validationLevel, String workFlowPhase)
        //throws ObjectValidityException, GeneralException;
        throws ServerException;

  /**
   * <p>Validates a digital object.</p>
   *
   * @param in The digital object provided as a file.
   * @param validationLevel The level of validation to perform on the digital object.
   *        This is an integer from 0-3 with the following meanings:
   *        0 = do all validation levels
   *        1 = perform only XML Schema validation
   *        2 = perform only Schematron Rules validation
   *        3 = perform only referential integrity checks for the object
   * @param workFlowPhase The stage in the work flow for which the validation should be contextualized.
   *        "ingest" = the object is in the submission format for the ingest stage phase
   *        "store" = the object is in the authoritative format for the final storage phase
   * @throws ServerException If validation fails for any reason.
   */
    public void validate(File in, int validationLevel, String workFlowPhase)
        //throws ObjectValidityException, GeneralException;
        throws ServerException;

}
