package fedora.server.resourceIndex;

import java.util.Set;

/**
 * Key information about a method of a behavior mechanism object.
 *
 * @author cwilper@cs.cornell.edu
 */
public class BMechMethodInfo {

    private String _name;

    private Set<String> _bindingKeys;

    private Set<String> _returnTypes;

    private Set<String> _permutations;

    public BMechMethodInfo(String name,
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

    public Set<String> getPermutations() {
        return _permutations;
    }

}