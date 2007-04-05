/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.types;

/**
 *
 * <p><b>Title:</b> Property.java</p>
 * <p><b>Description:</b> A data structure for holding properties as
 * name/value pairs. </p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class Property
{
  public String name;
  public String value;

  public Property() {
  }
    
  public Property(String propertyName, String propertyValue) {
  	name=propertyName;
  	value=propertyValue;
  }
}