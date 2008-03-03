
package fedora.server.storage.types;

import java.util.Arrays;

/**
 * <p>
 * <b>Title:</b> DSBindingMap.java
 * </p>
 * <p>
 * <b>Description:</b>
 * </p>
 * 
 * @author payette@cs.cornell.edu
 * @version $Id: DSBindingMap.java 3742 2005-03-15 18:51:44 +0000 (Tue, 15 Mar
 *          2005) eddie $
 */
public class DSBindingMap {

    public String dsBindMapID = null;

    public String dsBindMechanismPID = null;

    public String dsBindMapLabel = null;

    public String state = null;

    public DSBinding[] dsBindings = new DSBinding[0];

    public DSBindingMap() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(DSBindingMap.class)) {
            return false;
        }
        DSBindingMap that = (DSBindingMap) obj;
        return equivalent(dsBindMapID, that.dsBindMapID)
                && equivalent(dsBindMechanismPID, that.dsBindMechanismPID)
                && equivalent(dsBindMapLabel, that.dsBindMapLabel)
                && equivalent(state, that.state)
                && Arrays.deepEquals(dsBindings, that.dsBindings);
    }

    private boolean equivalent(Object o1, Object o2) {
        return (o1 == null) ? (o2 == null) : (o1.equals(o2));
    }

    @Override
    public int hashCode() {
        return hashIt(dsBindMapID) ^ hashIt(dsBindMechanismPID)
                ^ hashIt(dsBindMapLabel) ^ hashIt(state) ^ hashIt(dsBindings);
    }

    private int hashIt(Object o) {
        return (o == null) ? 0 : o.hashCode();
    }

    @Override
    public String toString() {
        return "DSBindingMap[dsBindMapID=" + dsBindMapID
                + ", dsBindMechanismPID=" + dsBindMechanismPID
                + ", dsBindMapLabel=" + dsBindMapLabel + ", state=" + state
                + ", dsBindings=" + dsBindings + "]";
    }
}
