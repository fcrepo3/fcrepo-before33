package fedora.server.security.servletfilters;

/** 
 *  @author Bill Niebel (niebel@virginia.edu)
 */
public interface CacheElementPopulator {
	
	public void populateCacheElement(CacheElement cacheElement, String password);

}
