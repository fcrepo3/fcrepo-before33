package fedora.server.storage.service;

/**
 *
 * <p><b>Title:</b> HTTPOperation.java</p>
 * <p><b>Description:</b> A data structure for holding WSDL HTTP binding
 * information for an operation.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class HTTPOperation extends AbstractOperation
{

  /**
   * operationLocation:  a relative URI for the operation.
   * The URI is ultimately combined with the URI in the http:address element to
   * (see Port object) form the full URI for the HTTP request.
   */
  public String operationLocation;

  /**
   * inputBinding:
   */
  public HTTPOperationInOut inputBinding;


  /**
   * outputBinding:
   */
  public HTTPOperationInOut outputBinding;
}