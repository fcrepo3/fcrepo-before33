package fedora.server.resourceIndex;

import fedora.server.errors.ResourceIndexException;
import java.util.List;
import java.util.Map;

/**
 * @author Edwin Shin
 *
 */
public interface RIResultIterator {
	/**
	 * Returns true if the iteration has more elements.
	 * 
	 * @return boolean
	 */
	public boolean hasNext() ;
	
	/**
	 * Returns the next element in the iteration.
	 * 
	 * @return java.util.Map
	 */
	public Map next() throws ResourceIndexException;
	
	/**
     * Get the names of the binding variables.
     * These will be the keys in the map for each row.
     * @return java.util.List
     */
    public List names() throws ResourceIndexException;
    
    /**
     * Release resources held by this iterator.
     */
    public void close() throws ResourceIndexException;
}
