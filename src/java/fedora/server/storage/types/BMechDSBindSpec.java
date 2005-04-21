package fedora.server.storage.types;

import java.util.*;

/**
 *
 * <p><b>Title:</b> BMechDSBindSpec.java</p>
 * <p><b>Description:</b> </p>
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
