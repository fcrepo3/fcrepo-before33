package fedora.server.storage.service;

/**
 *
 * <p><b>Title:</b> SOAPBinding.java</p>
 * <p><b>Description:</b> A data structure for holding a WSDL SOAP Binding for a set
 * of abstract operations.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class SOAPBinding extends Binding
{
  public String bindingStyle;
  public String bindingTransport;
  public SOAPOperation[] operations;
}