package fedora.server.storage.service;

/**
 * <p>Title: HTTPBinding.java</p>
 * <p>Description: A data structure for holding a WSDL HTTP Binding for a set
 * of abstract operations.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

public class HTTPBinding extends Binding
{
  public String bindingVerb;
  public HTTPOperation[] operations;
}