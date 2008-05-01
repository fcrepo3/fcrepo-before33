/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.deployment;

/**
 * @author Sandy Payette
 */
public class DeploymentBuilderException
        extends Exception {

    private static final long serialVersionUID = 1L;

    public DeploymentBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeploymentBuilderException(String message) {
        super(message);
    }

}
