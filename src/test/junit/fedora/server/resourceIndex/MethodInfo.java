package fedora.server.resourceIndex;

import java.util.Set;

/**
 * Key information about an implemented service method.
 *
 * @author cwilper@cs.cornell.edu
 */
public class MethodInfo {

    private String _name;

    private Set<String> _bindingKeys;

    private Set<String> _returnTypes;

    private Set<String> _permutations;

    public MethodInfo(String name,
                           Set<String> bindingKeys,
                           Set<String> returnTypes,
                           Set<String> permutations) {
        _name = name;
        _bindingKeys = bindingKeys;
        _returnTypes = returnTypes;
        _permutations = permutations;
    }

    public String getName() {
        return _name;
    }

    public Set<String> getBindingKeys() {
        return _bindingKeys;
    }

    public Set<String> getReturnTypes() {
        return _returnTypes;
    }

    /**
     * Return all permutations of the method.
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
        return _permutations;
    }

}