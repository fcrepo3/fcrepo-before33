package fedora.server.storage.types;

import java.util.Date;

/**
 *
 * <p><b>Title:</b> ObjectMethodsDef.java</p>
 * <p><b>Description:</b> Data structure to contain all method definitions for
 * a digital object.</p>
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