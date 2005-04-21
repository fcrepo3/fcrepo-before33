package fedora.server.storage.service;

/**
 * <p><b>Title: </b>AbstractOperation.java</p>
 * <p><b>Description: </b>A data structure for holding WSDL abstract operation.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class AbstractOperation
{
  public String operationName;
  public Message inputMessage;
  public Message outputMessage;
}