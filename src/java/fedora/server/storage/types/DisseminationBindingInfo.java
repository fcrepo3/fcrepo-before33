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
  public String dsLocation = null;
  public String dsControlGroupType = null;
  public String dsID = null;
  public String dsVersionID = null;
  public String AddressLocation = null;
  public String OperationLocation = null;
  public String ProtocolType = null;
}