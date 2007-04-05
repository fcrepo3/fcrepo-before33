/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.types;

import java.util.Date;

/**
 *
 * <p><b>Title:</b> ObjectMethodsDef.java</p>
 * <p><b>Description:</b> Data structure to contain all method definitions for
 * a digital object.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class ObjectMethodsDef
{
  public String PID = null;
  public String bDefPID = null;
  public String methodName = null;
  public MethodParmDef[] methodParmDefs = null;
  public Date asOfDate = null;
}