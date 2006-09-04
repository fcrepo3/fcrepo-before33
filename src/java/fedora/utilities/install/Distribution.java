package fedora.utilities.install;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class Distribution {
	public static final String FEDORA_WAR = "fedora.war";
	public static final String IMAGEMANIP_WAR = "imagemanip.war";
	public static final String SAXON_WAR = "saxon.war";
	public static final String FOP_WAR = "fop.war";
	public static final String FEDORA_HOME = "fedorahome.zip";
	public static final String KEYSTORE = "keystore";
	public static final String TRUSTSTORE = "truststore";
	public static final String JAAS_CONFIG = "jaas.config";
	
	public static final String TOMCAT;
	public static final String MCKOI;
	public static final String JDBC_MYSQL;
	public static final String JDBC_MCKOI;
	
	public static final String TOMCAT_BASENAME;
	public static final String MCKOI_BASENAME;
	
	private static Properties PROPS;
	static {
		// an up to date install.properties should be provided by the buildfile
        String path = "resources/install.properties";
        InputStream in = OptionDefinition.class.getClassLoader().
        getResourceAsStream(path);
        PROPS = new Properties();
        try {
            PROPS.load(in);
        } catch (Exception e) {
            System.err.println("ERROR: Unable to load required resource: " + path);
            System.exit(1);
        }       
        TOMCAT = PROPS.getProperty("install.tomcat");
        MCKOI = PROPS.getProperty("install.mckoi");
        JDBC_MYSQL = PROPS.getProperty("install.jdbc.mckoi");
        JDBC_MCKOI = PROPS.getProperty("install.jdbc.mckoi");
        TOMCAT_BASENAME = PROPS.getProperty("install.tomcat.basename");
        MCKOI_BASENAME = PROPS.getProperty("install.mckoi.basename");
    }
	
    public boolean isBundled() {
        return contains(TOMCAT);
    }

    public abstract boolean contains(String path);

    public abstract InputStream get(String path) throws IOException;

}
