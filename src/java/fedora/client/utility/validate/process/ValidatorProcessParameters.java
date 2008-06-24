/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.utility.validate.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import fedora.client.utility.validate.remote.ServiceInfo;

import fedora.server.errors.QueryParseException;
import fedora.server.search.Condition;
import fedora.server.search.FieldSearchQuery;

/**
 * Parse and store the command-line arguments for the {@link ValidatorProcess}.
 * 
 * @author Jim Blake
 */
public class ValidatorProcessParameters {

    /**
     * A description of how to invoke the process.
     */
    public static final String USAGE =
            "usage: ValidatorProcess -serverurl <url> -username <username> "
                    + "-password <password> {-terms <terms> | -query <query>} "
                    + "[-logConfig <filename>]";

    /**
     * Required parameter: the base url of the Fedora repository server. E.g.
     * <code>http://localhost:8080/fedora</code>
     */
    public static final String PARAMETER_SERVER_URL = "-serverurl";

    /**
     * Required parameter: a user who is authorized to access the repository
     * objects. E.g. <code>fedoraAdmin</code>
     */
    public static final String PARAMETER_USERNAME = "-username";

    /**
     * Required parameter: the password for the user specified by '-username'.
     * E.g. <code>fedoraAdminPassword</code>
     */
    public static final String PARAMETER_PASSWORD = "-password";

    /**
     * Optional parameter: a "terms" string that can be passed to the
     * <code>findObjects</code> method in API-A. Supply either -terms or
     * -query, but not both. E.g. <code></code>
     */
    public static final String PARAMETER_TERMS = "-terms";

    /**
     * Optional parameter: a "query" string that can be passed to the
     * <code>findObjects</code> method in API-A. Supply either -terms or
     * -query, but not both. E.g. <code>state~A</code>
     */
    public static final String PARAMETER_QUERY = "-query";

    /**
     * Optional parameter: the name of a Log4J properties-style configuration
     * file that will be used to restrict or format the output of the validator.
     * If this is not provided, output will be written to System.out. E.g.
     * <code>/usr/local/validator.config.properties</code>
     */
    public static final String PARAMETER_LOG_CONFIG = "-logConfig";

    private static final Set<String> ALL_PARAMETERS =
            Collections.unmodifiableSet(new HashSet<String>(Arrays
                    .asList(PARAMETER_SERVER_URL,
                            PARAMETER_USERNAME,
                            PARAMETER_PASSWORD,
                            PARAMETER_TERMS,
                            PARAMETER_QUERY,
                            PARAMETER_LOG_CONFIG)));

    /**
     * The URL, username and password of the repository we are operating
     * against.
     */
    private final ServiceInfo serviceInfo;

    /**
     * The Log4J configuration properties obtained from the -logConfig file, or
     * an empty set of properties if no filename was provided.
     */
    private final Properties logConfigProperties;

    /**
     * An API-A query object, built from either the -terms or -query parameter.
     */
    private final FieldSearchQuery fieldSearchQuery;

    /**
     * Parse the command line arguments and check for validity.
     */
    public ValidatorProcessParameters(String[] args) {
        Map<String, String> parms = parseArgsIntoMap(args);

        String username = getRequiredParameter(PARAMETER_USERNAME, parms);
        String password = getRequiredParameter(PARAMETER_PASSWORD, parms);
        URL serverUrl = getRequiredUrlParameter(PARAMETER_SERVER_URL, parms);
        serviceInfo = new ServiceInfo(serverUrl, username, password);

        String query = getOptionalParameter(PARAMETER_QUERY, parms);
        String terms = getOptionalParameter(PARAMETER_TERMS, parms);
        fieldSearchQuery = assembleFieldSearchQuery(query, terms);

        String logConfigFile =
                getOptionalParameter(PARAMETER_LOG_CONFIG, parms);
        logConfigProperties = readLogConfigFile(logConfigFile);
    }

