package fedora.server.errors;

/**
 * <p><b>Title: </b>InvalidUserParmException.java</p>
 * <p><b>Description: </b>Signals that one or more user-supplied method paramters
 * do not validate against the method paramter definitions in the associated
 * Behavior Mechanism object.</p>
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
public class InvalidUserParmException extends DisseminationException
{

  /**
   * Creates an InvalidUserParmException.
   *
   * @param message An informative message explaining what happened and
   *                (possibly) how to fix it.
   */
  public InvalidUserParmException(String message)
  {
      super(message);
  }

}