package fedora.client.bmech.data;

/**
 *
 * <p><b>Title:</b> Method.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class Method
{
  public static final String HTTP_MESSAGE_PROTOCOL = "HTTP";
  //public static final String SOAP_MESSAGE_PROTOCOL = "SOAP";

  public String methodName = null;
  public String methodLabel = null;
  public MethodProperties methodProperties = new MethodProperties();

  public Method()
  {
  }
}