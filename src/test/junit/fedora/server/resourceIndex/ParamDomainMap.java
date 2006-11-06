package fedora.server.resourceIndex;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import fedora.server.storage.types.MethodParmDef;

/**
 * A sorted map of all <code>ParamDomain</code>s for a method, keyed by
 * parameter name.
 *
 * As per the <code>SortedMap</code> contract, iterators over the parameter
 * names in this collection will provide the keys in ascending order.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ParamDomainMap extends TreeMap<String, ParamDomain> {

    /**
     * The name of the method this map describes.
     */
    private String _methodName;

    /**
     * Get an empty instance.
     *
     * @param methodName the name of the method this map describes.
     */
    public ParamDomainMap(String methodName) {
        _methodName = methodName;
    }

    /**
     * Get an instance from an existing array of <code>MethodParmDef</code>s.
     *
     * @param methodName the name of the method this map describes.
     * @param parmDefs existing parameter definitions.
     * @param userInputOnly if true, only USER_INPUT parameters from the
     *        given array will be used.
     */
    public ParamDomainMap(String methodName, MethodParmDef[] parmDefs, 
            boolean userInputOnly) {

        _methodName = methodName;

        for (int i = 0; i < parmDefs.length; i++) {
            if (!userInputOnly
                    || parmDefs[i].parmType.equals(MethodParmDef.USER_INPUT)) {
                ParamDomain domain = new ParamDomain(
                        parmDefs[i].parmName,
                        parmDefs[i].parmRequired, 
                        parmDefs[i].parmDomainValues);
                put(parmDefs[i].parmName, domain);
            }
        }
    }

    /**
     * Get the name of the method this map describes.
     *
     * @return the name of the method this map describes.
     */
    public String getMethodName() {
        return _methodName;
    }

    /**
     * Get all permutations of the method.
     *
     * A "permutation" is a known runtime method invocation, and is formatted
     * as in the following examples:
     * <ul>
     *   <li> methodName</li>
     *   <li> methodName?parm1=val1&amp;parm2=val1</li>
     *   <li> methodName?parm1=val1&amp;parm2=val2</li>
     *   <li> methodName?parm1=val2&amp;parm2=val1</li>
     *   <li> methodName?parm1=val2&amp;parm2=val2</li>
     * </ul>
     *
     * @return the set of invokable permutations for the method.
     */
    public Set<String> getPermutations() {
        Set<String> set = new HashSet<String>();
        addPermutations(_methodName, getValues(), 0, '?', set);
        return set;
    }

    /**
     * Recursively add permutations for all possible values of domains[index].
     */
    private static void addPermutations(String prefix,
                                        ParamDomain[] domains,
                                        int index,
                                        char delimiter,
                                        Set<String> set) {

        if (index + 1 > domains.length) {
            // no more parameters; prefix is a permutation
            set.add(prefix);
            return;
        } else {
            ParamDomain domain = domains[index];
            if (domain.size() > 0) {
                // add permutations for each domain value of this parameter
                for (String domainValue : domain) {
                    String newPrefix = prefix + delimiter 
                            + domain.getParameterName() + "=" + domainValue;
                    addPermutations(newPrefix, domains, index + 1, '&', set);
                }
            }
            if (!domain.isRequired()) {
                // add permutations where this parameter is unspecified
                addPermutations(prefix, domains, index + 1, delimiter, set);
            }
        }
    }
                                
    /**
     * Get an array of all <code>ParamDomain</code> values in this map.
     *
     * @return all values, sorted by parameter name.
     */
    private ParamDomain[] getValues() {
        ParamDomain[] values = new ParamDomain[size()];
        int i = 0;
        for (String key : keySet()) {
            values[i++] = get(key);
        }
        return values;
    }

}