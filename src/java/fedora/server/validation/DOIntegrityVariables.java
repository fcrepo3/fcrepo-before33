package fedora.server.validation;

import java.net.URL;

/**
 *
 * <p><b>Title:</b> DOIntegrityVariables.java</p>
 * <p><b>Description:</b> Data elements and attributes that are parsed out of
 * a digital object METS xml file to be used as the basis for referential
 * integrity checks performed as part of Level 3 Validation.</p>
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
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version 1.0
 */
public class DOIntegrityVariables
{

  /** A list of PIDs that represents all the behavior definitions found
   *  in the digital object.  Level 3 validation will make sure that
   *  all of these behavior definition objects exist in the local repository.
   */
  protected String[] bDefPIDs;

  /** A list of PIDs that represents all the behavior mechanisms found
   *  in the digital object.  Level 3 validation will make sure that
   *  all of these behavior mechanism objects exist in the local repository.
   */
  protected String[] bMechPIDs;

  /** A list of all the URLs for External-Ref and Protected-External-Ref
   *  Datastreams found in the digital object.  Level 3 validation can
   *  do link liveness checking on these.
   */
  protected URL[] dsLocationURLs;

  public DOIntegrityVariables()
  {
  }

}