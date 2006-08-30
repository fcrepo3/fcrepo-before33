package fedora.utilities.install;

import java.io.*;
import java.util.*;

public class InstallOptions {

    public static final String FEDORA_HOME           = "fedora.home";
    public static final String APIA_AUTH_REQUIRED    = "apia.auth.required";
    public static final String SSL_AVAILABLE         = "ssl.available";
    public static final String APIA_SSL_REQUIRED     = "apia.ssl.required";
    public static final String APIM_SSL_REQUIRED     = "apim.ssl.required";
    public static final String SERVLET_ENGINE        = "servlet.engine";
    public static final String BUNDLED_TOMCAT        = "bundledTomcat";
    public static final String EXISTING_TOMCAT       = "existingTomcat";
    public static final String OTHER                 = "other";
    public static final String TOMCAT_HOME           = "tomcat.home";
    public static final String FEDORA_ADMIN_PASS     = "fedora.admin.pass";
    public static final String TOMCAT_SHUTDOWN_PORT  = "tomcat.shutdown.port";
    public static final String TOMCAT_HTTP_PORT      = "tomcat.http.port";
    public static final String TOMCAT_SSL_PORT       = "tomcat.ssl.port";
    public static final String KEYSTORE_FILE         = "keystore.file";
    public static final String JDBC_JAR_FILE         = "jdbc.jar.file";
    public static final String XACML_ENABLED         = "xacml.enabled";
    public static final String DEPLOY_LOCAL_SERVICES = "deploy.local.services";

    private Map _map;
    private boolean _bundled;

    /**
     * Initialize options from the given map of String values, keyed by 
     * option id.
     */
    public InstallOptions(Map map, boolean bundled) 
            throws OptionValidationException {
    
        _map = map;
        _bundled = bundled;

        applyDefaults();
        validateAll();
    }

    /**
     * Initialize options interactively, via input from the console.
     */
    public InstallOptions(boolean bundled) 
            throws InstallationCancelledException {

        _bundled = bundled;
        _map = new HashMap();

        System.out.println();
        System.out.println("*******************************");
        System.out.println("  Fedora Installation Options  ");
        System.out.println("*******************************");
        System.out.println();
        System.out.println("Please answer the following questions to install Fedora.");
        System.out.println("You can enter CANCEL at any time to abort installation.");
        System.out.println();

        inputOption(FEDORA_HOME);
        inputOption(APIA_AUTH_REQUIRED);
        inputOption(SSL_AVAILABLE);

        if (getBooleanValue(SSL_AVAILABLE, true)) {
            inputOption(APIA_SSL_REQUIRED);
            inputOption(APIM_SSL_REQUIRED);
        }

        inputOption(SERVLET_ENGINE);

        if (!getValue(SERVLET_ENGINE).equals(OTHER)) {

            inputOption(TOMCAT_HOME);

            if (getValue(SERVLET_ENGINE).equals(BUNDLED_TOMCAT)) {

                inputOption(FEDORA_ADMIN_PASS);
                inputOption(TOMCAT_SHUTDOWN_PORT);
                inputOption(TOMCAT_HTTP_PORT);
                inputOption(TOMCAT_SSL_PORT);

                if (getBooleanValue(SSL_AVAILABLE, true)) {
                    inputOption(KEYSTORE_FILE);
                }
            }

            inputOption(XACML_ENABLED);
            inputOption(DEPLOY_LOCAL_SERVICES);
        }

        inputOption(JDBC_JAR_FILE);
    }

    private String dashes(int len) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < len; i++) {
            out.append('-');
        }
        return out.toString();
    }

    /**
     * Get the indicated option from the console.
     *
     * Continue prompting until the value is valid, or the user has
     * indicated they want to cancel the installation.
     */
    private void inputOption(String optionId) 
            throws InstallationCancelledException {

        OptionDefinition opt = OptionDefinition.get(optionId);

        System.out.println(opt.getLabel());
        System.out.println(dashes(opt.getLabel().length()));
        System.out.println(opt.getDescription(_bundled));

        System.out.println();

        String[] valids = opt.getValidValues(_bundled);
        if (valids != null) {
            System.out.print("Valid values : ");
            for (int i = 0; i < valids.length; i++) {
                if (i > 0) System.out.print(", ");
                System.out.print(valids[i]);
            }
            System.out.println();
        }

        String defaultVal = opt.getDefaultValue();
        if (defaultVal != null) {
            System.out.println("Default value: " + defaultVal);
        }

        if (valids != null || defaultVal != null) {
            System.out.println();
        }

        boolean gotValidValue = false;

        while (!gotValidValue) {

            System.out.print("Enter a value ");
            if (defaultVal != null) {
                System.out.print("[" + defaultVal + "] ");
            }
            System.out.print(":");

            String value = readLine().trim();
            if (value.length() == 0 && defaultVal != null) {
                value = defaultVal;
            }
            System.out.println();
            System.out.println();
            if (value.equalsIgnoreCase("cancel")) {
                throw new InstallationCancelledException("Cancelled by user.");
            }

            try {
                opt.validateValue(value, _bundled);
                gotValidValue = true;
                _map.put(optionId, value);
            } catch (OptionValidationException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

    }

    private String readLine() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            return reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException("Error: Unable to read from STDIN");
        }
    }

    /**
     * Dump all options (including any defaults that were applied) 
     * to the given stream, in java properties file format.
     *
     * The output stream remains open after this method returns.
     */
    public void dump(OutputStream out) 
            throws IOException {

        Properties props = new Properties();
        Iterator iter = _map.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            props.setProperty(key, getValue(key));
        }

        props.store(out, "Install Options");
    }

    /**
     * Get the value of the given option, or <code>null</code> if it doesn't exist.
     */
    public String getValue(String name) {
        return (String) _map.get(name);
    }

    /**
     * Get the value of the given option as an integer, or the given default value
     * if unspecified.
     *
     * @throws NumberFormatException if the value is specified, but cannot be parsed
     *         as an integer.
     */
    public int getIntValue(String name, int defaultValue) throws NumberFormatException {

        String value = getValue(name);

        if (value == null) {
            return defaultValue;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * Get the value of the given option as a boolean, or the given default value
     * if unspecified.
     *
     * If specified, the value is assumed to be <code>true</code> if given as "true",
     * regardless of case.  All other values are assumed to be <code>false</code>.
     */
    public boolean getBooleanValue(String name, boolean defaultValue) {

        String value = getValue(name);

        if (value == null) {
            return defaultValue;
        } else {
            return value.equals("true");
        }
    }

    /**
     * Get an iterator of the names of all specified options.
     */
    public Iterator getOptionNames() {
        return _map.keySet().iterator();
    }

    /**
     * Apply defaults to the options, where possible.
     */
    private void applyDefaults() {
    }

    /**
     * Validate the options, assuming defaults have already been applied.
     *
     * Validation for a given option might entail more than a syntax check.
     * It might check whether a given directory exists, for example.
     */
    private void validateAll() throws OptionValidationException {

        Iterator keys = getOptionNames();
        while (keys.hasNext()) {
            String optionId = (String) keys.next();
            OptionDefinition opt = OptionDefinition.get(optionId);
            opt.validateValue(getValue(optionId), _bundled);
        }
    }

}
