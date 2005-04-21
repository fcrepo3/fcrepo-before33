package fedora.server.storage.service;

/**
 *
 * <p><b>Title:</b> HTTPBinding.java</p>
 * <p><b>Description:</b> A data structure for holding a WSDL HTTP Binding for
 * a set of abstract operations.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class HTTPBinding extends Binding
{
  public String bindingVerb;
  public HTTPOperation[] operations;
}