package fedora.server.access;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class DissResultSet
{
  public String addressLocation = null;
  public String operationLocation = null;
  public String dsLocation = null;
  public String dsBindingKey = null;
  public String returnType = null;
  public String protocolType = null;
  public String doPID = null;
  public String dissName = null;
  public String method = null;

  public DissResultSet()
  {

  }

  public DissResultSet(String[] sqlResults)
  {
    this.doPID = sqlResults[0];
    this.dissName = sqlResults[1];
    this.method = sqlResults[2];
    this.addressLocation = sqlResults[3];
    this.operationLocation = sqlResults[4];
    this.protocolType = sqlResults[5];
    this.returnType = sqlResults[6];
    this.dsLocation = sqlResults[7];
    this.dsBindingKey = sqlResults[8];
  }

}