package fedora.client.bmech.data;

/**
 *
 * <p><b>Title:</b> MethodProperties.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class MethodProperties
{
  public static final String HTTP_MESSAGE_PROTOCOL = "HTTP";
  //public static final String SOAP_MESSAGE_PROTOCOL = "SOAP";

  // Data entered via MethodPropertiesDialog
  public MethodParm[] methodParms = new MethodParm[0];
  public String[] returnMIMETypes = new String[0];
  public String[] dsBindingKeys = new String[0];
  public String protocolType = null;
  public String methodRelativeURL = null;
  public String methodFullURL = null;
  public boolean wasValidated = false;

  public MethodProperties()
  {
  }
}