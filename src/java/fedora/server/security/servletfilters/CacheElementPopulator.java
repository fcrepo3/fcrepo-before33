/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.security.servletfilters;

/** 
 *  @author Bill Niebel (niebel@virginia.edu)
 */
public interface CacheElementPopulator {
	
	public void populateCacheElement(CacheElement cacheElement, String password);

}
