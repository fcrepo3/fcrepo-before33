package fedora.server.storage.service;

/**
 * <p>Title: SOAPOperation.java</p>
 * <p>Description: A data structure for holding WSDL SOAP binding for an operation.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
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