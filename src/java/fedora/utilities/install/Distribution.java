package fedora.utilities.install;

import java.io.*;

public abstract class Distribution {

    public static final String TOMCAT_PATH = "someDir/tomcat.zip";

    public boolean isBundled() {
        return contains(TOMCAT_PATH);
    }

    public abstract boolean contains(String path);

    public abstract InputStream get(String path) throws IOException;

}
