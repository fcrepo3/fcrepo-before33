package fedora.server.access;

/**
 * <p><b>Title: </b>RepositoryInfo.java</p>
 * <p><b>Description: </b>Data structure to contain a key information about
 * the repository.  This information is the return value of the API-A
 * "describeRepository" request.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
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