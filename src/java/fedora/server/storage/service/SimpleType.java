/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.service;

import java.util.Vector;

/**
 *
 * <p><b>Title:</b> SimpleType.java</p>
 * <p><b>Description:</b> A data structure for holding WSDL xsd type
 * declarations for simple types.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class SimpleType extends Type
{
  public Vector enumerationOfValues;
}