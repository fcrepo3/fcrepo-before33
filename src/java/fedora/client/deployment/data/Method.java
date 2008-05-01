/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.deployment.data;

/**
 * @author Sandy Payette
 */
public class Method {

    public static final String HTTP_MESSAGE_PROTOCOL = "HTTP";

    //public static final String SOAP_MESSAGE_PROTOCOL = "SOAP";

    public String methodName = null;

    public String methodLabel = null;

    public MethodProperties methodProperties = new MethodProperties();

    public Method() {
    }
}
