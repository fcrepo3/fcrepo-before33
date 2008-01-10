
package fedora.test;

import java.io.File;

import fedora.common.Constants;

/**
 * @author Edwin Shin
 */
public interface FedoraTestConstants
        extends Constants {

    public static final File FEDORA_HOME_CLIENT =
            new File(FEDORA_HOME + File.separator + "client");

    public static final File FEDORA_HOME_CLIENT_BIN =
            new File(FEDORA_HOME_CLIENT, "bin");

    public static final File FEDORA_HOME_SERVER =
            new File(FEDORA_HOME + File.separator + "server");

    public static final File FEDORA_HOME_SERVER_CONFIG =
            new File(FEDORA_HOME_SERVER, "config");

    public static final File FCFG =
            new File(FEDORA_HOME_SERVER_CONFIG, "fedora.fcfg");

    public static final File BESECURITY =
            new File(FEDORA_HOME_SERVER_CONFIG, "beSecurity.xml");

    public static final String NS_FCFG =
            "http://www.fedora.info/definitions/1/0/config/";

    public static final String NS_FEDORA_TYPES_PREFIX = "fedora-types";

    public static final String NS_FEDORA_TYPES =
            "http://www.fedora.info/definitions/1/0/types/";

    /* Property supplied by the build file */
    public static final String FEDORA_USERNAME =
            System.getProperty("fedora.username");

    public static final String FEDORA_PASSWORD =
            System.getProperty("fedora.password");

}
