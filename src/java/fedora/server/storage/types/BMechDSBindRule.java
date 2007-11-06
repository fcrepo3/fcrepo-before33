/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.types;

import java.util.*;

/**
 * A datastream binding rule.
 *
 * @author payette@cs.cornell.edu
 */
public class BMechDSBindRule
{
  public String bindingKeyName;

  public int minNumBindings;

  public int maxNumBindings;

  public boolean ordinality;

  public String bindingLabel;

  public String bindingInstruction;

  public String[] bindingMIMETypes;

  public BMechDSBindRule()
  {
  }

    private static final String ANY_MIME_TYPE = "*/*";

    /**
     * In human readable string, describe which mime types are allowed.
     */
    public String describeAllowedMimeTypes() {
        StringBuffer out = new StringBuffer();
        if (bindingMIMETypes == null || bindingMIMETypes.length == 0) {
            return ANY_MIME_TYPE;
        }
        for (int i = 0; i < bindingMIMETypes.length; i++) {
            String allowed = bindingMIMETypes[i];
            if (allowed == null) return ANY_MIME_TYPE;
            if (allowed.equals("*/*")) return ANY_MIME_TYPE;
            if (allowed.equals("*")) return ANY_MIME_TYPE;
            if (i > 0) {
                if ( i < bindingMIMETypes.length - 1) {
                    out.append(", ");
                } else {
                    if ( i > 1 ) {
                      out.append(",");
                    }
                    out.append(" or ");
                }
            }
            out.append(allowed);
        }
        return out.toString();
    }

}