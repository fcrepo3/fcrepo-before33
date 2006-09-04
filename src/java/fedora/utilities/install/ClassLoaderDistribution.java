package fedora.utilities.install;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ClassLoaderDistribution extends Distribution {

    private ClassLoader _cl;

    public ClassLoaderDistribution() {
        _cl = this.getClass().getClassLoader();
    }

    public ClassLoaderDistribution(ClassLoader cl) {
        _cl = cl;
    }

    public boolean contains(String path) {
        return _cl.getResource(rewritePath(path)) != null;
    }
    
    public InputStream get(String path) throws IOException {
        InputStream stream = _cl.getResourceAsStream(rewritePath(path));
        if (stream == null) {
            throw new FileNotFoundException("Not found in classpath: " + path);
        } else {
            return stream;
        }
    }
    
    /**
     * Note: we don't check for backtracking
     * @param path
     * @return
     */
    private static String rewritePath(String path) {
    	if (path.startsWith("/")) {
    		path = path.substring(1);
    	}
    	// Note, ClassLoader paths are always absolute, so , so no leading slash
    	return "resources/" + path;
    }

}
