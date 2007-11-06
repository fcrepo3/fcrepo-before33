/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.types;

import java.util.*;

/**
 * @author payette@cs.cornell.edu
 */
public class BMechDSBindSpec
{
  public String bMechPID;

  public String bDefPID;

  public String bindSpecLabel;

  public String state;

  public BMechDSBindRule[] dsBindRules;

  public BMechDSBindSpec()
  {
  }

}
