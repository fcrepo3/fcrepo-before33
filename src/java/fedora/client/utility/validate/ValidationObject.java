/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.utility.validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fedora.server.storage.types.DatastreamDef;
import fedora.server.storage.types.RelationshipTuple;

/**
 * An abstraction of a digital object, containing only those fields and
 * attributes that are needed for validation. (As validation becomes more
 * elaborate, this class will also.)
 * 
 * @author Jim Blake
 */
public class ValidationObject {

    private final String pid;

    private final Set<RelationshipTuple> relations;

    private final Set<DatastreamDef> datastreamDefs;

    public ValidationObject(String pid,
                            Collection<RelationshipTuple> relations,
                            Collection<DatastreamDef> datastreamDefs) {
        this.pid = pid;

        this.relations =
                Collections
                        .unmodifiableSet(new HashSet<RelationshipTuple>(relations));

        this.datastreamDefs =
                Collections
                        .unmodifiableSet(new HashSet<DatastreamDef>(datastreamDefs));
    }

    public String getPid() {
        return pid;
    }

    public boolean hasRelation(String relationship) {
        if (relationship == null) {
            throw new NullPointerException("'relationship' may not be null.");
        }
        for (RelationshipTuple relation : relations) {
            if (relationship.equals(relation.predicate)) {
                return true;
            }
        }
        return false;
    }

    public Collection<RelationshipTuple> getRelations(String relationship) {
        List<RelationshipTuple> result = new ArrayList<RelationshipTuple>();
        for (RelationshipTuple relation : relations) {
            if (relationship.equals(relation.predicate)) {
                result.add(relation);
            }
        }
        return result;
    }

    public Collection<String> getDatastreamIds() {
        Set<String> ids = new HashSet<String>();
        for (DatastreamDef def : datastreamDefs) {
            ids.add(def.dsID);
        }
        return ids;
    }

    @Override
    public String toString() {
        return "ValidationObject[pid='" + pid + "', relations=" + relations
                + "', datastreamDefs=" + datastreamDefs + "]";
    }

}
