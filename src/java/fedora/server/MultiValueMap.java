package fedora.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MultiValueMap {
	
	private boolean locked = false;

	private Map attributes = new HashMap();

    /**
     * Creates and initializes the <code>WritableContext</code>.
     * <p></p>
     * @param parameters A pre-loaded Map of name-value pairs
     *        comprising the context.
     */
    public MultiValueMap() {
    }

    public void set(String name, Object value) throws Exception {
    	if (locked) {
    		throw new Exception("object locked");    		
    	}
    	if (value instanceof String) {
    	} else if (value instanceof String[]) {
    		if (((String[])value).length == 1) {
        		value = ((String[])value)[0];
    		}
    	} else {
    		throw new Exception("unhandled type");
    	}
    	attributes.put(name, value);
    }

    public void lock() {
    	locked = true;
    }
    
    public Iterator names() {
        return attributes.keySet().iterator();
    }

    public int length(String name) {
    	if (attributes.get(name) instanceof String) {
    		return 1;
    	} else if (attributes.get(name) instanceof String[]) {
    		return ((String[])attributes.get(name)).length;
    	} else {
    		return -1;
    	}
    }
    
    public String getString(String name) {
        return (String) attributes.get(name);
    }

    public String[] getStringArray(String name) {
        return (String[]) attributes.get(name);
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        Iterator it = attributes.keySet().iterator();
        while (it.hasNext()) {
        	String key = (String) it.next();
        	String value = (String) attributes.get(key);
        	buffer.append(key + "=" + value + "\n");
        }
        return buffer.toString();
    }

}