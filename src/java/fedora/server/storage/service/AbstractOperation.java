package fedora.server.storage.service;

/**
 * <p>Title: AbstractOperation.java</p>
 * <p>Description: A data structure for holding WSDL abstract operation.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

public class AbstractOperation
{
  public String operationName;
  public Message inputMessage;
  public Message outputMessage;
}