package fedora.test;

import java.io.File;


/**
 * @author Edwin Shin
 */
public interface FedoraTestConstants {
    public static final String PROP_SETUP = "fedora.test.setup";
    public static final String PROP_FEDORA_HOME = "fedora.home";
    public static final String PROP_TEST_HOME = "fedora.test.home";
    public static final String PROP_TOMCAT_BASENAME = "tomcat.basename";

    public static final String FEDORA_HOME = System.getProperty(PROP_FEDORA_HOME, new File("dist").getAbsolutePath());
    public static final String FCFG = FEDORA_HOME + "/server/config/fedora.fcfg";
    public static final String FCFG_SRC = "src/fcfg/server/fedora.fcfg";
    public static final String FCFG_SRC_DIR = "src/fcfg/server";

    public static final String BESECURITY_PATH = FEDORA_HOME + "/server/config/beSecurity.xml";

    public static final String TOMCAT_HOME = FEDORA_HOME + "/server/" + System.getProperty(PROP_TOMCAT_BASENAME);
   
    public static final String JAAS      = "jaas.config";
    public static final String JAAS_PATH = TOMCAT_HOME + "/conf/" + JAAS;

    public static final String TOMCAT_USERS_TEMPLATE      = "tomcat-users_fedoraTemplate.xml";
    public static final String TOMCAT_USERS_TEMPLATE_PATH = TOMCAT_HOME + "/conf/" + TOMCAT_USERS_TEMPLATE;

    public static final String WEB_XML      = "web.xml";
    public static final String WEB_XML_PATH = TOMCAT_HOME + "/webapps/fedora/WEB-INF/" + WEB_XML;

    public static final String NS_FCFG = "http://www.fedora.info/definitions/1/0/config/";
    
    public static final String NS_FEDORA_TYPES_PREFIX = "fedora-types";
    public static final String NS_FEDORA_TYPES = "http://www.fedora.info/definitions/1/0/types/";
}
