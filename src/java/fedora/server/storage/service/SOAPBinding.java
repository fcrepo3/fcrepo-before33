package fedora.server.storage.service;

/**
 * <p>Title: SOAPBinding.java</p>
 * <p>Description: A data structure for holding a WSDL SOAP Binding for a set
 * of abstract operations.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

public class SOAPBinding extends Binding
{
  public String bindingStyle;
  public String bindingTransport;
  public SOAPOperation[] operations;
}