    /**
     * Decide which of the command line arguments are keywords and which are
     * values, and build a map to hold them.
     */
    private Map<String, String> parseArgsIntoMap(String[] args) {
        Map<String, String> parms = new HashMap<String, String>();

        for (int i = 0; i < args.length; i++) {
            String key = args[i];
            if (!isKeyword(key)) {
                throw trouble("'" + key + "' is not a keyword.");
            }
            if (!ALL_PARAMETERS.contains(key)) {
                throw trouble("'" + key + "' is not a recognized keyword.");
            }
            if (i >= args.length - 1) {
                parms.put(key, null);
            } else {
                String value = args[i + 1];
                if (isKeyword(value)) {
                    parms.put(key, null);
                } else {
                    parms.put(key, value);
                    i++;
                }
            }
        }

        return parms;
    }

    private boolean isKeyword(String arg) {
        return arg.startsWith("-");
    }

    /**
     * Get the requested parameter from the map. Complain if it's not found or
     * has a null value.
     */
    private String getRequiredParameter(String keyword,
                                        Map<String, String> parms) {
        if (!parms.containsKey(keyword)) {
            throw trouble("Parameter '" + keyword + "' is required.");
        }

        String value = parms.get(keyword);
        if (value == null) {
            throw trouble("Parameter '" + keyword + "' requires a value.");
        } else {
            return value;
        }
    }

    /**
     * Get the requested parameter from the map. Complain if not found, or not a
     * valid URL.
     */
    private URL getRequiredUrlParameter(String keyword,
                                        Map<String, String> parms) {
        String urlString = getRequiredParameter(keyword, parms);
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            throw trouble("Value '" + urlString + "' for parameter '" + keyword
                    + "' is not a valid URL: " + e.getMessage());
        }
    }

    /**
     * Get the requested parameter from the map. If it's not there, return null,
     * but if it's there with no value, complain.
     */
    private String getOptionalParameter(String keyword,
                                        Map<String, String> parms) {
        if (!parms.containsKey(keyword)) {
            return null;
        }

        String value = parms.get(keyword);
        if (value == null) {
            throw trouble("If parameter '" + keyword
                    + "' is provided, it must have a value.");
        } else {
            return value;
        }
    }

    /**
     * A {@link FieldSearchQuery} may be made from a terms string or a query
     * string, but not both.
     */
    private FieldSearchQuery assembleFieldSearchQuery(String query, String terms) {
        if (query == null) {
            if (terms == null) {
                throw trouble("You must provide either '" + PARAMETER_QUERY
                        + "' or '" + PARAMETER_TERMS + "'.");
            } else {
                return new FieldSearchQuery(terms);
            }
        } else {
            if (terms == null) {
                try {
                    return new FieldSearchQuery(Condition.getConditions(query));
                } catch (QueryParseException e) {
                    throw trouble("Value '" + query + "' of parameter '"
                            + PARAMETER_QUERY
                            + "' is not a valid query string.");
                }
            } else {
                throw trouble("You must provide either '" + PARAMETER_QUERY
                        + "' or '" + PARAMETER_TERMS + "', but not both.");
            }
        }
    }

    /**
     * Try to read the log configuration properties from the supplied file. If
     * no file was specified, return an empty {@link Properties} object.
     */
    private Properties readLogConfigFile(String logConfigFilename) {
        Properties props = new Properties();

        if (logConfigFilename != null) {
            File propertiesFile = new File(logConfigFilename);

            if (!propertiesFile.exists()) {
                throw trouble(PARAMETER_LOG_CONFIG + " file '"
                        + logConfigFilename + "' does not exist.");
            }
            if (!propertiesFile.isFile() || !propertiesFile.canRead()) {
                throw trouble(PARAMETER_LOG_CONFIG + " file '"
                        + logConfigFilename + "' is not a readable file.");
            }

            try {
                props.load(new FileInputStream(propertiesFile));
            } catch (IOException e) {
                throw trouble("Failed to load properties from "
                        + PARAMETER_LOG_CONFIG + " file '" + logConfigFilename
                        + "'");
            }
        }
        return props;
    }

    /**
     * Found something wrong with the command-line arguments? Prepare an
     * exception to be thrown.
     */
    private IllegalArgumentException trouble(String message) {
        return new IllegalArgumentException(message + "\n" + USAGE);
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }

    /**
     * Create a fresh {@link Properties} object to distribute, so this object
     * remains immutable.
     */
    public Properties getLogConfigProperties() {
        Properties props = new Properties();
        props.putAll(logConfigProperties);
        return props;
    }

    public FieldSearchQuery getQuery() {
        return fieldSearchQuery;
    }

}
