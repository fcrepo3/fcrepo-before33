package fedora.server.storage.service;

/**
 * <p>Title: Part.java</p>
 * <p>Description: A data structure for holding WSDL Message part.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import java.util.Vector;

public class Part
{
  public String partName;
  public String partTypeName;
  public String partBaseTypeNamespaceURI;
  public String partBaseTypeLocalName;

  // consider...
  public Vector enumerationOfValues;
  public String defaultValue;
}