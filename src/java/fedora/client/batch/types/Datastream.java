package fedora.client.batch.types;

/**
 *
 * <p><b>Title:</b> Datastream.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class Datastream
{

  public String dsID;

  public String dsVersionID;

  public String dsLabel;

  public String dsMIME;

  public String asOfDate;

  public String dsControlGrp;

  public String dsInfoType;

  public String dsState;

  public String dsLocation;

  public String objectPID;
  
  public boolean versionable = true;
  
  public String formatURI;

  public byte[] xmlContent;
  
  public boolean force = false;
  
  public String[] altIDs = new String[0];
  
  public String logMessage;



}
