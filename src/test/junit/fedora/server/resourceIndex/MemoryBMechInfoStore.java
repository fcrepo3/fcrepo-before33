package fedora.server.resourceIndex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fedora.server.errors.ResourceIndexException;

/**
 * Stores and provides key information about behavior mechanism objects,
 * in memory.
 *
 * @author cwilper@cs.cornell.edu
 */
public class MemoryBMechInfoStore implements BMechInfoStore {

    /**
     * Map of BMechMethodInfo for all behavior mechanisms, keyed by
     * bMech PID.
     */
    private Map<String, Set<BMechMethodInfo>> _methodInfoMap;

    /**
     * Construct an empty instance.
     */
    public MemoryBMechInfoStore() {
        _methodInfoMap = new HashMap<String, Set<BMechMethodInfo>>();
    }

    /**
     * {@inheritDoc}
     */
    public Set<BMechMethodInfo> getMethodInfo(String bMechPID)
            throws ResourceIndexException {
        Set<BMechMethodInfo> methodInfo = _methodInfoMap.get(bMechPID);
        if (methodInfo == null) {
            throw new ResourceIndexException("Nothing known about bMech: " 
                    + bMechPID);
        } else {
            return methodInfo;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setMethodInfo(String bMechPID, 
                              Set<BMechMethodInfo> methodInfo) {
        if (methodInfo == null || methodInfo.size() == 0) {
            _methodInfoMap.remove(bMechPID);
        } else {
            _methodInfoMap.put(bMechPID, methodInfo);
        }
    }

}