/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.utility.validate.process;

import java.io.IOException;

import java.util.Iterator;

import javax.xml.rpc.ServiceException;

import fedora.client.utility.validate.ObjectSource;
import fedora.client.utility.validate.ObjectSourceException;
import fedora.client.utility.validate.ObjectValidator;
import fedora.client.utility.validate.ValidationObject;
import fedora.client.utility.validate.ValidationResults;
import fedora.client.utility.validate.remote.RemoteObjectSource;

/**
 * A command-line utility that validates objects in a remote repository,
 * selected by criteria. See the javadoc for {@link ValidatorProcessParameters}
 * for the usage details.
 * 
 * @author Jim Blake
 */
public class ValidatorProcess {

    public static void main(String[] args) throws ObjectSourceException {
        // Parse the parameters.
        ValidatorProcessParameters parms = new ValidatorProcessParameters(args);

        // Create the tools from we will need.
        ObjectSource objectSource = null;
        ValidationResults results;

        try {
            objectSource = new RemoteObjectSource(parms.getServiceInfo());
            results =
                    new Log4jValidationResults(parms.getLogConfigProperties());

        } catch (ServiceException e) {
            throw new IllegalStateException("Failed to initialize the ValidatorProcess: ",
                                            e);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize the ValidatorProcess: ",
                                            e);
        }

        /*
         * Get the list of PIDs for the objects that we will be validating,
         * based on the query parameters.
         */
        Iterator<String> pids = objectSource.findObjectPids(parms.getQuery());

        // Go through the list, validating.
        ObjectValidator validator = new ObjectValidator(objectSource);
        while (pids.hasNext()) {
            ValidationObject object =
                    objectSource.getValidationObject(pids.next());
            results.record(validator.validate(object));
        }

        // Display the results.
        results.closeResults();
    }

}
