package fedora.server.resourceIndex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fedora.server.errors.ResourceIndexException;

import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;

import fedora.server.storage.types.BMechDSBindSpec;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodDefOperationBind;
import fedora.server.storage.types.MethodParmDef;

public class MockMethodInfoStore implements MethodInfoStore {

    /**
     * Whether permutation information should be stored.
     */
    private boolean _storePermutations;

    /**
     * Key: bDef pid, Value: Set of method names it defines.
     */
    private Map<String, Set<String>> _bDefMethods;

    /**
     * Key: bDef pid/methodName, Value: Set of permutations.
     */
    private Map<String, Set<String>> _bDefMethodPermutations;

    /**
     * Key: bMech pid, Value: Associated bDef pid.
     */
    private Map<String, String>      _bMechBDef;

    /**
     * Key: bMech pid, Value: Set of method names it implements.
     */
    private Map<String, Set<String>> _bMechMethods;

    /**
     * Key: bMech pid/methodName, Value: Set of binding keys for method.
     */
    private Map<String, Set<String>> _bMechMethodBindingKeys;

    /**
     * Key: bMech pid/methodName, Value: Set of return MIME types for method.
     */
    private Map<String, Set<String>> _bMechMethodReturnTypes;

    /**
     * Get an empty instance.
     */
    public MockMethodInfoStore(boolean storePermutations) {
        _storePermutations = storePermutations;

        _bDefMethods = new HashMap<String, Set<String>>();
        _bDefMethodPermutations = new HashMap<String, Set<String>>();

        _bMechBDef = new HashMap<String, String>();
        _bMechMethods = new HashMap<String, Set<String>>();
        _bMechMethodBindingKeys = new HashMap<String, Set<String>>();
        _bMechMethodReturnTypes = new HashMap<String, Set<String>>();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void putBDefInfo(BDefReader reader)
            throws ResourceIndexException {

        try {
            String pid = reader.GetObjectPID();

            deleteBDefInfo(pid);

            MethodDef[] methodDefs = reader.getAbstractMethods(null);
            Set<String> methods = new HashSet<String>();
            for (int i = 0; i < methodDefs.length; i++) {
                MethodDef def = methodDefs[i];
                methods.add(def.methodName);
                Set<String> perms;
                if (_storePermutations) {
                    perms = new ParamDomainMap(def.methodName,
                            def.methodParms, true).getPermutations();
                } else {
                    perms = new HashSet<String>();
                    // NOTE: This seems counterintuitive, but it mimicks the
                    //       original implementation.  If we knew it wouldn't
                    //       cause problems, we could remove it.
                    perms.add(def.methodName);
                }
                _bDefMethodPermutations.put(pid + "/" + def.methodName, perms);
            }

            _bDefMethods.put(pid, methods);
            
        } catch (Exception e) {
            throw new ResourceIndexException("Error putting BDef info", e);
        }

    }

    /**
     * {@inheritDoc}
     */
    public synchronized void putBMechInfo(BMechReader reader)
            throws ResourceIndexException {

        try {
            String pid = reader.GetObjectPID();

            deleteBMechInfo(pid);

            BMechDSBindSpec inputSpec = reader.getServiceDSInputSpec(null);
            _bMechBDef.put(pid, inputSpec.bDefPID);

            MethodDefOperationBind[] methodBindings = 
                    reader.getServiceMethodBindings(null);
            Set<String> methods = new HashSet<String>();
            for (int i = 0; i < methodBindings.length; i++) {
                MethodDefOperationBind methodBinding = methodBindings[i];
                methods.add(methodBinding.methodName);
                String key = pid + "/" + methodBinding.methodName;
                _bMechMethodBindingKeys.put(key, 
                        getStringSet(methodBinding.dsBindingKeys));
                _bMechMethodReturnTypes.put(key, 
                        getStringSet(methodBinding.outputMIMETypes));
            }

            _bMechMethods.put(pid, methods);

        } catch (Exception e) {
            throw new ResourceIndexException("Error putting BMech info", e);
        }
    }

    /**
     * Get a set of strings for the given array of strings.
     */
    private static Set<String> getStringSet(String[] values) {
        Set<String> set = new HashSet<String>(values.length);
        for (String value : values) {
            set.add(value);
        }
        return set;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void deleteBDefInfo(String bDefPID) {
        Set<String> methods = _bDefMethods.get(bDefPID);
        if (methods != null) {
            for (String method : methods) {
                _bDefMethodPermutations.remove(bDefPID + "/" + method);
            }
            _bDefMethods.remove(bDefPID);
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void deleteBMechInfo(String bMechPID)
            throws ResourceIndexException {
        _bMechBDef.remove(bMechPID);
        Set<String> methods = _bMechMethods.get(bMechPID);
        if (methods != null) {
            for (String method : methods) {
                _bMechMethodBindingKeys.remove(bMechPID + "/" + method);
                _bMechMethodReturnTypes.remove(bMechPID + "/" + method);
            }
            _bMechMethods.remove(bMechPID);
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Set<MethodInfo> getMethodInfo(String bMechPID)
            throws ResourceIndexException {
        Set<String> methods = _bMechMethods.get(bMechPID);
        if (methods != null) {
            Set<MethodInfo> methodInfo = 
                    new HashSet<MethodInfo>(methods.size());
            for (String method : methods) {
                String bDefPID = _bMechBDef.get(bMechPID);
                Set<String> perms = _bDefMethodPermutations.get(bDefPID + "/" + method); 
                if (perms == null) {
                    throw new ResourceIndexException("BDef (" + bDefPID + ") "
                            + "for BMech (" + bMechPID + ") is missing; "
                            + "cannot determine method permutations");
                }
                MethodInfo info = new MethodInfo(method,
                        _bMechMethodBindingKeys.get(bMechPID + "/" + method),
                        _bMechMethodReturnTypes.get(bMechPID + "/" + method),
                        perms);
                methodInfo.add(info);
            }
            return methodInfo;
        } else {
            throw new ResourceIndexException("No such bMech: " + bMechPID);
        }
    }

}
