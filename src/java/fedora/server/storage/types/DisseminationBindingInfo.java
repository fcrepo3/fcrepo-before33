package fedora.server.storage.types;

import fedora.server.storage.types.MethodParmDef;

/**
 * <p>Title: DisseminationBindingInfo.java</p>
 * <p>Description: Data struture for holding information necessary to
 * complete a dissemination request.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class DisseminationBindingInfo
{
  public String DSBindKey = null;
  public MethodParmDef[] methodParms = null;
  public String DSLocation = null;
  public String AddressLocation = null;
  public String OperationLocation = null;
  public String ProtocolType = null;
}