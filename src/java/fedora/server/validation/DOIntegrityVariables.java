package fedora.server.validation;

/**
 * <p>Title: DOIntegrityVariables.java</p>
 * <p>Description: Data elements and attributes that are parsed out of
 * a digital object METS xml file to be used as the basis for referential
 * integrity checks performed as part of Level 3 Validation. </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette,  payette@cs.cornell.edu
 * @version 1.0
 */

import java.net.URL;

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