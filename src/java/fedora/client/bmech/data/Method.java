package fedora.client.bmech.data;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Method
{
  public static final String HTTP_MESSAGE_PROTOCOL = "HTTP";
  public static final String SOAP_MESSAGE_PROTOCOL = "SOAP";

  public String methodName = null;
  public String methodLabel = null;
  public MethodProperties methodProperties = null;

  public Method()
  {
  }
}