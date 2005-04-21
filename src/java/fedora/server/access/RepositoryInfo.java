package fedora.server.access;

/**
 * <p><b>Title: </b>RepositoryInfo.java</p>
 * <p><b>Description: </b>Data structure to contain a key information about
 * the repository.  This information is the return value of the API-A
 * "describeRepository" request.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class RepositoryInfo
{
  public String repositoryName = null;
  public String repositoryBaseURL = null;
  public String repositoryVersion = null;
  public String repositoryPIDNamespace = null;
  public String defaultExportFormat = null;
  public String OAINamespace = null;
  public String[] adminEmailList = new String[0];
  public String samplePID = null;
  public String sampleOAIIdentifer = null;
  public String sampleSearchURL = null;
  public String sampleAccessURL = null;
  public String sampleOAIURL = null;
  public String[] retainPIDs = new String[0];
}