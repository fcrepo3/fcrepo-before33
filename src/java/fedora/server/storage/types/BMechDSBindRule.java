package fedora.server.storage.types;

import java.util.*;

/**
 * A datastream binding rule.
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
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


    /**
     * Validate the given DSBindingAugmented objects against the min/max
     * and mime type constraints of this rule.
     *
     * If the rule is satisfied, return null.
     * Otherwise, return a string explaining the problem.
     */
    public String validate(List augBindings) {

        // min/max constraints

        if (augBindings == null) {
            if (minNumBindings > 0) {
                return failedReason("Required at least " + minNumBindings 
                        + ", got 0.");
            }
            return null; // success
        }
        if (augBindings.size() < minNumBindings) {
            return failedReason("Required at least " + minNumBindings 
                    + ", got " + augBindings.size());
        }
        if (augBindings.size() > maxNumBindings) {
            return failedReason("Required at most " + maxNumBindings 
                    + ", got " + augBindings.size());
        }

        // mime type constraints

        String allowedDescription = describeAllowedMimeTypes();
        if (allowedDescription.equals(ANY_MIME_TYPE)) return null;

        Iterator iter = augBindings.iterator();
        while (iter.hasNext()) {
            DSBindingAugmented augBinding = (DSBindingAugmented) iter.next();
            String boundType = augBinding.DSMIME;
            boolean matched = false;
            for (int i = 0; i < bindingMIMETypes.length && !matched; i++) {
                String allowedType = bindingMIMETypes[i];
                if (mimeMatches(boundType, allowedType)) matched = true;
            }
            if (!matched) {
                String id = augBinding.datastreamID;
                return failedReason("Disallowed MIME type (" + boundType
                        + ") for " + id + " datastream: Must be "
                        + allowedDescription + ".");
            }
        }
        return null; // success
    }

    private static boolean mimeMatches(String mime, String toMatch) {
        if (mime.equalsIgnoreCase(toMatch)) return true;
        String[] parts = toMatch.split("/");
        return (parts.length == 2 
             && parts[1].equals("*")
             && mime.startsWith(parts[0] + "/"));
    }

    private String failedReason(String why) {
        return "Failed binding \'" + bindingKeyName + "\': " + why;
    }
}