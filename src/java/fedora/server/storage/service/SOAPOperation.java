package fedora.server.storage.service;

/**
 *
 * <p><b>Title:</b> SOAPOperation.java</p>
 * <p><b>Description:</b> A data structure for holding WSDL SOAP binding for
 * an operation.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class SOAPOperation extends AbstractOperation
{
  /**
   * soapAction:  a URI for the soap request
   */
  public String soapAction;

  /**
   * soapActionStyle:  indicates whether the soap messages will be RPC-oriented
   * (message contains parameters and return values) or document-oriented
   * (message contains document or documents).
   *
   * Valid values for soapActionStyle:
   * 1) rpc
   * 2) document
   */
  public String soapActionStyle;

  // FIXIT!  finish up defintion here....

  //public String inputBindingScheme;

  //public String outputBindingScheme;
}