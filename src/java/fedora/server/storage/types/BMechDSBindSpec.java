package fedora.server.storage.types;

import java.util.*;

/**
 *
 * <p><b>Title:</b> BMechDSBindSpec.java</p>
 * <p><b>Description:</b> </p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
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

    /**
     * Validate a set of datastream bindings against this binding spec.
     *
     * Return null if successful.  Otherwise, return an explanation of why not.
     */
    public String validate(DSBindingAugmented[] augBindings) {
        if (dsBindRules == null) return null;
        for (int i = 0; i < dsBindRules.length; i++) {
            BMechDSBindRule rule = dsBindRules[i];
            String key = rule.bindingKeyName;
            List bindingsForThisRule = new ArrayList();
            for (int j = 0; j < augBindings.length; j++) {
                if (augBindings[j].bindKeyName.equals(key)) {
                    bindingsForThisRule.add(augBindings[j]);
                }
            }
            String failedReason = rule.validate(bindingsForThisRule);
            if (failedReason != null) return failedReason;
        }
        return null;
    }

}
