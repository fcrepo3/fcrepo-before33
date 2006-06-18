package fedora.utilities.install;

import java.io.*;

public class ClassLoaderDistribution extends Distribution {

    private ClassLoader _cl;

    public ClassLoaderDistribution() {
        _cl = this.getClass().getClassLoader();
    }

    public ClassLoaderDistribution(ClassLoader cl) {
        _cl = cl;
    }

    public boolean contains(String path) {
        return _cl.getResource(path) != null;
    }

    public InputStream get(String path) throws IOException {

        InputStream stream = _cl.getResourceAsStream(path);
        if (stream == null) {
            throw new FileNotFoundException("Not found in classpath: " + path);
        } else {
            return stream;
        }
    }

}
