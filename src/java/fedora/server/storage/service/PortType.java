package fedora.server.storage.service;

/**
 * <p>Title: PortType.java</p>
 * <p>Description: A data structure for holding WSDL Port Type which defines
 * a set of abstract operations.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import java.util.Vector;

public class PortType
{
  public String portTypeName;
  public AbstractOperation[] operations;
}