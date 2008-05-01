/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.deployment.data;

/**
 * @author Sandy Payette
 */
public class ServiceProfile {

    public String serviceName = null;

    public String serviceLabel = null;

    public String serviceTestURL = null;

    public String transProtocol = null;

    public String msgProtocol = null;

    public String[] inputMIMETypes = new String[0];

    public String[] outputMIMETypes = new String[0];

    public ServiceSoftware[] software = new ServiceSoftware[0];

    public ServiceProfile() {
    }
}
