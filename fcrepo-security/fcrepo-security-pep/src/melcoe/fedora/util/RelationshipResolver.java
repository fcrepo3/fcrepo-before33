package melcoe.fedora.util;

import java.util.List;

import melcoe.fedora.pep.PEPException;

public interface RelationshipResolver
{
	/**
	 * Obtains a list of parents for the given pid.
	 * 
	 * @param pid object id whose parents we wish to find
	 * @return a Set containing the parents of the pid
	 * @throws PEPException
	 */
	public List<String> getParents(String pid) throws PEPException;

	/**
	 * Generates a REST based representation of an object and its parents. For example, given the parameter b,
	 * and if b belongs to collection a, then we will end up with /a/b
	 * 
	 * @param pid the pid whose parents we need to find
	 * @return the REST representation of the pid and its parents
	 * @throws PEPException
	 */
	public String buildRESTParentHierarchy(String pid) throws PEPException;
}
