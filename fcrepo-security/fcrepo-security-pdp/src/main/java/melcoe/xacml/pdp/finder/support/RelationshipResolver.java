package melcoe.xacml.pdp.finder.support;

import java.util.Map;
import java.util.Set;

import melcoe.xacml.pdp.finder.AttributeFinderException;

public interface RelationshipResolver {
	/**
	 * Obtains a list of parents for the given pid.
	 * 
	 * @param pid
	 *            object id whose parents we wish to find
	 * @return a Set containing the parents of the pid
	 * @throws AttributeFinderException
	 */
	public Set<String> getParents(String pid) throws AttributeFinderException;

	/**
	 * Generates a REST based representation of an object and its parents. For
	 * example, given the parameter b, and if b belongs to collection a, then we
	 * will end up with /a/b
	 * 
	 * @param pid
	 *            the pid whose parents we need to find
	 * @return the REST representation of the pid and its parents
	 * @throws AttributeFinderException
	 */
	public String buildRESTParentHierarchy(String pid)
			throws AttributeFinderException;

	/**
	 * Retrieves the relationships for this PID. Values for each relationship
	 * are placed in a set.
	 * 
	 * @param pid
	 *            the pid to return relationships for
	 * @return The map of relationships and values.
	 * @throws AttributeFinderException
	 */
	public Map<String, Set<String>> getRelationships(String pid)
			throws AttributeFinderException;
}
